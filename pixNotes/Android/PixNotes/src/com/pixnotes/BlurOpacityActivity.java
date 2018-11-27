package com.pixnotes;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.PreferenceConnector;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class BlurOpacityActivity extends BaseActivity implements OnClickListener{
	
	private int requestCode = 0;
	private SeekBar mSeekBar;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.opacity_setting);
		
		mSeekBar = (SeekBar) findViewById(R.id.size_bar);
		mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_red));
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBarEnd) {
				int progress = seekBarEnd.getProgress();
				if(progress == 0)
				mSeekBar.setProgress(1);
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seeBarStart) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBarChange, int arg1, boolean arg2) {
				int progress = seekBarChange.getProgress();
				if(progress == 0)
					mSeekBar.setProgress(1);
			}
		});
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_BLUR_OPACITY))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_BLUR_OPACITY);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_BLUR_OPACITY);
			}
		}
		
		int blurOpacity = PreferenceConnector.readInteger(this, Configs.FLAG_BLUR_OPACTIY, Configs.MIN_OPACITY_BLUR);
		mSeekBar.setProgress(blurOpacity);
	}
	
	@Override
	public void onClick(View arg0) {
		
		
	}

	@Override
	public void onBackPressed() {
		
		PreferenceConnector.writeInteger(this, Configs.FLAG_BLUR_OPACTIY, mSeekBar.getProgress());
		setResult(requestCode);
		finish();
	}
	
}
