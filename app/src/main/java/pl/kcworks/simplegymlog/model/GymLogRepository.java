package pl.kcworks.simplegymlog.model;

import android.app.Application;
import androidx.lifecycle.LiveData;

import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import pl.kcworks.simplegymlog.model.db.DayOfRoutineDao;
import pl.kcworks.simplegymlog.model.db.ExerciseDao;
import pl.kcworks.simplegymlog.model.db.GymLogRoomDatabase;
import pl.kcworks.simplegymlog.model.db.RoutineDao;
import pl.kcworks.simplegymlog.model.db.SingleSetDao;

public class GymLogRepository {

    private static GymLogRepository sInstance;

    private RoutineDao routineDao;
    private DayOfRoutineDao dayOfRoutineDao;
    private ExerciseDao exerciseDao;
    private SingleSetDao singleSetDao;

    private LiveData<List<ExerciseWithSets>> mExercisesWithSets;

    private long newExerciseId = -1;

    private GymLogRepository(Application application) {
        GymLogRoomDatabase db = GymLogRoomDatabase.getDatabase(application);
        routineDao = db.routineDao();
        dayOfRoutineDao = db.dayOfRoutineDao();
        exerciseDao = db.exerciseDao();
        singleSetDao = db.singleSetDao();
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

    public LiveData<List<RoutineWithDays>> getAllRoutinesWithDays() {
        return routineDao.getAllRoutinesWithDays();
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return exerciseDao.getAllExercises();
    }

    public LiveData<List<ExerciseWithSets>> getAllExercisesWithSets() {
        return exerciseDao.getAllExercisesWithSets();
    }

    public LiveData<List<ExerciseWithSets>> getExercisesWithSetsForDate(long date) {
        return exerciseDao.getExercisesWithSetsForDate(date);
    }

    public List<ExerciseWithSets> getExerciseWithSetsByIds(int[] ids) {
        return exerciseDao.getExerciseWithSetsByIds(ids);
    }

    public LiveData<List<Exercise>> getExercisesForMonth(long date) {
        return exerciseDao.getExercisesForMonth(date + "%");
    }

    public LiveData<ExerciseWithSets> getmSingleExerciseWithSets(int exerciseId) {
        return exerciseDao.getSingleExercisesWithSets(exerciseId);
    }

    public void insertRoutineWithDays(RoutineWithDays routineWithDays) {
        InsertRoutineWithDaysAsyncTask task = new InsertRoutineWithDaysAsyncTask(routineDao, dayOfRoutineDao);
        task.execute(routineWithDays);
    }

    public void updateRoutine(Routine routine) {
        UpdateRoutineAsyncTask task = new UpdateRoutineAsyncTask(routineDao);
        task.execute(routine);
    }

    public void updateDayOfRoutine(DayOfRoutine dayOfRoutine) {
        UpdateDayOfRoutineAsyncTask task = new UpdateDayOfRoutineAsyncTask(dayOfRoutineDao);
        task.execute(dayOfRoutine);
    }

    public void insertExercisesWithSets(ExerciseWithSets exerciseWithSets) {
        InsertExerciseWithSetsAsyncTask task = new InsertExerciseWithSetsAsyncTask(exerciseDao, singleSetDao);
        task.execute(exerciseWithSets);
    }

    public long insertExercise(Exercise exercise) {
        InsertExerciseAsyncTask task = new InsertExerciseAsyncTask(exerciseDao);

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
        UpdateExerciseAsyncTask task = new UpdateExerciseAsyncTask(exerciseDao);
        task.execute(exercise);
    }

    public void insertMultipleSingleSets(List<SingleSet> singleSetList) {
        InsertSetsAsyncTask task = new InsertSetsAsyncTask(singleSetDao);

        // conversion from List to array for the AsyncTask
        SingleSet[] ssArray = new SingleSet[singleSetList.size()];
        singleSetList.toArray(ssArray);

        task.execute(ssArray);
    }

    public void deleteMultipleSingleSets(List<SingleSet> singleSetList) {
        DeleteSingleSetAsyncTask task = new DeleteSingleSetAsyncTask(singleSetDao);

        // conversion from List to array for the AsyncTask
        SingleSet[] ssArray = new SingleSet[singleSetList.size()];
        singleSetList.toArray(ssArray);

        task.execute(ssArray);

    }

    public void updateSingleSet(SingleSet singleSet) {
        UpdateSingleSetAsyncTask task = new UpdateSingleSetAsyncTask(singleSetDao);
        task.execute(singleSet);
    }

    public void deleteExercises(List<Exercise> exercises) {
        DeleteExercisesAsyncTask task = new DeleteExercisesAsyncTask(exerciseDao);

        Exercise[] exerciseArray = new Exercise[exercises.size()];
        exercises.toArray(exerciseArray);

        task.execute(exerciseArray);
    }

    public void deleteExercise(Exercise exercise) {
        DeleteExerciseAsyncTask task = new DeleteExerciseAsyncTask(exerciseDao);
        task.execute(exercise);
    }

    private static class InsertRoutineWithDaysAsyncTask extends AsyncTask<RoutineWithDays, Void, Void> {
        private RoutineDao routineDao;
        private DayOfRoutineDao dayOfRoutineDao;

        public InsertRoutineWithDaysAsyncTask(RoutineDao routineDao, DayOfRoutineDao dayOfRoutineDao) {
            this.routineDao = routineDao;
            this.dayOfRoutineDao = dayOfRoutineDao;
        }


        @Override
        protected Void doInBackground(RoutineWithDays... routineWithDays) {
            Routine routine = routineWithDays[0].getRoutine();
            List<DayOfRoutine> dayOfRoutineList = routineWithDays[0].getDayOfRoutineList();
            long routineId = routineDao.insert(routine);
            for (DayOfRoutine dayOfRoutine : dayOfRoutineList) {
                dayOfRoutine.setParentRoutineId(routineId);
            }
            dayOfRoutineDao.insertMultiple(dayOfRoutineList);
            return null;
        }
    }

    private static class UpdateRoutineAsyncTask extends AsyncTask<Routine, Void, Void> {
        private RoutineDao routineDao;

        UpdateRoutineAsyncTask(RoutineDao routineDao) {
            this.routineDao = routineDao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            routineDao.update(routines[0]);
            return null;
        }
    }

    private static class UpdateDayOfRoutineAsyncTask extends AsyncTask<DayOfRoutine, Void, Void> {
        DayOfRoutineDao dayOfRoutineDao;

        UpdateDayOfRoutineAsyncTask(DayOfRoutineDao dayOfRoutineDao) {
            this.dayOfRoutineDao = dayOfRoutineDao;
        }

        @Override
        protected Void doInBackground(DayOfRoutine... dayOfRoutines) {
            dayOfRoutineDao.update(dayOfRoutines[0]);
            return null;
        }
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
        private SingleSetDao mAsyncSingleSetDao;

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
        private ExerciseDao exerciseDao;

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
