package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.util.List;

import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.Routine;

public class RoutineViewModel extends AndroidViewModel {

    private GymLogRepository repository;
    private final MediatorLiveData<List<Routine>> observableRoutineList;

    public RoutineViewModel(@NonNull Application application) {
        super(application);

        observableRoutineList = new MediatorLiveData<>();
        observableRoutineList.setValue(null);

        repository = GymLogRepository.getInstance(application);
        LiveData<List<Routine>> routineList = repository.getAllRoutines();

        observableRoutineList.addSource(routineList, new Observer<List<Routine>>() {
            @Override
            public void onChanged(List<Routine> routines) {
                observableRoutineList.setValue(routines);
            }
        });

    }

    public MediatorLiveData<List<Routine>> getRoutineList() {
        return observableRoutineList;
    }

    public void insertRoutine(Routine routine) {
        repository.insertRoutine(routine);
    }
}
