import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.observables.ConnectableObservable;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2019/4/21.
 */
public class ConnectableObservableTrial {

    public static void main(String[] args) {
        ConnectableObservableTrial connectableObservableTrial = new ConnectableObservableTrial();


//        connectableObservableTrial.publish(); //普通被观察者 转换为 可连接被观察者
        connectableObservableTrial.replay(); //支持重发，发送开始后，后订阅的观察者依然能接收完整数据
        /*---------------*/
//        connectableObservableTrial.refCount(); // 可连接被观察者 转化为 普通被观察者
//        connectableObservableTrial.autoConnect()(); //支持第一个观察者订阅后自动连接
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
//                .publish();
                .replay();

        connectableObservable.subscribe(integer -> System.out.println("one|" + integer));//不会理解发送，等到connect()方法调用后发送
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

        observable.subscribe(integer -> System.out.println("one|" + integer));//不会理解发送，等到connect()方法调用后发送
        observable.subscribe(integer -> System.out.println("two|" + integer));


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
        connectableObservable.subscribe(integer -> System.out.println("two|" + integer));

        connectableObservable.connect();//发送


    }
}