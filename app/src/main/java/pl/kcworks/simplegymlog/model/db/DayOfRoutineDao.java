package pl.kcworks.simplegymlog.model.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Update;

import java.util.List;

import pl.kcworks.simplegymlog.model.DayOfRoutine;

@Dao
public interface DayOfRoutineDao{

    @Insert
    void insert(DayOfRoutine dayOfRoutine);

    @Insert
    void insertMultiple(List<DayOfRoutine> dayOfRoutineList);

    @Update
    void update(DayOfRoutine dayOfRoutine);

}
