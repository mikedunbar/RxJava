import org.junit.Test;
import rx.Observable;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static rx.Observable.range;

/**
 * @author Mike Dunbar
 */
public class Ch2_CreatingObservables {

    @Test
    public void testRangeShallEmitValuesOneThroughNineGivenArgsOneAndNine() {
        Observable.range(1,9)
                .toList()
                .subscribe(l -> {
                    List<Integer> expected = Arrays.asList(1,2,3,4,5,6,7,8,9);
                    assertEquals(expected, l);
                });
    }
}
