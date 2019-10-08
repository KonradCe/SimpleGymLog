package pl.kcworks.simplegymlog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.db.DataTypeConverter;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATE_OF_EXERCISE_TAG = "DATE_OF_EXERCISE_TAG";
    private static final int COPY_EXERCISES_REQUEST_CODE = 31416;
    private static final int SELECT_DAY_OF_ROUTINE_REQUEST_CODE = 31417;

    private final String TAG = "KCTag-" + WorkoutActivity.class.getSimpleName();
    // TODO[3]: I don't like storing exercise date in this place, shouldn't this be in ViewModel only?
    private long dateOfExercise;
    private GymLogViewModel gymLogViewModel;
    private WorkoutAdapter workoutAdapter;

    private TextView dateOfExerciseTextView;
    private RecyclerView exerciseRecyclerView;
    private SpeedDialView fab;
    private Button addExercisesFromRoutineButton;
    private Button copyPreviousWorkoutButton;
    private Button addExerciseAdditionalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        grabDataFromIntent();
        setupViews();
        displayCurrentDate();
        setupRecyclerView();

        gymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);
        loadExercisesForDate(dateOfExercise);

    }

    private void grabDataFromIntent() {
        dateOfExercise = getIntent().getLongExtra(DATE_OF_EXERCISE_TAG, 19901030);
    }

    private void setupViews() {
        dateOfExerciseTextView = findViewById(R.id.workout_tv_exerciseDate);
        Button previousDayArrowButton = findViewById(R.id.workout_bt_leftArrow);
        previousDayArrowButton.setOnClickListener(this);
        Button nextDayArrowButton = findViewById(R.id.workout_bt_rightArrow);
        nextDayArrowButton.setOnClickListener(this);
        exerciseRecyclerView = findViewById(R.id.workout_rv_exercises);
        setUpFab();

        // additional buttons that show up only when there are no exercises
        addExercisesFromRoutineButton = findViewById(R.id.workout_bt_routine);
        addExercisesFromRoutineButton.setOnClickListener(this);
        copyPreviousWorkoutButton = findViewById(R.id.workout_bt_copy_previous);
        copyPreviousWorkoutButton.setOnClickListener(this);
        addExerciseAdditionalButton = findViewById(R.id.workout_bt_add_exercise_additional);
        addExerciseAdditionalButton.setOnClickListener(this);
    }

    private void setUpFab() {
        fab = findViewById(R.id.fab);

        SpeedDialActionItem addExerciseButton = new SpeedDialActionItem.Builder(R.id.fab_item_add_exercise, R.drawable.ic_add_black_24dp)
                .setLabel(getString(R.string.label_add_exercise))
                .setTheme(R.style.Fab)
                .create();
        SpeedDialActionItem insertRoutineButton = new SpeedDialActionItem.Builder(R.id.fab_item_add_routine, R.drawable.ic_add_black_24dp)
                .setLabel(getString(R.string.label_insert_routine))
                .setTheme(R.style.Fab)
                .create();
        SpeedDialActionItem copyPreviousDayButton = new SpeedDialActionItem.Builder(R.id.fab_item_copy_previous, R.drawable.ic_add_black_24dp)
                .setLabel(getString(R.string.label_copy_day))
                .setTheme(R.style.Fab)
                .create();
        fab.addActionItem(addExerciseButton);
        fab.addActionItem(copyPreviousDayButton);
        fab.addActionItem(insertRoutineButton);

        fab.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem actionItem) {
                switch (actionItem.getId()) {
                    case R.id.fab_item_add_exercise:
                        fab.close();
                        startAddExerciseActivity();
                        break;
                    case R.id.fab_item_copy_previous:
                        fab.close();
                        startCopyExerciseActivity();
                        break;
                    case R.id.fab_item_add_routine:
                        fab.close();
                        startInsertRoutineActivity();
                        break;
                }
                return false;
            }
        });
    }

    private void displayCurrentDate() {
        dateOfExerciseTextView.setText(DateConverterHelper.fromLongToString(dateOfExercise));
    }

    private void setupRecyclerView() {
        workoutAdapter = new WorkoutAdapter(this);
        exerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        exerciseRecyclerView.setAdapter(workoutAdapter);
    }

    private void loadExercisesForDate(long dateInGymLogFormat) {
        gymLogViewModel.getExercisesWithSetsForDate(dateInGymLogFormat).observe(this, new Observer<List<ExerciseWithSets>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseWithSets> exercisesWithSets) {
                if (exercisesWithSets.isEmpty()) {
                    displayNewWorkoutOptions();
                } else {
                    hideNewWorkoutOptions();
                }
                workoutAdapter.setExercises(exercisesWithSets);
                workoutAdapter.notifyDataSetChanged();
            }
        });
    }

    private void displayNewWorkoutOptions() {
        exerciseRecyclerView.setVisibility(View.GONE);
        addExercisesFromRoutineButton.setVisibility(View.VISIBLE);
        copyPreviousWorkoutButton.setVisibility(View.VISIBLE);
        addExerciseAdditionalButton.setVisibility(View.VISIBLE);
    }

    private void hideNewWorkoutOptions() {
        exerciseRecyclerView.setVisibility(View.VISIBLE);
        addExercisesFromRoutineButton.setVisibility(View.GONE);
        copyPreviousWorkoutButton.setVisibility(View.GONE);
        addExerciseAdditionalButton.setVisibility(View.GONE);
    }

    private void loadPreviousDay() {
        dateOfExercise--;
        loadExercisesForDate(dateOfExercise);
        displayCurrentDate();
    }

    private void loadNextDay() {
        dateOfExercise++;
        loadExercisesForDate(dateOfExercise);
        displayCurrentDate();
    }

    private void getExercisesWithSetsToCopyFromDb(int[] idIntArray) {
        AsyncResponse asyncResponse = new AsyncResponse() {
            @Override
            public void onTaskCompleted(List<ExerciseWithSets> exerciseWithSets) {
                insertCopiedExercisesWithSetsToDb(exerciseWithSets);
            }
        };
        GetExercisesFromDbAsyncTask task = new GetExercisesFromDbAsyncTask(GymLogRepository.getInstance(getApplication()), asyncResponse);
        // TODO[3]: this conversion from int[] to Integer[] has to be done twice, first one way, than the other, this is ineffective and something should be done about that
        // we read and write to db using int[] but we need ids in Integer[] in order to pass them as a parameter to the AsyncTask
        Integer[] idsToCopyIntegerArray = new Integer[idIntArray.length];
        for (int i = 0; i < idsToCopyIntegerArray.length; i++) {
            idsToCopyIntegerArray[i] = idIntArray[i];
        }

        task.execute(idsToCopyIntegerArray);
    }

    private void insertCopiedExercisesWithSetsToDb(List<ExerciseWithSets> list) {
        for (ExerciseWithSets eWs : list) {
            ExerciseWithSets exerciseWithSetsToAdd = ExerciseWithSets.createNewFromExisting(eWs);
            exerciseWithSetsToAdd.getExercise().setExerciseDate(dateOfExercise);
            gymLogViewModel.insertExerciseWithSets(exerciseWithSetsToAdd);
        }
    }

    private void deleteExercisesInCurrentDay() {
        List<ExerciseWithSets> exerciseWithSetsList = workoutAdapter.getExercisesWithSets();
        List<Exercise> exercisesToDelete = new ArrayList<>();
        for (ExerciseWithSets eWs : exerciseWithSetsList) {
            exercisesToDelete.add(eWs.getExercise());
        }
        gymLogViewModel.deleteExercises(exercisesToDelete);
    }

    private void startCopyExerciseActivity() {
        // copy exercises from previous day
        Intent intent = new Intent(this, CopyExercisesActivity.class);
        startActivityForResult(intent, COPY_EXERCISES_REQUEST_CODE);
    }

    private void startInsertRoutineActivity() {
        Intent selectDayOfRoutineIntent = new Intent(this, RoutineSelectorActivity.class);
        selectDayOfRoutineIntent.putExtra(RoutineSelectorActivity.SELECTOR_ACTIVITY_MODE, RoutineSelectorActivity.SELECT_DAY);
        startActivityForResult(selectDayOfRoutineIntent, SELECT_DAY_OF_ROUTINE_REQUEST_CODE);
    }

    private void startAddExerciseActivity() {
        Intent addExerciseActivityIntent = new Intent(this, AddExerciseActivity.class);
        addExerciseActivityIntent.putExtra(DATE_OF_EXERCISE_TAG, dateOfExercise);
        // TODO[2]: should this be startActivityForResult?
        startActivity(addExerciseActivityIntent);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.workout_bt_leftArrow):
                loadPreviousDay();
                break;

            case (R.id.workout_bt_rightArrow):
                loadNextDay();
                break;

            case (R.id.workout_bt_copy_previous):
                startCopyExerciseActivity();
                break;

            case (R.id.workout_bt_add_exercise_additional):
                startAddExerciseActivity();
                break;

            case (R.id.workout_bt_routine):
                startInsertRoutineActivity();
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == COPY_EXERCISES_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int[] idsOfExercisesToCopy = data.getIntArrayExtra(CopyExercisesActivity.IDS_OF_EXERCISES_TO_COPY_TAG);
                getExercisesWithSetsToCopyFromDb(idsOfExercisesToCopy);
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "result canceled", Toast.LENGTH_SHORT).show();
            }
        }
        else if (requestCode == SELECT_DAY_OF_ROUTINE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String dayOfRoutineJson = data.getStringExtra(RoutineSelectorActivity.DAY_OUF_ROUTINE_STRING_EXTRA);
                gymLogViewModel.insertDayOfRoutineAsExercises(DataTypeConverter.stringToDayOfRoutine(dayOfRoutineJson), dateOfExercise);
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "result canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.workout_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.workout_menu_delete) {
            deleteExercisesInCurrentDay();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Exercise exerciseToDelete = workoutAdapter.getExercisesWithSets().get(item.getGroupId()).getExercise();
        gymLogViewModel.deleteSingleExercise(exerciseToDelete);
        return true;
    }

    private static class GetExercisesFromDbAsyncTask extends AsyncTask <Integer , Void, List<ExerciseWithSets>> {

        private GymLogRepository repository;
        private AsyncResponse listener;
        private

        GetExercisesFromDbAsyncTask(GymLogRepository repository, AsyncResponse listener) {
            this.repository = repository;
            this.listener = listener;
        }

        @Override
        protected List<ExerciseWithSets> doInBackground(Integer... ints) {
            int[] idIntArray = new int[ints.length];
            for (int i = 0; i < ints.length; i++) {
                idIntArray[i] = ints[i];
            }
            return repository.getExerciseWithSetsByIds(idIntArray);
        }

        @Override
        protected void onPostExecute(List<ExerciseWithSets> exerciseWithSets) {
            listener.onTaskCompleted(exerciseWithSets);
        }
    }
}
