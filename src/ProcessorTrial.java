import io.reactivex.Flowable;
import io.reactivex.processors.*;
import io.reactivex.subjects.BehaviorSubject;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2020/11/13.
 */

public class ProcessorTrial {


    public static void main(String[] args) {
        ProcessorTrial processorTrial = new ProcessorTrial();

//        processorTrial.publishProcessor();
//        processorTrial.multicastProcessor();
//        processorTrial.unicastProcessor();
        processorTrial.replayProcessor();
//        processorTrial.asyncProcessor();
//        processorTrial.behaviorProcessor();
    }

    private void behaviorProcessor() {

        BehaviorProcessor<Integer> behaviorProcessor = BehaviorProcessor.create();

        behaviorProcessor.subscribe(new IntegerSubscribe("one"));
        behaviorProcessor.onNext(11);
        behaviorProcessor.onNext(22);
        behaviorProcessor.onNext(33);
        behaviorProcessor.subscribe(new IntegerSubscribe("two"));
        behaviorProcessor.onNext(44);
        behaviorProcessor.onNext(55);
        behaviorProcessor.onNext(66);
    }

    private void replayProcessor() {

        ReplayProcessor<Integer> replayProcessor = ReplayProcessor.create();

        replayProcessor.subscribe(new IntegerSubscribe("one"));

        Flowable<Integer> flowable = Flowable.just(11, 22, 33, 44);
        flowable.subscribe(replayProcessor);

        replayProcessor.subscribe(new IntegerSubscribe("two"));

    }


    private void asyncProcessor() {

        AsyncProcessor<Integer> asyncProcessor = AsyncProcessor.create();

        asyncProcessor.subscribe(new IntegerSubscribe("one"));

        Flowable<Integer> flowable = Flowable.just(11, 22, 33, 44);
        flowable.subscribe(asyncProcessor);

    }

    private void unicastProcessor() {
        UnicastProcessor<Integer> unicastProcessor = UnicastProcessor.create();

        unicastProcessor.subscribe(new IntegerSubscribe("one"));
        unicastProcessor.subscribe(new IntegerSubscribe("two"));

        Flowable<Integer> flowable = Flowable.just(11, 22, 33, 44);
        flowable.subscribe(unicastProcessor);


    }

    private void multicastProcessor() {

//        MulticastProcessor<Long> multicastProcessor = MulticastProcessor.create(true);
        MulticastProcessor<Long> multicastProcessor = MulticastProcessor.create(2);

        multicastProcessor.subscribe(new LongSubscribe("one"));

        Flowable.interval(1L, TimeUnit.SECONDS)
                .subscribe(multicastProcessor);

        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        multicastProcessor.subscribe(new LongSubscribe("two"));


        try {
            Thread.sleep(200000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private void publishProcessor() {
        PublishProcessor<Integer> publishProcessor = PublishProcessor.create();

        publishProcessor.subscribe(new IntegerSubscribe("one"));
        publishProcessor.subscribe(new IntegerSubscribe("two"));

        Flowable<Integer> flowable = Flowable.just(11, 22, 33, 44);
        flowable.subscribe(publishProcessor);

    }


    class IntegerSubscribe implements Subscriber<Integer> {
        Subscription s = null;
        String tag;

        public IntegerSubscribe(String tag) {
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
        public void onNext(Integer i) {
            System.out.println("[" + tag + "]~~onNext~~");
            System.out.println("[" + tag + "]integer is " + i);

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


    class LongSubscribe implements Subscriber<Long> {
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
        public void onNext(Long i) {
            System.out.println("[" + tag + "]~~onNext~~");
            System.out.println("[" + tag + "]Long is " + i);

            if (tag.equals("one") && i > 1) {
                System.out.println("one cancel!");
                s.cancel();
            } else {
                s.request(1);
            }
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
