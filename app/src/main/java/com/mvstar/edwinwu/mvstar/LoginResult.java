package com.mvstar.edwinwu.mvstar;

import com.google.auto.value.AutoValue;

/**
 * Created by edwinwu on 2018/2/12.
 */

@AutoValue
public abstract class LoginResult {
    abstract boolean success();
    abstract boolean isemailerror();
    abstract String failerror();

    public static LoginResult create(boolean success, boolean isemailerror, String failerror) {
        return new AutoValue_LoginResult(success, isemailerror, failerror);
    }
}