package com.pds.app.caloriecounter;

import android.content.Context;

import org.json.simple.JSONObject;

import java.util.List;
import java.util.LinkedList;

/**
 * Created by jhellinckx on 12/12/15.
 */
public class NetworkHandler {
    private static NetworkHandler _instance;
    private Context _context;
    private Listener _listener;
    private Sender _sender;
    private List<JSONObject> _in;
    private List<JSONObject> _out;

    private NetworkHandler(Context context){
        _context = context;
        _listener = new Listener();
        _sender = new Sender();
        _in = new LinkedList<JSONObject>();
        _out = new LinkedList<JSONObject>();
        new Thread(_sender).start();
        new Thread(_listener).start();
    }

    public static synchronized NetworkHandler getInstance(Context context){
        if(_instance == null){
            _instance = new NetworkHandler(context);
        }
        return _instance;
    }

    private class Listener implements Runnable{
        public void run(){
            
        }
    }

    private class Sender implements Runnable{
        public void run(){

        }
    }
}
