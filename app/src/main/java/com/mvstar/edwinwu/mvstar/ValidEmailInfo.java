package com.mvstar.edwinwu.mvstar;

import com.google.auto.value.AutoValue;

/**
 * Created by edwinwu on 2018/2/14.
 */

@AutoValue
public abstract class ValidEmailInfo {
    abstract String email();
    abstract String password();

    public static ValidEmailInfo create(String email, String password) {
        return new AutoValue_ValidEmailInfo(email, password);
    }
}