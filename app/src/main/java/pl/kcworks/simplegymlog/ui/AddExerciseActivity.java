package pl.kcworks.simplegymlog.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.DayOfRoutine;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.SingleSet;
import pl.kcworks.simplegymlog.model.db.DataTypeConverter;
import pl.kcworks.simplegymlog.viewmodel.AddExerciseViewModel;


public class AddExerciseActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO[3]: exercise name should probably be fixed - when exercise name can be edited at any time there can be chaos with sets based % of TM
    //  (TM value is read from SharedPreferences by key of exercise name)

    // TODO[1]: add functionality of selecting sets in order to edit them

    public static final String TAG = "KCtag-" + AddExerciseActivity.class.getSimpleName();
    public static final String UPDATE_EXERCISE_EXTRA = "UPDATE_EXERCISE_EXTRA";
    public static final String ROUTINE_ADD_EXERCISE = "ROUTINE_ADD_EXERCISE";
    public static final String ROUTINE_EDIT_EXERCISE = "ROUTINE_EDIT_EXERCISE";
    public static final String ROUTINE_EDIT_EXERCISE_ORDER = "ROUTINE_EDIT_EXERCISE_ORDER";
    public static final String ROUTINE_RESULT = "ROUTINE_RESULT";

    // TODO[3]: rounding factor will be loaded from preferences, adequate to the units (2.5 for kgs and 5 for lbs)
    public static double roundingFactor;
    private double weightIncrement;
    private int percentageIncrement;

    private static final String PREFS_FILE = "EXERCISE_MAXES";

    private long lastClickTime = 0; // this variable prevents from double clicking on the save button (this would result in "double saving"
    private ActivityMode activityMode;

    private AddExerciseViewModel addExerciseViewModel;
    private ExerciseWithSets currentExerciseWithSets;
    private SingleSet singleSetToAdd;
    private List<String> exerciseNamesList;

    private OnSetClickListener onSetClickListener;

    // VIEWS
    private TextView exerciseNameTextView;
    private TextView exerciseDateTextView;
    private TextView percentageInfoTextView;
    private Switch weightIsBasedOnPercentageSwitch;
    private TextView repsTextView;
    private TextView weightTextView;
    private Button saveExerciseButton;
    private LinearLayout setListLinearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        loadPreferences();
        setUpViews();
        setUpViewModel();
        getDataFromIntent(getIntent());

    }

    private void loadPreferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        weightIncrement = Double.parseDouble(preferences.getString(getString(R.string.DEFAULT_WEIGHT_INCREMENT_PREFERENCE_KEY), "5"));
        percentageIncrement = Integer.parseInt(preferences.getString(getString(R.string.DEFAULT_PERCENTAGE_INCREMENT_PREFERENCE_KEY), "5"));
        roundingFactor = preferences.getString(getString(R.string.UNIT_SYSTEM_PREFERENCE_KEY), "kg").equals("kg") ? 2.5 : 5;
    }

    private void setUpViewModel() {
        addExerciseViewModel = ViewModelProviders.of(this).get(AddExerciseViewModel.class);

        addExerciseViewModel.getExerciseWithSetsMutableLiveData().observe(this, new Observer<ExerciseWithSets>() {
            @Override
            public void onChanged(ExerciseWithSets exerciseWithSets) {
                currentExerciseWithSets = exerciseWithSets;
                updateViews();
            }
        });

        addExerciseViewModel.getSingleSetToAddMutableLiveData().observe(this, new Observer<SingleSet>() {
            @Override
            public void onChanged(SingleSet singleSet) {
                singleSetToAdd = singleSet;
                updateSetToAddRelatedViews();
            }
        });

        addExerciseViewModel.getAllExerciseNames().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                exerciseNamesList = strings;
            }
        });
    }

    private void getDataFromIntent(Intent intent) {
        ExerciseWithSets exerciseWithSets;
        
        // check if exercise is in EDIT_EXERCISE mode -> we are modifying existing exercise
        if (getIntent().hasExtra(UPDATE_EXERCISE_EXTRA)) {
            activityMode = ActivityMode.EDIT_EXERCISE;
            saveExerciseButton.setText(getString(R.string.label_update_exercise));

            String json = intent.getStringExtra(UPDATE_EXERCISE_EXTRA);
            exerciseWithSets = DataTypeConverter.stringToExerciseWithSets(json);
        }
        
        // check if exercise is in ADD_EXERCISE_TO_ROUTINE mode -> we are adding a new exercise to be a part of a routine template
        else if (getIntent().hasExtra(ROUTINE_ADD_EXERCISE)) {
            activityMode = ActivityMode.ADD_EXERCISE_TO_ROUTINE;

            Exercise exercise = new Exercise("", 5);
            List<SingleSet> singleSetList = new ArrayList<>();
            exerciseWithSets = new ExerciseWithSets(exercise, singleSetList);
        }
        
        // check if exercise is in EDIT_EXERCISE_FROM_ROUTINE mode -> we are editing exercise that is part of routine template
        else if (getIntent().hasExtra(ROUTINE_EDIT_EXERCISE)) {
            activityMode = ActivityMode.EDIT_EXERCISE_FROM_ROUTINE;

            String json = intent.getStringExtra(ROUTINE_EDIT_EXERCISE);
            int exerciseOrderInDay = intent.getIntExtra(ROUTINE_EDIT_EXERCISE_ORDER, -1);

            exerciseWithSets = DataTypeConverter.stringToDayOfRoutine(json).getExerciseWithSetsList().get(exerciseOrderInDay);
        }

        // if nothing above is true we are simply adding new exercise
        else {
            activityMode = ActivityMode.ADD_EXERCISE;
            // TODO[1] value of exerciseOrderInDay should be calculated accordingly
            long exerciseDate = intent.getLongExtra(WorkoutActivity.DATE_OF_EXERCISE_TAG, WorkoutActivity.DEFAULT_EXERCISE_DATE);
            Exercise exercise = new Exercise("", 5, exerciseDate);
            List<SingleSet> singleSetList = new ArrayList<>();
            exerciseWithSets = new ExerciseWithSets(exercise, singleSetList);
        }
        addExerciseViewModel.setInitialValue(exerciseWithSets);
    }

    private void setUpViews() {
        // this line prevents keyboard from opening up on activity start
//        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        onSetClickListener = new OnSetClickListener();

        exerciseNameTextView = findViewById(R.id.addExerciseActivity_tv_exerciseName);
        exerciseNameTextView.setOnClickListener(this);
        exerciseDateTextView = findViewById(R.id.addExerciseActivity_tv_exerciseDate);

        percentageInfoTextView = findViewById(R.id.addExerciseActivity_tv_percentage_info);
        weightIsBasedOnPercentageSwitch = findViewById(R.id.addExerciseActivity_sw_isBasedOnPercentage);
        weightIsBasedOnPercentageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean setIsBasedOnTm) {
                switchTypeOfSetToAdd(setIsBasedOnTm);
            }
        });

        // reps
        Button repsMinusButton = findViewById(R.id.addExerciseActivity_bt_repsMinus);
        repsMinusButton.setOnClickListener(this);
        Button repsPlusButton = findViewById(R.id.addExerciseActivity_bt_repsPlus);
        repsPlusButton.setOnClickListener(this);
        repsTextView = findViewById(R.id.addExerciseActivity_et_reps);
        repsTextView.setOnClickListener(this);

        // weight
        Button weightMinusButton = findViewById(R.id.addExerciseActivity_bt_weightMinus);
        weightMinusButton.setOnClickListener(this);
        Button weightPlusButton = findViewById(R.id.addExerciseActivity_bt_weightPlus);
        weightPlusButton.setOnClickListener(this);
        weightTextView = findViewById(R.id.addExerciseActivity_et_weight);
        weightTextView.setOnClickListener(this);

        // Buttons
        Button addSetButton = findViewById(R.id.addExerciseActivity_bt_addSet);
        addSetButton.setOnClickListener(this);

        Button removeSetButton = findViewById(R.id.addExerciseActivity_bt_deleteSet);
        removeSetButton.setOnClickListener(this);

        saveExerciseButton = findViewById(R.id.addExerciseActivity_bt_saveExercise);
        saveExerciseButton.setOnClickListener(this);

        setListLinearLayout = findViewById(R.id.addExerciseActivity_ll_listOfSets);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String units = preferences.getString(getString(R.string.UNIT_SYSTEM_PREFERENCE_KEY), "UNIT_SYSTEM_PREFERENCE_KEY");
        if (units.equals("lbs")) {
            TextView weightLabelTextView = findViewById(R.id.set_list_top_row_weight_label);
            weightLabelTextView.setText(R.string.weight_lbs);
        }

    }

    private void updateViews() {

        Exercise exercise = currentExerciseWithSets.getExercise();
        List<SingleSet> singleSetList = currentExerciseWithSets.getExerciseSetList();

        try {
            exerciseDateTextView.setText(DateConverterHelper.fromLongToString(exercise.getExerciseDate()));
        } catch (StringIndexOutOfBoundsException ignored) {
        }

        exerciseNameTextView.setText(exercise.getExerciseName());

        setListLinearLayout.removeAllViews();
        for (int i = 0; i < singleSetList.size(); i++) {
            SingleSet singleSet = singleSetList.get(i);
            displaySet(singleSet, (i + 1));
        }

    }

    private void updateSetToAddRelatedViews() {
        String repsText = (singleSetToAdd.getReps() == 0 ? "" : Integer.toString(singleSetToAdd.getReps()));
        String weightText = (singleSetToAdd.getWeight() == 0 ? "" : Double.toString(singleSetToAdd.getWeight()));

        repsTextView.setText(repsText);
        switchTypeOfSetToAdd(singleSetToAdd.isBasedOnTm());

        if(singleSetToAdd.isBasedOnTm()) {
            weightTextView.setHint(getString(R.string.hint_weight_Rm_set));
            weightTextView.setText(singleSetToAdd.getPercentageOfTm() == 0 ? "" : singleSetToAdd.getPercentageOfTm() + "%");

            String percentageInfo = singleSetToAdd.getPercentageOfTm() + "% " + getString(R.string.multiplication_sign) + " " + singleSetToAdd.getTrainingMax() + " " + getString(R.string.almost_equal_sign) + " " + singleSetToAdd.getWeight();
            percentageInfoTextView.setText(percentageInfo);
            percentageInfoTextView.setVisibility(View.VISIBLE);
        }
        else {
            weightTextView.setHint(getString(R.string.weight));
            weightTextView.setText(weightText);
            percentageInfoTextView.setVisibility(View.GONE);
        }

    }

    // this method adds new set or populates view with existing sets
    private void displaySet(SingleSet singleSet, int setNumber) {
        SetView setView = new SetView(this);
        setView.setNumber(setNumber);
        setView.setWeight(singleSet.getWeight());
        setView.setReps(singleSet.getReps());

        if (singleSet.isBasedOnTm()) {
            setView.setPercentageOfTm(singleSet.getPercentageOfTm());
            setView.setTrainingMax(singleSet.getTrainingMax());
            setView.setAdditionalInfoIsVisible(true);
        }
        else {
            // visibility is GONE by default - we need it to be INVISIBLE so it aligns properly with sets that have additional info
            setView.setAdditionalInfoIsVisible(false);
        }
        setView.setTag(setNumber);
        setView.setOnClickListener(onSetClickListener);

       int paddingInDp = (int) (getResources().getDimension(R.dimen.small_margin) * getResources().getDisplayMetrics().density / 2);
        setView.setPadding(0, 0, 0, paddingInDp);

        setListLinearLayout.addView(setView);

    }

    private void removeLastSet() {
        addExerciseViewModel.removeLastSet();
    }

    private void onSaveButtonPress() {
        switch (activityMode) {
            case ADD_EXERCISE:
            case EDIT_EXERCISE:
                addExerciseViewModel.saveToDb(activityMode);
                break;
            case ADD_EXERCISE_TO_ROUTINE:
                addNewExerciseToRoutine();
                break;
            case EDIT_EXERCISE_FROM_ROUTINE:
                updateExerciseInRoutine();

                break;
        }
        finish();
    }

    private void addNewExerciseToRoutine() {
        ExerciseWithSets exerciseWithSetsToAdd = addExerciseViewModel.getExerciseWithSetsMutableLiveData().getValue();
        String dayOfRoutineJson = getIntent().getStringExtra(ROUTINE_ADD_EXERCISE);
        DayOfRoutine dayOfRoutine = DataTypeConverter.stringToDayOfRoutine(dayOfRoutineJson);
        dayOfRoutine.getExerciseWithSetsList().add(exerciseWithSetsToAdd);

        Intent intent = new Intent();
        intent.putExtra(ROUTINE_RESULT, DataTypeConverter.dayOfRoutineToString(dayOfRoutine));
        setResult(Activity.RESULT_OK, intent);
    }

    private void updateExerciseInRoutine() {
        ExerciseWithSets exerciseWithSetsToAdd = addExerciseViewModel.getExerciseWithSetsMutableLiveData().getValue();
        String dayOfRoutineJson = getIntent().getStringExtra(ROUTINE_EDIT_EXERCISE);
        DayOfRoutine dayOfRoutine = DataTypeConverter.stringToDayOfRoutine(dayOfRoutineJson);
        dayOfRoutine.getExerciseWithSetsList().set(getIntent().getIntExtra(ROUTINE_EDIT_EXERCISE_ORDER, -1), exerciseWithSetsToAdd);

        Intent intent = new Intent();
        intent.putExtra(ROUTINE_RESULT, DataTypeConverter.dayOfRoutineToString(dayOfRoutine));
        setResult(Activity.RESULT_OK, intent);
    }

    // 4 methods changing interface accordingly to what buttons were pressed and if the weight will be added based on TM (tmPercentageMode)
    private void repsMinus() {
        addExerciseViewModel.setToAddModifyRepsBy(-1);
    }

    private void repsPlus() {
        addExerciseViewModel.setToAddModifyRepsBy(1);
    }

    private void weightMinus() {
        addExerciseViewModel.setToAddModifyWeightBy(-weightIncrement, -percentageIncrement);
    }

    private void weightPlus() {
        addExerciseViewModel.setToAddModifyWeightBy(weightIncrement, percentageIncrement);
    }

    // this is prompted by switching set type switch - it changes slightly UI accordingly to tmPercentageMode
    private void switchTypeOfSetToAdd(boolean setIsBasedOnTm) {

        // checks if switch position is same as state of currentSetToAdd
        if (weightIsBasedOnPercentageSwitch.isChecked() != setIsBasedOnTm) {
            weightIsBasedOnPercentageSwitch.setChecked(setIsBasedOnTm);
        }

        if (setIsBasedOnTm) {
            double trainingMax = getRmFromPreferences();
            if (trainingMax == 0) {
                editRmDialog(0);
            }
            else {
                addExerciseViewModel.setToAddSetTmMax(trainingMax);
            }

        }
        else {
            addExerciseViewModel.setToAddSetTmMax(0);
        }
    }

    private double getRmFromPreferences() {
        SharedPreferences preferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        String exerciseName = currentExerciseWithSets.getExercise().getExerciseName();
        return Double.longBitsToDouble(preferences.getLong(exerciseName, 0));
    }

    private void editNameDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_training_max_alert, null);
        TextView titleTextView = dialogView.findViewById(R.id.dialog_tv_title);
        titleTextView.setText(getString(R.string.edit_exercise_name));

        final AutoCompleteTextView nameEditText = dialogView.findViewById(R.id.dialog_edit_text);
        nameEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        nameEditText.setText(currentExerciseWithSets.getExercise().getExerciseName());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, exerciseNamesList.toArray(new String[0]));
        nameEditText.setAdapter(adapter);

        TextInputLayout textInputLayout = dialogView.findViewById(R.id.dialog_text_input_layout);
        textInputLayout.setHint(getString(R.string.hint_exercise_name));

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setView(dialogView)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String exerciseName = nameEditText.getText().toString();
                        if (exerciseName.trim().isEmpty() || exerciseName.equals("")) {
                            dialogInterface.dismiss();
                        }
                        else {
                            addExerciseViewModel.setName(exerciseName);
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    // if set type is switched to percentage mode and id doesn't have saved TM in SharedPreferences this method is called
    // it pops up a dialog for the user to enter TM and then saves it in SharedPreferences
    private void editRmDialog(double currentRm) {
        String exerciseName = currentExerciseWithSets.getExercise().getExerciseName();

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_training_max_alert, null);
        final EditText trainingMaxEditText = dialogView.findViewById(R.id.dialog_edit_text);
        String rmText = (currentRm == 0 ? "" : Double.toString(currentRm));
        trainingMaxEditText.setText(rmText);

        TextInputLayout textInputLayout = dialogView.findViewById(R.id.dialog_text_input_layout);
        textInputLayout.setHint(getString(R.string.hint_edit_Rm_dialog) + " " + exerciseName + ": ");

        TextView dialogTitles = dialogView.findViewById(R.id.dialog_tv_title);
        dialogTitles.setText(getString(R.string.edit_rm_dialog_title));

        dialogBuilder.setView(dialogView)
                .setCancelable(false)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            double trainingMax = Double.valueOf(trainingMaxEditText.getText().toString());
                            saveTmInPreferences(trainingMax);
                        } catch (NumberFormatException e) {
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        weightIsBasedOnPercentageSwitch.setChecked(false);
                        addExerciseViewModel.setToAddSetTmMax(0);
                    }
                })
                .show();
    }

    private void updateSetsToCurrentRmDialog(final double newRm) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder
                .setTitle(getString(R.string.dialog_update_existing_sets_title))
                .setMessage(getString(R.string.dialog_update_existing_sets_message))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        addExerciseViewModel.updateExerciseWithSetsWithNewTm(newRm);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();

    }

    private void editWeightDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_training_max_alert, null);
        TextView titleTextView = dialogView.findViewById(R.id.dialog_tv_title);
        titleTextView.setText((weightIsBasedOnPercentageSwitch.isChecked() ? getString(R.string.hint_weight_Rm_set) : getString(R.string.dialog_edit_weight_title)));
        final EditText weightEditText = dialogView.findViewById(R.id.dialog_edit_text);
        weightEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.dialog_text_input_layout);

        textInputLayout.setHint((weightIsBasedOnPercentageSwitch.isChecked() ? getString(R.string.hint_weight_Rm_set) : getString(R.string.weight)));

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setView(dialogView)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            double weight = Double.parseDouble(weightEditText.getText().toString());
                            addExerciseViewModel.setToAddSetWeight(weight);
                        } catch (NumberFormatException e) {
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void editRepsDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_training_max_alert, null);
        TextView titleTextView = dialogView.findViewById(R.id.dialog_tv_title);
        titleTextView.setText(getString(R.string.dialog_edit_reps_title));
        final EditText repsEditText = dialogView.findViewById(R.id.dialog_edit_text);
        repsEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        TextInputLayout textInputLayout = dialogView.findViewById(R.id.dialog_text_input_layout);
        textInputLayout.setHint(getString(R.string.reps));

        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(this);
        dialogBuilder.setView(dialogView)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int reps = 0;
                        try {
                            reps = Integer.parseInt(repsEditText.getText().toString());
                            addExerciseViewModel.setToAddSetReps(reps);
                        } catch (NumberFormatException e) {
                            dialogInterface.dismiss();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void saveTmInPreferences(double newTrainingMax) {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_FILE, MODE_PRIVATE).edit();
        editor.putLong(currentExerciseWithSets.getExercise().getExerciseName(), Double.doubleToLongBits(newTrainingMax));
        editor.apply();
        addExerciseViewModel.setToAddSetTmMax(newTrainingMax);
        if (currentExerciseWithSets.hasSetsBasedOnRm()) {
            updateSetsToCurrentRmDialog(newTrainingMax);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.addExerciseActivity_tv_exerciseName):
                editNameDialog();
                break;
            case (R.id.addExerciseActivity_bt_repsMinus):
                repsMinus();
                break;
            case (R.id.addExerciseActivity_et_reps):
                editRepsDialog();
                break;
            case (R.id.addExerciseActivity_bt_repsPlus):
                repsPlus();
                break;
            case (R.id.addExerciseActivity_bt_weightMinus):
                weightMinus();
                break;
            case (R.id.addExerciseActivity_et_weight):
                editWeightDialog();
                break;
            case (R.id.addExerciseActivity_bt_weightPlus):
                weightPlus();
                break;
            case (R.id.addExerciseActivity_bt_addSet):
                if (addExerciseViewModel.getSingleSetToAddMutableLiveData().getValue().getReps() == 0) {
                    Toast.makeText(this, getString(R.string.toast_zero_reps_warning), Toast.LENGTH_LONG).show();
                }
                else {
                    addExerciseViewModel.addSet();
                }
                break;

            case (R.id.addExerciseActivity_bt_deleteSet):
                removeLastSet();
                break;
            case (R.id.addExerciseActivity_bt_saveExercise):
                // mis-clicking prevention, using threshold of 1000 ms
                if (SystemClock.elapsedRealtime() - lastClickTime < 1000){
                    return;
                }
                lastClickTime = SystemClock.elapsedRealtime();
                onSaveButtonPress();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_exercise_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.addExerciseActivity_menu_edit_RM) {
            editRmDialog(getRmFromPreferences());
        }

        return super.onOptionsItemSelected(item);
    }



    private class OnSetClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            SetView setView = (SetView) view;
            Log.i(TAG, setView.toString());

            SingleSet singleSet = addExerciseViewModel.getSingleSetToAddMutableLiveData().getValue();
            singleSet.setPercentageOfTm(setView.getPercentageOfTm());
            singleSet.setTrainingMax(setView.getTrainingMax());

            if (setView.hasAdditionalInfo()) {
                singleSet.updateWeightForCurrentPercentageOfTm(roundingFactor);
            }
            else {
                singleSet.setWeight(setView.getWeight());
            }
            singleSet.setReps(setView.getReps());
            addExerciseViewModel.getSingleSetToAddMutableLiveData().setValue(singleSet);

        }
    }
    
    public enum ActivityMode{
        ADD_EXERCISE,
        EDIT_EXERCISE,
        ADD_EXERCISE_TO_ROUTINE,
        EDIT_EXERCISE_FROM_ROUTINE
    }

}


//TODO[2]: should override onBackPressed to prompt user if he wants to save or discard current exercise