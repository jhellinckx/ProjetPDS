package com.pds.app.caloriecounter;



import android.os.Bundle;
import android.util.Log;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static org.calorycounter.shared.Constants.network.*;

public class HistoryActivity extends HomeActivity {

    private TableLayout historyTable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_history,frameLayout);
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
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(HISTORY_REQUEST)){
            JSONArray response = (JSONArray) data.get(HISTORY_NAMES_DATES);
            ArrayList<JSONObject> namesDatesResults = new ArrayList<JSONObject>();
            for (int i = 0; i < response.size(); ++i) {
                namesDatesResults.add((JSONObject) response.get(i));
            }
            for(JSONObject nameDateRepr : namesDatesResults){
                String name = (String) nameDateRepr.get(HISTORY_NAME);
                String date = (String) nameDateRepr.get(HISTORY_DATE);
                addRowInTable(name, date);
            }
        }
    }

    private void addRowInTable(String name, String date){
        if(!name.isEmpty() && !date.isEmpty()){
            TableRow historyRow = new TableRow(this);
            //setClickListener(historyRow);
            makeTableRow(name,historyRow,true);
            makeTableRow(date,historyRow,false);

            historyTable.addView(historyRow);

        }
    }

    private void makeTableRow(String text, TableRow row, Boolean isName){
        TextView rowView = new TextView(this);
        rowView.setText(text);
        if(isName) {
            rowView.setPadding(50, 0, 0, 0);
        } else{
            rowView.setPadding(100,0,0,50);}
        row.addView(rowView);
    }

}
