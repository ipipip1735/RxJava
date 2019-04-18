import io.reactivex.Maybe;
import io.reactivex.Observable;

/**
 * Created by Administrator on 2019/4/18 14:08.
 */

public class MaybeTrial {

    public static void main(String[] args) {
        MaybeTrial maybeTrial = new MaybeTrial();
        maybeTrial.elementAt();
    }
    private void elementAt() {
        Maybe<Integer> maybe = Observable.range(2, 5)
                .elementAt(1);
    }
}
