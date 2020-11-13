import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2020/11/13 19:30.
 */
public class PublisherTrial {
    public static void main(String[] args) {

        PublisherTrial publisherTrial = new PublisherTrial();
        publisherTrial.publisher();
    }

    private void publisher() {

        Publisher<String> publisher = new Publisher<>() {

            List<String> list = Arrays.asList("one", "two", "three");

            @Override
            public void subscribe(Subscriber<? super String> s) {
                System.out.println("~~subscribe~~");
                for (String num : list) {
                    s.onNext(num);
                }
            }
        };

        publisher.subscribe(new StrinSubscriber("AAA"));
        publisher.subscribe(new StrinSubscriber("BBB"));


    }




    class StrinSubscriber implements Subscriber<String> {

        String tag = "";

        public StrinSubscriber(String tag) {
            this.tag = tag;
        }

        Subscription subscription = null;
        private long begin = System.currentTimeMillis();

        @Override
        public void onSubscribe(Subscription s) {
            System.out.println(tag + "|~~onSubscribe~~");
            System.out.println(tag + "|Subscription is " + s.hashCode() + "|" + s);
            this.subscription = s;
//                s.request(2);
        }

        @Override
        public void onNext(String s) {
            System.out.println(tag + "|~~onNext~~");
            System.out.println(tag + "|s is " + s);

//                if (System.currentTimeMillis() - begin < 3000) {
//                    try {
//                        Thread.sleep(1000L);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                subscription.request(1);
        }

        @Override
        public void onError(Throwable t) {
            System.out.println(tag + "|~~onError~~");
            System.out.println(tag + "|Throwable is " + t);
        }

        @Override
        public void onComplete() {
            System.out.println(tag + "~~onComplete~~");
        }
    }
}
