package pl.kcworks.simplegymlog;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.Date;

@Entity(tableName = "exercise_table", indices = {@Index("exerciseDate")})
public class Exercise {

    @PrimaryKey (autoGenerate = true)
    private int exerciseId;

    // name of the exercise e.g. bench, deadlift etc.
    @NonNull
    private String exerciseName;

    // order in a day for the purpose displaying all exercises in current workout
    private int exerciseOrderInDay;

    // exerciseDate if the exercise
    private long exerciseDate;

    public Exercise(int exerciseId, @NonNull String exerciseName, int exerciseOrderInDay, long exerciseDate) {
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
}
