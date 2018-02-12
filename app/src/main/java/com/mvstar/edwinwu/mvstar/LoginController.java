package com.mvstar.edwinwu.mvstar;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.List;

/**
 * Created by edwinwu on 2018/2/9.
 */

public class LoginController {

    private LoginActivity mLoginActivity;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginController.UserLoginTask mAuthTask = null;

    public LoginController(LoginActivity loginActivity, AutoCompleteTextView emailView, EditText passwordView) {
        mLoginActivity = loginActivity;
        mEmailView = emailView;
        mPasswordView = passwordView;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(mLoginActivity.getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(mLoginActivity.getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(mLoginActivity.getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Start show UI progress
            // Show a progress spinner, and kick off a background task to
            mLoginActivity.showProgress(true);
            mAuthTask = new LoginController.UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        private boolean emailMatched;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // get credentials from repository
            String[] DUMMYLIST = LoginRepository.getInstance().attemptGetCrenditials();

            emailMatched = false;
            for (String credential : DUMMYLIST) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    emailMatched = true;
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mLoginActivity.showProgress(false);

            if (success) {
                mLoginActivity.informAboutLoginSuccess("token");
            } else {
                boolean isemailerror = !emailMatched;
                if (isemailerror) {
                    mEmailView.setError(mLoginActivity.getString(R.string.error_invalid_email));
                    mEmailView.requestFocus();
                } else {
                    mPasswordView.setError(mLoginActivity.getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mLoginActivity.showProgress(false);
        }
    }
}
