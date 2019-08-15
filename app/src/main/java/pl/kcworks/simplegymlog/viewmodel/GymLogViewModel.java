package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import pl.kcworks.simplegymlog.GymLogRepository;
import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.SingleSet;

public class GymLogViewModel extends AndroidViewModel {

    private GymLogRepository mGymLogRepository;

    public GymLogViewModel(@NonNull Application application) {
        super(application);
        mGymLogRepository = GymLogRepository.getInstance(application);
    }

    public LiveData<List<ExerciseWithSets>> getAllExercisesWithSets() {
        return mGymLogRepository.getAllExercisesWithSets();
    }

    public LiveData<List<ExerciseWithSets>> getExercisesWithSetsForDate(long date) {
        return mGymLogRepository.getExercisesWithSetsForDate(date);
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return mGymLogRepository.getAllExercises();
    }

    public LiveData<List<Exercise>> getExerciseForMonth(long date) {
        return mGymLogRepository.getExercisesForMonth(date);
    }


    public void insertExerciseWithSets(ExerciseWithSets exerciseWithSets) {
        mGymLogRepository.insertExercisesWithSets(exerciseWithSets);
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

    public void deleteMultipleSingleSets(List<SingleSet> singleSetList) {
        mGymLogRepository.deleteMultipleSingleSets(singleSetList);
    }

    public void deleteExercises(List<Exercise> exercises) {
        mGymLogRepository.deleteExercises(exercises);
    }
}
