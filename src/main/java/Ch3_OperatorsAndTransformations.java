import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Mike Dunbar
 */
public class Ch3_OperatorsAndTransformations {

    public static final List<String> STRING_LIST = Arrays.asList("hello", "goodbye", "tuna", "ham");

    public static void main(String[] args) {
        //doSimpleFilteringAndMapping();
        //doSomeFlatMapping();
        //doSomeFlatMapIterable();
        //doSomeFlatMapReactingToCompletionAndErrorAsWell();
        //doDelayOperator();
        //doTimerWithFlatMap();
        //doDelayOperatorVariedByInput();
        //rewriteDelayWithFlatMapAndTimer();
        showConcatMatPreservingOrderOfSourceStreamEvents();
    }

    private static void showConcatMatPreservingOrderOfSourceStreamEvents() {
        Observable
                .from(STRING_LIST)
                .concatMap(x ->
                        Observable.timer(x.length(), TimeUnit.SECONDS).map(n -> x))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        try {
            System.out.println(new Date() + ": Going to sleep");
            Thread.sleep(30000);
            System.out.println(new Date() + ": Done sleeping");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void rewriteDelayWithFlatMapAndTimer() {
        Observable
                .from(STRING_LIST)
                .flatMap(x ->
                    Observable.timer(x.length(), TimeUnit.SECONDS).map(n -> x))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        try {
            System.out.println(new Date() + ": Going to sleep");
            Thread.sleep(10000);
            System.out.println(new Date() + ": Done sleeping");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void doDelayOperatorVariedByInput() {
        Observable
                .from(STRING_LIST)
                .delay(s -> Observable.just(s).timer(s.length(), TimeUnit.SECONDS))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        try {
            System.out.println(new Date() + ": Going to sleep");
            Thread.sleep(10000);
            System.out.println(new Date() + ": Done sleeping");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void doTimerWithFlatMap() {
        Observable
                .timer(1, TimeUnit.SECONDS)
                .flatMap(i -> Observable.just("a", "b", "c", "d"))
                .subscribe(i -> System.out.println(new Date() + ": " + i));
        try {
            System.out.println(new Date() + ": Going to sleep");
            Thread.sleep(2000);
            System.out.println(new Date() + ": Done sleeping");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void doDelayOperator() {
        Observable
                .just("a", "b", "c", "d")
                .delay(1, TimeUnit.SECONDS)
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        try {
            System.out.println(new Date() + ": Going to sleep");
            Thread.sleep(2000);
            System.out.println(new Date() + ": Done sleeping");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void doSomeFlatMapReactingToCompletionAndErrorAsWell() {
        Observable
                .from(STRING_LIST)
                .flatMap(
                        (String s) -> {
                            System.out.println("onNext: " + s);
                            return charsFromString(s);},
                        (e) -> {
                            System.out.println("onError: " + e);
                            return Observable.empty();},
                        () -> {
                            System.out.println("onCompleted");
                            return Observable.empty();})
                .subscribe(System.out::println);

    }

    private static void doSomeFlatMapping() {
        Observable
                .from(STRING_LIST)
                .doOnNext(s -> System.out.println("From Source Stream: " + s))
                .flatMap(s -> charsFromString(s))
                .subscribe(s -> System.out.println("From final Stream: " + s));
    }

    private static Observable<String> charsFromString(String s) {
        List<String> chars = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            chars.add("" + s.charAt(i));
        }
        return Observable.from(chars);
    }

    private static void doSomeFlatMapIterable() {
        Observable
                .from(STRING_LIST)
                .doOnNext(s -> System.out.println("From Source Stream: " + s))
                .flatMapIterable((s) -> {
                    List<String> chars = new ArrayList<String>();
                    for (int i = 0; i < s.length(); i++) {
                        chars.add("" + s.charAt(i));
                    }
                    return chars;
                })
                .subscribe(s -> System.out.println("From final Stream: " + s));

    }public static void doSimpleFilteringAndMapping() {
        Observable
                .from(STRING_LIST)
                .doOnNext(s -> System.out.println("Initial: " + s))
                .filter(s -> s.startsWith("h"))
                .doOnNext(s -> System.out.println("Filtered: " + s))
                .map(s -> s.toUpperCase())
                .subscribe(s -> System.out.println("Mapped: " + s));

    }
}
