package pl.kcworks.simplegymlog;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.SingleSet;

public class GymLogViewModel extends AndroidViewModel {

    private GymLogRepository mGymLogRepository;
    private LiveData<List<Exercise>> mAllExercises;
    private LiveData<List<SingleSet>> mAllSingleSets;
    private LiveData<List<ExerciseWithSets>> mExercisesWithSets;

    // TODO[3]: should we fetch all data in ViewModel constructor?
    public GymLogViewModel(@NonNull Application application) {
        super(application);
        mGymLogRepository = new GymLogRepository(application);
        mAllExercises = mGymLogRepository.getAllExercises();
        mExercisesWithSets = mGymLogRepository.getmExercisesWithSets();
    }

    public LiveData<List<Exercise>> getmAllExercises() {
        return mAllExercises;
    }

    public LiveData<List<ExerciseWithSets>> getmExercisesWithSets() {
        return mExercisesWithSets;
    }

    public ExerciseWithSets getSingleExerciseWithSets(int exerciseId) {
        return mGymLogRepository.getmSingleExerciseWithSets(exerciseId);
    }

    public long insertExercise(Exercise exercise) {
        return mGymLogRepository.insertExercise(exercise);
    }

    public void insertMultipleSingleSets(List<SingleSet> singleSetList) {
        mGymLogRepository.insertMultipleSingleSets(singleSetList);
    }
}
