package com.pds.app.caloriecounter;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

    public void handleMessage(JSONObject msg){
        Log.d("SCANNINGCTIVITY HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(FOOD_CODE_REQUEST)){
            String response =  (String)data.get(FOOD_CODE_RESPONSE);
            if(response.equals(FOOD_CODE_SUCCESS)){
                String image_url = (String) data.get(FOOD_IMAGE_URL);
                System.out.println("\n"+image_url+"\n");

                String product_name = (String) data.get(FOOD_NAME);
                String energy_100g = (String) data.get(FOOD_ENERGY100G);
            }
        }
    }

    public void startScan() {
        //Sending test code
        String code = "0000000024600";
        JSONObject data = new JSONObject();
        data.put(FOOD_CODE, code);
        try {
            send(networkJSON(FOOD_CODE_REQUEST, data));
        } catch (IOException e) {
            Toast toast = Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG);
            toast.getView().setBackgroundColor(Color.RED);
            toast.show();;
        }
        System.out.println("------------------CODE SENT -------------------");
        //IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        //scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        IntentResult scanResults = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResults != null && scanResults.getContents() != null){

            // TODO Send scanned content to server.

            String scanContent = scanResults.getContents();
            String scanFormat = scanResults.getFormatName();

            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.infos_layout, new ItemInfosFragment());
            transaction.addToBackStack(null);
            transaction.commit();



        } else{
            Toast toast = Toast.makeText(getApplicationContext(), "Scan Cancelled", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
