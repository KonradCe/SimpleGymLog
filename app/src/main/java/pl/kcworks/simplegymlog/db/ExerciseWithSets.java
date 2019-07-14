package pl.kcworks.simplegymlog.db;


import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class ExerciseWithSets {
    @Embedded private Exercise exercise;

    @Relation(parentColumn = "exerciseId", entityColumn = "correspondingExerciseId")
    private List<SingleSet> exerciseSetList;

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
