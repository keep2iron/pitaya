package io.github.keep2iron.api.core;

import android.content.Intent;
import android.util.Log;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/07/01 22:53
 */
public abstract class RouteSubscriber implements Subscriber<ResultWrapper>, Observer<ResultWrapper> {
	private Subscription mSubscription;
	private Disposable mDisposable;

	@Override public void onSubscribe(Subscription s) {
		mSubscription = s;
	}

	@Override public void onSubscribe(Disposable d) {
		mDisposable = d;
	}

	public abstract void onActivityResult(Intent intent, int requestCode, int resultCode);

	@Override public void onNext(ResultWrapper resultWrapper) {
		onActivityResult(resultWrapper.mIntent, resultWrapper.mRequestCode, resultWrapper.mResultCode);

		if (mSubscription != null) {
			mSubscription.cancel();
		}
		if (mDisposable != null) {
			mDisposable.dispose();
		}
	}

	@Override public void onError(Throwable t) {
		Log.e("RouteSubscriber", Log.getStackTraceString(t));
	}

	@Override public void onComplete() {
	}
}
