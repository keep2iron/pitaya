package io.github.keep2iron.api.core;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.alibaba.android.arouter.facade.Postcard;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 16:00
 */
public class SupportFragment extends Fragment {
	private NavigationDelegate mDelegate;
	private int requestCode;

	public static SupportFragment getInstance(Postcard postcard, int requestCode) {
		PublishSubject<ResultWrapper> mSubject = PublishSubject.create();
		SupportFragment fragment = new SupportFragment();
		fragment.mDelegate = new NavigationDelegate(fragment, postcard, mSubject);
		fragment.requestCode = requestCode;

		return fragment;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		mDelegate.navigationForResult(requestCode);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK && this.requestCode == requestCode) {
			mDelegate.onNavigationResult(requestCode, resultCode, data);
		}
	}

	@Override public void onDestroy() {
		super.onDestroy();
		mDelegate.clear();
	}

	public Observable<ResultWrapper> toObservable() {
		return mDelegate.toObservable();
	}
}
