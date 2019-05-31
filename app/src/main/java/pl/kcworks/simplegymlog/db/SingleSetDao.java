package pl.kcworks.simplegymlog.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import pl.kcworks.simplegymlog.db.SingleSet;

@Dao
public interface SingleSetDao {

    @Insert
    void insert(SingleSet singleSet);

    @Query("SELECT * FROM singleset")
    LiveData<List<SingleSet>> getAllSingleSets();

    @Query("SELECT * FROM singleset WHERE correspondingExerciseId=:exerciseId")
    LiveData<List<SingleSet>> getSingleSetsForExercise(int exerciseId);

    @Update
    void updateSet(SingleSet singleSet);
}
