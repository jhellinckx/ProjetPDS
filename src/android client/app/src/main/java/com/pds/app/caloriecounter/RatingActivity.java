package com.pds.app.caloriecounter;


import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.pds.app.caloriecounter.dayrecording.DailyRecording;
import com.pds.app.caloriecounter.itemview.EdibleItemActionCallback;
import com.pds.app.caloriecounter.itemview.EdibleItemList;
import com.pds.app.caloriecounter.rawlibs.CircularButton;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.Food;
import org.calorycounter.shared.models.Recipe;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_EXPANDABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemList.FLAG_RATABLE;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_HEIGHT;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_WIDTH;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_SIZE;
import static org.calorycounter.shared.Constants.network.CATEGORY_NAME;
import static org.calorycounter.shared.Constants.network.DATA;
import static org.calorycounter.shared.Constants.network.FOOD_ID;
import static org.calorycounter.shared.Constants.network.FOOD_NAME;
import static org.calorycounter.shared.Constants.network.FOOD_QUANTITY;
import static org.calorycounter.shared.Constants.network.FOOD_RATING;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_CARBOHYDRATES;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_ENERGY;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_FAT;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_PROTEINS;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_SATURATED_FAT;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_SODIUM;
import static org.calorycounter.shared.Constants.network.FOOD_TOTAL_SUGARS;
import static org.calorycounter.shared.Constants.network.NUMBER_RANDOM_FOODS;
import static org.calorycounter.shared.Constants.network.RANDOM_RECIPES_FOR_CATEGORY_REQUEST;
import static org.calorycounter.shared.Constants.network.RANDOM_UNRANKED_FOODS_REQUEST;
import static org.calorycounter.shared.Constants.network.RANDOM_UNRANKED_FOODS_RESPONSE;
import static org.calorycounter.shared.Constants.network.RANDOM_UNRANKED_FOODS_SUCCESS;
import static org.calorycounter.shared.Constants.network.RECIPE_CATEGORIES_REQUEST_FROM_RATING;
import static org.calorycounter.shared.Constants.network.RECIPE_CATEGORY;
import static org.calorycounter.shared.Constants.network.RECIPE_OR_FOOD;
import static org.calorycounter.shared.Constants.network.REQUEST_TYPE;
import static org.calorycounter.shared.Constants.network.SEND_RATINGS_REQUEST;
import static org.calorycounter.shared.Constants.network.networkJSON;

public class RatingActivity extends MenuNavigableActivity implements RateFoodDialogFragment.RateFoodDialogListener, EdibleItemActionCallback{

    private static final int NB_RATINGS = NUMBER_RANDOM_FOODS;

    private LinearLayout stickersLayout;
    private Context context;
    private ArrayList<EdibleItem> foodsToBeRated;
    private Spinner categoriesSpinner = null;
    private LinearLayout ratingFoodsLayout;
    private static ArrayList<String> recipeCategories = new ArrayList<String>();
    private int id;
    private DailyRecording ratingContainer;
    private Boolean init=true;
    private EdibleItemList edibleItemList;
    private LinearLayout loadingLayout;
    private boolean loadingLayoutEnabled = true;



    private void initializer(ArrayList<?> alist){
        for (int i = 0; i < NB_RATINGS; i++){
            alist.add(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        v = getLayoutInflater().inflate(R.layout.activity_day_recording,frameLayout);
        stickersLayout = (LinearLayout) v.findViewById(R.id.day_recording_layout);
        stickersLayout.setOrientation(LinearLayout.VERTICAL);
        foodsToBeRated = new ArrayList<>();

        context= v.getContext();


        addHeader();
        sendRecipeCategoriesRequest();

    }

    private void addLoadingLayout() {
        loadingLayout = new LinearLayout(this);
        LinearLayout.LayoutParams loadingParams_ = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        loadingLayout.setLayoutParams(loadingParams_);
        loadingLayout.setOrientation(LinearLayout.HORIZONTAL);
        loadingLayout.setGravity(Gravity.CENTER);

        ProgressBar progressBar = new ProgressBar(this);
        LinearLayout.LayoutParams loadingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(loadingParams);
        progressBar.setIndeterminate(true);
        loadingLayout.addView(progressBar);

        stickersLayout.addView(loadingLayout);
    }

    private void sendRecipeCategoriesRequest() {
        JSONObject data = new JSONObject();
        send(networkJSON(RECIPE_CATEGORIES_REQUEST_FROM_RATING, data));
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
        categorieText.setText("Catégorie : ");
        categorieText.setPadding(20,0,0,0);
        categorieText.setMaxLines(MAIN_TEXT_MAX_LINES);
        categorieText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        categorieText.setEllipsize(TextUtils.TruncateAt.END);
        categorieTextLayout.addView(categorieText);

        categoriesSpinner = new Spinner(this);
        LinearLayout.LayoutParams categoriesSpinnerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        categoriesSpinner.setLayoutParams(categoriesSpinnerParams);

        categoriesSpinner.canScrollHorizontally(LinearLayout.HORIZONTAL);
        categoriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                stickersLayout.removeView(ratingContainer);
                if(!loadingLayoutEnabled){
                    loadingLayoutEnabled = true;
                    addLoadingLayout();
                }
                sendFoodsToBeRatedRequest();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {}
        });

        categorieTextLayout.addView(categoriesSpinner);
        stickersLayout.addView(categorieTextLayout);
    }

    private void addFoodListLayout(){
        edibleItemList = new EdibleItemList(this, foodsToBeRated, this,FLAG_RATABLE, FLAG_EXPANDABLE);
        ratingContainer = new DailyRecording(this, "FOODS", edibleItemList);

        LinearLayout validateLayout = new LinearLayout(this);
        validateLayout.setOrientation(LinearLayout.HORIZONTAL);

        final CircularButton validateButton = new CircularButton(this);
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

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateButton.setClickable(false);
                stickersLayout.removeView(ratingContainer);
                if(!loadingLayoutEnabled){
                    addLoadingLayout();
                }
                sendFoodsToBeRatedRequest();

            }

        });
    }

    private void updateAddFoodListLayout(){
        stickersLayout.removeView(ratingContainer);
        addFoodListLayout();
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog,long id, float rating){
        JSONObject data = new JSONObject();
        data.put(FOOD_ID, id);
        data.put(FOOD_RATING, rating);

        for(int i =0; i<foodsToBeRated.size();++i){
            if(foodsToBeRated.get(i).getId() == id){
                edibleItemList.setRatingBar(foodsToBeRated.get(i),rating);
            }
        }

        send(networkJSON(SEND_RATINGS_REQUEST, data));
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog){
        // Do nothing, Simply dismiss the Dialog.
    }



    public void handleMessage(JSONObject msg){
        Log.d("RateACTI HANDLE MSG", msg.toString());
        String request = (String) msg.get(REQUEST_TYPE);
        JSONObject data = (JSONObject)msg.get(DATA);
        if(request.equals(RANDOM_RECIPES_FOR_CATEGORY_REQUEST)){
            String response =  (String)data.get(RANDOM_UNRANKED_FOODS_RESPONSE);
            if(response.equals(RANDOM_UNRANKED_FOODS_SUCCESS)){
                onRecipesReceived(data);
            }
        }
        if(request.equals(RECIPE_CATEGORIES_REQUEST_FROM_RATING)){
            System.out.println("---------------------------ok");
            for(int i=0; i<data.size(); i++) {
                recipeCategories.add(((String) data.get(CATEGORY_NAME + String.valueOf(i))));
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    initSpinner();
                }
            });

        }
    }

    private void onRecipesReceived(JSONObject data){
        loadingLayoutEnabled = false;
        foodsToBeRated = new ArrayList<>();
        initializer(foodsToBeRated);

        JSONArray jsonRecipes = (JSONArray) data.get(FOOD_NAME);
        for(int i = 0; i < NUMBER_RANDOM_FOODS ; ++i){
            EdibleItem item = new Recipe();
            item.initFromJSON((JSONObject) jsonRecipes.get(i));
            foodsToBeRated.set(i, item);
            System.out.println(item.getProductName());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stickersLayout.removeView(loadingLayout);
                if(init){
                    addFoodListLayout();
                    init=false;
                }else{
                    updateAddFoodListLayout();
                }
            }
        });
    }



    private ArrayList<String> getUrlsFromServer(){
        JSONObject data = new JSONObject();
        send(networkJSON(RANDOM_UNRANKED_FOODS_REQUEST, data));

        System.out.println("------------------request for random unranked foods SENT -------------------");
        return new ArrayList<String>();
    }

    private void initSpinner(){
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>(this,R.layout.spinner_item, createCategoriesList());
        categoriesSpinner.setAdapter(ageAdapter);
    }

    private ArrayList<String> createCategoriesList(){
        ArrayList<String> categories = new ArrayList<String>();
        for(String category : recipeCategories)
        categories.add(category);
        return categories;
    }

    private void sendFoodsToBeRatedRequest(){
        JSONObject data = new JSONObject();
        data.put(RECIPE_CATEGORY, categoriesSpinner.getSelectedItem().toString());
        send(networkJSON(RANDOM_RECIPES_FOR_CATEGORY_REQUEST, data));
    }

    @Override
    public void onAddEdibleItem(EdibleItem item){
    }

    @Override
    public void onRateEdibleItem(final EdibleItem item){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RateFoodDialogFragment frag = new RateFoodDialogFragment();
                Bundle bundle = new Bundle();
                bundle.putLong("id", item.getId());
                bundle.putString("name", item.getProductName());
                frag.setArguments(bundle);
                frag.show(getFragmentManager(), "titletest");
            }
        });

    }

    @Override
    public void onExpandEdibleItem(EdibleItem item){
        Bundle b = new Bundle();
        b.putString(FOOD_NAME, item.getProductName());

        b.putFloat(FOOD_TOTAL_ENERGY, item.getTotalEnergy());
        b.putFloat(FOOD_TOTAL_FAT, item.getTotalFat());
        b.putFloat(FOOD_TOTAL_PROTEINS, item.getTotalProteins());
        b.putFloat(FOOD_TOTAL_CARBOHYDRATES, item.getTotalCarbohydrates());
        if(item instanceof Food){
            b.putString(FOOD_QUANTITY, item.getQuantity());
            b.putFloat(FOOD_TOTAL_SUGARS, item.getTotalSugars());
            b.putFloat(FOOD_TOTAL_SODIUM, item.getTotalSalt());
            b.putFloat(FOOD_TOTAL_SATURATED_FAT, item.getTotalSaturatedFat());
            b.putString(RECIPE_OR_FOOD, "food");
        }else{
            b.putString(RECIPE_OR_FOOD, "recipe");
        }
        ItemInfoDialog dialog = new ItemInfoDialog();
        dialog.setArguments(b);
        dialog.show(getFragmentManager(), "infos");

    }

    @Override
    public void onCheckEdibleItem(EdibleItem item){

    }

    @Override
    public void onRemoveEdibleItem(EdibleItem item){

    }

}
