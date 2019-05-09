package pl.kcworks.simplegymlog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button mStartNewWorkoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStartNewWorkoutButton = findViewById(R.id.mainActivity_bt_start_workout);
        mStartNewWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startWorkoutActivityPickerIntent = new Intent(MainActivity.this, WorkoutPickerActivity.class);
                startActivity(startWorkoutActivityPickerIntent);
            }
        });
    }
}
