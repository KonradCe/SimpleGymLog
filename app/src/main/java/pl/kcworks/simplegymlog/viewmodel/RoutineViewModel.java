package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.kcworks.simplegymlog.model.DayOfRoutine;
import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.RoutineWithDays;

public class RoutineViewModel extends AndroidViewModel {

    private GymLogRepository repository;
    private LiveData<List<RoutineWithDays>> routineWithDaysList;

    public RoutineViewModel(@NonNull Application application) {
        super(application);
        repository = GymLogRepository.getInstance(application);
        routineWithDaysList = repository.getAllRoutinesWithDays();
    }

    public LiveData<List<RoutineWithDays>> getRoutineWithDaysList() {
        return routineWithDaysList;
    }

    public void insertRoutine(RoutineWithDays routineWithDays) {
        repository.insertRoutineWithDays(routineWithDays);
    }

    public void updateRoutine(Routine routine) {
        repository.updateRoutine(routine);
    }

    public void updateDayOfRoutine(DayOfRoutine dayOfRoutine) {
        repository.updateDayOfRoutine(dayOfRoutine);
    }

}
