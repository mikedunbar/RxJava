import org.junit.Test;
import rx.Observable;
import rx.Subscriber;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static rx.Observable.empty;
import static rx.Observable.just;
import static rx.Observable.range;

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

    @Test
    public void testToStringOfOddCustomOperatorShallEmitStringValuesOfAllOddElement() {
        range(1, 10)
            .lift(toStringOfOdd())
            .toList().subscribe(l -> {
            List<String> expected = Arrays.asList("1", "3", "5", "7", "9");
            assertEquals(expected, l);  
        });
    }


    <T> Observable.Operator<String, T> toStringOfOdd() {
        return new Observable.Operator<String, T>() {
            private boolean odd = true;

            public Subscriber<? super T> call(Subscriber<? super String> child) {
                return new Subscriber<T>(child) {
                    @Override
                    public void onCompleted() {
                        child.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onNext(T t) {
                        if (odd) {
                            child.onNext(t.toString());
                        } else {
                            request(1);
                        }
                        odd = !odd;
                    }
                };
            }
        };
    }

}
