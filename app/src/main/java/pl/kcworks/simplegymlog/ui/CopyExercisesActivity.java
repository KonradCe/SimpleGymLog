package pl.kcworks.simplegymlog.ui;

import android.app.Activity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.util.LongSparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

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

public class CopyExercisesActivity extends AppCompatActivity {

    public static final String IDS_OF_EXERCISES_TO_COPY_TAG = "IDS_OF_EXERCISES_TO_COPY_TAG";
    private final String TAG = "KCTag-" + CopyExercisesActivity.class.getSimpleName();
    private MaterialCalendarView mCalendarView;
    private TextView mExercisesInSelectedDayTextView;
    private List<ExerciseWithSets> mAllExercisesWithSets;
    private LongSparseArray<ArrayList<ExerciseWithSets>> exerciseWithListSparseArray = new LongSparseArray<>();
    private GymLogViewModel mGymLogViewModel;
    private List<ExerciseWithSets> mSelectedExercisesWithSets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_copy_exercises);

        mGymLogViewModel = ViewModelProviders.of(this).get(GymLogViewModel.class);
        grabDataFromDb();

        setUpViews();

    }

    private void setUpViews() {
        mCalendarView = findViewById(R.id.copyExerciseActivity_picker_calendar);
        mExercisesInSelectedDayTextView = findViewById(R.id.copyExerciseActivity_tv_exercisesInSelectedDayInfo);
    }

    private void grabDataFromDb() {
        mGymLogViewModel.getAllExercisesWithSets().observe(this, new Observer<List<ExerciseWithSets>>() {
            @Override
            public void onChanged(@Nullable List<ExerciseWithSets> exerciseWithSets) {
                mAllExercisesWithSets = exerciseWithSets;
                setUpCalendar();
            }
        });
    }

    private void setUpCalendar() {
        List<CalendarDay> calendarDayList = processListOfExercisesFromDb(mAllExercisesWithSets);
        setDecoratorsToDaysWithExercises(calendarDayList);

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

    private void setDecoratorsToDaysWithExercises(List<CalendarDay> listOfDaysWithExercises) {
        mCalendarView.addDecorator(new MainActivity.DaysWithExerciseDecorator(listOfDaysWithExercises, getResources().getColor(R.color.calendarDayWithExerciseColor)));
    }

    private void presentExerciseInfoForDay(long dateInGymLogFormat) {
        mSelectedExercisesWithSets = exerciseWithListSparseArray.get(dateInGymLogFormat);
        if (mSelectedExercisesWithSets == null) {
            mExercisesInSelectedDayTextView.setText("No exercises for selected day.");
        } else {
            mExercisesInSelectedDayTextView.setText(createDayInfoFromExerciseWithSetList(mSelectedExercisesWithSets));
        }
    }

    private String createDayInfoFromExerciseWithSetList(List<ExerciseWithSets> exerciseWithSets) {
        String exercisesInDayInfo;
        if (exerciseWithSets.size() == 1) {
            exercisesInDayInfo = "There is " + exerciseWithSets.size() + " exercise in selected day: \n";
        }
        else {
            exercisesInDayInfo = "There are " + exerciseWithSets.size() + " exercises in selected day: \n";
        }

        for (ExerciseWithSets e : exerciseWithSets) {
            exercisesInDayInfo += e.getExercise().getExerciseName() + " - ";
            if (e.getExerciseSetList().size() == 1) {
                exercisesInDayInfo += "" + e.getExerciseSetList().size() + " set";
            } else {
                exercisesInDayInfo += "" + e.getExerciseSetList().size() + " sets";
            }
            exercisesInDayInfo += "\n";
        }

        return exercisesInDayInfo;
    }

    private void returnExerciseIdsFromSelectedDay() {
        Intent dataIntent = new Intent();
        if (mSelectedExercisesWithSets != null) {
            int[] idsOfExercisesToCopy = new int[mSelectedExercisesWithSets.size()];
            for (int i = 0; i < mSelectedExercisesWithSets.size(); i++) {
                idsOfExercisesToCopy[i] = mSelectedExercisesWithSets.get(i).getExercise().getExerciseId();
            }
            dataIntent.putExtra(IDS_OF_EXERCISES_TO_COPY_TAG, idsOfExercisesToCopy);
            setResult(RESULT_OK, dataIntent);
            finish();
        } else {
            Toast.makeText(this, "no exercises in selected day", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.copy_activitiy_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.copy_menu_copyDay) {
            returnExerciseIdsFromSelectedDay();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

}
