package io.github.keep2iron.api.exception;

import com.alibaba.android.arouter.facade.Postcard;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2018/07/01 22:08
 * <p>
 * Activity丢失异常,执行navigation callback的onLost回调时触发
 */
public class ActivityLostException extends Throwable {

	public ActivityLostException(Postcard postcard) {
		super(postcard.getUri().toString() + "is not equals any activity uri!");
	}

	public ActivityLostException(String message) {
		super(message);
	}

	public ActivityLostException(String message, Throwable cause) {
		super(message, cause);
	}

	public ActivityLostException(Throwable cause) {
		super(cause);
	}

	public ActivityLostException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
