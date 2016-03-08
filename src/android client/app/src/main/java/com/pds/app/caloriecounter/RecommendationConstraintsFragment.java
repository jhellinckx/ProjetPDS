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
import static org.calorycounter.shared.Constants.network.*;


public class RecommendationConstraintsFragment extends Fragment {
    private OnItemClickListener listener;
    private RadioButton _recipe;
    private RadioGroup _radioGroup;
    private AutoCompleteTextView _autoComplete;
    private ArrayAdapter<String> _adapter;
    private ArrayList<String> _foodCategories;
    private LinearLayout stickersLayout;

    private LinearLayout constraintsLayout;
    private DailyRecording infosContainer = null;
    private SeekBar tmpSeekBar = null;
    private EditText tmpEditText = null;

    private ArrayList<SeekBar> seekBarList = new ArrayList<SeekBar>();
    private ArrayList<EditText> editTextList = new ArrayList<EditText>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_day_recording,container,false);
        stickersLayout = (LinearLayout) view.findViewById(R.id.day_recording_layout);
        stickersLayout.setOrientation(LinearLayout.VERTICAL);
        initInfosLayout();
        //addEnergyLayout();
        addSliderLayout("Calories du jour : ");
        addSliderLayout("Fat : ");
        addSliderLayout("Protein : ");
        addSliderLayout("Carbohydates : ");
        addListenersToSeekBars(view);
        //addFatLayout();
        //addProteinLayout();
        //addCarbohydratesLayout();
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

    public void addSliderLayout(String title){
        LinearLayout calorieTextLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams calorieSliderContParams_ = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        calorieTextLayout.setLayoutParams(calorieSliderContParams_);
        calorieTextLayout.setOrientation(LinearLayout.HORIZONTAL);
        calorieTextLayout.setGravity(Gravity.CENTER_VERTICAL);

        TextView calorieText = new TextView(getContext());
        LinearLayout.LayoutParams calorieTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        calorieText.setLayoutParams(calorieTextParams);
        calorieText.setTextSize(MAIN_TEXT_SIZE);
        calorieText.setTextColor(MAIN_TEXT_COLOR);
        calorieText.setText(title);
        calorieText.setMaxLines(MAIN_TEXT_MAX_LINES);
        calorieText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        calorieText.setEllipsize(TextUtils.TruncateAt.END);
        calorieTextLayout.addView(calorieText);

        tmpEditText = new EditText(getContext());
        editTextList.add(tmpEditText);
        LinearLayout.LayoutParams calorieSeekBarEditTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tmpEditText.setLayoutParams(calorieSeekBarEditTextParams);
        tmpEditText.setTextSize(MAIN_TEXT_SIZE);
        tmpEditText.setTextColor(MAIN_TEXT_COLOR);
        tmpEditText.setHintTextColor(MAIN_TEXT_COLOR);
        tmpEditText.setHint("");
        tmpEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        tmpEditText.setMaxLines(MAIN_TEXT_MAX_LINES);
        tmpEditText.canScrollHorizontally(LinearLayout.HORIZONTAL);
        tmpEditText.setEllipsize(TextUtils.TruncateAt.END);
        tmpEditText.setFocusable(false);
        calorieTextLayout.addView(tmpEditText);


        constraintsLayout.addView(calorieTextLayout);

        //calorie seekBar+editText
        LinearLayout calorieSliderLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams calorieSliderContParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        calorieSliderLayout.setLayoutParams(calorieSliderContParams);
        calorieSliderLayout.setOrientation(LinearLayout.HORIZONTAL);
        calorieSliderLayout.setGravity(Gravity.CENTER_VERTICAL);


        //calorie - seekBar
        tmpSeekBar = new SeekBar(getContext());
        seekBarList.add(tmpSeekBar);
        LinearLayout.LayoutParams calorieSeekBarParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tmpSeekBar.setLayoutParams(calorieSeekBarParams);
        tmpSeekBar.canScrollHorizontally(LinearLayout.HORIZONTAL);
        calorieSliderLayout.addView(tmpSeekBar);

        //ajout calorie
        constraintsLayout.addView(calorieSliderLayout);
    }




    public void addFooterButton(){
        LinearLayout validateLayout = new LinearLayout(getContext());
        validateLayout.setOrientation(LinearLayout.HORIZONTAL);
        CircularButton validate = new CircularButton(getContext());
        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(IMAGE_WIDTH, IMAGE_HEIGHT);
        buttonParams.gravity = Gravity.RIGHT;
        validate.setLayoutParams(buttonParams);
        validate.setImageResource(R.drawable.ic_check);
        validate.setButtonColor(getResources().getColor(R.color.primary));
        validate.setShadowColor(Color.BLACK);

        validateLayout.addView(new EvenSpaceView(getContext()));
        validateLayout.addView(validate);
        infosContainer.setFooter(validateLayout);

        stickersLayout.addView(infosContainer);

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResults();

            }

        });
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

    private void setText(EditText text, int progress){
        text.setText(Integer.toString(progress));
    }


    private void initRadioButtons(View v){
        _radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
        _recipe = (RadioButton) v.findViewById(R.id.recipeButton);
        _radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == _recipe.getId()) {
                    _autoComplete.setVisibility(View.GONE);
                } else {
                    _autoComplete.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initAutoComplete(View v){
        _autoComplete = (AutoCompleteTextView) v.findViewById(R.id.autoCompleteTextView);
        //String[] foo = new String[] { "Vins" };
        _adapter = new ArrayAdapter<String>(v.getContext(),android.R.layout.select_dialog_item,_foodCategories);
        _autoComplete.setAdapter(_adapter);
        _autoComplete.setThreshold(0);
        _autoComplete.setVisibility(View.GONE);
    }

    private int computeMaxEnergy(){
        Bundle b = getArguments();
        String gender = b.getString("gender");
        float max_energy;
        switch (gender){
            case MAN:
                max_energy = MEN_DAILY_ENERGY;
                break;
            case WOMAN:
                max_energy = WOMEN_DAILY_ENERGY;
                break;
            case TEEN:
                max_energy = TEEN_DAILY_ENERGY;
                break;
            default:
                max_energy = CHILD_DAILY_ENERGY;
                break;

        }
        return (int) ((max_energy/CAL_TO_JOULE_FACTOR)/TOTAL_MEAL_PER_DAY);

    }

    private SeekBar updateSeekBarAndText(SeekBar bar){
        EditText text = null;
        if (bar == seekBarList.get(0)) {
            bar.setMax(computeMaxEnergy());
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

    private String getRadioButtonName(int id){
        if( id == _recipe.getId()){
            return "recipe";
        }
        else{
            return "food";
        }
    }

    public interface OnItemClickListener {
        public void onResultsClick(String energy, String fat, String prot, String carbo, String recipeOrFood, String category);
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
        /*
        if (_autoComplete.getText().toString().isEmpty()) {
            _autoComplete.setText("None");
            return false;
        } else if (_adapter.getCount() == 0 || _adapter.getPosition(_autoComplete.getText().toString()) == -1) {
            _autoComplete.setError("Enter a valid category");
            return true;
        }
        */
        return false;

    }

    public void getResults(){
        if(!checkErrors()) {
            listener.onResultsClick(editTextList.get(0).getText().toString(), editTextList.get(1).getText().toString(),
                    editTextList.get(2).getText().toString(), editTextList.get(3).getText().toString(),"food","None");//, getRadioButtonName(_radioGroup.getCheckedRadioButtonId()),_autoComplete.getText().toString());
        }
    }
}
