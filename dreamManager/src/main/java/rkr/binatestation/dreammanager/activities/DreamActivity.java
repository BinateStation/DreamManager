package rkr.binatestation.dreammanager.activities;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

import rkr.binatestation.dreammanager.R;
import rkr.binatestation.dreammanager.database.DbActionsIntentService;
import rkr.binatestation.dreammanager.fragments.dialog.AddAmountFragment;
import rkr.binatestation.dreammanager.fragments.dialog.AlertDialogFragment;
import rkr.binatestation.dreammanager.fragments.dialog.ImagePickerFragment;
import rkr.binatestation.dreammanager.models.DialogType;
import rkr.binatestation.dreammanager.models.DreamModel;

import static rkr.binatestation.dreammanager.database.DbActionsIntentService.RESULT_CODE_SUCCESS;
import static rkr.binatestation.dreammanager.utils.Constants.KEY_DREAM_MODEL;
import static rkr.binatestation.dreammanager.utils.GeneralUtils.monthsBetweenDates;
import static rkr.binatestation.dreammanager.utils.GeneralUtils.showAlert;

public class DreamActivity extends AppCompatActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, DatePickerDialog.OnDateSetListener,
        DialogInterface.OnClickListener, ImagePickerFragment.ImagePickerListener, AddAmountFragment.AddAmountListener {

    private static final String TAG = "DreamActivity";
    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    private AppCompatImageView mDreamImageView;
    private EditText mDreamNameEditText;
    private EditText mDateToAchieveEditText;
    private EditText mAmountEditText;
    private EditText mAmountSpentTillDayEditText;
    private TextView mAmountIconTextView;
    private ProgressBar mProgressBar;

    private DreamModel mDreamModel = new DreamModel();
    private Handler mHandler = new Handler();

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_DREAM_MODEL, mDreamModel);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dream);
        Toolbar toolbar = (Toolbar) findViewById(R.id.AD_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (getIntent().hasExtra(KEY_DREAM_MODEL)) {
            mDreamModel = getIntent().getParcelableExtra(KEY_DREAM_MODEL);
        }
        mDreamImageView = (AppCompatImageView) findViewById(R.id.AD_dream_image);
        mDreamNameEditText = (EditText) findViewById(R.id.AD_dream_name);
        mDateToAchieveEditText = (EditText) findViewById(R.id.AD_date);
        mAmountEditText = (EditText) findViewById(R.id.AD_amount);
        mAmountSpentTillDayEditText = (EditText) findViewById(R.id.AD_add_money);
        mAmountIconTextView = (TextView) findViewById(R.id.AD_amount_icon);
        mProgressBar = (ProgressBar) findViewById(R.id.AD_progress_bar);

        mDreamNameEditText.setOnEditorActionListener(this);
        mAmountEditText.setOnEditorActionListener(this);
        mDreamImageView.setOnClickListener(this);

        if (savedInstanceState != null) {
            mDreamModel = savedInstanceState.getParcelable(KEY_DREAM_MODEL);
            setImageView();
        }

        setCurrencySymbol();
        setView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDreamModel.getId() == 0) {
            return super.onCreateOptionsMenu(menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.MD_action_delete) {
            deleteConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteConfirmation() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                        getString(android.R.string.dialog_alert_title),
                        getString(R.string.delete_confirmation_msg),
                        DialogType.POSITIVE_NEGATIVE_BUTTON
                );
                alertDialogFragment.show(getSupportFragmentManager(), alertDialogFragment.getTag());
                alertDialogFragment.setOnClickListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            actionDelete();
                        }
                    }
                });
            }
        });
    }

    private void actionDelete() {
        File file = new File(mDreamModel.getImagePath());
        DreamModel.delete(getContentResolver(), mDreamModel.getId());
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                Log.d(TAG, "actionDelete: File Deleted !");
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            onNavigateUp();
        } else {
            finish();
        }
    }

    private void setView() {
        if (mDreamModel.getId() != 0) {
            mDreamNameEditText.setText(mDreamModel.getName());
            mDateToAchieveEditText.setText(String.format(Locale.getDefault(), "On %s", DateUtils.formatDateTime(
                    this,
                    mDreamModel.getAchieveDate(),
                    DateUtils.FORMAT_NO_MONTH_DAY)
            ));
            mAmountEditText.setText(String.format(Locale.getDefault(), "%.2f", mDreamModel.getTargetAmount()));
            mAmountSpentTillDayEditText.setText(String.format(Locale.getDefault(), "%.2f", mDreamModel.getAmountSpentTillDay()));
            setImageView();
        }
    }

    private void setCurrencySymbol() {
        Currency currency = Currency.getInstance(Locale.getDefault());
        mAmountIconTextView.setText(currency.getSymbol());
    }

    @Override
    public void onClick(View v) {
        makeViewEditable(v);
        if (v.getId() == R.id.AD_date || v.getId() == R.id.AD_date_layout) {
            showDatePicker();
        } else if (v.getId() == R.id.AD_dream_image) {
            checkPermissionBeforePickImage();
        } else if (v.getId() == R.id.AD_action_done) {
            showProgress();
            validateInputs();
        } else if (v.getId() == R.id.AD_action_add_money) {
            showAddAmount();
        }
    }

    private void showProgress() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    private void showAddAmount() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AddAmountFragment addAmountFragment = AddAmountFragment.newInstance();
                addAmountFragment.show(getSupportFragmentManager(), addAmountFragment.getTag());
            }
        });
    }

    private void validateInputs() {
        calculateAmountToSpentPerMonth();
        String name = mDreamNameEditText.getText().toString();
        String date = mDateToAchieveEditText.getText().toString();
        String amount = mAmountEditText.getText().toString();
        if (TextUtils.isEmpty(name)) {
            mDreamNameEditText.setError(getString(R.string.name_your_dream));
            mDreamNameEditText.requestFocus();
            hideProgress();
        } else if (TextUtils.isEmpty(date)) {
            mDateToAchieveEditText.setError(getString(R.string.when_you_are_going_to_achieve_this_dream));
            mDateToAchieveEditText.requestFocus();
            hideProgress();
        } else if (TextUtils.isEmpty(amount)) {
            mAmountEditText.setError(getString(R.string.how_much_it_costs_to_achieve_this_dream));
            mAmountEditText.requestFocus();
            hideProgress();
        } else {
            mDreamModel.setName(name);
            saveDream();
        }
    }

    private void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
    }

    private void saveDream() {
        DbActionsIntentService.startActionAddDream(this, mDreamModel, new ResultReceiver(new Handler()) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                super.onReceiveResult(resultCode, resultData);
                if (resultCode == RESULT_CODE_SUCCESS) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        onNavigateUp();
                    } else {
                        finish();
                    }
                }
                hideProgress();
            }
        });
    }

    private void makeViewEditable(View v) {
        mDreamNameEditText.setFocusable(true);
        mDreamNameEditText.setFocusableInTouchMode(true);
        mAmountEditText.setFocusable(true);
        mAmountEditText.setFocusableInTouchMode(true);
        switch (v.getId()) {
            case R.id.AD_name_layout:
            case R.id.AD_dream_name:
                mDreamNameEditText.requestFocus();
                break;
            case R.id.AD_amount:
                mAmountEditText.requestFocus();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT && v.getId() == R.id.AD_dream_name) {
            showDatePicker();
        } else if (actionId == EditorInfo.IME_ACTION_DONE && v.getId() == R.id.AD_amount) {
            calculateAmountToSpentPerMonth();
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
            return true;
        }
        return false;
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void calculateAmountToSpentPerMonth() {
        Log.d(TAG, "calculateAmountToSpentPerMonth() called");
        String amount = mAmountEditText.getText().toString();
        double amountDouble = 0;
        if ((!TextUtils.isEmpty(amount))) {
            amountDouble = Double.parseDouble(amount);
        }
        if (mDreamModel.getAchieveDate() > 0 && amountDouble > 0) {
            mDreamModel.setTargetAmount(amountDouble);
            int noOfMonths = monthsBetweenDates(new Date(mDreamModel.getCreatedDate()), new Date(mDreamModel.getAchieveDate()));
            mDreamModel.setPerMonthAmount(amountDouble / noOfMonths);
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        mDreamModel.setAchieveDate(calendar.getTimeInMillis());
        mDateToAchieveEditText.setText(String.format(Locale.getDefault(), "On %s", DateUtils.formatDateTime(
                this,
                mDreamModel.getAchieveDate(),
                DateUtils.FORMAT_NO_MONTH_DAY)
        ));
        mAmountEditText.requestFocus();
        calculateAmountToSpentPerMonth();
    }

    private void showImagePicker() {
        Log.d(TAG, "showImagePicker() called");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                ImagePickerFragment imagePickerFragment = ImagePickerFragment.newInstance();
                imagePickerFragment.show(getSupportFragmentManager(), imagePickerFragment.getTag());
            }
        });
    }

    private void setImageView() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(mDreamModel.getImagePath());
                int width = mDreamImageView.getWidth();
                int height = mDreamImageView.getHeight();
                if (width > 0 && height > 0) {
                    Bitmap resized = ThumbnailUtils.extractThumbnail(bitmap, width, height);
                    mDreamImageView.setImageBitmap(resized);
                    if (resized == null) {
                        mDreamImageView.setImageDrawable(ContextCompat.getDrawable(mDreamImageView.getContext(), R.drawable.ic_add_a_photo_white_24dp));
                    }
                }
            }
        });
    }

    private void checkPermissionBeforePickImage() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showAlertForPermission();
            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            showImagePicker();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                showImagePicker();
            } else {
                showAlertForDeny();
            }
        }
    }

    private void showAlertForDeny() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showAlert(
                        DreamActivity.this,
                        getString(android.R.string.dialog_alert_title),
                        getString(R.string.external_storage_permission_alert_msg)
                );
            }
        });
    }

    private void showAlertForPermission() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(null, getString(R.string.external_storage_permission_alert_msg), DialogType.POSITIVE_NEGATIVE_BUTTON);
                alertDialogFragment.show(getSupportFragmentManager(), alertDialogFragment.getTag());
            }
        });
    }


    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
            ActivityCompat.requestPermissions(DreamActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

        }
        dialog.dismiss();
    }

    @Override
    public void onImagePicked(String imageFilePath) {
        if (!TextUtils.isEmpty(imageFilePath)) {
            File file = new File(imageFilePath);
            if (file.exists() && file.isFile()) {
                mDreamModel.setImagePath(imageFilePath);
                setImageView();
            }
        } else {
            Log.e(TAG, "onPickResult: " + imageFilePath);
        }

    }

    @Override
    public void done(double amount) {
        mDreamModel.setAmountSpentTillDay(mDreamModel.getAmountSpentTillDay() + amount);
        mAmountSpentTillDayEditText.setText(String.format(Locale.getDefault(), "%.2f", mDreamModel.getAmountSpentTillDay()));
    }
}
