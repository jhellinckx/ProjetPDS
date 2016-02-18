package com.pds.app.caloriecounter;



import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.Toast;
import org.json.simple.JSONObject;
import java.io.IOException;
import java.util.ArrayList;


import static org.calorycounter.shared.Constants.network.*;

public class RatingActivity extends HomeActivity {

    private RatingBar ratingBar;
    private GridView gridView;
    private float lastRating = -1.0f;//special value to test if set


    private void initializer(){
        ArrayList<String> urls = new ArrayList<String>(getUrlsFromServer());

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_rating,frameLayout);


        ArrayList<String> urls = new ArrayList();
        urls.add("http://static.openfoodfacts.org/images/products/356/007/076/4884/front.6.400.jpg");
        urls.add("http://static.openfoodfacts.org/images/products/356/007/076/5034/front.6.400.jpg");
        urls.add("http://static.openfoodfacts.org/images/products/326/288/010/2213/front.6.400.jpg");
        urls.add("http://static.openfoodfacts.org/images/products/356/007/098/1489/front.5.400.jpg");

        gridView = (GridView) findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(this, urls));
        initializer();
        addListenerGridView();

    }

    private void addListenerGridView(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(RatingActivity.this, "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        //TODO send rates
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent homeactivity = new Intent(RatingActivity.this, Home.class);

            startActivity(homeactivity);
        } else if (id == R.id.nav_data) {
            Intent dataactivity = new Intent(RatingActivity.this, PersonalDataActivity.class);

            startActivity(dataactivity);

        } else if (id == R.id.nav_recommend) {
            Intent recommendactivity = new Intent(RatingActivity.this, RecommendationActivity.class);

            startActivity(recommendactivity);

        } else if (id == R.id.nav_camera) {
            Intent cameractivity = new Intent(RatingActivity.this, ScanningActivity.class);

            startActivity(cameractivity);

        } else if (id == R.id.nav_history) {
            Intent historyactivity = new Intent(RatingActivity.this, HistoryActivity.class);

            startActivity(historyactivity);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

        /*
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

                //addFragment();
                //updateFragment(image_url, product_name, energy_100g);
            }
        }
    }
    */

    private ArrayList<String> getUrlsFromServer(){
        //TODO ask 9 urls of food not ranked by current User
        JSONObject data = new JSONObject();
        try {
            send(networkJSON(RANDOM_UNRANKED_FOODS_REQUEST, data));
        } catch (IOException e) {
            // Client not connected...
        }
        System.out.println("------------------request for random unranked foods SENT -------------------");
        return new ArrayList<String>();
    }
}
