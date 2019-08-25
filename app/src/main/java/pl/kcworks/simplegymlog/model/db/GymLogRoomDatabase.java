package pl.kcworks.simplegymlog.model.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.SingleSet;

@Database(entities = {Routine.class, Exercise.class, SingleSet.class}, version = 1, exportSchema = false)
public abstract class GymLogRoomDatabase extends RoomDatabase {

    private static volatile GymLogRoomDatabase INSTANCE;

    public static GymLogRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (GymLogRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            GymLogRoomDatabase.class, "gymlog_database")
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public abstract RoutineDao routineDao();

    public abstract ExerciseDao exerciseDao();

    public abstract SingleSetDao singleSetDao();


}
