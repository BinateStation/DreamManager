package rkr.binatestation.dreammanager.fragments.dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import rkr.binatestation.dreammanager.models.DialogType;

/**
 * A simple alert dialog fragment {@link Fragment} subclass.
 */
public class AlertDialogFragment extends DialogFragment {

    private static final String TAG = "AlertDialogFragment";

    private static final String KEY_TITLE = "title";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_DIALOG_TYPE = "dialog_type";

    private String mTitle;
    private String mMessage;
    private DialogInterface.OnClickListener mOnClickListener;

    public AlertDialogFragment() {
        // Required empty public constructor
    }

    public static AlertDialogFragment newInstance(String title, String message, DialogType dialogType) {
        Log.d(TAG, "newInstance() called with: title = [" + title + "], message = [" + message + "], dialogType = [" + dialogType + "]");
        Bundle args = new Bundle();
        args.putString(KEY_TITLE, title);
        args.putString(KEY_MESSAGE, message);
        args.putSerializable(KEY_DIALOG_TYPE, dialogType);
        AlertDialogFragment fragment = new AlertDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogInterface.OnClickListener) {
            mOnClickListener = (DialogInterface.OnClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mOnClickListener = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        String title = bundle.getString(KEY_TITLE);
        String message = bundle.getString(KEY_MESSAGE);
        DialogType dialogType = (DialogType) bundle.getSerializable(KEY_DIALOG_TYPE);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title == null ? getString(android.R.string.dialog_alert_title) : title);
        if (message != null) {
            alertDialogBuilder.setMessage(message);
        }
        if (dialogType == DialogType.POSITIVE_BUTTON) {
            if (mOnClickListener == null) {
                alertDialogBuilder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // on success
                        dismiss();
                    }
                });
            } else {
                alertDialogBuilder.setPositiveButton(getString(android.R.string.yes), mOnClickListener);
            }
        } else if (dialogType == DialogType.POSITIVE_NEGATIVE_BUTTON) {
            if (mOnClickListener == null) {
                alertDialogBuilder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // on success
                        dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
            } else {
                alertDialogBuilder.setPositiveButton(getString(android.R.string.yes), mOnClickListener);
                alertDialogBuilder.setNegativeButton(getString(android.R.string.no), mOnClickListener);
            }
        } else if (dialogType == DialogType.POSITIVE_NEGATIVE_NUTRAL_BUTTON) {
            if (mOnClickListener == null) {
                alertDialogBuilder.setPositiveButton(getString(android.R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // on success
                        dismiss();
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(android.R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
                alertDialogBuilder.setNeutralButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                });
            } else {
                alertDialogBuilder.setPositiveButton(getString(android.R.string.yes), mOnClickListener);
                alertDialogBuilder.setNegativeButton(getString(android.R.string.no), mOnClickListener);
                alertDialogBuilder.setNeutralButton(getString(android.R.string.cancel), mOnClickListener);
            }
        }
        return alertDialogBuilder.create();
    }


}
