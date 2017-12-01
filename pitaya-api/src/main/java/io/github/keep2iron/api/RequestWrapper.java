package io.github.keep2iron.api;

import android.content.Intent;

import com.alibaba.android.arouter.facade.Postcard;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionSpecBuilder;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 16:22
 */
public class RequestWrapper {

    public Postcard mPostcard;
    public int requestCode;

    public RequestWrapper(Postcard postcard, int requestCode) {
        mPostcard = postcard;
        this.requestCode = requestCode;
    }
}
