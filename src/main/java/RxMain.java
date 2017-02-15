import com.google.common.base.Strings;
import com.google.common.util.concurrent.MoreExecutors;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Mike Dunbar
 */
public class RxMain {

    public static void main(String... args) {
        basicStreamTransform();
        asyncStreamTransform();
    }

    private static void basicStreamTransform() {
        logThreadState("Basic / Sync transform start");
        List<String> start = Arrays.asList("my", "name", "is", "barr");
        System.out.println("Start: " + start);
        Observable.from(start)
                .map((s) -> {
                    logThreadState("convert to upper case");
                    return Strings.isNullOrEmpty(s) ? s : s.toUpperCase();
                })
                .toList()
                .map((list) -> {
                    logThreadState("reverse list");
                    Collections.reverse(list);
                    return list.toString();
                })
                .subscribe(s -> System.out.println("Finish: " + s));
        logThreadState("Basic / sync transform end");
    }

    private static void asyncStreamTransform() {
        logThreadState("Async transform start");
        Observable.fromCallable(() -> {
            logThreadState("create from Callable");
            return Arrays.asList("one", "two", "three");
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .flatMap((list) -> {
                    logThreadState("flat map");
                    return rx.Observable.from(list);
                })
                .observeOn(Schedulers.from(MoreExecutors.directExecutor()))
                .map((s) -> {
                    logThreadState("convert to upper case");
                    return s.toUpperCase();
                })
                .subscribe((s) -> {
                    logThreadState(String.format("subscribe method, received %s", s));
                });
        // Give the background threads time to complete
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logThreadState("Async transform done");
    }


    private static void logThreadState(String desc) {
        Thread t = Thread.currentThread();
        System.out.println(String.format("%s - current thread: %s", desc, t.getName()));
    }
}
