package pl.kcworks.simplegymlog.ui;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "KCtag-" + MainActivity.class.getSimpleName();

    private MaterialCalendarView mCalendarView;

    private GymLogViewModel gymLogViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidThreeTen.init(this);
        setContentView(R.layout.activity_main);

        setUpViews();
        // TODO[3]: make sure setting up the calendar doesn't take too long - we don't want a laggy startup;
        calendarSetUp();
    }

    private void setUpViews() {
        Button startNewWorkoutButton = findViewById(R.id.mainActivity_bt_start_workout);
        startNewWorkoutButton.setOnClickListener(this);

        Button customizeRoutineButton = findViewById(R.id.mainActivity_bt_customize_routine);
        customizeRoutineButton.setOnClickListener(this);

        mCalendarView = findViewById(R.id.calendar);
    }

    private long getSelectedDay() {
        return DateConverterHelper.dateToLong(mCalendarView.getSelectedDate().getDate());
    }

    private void calendarSetUp() {

        mCalendarView.setSelectedDate(LocalDate.now());

        gymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);
        // TODO[2]: get only exercises for the current month so setting up the calendar won't take too long
        // TODO[3]: do not fetch every exercise after every update - get only those that were added
        // TODO[2]: when exercises were deleted from a day update it so it will not look like it still has exercises
        gymLogViewModel.getAllExercises().observe(this, new Observer<List<Exercise>>() {
            @Override
            public void onChanged(@Nullable List<Exercise> listOfAllExercises) {
                setDecoratorsToDaysWithExercises(listOfAllExercises);
            }
        });

    }

    private void setDecoratorsToDaysWithExercises(List<Exercise> exerciseList) {
        mCalendarView.removeDecorators();
        List<CalendarDay> listOfDaysWithExercises = new ArrayList<>();
        for (Exercise exercise : exerciseList) {
            int[] calendarDayNumbers = DateConverterHelper.gymLogDateFormatToYearMonthDayInt(exercise.getExerciseDate());
            listOfDaysWithExercises.add(CalendarDay.from(calendarDayNumbers[0], calendarDayNumbers[1], calendarDayNumbers[2]));
        }

        mCalendarView.addDecorator(new DaysWithExerciseDecorator(listOfDaysWithExercises, getResources().getColor(R.color.colorPrimary)));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.mainActivity_bt_start_workout):
                Intent startWorkoutIntent = new Intent(this, WorkoutActivity.class);
                startWorkoutIntent.putExtra(WorkoutActivity.DATE_OF_EXERCISE_TAG, getSelectedDay());
                startActivity(startWorkoutIntent);
                break;

            case (R.id.mainActivity_bt_customize_routine):
                Intent intent = new Intent(this, RoutineSelectorActivity.class);
                intent.putExtra(RoutineSelectorActivity.SELECTOR_ACTIVITY_MODE, RoutineSelectorActivity.SELECT_ROUTINE_TO_EDIT);
                startActivity(intent);
                break;
        }
    }

    static class DaysWithExerciseDecorator implements DayViewDecorator {

        private final List<CalendarDay> dates;
        private final int color;

        DaysWithExerciseDecorator(Collection<CalendarDay> dates, int color) {
            this.dates = new ArrayList<>(dates);
            this.color = color;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            // this adds dot below date
//            view.addSpan(new DotSpan(5, color));
            view.addSpan(new ForegroundColorSpan(color));
        }
    }

}
