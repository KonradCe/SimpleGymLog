package pl.kcworks.simplegymlog;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.GymLogRoomDatabase;

public class AddExerciseViewModel extends ViewModel {

    private LiveData<ExerciseWithSets> exerciseWithSets;

    public AddExerciseViewModel (GymLogRoomDatabase db, int id) {
        exerciseWithSets = db.exerciseDao().getSingleExercisesWithSets(id);
    }

    public LiveData<ExerciseWithSets> getExerciseWithSets() {
        return exerciseWithSets;
    }
}
