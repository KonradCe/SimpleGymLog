package pl.kcworks.simplegymlog.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.SingleSet;
import pl.kcworks.simplegymlog.model.db.DataTypeConverter;
import pl.kcworks.simplegymlog.viewmodel.ExerciseViewModel;

import static java.lang.Thread.sleep;

public class AddExerciseActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO[3]: exercise name should probably be fixed - when exercise name can be edited at any time there can be chaos with sets based % of TM
    //  (TM value is read from SharedPreferences by key of exercise name)

    // TODO[1]: add functionality of selecting sets in order to edit them

    public static final String TAG = "KCtag-" + AddExerciseActivity.class.getSimpleName();
    public static final String UPDATE_EXERCISE_EXTRA = "UPDATE_EXERCISE_EXTRA";
    private static final String PREFS_FILE = "EXERCISE_MAXES";

    private int setNumber = 0;     // variable to keep track and display number of sets added by the user
    private boolean editMode = false; // states if activity was started to edit existing exercise (true) or if to add new (false)
    private boolean tmPercentageMode = false; // states if the weight of the exercise is determined by the maximum weight user can train with - Training Max or TM
    private long exerciseDate;
    private long mLastClickTime = 0; // this variable prevents from double clicking on the save button (this would result in "double saving"

    private ExerciseViewModel exerciseViewModel;
    private OnSetClickListener onSetClickListener;

    // VIEWS
    private EditText exerciseNameEditText;
    private TextView exerciseDateTextView;
    private TextView trainingMaxInfoEditText;
    private Switch weightIsBasedOnPercentageSwitch;
    private Button repsMinusButton;
    private EditText repsEditText;
    private Button repsPlusButton;
    private Button weightMinusButton;
    private EditText weightEditText;
    private Button weightPlusButton;
    private Button addSetButton;
    private Button removeSetButton;
    private Button saveExerciseButton;
    private LinearLayout setListLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        setUpViews();
        setUpViewModel();
        getDataFromIntent(getIntent());

    }

    private void setUpViewModel() {
        exerciseViewModel = ViewModelProviders.of(this).get(ExerciseViewModel.class);

        exerciseViewModel.getExerciseWithSetsMutableLiveData().observe(this, new Observer<ExerciseWithSets>() {
            @Override
            public void onChanged(ExerciseWithSets exerciseWithSets) {
                updateViews(exerciseWithSets);
            }
        });

        exerciseViewModel.getSingleSetToAddMutableLiveData().observe(this, new Observer<SingleSet>() {
            @Override
            public void onChanged(SingleSet singleSet) {
                Log.i(TAG, singleSet.toString());
                updateSetToAddRelatedViews(singleSet);
            }
        });
    }

    private void getDataFromIntent(Intent intent) {
        // check if the activity was launched to edit exercise - and if so to act on it
        ExerciseWithSets exerciseWithSets;
        if (getIntent().hasExtra(UPDATE_EXERCISE_EXTRA)) {
            editMode = true;
            saveExerciseButton.setText("Update exercise");

            String json = intent.getStringExtra(UPDATE_EXERCISE_EXTRA);
            exerciseWithSets = DataTypeConverter.stringToExerciseWithSets(json);
        }

        else {
            // TODO[1] value of exerciseOrderInDay should be calculated accordingly
            exerciseDate = intent.getLongExtra(WorkoutActivity.DATE_OF_EXERCISE_TAG, 19901029);
            Exercise exercise = new Exercise("", 5, exerciseDate);
            List<SingleSet> singleSetList = new ArrayList<>();
            exerciseWithSets = new ExerciseWithSets(exercise, singleSetList);
        }
        exerciseViewModel.setInitialValue(exerciseWithSets);
    }

    private void setUpViews() {
        // this line prevents keyboard from opening up on activity start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        onSetClickListener = new OnSetClickListener();

        exerciseNameEditText = findViewById(R.id.addExerciseActivity_et_exerciseName);
        exerciseNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                exerciseViewModel.setName(editable.toString());

            }
        });
        exerciseDateTextView = findViewById(R.id.addExerciseActivity_tv_exerciseDate);

        trainingMaxInfoEditText = findViewById(R.id.addExerciseActivity_tv_trainingMaxInfo);
        weightIsBasedOnPercentageSwitch = findViewById(R.id.addExerciseActivity_sw_isBasedOnPercentage);
        weightIsBasedOnPercentageSwitch.setChecked(tmPercentageMode);
        weightIsBasedOnPercentageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean setIsBasedOnTm) {
                switchTypeOfSetToAdd(setIsBasedOnTm);
            }
        });

        // reps
        repsMinusButton = findViewById(R.id.addExerciseActivity_bt_repsMinus);
        repsMinusButton.setOnClickListener(this);
        repsEditText = findViewById(R.id.addExerciseActivity_et_reps);
        repsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                int reps = 0;
                try {
                    reps = Integer.parseInt(editable.toString());
                } catch (NumberFormatException ignored) {
                }
                exerciseViewModel.serToAddSetReps(reps);
            }
        });
        repsPlusButton = findViewById(R.id.addExerciseActivity_bt_repsPlus);
        repsPlusButton.setOnClickListener(this);

        // weight
        weightMinusButton = findViewById(R.id.addExerciseActivity_bt_weightMinus);
        weightMinusButton.setOnClickListener(this);
        weightEditText = findViewById(R.id.addExerciseActivity_et_weight);
        weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                float weight = 0;
                try {
                    weight = Float.parseFloat(editable.toString());
                } catch (NumberFormatException ignore) {}

                exerciseViewModel.setToAddSetWeight(weight);
            }
        });
        weightPlusButton = findViewById(R.id.addExerciseActivity_bt_weightPlus);
        weightPlusButton.setOnClickListener(this);

        addSetButton = findViewById(R.id.addExerciseActivity_bt_addSet);
        addSetButton.setOnClickListener(this);

        removeSetButton = findViewById(R.id.addExerciseActivity_bt_deleteSet);
        removeSetButton.setOnClickListener(this);

        saveExerciseButton = findViewById(R.id.addExerciseActivity_bt_saveExercise);
        saveExerciseButton.setOnClickListener(this);

        setListLinearLayout = findViewById(R.id.addExerciseActivity_ll_listOfSets);
    }

    private void updateViews(ExerciseWithSets exerciseWithSets) {
        Exercise exercise = exerciseWithSets.getExercise();
        List<SingleSet> singleSetList = exerciseWithSets.getExerciseSetList();

        exerciseDateTextView.setText(DateConverterHelper.fromLongToString(exercise.getExerciseDate()));
        exerciseNameEditText.setText(exercise.getExerciseName());


        setListLinearLayout.removeAllViews();
        for (int i = 0; i < singleSetList.size(); i++) {
            SingleSet singleSet = singleSetList.get(i);
            displaySet(singleSet, (i + 1));
        }

    }

    private void updateSetToAddRelatedViews(SingleSet singleSet) {
        String repsText = Integer.toString(singleSet.getReps());
        String weightText = Double.toString(singleSet.getWeight());

        repsEditText.setText(repsText);
        weightEditText.setText(weightText);

        if(!singleSet.isBasedOnTm()) {
            weightEditText.setHint("weight");
            trainingMaxInfoEditText.setText("standard set with fixed weight");
        }
        else {
            weightEditText.setHint("% of TM");
            trainingMaxInfoEditText.setText("weight based on % of Training Max \nCurrent Training Max for " + exerciseNameEditText.getText().toString() + " is set at: " + singleSet.getTrainingMax());
        }
    }

    // TODO[3]: this method shouldn't do both of those things, refactor - leave populating views with set info to populateViewWithSetsInfoInEditMode method? (DUH)
    // this method adds new set or populates view with existing sets
    private void displaySet(SingleSet singleSet, int setNumber) {
        LinearLayout setView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_set, null);
        TextView setNumberTextView = setView.findViewById(R.id.item_addSet_tv_setNumber);
        setNumberTextView.setText(Integer.toString(setNumber));
        if (singleSet.getReps() != 0 && singleSet.getWeight() != 0) {
            TextView setReps = setView.findViewById(R.id.item_addSet_tv_setReps);
            setReps.setText(Integer.toString(singleSet.getReps()));
            TextView setWeight = setView.findViewById(R.id.item_addSet_tv_setWeight);
            setWeight.setText(Double.toString(singleSet.getWeight()));

            if (singleSet.isBasedOnTm()) {
                TextView percentageOfMaxTextView = setView.findViewById(R.id.item_addSet_tv_percentageOfMax);
                percentageOfMaxTextView.setVisibility(View.VISIBLE);
                String percentageOfMaxInfo = singleSet.getPercentageOfTm() + "% of " + singleSet.getTrainingMax();
                percentageOfMaxTextView.setText(percentageOfMaxInfo);
            }
            setView.setTag(this.setNumber);
            setView.setOnClickListener(onSetClickListener);
            setListLinearLayout.addView(setView);
        }

        else {
            Toast.makeText(this, "set can not have 0 reps or 0 weight", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeLastSet() {
        exerciseViewModel.removeLastSet();
    }

    private void onSaveButtonPress() {
        exerciseViewModel.saveToDb(editMode);
        finish();
    }

    // 4 methods changing interface accordingly to what buttons were pressed and if the weight will be added based on TM (tmPercentageMode)
    private void repsMinus() {
        exerciseViewModel.setToAddModifyRepsBy(-1);
    }

    private void repsPlus() {
        exerciseViewModel.setToAddModifyRepsBy(1);
    }

    private void weightMinus() {
        exerciseViewModel.setToAddModifyWeightBy(-5);
    }

    private void weightPlus() {
        exerciseViewModel.setToAddModifyWeightBy(5);
    }

    // this is prompted by switching set type switch - it changes slightly UI accordingly to tmPercentageMode
    private void switchTypeOfSetToAdd(boolean setIsBasedOnTm) {

        if (setIsBasedOnTm) {
            SharedPreferences preferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
            String exerciseName = exerciseNameEditText.getText().toString();
            double trainingMax = Double.longBitsToDouble(preferences.getLong(exerciseName, 0));
            if (trainingMax == 0) {
                showTrainingMaxForExerciseDialog(exerciseName);
            }
            else {
                exerciseViewModel.setToAddSetTmMax(trainingMax);
            }
        }
        else {
            exerciseViewModel.setToAddSetTmMax(0);
        }
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
                double trainingMax = Double.valueOf(trainingMaxEditText.getText().toString());
                editor.putLong(exerciseName, Double.doubleToLongBits(trainingMax));
                editor.apply();
                exerciseViewModel.setToAddSetTmMax(trainingMax);
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                weightIsBasedOnPercentageSwitch.setChecked(false);
                exerciseViewModel.setToAddSetTmMax(0);
            }
        });
        builder.setView(dialogView)
                .setCancelable(false)
                .show();
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
                exerciseViewModel.addSet(editMode);
                break;

            case (R.id.addExerciseActivity_bt_deleteSet):
                removeLastSet();
                break;
            case (R.id.addExerciseActivity_bt_saveExercise):
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                onSaveButtonPress();
                break;
        }
    }

    private class OnSetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextView percentageTextView = view.findViewById(R.id.item_addSet_tv_percentageOfMax);
            TextView setWeigthTextView = view.findViewById(R.id.item_addSet_tv_setWeight);
            TextView setRepsTextView = view.findViewById(R.id.item_addSet_tv_setReps);

            if (!percentageTextView.getText().toString().matches("")) {
                weightIsBasedOnPercentageSwitch.setChecked(true);
                weightEditText.setText(percentageTextView.getText().toString().split("%")[0]);
            }
            else {
                weightIsBasedOnPercentageSwitch.setChecked(false);
                weightEditText.setText(setWeigthTextView.getText());
            }
            repsEditText.setText(setRepsTextView.getText());

        }
    }

}


//TODO[2]: should override onBackPressed to prompt user if he wants to save or discard current exercise