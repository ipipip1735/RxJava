import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.ResourceObserver;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/18 14:40.
 */

public class DisposableObserver {
    public static void main(String[] args) {
        DisposableObserver disposableTrial = new DisposableObserver();

//        disposableTrial.base();
        disposableTrial.resourceObserver();

    }


    private void resourceObserver() {


        Disposable disposable = Observable.range(1, 5)
                .subscribeWith(new ResourceObserver<Integer>() {
                    @Override
                    public void onNext(@NonNull Integer integer) {
                        System.out.println("~~.onNext~~");
                        System.out.println("integer = " + integer);
                        add(Schedulers.single().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000L);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("Loading URL" + integer);
                            }
                        }));
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("~~onError~~");
                        dispose();
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("~~onComplete~~");
                        try {
                            Thread.sleep(20000L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        dispose();
                    }
                });



//        disposable.dispose();

    }


    private void base() {
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


        //创建观察者
        io.reactivex.observers.DisposableObserver<Integer> disposableObserver = new io.reactivex.observers.DisposableObserver<>() {

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


        //获取处理器
        Disposable disposable = observable
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("~~doOnDispose.Action.run~~");
                    }
                })
                .subscribeWith(disposableObserver);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000L);
                    disposable.dispose();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
}
