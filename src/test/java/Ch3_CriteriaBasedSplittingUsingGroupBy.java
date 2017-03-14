import org.junit.Test;
import rx.Observable;
import rx.observables.GroupedObservable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Mike Dunbar
 */
public class Ch3_CriteriaBasedSplittingUsingGroupBy {

    @Test
    public void testGroupByShallSplitStreamIntoSubStreamsBasedOnKey() {
        Observable<GroupedObservable<Integer, String>> grouped = Observable.just("one", "blue", "my", "you", "to", "crew")
                .groupBy(s -> s.length());

        grouped.toSortedList((e1, e2) -> e1.getKey().compareTo(e2.getKey())).subscribe(groupList -> {
            assertEquals(3, groupList.size());

            groupList.get(0).toList().subscribe(strings -> {
                List<String> expected = Arrays.asList("my", "to");
                assertEquals(expected, strings);
            });

            groupList.get(1).toList().subscribe(strings -> {
                List<String> expected = Arrays.asList("one", "you");
                assertEquals(expected, strings);
            });

            groupList.get(2).toList().subscribe(strings -> {
                List<String> expected = Arrays.asList("blue", "crew");
                assertEquals(expected, strings);
            });

        });

    }
}
