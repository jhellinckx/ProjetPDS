package com.pds.app.caloriecounter;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanningActivity extends HomeActivity{

    private Button scan;
    private TextView format, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_camera, frameLayout);

        scan = (Button) v.findViewById(R.id.scan_button);
        format = (TextView) v.findViewById(R.id.formatTxt);
        content = (TextView) v.findViewById(R.id.contentTxt);

        scan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startScan();
            }
        });

    }

    public void startScan() {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){

        IntentResult scanResults = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResults != null){

            // TODO Send scanned content to server.

            String scanContent = scanResults.getContents();
            String scanFormat = scanResults.getFormatName();

            format.setText("FORMAT: " + scanFormat);
            content.setText("CONTENT: " + scanContent);

        } else{
            Toast toast = Toast.makeText(getApplicationContext(), "Scan Cancelled", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
