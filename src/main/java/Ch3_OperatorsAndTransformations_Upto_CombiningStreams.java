import rx.Observable;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static rx.Observable.interval;

/**
 * @author Mike Dunbar
 */
public class Ch3_OperatorsAndTransformations_Upto_CombiningStreams {

    public static final List<String> STRING_LIST = Arrays.asList("hello", "goodbye", "tuna", "ham", "jelly", "trump", "funk");

    public static void main(String[] args) {
        //doSimpleFilteringAndMapping();
        //doSomeFlatMapping();
        //doSomeFlatMapIterable();
        //doSomeFlatMapReactingToCompletionAndErrorAsWell();
        //doDelayOperator();
        //doTimerWithFlatMap();
        doDelayOperatorVariedByInput();
        //rewriteDelayWithFlatMapAndTimer();
        //showConcatMatPreservingOrderOfSourceStreamEvents();
        //showHowToLimitConcurrencyWithFlatMap();
        //mergeThreeObservableStreams();
        //mergeWithDelayError();
        //zipThreeStreams();
        //zipTwoStreamsProducingEventsAtDifferentFrequency();
        //combineLatestShowingDrop();
        //showlatestFrom();

        //scanWithoutInitialValue();
        //scanWithInitialValue();
        reduceWithInitialValue();
        //collect();

        //showSingleEnforcingOneValue();
        //showDistinctDroppingDuplicates();
        //showKeyVersionOfDistinct();
        //showDistinctUntilChanged();

        //showTake3();
        //showSkip3();
        //showTakeLast3();
        //showSkipLast3();
        //showFirst();
        //showLast();
        //showTakeFirstWithPred();
        //showTakeUntil();
        //showTakeWhile();
        //showElementAt();
        //showAllWithPredicate();
        //showExists();
        //showContains();
        showTakeFirstWithSingle();
    }

    private static void showTakeFirstWithSingle() {
                Observable.range(1, 5)
                        .takeFirst(x -> x > 5)
                        //.single()
                        .subscribe(System.out::println);
    }

    private static void showContains() {
        Observable.range(1,5)
                .contains(5)
                .subscribe(System.out::println);

        Observable.range(1,5)
                .contains(6)
                .subscribe(System.out::println);
    }

    private static void showExists() {
        Observable.range(1,5)
                .exists(x -> x % 2 == 0)
                .subscribe(System.out::println);
    }

    private static void showAllWithPredicate() {
        Observable.range(1,5)
                .all(x -> x % 2 == 0)
                .subscribe(System.out::println);

        Observable.just(2,4,6,8)
                .all(x -> x % 2 == 0)
                .subscribe(System.out::println);
    }

    private static void showElementAt() {
        Observable.range(1,5)
                .elementAt(1)
                .subscribe(System.out::println);
    }

    private static void showTakeWhile() {
        Observable.range(1,5)
                .takeWhile(x -> x < 4)
                .subscribe(System.out::println);
    }

    private static void showTakeUntil() {
        Observable.range(1,5)
                .takeUntil(x -> x >= 4)
                .subscribe(System.out::println);
    }

    private static void showTakeFirstWithPred() {
        Observable.range(1,5)
                .takeFirst(x -> x %2 == 0)
                .subscribe(System.out::println);
    }

    private static void showLast() {
        Observable.range(1,5)
                .last()
                .subscribe(System.out::println);
    }

    private static void showFirst() {
        Observable.range(1,5)
                .first()
                .subscribe(System.out::println);
    }

    private static void showSkipLast3() {
        Observable.range(1,5)
                .skipLast(3)
                .subscribe(System.out::println);
    }

    private static void showTakeLast3() {
        Observable.range(1,5)
                .takeLast(3)
                .subscribe(System.out::println);
    }

    private static void showSkip3() {
        Observable.range(1,5)
                .take(6)
                .subscribe(System.out::println);
    }

    private static void showTake3() {
        Observable.range(1,5)
                .take(3)
                .subscribe(System.out::println);
    }

    @CheckForNull
    public String getIt(@Nullable String init) {
        return null;
    }


    private static void showDistinctUntilChanged() {
        Observable.just(1,1,7,7,7,2,2,2,2,9)
                .distinctUntilChanged()
                .subscribe(System.out::println);
    }

    private static void showKeyVersionOfDistinct() {
        Observable.just(new Tweet("Trump sucks", 7), new Tweet("Obama lied", 3), new Tweet("Jimmy crack corn", 7))
                .distinct(tweet -> tweet.userId)
                .subscribe(tweet -> {System.out.println(tweet.text);});
    }

    static class Tweet {
        String text;
        Integer userId;

        public Tweet(String text, Integer userId) {
            this.text = text;
            this.userId = userId;
        }
    }

    private static void showDistinctDroppingDuplicates() {
        Observable.just(1, 2, 3, 1, 4, 2, 5, 1, 6)
                .distinct()
                .subscribe(System.out::println);
    }

    private static void showSingleEnforcingOneValue() {
        Observable.range(1,2)
                .single()
                .subscribe((Integer n) -> {System.out.println("Got next: " + n);},
                        (Throwable t) -> {System.out.println("Got error: " + t);},
                        () -> {});
    }

    private static void collect() {
        Observable.range(10, 20)
                .collect(ArrayList::new, List::add)
                .subscribe(System.out::println);
    }

    private static void reduceWithInitialValue() {
        Observable.range(150, 200)
                .reduce(500, (total, cur) -> total + cur)
                .subscribe(System.out::println);
    }

    private static void scanWithInitialValue() {
        Observable.range(1,100)
                .scan(100, (total, cur) -> total + cur)
                .subscribe(System.out::println);
    }

    private static void scanWithoutInitialValue() {
        Observable<Integer> progress = Observable.range(1, 100)
                .scan((total, cur) -> total + cur);
        progress.subscribe(System.out::println);
    }

    private static void showlatestFrom() {
        Observable<String> fast = Observable
                .interval(10, TimeUnit.MILLISECONDS)
                .map(x -> "F" + x);
        Observable<String> slow = Observable
                .interval(18, TimeUnit.MILLISECONDS)
                .map(x -> "S" + x);
        slow.withLatestFrom(fast, (f,s) -> f + ":" + s)
                .forEach(System.out::println);
        RxMain.sleep(5000);
    }

    private static void combineLatestShowingDrop() {
        Observable.combineLatest(
                interval(17, TimeUnit.MILLISECONDS).map(x -> "S" + x).startWith("SX"),
                interval(5, TimeUnit.MILLISECONDS).map(x -> "F" + x),
                (s, f) -> f + ":" + s
        ).forEach(System.out::println);

        RxMain.sleep(5000);
    }

    private static void zipThreeStreams() {
        Observable<String> obs1 = Observable.just("hi", "bye");
        Observable<String> obs2 = Observable.just("blue", "green");
        Observable<String> obs3 = Observable.just("big", "small");
        Observable<List<String>> listObservable = obs1.zip(obs1, obs2, obs3,
                (a, b, c) -> Arrays.asList(a, b, c));
        listObservable.forEach(System.out::println);
    }

    private static void zipTwoStreamsProducingEventsAtDifferentFrequency() {
        Observable<String> strings1 =
                Observable
                        .interval(10, TimeUnit.MILLISECONDS)
                        .map(i -> "F:" + i);
        Observable<String> strings2 =
                Observable
                        .interval(50, TimeUnit.MILLISECONDS)
                        .map(i -> "S:" + i);
        strings1.zipWith(strings2,
                (s1, s2) -> Arrays.asList(s1, s2))
                .forEach(System.out::println);

        RxMain.sleep(1000);
    }

    private static void mergeThreeObservableStreams() {
        Observable<String> obs1 = Observable.just("hi", "bye");
        Observable<String> obs2 = Observable.just("blue", "green");
        Observable<String> obs3 = Observable.just("night", "day");

        Observable<String> all = Observable.merge(obs1,obs2, obs3);
        all.subscribe(System.out::println);

        Observable<String> both = obs1.mergeWith(obs3);
        both.subscribe(System.out::println);
    }

    private static void mergeWithDelayError() {
        Observable<String> obs1 = Observable.create(subscriber -> {
            subscriber.onNext("green");
            subscriber.onNext("red");
            subscriber.onError(new RuntimeException("why you so crazy, Donald!"));
        });

        Observable<String> obs2 = Observable.create(subscriber -> {
            subscriber.onNext("russia");
            subscriber.onNext("first");
            subscriber.onNext("america");
            subscriber.onNext("second");
            subscriber.onCompleted();
        });

        Observable<String> both = Observable.mergeDelayError(obs1, obs2);
        both.subscribe(
                (String s) -> {System.out.println(s);},
                (Throwable t) -> {System.out.println("Error: " + t.getMessage());},
                () -> {System.out.println("we done");}
        );


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
        RxMain.sleep(10000);

    }

    private static void showConcatMatPreservingOrderOfSourceStreamEvents() {
        Observable
                .from(STRING_LIST)
                .concatMap(x ->
                        Observable.timer(x.length(), TimeUnit.SECONDS).map(n -> x))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        RxMain.sleep(30000);
    }

    private static void rewriteDelayWithFlatMapAndTimer() {
        Observable
                .from(STRING_LIST)
                .flatMap(x ->
                    Observable.timer(x.length(), TimeUnit.SECONDS).map(n -> x))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        RxMain.sleep(10000);
    }

    private static void doDelayOperatorVariedByInput() {
        Observable
                .from(STRING_LIST)
                .delay(s -> Observable.just(s).timer(s.length(), TimeUnit.SECONDS))
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        RxMain.sleep(10000);
    }

    private static void doTimerWithFlatMap() {
        Observable
                .timer(1, TimeUnit.SECONDS)
                .flatMap(i -> Observable.just("a", "b", "c", "d"))
                .subscribe(i -> System.out.println(new Date() + ": " + i));
        RxMain.sleep(2000);
    }

    private static void doDelayOperator() {
        Observable
                .just("a", "b", "c", "d")
                .delay(1, TimeUnit.SECONDS)
                .subscribe(s -> System.out.println(new Date() + ": " + s));
        RxMain.sleep(2000);
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


}
