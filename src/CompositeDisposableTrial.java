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


        Disposable disposable = Observable.range(0, 20)
                .observeOn(Schedulers.io())
                .doOnDispose(() -> {
                    System.out.println("~~doOnDispose1~~");
                    System.out.println(Thread.currentThread());
                })
                .forEach(n -> {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(n);
                });
        compositeDisposable.add(disposable);

        compositeDisposable.add(Observable.range(0, 20)
                .observeOn(Schedulers.io())
                .map(n -> n + 100)
                .doOnDispose(() -> {
                    System.out.println("~~doOnDispose2~~");
                    System.out.println(Thread.currentThread());
                })
                .forEach(n -> {
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(n);
                }));




        try {
            Thread.sleep(3000L);
            compositeDisposable.dispose();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }




        try {
            Thread.sleep(60000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }


}
