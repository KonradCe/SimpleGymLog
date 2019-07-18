package pl.kcworks.simplegymlog.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface SingleSetDao {

    @Insert
    void insert(SingleSet singleSet);

    @Insert
    void insertMultiple(List<SingleSet> singleSetList);

    @Query("SELECT * FROM singleset")
    LiveData<List<SingleSet>> getAllSingleSets();

    @Query("SELECT * FROM singleset WHERE correspondingExerciseId=:exerciseId")
    LiveData<List<SingleSet>> getSingleSetsForExercise(int exerciseId);

    @Update
    void updateSet(SingleSet singleSet);

    @Delete
    void deleteSet(SingleSet singleSet);
}
