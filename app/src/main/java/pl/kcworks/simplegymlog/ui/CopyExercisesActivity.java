package pl.kcworks.simplegymlog.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.button.MaterialButton;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.DateConverterHelper;
import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.viewmodel.GymLogViewModel;

public class CopyExercisesActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String IDS_OF_EXERCISES_TO_COPY_TAG = "IDS_OF_EXERCISES_TO_COPY_TAG";
    private final String TAG = "KCTag-" + CopyExercisesActivity.class.getSimpleName();
    private TextView exercisesInSelectedDayTextView;
    private List<ExerciseWithSets> allExercisesWithSets;
    private LongSparseArray<ArrayList<ExerciseWithSets>> exerciseWithListSparseArray = new LongSparseArray<>();
    private GymLogViewModel gymLogViewModel;
    private List<ExerciseWithSets> selectedExercisesWithSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_exercises);

        gymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);
        grabDataFromDb();
        setUpViews();
    }

    private void setUpViews() {
        exercisesInSelectedDayTextView = findViewById(R.id.copyExerciseActivity_tv_exercisesInSelectedDayInfo);
        MaterialButton copySelectedDayButton = findViewById(R.id.bt_copy_selected_day);
        copySelectedDayButton.setOnClickListener(this);

    }

    private void grabDataFromDb() {
        gymLogViewModel.getAllExercisesWithSets().observe(this, new Observer<List<ExerciseWithSets>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseWithSets> exerciseWithSets) {
                allExercisesWithSets = exerciseWithSets;
                setUpCalendar();
            }
        });
    }

    private void setUpCalendar() {
        MaterialCalendarView mCalendarView = findViewById(R.id.calendar);
        List<CalendarDay> calendarDayList = processListOfExercisesFromDb(allExercisesWithSets);
        mCalendarView.addDecorator(new MainActivity.DaysWithExerciseDecorator(calendarDayList, getResources().getColor(R.color.colorPrimary)));

        mCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                long selectedDayGymLogDate = date.getYear() * 10000 + date.getMonth() * 100 + date.getDay();
                presentExerciseInfoForDay(selectedDayGymLogDate);
            }
        });


    }

    // TODO[3]: "this method does 2 things..." - it should do only one
    // this method does 2 things - first it creates a List<CalendarDay> of days with exercises for the purpose of feeding it to the setDecoratorsToDaysWithExercises(...) method,
    // then it creates a LongSparseArray of ArrayList of ExerciseWithSets. We later use data from that list to present info about exercises in selected day.
    private List<CalendarDay> processListOfExercisesFromDb(List<ExerciseWithSets> allExercises) {
        List<CalendarDay> calendarDayList = new ArrayList<>();
        for (ExerciseWithSets exerciseWithSets : allExercises) {
            Exercise exercise = exerciseWithSets.getExercise();
            int[] calendarDayNumbers = DateConverterHelper.gymLogDateFormatToYearMonthDayInt(exercise.getExerciseDate());
            calendarDayList.add(CalendarDay.from(calendarDayNumbers[0], calendarDayNumbers[1], calendarDayNumbers[2]));
            if (exerciseWithListSparseArray.get(exercise.getExerciseDate()) == null) {
                ArrayList<ExerciseWithSets> exerciseWithSetsArrayList = new ArrayList<>();
                exerciseWithSetsArrayList.add(exerciseWithSets);
                exerciseWithListSparseArray.put(exercise.getExerciseDate(), exerciseWithSetsArrayList);
            } else {
                exerciseWithListSparseArray.get(exercise.getExerciseDate()).add(exerciseWithSets);
            }
        }

        return calendarDayList;
    }

    private void presentExerciseInfoForDay(long dateInGymLogFormat) {
        selectedExercisesWithSets = exerciseWithListSparseArray.get(dateInGymLogFormat);
        if (selectedExercisesWithSets == null) {
            exercisesInSelectedDayTextView.setText(getString(R.string.copy_exercise_no_exercises_selected));
        } else {
            exercisesInSelectedDayTextView.setText(createDayInfoFromExerciseWithSetList(selectedExercisesWithSets));
        }
    }

    private String createDayInfoFromExerciseWithSetList(List<ExerciseWithSets> exerciseWithSets) {
        String exercisesInDayInfo;
        if (exerciseWithSets.size() == 1) {
            exercisesInDayInfo = getString(R.string.copy_exercise_day_info_11) + exerciseWithSets.size() + getString(R.string.copy_exercise_day_info_12);
        }
        else {
            exercisesInDayInfo = getString(R.string.copy_exercise_day_info_21) + exerciseWithSets.size() + getString(R.string.copy_exercise_day_info_22);
        }

        for (ExerciseWithSets e : exerciseWithSets) {
            exercisesInDayInfo += e.getExercise().getExerciseName() + " - ";
            if (e.getExerciseSetList().size() == 1) {
                exercisesInDayInfo += "" + e.getExerciseSetList().size() + " " + getString(R.string.set);
            } else {
                exercisesInDayInfo += "" + e.getExerciseSetList().size() + " " + getString(R.string.sets);
            }
            exercisesInDayInfo += "\n";
        }

        return exercisesInDayInfo;
    }

    private void returnExerciseIdsFromSelectedDay() {
        Intent dataIntent = new Intent();
        if (selectedExercisesWithSets != null) {
            int[] idsOfExercisesToCopy = new int[selectedExercisesWithSets.size()];
            for (int i = 0; i < selectedExercisesWithSets.size(); i++) {
                idsOfExercisesToCopy[i] = selectedExercisesWithSets.get(i).getExercise().getExerciseId();
            }
            dataIntent.putExtra(IDS_OF_EXERCISES_TO_COPY_TAG, idsOfExercisesToCopy);
            setResult(RESULT_OK, dataIntent);
            finish();
        } else {
            Toast.makeText(this, getString(R.string.copy_exercise_no_exercises_selected), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_copy_selected_day) {
            returnExerciseIdsFromSelectedDay();
        }
    }
}
