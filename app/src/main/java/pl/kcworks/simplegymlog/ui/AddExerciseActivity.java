package pl.kcworks.simplegymlog.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.SingleSet;
import pl.kcworks.simplegymlog.viewmodel.SingleExerciseViewModel;

public class AddExerciseActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO[3]: move all db read and write work from this activity to WorkoutActivity. It'd be better to only read and give back serializable objects for it to read/write.

    public static final String TAG = "KCtag-" + AddExerciseActivity.class.getSimpleName();
    public static final String UPDATE_EXERCISE_ID_EXTRA = "UPDATE_EXERCISE_ID_EXTRA";

    private int mSetNumber = 0;     // variable to keep track and display number of sets added by the user
    private boolean mEditMode = false; // states if activity was started to edit existing exercise (true) or if to add new (false)
    private long mExerciseId; // we need exercise id so we can pass it while adding SingleSets to db (so they can have proper "parent" column value)

    private GymLogViewModel mGymLogViewModel;
    private Exercise mCurrentExercise;
    private List<SingleSet> mCurrentSingleSetList;

    // VIEWS
    private Button mRepsMinusButton;
    private EditText mRepsEditText;
    private Button mRepsPlusButton;
    private Button mWeightMinusButton;
    private EditText mWeightEditText;
    private Button mWeightPlusButton;
    private Button mAddSetButton;
    private Button mRemoveSetButton;
    private Button mSaveExerciseButton;
    private LinearLayout mSetListLinearLayout;
    private EditText mExerciseNameEditText;
    private EditText mExerciseDateEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        setUpViews();

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);

        // check if the activity was launched to edit exercise - and if so to act on it
        if (getIntent().hasExtra(UPDATE_EXERCISE_ID_EXTRA)) {
            mEditMode = true;
            int editExerciseId = getIntent().getIntExtra(UPDATE_EXERCISE_ID_EXTRA, -1);

            SingleExerciseViewModel.Factory factory = new SingleExerciseViewModel.Factory(getApplication(), editExerciseId);
            SingleExerciseViewModel singleExerciseViewModel = ViewModelProviders.of(this, factory).get(SingleExerciseViewModel.class);

            subscribeToModel(singleExerciseViewModel);
        }
    }

    private void setUpViews() {
        mExerciseNameEditText = findViewById(R.id.addExerciseActivity_et_exerciseName);
        mExerciseDateEditText = findViewById(R.id.addExerciseActivity_et_exerciseDate);

        // reps
        mRepsMinusButton = findViewById(R.id.addExerciseActivity_bt_repsMinus);
        mRepsMinusButton.setOnClickListener(this);
        mRepsEditText = findViewById(R.id.addExerciseActivity_et_reps);
        mRepsPlusButton = findViewById(R.id.addExerciseActivity_bt_repsPlus);
        mRepsPlusButton.setOnClickListener(this);

        // weight
        mWeightMinusButton = findViewById(R.id.addExerciseActivity_bt_weightMinus);
        mWeightMinusButton.setOnClickListener(this);
        mWeightEditText = findViewById(R.id.addExerciseActivity_et_weight);
        mWeightPlusButton= findViewById(R.id.addExerciseActivity_bt_weightPlus);
        mWeightPlusButton.setOnClickListener(this);

        mAddSetButton = findViewById(R.id.addExerciseActivity_bt_addSet);
        mAddSetButton.setOnClickListener(this);

        mRemoveSetButton = findViewById(R.id.addExerciseActivity_bt_deleteSet);
        mRemoveSetButton.setOnClickListener(this);

        mSaveExerciseButton = findViewById(R.id.addExerciseActivity_bt_saveExercise);
        mSaveExerciseButton.setOnClickListener(this);

        mSetListLinearLayout = findViewById(R.id.addExerciseActivity_ll_listOfSets);

    }

    private void subscribeToModel(SingleExerciseViewModel singleExerciseViewModel) {
        singleExerciseViewModel.getExerciseWithSets().observe(this, new Observer<ExerciseWithSets>() {
            @Override
            public void onChanged(@Nullable ExerciseWithSets exerciseWithSets) {
                mCurrentExercise = exerciseWithSets.getExercise();
                mCurrentSingleSetList = exerciseWithSets.getExerciseSetList();

                mExerciseId = mCurrentExercise.getExerciseId();

                populateViewWithExerciseInfo();
                populateViewWithSetsInfo();
            }
        });
    }

    private void populateViewWithExerciseInfo() {
        mSaveExerciseButton.setText("Update exercise");

        mExerciseNameEditText.setText(mCurrentExercise.getExerciseName());
        mExerciseDateEditText.setText(Long.toString(mCurrentExercise.getExerciseDate()));
    }

    private void populateViewWithSetsInfo() {
        for (SingleSet singleSet : mCurrentSingleSetList) {
            addSet(singleSet.getReps(), singleSet.getWeight());
            Log.i(TAG, "populateViewWithSetsInfo() for set: " + singleSet.toString());
        }
    }

    // TODO[3]: this method shouldn't do both of those things, refactor - leave populating views with set info to populateViewWithSetsInfo method? (DUH)
    // this method adds new set or populates view with existing sets
    private void addSet(Integer reps, Float weight) {
        Log.i(TAG, "addSet() was called with arguments: " + reps + " x " + weight);
        mSetNumber++;
        LinearLayout setView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_set, null);
        TextView setNumber = setView.findViewById(R.id.item_addSet_tv_setNumber);
        setNumber.setText(Integer.toString(mSetNumber));
        if (reps != null && weight != null) {
            EditText setReps = setView.findViewById(R.id.item_addSet_tv_setReps);
            setReps.setText(reps.toString());
            EditText setWeight = setView.findViewById(R.id.item_addSet_tv_setWeight);
            setWeight.setText(weight.toString());
        }
        setView.setTag(mSetNumber);
        mSetListLinearLayout.addView(setView);
    }

    private void removeSet() {
        View setToRemove = mSetListLinearLayout.findViewWithTag(mSetNumber);
        if (setToRemove != null) {
            mSetListLinearLayout.removeView(setToRemove);
            mSetNumber--;
        }
    }

    private void onSaveButtonPress() {
        if (!mEditMode) {
            saveExerciseWithSets();
        }
        else {
            updateExercise();
            updateSets();
        }
        finish();
    }

    // TODO[3]: this method probably can be refactored to something cleaner, divided into more methods
    private void saveExerciseWithSets() {
        String exerciseName = mExerciseNameEditText.getText().toString();

        // TODO[2]: this will be date object in the future?
        long exerciseDate = Long.parseLong(mExerciseDateEditText.getText().toString());

        // TODO[2]: this value should be passed with starting this activity, 2 is a temporary value
        //  do we need this field anyway? - not as long as we won't implement changing exercises order
        int exerciseOrderInDay = 2;

        Exercise exercise = new Exercise(exerciseName, exerciseOrderInDay, exerciseDate);
        mExerciseId = mGymLogViewModel.insertExercise(exercise);
        // TODO[3]: this can be the place to add some sort of validation that exercise was saved correctly - if not newExerciseId = -1

        // creating and adding SingleSetList to db
        mGymLogViewModel.insertMultipleSingleSets(createSingleSetListFromViews(mExerciseId));
    }

    //TODO[1]: do not try to make SingleSet objects from empty views
    private List<SingleSet> createSingleSetListFromViews(long newExerciseId) {
        List<SingleSet> singleSetList = new ArrayList<>();
        // adding SingleSets in this part
        for (int i = 0; i < mSetListLinearLayout.getChildCount(); i++) {
            View setView = mSetListLinearLayout.getChildAt(i);

            TextView setWeightTextView = setView.findViewById(R.id.item_addSet_tv_setWeight);
            float setWeight = Float.valueOf(setWeightTextView.getText().toString());

            TextView setRepsTextView = setView.findViewById(R.id.item_addSet_tv_setReps);
            int setReps = Integer.valueOf(setRepsTextView.getText().toString());

            singleSetList.add(new SingleSet(newExerciseId, setReps, setWeight, false));
        }
        return singleSetList;
    }

    private void updateExercise() {
        String newExerciseName = mExerciseNameEditText.getText().toString();
        if (!mCurrentExercise.getExerciseName().equals(newExerciseName)) {
            mCurrentExercise.setExerciseName(newExerciseName);
        }
        mGymLogViewModel.updateExercise(mCurrentExercise);
    }

    private void updateSets() {
        List<SingleSet> newSingleSetList = createSingleSetListFromViews(mExerciseId);

        if (mCurrentSingleSetList.size() < newSingleSetList.size()) {
            // some sets were added - we need to update existing and add the rest

            // TODO[2]: this can be extracted as separate method? it copied and pasted in all 3 cases of updateSets()
            // updating existing
            for (int i = 0; i < mCurrentSingleSetList.size(); i++) {
                if (mCurrentSingleSetList.get(i).needsUpdate(newSingleSetList.get(i))) {
                    SingleSet singleSetToUpdate = mCurrentSingleSetList.get(i);
                    SingleSet singleSetWithUpdatedValues = newSingleSetList.get(i);

                    singleSetToUpdate.setReps(singleSetWithUpdatedValues.getReps());
                    singleSetToUpdate.setWeight(singleSetWithUpdatedValues.getWeight());

                    mGymLogViewModel.updateSingleSet(singleSetToUpdate);
                }
            }

            // adding the rest
            List<SingleSet> subList = newSingleSetList.subList(mCurrentSingleSetList.size(), newSingleSetList.size());
            mGymLogViewModel.insertMultipleSingleSets(subList);
        }

        else if (mCurrentSingleSetList.size() > newSingleSetList.size()) {
            // some sets were deleted - we need to update existing and delete the rest from db
            // updating existing
            for (int i = 0; i < newSingleSetList.size(); i++) {
                if (mCurrentSingleSetList.get(i).needsUpdate(newSingleSetList.get(i))) {
                    SingleSet singleSetToUpdate = mCurrentSingleSetList.get(i);
                    SingleSet singleSetWithUpdatedValues = newSingleSetList.get(i);

                    singleSetToUpdate.setReps(singleSetWithUpdatedValues.getReps());
                    singleSetToUpdate.setWeight(singleSetWithUpdatedValues.getWeight());

                    mGymLogViewModel.updateSingleSet(singleSetToUpdate);
                }
            }

            // deleting the rest
            List<SingleSet> subList = mCurrentSingleSetList.subList(newSingleSetList.size(), mCurrentSingleSetList.size());
            mGymLogViewModel.deleteMultipleSingleSets(subList);
        }

        else {
            // number of sets did not change - we only need to update
            for (int i = 0; i < mCurrentSingleSetList.size(); i++) {
                if (mCurrentSingleSetList.get(i).needsUpdate(newSingleSetList.get(i))) {
                    SingleSet singleSetToUpdate = mCurrentSingleSetList.get(i);
                    SingleSet singleSetWithUpdatedValues = newSingleSetList.get(i);

                    singleSetToUpdate.setReps(singleSetWithUpdatedValues.getReps());
                    singleSetToUpdate.setWeight(singleSetWithUpdatedValues.getWeight());

                    mGymLogViewModel.updateSingleSet(singleSetToUpdate);
                }
            }
        }
    }

    private void repsMinus() {
        if(!mRepsEditText.getText().toString().isEmpty()) {
            int reps = Integer.valueOf(mRepsEditText.getText().toString());
            if (reps > 0) {
                reps--;
                mRepsEditText.setText(Integer.toString(reps));
            }
        }
        else {
            mRepsEditText.setText("0");
        }
    }

    private void repsPlus() {
        if(!mRepsEditText.getText().toString().isEmpty()) {
            int reps = Integer.valueOf(mRepsEditText.getText().toString());
            reps++;
            mRepsEditText.setText(Integer.toString(reps));
            }
        else {
            mRepsEditText.setText("1");
        }
    }

    private void weightMinus() {
        if(!mWeightEditText.getText().toString().isEmpty()) {
            float weight = Float.valueOf(mWeightEditText.getText().toString());
            if (weight > 0) {
                // TODO[2]: in the future value of the weight increment will be loaded from preferences (depends of units user chose: 5 for lbs and 2.5 for kg)
                weight -= 2.5;
                mWeightEditText.setText(Float.toString(weight));
            }
        }
        else {
            mWeightEditText.setText("0");
        }
    }

    private void weightPlus() {
        if (!mWeightEditText.getText().toString().isEmpty()) {
            float weight = Float.valueOf(mWeightEditText.getText().toString());
            // TODO[2]: in the future value of the weight increment will be loaded from preferences (depends of units user chose: 5 for lbs and 2.5 for kg)
            weight += 2.5;
            mWeightEditText.setText(Float.toString(weight));
        } else {
            mWeightEditText.setText("2.5");
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.addExerciseActivity_bt_repsMinus):
                repsMinus();
                break;
            case (R.id.addExerciseActivity_bt_repsPlus):
                repsPlus();
                break;
            case (R.id.addExerciseActivity_bt_weightMinus):
                weightMinus();
                break;
            case (R.id.addExerciseActivity_bt_weightPlus):
                weightPlus();
                break;
            case (R.id.addExerciseActivity_bt_addSet):
                try {
                    int reps = Integer.valueOf(mRepsEditText.getText().toString());
                    float weight = Float.valueOf(mWeightEditText.getText().toString());
                    addSet(reps, weight);
                } catch (NumberFormatException e) {
                    addSet(null, null);
                }
                break;

            case (R.id.addExerciseActivity_bt_deleteSet):
                removeSet();
                break;
            case (R.id.addExerciseActivity_bt_saveExercise):
                onSaveButtonPress();
                break;
        }
    }

}


//TODO[2]: should override onBackPressed to prompt user if he wants to save or discard current exercise