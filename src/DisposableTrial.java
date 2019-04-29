import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.observers.DisposableObserver;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/18 14:40.
 */

public class DisposableTrial {
    public static void main(String[] args) {
        DisposableTrial disposableTrial = new DisposableTrial();

//        disposableTrial.mannerOne();
        disposableTrial.mannerTwo();

    }

    private void mannerTwo() {

        //创建被观察者
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < 10; i++) {
                                Thread.sleep(1000L);
                                emitter.onNext(i);
                            }
                            emitter.onComplete();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }).subscribe(new Consumer<Integer>() { //注册观察者
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("integer is " + integer);
            }
        });


        try {
            Thread.sleep(3000L);
            disposable.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    private void mannerOne() {

        //创建观察者
        Observer<Integer> observer = new Observer<>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);

                disposable = d;
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("~~onNext~~");
                System.out.println("s is " + integer);
                if (integer > 2) disposable.dispose(); //取消订阅
            }

            @Override
            public void onError(Throwable e) {
                System.out.println("~~onError~~");
                System.out.println("error is " + e);
            }

            @Override
            public void onComplete() {
                System.out.println("~~onComplete~~");
            }
        };


        //创建被观察者
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < 10; i++) {
                                Thread.sleep(1000L);
                                emitter.onNext(i);
                            }
                            emitter.onComplete();

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        observable.subscribe(observer);//注册观察者


    }
}
