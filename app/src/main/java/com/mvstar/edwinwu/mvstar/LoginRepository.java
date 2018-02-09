package com.mvstar.edwinwu.mvstar;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

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

    public String[] attemptGetCrenditials() {

        try {
            // Simulate network access.
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            return null;
        }

        return DUMMY_CREDENTIALS;
    }
}