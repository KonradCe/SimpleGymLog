package pl.kcworks.simplegymlog.ui;

import java.util.List;

import pl.kcworks.simplegymlog.model.ExerciseWithSets;

interface AsyncResponse {
    void onTaskCompleted(List<ExerciseWithSets> exerciseWithSets);
}