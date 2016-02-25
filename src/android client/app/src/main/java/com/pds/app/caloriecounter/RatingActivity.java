package com.pds.app.caloriecounter;




import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.Toast;


import com.squareup.picasso.Picasso;

import org.json.simple.JSONObject;
import java.io.IOException;
import java.util.ArrayList;


import static org.calorycounter.shared.Constants.network.*;

public class RatingActivity extends HomeActivity implements RateFoodDialogFragment.RateFoodDialogListener{

    private static final int NB_RATINGS = 9;

    private GridView gridView;
    private Button _validButton;
    private ArrayList<String> urls;
    private ArrayList<Float> ratings;
    private ArrayList<String> names;



    private void initializer(ArrayList<?> alist){
        for (int i = 0; i < NB_RATINGS; i++){
            alist.add(null);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        v = getLayoutInflater().inflate(R.layout.activity_rating,frameLayout);
        _validButton = (Button) v.findViewById(R.id.rating_button);
        urls = new ArrayList<String>();
        ratings = new ArrayList<Float>();
        names = new ArrayList<String>();
        gridView = (GridView) findViewById(R.id.gridView);
        initializer(ratings);
        initializer(urls);
        initializer(names);
        setUpUrls();

    }

    private void setUpUrls(){
        getUrlsFromServer();

    }

    private void addListenerGridView(){
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                //position defines which food in urls
                RateFoodDialogFragment frag = new RateFoodDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putString("url", urls.get(position));
                bundle.putString("name", names.get(position));
                frag.setArguments(bundle);
                frag.show(getFragmentManager(), "titletest");

            }
        });
    }

    private void addListenerButton(){
        _validButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRates();
                refreshGridView();

            }
        });
    }

    public void sendRates(){

        /*
         *  This Method sends the rates and the urls to the server. The index of a rate/an url
         *  in the lists (urls and rates) is added to the keys in the JSONObject. This allows the
         *  server to retrieve the correct matching between an url (i.e. a food) and its associated
         *  rating.
         */


        JSONObject data = new JSONObject();
        JSONObject data2 = new JSONObject();
        int index1 = 0;
        int index2 = 0;
        for (int i = 0; i < RatingActivity.NB_RATINGS; i++){
            if (ratings.get(i) != null) {
                if(i<NB_RATINGS/2){
                    data.put(FOOD_IMAGE_URL + Integer.toString(index1), urls.get(i));
                    data.put(FOOD_RATING + Integer.toString(index1), ratings.get(i));
                    index1 += 1;
                }
                else{
                    data2.put(FOOD_IMAGE_URL + Integer.toString(index2), urls.get(i));
                    data2.put(FOOD_RATING + Integer.toString(index2), ratings.get(i));
                    index2+=1;
                }

            }
        }

        send(networkJSON(SEND_RATINGS_REQUEST, data));
        send(networkJSON(SEND_RATINGS_REQUEST, data2));

    }

    private void resetRatings(){
        for (int i = 0; i < NB_RATINGS; i++){
            ratings.set(i, null);
        }
    }

    public void refreshGridView(){
        Picasso p = Picasso.with(RatingActivity.this);
        p.cancelTag("tag");
        resetRatings();
        setUpUrls();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog, int position, float rating){
        View v = (View) gridView.getItemAtPosition(position);
        RatingBar rbar = (RatingBar) v.findViewById(R.id.grid_ratingBar);
        rbar.setRating(rating);
        ratings.set(position, rating);
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        // Do nothing, Simply dismiss the Dialog.
    }



    public void handleMessage(JSONObject msg){
        Log.d("RATINGACTIVITY HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(RANDOM_UNRANKED_FOODS_REQUEST)){
            String response =  (String)data.get(RANDOM_UNRANKED_FOODS_RESPONSE);
            if(response.equals(RANDOM_UNRANKED_FOODS_SUCCESS)){
                for(int i = 0; i < NUMBER_RANDOM_FOODS ; ++i){
                    urls.set(i, (String) data.get(FOOD_IMAGE_URL + String.valueOf(i)));
                    names.set(i, (String) data.get(FOOD_NAME + String.valueOf(i)));

                }
            }
        }
        runOnUiThread(new Runnable() {
            public void run() {
                gridView.setAdapter(new ImageAdapter(RatingActivity.this, urls));
                addListenerGridView();
                addListenerButton();
            }
        });
    }



    private ArrayList<String> getUrlsFromServer(){
        JSONObject data = new JSONObject();
        send(networkJSON(RANDOM_UNRANKED_FOODS_REQUEST, data));

        System.out.println("------------------request for random unranked foods SENT -------------------");
        return new ArrayList<String>();
    }

}
