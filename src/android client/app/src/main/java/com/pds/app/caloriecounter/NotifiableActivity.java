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
        JSONObject connectionNotifierData = new JSONObject();
        connectionNotifierData.put(CONNECTION_STATUS, connectionStatus());
        handleMessage(networkJSON(CONNECTION_NOTIFIER, connectionNotifierData));
    }

    abstract public void handleMessage(JSONObject msg);

    public void send(JSONObject msg){
        NetworkHandler.getInstance(getApplicationContext()).addOutgoingMessage(msg);
    }

    public void retryConnect(){
        NetworkHandler.getInstance(getApplicationContext()).retryConnect();
    }

    public boolean connected(){
        return NetworkHandler.getInstance((getApplicationContext())).isConnected();
    }

    public String connectionStatus(){
        return  connected() ? CONNECTION_SUCCESS : CONNECTION_FAILURE;
    }
}
