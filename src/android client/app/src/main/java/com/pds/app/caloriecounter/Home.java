package com.pds.app.caloriecounter;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class Home extends HomeActivity implements AdapterView.OnItemClickListener {

    private String[] functionalities;
    private ListView functionList;

    private void initFunctionList(){
        functionalities = getResources().getStringArray(R.array.homenav);
        functionList = (ListView) v.findViewById(R.id.homelist);

        functionList.setAdapter(new ArrayAdapter<String>(this, R.layout.list_home_item, functionalities));

        functionList.setOnItemClickListener(this);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_home,frameLayout);

        initFunctionList();

    }

    @Override
    public void onBackPressed(){

    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = (String) functionList.getItemAtPosition(position);

         if (selectedItem.compareTo(getResources().getString(R.string.homeitem1)) == 0) {

             Intent recommendactivity = new Intent(Home.this, RecommendationActivity.class);

             startActivity(recommendactivity);

        } else if (selectedItem.compareTo(getResources().getString(R.string.homeitem2)) == 0) {
             Intent dataactivity = new Intent(Home.this, PersonalDataActivity.class);

             startActivity(dataactivity);

        } else if (selectedItem.compareTo(getResources().getString(R.string.homeitem3)) == 0) {
             Intent ratingactivity = new Intent(Home.this, RatingActivity.class);

             startActivity(ratingactivity);

        } else if (selectedItem.compareTo(getResources().getString(R.string.homeitem4)) == 0) {
             Intent scanactivity = new Intent(Home.this, ScanningActivity.class);

             startActivity(scanactivity);

        }

    }
}
