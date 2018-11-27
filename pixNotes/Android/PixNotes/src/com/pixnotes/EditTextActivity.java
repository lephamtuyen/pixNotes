package com.pixnotes;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.PreferenceConnector;

public class EditTextActivity extends BaseActivity implements OnClickListener{

	private ImageView mChoose_imgcolor_1;
	private ImageView mChoose_imgcolor_2;
	private ImageView mChoose_imgcolor_3;
	private ImageView mChoose_imgcolor_4;
	private ImageView mChoose_imgcolor_5;
	private ImageView mChoose_imgcolor_6;
	private ImageView mImgcolor_1;
	private ImageView mImgcolor_2;
	private ImageView mImgcolor_3;
	private ImageView mImgcolor_4;
	private ImageView mImgcolor_5;
	private ImageView mImgcolor_6;
	private int mCurrentStrokeWith = 0;
	private int mCurrentColorType = 0;
	private int mStrokeWith = 0;
	private TextView line_tv_small;
	private TextView line_tv_medium;
	private TextView line_tv_large;
	private int[] arrayColor = new int[]{0xFFFF0000,0xFFFFFF33,0xFF0033FF,0xFF00FF00,0xFF838383,0xFFFFFFFF};
	private SeekBar mSeekBar;
	private int requestCode;
	private EditText edit_text;
	private String textValue;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_text_layout);
		initComponnet();
		setComponentListener();
		loadDataForView();
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT);
			}
			if(mBundle.containsKey(Configs.KEY_BUNDLE_VALUE_TEXT))
			{
				textValue = mBundle.getString(Configs.KEY_BUNDLE_VALUE_TEXT);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT);
			}
		}
		
		edit_text.setText(textValue);
	}

	public void initComponnet()
	{
		mSeekBar = (SeekBar) findViewById(R.id.size_bar);
		mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_red));
		mImgcolor_1 = (ImageView) findViewById(R.id.imgcolor_1);
		mImgcolor_2 = (ImageView) findViewById(R.id.imgcolor_2);
		mImgcolor_3 = (ImageView) findViewById(R.id.imgcolor_3);
		mImgcolor_4 = (ImageView) findViewById(R.id.imgcolor_4);
		mImgcolor_5 = (ImageView) findViewById(R.id.imgcolor_5);
		mImgcolor_6 = (ImageView) findViewById(R.id.imgcolor_6);
		mChoose_imgcolor_1 = (ImageView) findViewById(R.id.choose_imgcolor_1);
		mChoose_imgcolor_2 = (ImageView) findViewById(R.id.choose_imgcolor_2);
		mChoose_imgcolor_3 = (ImageView) findViewById(R.id.choose_imgcolor_3);
		mChoose_imgcolor_4 = (ImageView) findViewById(R.id.choose_imgcolor_4);
		mChoose_imgcolor_5 = (ImageView) findViewById(R.id.choose_imgcolor_5);
		mChoose_imgcolor_6 = (ImageView) findViewById(R.id.choose_imgcolor_6);
		line_tv_small = (TextView) findViewById(R.id.line_tv_small);
		line_tv_medium = (TextView) findViewById(R.id.line_tv_medium);
		line_tv_large = (TextView) findViewById(R.id.line_tv_large);
		edit_text = (EditText)findViewById(R.id.edit_text);
	}
	public void setComponentListener()
	{
		mImgcolor_1.setOnClickListener(this);
		mImgcolor_2.setOnClickListener(this);
		mImgcolor_3.setOnClickListener(this);
		mImgcolor_4.setOnClickListener(this);
		mImgcolor_5.setOnClickListener(this);
		mImgcolor_6.setOnClickListener(this);
		
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBarEnd) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seeBarStart) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBarChange, int arg1, boolean arg2) {
				int progress = seekBarChange.getProgress();
				
				if(progress > 0  && progress <= 40)   // touch
				{
					mSeekBar.setProgress(0);
					mCurrentStrokeWith = 0;
				}
				else if(progress > 40 && progress <= 60)
				{
					mSeekBar.setProgress(50);
					mCurrentStrokeWith = 1;
				}
				else if(progress > 60)
				{
					mSeekBar.setProgress(100);
					mCurrentStrokeWith = 2;
				}
				
			}
		});
		
		edit_text.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable edit) {
				if(edit.toString().length() == 0)
				{
					showAlertDialogTextNotNull();
					return ;
				}
				textValue = edit.toString();
			}
		});
	}
	
	private void showAlertDialogTextNotNull()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(R.string.str_alert_text_null);
		builder.setNegativeButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.cancel();
            }
        });
		
		AlertDialog alert = builder.create();
        alert.show();
	}
	
	
	public void loadDataForView()
	{
		mCurrentColorType = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_COLOR, 0);
		mCurrentStrokeWith = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_STROKEWIDTH, 0);
		setColorType(mCurrentColorType);
		if(mCurrentStrokeWith == 0)
		{
			mSeekBar.setProgress(0);
		}
		else if(mCurrentStrokeWith == 1)
		{
			mSeekBar.setProgress(50);
		}
		else
		{
			mSeekBar.setProgress(100);
		}
	}
	public void setLineColor(int indexColor)
	{
		line_tv_small.setBackgroundColor(arrayColor[indexColor]);
		line_tv_medium.setBackgroundColor(arrayColor[indexColor]);
		line_tv_large.setBackgroundColor(arrayColor[indexColor]);
	}
	public void setColorType(int colorType)
	{
		int indexcolor = 0;
		mChoose_imgcolor_1.setVisibility(View.INVISIBLE);
		mChoose_imgcolor_2.setVisibility(View.INVISIBLE);
		mChoose_imgcolor_3.setVisibility(View.INVISIBLE);
		mChoose_imgcolor_4.setVisibility(View.INVISIBLE);
		mChoose_imgcolor_5.setVisibility(View.INVISIBLE);
		mChoose_imgcolor_6.setVisibility(View.INVISIBLE);
	    switch (colorType) {
		case Configs.FLAG_COLORTYPE_1:
			mChoose_imgcolor_1.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_1;
			mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_red));
			break;
		case Configs.FLAG_COLORTYPE_2:
			mChoose_imgcolor_2.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_2;
			mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_yellow));
			break;
		case Configs.FLAG_COLORTYPE_3:
			mChoose_imgcolor_3.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_3;
			mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_blue));
			break;
		case Configs.FLAG_COLORTYPE_4:
			mChoose_imgcolor_4.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_4;
			mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_green));
			break;
		case Configs.FLAG_COLORTYPE_5:
			mChoose_imgcolor_5.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_5;
			mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_gray));
			break;
		case Configs.FLAG_COLORTYPE_6:
			mChoose_imgcolor_6.setVisibility(View.VISIBLE);
			mCurrentColorType = Configs.FLAG_COLORTYPE_6;
			mSeekBar.setThumb(getResources().getDrawable(R.drawable.thumb_seekbar_white));
			break;	
		}
	    setLineColor(mCurrentColorType);
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
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
	
	@Override
	public void onBackPressed() {
		
		PreferenceConnector.writeInteger(this, Configs.FLAG_INDEX_COLOR, mCurrentColorType);
		PreferenceConnector.writeInteger(this, Configs.FLAG_INDEX_STROKEWIDTH, mCurrentStrokeWith);
		setResult(requestCode,getIntent().putExtra(Configs.KEY_BUNDLE_VALUE_TEXT, textValue));
		finish();
	}
}
