package pl.kcworks.simplegymlog.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = "exercise_table", indices = {@Index("exerciseDate")})
public class Exercise implements GymLogListItem {

    @PrimaryKey(autoGenerate = true)
    private int exerciseId;

    // name of the exercise e.g. bench, deadlift etc.
    @NonNull
    private String exerciseName;

    // order in a day for the purpose displaying all exercises in current workout
    private int exerciseOrderInDay;

    // exerciseDate if the exercise
    private long exerciseDate;

    public Exercise(int exerciseId, @NotNull String exerciseName, int exerciseOrderInDay, long exerciseDate) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.exerciseOrderInDay = exerciseOrderInDay;
        this.exerciseDate = exerciseDate;
    }

    @Ignore
    public Exercise(@NonNull String exerciseName, int exerciseOrderInDay, long exerciseDate) {
        this.exerciseName = exerciseName;
        this.exerciseOrderInDay = exerciseOrderInDay;
        this.exerciseDate = exerciseDate;
    }

    // constructor for the routine
    @Ignore
    public Exercise(@NonNull String exerciseName, int exerciseOrderInDay) {
        this.exerciseName = exerciseName;
        this.exerciseOrderInDay = exerciseOrderInDay;
    }

    static Exercise createNewFromExisting(Exercise existingExercise) {
        return new Exercise(existingExercise.getExerciseName(),
                existingExercise.getExerciseOrderInDay(),
                existingExercise.getExerciseDate());
    }

    @Override
    public String toString() {
        return "Exercise{" +
                "exerciseId=" + exerciseId +
                ", exerciseName='" + exerciseName + '\'' +
                ", exerciseOrderInDay=" + exerciseOrderInDay +
                ", exerciseDate=" + exerciseDate +
                '}';
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    @NonNull
    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(@NonNull String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getExerciseOrderInDay() {
        return exerciseOrderInDay;
    }

    public void setExerciseOrderInDay(int exerciseOrderInDay) {
        this.exerciseOrderInDay = exerciseOrderInDay;
    }

    public long getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(long exerciseDate) {
        this.exerciseDate = exerciseDate;
    }

    @Override
    public GymLogType getType() {
        return GymLogType.EXERCISE;
    }


}
