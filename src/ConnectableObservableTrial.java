import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/21.
 */
public class ConnectableObservableTrial {

    public static void main(String[] args) {
        ConnectableObservableTrial connectableObservableTrial = new ConnectableObservableTrial();


        connectableObservableTrial.publish(); //普通被观察者 转换为 可连接被观察者，即Hot
//        connectableObservableTrial.replay(); //支持重发，发送开始后，后订阅的观察者依然能接收完整数据
        /*---------------*/
//        connectableObservableTrial.refCount(); // 可连接被观察者 转化为 普通被观察者
//        connectableObservableTrial.autoConnect(); //支持第一个观察者订阅后自动连接
        /*---------------*/
//        connectableObservableTrial.connect(); //发送


    }

    private void replay() {
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
        }).sample(1, TimeUnit.SECONDS)
                .replay(); //创建可连接被观察者

        connectableObservable.subscribe(integer -> System.out.println("one|" + integer));//不会立即发送，等到connect()方法调用后发送
        connectableObservable.subscribe(integer -> System.out.println("two|" + integer));

        connectableObservable.connect();//发送

        try {
            Thread.sleep(4000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connectableObservable.subscribe(integer -> System.out.println("three|" + integer));
    }


    private void refCount() {

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
        }).sample(1, TimeUnit.SECONDS).publish();//refCount()是publish()的逆方法

        Observable<Integer> observable = connectableObservable.refCount();//转换为普通被观察者
        connectableObservable.subscribe(integer -> System.out.println("one|" + integer));

    }

    private void publish() {
        /*
         * 使用普通方法，map操作要执行多
         * 但使用可连接被观察者可以缓存结果
         * */
//        Observable<String> observable = Observable.fromArray(1,2,3)
//                .map(new Function<Integer, String>() { //map操作要执行多次
//                    @Override
//                    public String apply(Integer integer) throws Exception {
//                        System.out.println("~~map.Function.apply~~");
//                        System.out.println("integer is " + integer);
//                        return integer.toString();
//                    }
//                });
//        observable.subscribe(integer -> System.out.println("one|" + integer));
//        observable.subscribe(integer -> System.out.println("two|" + integer));
//        observable.subscribe(integer -> System.out.println("three|" + integer));




        /*
         * 使用可连接被观察者可以缓存结果
         * */
        ConnectableObservable<String> connectableObservable = Observable.fromArray(1,2,3)
        .map(new Function<Integer, String>() { //map操作仅执行一次
            @Override
            public String apply(Integer integer) throws Exception {
                System.out.println("~~map.Function.apply~~");
                System.out.println("integer is " + integer);
                return integer.toString();
            }
        }).publish();//转化为可连接被观察者

        connectableObservable.subscribe(integer -> System.out.println("one|" + integer));  //不会立即发送，等到connect()方法调用后发送
        connectableObservable.subscribe(integer -> System.out.println("two|" + integer));  //无法接收到完整数据
        connectableObservable.subscribe(integer -> System.out.println("three|" + integer));  //无法接收到完整数据
        connectableObservable.connect();

    }


    private void connect() {

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
        Disposable disposable = connectableObservable.connect();//发送

        try {
            Thread.sleep(3000L);
            disposable.dispose();//取消订阅
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        connectableObservable.connect();//重连
        connectableObservable.subscribe(integer -> System.out.println("two|" + integer));//再注册一个观察者，它接收不到完整的数据，前2秒发送的数据被漏掉


        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
