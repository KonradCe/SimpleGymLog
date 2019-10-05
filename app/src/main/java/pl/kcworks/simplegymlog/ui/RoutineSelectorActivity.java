package pl.kcworks.simplegymlog.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Collections;
import java.util.List;

import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.DayOfRoutine;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.GymLogListItem;
import pl.kcworks.simplegymlog.model.GymLogType;
import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.RoutineWithDays;
import pl.kcworks.simplegymlog.model.SingleSet;
import pl.kcworks.simplegymlog.model.db.DataTypeConverter;
import pl.kcworks.simplegymlog.viewmodel.RoutineSelectorViewModel;

public class RoutineSelectorActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "KCtag-" + RoutineSelectorActivity.class.getSimpleName();

    public static final String SELECTOR_ACTIVITY_MODE = "SELECTOR_ACTIVITY_MODE";
    public static final String DAY_OUF_ROUTINE_STRING_EXTRA = "DAY_OUF_ROUTINE_STRING_EXTRA";
    public static final int SELECT_ROUTINE_TO_EDIT = 1601;
    public static final int SELECT_DAY = 1602;

    private RoutineSelectorViewModel routineSelectorViewModel;
    private int selectorActivityMode;
    private RoutineAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine_selector);

        grabDataFromIntent(getIntent());
        setUpViewModel();
        setUpViews();
        setUpRecyclerView();
    }

    private void grabDataFromIntent(Intent intent) {
        selectorActivityMode = intent.getIntExtra(SELECTOR_ACTIVITY_MODE, 0);
    }

    private void setUpViewModel() {
        routineSelectorViewModel = ViewModelProviders.of(this).get(RoutineSelectorViewModel.class);
    }

    private void setUpViews() {
        Button addRoutineButton = findViewById(R.id.bt_add_routine);

        switch (selectorActivityMode) {
            case SELECT_ROUTINE_TO_EDIT:
                addRoutineButton.setOnClickListener(this);
                break;
            case SELECT_DAY:
                addRoutineButton.setVisibility(View.GONE);
                break;
        }
    }

    private void setUpRecyclerView() {
        RecyclerView routineRecyclerView = findViewById(R.id.rv_routine_list);
        adapter = new RoutineAdapter(RoutineAdapter.AdapterMode.SELECT_ROUTINE);
        routineRecyclerView.setAdapter(adapter);
        routineRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        routineSelectorViewModel.getRoutineList().observe(this, new Observer<List<Routine>>() {
            @Override
            public void onChanged(List<Routine> routineList) {
                adapter.setRoutineList(routineList);
            }
        });

        RoutineAdapter.RoutineAdapterClickListener listener = null;
        if (selectorActivityMode == SELECT_ROUTINE_TO_EDIT) {
            listener = new RoutineAdapter.RoutineAdapterClickListener() {
                @Override
                public void onItemClicked(GymLogListItem clickedView) {
                    Routine routine = (Routine) clickedView;
                    startEditRoutineActivity(routine);
                }
            };
        }
        else if (selectorActivityMode == SELECT_DAY) {
            listener = new RoutineAdapter.RoutineAdapterClickListener() {
                @Override
                public void onItemClicked(GymLogListItem clickedView) {
                    switch (clickedView.getType()) {
                        case ROUTINE:
                            Routine routine = (Routine) clickedView;
                            displaySingleRoutineWithDays(routine.getRoutineId());
                            break;
                        case DAY:
                            selectDayOfRoutine((DayOfRoutine) clickedView);
                            break;
                        case EXERCISE:
                            Exercise exercise = (Exercise) clickedView;
                            Log.i(TAG, exercise.toString());
                            break;
                        case SET:
                            SingleSet set = (SingleSet) clickedView;
                            Log.i(TAG, set.toString());
                            break;
                    }
                }
            };
        }
        adapter.setListener(listener);
    }

    private void displaySingleRoutineWithDays(int routineId) {
        if (routineSelectorViewModel.getRoutineList().hasActiveObservers()) {
            Log.i(TAG, "routineSelectorViewModel.getRoutineList() has active observers");
            routineSelectorViewModel.getRoutineList().removeObservers(RoutineSelectorActivity.this);
        }
        routineSelectorViewModel.getRoutineWithDaysById(routineId).observe(this, new Observer<RoutineWithDays>() {
            @Override
            public void onChanged(RoutineWithDays routineWithDays) {
                adapter.setRoutineWithDaysList(Collections.singletonList(routineWithDays));
            }
        });
    }

    private void startEditRoutineActivity(Routine routine) {
        Intent intent = new Intent(this, RoutineEditorActivity.class);
        intent.putExtra(RoutineEditorActivity.ID_OF_ROUTINE_TO_EDIT_EXTRA, routine.getRoutineId());
        startActivity(intent);
    }

    private void addNewRoutine() {
        final RoutineWithDays routineWithDays = RoutineWithDays.createEmpty();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final EditText editText = new EditText(this);
        editText.setHint("name of the new routine");
        editText.setSelectAllOnFocus(true);

        builder.setTitle("Routine name")
                .setView(editText)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        routineWithDays.getRoutine().setRoutineName(editText.getText().toString());
                        routineSelectorViewModel.insertRoutine(routineWithDays);
                    }
                })
                .show();
    }

    private void selectDayOfRoutine(DayOfRoutine dayOfRoutine) {
        Intent data = new Intent();
        String dayOfRoutineJson = DataTypeConverter.dayOfRoutineToString(dayOfRoutine);
        data.putExtra(DAY_OUF_ROUTINE_STRING_EXTRA, dayOfRoutineJson);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_add_routine) {
            addNewRoutine();
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        GymLogListItem listItem = adapter.getGymLogItems().get(item.getGroupId());
        if (listItem.getType() == GymLogType.ROUTINE) {
            routineSelectorViewModel.deleteSingleRoutine((Routine) listItem);
        }
        return true;
    }
}
