import io.reactivex.*;
import io.reactivex.functions.Predicate;

/**
 * Created by Administrator on 2019/4/18 14:08.
 */

public class SingleTrial {

    public static void main(String[] args) {
        SingleTrial singleTrial = new SingleTrial();

//        singleTrial.last();
//        singleTrial.first();
//        singleTrial.firstWithCustom();

        singleTrial.create();
    }

    private void firstWithCustom() {
        Single<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onComplete();
            }
        }).first(4);
        observable.subscribe(System.out::println);
    }

    private void last() {
        Single<Integer> observable = Observable.range(2, 5)
                .last(4);
        observable.subscribe(System.out::println);
    }

    private void first() {
        Single<Integer> observable = Observable.range(2, 5)
                .first(4);
        observable.subscribe(System.out::println);
    }


    private void create() {

        Single<Integer> single = Single.create(new SingleOnSubscribe<Integer>() {
            @Override
            public void subscribe(SingleEmitter<Integer> emitter) throws Exception {
                emitter.onSuccess(888);//没有onNext和onComplete事件
//                emitter.onError(new Throwable("xXx"));
            }
        });

        single.subscribe(System.out::println);
    }
}
