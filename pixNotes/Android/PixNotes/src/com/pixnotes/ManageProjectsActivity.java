package com.pixnotes;

import java.io.File;
import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.pixnotes.common.Utilities;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.ExternalStorage;
import com.pixnotes.objects.ManagerImageObject;

public class ManageProjectsActivity extends BaseActivity implements OnClickListener{

	private RelativeLayout rl_back;
	private Button btn_back;
	private ItemProjectName mAdapterProjectName;
	private SwipeListView swipeListView;
	private String[] arrayProjectName ;
	private String mProjectName = "";
	private boolean isFirstLoadProject = true;
	private String mCurrentProject = "";
	private int requestCode = 0;
	private String mDeleteCurrentProjectName = "";
	private String mEditCurrentProjectName = "";
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_project_screen);
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		rl_back.setOnClickListener(this);
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);
		
		if (Build.VERSION.SDK_INT >= 11) {
//          swipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
          swipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

              @Override
              public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                    long id, boolean checked) {
                  
              }

              @Override
              public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                  
                  return false;
              }

              @Override
              public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                  
                  return true;
              }

              @Override
              public void onDestroyActionMode(ActionMode mode) {
                  swipeListView.unselectedChoiceStates();
              }

              @Override
              public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                  return false;
              }
          });
      }
		
		swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            	 Log.e("onDismiss", "onOpened onOpened");
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            	 Log.e("onDismiss", "onClosed onClosed");
            }

            @Override
            public void onListChanged() {
            	 Log.e("onDismiss", "onListChanged onListChanged");
            }

            @Override
            public void onMove(int position, float x) {
            	 Log.e("onDismiss", "onMove onMove");
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.e("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.e("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                mProjectName = mAdapterProjectName.listProjectName.get(position);
            	try {
        			String className = EditProjectName.class.getSimpleName();
        			String packageName = getApplicationContext().getPackageName();
        			if (className.indexOf(packageName) == -1) {
        				className = packageName + "." + className;
        			}
        			Intent intent = new Intent(ManageProjectsActivity.this, Class.forName(className));
        			intent.putExtra(Configs.BUNDLE_PROJECT_NAME, mProjectName);
        			intent.putExtra(Configs.KEY_BUNDLE_FORM_MANAGE_SCREEN_EDIT_PROJECT, Configs.BUNDLE_FORM_MANAGE_SCREEN_EDIT_PROJECT);
        			startActivityForResult(intent, Configs.BUNDLE_FORM_MANAGE_SCREEN_EDIT_PROJECT);
        		} catch (Exception e) {
        			e.printStackTrace();
        		}
            	
            }

            @Override
            public void onClickBackView(int position) {
                Log.e("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
            	String deleteProjectName = "";
                for (int position : reverseSortedPositions) {
                	deleteProjectName = mAdapterProjectName.listProjectName.get(position);
                	mAdapterProjectName.listProjectName.remove(position);
                }
                mAdapterProjectName.notifyDataSetChanged();
                
                if(mCurrentProject.equals(deleteProjectName))
                {
                	mDeleteCurrentProjectName = deleteProjectName;
                }
                
                new WaittingDeleteProject(ManageProjectsActivity.this,deleteProjectName).execute();
                Log.e("onDismiss", "onDismiss onDismiss");
            }

        });
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.BUNDLE_PROJECT_NAME))
			{
				mCurrentProject = mBundle.getString(Configs.BUNDLE_PROJECT_NAME);
				mBundle.remove(Configs.BUNDLE_PROJECT_NAME);
			}
			
			if(mBundle.containsKey(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_MANAGE_PROJECT))
			{
				requestCode = mBundle.getInt(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_MANAGE_PROJECT);
				mBundle.remove(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_MANAGE_PROJECT);
			}
		}
		
		
		loadProject("");
		
		swipeListView.setAdapter(mAdapterProjectName);
		swipeListView.setSwipeMode(swipeListView.SWIPE_MODE_LEFT);
        swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
        swipeListView.setAnimationTime(50);
        swipeListView.setmLimitDistanceBackView(Utilities.convertDpToPixel(this,70));
		
	}
	
	@Override
	public void onBackPressed() {
		
		Bundle mBundle = new Bundle();
		if(mDeleteCurrentProjectName.length() > 0)
		mBundle.putString(Configs.BUNDLE_DELETE_PROJECT_NAME, mDeleteCurrentProjectName);
		if(mEditCurrentProjectName.length() > 0)
		mBundle.putString(Configs.BUNDLE_EDIT_PROJECT_NAME, mEditCurrentProjectName);
		getIntent().putExtras(mBundle);
		setResult(requestCode, getIntent());
		// finish two activity
		finish();
	}
	
	class WaittingDeleteProject extends AsyncTask<String, String, String> {

		private Dialog mProgressDialog;
		private Context mconContext;
		private String deleteProjectName;
		public WaittingDeleteProject(Context mContext,String deleteProjectName) {
			this.mconContext = mContext;
			this.deleteProjectName = deleteProjectName;
		}

		@Override
		protected void onPreExecute() {
			if (mProgressDialog == null) {
				mProgressDialog = new Dialog(mconContext, R.style.progressDialog);
				final ProgressBar proBar = new ProgressBar(mconContext);
				mProgressDialog.addContentView(proBar, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			}

			if (mProgressDialog.isShowing())
				return;
			mProgressDialog.show();
			mProgressDialog.setCanceledOnTouchOutside(false) ;
		}

		@Override
		protected String doInBackground(String... params) {
			deleteProject(deleteProjectName);
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}
	}
	
	private void deleteProject(String projectNameDelete)
	{
		ArrayList<String> listProjectName = new ArrayList<String>();
		// delete internal data manager project and delete data in SDCard
		File dir = getFilesDir();
		try
        {
            File[] fs = dir.listFiles();
            
            for (int i = 0; i < fs.length; i++)
            {
                try
                {
                    String filePath = fs[i].getAbsoluteFile().getAbsolutePath();
                    String [] getTempFileName = filePath.split("/");
                    String [] fileName = getTempFileName[getTempFileName.length-1].split("\\.");
                    String projectName = fileName[0];
                    if(projectNameDelete.equals(projectName))
                    {
                    	// delete data manager 
                    	fs[i].delete();
                    }
                    else
                    {
                    	listProjectName.add(projectName);
                    }
                }
                catch (Exception e)
                {
                   e.printStackTrace();
                }
            }
            // delete data in SDCard
            ExternalStorage.getObj().isDeleteForder(Configs.ROOT_FOLDER_NAME+"/"+projectNameDelete);
            // update data for arrayProjectName
            arrayProjectName = null;
            arrayProjectName = listProjectName.toArray(new String[listProjectName.size()]);
        }
        catch (Exception e)
        {
           e.printStackTrace();
        }
		
	}
	
	private void loadProject(String mCurrentProject)
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
                    if(!mCurrentProject.equals(projectName))
                    {
                    	 listProjectName.add(projectName);
                         arrayProjectName[i] = projectName;
                    }
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
            	row = LayoutInflater.from(mContext).inflate(R.layout.open_project_item, parent, false);
            } 
            String projectName = listProjectName.get(position);
            TextView tv_projectname = (TextView)row.findViewById(R.id.tv_projectname);
            tv_projectname.setText(projectName);
            
            Button btn_delete = (Button)row.findViewById(R.id.btn_delete);
            btn_delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					swipeListView.getTouchListener().dismissView(true, false, position);
					
				}
			});
    		return row;
    	}
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == Configs.BUNDLE_FORM_MANAGE_SCREEN_EDIT_PROJECT)
		{
			if(data != null)
			{
				Bundle mBundle = data.getExtras();
				if(mBundle != null)
				{
					String editProjectName = "";
					if(mBundle.containsKey(Configs.BUNDLE_PROJECT_NAME))
					{
						editProjectName = mBundle.getString(Configs.BUNDLE_PROJECT_NAME);
						mBundle.remove(Configs.BUNDLE_PROJECT_NAME);
						if(editProjectName.trim().length() > 0)
						{
							editProjectName(editProjectName);
						}
					}
				}
			}
		}
	}
	
	private void editProjectName(String editProjectName)
	{
		// edit project name 
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
                    if(mProjectName.equals(projectName))
                    {
                    	if(ExternalStorage.isForderExited(Configs.ROOT_FOLDER_NAME+"/"+mProjectName))
                        {
                        	 File fileFolderName = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+mProjectName);
                        	 File newfileFolderName=new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME,editProjectName);
                        	 fileFolderName.renameTo(newfileFolderName);
                        }
                        else
                        {
                        	ExternalStorage.CreateForder(Configs.ROOT_FOLDER_NAME+"/"+editProjectName);
                        }
                    	
                    	// edit infor select file manage 
                        ManagerImageObject managerImageObj = ManagerImageObject.readFromFile(this, mProjectName);
                        File fileEditFolderName = new File(ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+editProjectName);
                        Log.e("fileEditFolderName", "fileEditFolderName === "+fileEditFolderName.getAbsolutePath());
                        for(int k = 0 ; k < managerImageObj.getListChooseDrawObject().size();k++)
                        {
                        	String [] splitOriginalImagePath = managerImageObj.getListChooseDrawObject().get(k).getOriginalImagePath().split("/");
                        	String [] splitOriginalImageGalleryPath = managerImageObj.getListChooseDrawObject().get(k).getOriginalImageGalleryPath().split("/");
                        	String [] splitEditImagePath = managerImageObj.getListChooseDrawObject().get(k).getEditImagePath().split("/");
                        	
                        	if(splitOriginalImagePath != null && splitOriginalImagePath.length > 1)
                        	{
                        		managerImageObj.getListChooseDrawObject().get(k).setOriginalImagePath(fileEditFolderName.getAbsolutePath()+"/"+splitOriginalImagePath[splitOriginalImagePath.length-1]);
                        		Log.e("Original", "Original  iii === "+k + "   "+splitOriginalImagePath[splitOriginalImagePath.length-1] + "  "+managerImageObj.getListChooseDrawObject().get(k).getOriginalImagePath());
                        	}
                        	
                        	if(splitEditImagePath != null && splitEditImagePath.length > 1)
                        	{
                        		managerImageObj.getListChooseDrawObject().get(k).setEditImagePath(fileEditFolderName.getAbsolutePath()+"/"+splitEditImagePath[splitEditImagePath.length-1]);
                        		Log.e("Edit", "Edit  iii === "+k + "   "+splitEditImagePath[splitEditImagePath.length-1] + "  "+managerImageObj.getListChooseDrawObject().get(k).getEditImagePath());
                        	}
                        }
                        
                        // delete old file manage
                        if(this.deleteFile(mProjectName+Configs.TYPE_FORMAT_PROJECT_NAME))
            			{
            				Log.e("context.deleteFile", "context.deleteFile context.deleteFile ==== "+mProjectName);
            			}
                        
                        managerImageObj.writeToFile(this, editProjectName);
                        managerImageObj = null;
                      
                        if(mProjectName.equals(mCurrentProject))
                        {
                        	mEditCurrentProjectName = editProjectName;
                        	mCurrentProject = editProjectName;
                        }
                        
                	    listProjectName.add(editProjectName);
                        arrayProjectName[i] = editProjectName;
                    }
                    else
                    {
                    	 listProjectName.add(projectName);
                         arrayProjectName[i] = projectName;
                    }
                }
                catch (Exception e)
                {
                   e.printStackTrace();
                }
                
                // referresh list view
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
        }
        catch (Exception e)
        {
           e.printStackTrace();
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
}
