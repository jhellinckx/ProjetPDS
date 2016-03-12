package com.pds.app.caloriecounter;




import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pds.app.caloriecounter.dayrecording.DailyRecording;
import com.pds.app.caloriecounter.dayrecording.DayRecordingActivity;
import com.pds.app.caloriecounter.itemview.EdibleItemActionCallback;
import com.pds.app.caloriecounter.itemview.EdibleItemList;
import com.pds.app.caloriecounter.itemview.RatingEdibleItemList;
import com.pds.app.caloriecounter.rawlibs.CircularButton;
import com.pds.app.caloriecounter.utils.EvenSpaceView;
import com.shehabic.droppy.DroppyClickCallbackInterface;
import com.shehabic.droppy.DroppyMenuPopup;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.List;


import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_EXPANDABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_RATABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_HEIGHT;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_WIDTH;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_SIZE;
import static org.calorycounter.shared.Constants.network.*;
import org.calorycounter.shared.models.EdibleItemImage;

public class RatingActivity extends MenuNavigableActivity implements RateFoodDialogFragment.RateFoodDialogListener, EdibleItemActionCallback{

    private static final int NB_RATINGS = 9;

    private GridView gridView;
    private Button _validButton;
    private ArrayList<String> urls;
    private ArrayList<Float> ratings;
    private ArrayList<EdibleItem> names;
    private ArrayList<EdibleItemImage> images;
    private LinearLayout stickersLayout;
    //private DailyRecording foodsContainer;
    private DailyRecording ratingContainer;
    private Context context;
    private List<EdibleItem> foodsToBeRated;
    private Spinner categoriesSpinner = null;
    private LinearLayout ratingFoodsLayout;



    private void initializer(ArrayList<?> alist){
        for (int i = 0; i < NB_RATINGS; i++){
            alist.add(null);
        }
    }

    private void initRatingFoodsLayout(){
        ratingFoodsLayout = new LinearLayout(this);
        LinearLayout.LayoutParams textContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        ratingFoodsLayout.setLayoutParams(textContParams);
        ratingFoodsLayout.setOrientation(LinearLayout.VERTICAL);
        ratingFoodsLayout.setGravity(Gravity.CENTER_VERTICAL);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //v = getLayoutInflater().inflate(R.layout.activity_rating,frameLayout);

        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
        stickersLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);
        stickersLayout.setOrientation(LinearLayout.VERTICAL);
        foodsToBeRated = new ArrayList<>();

        /*
        //_validButton = (Button) v.findViewById(R.id.rating_button);
        urls = new ArrayList<String>();
        ratings = new ArrayList<Float>();
        names = new ArrayList<EdibleItem>();
        images = new ArrayList<>();
        //gridView = (GridView) findViewById(R.id.gridView);
        initializer(ratings);
        initializer(urls);
        initializer(names);
        //getUrlsFromServer();
        */
        context= v.getContext();

        //initRatingFoodsLayout();

        //ratingContainer = new DailyRecording(this, "Foods", new EdibleItemList(this, foodsToBeRated, this,FLAG_RATABLE, FLAG_EXPANDABLE));
        addHeader();
        addFoodListLayout();
        //addFooterButton();
    }

    private void addHeader() {

        LinearLayout categorieTextLayout = new LinearLayout(this);
        LinearLayout.LayoutParams categorieTextContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        categorieTextLayout.setLayoutParams(categorieTextContParams);
        categorieTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        categorieTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        //Age Bracket - texte
        TextView categorieText = new TextView(this);
        LinearLayout.LayoutParams categorieTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        categorieText.setLayoutParams(categorieTextParams);
        categorieText.setTextSize(MAIN_TEXT_SIZE);
        categorieText.setTextColor(MAIN_TEXT_COLOR);
        categorieText.setText("Catégorie de Recettes : ");
        categorieText.setMaxLines(MAIN_TEXT_MAX_LINES);
        categorieText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        categorieText.setEllipsize(TextUtils.TruncateAt.END);
        categorieTextLayout.addView(categorieText);

        categoriesSpinner = new Spinner(this);
        LinearLayout.LayoutParams categoriesSpinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        categoriesSpinner.setLayoutParams(categoriesSpinnerParams);

        categoriesSpinner.canScrollHorizontally(LinearLayout.HORIZONTAL);
        //initSpinner();
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                //updateSeekBarAndText();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }
        });

        categorieTextLayout.addView(categoriesSpinner);
        stickersLayout.addView(categorieTextLayout);
    }

    private void addFoodListLayout(){

        DailyRecording ratingContainer = new DailyRecording(this, "FOODS", new EdibleItemList(this, foodsToBeRated, this,FLAG_RATABLE, FLAG_EXPANDABLE));

        LinearLayout validateLayout = new LinearLayout(this);
        validateLayout.setOrientation(LinearLayout.HORIZONTAL);

        CircularButton validateButton = new CircularButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        buttonParams.gravity = Gravity.RIGHT;
        validateButton.setLayoutParams(buttonParams);
        validateButton.setImageResource(R.drawable.ic_done_white_24dp);
        validateButton.setButtonColor(getResources().getColor(R.color.primary));
        validateButton.setShadowColor(Color.BLACK);

        validateLayout.addView(new EvenSpaceView(this));
        validateLayout.addView(validateButton);
        ratingContainer.setFooter(validateLayout);

        stickersLayout.addView(ratingContainer);
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
                bundle.putString("name", names.get(position).getProductName());
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
        int index = 0;
        for (int i = 0; i < RatingActivity.NB_RATINGS; i++){
            if (ratings.get(i) != null) {

                data.put(FOOD_IMAGE_URL + Integer.toString(index), urls.get(i));
                data.put(FOOD_RATING + Integer.toString(index), ratings.get(i));
                index += 1;

            }
        }
        send(networkJSON(SEND_RATINGS_REQUEST, data));
    }

    private void resetRatings(){
        for (int i = 0; i < NB_RATINGS; i++){
            ratings.set(i, null);
        }
    }

    public void refreshGridView(){
        //Picasso p = Picasso.with(RatingActivity.this);
        //p.cancelTag("tag");
        resetRatings();
        //getUrlsFromServer();
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
        Log.d("RateACTI HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(RANDOM_UNRANKED_FOODS_REQUEST)){
            String response =  (String)data.get(RANDOM_UNRANKED_FOODS_RESPONSE);
            if(response.equals(RANDOM_UNRANKED_FOODS_SUCCESS)){
                for(int i = 0; i < NUMBER_RANDOM_FOODS ; ++i){
                    EdibleItem item = new Food();
                    item.initFromJSON((JSONObject) data.get(FOOD_NAME + String.valueOf(i)));
                    names.set(i, item);
                    System.out.println(item.getProductName());
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        gridView.setAdapter(new ImageAdapter(RatingActivity.this, names));
                        addListenerGridView();
                        addListenerButton();
                        //initAll();
                    }
                });

            }
        }
    }



    private ArrayList<String> getUrlsFromServer(){
        JSONObject data = new JSONObject();
        send(networkJSON(RANDOM_UNRANKED_FOODS_REQUEST, data));

        System.out.println("------------------request for random unranked foods SENT -------------------");
        return new ArrayList<String>();
    }

    public void addFooterButton(){
        LinearLayout validateLayout = new LinearLayout(this);
        validateLayout.setOrientation(LinearLayout.HORIZONTAL);
        CircularButton validate = new CircularButton(this);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        buttonParams.gravity = Gravity.RIGHT;
        validate.setLayoutParams(buttonParams);
        validate.setImageResource(R.drawable.ic_done_white_24dp);
        validate.setButtonColor(getResources().getColor(R.color.primary));
        validate.setShadowColor(Color.BLACK);

        validateLayout.addView(new EvenSpaceView(this));
        validateLayout.addView(validate);
        ratingContainer.setFooter(validateLayout);

        stickersLayout.addView(ratingContainer);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }

        });
    }

    @Override
    public void onAddEdibleItem(EdibleItem item){
    }

    @Override
    public void onRateEdibleItem(EdibleItem item){

    }

    @Override
    public void onExpandEdibleItem(EdibleItem item){

    }

    @Override
    public void onCheckEdibleItem(EdibleItem item){

    }

    @Override
    public void onRemoveEdibleItem(EdibleItem item){

    }

}
