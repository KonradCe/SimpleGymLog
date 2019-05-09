package pl.kcworks.simplegymlog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AddExerciseFragment extends Fragment implements View.OnClickListener{
    private Button mAddSetButton;
    private LinearLayout mSetListLinearLayout;

    // variable to keep track and display number of sets added by the user
    private int mSetNumber = 0;

    // mandatory empty constructor for the fragment manager to instantiate the fragment
    public AddExerciseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_exercise, container, false);

        mAddSetButton = rootView.findViewById(R.id.addExerciseFragment_bt_addSet);
        mAddSetButton.setOnClickListener(this);

        mSetListLinearLayout = rootView.findViewById(R.id.addExerciseFragment_ll_listOfSets);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case (R.id.addExerciseFragment_bt_addSet):
                mSetListLinearLayout.addView(addSet());

        }
    }

    private LinearLayout addSet() {
        mSetNumber++;
        LinearLayout setView = (LinearLayout) getLayoutInflater().inflate(R.layout.item_add_set, null);
        TextView setNumber = setView.findViewById(R.id.item_addSet_tv_setNumber);
        setNumber.setText(Integer.toString(mSetNumber));
        return setView;
    }
}
