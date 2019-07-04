package pl.kcworks.simplegymlog.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.applikeysolutions.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.applikeysolutions.cosmocalendar.view.CalendarView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;

// TODO[3]: replace with some pop up window or dialog or smth - no need for this to be an entire activity

public class WorkoutPickerActivity extends AppCompatActivity {
    private static final String TAG = "KCtag-" + AddExerciseActivity.class.getSimpleName();
    public static final String DATE_OF_EXERCISE_TAG = "DATE_OF_EXERCISE_TAG";
    private Button mContinueRoutineButton;
    private Button mCopyPreviousWorkoutButton;
    private Button mStartNewWorkoutButton;
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_picker);
        setUpViews();
        calendarSetUp();
    }

    private void setUpViews() {
        mContinueRoutineButton = findViewById(R.id.workout_picker_bt_routine);
        mCopyPreviousWorkoutButton = findViewById(R.id.workout_picker_bt_copy_previous);
        mStartNewWorkoutButton = findViewById(R.id.workout_picker_bt_new_workout);
        mCalendarView = findViewById(R.id.workout_picker_calendar);


        mStartNewWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startWorkoutActivityIntent = new Intent(WorkoutPickerActivity.this, WorkoutActivity.class);
                startWorkoutActivityIntent.putExtra(DATE_OF_EXERCISE_TAG, getSelectedDay());
                startActivity(startWorkoutActivityIntent);
            }
        });
    }

    private void calendarSetUp() {
        // this will be useul when will try to implement 'event' days - days with workouts
/*        Calendar calendar = Calendar.getInstance();

        // connected (event) days styling
        mCalendarView.setConnectedDayIconPosition(ConnectedDayIconPosition.BOTTOM);
        mCalendarView.setConnectedDayIconRes(R.drawable.calendar_circle);
        mCalendarView.setConnectedDaySelectedIconRes(R.drawable.calendar_circle);*/
    }

    private long getSelectedDay() {
        Date dateOfExercise;
        try {
            dateOfExercise = mCalendarView.getSelectedDays().get(0).getCalendar().getTime();
        }
        catch (IndexOutOfBoundsException e) {
            Log.i(TAG, "No date was selected so today's date was passed as default");
            dateOfExercise = Calendar.getInstance().getTime();
        }
        return DateConverterHelper.dateToLong(dateOfExercise);
    }
}
