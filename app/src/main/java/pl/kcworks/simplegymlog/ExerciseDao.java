package pl.kcworks.simplegymlog;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Insert
    void insert(Exercise exercise);

    @Query("SELECT * FROM exercise_table ORDER BY exerciseId")
    LiveData<List<Exercise>> getAllExercisese();

    @Query("SELECT * FROM exercise_table WHERE exerciseDate=:date")
    LiveData<List<Exercise>> getExercisesByDate(long date);

    @Query ("DELETE FROM exercise_table")
    void deleteAllExercises();

}
