package com.mvstar.edwinwu.mvstar;

/**
 * Created by edwinwu on 2018/2/9.
 */

public interface LoginView {
    void showProgress(boolean show);
    String getEmail();
    String getPassword();
    void setEmailError(int id);
    void setPasswordError(int id);
    void requestEmailFocus();
    void requestPasswordFocus();
    void informAboutLoginSuccess(String token);
    void informAboutError(Throwable error);
}
