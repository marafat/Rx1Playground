package com.arccosgolf.rx1playground;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Subscription onNextOnlySubscription;
    private Subscription regSubscription;
    private Subscription secondSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart() {
        super.onStart();

        regSubscription = getMyStringObservable().subscribe(new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted() called on Thread: " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError() called with: e = [" + e + "] on Thread: " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext() called with: s = [" + s + "] on Thread " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
            }
        });

        onNextOnlySubscription = getMyStringObservable().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "call() of onNext only called with: s = [" + s + "] on Thread: " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
            }
        });

        secondSubscription = getMySecondStringObservable().subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "call() called with: s = [" + s + "] on Thread: " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (regSubscription == null) {
            Log.d(TAG, "onStop: regSubs is Null");
        }

        if (regSubscription != null && regSubscription.isUnsubscribed()) {
            Log.d(TAG, "onStop: regSubs is unsubscribed"); // with rxAndroid 1.2.1 this would happen // i.e auto unsubscribe
        }
        if (regSubscription != null && !regSubscription.isUnsubscribed()) {
            Log.d(TAG, "onStop: Unsubscribing regSubs");
            regSubscription.unsubscribe();
        }


        if (onNextOnlySubscription == null) {
            Log.d(TAG, "onStop: onNextOnlySubs is Null");
        }

        if (onNextOnlySubscription != null && onNextOnlySubscription.isUnsubscribed()) {
            Log.d(TAG, "onStop: onNextOnlySubs is unsubscribed");
        }
        if (onNextOnlySubscription != null && !onNextOnlySubscription.isUnsubscribed()) {
            Log.d(TAG, "onStop: Unsubscribing onNextOnlySubs");
            onNextOnlySubscription.unsubscribe();
        }

    }

    Observable<String> getMyStringObservable() {
        return Observable.just("Hello World!")
                .subscribeOn(Schedulers.io())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        Log.d(TAG, "call: Adding signature to string on thread: " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
                        return s + " -Moe";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * This observable is to investigate what the example at: https://github.com/ReactiveX/RxAndroid/tree/1.x#observing-on-the-main-thread
     * <p>
     * So far, it seems like the example is misleading as far as the use of subscribeOn() and observeOn()
     * I personally thought that if both are called in the way used in the example then any code inside
     * the just() will run on the background thread, however, getMySecondStringObservable proves this
     * theory wrong, as buildSecondString is still called on the main thread.
     * <p>
     * And calling subscribeOn() and observeOn() in succession, like the example at the link above, is pointless.
     *
     * @return
     */
    Observable<String> getMySecondStringObservable() {
        return Observable.just(buildSecondString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        Log.d(TAG, "call: Adding signature to second string on thread: " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
                        return "محمد" + s;
                    }
                });
    }

    private String buildSecondString() {
        Log.d(TAG, "buildSecondString() called on Thread: " + Thread.currentThread().getId() + "|" + Thread.currentThread().getName());
        return "مرحبا!";
    }
}
