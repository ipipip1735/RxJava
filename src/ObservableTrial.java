import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
        /*-------异步创建----------*/
//        observableTrial.timer(); //定时发送一次
//        observableTrial.interval(); //间隔发送
//        observableTrial.sampler();
//        observableTrial.delay();//延迟指定时间开始发送首个onNext事件
        /*-------其他创建----------*/
//        observableTrial.defer(); //延迟到订阅时创建被观察者
//        observableTrial.using();//发送完成后销毁资源
//        observableTrial.emptyNeverError();


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
//        observableTrial.takeUntil();
//        observableTrial.skip();


        /*=============中间操作===========================*/
//        observableTrial.cache();
//        observableTrial.repeat();
        /*-----------------*/
//        observableTrial.map();
//        observableTrial.flatMap();
//        observableTrial.buffer();
//        observableTrial.groupBy();
//        observableTrial.window();
//        observableTrial.scan();
//        observableTrial.zip();
//        observableTrial.join();
//        observableTrial.merge();



        /*===================判断函数=====================*/
//        observableTrial.contains();
//        observableTrial.all();


        /*===================聚合函数=====================*/
//        observableTrial.concat();//多个被观察者按顺序依次发送
//        observableTrial.count();
//        observableTrial.reduce();



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


        /*==================转换函数=====================*/
//        observableTrial.to();//将被观察者转换为任意对象
//        observableTrial.as();//同功能和to()类似

    }


    private void as() {
        String info = Observable.just(1).as(new ObservableConverter<Integer, String>() {
            @Override
            public String apply(Observable<Integer> upstream) {
                System.out.println("~~as.ObservableConverter.apply~~");
                System.out.println("upstream is " + upstream);
                return upstream.toString();
            }
        });
        System.out.println("info is " + info);
    }

    private void to() {
        String info = Observable.just(1).to(new Function<Observable<Integer>, String>() {
            @Override
            public String apply(Observable<Integer> integerObservable) throws Exception {
                System.out.println("~~to.Function.apply~~");
                return integerObservable.toString();
            }
        });
        System.out.println("info is " + info);
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


    private void takeUntil() {
        Observable<Integer> observable = Observable.interval(1, TimeUnit.SECONDS).map(Long::intValue);
        Observable<Integer> other = Observable.interval(3, TimeUnit.SECONDS).map(Long::intValue);

        observable.takeUntil(other)//当other发送数据时就结束
                .subscribe(System.out::println);

        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        //创建2个被观察者，让他们在各自的线程中发送
        List<Observable<Integer>> list = new ArrayList<>();
        list.add(Observable.fromArray(11, 12, 14).subscribeOn(Schedulers.io()));
        list.add(Observable.fromArray(27, 22, 28).subscribeOn(Schedulers.io()));
        list.add(Observable.fromArray(37, 32, 38).subscribeOn(Schedulers.io()));
        list.add(Observable.fromArray(47, 42, 48).subscribeOn(Schedulers.io()));
        list.add(Observable.fromArray(57, 52, 58).subscribeOn(Schedulers.io()));

        //让2个被观察者依次发送，一个被观察者onComplete()后才会发送另外一个
        Observable.concat(list)
                .subscribe(integer -> {
                    System.out.println("integer is " + integer);
                    System.out.println(Thread.currentThread());
                    System.out.println("-------------");
                });


        try {
            Thread.sleep(3000L);
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

    private void zip() {
        Observable<Integer> observable1 = Observable.fromArray(1, 1, 2, 2);
        Observable<Integer> observable2 = Observable.fromArray(3, 5, 7, 9);

        Observable.zip(observable1, observable2, new BiFunction<Integer, Integer, String>() {
            @Override
            public String apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("~~zip.BiFunction.apply~~");
                return integer.toString() + "-" + integer2.toString();
            }
        }).subscribe(System.out::println);

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


    private void buffer() {
        Observable<List<Integer>> observable = Observable.range(0, 12)
                .buffer(4, 1);//步长为一
//         .buffer(4, 3);

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
        Observable.empty()//仅发送onSubscribe和onComplete事件，不发送任何onNext
                .subscribe(observer);


        //使用never
        Observable.never()//仅发送onSubscribe，没有其他任何事件
                .subscribe(observer);

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


    private void cache() {
        /*
         * cache操作可以缓存耗时操作结果
         * */

        //方法一：使用定时器
//        Observable<Long> observable = Observable.interval(1, TimeUnit.SECONDS)
//                .cache();
//        observable.subscribe(l -> System.out.println("one|" + l));//订阅后才会发送数据，第一次发送数据后结果就被缓存起来
//        try {
//            Thread.sleep(3000L);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        observable.subscribe(l -> System.out.println("two|" + l));//后续下游直接使用缓存中的结果，不需要等待
//        observable.subscribe(l -> System.out.println("three|" + l));//后续下游直接使用缓存中的结果，不需要等待
//        observable.subscribe(l -> System.out.println("four|" + l));//后续下游直接使用缓存中的结果，不需要等待
//        observable.subscribe(l -> System.out.println("five|" + l));//后续下游直接使用缓存中的结果，不需要等待


        //方法二：使用映射
        Observable<String> observable = Observable.fromArray(1, 2, 3)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        System.out.println("~~map.Function.apply~~");
                        return integer.toString();
                    }
                })
                .cache();//缓存map操作的结果


        observable.subscribe(l -> System.out.println("one|" + l));  //map操作仅在第一次需要，首次订阅后结果就被缓存起来
        observable.subscribe(l -> System.out.println("two|" + l));  //后续下游直接使用缓存中的结果，不需要在map
        observable.subscribe(l -> System.out.println("three|" + l));  //后续下游直接使用缓存中的结果，不需要在map
        observable.subscribe(l -> System.out.println("four|" + l));  //后续下游直接使用缓存中的结果，不需要在map
        observable.subscribe(l -> System.out.println("five|" + l));  //后续下游直接使用缓存中的结果，不需要在map


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

        List<Observable<Integer>> list = Arrays.asList(Observable.fromArray(72, 73), Observable.just(81));
        Observable.merge(list)
                .subscribe(System.out::println);
    }


    private void join() {

//        joinSync();//同步join
//        joinAsync();//同步join
//        joinDefer();//使用定时器
//        joinTime();//使用定时器

    }

    private void joinTime() {
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


        //待连接者
        Observable<Integer> origin = Observable.interval(1, TimeUnit.SECONDS)
                .map(Long::intValue);
        Observable<Integer> other = Observable.fromArray(1, 2);


        //左定时器
        Function<Integer, Observable<Integer>> leftEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                return Observable.timer(1, TimeUnit.SECONDS)//使用定时器
                        .map(Long::intValue);//将Long转化为Integer
            }
        };

        //右定时器
        Function<Integer, Observable<Integer>> rightEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~rightEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.never();
            }
        };

        //连接器
        BiFunction<Integer, Integer, Integer> resultSelector = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("----resultSelector.BiFunction.apply----");
                System.out.println("integer is " + integer);
                System.out.println("integer2 is " + integer2);
                return integer + integer2;
            }
        };


        origin.join(other,
                leftEnd,//左buffer定时器
                rightEnd,//右buffer定时器
                resultSelector)
                .subscribe(observer);


        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    private void joinDefer() {
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

        //待连接者
        Observable<Integer> origin = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>origin.create.subscribe<<--");
                System.out.println("emitter is " + emitter);
                emitter.onNext(17);
                emitter.onNext(27);
                emitter.onNext(37);
            }
        });

        //被连接者
        Observable<Integer> other = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>other.create.subscribe<<--");
                System.out.println("emitter is " + emitter);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            emitter.onNext(81);
                            Thread.sleep(1000L);
                            emitter.onNext(82);
                            Thread.sleep(1000L);
                            emitter.onNext(83);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        //左定时器
        Function<Integer, Observable<Integer>> leftEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~leftEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (integer.equals(17))
                                        Thread.sleep(1000L);//设置定时器，17的生存时间
                                    if (integer.equals(27))
                                        Thread.sleep(2000L);//设置定时器，27的生存时间
                                    if (integer.equals(37))
                                        Thread.sleep(2500L);//设置定时器，37的生存时间


                                    emitter.onNext(0); //后面的onNext是无效的，onNext发送的数据也没有意义
//                                    emitter.onNext(new Random().nextInt(9999));
//                                    emitter.onNext(new Random().nextInt());

                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
            }
        };

        //右定时器
        Function<Integer, Observable<Integer>> rightEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~rightEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.never();
            }
        };

        //连接器
        BiFunction<Integer, Integer, Integer> resultSelector = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("----resultSelector.BiFunction.apply----");
                System.out.println("integer is " + integer);
                System.out.println("integer2 is " + integer2);
                return integer + integer2;
            }
        };

        origin.join(other,
                leftEnd,//左buffer定时器
                rightEnd,//右buffer定时器
                resultSelector)
                .subscribe(observer);
    }

    private void joinAsync() {

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

        //待连接者
        Observable<Integer> origin = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>origin.create.subscribe<<--");
                System.out.println("emitter is " + emitter);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000L);
                            emitter.onNext(17);
                            emitter.onNext(27);
                            emitter.onNext(37);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        //被连接者
        Observable<Integer> other = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>other.create.subscribe<<--");
                System.out.println("emitter is " + emitter);
                emitter.onNext(81);
                emitter.onNext(82);
                emitter.onNext(83);
            }
        });

        //左定时器
        Function<Integer, Observable<Integer>> leftEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~leftEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.never();
            }
        };

        //右定时器
        Function<Integer, Observable<Integer>> rightEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~rightEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.never();
            }
        };

        //连接器
        BiFunction<Integer, Integer, Integer> resultSelector = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("----resultSelector.BiFunction.apply----");
                System.out.println("integer is " + integer);
                System.out.println("integer2 is " + integer2);
                return integer + integer2;
            }
        };

        origin.join(other,
                leftEnd,//左buffer定时器
                rightEnd,//右buffer定时器
                resultSelector)
                .subscribe(observer);

    }

    private void joinSync() {

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


        //待连接者
        Observable<Integer> origin = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>origin.create.subscribe<<--");
                System.out.println("emitter is " + emitter);
                emitter.onNext(17);
                emitter.onNext(27);
                emitter.onNext(37);
            }
        });

        //被连接者
        Observable<Integer> other = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                System.out.println("-->>other.create.subscribe<<--");
                System.out.println("emitter is " + emitter);
                emitter.onNext(81);
                emitter.onNext(82);
            }
        });

        //左定时器
        Function<Integer, Observable<Integer>> leftEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~leftEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.never();
            }
        };

        //右定时器
        Function<Integer, Observable<Integer>> rightEnd = new Function<Integer, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer integer) throws Exception {
                System.out.println("~~rightEnd.Function.apply~~");
                System.out.println("integer is " + integer);
                return Observable.never();
            }
        };

        BiFunction<Integer, Integer, Integer> resultSelector = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer integer, Integer integer2) throws Exception {
                System.out.println("----resultSelector.BiFunction.apply----");
                System.out.println("integer is " + integer);
                System.out.println("integer2 is " + integer2);
                return integer + integer2;
            }
        };

        origin.join(other,
                leftEnd,//左buffer定时器
                rightEnd,//右buffer定时器
                resultSelector)
                .subscribe(observer);

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
//        Observable<Integer> observable = Observable.fromArray(integers);
//        observable.subscribe(System.out::println);


        //方式二：发送前变更数据
        Observable<Integer> observable = Observable.fromArray(integers);
        observable.subscribe(System.out::println);
        System.out.println("--------");
        integers = tmp;//由于观察者创建时，数据源已经保存了引用，所有现在修改引用是无用的
        observable.subscribe(System.out::println);


        //方式三
//        Observable<Integer> observable = Observable.fromArray(integers);
//        observable.subscribe(System.out::println);
//        System.out.println("--------");
//        integers[0] = 25; //修改元素值是可以的，发送时还同一个数组
//        observable.subscribe(System.out::println);


    }

    private void create() {

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
                if (integer > 2) disposable.dispose();
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
                System.out.println("===subscribe===");
                System.out.println("emitter is " + emitter.hashCode() + "|" + emitter);

                for (int i = 0; i < 5; i++) {
                    emitter.onNext(i);
                }

//                emitter.onError(new Throwable("xxx"));//如果有错误就发送
                emitter.onComplete();
            }
        };

        //创建被观察者
        Observable<Integer> observable = Observable.create(observableOnSubscribe);
        observable.subscribe(observer); //注册观察者
    }

}
