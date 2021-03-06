package rkr.binatestation.dreammanager.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import rkr.binatestation.dreammanager.R;
import rkr.binatestation.dreammanager.fragments.dialog.AlertDialogFragment;
import rkr.binatestation.dreammanager.models.DialogType;
import rkr.binatestation.dreammanager.utils.SessionManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, TextWatcher {

    private EditText mPassword1, mPassword2, mPassword3, mPassword4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPassword1 = (EditText) findViewById(R.id.AL_password_1);
        mPassword2 = (EditText) findViewById(R.id.AL_password_2);
        mPassword3 = (EditText) findViewById(R.id.AL_password_3);
        mPassword4 = (EditText) findViewById(R.id.AL_password_4);

        mPassword1.addTextChangedListener(this);
        mPassword2.addTextChangedListener(this);
        mPassword3.addTextChangedListener(this);
        mPassword4.addTextChangedListener(this);

        TextView passwordHintTextView = (TextView) findViewById(R.id.AL_password_hint);
        passwordHintTextView.setText(String.format("%s %s %s", getString(R.string.forgot_password), getEmojiByUnicode(0x1F61E), getString(R.string.help_me)));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.AL_action_sign_up) {
            startActivity(new Intent(this, SignUpActivity.class));
            finish();
        } else if (v.getId() == R.id.AL_password_hint) {
            showPasswordHint();
        }
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    private void showPasswordHint() {
        String passwordHint = SessionManager.getPasswordHint(this);
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(getString(R.string.password_hint), passwordHint, DialogType.POSITIVE_BUTTON);
        alertDialogFragment.show(getSupportFragmentManager(), alertDialogFragment.getTag());

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
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
        if (mPassword1.isFocused()) {
            mPassword2.requestFocus();
        } else if (mPassword2.isFocused()) {
            mPassword3.requestFocus();
        } else if (mPassword3.isFocused()) {
            mPassword4.requestFocus();
        } else {
            validateInput();
        }
    }

    private void validateInput() {
        String input = mPassword1.getText().toString() + mPassword2.getText().toString() + mPassword3.getText().toString() + mPassword4.getText().toString();
        String password = SessionManager.getPassword(this);
        if (password.equals(input)) {
            navigateTo();
        } else {
            showAlert();
        }
    }

    private void showAlert() {
        clearFields();
        AlertDialogFragment alertDialogFragment = AlertDialogFragment.newInstance(null, getString(R.string.password_wrong_error_msg), DialogType.POSITIVE_BUTTON);
        alertDialogFragment.show(getSupportFragmentManager(), alertDialogFragment.getTag());
    }

    private void clearFields() {
        mPassword1.setText("");
        mPassword2.setText("");
        mPassword3.setText("");
        mPassword4.setText("");
    }


    private void navigateTo() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }


}
