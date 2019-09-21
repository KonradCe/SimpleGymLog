package pl.kcworks.simplegymlog.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

import pl.kcworks.simplegymlog.model.db.DataTypeConverter;

@Entity
public class DayOfRoutine implements GymLogListItem, Comparable<DayOfRoutine>{

    @PrimaryKey(autoGenerate = true)
    private int dayId;
    private long parentRoutineId;
    private String dayName;

    @TypeConverters(DataTypeConverter.class)
    private List<ExerciseWithSets> exerciseWithSetsList;

    public DayOfRoutine(int dayId, long parentRoutineId, String dayName, List<ExerciseWithSets> exerciseWithSetsList) {
        this.dayId = dayId;
        this.parentRoutineId = parentRoutineId;
        this.dayName = dayName;
        this.exerciseWithSetsList = exerciseWithSetsList;
    }

    @Ignore
    public DayOfRoutine(String dayName, List<ExerciseWithSets> exerciseWithSetsList) {
        this.dayName = dayName;
        this.exerciseWithSetsList = exerciseWithSetsList;
    }

    public static List<DayOfRoutine> createEmpty() {
        return new ArrayList<DayOfRoutine>();
    }

    public int getDayId() {
        return dayId;
    }

    public void setDayId(int dayId) {
        this.dayId = dayId;
    }

    public long getParentRoutineId() {
        return parentRoutineId;
    }

    public void setParentRoutineId(long parentRoutineId) {
        this.parentRoutineId = parentRoutineId;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public List<ExerciseWithSets> getExerciseWithSetsList() {
        return exerciseWithSetsList;
    }

    public void setExerciseWithSetsList(List<ExerciseWithSets> exerciseWithSetsList) {
        this.exerciseWithSetsList = exerciseWithSetsList;
    }

    @Override
    public String toString() {
        return "DayOfRoutine{" +
                "dayName='" + dayName + '\'' +
                ", exerciseWithSetsList=" + exerciseWithSetsList +
                '}';
    }

    @Override
    public GymLogType getType() {
        return GymLogType.DAY;
    }


    @Override
    public int compareTo(DayOfRoutine dayOfRoutine) {
        return dayName.compareTo(dayOfRoutine.getDayName());
    }
}