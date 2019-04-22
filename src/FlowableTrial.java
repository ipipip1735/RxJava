import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import static io.reactivex.BackpressureStrategy.BUFFER;

/**
 * Created by Administrator on 2019/4/18.
 */

public class FlowableTrial {
    public static void main(String[] args) {
        FlowableTrial flowableTrial = new FlowableTrial();
        flowableTrial.create();
    }

    private void create() {


        Subscriber<Integer> subscriber = new Subscriber<>() {
            Subscription s = null;

            @Override
            public void onSubscribe(Subscription s) {
                System.out.println("~~onSubscribe~~");
                System.out.println("Subscription is " + s.hashCode() + "|" + s);
                this.s = s;
                s.request(10);
            }

            @Override
            public void onNext(Integer i) {
                System.out.println("~~onNext~~");
                System.out.println("integer is " + i);
//                s.request(1);
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


        Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                System.out.println("~~create.subscribe~~");
                for (int i = 0; i < 5; i++) {
                    emitter.onNext(i);
                }
                emitter.onComplete();
            }
        }, BUFFER);

        flowable.subscribe(subscriber);


        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
