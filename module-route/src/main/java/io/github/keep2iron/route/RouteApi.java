package io.github.keep2iron.route;

import io.github.keep2iron.api.core.ResultWrapper;
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
	 * 以startActivityForResult 跳转testModule的MainActivity
	 *
	 * @param requestCode 跳转的一个数据
	 * @param testString  传入的testString
	 * @return ResultWrapper
	 */
	@RouteUri(path = MODULE_TEST.Route.TEST_ACTIVITY)
	Observable<ResultWrapper> requestTestModule(@Extra(name = MODULE_TEST.Extra.EXTRA_INT_REQUEST_CODE) int requestCode,
	                                            @Extra(name = MODULE_TEST.Extra.EXTRA_STRING_TEST_STRING) String testString);

}
