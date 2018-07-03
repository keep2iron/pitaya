package io.github.keep2iron.main;

import android.app.Application;

import com.alibaba.android.arouter.launcher.ARouter;
import com.squareup.leakcanary.LeakCanary;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 11:26
 */
public class AppApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// 这两行必须写在init之前，否则这些配置在init过程中将无效
		if (BuildConfig.DEBUG) {
			// 打印日志
			ARouter.openLog();
			// 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
			ARouter.openDebug();
		}
		// 尽可能早，推荐在Application中初始化
		ARouter.init(this);

		if (LeakCanary.isInAnalyzerProcess(this)) {
			// This process is dedicated to LeakCanary for heap analysis.
			// You should not init your app in this process.
			return;
		}
		LeakCanary.install(this);
		// Normal app init code...
	}
}
