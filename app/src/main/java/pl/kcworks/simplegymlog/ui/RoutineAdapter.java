package pl.kcworks.simplegymlog.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.kcworks.simplegymlog.R;
import pl.kcworks.simplegymlog.model.DayOfRoutine;
import pl.kcworks.simplegymlog.model.Exercise;
import pl.kcworks.simplegymlog.model.ExerciseWithSets;
import pl.kcworks.simplegymlog.model.GymLogListItem;
import pl.kcworks.simplegymlog.model.GymLogType;
import pl.kcworks.simplegymlog.model.Routine;
import pl.kcworks.simplegymlog.model.SingleSet;

public class RoutineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "KCTag-" + ExerciseAdapter.class.getSimpleName();

    private static final int TYPE_ROUTINE = 0;
    private static final int TYPE_DAY = 1;
    private static final int TYPE_EXERCISE = 2;
    private static final int TYPE_SET = 3;

    private static final int DECORATOR_DIVIDER = 10;
    private static final int DECORATOR_ADD_DAY_BT = 11;
    private static final int DECORATOR_ADD_EXERCISE_BT = 12;

    private List<GymLogListItem> gymLogItems = Collections.emptyList();
    private RoutineAdapterClickListener listener;
    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Object object = view.getTag();
            if (listener != null && object instanceof GymLogListItem) {
                listener.onItemClicked((GymLogListItem) object);
            }
        }
    };


    void setRoutineList(List<Routine> routineList) {
        gymLogItems = flattenRoutineList(routineList);
        notifyDataSetChanged();
    }

    private List<GymLogListItem> flattenRoutineList(List<Routine> routineList) {
        List<GymLogListItem> flatList = new ArrayList<>();

        for (Routine routine : routineList) {
            flatList.add(routine);
            flatList.addAll(flattenDayList(routine.getDaysOfRoutineList()));
            RvExtras addDayButtonRvExtra = new RvExtras(GymLogType.RV_ADD_DAY_BT);
            addDayButtonRvExtra.setParent(routine);
            flatList.add(addDayButtonRvExtra);
            flatList.add(new RvExtras(GymLogType.RV_DIVIDER));
        }

        return flatList;
    }

    private List<GymLogListItem> flattenDayList(List<DayOfRoutine> dayOfRoutineList) {
        List<GymLogListItem> flatList = new ArrayList<>();
        for (DayOfRoutine dayOfRoutine : dayOfRoutineList) {
            flatList.add(dayOfRoutine);
            flatList.addAll(flattenExerciseWithSetsList(dayOfRoutine.getExerciseWithSetsList()));
            RvExtras addExerciseButtonRvExtra =new RvExtras(GymLogType.RV_ADD_EXERCISE_BT);
            addExerciseButtonRvExtra.setParent(dayOfRoutine);
            flatList.add(addExerciseButtonRvExtra);
        }
        return flatList;
    }

    private List<GymLogListItem> flattenExerciseWithSetsList (List<ExerciseWithSets> exerciseWithSetsList) {
        List<GymLogListItem> flatList = new ArrayList<>();
        for (ExerciseWithSets exerciseWithSets : exerciseWithSetsList) {
            flatList.add(exerciseWithSets.getExercise());
            flatList.addAll(exerciseWithSets.getExerciseSetList());
        }
        return flatList;
    }

    public void setListener(RoutineAdapterClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        switch (gymLogItems.get(position).getType() ) {
            case ROUTINE:
                return TYPE_ROUTINE;
            case DAY:
                return TYPE_DAY;
            case EXERCISE:
                return TYPE_EXERCISE;
            case SET:
                return TYPE_SET;
            case RV_DIVIDER:
                return DECORATOR_DIVIDER;
            case RV_ADD_DAY_BT:
                return DECORATOR_ADD_DAY_BT;
            case RV_ADD_EXERCISE_BT:
                return DECORATOR_ADD_EXERCISE_BT;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        switch (viewType) {
            case TYPE_ROUTINE:
                view = inflater.inflate(R.layout.item_rv_routine, parent, false);
                return new RoutineViewHolder(view);
            case TYPE_DAY:
                view = inflater.inflate(R.layout.item_rv_day, parent, false);
                return new DayViewHolder(view);
            case TYPE_EXERCISE:
                view = inflater.inflate(R.layout.item_rv_routineexercise, parent, false);
                return new ExerciseViewHolder(view);
            case TYPE_SET:
                view = inflater.inflate(R.layout.item_rv_routineset, parent, false);
                return new SetViewHolder(view);
            case DECORATOR_DIVIDER:
                view = inflater.inflate(R.layout.item_rvextra_divider, parent, false);
                return new RvExtrasViewHolder(view);
            case DECORATOR_ADD_DAY_BT:
                view = inflater.inflate(R.layout.item_rvextra_add_day_bt, parent, false);
                return new RvExtrasViewHolder(view);
            case DECORATOR_ADD_EXERCISE_BT:
                view = inflater.inflate(R.layout.item_rvextra_add_exercise_bt, parent, false);
                return new RvExtrasViewHolder(view);


        }

        // null should never be returned, all cases should be handled by switch above
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ROUTINE:
                onBindRoutineItem((RoutineViewHolder) holder, position);
                break;

            case TYPE_DAY:
                onBindDayItem((DayViewHolder) holder, position);
                break;

            case TYPE_EXERCISE:
                onBindExerciseItem((ExerciseViewHolder) holder, position);
                break;

            case TYPE_SET:
                onBindSetItem((SetViewHolder) holder, position);
                break;

            case DECORATOR_ADD_DAY_BT:
            case DECORATOR_ADD_EXERCISE_BT:
                RvExtras rvExtras = (RvExtras) gymLogItems.get(position);
                holder.itemView.setOnClickListener(clickListener);
                holder.itemView.setTag(rvExtras);
                break;
        }

    }

    private void onBindRoutineItem(@NonNull RoutineViewHolder holder, int position) {
        Routine routine = ((Routine) gymLogItems.get(position));
        holder.routineNameTextView.setText(routine.getRoutineName());
        holder.editRoutineImageView.setOnClickListener(clickListener);
        holder.editRoutineImageView.setTag(routine);
    }

    private void onBindDayItem(@NonNull DayViewHolder holder, int position) {
        DayOfRoutine day = (DayOfRoutine) gymLogItems.get(position);
        holder.dayNameTextView.setText(day.getDayName());
        holder.editDayImageView.setOnClickListener(clickListener);
        holder.editDayImageView.setTag(day);
    }

    private void onBindExerciseItem(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = (Exercise) gymLogItems.get(position);
        holder.exerciseNameTextView.setText(exercise.getExerciseName());
        holder.editExerciseImageView.setOnClickListener(clickListener);
        holder.editExerciseImageView.setTag(exercise);
    }

    private void onBindSetItem(@NonNull SetViewHolder holder, int position) {
        SingleSet set = (SingleSet) gymLogItems.get(position);
        holder.setRepsTextView.setText(Integer.toString(set.getReps()));
        holder.setWeightTextView.setText(Float.toString(set.getWeight()));
    }

    @Override
    public int getItemCount() {
        return gymLogItems.size();
    }

    class RoutineViewHolder extends RecyclerView.ViewHolder {
            private TextView routineNameTextView;
            private ImageView editRoutineImageView;

        RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            routineNameTextView = itemView.findViewById(R.id.tv_routine_name);
            editRoutineImageView = itemView.findViewById(R.id.iv_edit_routine);
        }

    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private TextView dayNameTextView;
        private ImageView editDayImageView;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayNameTextView = itemView.findViewById(R.id.tv_day_name);
            editDayImageView = itemView.findViewById(R.id.iv_edit_day);
        }
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private TextView exerciseNameTextView;
        private ImageView editExerciseImageView;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseNameTextView = itemView.findViewById(R.id.tv_exercise_name);
            editExerciseImageView = itemView.findViewById(R.id.iv_edit_exercise);
        }
    }

    class SetViewHolder extends RecyclerView.ViewHolder {
        private TextView setRepsTextView;
        private TextView setWeightTextView;

        SetViewHolder(@NonNull View itemView) {
            super(itemView);
            setRepsTextView = itemView.findViewById(R.id.tv_set_reps);
            setWeightTextView = itemView.findViewById(pl.kcworks.simplegymlog.R.id.tv_set_weight);
        }
    }


    private class RvExtras implements GymLogListItem, ConnectButtonWithObject{

        GymLogType type;
        GymLogListItem correspondingObject;

        RvExtras(GymLogType type) {
            this.type = type;
        }

        @Override
        public GymLogType getType() {
            return type;
        }

        @Override
        public void setParent(GymLogListItem parent) {
            correspondingObject = parent;
        }

        @Override
        public GymLogListItem getParent() {
            return correspondingObject;
        }
    }

    class RvExtrasViewHolder extends RecyclerView.ViewHolder {
        RvExtrasViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface RoutineAdapterClickListener {
        void onItemClicked(GymLogListItem clickedView);
    }

    private interface ConnectButtonWithObject {

        void setParent(GymLogListItem parent);
        GymLogListItem getParent();
    }


}
