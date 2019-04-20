import io.reactivex.*;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.*;
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
//        observableTrial.defer(); //延迟到订阅时创建观察者
//        observableTrial.using();
        /*-------异步创建----------*/
//        observableTrial.interval();
//        observableTrial.timer();
//        observableTrial.sampler();
//        observableTrial.debounce();


        /*=============中间操作===========================*/
//        observableTrial.map();
//        observableTrial.flatMap();
//        observableTrial.buffer();
//        observableTrial.groupBy();
//        observableTrial.window();
//        observableTrial.scan();
//        observableTrial.zip();


        /*===================判断函数=====================*/
//        observableTrial.contains();
//        observableTrial.all();


        /*===================聚合函数=====================*/
//        observableTrial.concat();//*****
//        observableTrial.count();
//        observableTrial.reduce();



        /*===================钩子函数=====================*/
//        observableTrial.doOnSubscribe();
        observableTrial.doOnDispose();
//        observableTrial.doOnError();
////        observableTrial.doOnNext();
//        observableTrial.doOnComplete();
        /*-----------------*/
//        observableTrial.doOnEach();//*****
//        observableTrial.doOnLifecycle();
//        observableTrial.doOnTerminate();





        /*=================顾虑函数=======================*/
//        observableTrial.distinct();
//        observableTrial.elementAt();
        /*-----------------*/
//        observableTrial.first();
//        observableTrial.last();
//        observableTrial.filter();
//        observableTrial.amb(); //使用最先抵达者
//        observableTrial.ignoreElements(); //过滤所有元素
        /*-----------------*/
//        observableTrial.skip();
//        observableTrial.take();


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

    private void ignoreElements() {
        Observable.range(0, 5)
                .ignoreElements() //过滤所有元素
                .subscribe(System.out::println);
    }

    private void first() {


        //方式一：使用发射器
//        Single<Integer> single = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                for (int i = 0; i < 6; i++) {
//                    emitter.onNext(i);
//                }
//
//                emitter.onComplete();
//            }
//        }).first(-2);//直接发送onComplete()，而不发送onNext()将使用默认值-2
//        single.subscribe(System.out::println);


        //方式二
        Observable.range(0, 6)
                .first(-2)//直接发送onComplete()，而不发送onNext()将使用默认值-2
                .subscribe(System.out::println);
    }

    private void last() {
        //方式一：使用发射器
//        Single<Integer> single = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                for (int i = 0; i < 6; i++) {
//                    emitter.onNext(i);
//                }
//
//                emitter.onComplete();
//            }
//        }).last(-2);//直接发送onComplete()，而不发送onNext()将使用默认值-2
//        single.subscribe(System.out::println);


        //方式二
        Observable.range(0, 6)
                .last(-2)//直接发送onComplete()，而不发送onNext()将使用默认值-2
                .subscribe(System.out::println);
    }

    private void sample() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(1);
                emitter.onNext(2);
                Thread.sleep(1100L);//超过1秒，所以2可以发送，然后多出100毫秒
                emitter.onNext(3);
                Thread.sleep(300L);//100+300小于1秒，3被过滤
                emitter.onNext(4);
                Thread.sleep(900L);//100+300+900大于1秒，4可以发送
                emitter.onNext(5);
                emitter.onNext(6);
                emitter.onNext(7);
                emitter.onNext(8); //最后一个所以直接发送
                emitter.onComplete();
            }
        }).sample(1L, TimeUnit.SECONDS).subscribe(System.out::println);


        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void skip() {
        Observable.range(0, 5).skip(2).subscribe(System.out::println);
        System.out.println("------");
        Observable.range(0, 5).skipLast(2).subscribe(System.out::println);
    }

    private void take() {
        Observable.range(0, 5).take(2).subscribe(System.out::println);
        System.out.println("------");
        Observable.range(0, 5).takeLast(2).subscribe(System.out::println);
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
                            Thread.sleep(900L);//间隔小于1秒，4被过滤
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

        Integer[] integers = {Integer.valueOf(221), Integer.valueOf(211), Integer.valueOf(201)};

        //创建数据集
        Callable<Integer[]> callable = new Callable<Integer[]>() {
            @Override
            public Integer[] call() throws Exception {
                System.out.println("~~Callable.call~~");
                return integers;
            }
        };

        //创建消费者
        Function<Integer[], Observable<Integer>> function = new Function<Integer[], Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer[] integers) throws Exception {
                System.out.println("~~Function.apply~~");
                System.out.println(integers);
                for (Integer i : integers) System.out.println(i);
                return Observable.fromArray(integers);
            }
        };

        //创建回收器
        Consumer<Integer[]> consumer = new Consumer<Integer[]>() {
            @Override
            public void accept(Integer[] integers) throws Exception {
                System.out.println("~~Consumer.accept~~");
                System.out.println(integers);
                for (Integer i : integers) System.out.println(i);
                integers = null;

            }
        };

        Observable.using(callable, function, consumer).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                System.out.println("~~using.accept~~");
                System.out.println(integer);
            }
        });

    }


    private void concat() {
        Observable<Integer> observable = Observable.fromArray(1, 1, 2, 2);
        observable.contains(3).subscribe(System.out::println);
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

//        //创建观察者
//        Observer observer = new Observer<Object>() {
//            Disposable disposable = null;
//
//            @Override
//            public void onSubscribe(Disposable d) {
//                System.out.println("~~onSubscribe~~");
//                System.out.println("Disposable is " + d.hashCode() + "|" + d);
//
//                disposable = d;
//            }
//
//            @Override
//            public void onNext(Object o) {
//                System.out.println("~~onNext~~");
//                System.out.println("o is " + o);
//
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                System.out.println("~~onError~~");
//
//            }
//
//            @Override
//            public void onComplete() {
//                System.out.println("~~onComplete~~");
//
//            }
//        };
//
//
//        //创建发射器
//        ObservableOnSubscribe observableOnSubscribe = new ObservableOnSubscribe<String>() {
//            @Override
//            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
//                System.out.println("===subscribe===");
//                System.out.println("emitter is " + emitter.hashCode() + "|" + emitter);
//
//
//                for (int i = 0; i < 5; i++) {
//                    emitter.onNext("oooo-" + i);
//                }
//                emitter.onComplete();
//
//
//            }
//        };
//
//        //创建被观察者
//        Observable<String> observable = Observable.create(observableOnSubscribe)
//                .doOnEach();
//        observable.subscribe(observer); //注册观察者

    }

    private void doOnDispose() {
        Disposable disposable = Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        System.out.println("~~doOnDispose.Action.run~~");
                    }
                }).subscribe(System.out::println);

        try {
            Thread.sleep(2000L);
            disposable.dispose();//2秒后取消订阅
            Thread.sleep(4000L);//4秒后主线程结束，interval()启动的守护线程也将自带关闭
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void doOnSubscribe() {
        Observable<Integer> observable = Observable.fromArray(1, 1, 2, 2)
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        System.out.println("~~doOnSubscribe.Consumer.accept~~");
                        System.out.println(disposable);
                    }
                });
        observable.subscribe();
    }

    private void doOnError() {
    }

    private void doOnLifecycle() {
    }

    private void doOnTerminate() {
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
        Observable<GroupedObservable<String, Integer>> observable = Observable.range(3, 7)
                .groupBy(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        System.out.println("~~groupBy.Function.apply~~");
                        System.out.println("integer is " + integer);

                        return integer.intValue() % 2 == 0 ? "even" : "odd";
                    }
                });

        observable.subscribe(new Consumer<GroupedObservable<String, Integer>>() {
            @Override
            public void accept(GroupedObservable<String, Integer> stringIntegerGroupedObservable) throws Exception {
                System.out.println("~~subscribe.Consumer.accept~~");
                System.out.println("stringIntegerGroupedObservable is " + stringIntegerGroupedObservable);

                String flag = stringIntegerGroupedObservable.getKey();
                System.out.println("getKey is " + flag);
                stringIntegerGroupedObservable.subscribe(integer -> System.out.println(flag + "|" + integer));
            }
        });


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
        //方式一
//        Observable.empty().subscribe(System.out::println);

        //方式二
        Observable.never().subscribe(System.out::println);

        //方式三
//        Observable.error(new Throwable()).subscribe(System.out::println);
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
