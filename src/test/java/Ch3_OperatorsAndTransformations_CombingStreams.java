import org.junit.Test;
import rx.Observable;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static rx.Observable.interval;
import static rx.Observable.just;
import static rx.Observable.zip;


/**
 * @author Mike Dunbar
 */
public class Ch3_OperatorsAndTransformations_CombingStreams {

    @Test
    public void testConcatShallEmitAllItemsOfFirstStreamFollowedByAllItemsOfSecondStream() {
        Observable<String> stooges = just("moe", "larry", "curly");
        Observable<String> hucksters = just("Trump", "Spicer", "Bannon");

        List<String> all = new ArrayList<>();
        stooges
                .concatWith(hucksters)
                .subscribe(a -> all.add(a));
        List<String> expected = Arrays.asList("moe", "larry", "curly", "Trump", "Spicer", "Bannon");
        assertEquals(expected, all);
    }

    @Test
    public void testConcatCanCombineASingleSourceStreamMultiipleTimesWithDifferentOperators() {
        Observable<String> colors = just("Trump", "Spicer", "Bannon");
        Observable<String> someColors = Observable.concat(
                colors.take(1),
                colors.takeLast(1)
        );

        List<String> all = new ArrayList<>();
        someColors.subscribe(a -> all.add(a));
        List<String> expected = Arrays.asList("Trump", "Bannon");
        assertEquals(expected, all);
    }

    @Test
    public void testConcatCanServeAsFallbackWhenFirstStreamEmitsNothing() {
        Observable<String> stream1 = Observable.create(subscriber -> {
            subscriber.onCompleted();
        });

        Observable<String> stream2 = just("Trump", "Spicer", "Bannon");

        List<String> vals = new ArrayList<>();
        Observable.concat(
                stream1,
                stream2)
                .first()
                .subscribe(a -> vals.add(a));
        assertEquals(Arrays.asList("Trump"), vals);
    }

    @Test
    public void testMergeShallInterleaveElementsFromCombinedStreamsUnlikeConcat() {
        List<String> stoogeList = Arrays.asList("moe", "larry", "curly");
        List<String> hucksterList = Arrays.asList("Trump", "Spicer", "Bannon");
        List<String> orderedList = Arrays.asList("moe", "larry", "curly", "Trump", "Spicer", "Bannon");


        // Must be a better way to make the streams emit concurrently
        Observable<String> stooges = Observable.create(subscriber -> {
            new Thread(() -> {
                for(String s : stoogeList) {
                    RxMain.sleep(20);
                    System.out.println("Emitting stooge " + s);
                    subscriber.onNext(s);
                }
                subscriber.onCompleted();
            }).start();

        });

        Observable<String> hucksters = Observable.create(subscriber -> {
            new Thread(() -> {
                for(String s : hucksterList) {
                    RxMain.sleep(10);
                    System.out.println("Emitting huckster " + s);
                    subscriber.onNext(s);
                }
                subscriber.onCompleted();
            }).start();
        });

        List<String> mergedList = new ArrayList<>();
        stooges.mergeWith(hucksters).subscribe(a -> mergedList.add(a));
        RxMain.sleep(200);
        assertNotEquals(orderedList, mergedList);

        List<String> concatedList = new ArrayList<>();
        stooges.concatWith(hucksters).subscribe(a -> concatedList.add(a));
        RxMain.sleep(200);
        assertEquals(orderedList, concatedList);
    }

    @Test
    public void testSwitchOnNextShallUnsubscribeFromEachInnerStreamWhenTheNextInnerStreamAppears() {
        Observable<String> stoogeList = zip(
                just("moe", "larry", "curly"),
                interval(500, TimeUnit.MILLISECONDS),
                (item, delay) -> item);
        Observable<String> hucksterList = zip(
                just("Trump", "Spicer", "Bannon"),
                interval(500, TimeUnit.MILLISECONDS),
                (item, delay) -> item);
        Observable<String> heroList = zip(
                just("batman", "wonder woman", "super man", "gi jane"),
                interval(500, TimeUnit.MILLISECONDS),
                (item, delay) -> item);

        Random rnd = new Random();

        Observable<Observable<String>> people = just(
                stoogeList.map(e -> "Stooge: " + e),
                hucksterList.map(e -> "Huckster: " + e),
                heroList.map(e -> "Hero: " + e))
            .flatMap((Observable<String> g) -> {
                Observable<Observable<String>> inner = just(g).delay(rnd.nextInt(5), TimeUnit.SECONDS);
                return inner;
            });

        // TODO: De-randomize this and test for expected behavior. For now, just look at output

        Observable
                .switchOnNext(people)
                .subscribe(e -> RxMain.log(e + " at " + System.currentTimeMillis()));

        RxMain.sleep(10000);


    }

}
