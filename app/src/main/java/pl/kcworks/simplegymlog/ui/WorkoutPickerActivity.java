package pl.kcworks.simplegymlog.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import pl.kcworks.simplegymlog.R;

// TODO[3]: replace with some pop up window or dialog or smth - no need for this to be an entire activity

public class WorkoutPickerActivity extends AppCompatActivity {
    private Button mContinueRoutineButton;
    private Button mCopyPreviousWorkoutButton;
    private Button mStartNewWorkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_picker);

        setUpViews();
    }

    private void setUpViews() {
        mContinueRoutineButton = findViewById(R.id.workout_picker_bt_routine);
        mCopyPreviousWorkoutButton = findViewById(R.id.workout_picker_bt_copy_previous);
        mStartNewWorkoutButton = findViewById(R.id.workout_picker_bt_new_workout);

        mStartNewWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startWorkoutActivityIntent = new Intent(WorkoutPickerActivity.this, WorkoutActivity.class);
                startActivity(startWorkoutActivityIntent);
            }
        });
    }
}
