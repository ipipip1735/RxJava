import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/18 13:14.
 */

public class SchedulerTrial {
    public static void main(String[] args) {
        SchedulerTrial schedulerTrial = new SchedulerTrial();

        schedulerTrial.scheduler();




//        schedulerTrial.delay();
//        schedulerTrial.interval();
//        schedulerTrial.timer();
//        schedulerTrial.sampler();


    }


    private void sampler() {

        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            emitter.onNext(1);
                            emitter.onNext(2);
                            Thread.sleep(200L);
                            emitter.onNext(3);
                            Thread.sleep(200L);
                            emitter.onNext(4);//发送，因为后面要停800毫秒，加前面的400毫秒，已经超过1秒了
                            Thread.sleep(800L);
                            emitter.onNext(5);
                            emitter.onNext(6);
                            emitter.onNext(7);
                            emitter.onNext(8);
                            emitter.onComplete();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }).sample(1, TimeUnit.SECONDS);//每秒采样一次
        observable.subscribe(System.out::println);
    }

    private void timer() {
        Observable<Long> observable = Observable.timer(1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
        observable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                System.out.println(Thread.currentThread());
                System.out.println("aLong is " + aLong);
            }
        });

        try {
            Thread.sleep(30000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void interval() {

        Observable<Long> observable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
        observable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                System.out.println(Thread.currentThread());
                System.out.println("aLong is " + aLong);
            }
        });

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void scheduler() {

        //方式一：使用内置守护线程
//        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                emitter.onNext("aaa");
//                emitter.onNext("bbb");
//                System.out.println("ssss");
//                emitter.onNext("ccc");
//                emitter.onNext("dddd");
//                emitter.onComplete();
//                System.out.println(Thread.currentThread());
//            }
//        }).subscribeOn(Schedulers.io()); //使用调度器创建的守护线程
//
//        observable.observeOn(Schedulers.io()) //使用调度器创建的守护线程
//        .subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                System.out.println(s);
//                System.out.println(Thread.currentThread());
//            }
//        });
//
//        try {
//            Thread.sleep(3000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }





        //方式二：自定义线程
        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            emitter.onNext("aaa");
                            emitter.onNext("bbb");
                            Thread.sleep(1000L);
                            System.out.println("ssss");
                            emitter.onNext("ccc");
                            emitter.onNext("dddd");
                            emitter.onComplete();
                            System.out.println(Thread.currentThread());

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        observable.observeOn(Schedulers.io())//使用调度器创建的守护线程
                .subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println(s);
                System.out.println(Thread.currentThread());

            }
        });

    }

    private void delay() {

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                emitter.onNext("aaa");
                emitter.onNext("bbb");
                System.out.println("ssss");
                emitter.onNext("ccc");
                emitter.onNext("dddd");
                emitter.onComplete();
                System.out.println(Thread.currentThread());
            }
        }).subscribeOn(Schedulers.io())
                .delay(2, TimeUnit.SECONDS);//所有onNext()延迟发送，其他不会延迟

        observable.subscribe(System.out::println);


        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
