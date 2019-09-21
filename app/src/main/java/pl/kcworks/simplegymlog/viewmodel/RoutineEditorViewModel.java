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

public class RoutineEditorViewModel extends AndroidViewModel {

    private GymLogRepository repository;
    private RoutineWithDays cachedRoutineWithDays;

    public RoutineEditorViewModel(@NonNull Application application) {
        super(application);
        repository = GymLogRepository.getInstance(application);
    }

    public RoutineWithDays getCachedRoutineWithDays() {
        return cachedRoutineWithDays;
    }

    public void setCachedRoutineWithDays(RoutineWithDays cachedRoutineWithDays) {
        this.cachedRoutineWithDays = cachedRoutineWithDays;
    }

    public LiveData<RoutineWithDays> getRoutineWithDaysById(int routineId) {
        return repository.getRoutinesWithDaysById(routineId);
    }

    public void insertRoutine(RoutineWithDays routineWithDays) {
        repository.insertRoutineWithDays(routineWithDays);
    }

    public void insertDayOfRoutine(DayOfRoutine dayOfRoutine) {
        repository.insertDayOfRoutine(dayOfRoutine);
    }

    public void updateRoutine(Routine routine) {
        repository.updateRoutine(routine);
    }

    public void updateDayOfRoutine(DayOfRoutine dayOfRoutine) {
        repository.updateDayOfRoutine(dayOfRoutine);
    }

}
