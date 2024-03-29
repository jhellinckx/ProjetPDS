package com.pds.app.caloriecounter;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pds.app.caloriecounter.dayrecording.DailyRecording;
import com.pds.app.caloriecounter.rawlibs.CircularButton;
import com.pds.app.caloriecounter.utils.EvenSpaceView;

import java.util.ArrayList;

import static com.pds.app.caloriecounter.GraphicsConstants.Global.TITLE_RECOM_CONSTR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_HEIGHT;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.IMAGE_WIDTH;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_COLOR;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_MAX_LINES;
import static com.pds.app.caloriecounter.GraphicsConstants.ItemSticker.MAIN_TEXT_SIZE;
import static org.calorycounter.shared.Constants.network.HUMAN_DAILY_CARBOHYDRATES;
import static org.calorycounter.shared.Constants.network.HUMAN_DAILY_FAT;
import static org.calorycounter.shared.Constants.network.HUMAN_DAILY_PROTEINS;


public class RecommendationConstraintsFragment extends Fragment {
    private OnItemClickListener listener;
    private RadioButton _recipe;
    private RadioGroup _radioGroup;
    private AutoCompleteTextView _autoComplete;
    private ArrayAdapter<String> _adapter;
    private ArrayList<String> _categoriesNames;
    private LinearLayout stickersLayout;

    private LinearLayout constraintsLayout;
    private DailyRecording infosContainer = null;
    private SeekBar tmpSeekBar = null;
    private EditText tmpEditText = null;

    private ArrayList<SeekBar> seekBarList = new ArrayList<>();
    private ArrayList<EditText> editTextList = new ArrayList<>();
    private ArrayList<LinearLayout> sliderLayouts = new ArrayList<>();
    private ArrayList<LinearLayout> sliderTextLayouts = new ArrayList<>();

    private LinearLayout loadingLayout;
    private LinearLayout calorieTextLayout;
    private LinearLayout validateLayout;

    private Boolean isReceipt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_day_recording, container, false);
        stickersLayout = (LinearLayout) view.findViewById(R.id.day_recording_layout);
        stickersLayout.setOrientation(LinearLayout.VERTICAL);
        Bundle b = this.getArguments();
        _categoriesNames = b.getStringArrayList("categoriesNames");
        isReceipt = b.getBoolean("isReceipt");
        initInfosLayout();
        //addEnergyLayout();
        addSliderLayout("Calories du jour : ");
        addSliderLayout("Lipides : ");
        addSliderLayout("Protéines : ");
        addSliderLayout("Glucides : ");
        addListenersToSeekBars(view);
        addCategories();
        initAutoComplete(view);
        infosContainer = new DailyRecording(getContext(), TITLE_RECOM_CONSTR, constraintsLayout);
        addFooterButton();


        return view;
    }

    private void initInfosLayout(){
        constraintsLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams textContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        constraintsLayout.setLayoutParams(textContParams);
        constraintsLayout.setOrientation(LinearLayout.VERTICAL);
        constraintsLayout.setGravity(Gravity.CENTER_VERTICAL);

    }

    private void addLoadingLayout() {
        loadingLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams loadingParams_ = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        loadingLayout.setLayoutParams(loadingParams_);
        loadingLayout.setOrientation(LinearLayout.HORIZONTAL);
        loadingLayout.setGravity(Gravity.CENTER);

        ProgressBar progressBar = new ProgressBar(getContext());
        LinearLayout.LayoutParams loadingParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        progressBar.setLayoutParams(loadingParams);
        progressBar.setIndeterminate(true);
        loadingLayout.addView(progressBar);

        constraintsLayout.addView(loadingLayout);

    }

    public void addSliderLayout(String title){
        LinearLayout sliderTextLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams sliderContParams_ = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sliderTextLayout.setLayoutParams(sliderContParams_);
        sliderTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        sliderTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView sliderText = new TextView(getContext());
        LinearLayout.LayoutParams calorieTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        sliderText.setLayoutParams(calorieTextParams);
        sliderText.setTextSize(MAIN_TEXT_SIZE);
        sliderText.setTextColor(MAIN_TEXT_COLOR);
        sliderText.setText(title);
        sliderText.setMaxLines(MAIN_TEXT_MAX_LINES);
        sliderText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        sliderText.setEllipsize(TextUtils.TruncateAt.END);
        sliderTextLayout.addView(sliderText);

        tmpEditText = new EditText(getContext());
        editTextList.add(tmpEditText);
        LinearLayout.LayoutParams SliderEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tmpEditText.setLayoutParams(SliderEditTextParams);
        tmpEditText.setTextSize(MAIN_TEXT_SIZE);
        tmpEditText.setTextColor(MAIN_TEXT_COLOR);
        tmpEditText.setHintTextColor(MAIN_TEXT_COLOR);
        tmpEditText.setHint("");
        tmpEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        tmpEditText.setMaxLines(MAIN_TEXT_MAX_LINES);
        tmpEditText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        tmpEditText.setEllipsize(TextUtils.TruncateAt.END);
        tmpEditText.setFocusable(false);
        sliderTextLayout.addView(tmpEditText);

        sliderTextLayouts.add(sliderTextLayout);
        constraintsLayout.addView(sliderTextLayout);

        //calorie seekBar+editText
        LinearLayout sliderLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams calorieSliderContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        sliderLayout.setLayoutParams(calorieSliderContParams);
        sliderLayout.setOrientation(LinearLayout.HORIZONTAL);
        sliderLayout.setGravity(Gravity.CENTER_VERTICAL);


        //calorie - seekBar
        tmpSeekBar = new SeekBar(getContext());
        seekBarList.add(tmpSeekBar);
        LinearLayout.LayoutParams sliderParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tmpSeekBar.setLayoutParams(sliderParams);
        tmpSeekBar.canScrollHorizontally(LinearLayout.HORIZONTAL);
        sliderLayout.addView(tmpSeekBar);

        //ajout calorie
        sliderLayouts.add(sliderLayout);
        constraintsLayout.addView(sliderLayout);
    }

    public void addCategories(){
        calorieTextLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams calorieSliderContParams_ = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        calorieTextLayout.setLayoutParams(calorieSliderContParams_);
        calorieTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        calorieTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        _autoComplete = new AutoCompleteTextView(getContext());
        LinearLayout.LayoutParams AutoCompleteTextViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        _autoComplete.setLayoutParams(AutoCompleteTextViewParams);
        _autoComplete.setTextSize(MAIN_TEXT_SIZE);
        _autoComplete.setTextColor(MAIN_TEXT_COLOR);
        _autoComplete.setHint("Entrez une catégorie");
        _autoComplete.setThreshold(0);
        _autoComplete.setVisibility(View.VISIBLE);
        _autoComplete.setEllipsize(TextUtils.TruncateAt.END);
        calorieTextLayout.addView(_autoComplete);
        constraintsLayout.addView(calorieTextLayout);

    }





    public void addFooterButton(){
        validateLayout = new LinearLayout(getContext());
        validateLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        buttonParams.gravity = Gravity.RIGHT;
        final CircularButton validate = new CircularButton(getContext());
        validate.setLayoutParams(buttonParams);
        validate.setImageResource(R.drawable.ic_done_white_24dp);
        validate.setButtonColor(getResources().getColor(R.color.primary));
        validate.setShadowColor(Color.BLACK);

        validateLayout.addView(new EvenSpaceView(getContext()));
        validateLayout.addView(validate);
        infosContainer.setFooter(validateLayout);

        stickersLayout.addView(infosContainer);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeViews();
                infosContainer.setTitle("Calcul des recommandations en cours");
                addLoadingLayout();
                getResults();

            }

        });
    }

    private void removeViews(){
        constraintsLayout.removeView(calorieTextLayout);
        for(LinearLayout ll : sliderTextLayouts){
            constraintsLayout.removeView(ll);
        }
        for(LinearLayout ll : sliderLayouts){
            constraintsLayout.removeView(ll);
        }
        validateLayout.setVisibility(View.GONE);
    }

    private void addListenerToSeekBar(SeekBar bar, final EditText text){
        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setText(EditText text, int progress) {
        text.setText(Integer.toString(progress));
    }


    private void initAutoComplete(View v){
        //String[] foo = new String[] { "Vins" };
        _adapter = new ArrayAdapter<String>(v.getContext(),R.layout.spinner_item,_categoriesNames);
        _autoComplete.setAdapter(_adapter);
        _autoComplete.setThreshold(0);
    }

    private float computeMaxEnergy(){
        Bundle b = getArguments();
        float max_energy = b.getFloat("maxCal");
        return max_energy;

    }

    private SeekBar updateSeekBarAndText(SeekBar bar){
        EditText text = null;
        if (bar == seekBarList.get(0)) {
            bar.setMax((int)computeMaxEnergy());
            text = editTextList.get(0);
        }
        else if (bar == seekBarList.get(1)){
            bar.setMax((int)HUMAN_DAILY_FAT);
            text = editTextList.get(1);
        }
        else if (bar == seekBarList.get(2)){
            bar.setMax((int)HUMAN_DAILY_PROTEINS);
            text = editTextList.get(2);
        }
        else {
            bar.setMax((int) HUMAN_DAILY_CARBOHYDRATES);
            text = editTextList.get(3);
        }

        setText(text, bar.getMax());

        bar.setProgress(bar.getMax());
        return bar;

    }

    private void addListenersToSeekBars(View v){
        for(int i = 0 ; i<4 ; ++i){
            addListenerToSeekBar(updateSeekBarAndText(seekBarList.get(i)), editTextList.get(i));
        }
    }

    public interface OnItemClickListener {
        public void onResultsClick(String energy, String fat, String prot, String carbo, String recipeOrFood, String category, DailyRecording container);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnItemClickListener) {
            listener = (OnItemClickListener) context;
        } else {
            throw new ClassCastException(context.toString() + "not instance of this.OnItemClickListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }

    private boolean checkErrors() {
        if(isReceipt){
            if (_autoComplete.getText().toString().isEmpty()) {
                _autoComplete.setText("None");
                return false;
            } else if (_adapter.getCount() == 0 || _adapter.getPosition(_autoComplete.getText().toString()) == -1) {
                _autoComplete.setError("Enter a valid category");
                return true;
            }
        }
        return false;
    }

    public void getResults(){
        if(!checkErrors()) {
            String recipeOrFood;

            if(isReceipt){
                recipeOrFood= "recipe";
                listener.onResultsClick(editTextList.get(0).getText().toString(), editTextList.get(1).getText().toString(),
                        editTextList.get(2).getText().toString(), editTextList.get(3).getText().toString(),recipeOrFood,_autoComplete.getText().toString(), infosContainer);
            }
            else{
                recipeOrFood = "food";
                listener.onResultsClick(editTextList.get(0).getText().toString(), editTextList.get(1).getText().toString(),
                        editTextList.get(2).getText().toString(), editTextList.get(3).getText().toString(),recipeOrFood,_autoComplete.getText().toString(), infosContainer);
            }
        }
    }
}
