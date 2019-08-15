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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.GymLogRepository;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String DATE_OF_EXERCISE_TAG = "DATE_OF_EXERCISE_TAG";
    private static final int COPY_EXERCISES_REQUEST_CODE = 31416;
    // TODO[3]: standardize field names and widgets ids
    private final String TAG = "KCTag-" + WorkoutActivity.class.getSimpleName();
    // TODO[3]: not sure if this variable is needed yet
    private long mDateOfExercise;
    private GymLogViewModel mGymLogViewModel;
    private ExerciseAdapter mExerciseAdapter;
    private int[] idsOfExercisesToCopyArray;

    private TextView mDateOfExerciseTextView;
    private ImageView mPreviousDayArrowImageView;
    private ImageView mNextDayArrowImageView;
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

        Log.i(TAG, "Date of exercise: " + mDateOfExercise);
    }

    private void grabDataFromIntent() {
        mDateOfExercise = getIntent().getLongExtra(DATE_OF_EXERCISE_TAG, 19901030);
    }

    private void setupViews() {
        mDateOfExerciseTextView = findViewById(R.id.workout_tv_exerciseDate);
        mPreviousDayArrowImageView = findViewById(R.id.workout_iv_leftArrow);
        mPreviousDayArrowImageView.setOnClickListener(this);
        mNextDayArrowImageView = findViewById(R.id.workout_iv_rightArrow);
        mNextDayArrowImageView.setOnClickListener(this);
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
        mExerciseAdapter = new ExerciseAdapter(this);
        mExerciseRecyclerView.setAdapter(mExerciseAdapter);
        mExerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i(TAG, "setting up RecyclerView");
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
                Log.i(TAG, "change in data observed");
                mExerciseAdapter.setExercises(exercisesWithSets);
                mExerciseAdapter.notifyDataSetChanged();
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

    private void getExercisesWithSetsToCopyFromDb(int[] idArray) {
        GetExercisesFromDbAsyncTask task = new GetExercisesFromDbAsyncTask(GymLogRepository.getInstance(getApplication()));
        idsOfExercisesToCopyArray = idArray;
        task.execute();
    }

    private void insertCopiedExercisesWithSetsToDb(List<ExerciseWithSets> list) {
        for (ExerciseWithSets eWs : list) {
            ExerciseWithSets exerciseWithSetsToAdd = ExerciseWithSets.createNewFromExisting(eWs);
            exerciseWithSetsToAdd.getExercise().setExerciseDate(mDateOfExercise);
            mGymLogViewModel.insertExerciseWithSets(exerciseWithSetsToAdd);
        }
    }

    private void deleteExercisesInCurrentDay() {
        List<ExerciseWithSets> exerciseWithSetsList = mExerciseAdapter.getmExercisesWithSets();
        List<Exercise> exercisesToDelete = new ArrayList<>();
        for (ExerciseWithSets eWs : exerciseWithSetsList) {
            exercisesToDelete.add(eWs.getExercise());
        }
        mGymLogViewModel.deleteExercises(exercisesToDelete);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.workout_iv_leftArrow):
                loadPreviousDay();
                break;

            case (R.id.workout_iv_rightArrow):
                loadNextDay();
                break;

            case (R.id.workout_bt_copy_previous):
                startCopyExerciseActivity();
                break;

            // OR statement within switch
            case (R.id.workout_bt_add_exercise_additional):
            case (R.id.workout_fab_addExercise):
                Intent intent = new Intent(this, AddExerciseActivity.class);
                intent.putExtra(DATE_OF_EXERCISE_TAG, mDateOfExercise);
                startActivity(intent);
                break;

            case (R.id.workout_bt_routine):
                // add exercises from routine
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
            } else if (resultCode == RESULT_CANCELED) {
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
            case R.id.workout_menu_copy:
                startCopyExerciseActivity();
                break;
            case R.id.workout_menu_addFromRoutine:
                Toast.makeText(this, "not implemented yet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.workout_menu_delete:
                deleteExercisesInCurrentDay();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // TODO[1]: this should be static or leaks will occur
    private class GetExercisesFromDbAsyncTask extends AsyncTask <Void , Void, List<ExerciseWithSets>> {

        GymLogRepository repository;

        GetExercisesFromDbAsyncTask (GymLogRepository gymLogRepository) {
            repository = gymLogRepository;
        }

        @Override
        protected List<ExerciseWithSets> doInBackground(Void... voids) {
            return repository.getExerciseWithSetsByIds(idsOfExercisesToCopyArray);
        }

        @Override
        protected void onPostExecute(List<ExerciseWithSets> exerciseWithSets) {
            insertCopiedExercisesWithSetsToDb(exerciseWithSets);
        }
    }
}
