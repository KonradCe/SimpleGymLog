package pl.kcworks.simplegymlog;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.SingleSet;

public class AddExerciseActivity extends AppCompatActivity implements View.OnClickListener {
    private Button mAddSetButton;
    private Button mRemoveSetButton;
    private Button mSaveExerciseButton;
    private LinearLayout mSetListLinearLayout;
    private EditText mExerciseNameEditText;
    private EditText mExerciseDateEditText;

    // variable to keep track and display number of sets added by the user
    private int mSetNumber = 0;

    GymLogViewModel mGymLogViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        setUpViews();

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);


    }

    private void setUpViews() {
        // setting up views
        mAddSetButton = findViewById(R.id.addExerciseFragment_bt_addSet);
        mAddSetButton.setOnClickListener(this);

        mRemoveSetButton = findViewById(R.id.addExerciseFragment_bt_deleteSet);
        mRemoveSetButton.setOnClickListener(this);

        mSaveExerciseButton = findViewById(R.id.addExerciseFragment_bt_saveExercise);
        mSaveExerciseButton.setOnClickListener(this);

        mSetListLinearLayout = findViewById(R.id.addExerciseFragment_ll_listOfSets);

        mExerciseNameEditText = findViewById(R.id.addExerciseFragment_et_exerciseName);

        mExerciseDateEditText = findViewById(R.id.addExerciseFragment_et_exerciseDate);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.addExerciseFragment_bt_addSet):
                mSetListLinearLayout.addView(addSet());
                break;
            case (R.id.addExerciseFragment_bt_deleteSet):
                removeSet();
                break;
            case (R.id.addExerciseFragment_bt_saveExercise):
                onSaveButtonPress();
                break;

        }
    }


    private LinearLayout addSet() {
        mSetNumber++;
        LinearLayout setView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_set, null);
        TextView setNumber = setView.findViewById(R.id.item_addSet_tv_setNumber);
        setNumber.setText(Integer.toString(mSetNumber));
        setView.setTag(mSetNumber);
        return setView;
    }

    private void removeSet() {
        View setToRemove = mSetListLinearLayout.findViewWithTag(mSetNumber);
        if (setToRemove != null) {
            mSetListLinearLayout.removeView(setToRemove);
            mSetNumber--;
        }
    }

    private void onSaveButtonPress() {
        saveExercise();
        finish();
    }

    // TODO[3]: this method probably can be refactored to something cleaner
    private void saveExercise() {
        long newExerciseId; // we need this variable so we can create SingleSets with proper "parent" column value
        List<SingleSet> singleSetList = new ArrayList<SingleSet>();

        String exerciseName = mExerciseNameEditText.getText().toString();

        // TODO[2]: this will be date object in the future?
        long exerciseDate = Long.parseLong(mExerciseDateEditText.getText().toString());

        // TODO[1]: this value should be passed with starting this activity, 2 is a temporary value
        int exerciseOrderInDay = 2;

        Exercise exercise = new Exercise(exerciseName, exerciseOrderInDay, exerciseDate);
        newExerciseId = mGymLogViewModel.insertExercise(exercise);
        // TODO[3]: this can be the place to add some sort of validation that exercise was saved correctly - if not newExerciseId = -1

        // adding SingleSets in this part
        for (int i = 0; i < mSetListLinearLayout.getChildCount(); i++) {
            View setView = mSetListLinearLayout.getChildAt(i);

            TextView setWeightTextView = setView.findViewById(R.id.item_addSet_tv_setWeight);
            float setWeight = Float.valueOf(setWeightTextView.getText().toString());

            TextView setRepsTextView = setView.findViewById(R.id.item_addSet_tv_setReps);
            int setReps = Integer.valueOf(setRepsTextView.getText().toString());

            singleSetList.add(new SingleSet(newExerciseId, setReps, setWeight, false));
        }

        mGymLogViewModel.insertMultipleSingleSets(singleSetList);
    }
}
