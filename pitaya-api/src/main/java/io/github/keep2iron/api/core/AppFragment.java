package io.github.keep2iron.api.core;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;

import com.alibaba.android.arouter.facade.Postcard;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 16:00
 */
public class AppFragment extends Fragment {
    private NavigationDelegate mDelegate;

    private void setDelegate(NavigationDelegate delegate) {
        mDelegate = delegate;
    }

    public static AppFragment getInstance(Postcard postcard) {
        PublishSubject<ResultWrapper> mSubject = PublishSubject.create();
        AppFragment fragment = new AppFragment();
        fragment.setDelegate(new NavigationDelegate(fragment, postcard, mSubject));

        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mDelegate.navigation();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mDelegate.onNavigationResult(requestCode, resultCode, data);
    }

    @Override public void onDestroy() {
        super.onDestroy();
        mDelegate.clear();
    }

    public Observable<ResultWrapper> toObservable(){
        return mDelegate.toObservable();
    }
}
