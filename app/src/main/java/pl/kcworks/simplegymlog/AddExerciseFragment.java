package pl.kcworks.simplegymlog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddExerciseFragment extends Fragment implements View.OnClickListener {
    private Button mAddSetButton;
    private Button mRemoveSetButton;
    private Button mSaveExerciseButton;
    private LinearLayout mSetListLinearLayout;
    private EditText mExerciseNameEditText;
    private EditText mExerciseDateEditText;

    // variable to keep track and display number of sets added by the user
    private int mSetNumber = 0;

    // mandatory empty constructor for the fragment manager to instantiate the fragment
    public AddExerciseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_exercise, container, false);

        // setting up views
        mAddSetButton = rootView.findViewById(R.id.addExerciseFragment_bt_addSet);
        mAddSetButton.setOnClickListener(this);

        mRemoveSetButton = rootView.findViewById(R.id.addExerciseFragment_bt_deleteSet);
        mRemoveSetButton.setOnClickListener(this);

        mSaveExerciseButton = rootView.findViewById(R.id.addExerciseFragment_bt_saveExercise);
        mSaveExerciseButton.setOnClickListener(this);

        mSetListLinearLayout = rootView.findViewById(R.id.addExerciseFragment_ll_listOfSets);

        mExerciseNameEditText = rootView.findViewById(R.id.addExerciseFragment_et_exerciseName);

        mExerciseDateEditText = rootView.findViewById(R.id.addExerciseFragment_et_exerciseDate);


        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.addExerciseFragment_bt_addSet):
                mSetListLinearLayout.addView(addSet());
                break;
            case (R.id.addExerciseFragment_bt_deleteSet):
                removeSet();
                break;
            case (R.id.addExerciseFragment_bt_saveExercise):
                saveExercise();
                break;

        }
    }

    private LinearLayout addSet() {
        mSetNumber++;
        LinearLayout setView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_set, null);
        TextView setNumber = setView.findViewById(R.id.item_addSet_tv_setNumber);
        setNumber.setText(Integer.toString(mSetNumber));
        setView.setTag(mSetNumber);
        return setView;
    }

    private void removeSet() {
        View setToRemove = mSetListLinearLayout.findViewWithTag(mSetNumber);
        if (setToRemove != null) {
            mSetListLinearLayout.removeView(setToRemove);
            mSetNumber--;
        }
    }

    private void onSaveButtonPress() {
        saveExercise();

    }
    private void saveExercise() {
        String exerciseName = mExerciseNameEditText.getText().toString();
        long exerciseDate = Long.parseLong(mExerciseDateEditText.getText().toString());
        // TODO[1]: this value should be passed with starting this activity, 2 is a temporrary value
        int exerciseOrderInDay = 2;

        //TODO[1]: add adding sets to database
//        for (int i = 0; i < mSetListLinearLayout.getChildCount(); i++) {
//            View set = mSetListLinearLayout.getChildAt(i);
//
//        }

        Exercise exercise = new Exercise(exerciseName, exerciseOrderInDay, exerciseDate);

    }
}
