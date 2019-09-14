package pl.kcworks.simplegymlog.model.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import pl.kcworks.simplegymlog.model.SingleSet;

@Dao
public interface SingleSetDao {

    @Insert
    void insert(SingleSet singleSet);

    @Insert
    void insertMultiple(List<SingleSet> singleSetList);

    @Query("SELECT * FROM singleset")
    LiveData<List<SingleSet>> getAllSingleSets();

    @Query("SELECT * FROM singleset WHERE correspondingExerciseId=:exerciseId")
    List<SingleSet> getSingleSetsForExercise(int exerciseId);

    @Update
    void updateSet(SingleSet singleSet);

    @Update
    void updateMultipleSets(List<SingleSet> singleSetList);

    @Delete
    void deleteSet(SingleSet singleSet);

    @Delete
    void deleteMultipleSets(List<SingleSet> singleSetList);
}
