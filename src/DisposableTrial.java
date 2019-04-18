import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/18 14:40.
 */

public class DisposableTrial {
    public static void main(String[] args) {
        DisposableTrial disposableTrial = new DisposableTrial();

        disposableTrial.base();


//        Disposable d = Observable.just("Hello world!")
//                .delay(1, TimeUnit.SECONDS)
//                .subscribeWith(new DisposableObserver<String>() {
//                    @Override public void onStart() {
//                        System.out.println("Start!");
//                    }
//                    @Override public void onNext(String t) {
//                        System.out.println(t);
//                    }
//                    @Override public void onError(Throwable t) {
//                        t.printStackTrace();
//                    }
//                    @Override public void onComplete() {
//                        System.out.println("Done!");
//                    }
//                });
//
//        try {
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        // the sequence can now be disposed via dispose()
////        d.dispose();



    }

    private void base() {

//        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                for (int i = 0; i < 10; i++) {
//                emitter.onNext(i);
//
//                }
//                emitter.onComplete();
//            }
//        });


        Observable<Integer> observable = Observable.fromArray(1,2,3);


        //创建观察者
        DisposableObserver<Integer> disposableObserver = new DisposableObserver<>() {

            @Override
            public void onNext(Integer o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);
                System.out.println(this.isDisposed());

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("~~onError~~");

            }

            @Override
            public void onComplete() {
                System.out.println("~~onComplete~~");

            }
        };

        Disposable disposable = observable.subscribeWith(disposableObserver);
//        disposable.dispose();

    }
}
