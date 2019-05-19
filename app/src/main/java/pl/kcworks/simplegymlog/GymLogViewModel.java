package pl.kcworks.simplegymlog;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

public class GymLogViewModel extends AndroidViewModel {

    private GymLogRepository mGymLogRepository;
    private LiveData<List<Exercise>> mAllExercises;
    private LiveData<List<SingleSet>> mAllSingleSets;


    public GymLogViewModel(@NonNull Application application) {
        super(application);
        mGymLogRepository = new GymLogRepository(application);
        mAllExercises = mGymLogRepository.getAllExercises();
        mAllSingleSets = mGymLogRepository.getAllSingleSets();

    }

    public LiveData<List<Exercise>> getmAllExercises() {
        return mAllExercises;
    }

    public LiveData<List<SingleSet>> getmAllSingleSets() {
        return mAllSingleSets;
    }

    public void insertExercise(Exercise exercise) {
        mGymLogRepository.insertExercise(exercise);
    }
}
