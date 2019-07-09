package pl.kcworks.simplegymlog;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseDao;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;
import pl.kcworks.simplegymlog.db.GymLogRoomDatabase;
import pl.kcworks.simplegymlog.db.SingleSet;
import pl.kcworks.simplegymlog.db.SingleSetDao;

public class GymLogRepository {

    private static GymLogRepository sInstance;

    private ExerciseDao mExerciseDao;
    private SingleSetDao mSingleSetDao;

    private LiveData<List<ExerciseWithSets>> mExercisesWithSets;

    private long newExerciseId = -1;

    private GymLogRepository(Application application) {
        GymLogRoomDatabase db = GymLogRoomDatabase.getDatabase(application);
        mExerciseDao = db.exerciseDao();
        mSingleSetDao = db.singleSetDao();

        mExercisesWithSets = mExerciseDao.getExercisesWithSets();
    }

    public static GymLogRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (GymLogRepository.class) {
                if (sInstance == null) {
                    sInstance = new GymLogRepository(application);
                }
            }
        }
        return sInstance;
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return mExerciseDao.getAllExercises();
    }

    public LiveData<List<ExerciseWithSets>> getmExercisesWithSets() {
        return mExercisesWithSets;
    }

    public LiveData<List<ExerciseWithSets>> getmExercisesWithSetsForDate(long date) {
        return mExerciseDao.getExercisesWithSetsForDate(date);
    }

    public LiveData<List<Exercise>> getExercisesForMonth(long date) {
        return mExerciseDao.getExercisesForMonth(date + "%");
    }

    public LiveData<ExerciseWithSets> getmSingleExerciseWithSets(int exerciseId) {
        return mExerciseDao.getSingleExercisesWithSets(exerciseId);
    }

    public long insertExercise(Exercise exercise) {
        InsertExerciseAsyncTask task = new InsertExerciseAsyncTask(mExerciseDao);

        try {
            // using .get() method on AsyncTask in this place denies the whole purpose of AsyncTask - using get we still have to wait for results from the task on the UI thread;
            // still, for this moment I don't know how to do this task* any other way and inserting ONLY exercise should not take that long anyway
            // *this task - task of inserting exercise into db and getting its newly created id
            newExerciseId = task.execute(exercise).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return newExerciseId;
    }

    public void updateExercise(Exercise exercise) {
        UpdateExerciseAsyncTask task = new UpdateExerciseAsyncTask(mExerciseDao);
        task.execute(exercise);
    }

    public void insertMultipleSingleSets(List<SingleSet> singleSetList) {
        InsertSetsAsyncTask task = new InsertSetsAsyncTask(mSingleSetDao);

        // conversion from List to array for the AsyncTask
        SingleSet[] ssArray = new SingleSet[singleSetList.size()];
        singleSetList.toArray(ssArray);

        task.execute(ssArray);
    }

    public void deleteMultipleSingleSets(List<SingleSet> singleSetList) {
        DeleteSingleSetAsyncTask task = new DeleteSingleSetAsyncTask(mSingleSetDao);

        // conversion from List to array for the AsyncTask
        SingleSet[] ssArray = new SingleSet[singleSetList.size()];
        singleSetList.toArray(ssArray);

        task.execute(ssArray);

    }

    public void updateSingleSet(SingleSet singleSet) {
        UpdateSingleSetAsyncTask task = new UpdateSingleSetAsyncTask(mSingleSetDao);
        task.execute(singleSet);
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

    private static class UpdateExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private ExerciseDao mAsyncExerciseDao;

        public UpdateExerciseAsyncTask(ExerciseDao dao) {
            mAsyncExerciseDao = dao;
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            mAsyncExerciseDao.update(exercises[0]);
            return null;
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

    private static class UpdateSingleSetAsyncTask extends AsyncTask<SingleSet, Void, Void> {
        private SingleSetDao mAsyncSingleSetDao;

        public UpdateSingleSetAsyncTask(SingleSetDao mAsyncSingleSetDao) {
            this.mAsyncSingleSetDao = mAsyncSingleSetDao;
        }

        @Override
        protected Void doInBackground(SingleSet... singleSets) {
            mAsyncSingleSetDao.updateSet(singleSets[0]);
            return null;
        }
    }

    private static class DeleteSingleSetAsyncTask extends AsyncTask<SingleSet, Void, Void> {
        SingleSetDao mAsyncSingleSetDao;

        public DeleteSingleSetAsyncTask(SingleSetDao mAsyncSingleSetDao) {
            this.mAsyncSingleSetDao = mAsyncSingleSetDao;
        }

        @Override
        protected Void doInBackground(SingleSet... singleSets) {
            for (SingleSet ss : singleSets) {
                mAsyncSingleSetDao.deleteSet(ss);
            }
            return null;
        }
    }
}
