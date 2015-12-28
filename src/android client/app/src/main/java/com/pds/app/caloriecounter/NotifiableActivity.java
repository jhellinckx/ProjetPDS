package com.pds.app.caloriecounter;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static org.calorycounter.shared.Constants.network.*;

public abstract class NotifiableActivity extends AppCompatActivity {
    @Bind(R.id.connection_state) View _connectionState;

    //protected ProgressDialog _networkProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceBundle) {

        super.onCreate((savedInstanceBundle));
    }

    @Override
    protected void onResume(){
        super.onResume();
        handleMessagesOnHold();

        if(connected()) setConnected();
        else setDisconnected();
    }

    public void handleMessage(JSONObject msg){
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(CONNECTION_NOTIFIER)){
            String res = (String) data.get(CONNECTION_STATUS);
            if(res.equals(CONNECTION_SUCCESS)){
                setConnected();
            } else if (res.equals(CONNECTION_FAILURE)) {
                setDisconnected();
            }
        }
    }

    public void handleMessagesOnHold(){
        for(JSONObject msg : messagesOnHold()){
            this.handleMessage(msg);
        }
    }

    public ArrayList<JSONObject> messagesOnHold(){
        return NetworkHandler.getInstance(getApplicationContext()).getMessagesOnHold(this.getClass());
    }

    public void send(JSONObject msg) throws IOException{
        if(!connected()){
            throw new IOException("Connection failure");
        }
        else NetworkHandler.getInstance(getApplicationContext()).addOutgoingMessage(msg);
    }

    public void send(JSONObject msg, boolean showProgressDialog, String dialogMessage){
        //TODO progressdialog
        /*
        _networkProgressDialog = new ProgressDialog(NotifiableActivity.this, R.style.AppTheme_Dark_Dialog);
        _networkProgressDialog.setIndeterminate(true);
        _networkProgressDialog.setMessage("Authenticating...");
        _networkProgressDialog.show();
        */
        return;
    }

    public boolean connected(){
        return NetworkHandler.getInstance((getApplicationContext())).isConnected();
    }

    public String connectionStatus(){
        return  connected() ? CONNECTION_SUCCESS : CONNECTION_FAILURE;
    }

    public void setConnected(){
        if(_connectionState == null) return;
        runOnUiThread(new Runnable() {
            public void run() {
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    _connectionState.setBackgroundDrawable(getResources().getDrawable(R.drawable.connection_state_success));
                } else {
                    _connectionState.setBackground(getResources().getDrawable(R.drawable.connection_state_success));
                }
            }
        });
    }

    public void setDisconnected(){
        if(_connectionState == null) return;
        runOnUiThread(new Runnable() {
            public void run() {
                final int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                    _connectionState.setBackgroundDrawable(getResources().getDrawable(R.drawable.connection_state_failure));
                } else {
                    _connectionState.setBackground(getResources().getDrawable(R.drawable.connection_state_failure));
                }
            }
        });
    }
}
