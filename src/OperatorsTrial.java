import io.reactivex.*;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/17.
 */

public class OperatorsTrial {

    public static void main(String[] args) {
        OperatorsTrial operatorsTrial = new OperatorsTrial();

//        OperatorsTrial.create();
//        OperatorsTrial.from();
//        OperatorsTrial.just();
//        OperatorsTrial.rangeRepeat();
//        OperatorsTrial.defer();
        /*-----------------*/
//        OperatorsTrial.emptyNeverThrow();

        /*========================================*/
//        operatorsTrial.map();
//        operatorsTrial.Buffer();
        /*========================================*/
//        operatorsTrial.debounce();
//        operatorsTrial.filter();


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

                            emitter.onNext(1); //过滤
                            emitter.onNext(2); //发送
                            Thread.sleep(2000L);//大于1秒，所以2可以发送
                            emitter.onNext(3); //过滤
                            Thread.sleep(900L);//小于1秒，3被过滤
                            emitter.onNext(4); //过滤
                            Thread.sleep(900L);//小于1秒，3被过滤
                            emitter.onNext(5); //过滤
                            emitter.onNext(6); //过滤
                            emitter.onNext(7); //过滤
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

    private void Buffer() {
        Observable<List<Integer>> observable = Observable.range(0, 12)
                .buffer(4, 1);
        observable.subscribe(System.out::println);

    }

    private void map() {
        Observable<String> observable = Observable.range(0, 12)
                .map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer integer) throws Exception {
                        return integer.toString();
                    }
                });

        observable.subscribe(System.out::println);
    }


    /*========================================*/

    private void emptyNeverThrow() {
        Observable.never().subscribe(System.out::println);
    }


    private void defer() {

        int[] ints = new int[2];
        Callable<ObservableSource<Integer>> callable = new Callable<ObservableSource<Integer>>() {
            @Override
            public ObservableSource<Integer> call() throws Exception {
                System.out.println("~~ObservableSource.call~~");
                return Observable.just(ints[0]);
            }
        };

        Observable<Integer> observable = Observable.defer(callable);
        ints[0] = 16;
        observable.subscribe(System.out::println);
        ints[0] = 19;
        observable.subscribe(System.out::println);

    }


    private void rangeRepeat() {
        Observable.range(2, 100)
                .repeat(2)
                .subscribe(System.out::println);
    }


    private void just() {


        int i = 25;

        Observable<Integer> observable = Observable.just(Integer.valueOf(i));
//        Observable<Integer> observable = Observable.just(Integer.valueOf(1), Integer.valueOf(12));//多值版本是间接调用fromArray()

        observable.subscribe(System.out::println);
        i = 18;
        observable.subscribe(System.out::println);
    }

    private void from() {

        //数据集
        Integer[] integers = {1, 2, 3, 4, 5};
        Integer[] tmp = {8, 9, 31, 41, 15};

        //方式一
//        Observable<Integer> observable = Observable.fromArray(integers);
//        observable.subscribe(System.out::println);
//        System.out.println("--------");
//        integers = tmp;//数组改变了，发送数据时还是原数组
//        observable.subscribe(System.out::println);


        //方式二
        Observable<Integer> observable = Observable.fromArray(integers);
        observable.subscribe(System.out::println);
        System.out.println("--------");
        integers[0] = 25; //修改元素值是可以的，发送时还同一个数组
        observable.subscribe(System.out::println);


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
