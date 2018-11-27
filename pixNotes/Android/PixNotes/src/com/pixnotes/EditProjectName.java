package com.pixnotes;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.pixnotes.configsapp.Configs;

public class EditProjectName extends BaseActivity implements OnClickListener{
	
	private int requestCode = 0;
	private String mCurrentProject = "";
	private String[] arrayProjectName ;
	private EditText edt_projectname;
	private RelativeLayout rl_main;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_project_name);
		
		edt_projectname = (EditText)findViewById(R.id.edt_projectname);
		rl_main = (RelativeLayout)findViewById(R.id.rl_main);
		rl_main.setOnClickListener(this);
		
		loadProject();
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.BUNDLE_PROJECT_NAME))
			{
				mCurrentProject = mBundle.getString(Configs.BUNDLE_PROJECT_NAME);
				mBundle.remove(Configs.BUNDLE_PROJECT_NAME);
				edt_projectname.setText(mCurrentProject);
			}
			
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MANAGE_SCREEN_EDIT_PROJECT))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_MANAGE_SCREEN_EDIT_PROJECT);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MANAGE_SCREEN_EDIT_PROJECT);
			}
		}
		
		
	}
	
	private void loadProject()
	{
		ArrayList<String> listProjectName = new ArrayList<String>();
		File dir = getFilesDir();
		try
        {
            File[] fs = dir.listFiles();
            arrayProjectName = null;
            arrayProjectName = new String[fs.length];
            for (int i = 0; i < fs.length; i++)
            {
                try
                {
                    String filePath = fs[i].getAbsoluteFile().getAbsolutePath();
                    String [] getTempFileName = filePath.split("/");
                    String [] fileName = getTempFileName[getTempFileName.length-1].split("\\.");
                    String projectName = fileName[0];
                	listProjectName.add(projectName);
                    arrayProjectName[i] = projectName;
                }
                catch (Exception e)
                {
                   e.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
	}
	
	private boolean isNullProjectName()
	{
		boolean flag = true;
		String projectName = edt_projectname.getText().toString().trim();
		if(projectName != null && projectName.length() > 0)
		{
			return false;
		}
		return flag;
	}
	
	private boolean isDuplicateProject()
	{
		boolean flag = false;
		String projectName = edt_projectname.getText().toString().trim();
		if(arrayProjectName != null && arrayProjectName.length > 0)
		{
			for(int i = 0 ; i < arrayProjectName.length;i++)
			{
				if(projectName.equals(arrayProjectName[i]))
				{
					return true;
				}
			}
		}
		return flag;
	}
	
	private void showAlertDialogProjectNameNull()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(R.string.str_alert_project_name);
		builder.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.cancel();
            }
        });
		
		 AlertDialog alert = builder.create();
         alert.show();
	}
	
	private void showAlertDialogDuplicateProject()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this,AlertDialog.THEME_HOLO_LIGHT);
		builder.setTitle(R.string.str_alert_duplicate_project);
		builder.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.cancel();
            }
        });
		
		 AlertDialog alert = builder.create();
         alert.show();
	}
	
	private void editProjectName()
	{
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(edt_projectname.getWindowToken(), 0);
		setResult(requestCode, getIntent().putExtra(Configs.BUNDLE_PROJECT_NAME, edt_projectname.getText().toString().trim()));
		finish();
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.rl_main:
			if(isNullProjectName())
			{
				showAlertDialogProjectNameNull();
			}
			else if(isDuplicateProject())
			{
				showAlertDialogDuplicateProject();
			}
			else
			{
				editProjectName();
			}
			
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		
		editProjectName();
		
	}
	
	
	
}
