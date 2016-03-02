package com.pds.app.caloriecounter;



import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static org.calorycounter.shared.Constants.network.*;

public class HistoryActivity extends HomeActivity {

    private TableLayout historyTable;
    private LinearLayout.LayoutParams _params;
    private Button addFoodButton = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_history,frameLayout);
        addFoodButton = (Button) v.findViewById(R.id.scan);

        addFoodButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startScan();
            }
        });


        historyTable = (TableLayout) v.findViewById(R.id.histTable);
        serverRequestHistory();
    }

    private void serverRequestHistory(){
        JSONObject data = new JSONObject();
        send(networkJSON(HISTORY_REQUEST, data));
    }

    public void handleMessage(JSONObject msg){
        Log.d("HISTORYACTIVITY HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        final JSONObject data = (JSONObject)msg.get(DATA);
        _params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
        if(request.equals(HISTORY_REQUEST)){
            runOnUiThread(new Runnable() {
                public void run() {
                    handleHistoryRequest(data);
                }
            });
        }
        else if(request.equals(FOOD_CODE_REQUEST_HISTORY)){

            runOnUiThread(new Runnable() {
                public void run() {
                    handleCodeRequest(data);
                }
            });
        }
    }

    private void handleHistoryRequest(JSONObject data){
        JSONArray response = (JSONArray) data.get(HISTORY_NAMES_DATES);
        ArrayList<JSONObject> namesDatesResults = new ArrayList<JSONObject>();
        for (int i = 0; i < response.size(); ++i) {
            namesDatesResults.add((JSONObject) response.get(i));
        }
        for(int i = 0; i<namesDatesResults.size(); i++){
            JSONObject nameDateRepr = namesDatesResults.get(i);
            String name = (String) nameDateRepr.get(HISTORY_NAME);
            String date = (String) nameDateRepr.get(HISTORY_DATE);
            addRowInTable(name, date);
        }
    }

    private void handleCodeRequest(JSONObject data){
        String foodName = (String) data.get(FOOD_NAME);
        String date = (String) data.get(HISTORY_DATE);
        addRowInTable(foodName, date);
    }

    private void addRowInTable(String name, String date){
        if(!name.isEmpty() && !date.isEmpty()){
            TableRow historyRow = new TableRow(this);
            //setClickListener(historyRow);
            makeTableRow(name,historyRow);
            makeTableRow(date,historyRow);


            historyRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
            historyTable.addView(historyRow,0);

        }
    }

    private void makeTableRow(String text, TableRow row){
        TextView rowView = new TextView(this);
        rowView.setText(text);
        rowView.setLayoutParams(_params);
        rowView.setPadding(70, 50, 0, 0);
        row.addView(rowView);
    }

    public void startScan(){
        //String scanContent = "96092521";
        //sendCode(scanContent, sdf.format(Calendar.getInstance().getTime()));
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void sendCode(String code, String date) {
        JSONObject data = new JSONObject();
        data.put(FOOD_CODE, code);
        data.put(HISTORY_DATE, date);
        send(networkJSON(FOOD_CODE_REQUEST_HISTORY, data));
        System.out.println("------------------CODE SENT -------------------");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        IntentResult scanResults = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResults != null && scanResults.getContents() != null){
            String scanContent = scanResults.getContents();
            sendCode(scanContent, sdf.format(Calendar.getInstance().getTime()));
        } else{
            Toast toast = Toast.makeText(getApplicationContext(), "Scan Cancelled", Toast.LENGTH_SHORT);
            toast.show();
        }

    }
}
