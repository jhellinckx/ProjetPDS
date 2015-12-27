package com.pds.app.caloriecounter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.simple.JSONObject;

import java.io.IOException;

import butterknife.Bind;

import static org.calorycounter.shared.Constants.network.*;

public abstract class NotifiableActivity extends AppCompatActivity {
    @Bind(R.id.connection_state) View _connectionState;

    protected ProgressDialog _networkProgressDialog = null;

    protected void onCreate(Bundle savedInstanceBundle){
        super.onCreate((savedInstanceBundle));
    }

    abstract public void handleMessage(JSONObject msg);

    public void updateWithNetInfo(){
        /* Auto-notify connection status, will be deprecated when _messagesOnHold implemented in NetworkHandler */
        JSONObject connectionNotifierData = new JSONObject();
        connectionNotifierData.put(CONNECTION_STATUS, connectionStatus());
        handleMessage(networkJSON(CONNECTION_NOTIFIER, connectionNotifierData));
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
