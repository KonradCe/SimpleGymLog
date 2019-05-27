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

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private LayoutInflater mInflater;
    private List<Exercise> mExercises = Collections.emptyList(); // cached copy of exercises

    ExerciseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setExercises(List<Exercise> exercises) {
        mExercises = exercises;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.item_rv_exercise, viewGroup, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        if (mExercises != null) {
            Exercise exercise = mExercises.get(position);
            holder.exerciseNameTextView.setText(exercise.getExerciseName());
        }
        else {
            // Covers the case of data not being ready yet.
            holder.exerciseNameTextView.setText("no excersises to show!");
        }
    }

    @Override
    public int getItemCount() {
        if (mExercises != null) {
            return mExercises.size();
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
