package pl.kcworks.simplegymlog.ui;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.SingleSet;
import pl.kcworks.simplegymlog.model.db.DataTypeConverter;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.ExerciseViewHolder> {

    private static final String TAG = "KCTag-" + WorkoutAdapter.class.getSimpleName();
    private LayoutInflater inflater;
    private List<ExerciseWithSets> exercisesWithSets = Collections.emptyList(); // cached copy of exercises

    WorkoutAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    void setExercises(List<ExerciseWithSets> exercises) {
        exercisesWithSets = exercises;
        notifyDataSetChanged();
    }

    List<ExerciseWithSets> getExercisesWithSets() {
        return exercisesWithSets;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = inflater.inflate(R.layout.item_card_view_exercise, viewGroup, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        if (!exercisesWithSets.isEmpty()) {
            Exercise exercise = exercisesWithSets.get(position).getExercise();
            List<SingleSet> singleSetList = exercisesWithSets.get(position).getExerciseSetList();

            holder.exerciseNameTextView.setText(exercise.getExerciseName());
            holder.setListLinearLayout.removeAllViews(); // in case we get recycled view where there are already some sets added

            // populating view with information about each SingleSet
            if (singleSetList.size() != 0) {
                // in case when we get recycled view where noSetsInfoTextView is visible
                holder.noSetsInfoTextView.setVisibility(View.GONE);
                holder.exerciseTopRow.setVisibility(View.VISIBLE);
                for (SingleSet set : singleSetList) {
                    SetView setView = new SetView(inflater.getContext());
                    setView.setNumber(singleSetList.indexOf(set)  + 1);
                    setView.setWeight(set.getWeight());
                    setView.setReps(set.getReps());

                    if(set.isBasedOnTm()) {
                        setView.setTrainingMax(set.getTrainingMax());
                        setView.setPercentageOfTm(set.getPercentageOfTm());
                    }

                    setView.setTag(set.getSingleSetID());

                    setView.setOnClickListener(holder);
                    holder.setListLinearLayout.addView(setView);
                }
            } else {
                holder.noSetsInfoTextView.setVisibility(View.VISIBLE);
                holder.exerciseTopRow.setVisibility(View.GONE);
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.exerciseNameTextView.setText("no excersises to show!");
        }
    }

    @Override
    public int getItemCount() {
        if (exercisesWithSets != null) {
            return exercisesWithSets.size();
        } else {
            return 0;
        }
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        private TextView exerciseNameTextView;
        private LinearLayout setListLinearLayout;
        private LinearLayout exerciseTopRow;
        private TextView noSetsInfoTextView;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseNameTextView = itemView.findViewById(R.id.rvitem_tv_exercise_name);
            setListLinearLayout = itemView.findViewById(R.id.rvitem_ll_sets);
            exerciseTopRow = itemView.findViewById(R.id.ll_exercise_top_row);
            noSetsInfoTextView = itemView.findViewById(R.id.rvitem_tv_noSetsInfo);
            Button editExerciseButton = itemView.findViewById(R.id.rvitem_iv_editExercise);


            if (!editExerciseButton.hasOnClickListeners()) {
                editExerciseButton.setOnClickListener(this);
            }

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            // edit exercise button
            if (view.getId() == R.id.rvitem_iv_editExercise) {
                ExerciseWithSets exerciseWithSetsToEdit = exercisesWithSets.get(getAdapterPosition());
                Intent editExerciseIntent = new Intent(view.getContext(), AddExerciseActivity.class);

                editExerciseIntent.putExtra(AddExerciseActivity.UPDATE_EXERCISE_EXTRA, DataTypeConverter.exerciseWithSetsToString(exerciseWithSetsToEdit));
                view.getContext().startActivity(editExerciseIntent);
            }
            // TODO[3]: don't know the proper way to access db from here, but this function is not essential at this point
            // mark set as completed
/*            else {
                Toast.makeText(mContext, "Id of SingleSet that was clicked: " + view.getTag(), Toast.LENGTH_LONG).show();
                TextView temp = view.findViewById(R.id.rvitem_tv_setWeight);
                temp.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            }
            */

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            //TODO[3]: export hardcoded string
            contextMenu.add(this.getAdapterPosition(),1, 1, view.getContext().getString(R.string.label_delete_exercise));
        }
    }

}
