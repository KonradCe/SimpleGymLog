package pl.kcworks.simplegymlog.ui;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.dialog.OnDaysSelectionListener;
import com.applikeysolutions.cosmocalendar.model.Day;

import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO[3]: standardize field names and widgets ids
    private final String TAG = "KCTag-" + WorkoutActivity.class.getSimpleName();
    public static final String DATE_OF_EXERCISE_TAG = "DATE_OF_EXERCISE_TAG";

    // TODO[3]: not sure if this variable is needed yet
    private long mDateOfExercise;
    private GymLogViewModel mGymLogViewModel;
    private ExerciseAdapter mExerciseAdapter;

    private TextView mDateOfExerciseTextView;
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
        setupRecyclerView();

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);
        subscribeToModel(mGymLogViewModel);

        Log.i(TAG, "Date of exercise: " +mDateOfExercise);
    }

    private void grabDataFromIntent() {
        mDateOfExercise = getIntent().getLongExtra(DATE_OF_EXERCISE_TAG, 19901030);
    }

    private void setupViews() {
        mExerciseRecyclerView = findViewById(R.id.workout_rv_exercises);
        mAddExercisesOptionsLinearLayout = findViewById(R.id.workout_ll_new_exercises_options);
        mAddExerciseButton = findViewById(R.id.workout_bt_add_exercise);
        mAddExerciseButton.setOnClickListener(this);
        mDateOfExerciseTextView = findViewById(R.id.workout_tv_exerciseDate);
        mDateOfExerciseTextView.setText(DateConverterHelper.fromLongToString(mDateOfExercise));

        // additional buttons that show up only when there are, probably will be replaced by FAB
        mAddExercisesFromRoutineButton = findViewById(R.id.workout_bt_routine);
        mAddExercisesFromRoutineButton.setOnClickListener(this);
        mCopyPreviousWorkoutButton = findViewById(R.id.workout_bt_copy_previous);
        mCopyPreviousWorkoutButton.setOnClickListener(this);
        mAddExerciseAdditionalButton = findViewById(R.id.workout_bt_add_exercise_additional);
        mAddExerciseAdditionalButton.setOnClickListener(this);


    }

    private void setupRecyclerView() {

        mExerciseAdapter = new ExerciseAdapter(this);
        mExerciseRecyclerView.setAdapter(mExerciseAdapter);
        mExerciseRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        Log.i(TAG, "setting up RecyclerView");

    }

    private void subscribeToModel(GymLogViewModel model) {
        Log.i(TAG, "getExercisesWithSets method is being observed - subscribed to ViewModel");
        model.getExercisesWithSetsForDate(mDateOfExercise).observe(this, new Observer<List<ExerciseWithSets>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseWithSets> exercisesWithSets) {
                if (exercisesWithSets.isEmpty()) { displayNewWorkoutOptions(); }
                else { hideNewWorkoutOptions();
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

    private void copyExercisesFromPreviousDay() {
        new CalendarDialog(this, new OnDaysSelectionListener() {
            @Override
            public void onDaysSelected(List<Day> selectedDays) {

            }
        }).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case (R.id.workout_bt_copy_previous):
                copyExercisesFromPreviousDay();
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
}
