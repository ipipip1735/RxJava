import io.reactivex.Flowable;
import io.reactivex.flowables.ConnectableFlowable;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2020/11/15.
 */
public class ConnectableFlowableTrial {

    public static void main(String[] args) {
        ConnectableFlowableTrial connectableFlowableTrial = new ConnectableFlowableTrial();
        connectableFlowableTrial.publish();
    }

    private void publish() {

        ConnectableFlowable<Long> connectableFlowable = Flowable.interval(1L, TimeUnit.SECONDS)
                .publish();
        connectableFlowable.connect();

        connectableFlowable.subscribe(new LongSubscribe("one"));

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        connectableFlowable.subscribe(new LongSubscribe("two"));


        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }


    class LongSubscribe implements Subscriber<Long>{
        Subscription s = null;
        String tag;

        public LongSubscribe(String tag) {
            this.tag = tag;
        }

        @Override
        public void onSubscribe(Subscription s) {
            System.out.println("[" + tag + "]~~onSubscribe~~");
            System.out.println("[" + tag + "]Subscription is " + s.hashCode() + "|" + s);
            this.s = s;
            s.request(1);
        }

        @Override
        public void onNext(Long l) {
            System.out.println("[" + tag + "]~~onNext~~");
            System.out.println("[" + tag + "]long is " + l);

            s.request(1);
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("[" + tag + "]~~onError~~");
            System.out.println("[" + tag + "]Throwable is " + t);
        }

        @Override
        public void onComplete() {
            System.out.println("[" + tag + "]~~onComplete~~");
        }
    }

}
