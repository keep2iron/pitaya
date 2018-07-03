package io.github.keep2iron.api;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.launcher.ARouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import io.github.keep2iron.api.core.NavigationDelegate;
import io.github.keep2iron.api.core.ResultWrapper;
import io.github.keep2iron.api.core.SupportFragment;
import io.github.keep2iron.api.exception.Assertions;
import io.github.keep2iron.pitaya.annntation.Extra;
import io.github.keep2iron.pitaya.annntation.RouteUri;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/07/08 10:34
 */
public class Pitaya {
	public static final int DEFAULT_REQUEST_CODE = 0xffff;

	public static void bind(Object thiz) {
		ARouter.getInstance().inject(thiz);
	}

	public static <T> T create(Context context, Class<T> service) {
		return create((Activity) context, 0, 0, service);
	}

	public static <T> T create(Class<T> service) {
		return create(null, 0, 0, service);
	}

	@SuppressWarnings("unchecked")
	public static <T> T create(final Activity activity,
	                           final int animInRes,
	                           final int animOutRes,
	                           Class<T> service) {
		Assertions.assertCondition(!service.isInterface(), "API declarations must be interfaces.");

		// Prevent API interfaces from extending other interfaces. This not only avoids a bug in
		// Android (http://b.android.com/58753) but it forces composition of API declarations which is
		// the recommended pattern.
		Assertions.assertCondition(service.getInterfaces().length > 0, "API interfaces must not extend other interfaces.");

		return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
				new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) {
						RouteUri uri = method.getAnnotation(RouteUri.class);
						//创建postcard，并检查参数
						Postcard postcard = createPostcard(method, args, uri);
						//为postcard设置参数
						setArguments(postcard, method, args, animInRes, animOutRes, activity);

						Class<?> returnClass = method.getReturnType();
						boolean isStartActivityResult = returnClass.equals(Observable.class);
						if (isStartActivityResult) {
							Assertions.assertCondition(activity == null, "if your startActivityForResult() activity is not be null!!");
							return buildStartActivityForResult(uri, postcard, activity);
						} else {
							buildOnStartActivity(postcard, activity);
							return Observable.empty();
						}
					}
				});
	}

	private static Observable<ResultWrapper> buildStartActivityForResult(RouteUri uri, Postcard postcard, Activity activity) {
		int requestCode = uri.requestCode() == -1 ? DEFAULT_REQUEST_CODE : uri.requestCode();

		Assertions.assertCondition(!(activity instanceof FragmentActivity), "your should extends FragmentActivity.");

		FragmentActivity fragmentActivity = (FragmentActivity) activity;
		final SupportFragment fragment = SupportFragment.getInstance(postcard, requestCode);
		final FragmentManager manager = fragmentActivity.getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.replace(android.R.id.content, fragment);
		transaction.commitAllowingStateLoss();

		return fragment.toObservable()
				.doOnError(new Consumer<Throwable>() {
					@Override public void accept(Throwable throwable) {
						removeFragment(manager, fragment);
					}
				})
				.doOnNext(new Consumer<ResultWrapper>() {
					@Override public void accept(ResultWrapper resultWrapper) throws Exception {
						removeFragment(manager, fragment);
					}
				});
	}

	private static void removeFragment(FragmentManager manager, SupportFragment fragment) {
		FragmentTransaction transaction = manager.beginTransaction();
		transaction.remove(fragment);
		transaction.commit();
	}

	private static void buildOnStartActivity(Postcard postcard, Activity activity) {
		NavigationDelegate delegate = new NavigationDelegate(postcard);
		delegate.navigation(activity);
	}

	@NonNull private static Postcard createPostcard(Method method, Object[] args, RouteUri uri) {
		if (uri == null || uri.path().isEmpty()) {
			throw new IllegalArgumentException("your mast add @RouteUri in your method and not null or empty....");
		}
		if (args != null && method.getParameterAnnotations().length != args.length) {
			throw new IllegalArgumentException("you must sure parameter annotation size == args size");
		}

		String route = uri.path();
		return ARouter.getInstance()
				.build(route);
	}


	/**
	 * build params
	 *
	 * @param method
	 * @param args   method arguments  @return      ARouter building object
	 */
	private static void setArguments(Postcard postcard,
	                                 Method method,
	                                 Object[] args,
	                                 int animInRes,
	                                 int animOutRes,
	                                 Activity activity) {
		boolean isUseAnim = animInRes > 0 || animOutRes > 0;
		Assertions.assertCondition(isUseAnim && activity == null,
				"please add @Extra(isActivity) Activity activity Parameter in your XService interface,if not anim don't effect");

		postcard.withTransition(animInRes, animOutRes);

		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		if (args == null || args.length == 0) {
			return;
		}

		for (int i = 0; i < args.length; i++) {
			Object argument = args[i];
			Extra extra = (Extra) parameterAnnotations[i][0];

			Assertions.assertCondition(!(parameterAnnotations[i][0] instanceof Extra), "Annotation must be @Extra");
			Assertions.assertCondition(extra == null, "you must add @Extra in your field");
			Assertions.assertCondition(extra.name().isEmpty(), "you must add @Extra(name = '....') is not null");

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
	}
}
