package pl.kcworks.simplegymlog.db;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity (
        foreignKeys = @ForeignKey(
                entity= Exercise.class,
                parentColumns = "exerciseId",
                childColumns = "correspondingExerciseId",
                onDelete = CASCADE),
        indices = @Index("correspondingExerciseId"))
public class SingleSet {

    @PrimaryKey (autoGenerate = true)
    private int singleSetID;                // id
    private long correspondingExerciseId;   // parent exercise ID
    private int reps;
    private float weight;
    private boolean completed;              // states if the set was marked as completed

    // CONSTRUCTORS
    @Ignore
    public SingleSet(long correspondingExerciseId, int reps, float weight, boolean completed) {
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
    }

    public SingleSet(int singleSetID, long correspondingExerciseId, int reps, float weight, boolean completed) {
        this.singleSetID = singleSetID;
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.weight = weight;
        this.completed = completed;
    }

    public boolean needsUpdate(SingleSet ss) {
        if (correspondingExerciseId == ss.getCorrespondingExerciseId() &&
                reps == ss.getReps() &&
                weight == ss.getWeight()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SingleSet{" +
                "singleSetID=" + singleSetID +
                ", correspondingExerciseId=" + correspondingExerciseId +
                ", reps=" + reps +
                ", weight=" + weight +
                ", completed=" + completed +
                '}';
    }

    // GETTERS AND SETTERS
    public int getSingleSetID() {
        return singleSetID;
    }

    public void setSingleSetID(int singleSetID) {
        this.singleSetID = singleSetID;
    }

    public long getCorrespondingExerciseId() {
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
