package com.pixnotes;

import java.io.File;
import java.util.ArrayList;

import com.pixnotes.OpenProjectActivity.ItemProjectName;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.PreferenceConnector;
import com.pixnotes.objects.ManagerImageObject;
import com.pixnotes.utils.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class HelpActivity extends Activity implements OnClickListener{
	
	private Button btn_new_project;
	private String[] arrayProjectName ;
	private ListView lv_open_project ;
	private ItemProjectName mAdapterProjectName;
	private RelativeLayout rl_listview;
	private RelativeLayout rl_message;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_screen);
		initComponent();
		setComponentListener();
		
		boolean isOpenTutorial = PreferenceConnector.readBoolean(this, Configs.FLAG_OPEN_TUTORIAL, false);
		if(!isOpenTutorial)
		{
			openTutorial();
			checkVisibleMessage(false);
		}
		else
		{
			checkVisibleMessage(true);
		}
		
		if(mAdapterProjectName != null && mAdapterProjectName.listProjectName.size() > 0)
		{
			checkVisibleListView(true);
			checkVisibleMessage(false);
		}
		
			
	}

	private void initComponent()
	{
		btn_new_project = (Button)findViewById(R.id.btn_new_project);
		lv_open_project = (ListView)findViewById(R.id.example_lv_list);
		rl_listview = (RelativeLayout)findViewById(R.id.rl_listview);
		rl_message = (RelativeLayout)findViewById(R.id.rl_message);
		
		lv_open_project.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	
            	String mProjectName = mAdapterProjectName.listProjectName.get(position);
            	openMainScreen(mProjectName);
            }
        });
		
		loadProject();
		
		lv_open_project.setAdapter(mAdapterProjectName);
	}
	
	private void checkVisibleListView(boolean isVisible)
	{
		if(isVisible)
		{
			rl_listview.setVisibility(View.VISIBLE);
		}
		else
		{
			rl_listview.setVisibility(View.GONE);
		}
	}
	
	private void checkVisibleMessage(boolean isVisible)
	{
		if(isVisible)
		{
			rl_message.setVisibility(View.VISIBLE);
		}
		else
		{
			rl_message.setVisibility(View.GONE);
		}
	}
	
	private void setComponentListener()
	{
		btn_new_project.setOnClickListener(this);
	}
	
	private void openTutorial()
	{
		try {
			String className = TutorialActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(getApplicationContext(), Class.forName(className));
			intent.putExtra(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL, Configs.BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL);
			startActivityForResult(intent, Configs.BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openProject()
	{
		try {
			String className = OpenProjectNewLayoutActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(getApplicationContext(), Class.forName(className));
			intent.putExtra(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT, Configs.BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT);
			startActivityForResult(intent, Configs.BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT);
			
		} catch (Exception e) {
			e.printStackTrace();
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
		
		if(mAdapterProjectName == null)
		{
			mAdapterProjectName = new ItemProjectName(this, listProjectName);
		}
		else
		{
			mAdapterProjectName.listProjectName.removeAll(mAdapterProjectName.listProjectName);
			mAdapterProjectName.listProjectName.addAll(listProjectName);
		}
		mAdapterProjectName.notifyDataSetChanged();
	}
	
	public class ItemProjectName extends BaseAdapter {
		
		public Context mContext;
		public ArrayList<String> listProjectName ;
		public ItemProjectName(Context context,ArrayList<String> listProjectName) {
			mContext = context;
			this.listProjectName = listProjectName; 
		}
    	
		public void updateListImage(ManagerImageObject mManagerObject)
		{
			notifyDataSetChanged();
		}
    	
		@Override
    	public int getCount() {
    		
    		return listProjectName.size();
    	}

    	@Override
    	public Object getItem(int position) {
    		
    		return listProjectName.get(position);
    	}

    	@Override
    	public long getItemId(int position) {
    		
    		return position;
    	}

    	@Override
    	public View getView(final int position,final View convertView, ViewGroup parent) {
    		
    		View row = convertView;
            if (convertView == null) {
            	row = LayoutInflater.from(mContext).inflate(R.layout.open_project_item_not_delete, parent, false);
            } 
            String projectName = listProjectName.get(position);
            TextView tv_projectname = (TextView)row.findViewById(R.id.tv_projectname);
            tv_projectname.setText(projectName);
            
    		return row;
    	}
    }
	
	private void newProject()
	{
		try {
			String className = InsertImageForProjectActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(this, Class.forName(className));
			intent.putExtra(Configs.BUNDLE_LIST_PROJECT_NAME, arrayProjectName);
			intent.putExtra(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_ADD_PROJECT, Configs.BUNDLE_FORM_HELP_SCREEN_ADD_PROJECT);
			startActivityForResult(intent, Configs.BUNDLE_FORM_HELP_SCREEN_ADD_PROJECT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(data != null)
		{
			Bundle mBundle = data.getExtras();
			String projectName = "";
			if(mBundle != null)
			{
				if(mBundle.containsKey(Configs.BUNDLE_PROJECT_NAME))
				{
					projectName = mBundle.getString(Configs.BUNDLE_PROJECT_NAME);
					mBundle.remove(Configs.BUNDLE_PROJECT_NAME);
				}
				
				if(requestCode == Configs.BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT)
				{
					openMainScreen(projectName);
				}
				else if(requestCode == Configs.BUNDLE_FORM_HELP_SCREEN_ADD_PROJECT)
				{
					openMainScreen(projectName);
				}
				else if(requestCode == Configs.BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL)
				{
					checkVisibleMessage(true);
					checkVisibleListView(false);
				}
			}
		}
		else
		{
			if(requestCode == Configs.BUNDLE_FORM_HELP_SCREEN_OPEN_TUTORIAL)
			{
				checkVisibleMessage(true);
				checkVisibleListView(false);
			}
		}
	}
	
	private void openMainScreen(String projectName)
	{
		if(projectName != null && projectName.length() > 0)
		{
			try {
				String className = MainActivity.class.getSimpleName();
				String packageName = getApplicationContext().getPackageName();
				if (className.indexOf(packageName) == -1) {
					className = packageName + "." + className;
				}
				Intent intent = new Intent(getApplicationContext(), Class.forName(className));
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra(Configs.BUNDLE_PROJECT_NAME, projectName);
				startActivity(intent);
				// finish two activity
				finish();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) 
		{
			case R.id.btn_new_project:
				newProject();
				break;
			default:
				break;
		}
	}	
}
