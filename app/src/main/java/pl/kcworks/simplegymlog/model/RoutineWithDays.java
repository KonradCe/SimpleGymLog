package pl.kcworks.simplegymlog.model;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import java.util.Collections;
import java.util.List;

public class RoutineWithDays implements GymLogListItem{

    @Embedded
    private Routine routine;

    @Relation(parentColumn = "routineId", entityColumn = "parentRoutineId")
    private List<DayOfRoutine> dayOfRoutineList;

    public RoutineWithDays() {
    }

    @Ignore
    public RoutineWithDays(Routine routine, List<DayOfRoutine> dayOfRoutineList) {
        this.routine = routine;
        this.dayOfRoutineList = dayOfRoutineList;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public List<DayOfRoutine> getDayOfRoutineList() {
        return dayOfRoutineList;
    }

    public List<DayOfRoutine> getSortedDayOuRoutineList() {
        Collections.sort(dayOfRoutineList);
        return dayOfRoutineList;
    }

    public void setDayOfRoutineList(List<DayOfRoutine> dayOfRoutineList) {
        this.dayOfRoutineList = dayOfRoutineList;
    }

    @Override
    public GymLogType getType() {
        return GymLogType.ROUTINE;
    }
}
