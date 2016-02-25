package com.pds.app.caloriecounter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import static org.calorycounter.shared.Constants.network.*;

/**
 * Created by aurelien on 15/12/15.
 */
public class RecommendationConstraintsFragment extends Fragment {
    private OnItemClickListener listener;
    private EditText _energy;
    private EditText _fat;
    private EditText _prot;
    private EditText _carbo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_constraints_step, container, false);
        initEditTexts(view);
        addListenersToSeekBars(view);

        Button next = (Button) view.findViewById(R.id.constraints_res);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getResults();
            }
        });

        return view;
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

    private void initEditTexts(View v){
        _energy = (EditText) v.findViewById(R.id.constraints_cal);
        _fat = (EditText) v.findViewById(R.id.constraints_fat);
        _prot = (EditText) v.findViewById(R.id.constraints_prot);
        _carbo = (EditText) v.findViewById(R.id.constraints_carbo);

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
        if (bar.getId() == R.id.bar_cal){
            bar.setMax(computeMaxEnergy());
            text = _energy;
        }
        else if (bar.getId() == R.id.bar_fat){
            bar.setMax((int)HUMAN_DAILY_FAT);
            text = _fat;
        }
        else if (bar.getId() == R.id.bar_prot){
            bar.setMax((int)HUMAN_DAILY_PROTEINS);
            text = _prot;
        }
        else {
            bar.setMax((int) HUMAN_DAILY_CARBOHYDRATES);
            text = _carbo;
        }

        setText(text, bar.getMax());

        bar.setProgress(bar.getMax());
        return bar;

    }

    private void addListenersToSeekBars(View v){
        addListenerToSeekBar(updateSeekBarAndText((SeekBar) v.findViewById(R.id.bar_cal)), _energy);
        addListenerToSeekBar(updateSeekBarAndText((SeekBar) v.findViewById(R.id.bar_fat)), _fat);
        addListenerToSeekBar(updateSeekBarAndText((SeekBar) v.findViewById(R.id.bar_prot)), _prot);
        addListenerToSeekBar(updateSeekBarAndText((SeekBar) v.findViewById(R.id.bar_carbo)), _carbo);
    }

    public interface OnItemClickListener {
        public void onResultsClick(String energy, String fat, String prot, String carbo);
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

    public void getResults(){

        listener.onResultsClick(_energy.getText().toString(), _fat.getText().toString(),
                _prot.getText().toString(), _carbo.getText().toString());
    }
}
