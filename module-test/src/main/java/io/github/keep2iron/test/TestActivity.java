package io.github.keep2iron.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;

import java.util.ArrayList;

import io.github.keep2iron.api.Pitaya;

/**
 * @author keep2iron <a href="http://keep2iron.github.io">Contract me.</a>
 * @version 1.0
 * @since 2017/11/14 11:06
 */
@Route(path = "/test/test_activity")
public class TestActivity extends Activity {

	@Autowired
	int requestCode;

	@Autowired
	String testString;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		Pitaya.bind(this);

		findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("test_code", 123456);
				setResult(RESULT_OK, intent);
				finish();
			}
		});

		Toast.makeText(this, "requestCode : " + requestCode + " testString : " + testString, Toast.LENGTH_SHORT).show();
	}
}
