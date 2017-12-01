package io.github.keep2iron.api.matisse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.zhihu.matisse.SelectionSpecBuilder;
import com.zhihu.matisse.ui.MatisseActivity;

import io.github.keep2iron.api.ResultEventBus;
import io.github.keep2iron.api.ResultWrapper;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/12/01 12:02
 */
public class MatisseSupportFragment extends Fragment {

    ResultEventBus mBus;

    int requestCode = 0xff12;
    private SelectionSpecBuilder builder;

    public int getRequestCode() {
        return requestCode;
    }


    public void setBus(ResultEventBus bus) {
        this.mBus = bus;
    }

    public static MatisseSupportFragment getInstance(ResultEventBus bus) {
        MatisseSupportFragment matisseFragment = new MatisseSupportFragment();
        matisseFragment.setBus(bus);

        return matisseFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        builder.forResult(getRequestCode());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mBus.post(new ResultWrapper(data, requestCode, resultCode));
    }

    public void forResult(SelectionSpecBuilder builder) {
        this.builder = builder;
    }
}
