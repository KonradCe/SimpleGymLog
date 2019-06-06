package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import pl.kcworks.simplegymlog.GymLogRepository;
import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.SingleSet;

public class GymLogViewModel extends AndroidViewModel {

    private GymLogRepository mGymLogRepository;
    private LiveData<List<ExerciseWithSets>> mExercisesWithSets;

    public GymLogViewModel(@NonNull Application application) {
        super(application);
        mGymLogRepository = GymLogRepository.getInstance(application);
        mExercisesWithSets = mGymLogRepository.getmExercisesWithSets();
    }

    public LiveData<List<ExerciseWithSets>> getmExercisesWithSets() {
        return mExercisesWithSets;
    }

    public long insertExercise(Exercise exercise) {
        return mGymLogRepository.insertExercise(exercise);
    }

    public void updateExercise(Exercise exercise) {
        mGymLogRepository.updateExercise(exercise);
    }

    public void insertMultipleSingleSets(List<SingleSet> singleSetList) {
        mGymLogRepository.insertMultipleSingleSets(singleSetList);
    }

    public void updateSingleSet(SingleSet singleSet) {
        mGymLogRepository.updateSingleSet(singleSet);

    }
}
