package pl.kcworks.simplegymlog.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

import pl.kcworks.simplegymlog.model.db.DayOfRoutineDao;
import pl.kcworks.simplegymlog.model.db.ExerciseDao;
import pl.kcworks.simplegymlog.model.db.GymLogRoomDatabase;
import pl.kcworks.simplegymlog.model.db.RoutineDao;
import pl.kcworks.simplegymlog.model.db.SingleSetDao;

public class GymLogRepository {

    private final String TAG = "KCTag-" + GymLogRepository.class.getSimpleName();
    private static GymLogRepository sInstance;

    private RoutineDao routineDao;
    private DayOfRoutineDao dayOfRoutineDao;
    private ExerciseDao exerciseDao;
    private SingleSetDao singleSetDao;

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

    public LiveData<RoutineWithDays> getRoutinesWithDaysById(int routineId) {
        return routineDao.getRoutinesWithDaysById(routineId);
    }

    public LiveData<List<Routine>> getRoutines() {
        return routineDao.getAllRoutines();
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return exerciseDao.getAllExercises();
    }

    public LiveData<List<String>> getAllExerciseNames() {
        return exerciseDao.getAllExerciseNames();
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

    public LiveData<ExerciseWithSets> getSingleExerciseWithSets(int exerciseId) {
        return exerciseDao.getSingleExercisesWithSets(exerciseId);
    }

    public void insertRoutineWithDays(RoutineWithDays routineWithDays) {
        InsertRoutineWithDaysAsyncTask task = new InsertRoutineWithDaysAsyncTask(routineDao, dayOfRoutineDao);
        task.execute(routineWithDays);
    }

    public void insertDayOfRoutine(DayOfRoutine dayOfRoutine) {
        InsertDayOfRoutineAsyncTask task = new InsertDayOfRoutineAsyncTask(dayOfRoutineDao);
        task.execute(dayOfRoutine);
    }

    public void insertDayOfRoutineAsExercises(DayOfRoutine dayOfRoutine) {
        InsertDayOufRoutineAsExercisesAsyncTask task = new InsertDayOufRoutineAsExercisesAsyncTask(exerciseDao, singleSetDao);
        task.execute(dayOfRoutine);
    }

    public void updateRoutine(Routine routine) {
        UpdateRoutineAsyncTask task = new UpdateRoutineAsyncTask(routineDao);
        task.execute(routine);
    }

    public void updateDayOfRoutine(DayOfRoutine dayOfRoutine) {
        UpdateDayOfRoutineAsyncTask task = new UpdateDayOfRoutineAsyncTask(dayOfRoutineDao);
        task.execute(dayOfRoutine);
    }

    public void insertExerciseWithSets(ExerciseWithSets exerciseWithSets) {
        InsertExerciseWithSetsAsyncTask task = new InsertExerciseWithSetsAsyncTask(exerciseDao, singleSetDao);
        task.execute(exerciseWithSets);
    }

    public void updateExerciseWithSets(ExerciseWithSets exerciseWithSets) {
        UpdateExerciseWithSetsAsyncTask task = new UpdateExerciseWithSetsAsyncTask(exerciseDao, singleSetDao);
        task.execute(exerciseWithSets);
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

    public void deleteRoutine(Routine routine) {
        DeleteRoutineAsyncTask task = new DeleteRoutineAsyncTask(routineDao);
        task.execute(routine);
    }

    private static class InsertRoutineWithDaysAsyncTask extends AsyncTask<RoutineWithDays, Void, Void> {
        private RoutineDao routineDao;
        private DayOfRoutineDao dayOfRoutineDao;

        InsertRoutineWithDaysAsyncTask(RoutineDao routineDao, DayOfRoutineDao dayOfRoutineDao) {
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

    private static class InsertDayOfRoutineAsyncTask extends AsyncTask<DayOfRoutine, Void, Void> {
        private DayOfRoutineDao dayOfRoutineDao;

        InsertDayOfRoutineAsyncTask(DayOfRoutineDao dayOfRoutineDao) {
            this.dayOfRoutineDao = dayOfRoutineDao;
        }


        @Override
        protected Void doInBackground(DayOfRoutine... daysOfRoutine) {
            DayOfRoutine dayOfRoutine = daysOfRoutine[0];
            dayOfRoutineDao.insert(dayOfRoutine);
            return null;
        }
    }

    private static class InsertDayOufRoutineAsExercisesAsyncTask extends AsyncTask<DayOfRoutine, Void, Void> {
        private ExerciseDao exerciseDao;
        private SingleSetDao singleSetDao;

        public InsertDayOufRoutineAsExercisesAsyncTask(ExerciseDao exerciseDao, SingleSetDao singleSetDao) {
            this.exerciseDao = exerciseDao;
            this.singleSetDao = singleSetDao;
        }

        @Override
        protected Void doInBackground(DayOfRoutine... dayOfRoutines) {
            List<ExerciseWithSets> exerciseWithSetsList = dayOfRoutines[0].getExerciseWithSetsList();
            for (ExerciseWithSets exerciseWithSets : exerciseWithSetsList) {
                Exercise exerciseToInsert = Exercise.createNewFromExisting(exerciseWithSets.getExercise());
                long exerciseId = exerciseDao.insert(exerciseToInsert);
                List<SingleSet> singleSetList = exerciseWithSets.getExerciseSetList();
                for (SingleSet ss : singleSetList) {
                    ss.setCorrespondingExerciseId(exerciseId);
                }
                singleSetDao.insertMultiple(singleSetList);

            }
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

    private static class UpdateExerciseWithSetsAsyncTask extends AsyncTask<ExerciseWithSets, Void, Void> {

        private ExerciseDao exerciseDao;
        private SingleSetDao singleSetDao;

        UpdateExerciseWithSetsAsyncTask(ExerciseDao exerciseDao, SingleSetDao singleSetDao) {
            this.exerciseDao = exerciseDao;
            this.singleSetDao = singleSetDao;
        }

        @Override
        protected Void doInBackground(ExerciseWithSets... exerciseWithSets) {
            Exercise exercise = exerciseWithSets[0].getExercise();
            exerciseDao.update(exercise);

            List<SingleSet> singleSetListToAdd = exerciseWithSets[0].getExerciseSetList();
            List<SingleSet> singleSetListToDelete = singleSetDao.getSingleSetsForExercise(exercise.getExerciseId());
            singleSetDao.deleteMultipleSets(singleSetListToDelete);
            singleSetDao.insertMultiple(singleSetListToAdd);
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

    private static class DeleteRoutineAsyncTask extends AsyncTask<Routine, Void, Void> {
        private RoutineDao routineDao;

        public DeleteRoutineAsyncTask(RoutineDao routineDao) {
            this.routineDao = routineDao;
        }

        @Override
        protected Void doInBackground(Routine... routines) {
            routineDao.delete(routines[0]);
            return null;
        }
    }
}
