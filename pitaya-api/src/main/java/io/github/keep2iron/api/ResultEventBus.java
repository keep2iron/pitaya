package io.github.keep2iron.api;

import io.reactivex.Observable;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 16:34
 */
public class ResultEventBus {

    private PublishSubject<ResultWrapper> mSubject = PublishSubject.create();

    private ResultEventBus() {
    }

    public Observable<ResultWrapper> filter(final int requestCode){
        return mSubject.filter(new Predicate<ResultWrapper>() {
            @Override
            public boolean test(ResultWrapper resultWrapper) throws Exception {
                return requestCode == resultWrapper.mRequestCode;
            }
        });
    }

    public void post(ResultWrapper result) {
        mSubject.onNext(result);
    }

    public static ResultEventBus getInstance() {
        return new ResultEventBus();
    }
}
