package com.pds.app.caloriecounter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import static org.calorycounter.shared.Constants.network.*;
import static org.calorycounter.shared.Constants.client.*;

public class NetworkHandler {
    private final int SAME_REQUESTS_ON_HOLD_LIMIT = 5;

    private static NetworkHandler _instance;
    private Context _context;
    private List<JSONObject> _in;
    private List<JSONObject> _out;
    private ActivityNetworkCallback _callback;
    private HashMap<Class<? extends NotifiableActivity>, ArrayList<JSONObject>> _messagesOnHold;

    protected Socket _socket;
    protected Listener _listener;
    protected Thread _listenerThread;
    protected Sender _sender;
    protected Thread _senderThread;
    protected Object _socketLock;
    protected int _port;
    protected String _host;

    private NetworkHandler(Context context) {
        _context = context;
        _listener = new Listener(this);
        _sender = new Sender(this);
        _in = new LinkedList<>();
        _out = new LinkedList<>();
        _socket = null;
        _callback = new ActivityNetworkCallback(this);
        ((Application)_context.getApplicationContext()).registerActivityLifecycleCallbacks(_callback);
        _messagesOnHold = new HashMap<>();
        _socketLock = new Object();
        JSONObject config = loadNetworkConfig(context);
        _host = (String)config.get("host");
        if (_host.equals(LOCALHOST) || _host.equals(LOCALHOST_STRING) )
            _host = EMULATOR_DEVICE_ADDRESS;
        _port = Integer.parseInt((String)config.get("port"));

    }

    private JSONObject loadNetworkConfig(Context context){
        InputStream is = context.getResources().openRawResource(R.raw.networkconfig);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        }catch(IOException e){}
        finally {
            try {is.close();}catch(IOException e){}
        }

        try{
            return (JSONObject)(new JSONParser()).parse(writer.toString());
        }catch(org.json.simple.parser.ParseException e){
            return null;
        }
    }

    public static synchronized NetworkHandler getInstance(Context context){
        if(_instance == null){
            _instance = new NetworkHandler(context);
        }
        return _instance;
    }

    public void launchThreads(){
        _listenerThread = new Thread(_listener);
        _senderThread = new Thread(_sender);
        _listenerThread.start();
        _senderThread.start();
    }

    public boolean isConnected(){
        synchronized (_socketLock){
            return (_socket == null) ? false : _socket.isConnected() && !_socket.isClosed();
        }
    }

    private void dispatch(JSONObject msg) {
        try {
            /* Assert message validity */
            if (!msg.containsKey(REQUEST_TYPE) || !msg.containsKey(DATA))
                throw new IOException("Network message has to contain a " +
                        REQUEST_TYPE + " key and a " + DATA + " key.");

            String request = (String) msg.get(REQUEST_TYPE);

            /* When no activity is specified in _doDispatch, the message is dispatched to
            * the current front activity */
            if (request.equals(CONNECTION_NOTIFIER)) {
                _doDispatch(msg);
            } else if (request.equals(LOG_IN_REQUEST)) {
                _doDispatch(msg, LogActivity.class);
            } else if (request.equals(SIGN_UP_REQUEST)) {
                _doDispatch(msg, SignActivity.class);
            } else if (request.equals(FOOD_CODE_REQUEST)) {
                _doDispatch(msg, RecommendationActivity.class);
            } else if (request.equals(RANDOM_UNRANKED_FOODS_REQUEST)) {
                _doDispatch(msg, RatingActivity.class);
            } else if (request.equals(SPORTS_LIST_REQUEST)) {
                _doDispatch(msg, RecommendationActivity.class);
            } else if (request.equals(RECOMMEND_REQUEST)) {
                _doDispatch(msg, RecommendationActivity.class);
            } else if (request.equals(UPDATE_DATA_REQUEST)) {
                _doDispatch(msg, RecommendationActivity.class);
            } else if (request.equals(HISTORY_REQUEST)) {
                _doDispatch(msg, HistoryActivity.class);
            } else if (request.equals(FOOD_CODE_REQUEST_HISTORY)) {
                _doDispatch(msg, HistoryActivity.class);
            } else {
                throw new UnsupportedOperationException("Unknown request : " + request.toString());

            }

        } catch (IOException e) {
            Log.d("Dispatcher", e.getMessage());
        }
    }


    private void _doDispatch(JSONObject msg){
        /* No specified activity, dispatch to front activity */
        _doDispatch(msg, NotifiableActivity.class);
    }

    private <T extends NotifiableActivity> void _doDispatch(JSONObject msg, Class<T> classname){
        NotifiableActivity destActivity;
         /* First case, no activity was specified, thus dispatch msg to front activity */
        if(classname.equals(NotifiableActivity.class)) destActivity = getFrontActivity();
        /* Else, get specified activity */
        else destActivity = (NotifiableActivity)_callback.getLastActivityByName(classname.getName());

        /*  Despite its certain assignation, destActivity can be null.
         *  First case - no activity was specified :
         *      - destActivity == null in this case happens when
         *      no activity is visible. e.g. at application launch or when
         *      application is in background.
         *  Second case - activity is specified :
         *      - destActivity == null here simply happens when
         *      the specified activity has not yet been created.
         *  In any case, we put the message on hold. The activity will
         *  then have to retrieve the message when it is created. */
        if(destActivity == null) {
            Log.d("PUTTING ON HOLD",classname+ " -> " + msg.toString());
            _addMessageOnHold(msg, classname);
        }
        else {
            Log.d("DIRECT HANDLE MSG",destActivity.getClass().toString()+ " -> " + msg.toString());
            destActivity.handleMessage(msg);
        }
    }

    private <T extends NotifiableActivity> void _addMessageOnHold(JSONObject msg, Class<T> classname){
        synchronized (_messagesOnHold) {
            if (!_messagesOnHold.containsKey(classname)) {
                _messagesOnHold.put(classname, new ArrayList<JSONObject>());
            }
            removeOldestEqualRequestAtLimit(_messagesOnHold.get(classname), msg);
            _messagesOnHold.get(classname).add(msg);
            Log.d("ON HOLD DICT",_messagesOnHold.toString());
        }
    }

    private void removeOldestEqualRequestAtLimit(ArrayList<JSONObject> messages, JSONObject msg){
        String request = (String) msg.get(REQUEST_TYPE);
        int equalRequestsCount = 0;
        int index = 0;
        int oldestEqualRequestIndex = -1;

        for(JSONObject message : messages){
            if (message.get(REQUEST_TYPE).equals(request)) {
                if(equalRequestsCount == 0)
                    oldestEqualRequestIndex = index;
                ++equalRequestsCount;
            }
            ++index;
        }
        if(equalRequestsCount > SAME_REQUESTS_ON_HOLD_LIMIT) {
            JSONObject removed = messages.remove(oldestEqualRequestIndex);
            Log.d("REMOVED ON HOLD",removed.toString());
            Log.d("ON HOLD MSG SIZE",Integer.toString(messages.size()));
        }
    }

    public <T extends NotifiableActivity> ArrayList<JSONObject> getMessagesOnHold(Class<T> classname){
        /*  Return messages intended for the specified activity and for
         *  NotifiableActivity.class (since the messages intended for this
         *  super class are intended for every activity). */
        synchronized (_messagesOnHold){
            ArrayList<JSONObject> messages = new ArrayList<>();

            /* Get the messages intended for every activity */
            if(_messagesOnHold.containsKey(NotifiableActivity.class))
                messages.addAll(_messagesOnHold.put(NotifiableActivity.class, new ArrayList<JSONObject>(0)));

            /* Get the messages intended for the specified activity */
            if(_messagesOnHold.containsKey(classname))
                messages.addAll(_messagesOnHold.remove(classname));

            return messages;
        }
    }

    public void stop(){
        try {
            _socket.close();
        } catch (IOException e) {
           Log.d("NetworkHandler : ",e.getMessage());
        }
        _listener.stop();
        _sender.stop();
        synchronized(_out) {_out.notify(); }
        synchronized(_socketLock){_socketLock.notifyAll();}
    }

    public void addOutgoingMessage(JSONObject msg){
        if(isConnected()) {
            synchronized (this._out) {
                this._out.add(msg);
                this._out.notify();
            }
        }
    }

    public JSONObject receiveOutgoingMessage() throws InterruptedException,IOException{
        synchronized(this._out){
            while(this._out.isEmpty()){
                if(!this._sender.isRunning()){
                    throw new InterruptedException("Sender stopped.");
                }
                try{
                    this._out.wait();
                }
                catch(InterruptedException e){
                    if(!this.isConnected())
                        throw new IOException("Connection lost");
                }
            }
            return this._out.remove(0);
        }
    }

    public NotifiableActivity getFrontActivity(){
        return (NotifiableActivity)_callback.getFrontActivity();
    }

    private class Listener implements Runnable{
        final int RETRY_CONNECT_INTERVAL = 1000;

        NetworkHandler _handler;
        Boolean _run;
        DataInputStream _inStream;
        JSONParser _parser;

        public Listener(NetworkHandler handler){
            _handler = handler;
            _run = new Boolean(false);
            _inStream = null;
            _parser = new JSONParser();
        }

        private void _doRead() throws IOException {
            try{
                int msgLength = _inStream.readInt();
                byte[] rawMsg = new byte[msgLength];
                int bytesRead = _inStream.read(rawMsg, 0, msgLength);
                if(bytesRead != msgLength)
                    throw new IOException("could not read a message of given size.");
                String msg = new String(rawMsg, ENCODING);
                _handler.dispatch((JSONObject)_parser.parse(msg));
            }
            catch(ParseException e){
                Log.d("Listener", e.getMessage());
            }
            catch(UnsupportedEncodingException e){
                Log.d("Listener",e.getMessage());
            }
        }

        private void _doConnect() throws IOException{
            synchronized (_handler._socketLock) {
                if (!isConnected()){
                    _handler._socket = new Socket(_host, _port);
                }
                _handler._socketLock.notify(); //notify Sender thread
            }
            JSONObject connectionNotifierData = new JSONObject();
            connectionNotifierData.put(CONNECTION_STATUS, CONNECTION_SUCCESS);
            _handler.dispatch(networkJSON(CONNECTION_NOTIFIER, connectionNotifierData));
        }

        public void run(){
            _run = true;
            while(isRunning()) {
                try {
                    _doConnect();
                    _inStream = new DataInputStream(_handler._socket.getInputStream());
                    while (isRunning()) {
                        _doRead();
                    }
                } catch (IOException e) {
                    try {
                        if(_handler._socket != null)
                            _handler._socket.close();
                    }catch (IOException innerE){
                        Log.d("Listener","could not close socket");
                    }
                   _handler._senderThread.interrupt(); // Notify Sender that connection is lost
                    JSONObject connectionNotifierData = new JSONObject();
                    connectionNotifierData.put(CONNECTION_STATUS, CONNECTION_FAILURE);
                    _handler.dispatch(networkJSON(CONNECTION_NOTIFIER, connectionNotifierData));
                }
                try {
                    Thread.sleep(RETRY_CONNECT_INTERVAL); //Wait for connection retry
                } catch (InterruptedException e) {}
            }
        }

        public void stop(){
            synchronized (_run){
                _run = false;
            }
        }

        public boolean isRunning(){
            synchronized (_run){
                return _run;
            }
        }
    }

    private class Sender implements Runnable{
        NetworkHandler _handler;
        Boolean _run;
        DataOutputStream _outStream;

        public Sender(NetworkHandler handler){
            _handler = handler;
            _run = new Boolean(false);
            _outStream = null;
        }

        /* Double loop on isRunning ?
        *   - Outer loop always checks first if socket connected, and wait until it is
        *   - Outer loop then create a stream from this socket and enters inner isRunning loop
        *      - inner loop is a trivial loop on waiting for a message to be sent, then write it on socket
        *      - inner loop can however be interrupted by _doWrite if socket is closed
        *  - hence the need of an outer loop : if inner loop interrupted, don't break run() if _run is true
        *  but instead wait again for a new socket
        *  */
        public void run(){
            _run = true;
            while(isRunning()) {
                try {
                    synchronized(_handler._socketLock) {
                        while ((!isConnected()) && isRunning()) {
                            try {
                                _handler._socketLock.wait();
                            } catch (InterruptedException e) {}
                        }
                    }
                    if(isRunning()) _outStream = new DataOutputStream(_handler._socket.getOutputStream());
                    while (isRunning() && isConnected()) {
                        try {
                            JSONObject msg = _handler.receiveOutgoingMessage();
                            _doWrite(msg);
                        } catch (InterruptedException e) {
                            Log.d("Sender : ", e.getMessage());
                        }
                    }
                } catch (IOException e) {
                    Log.d("Sender : ", e.getMessage());
                }
            }
        }

        private void _doWrite(JSONObject msg) throws IOException{
            try{
                byte[] rawMsg = msg.toString().getBytes(ENCODING);
                _outStream.writeInt(rawMsg.length);
                _outStream.write(rawMsg, 0, rawMsg.length);
            }
            catch(UnsupportedEncodingException e){
                Log.d("Listener",e.getMessage());
            }
        }

        public void stop(){
            synchronized (_run){
                _run = false;
            }
        }

        public boolean isRunning(){
            synchronized (_run){
                return _run;
            }
        }
    }

    private class ActivityNetworkCallback implements Application.ActivityLifecycleCallbacks{
        private NetworkHandler _handler;
        private Activity _front;
        private Object _frontLock;
        private ArrayList<Activity> _createdActivities;

        public ActivityNetworkCallback(NetworkHandler handler){
            _handler = handler;
            _front = null;
            _frontLock = new Object();
            _createdActivities = new ArrayList<>();
        }

        public Activity getFrontActivity(){
            synchronized(_frontLock){
                return _front;
            }
        }

        public Activity getActivityByName(String activityName){
            synchronized (_createdActivities){
                for(Activity activity : _createdActivities){
                    if(activity.getClass().getName().equals(activityName)) {
                        return activity;
                    }
                }
                return null;
            }
        }

        public Activity getLastActivityByName(String activityName){
            synchronized (_createdActivities){
                Activity res = null;
                for(Activity activity : _createdActivities){
                    if(activity.getClass().getName().equals(activityName)) {
                        res = activity;
                    }
                }
                return res;
            }
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            synchronized (_createdActivities){
                Log.d("CREATING ACTIVITY",activity.getClass().getName());
                Log.d("ALL ACTIVITIES",_createdActivities.toString());
                if(NotifiableActivity.class.isInstance(activity)) {
                    _createdActivities.add(activity);
                }
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            synchronized(_createdActivities){
                Log.d("DESTROYING ACTIVITY",activity.getClass().getName());
                if(NotifiableActivity.class.isInstance(activity)) {
                    _createdActivities.remove(activity);
                }
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            synchronized(_frontLock){
                if(NotifiableActivity.class.isInstance(activity)) {
                    _front = activity;
                }
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            synchronized (_frontLock){
                if(NotifiableActivity.class.isInstance(activity)) {
                    if (_front != null) {
                        if (_front.equals(activity))
                            _front = null;
                    }
                }
            }
        }

        @Override
        public void onActivityStarted(Activity activity) {}

        @Override
        public void onActivityStopped(Activity activity) {}

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}


    }
}
