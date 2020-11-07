import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

/**
 * Created by Administrator on 2020/11/3 16:17.
 */
public class CompositeDisposableTrial {
    public static void main(String[] args) {


        CompositeDisposable compositeDisposable = new CompositeDisposable();


        Disposable disposable = Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .doOnDispose(() -> {
                    System.out.println("~~doOnDispose1~~");
                    System.out.println("obs1|" + Thread.currentThread());
                })
                .forEach(n -> System.out.println("obs1|" + n));
        compositeDisposable.add(disposable);

        compositeDisposable.add(Observable.interval(1000L, TimeUnit.MILLISECONDS)
                .map(n -> n * 2)
                .doOnDispose(() -> {
                    System.out.println("~~doOnDispose2~~");
                    System.out.println("obs2|" + Thread.currentThread());
                })
                .forEach(n -> System.out.println("obs2|" + n)));

        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        compositeDisposable.dispose();


        try {
            Thread.sleep(6000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}
