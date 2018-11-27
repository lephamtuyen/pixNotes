package com.pixnotes;

import com.pixnotes.common.MySwitch;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.PreferenceConnector;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends BaseActivity implements OnClickListener{
	
//	private Button btn_apply;
	private ImageView type_gridview_1;
	private ImageView img_check_type_gridview_1;
	private ImageView type_gridview_2;
	private ImageView img_check_type_gridview_2;
	private ImageView type_gridview_3;
	private ImageView img_check_type_gridview_3;
	private ImageView type_gridview_4;
	private ImageView img_check_type_gridview_4;
	private ImageView choose_imgcolor_1;
	private ImageView choose_imgcolor_2;
	private ImageView choose_imgcolor_3;
	private ImageView choose_imgcolor_4;
	private ImageView choose_imgcolor_5;
	private ImageView choose_imgcolor_6;
	private ImageView imgcolor_1;
	private ImageView imgcolor_2;
	private ImageView imgcolor_3;
	private ImageView imgcolor_4;
	private ImageView imgcolor_5;
	private ImageView imgcolor_6;
	private MySwitch switch_on_off;
	private MySwitch switch_on_off_auto_detect;
	private LinearLayout ln_block_2;
	private LinearLayout ln_block_3;
	private final int mNumberTypeGridView = 4;
	private int mCurrentGridType = 0;
	private int mCurrentColorType = 0;
	private boolean isOnAutoDetect = true;
	private RelativeLayout rl_back;
	private Button btn_back;
	private int requestCode = 0 ;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.setting);
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING);
			}
		}
		
		initComponnet();
		setComponentListener();
	}
	
	@Override
	public void onBackPressed() {
		
			PreferenceConnector.writeInteger(this, Configs.FLAG_TYPE_GIRD, mCurrentGridType);
			PreferenceConnector.writeInteger(this, Configs.FLAG_TYPE_GIRD_COLOR, mCurrentColorType);
			
			setResult(requestCode, getIntent());
			finish();
	}
	public void setOn_Off_GridView(boolean isOn)
	{
		if(isOn)
		{
			ln_block_2.setVisibility(View.VISIBLE);
			ln_block_3.setVisibility(View.VISIBLE);
		}
		else
		{
			ln_block_2.setVisibility(View.GONE);
			ln_block_3.setVisibility(View.GONE);
		}
		actionApplySetting(isOn);
	}
	
	public void initComponnet()
	{
		ln_block_2 = (LinearLayout) findViewById(R.id.ln_block_2);
		ln_block_3 = (LinearLayout) findViewById(R.id.ln_block_3);
//		btn_apply = (Button) findViewById(R.id.btn_apply);
		switch_on_off = (MySwitch) findViewById(R.id.switch_on_off);
		switch_on_off_auto_detect = (MySwitch) findViewById(R.id.switch_on_off_auto_detect);
		type_gridview_1 = (ImageView) findViewById(R.id.type_gridview_1);
	    img_check_type_gridview_1 = (ImageView) findViewById(R.id.img_check_type_gridview_1);
		type_gridview_2 = (ImageView) findViewById(R.id.type_gridview_2);
		img_check_type_gridview_2 = (ImageView) findViewById(R.id.img_check_type_gridview_2);
		type_gridview_3 = (ImageView) findViewById(R.id.type_gridview_3);
		img_check_type_gridview_3 = (ImageView) findViewById(R.id.img_check_type_gridview_3);
		type_gridview_4 = (ImageView) findViewById(R.id.type_gridview_4);
		img_check_type_gridview_4 = (ImageView) findViewById(R.id.img_check_type_gridview_4);
		imgcolor_1 = (ImageView) findViewById(R.id.imgcolor_1);
		imgcolor_2 = (ImageView) findViewById(R.id.imgcolor_2);
		imgcolor_3 = (ImageView) findViewById(R.id.imgcolor_3);
		imgcolor_4 = (ImageView) findViewById(R.id.imgcolor_4);
		imgcolor_5 = (ImageView) findViewById(R.id.imgcolor_5);
		imgcolor_6 = (ImageView) findViewById(R.id.imgcolor_6);
		choose_imgcolor_1 = (ImageView) findViewById(R.id.choose_imgcolor_1);
		choose_imgcolor_2 = (ImageView) findViewById(R.id.choose_imgcolor_2);
		choose_imgcolor_3 = (ImageView) findViewById(R.id.choose_imgcolor_3);
		choose_imgcolor_4 = (ImageView) findViewById(R.id.choose_imgcolor_4);
		choose_imgcolor_5 = (ImageView) findViewById(R.id.choose_imgcolor_5);
		choose_imgcolor_6 = (ImageView) findViewById(R.id.choose_imgcolor_6);
		rl_back = (RelativeLayout)findViewById(R.id.rl_back);
		btn_back = (Button)findViewById(R.id.btn_back);
	}
	
	public void setComponentListener()
	{
//		btn_apply.setOnClickListener(this);
		type_gridview_1.setOnClickListener(this);
		type_gridview_2.setOnClickListener(this);
		type_gridview_3.setOnClickListener(this);
		type_gridview_4.setOnClickListener(this);
		imgcolor_1.setOnClickListener(this);
		imgcolor_2.setOnClickListener(this);
		imgcolor_3.setOnClickListener(this);
		imgcolor_4.setOnClickListener(this);
		imgcolor_5.setOnClickListener(this);
		imgcolor_6.setOnClickListener(this);
		rl_back.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		switch_on_off.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
				setOn_Off_GridView(isChecked);
			}
		});
		
		switch_on_off_auto_detect.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.e("isChecked", "isChecked ==== "+isChecked);
				PreferenceConnector.writeBoolean(SettingActivity.this, Configs.BUNDLE_AUTO_DETECT, isChecked);
			}
		});
		
		boolean isAutoDetect = PreferenceConnector.readBoolean(this, Configs.BUNDLE_AUTO_DETECT, false);
		switch_on_off_auto_detect.setChecked(isAutoDetect);
		
		boolean gridTemplate = PreferenceConnector.readBoolean(this, Configs.FLAG_APPLY_GIRD, false);
		
		switch_on_off.setChecked(gridTemplate);
		
		mCurrentGridType = PreferenceConnector.readInteger(this, Configs.FLAG_TYPE_GIRD, 0);
		setGridType(mCurrentGridType);
		mCurrentColorType = PreferenceConnector.readInteger(this, Configs.FLAG_TYPE_GIRD_COLOR, 0);
		setColorType(mCurrentColorType);
		
		setOn_Off_GridView(gridTemplate);
	}
	
	public void setGridType(int gridType)
	{
		img_check_type_gridview_1.setVisibility(View.INVISIBLE);
		img_check_type_gridview_2.setVisibility(View.INVISIBLE);
		img_check_type_gridview_3.setVisibility(View.INVISIBLE);
		img_check_type_gridview_4.setVisibility(View.INVISIBLE);
		switch (gridType) {
		case Configs.FLAG_GRIDTYPE_1:
			img_check_type_gridview_1.setVisibility(View.VISIBLE);
			mCurrentGridType = Configs.FLAG_GRIDTYPE_1;
			break;
		case Configs.FLAG_GRIDTYPE_2:
			img_check_type_gridview_2.setVisibility(View.VISIBLE);
			mCurrentGridType = Configs.FLAG_GRIDTYPE_2;
			break;
		case Configs.FLAG_GRIDTYPE_3:
			img_check_type_gridview_3.setVisibility(View.VISIBLE);
			mCurrentGridType = Configs.FLAG_GRIDTYPE_3;
			break;
		case Configs.FLAG_GRIDTYPE_4:
			img_check_type_gridview_4.setVisibility(View.VISIBLE);
			mCurrentGridType = Configs.FLAG_GRIDTYPE_4;
			break;
		}
	}
	
	public void setColorType(int colorType)
	{
		choose_imgcolor_1.setVisibility(View.INVISIBLE);
		choose_imgcolor_2.setVisibility(View.INVISIBLE);
		choose_imgcolor_3.setVisibility(View.INVISIBLE);
		choose_imgcolor_4.setVisibility(View.INVISIBLE);
		choose_imgcolor_5.setVisibility(View.INVISIBLE);
		choose_imgcolor_6.setVisibility(View.INVISIBLE);
	    switch (colorType) {
		case Configs.FLAG_COLORTYPE_1:
			choose_imgcolor_1.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_1;
			break;
		case Configs.FLAG_COLORTYPE_2:
			choose_imgcolor_2.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_2;
			break;
		case Configs.FLAG_COLORTYPE_3:
			choose_imgcolor_3.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_3;
			break;
		case Configs.FLAG_COLORTYPE_4:
			choose_imgcolor_4.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_4;
			break;
		case Configs.FLAG_COLORTYPE_5:
			choose_imgcolor_5.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_5;
			break;
		case Configs.FLAG_COLORTYPE_6:
			choose_imgcolor_6.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_6;
			break;	
		}
	}
	
	
	public void actionApplySetting(boolean isOn)
	{
		PreferenceConnector.writeBoolean(this, Configs.FLAG_APPLY_GIRD, isOn);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			onBackPressed();
			break;
		case R.id.btn_back:
			onBackPressed();
			break;
		case R.id.type_gridview_1:
			setGridType(Configs.FLAG_GRIDTYPE_1);
			break;
		case R.id.type_gridview_2:
			setGridType(Configs.FLAG_GRIDTYPE_2);
			break;
		case R.id.type_gridview_3:
			setGridType(Configs.FLAG_GRIDTYPE_3);
			break;
		case R.id.type_gridview_4:
			setGridType(Configs.FLAG_GRIDTYPE_4);
			break;
		case R.id.imgcolor_1:
			setColorType(Configs.FLAG_COLORTYPE_1);
			break;
		case R.id.imgcolor_2:
			setColorType(Configs.FLAG_COLORTYPE_2);
			break;
		case R.id.imgcolor_3:
			setColorType(Configs.FLAG_COLORTYPE_3);
			break;
		case R.id.imgcolor_4:
			setColorType(Configs.FLAG_COLORTYPE_4);
			break;
		case R.id.imgcolor_5:
			setColorType(Configs.FLAG_COLORTYPE_5);
			break;
		case R.id.imgcolor_6:
			setColorType(Configs.FLAG_COLORTYPE_6);
			break;
			
		}
		
	}
		
}