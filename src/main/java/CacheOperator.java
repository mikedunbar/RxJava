import rx.Observable;

/**
 * @author Mike Dunbar
 */
public class CacheOperator {

    public static void main (String[] args) {
        // No caching
        Observable<Integer> ints = Observable.create(subscriber -> {
            RxMain.log("create");
            subscriber.onNext(42);
            subscriber.onCompleted();
        });

        RxMain.log("start no cache");
        ints.subscribe(i -> {RxMain.log("elem a: " + i);});
        ints.subscribe(i -> {RxMain.log("elem b: " + i);});
        RxMain.log("finished no cache");

        // Caching
        Observable<Integer> cachedInts = ints.cache();

        RxMain.log("start with cache");
        cachedInts.subscribe(i -> {RxMain.log("elem a: " + i);});
        cachedInts.subscribe(i -> {RxMain.log("elem b: " + i);});
        RxMain.log("finished with cache");
    }
}
