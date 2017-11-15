package io.github.keep2iron.api;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.github.keep2iron.api.fragment.AppFragment;
import io.github.keep2iron.api.fragment.SupportFragment;
import io.github.keep2iron.pitaya.annntation.Extra;
import io.github.keep2iron.pitaya.annntation.RouteUri;
import io.reactivex.Observable;

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

                        if (method != null && args != null && method.getParameterAnnotations().length != args.length) {
                            throw new IllegalArgumentException("you must sure parameter annotation size == args size");
                        }

                        String route = uri.path();
                        Postcard postcard = ARouter.getInstance()
                                .build(route);

                        Activity activity = buildPostcard(postcard, method, args);
                        if (activity != null) {
                            int requestCode = uri.requestCode() == -1 ? DEFAULT_REQUEST_CODE : uri.requestCode();
                            return startActivityForResult(activity, postcard, requestCode);
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
            AppFragment fragment = AppFragment.getInstance(wrapper, BUS);
            android.app.FragmentManager manager = activity.getFragmentManager();
            android.app.FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(android.R.id.content, fragment).commitAllowingStateLoss();
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

        Class<?> returnClass = method.getReturnType();

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

        if (!returnClass.equals(Observable.class)) {
            throw new IllegalArgumentException(method.getName() + "'s return type must be Observable<ResultWrapper>");
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


}
