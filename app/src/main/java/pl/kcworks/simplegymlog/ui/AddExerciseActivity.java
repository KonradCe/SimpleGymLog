package pl.kcworks.simplegymlog.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.SingleSet;
import pl.kcworks.simplegymlog.viewmodel.SingleExerciseViewModel;

public class AddExerciseActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO[3]: consider moving all db read and write work from this activity to WorkoutActivity. Would it be better to do all read and write (to/from db) work there?
    // It would have to work by giving back serialized objects of exercises from here to WorkoutActivity/

    // TODO[2]: exercise name should probably be fixed - when exercise name can be edited at any time there can be chaos with sets based % of TM
    //  (TM value is read from SharedPreferences by key of exercise name)

    public static final String TAG = "KCtag-" + AddExerciseActivity.class.getSimpleName();
    public static final String UPDATE_EXERCISE_ID_EXTRA = "UPDATE_EXERCISE_ID_EXTRA";
    private static final String PREFS_FILE = "EXERCISE_MAXES";

    private int mSetNumber = 0;     // variable to keep track and display number of sets added by the user
    private boolean mEditMode = false; // states if activity was started to edit existing exercise (true) or if to add new (false)
    private boolean mTmPercentageMode = false; // states if the weight of the exercise is determined by the maximum weight user can train with - Training Max or TM
    private long mExerciseId; // we need exercise id so we can pass it while adding SingleSets to db (so they can have proper "parent" column value)
    private long mExerciseDate;
    private float mCurrentTrainingMax;

    private GymLogViewModel mGymLogViewModel;
    private Exercise mCurrentExercise; // this variable is used when the activity is launched in EditMode
    private List<SingleSet> mCurrentSingleSetList;
    private Calendar mCalendar;

    // VIEWS
    private EditText mExerciseNameEditText;
    private TextView mExerciseDateTextView;
    private TextView mTrainingMaxInfoEditText;
    private Switch mWeightIsBasedOnPercentageSwitch;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        getDataFromIntent(getIntent());
        setUpViews();

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);

    }

    private void getDataFromIntent(Intent intent) {
        // check if the activity was launched to edit exercise - and if so to act on it
        if (getIntent().hasExtra(UPDATE_EXERCISE_ID_EXTRA)) {
            mEditMode = true;
            int editExerciseId = intent.getIntExtra(UPDATE_EXERCISE_ID_EXTRA, -1);

            SingleExerciseViewModel.Factory factory = new SingleExerciseViewModel.Factory(getApplication(), editExerciseId);
            SingleExerciseViewModel singleExerciseViewModel = ViewModelProviders.of(this, factory).get(SingleExerciseViewModel.class);

            subscribeToModel(singleExerciseViewModel);
        }

        if (!mEditMode) {
            mExerciseDate = intent.getLongExtra(WorkoutActivity.DATE_OF_EXERCISE_TAG, 19901029);
        }
    }

    private void setUpViews() {
        mExerciseNameEditText = findViewById(R.id.addExerciseActivity_et_exerciseName);
        mExerciseDateTextView = findViewById(R.id.addExerciseActivity_tv_exerciseDate);
        // if exercise is editmode mExerciseDate wasn't initialized yet; in EditMode this is set later in populateViewWithExerciseInfoInEditMode method, called by subscribeToModel method
        // TODO[3]: This solution is stupid and should be refactored
        if (!mEditMode) {
            mExerciseDateTextView.setText(DateConverterHelper.fromLongToString(mExerciseDate));
        }
        mTrainingMaxInfoEditText = findViewById(R.id.addExerciseActivity_tv_trainingMaxInfo);
        mWeightIsBasedOnPercentageSwitch = findViewById(R.id.addExerciseActivity_sw_isBasedOnPercentage);
        mWeightIsBasedOnPercentageSwitch.setChecked(mTmPercentageMode);
        mWeightIsBasedOnPercentageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isPercentage) {
                switchTypeOfSetsToPercentage(isPercentage);
            }
        });

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

    // this method is called only if the activity was started up in edit mode (intent has id of exercise that is being updated)
    // it fetches data from db for current exercise and calls appropriate methods to populate UI with that info
    private void subscribeToModel(SingleExerciseViewModel singleExerciseViewModel) {
        singleExerciseViewModel.getExerciseWithSets().observe(this, new Observer<ExerciseWithSets>() {
            @Override
            public void onChanged(@Nullable ExerciseWithSets exerciseWithSets) {
                mCurrentExercise = exerciseWithSets.getExercise();
                mExerciseDate = mCurrentExercise.getExerciseDate();

                mCurrentSingleSetList = exerciseWithSets.getExerciseSetList();

                mExerciseId = mCurrentExercise.getExerciseId();

                populateViewWithExerciseInfoInEditMode();
                populateViewWithSetsInfo();
            }
        });
    }

    // this method is called when activity is started in edit mode in onCreate
    // it updates views with exercise info
    private void populateViewWithExerciseInfoInEditMode() {
        mSaveExerciseButton.setText("Update exercise");

        mExerciseNameEditText.setText(mCurrentExercise.getExerciseName());
        mExerciseDateTextView.setText(DateConverterHelper.fromLongToString(mExerciseDate));
    }



    // this method is called when activity is started in edit mode
    // it updates views with info for every set
    private void populateViewWithSetsInfo() {
        for (SingleSet singleSet : mCurrentSingleSetList) {
            addSet(singleSet.getReps(), singleSet.getWeight(), singleSet.getMaxWeightPercentageInfo());
            Log.i(TAG, "populateViewWithSetsInfo() for set: " + singleSet.toString());
        }
    }

    // TODO[3]: this method shouldn't do both of those things, refactor - leave populating views with set info to populateViewWithSetsInfo method? (DUH)
    // this method adds new set or populates view with existing sets
    private void addSet(Integer reps, Float weight, String percentageOfMaxInfo) {
        Log.i(TAG, "addSet() was called with arguments: " + reps + " x " + weight);
        mSetNumber++;
        LinearLayout setView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_set, null);
        TextView setNumber = setView.findViewById(R.id.item_addSet_tv_setNumber);
        setNumber.setText(Integer.toString(mSetNumber));
        if (reps != null && weight != null) {
            TextView setReps = setView.findViewById(R.id.item_addSet_tv_setReps);
            setReps.setText(reps.toString());
            TextView setWeight = setView.findViewById(R.id.item_addSet_tv_setWeight);
            setWeight.setText(weight.toString());
            if (percentageOfMaxInfo != null) {
                TextView percentageOfMaxTextView = setView.findViewById(R.id.item_addSet_tv_percentageOfMax);
                percentageOfMaxTextView.setVisibility(View.VISIBLE);
                percentageOfMaxTextView.setText(percentageOfMaxInfo);
            }
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
        //TODO[2]: there should be a check if exercise name is not empty
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

        // TODO[2]: this value should be passed with starting this activity, 2 is a temporary value
        //  do we need this field anyway? - not as long as we won't implement changing exercises order
        int exerciseOrderInDay = 2;

        Exercise exercise = new Exercise(exerciseName, exerciseOrderInDay, mExerciseDate);
        mExerciseId = mGymLogViewModel.insertExercise(exercise);
        // TODO[3]: this can be the place to add some sort of validation that exercise was saved correctly - if not newExerciseId = -1

        // creating and adding SingleSetList to db
        mGymLogViewModel.insertMultipleSingleSets(createSingleSetListFromViews(mExerciseId));
    }

    private List<SingleSet> createSingleSetListFromViews(long newExerciseId) {
        List<SingleSet> singleSetList = new ArrayList<>();
        // adding SingleSets in this part
        for (int i = 0; i < mSetListLinearLayout.getChildCount(); i++) {
            View setView = mSetListLinearLayout.getChildAt(i);

            TextView setWeightTextView = setView.findViewById(R.id.item_addSet_tv_setWeight);
            float setWeight = Float.valueOf(setWeightTextView.getText().toString());

            TextView setRepsTextView = setView.findViewById(R.id.item_addSet_tv_setReps);
            int setReps = Integer.valueOf(setRepsTextView.getText().toString());

            SingleSet singleSet = new SingleSet(newExerciseId, setReps, null, setWeight, false);

            if (mTmPercentageMode) {
                TextView percentageOfMaxTextView = setView.findViewById(R.id.item_addSet_tv_percentageOfMax);
                String percentageAndMaxInfo = percentageOfMaxTextView.getText().toString();
                singleSet.setMaxWeightPercentageInfo(percentageAndMaxInfo);
            }

            singleSetList.add(singleSet);
        }
        return singleSetList;
    }

    // called in onSaveButtonPress in edit mode only
    //  it updates exercise entity - so basically only name. It's not very useful right now but might be needed in the future
    private void updateExercise() {
        String newExerciseName = mExerciseNameEditText.getText().toString();
        if (!mCurrentExercise.getExerciseName().equals(newExerciseName)) {
            mCurrentExercise.setExerciseName(newExerciseName);
        }
        mGymLogViewModel.updateExercise(mCurrentExercise);
    }

    // called in onSaveButtonPress in edit mode only
    // it compares sets (existing views of sets) with the sets saved in db (for the current exercise) and updates / saves new / deletes sets accordingly
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

    // 4 methods changing interface accordingly to what buttons were pressed and if the weight will be added based on TM (mTmPercentageMode)
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
        if(!mWeightEditText.getText().toString().trim().isEmpty()) {
            float weight = Float.valueOf(mWeightEditText.getText().toString());
            if (weight > 0) {
                // TODO[2]: in the future value of the weight increment will be loaded from preferences (depends of units user chose: 5 for lbs and 2.5 for kg)
                if (mTmPercentageMode) {
                    weight -= 5;
                }
                else {
                    weight -= 2.5;
                }
                mWeightEditText.setText(Float.toString(weight));
            }
        }
        else {
            if (mTmPercentageMode) {
                mWeightEditText.setText("50");
            }
            mWeightEditText.setText("0");
        }
    }

    private void weightPlus() {
        if (!mWeightEditText.getText().toString().isEmpty()) {
            float weight = Float.valueOf(mWeightEditText.getText().toString());
            // TODO[2]: in the future value of the weight increment will be loaded from preferences (depends of units user chose: 5 for lbs and 2.5 for kg)
            if (mTmPercentageMode) {
                weight += 5;
            } else {
                weight += 2.5;
            }
            mWeightEditText.setText(Float.toString(weight));
        } else {
            if (mTmPercentageMode) {
                mWeightEditText.setText("55");
            } else {
                mWeightEditText.setText("2.5");
            }
        }
    }

    // this is prompted by switching set type switch - it changes slightly UI accordingly to mTmPercentageMode
    private void switchTypeOfSetsToPercentage(boolean isPercentage) {
        Log.i(TAG, "current mTmPercentageMode = " + mTmPercentageMode + " switching to: " + isPercentage);

        if (mTmPercentageMode == isPercentage) {
            return;
        }
        mTmPercentageMode = isPercentage;

        if (isPercentage) {
            mWeightEditText.setHint("% of TM");
            mWeightEditText.setText("");
            if(mWeightEditText.getText().toString().trim().length() != 0) {
                mWeightEditText.setText("70%");
            }
            SharedPreferences preferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
            String exerciseName = mExerciseNameEditText.getText().toString();
            mCurrentTrainingMax = preferences.getFloat(exerciseName, 0);
            if (mCurrentTrainingMax == 0) {
                showTrainingMaxForExerciseDialog(exerciseName);
            }
            else {
                displaySetTypeInfo();
            }
        }
        else {
            mWeightEditText.setHint("weight");
            mWeightEditText.setText("");
        }
        displaySetTypeInfo();
    }

    // if set type is switched to percentage mode and id doesn't have saved TM in SharedPreferences this method is called
    // it pops up a dialog for the user to enter TM and then saves it in SharedPreferences
    private void showTrainingMaxForExerciseDialog(final String exerciseName) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.alert_training_max_dialog, null);
        TextView dialogMessageTextView = dialogView.findViewById(R.id.trainingMaxDialog_tv_message);
        final EditText trainingMaxEditText = dialogView.findViewById(R.id.trainingMaxDialog_et_trainingMax);
        dialogMessageTextView.setText("Enter your Training Max for " + exerciseName + ": ");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE, MODE_PRIVATE).edit();
                mCurrentTrainingMax = Float.valueOf(trainingMaxEditText.getText().toString());
                editor.putFloat(exerciseName, mCurrentTrainingMax);
                editor.apply();
                displaySetTypeInfo();
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mWeightIsBasedOnPercentageSwitch.setChecked(false);
            }
        });
        builder.setView(dialogView)
                .setCancelable(false)
                .show();
    }

    // tris method changes text next to the set type switch
    private void displaySetTypeInfo() {
        if (mTmPercentageMode) {
            mTrainingMaxInfoEditText.setText("weight based on % of Training Max \nCurrent Training Max for " + mExerciseNameEditText.getText().toString() + " is set at: " + mCurrentTrainingMax);
        }
        else {
            mTrainingMaxInfoEditText.setText("standard set with fixed weight");
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
                    float weightOrPercentage = Float.valueOf(mWeightEditText.getText().toString());
                    if (mTmPercentageMode) {
                        float weight = weightOrPercentage * mCurrentTrainingMax / 100;
                        addSet(reps, weight, weightOrPercentage + "% of " + mCurrentTrainingMax);
                    }
                    else {
                        addSet(reps, weightOrPercentage, null);
                    }
                } catch (NumberFormatException e) {
                   Toast.makeText(this, "incorrect set numbers", Toast.LENGTH_SHORT).show();
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