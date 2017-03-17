package rkr.binatestation.dreammanager.fragments.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import rkr.binatestation.dreammanager.R;

/**
 * Dialog fragment to add the amount
 */
public class AddAmountFragment extends DialogFragment implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = "AddAmountFragment";

    private AddAmountListener mAddAmountListener;

    private EditText mAmountEditText;

    public AddAmountFragment() {
        // Required empty public constructor
    }

    public static AddAmountFragment newInstance() {
        Log.d(TAG, "newInstance() called");
        Bundle args = new Bundle();

        AddAmountFragment fragment = new AddAmountFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        // request a window without the title
        Window window = dialog.getWindow();
        if (window != null) {
            window.requestFeature(Window.FEATURE_NO_TITLE);
        }
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AddAmountListener) {
            mAddAmountListener = (AddAmountListener) context;
        }
    }

    @Override
    public void onDetach() {
        mAddAmountListener = null;
        super.onDetach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_amount, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAmountEditText = (EditText) view.findViewById(R.id.FAM_input_box);
        View actionDoneView = view.findViewById(R.id.FAM_action_done);
        actionDoneView.setOnClickListener(this);
        mAmountEditText.setOnEditorActionListener(this);
    }

    @Override
    public void onClick(View v) {
        validateInput();
    }

    private void validateInput() {
        String amountString = mAmountEditText.getText().toString();
        double amount = 0;
        if (!TextUtils.isEmpty(amountString) || TextUtils.isDigitsOnly(amountString)) {
            amount = Double.parseDouble(amountString);
        }
        if (mAddAmountListener != null) {
            mAddAmountListener.done(amount);
        }
        dismiss();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            validateInput();
        }
        return false;
    }

    public interface AddAmountListener {
        void done(double amount);
    }
}
