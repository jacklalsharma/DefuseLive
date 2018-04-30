package com.app.defuse.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.app.defuse.base.BaseActivity;
import com.minixeroxindia.defuse.R;

public class SplashActivity extends BaseActivity {

	public final static int SPLASH_DISPLAY_LENGTH = 1 * 1500;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		
		if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		
		setContentView(R.layout.fragment_splash);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(SplashActivity.this,
						HomeActivity.class));
				finish();
			}
		}, SPLASH_DISPLAY_LENGTH);
	}

}
