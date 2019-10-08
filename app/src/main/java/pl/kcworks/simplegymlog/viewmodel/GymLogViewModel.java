package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import pl.kcworks.simplegymlog.model.DayOfRoutine;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.GymLogRepository;

public class GymLogViewModel extends AndroidViewModel {

    private GymLogRepository gymLogRepository;

    public GymLogViewModel(@NonNull Application application) {
        super(application);
        gymLogRepository = GymLogRepository.getInstance(application);
    }

    public LiveData<List<ExerciseWithSets>> getAllExercisesWithSets() {
        return gymLogRepository.getAllExercisesWithSets();
    }

    public LiveData<List<ExerciseWithSets>> getExercisesWithSetsForDate(long date) {
        return gymLogRepository.getExercisesWithSetsForDate(date);
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return gymLogRepository.getAllExercises();
    }

    public void insertExerciseWithSets(ExerciseWithSets exerciseWithSets) {
        gymLogRepository.insertExerciseWithSets(exerciseWithSets);
    }

    public void deleteExercises(List<Exercise> exercises) {
        gymLogRepository.deleteExercises(exercises);
    }

    public void deleteSingleExercise(Exercise exercise) {
        gymLogRepository.deleteExercise(exercise);
    }

    public void insertDayOfRoutineAsExercises(DayOfRoutine dayOfRoutine, long dateOfExercise) {
        for (ExerciseWithSets exerciseWithSets : dayOfRoutine.getExerciseWithSetsList()) {
            exerciseWithSets.getExercise().setExerciseDate(dateOfExercise);
        }
        gymLogRepository.insertDayOfRoutineAsExercises(dayOfRoutine);
    }
}
