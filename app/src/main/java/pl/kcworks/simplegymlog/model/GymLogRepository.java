package pl.kcworks.simplegymlog.model;

import android.app.Application;
import androidx.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.kcworks.simplegymlog.model.db.ExerciseDao;
import pl.kcworks.simplegymlog.model.db.GymLogRoomDatabase;
import pl.kcworks.simplegymlog.model.db.SingleSetDao;

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

    public LiveData<List<ExerciseWithSets>> getAllExercisesWithSets() {
        return mExerciseDao.getAllExercisesWithSets();
    }

    public LiveData<List<ExerciseWithSets>> getExercisesWithSetsForDate(long date) {
        return mExerciseDao.getExercisesWithSetsForDate(date);
    }

    public List<ExerciseWithSets> getExerciseWithSetsByIds(int[] ids) {
        return mExerciseDao.getExerciseWithSetsByIds(ids);
    }

    public LiveData<List<Exercise>> getExercisesForMonth(long date) {
        return mExerciseDao.getExercisesForMonth(date + "%");
    }

    public LiveData<ExerciseWithSets> getmSingleExerciseWithSets(int exerciseId) {
        return mExerciseDao.getSingleExercisesWithSets(exerciseId);
    }

    public void insertExercisesWithSets(ExerciseWithSets exerciseWithSets) {
        InsertExerciseWithSetsAsyncTask task = new InsertExerciseWithSetsAsyncTask(mExerciseDao, mSingleSetDao);
        task.execute(exerciseWithSets);
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

    public void deleteExercises(List<Exercise> exercises) {
        DeleteExercisesAsyncTask task = new DeleteExercisesAsyncTask(mExerciseDao);

        Exercise[] exerciseArray = new Exercise[exercises.size()];
        exercises.toArray(exerciseArray);

        task.execute(exerciseArray);
    }

    public void deleteExercise(Exercise exercise) {
        DeleteExerciseAsyncTask task = new DeleteExerciseAsyncTask(mExerciseDao);
        task.execute(exercise);
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

    private static class InsertExerciseWithSetsAsyncTask extends AsyncTask<ExerciseWithSets, Void, Void> {
        private ExerciseDao mAsyncExerciseDao;
        private SingleSetDao mAsyncSingleSetDao;

        InsertExerciseWithSetsAsyncTask(ExerciseDao mAsyncExerciseDao, SingleSetDao mAsyncSingleSetDao) {
            this.mAsyncExerciseDao = mAsyncExerciseDao;
            this.mAsyncSingleSetDao = mAsyncSingleSetDao;
        }

        @Override
        protected Void doInBackground(ExerciseWithSets... exerciseWithSetsArray) {
            ExerciseWithSets exerciseWithSets = exerciseWithSetsArray[0];
            long exerciseId = mAsyncExerciseDao.insert(exerciseWithSets.getExercise());
            List<SingleSet> singleSetList = exerciseWithSets.getExerciseSetList();
            for (SingleSet ss : singleSetList) {
                ss.setCorrespondingExerciseId(exerciseId);
            }
            mAsyncSingleSetDao.insertMultiple(singleSetList);
            return null;
        }
    }

    private static class UpdateExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private ExerciseDao mAsyncExerciseDao;

        UpdateExerciseAsyncTask(ExerciseDao dao) {
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

        UpdateSingleSetAsyncTask(SingleSetDao mAsyncSingleSetDao) {
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

        DeleteSingleSetAsyncTask(SingleSetDao mAsyncSingleSetDao) {
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

    private static class DeleteExerciseAsyncTask extends AsyncTask<Exercise, Void, Void> {
        ExerciseDao exerciseDao;

        DeleteExerciseAsyncTask(ExerciseDao exerciseDao) {
            this.exerciseDao = exerciseDao;
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            exerciseDao.deleteExercise(exercises[0]);
            return null;
        }
    }

    private static class DeleteExercisesAsyncTask extends AsyncTask<Exercise, Void, Void> {
        private ExerciseDao exerciseDao;

        DeleteExercisesAsyncTask(ExerciseDao exerciseDao) {
            this.exerciseDao = exerciseDao;
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            for (Exercise exercise : exercises) {
                exerciseDao.deleteExercise(exercise);
            }
            return null;
        }
    }
}
