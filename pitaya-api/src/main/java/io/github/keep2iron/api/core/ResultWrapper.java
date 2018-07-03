package io.github.keep2iron.api.core;

import android.content.Intent;

import java.io.Serializable;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 16:02
 */
public class ResultWrapper implements Serializable{
    public Intent mIntent;
    public int mRequestCode;
    public int mResultCode;

    public ResultWrapper(Intent intent, int requestCode, int resultCode) {
        mIntent = intent;
        mRequestCode = requestCode;
        mResultCode = resultCode;
    }
}
