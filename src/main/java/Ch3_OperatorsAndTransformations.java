import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static rx.Observable.interval;

/**
 * @author Mike Dunbar
 */
public class Ch3_OperatorsAndTransformations {

    public static final List<String> STRING_LIST = Arrays.asList("hello", "goodbye", "tuna", "ham", "jelly", "trump", "funk");

    public static void main(String[] args) {
        //doSimpleFilteringAndMapping();
        //doSomeFlatMapping();
        //doSomeFlatMapIterable();
        //doSomeFlatMapReactingToCompletionAndErrorAsWell();
        //doDelayOperator();
        //doTimerWithFlatMap();
        //doDelayOperatorVariedByInput();
        //rewriteDelayWithFlatMapAndTimer();
        //showConcatMatPreservingOrderOfSourceStreamEvents();
        //showHowToLimitConcurrencyWithFlatMap();
        //mergeThreeObservableStreams();
        //TODO mergeWithDelayError();//p.79
        //TODO zipThreeStreams();
        //TODO zipTwoStreamsProducingEventsAtDifferentFrequency
        combineLatestShowingDrop();
        //TODO latestFrom



    }

    private static void combineLatestShowingDrop() {
        Observable.combineLatest(
                interval(17, TimeUnit.MILLISECONDS).map(x -> "S" + x).startWith("SX"),
                interval(5, TimeUnit.MILLISECONDS).map(x -> "F" + x),
                (s, f) -> f + ":" + s
        ).forEach(System.out::println);

        sleep(5000);
    }

//    private static void zipThreeStreams() {
//        Observable<String> obs1 = Observable.just("hi", "bye");
//        Observable<String> obs2 = Observable.just("blue", "green");
//        obs1.zipWith(obs2,
//                (a, b) -> {System.out.println("Zipped: " + a + ", " + b);});
//    }

    private static void mergeThreeObservableStreams() {
        Observable<String> obs1 = Observable.just("hi", "bye");
        Observable<String> obs2 = Observable.just("blue", "green");
        Observable<String> obs3 = Observable.just("night", "day");

        Observable<String> all = Observable.merge(obs1,obs2, obs3);
        all.subscribe(System.out::println);

        Observable<String> both = obs1.mergeWith(obs3);
        both.subscribe(System.out::println);
    }

    private static void doSomeFlatMapping() {
        Observable
                .from(STRING_LIST)
                .doOnNext(s -> System.out.println("From Source Stream: " + s))
                .flatMap(s -> charsFromString(s))
                .subscribe(s -> System.out.println("From final Stream: " + s));
    }

    private static void showHowToLimitConcurrencyWithFlatMap() {
        Observable
                .from(STRING_LIST)
                .doOnNext(s -> System.out.println(new Date() + ": From Source Stream: " + s))
                .flatMap(s ->
                        interval(1, TimeUnit.SECONDS)
                        .take(2)
                        .map(i -> s.charAt(i.intValue())), 2      )
                .subscribe(s -> System.out.println(new Date() + ": From final Stream: " + s));
        sleep(10000);

    }

    private static void showConcatMatPreservingOrderOfSourceStreamEvents() {
        Observable
                .from(STRING_LIST)
                .concatMap(x ->
                        Observable.timer(x.length(), TimeUnit.SECONDS).map(n -> x))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        sleep(30000);
    }

    private static void rewriteDelayWithFlatMapAndTimer() {
        Observable
                .from(STRING_LIST)
                .flatMap(x ->
                    Observable.timer(x.length(), TimeUnit.SECONDS).map(n -> x))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        sleep(10000);
    }

    private static void doDelayOperatorVariedByInput() {
        Observable
                .from(STRING_LIST)
                .delay(s -> Observable.just(s).timer(s.length(), TimeUnit.SECONDS))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        sleep(10000);
    }

    private static void doTimerWithFlatMap() {
        Observable
                .timer(1, TimeUnit.SECONDS)
                .flatMap(i -> Observable.just("a", "b", "c", "d"))
                .subscribe(i -> System.out.println(new Date() + ": " + i));
        sleep(2000);
    }

    private static void doDelayOperator() {
        Observable
                .just("a", "b", "c", "d")
                .delay(1, TimeUnit.SECONDS)
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        sleep(2000);
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

    private static Observable<String> charsFromString(String s) {
        RxMain.log("In charsFromString");
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

    private static void sleep(int millis) {
        try {
            System.out.println(new Date() + ": Going to sleep");
            Thread.sleep(millis);
            System.out.println(new Date() + ": Done sleeping");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
