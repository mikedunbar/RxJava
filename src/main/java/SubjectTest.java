import rx.subjects.PublishSubject;

/**
 * @author Mike Dunbar
 */
public class SubjectTest {



    public static void main(String[] args) {
        PublishSubject<String> subject = PublishSubject.create();

        subject.subscribe(
                next -> {RxMain.log("Observer1 onNext: " + next);},
                error -> {RxMain.log("Observer1 onError: " + error);},
                () -> {RxMain.log("Observer1 onCompleted: done");});
        subject.onNext("one");
        subject.onNext("two");

        subject.subscribe(
                next -> {RxMain.log("Observer2 onNext: " + next);},
                error -> {RxMain.log("Observer2 onError: " + error);},
                () -> {RxMain.log("Observer2 onCompleted: done");});

        subject.onNext("three");
        subject.onCompleted();
        subject.onError(new RuntimeException("shit went wrong"));
        subject.onError(new RuntimeException("shit still going wrong"));
        subject.onNext("four");

    }
}
