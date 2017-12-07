package io.github.keep2iron.route;

import android.app.Activity;

import io.github.keep2iron.api.ResultWrapper;
import io.github.keep2iron.pitaya.annntation.Extra;
import io.github.keep2iron.pitaya.annntation.RouteUri;
import io.reactivex.Observable;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 11:33
 */
public interface RouteApi {

    /**
     *
     */
    @RouteUri(path = MODULE_MAIN.Route.MAIN_ACTIVITY)
    void requestTestModuleMainActivity(@Extra(name = MODULE_MAIN.Extra.EXTRA_INT_TEST) int test);

    /**
     * 跳转testModule的MainActivity
     *
     * @param requestCode 跳转的一个数据
     * @param activity    需要传入的Activity因为需要
     * @return
     */
    @RouteUri(path = MODULE_TEST.Route.TEST_ACTIVITY)
    Observable<ResultWrapper> requestTestModule(@Extra(name = MODULE_TEST.Extra.EXTRA_INT_REQUEST_CODE) int requestCode,
                                                @Extra(isActivity = true) Activity activity);

}
