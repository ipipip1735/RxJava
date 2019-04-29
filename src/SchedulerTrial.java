import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/18 13:14.
 */

public class SchedulerTrial {
    public static void main(String[] args) {
        SchedulerTrial schedulerTrial = new SchedulerTrial();


        /*---显式调用---*/
        schedulerTrial.schedulerFrom();
//        schedulerTrial.schedulerIO();
//        schedulerTrial.scheduler();


        /*---隐式调用---*/
//        schedulerTrial.from();
//        schedulerTrial.delay();
//        schedulerTrial.interval();
//        schedulerTrial.timer();
//        schedulerTrial.sampler();


    }

    private void schedulerIO() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        }).observeOn(Schedulers.io())
                .map(integer -> {
                    System.out.println("map|" + Thread.currentThread());
                    return String.valueOf(integer);
                })
//                .observeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
//                .observeOn(Schedulers.trampoline())
                .subscribe(s -> System.out.println("subscribe|" + Thread.currentThread()));

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void schedulerFrom() {

        //创建三个执行器
        Executor executor1 = new Executor() {
            @Override
            public void execute(Runnable command) {
                System.out.println("~~execute1~~");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("~~run1.start~~");
                        System.out.println(Thread.currentThread());
                        command.run();
                        System.out.println("~~run1.end~~");
                    }
                }, "mThread1").start();
            }
        };
        Executor executor2 = new Executor() {
            @Override
            public void execute(Runnable command) {
                System.out.println("~~execute2~~");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("~~run2.start~~");
                        System.out.println(Thread.currentThread());
                        command.run();
                        System.out.println("~~run2.end~~");
                    }
                }, "mThread2").start();
            }
        };
        Executor executor3 = new Executor() {
            @Override
            public void execute(Runnable command) {
                System.out.println("~~execute3~~");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("~~run3.start~~");
                        System.out.println(Thread.currentThread());
                        command.run();
                        System.out.println("~~run3.end~~");
                    }
                }, "mThread3").start();
            }
        };

        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("~~create~~");
                System.out.println("create|" + Thread.currentThread());
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
            }
        }).subscribeOn(Schedulers.from(executor1)) //顺序不限制
                .observeOn(Schedulers.from(executor2)) //指定下游顺序
                .map(integer -> {
                    System.out.println("~~map~~");
                    System.out.println("map|" + Thread.currentThread());
                    return "one";
                })
                .observeOn(Schedulers.from(executor3)) //指定下游顺序
                .subscribe(integer -> {
                    System.out.println("~~subscribe~~");
                    System.out.println("subscribe|" + Thread.currentThread());
                });
    }


    private void scheduler() {

        //方式一：使用内置守护线程
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
        }).subscribeOn(Schedulers.io()); //使用调度器创建的守护线程

        observable.observeOn(Schedulers.io()) //使用调度器创建的守护线程
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println(s);
                        System.out.println(Thread.currentThread());
                    }
                });

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        //方式二：自定义线程
//        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            emitter.onNext("aaa");
//                            emitter.onNext("bbb");
//                            Thread.sleep(1000L);
//                            System.out.println("ssss");
//                            emitter.onNext("ccc");
//                            emitter.onNext("dddd");
//                            emitter.onComplete();
//                            System.out.println(Thread.currentThread());
//
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//            }
//        });
//
//        observable.observeOn(Schedulers.io())//使用调度器创建的守护线程
//                .subscribe(new Consumer<String>() {
//                    @Override
//                    public void accept(String s) throws Exception {
//                        System.out.println(s);
//                        System.out.println(Thread.currentThread());
//
//                    }
//                });

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
