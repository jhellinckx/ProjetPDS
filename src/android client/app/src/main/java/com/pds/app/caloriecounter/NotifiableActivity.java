package com.pds.app.caloriecounter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import org.json.simple.JSONObject;
import static org.calorycounter.shared.Constants.network.*;

public abstract class NotifiableActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate((savedInstanceBundle));
    }

    public void updateWithNetInfo(){
        /* Auto-notify connection status, will be deprecated when _messagesOnHold implemented in NetworkHandler */
        JSONObject status = new JSONObject();
        status.put(REQUEST_TYPE, CONNECTION_STATUS);
        status.put(DATA, connectionStatus());
        handleMessage(status);
    }

    abstract public void handleMessage(JSONObject msg);

    public void send(JSONObject msg){
        NetworkHandler.getInstance(getApplicationContext()).addOutgoingMessage(msg);
    }

    public void retryConnect(){
        NetworkHandler.getInstance(getApplicationContext()).retryConnect();
    }

    public String connectionStatus(){
        return NetworkHandler.getInstance((getApplicationContext())).isConnected() ? CONNECTION_SUCCESS : CONNECTION_FAILURE;
    }
}
