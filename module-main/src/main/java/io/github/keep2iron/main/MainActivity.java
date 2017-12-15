package io.github.keep2iron.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.util.Arrays;
import java.util.List;

import io.github.keep2iron.api.Pitaya;
import io.github.keep2iron.api.RequestWrapper;
import io.github.keep2iron.api.ResultEventBus;
import io.github.keep2iron.api.ResultWrapper;
import io.github.keep2iron.api.fragment.SupportFragment;
import io.github.keep2iron.api.matisse.GifSizeFilter;
import io.github.keep2iron.api.matisse.IPhotoSelector;
import io.github.keep2iron.route.RouteApi;
import io.reactivex.functions.Consumer;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 11:01
 */
@Route(path = "/main/main_activity")
public class MainActivity extends AppCompatActivity {

    @Autowired
    int test;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARouter.getInstance().inject(this);
        Toast.makeText(this, "" + test, Toast.LENGTH_LONG).show();

        IPhotoSelector photoService = Pitaya.createPhotoService(IPhotoSelector.class);
        photoService.requestPhotoSelector(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteApi routeApi = Pitaya.create(RouteApi.class);
                routeApi.requestTestModuleMainActivity(123, MainActivity.this);

                ARouter.getInstance()
                        .build("/main/main_activity")
                        .withTransition(R.anim.anim_alpha_trans_in, R.anim.anim_alpha_trans_out)
                        .navigation(MainActivity.this);

//                Intent intent = new Intent(MainActivity.this, MainActivity.class);
//                startActivityForResult(intent, 0);
//                overridePendingTransition(R.anim.anim_alpha_trans_in, R.anim.anim_alpha_trans_out);

//                routeApi.requestTestModule(123456, MainActivity.this)
//                        .subscribe(new Consumer<ResultWrapper>() {
//                            @Override
//                            public void accept(ResultWrapper resultWrapper) throws Exception {
//                                Toast.makeText(MainActivity.this, resultWrapper + "", Toast.LENGTH_LONG).show();
//                            }
//                        });
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
        overridePendingTransition(R.anim.anim_alpha_trans_in, R.anim.anim_alpha_trans_out);
    }
}