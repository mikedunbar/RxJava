import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static rx.Observable.empty;
import static rx.Observable.just;

/**
 * @author Mike Dunbar
 */
public class Ch3_CustomOperators {

    Observable<String> words = just("red", "one", "blue", "two");

    Observable<Boolean> trueFalse = just(true, false, true, false);

    @Test
    public void testZipWithBooleanAndFilterShallProvideEveryOddElement() {
        Observable<String> oddWords = words.zipWith(
                trueFalse,
                (s, bool) -> bool ? just(s) : Observable.<String>empty())
              .flatMap(obs -> obs);

        oddWords.toList().subscribe(l -> {
            List<String> expected = Arrays.asList("red", "blue");
            assertEquals(expected, l);
        });
    }

    @Test
    public void testComposeWithAnOddElementFilterShallAllowFluentStyle() {
        words
            .compose(odd())
            .last()
            .toList().subscribe(s -> {
                assertEquals(Arrays.asList("blue"), s);
            });
    }

    private <T> Observable.Transformer<T, T> odd() {
        return words ->
                words.zipWith(trueFalse, (s, bool) -> bool ? just(s) : Observable.<T>empty())
                .flatMap(obs -> obs);
    }
}
