package pl.kcworks.simplegymlog.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.List;

import pl.kcworks.simplegymlog.model.db.DataTypeConverter;

@Entity(tableName = "routines_table")
public class Routine implements GymLogListItem {
    @PrimaryKey(autoGenerate = true)
    private int routineId;

    @NonNull
    private String routineName;

    @TypeConverters(DataTypeConverter.class)
    private List<DayOfRoutine> daysOfRoutineList;

    public Routine(int routineId, @NonNull String routineName, List<DayOfRoutine> daysOfRoutineList) {
        this.routineId = routineId;
        this.routineName = routineName;
        this.daysOfRoutineList = daysOfRoutineList;
    }

    @Ignore
    public Routine(@NonNull String routineName, List<DayOfRoutine> daysOfRoutineList) {
        this.routineName = routineName;
        this.daysOfRoutineList = daysOfRoutineList;
    }

    public int getRoutineId() {
        return routineId;
    }

    public void setRoutineId(int routineId) {
        this.routineId = routineId;
    }

    @NonNull
    public String getRoutineName() {
        return routineName;
    }

    public void setRoutineName(@NonNull String routineName) {
        this.routineName = routineName;
    }

    public List<DayOfRoutine> getDaysOfRoutineList() {
        return daysOfRoutineList;
    }

    public void setDaysOfRoutineList(List<DayOfRoutine> daysOfRoutineList) {
        this.daysOfRoutineList = daysOfRoutineList;
    }

    @Override
    public String toString() {
        String result =
                "Routine{" +
                        "routineId=" + routineId +
                        ", routineName='" + routineName + '\'' +
                        ", daysOfRoutineList=" + daysOfRoutineList +
                        '}';

        return result;
    }

    @Override
    public GymLogType getType() {
        return GymLogType.ROUTINE;
    }


}
