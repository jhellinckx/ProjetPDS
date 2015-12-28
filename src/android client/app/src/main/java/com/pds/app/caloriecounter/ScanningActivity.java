package com.pds.app.caloriecounter;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.simple.JSONObject;

import java.io.IOException;

import static org.calorycounter.shared.Constants.network.*;

public class ScanningActivity extends HomeActivity{

    private Button scan;
    private FragmentManager manager = getSupportFragmentManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_camera, frameLayout);

        scan = (Button) v.findViewById(R.id.scan_button);

        scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startScan();
            }
        });
    }

    private void updateFragment(String image, String product, String energy){
        ItemInfosFragment frag = (ItemInfosFragment) manager.findFragmentByTag("info");
        frag.setImage(image);
        frag.setProductName(product);
        frag.setCal(energy);

    }

    private void addFragment(){
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.add(R.id.infos_layout, new ItemInfosFragment(), "info");
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void handleMessage(JSONObject msg){
        Log.d("SCANNINGCTIVITY HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(FOOD_CODE_REQUEST)){
            String response =  (String)data.get(FOOD_CODE_RESPONSE);
            if(response.equals(FOOD_CODE_SUCCESS)){
                String image_url = (String) data.get(FOOD_IMAGE_URL);

                String product_name = (String) data.get(FOOD_NAME);
                String energy_100g = (String) data.get(FOOD_ENERGY100G);

                addFragment();
                updateFragment(image_url, product_name, energy_100g);
            }
        }
    }

    public void startScan() {

        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void sendData(String code) {
        JSONObject data = new JSONObject();
        data.put(FOOD_CODE, code);
        try {
            send(networkJSON(FOOD_CODE_REQUEST, data));
        } catch (IOException e) {
            // Client not connected...
        }
        System.out.println("------------------CODE SENT -------------------");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        IntentResult scanResults = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResults != null && scanResults.getContents() != null){

            // TODO Send scanned content to server.

            String scanContent = scanResults.getContents();

            sendData(scanContent);



        } else{
            Toast toast = Toast.makeText(getApplicationContext(), "Scan Cancelled", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
