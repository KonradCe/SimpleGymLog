package pl.kcworks.simplegymlog.ui;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.db.DataTypeConverter;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATE_OF_EXERCISE_TAG = "DATE_OF_EXERCISE_TAG";
    private static final int COPY_EXERCISES_REQUEST_CODE = 31416;
    private static final int SELECT_DAY_OF_ROUTINE_REQUEST_CODE = 31417;
    // TODO[3]: standardize field names and widgets ids
    private final String TAG = "KCTag-" + WorkoutActivity.class.getSimpleName();
    // TODO[3]: not sure if this variable is needed yet
    private long mDateOfExercise;
    private GymLogViewModel mGymLogViewModel;
    private WorkoutAdapter mWorkoutAdapter;


    private TextView mDateOfExerciseTextView;
    private Button mPreviousDayArrowButton;
    private Button mNextDayArrowButton;
    private RecyclerView mExerciseRecyclerView;
    private LinearLayout mAddExercisesOptionsLinearLayout;
    private FloatingActionButton mAddExerciseFaB;

    // additional buttons that show up only when there is no exercises for this day, probably will be replaced by FAB
    private Button mAddExercisesFromRoutineButton;
    private Button mCopyPreviousWorkoutButton;
    private Button mAddExerciseAdditionalButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        grabDataFromIntent();
        setupViews();
        displayCurrentDate();
        setupRecyclerView();

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);
        loadExercisesForDate(mDateOfExercise);

    }

    private void grabDataFromIntent() {
        mDateOfExercise = getIntent().getLongExtra(DATE_OF_EXERCISE_TAG, 19901030);
    }

    private void setupViews() {
        mDateOfExerciseTextView = findViewById(R.id.workout_tv_exerciseDate);
        mPreviousDayArrowButton = findViewById(R.id.workout_bt_leftArrow);
        mPreviousDayArrowButton.setOnClickListener(this);
        mNextDayArrowButton = findViewById(R.id.workout_bt_rightArrow);
        mNextDayArrowButton.setOnClickListener(this);
        mExerciseRecyclerView = findViewById(R.id.workout_rv_exercises);
        mAddExercisesOptionsLinearLayout = findViewById(R.id.workout_ll_new_exercises_options);
        mAddExerciseFaB = findViewById(R.id.workout_fab_addExercise);
        mAddExerciseFaB.setOnClickListener(this);


        // additional buttons that show up only when there are, probably will be replaced by FAB
        mAddExercisesFromRoutineButton = findViewById(R.id.workout_bt_routine);
        mAddExercisesFromRoutineButton.setOnClickListener(this);
        mCopyPreviousWorkoutButton = findViewById(R.id.workout_bt_copy_previous);
        mCopyPreviousWorkoutButton.setOnClickListener(this);
        mAddExerciseAdditionalButton = findViewById(R.id.workout_bt_add_exercise_additional);
        mAddExerciseAdditionalButton.setOnClickListener(this);
    }

    private void displayCurrentDate() {
        mDateOfExerciseTextView.setText(DateConverterHelper.fromLongToString(mDateOfExercise));
    }

    private void setupRecyclerView() {
        mWorkoutAdapter = new WorkoutAdapter(this);
        mExerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mExerciseRecyclerView.setAdapter(mWorkoutAdapter);
    }

    private void loadExercisesForDate(long dateInGymLogFormat) {
        mGymLogViewModel.getExercisesWithSetsForDate(dateInGymLogFormat).observe(this, new Observer<List<ExerciseWithSets>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseWithSets> exercisesWithSets) {
                if (exercisesWithSets.isEmpty()) {
                    displayNewWorkoutOptions();
                } else {
                    hideNewWorkoutOptions();
                }
                mWorkoutAdapter.setExercises(exercisesWithSets);
                mWorkoutAdapter.notifyDataSetChanged();
            }
        });
    }

    private void displayNewWorkoutOptions() {
        mExerciseRecyclerView.setVisibility(View.GONE);
        mAddExercisesOptionsLinearLayout.setVisibility(View.VISIBLE);
    }

    private void hideNewWorkoutOptions() {
        mExerciseRecyclerView.setVisibility(View.VISIBLE);
        mAddExercisesOptionsLinearLayout.setVisibility(View.GONE);
    }

    private void loadPreviousDay() {
        mDateOfExercise--;
        loadExercisesForDate(mDateOfExercise);
        displayCurrentDate();
    }

    private void loadNextDay() {
        mDateOfExercise++;
        loadExercisesForDate(mDateOfExercise);
        displayCurrentDate();
    }

    private void startCopyExerciseActivity() {
        // copy exercises from previous day
        Intent intent = new Intent(this, CopyExercisesActivity.class);
        startActivityForResult(intent, COPY_EXERCISES_REQUEST_CODE);
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
            exerciseWithSetsToAdd.getExercise().setExerciseDate(mDateOfExercise);
            mGymLogViewModel.insertExerciseWithSets(exerciseWithSetsToAdd);
        }
    }

    private void deleteExercisesInCurrentDay() {
        List<ExerciseWithSets> exerciseWithSetsList = mWorkoutAdapter.getmExercisesWithSets();
        List<Exercise> exercisesToDelete = new ArrayList<>();
        for (ExerciseWithSets eWs : exerciseWithSetsList) {
            exercisesToDelete.add(eWs.getExercise());
        }
        mGymLogViewModel.deleteExercises(exercisesToDelete);
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
            case (R.id.workout_fab_addExercise):
                Intent addExerciseActivityIntent = new Intent(this, AddExerciseActivity.class);
                addExerciseActivityIntent.putExtra(DATE_OF_EXERCISE_TAG, mDateOfExercise);
                startActivity(addExerciseActivityIntent);
                break;

            case (R.id.workout_bt_routine):
                Intent selectDayOfRoutineIntent = new Intent(this, RoutineSelectorActivity.class);
                selectDayOfRoutineIntent.putExtra(RoutineSelectorActivity.SELECTOR_ACTIVITY_MODE, RoutineSelectorActivity.SELECT_DAY);
                startActivityForResult(selectDayOfRoutineIntent, SELECT_DAY_OF_ROUTINE_REQUEST_CODE);
                break;

        }
        // TODO[2]: should this be startActivityForResult?

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
                mGymLogViewModel.insertDayOfRoutineAsExercises(DataTypeConverter.stringToDayOfRoutine(dayOfRoutineJson), mDateOfExercise);
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
        switch (item.getItemId()) {
            case R.id.workout_menu_delete:
                deleteExercisesInCurrentDay();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Exercise exerciseToDelete = mWorkoutAdapter.getmExercisesWithSets().get(item.getGroupId()).getExercise();
        mGymLogViewModel.deleteSingleExercise(exerciseToDelete);
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
