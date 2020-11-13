import io.reactivex.*;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import static io.reactivex.BackpressureStrategy.*;

/**
 * Created by Administrator on 2019/4/18.
 */

public class FlowableTrial {
    public static void main(String[] args) {
        FlowableTrial flowableTrial = new FlowableTrial();
        flowableTrial.create();
//        flowableTrial.collect();
    }

    private void collect() {

        Flowable<Integer> flowable = Flowable.fromArray(1, 2, 3);

        Callable<Integer> initialItemSupplier = new Callable<>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("~~initialItemSupplier.call~~");
                return Integer.valueOf(99);
            }
        };

        BiConsumer<Integer, Integer> biConsumer = new BiConsumer<>() {
            @Override
            public void accept(Integer integer, Integer integer2) throws Exception {
                System.out.println("~~initialItemSupplier.call~~");
                System.out.println("integer is " + integer);
                System.out.println("integer2 is " + integer2);

            }
        };

        flowable.collect(initialItemSupplier, biConsumer)
        .subscribe(System.out::println);



    }

    private void create() {

//        System.setProperty("rx2.buffer-size", "2");
//        System.out.println("Flowable.bufferSize() is " + Flowable.bufferSize());


        //订阅器
        Subscriber<Integer> subscriber = new Subscriber<>() {
            Subscription s = null;
            private long begin = System.currentTimeMillis();

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Subscription is " + s.hashCode() + "|" + s);
                this.s = s;
                s.request(1);
            }

            @Override
            public void onNext(Integer i) {
                System.out.println("~~onNext~~");
                System.out.println("integer is " + i);

                if (System.currentTimeMillis() - begin < 3000) {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                s.request(1);
            }

            @Override
            public void onError(Throwable t) {
                System.out.println("~~onError~~");
                System.out.println("Throwable is " + t);
            }

            @Override
            public void onComplete() {
                System.out.println("~~onComplete~~");
            }
        };

        //发送器
        FlowableOnSubscribe<Integer> flowableOnSubscribe = new FlowableOnSubscribe<>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                System.out.println("~~create.subscribe~~");

                long begin = System.currentTimeMillis();
                for (int i = 0; i<500; ++i) {
                    emitter.onNext(i);
                }
            }
        };

//        Flowable<Integer> flowable = Flowable.create(flowableOnSubscribe, ERROR);
//        Flowable<Integer> flowable = Flowable.create(flowableOnSubscribe, MISSING);
        //Flowable<Integer> flowable = Flowable.create(flowableOnSubscribe, DROP);
        Flowable<Integer> flowable = Flowable.create(flowableOnSubscribe, LATEST);
//        Flowable<Integer> flowable = Flowable.create(flowableOnSubscribe, BUFFER);
        flowable.observeOn(Schedulers.newThread()).subscribe(subscriber);

        try {
            Thread.sleep(6500L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
