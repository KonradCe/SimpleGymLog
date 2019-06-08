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

import java.util.List;

import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.Workout;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO[3]: standardize field names and widgets ids
    private final String TAG = "KCTag-" + WorkoutActivity.class.getSimpleName();

    private Button mAddExerciseButton;
    private GymLogViewModel mGymLogViewModel;
    private ExerciseAdapter mExerciseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        setupViews();
        setupRecyclerView();

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);
        subscribeToModel(mGymLogViewModel);
    }

    private void subscribeToModel(GymLogViewModel model) {
        Log.i(TAG, "getExercisesWithSets method is being observed - subscribed to ViewModel");
        model.getmExercisesWithSets().observe(this, new Observer<List<ExerciseWithSets>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseWithSets> exercisesWithSets) {
                Log.i(TAG, "change in data observed");
                if (mExerciseAdapter == null) {
                    Log.i(TAG, "mExerciseAdapter is null - nothing to present");
                }
                mExerciseAdapter.setExercises(exercisesWithSets);
                mExerciseAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.workout_rv_exercises);
        mExerciseAdapter = new ExerciseAdapter(this);
        rv.setAdapter(mExerciseAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
        Log.i(TAG, "setting up RecyclerView");

    }

    private void setupViews() {
        mAddExerciseButton = findViewById(R.id.workout_bt_add_exercise);
        mAddExerciseButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO[1]: this should be startActivityForResult
        Intent intent = new Intent(this, AddExerciseActivity.class);
        startActivity(intent);
    }
}
