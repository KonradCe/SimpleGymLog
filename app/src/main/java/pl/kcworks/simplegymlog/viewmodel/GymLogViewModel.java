package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.annotation.NonNull;

import java.util.List;

import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;

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

    public void insertExerciseWithSets(ExerciseWithSets exerciseWithSets) {
        mGymLogRepository.insertExerciseWithSets(exerciseWithSets);
    }

    public void deleteExercises(List<Exercise> exercises) {
        mGymLogRepository.deleteExercises(exercises);
    }

    public void deleteSingleExercise(Exercise exercise) {
        mGymLogRepository.deleteExercise(exercise);
    }
}
