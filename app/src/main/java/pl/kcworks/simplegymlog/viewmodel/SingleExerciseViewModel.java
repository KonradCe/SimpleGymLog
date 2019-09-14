package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;

public class SingleExerciseViewModel extends AndroidViewModel {

    private LiveData<ExerciseWithSets> exerciseWithSets;

    public SingleExerciseViewModel(Application application, GymLogRepository repository, int exerciseId) {
        super(application);
        exerciseWithSets = repository.getSingleExerciseWithSets(exerciseId);
    }

    public LiveData<ExerciseWithSets> getExerciseWithSets() {
        return exerciseWithSets;
    }

    public static class Factory extends ViewModelProvider.NewInstanceFactory {
        private final Application application;
        private final int mExerciseId;
        private final GymLogRepository mRepository;

        public Factory(Application application, int mExerciseId) {
            this.application = application;
            this.mExerciseId = mExerciseId;
            mRepository = GymLogRepository.getInstance(application);
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new SingleExerciseViewModel(application, mRepository, mExerciseId);
        }
    }
}
