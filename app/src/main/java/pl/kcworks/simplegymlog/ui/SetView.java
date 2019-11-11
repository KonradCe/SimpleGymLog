package pl.kcworks.simplegymlog.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import pl.kcworks.simplegymlog.R;

public class SetView extends LinearLayout {

    private TextView numberTextView, additionalInfoTextView, weightTextView, repsTextView;
    private int number;
    private double trainingMax;
    private int percentageOfTm;
    private int reps;
    private double weight;

    public SetView(Context context) {
        super(context);
        init(context);
    }

    public SetView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SetView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.single_set_view, this);

        numberTextView = findViewById(R.id.set_number);
        additionalInfoTextView = findViewById(R.id.set_additional_info);
        weightTextView = findViewById(R.id.set_weight);
        repsTextView = findViewById(R.id.set_reps);
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        setNumberTextView(number);
    }

    public void setOnClickListenerOnNumberTextView(OnClickListener onClickListener) {
        numberTextView.setOnClickListener(onClickListener);
    }

    public double getTrainingMax() {
        return trainingMax;
    }

    public void setTrainingMax(double trainingMax) {
        this.trainingMax = trainingMax;
        updateAdditionalInfoTextView();
    }

    public int getPercentageOfTm() {
        return percentageOfTm;
    }

    public void setPercentageOfTm(int percentageOfRmMax) {
        this.percentageOfTm = percentageOfRmMax;
        updateAdditionalInfoTextView();
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
        repsTextView.setText(Integer.toString(reps));
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
        weightTextView.setText(Double.toString(weight));
    }

    private void setNumberTextView(int setNumber) {
        numberTextView.setText(Integer.toString(setNumber));
    }

    private void updateAdditionalInfoTextView() {
        if (trainingMax != 0 && percentageOfTm != 0) {
            String additionalInfoString = percentageOfTm + "% of " + trainingMax;
            additionalInfoTextView.setText(additionalInfoString);
        }
    }

    public void setAdditionalInfoIsVisible(boolean isVisible) {
        if (isVisible) {
            additionalInfoTextView.setVisibility(VISIBLE);
        }
        else {
            additionalInfoTextView.setVisibility(INVISIBLE);
        }
    }

    public boolean hasAdditionalInfo() {
        return getTrainingMax() != 0;
    }

    @Override
    public String toString() {
        return "SetView{" +
                "number=" + number +
                ", trainingMax=" + trainingMax +
                ", percentageOfTm=" + percentageOfTm +
                ", reps=" + reps +
                ", weight=" + weight +
                '}';
    }

}
