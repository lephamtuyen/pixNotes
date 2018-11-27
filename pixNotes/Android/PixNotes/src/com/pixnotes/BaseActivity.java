package com.pixnotes;

import android.app.Activity;

import com.pixnotes.configsapp.Configs;

public class BaseActivity extends Activity {

	@Override
	public void onBackPressed() {
		setResult(Configs.BACK_CODE, getIntent());
		finish();
	}

}
