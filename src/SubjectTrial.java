import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.Subject;

/**
 * Created by Administrator on 2019/4/18.
 */

public class SubjectTrial {

    Subject<String> subject = new Subject<String>() {
        @Override
        public boolean hasObservers() {
            System.out.println("~~hasObservers~~");
            return false;
        }

        @Override
        public boolean hasThrowable() {
            System.out.println("~~hasThrowable~~");
            return false;
        }

        @Override
        public boolean hasComplete() {
            System.out.println("~~hasComplete~~");
            return false;
        }

        @Override
        public Throwable getThrowable() {
            System.out.println("~~getThrowable~~");
            return null;
        }

        @Override
        protected void subscribeActual(Observer<? super String> observer) {
            System.out.println("~~subscribeActual~~");
            System.out.println("observer is " + observer);
        }

        @Override
        public void onSubscribe(Disposable d) {
            System.out.println("~~onSubscribe~~");
            System.out.println("disposable is " + d);
        }

        @Override
        public void onNext(String s) {
            System.out.println("~~onNext~~");
            System.out.println("string is " + s);
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("~~onError~~");
            System.out.println("Throwable is " + e);
        }

        @Override
        public void onComplete() {
            System.out.println("~~onComplete~~");
        }
    };

    public static void main(String[] args) {
        SubjectTrial subjectTrial = new SubjectTrial();

    }
}
