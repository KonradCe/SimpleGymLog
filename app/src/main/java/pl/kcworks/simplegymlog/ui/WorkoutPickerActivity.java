package pl.kcworks.simplegymlog.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.dialog.OnDaysSelectionListener;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.settings.lists.connected_days.ConnectedDays;
import com.applikeysolutions.cosmocalendar.view.CalendarView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;


import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

import static pl.kcworks.simplegymlog.ui.WorkoutActivity.DATE_OF_EXERCISE_TAG;

// TODO[3]: replace with some pop up window or dialog or smth - no need for this to be an entire activity

public class WorkoutPickerActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "KCtag-" + AddExerciseActivity.class.getSimpleName();

    private Button mContinueRoutineButton;
    private Button mCopyPreviousWorkoutButton;
    private Button mStartNewWorkoutButton;
    private CalendarView mCalendarView;

    private GymLogViewModel mGymLogViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_picker);
        setUpViews();
        calendarSetUp();
    }

    private void setUpViews() {
        mContinueRoutineButton = findViewById(R.id.workout_picker_bt_routine);
        mContinueRoutineButton.setOnClickListener(this);

        mCopyPreviousWorkoutButton = findViewById(R.id.workout_picker_bt_copy_previous);
        mCopyPreviousWorkoutButton.setOnClickListener(this);

        mStartNewWorkoutButton = findViewById(R.id.workout_picker_bt_new_workout);
        mStartNewWorkoutButton.setOnClickListener(this);

        mCalendarView = findViewById(R.id.workout_picker_calendar);
    }

    private void calendarSetUp() {
        Log.i(TAG, "calendarSetUp()");
        // Connected days = days specially marked in CalendarView either by icon show below / above date, or by different color
        // for more info on CosmoCalendar visit: https://github.com/AppliKeySolutions/CosmoCalendar

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);

        // due to onMonthChangeListener not working we're forced to load all exercises to mark in calendar (instead of loading exercises only for chosen month)
 /*       mGymLogViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {

            @Override
            public void onChanged(@Nullable List<Exercise> listOfAllExercises) {

                mCalendarView.addConnectedDays(createConnectedDaysFromListOfExercises(listOfAllExercises));
            }
        });*/
    }

    private long getSelectedDay() {
        Date dateOfExercise;
        try {
            dateOfExercise = mCalendarView.getSelectedDates().get(0).getTime();
        }
        // in case no day is selected, today's date is passed as chosen by default
        catch (IndexOutOfBoundsException e) {
            Log.i(TAG, "No date was selected so today's date was passed as default");
            dateOfExercise = Calendar.getInstance().getTime();
        }
        return DateConverterHelper.dateToLong(dateOfExercise);
    }

    private ConnectedDays createConnectedDaysFromListOfExercises(List<Exercise> listOfExercises) {
        Set<Long> daysWithWorkouts;
        List<Long> listOfGymLogDateFormatDates = new ArrayList<>();

        for (Exercise exercise : listOfExercises) {
            listOfGymLogDateFormatDates.add(exercise.getExerciseDate());
        }
        daysWithWorkouts = DateConverterHelper.convertListOfGymLogDateFormatToSetOfTimeInMillis(listOfGymLogDateFormatDates);

        return new ConnectedDays(daysWithWorkouts, getResources().getColor(R.color.testColor));
    }

    private void copyWorkout() {
        // select day to copy from
        selectDayToCopyExercisesFrom();
        // select day to copy into
        // chose which exercises to copy

    }

    private void selectDayToCopyExercisesFrom() {
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.workout_picker_bt_new_workout):
                Intent startWorkoutActivityIntent = new Intent(WorkoutPickerActivity.this, WorkoutActivity.class);
                startWorkoutActivityIntent.putExtra(DATE_OF_EXERCISE_TAG, getSelectedDay());
                startActivity(startWorkoutActivityIntent);
                break;

            case (R.id.workout_picker_bt_copy_previous):
                Log.i(TAG, "copy previous workout button was clicked");
                copyWorkout();
                break;
        }


    }

}
