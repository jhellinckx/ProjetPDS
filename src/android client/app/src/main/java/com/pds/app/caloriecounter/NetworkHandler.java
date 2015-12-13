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
import java.net.ConnectException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by jhellinckx on 12/12/15.
 */
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

    public static String HOST = "localhost";
    public static int PORT = 2015;
    public static Charset ENCODING = UTF_8;

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

    private void dispatch(JSONObject msg){
        try{
            /* Assert message validity */
            if(!msg.containsKey("RequestType"))
                throw new IOException("Network message has to contain a RequestType key.");
            if(!msg.containsKey("Data"))
                throw new IOException("Network message has to contain a Data key");

            String request = (String) msg.get("RequestType");
            if(request.equals("CONNECTION_NOTIFIER")){
                _doDispatch(msg, LogActivity.class);
            }
        }
        catch(IOException e){
            Log.d("Dispatcher, : ", e.getMessage());
        }
    }

    private <T extends NotifiableActivity> void _doDispatch(JSONObject msg, Class<T> classname){
        NotifiableActivity dest = (NotifiableActivity)_callback.getActivityByName(classname.getName());
        if(dest == null){
            //TODO : Activity not created, push JSONObject in HashMap
            Log.d("BIGWTFLOL","slt");
        }
        else{
            dest.handleMessage(msg);
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
        _out.notify();
    }

    public void addOutgoingMessage(JSONObject msg){
        synchronized(this._out){
            this._out.add(msg);
            this._out.notify();
        }
    }

    public JSONObject receiveOutgoingMessage() throws InterruptedException{
        synchronized(this._out){
            while(this._out.isEmpty()){
                if(!this._sender.isRunning()){
                    throw new InterruptedException("Sender stopped.");
                }
                try{
                    this._out.wait();
                }
                catch(InterruptedException e){}
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

        private void _doRead(){
            try{
                int msgLength = _inStream.readInt();
                byte[] rawMsg = new byte[msgLength];
                int bytesRead = _inStream.read(rawMsg, 0, msgLength);
                if(bytesRead != msgLength)
                    throw new IOException("could not read a message of given size.");
                String msg = new String(rawMsg, ENCODING);
                _handler.dispatch((JSONObject)_parser.parse(msg));
            }
            catch(IOException e){
                Log.d("Listener", e.getMessage());
            }
            catch(ParseException e){
                Log.d("Listener", e.getMessage());
            }
        }

        private void _doConnect() throws IOException{
            synchronized (_handler) {
                if (_handler._socket == null || _handler._socket.isClosed()) {
                    _handler._socket = new Socket(_handler.HOST, _handler.PORT);
                }
                _handler.notify();
            }
            JSONObject connectionNotifier = new JSONObject();
            connectionNotifier.put("RequestType","CONNECTION_NOTIFIER");
            connectionNotifier.put("Data","CONNECTION_SUCCESS");
            _handler.dispatch(connectionNotifier);
        }

        public void run(){
            try {
                _doConnect();
                _inStream = new DataInputStream(_handler._socket.getInputStream());
                _run = true;
                while (isRunning()) {
                    _doRead();
                }
            }
            catch(ConnectException e){
                JSONObject connectionNotifier = new JSONObject();
                connectionNotifier.put("RequestType","CONNECTION_NOTIFIER");
                connectionNotifier.put("Data","CONNECTION_FAILURE");
                _handler.dispatch(connectionNotifier);
            }
            catch(IOException e){
                Log.d("Listener : ", e.getMessage());
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

        public void run(){
            try {
                synchronized (_handler) {
                    if (_handler._socket == null || _handler._socket.isClosed()) {
                        try {
                            _handler.wait();
                        } catch (InterruptedException e) {}
                    }
                }
                _outStream = new DataOutputStream(_handler._socket.getOutputStream());
                _run = true;
                while (isRunning()) {
                    try {
                        JSONObject msg = _handler.receiveOutgoingMessage();
                        _doWrite(msg);
                    } catch (InterruptedException e) {
                        Log.d("Sender : ", e.getMessage());
                    }
                }
            }
            catch(IOException e){
                Log.d("Sender : ", e.getMessage());
            }
        }

        private void _doWrite(JSONObject msg) {
            byte[] rawMsg = msg.toString().getBytes(ENCODING);
            try {
                _outStream.writeInt(rawMsg.length);
                _outStream.write(rawMsg, 0, rawMsg.length);
            }
            catch(IOException e){
                Log.d("Sender :", e.getMessage());
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
            Log.d("HOOKD CREATE",activity.getClass().getName());
            synchronized (_createdActivities){
                _createdActivities.add(activity);
            }
            //TODO savedInstanceState.putStringArrayList("Messages", _handler.getMessages(activity.getClass().getName()));
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.d("HOOKD DESTROY",activity.getClass().getName());
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
                if(_front.equals(activity))
                    _front = null;
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
