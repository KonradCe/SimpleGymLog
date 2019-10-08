package pl.kcworks.simplegymlog.ui;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import pl.kcworks.simplegymlog.model.RoutineWithDays;
import pl.kcworks.simplegymlog.model.SingleSet;

public class RoutineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "KCTag-" + RoutineAdapter.class.getSimpleName();

    private static final int TYPE_ROUTINE = 0;
    private static final int TYPE_DAY = 1;
    private static final int TYPE_EXERCISE = 2;
    private static final int TYPE_SET = 3;

    private static final int DECORATOR_DIVIDER = 10;
    private static final int DECORATOR_ADD_DAY_BT = 11;
    private static final int DECORATOR_ADD_EXERCISE_BT = 12;

    private AdapterMode adapterMode;
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


    RoutineAdapter(AdapterMode adapterMode) {
        this.adapterMode = adapterMode;
    }

    void setRoutineWithDaysList(List<RoutineWithDays> routineWithDaysList) {
        gymLogItems = flattenRoutineWithDaysList(routineWithDaysList);
        notifyDataSetChanged();
    }

    void setRoutineList(List<Routine> routineList) {
        gymLogItems = flattenRoutineOnlyList(routineList);
        notifyDataSetChanged();
    }

    List<GymLogListItem> getGymLogItems() {
        return gymLogItems;
    }

    private List<GymLogListItem> flattenRoutineOnlyList(List<Routine> routineList) {
        ArrayList<GymLogListItem> list = new ArrayList<>();

        for (Routine routine : routineList) {
            list.add(routine);
            list.add(new RvExtras(GymLogType.RV_DIVIDER));
        }

        return list;

    }

    private List<GymLogListItem> flattenRoutineWithDaysList(List<RoutineWithDays> routineWithDaysList) {
        List<GymLogListItem> flatList = new ArrayList<>();

        for (RoutineWithDays routineWithDays : routineWithDaysList) {
            flatList.add(routineWithDays.getRoutine());
            flatList.addAll(flattenDayList(routineWithDays.getSortedDayOuRoutineList()));

        }

        return flatList;
    }

    private List<GymLogListItem> flattenDayList(List<DayOfRoutine> dayOfRoutineList ) {
        List<GymLogListItem> flatList = new ArrayList<>();
        for (DayOfRoutine dayOfRoutine : dayOfRoutineList) {
            flatList.add(dayOfRoutine);
            flatList.addAll(flattenExerciseWithSetsList(dayOfRoutine.getExerciseWithSetsList(), dayOfRoutine.getDayId()));
            RvExtras addExerciseButtonRvExtra =new RvExtras(GymLogType.RV_ADD_EXERCISE_BT);
            addExerciseButtonRvExtra.setParent(dayOfRoutine);
            flatList.add(addExerciseButtonRvExtra);
            flatList.add(new RvExtras(GymLogType.RV_DIVIDER));
        }
        return flatList;
    }

    private List<GymLogListItem> flattenExerciseWithSetsList (List<ExerciseWithSets> exerciseWithSetsList, int dayId) {
        List<GymLogListItem> flatList = new ArrayList<>();
        for (ExerciseWithSets exerciseWithSets : exerciseWithSetsList) {
            Exercise exercise =exerciseWithSets.getExercise();
            exercise.setExerciseId(dayId);
            exercise.setExerciseOrderInDay(exerciseWithSetsList.indexOf(exerciseWithSets));
            flatList.add(exercise);
            flatList.addAll(exerciseWithSets.getExerciseSetList());
        }
        return flatList;
    }

    private GymLogListItem getParentOfType(int position, GymLogType parentType) {
        GymLogListItem parent = null;
        do {
            position--;
            parent = gymLogItems.get(position);
        }
        while (parent.getType() != parentType);

        return parent;
    }

    void setListener(RoutineAdapterClickListener listener) {
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
            case DECORATOR_ADD_EXERCISE_BT:
                view = inflater.inflate(R.layout.item_rvextra_add_exercise_bt, parent, false);
                return new RvExtrasViewHolder(view);
        }

        // null should never be returned, all cases are (and should be) handled by switch above
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
        if (adapterMode == AdapterMode.SELECT_ROUTINE) {
            holder.editRoutineButton.setVisibility(View.GONE);
            holder.routineRowLinearLayout.setOnClickListener(clickListener);
            holder.routineRowLinearLayout.setTag(routine);
        }
        else if (adapterMode == AdapterMode.EDIT_ROUTINE) {
            holder.editRoutineButton.setOnClickListener(clickListener);
            holder.editRoutineButton.setTag(routine);
        }
    }

    private void onBindDayItem(@NonNull DayViewHolder holder, int position) {
        DayOfRoutine day = (DayOfRoutine) gymLogItems.get(position);
        holder.dayNameTextView.setText(day.getDayName());
        if (adapterMode == AdapterMode.SELECT_ROUTINE) {
            holder.editDayButton.setVisibility(View.GONE);
            holder.dayRowLinearLayout.setOnClickListener(clickListener);
            holder.dayRowLinearLayout.setTag(day);
        }
        else if (adapterMode == AdapterMode.EDIT_ROUTINE) {
            holder.editDayButton.setOnClickListener(clickListener);
            holder.editDayButton.setTag(day);
        }
    }

    private void onBindExerciseItem(@NonNull ExerciseViewHolder holder, int position) {
        Exercise exercise = (Exercise) gymLogItems.get(position);
        holder.exerciseNameTextView.setText(exercise.getExerciseName());
        if (adapterMode == AdapterMode.SELECT_ROUTINE) {
            holder.editExerciseButton.setVisibility(View.GONE);
            holder.exerciseRowLinearLayout.setOnClickListener(clickListener);
            holder.exerciseRowLinearLayout.setTag(getParentOfType(position, GymLogType.DAY));
        }
        else if (adapterMode == AdapterMode.EDIT_ROUTINE) {
            holder.editExerciseButton.setOnClickListener(clickListener);
            holder.editExerciseButton.setTag(exercise);
        }
    }

    private void onBindSetItem(@NonNull SetViewHolder holder, int position) {
        SingleSet set = (SingleSet) gymLogItems.get(position);

        holder.setRepsTextView.setText(Integer.toString(set.getReps()));
        holder.setWeightTextView.setText(Double.toString(set.getWeight()));

        if (set.isBasedOnTm()) {
            String additionalInfo = set.getPercentageOfTm() + "% of " + set.getTrainingMax();
            holder.setAdditionalInfoTextView.setText(additionalInfo);
        }

        if (adapterMode == AdapterMode.SELECT_ROUTINE) {
            holder.setRowLinearLayout.setOnClickListener(clickListener);
            holder.setRowLinearLayout.setTag(getParentOfType(position, GymLogType.DAY));
        }
    }

    @Override
    public int getItemCount() {
        return gymLogItems.size();
    }

    class RoutineViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        private LinearLayout routineRowLinearLayout;
        private TextView routineNameTextView;
        private Button editRoutineButton;

        RoutineViewHolder(@NonNull View itemView) {
            super(itemView);
            routineRowLinearLayout = itemView.findViewById(R.id.ll_routine_row);
            routineNameTextView = itemView.findViewById(R.id.tv_routine_name);
            editRoutineButton = itemView.findViewById(R.id.iv_edit_routine);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(this.getAdapterPosition(),1, 1, view.getContext().getString(R.string.label_delete_routine));
        }

    }

    class DayViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout dayRowLinearLayout;
        private TextView dayNameTextView;
        private Button editDayButton;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayRowLinearLayout = itemView.findViewById(R.id.ll_day_row);
            dayNameTextView = itemView.findViewById(R.id.tv_day_name);
            editDayButton = itemView.findViewById(R.id.iv_edit_day);
        }
    }

    class ExerciseViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout exerciseRowLinearLayout;
        private TextView exerciseNameTextView;
        private Button editExerciseButton;

        ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            exerciseRowLinearLayout = itemView.findViewById(R.id.ll_exercise_row);
            exerciseNameTextView = itemView.findViewById(R.id.tv_exercise_name);
            editExerciseButton = itemView.findViewById(R.id.iv_edit_exercise);
        }
    }

    class SetViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout setRowLinearLayout;
        private TextView setAdditionalInfoTextView;
        private TextView setWeightTextView;
        private TextView setRepsTextView;

        SetViewHolder(@NonNull View itemView) {
            super(itemView);

            TextView setNumberTextView = itemView.findViewById(R.id.set_number);
            setNumberTextView.setVisibility(View.INVISIBLE);

            setAdditionalInfoTextView = itemView.findViewById(R.id.set_additional_info);
            setRowLinearLayout = itemView.findViewById(R.id.ll_set_row);
            setRepsTextView = itemView.findViewById(R.id.set_reps);
            setWeightTextView = itemView.findViewById(R.id.set_weight);
        }
    }

    class RvExtras implements GymLogListItem, ConnectWithParentObject {

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

    private interface ConnectWithParentObject {
        void setParent(GymLogListItem parent);
        GymLogListItem getParent();
    }

    enum AdapterMode{
        SELECT_ROUTINE,
        SELECT_DAY,
        EDIT_ROUTINE
    }
}
