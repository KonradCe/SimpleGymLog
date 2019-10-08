package pl.kcworks.simplegymlog.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "routines_table")
public class Routine implements GymLogListItem {
    @PrimaryKey(autoGenerate = true)
    private int routineId;

    @NonNull
    private String routineName;

    public Routine(int routineId, @NonNull String routineName) {
        this.routineId = routineId;
        this.routineName = routineName;
    }

    @Ignore
    public Routine(@NonNull String routineName) {
        this.routineName = routineName;
    }

    static Routine createEmpty() {
        return new Routine("");
    }

    public int getRoutineId() {
        return routineId;
    }

    @NonNull
    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(@NonNull String routineName) {
        this.routineName = routineName;
    }

    @Override
    public GymLogType getType() {
        return GymLogType.ROUTINE;
    }

}
