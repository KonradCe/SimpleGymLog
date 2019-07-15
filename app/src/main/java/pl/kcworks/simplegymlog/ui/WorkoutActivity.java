package pl.kcworks.simplegymlog.ui;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO[3]: standardize field names and widgets ids
    private final String TAG = "KCTag-" + WorkoutActivity.class.getSimpleName();
    public static final String DATE_OF_EXERCISE_TAG = "DATE_OF_EXERCISE_TAG";
    private static final int COPY_EXERCISES_REQUEST_CODE = 31416;

    // TODO[3]: not sure if this variable is needed yet
    private long mDateOfExercise;
    private GymLogViewModel mGymLogViewModel;
    private ExerciseAdapter mExerciseAdapter;

    private TextView mDateOfExerciseTextView;
    private ImageView mPreviousDayArrowImageView;
    private ImageView mNextDayArrowImageView;
    private RecyclerView mExerciseRecyclerView;
    private LinearLayout mAddExercisesOptionsLinearLayout;
    private Button mAddExerciseButton;

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
        mAddExerciseButton = findViewById(R.id.workout_bt_add_exercise);
        mAddExerciseButton.setOnClickListener(this);


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
        Log.i(TAG, "getExercisesWithSets method is being observed - subscribed to ViewModel");
        if (mGymLogViewModel.getExercisesWithSetsForDate(dateInGymLogFormat).hasObservers()) {
//            mGymLogViewModel.getExercisesWithSetsForDate(dateInGymLogFormat).removeObservers(this);
            Log.i("dziab", "mGymLogViewModel.getExercisesWithSetsForDate(dateInGymLogFormat) had observers, they were removed");
        }
        mGymLogViewModel.getExercisesWithSetsForDate(dateInGymLogFormat).observe(this, new Observer<List<ExerciseWithSets>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseWithSets> exercisesWithSets) {
                if (exercisesWithSets.isEmpty()) {
                    displayNewWorkoutOptions();
                }
                else {
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
        mAddExerciseButton.setVisibility(View.INVISIBLE);
    }

    private void hideNewWorkoutOptions() {
        mExerciseRecyclerView.setVisibility(View.VISIBLE);
        mAddExercisesOptionsLinearLayout.setVisibility(View.GONE);
        mAddExerciseButton.setVisibility(View.VISIBLE);
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

    private void copyExercisesFromPreviousDayButton() {
        // copy exercises from previous day
        Intent intent = new Intent(this, CopyExercisesActivity.class);
        startActivityForResult(intent, COPY_EXERCISES_REQUEST_CODE);
    }

    private void copyExercisesWithId(int[] idArray) {
/*        final Observer<ExerciseWithSets> observer = new Observer<ExerciseWithSets>() {
            @Override
            public void onChanged(@Nullable ExerciseWithSets exerciseWithSets) {
                ExerciseWithSets newExerciseWithSets  = ExerciseWithSets.createNewFromExisting(exerciseWithSets);
                newExerciseWithSets.getExercise().setExerciseDate(mDateOfExercise);
                mGymLogViewModel.insertExercisesWithSets(newExerciseWithSets);
                mGymLogViewModel.insertExercisesWithSets();
                Log.i("dziab","Kopiuje cwiczenie");
            }
        };*/

        for (final int id : idArray) {
            mGymLogViewModel.getExerciseWithSetById(id).observe(this, new Observer<ExerciseWithSets>() {
                @Override
                public void onChanged(@Nullable ExerciseWithSets exerciseWithSets) {
                    ExerciseWithSets newExerciseWithSets  = ExerciseWithSets.createNewFromExisting(exerciseWithSets);
                    newExerciseWithSets.getExercise().setExerciseDate(mDateOfExercise);
                    mGymLogViewModel.insertExercisesWithSets(newExerciseWithSets);

                    Log.i("dziab","Kopiuje cwiczenie o id: " + id);
                }
            });
        }
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
                copyExercisesFromPreviousDayButton();
                break;

            // OR statement within switch
            case (R.id.workout_bt_add_exercise_additional):
            case (R.id.workout_bt_add_exercise):
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

        if(requestCode == COPY_EXERCISES_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                int[] idsOfExercisesToCopy = data.getIntArrayExtra(CopyExercisesActivity.IDS_OF_EXERCISES_TO_COPY_TAG);
                copyExercisesWithId(idsOfExercisesToCopy);
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "result canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
