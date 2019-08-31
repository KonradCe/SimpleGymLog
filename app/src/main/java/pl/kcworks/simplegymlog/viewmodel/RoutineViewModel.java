package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.Routine;

public class RoutineViewModel extends AndroidViewModel {

    private GymLogRepository repository;
    private LiveData<List<Routine>> routineList;

    public RoutineViewModel(@NonNull Application application) {
        super(application);
        repository = GymLogRepository.getInstance(application);
        routineList = repository.getAllRoutines();
    }

    public LiveData<List<Routine>> getRoutineList() {
        return routineList;
    }

    public void insertRoutine(Routine routine) {
        repository.insertRoutine(routine);
    }

    public void updateRoutine(Routine routine) {
        repository.updateRoutine(routine);
    }

}
