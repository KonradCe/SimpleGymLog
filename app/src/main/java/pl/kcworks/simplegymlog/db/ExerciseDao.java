package pl.kcworks.simplegymlog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import pl.kcworks.simplegymlog.db.Exercise;

@Dao
public interface ExerciseDao {

    @Insert
    long insert(Exercise exercise);

    @Query("SELECT * FROM exercise_table ORDER BY exerciseId")
    LiveData<List<Exercise>> getAllExercisese();

    @Query("SELECT * FROM exercise_table WHERE exerciseDate=:date")
    LiveData<List<Exercise>> getExercisesByDate(long date);

    @Query ("DELETE FROM exercise_table")
    void deleteAllExercises();

    @Query("SELECT * FROM exercise_table LEFT JOIN SingleSet ON exerciseId = correspondingExerciseId")
    LiveData<List<ExerciseWithSets>> getExerciseWithSets();

    @Query("SELECT * from exercise_table")
    LiveData<List<ExerciseWithSets>> selectExercisesWithSets();

}
