package com.mvstar.edwinwu.mvstar;

import com.google.auto.value.AutoValue;

/**
 * Created by edwinwu on 2018/2/12.
 */

@AutoValue
public abstract class LoginResult {
    abstract String email();
    abstract String password();
    abstract boolean finish();
    abstract boolean success();
    abstract boolean isemailerror();
    abstract String failerror();

    public static LoginResult create(String email, String password, boolean finish,
                                     boolean success, boolean isemailerror, String failerror) {
        return new AutoValue_LoginResult(email, password, finish, success, isemailerror, failerror);
    }
}