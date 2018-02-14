package com.mvstar.edwinwu.mvstar;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.content.Context;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by edwinwu on 2018/2/12.
 */

public class LoginViewModel extends ViewModel {

    private final Context mContext;
    private final LoginRepository mRepository;

    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<Boolean> isValidating = new MutableLiveData<>();
    private final MutableLiveData<List<ValidEmailInfo>> emailInfoResultList = new MutableLiveData<>();

    private final MutableLiveData<LoginResult> mLoginResultLiveData = new MutableLiveData<>();
    private String mEmail;
    private String mPassword;

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

    @Override
    protected void onCleared() {
        disposables.clear();
    }

    LiveData<Boolean> getValdateStatus() {
        return isValidating;
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin(String email, String password) {
        this.mEmail = email;
        this.mPassword = password;

        // first not show progress
        isValidating.setValue(false);

        LoginResult loginresult = LoginResult.create(false, false, "");
        mLoginResultLiveData.setValue(loginresult);

        // Check for a valid email address.
        if (!isEmailValid(mEmail)) {
            loginresult = LoginResult.create(false, true,
                    mContext.getString(R.string.error_invalid_email));
            mLoginResultLiveData.setValue(loginresult);
            return;
        }

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(mPassword)) {
            loginresult = LoginResult.create(false, false,
                    mContext.getString(R.string.error_invalid_password));
            mLoginResultLiveData.setValue(loginresult);
            return;
        }

        disposables.add(
                LoginRepository.getInstance().attemptGetCrenditials()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(s -> isValidating.setValue(true))
                        .doAfterTerminate(() -> isValidating.setValue(false))
                        .subscribe(
                                this::checkCredentials
                        )
        );
    }

    private void checkCredentials(List<ValidEmailInfo> list) {
        boolean emailMatched = false;
        boolean success = false;
        for (ValidEmailInfo item : list) {
            if (item.email().equals(mEmail)) {
                // Account exists, return true if the password matches.
                emailMatched = true;
                success = item.password().equals(mPassword);
                break;
            }
        }
        boolean isemailerror = false;
        String failerror = "";
        if (!success) {
            isemailerror = !emailMatched;

            failerror = isemailerror ? mContext.getString(R.string.error_invalid_email) :
                    mContext.getString(R.string.error_incorrect_password);
        }

        mLoginResultLiveData.setValue(LoginResult.create(success, isemailerror, failerror));
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }
}