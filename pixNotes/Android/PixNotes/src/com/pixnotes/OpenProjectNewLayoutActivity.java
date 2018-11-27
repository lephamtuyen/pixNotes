package com.pixnotes;

import java.io.File;
import java.util.ArrayList;

import com.pixnotes.OpenProjectFromBehindMenuActivity.ItemProjectName;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.objects.ManagerImageObject;

import android.content.Context;
import android.os.Bundle;
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

public class OpenProjectNewLayoutActivity extends BaseActivity implements OnClickListener{
	
	private RelativeLayout rl_back;
	private ItemProjectName mAdapterProjectName;
	private String[] arrayProjectName ;
	private String mProjectName = "";
	private boolean isFirstLoadProject = true;
	private String mCurrentProject = "";
	private ListView lv_open_project ;
	private Button btn_back;
	private int requestCode = 0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_project_new_layout);
		
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		rl_back.setOnClickListener(this);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT);
			}
			else if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_PROJECT))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_HELP_SCREEN_OPEN_PROJECT);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_PROJECT);
			}
		}
		
		
		lv_open_project = (ListView)findViewById(R.id.example_lv_list);
		lv_open_project.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	 mProjectName = mAdapterProjectName.listProjectName.get(position);
                 setResult(requestCode, getIntent().putExtra(Configs.BUNDLE_PROJECT_NAME, mProjectName));
   				 // finish two activity
   				 finish();
            }
        });
		
		loadProject();
		
		lv_open_project.setAdapter(mAdapterProjectName);
		
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			onBackPressed();
			break;
		case R.id.btn_back:
			onBackPressed();
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		finish();
	}
	
}
