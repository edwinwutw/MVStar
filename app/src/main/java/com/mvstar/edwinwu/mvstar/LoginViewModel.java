package com.mvstar.edwinwu.mvstar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by edwinwu on 2018/2/12.
 */

public class LoginViewModel extends ViewModel {

    private final Context mContext;
    private final LoginRepository mRepository;

    private final MutableLiveData<LoginResult> mLoginResultLiveData = new MutableLiveData<>();
    private String mEmail;
    private String mPassword;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private LoginViewModel.UserLoginTask mAuthTask = null;

    public LoginViewModel(Context context, LoginRepository repository) {
        mContext = context;
        mRepository = repository;

        mLoginResultLiveData.setValue(null);
        mEmail = "";
        mPassword = "";
    }

    public LiveData<LoginResult> getLoginResult() {
        return mLoginResultLiveData;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String email, String password) {
        this.mEmail = email;
        this.mPassword = password;

        LoginResult loginresult = LoginResult.create(mEmail, mPassword, false, false, false, "");
        mLoginResultLiveData.setValue(loginresult);

        // Check for a valid email address.
        if (!isEmailValid(mEmail)) {
            loginresult = LoginResult.create(mEmail, mPassword, true, false, true,
                    mContext.getString(R.string.error_invalid_email));
            mLoginResultLiveData.setValue(loginresult);
            return;
        }

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(mPassword)) {
            loginresult = LoginResult.create(mEmail, mPassword, true, false, false,
                    mContext.getString(R.string.error_invalid_password));
            mLoginResultLiveData.setValue(loginresult);
            return;
        }

        //kick off a background task to
        mAuthTask = new LoginViewModel.UserLoginTask(email, password);
        mAuthTask.execute((Void) null);
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

            boolean isemailerror = false;
            String failerror = "";
            if (!success) {
                isemailerror = !emailMatched;

                failerror = isemailerror ? mContext.getString(R.string.error_invalid_email) :
                        mContext.getString(R.string.error_incorrect_password);
            }

            LoginResult loginresult = LoginResult.create(mEmail, mPassword, true, success, isemailerror, failerror);
            mLoginResultLiveData.setValue(loginresult);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;

            LoginResult loginresult = LoginResult.create(mEmail, mPassword, true, false, false, "");
            mLoginResultLiveData.setValue(loginresult);
        }
    }
}
