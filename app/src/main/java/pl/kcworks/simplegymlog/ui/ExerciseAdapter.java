package pl.kcworks.simplegymlog.ui;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.SingleSet;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private static final String TAG = "KCTag-" + ExerciseAdapter.class.getSimpleName();
    private LayoutInflater mInflater;
    private List<ExerciseWithSets> mExercisesWithSets = Collections.emptyList(); // cached copy of exercises
    private DeleteExerciseCallback callback;

    ExerciseAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    public void setExercises(List<ExerciseWithSets> exercises) {
        mExercisesWithSets = exercises;
        notifyDataSetChanged();
    }

    public List<ExerciseWithSets> getmExercisesWithSets() {
        return mExercisesWithSets;
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = mInflater.inflate(R.layout.item_rv_exercise, viewGroup, false);
        return new ExerciseViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        if (!mExercisesWithSets.isEmpty()) {
            Exercise exercise = mExercisesWithSets.get(position).getExercise();
            List<SingleSet> singleSetList = mExercisesWithSets.get(position).getExerciseSetList();

            holder.exerciseNameTextView.setText(exercise.getExerciseName());
            holder.setListLinearLayout.removeAllViews(); // in case we get recycled view where there are already some sets added

            // populating view with information about each SingleSet
            if (singleSetList.size() != 0) {
                // in case when we get recycled view where noSetsInfoTextView is visible
                holder.noSetsInfoTextView.setVisibility(View.GONE);
                for (SingleSet set : singleSetList) {
                    LinearLayout newSet = (LinearLayout) mInflater.inflate(R.layout.item_rv_set, null);
                    ((TextView) newSet.findViewById(R.id.rvitem_tv_setNumber)).setText(String.format("%d", singleSetList.indexOf(set) + 1));
                    ((TextView) newSet.findViewById(R.id.rvitem_tv_setWeight)).setText("" + set.getWeight());
                    ((TextView) newSet.findViewById(R.id.rvitem_tv_setReps)).setText("" + set.getReps());

                    newSet.setTag(set.getSingleSetID());

                    newSet.setOnClickListener(holder);
                    holder.setListLinearLayout.addView(newSet);
                }
            } else {
                holder.noSetsInfoTextView.setVisibility(View.VISIBLE);
            }

        } else {
            // Covers the case of data not being ready yet.
            holder.exerciseNameTextView.setText("no excersises to show!");
        }
    }


    interface DeleteExerciseCallback{
        void deleteExercise(int idOfExerciseToDelete);
    }

    @Override
    public int getItemCount() {
        if (mExercisesWithSets != null) {
            return mExercisesWithSets.size();
        } else {
            return 0;
        }
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
        private TextView exerciseNameTextView;
        private LinearLayout setListLinearLayout;
        private TextView noSetsInfoTextView;
        private ImageView editExerciseImageView;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);

            exerciseNameTextView = itemView.findViewById(R.id.rvitem_tv_exercise_name);
            setListLinearLayout = itemView.findViewById(R.id.rvitem_ll_sets);
            noSetsInfoTextView = itemView.findViewById(R.id.rvitem_tv_noSetsInfo);
            editExerciseImageView = itemView.findViewById(R.id.rvitem_iv_editExercise);

            editExerciseImageView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "something in adapter was clicked id: " + view.getId());
            // edit exercise button
            if (view.getId() == R.id.rvitem_iv_editExercise) {
                Intent editExerciseIntent = new Intent(view.getContext(), AddExerciseActivity.class);
                editExerciseIntent.putExtra(AddExerciseActivity.UPDATE_EXERCISE_ID_EXTRA, mExercisesWithSets.get(getAdapterPosition()).getExercise().getExerciseId());
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
//            MenuItem delete = contextMenu.add(this.getAdapterPosition(),1, 1, "delete exercise");
            contextMenu.add(this.getAdapterPosition(),1, 1, "delete exercise");
        }
    }

}
