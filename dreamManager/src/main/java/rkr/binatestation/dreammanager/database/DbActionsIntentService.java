package rkr.binatestation.dreammanager.database;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import rkr.binatestation.dreammanager.models.DreamModel;

/**
 * IntentService used to do db actions in background thread.
 */
public class DbActionsIntentService extends IntentService {
    public static final int RESULT_CODE_SUCCESS = 1;
    public static final int RESULT_CODE_IN_PROGRESS = 2;
    public static final int RESULT_CODE_ERROR = 3;
    public static final String KEY_ERROR_MESSAGE = "error_message";
    public static final String KEY_IN_PROGRESS_MESSAGE = "in_progress_message";
    public static final String KEY_SUCCESS_MESSAGE = "success_message";
    public static final String KEY_INSERT_ID = "insert_id";

    private static final String ACTION_ADD_DREAM = "rkr.binatestation.dreammanager.database.action.ADD_DREAM";

    private static final String EXTRA_PARAM1 = "rkr.binatestation.dreammanager.database.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "rkr.binatestation.dreammanager.database.extra.PARAM2";
    private static final String EXTRA_PARAM3 = "rkr.binatestation.dreammanager.database.extra.PARAM3";
    private static final String EXTRA_PARAM4 = "rkr.binatestation.dreammanager.database.extra.PARAM4";

    private static final String TAG = "DbActionsIntentService";

    private ResultReceiver mResultReceiver;

    public DbActionsIntentService() {
        super("DbActionsIntentService");
    }

    /**
     * Starts this service to perform action Add Dream with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionAddDream(Context context, DreamModel dreamModel, ResultReceiver resultReceiver) {
        Log.d(TAG, "startActionAddDream() called with: context = [" + context + "], dreamModel = [" + dreamModel + "], resultReceiver = [" + resultReceiver + "]");
        Intent intent = new Intent(context, DbActionsIntentService.class);
        intent.setAction(ACTION_ADD_DREAM);
        intent.putExtra(EXTRA_PARAM1, dreamModel);
        intent.putExtra(EXTRA_PARAM2, resultReceiver);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            switch (action) {
                case ACTION_ADD_DREAM: {
                    final DreamModel dreamModel = intent.getParcelableExtra(EXTRA_PARAM1);
                    mResultReceiver = intent.getParcelableExtra(EXTRA_PARAM2);
                    handleActionAddDream(dreamModel);
                }
                break;
            }
        }
    }

    private void sendReceiverData(int resultCode, String key, String message) {
        Log.d(TAG, "sendReceiverData() called with: resultCode = [" + resultCode + "], key = [" + key + "], message = [" + message + "]");
        if (mResultReceiver != null) {
            Bundle bundle = new Bundle();
            bundle.putString(key, message);
            mResultReceiver.send(resultCode, bundle);
        }
    }

    private void sendReceiverData(int resultCode, Bundle bundle) {
        Log.d(TAG, "sendReceiverData() called with: resultCode = [" + resultCode + "], bundle = [" + bundle + "]");
        if (mResultReceiver != null) {
            mResultReceiver.send(resultCode, bundle);
        }
    }

    /**
     * Handle action Save login Data in the provided background thread with the provided
     * parameters.
     *
     * @param dreamModel the row to add
     */
    private void handleActionAddDream(DreamModel dreamModel) {
        Log.d(TAG, "handleActionAddDream() called with: dreamModel = [" + dreamModel + "]");

        if (dreamModel != null) {
            long insertId = DreamModel.insert(getContentResolver(), dreamModel);
            Bundle bundle = new Bundle();
            bundle.putLong(KEY_INSERT_ID, insertId);
            sendReceiverData(RESULT_CODE_SUCCESS, bundle);
        } else {
            sendReceiverData(RESULT_CODE_ERROR, KEY_ERROR_MESSAGE, "Status false");
        }
    }
}
