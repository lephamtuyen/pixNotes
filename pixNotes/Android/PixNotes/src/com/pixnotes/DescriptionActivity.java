package com.pixnotes;

import com.pixnotes.common.PreferenceConnector;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.objects.ManagerImageObject;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

public class DescriptionActivity extends BaseActivity implements OnClickListener {

	private RelativeLayout rl_parent;
	private Button btn_ok;
	private EditText edt_description;
	private int requestCode;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.description_screen);
		initComponent();
		setComponentListener();
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_ADD_DESCRIPTION))
			{
				requestCode =  mBundle.getInt(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_ADD_DESCRIPTION);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_ADD_DESCRIPTION);
			}
			
			if(mBundle.containsKey(Configs.BUNDLE_DESCRIPTION_RESULT))
			{
				String description = mBundle.getString(Configs.BUNDLE_DESCRIPTION_RESULT);
				edt_description.setText(description);
				mBundle.remove(Configs.BUNDLE_DESCRIPTION_RESULT);
			}
		}
	}
	
	public void initComponent()
	{
		rl_parent = (RelativeLayout)findViewById(R.id.rl_parent);
		btn_ok = (Button)findViewById(R.id.btn_ok);
		edt_description = (EditText)findViewById(R.id.edt_description);
	}
	
	public void setComponentListener()
	{
		rl_parent.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
	}
	
	public void closeScreen(String descriptionResult)
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
	    imm.hideSoftInputFromWindow(edt_description.getWindowToken(), 0);
	    
	    setResult(requestCode,getIntent().putExtra(Configs.BUNDLE_DESCRIPTION_RESULT,descriptionResult));
	    finish();
	}
	
	@Override
	public void onBackPressed() {
		closeScreen(edt_description.getText().toString().trim());
	}
	
	public void saveDescription()
	{
//		int index = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
//		String description = edt_description.getText().toString().trim();
//		if(description.length() > 0)
//		{	
//			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(index).setmDescription(description);
//		}
//		MainActivity.getInstance().mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
//		MainActivity.getInstance().mManagerObject = null;
//		System.gc();
//		MainActivity.getInstance().mManagerObject  = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
//		closeScreen();
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.rl_parent:
			closeScreen(edt_description.getText().toString().trim());
			break;
		case R.id.btn_ok:
			closeScreen(edt_description.getText().toString().trim());
			break;
		
		default:
			break;
		}
	}

}
