package pl.kcworks.simplegymlog.model.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import pl.kcworks.simplegymlog.model.SingleSet;

@Dao
public interface SingleSetDao {

    @Insert
    void insert(SingleSet singleSet);

    @Insert
    void insertMultiple(List<SingleSet> singleSetList);

    @Query("SELECT * FROM singleset WHERE correspondingExerciseId=:exerciseId")
    List<SingleSet> getSingleSetsForExercise(int exerciseId);

    @Delete
    void deleteMultipleSets(List<SingleSet> singleSetList);
}
