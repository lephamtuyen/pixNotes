package com.pixnotes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class SplashScreenActivity extends BaseActivity{
	
	private Handler handler;
	private int mDelayTime = 100;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashscreen);
		handler = new Handler();
		handler.postDelayed(runnable, mDelayTime);
	}
	
	private Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
		     
			   try {
				String className = HelpActivity.class.getSimpleName();
   				String packageName = getApplicationContext().getPackageName();
   				if (className.indexOf(packageName) == -1) {
   					className = packageName + "." + className;
   				}
       			Intent intent = new Intent(SplashScreenActivity.this, Class.forName(className));
       			startActivity(intent);
       			finish();
				} catch (Exception e) {
					e.printStackTrace();
				}
			   
		   }
	};

}
