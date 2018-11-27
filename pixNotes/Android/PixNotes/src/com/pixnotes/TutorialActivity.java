package com.pixnotes;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.PreferenceConnector;
import com.pixnotes.utils.LogUtils;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

public class TutorialActivity extends FragmentActivity {

	 private ViewPager mPager;
	 private PageIndicator mIndicator;
	 private TutorialFragmentAdapter mAdapter;
	 private Button btn_skip;
	 private int requestCode = 0;
	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.tutorial_screen);

	        mAdapter = new TutorialFragmentAdapter(getSupportFragmentManager());

	        mPager = (ViewPager)findViewById(R.id.pager);
	        mPager.setAdapter(mAdapter);

	        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
	        mIndicator.setViewPager(mPager);
	        
	        Bundle mBundle = getIntent().getExtras();
			if(mBundle != null)
			{
				if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL))
				{
					requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL);
					mBundle.remove(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL);
					LogUtils.LogError("requestCode", "requestCode ==== "+requestCode);
				}
			}
	        
	        btn_skip = (Button)findViewById(R.id.btn_skip);
	        btn_skip.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					
					PreferenceConnector.writeBoolean(TutorialActivity.this, Configs.FLAG_OPEN_TUTORIAL, true);
					setResult(requestCode);
					finish();
				}
			});
	        
	        mIndicator.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int position) {
					
					if(mAdapter.ICONS.length - 1 == position)
					{
						btn_skip.setBackgroundColor(getResources().getColor(R.color.start));
						btn_skip.setText(getResources().getString(R.string.str_start));
					}
					else
					{
						btn_skip.setBackgroundColor(getResources().getColor(R.color.skip));
						btn_skip.setText(getResources().getString(R.string.str_skip));
					}
					
				}
				
				@Override
				public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
					
					
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
					
				}
			});
	        
	       
	        
	 }
	 
}
