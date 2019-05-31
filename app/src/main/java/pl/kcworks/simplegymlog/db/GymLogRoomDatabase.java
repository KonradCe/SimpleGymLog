package pl.kcworks.simplegymlog.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Exercise.class, SingleSet.class}, version = 1)
public abstract class GymLogRoomDatabase extends RoomDatabase {

    private static volatile GymLogRoomDatabase INSTANCE;

    public abstract ExerciseDao exerciseDao();
    public abstract SingleSetDao singleSetDao();

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



}
