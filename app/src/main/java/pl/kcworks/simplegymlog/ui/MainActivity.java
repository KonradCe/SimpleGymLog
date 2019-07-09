package pl.kcworks.simplegymlog.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "KCtag-" + MainActivity.class.getSimpleName();

    private Button mStartNewWorkoutButton;
    private CalendarView mCalendarView;

    private GymLogViewModel mGymLogViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpViews();
        // TODO[3]: make sure setting up the calendar doesn't take too long - we don't want a laggy startup;
        //  put in async task if needed?
        calendarSetUp();
    }

    private void setUpViews() {
        mStartNewWorkoutButton = findViewById(R.id.mainActivity_bt_start_workout);
        mStartNewWorkoutButton.setOnClickListener(this);

        mCalendarView = findViewById(R.id.mainActivity_picker_calendar);
    }

    private long getSelectedDay() {
        Date dateOfExercise;
        try {
            dateOfExercise = mCalendarView.getSelectedDates().get(0).getTime();
        }
        // in case no day is selected, today's date is passed as chosen by default
        catch (IndexOutOfBoundsException e) {
            dateOfExercise = Calendar.getInstance().getTime();
        }
        return DateConverterHelper.dateToLong(dateOfExercise);
    }

    private void calendarSetUp() {
        // Connected days = days specially marked in CalendarView either by icon show below / above date, or by different color
        // for more info on CosmoCalendar visit: https://github.com/AppliKeySolutions/CosmoCalendar

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);

        // due to onMonthChangeListener not working we're forced to load all exercises to mark in calendar (instead of loading exercises only for chosen month)
        mGymLogViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> listOfAllExercises) {
            Log.i(TAG, "observing getAllExercises method - to populate calendar with ConnectedDays");
                mCalendarView.addConnectedDays(createConnectedDaysFromListOfExercises(listOfAllExercises));
            }
        });

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.mainActivity_bt_start_workout):
                Intent startWorkoutIntent = new Intent(this, WorkoutActivity.class);
                startWorkoutIntent.putExtra(WorkoutActivity.DATE_OF_EXERCISE_TAG, getSelectedDay());
                startActivity(startWorkoutIntent);
                break;
        }
    }

}
