package com.pds.app.caloriecounter;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import static org.calorycounter.shared.Constants.network.*;

public class NetworkHandler {
    private static NetworkHandler _instance;
    private Context _context;
    private List<JSONObject> _in;
    private List<JSONObject> _out;
    private ActivityNetworkCallback _callback;
    private HashMap<String, ArrayList<String>> _messagesOnHold;

    protected Socket _socket;
    protected Listener _listener;
    protected Sender _sender;
    protected Object _socketLock;

    private NetworkHandler(Context context) {
        _context = context;
        _listener = new Listener(this);
        _sender = new Sender(this);
        _in = new LinkedList<>();
        _out = new LinkedList<>();
        _socket = null;
        _callback = new ActivityNetworkCallback(this);
        ((Application)context.getApplicationContext()).registerActivityLifecycleCallbacks(_callback);
        _messagesOnHold = new HashMap<String, ArrayList<String>>();
        _socketLock = new Object();

    }

    public static synchronized NetworkHandler getInstance(Context context){
        if(_instance == null){
            _instance = new NetworkHandler(context);
        }
        return _instance;
    }

    public void launchThreads(){
        new Thread(_listener).start();
        new Thread(_sender).start();
    }

    public void retryConnect(){
        synchronized (_socketLock) {
            _socketLock.notifyAll(); //Wake up Listener and Sender threads
        }
    }

    public boolean isConnected(){
        synchronized (_socketLock){
            return (_socket == null) ? false : _socket.isConnected() && !_socket.isClosed();
        }
    }

    private void dispatch(JSONObject msg){
        try{
            /* Assert message validity */
            if(!msg.containsKey(REQUEST_TYPE) || !msg.containsKey(DATA))
                throw new IOException("Network message has to contain a " +
                        REQUEST_TYPE +" key and a " + DATA + " key.");

            String request = (String) msg.get(REQUEST_TYPE);

            if(request.equals(CONNECTION_NOTIFIER)){
                _doDispatch(msg, LogActivity.class);
            }
            else if(request.equals(LOG_IN_REQUEST)){
                _doDispatch(msg, LogActivity.class);
            }
            else if(request.equals(SIGN_UP_REQUEST)){
                _doDispatch(msg,LogActivity.class);
            }
            else if(request.equals(FOOD_CODE_REQUEST)){
                _doDispatch(msg,ScanningActivity.class);
            }
        }
        catch(IOException e){
            Log.d("Dispatcher", e.getMessage());
        }
    }

    private <T extends NotifiableActivity> void _doDispatch(JSONObject msg, Class<T> classname){
        NotifiableActivity dest = (NotifiableActivity)_callback.getActivityByName(classname.getName());
        if(dest == null){
            //TODO : Activity not created, push JSONObject in HashMap
            Log.d("cannot dispatch : ",msg.toString());
        }
        else{
            dest.handleMessage(msg);
        }
    }

    /*
        - Listener gets automatically interrupted on read() since socket is closed
        - Sender gets interrupted by _out.notify()
        - if Listener and Sender waiting for socket to connect, _socketLock.notifyAll() interrupts them both
    */
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
                    Log.d("IS CONNECTED -> ",new Boolean(isConnected()).toString());
                    if(!this.isConnected())
                        throw new IOException("Connection lost");
                }
            }
            return this._out.remove(0);
        }
    }

    public Activity getFrontActivity(){
        return _callback.getFrontActivity();
    }

    private class Listener implements Runnable{
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
                    _handler._socket = new Socket(EMULATOR_DEVICE_ADDRESS, PORT);
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
                        Log.d("Socket close","could not close socket");
                    }
                   synchronized (_handler._out) { _handler._out.notify(); } // Notify Sender that connection is lost
                    JSONObject connectionNotifierData = new JSONObject();
                    connectionNotifierData.put(CONNECTION_STATUS, CONNECTION_FAILURE);
                    _handler.dispatch(networkJSON(CONNECTION_NOTIFIER, connectionNotifierData));
                }
                synchronized (_handler._socketLock) {
                    try {
                        _handler._socketLock.wait(); //Wait for connection retry
                    } catch (InterruptedException e) {}
                }
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
                    _outStream = new DataOutputStream(_handler._socket.getOutputStream());
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
        private List<Activity> _createdActivities;

        public ActivityNetworkCallback(NetworkHandler handler){
            _handler = handler;
            _front = null;
            _frontLock = new Object();
            _createdActivities = new LinkedList<Activity>();
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

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.d("CREATE ACTIVITY",activity.getClass().getName());
            synchronized (_createdActivities){
                _createdActivities.add(activity);
            }
            //TODO savedInstanceState.putStringArrayList("Messages", _handler.getMessages(activity.getClass().getName()));
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d("DESTROY ACTIVITY",activity.getClass().getName());
            synchronized(_createdActivities){
                _createdActivities.remove(activity);
            }
        }

        @Override
        public void onActivityResumed(Activity activity) {
            synchronized(_frontLock){
                _front = activity;
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {
            synchronized (_frontLock){
                if(_front != null){
                    if(_front.equals(activity))
                        _front = null;
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
