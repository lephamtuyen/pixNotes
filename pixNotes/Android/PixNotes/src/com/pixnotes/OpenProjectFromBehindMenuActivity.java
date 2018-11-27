package com.pixnotes;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.pixnotes.common.Utilities;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.ExternalStorage;
import com.pixnotes.objects.ManagerImageObject;


public class OpenProjectFromBehindMenuActivity extends Activity implements OnClickListener{

	private RelativeLayout rl_back;
	private ItemProjectName mAdapterProjectName;
//	private SwipeListView swipeListView;
	private String[] arrayProjectName ;
	private String mProjectName = "";
	private boolean isFirstLoadProject = true;
	private String mCurrentProject = "";
	private ListView lv_open_project ;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.openprojectfrombehindmenu);
		rl_back = (RelativeLayout) findViewById(R.id.rl_back);
		rl_back.setOnClickListener(this);
		
//		swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);
//		
//		if (Build.VERSION.SDK_INT >= 11) {
////          swipeListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//      }
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
//          swipeListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
//
//              @Override
//              public void onItemCheckedStateChanged(ActionMode mode, int position,
//                                                    long id, boolean checked) {
//                  
//              }
//
//              @Override
//              public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                  
//                  return false;
//              }
//
//              @Override
//              public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                  
//                  return true;
//              }
//
//              @Override
//              public void onDestroyActionMode(ActionMode mode) {
//                  swipeListView.unselectedChoiceStates();
//              }
//
//              @Override
//              public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                  return false;
//              }
//          });
//      }
//		
//		swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
//            @Override
//            public void onOpened(int position, boolean toRight) {
//            	 Log.e("onDismiss", "onOpened onOpened");
//            }
//
//            @Override
//            public void onClosed(int position, boolean fromRight) {
//            	 Log.e("onDismiss", "onClosed onClosed");
//            }
//
//            @Override
//            public void onListChanged() {
//            	 Log.e("onDismiss", "onListChanged onListChanged");
//            }
//
//            @Override
//            public void onMove(int position, float x) {
//            	 Log.e("onDismiss", "onMove onMove");
//            }
//
//            @Override
//            public void onStartOpen(int position, int action, boolean right) {
//                Log.e("swipe", String.format("onStartOpen %d - action %d", position, action));
//            }
//
//            @Override
//            public void onStartClose(int position, boolean right) {
//                Log.e("swipe", String.format("onStartClose %d", position));
//            }
//
//            @Override
//            public void onClickFrontView(int position) {
//                Log.e("swipe", String.format("onClickFrontView %d", position));
//                mProjectName = mAdapterProjectName.listProjectName.get(position);
//                setResult(MainActivity.getInstance().RESULT_OPEN_PROJECT, getIntent().putExtra(Configs.BUNDLE_PROJECT_NAME, mProjectName));
//    			// finish two activity
//    			finish();
//            }
//
//            @Override
//            public void onClickBackView(int position) {
//                Log.e("swipe", String.format("onClickBackView %d", position));
//            }
//
//            @Override
//            public void onDismiss(int[] reverseSortedPositions) {
//            	String deleteProjectName = "";
//                for (int position : reverseSortedPositions) {
//                	deleteProjectName = mAdapterProjectName.listProjectName.get(position);
//                	mAdapterProjectName.listProjectName.remove(position);
//                }
//                mAdapterProjectName.notifyDataSetChanged();
//                new WaittingDeleteProject(OpenProjectFromBehindMenuActivity.this,deleteProjectName).execute();
//                Log.e("onDismiss", "onDismiss onDismiss");
//            }
//
//        });
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.BUNDLE_PROJECT_NAME))
			{
				mCurrentProject = mBundle.getString(Configs.BUNDLE_PROJECT_NAME);
				mBundle.remove(Configs.BUNDLE_PROJECT_NAME);
			}
		}
		
		lv_open_project = (ListView)findViewById(R.id.example_lv_list);
		lv_open_project.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
            	 mProjectName = mAdapterProjectName.listProjectName.get(position);
//                 setResult(MainActivity.getInstance().RESULT_OPEN_PROJECT, getIntent().putExtra(Configs.BUNDLE_PROJECT_NAME, mProjectName));
//   				 // finish two activity
//   				 finish();
            }
        });
		
		loadProject(mCurrentProject);
		
		lv_open_project.setAdapter(mAdapterProjectName);
		
//		swipeListView.setAdapter(mAdapterProjectName);
//		swipeListView.setSwipeMode(swipeListView.SWIPE_MODE_LEFT);
//      swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
//      swipeListView.setAnimationTime(50);
//      swipeListView.setmLimitDistanceBackView(Utilities.convertDpToPixel(this,70));
		
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
            
//            Button btn_delete = (Button)row.findViewById(R.id.btn_delete);
//            btn_delete.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					
//					swipeListView.getTouchListener().dismissView(true, false, position);
//					
//				}
//			});
    		return row;
    	}
    }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_back:
			onBackPressed();
			break;
		
		}
		
	}
	
}
