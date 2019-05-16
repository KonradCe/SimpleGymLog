package pl.kcworks.simplegymlog;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity (
        foreignKeys = @ForeignKey(
                entity=Exercise.class,
                parentColumns = "exerciseId",
                childColumns = "correspondingExerciseId",
                onDelete = CASCADE),
        indices = @Index("correspondingExerciseId"))
public class SingleSet {

    @PrimaryKey (autoGenerate = true)
    private int singleSetID;
    private int correspondingExerciseId;
    private int reps;
    private float weight;
    private boolean completed;

    // CONSTRUCTORS
    @Ignore
    public SingleSet(int correspondingExerciseId, int reps, float weight, boolean completed) {
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
    }

    public SingleSet(int singleSetID, int correspondingExerciseId, int reps, float weight, boolean completed) {
        this.singleSetID = singleSetID;
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
    }


    // GETTERS AND SETTERS
    public int getSingleSetID() {
        return singleSetID;
    }

    public void setSingleSetID(int singleSetID) {
        this.singleSetID = singleSetID;
    }

    public int getCorrespondingExerciseId() {
        return correspondingExerciseId;
    }

    public void setCorrespondingExerciseId(int correspondingExerciseId) {
        this.correspondingExerciseId = correspondingExerciseId;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
