package com.mvstar.edwinwu.mvstar;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;

/**
 * Created by edwinwu on 2018/2/12.
 */

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Context mContext;
    private final LoginRepository mRepository;

    public LoginViewModelFactory(Context context, LoginRepository repository) {
        this.mContext = context;
        this.mRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        return (T) new LoginViewModel(mContext, mRepository);
    }

}
