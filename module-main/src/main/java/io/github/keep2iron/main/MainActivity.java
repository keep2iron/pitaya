package io.github.keep2iron.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import io.github.keep2iron.api.Pitaya;
import io.github.keep2iron.api.core.ResultWrapper;
import io.github.keep2iron.api.core.RouteSubscriber;
import io.github.keep2iron.route.RouteApi;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 11:01
 */
@Route(path = "/main/main_activity")
public class MainActivity extends AppCompatActivity {

	@Autowired int test;
	@Autowired(name = "parcelable") ArrayList<TestParcelable> testParcelables;
	@Autowired GsonItemType gsonItemType;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ARouter.getInstance().inject(this);
		Toast.makeText(this, "" + test, Toast.LENGTH_LONG).show();
		Log.e("test", "" + testParcelables);
		final RouteApi routeApi = Pitaya.create(MainActivity.this, RouteApi.class);

		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//                routeApi.requestTestModuleMainActivity(123);
//                ArrayList<TestParcelable> testParcelables = new ArrayList<>();
//                testParcelables.add(new TestParcelable(1,""));
//                ARouter.getInstance()
//                        .build("/main/main_activity")
//                        .withTransition(R.anim.anim_alpha_trans_in, R.anim.anim_alpha_trans_out)
//                        .withObject("parcelable", testParcelables)
//                        .navigation(MainActivity.this);

//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivityForResult(intent, 0);

				routeApi.requestTestModule(123456,"1231321")
						.subscribe(new RouteSubscriber() {
							@Override public void onActivityResult(Intent intent, int requestCode, int resultCode) {
								Toast.makeText(MainActivity.this, intent + "" + " requestCode :" + requestCode + " resultCode : " + resultCode, Toast.LENGTH_LONG).show();
							}
						});
				overridePendingTransition(R.anim.anim_alpha_trans_in, R.anim.anim_alpha_trans_out);
			}
		});


//        IPhotoSelector photoService = Pitaya.createPhotoService(IPhotoSelector.class);
//        photoService.requestPhotoSelector(this,4)
//                .subscribe(new Consumer<List<Uri>>() {
//                    @Override
//                    public void accept(List<Uri> uris) throws Exception {
//                        Log.e("tag", "uris " + uris);
//                        Log.e("tag", "uris " + uris.size());
//                    }
//                });

//        Matisse.from(MainActivity.this)
//                .choose(MimeType.allOf())
//                .countable(true)
//                .maxSelectable(9)
//                .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
//                .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
//                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                .thumbnailScale(0.85f)
//                .imageEngine(new GlideEngine())
//                .forResult(123);
	}

	@Override
	public void onBackPressed() {
		finish();
//		overridePendingTransition(R.anim.anim_alpha_trans_in, R.anim.anim_alpha_trans_out);
	}
}