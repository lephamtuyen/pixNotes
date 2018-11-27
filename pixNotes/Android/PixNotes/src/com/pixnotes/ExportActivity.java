package com.pixnotes;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.pixnotes.common.MySwitch;
import com.pixnotes.common.MySwitch.OnChangeAttemptListener;
import com.pixnotes.common.Utilities;
import com.pixnotes.objects.DeviceObject;

public class ExportActivity extends BaseActivity implements OnClickListener {

	private Button btn_back;
	private Button btn_send;
	private MySwitch switch_on_off;
	private TextView tv_title;
	private ImageView img_pdf;
	private ImageView img_world;
	private ImageView img_check_pdf;
	private ImageView img_check_world;
	private boolean isPDF = true;
	private boolean isOn = true;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.export_or_send_email);
		initComponent();
		setTitleHeader();
		isCheckPDF(isPDF);
		setComponentListener();
	}
	
	public void initComponent()
	{
		btn_send = (Button) findViewById(R.id.btn_send);
		btn_back = (Button) findViewById(R.id.btn_back);
		switch_on_off = (MySwitch) findViewById(R.id.switch_on_off);
		tv_title = (TextView)findViewById(R.id.tv_title);
		img_pdf = (ImageView) findViewById(R.id.img_pdf);
		img_world = (ImageView) findViewById(R.id.img_world);
		img_check_pdf = (ImageView) findViewById(R.id.img_check_pdf);
		img_check_world = (ImageView) findViewById(R.id.img_check_world);
	}
	
	public void setComponentListener()
	{
		btn_send.setOnClickListener(this);
		btn_back.setOnClickListener(this);
		img_pdf.setOnClickListener(this);
		img_world.setOnClickListener(this);
		switch_on_off.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				isOn = !isChecked;
			}
		});
	}
	
	public void setTitleHeader()
	{
		tv_title.setText(getResources().getString(R.string.export));
	}
	
	
	
	public void isCheckPDF(boolean isPDF)
	{
		if(isPDF)
		{
			img_check_world.setVisibility(View.INVISIBLE);
			img_check_pdf.setVisibility(View.VISIBLE);
		}
		else
		{
			img_check_world.setVisibility(View.VISIBLE);
			img_check_pdf.setVisibility(View.INVISIBLE);
		}
	}
	
	public void actionSave()
	{
		
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_back:
			onBackPressed();
			break;
		case R.id.img_pdf:
			isPDF = true;
			isCheckPDF(isPDF);
			break;
		case R.id.img_world:
			isPDF = false;
			isCheckPDF(isPDF);
			break;
		case R.id.btn_send:
			actionSave();
			break;
		}
		
	}
	
	
}
