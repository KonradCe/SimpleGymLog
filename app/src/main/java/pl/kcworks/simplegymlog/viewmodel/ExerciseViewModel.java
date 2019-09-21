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

public class ExerciseViewModel extends AndroidViewModel {

    public static final String TAG = "KCtag-" + AddExerciseActivity.class.getSimpleName();

    private GymLogRepository repository;
    private MutableLiveData<ExerciseWithSets> exerciseWithSetsMutableLiveData;
    private MutableLiveData<SingleSet> singleSetToAddMutableLiveData;

    public ExerciseViewModel(@NonNull Application application) {
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

    public void setToAddModifyRepsBy(int modifier) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();
        int reps = singleSet.getReps() + modifier;
        singleSet.setReps(reps);
        singleSetToAddMutableLiveData.setValue(singleSet);
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

    public void serToAddSetReps(int reps) {
        singleSetToAddMutableLiveData.getValue().setReps(reps);
    }

    public void setToAddSetWeight(float weight) {
        singleSetToAddMutableLiveData.getValue().setWeight(weight);
    }

    public void setToAddModifyWeightBy(int modifier) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();
        double newWeight;
        if (singleSet.isBasedOnTm()) {
            int percentageOfTm = singleSet.getPercentageOfTm() + modifier;
            newWeight = roundTo((percentageOfTm * singleSet.getTrainingMax()) / 100, 2.5);
            singleSet.setPercentageOfTm(percentageOfTm);
        }
        else {
            newWeight = singleSet.getWeight() + modifier;
        }
        singleSet.setWeight(newWeight);
        singleSetToAddMutableLiveData.setValue(singleSet);
    }

    public void setToAddSetTmMax(double trainingMax) {
        SingleSet singleSet = singleSetToAddMutableLiveData.getValue();
        singleSet.setTrainingMax(trainingMax);
        singleSetToAddMutableLiveData.setValue(singleSet);
    }

    private double roundTo(double numberToRound, double roundingFactor) {
        return Math.round(numberToRound / roundingFactor) * roundingFactor;
    }

}
