package pl.kcworks.simplegymlog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;

import pl.kcworks.simplegymlog.db.Exercise;

@Dao
public interface ExerciseDao {

    @Insert
    long insert(Exercise exercise);

    @Update
    void update(Exercise exercise);

    @Query("SELECT * FROM exercise_table ORDER BY exerciseId")
    List<Exercise> getAllExercises();

    @Query("SELECT * FROM exercise_table WHERE exerciseDate=:date")
    LiveData<List<Exercise>> getExercisesByDate(long date);

    @Query ("DELETE FROM exercise_table")
    void deleteAllExercises();

    @Transaction
    @Query("SELECT * from exercise_table")
    LiveData<List<ExerciseWithSets>> getExercisesWithSets();

    @Transaction
    @Query("SELECT * from exercise_table WHERE exerciseDate=:date")
    LiveData<List<ExerciseWithSets>> getExercisesWithSetsForDate(long date);

    @Transaction
    @Query("SELECT * from exercise_table WHERE exerciseDate LIKE :date")
    LiveData<List<Exercise>> getExercisesForMonth(String date);

    @Transaction
    @Query("SELECT * from exercise_table WHERE exerciseId=:id ")
    LiveData<ExerciseWithSets> getSingleExercisesWithSets(int id);

}
