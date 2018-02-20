package com.mvstar.edwinwu.mvstar;

import android.app.Activity;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowApplication;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.plugins.RxJavaPlugins;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

/**
 * Created by edwinwu on 2018/2/20.
 */
@Config(constants = BuildConfig.class, shadows = { ShadowTextInputLayout.class, ShadowSnackbar.class})
@RunWith(RobolectricTestRunner.class)
public class LoginActivityTest {

    private EditText mEmailView;
    private EditText mPasswordView;
    private Button mEmailSignInButton;
    private LoginActivity activity;

    @Before
    public void setup() {
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> AndroidSchedulers.mainThread());

        activity = Robolectric.setupActivity(LoginActivity.class);
        mEmailSignInButton = (Button) activity.findViewById(R.id.email_sign_in_button);
        mEmailView = (EditText) activity.findViewById(R.id.email);
        mPasswordView = (EditText) activity.findViewById(R.id.password);
    }

    @Test
    public void testLoginActivity() {
        Assert.assertNotNull(activity);
        Assert.assertEquals(activity.getTitle(), "MVStar");
    }

    @Test
    public void loginSuccess() {
        mEmailView.setText("foo@example.com");
        mPasswordView.setText("hello");
        mEmailSignInButton.performClick();

        assertThat("Show error for Password field", ShadowSnackbar.getLatestSnackbar(), is(CoreMatchers.notNullValue()));
    }

    @Test
    public void loginFailure() {
        mEmailView.setText("edwinwu@google.com");
        mPasswordView.setText("wrongpassword");
        mEmailSignInButton.performClick();

        assertThat("Show error for Password field", mPasswordView.getError(), is(CoreMatchers.notNullValue()));
    }

    @Test
    public void loginWithEmptyEmail() {
        mEmailView.setText("");
        mPasswordView.setText("wrongpassword");
        mEmailSignInButton.performClick();

        assertThat("Show error for Email field", mEmailView.getError(), is(CoreMatchers.notNullValue()));
    }

    @Test
    public void loginWithEmptyPassword() {
        mEmailView.setText("foo@example.com");
        mPasswordView.setText("");
        mEmailSignInButton.performClick();

        assertThat("Show error for Email field", mPasswordView.getError(), is(CoreMatchers.notNullValue()));
    }
}