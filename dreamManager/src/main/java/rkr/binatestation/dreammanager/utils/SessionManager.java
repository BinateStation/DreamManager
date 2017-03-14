package rkr.binatestation.dreammanager.utils;

import android.content.Context;

/**
 * SessionManager includes function to manage sessions
 */

public class SessionManager {

    private static final String PREF_NAME = "DreamManagerSharedPreferences";
    private static final String KEY_NAME = "name";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_PASSWORD_HINT = "password_hint";

    /**
     * creates login session for offline mode
     *
     * @param context      the context from where the session called
     * @param name         Name of person
     * @param mobileNumber Mobile number
     * @param password     password created
     * @param passwordHint password hint to recover password
     */
    public static void signUp(Context context, String name, String mobileNumber, String password, String passwordHint) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_NAME, name)
                .putString(KEY_MOBILE_NUMBER, mobileNumber)
                .putString(KEY_PASSWORD, password)
                .putString(KEY_PASSWORD_HINT, passwordHint)
                .apply();
    }

    /**
     * gets the logged in instructor id from session
     *
     * @param context the context from where this called
     * @return the password string
     */
    public static String getPassword(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).getString(KEY_PASSWORD, "");
    }


}