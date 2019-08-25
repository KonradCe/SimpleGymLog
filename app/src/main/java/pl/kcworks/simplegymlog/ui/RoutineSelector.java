package pl.kcworks.simplegymlog.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.DayOfRoutine;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.SingleSet;
import pl.kcworks.simplegymlog.viewmodel.RoutineViewModel;

public class RoutineSelector extends AppCompatActivity implements View.OnClickListener {

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

        routineViewModel.getRoutineList().observe(this, new Observer<List<Routine>>() {
            @Override
            public void onChanged(List<Routine> routines) {
                if (routines != null) {
                    adapter.setRoutineList(routines);
                }
            }
        });
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


        DayOfRoutine dayOfRoutine = new DayOfRoutine("dzien A", exerciseWithSetsList);

        Random r = new Random();
        int randomInt = r.nextInt(100);

        Routine routine = new Routine("routine " + randomInt, Collections.singletonList(dayOfRoutine));

        routineViewModel.insertRoutine(routine);
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.bt_add_routine) {
            insertDummyRoutineToDb();
        }
    }
}
