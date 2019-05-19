package pl.kcworks.simplegymlog;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class GymLogRepository {

    private ExerciseDao mExerciseDao;
    private SingleSetDao mSingleSetDao;

    private LiveData<List<Exercise>> mAllExercises;
    private LiveData<List<Exercise>> mExercisesFromDay;

    private LiveData<List<SingleSet>> mAllSingleSets;
    private LiveData<List<SingleSet>> mSingleSetsForExercise;

    GymLogRepository(Application application) {
        GymLogRoomDatabase db = GymLogRoomDatabase.getDatabase(application);
        mExerciseDao = db.exerciseDao();
        mSingleSetDao = db.singleSetDao();

        mAllExercises = mExerciseDao.getAllExercisese();
        mAllSingleSets = mSingleSetDao.getAllSingleSets();
    }

    LiveData<List<Exercise>> getAllExercises() {
        return mAllExercises;
    }

    LiveData<List<SingleSet>> getAllSingleSets() {
        return mAllSingleSets;
    }

    public void insertExercise(Exercise exercise) {
        new insertExerciseAsyncTask(mExerciseDao).execute(exercise);
    }


    private static class insertExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private ExerciseDao mAsyncExerciseDao;

        insertExerciseAsyncTask(ExerciseDao exerciseDao) {
            mAsyncExerciseDao = exerciseDao;
        }
        @Override
        protected Void doInBackground(Exercise... exercises) {
            mAsyncExerciseDao.insert(exercises[0]);
            return null;
        }
    }
}
