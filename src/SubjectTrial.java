import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.*;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/18.
 */

public class SubjectTrial {

    public static void main(String[] args) {
        SubjectTrial subjectTrial = new SubjectTrial();

//        subjectTrial.publishSubject();
//        subjectTrial.asyncSubject();
//        subjectTrial.replaySubject();
//        subjectTrial.unicastSubject();
        subjectTrial.behaviorSubject();


    }

    private void behaviorSubject() {

        BehaviorSubject<Integer> behaviorSubject = BehaviorSubject.create();
        behaviorSubject.subscribe(new IntegerObserver("one"));
        behaviorSubject.onNext(11);
        behaviorSubject.onNext(22);
        behaviorSubject.subscribe(new IntegerObserver("two"));
        behaviorSubject.onNext(33);
        behaviorSubject.onNext(44);
        behaviorSubject.onNext(55);
        behaviorSubject.subscribe(new IntegerObserver("three"));
        behaviorSubject.onNext(66);
    }

    private void unicastSubject() {

        UnicastSubject<Integer> unicastSubject = UnicastSubject.create();
        unicastSubject.subscribe(new IntegerObserver("one"));
        unicastSubject.subscribe(new IntegerObserver("two"));
        Observable.just(11, 22, 33, 44).subscribe(unicastSubject);

    }

    private void replaySubject() {
        ReplaySubject<Integer> replaySubject = ReplaySubject.create();
        replaySubject.subscribe(new IntegerObserver("one"));
        Observable.just(11, 22, 33, 44).subscribe(replaySubject);
        replaySubject.subscribe(new IntegerObserver("two"));
    }

    private void asyncSubject() {

        AsyncSubject<Integer> asyncSubject = AsyncSubject.create();
        asyncSubject.subscribe(new IntegerObserver("one"));
        asyncSubject.subscribe(new IntegerObserver("two"));
        Observable.just(11, 22, 33, 44).subscribe(asyncSubject);


    }

    private void publishSubject() {

        //方式一
//        PublishSubject<Integer> publishSubject = PublishSubject.create();
//
//        publishSubject.subscribe(new IntegerObserver("one"));
//        publishSubject.onNext(1);
//        publishSubject.onNext(2);
//        publishSubject.subscribe(new IntegerObserver("two"));
//        publishSubject.onNext(3);
//        publishSubject.onComplete();


        //方式二
//        PublishSubject<Integer> publishSubject = PublishSubject.create();
//
//        publishSubject.subscribe(new IntegerObserver("one"));
//        publishSubject.map(n -> n * 2).subscribe(new IntegerObserver("two"));
//
//        Observable<Integer> observable = Observable.just(11, 22, 33, 44);
//        observable.subscribe(publishSubject);


        //方式三
        Subject<Object> publishSubject = PublishSubject.create()
                .toSerialized();

        publishSubject.cast(Integer.class).subscribe(new IntegerObserver("one"));
        publishSubject.cast(Integer.class).subscribe(new IntegerObserver("two"));
        Observable<Integer> observable = Observable.just(11, 22, 33, 44);
        observable.subscribe(publishSubject);


    }


    class IntegerObserver implements Observer<Integer> {
        Disposable disposable = null;
        String tag;

        public IntegerObserver(String tag) {
            this.tag = tag;
        }

        @Override
        public void onSubscribe(Disposable d) {
            System.out.println("[" + tag + "]" + "~~onSubscribe~~");
            System.out.println("[" + tag + "]" + "Disposable is " + d.hashCode() + "|" + d);

            disposable = d;
        }

        @Override
        public void onNext(Integer integer) {
            System.out.println("[" + tag + "]" + "~~onNext~~");
            System.out.println("[" + tag + "]" + "s is " + integer);
//            if (integer > 20) disposable.dispose();
        }

        @Override
        public void onError(Throwable e) {
            System.out.println("[" + tag + "]" + "~~onError~~");
            System.out.println("[" + tag + "]" + "error is " + e);
        }

        @Override
        public void onComplete() {
            System.out.println("[" + tag + "]" + "~~onComplete~~");
        }
    }
}

