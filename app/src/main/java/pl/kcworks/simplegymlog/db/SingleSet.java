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
    private String maxWeightPercentageInfo;      // states if weight for each set is calculated as a percentage of maximum weight user can perform for 1 rep
    private float weight;
    private boolean completed;              // states if the set was marked as completed

    // CONSTRUCTORS
    @Ignore
    public SingleSet(long correspondingExerciseId, int reps, String maxWeightPercentageInfo, float weight, boolean completed) {
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.maxWeightPercentageInfo = maxWeightPercentageInfo;
        this.weight = weight;
        this.completed = completed;
    }

    public SingleSet(int singleSetID, long correspondingExerciseId, int reps, String maxWeightPercentageInfo, float weight, boolean completed) {
        this.singleSetID = singleSetID;
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.maxWeightPercentageInfo = maxWeightPercentageInfo;
        this.weight = weight;
        this.completed = completed;
    }

    // method for estimating if SingleSet needs to be updated in db by comparing two SingleSet objects
    public boolean needsUpdate(SingleSet ss) {
        if (correspondingExerciseId == ss.getCorrespondingExerciseId() &&
                reps == ss.getReps() &&
                weight == ss.getWeight()) {
            return false;
        }
        return true;
    }

    public static SingleSet createNewFromExisting(SingleSet existingSingleSet) {
        return new SingleSet(existingSingleSet.getCorrespondingExerciseId(),
                existingSingleSet.getReps(),
                existingSingleSet.maxWeightPercentageInfo,
                existingSingleSet.getWeight(),
                existingSingleSet.isCompleted());
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

    public void setCorrespondingExerciseId(long correspondingExerciseId) {
        this.correspondingExerciseId = correspondingExerciseId;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public String getMaxWeightPercentageInfo() {
        return maxWeightPercentageInfo;
    }

    public void setMaxWeightPercentageInfo(String maxWeightPercentageInfo) {
        this.maxWeightPercentageInfo = maxWeightPercentageInfo;
    }

    public boolean hasMaxWeightPercentageInfo() {
        if (maxWeightPercentageInfo != null) {
            return true;
        }
        return false;
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
