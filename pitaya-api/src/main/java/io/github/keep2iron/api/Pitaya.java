package io.github.keep2iron.api;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.SelectionSpecBuilder;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.keep2iron.api.fragment.AppFragment;
import io.github.keep2iron.api.fragment.SupportFragment;
import io.github.keep2iron.api.matisse.GifSizeFilter;
import io.github.keep2iron.api.matisse.Glide4Engine;
import io.github.keep2iron.api.matisse.IPhotoSelector;
import io.github.keep2iron.api.matisse.MatisseSupportFragment;
import io.github.keep2iron.pitaya.annntation.Extra;
import io.github.keep2iron.pitaya.annntation.RouteAnim;
import io.github.keep2iron.pitaya.annntation.RouteUri;
import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/08 10:34
 */
public class Pitaya {
    public static final int DEFAULT_REQUEST_CODE = 0xffff;
    /**
     * 当onActivityResult过来的时候，使用该对象进行发送事件
     */
    private static final ResultEventBus BUS = ResultEventBus.getInstance();

    public static <T> T create(Class<T> service) {
        validateServiceInterface(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        RouteUri uri = method.getAnnotation(RouteUri.class);
                        if (uri == null || uri.path() == null || uri.path().isEmpty()) {
                            throw new IllegalArgumentException("your mast add @RouteUri in your method and not null or empty....");
                        }

                        RouteAnim anim = method.getAnnotation(RouteAnim.class);

                        if (method != null && args != null && method.getParameterAnnotations().length != args.length) {
                            throw new IllegalArgumentException("you must sure parameter annotation size == args size");
                        }

                        String route = uri.path();
                        Postcard postcard = ARouter.getInstance()
                                .build(route);
                        if (anim != null) {
                            postcard.withTransition(anim.inAnim(), anim.outAnim());
                        }
                        Activity activity = buildPostcard(postcard, method, args);
                        if (anim != null && activity == null) {
                            throw new IllegalArgumentException("please add @Extra(isActivity) Activity activity Parameter in your XService interface,if not anim don't effect");
                        }

                        Class<?> returnClass = method.getReturnType();

                        if (activity != null && returnClass.equals(Observable.class)) {
                            if (anim != null) {
                                throw new IllegalArgumentException("now not support with @RouteAnim with Observable<ResultWrapper>");
                            }

                            int requestCode = uri.requestCode() == -1 ? DEFAULT_REQUEST_CODE : uri.requestCode();
                            return startActivityForResult(activity, postcard, requestCode);
                        } else {
                            if (activity != null) {
                                postcard.navigation(activity);
                            } else {
                                postcard.navigation();
                            }
                        }

                        return Observable.empty();
                    }
                });
    }

    private static Observable<ResultWrapper> startActivityForResult(Activity activity, Postcard postcard, int requestCode) {
        RequestWrapper wrapper = new RequestWrapper(postcard, requestCode);

        if (activity instanceof FragmentActivity) {
            FragmentActivity fragmentActivity = (FragmentActivity) activity;
            SupportFragment fragment = SupportFragment.getInstance(wrapper, BUS);
            FragmentManager manager = fragmentActivity.getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(android.R.id.content, fragment);
            transaction.commitAllowingStateLoss();
        } else {
            throw new IllegalArgumentException("your should extends FragmentActivity.");
        }

        return BUS.filter(requestCode);
    }


    private static void setArguments(Postcard postcard, Object argument, Extra extra) {

        String key = extra.name();
        if (argument.getClass() == Byte.class) {
            postcard.withByte(key, (Byte) argument);
        } else if (argument.getClass() == Short.class) {
            postcard.withShort(key, (Short) argument);
        } else if (argument.getClass() == Integer.class) {
            postcard.withInt(key, (Integer) argument);
        } else if (argument.getClass() == Long.class) {
            postcard.withLong(key, (Long) argument);
        } else if (argument.getClass() == Float.class) {
            postcard.withFloat(key, (Long) argument);
        } else if (argument.getClass() == Double.class) {
            postcard.withDouble(key, (Long) argument);
        } else if (argument.getClass() == Boolean.class) {
            postcard.withBoolean(key, (Boolean) argument);
        } else if (argument.getClass() == Character.class) {
            postcard.withChar(key, (Character) argument);
        } else if (argument.getClass() == String.class) {
            postcard.withString(key, (String) argument);
        } else {
            postcard.withObject(key, argument);
        }
    }

    /**
     * build params
     *
     * @param method
     * @param args   method arguments  @return      ARouter building object
     */
    private static Activity buildPostcard(Postcard postcard, Method method, Object[] args) {
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        if (args == null || args.length == 0) {
            return null;
        }
        Activity activity = null;

        for (int i = 0; i < args.length; i++) {
            Object argument = args[i];
            if (!(parameterAnnotations[i][0] instanceof Extra)) {
                throw new IllegalArgumentException("Annotation must be @Extra");
            }

            Extra extra = (Extra) parameterAnnotations[i][0];
            if (extra == null) {
                throw new IllegalArgumentException("you must add @Extra in your field");
            }

            boolean isNameEmpty = (extra.name() == null || extra.name().isEmpty());
            if (isNameEmpty && !extra.isActivity()) {
                throw new IllegalArgumentException("you must add @Extra(name = '....') is not null");
            }

            setArguments(postcard, argument, extra);

            if (activity != null && extra.isActivity()) {
                throw new IllegalArgumentException(method.getName() + "'s parameter must have one Activity parameter");
            }

            if (extra.isActivity()) {
                if (!(argument instanceof Activity)) {
                    throw new IllegalArgumentException(method.getName() + "'s @Extra(activity) type is not a Activity");
                }

                activity = (Activity) argument;
            }
        }

        return activity;
    }

    private static <T> void validateServiceInterface(Class<T> service) {
        if (!service.isInterface()) {
            throw new IllegalArgumentException("API declarations must be interfaces.");
        }
        // Prevent API interfaces from extending other interfaces. This not only avoids a bug in
        // Android (http://b.android.com/58753) but it forces composition of API declarations which is
        // the recommended pattern.
        if (service.getInterfaces().length > 0) {
            throw new IllegalArgumentException("API interfaces must not extend other interfaces.");
        }
    }

    public static <T extends IPhotoSelector> T createPhotoService(final Class<T> service) {
        validateServiceInterface(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        Activity activity = (Activity) args[0];
                        Resources resources = activity.getResources();
                        Set<MimeType> mimeTypes;
                        MatisseSupportFragment supportFragment;
                        int max = 1;
                        Matisse matisse;

                        if (activity instanceof FragmentActivity) {
                            supportFragment = MatisseSupportFragment.getInstance(BUS);
                        } else {
                            throw new IllegalArgumentException("please make activity extends FragmentActivity");
                        }

                        FragmentActivity fragmentActivity = (FragmentActivity) activity;
                        FragmentManager manager = fragmentActivity.getSupportFragmentManager();
                        FragmentTransaction transaction = manager.beginTransaction();
                        transaction.replace(android.R.id.content, supportFragment);
                        transaction.commitAllowingStateLoss();

                        matisse = Matisse.from(supportFragment);
                        Class<? extends Matisse> matisseClass = matisse.getClass();
                        Field mMatisse = matisseClass.getDeclaredField("mContext");
                        mMatisse.setAccessible(true);
                        mMatisse.set(matisse, new WeakReference<>(activity));

                        boolean haveMimeType = args.length > 1;
                        if (haveMimeType) {
                            Object arg = args[1];
                            if (arg instanceof Set) {
                                mimeTypes = (Set<MimeType>) arg;
                            } else if (arg instanceof MimeType) {
                                mimeTypes = new HashSet<>();
                                mimeTypes.add((MimeType) arg);
                            } else {
                                mimeTypes = MimeType.allOf();
                                max = (int) arg;
                            }
                        } else {
                            mimeTypes = MimeType.allOf();
                        }

                        SelectionSpecBuilder builder = matisse.choose(mimeTypes);

                        boolean isHaveMaxCount = args.length > 2;
                        if (isHaveMaxCount) {
                            max = (int) args[2];
                        }

                        builder.countable(true)
                                .maxSelectable(max)
                                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                                .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                                .thumbnailScale(0.85f)
                                .imageEngine(new Glide4Engine());
                        supportFragment.forResult(builder);

                        return BUS.filter(supportFragment.getRequestCode())
                                .map(new Function<ResultWrapper, List<Uri>>() {
                                    @Override
                                    public List<Uri> apply(ResultWrapper resultWrapper) throws Exception {
                                        List<Uri> uris = Matisse.obtainResult(resultWrapper.mIntent);
                                        return uris;
                                    }
                                });
                    }
                }
        );
    }
}
