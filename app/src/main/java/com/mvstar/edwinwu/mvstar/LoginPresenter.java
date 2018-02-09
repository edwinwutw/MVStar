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

public class LoginPresenter {

    private LoginView mLoginView;
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginPresenter.UserLoginTask mAuthTask = null;

    public LoginPresenter(LoginView view) {
        mLoginView = view;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Store values at the time of the login attempt.
        String email = mLoginView.getEmail();
        String password = mLoginView.getPassword();

        Integer passwordErrorId = getPasswordErrorId(password);
        if (passwordErrorId != null)
            mLoginView.setPasswordError(passwordErrorId.intValue());

        Integer emailErrorId = getEmailErrorId(email);
        if (emailErrorId != null)
           mLoginView.setEmailError(emailErrorId.intValue());


        if (emailErrorId != null)
            mLoginView.requestEmailFocus();
        else if (passwordErrorId != null)
            mLoginView.requestPasswordFocus();
        else {
            // Start show UI progress
            // Show a progress spinner, and kick off a background task to
            mLoginView.showProgress(true);
            mAuthTask = new LoginPresenter.UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private Integer getEmailErrorId(String email) {
        if (email.isEmpty())
            return R.string.error_field_required;
        else if (!isEmailValid(email))
            return R.string.error_invalid_email;
        else
            return null;
    }

    private Integer getPasswordErrorId(String password) {
        if (password.isEmpty())
            return R.string.error_field_required;
        else if (!isPasswordValid(password))
            return R.string.error_invalid_password;
        else
            return null;
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

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            // get credentials from repository
            String[] DUMMYLIST = LoginRepository.getInstance().attemptGetCrenditials();

            for (String credential : DUMMYLIST) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            mLoginView.showProgress(false);

            if (success) {
                mLoginView.informAboutLoginSuccess("token");
            } else {
                mLoginView.setPasswordError(R.string.error_incorrect_password);
                mLoginView.requestPasswordFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            mLoginView.showProgress(false);
        }
    }
}
