package pl.kcworks.simplegymlog.db;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import java.util.ArrayList;
import java.util.List;

public class ExerciseWithSets {
    @Embedded private Exercise exercise;

    @Relation(parentColumn = "exerciseId", entityColumn = "correspondingExerciseId")
    private List<SingleSet> exerciseSetList;

    // this empty constructor is required by room
    // (because of another constructor that was created, compiler wasn't creating default (empty) constructor anymore.
    // However the other constructor has @Ignore annotation so Room had no constructor to use)
    public ExerciseWithSets() {
    }

    @Ignore
    public ExerciseWithSets(Exercise exercise, List<SingleSet> exerciseSetList) {
        this.exercise = exercise;
        this.exerciseSetList = exerciseSetList;
    }

    public static ExerciseWithSets createNewFromExisting(ExerciseWithSets existingExerciseWithSets) {
        List<SingleSet> newSingleSetList = new ArrayList<>();
        for (SingleSet ss : existingExerciseWithSets.getExerciseSetList()) {
            newSingleSetList.add(SingleSet.createNewFromExisting(ss));
        }
        return new ExerciseWithSets(Exercise.createNewFromExisting(existingExerciseWithSets.getExercise()), newSingleSetList);
    }

    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<SingleSet> getExerciseSetList() {
        return exerciseSetList;
    }

    public void setExerciseSetList(List<SingleSet> exerciseSetList) {
        this.exerciseSetList = exerciseSetList;
    }

    @Override
    public String toString() {
        return "ExerciseWithSets{" +
                "exercise=" + exercise +
                ", exerciseSetList=" + exerciseSetList +
                '}';
    }
}
