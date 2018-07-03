package io.github.keep2iron.api.core;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavCallback;

import java.lang.ref.WeakReference;

import io.github.keep2iron.api.Pitaya;
import io.github.keep2iron.api.exception.ActivityLostException;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/07/01 19:56
 */
public class NavigationDelegate {
	private PublishSubject<ResultWrapper> mSubject;
	private Postcard mPostcard;

	private WeakReference<AppFragment> mAppFragmentRef;
	private WeakReference<SupportFragment> mSupportFragmentRef;

	public NavigationDelegate(@NonNull Postcard postcard) {
		mPostcard = postcard;
	}

	public NavigationDelegate(@NonNull AppFragment fragment,
	                          @NonNull Postcard postcard,
	                          @NonNull PublishSubject<ResultWrapper> subject) {
		mPostcard = postcard;
		mSubject = subject;
		mAppFragmentRef = new WeakReference<>(fragment);
	}

	public NavigationDelegate(@NonNull SupportFragment fragment,
	                          @NonNull Postcard postcard,
	                          @NonNull PublishSubject<ResultWrapper> subject) {
		mPostcard = postcard;
		mSubject = subject;
		mSupportFragmentRef = new WeakReference<>(fragment);
	}


	public void navigationForResult(int requestCode) {
		if (mAppFragmentRef != null) {
			AppFragment fragment = mAppFragmentRef.get();
			if (fragment != null) {
				onNavigationWithAppFragment(requestCode, fragment);
			}
		} else if (mSupportFragmentRef != null) {
			SupportFragment fragment = mSupportFragmentRef.get();
			if (fragment != null) {
				onNavigationWithSupportFragment(requestCode, fragment);
			}
		} else {
			throw new IllegalArgumentException("you must attach a Fragment or android.support,v4.Fragment");
		}
	}

	public void navigation(){
		navigation(null);
	}

	public void navigation(Context context){
		if(context != null) {
			mPostcard.navigation(context);
		}else{
			mPostcard.navigation();
		}
	}

	/**
	 * 该方法调用的时机在于
	 *
	 * @param requestCode 请求code @see Pitaya.DEFAULT_REQUEST_CODE
	 * @param resultCode  一般是Intent.RESULT_OK
	 * @param data        intent带来的数据
	 */
	public void onNavigationResult(int requestCode, int resultCode, Intent data) {
		mSubject.onNext(new ResultWrapper(data, requestCode, resultCode));
		mSubject.onComplete();
	}

	private void onNavigationWithAppFragment(final int requestCode, AppFragment fragment) {
		mPostcard.navigation(fragment, requestCode, new NavCallback() {
			@Override
			public void onLost(Postcard postcard) {
				mSubject.onError(new ActivityLostException(postcard));
			}

			@Override
			public void onArrival(Postcard postcard) {
				// Do nothing
			}
		});
	}

	private void onNavigationWithSupportFragment(final int requestCode, SupportFragment fragment) {
		mPostcard.navigation(fragment, requestCode, new NavCallback() {
			@Override
			public void onLost(Postcard postcard) {
				mSubject.onError(new ActivityLostException(postcard));
			}

			@Override
			public void onArrival(Postcard postcard) {
				// Do nothing
			}
		});
	}

	public Observable<ResultWrapper> toObservable() {
		return mSubject;
	}

	public void clear() {
		mSubject = null;
		mPostcard = null;
		if (mAppFragmentRef != null) {
			mAppFragmentRef.clear();
			mAppFragmentRef = null;
		}
		if (mSupportFragmentRef != null) {
			mSupportFragmentRef.clear();
			mSupportFragmentRef = null;
		}
	}
}
