package rkr.binatestation.dreammanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import rkr.binatestation.dreammanager.R;
import rkr.binatestation.dreammanager.utils.GeneralUtils;
import rkr.binatestation.dreammanager.utils.SessionManager;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {

    private static final String TAG = "SignUpActivity";

    private EditText mNameEditText;
    private EditText mMobileNumberEditText;
    private EditText mPassword1EditText;
    private EditText mPassword2EditText;
    private EditText mPassword3EditText;
    private EditText mPassword4EditText;
    private EditText mConfirmPassword1EditText;
    private EditText mConfirmPassword2EditText;
    private EditText mConfirmPassword3EditText;
    private EditText mConfirmPassword4EditText;
    private EditText mPasswordHintEditText;

    private TextInputLayout mNameTextInputLayout;
    private TextInputLayout mMobileNumberTextInputLayout;
    private TextInputLayout mPasswordHintTextInputLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mNameEditText = (EditText) findViewById(R.id.ASU_name);
        mMobileNumberEditText = (EditText) findViewById(R.id.ASU_mobile_number);
        mPassword1EditText = (EditText) findViewById(R.id.ASU_password_1);
        mPassword2EditText = (EditText) findViewById(R.id.ASU_password_2);
        mPassword3EditText = (EditText) findViewById(R.id.ASU_password_3);
        mPassword4EditText = (EditText) findViewById(R.id.ASU_password_4);
        mConfirmPassword1EditText = (EditText) findViewById(R.id.ASU_confirm_password_1);
        mConfirmPassword2EditText = (EditText) findViewById(R.id.ASU_confirm_password_2);
        mConfirmPassword3EditText = (EditText) findViewById(R.id.ASU_confirm_password_3);
        mConfirmPassword4EditText = (EditText) findViewById(R.id.ASU_confirm_password_4);
        mPasswordHintEditText = (EditText) findViewById(R.id.ASU_password_hint);
        mNameTextInputLayout = (TextInputLayout) findViewById(R.id.ASU_name_layout);
        mMobileNumberTextInputLayout = (TextInputLayout) findViewById(R.id.ASU_Mobile_number_layout);
        mPasswordHintTextInputLayout = (TextInputLayout) findViewById(R.id.ASU_password_hint_layout);

        mPassword1EditText.addTextChangedListener(this);
        mPassword2EditText.addTextChangedListener(this);
        mPassword3EditText.addTextChangedListener(this);
        mPassword4EditText.addTextChangedListener(this);
        mConfirmPassword1EditText.addTextChangedListener(this);
        mConfirmPassword2EditText.addTextChangedListener(this);
        mConfirmPassword3EditText.addTextChangedListener(this);
        mConfirmPassword4EditText.addTextChangedListener(this);

        mNameEditText.setOnEditorActionListener(this);
        mMobileNumberEditText.setOnEditorActionListener(this);
        mPassword1EditText.setOnEditorActionListener(this);
        mPassword2EditText.setOnEditorActionListener(this);
        mPassword3EditText.setOnEditorActionListener(this);
        mPassword4EditText.setOnEditorActionListener(this);
        mConfirmPassword1EditText.setOnEditorActionListener(this);
        mConfirmPassword2EditText.setOnEditorActionListener(this);
        mConfirmPassword3EditText.setOnEditorActionListener(this);
        mConfirmPassword4EditText.setOnEditorActionListener(this);

        mNameEditText.addTextChangedListener(this);
        mMobileNumberEditText.addTextChangedListener(this);
        mPasswordHintEditText.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        clearErrorMsg();
    }

    private void clearErrorMsg() {
        mNameTextInputLayout.setErrorEnabled(false);
        mMobileNumberTextInputLayout.setErrorEnabled(false);
        mPasswordHintTextInputLayout.setErrorEnabled(false);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() > 0) {
            jumpView();
        }
    }


    private void jumpView() {
        Log.d(TAG, "jumpView() called");
        if (mPassword1EditText.isFocused()) {
            mPassword2EditText.requestFocus();
        } else if (mPassword2EditText.isFocused()) {
            mPassword3EditText.requestFocus();
        } else if (mPassword3EditText.isFocused()) {
            mPassword4EditText.requestFocus();
        } else if (mPassword4EditText.isFocused()) {
            mConfirmPassword1EditText.requestFocus();
        } else if (mConfirmPassword1EditText.isFocused()) {
            mConfirmPassword2EditText.requestFocus();
        } else if (mConfirmPassword2EditText.isFocused()) {
            mConfirmPassword3EditText.requestFocus();
        } else if (mConfirmPassword3EditText.isFocused()) {
            mConfirmPassword4EditText.requestFocus();
        } else if (mConfirmPassword4EditText.isFocused()) {
            mPasswordHintEditText.requestFocus();
        }
    }

    private void validateInput() {
        Log.d(TAG, "validateInput() called");
        String name = mNameEditText.getText().toString().trim();
        String mobileNumber = mMobileNumberEditText.getText().toString().trim();
        String password = mPassword1EditText.getText().toString().trim() + mPassword2EditText.getText().toString().trim() + mPassword3EditText.getText().toString().trim() + mPassword4EditText.getText().toString().trim();
        String confirmPassword = mConfirmPassword1EditText.getText().toString().trim() + mConfirmPassword2EditText.getText().toString().trim() + mConfirmPassword3EditText.getText().toString().trim() + mConfirmPassword4EditText.getText().toString().trim();
        String passwordHint = mPasswordHintEditText.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            mNameTextInputLayout.setError(getString(R.string.name_empty_error_msg));
            mNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(mobileNumber)) {
            mMobileNumberTextInputLayout.setError(getString(R.string.mobile_number_empty_error_msg));
            mMobileNumberEditText.requestFocus();
        } else if (TextUtils.isEmpty(mPassword1EditText.getText().toString())) {
            mPassword1EditText.setError(getString(R.string.password_empty_error_msg));
            mPassword1EditText.requestFocus();
        } else if (TextUtils.isEmpty(mPassword2EditText.getText().toString())) {
            mPassword2EditText.setError(getString(R.string.password_empty_error_msg));
            mPassword2EditText.requestFocus();
        } else if (TextUtils.isEmpty(mPassword3EditText.getText().toString())) {
            mPassword3EditText.setError(getString(R.string.password_empty_error_msg));
            mPassword3EditText.requestFocus();
        } else if (TextUtils.isEmpty(mPassword4EditText.getText().toString())) {
            mPassword4EditText.setError(getString(R.string.password_empty_error_msg));
            mPassword4EditText.requestFocus();
        } else if (TextUtils.isEmpty(mConfirmPassword1EditText.getText().toString())) {
            mConfirmPassword1EditText.setError(getString(R.string.password_empty_error_msg));
            mConfirmPassword1EditText.requestFocus();
        } else if (TextUtils.isEmpty(mConfirmPassword2EditText.getText().toString())) {
            mConfirmPassword2EditText.setError(getString(R.string.password_empty_error_msg));
            mConfirmPassword2EditText.requestFocus();
        } else if (TextUtils.isEmpty(mConfirmPassword3EditText.getText().toString())) {
            mConfirmPassword3EditText.setError(getString(R.string.password_empty_error_msg));
            mConfirmPassword3EditText.requestFocus();
        } else if (TextUtils.isEmpty(mConfirmPassword4EditText.getText().toString())) {
            mConfirmPassword4EditText.setError(getString(R.string.password_empty_error_msg));
            mConfirmPassword4EditText.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            GeneralUtils.showAlert(this, getString(android.R.string.dialog_alert_title), getString(R.string.password_mismatch_error_msg));
            mPassword1EditText.setText("");
            mPassword2EditText.setText("");
            mPassword3EditText.setText("");
            mPassword4EditText.setText("");
            mConfirmPassword1EditText.setText("");
            mConfirmPassword2EditText.setText("");
            mConfirmPassword3EditText.setText("");
            mConfirmPassword4EditText.setText("");
            mPassword1EditText.requestFocus();
        } else if (TextUtils.isEmpty(passwordHint)) {
            mPasswordHintTextInputLayout.setError(getString(R.string.password_empty_error_msg));
            mPasswordHintEditText.requestFocus();
        } else {
            saveLoginDetails(name, mobileNumber, password, passwordHint);
        }
    }

    private void saveLoginDetails(String name, String mobileNumber, String password, String passwordHint) {
        Log.d(TAG, "saveLoginDetails() called with: name = [" + name + "], mobileNumber = [" + mobileNumber + "], password = [" + password + "], passwordHint = [" + passwordHint + "]");
        SessionManager.signUp(this, name, mobileNumber, password, passwordHint);
        navigateTo();
    }

    private void navigateTo() {
        Log.d(TAG, "navigateTo() called");
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ASU_action_sign_up) {
            validateInput();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_NEXT) {
            switch (v.getId()) {
                case R.id.ASU_name:
                    mMobileNumberEditText.requestFocus();
                    return true;
                case R.id.ASU_mobile_number:
                    mPassword1EditText.requestFocus();
                    return true;
                case R.id.ASU_password_1:
                    mPassword2EditText.requestFocus();
                    return true;
                case R.id.ASU_password_2:
                    mPassword3EditText.requestFocus();
                    return true;
                case R.id.ASU_password_3:
                    mPassword4EditText.requestFocus();
                    return true;
                case R.id.ASU_password_4:
                    mConfirmPassword1EditText.requestFocus();
                    return true;
                case R.id.ASU_confirm_password_1:
                    mConfirmPassword2EditText.requestFocus();
                    return true;
                case R.id.ASU_confirm_password_2:
                    mConfirmPassword3EditText.requestFocus();
                    return true;
                case R.id.ASU_confirm_password_3:
                    mConfirmPassword4EditText.requestFocus();
                    return true;
                case R.id.ASU_confirm_password_4:
                    mPasswordHintEditText.requestFocus();
                    return true;
            }
        }
        return false;
    }
}
