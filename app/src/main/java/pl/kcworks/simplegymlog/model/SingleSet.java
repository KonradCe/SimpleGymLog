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
    private double weight;
    private double trainingMax;
    private int percentageOfTm;             // percentage in int -> 100% = 100, 50% = 50 etc.
    private boolean completed;              // states if the set was marked as completed


    // CONSTRUCTORS
    public SingleSet(int singleSetID, long correspondingExerciseId, int reps, double weight, double trainingMax, int percentageOfTm, boolean completed) {
        this.singleSetID = singleSetID;
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.weight = weight;
        this.trainingMax = trainingMax;
        this.percentageOfTm = percentageOfTm;
        this.completed = completed;
    }

    @Ignore
    public SingleSet(long correspondingExerciseId, int reps, double weight, double trainingMax, int percentageOfTm, boolean completed) {
        this.correspondingExerciseId = correspondingExerciseId;
        this.reps = reps;
        this.weight = weight;
        this.trainingMax = trainingMax;
        this.percentageOfTm = percentageOfTm;
        this.completed = completed;
    }

    @Ignore
    public SingleSet(int reps, double weight) {
        this.reps = reps;
        this.weight = weight;
    }

    public static SingleSet createNewSetFromExisting(SingleSet existingSingleSet) {
        return new SingleSet(existingSingleSet.getCorrespondingExerciseId(),
                existingSingleSet.getReps(),
                existingSingleSet.getWeight(),
                existingSingleSet.getTrainingMax(),
                existingSingleSet.getPercentageOfTm(),
                existingSingleSet.isCompleted());
    }

    public void updateWeightForCurrentPercentageOfTm(double roundingFactor) {
        weight = roundTo((percentageOfTm * trainingMax) / 100, roundingFactor);
    }

    private double roundTo(double numberToRound, double roundingFactor) {
        return Math.round(numberToRound / roundingFactor) * roundingFactor;
    }


    @Override
    public String toString() {
        return "SingleSet{" +
                "singleSetID=" + singleSetID +
                ", correspondingExerciseId=" + correspondingExerciseId +
                ", reps=" + reps +
                ", weight=" + weight +
                ", trainingMax=" + trainingMax +
                ", percentageOfTm=" + percentageOfTm +
                ", completed=" + completed +
                '}';
    }

    public boolean isBasedOnTm() {
        return trainingMax != 0;
    }

    // GETTERS AND SETTERS
    public int getSingleSetID() {
        return singleSetID;
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getTrainingMax() {
        return trainingMax;
    }

    public void setTrainingMax(double trainingMax) {
        this.trainingMax = trainingMax;
    }

    public int getPercentageOfTm() {
        return percentageOfTm;
    }

    public void setPercentageOfTm(int percentageOfTm) {
        this.percentageOfTm = percentageOfTm;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }


    @Override
    public GymLogType getType() {
        return GymLogType.SET;
    }

}
