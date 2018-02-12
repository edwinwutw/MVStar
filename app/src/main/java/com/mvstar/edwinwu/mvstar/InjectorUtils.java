package com.mvstar.edwinwu.mvstar;

import android.content.Context;

/**
 * Created by edwinwu on 2018/2/12.
 */

public class InjectorUtils {

    public static LoginRepository provideRepository(Context context) {
        return LoginRepository.getInstance();
    }


    public static LoginViewModelFactory provideLoginActivityViewModelFactory(Context context) {
        LoginRepository repository = provideRepository(context.getApplicationContext());
        return new LoginViewModelFactory(context.getApplicationContext(), repository);
    }
}

