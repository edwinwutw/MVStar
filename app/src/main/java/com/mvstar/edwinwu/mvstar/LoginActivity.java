package com.mvstar.edwinwu.mvstar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import org.reactivestreams.Processor;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.processors.ReplayProcessor;
import io.reactivex.subjects.ReplaySubject;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // ViewModel
    private LoginViewModel mViewModel;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        // Get the ViewModel from the factory
        LoginViewModelFactory factory = InjectorUtils.provideLoginActivityViewModelFactory(
                this.getApplicationContext());
        mViewModel = ViewModelProviders.of(this, factory).get(LoginViewModel.class);
        mViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginresult) {
                if (loginresult != null) {
                    if (loginresult.success()) {
                        informAboutLoginSuccess("token");
                    } else {
                        if (loginresult.isemailerror())
                            requestEmailFocus(loginresult.failerror());
                        else
                            requestPasswordFocus(loginresult.failerror());
                    }
                }
            }
        });

        observeValidatingStatus();

        testRx5();
    }


    public void testFlowable() {
        //Flowable<String> flowable = Flowable.fromArray(new String[]{"red", "green", "blue", "black"});
        String foo[] = {"red", "green", "blue", "black"};
        Iterable<String> iterable = Arrays.asList(foo);
        Flowable<String> flowable = Flowable.fromArray(foo);
        flowable.subscribe(
                val -> Log.i("Edwin", "Subscriber received:" + val ));

        CompletableFuture<String> completableFuture = CompletableFuture.
                supplyAsync(() -> { //starts a background thread the ForkJoin common pool
                    sleepMillis(100);
                    return "red";
                });

        Single<String> single = Single.fromFuture(completableFuture);
        single.subscribe(val -> Log.i("Edwin", "Stream completed successfully : {}" + val));

    }

    public void testRx1() {
        Single<Boolean> isUserBlockedStream =
                Single.fromFuture(CompletableFuture.supplyAsync(() -> {
                    sleepMillis(200);
                    return Boolean.FALSE;
                }));

        Single<Integer> userCreditScoreStream =
                Single.fromFuture(CompletableFuture.supplyAsync(() -> {
                    sleepMillis(2300);
                    return 5;
                }));

        Single<Pair<Boolean, Integer>> userCheckStream = Single.zip(isUserBlockedStream, userCreditScoreStream,
                (blocked, creditScore) -> new Pair<Boolean, Integer>(blocked, creditScore));

        userCheckStream.subscribe(pair -> Log.i("Edwin", "Received " + pair));
    }

    public void testRx2() {
        Flowable<String> colors = Flowable.just("red", "green", "blue");
        Flowable<Long> timer = Flowable.interval(2, TimeUnit.SECONDS);

        Flowable<String> periodicEmitter = Flowable.zip(colors, timer, (key, val) -> key);

        Flowable<Long> numbers = Flowable.interval(1, TimeUnit.SECONDS)
                .take(5);
        Flowable flowable = Flowable.merge(periodicEmitter, numbers);
        flowable.subscribe(pair -> Log.i("Edwin", "Received " + pair));
    }

    public void testRx3() {
        Flowable<String> colors = Flowable.just("red", "green", "blue");
        Flowable<Long> timer = Flowable.interval(2, TimeUnit.SECONDS);

        Flowable<String> periodicEmitter = Flowable.zip(colors, timer, (key, val) -> key);

        Flowable<Long> numbers = Flowable.interval(1, TimeUnit.SECONDS)
                .take(4);
        Flowable events = Flowable.concat(periodicEmitter, numbers);

        events.subscribe(pair -> Log.i("Edwin", "Received " + pair));
    }

    public void testRx4() {
        //ReplayProcessor<Integer> subject = ReplayProcessor.createWithSize(50);
        PublishProcessor<Integer> subject = PublishProcessor.create();

        Log.i("Edwin", "Pushing 0");
        subject.onNext(0);
        Log.i("Edwin","Pushing 1");
        subject.onNext(1);

        Log.i("Edwin","Subscribing 1st");
        subject.subscribe(val ->  Log.i("Edwin", "Subscriber1 received =" + val),
                logError(), logComplete());

        Log.i("Edwin","Pushing 2");
        subject.onNext(2);

        Log.i("Edwin","Subscribing 2nd");
        subject.subscribe(val ->  Log.i("Edwin", "Subscriber2 received =" + val),
                logError(), logComplete());

        Log.i("Edwin","Pushing 3");
        subject.onNext(3);

        subject.onComplete();
    }

    public void testRx5() {
        ConnectableObservable<Integer> connectableObservable =
                Observable.<Integer>create(subscriber -> {
                    Log.i("Edwin", "Inside create()");

     /* A JMS connection listener example
         Just an example of a costly operation that is better to be shared **/

     /* Connection connection = connectionFactory.createConnection();
        Session session = connection.createSession(true, AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createConsumer(orders);
        consumer.setMessageListener(subscriber::onNext); */

                    subscriber.setCancellable(() -> Log.i("Edwin", "Subscription cancelled"));

                    Log.i("Edwin", "Emitting 1");
                    subscriber.onNext(1);

                    Log.i("Edwin", "Emitting 2");
                    subscriber.onNext(2);

                    subscriber.onComplete();
                }).publish();

        connectableObservable
                .take(1)
                .subscribe((val) -> Log.i("Edwin", "Subscriber1 received: "+val),
                        logError(), logComplete());

        connectableObservable
                .subscribe((val) -> Log.i("Edwin", "Subscriber2 received: "+ val),
                        logError(), logComplete());

        Log.i("Edwin", "Now connecting to the ConnectableObservable");
        connectableObservable.connect();

    }

    public static void sleepMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Log.e("Edwin", "Interrupted Thread");
            throw new RuntimeException("Interrupted thread");
        }
    }

    public <T> Consumer<? super T> logNext() {
        return (Consumer<T>) val ->  Log.i("Edwin", "Subscriber received: " + val);
    }

    public <T> Consumer<? super T> logNextAndSlowByMillis(int millis) {
        return (Consumer<T>) val -> {
            Log.i("Edwin", "Subscriber received: {}"+ val);
            sleepMillis(millis);
        };
    }

    public Consumer<? super Throwable> logError() {
        return err ->  Log.e("Edwin", "Subscriber received error =" + err.getMessage());
    }

    public Consumer<? super Throwable> logError(CountDownLatch latch) {
        return err -> {
            Log.e("Edwin", "Subscriber received error =" + err.getMessage());
            latch.countDown();
        };
    }

    public Action logComplete() {
        return () ->  Log.i("Edwin", "Subscriber got Completed event");
    }

    public Action logComplete(CountDownLatch latch) {
        return () -> {
            Log.i("Edwin", "Subscriber got Completed event");
            latch.countDown();
        };
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            requestEmailFocus(getString(R.string.error_field_required));
            return;
        }
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            requestPasswordFocus(getString(R.string.error_invalid_password));
            return;
        }

        mViewModel.attemptLogin(email, password);
    }

    void requestEmailFocus(String error) {
        mEmailView.setError(error);
        mEmailView.requestFocus();
    }

    void requestPasswordFocus(String error) {
        mPasswordView.setError(error);
        mPasswordView.requestFocus();
    }

    private void observeValidatingStatus() {
        mViewModel.getValdateStatus().observe(this, this::showProgress);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public void informAboutLoginSuccess(String token) {
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.login_form),
                "Login succeed. Token: " + token, Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }

    public void informAboutError(Throwable error) {
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.login_form),
                "Error: " + error.getMessage(), Snackbar.LENGTH_SHORT);
        mySnackbar.show();
    }
}
