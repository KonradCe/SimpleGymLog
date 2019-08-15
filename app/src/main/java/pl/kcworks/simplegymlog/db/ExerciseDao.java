package pl.kcworks.simplegymlog.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ExerciseDao {

    @Insert
    long insert(Exercise exercise);

    @Update
    void update(Exercise exercise);

    @Query("SELECT * FROM exercise_table ORDER BY exerciseId")
    LiveData<List<Exercise>> getAllExercises();

//    @Query("DELETE FROM exercise_table")
//    void deleteAllExercises();

    @Delete
    void deleteExercise(Exercise exercise);

    @Transaction
    @Query("SELECT * from exercise_table ORDER BY exerciseDate")
    LiveData<List<ExerciseWithSets>> getAllExercisesWithSets();

    @Transaction
    @Query("SELECT * from exercise_table WHERE exerciseDate=:date")
    LiveData<List<ExerciseWithSets>> getExercisesWithSetsForDate(long date);

    @Transaction
    @Query("SELECT * from exercise_table WHERE exerciseId IN (:ids)")
    List<ExerciseWithSets> getExerciseWithSetsByIds(int[] ids);

    @Transaction
    @Query("SELECT * from exercise_table WHERE exerciseDate LIKE :date")
    LiveData<List<Exercise>> getExercisesForMonth(String date);

    @Transaction
    @Query("SELECT * from exercise_table WHERE exerciseId=:id ")
    LiveData<ExerciseWithSets> getSingleExercisesWithSets(int id);

}
