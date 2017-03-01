import com.google.common.base.Strings;
import com.google.common.util.concurrent.MoreExecutors;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Mike Dunbar
 */
public class RxMain {
    public static String DESC = "hi";

    public static void log(String desc) {
        Thread t = Thread.currentThread();
        System.out.println(String.format("%s: %s", t.getName(), desc));
    }

    public static void main(String... args) {
        basicSubscriptionStuff();
        basicUnSubscribeStuff();


        // Prior to book
//        basicStreamTransform();
//        asyncStreamTransform();
    }



    private static void basicSubscriptionStuff() {
        log("start of basicSubscriptionStuff");
        Observable<String> strings = Observable.from(Arrays.asList("do", "re", "mi", "fa", "so", "la", "ti"));

        // Lambda
        strings.subscribe((String s) -> {
            log(s);});
        System.out.println();

        // Method Reference
        strings.subscribe(System.out::print);
        System.out.println();

        // With onNext, onError, and onCompleted
        strings.subscribe(
                (String s) -> {
                    log(s);},
                (Throwable t) -> {t.printStackTrace();},
                () -> {
                    log("That's all folks");}
        );
        System.out.println();

        // With an Observer<T>
        strings.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                log("That's all folks");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                log(s);
            }
        });
        System.out.println();
    }

    private static void basicUnSubscribeStuff() {
        log("Start of basicUnSubscribeStuff");
        Observable<String> strings = Observable.from(Arrays.asList("do", "re", "mi", "fa", "so", "la", "ti"));

        // With a Subscription, enables unsubcription outside of observer/callback
        log("before subscribing");
        Subscription subscription = strings.subscribe(s -> {
            log(s);});
        log("before unsubscribe, isUnsubscribed: " + subscription.isUnsubscribed()); // true - because all events handled?
        subscription.unsubscribe();
        log("after unsubscribe, isUnsubscribed: " + subscription.isUnsubscribed());

        // With a Subscriber<T>, which implements both Observer<T> & Subscription
        log("before subscribing with a Subscriber");
        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                log("onCompleted");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String s) {
                if (s.equals("so")) {
                    unsubscribe();
                }
                log(s);
            }
        };
        strings.subscribe(subscriber);
        System.out.println();
    }

    private static void basicStreamTransform() {
        log("Basic / Sync transform start");
        List<String> start = Arrays.asList("my", "name", "is", "barr");
        System.out.println("Start: " + start);
        Observable.from(start)
                .map((s) -> {
                    log("convert to upper case");
                    return Strings.isNullOrEmpty(s) ? s : s.toUpperCase();
                })
                .toList()
                .map((list) -> {
                    log("reverse list");
                    Collections.reverse(list);
                    return list.toString();
                })
                .subscribe(s -> System.out.println("Finish: " + s));
        log("Basic / sync transform end");
    }

    private static void asyncStreamTransform() {
        log("Async transform start");
        Observable.fromCallable(() -> {
            log("create from Callable");
            return Arrays.asList("one", "two", "three");
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap((list) -> {
                    log("flat map");
                    return rx.Observable.from(list);
                })
                .observeOn(Schedulers.from(MoreExecutors.directExecutor()))
                .map((s) -> {
                    log("convert to upper case");
                    return s.toUpperCase();
                })
                .subscribe((s) -> {
                    log(String.format("subscribe method, received %s", s));
                });
        // Give the background threads time to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log("Async transform done");
    }
}
