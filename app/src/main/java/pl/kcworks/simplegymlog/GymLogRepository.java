package pl.kcworks.simplegymlog;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseDao;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.GymLogRoomDatabase;
import pl.kcworks.simplegymlog.db.SingleSet;
import pl.kcworks.simplegymlog.db.SingleSetDao;

public class GymLogRepository {

    private ExerciseDao mExerciseDao;
    private SingleSetDao mSingleSetDao;

    private LiveData<List<Exercise>> mAllExercises;
    private LiveData<List<Exercise>> mExercisesFromDay;

    private LiveData<List<SingleSet>> mAllSingleSets;
    private LiveData<List<SingleSet>> mSingleSetsForExercise;

    private LiveData<List<ExerciseWithSets>> mExercisesWithSets;

    private long newExerciseId = -1;

    GymLogRepository(Application application) {
        GymLogRoomDatabase db = GymLogRoomDatabase.getDatabase(application);
        mExerciseDao = db.exerciseDao();
        mSingleSetDao = db.singleSetDao();

        mAllExercises = mExerciseDao.getAllExercisese();
        mAllSingleSets = mSingleSetDao.getAllSingleSets();

        mExercisesWithSets = mExerciseDao.selectExercisesWithSets();
    }

    LiveData<List<Exercise>> getAllExercises() {
        return mAllExercises;
    }

    LiveData<List<SingleSet>> getAllSingleSets() {
        return mAllSingleSets;
    }

    public LiveData<List<ExerciseWithSets>> getmExercisesWithSets() {
        return mExercisesWithSets;
    }

    public long insertExercise(Exercise exercise) {
        InsertExerciseAsyncTask task = new InsertExerciseAsyncTask(mExerciseDao);

        try {
            // using .get() method on AsyncTask in this place denies the whole purpose of AsyncTask - using get we still have to wait for results from the task on the UI thread;
            // still, for this moment I don't know how to do this any other way and inserting ONLY exercise should not take that long anyway
            newExerciseId = task.execute(exercise).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return newExerciseId;
    }

    public void insertMultipleSingleSets(List<SingleSet> singleSetList) {
        InsertSetsAsyncTask task = new InsertSetsAsyncTask(mSingleSetDao);
        // conversion from List to array for the AsyncTask
        SingleSet[] ssArray = new SingleSet[singleSetList.size()];
        singleSetList.toArray(ssArray);
        task.execute(ssArray);
    }

    private static class InsertExerciseAsyncTask extends AsyncTask<Exercise, Void, Long> {
        private ExerciseDao mAsyncExerciseDao;

        InsertExerciseAsyncTask(ExerciseDao exerciseDao) {
            mAsyncExerciseDao = exerciseDao;
        }

        @Override
        protected Long doInBackground(Exercise... exercises) {
            long newExerciseId = mAsyncExerciseDao.insert(exercises[0]);
            return newExerciseId;
        }

    }

    private static class InsertSetsAsyncTask extends AsyncTask<SingleSet, Void, Void> {
        private SingleSetDao mAsyncSingleSetDao;

        InsertSetsAsyncTask(SingleSetDao singleSetDao) {
            mAsyncSingleSetDao = singleSetDao;
        }

        @Override
        protected Void doInBackground(SingleSet... singleSets) {
            for (SingleSet set : singleSets) {
                mAsyncSingleSetDao.insert(set);
            }
            return null;
        }
    }
}
