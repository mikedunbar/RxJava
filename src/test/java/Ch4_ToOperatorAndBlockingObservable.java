import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.observables.BlockingObservable;

import static org.junit.Assert.assertEquals;

/**
 * @author Mike Dunbar
 */

public class Ch4_ToOperatorAndBlockingObservable {

    @Test
    public void testObservableToListToBlockingShallConvertStreamToSingleListItem() {
        List<String> origStringList = Arrays.asList("one", "two", "three", "four");

        Observable<String> stringObservable = Observable.from(origStringList);
        Observable<List<String>> stringListObservable = stringObservable.toList();
        BlockingObservable<List<String>> stringListBlockingObservable = stringListObservable.toBlocking();
        List<String> stringList = stringListBlockingObservable.single();

        assertEquals(origStringList, stringList);
    }

}
