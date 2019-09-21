package pl.kcworks.simplegymlog.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
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
import pl.kcworks.simplegymlog.model.db.DataTypeConverter;
import pl.kcworks.simplegymlog.viewmodel.RoutineEditorViewModel;

public class RoutineEditorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "KCTag-" + RoutineEditorActivity.class.getSimpleName();
    private static final int NEW_EXERCISE_REQUEST_CODE = 1500;
    private static final int UPDATE_EXERCISE_REQUEST_CODE = 1501;
    static final String ID_OF_ROUTINE_TO_EDIT_EXTRA = "ID_OF_ROUTINE_TO_EDIT_EXTRA";

    private RecyclerView routineRecyclerView;
    private RoutineEditorViewModel routineEditorViewModel;
    private int idOfRoutineBeingEdited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_editor);

        idOfRoutineBeingEdited = getIntent().getIntExtra(ID_OF_ROUTINE_TO_EDIT_EXTRA, -1);
        setUpViewModel();
        setUpViews();
        setUpRecyclerView();
    }

    private void setUpViewModel() {
        routineEditorViewModel = ViewModelProviders.of(this).get(RoutineEditorViewModel.class);
    }

    private void setUpViews() {
        Button addRoutineButton = findViewById(R.id.bt_add_day);
        addRoutineButton.setOnClickListener(this);
        routineRecyclerView = findViewById(R.id.rv_routine_list);
    }

    private void setUpRecyclerView() {
        final RoutineAdapter adapter = new RoutineAdapter(RoutineAdapter.AdapterMode.EDIT_ROUTINE);
        routineRecyclerView.setAdapter(adapter);
        routineRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        routineEditorViewModel.getRoutineWithDaysById(idOfRoutineBeingEdited).observe(this, new Observer<RoutineWithDays>() {
                    @Override
                    public void onChanged(RoutineWithDays routineWithDays) {
                        adapter.setRoutineWithDaysList(Collections.singletonList(routineWithDays));
                        routineEditorViewModel.setCachedRoutineWithDays(routineWithDays);
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
                                editDayNameDialog((DayOfRoutine) clickedView);
                                break;
                            case EXERCISE:
                                editExercise((Exercise) clickedView);
                                break;
                            case RV_ADD_EXERCISE_BT:
                                createNewExercise(((RoutineAdapter.RvExtras) clickedView).getParent());
                                break;
                        }
                    }
                };
        adapter.setListener(listener);
    }

    private void editExercise(Exercise exercise) {
        List<DayOfRoutine> dayOfRoutineList = routineEditorViewModel.getCachedRoutineWithDays().getDayOfRoutineList();
        DayOfRoutine dayOfRoutine = null;
        for (DayOfRoutine day : dayOfRoutineList) {
            if (day.getDayId() == exercise.getExerciseId()) {
                dayOfRoutine = day;
                break;
            }
        }

        Intent intent = new Intent(this, AddExerciseActivity.class);
        intent.putExtra(AddExerciseActivity.ROUTINE_EDIT_EXERCISE, DataTypeConverter.dayOfRoutineToString(dayOfRoutine));
        intent.putExtra(AddExerciseActivity.ROUTINE_EDIT_EXERCISE_ORDER, exercise.getExerciseOrderInDay());

        startActivityForResult(intent, UPDATE_EXERCISE_REQUEST_CODE);
    }

    private void addNewDayToRoutine() {
        List<ExerciseWithSets> list = new ArrayList<>();
        final DayOfRoutine dayOfRoutine = new DayOfRoutine("", list);
        dayOfRoutine.setParentRoutineId(idOfRoutineBeingEdited);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setHint("name of the day");
        editText.setSelectAllOnFocus(true);

        builder.setTitle("Day name")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dayOfRoutine.setDayName(editText.getText().toString());
                        routineEditorViewModel.insertDayOfRoutine(dayOfRoutine);
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

    private void createNewExercise(GymLogListItem parent) {
        DayOfRoutine dayOfRoutine = (DayOfRoutine) parent;

        Intent intent = new Intent(this, AddExerciseActivity.class);
        intent.putExtra(AddExerciseActivity.ROUTINE_ADD_EXERCISE, DataTypeConverter.dayOfRoutineToString(dayOfRoutine));

        startActivityForResult(intent, NEW_EXERCISE_REQUEST_CODE);
    }

    private void onEditRoutineClick(final Routine routine) {
        // TODO[3]: this dialog builder is almost the same as in editDayNameDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setText(routine.getRoutineName());
        editText.setSelectAllOnFocus(true);

        builder.setTitle("Routine name")
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        routine.setRoutineName(editText.getText().toString());
                        routineEditorViewModel.updateRoutine(routine);
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

    private void editDayNameDialog(final DayOfRoutine dayOfRoutine) {
        // TODO[3]: this dialog builder is almost the same as in onEditRoutineClick
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setText(dayOfRoutine.getDayName());
        editText.setSelectAllOnFocus(true);

        builder.setTitle("Day name")
                .setView(editText)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dayOfRoutine.setDayName(editText.getText().toString());
                        routineEditorViewModel.updateDayOfRoutine(dayOfRoutine);
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
        singleSetList.add(new SingleSet(1,  100));
        singleSetList.add(new SingleSet(2,  200));
        singleSetList.add(new SingleSet(3,  300));
        singleSetList.add(new SingleSet(4,  400));
        singleSetList.add(new SingleSet(5,  500));


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


        routineEditorViewModel.insertRoutine(routineWithDays);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_EXERCISE_REQUEST_CODE || requestCode == UPDATE_EXERCISE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String dayOfRoutineJson = data.getStringExtra(AddExerciseActivity.ROUTINE_RESULT);
                DayOfRoutine dayOfRoutine = DataTypeConverter.stringToDayOfRoutine(dayOfRoutineJson);
                routineEditorViewModel.updateDayOfRoutine(dayOfRoutine);
            }
            else {
                Toast.makeText(this, "no changes were made", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_add_day) {
            addNewDayToRoutine();
        }
    }

}
