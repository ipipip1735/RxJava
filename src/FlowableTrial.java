import io.reactivex.Flowable;

/**
 * Created by Administrator on 2019/4/18.
 */

public class FlowableTrial {
    public static void main(String[] args) {
        FlowableTrial flowableTrial = new FlowableTrial();
        flowableTrial.create();
    }

    private void create() {
        String[] strings = {"aa", "bb"};
        Flowable.fromArray(strings).subscribe(s -> System.out.println("Hello " + s + "!"));
    }
}
