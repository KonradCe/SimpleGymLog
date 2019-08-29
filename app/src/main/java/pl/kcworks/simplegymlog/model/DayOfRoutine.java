package pl.kcworks.simplegymlog.model;

import java.util.List;

public class DayOfRoutine implements GymLogListItem{
    private String dayName;
    private List<ExerciseWithSets> exerciseWithSetsList;

    public DayOfRoutine(String dayName, List<ExerciseWithSets> exerciseWithSetsList) {
        this.dayName = dayName;
        this.exerciseWithSetsList = exerciseWithSetsList;
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

}