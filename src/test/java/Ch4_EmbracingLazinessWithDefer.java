import org.junit.Test;

import rx.Observable;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * @author Mike Dunbar
 */

public class Ch4_EmbracingLazinessWithDefer {

    @Test
    public void testDeferShallNotExecuteFactoryToGenerateObsevableUntilSubscription() {
        Tracker tracker = new Tracker();
        Observable<Integer> deferedObservable = Observable.defer(() -> {
            tracker.hasBeenCalled = true;
            return Observable.just(1, 2, 3, 4, 5, 6);});
        deferedObservable.first();
        assertFalse(tracker.hasBeenCalled);
        deferedObservable.subscribe(e -> {});
        assertTrue(tracker.hasBeenCalled);
    }

    static class Tracker {
        boolean hasBeenCalled;

    }
}
