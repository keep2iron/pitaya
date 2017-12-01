package io.github.keep2iron.api.matisse;

import android.app.Activity;
import android.net.Uri;
import android.support.v4.app.Fragment;

import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionSpecBuilder;

import java.util.List;
import java.util.Set;

import io.reactivex.Observable;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/12/01 11:22
 */
public interface IPhotoSelector {

    /**
     * 启动图片选择器，一切按默认选择项设置
     *
     * @param activity 启动的Activity
     * @return List<Uri> 图片的uri集合
     */
    Observable<List<Uri>> requestPhotoSelector(Activity activity);


    /**
     * 启动图片选择器
     *
     * @param activity 启动的Activity
     * @param type     选择的类型
     * @param builder  选择图片时的构建器
     * @return List<Uri> 图片的uri集合
     */
    Observable<List<Uri>> requestPhotoSelector(Activity activity, MimeType type, SelectionSpecBuilder builder);

    /**
     * 启动图片选择器
     *
     * @param activity 启动的Activity
     * @param types    选择的类型集合
     * @param builder  图片的构建器
     * @return List<Uri> 图片的uri集合
     */
    Observable<List<Uri>> requestPhotoSelector(Activity activity, Set<MimeType> types, SelectionSpecBuilder builder);
}