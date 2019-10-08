package pl.kcworks.simplegymlog.viewmodel;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.GymLogRepository;
import pl.kcworks.simplegymlog.model.SingleSet;
import pl.kcworks.simplegymlog.ui.AddExerciseActivity;

public class AddExerciseViewModel extends AndroidViewModel {

    public static final String TAG = "KCtag-" + AddExerciseViewModel.class.getSimpleName();

    private GymLogRepository repository;
    private MutableLiveData<ExerciseWithSets> exerciseWithSetsMutableLiveData;
    private MutableLiveData<SingleSet> singleSetToAddMutableLiveData;

    public AddExerciseViewModel(@NonNull Application application) {
        super(application);

        exerciseWithSetsMutableLiveData = new MutableLiveData<>();

        singleSetToAddMutableLiveData = new MutableLiveData<>();
        singleSetToAddMutableLiveData.setValue(new SingleSet(0, 0));

        repository = GymLogRepository.getInstance(application);

    }

    public LiveData<ExerciseWithSets> getExerciseWithSetsMutableLiveData() {
        return exerciseWithSetsMutableLiveData;
    }

    public MutableLiveData<SingleSet> getSingleSetToAddMutableLiveData() {
        return singleSetToAddMutableLiveData;
    }

    public void setInitialValue(ExerciseWithSets exerciseWithSets) {
        exerciseWithSetsMutableLiveData.setValue(exerciseWithSets);
    }

    public void removeLastSet() {
        ExerciseWithSets exerciseWithSets = exerciseWithSetsMutableLiveData.getValue();
        exerciseWithSets.getExerciseSetList().remove(exerciseWithSets.getExerciseSetList().size() - 1);

        exerciseWithSetsMutableLiveData.setValue(exerciseWithSets);
    }

    public void saveToDb(AddExerciseActivity.ActivityMode mode) {
        ExerciseWithSets exerciseWithSets = exerciseWithSetsMutableLiveData.getValue();

        if(mode == AddExerciseActivity.ActivityMode.ADD_EXERCISE) {
            repository.insertExerciseWithSets(exerciseWithSets);
        }
        else if (mode == AddExerciseActivity.ActivityMode.EDIT_EXERCISE) {
            repository.updateExerciseWithSets(exerciseWithSets);
        }
    }

    public void addSet() {
        ExerciseWithSets exerciseWithSets = exerciseWithSetsMutableLiveData.getValue();
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();
        SingleSet singleSetToAdd = SingleSet.createNewSetFromExisting(singleSet);
        singleSetToAdd.setCorrespondingExerciseId(exerciseWithSets.getExercise().getExerciseId());
        exerciseWithSets.getExerciseSetList().add(singleSetToAdd);
        exerciseWithSetsMutableLiveData.setValue(exerciseWithSets);
    }

    public void setName(String exerciseName) {
        exerciseWithSetsMutableLiveData.getValue().getExercise().setExerciseName(exerciseName);
    }

    public void setToAddSetReps(int reps) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();
        singleSet.setReps(reps);
        singleSetToAddMutableLiveData.setValue(singleSet);
    }

    public void setToAddModifyRepsBy(int modifier) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();
        int reps = singleSet.getReps() + modifier;
        singleSet.setReps(reps);
        singleSetToAddMutableLiveData.setValue(singleSet);
    }

    public void setToAddSetWeight(double weight) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();

        if (singleSet.isBasedOnTm()) {
            singleSet.setPercentageOfTm((int) weight);
            singleSet.updateWeightForCurrentPercentageOfTm(2.5);
        }
        else {
            singleSet.setWeight(weight);
        }
        Log.i(TAG, singleSet.toString());
        singleSetToAddMutableLiveData.setValue(singleSet);
    }

    public void setToAddModifyWeightBy(int modifier) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();

        if (singleSet.isBasedOnTm()) {
            int percentageOfTm = singleSet.getPercentageOfTm() + modifier;
            singleSet.setPercentageOfTm(percentageOfTm);
            singleSet.updateWeightForCurrentPercentageOfTm(2.5);
        }
        else {
            double newWeight = singleSet.getWeight() + modifier;
            singleSet.setWeight(newWeight);
        }
        Log.i(TAG, singleSet.toString());
        singleSetToAddMutableLiveData.setValue(singleSet);
    }

    public void setToAddSetTmMax(double trainingMax) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();
        if (singleSet.getTrainingMax() == trainingMax) {
            return;
        }
        singleSet.setTrainingMax(trainingMax);
        singleSetToAddMutableLiveData.setValue(singleSet);
    }

    public void updateExerciseWithSetsWithNewTm(double newTm) {
        ExerciseWithSets exerciseWithSets = exerciseWithSetsMutableLiveData.getValue();
        for (SingleSet singleSet : exerciseWithSets.getExerciseSetList()) {
            if (singleSet.isBasedOnTm()) {
                singleSet.setTrainingMax(newTm);
                singleSet.updateWeightForCurrentPercentageOfTm(2.5);
            }
        }

        exerciseWithSetsMutableLiveData.setValue(exerciseWithSets);
    }

}
