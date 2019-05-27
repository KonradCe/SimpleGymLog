package pl.kcworks.simplegymlog;

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

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    // TODO[3]: standardize field names and widgets ids

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
        mGymLogViewModel.getmAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> exercises) {
                if (mExerciseAdapter == null) {
                    Log.i("dziab dziab", "mExerciseAdapter jest rowny null");
                }
                mExerciseAdapter.setExercises(exercises);
                mExerciseAdapter.notifyDataSetChanged();
            }
        });
        Log.i("dziab", "ilosc w adapterze: " + mExerciseAdapter.getItemCount());



    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.workout_rv_exercises);
        mExerciseAdapter = new ExerciseAdapter(this);
        rv.setAdapter(mExerciseAdapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViews() {
        mAddExerciseButton = findViewById(R.id.workout_bt_add_exercise);
        mAddExerciseButton.setOnClickListener(this);


    }

    @Override
    public void onClick(View view) {
        // TODO[2]: this should be start intent for activity
        Intent intent = new Intent(this, AddExerciseActivity.class);
        startActivity(intent);
    }
}
