import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


/**
 * @author Mike Dunbar
 */
public class Ch3_CombingStreams {

    @Test
    public void testConcatShallEmitAllItemsOfFirstStreamFollowedByAllItemsOfSecondStream() {
        Observable<String> stooges = Observable.just("moe", "larry", "curly");
        Observable<String> hucksters = Observable.just("Trump", "Spicer", "Bannon");

        List<String> all = new ArrayList<>();
        stooges
                .concatWith(hucksters)
                .subscribe(a -> all.add(a));
        List<String> expected = Arrays.asList("moe", "larry", "curly", "Trump", "Spicer", "Bannon");
        assertEquals(expected, all);
    }

    @Test
    public void testConcatShallCombineTheSameStreamWithDifferentOperators() {
        Observable<String> colors = Observable.just("Trump", "Spicer", "Bannon");
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

        Observable<String> stream2 = Observable.just("Trump", "Spicer", "Bannon");

        List<String> vals = new ArrayList<>();
        Observable.concat(
                stream1,
                stream2)
                .first()
                .subscribe(a -> vals.add(a));
        assertEquals(Arrays.asList("Trump"), vals);
    }

    @Test
    public void testMergeShallInterleaveElementsFromTheStreamsUnlikeConcat() {
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
}
