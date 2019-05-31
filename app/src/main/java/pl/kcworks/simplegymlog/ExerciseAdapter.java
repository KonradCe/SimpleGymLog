package pl.kcworks.simplegymlog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import pl.kcworks.simplegymlog.db.Exercise;
import pl.kcworks.simplegymlog.db.ExerciseWithSets;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private LayoutInflater mInflater;
    private List<ExerciseWithSets> mExercisesWithSets = Collections.emptyList(); // cached copy of exercises

    ExerciseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setExercises(List<ExerciseWithSets> exercises) {
        mExercisesWithSets = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.item_rv_exercise, viewGroup, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        if (mExercisesWithSets != null) {
            Exercise exercise = mExercisesWithSets.get(position).getExercise();
            String exerciseName;
            exerciseName = exercise.getExerciseName();
            exerciseName += " number of sets: ";
            exerciseName += mExercisesWithSets.get(position).getExerciseSetList().size();
            holder.exerciseNameTextView.setText(exerciseName);
        }
        else {
            // Covers the case of data not being ready yet.
            holder.exerciseNameTextView.setText("no excersises to show!");
        }
    }

    @Override
    public int getItemCount() {
        if (mExercisesWithSets != null) {
            return mExercisesWithSets.size();
        }
        else {
            return 0;
        }
    }

    class ExerciseViewHolder  extends RecyclerView.ViewHolder {
        private TextView exerciseNameTextView;
        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseNameTextView = itemView.findViewById(R.id.rvitem_tv_exercise_name);
        }
    }
}
