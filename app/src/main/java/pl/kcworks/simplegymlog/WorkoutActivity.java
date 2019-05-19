package pl.kcworks.simplegymlog;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

public class WorkoutActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mAddExerciseButton;
    private FrameLayout mFragmentContainerFrameLayout;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        setupViews();
        setupRecyclerView();

    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.workout_rv_exercises);
        ExerciseListAdapter adapter = new ExerciseListAdapter(this);
        rv.setAdapter(adapter);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupViews() {
        mAddExerciseButton = findViewById(R.id.workout_bt_add_exercise);
        mAddExerciseButton.setOnClickListener(this);
        mFragmentContainerFrameLayout = findViewById(R.id.workout_fl_fragment_container);

    }

    @Override
    public void onClick(View view) {

        mFragmentContainerFrameLayout.setVisibility(View.VISIBLE);

        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.workout_fl_fragment_container, new AddExerciseFragment())
                    .commit();
        }
        else {
            fragmentManager.beginTransaction()
                    .replace(R.id.workout_fl_fragment_container, new AddExerciseFragment())
                    .commit();
        }


    }
}
