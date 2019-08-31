package pl.kcworks.simplegymlog.model.db;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.kcworks.simplegymlog.model.Routine;

@Dao
public interface RoutineDao {

    @Insert
    void insert(Routine routine);

    @Update
    void update(Routine routine);

    @Query("SELECT * FROM routines_table ORDER BY routineId")
    LiveData<List<Routine>> getAllRoutines();
}


