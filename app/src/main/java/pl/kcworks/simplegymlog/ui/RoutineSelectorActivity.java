package pl.kcworks.simplegymlog.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.DayOfRoutine;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.GymLogListItem;
import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.RoutineWithDays;
import pl.kcworks.simplegymlog.model.SingleSet;
import pl.kcworks.simplegymlog.viewmodel.RoutineViewModel;

public class RoutineSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "KCTag-" + ExerciseAdapter.class.getSimpleName();

    private Button addRoutineButton;
    private RecyclerView routineRecyclerView;
    private RoutineViewModel routineViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_selector);

        setUpViewModel();
        setUpViews();
        setUpRecyclerView();
    }

    private void setUpViewModel() {
        routineViewModel = ViewModelProviders.of(this).get(RoutineViewModel.class);
    }

    private void setUpViews() {
        addRoutineButton = findViewById(R.id.bt_add_routine);
        addRoutineButton.setOnClickListener(this);
        routineRecyclerView = findViewById(R.id.rv_routine_list);
    }

    private void setUpRecyclerView() {
        final RoutineAdapter adapter = new RoutineAdapter();
        routineRecyclerView.setAdapter(adapter);
        routineRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        routineViewModel.getRoutineWithDaysList().observe(this, new Observer<List<RoutineWithDays>>() {
            @Override
            public void onChanged(List<RoutineWithDays> routineWithDaysList) {
                adapter.setRoutineWithDaysList(routineWithDaysList);
            }
        });

        RoutineAdapter.RoutineAdapterClickListener listener = new RoutineAdapter.RoutineAdapterClickListener() {
            @Override
            public void onItemClicked(GymLogListItem clickedView) {
                switch (clickedView.getType()) {
                    case ROUTINE:
                        onEditRoutineClick((Routine) clickedView);
                        break;
                    case DAY:
                        onEditDayClick((DayOfRoutine) clickedView);
                        break;
                    case EXERCISE:
                        Log.i(TAG, "edit exercise button was clicked");
                        break;
                    case RV_ADD_DAY_BT:
                        Log.i(TAG, "add day button was clicked");
                        break;
                    case RV_ADD_EXERCISE_BT:
                        Log.i(TAG, "add exercise button was clicked");
                        break;
                }
            }
        };
        adapter.setListener(listener);
    }

    private void onEditRoutineClick(final Routine routine) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setText(routine.getRoutineName());
        editText.setSelectAllOnFocus(true);

        builder.setTitle("Edit routine name")
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        routine.setRoutineName(editText.getText().toString());
                        routineViewModel.updateRoutine(routine);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void onEditDayClick(final DayOfRoutine dayOfRoutine) {
        // TODO[3]: this dialog builder is almost the same as in onEditRoutineClick
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setText(dayOfRoutine.getDayName());
        editText.setSelectAllOnFocus(true);

        builder.setTitle("Edit day name")
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dayOfRoutine.setDayName(editText.getText().toString());
                        routineViewModel.updateDayOfRoutine(dayOfRoutine);
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .show();
    }

    private void insertDummyRoutineToDb() {
        List<SingleSet> singleSetList = new ArrayList<>();
        singleSetList.add(new SingleSet(1, null,  100));
        singleSetList.add(new SingleSet(2, null,  100));
        singleSetList.add(new SingleSet(3, null,  100));
        singleSetList.add(new SingleSet(4, null,  100));
        singleSetList.add(new SingleSet(5, null,  100));

        Exercise exercise = new Exercise("cwiczenie 1", 2);

        ExerciseWithSets exerciseWithSets = new ExerciseWithSets(exercise, singleSetList);

        List<ExerciseWithSets> exerciseWithSetsList = new ArrayList<>();
        exerciseWithSetsList.add(exerciseWithSets);
        exerciseWithSetsList.add(exerciseWithSets);


        List<DayOfRoutine> dayOfRoutineList = new ArrayList<>();
        dayOfRoutineList.add(new DayOfRoutine("dzien A", exerciseWithSetsList));
        dayOfRoutineList.add(new DayOfRoutine("dzien B", exerciseWithSetsList));

        Random r = new Random();
        int randomInt = r.nextInt(100);

        Routine routine = new Routine("routine " + randomInt);
        RoutineWithDays routineWithDays = new RoutineWithDays(routine, dayOfRoutineList);


        routineViewModel.insertRoutine(routineWithDays);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_add_routine) {
            insertDummyRoutineToDb();
        }
    }

}
