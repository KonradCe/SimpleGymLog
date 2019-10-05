package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.RoutineWithDays;

public class RoutineSelectorViewModel extends AndroidViewModel {

    private GymLogRepository repository;

    public RoutineSelectorViewModel(@NonNull Application application) {
        super(application);
        repository = GymLogRepository.getInstance(application);
    }

    public LiveData<List<Routine>> getRoutineList() {
        return repository.getRoutines();
    }

    public LiveData<RoutineWithDays> getRoutineWithDaysById(int routineId) {
        return repository.getRoutinesWithDaysById(routineId);
    }

    public void insertRoutine(RoutineWithDays routineWithDays) {
        repository.insertRoutineWithDays(routineWithDays);
    }

    public void deleteSingleRoutine(Routine routine) {
        repository.deleteRoutine(routine);
    }


}
