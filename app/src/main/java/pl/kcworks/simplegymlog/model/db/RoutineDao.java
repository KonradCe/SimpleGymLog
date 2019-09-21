package pl.kcworks.simplegymlog.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.RoutineWithDays;

@Dao
public interface RoutineDao {

    @Insert
    long insert(Routine routine);

    @Update
    void update(Routine routine);

    @Transaction
    @Query("SELECT * FROM routines_table ORDER BY routineId")
    LiveData<List<RoutineWithDays>> getAllRoutinesWithDays();

    @Query("SELECT * FROM routines_table ORDER BY routineId")
    LiveData<List<Routine>> getAllRoutines();

    @Transaction
    @Query("SELECT * FROM routines_table WHERE routineId=:id")
    LiveData<RoutineWithDays> getRoutinesWithDaysById(int id);





}


