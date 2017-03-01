import rx.Observable;

/**
 * @author Mike Dunbar
 */
public class ObservableCreation {

    public static void main(String[] args) {
        doSingleElementSubscribe();
        doNeverSubscription();
    }

    private static void doSingleElementSubscribe() {
        RxMain.log("starting single elem subscribe");
        Observable<Integer> justFive = just(5);
        justFive.subscribe(i -> {RxMain.log("element: " + i);});
        RxMain.log("done");
    }

    private static void doNeverSubscription() {
        RxMain.log("starting never subscribe");
        Observable<Integer> neverInt = never();
        neverInt.subscribe(i -> {RxMain.log("elem: " + i);});
        RxMain.log("done");
    }

    static <T> Observable<T> just(T x) {
        return Observable.create(subscriber -> {
            subscriber.onNext(x);
            subscriber.onCompleted();
        });
    }

    static <T> Observable<T> empty() {
        return Observable.create(subscriber -> {
            subscriber.onCompleted();
        });
    }

    static <T> Observable<T> never() {
        return Observable.create(subscriber -> {});
    }
}
