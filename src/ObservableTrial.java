import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/17.
 */

public class ObservableTrial {

    public static void main(String[] args) {
        ObservableTrial observableTrial = new ObservableTrial();


        /*================创建观察者========================*/
//        observableTrial.create();
//        observableTrial.from();
//        observableTrial.just();
//        observableTrial.range();
//        observableTrial.repeat();
//        observableTrial.emptyNeverError();
        /*-------异步创建----------*/
//        observableTrial.timer(); //定时发送一次
//        observableTrial.interval(); //间隔发送
//        observableTrial.sampler();
//        observableTrial.delay();//延迟指定时间开始发送首个onNext事件
        /*-------其他创建----------*/
//        observableTrial.defer(); //延迟到订阅时创建被观察者
//        observableTrial.using();//发送完成后销毁资源

        /*=================过滤函数=======================*/
//        observableTrial.distinct();
//        observableTrial.elementAt();
        /*-----------------*/
//        observableTrial.amb(); //使用最先抵达者
//        observableTrial.debounce(); //过滤间隔时间较短的事件
        /*-----------------*/
//        observableTrial.filter();
//        observableTrial.first();
//        observableTrial.last();
//        observableTrial.take();
//        observableTrial.skip();


        /*=============中间操作===========================*/
//        observableTrial.map();
//        observableTrial.flatMap();
//        observableTrial.buffer();
//        observableTrial.groupBy();
//        observableTrial.window();
//        observableTrial.scan();
        observableTrial.zip();
//        observableTrial.combineLatest();
//        observableTrial.join();//链接多个被观察者，多个被观察者并行发送
//        observableTrial.merge();



        /*===================判断函数=====================*/
//        observableTrial.contains();
//        observableTrial.all();


        /*===================聚合函数=====================*/
//        observableTrial.concat();//合并多个被观察者，多个被观察者按顺序依次发送
//        observableTrial.count();
//        observableTrial.reduce();
//        observableTrial.collect();



        /*===================钩子函数=====================*/
//        observableTrial.doOnSubscribe();
//        observableTrial.doOnError();
//        observableTrial.doOnNext();
//        observableTrial.doOnComplete();
//        observableTrial.doOnDispose();
        /*-----------------*/
//        observableTrial.doOnEach();
//        observableTrial.doOnLifecycle();
//        observableTrial.doOnTerminate();



        /*==================连接函数=====================*/
//        observableTrial.publish();



        /*==================转换函数=====================*/
        observableTrial.to();

    }


    private void to() {

        /*
         * 将 被观察者 转换成 任意对象
         */
        Observable<Integer> observable = Observable.just(1);
        String info = observable.to(new Function<Observable<Integer>, String>() {
            @Override
            public String apply(Observable<Integer> integerObservable) throws Exception {
                System.out.println("integerObservable is " + integerObservable);
                String size = integerObservable.count().toString();
                return size;
            }
        });

        System.out.println(info);


    }

    private void publish() {
        ConnectableObservable<Integer> connectableObservable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            for (int i = 0; i < 6; i++) {
                                emitter.onNext(Integer.valueOf(i));
                                Thread.sleep(1000L);
                            }
                            emitter.onComplete();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        }).sample(1, TimeUnit.SECONDS).publish();

        connectableObservable.subscribe(integer -> System.out.println("one|" + integer));//不会理解发送，等到connect()方法调用后发送
        connectableObservable.subscribe(integer -> System.out.println("two|" + integer));

        connectableObservable.connect();//发送

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connectableObservable.subscribe(integer -> System.out.println("three|" + integer));


    }

    private void delay() {
        //方法一
//        Observable<Integer> observable = Observable.fromArray(1, 2, 3);
//        observable.delay(1, TimeUnit.SECONDS)
//                .subscribe(System.out::println);
//

        //方法二
        //创建观察者
        Observer observer = new Observer<Object>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);

                disposable = d;
            }

            @Override
            public void onNext(Object o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);

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

        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("~~create.subscribe~~");
                emitter.onNext(1);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onComplete();
            }
        })
                .doOnNext(integer -> System.out.println("doOnNext|" + integer));
        observable.delay(3, TimeUnit.SECONDS).subscribe(observer);


        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
                            Thread.sleep(300L);
                            emitter.onNext(4);//发送，因为后面要停800毫秒，加前面的400毫秒，已经超过1秒了
                            Thread.sleep(800L);
                            emitter.onNext(5);
                            emitter.onNext(6);
                            Thread.sleep(700L);//前面200+300+800，还差700秒，正好是2秒
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
        //timer是定时器，超时后执行一次
        Observable<Long> observable = Observable.timer(1000L, TimeUnit.MILLISECONDS);
        observable.subscribe(System.out::println);

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void interval() {

        Observable<Long> observable = Observable.interval(1000L, TimeUnit.MILLISECONDS);
        observable.subscribe(System.out::println);

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void first() {
        Single<Integer> single = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                for (int i = 0; i < 4; i++) {
//                    System.out.println("~~create.ObservableOnSubscribe.subscribe~~");
//                    emitter.onNext(i);
//                }
//                emitter.onError(new Throwable("XXX"));
                emitter.onComplete();
            }
        }).first(-2);//如果直接发送onComplete()而无onNext(),则发送默认值-2
        single.subscribe(System.out::println);
    }


    private void last() {
        Single<Integer> single = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 4; i++) {
                    System.out.println("~~create.ObservableOnSubscribe.subscribe~~");
                    emitter.onNext(i);
                }
//                emitter.onError(new Throwable("XXX"));
                emitter.onComplete();
            }
        }).last(-2);//如果直接发送onComplete()而无onNext(),则发送默认值-2
        single.subscribe(System.out::println);
    }


    private void take() {
        Observable.range(0, 4)
//                .take(2)//取前2个
                .takeLast(2)//取后2个
                .subscribe(System.out::println);
    }


    private void skip() {
        Observable.range(0, 4)
//                .skip(2)//跳过前2个
                .skipLast(2)//跳过后2个
                .subscribe(System.out::println);
    }


    private void filter() {
        Observable<Integer> observable = Observable.range(2, 5)
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        return integer.intValue() > 4;
                    }
                });

        observable.subscribe(System.out::println);
    }

    private void debounce() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            emitter.onNext(1);
                            emitter.onNext(2);
                            Thread.sleep(2000L);//间隔大于1秒，所以2可以发送
                            emitter.onNext(3);
                            Thread.sleep(900L);//间隔小于1秒，3被过滤
                            emitter.onNext(4);
                            Thread.sleep(900L);//间隔小于1秒，3被过滤
                            emitter.onNext(5);
                            emitter.onNext(6);
                            emitter.onNext(7);
                            emitter.onNext(8); //发送，最后一个所以直接发送
                            emitter.onComplete();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        observable.debounce(1, TimeUnit.SECONDS)//间隔大于1秒
                .subscribe(System.out::println);

    }


    private void using() {

        int n = 4;
        Random random = new Random();
        List<Integer> dataSet = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            dataSet.add(Integer.valueOf(random.nextInt(99)));
        }


        //创建数据集
        Callable<List<Integer>> callable = new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() throws Exception {
                System.out.println("~~Callable.call~~");
                return dataSet;
            }
        };


        //创建被观察者
        Function<List<Integer>, Observable<Integer>> function = new Function<List<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(List<Integer> list) throws Exception {
                System.out.println("~~Function.apply~~");
                System.out.println(list);
                for (Integer i : list) System.out.println(i);
                return Observable.fromArray(dataSet.toArray(new Integer[dataSet.size()]));//根据数据集创建被观察者
            }
        };

        //创建回收器
        Consumer<List<Integer>> consumer = new Consumer<List<Integer>>() {
            @Override
            public void accept(List<Integer> list) throws Exception {
                System.out.println("~~Consumer.accept~~");
                System.out.println(list);
                while (list.iterator().hasNext()) list.remove(0);//释放资源
            }
        };

        Observable.using(callable, function, consumer).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("~~using.accept~~");
                System.out.println(integer);
            }
        });

        System.out.println("-----over-----");
        System.out.println(dataSet);
    }


    private void concat() {
        /*
         * concat()是串行发送，而merge()将并行发送
         * */

        //创建2个被观察者，让他们工作在各自的线程中
        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(1000L);
                emitter.onNext(2);
                emitter.onNext(3);
                Thread.sleep(1000L);
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).map(integer -> "A|" + integer);

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(1000L);
                emitter.onNext(2);
                emitter.onNext(3);
                Thread.sleep(1000L);
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).map(integer -> "B|" + integer);


        Observable.concat(observable1, observable2)//合并2个被观察者
                .subscribe(integer -> {
                    System.out.println("integer is " + integer);
                    System.out.println(Thread.currentThread());
                    System.out.println("-------------");
                });


        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void count() {
        Single<Long> single = Observable.fromArray(1, 1, 2, 2)
                .count();
        single.subscribe(System.out::println);
    }


    private void reduce() {
        Maybe<Integer> maybe = Observable.fromArray(1, 1, 2, 2)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        System.out.println("~~reduce.BiFunction.apply~~");
                        System.out.println("integer is " + integer);
                        System.out.println("integer2 is " + integer2);
                        return integer + integer2;
                    }
                });


        maybe.subscribe(System.out::println);

    }


    private void contains() {
        Observable<Integer> observable = Observable.fromArray(1, 1, 2, 2);
        observable.contains(3).subscribe(System.out::println);

    }


    private void distinct() {
        Observable.fromArray(1, 4, 5, 5, 1, 3)
                .distinct()
                .subscribe(System.out::println);
    }

    private void elementAt() {
        Observable.range(9, 3)
                .elementAt(1) //过滤所有元素
                .subscribe(System.out::println);
    }

    private void amb() {
        Observable<Integer> observable1 = Observable.fromArray(1, 1, 2, 2);
        Observable<Integer> observable2 = Observable.fromArray(19, 19, 29, 223);
        List<Observable<Integer>> list = new ArrayList<>();
        list.add(observable1);
        list.add(observable2);

        Observable.amb(list).subscribe(System.out::println);
    }

    private void all() {
        Single<Boolean> single = Observable.fromArray(1, 1, 2, 2)
                .all(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) throws Exception {
                        System.out.println("~~all.Predicate.test~~");
                        System.out.println("integer is " + integer);
                        return integer > 3;
                    }
                });

        single.subscribe(System.out::println);
    }


    private void doOnComplete() {
    }


    private void doOnEach() {

        //创建观察者
        Observer observer = new Observer<Object>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);

                disposable = d;
            }

            @Override
            public void onNext(Object o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);

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


        //创建发射器
        ObservableOnSubscribe<String> observableOnSubscribe = new ObservableOnSubscribe<>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                System.out.println("~~ObservableOnSubscribe.subscribe~~");
                for (int i = 0; i < 5; i++) {
                    emitter.onNext(String.valueOf(i));
                }
                emitter.onError(new Throwable("XXX"));
                emitter.onComplete();
            }
        };

        //创建被观察者
        Observable<String> observable = Observable.create(observableOnSubscribe)
                .doOnEach(new Consumer<Notification>() {//onNext, onError and onComplete调用前先调用本方法
                    @Override
                    public void accept(Notification notification) throws Exception {
                        System.out.println("~~doOnEach.Consumer.accept~~");
                        System.out.println("notification is " + notification);
                        System.out.println("isOnComplete is " + notification.isOnComplete());
                        System.out.println("isOnError is " + notification.isOnError());
                        System.out.println("isOnNext is " + notification.isOnNext());

                    }
                });
        observable.subscribe(observer); //注册观察者

    }

    private void doOnDispose() {
    }

    private void doOnSubscribe() {
    }

    private void doOnError() {
    }

    private void doOnLifecycle() {

        //创建观察者
        Observer observer = new Observer<Integer>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);
                disposable = d;
            }

            @Override
            public void onNext(Integer o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);
                if (o > 1) disposable.dispose();//取消订阅
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


        //创建发射器
        ObservableOnSubscribe<Integer> observableOnSubscribe = new ObservableOnSubscribe<>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("~~ObservableOnSubscribe.subscribe~~");


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < 5; i++) {
                            emitter.onNext(Integer.valueOf(i));
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
//                emitter.onError(new Throwable("XXX"));
                        emitter.onComplete();
                    }
                }).start();

            }
        };

        //创建被观察者
        Observable<Integer> observable = Observable.create(observableOnSubscribe)
                .doOnLifecycle(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("~~doOnLifecycle.Consumer.accept~~");
                        System.out.println("disposable is " + disposable.hashCode() + "|" + disposable);
                    }
                }, new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("~~doOnLifecycle.Action.run~~");
                    }
                });
        observable.subscribe(observer); //注册观察者
    }


    private void doOnTerminate() {
        //创建观察者
        Observer observer = new Observer<Integer>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);
                disposable = d;
            }

            @Override
            public void onNext(Integer o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);
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


        //创建发射器
        ObservableOnSubscribe<Integer> observableOnSubscribe = new ObservableOnSubscribe<>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("~~ObservableOnSubscribe.subscribe~~");
//                emitter.onError(new Throwable("xxxx"));
                emitter.onComplete();
            }
        };

        //创建被观察者
        Observable<Integer> observable = Observable.create(observableOnSubscribe)
                .doOnTerminate(new Action() { //onComplete()或onError()触发
                    @Override
                    public void run() throws Exception {
                        System.out.println("~~doOnTerminate.Action.run~~");
                    }
                });
        observable.subscribe(observer); //注册观察者

    }


    private void doOnNext() {

        //方式一：实现接口
//        Observable<Integer> observable = Observable.fromArray(1, 1, 2, 2);
//        observable.doOnNext(new Consumer<Integer>() {
//            @Override
//            public void accept(Integer integer) throws Exception {
//                System.out.println("~~doOnNext.accept~~");
//                System.out.println("integer is " + integer);
//            }
//        });
//        observable.subscribe();


        //方式二：使用Lambda表达式
        Observable<Integer> observable = Observable.fromArray(1, 1, 2, 2)
                .doOnNext(System.out::println);
        observable.subscribe();

    }
    private void combineLatest() {

        /*
        * combineLatest()和zip()类似，去内部buffer中的值来合并
        * 但zip是同步的，combineLatest是并发的
        * */
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(1000L);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<Integer> observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(111);
                emitter.onNext(112);
                emitter.onNext(113);
                Thread.sleep(2000L);
                emitter.onNext(114);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.combineLatest(observable1, observable2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("~~combineLatest.BiFunction.apply~~");
                return integer.toString() + "-" + integer2.toString();
            }
        }).subscribe(System.out::println);

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void zip() {

        /*
         * zip()和combineLatest()类似，去内部buffer中的值来合并
         * zip是同步的，combineLatest是并发的
         * */
        Observable<Integer> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(1000L);
                emitter.onNext(2);
                emitter.onNext(3);
                emitter.onNext(4);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable<Integer> observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(111);
                emitter.onNext(112);
                emitter.onNext(113);
                Thread.sleep(2000L);
                emitter.onNext(114);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());

        Observable.zip(observable1, observable2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("~~zip.BiFunction.apply~~");
                return integer.toString() + "-" + integer2.toString();
            }
        }).subscribe(System.out::println);

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void scan() {
        Observable<Integer> observable = Observable.range(6, 9)
                .scan(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        System.out.println("~~scan.BiFunction.apply~~");
                        System.out.println("integer is " + integer);
                        System.out.println("integer2 is " + integer2);
                        System.out.println("------------");
                        return integer2;
                    }
                });
        observable.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("~~subscribe.Consumer.accept~~");
                System.out.println("integer is " + integer);
            }
        });
    }

    private void window() {
        List<Observable<Integer>> list = new ArrayList<>();
        Observable<Observable<Integer>> observableObservable = Observable.range(0, 12)
                .window(7);//创建2个Observable，每个包含7个元素
        observableObservable.subscribe(list::add); //将返回的Observable保存到容器

        while (list.iterator().hasNext()) {
            list.remove(0).subscribe(System.out::println);
            System.out.println("-------");
        }
    }


    private void groupBy() {
        Observable<GroupedObservable<String, Integer>> observable = Observable.range(0, 12)
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        System.out.println("~~groupBy.Function.apply~~");
                        System.out.println("integer is " + integer);

                        String result = integer % 2 == 0 ? "even" : "odd";
                        return result;
                    }
                });

        observable.subscribe(new Consumer<GroupedObservable<String, Integer>>() {
            @Override
            public void accept(GroupedObservable<String, Integer> stringIntegerGroupedObservable) throws Exception {
                System.out.println("~~subscribe.Consumer.accept~~");
                System.out.println("stringIntegerGroupedObservable is " + stringIntegerGroupedObservable);
                String flag = stringIntegerGroupedObservable.getKey();
                stringIntegerGroupedObservable.subscribe(integer -> System.out.println(flag + "|" + integer));
            }
        });


    }


    private void collect() {

        /*
        * 初始化时，给定容器，collect()中将发送的数据保存到容器
        * collect()和reduce()执行流程差不多
        * */
        Single<List<Integer>> single = Observable.range(1, 3)
                .collect(ArrayList::new,//给定容器
                        new BiConsumer<List<Integer>, Integer>() {
                            @Override
                            public void accept(List<Integer> list, Integer integer2) throws Exception {
                                System.out.println("~~collect.BiConsumer.accept~~");
                                System.out.println("integer is " + list);
                                System.out.println("integer2 is " + integer2);
                                list.add(integer2);
                            }
                        });
        single.subscribe(System.out::println);

    }


    private void buffer() {
        Observable<List<Integer>> observable = Observable.range(0, 12)
                .buffer(4, 1);//步长为一
//                .buffer(4, 4);

        observable.subscribe(System.out::println);

    }

    private void flatMap() {

        Observable<String> observable = Observable.range(2, 6)
                .flatMap(new Function<Integer, ObservableSource<String>>() {
                    Random random = new Random();

                    @Override
                    public ObservableSource<String> apply(Integer integer) {
                        System.out.println("~~flatMap.Function.apply~~");
                        return Observable.just(integer.toString() + "-" + random.nextInt(99));
                    }
                });
        observable.subscribe(System.out::println);
    }

    private void map() {
        Observable<String> observable = Observable.range(0, 12)
                .map(new Function<Integer, String>() { //将Integer映射为String
                    @Override
                    public String apply(Integer integer) throws Exception {
                        System.out.println("~~map.Function.apply~~");
                        System.out.println("integer is " + integer);
                        return integer.toString() + "--";
                    }
                });
        observable.subscribe(System.out::println);
    }


    /*========================================*/

    private void emptyNeverError() {
        //创建观察者
        Observer observer = new Observer<Object>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);

                disposable = d;
            }

            @Override
            public void onNext(Object o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);

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

        //使用empty
//        Observable.empty()//仅发送onSubscribe和onComplete事件，不发送任何onNext
//                .subscribe(observer);


        //使用never
//        Observable.never()//仅发送onSubscribe，没有其他任何事件
//                .subscribe(observer);

        //使用error
        Observable.error(new Throwable())//仅发送onSubscribe和onError事件，不发送任何onNext
                .subscribe(observer);


        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void defer() {

        int[] ints = new int[1];
        Callable<ObservableSource<Integer>> callable = new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                System.out.println("~~ObservableSource.call~~");
                return Observable.just(ints[0]);
            }
        };

        Observable<Integer> observable = Observable.defer(callable);
        ints[0] = 16;//延迟到注册观察者时才创建被观察者（被观察者创建后才保存数据集），只有defer()有这样的特性
        observable.subscribe(System.out::println);
        ints[0] = 19;
        observable.subscribe(System.out::println);

    }


    private void repeat() {
        Observable.fromArray(1, 3, 5)
                .repeat(2)
                .subscribe(System.out::println);
    }


    private void range() {
        Observable.range(2, 100)
                .subscribe(System.out::println);
    }


    private void merge() {
        Observable<String> observable1 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(1000L);
                emitter.onNext(2);
                emitter.onNext(3);
                Thread.sleep(1000L);
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).map(integer -> "A|" + integer);

        Observable<String> observable2 = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                Thread.sleep(1000L);
                emitter.onNext(2);
                emitter.onNext(3);
                Thread.sleep(1000L);
                emitter.onNext(4);
                emitter.onNext(5);
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io()).map(integer -> "B|" + integer);

        /*
         * merge()将并行发送，而concat()是串行发送
         * merge()必须使用子线程才能体现效果，否则将和concat()串行发送
         * */
        Observable.merge(observable1, observable2)
                .subscribe(System.out::println);


        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


    private void join() {
        /*
         * merge()将并行发送，而concat()是串行发送
         * merge()必须使用子线程才能体现效果，否则将和concat()同样使用串行发送
         * */

        //创建观察者
        Observer observer = new Observer<Integer>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);

                disposable = d;
            }

            @Override
            public void onNext(Integer o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);

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


        //创建2个被观察者，让他们工作在各自的线程中
        Observable<Integer> origin = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>origin.create.subscribe<<--");
                System.out.println("emitter is " + emitter);
                emitter.onNext(17);
                Thread.sleep(1000L);
                emitter.onNext(27);
                emitter.onNext(37);
                emitter.onComplete();
            }
        });
        Observable<Integer> other = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>other.create.subscribe<<--");
                System.out.println("emitter is " + emitter);
//                Thread.sleep(1100L);
                emitter.onNext(81);
//                 emitter.onNext(82);
//                emitter.onNext(83);
                emitter.onComplete();
            }
        });


        //创建origin时间窗口
        Function<Integer, Observable<Integer>> leftEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~leftEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        if (integer.equals(27)) {
                            emitter.onNext(9527);
//                            emitter.onNext(414);//按索引清除的，多个onNext是无效的
//                            emitter.onNext(-56);
//                            emitter.onNext(44);
                        }
                    }
                });
            }
        };

        //创建other时间窗口
        Function<Integer, Observable<Integer>> rightEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~rightEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.never();
//                        .delay(3, TimeUnit.SECONDS);//设置延迟
            }
        };

        //创建连接器
        BiFunction<Integer, Integer, Integer> resultSelector = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("----resultSelector.BiFunction.apply----");
                System.out.println("integer is " + integer);
                System.out.println("integer2 is " + integer2);
                return integer + integer2;
            }
        };


        origin = origin.subscribeOn(Schedulers.io());//启用子线程
        other = other.subscribeOn(Schedulers.io());//启用子线程


        origin.join(other, //让origin和other连接
                leftEnd,//设置时间窗口
                rightEnd,//设置时间窗口
                resultSelector)
                .subscribe(observer);

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void just() {

        //方式一：使用单值
        Observable<Integer> observable = Observable.just(Integer.valueOf(25));
        observable.subscribe(System.out::println);


        //方式二：使用多值
//        Observable<Integer> observable = Observable.just(Integer.valueOf(1),
//                Integer.valueOf(12),
//                Integer.valueOf(75),
//                Integer.valueOf(32));//多值版本是间接调用fromArray()
//        observable.subscribe(System.out::println);


        //方式三：
//        int i = 1;
//        Observable<Integer> observable = Observable.just(Integer.valueOf(25));
//        observable.subscribe(System.out::println);
//        i = 18;////由于观察者创建时，数据源已经保存了引用，所有现在修改引用是无用的
//        observable.subscribe(System.out::println);
    }


    private void from() {

        //数据集
        Integer[] integers = {1, 2, 3, 4, 5};
        Integer[] tmp = {8, 9, 31, 41, 15};

        //方式一：基本使用
        Observable<Integer> observable = Observable.fromArray(integers);
        observable.subscribe(System.out::println);


        //方式二：发送前变更数据
//        Observable<Integer> observable = Observable.fromArray(integers);
//        observable.subscribe(System.out::println);
//        System.out.println("--------");
//        integers = tmp;//由于观察者创建时，数据源已经保存了引用，所有现在修改引用是无用的
//        observable.subscribe(System.out::println);


        //方式三
//        Observable<Integer> observable = Observable.fromArray(integers);
//        observable.subscribe(System.out::println);
//        System.out.println("--------");
//        integers[0] = 25; //修改元素值是可以的，发送时还同一个数组
//        observable.subscribe(System.out::println);


    }

    private void create() {

        //创建观察者
        Observer observer = new Observer<Object>() {
            Disposable disposable = null;

            @Override
            public void onSubscribe(Disposable d) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Disposable is " + d.hashCode() + "|" + d);

                disposable = d;
            }

            @Override
            public void onNext(Object o) {
                System.out.println("~~onNext~~");
                System.out.println("o is " + o);
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


        //创建发射器
        ObservableOnSubscribe observableOnSubscribe = new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                System.out.println("===subscribe===");
                System.out.println("emitter is " + emitter.hashCode() + "|" + emitter);


                for (int i = 0; i < 5; i++) {
                    emitter.onNext("oooo-" + i);
                }
                emitter.onComplete();


            }
        };

        //创建被观察者
        Observable<String> observable = Observable.create(observableOnSubscribe);
        observable.subscribe(observer); //注册观察者
    }

}
