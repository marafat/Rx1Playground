package com.arccosgolf.rx1playground;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.schedulers.TestScheduler;
import rx.subscriptions.CompositeSubscription;

/**
 * ${FILE_NAME}
 * author mhdarafat
 * on 7/21/17.
 */
public class SwitchMapTest {
    private CompositeSubscription compositeSubscription;

    @Before
    public void setUp() throws Exception {
        compositeSubscription = new CompositeSubscription();
    }

    @After
    public void tearDown() throws Exception {
        compositeSubscription.clear();
    }


    /**
     * <B>From Rx Docs:</B>
     * <I>Note that FlatMap merges the emissions of these Observables, so that they
     * may interleave.</I>
     * <p>
     * <B>Notes:</B>
     * So what actually happens here is operator flatMap does not care about the order
     * of the items. It creates a new observable for each item and that observable lives
     * its own life.
     * <p>
     * Similar effect will happen without delaying, but it was added to emphasize the effect.
     */
    @Test
    public void flatMap() throws Exception {
        System.out.println("Testing flatMap:...");
        final List<String> items = Lists.newArrayList("a", "b", "c", "d", "e", "f");
        System.out.println("Input stream: " + items);

        final TestScheduler scheduler = new TestScheduler();

        final Subscription subscription = Observable.from(items)
                .flatMap(s -> {
                    final int delay = new Random().nextInt(10);
                    return Observable.just(s + "x")
                            .delay(delay, TimeUnit.SECONDS, scheduler);
                })
                .toList()
                .doOnSubscribe(() -> System.out.println("Output stream from flatMap:"))
                .doOnNext(System.out::println)
                .subscribe();

        compositeSubscription.add(subscription);

        /*
         * advancing in time by 1 minute, just to be sure that everything will have the time
         * to emit. (If we would not do this, the test would end before any emission).
         */
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
    }


    /**
     * <B>From Rx Docs:</B>
     * <I>whenever a new item is emitted by the source Observable, it will unsubscribe to
     * and stop mirroring the Observable that was generated from the previously-emitted item,
     * and begin only mirroring the current one</I>
     * <p>
     * <B>Notes:</B>
     * A common example for using switchMap is running a search query on a stream of search
     * terms coming from a textWatcher. On the other hand, you almost never want to use
     * switchMap after Observable.from(). And if you have to do it, do it with caution.
     */
    @Test
    public void switchMap() throws Exception {
        System.out.println("Testing correct implementation of switchMap:...");
        final List<String> items = Lists.newArrayList("a", "b", "c", "d", "e", "f");
        System.out.println("Input stream: " + items);

        final TestScheduler scheduler = new TestScheduler();

        final Subscription subscription = Observable.from(items)
                .switchMap(s -> {
                    final int delay = new Random().nextInt(10);
                    return Observable.just(s + "x")
                            .delay(delay, TimeUnit.SECONDS, scheduler);
                })
                .toList()
                .doOnSubscribe(() -> System.out.println("Output stream from correct implementation of switchMap:"))
                .doOnNext(System.out::println)
                .subscribe();

        compositeSubscription.add(subscription);

        /*
         * advancing in time by 1 minute, just to be sure that everything will have the time
         * to emit. (If we would not do this, the test would end before any emission).
         */
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
    }


    @Test
    public void badSwitchMap() throws Exception {
        System.out.println("Testing bad implementation of switchMap:...");
        final List<String> items = Lists.newArrayList("a", "b", "c", "d", "e", "f");
        System.out.println("Input stream: " + items);

        final TestScheduler scheduler = new TestScheduler();

        final Subscription subscription = Observable.from(items)
                .switchMap(Observable::just)
                .flatMap(s -> {
                    final int delay = new Random().nextInt(10);
                    return Observable.just(s + "x")
                            .delay(delay, TimeUnit.SECONDS, scheduler);
                })
                .toList()
                .doOnSubscribe(() -> System.out.println("Output stream from bad implementation of switchMap:"))
                .doOnNext(System.out::println)
                .subscribe();

        compositeSubscription.add(subscription);

        /*
         * advancing in time by 1 minute, just to be sure that everything will have the time
         * to emit. (If we would not do this, the test would end before any emission).
         */
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
    }


    /**
     * <B>Notes:</B>
     * ConcatMap works almost the same as flatMap, but preserves the order of items.
     * But concatMap has one big flaw: it waits for each observable to finish all the work
     * until next one is processed. That is why it is SLOW!!
     */
    @Test
    public void concatMap() throws Exception {
        System.out.println("Testing concatMap:...");
        final List<String> items = Lists.newArrayList("a", "b", "c", "d", "e", "f");
        System.out.println("Input stream: " + items);

        final TestScheduler scheduler = new TestScheduler();

        final Subscription subscription = Observable.from(items)
                .concatMap(s -> {
                    final int delay = new Random().nextInt(10);
                    return Observable.just(s + "x")
                            .delay(delay, TimeUnit.SECONDS, scheduler);
                })
                .toList()
                .doOnSubscribe(() -> System.out.println("Output stream from concatMap:"))
                .doOnNext(System.out::println)
                .subscribe();

        compositeSubscription.add(subscription);

        /*
         * advancing in time by 1 minute, just to be sure that everything will have the time
         * to emit. (If we would not do this, the test would end before any emission).
         */
        scheduler.advanceTimeBy(1, TimeUnit.MINUTES);
    }
}