package com.mvstar.edwinwu.mvstar;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.SingleObserver;

/**
 * Created by edwinwu on 2018/2/9.
 */

public class LoginRepository {
    private static final String LOG_TAG = LoginRepository.class.getSimpleName();

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    // For Singleton instantiation
    private static final Object LOCK = new Object();
    private static LoginRepository sInstance;

    private LoginRepository() {
    }

    public synchronized static LoginRepository getInstance() {
        Log.d(LOG_TAG, "Getting the repository");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new LoginRepository();
                Log.d(LOG_TAG, "Made new repository");
            }
        }
        return sInstance;
    }

    public Single<List<ValidEmailInfo>> attemptGetCrenditials() {

        try {
            // Simulate network access.
            Thread.sleep(100);
        } catch (InterruptedException e) {
            return null;
        }
        return Observable.fromIterable(Arrays.asList(DUMMY_CREDENTIALS))
                .map(item -> {
                    String[] pieces = item.split(":");
                    return ValidEmailInfo.create(pieces[0], pieces[1]);
                }).toList();

    }
}