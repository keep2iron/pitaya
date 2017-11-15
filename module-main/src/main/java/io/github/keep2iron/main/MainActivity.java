package io.github.keep2iron.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.gengqiquan.result.Result;
import com.gengqiquan.result.RxActivityResult;

import io.github.keep2iron.api.Pitaya;
import io.github.keep2iron.api.ResultWrapper;
import io.github.keep2iron.route.RouteApi;
import io.reactivex.functions.Consumer;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 11:01
 */
@Route(path = "/main/main_activity")
public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ARouter.getInstance().inject(this);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RouteApi routeApi = Pitaya.create(RouteApi.class);
                routeApi.requestTestModule(123456, MainActivity.this)
                        .subscribe(new Consumer<ResultWrapper>() {
                            @Override
                            public void accept(ResultWrapper resultWrapper) throws Exception {
                                Toast.makeText(MainActivity.this, resultWrapper + "", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}
