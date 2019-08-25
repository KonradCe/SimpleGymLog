package pl.kcworks.simplegymlog.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        foreignKeys = @ForeignKey(
                entity = Exercise.class,
                parentColumns = "exerciseId",
                childColumns = "correspondingExerciseId",
                onDelete = CASCADE),
        indices = @Index("correspondingExerciseId"))
public class SingleSet implements GymLogListItem{

    @PrimaryKey(autoGenerate = true)
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

    // constructor for the routine
    @Ignore
    public SingleSet(int reps, String maxWeightPercentageInfo, float weight) {
        this.reps = reps;
        this.maxWeightPercentageInfo = maxWeightPercentageInfo;
        this.weight = weight;
    }

    public SingleSet(int singleSetID, long correspondingExerciseId, int reps, String maxWeightPercentageInfo, float weight, boolean completed) {
        this.singleSetID = singleSetID;
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.maxWeightPercentageInfo = maxWeightPercentageInfo;
        this.weight = weight;
        this.completed = completed;
    }

    public static SingleSet createNewFromExisting(SingleSet existingSingleSet) {
        return new SingleSet(existingSingleSet.getCorrespondingExerciseId(),
                existingSingleSet.getReps(),
                existingSingleSet.maxWeightPercentageInfo,
                existingSingleSet.getWeight(),
                existingSingleSet.isCompleted());
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

    @Override
    public String getValueToPresent() {
        return reps + " x " + weight;
    }

    @Override
    public GymLogType getType() {
        return GymLogType.SET;
    }

}
