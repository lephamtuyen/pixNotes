package com.pixnotes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnCloseListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnClosedListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu.OnOpenedListener;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingActivity;
import com.mobeta.android.dslv.DragSortListView;
import com.pixnotes.adapter.Action;
import com.pixnotes.common.MyEditText;
import com.pixnotes.common.Utilities;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.ExternalStorage;
import com.pixnotes.datastorage.PreferenceConnector;
import com.pixnotes.imageloader.ImageLoader;
import com.pixnotes.interfaceconstructe.ListenerEventUpdate;
import com.pixnotes.objects.ImageDrawObject;
import com.pixnotes.objects.ManagerImageObject;
import com.pixnotes.utils.LogUtils;
import com.pixnotes.views.CustomerShapeView;
import com.pixnotes.views.DrawingView;
import com.pixnotes.views.WorkBoxView;

public class MainActivity extends SlidingActivity implements OnClickListener,ListenerEventUpdate {

	private ArrayList<String> mHistory = new ArrayList<String>();
	private ListView lv_behind_menu;
	private BehindMenuAdapter mbehindMenuAdapter;
	private Button btn_menu;
	private Button btn_redo;
	private Button btn_undo;
	private Button btn_choose_image;
//	private Button btn_delete;
//	private Button btn_setting_paint;
	private DrawingView mDrawingView;
	public ManagerImageObject mManagerObject;
	public static MainActivity instance;
//	private boolean mFirstLoadImage = true;
	private TextView tv_description;
	private ImageView img_description;
	private ArrayList<String> listPathFile = new ArrayList<String>();
	public String mCurrentProject = "";
	private int indexFirstImageCreateProject = 0;
	private ImageView img_edit; 
	private boolean isEdit = false;
	private RelativeLayout rl_delete;
	private final int OPEN_LEFT_MENU = 0;
	private final int OPEN_RIGHT_MENU = 1;
	
	// config for sort listview and fling to delete
	private DragSortListView mDragSortListView;
	private ImageAdapter mAdapter;
	private ImageView img_capture;
	private ImageView img_insert_photo;
	private int RESULT_LOAD_IMAGE = 1;
//	public static final int RESULT_NEW_PROJECT = 2;
//	public static final int RESULT_OPEN_PROJECT = 3;
	private Uri fileUri; // file url to store image/video
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final String IMAGE_DIRECTORY_NAME = "CaptureImageFormCamera";
	private ImageView img_sendmail;
	private ImageView img_delete;
	private RelativeLayout rl_drawing_content;
	
	private int mWidthView = 0;
	private int mHeightView = 0;
	
	private MediaRecorder mRecorder = null;
	private MediaPlayer   mPlayer = null;
	private ImageView img_record_audio;
	
	private Bitmap mCurrentLoadingBitmap;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		// Top View
		setContentView(R.layout.top_screen);
		// Left Behind View
		setBehindContentView(R.layout.behind_left_menu);
		SlidingMenu mSlidingMenu = getSlidingMenu(); //get slidingmenu
		mSlidingMenu.setMode(SlidingMenu.LEFT_RIGHT); //set chế độ sliding menu ở hai bên, mặc định là LEFT, chỉ dùng khi chúng ta có hai menu trái và phải
		mSlidingMenu.setBehindWidth((int) (Configs.SLIDING_MENU_WIDTH * getResources().getDisplayMetrics().widthPixels));
		// Right Behind View
		mSlidingMenu.setSecondaryMenu(R.layout.behind_right_menu);
		mSlidingMenu.setFadeDegree(0.0f);
		mSlidingMenu.setBehindScrollScale(0.0f);
		mSlidingMenu.setSlidingEnabled(false);
		mSlidingMenu.setTouchModeAbove(mSlidingMenu.TOUCHMODE_NONE);
		
		mSlidingMenu.setSecondaryOnOpenListner(new OnOpenListener() {
			
			@Override
			public void onOpen() {
				
				updateCurrentImageEdit();
			}
		});
		
		mSlidingMenu.setOnOpenListener(new OnOpenListener() {
			
			@Override
			public void onOpen() {
				
			}
		});
		
		mSlidingMenu.setOnOpenedListener(new OnOpenedListener() {
			
			@Override
			public void onOpened() {
				
				
			}
		});
		
		mSlidingMenu.setOnCloseListener(new OnCloseListener() {
			
			@Override
			public void onClose() {
				
				
			}
		});
		
		mSlidingMenu.setOnClosedListener(new OnClosedListener() {
			
			@Override
			public void onClosed() {
				
				
			}
		});
		
		initComponent();
		setComponentListener();
		
		// Create Root Folder 
		if(!ExternalStorage.getObj().isForderExited(Configs.ROOT_FOLDER_NAME))
		{
			ExternalStorage.getObj().CreateForder(Configs.ROOT_FOLDER_NAME);
		}
//		// reset gridview 
//		PreferenceConnector.writeInteger(this, Configs.FLAG_TYPE_GIRD, 0);
//		PreferenceConnector.writeInteger(this, Configs.FLAG_TYPE_GIRD_COLOR, 0);
//		PreferenceConnector.writeBoolean(this, Configs.FLAG_APPLY_GIRD, false);
		
		Bundle mBundle = getIntent().getExtras();
		if(mBundle != null)
		{
			if(mBundle.containsKey(Configs.BUNDLE_PROJECT_NAME))
			{
				mCurrentProject = mBundle.getString(Configs.BUNDLE_PROJECT_NAME);
				mBundle.remove(Configs.BUNDLE_PROJECT_NAME);
			}
		}
		
		// Get Object Mananger Data
		mManagerObject = ManagerImageObject.readFromFile(this,mCurrentProject);
		if(mManagerObject == null)
		{
			mManagerObject = new ManagerImageObject();
		}
		else
		{
			if(mManagerObject.getmFirstCreateProject() == Configs.FIRST_TIME_CREATE_PROJECT)
			{
				// save current index draw
				PreferenceConnector.writeInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, indexFirstImageCreateProject);
				mManagerObject.getListChooseDrawObject().get(indexFirstImageCreateProject).setIsSelect(Configs.FLAG_IS_SELECT);
				mManagerObject.setmFirstCreateProject(indexFirstImageCreateProject);
				mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
				mManagerObject = null;
				System.gc();
				mManagerObject  = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
				// the first set edit off
				isEdit = false;
			}
			else
			{
				isEdit = true;
			}
		}
		
		// init Drag Sort ListView
		mDragSortListView = (DragSortListView)findViewById(R.id.list);
		mDragSortListView.setDropListener(onDrop);
		mDragSortListView.setRemoveListener(onRemove);
		mDragSortListView.setDragScrollProfile(ssProfile);
		mDragSortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
				// close menu right
//				OpenOrCloseMenu(OPEN_RIGHT_MENU);
				updateDataImageCurrentDraw(position);
				boolean isOpenMenu = true;
				if(mDrawingView != null)
				{
					mDrawingView.setShowingMenu(isOpenMenu);
				}
			}
		});
		
		mAdapter = new ImageAdapter(this,mManagerObject);
	    mDragSortListView.setAdapter(mAdapter);
	    mAdapter.notifyDataSetChanged();
	    
	    initWorkBox();
	   
	}
	
	private void initWorkBox()
	{
		// detect view box 
	    rl_drawing_content.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
	        @Override 
	        public void onGlobalLayout() { 
	        	rl_drawing_content.getViewTreeObserver().removeGlobalOnLayoutListener(this); 
	            mWidthView  = rl_drawing_content.getMeasuredWidth();
	            mHeightView = rl_drawing_content.getMeasuredHeight(); 
	            
	         // check all action    // DuongGa
	    	    check_All_Action_Mail_Edit_Voice();
	            
	            for(int i = 0 ; i < mManagerObject.getListChooseDrawObject().size();i++)
	    		{
	    			if(mManagerObject.getListChooseDrawObject().get(i).getIsSelect() == Configs.FLAG_IS_SELECT)
	    			{
	    				String mImageEditPath =  mManagerObject.getListChooseDrawObject().get(i).getEditImagePath();
	    	            String mImageDisplayPath = mManagerObject.getListChooseDrawObject().get(i).getOriginalImagePath();
	    	            boolean flagLoadListRootShape = false;
	    	            if((mImageEditPath != null && mImageEditPath.length() > 0) || mManagerObject.getListChooseDrawObject().get(i).getListRootShape().size() > 0)
	    	            {
	    	            	flagLoadListRootShape = true;
	    	            }
	    	    		File fileImage = new File(mImageDisplayPath);
	    	    		if(fileImage.exists())
	    	    		{
	    	    			mCurrentLoadingBitmap = Utilities.scaleToActualAspectRatio(BitmapFactory.decodeFile(mImageDisplayPath), mWidthView, mHeightView);
	    	            	setEdit(isEdit);
	    	            	mDrawingView.setEdit(isEdit);
	    	            	mDrawingView.setListener(MainActivity.this);
	    		    	    // init width view and height view
	    		    	    mDrawingView.setwMainView(mWidthView);
	    		    	    mDrawingView.sethMainView(mHeightView);

	    		    	    mDrawingView.setMainBitmap(mCurrentLoadingBitmap);
	    		    	    
	    					// load gridview
	    					loadGridView();
	    					// check audio
	    					CheckAudioExist();
	    					if(flagLoadListRootShape)
	        				{
	    						ArrayList<CustomerShapeView> listShape = mManagerObject.getListChooseDrawObject().get(i).getListRootShape();
	        					mDrawingView.loadListRootShape(listShape);
	        				}
	        				else
	        				{
	        					mDrawingView.clearAllRootShape();
	        				}
	    	    		}
	    	            return;
	    			}
	    		}
	            
	        } 
	    });
	}
	
	private void updateCurrentImageEdit()
	{
		if(mDrawingView != null && mDrawingView.getMainBitmap() == null)
		{
			return;
		}
		
	    int mWidthImage = (int)getResources().getDimension(R.dimen.image_width_size_100dp);
		int mHeightImage = (int)getResources().getDimension(R.dimen.image_height_size_120dp);
	    
		int index = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
		if(index <= mManagerObject.getListChooseDrawObject().size() -1 )
		{
//			Bitmap bmCurrentDraw = Utilities.getBitmapFromView(MainActivity.getInstance().getmDrawingView());
			Bitmap bmCurrentDraw = MainActivity.getInstance().getmDrawingView().scaleBitmapAfterZoomInOut();
			
			String folderPath = mManagerObject.getListChooseDrawObject().get(index).getOriginalImageGalleryPath();
			String fileEditPath = Utilities.getImageDrawFileNamePath(mCurrentProject, folderPath + Configs.FLAG_EDIT_FILE_NAME);
			File fileEdit = new File(fileEditPath);
			if(fileEdit.exists())
			{
				fileEdit.delete();
			}
			
			bmCurrentDraw = Utilities.scaleToActualAspectRatio(bmCurrentDraw, mWidthImage, mHeightImage);
			Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
			
			Log.e("fileEditPath", "fileEditPath ===== "+fileEdit.getAbsolutePath());
			mManagerObject.getListChooseDrawObject().get(index).setEditImagePath(fileEdit.getAbsolutePath());
			// remove all shape in database
			mManagerObject.getListChooseDrawObject().get(index).getListRootShape().removeAll(mManagerObject.getListChooseDrawObject().get(index).getListRootShape());
			// save shape in current view
			mManagerObject.getListChooseDrawObject().get(index).getListRootShape().addAll(getmDrawingView().getListRootShape());
			
			mManagerObject.writeToFile(this,mCurrentProject);
			mManagerObject = null;
			
			bmCurrentDraw.recycle();
			bmCurrentDraw = null;
			
			mManagerObject  = ManagerImageObject.readFromFile(this,mCurrentProject);
			Log.e("readfromfile ", "readfromfile ===== "+mManagerObject.getListChooseDrawObject().get(index).getEditImagePath());
			if(mAdapter != null)
			{
				mAdapter.mManagerObject = mManagerObject;
				mAdapter.notifyDataSetChanged();
			}
		}
	}
	
	private void updateDataImageCurrentDraw(int position)
	{
		int sizeHistoryImage = mManagerObject.getListChooseDrawObject().size();
		if(sizeHistoryImage == 1)
		{	
			mManagerObject.getListChooseDrawObject().get(0).setIsSelect(Configs.FLAG_IS_SELECT);
		}
		for(int i = 0; i < sizeHistoryImage ;i++)
		{
			if(mManagerObject.getListChooseDrawObject().get(i).getIsSelect() == Configs.FLAG_IS_SELECT)
			{
				if(i == position && sizeHistoryImage > 1)
				{
					return;
				}
				
				if(i != position)  // save current image edit
				{
					mManagerObject.getListChooseDrawObject().get(i).setIsSelect(Configs.FLAG_IS_NOT_SELECT);
//					if(MainActivity.getInstance().getmDrawingView().getMainBitmap() != null)
//					{
//						getmDrawingView().resetDataWhenUserDrawOtherImage();
//						break;
////						Bitmap bmCurrentDraw = Utilities.getBitmapFromView(MainActivity.getInstance().getmDrawingView());
////						String folderPath = mManagerObject.getListChooseDrawObject().get(i).getOriginalImageGalleryPath();
////						String fileEditPath = Utilities.getImageDrawFileNamePath(mCurrentProject, folderPath + Configs.FLAG_EDIT_FILE_NAME);
//////						Log.e("fileEditPath", "fileEditPath ========= "+fileEditPath);
////						File fileEdit = new File(fileEditPath);
////						if(fileEdit.exists())
////						{
////							fileEdit.delete();
////						}
////						Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
////						mManagerObject.getListChooseDrawObject().get(i).setEditImagePath(fileEdit.getAbsolutePath());
////						// remove all shape in database
////						mManagerObject.getListChooseDrawObject().get(i).getListRootShape().removeAll(mManagerObject.getListChooseDrawObject().get(i).getListRootShape());
////						// save shape in current view
////						mManagerObject.getListChooseDrawObject().get(i).getListRootShape().addAll(getmDrawingView().getListRootShape());
////						getmDrawingView().resetDataWhenUserDrawOtherImage();
////						bmCurrentDraw.recycle();
////						bmCurrentDraw = null;
//					}
				}
			}
		}
		
		// save current index draw
		filterShapeInCurrentBitmap();
		PreferenceConnector.writeInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, position);
		// DuongGa
		if(position <= mManagerObject.getListChooseDrawObject().size() - 1)
		{
			mManagerObject.getListChooseDrawObject().get(position).setIsSelect(Configs.FLAG_IS_SELECT);
		}
		
		mManagerObject.writeToFile(this,mCurrentProject);
		mManagerObject = null;
		System.gc();
		mManagerObject  = ManagerImageObject.readFromFile(this,mCurrentProject);
		
		// load image select for draw
		loadImageSelectForDraw();
		// refresh adapter listview
		if(mAdapter != null)
		{
			mAdapter.mManagerObject = mManagerObject;
			mAdapter.notifyDataSetChanged();
		}
		 // check all action   // DuongGa
	    check_All_Action_Mail_Edit_Voice();
		// check audio
		CheckAudioExist();
	}
	
	
	public class ImageAdapter extends BaseAdapter {
		
		public Context mContext;
		public ImageLoader mImageLoader;
		public ManagerImageObject mManagerObject;
		
		public ImageAdapter(Context context,ManagerImageObject mManagerObject) {
			mContext = context;
			this.mManagerObject = mManagerObject;
			mImageLoader = new ImageLoader(context);
			
		}
    	
		public void updateListImage(ManagerImageObject mManagerObject)
		{
			
			this.mManagerObject = mManagerObject;
			notifyDataSetChanged();
		}
    	
		@Override
    	public int getCount() {
    		
    		return mManagerObject.getListChooseDrawObject().size();
    	}

    	@Override
    	public Object getItem(int position) {
    		
    		return mManagerObject.getListChooseDrawObject().get(position);
    	}

    	@Override
    	public long getItemId(int position) {
    		
    		return position;
    	}

    	@Override
    	public View getView(int position,final View convertView, ViewGroup parent) {
    		if(mManagerObject.getListChooseDrawObject().size() == 0)
    			return null;
    		View row = convertView;
            if (convertView == null) {
            	row = LayoutInflater.from(mContext).inflate(R.layout.imagedrawing, parent, false);
            } 
            ImageView imgViewEye = (ImageView) row.findViewById(R.id.imgcurrentdraw);
            ImageView imgView = (ImageView) row.findViewById(R.id.drag_handle);
            String mImageEditPath =  mManagerObject.getListChooseDrawObject().get(position).getEditImagePath();
            String mImageDisplayPath = "";
            if(mImageEditPath != null && mImageEditPath.length() > 0)
            {
            	mImageDisplayPath = mImageEditPath;
            	Log.e("mImageEditPath", "mImageEditPath  ==== "+mImageEditPath);
            }
            else
            {
            	mImageDisplayPath = mManagerObject.getListChooseDrawObject().get(position).getOriginalImagePath();
            	Log.e("original", "original  ==== "+mImageDisplayPath);
            }
            
            int isSelect = mManagerObject.getListChooseDrawObject().get(position).getIsSelect();
            if(isSelect == 1)
            {
            	imgViewEye.setVisibility(View.VISIBLE);
            }
            else
            {
            	imgViewEye.setVisibility(View.GONE);
            }
            mImageLoader.DisplayImageViewForLoadEdit(true, position, mImageDisplayPath, imgView, 0, 0);
    		return row;
    	}
    }
	
	
//	private ArrayAdapter<String> adapter;

    private String[] array;
    private ArrayList<String> list;

    private DragSortListView.DropListener onDrop =
        new DragSortListView.DropListener() {
            @Override
            public void drop(int from, int to) {
              
//            	String item=adapter.getItem(from);
//              adapter.notifyDataSetChanged();
//              adapter.remove(item);
//              adapter.insert(item, to);
            	
            	ImageDrawObject imgDrawObject = (ImageDrawObject)mAdapter.getItem(from);
            	mAdapter.notifyDataSetChanged();
            	mAdapter.mManagerObject.getListChooseDrawObject().remove(from);
            	mAdapter.mManagerObject.getListChooseDrawObject().add(to, imgDrawObject);
            	
            }
        };

    private DragSortListView.RemoveListener onRemove = 
        new DragSortListView.RemoveListener() {
            @Override
            public void remove(int position) {
            	
            	 if(mAdapter.mManagerObject.getListChooseDrawObject().get(position).getIsSelect() == Configs.FLAG_IS_SELECT)
            	 {
            		 if(mDrawingView != null)
            		 {
            			 mDrawingView.resetAllData();
//            			 mDrawingView.postInvalidate();
            			 
            			 mAdapter.mManagerObject.getListChooseDrawObject().remove(position);
            			 int sizeList = mAdapter.mManagerObject.getListChooseDrawObject().size() - 1;
            			// detect size list view
                		 if(position < sizeList )
                		 {
                			 LogUtils.LogError("aaa == ", "aaa === "+position + "  "+sizeList);
                		 }
                		 else
                		 {
                			 if(position > sizeList && sizeList >= 0)
                			 {
                				 LogUtils.LogError("bbb == ", "bbb === "+position + "  "+sizeList);
                				 position = sizeList;
                			 }
                			 else if(position == sizeList && sizeList > 0)
                			 {
                				 position = sizeList - 1;
                				 LogUtils.LogError("cccc == ", "cccccc === "+position + "  "+sizeList);
                			 }
                			
                		 }
                		 
                		updateDataImageCurrentDraw(position);
          				boolean isOpenMenu = true;
          				if(mDrawingView != null)
          				{
          					mDrawingView.setShowingMenu(isOpenMenu);
          				}
            		 }
            	 }
            	 else
            	 {
            		 mAdapter.mManagerObject.getListChooseDrawObject().remove(position);
                	 mManagerObject.writeToFile(MainActivity.this,MainActivity.getInstance().mCurrentProject);
    				 mManagerObject = null;
    				 mManagerObject  = ManagerImageObject.readFromFile(MainActivity.this,MainActivity.getInstance().mCurrentProject);
    				 mAdapter.mManagerObject = mManagerObject;
                	 mAdapter.notifyDataSetChanged();
            	 }
            	 
            }
    };
    
    
    
    private DragSortListView.DragScrollProfile ssProfile =
        new DragSortListView.DragScrollProfile() {
            @Override
            public float getSpeed(float w, long t) {
                if (w > 0.8f) {
                    // Traverse all views in a millisecond
                    return ((float) mAdapter.getCount()) / 0.9f;
                } else {
                    return 10.0f * w;
                }
            }
        };
	private int mOldWidthView;
	private int mOldHeightView;
	
	
	public static MainActivity getInstance() {
		return instance;
	}


	public static void setInstance(MainActivity instance) {
		MainActivity.instance = instance;
	}
	
	public void initComponent()
	{
		btn_menu = (Button)findViewById(R.id.btn_menu);
		btn_redo = (Button)findViewById(R.id.btn_redo);
		btn_undo = (Button)findViewById(R.id.btn_undo);
		btn_choose_image = (Button)findViewById(R.id.btn_choose_image);
		img_edit = (ImageView)findViewById(R.id.img_edit);
		lv_behind_menu = (ListView)findViewById(R.id.lv_behind_menu);
		mbehindMenuAdapter = new BehindMenuAdapter(this);
		lv_behind_menu.setAdapter(mbehindMenuAdapter);
		mDrawingView = (DrawingView) findViewById(R.id.drawing_view);
//		mDrawingView.setEdit(true);
		tv_description = (TextView)findViewById(R.id.tv_description);
		img_description = (ImageView)findViewById(R.id.img_description);
		rl_delete = (RelativeLayout)findViewById(R.id.rl_delete);
		img_capture = (ImageView)findViewById(R.id.img_capture);
		img_insert_photo = (ImageView)findViewById(R.id.img_insert_photo);
		img_sendmail =  (ImageView)findViewById(R.id.img_sendmail);
		rl_drawing_content = (RelativeLayout)findViewById(R.id.rl_drawing_content);
		img_delete = (ImageView)findViewById(R.id.img_delete);
		img_record_audio = (ImageView)findViewById(R.id.img_record_audio);
	}
	
	
	
	
	private void openNewProject()
	{
		checkCloseMenu();
		try {
			String className = InsertImageForProjectActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(this, Class.forName(className));
			intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_ADD_PROJECT, Configs.BUNDLE_FORM_MAIN_SCREEN_ADD_PROJECT);
			startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_ADD_PROJECT);
			overridePendingTransition(R.anim.right_slide_in, R.anim.hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openProjects()
	{
		checkCloseMenu();
		try {
			String className = OpenProjectNewLayoutActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(this, Class.forName(className));
			intent.putExtra(Configs.BUNDLE_PROJECT_NAME, mCurrentProject);
			intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_PROJECT, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_PROJECT);
			startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_PROJECT);
			overridePendingTransition(R.anim.right_slide_in, R.anim.hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openSetting()
	{
		checkCloseMenu();
		try {
			String className = SettingActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(getApplicationContext(), Class.forName(className));
			intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING);
			startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING);
			overridePendingTransition(R.anim.right_slide_in, R.anim.hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void checkCloseMenu()
	{
		if (getSlidingMenu().isMenuShowing()) {
			getSlidingMenu().showContent();
		}
	}
	public void setComponentListener()
	{
//		mDrawingView.setListener(this);
		btn_menu.setOnClickListener(this);
		btn_redo.setOnClickListener(this);
		btn_undo.setOnClickListener(this);
		btn_choose_image.setOnClickListener(this);
		img_edit.setOnClickListener(this);
//		btn_delete.setOnClickListener(this);
//		btn_setting_paint.setOnClickListener(this);
		img_description.setOnClickListener(this);
		img_capture.setOnClickListener(this);
		img_insert_photo.setOnClickListener(this);
		img_sendmail.setOnClickListener(this);
		img_record_audio.setOnClickListener(this);
		lv_behind_menu.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long arg3) {
				
				switch (position) {
				case Configs.FLAG_NEW_PROJECT:
					openNewProject();
					break;
				case Configs.FLAG_OPEN_PROJECT:
					openProjects();
					break;
				case Configs.FLAG_MANAGE_PROJECTS:
					openManageProjects();
					break;
				case Configs.FLAG_SETTING:
					openSetting();
					break;
				}
			}
		});
	}
	
	private void openManageProjects()
	{
		checkCloseMenu();
		try {
			String className = ManageProjectsActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(this, Class.forName(className));
			intent.putExtra(Configs.BUNDLE_PROJECT_NAME, mCurrentProject);
			intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_MANAGE_PROJECT, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_MANAGE_PROJECT);
			startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_MANAGE_PROJECT);
			overridePendingTransition(R.anim.right_slide_in, R.anim.hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openSettingPaint(int typeShape)
	{
		try {
			if(typeShape == Configs.ADD_TEXT_TYPE)
			{
				if(mDrawingView != null)
				{
					openEditText(mDrawingView.getTextObjValue());
				}
			}
			else if(typeShape == Configs.BLUR_TYPE)
			{
				String className = BlurOpacityActivity.class.getSimpleName();
				String packageName = getApplicationContext().getPackageName();
				if (className.indexOf(packageName) == -1) {
					className = packageName + "." + className;
				}
				Intent intent = new Intent(this, Class.forName(className));
				intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_BLUR_OPACITY, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_BLUR_OPACITY);
				startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_BLUR_OPACITY);
			}
			else
			{
				String className = SettingPaintActivity.class.getSimpleName();
				String packageName = getApplicationContext().getPackageName();
				if (className.indexOf(packageName) == -1) {
					className = packageName + "." + className;
				}
				Intent intent = new Intent(this, Class.forName(className));
				intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING_PAINT, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING_PAINT);
				startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING_PAINT);
			}
//			overridePendingTransition(R.anim.right_slide_in, R.anim.hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
		
	private void openRecordActivity()
	{
		try {
			String className = RecordActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(this, Class.forName(className));
			intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_RECORD_AUDIO, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_RECORD_AUDIO);
			startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_RECORD_AUDIO);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public void actionDelete()
	{
		if(mDrawingView != null)
		{
			mDrawingView.deleteCurrentShape();
		}
	}
	
	public void actionChooseImageEdit()
	{
		Bundle mBundle = new Bundle();
    	mBundle.putString(Configs.OPEN_SCREEN, CaptureImageActivity.class.getSimpleName());
    	openScreen(mBundle);
	}
	
	public void actionUndo()
	{
		mDrawingView.actionUndo();
	}
	
	public void actionRedo()
	{
		mDrawingView.actionRedo();
	}
	
//	private void loadDescription()
//	{
//		// check show description 
//		if(mDrawingView.getMainBitmap() != null)
//		{
//			setInvisibleDescription(false);
//		}
//		else
//		{
//			setInvisibleDescription(true);
//		}
//		
//		int indexCurrentDraw = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW	, 0);
//		if(mManagerObject != null)
//		{
//			if(indexCurrentDraw <= mManagerObject.getListChooseDrawObject().size() -1 )
//			{
//				String description = mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getmDescription();
//				tv_description.setText(description);
//			}
//		}
//	}
	
//	private void CheckSendMailStatus()
//	{
//		if(mManagerObject != null && mManagerObject.getListChooseDrawObject().size() > 0)
//		{
//			img_sendmail.setBackgroundResource(R.drawable.share_blue);
//			isSendMail = true;
//		}
//		else
//		{
//			img_sendmail.setBackgroundResource(R.drawable.share);
//			isSendMail = false;
//		}
//	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		
		
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);		
		
		rl_drawing_content.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
	        @Override 
	        public void onGlobalLayout() {
	        	rl_drawing_content.getViewTreeObserver().removeGlobalOnLayoutListener(this); 
	            mWidthView  = rl_drawing_content.getMeasuredWidth();
	            mHeightView = rl_drawing_content.getMeasuredHeight();
	            LogUtils.LogInfo("*****   "+mDrawingView.getwMainView() + "  "+mDrawingView.gethMainView());
	            scaleViewRotation();
	            
	        } 
        });
		
	}
	
	public void scaleViewRotation()
	{
		for(int i = 0 ; i < mManagerObject.getListChooseDrawObject().size();i++)
		{
			if(mManagerObject.getListChooseDrawObject().get(i).getIsSelect() == Configs.FLAG_IS_SELECT)
			{
				String mImageEditPath =  mManagerObject.getListChooseDrawObject().get(i).getEditImagePath();
	            String mImageDisplayPath = mManagerObject.getListChooseDrawObject().get(i).getOriginalImagePath();
	            boolean flagLoadListRootShape = false;
	            if((mImageEditPath != null && mImageEditPath.length() > 0) || mManagerObject.getListChooseDrawObject().get(i).getListRootShape().size() > 0)
	            {
	            	flagLoadListRootShape = true;
	            }
	    		File fileImage = new File(mImageDisplayPath);
	    		if(fileImage.exists())
	    		{
	    			Bitmap mainBitmap = Utilities.scaleToActualAspectRatio(BitmapFactory.decodeFile(mImageDisplayPath), mWidthView, mHeightView);
		    	    // init width view and height view
		    	    //mDrawingView.setMainBitmap(mainBitmap);
	    			float scale = (float)mainBitmap.getWidth()/mOldWidthView;
	    			mDrawingView.scaleFactor = scale;
	    			
	    			LogUtils.LogError("mDrawingView.scaleFactor " + mDrawingView.scaleFactor);
	    			
	    			mOldWidthView = mainBitmap.getWidth();
	    			mOldHeightView = mainBitmap.getHeight();
	    			mDrawingView.invalidate();
	    			
	    		}
	            break;
			}
		}
		
	}
	
	public void loadImageSelectForDraw()
	{
		 // check all action  // DuongGa
	    check_All_Action_Mail_Edit_Voice();
	    if(mManagerObject.getListChooseDrawObject().size() == 0)
	    {
	    	if(mDrawingView != null)
	    	{
	    		mDrawingView.postInvalidate();
	    	}
	    }
	    
		 for(int i = 0 ; i < mManagerObject.getListChooseDrawObject().size();i++)
 		 {
 			if(mManagerObject.getListChooseDrawObject().get(i).getIsSelect() == Configs.FLAG_IS_SELECT)
 			{
 				String mImageEditPath =  mManagerObject.getListChooseDrawObject().get(i).getEditImagePath();
 	            String mImageDisplayPath = mManagerObject.getListChooseDrawObject().get(i).getOriginalImagePath();
 	            boolean flagLoadListRootShape = false;
 	            if((mImageEditPath != null && mImageEditPath.length() > 0) || mManagerObject.getListChooseDrawObject().get(i).getListRootShape().size() > 0)
 	            {
 	            	flagLoadListRootShape = true;
 	            }
 	    		File fileImage = new File(mImageDisplayPath);
 	    		if(fileImage.exists())
 	    		{

 	    			mCurrentLoadingBitmap = Utilities.scaleToActualAspectRatio(BitmapFactory.decodeFile(mImageDisplayPath), mWidthView, mHeightView);
	            	setEdit(isEdit);
	            	mDrawingView.setEdit(isEdit);
	            	mDrawingView.setListener(MainActivity.this);
 		    	    // init width view and height view
	            	mDrawingView.setwMainView(mWidthView);
 		    	    mDrawingView.sethMainView(mHeightView);

 		    	    mDrawingView.setMainBitmap(mCurrentLoadingBitmap);
 		    	  
 					// load gridview
 					loadGridView();
 					// check audio
 					CheckAudioExist();
 					if(flagLoadListRootShape)
     				{
 						ArrayList<CustomerShapeView> listShape = mManagerObject.getListChooseDrawObject().get(i).getListRootShape();
 						for (int index = 0; index < listShape.size(); index++){
 							listShape.get(index)._shapeId = index;
 						}
     					mDrawingView.loadListRootShape(listShape);
     				}
     				else
     				{
     					mDrawingView.clearAllRootShape();
     				}
 	    		}
 	            return;
 			}
 		}
		
		
	}
	
//	private void updateDataImageCurrentDraw()
//	{
//		int sizeHistoryImage = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size();
//		for(int i = 0; i < sizeHistoryImage ;i++)
//		{
//			if(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getIsSelect() == Configs.FLAG_IS_SELECT)
//			{
//				if(MainActivity.getInstance().getmDrawingView().getMainBitmap() != null)
//				{
//					Bitmap bmCurrentDraw = Utilities.getBitmapFromView(MainActivity.getInstance().getmDrawingView());
//					String folderPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getOriginalImageGalleryPath();
//					String fileEditPath = Utilities.getImageDrawFileNamePath(mCurrentProject, folderPath + Configs.FLAG_EDIT_FILE_NAME);
//					File fileEdit = new File(fileEditPath);
//					if(fileEdit.exists())
//					{
//						fileEdit.delete();
//					}
//					Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
//					MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).setEditImagePath(fileEdit.getAbsolutePath());
//					MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getListRootShape().removeAll(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getListRootShape());
//					MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getListRootShape().addAll(MainActivity.getInstance().getmDrawingView().getListRootShape());
//				
//					MainActivity.getInstance().mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
//					MainActivity.getInstance().mManagerObject = null;
//					System.gc();
//					MainActivity.getInstance().mManagerObject  = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
//				}
//			}
//		}
//	}
	
	private void addDescription()
	{
		try {
			String className = DescriptionActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(this, Class.forName(className));
			intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_ADD_DESCRIPTION, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_ADD_DESCRIPTION);
			int index = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
			intent.putExtra(Configs.BUNDLE_DESCRIPTION_RESULT, mManagerObject.getListChooseDrawObject().get(index).getmDescription());
			startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_ADD_DESCRIPTION);
//			overridePendingTransition(R.anim.right_slide_in, R.anim.hold);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
//	class LoadingShowScreenChooseImage extends AsyncTask<String, String, String> {
//
//		private Dialog mProgressDialog;
//		private Context mconContext;
//		public LoadingShowScreenChooseImage(Context mContext) {
//			this.mconContext = mContext;
//		}
//
//		@Override
//		protected void onPreExecute() {
//			if (mProgressDialog == null) {
//				mProgressDialog = new Dialog(mconContext, R.style.progressDialog);
//				final ProgressBar proBar = new ProgressBar(mconContext);
//				mProgressDialog.addContentView(proBar, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
//			}
//
//			if (mProgressDialog.isShowing())
//				return;
//			mProgressDialog.show();
//			mProgressDialog.setCanceledOnTouchOutside(false) ;
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			updateDataImageCurrentDraw();
//			actionChooseImageEdit();
//			return "";
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			
//			if (mProgressDialog != null && mProgressDialog.isShowing())
//				mProgressDialog.dismiss();
//		}
//	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_menu:
			OpenOrCloseMenu(OPEN_LEFT_MENU);
			break;
		case R.id.btn_undo:
			actionUndo();
			break;
		case R.id.btn_redo:
			actionRedo();
			break;
		case R.id.btn_choose_image:
//			new LoadingShowScreenChooseImage(this).execute();
			OpenOrCloseMenu(OPEN_RIGHT_MENU);
			break;
//		case R.id.btn_delete:
//			actionDelete();
//			break;
//		case R.id.btn_setting_paint:
//			showSettingPaint();
//			break;
		case R.id.img_description:
			addDescription();
			break;
		case R.id.img_edit:
			isEdit = !isEdit;
			setEdit(isEdit);
			if(mDrawingView != null)
			{
				mDrawingView.setEdit(isEdit);
			}
			break;
		case R.id.img_capture:
			actionCaptureImage();
			break;
		case R.id.img_insert_photo:
			actionInsetPhoto();
			break;
		case R.id.img_sendmail:
			sendMail();
			break;
		case R.id.img_record_audio:
			openRecordActivity();
			break;
		default:
			break;
		}
	}
	
	private void startPlaying(String mFileName) {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	private void pausePlaying()
	{
		if(mPlayer == null)
			return ;
		if(mPlayer.isPlaying()){
		    mPlayer.pause();
		} else {
		    mPlayer.start();
		}
	}
	
    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }
    
    private void CheckAudioExist()
    {
    	int index = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
    	// DuongGa
    	if(index <= MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size() - 1)
    	{
    		String folderPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(index).getOriginalImageGalleryPath();
    		String fileAudioPath = Utilities.getImageDrawFileNamePath(mCurrentProject, folderPath + Configs.FLAG_AUDIO_FORMAT);
    		File fileAudio = new File(fileAudioPath);
    		if(fileAudio.exists())
    		{
    			img_record_audio.setBackgroundResource(R.drawable.listen_sound);
    			return ;
    		}
    		img_record_audio.setBackgroundResource(R.drawable.add_voice);
    	}
    }
    
	private void startRecording(String mFileName) {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOnInfoListener(new OnInfoListener() {
			
			@Override
			public void onInfo(MediaRecorder arg0, int arg1, int arg2) {
				Log.e("onInfo", "onInfo onInfo onInfo");
			}
		});
        
        try {
            mRecorder.prepare();
        } catch (IOException e) {
           e.printStackTrace();
        }
        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

	
	private void sendMail()
	{
		checkCloseMenu();
		Bundle mBundle = new Bundle();
		String SceneName = SendEmailActivity.class.getSimpleName();
    	mBundle.putString(Configs.OPEN_SCREEN, SceneName);
    	openScreen(mBundle);
    	overridePendingTransition(R.anim.right_slide_in, R.anim.hold);
	}
	
	public void actionInsetPhoto()
	{
		Intent i = new Intent(Action.ACTION_MULTIPLE_PICK);
		startActivityForResult(i, RESULT_LOAD_IMAGE);
	}
	
	public void actionCaptureImage()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
	}
	
	/*
	 * Creating file uri to store image/video
	 */
	public Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}
	
	/*
	 * returning image / video
	 */
	private static File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				IMAGE_DIRECTORY_NAME);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
						+ IMAGE_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
				Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator
					+ "IMG_" + timeStamp + ".jpg");
		}  else {
			return null;
		}

		return mediaFile;
	}
	
	private void setEdit(boolean isEdit)
	{
		if(isEdit)
		{
			getSlidingMenu().setSlidingEnabled(false);
			getSlidingMenu().setTouchModeAbove(getSlidingMenu().TOUCHMODE_NONE);
			img_edit.setBackgroundResource(R.drawable.edit);
		}
		else
		{
			getSlidingMenu().setSlidingEnabled(true);
			getSlidingMenu().setTouchModeAbove(getSlidingMenu().TOUCHMODE_FULLSCREEN);
			
			img_edit.setBackgroundResource(R.drawable.edit_invisible);
		}
	}
	
	private void OpenOrCloseMenu(int isShowLeft_Right)
	{
		boolean isOpenMenu = false;
		if (getSlidingMenu().isMenuShowing()) {
			getSlidingMenu().showContent();
		} else {
			if(isShowLeft_Right == OPEN_LEFT_MENU)
			{
				getSlidingMenu().showMenu();
			}
			else
			{
				getSlidingMenu().showSecondaryMenu(true);
			}
			isOpenMenu = true;
		}
		
		if(mDrawingView != null)
		{
			mDrawingView.setShowingMenu(isOpenMenu);
		}
	}
	
	public class BehindMenuAdapter extends BaseAdapter {

		public Context mContext;
		public HashMap<String, ImageView> mHistoryClearImageViewItem;
		public TypedArray listIcon ;
		public TypedArray listText ;
		public TypedArray listcolor ;
		public BehindMenuAdapter(Context mContext)
		{
			this.mContext = mContext;
			mHistoryClearImageViewItem = new HashMap<String, ImageView>();
			listIcon = mContext.getResources().obtainTypedArray(R.array.listicon);
			listText = mContext.getResources().obtainTypedArray(R.array.listtext);
			listcolor =  mContext.getResources().obtainTypedArray(R.array.listcolor);
		}
		
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return listIcon.length();
		}

		@Override
		public Object getItem(int index) {
			
			return null;
		}

		@Override
		public long getItemId(int index) {
			
			return index;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if (row == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.item_setting, null);
			}
			
			ImageView imgIcon = (ImageView) row.findViewById(R.id.img_icon);
			imgIcon.setImageResource(listIcon.getResourceId(position, -1));
			TextView tvContent = (TextView) row.findViewById(R.id.tv_content);
			tvContent.setText(listText.getText(position));
			tvContent.setTextColor(listcolor.getColor(position, -1));
			return row;
		}
		
		public void ClearData() {

			if (mHistoryClearImageViewItem != null) {
				for (int i = 0; i < mHistoryClearImageViewItem.size(); i++) {
					ImageView view = mHistoryClearImageViewItem.get(i);
					if (view != null) {
						Utilities.recycleImageView(view,false);
					}
				}
				mHistoryClearImageViewItem.clear();
				mHistoryClearImageViewItem = null;
			}
			System.gc();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case Configs.BACK_CODE:
			back(data.getExtras());
			break;

		case Configs.OPEN_CODE:
			if (data != null) {
				Bundle bundle = data.getExtras();
				openScreen(bundle);
			}
			break;
		default:
			break;
		}
		
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			String[] all_path = data.getStringArrayExtra("all_path");
			if(all_path != null && all_path.length > 0)
			{
				new LoadingCopyChooseImage(MainActivity.this, all_path).execute();
			}
		}
		else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				
				copyImageToSDCard(fileUri.getPath());
				
				if(mAdapter != null)
				{
					mAdapter.mManagerObject = null;
					mAdapter.mManagerObject = mManagerObject;
					mAdapter.notifyDataSetChanged();
				}
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				
			} else {
				// failed to capture image
			}
		} 
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_ADD_PROJECT || requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_PROJECT)
		{
			if(data != null)
			{
				loadNewProject(data);
			}
		}
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_MANAGE_PROJECT)
		{
			if(data != null)
			{
				Bundle mBundle = data.getExtras();
				if(mBundle != null)
				{
					String deleteCurrentProject = "";
					if(mBundle.containsKey(Configs.BUNDLE_DELETE_PROJECT_NAME))
					{
						deleteCurrentProject = mBundle.getString(Configs.BUNDLE_DELETE_PROJECT_NAME);
						mBundle.remove(Configs.BUNDLE_DELETE_PROJECT_NAME);
						if(deleteCurrentProject.equals(mCurrentProject))   // current project is delete
						{
							deleteCurrentProject(mCurrentProject);
							return ;
						}
					}
					
					if(mBundle.containsKey(Configs.BUNDLE_EDIT_PROJECT_NAME))
					{
						String editCurrentProject = mBundle.getString(Configs.BUNDLE_EDIT_PROJECT_NAME);
						mBundle.remove(Configs.BUNDLE_EDIT_PROJECT_NAME);
						Log.e("editCurrentProject", "editCurrentProject ==== "+editCurrentProject);
						if(editCurrentProject != null && editCurrentProject.trim().length() > 0)
						{
							mCurrentProject = editCurrentProject;
							mManagerObject = ManagerImageObject.readFromFile(this, mCurrentProject);
							if(mAdapter != null)
							{
								mAdapter.mManagerObject = mManagerObject;
								mAdapter.notifyDataSetChanged();
							}
						}
					}
				}
			}
		}
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING)   // load data from setting
		{
			loadGridView();
		}
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_ADD_DESCRIPTION)  // add description
		{
			if(data != null)
			{
				Bundle mBundle = data.getExtras();
				if(mBundle != null)
				{
					String description = "";
					if(mBundle.containsKey(Configs.BUNDLE_DESCRIPTION_RESULT))
					{
						description = mBundle.getString(Configs.BUNDLE_DESCRIPTION_RESULT);
						mBundle.remove(Configs.BUNDLE_DESCRIPTION_RESULT);
						
						int index = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
						mManagerObject.getListChooseDrawObject().get(index).setmDescription(description);
						mManagerObject.writeToFile(this,mCurrentProject);
						mManagerObject = null;
						System.gc();
						mManagerObject  = ManagerImageObject.readFromFile(this,mCurrentProject);
						 // check all action  // DuongGa
    		    	    check_All_Action_Mail_Edit_Voice();
						
					}
				}
			}
		}
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_SETTING_PAINT)
		{
			if(mDrawingView != null)
			{
				int indexColor = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_COLOR, 0);
				int indexStrokeWidth = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_STROKEWIDTH, 0);
				mDrawingView.setCurrentPaint(indexColor, indexStrokeWidth,"");
			}
		}
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_BLUR_OPACITY)
		{
			if(mDrawingView != null)
			{
				int blurOpacity = PreferenceConnector.readInteger(this, Configs.FLAG_BLUR_OPACTIY, Configs.MIN_OPACITY_BLUR);
				mDrawingView.setBlurOpacity(blurOpacity);
			}
		}
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT)
		{
			if(data != null)
			{
				Bundle mBundle = data.getExtras();
				if(mBundle != null)
				{
					String textValue = "";
					if(mBundle.containsKey(Configs.KEY_BUNDLE_VALUE_TEXT))
					{
						textValue = mBundle.getString(Configs.KEY_BUNDLE_VALUE_TEXT);
						mBundle.remove(Configs.KEY_BUNDLE_VALUE_TEXT);
						if(mDrawingView != null)
						{
							int indexColor = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_COLOR, 0);
							int indexStrokeWidth = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_STROKEWIDTH, 0);
							mDrawingView.setCurrentPaint(indexColor, indexStrokeWidth,textValue);
						}
					}
				}
			}
		}
		else if(requestCode == Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_RECORD_AUDIO)
		{
			
			
			// check audio
			CheckAudioExist();
		}
	}
	
	private void loadGridView()
	{
		if(mDrawingView != null)
		{
			boolean isApplyGrid = PreferenceConnector.readBoolean(this, Configs.FLAG_APPLY_GIRD, false);
			Log.e("onResume", "onResume onResume onResume === "+isApplyGrid);
			if(isApplyGrid)
			{
				int mGridType = PreferenceConnector.readInteger(this, Configs.FLAG_TYPE_GIRD, 0);
				int mColorGrid = PreferenceConnector.readInteger(this, Configs.FLAG_TYPE_GIRD_COLOR, 0);
				mDrawingView.applyGrid(isApplyGrid,mGridType, mColorGrid);
			}
			else
			{
				mDrawingView.setDragGrid(false);
			}
			mDrawingView.setShowingMenu(false);
			mDrawingView.invalidate();
		}
	}
	
	private void deleteCurrentProject(String projectNameDelete)
	{
		if(mDrawingView != null)
  		{
  			 mDrawingView.resetAllData();
  			 mDrawingView.postInvalidate();
  		}
        
        mAdapter.mManagerObject.getListChooseDrawObject().removeAll(mAdapter.mManagerObject.getListChooseDrawObject());
        mManagerObject = null;
        mAdapter.notifyDataSetChanged();
        mCurrentProject = "";
        
        try {
   			String className = HelpActivity.class.getSimpleName();
				String packageName = getApplicationContext().getPackageName();
				if (className.indexOf(packageName) == -1) {
					className = packageName + "." + className;
				}
   			Intent intent = new Intent(this, Class.forName(className));
   			startActivity(intent);
   			finish();
			} catch (Exception e) {
				e.printStackTrace();
	    }
        
	}
	
	private void loadNewProject(Intent data)
	{
		Bundle mBundle = data.getExtras();
		if(mBundle != null)
		{
			String selectOpenProject = "";
			if(mBundle.containsKey(Configs.BUNDLE_PROJECT_NAME))
			{
				selectOpenProject = mBundle.getString(Configs.BUNDLE_PROJECT_NAME);
				mBundle.remove(Configs.BUNDLE_PROJECT_NAME);
				if(selectOpenProject.equals(mCurrentProject))
				{
					mDrawingView.setShowingMenu(false);
					return ;
				}
				mCurrentProject = selectOpenProject;
			}
		}
		
		// Get Object Mananger Data
		mManagerObject = ManagerImageObject.readFromFile(this,mCurrentProject);
		if(mManagerObject == null)
		{
			mManagerObject = new ManagerImageObject();
		}
		else
		{
			if(mManagerObject.getmFirstCreateProject() == Configs.FIRST_TIME_CREATE_PROJECT)
			{
				// save current index draw
				PreferenceConnector.writeInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, indexFirstImageCreateProject);
				mManagerObject.getListChooseDrawObject().get(indexFirstImageCreateProject).setIsSelect(Configs.FLAG_IS_SELECT);
				mManagerObject.setmFirstCreateProject(indexFirstImageCreateProject);
				mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
				mManagerObject = null;
				System.gc();
				mManagerObject  = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
			}
		}
		
		if(mAdapter != null)
		{
			mAdapter.mManagerObject = mManagerObject;
			mAdapter.notifyDataSetChanged();
		}
		
		if(mDrawingView != null)
		{
			mDrawingView.resetAllData();
		}
		
		loadImageSelectForDraw();
		
//		mFirstLoadImage = true;
//		if(mFirstLoadImage)
//		{	
//			Handler handlerDeplay = new Handler();
//			handlerDeplay.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					loadImageSelectForDraw();
//					mFirstLoadImage = false;
//					// load description 
//					loadDescription();
//					// load gridview
//					loadGridView();
//					// check sendMail
//					CheckSendMailStatus();
//				}
//			}, Configs.DELAY_TIME_TO_INIT_VIEW);
//		}
		
	}
	
	public void copyImageToSDCard(String picturePath)
	{
		if(mManagerObject.getListChooseDrawObject().size() == 0)
		{   
//			Log.e("imageOriginalPath", "imageOriginalPath ========== " + " picturePath ==== "+picturePath);
			copyImageFormPath(picturePath);
		}
		else
		{
			boolean flagCopy = true;
			for(int i = 0 ; i < mManagerObject.getListChooseDrawObject().size() ; i++)
			{
				String imageOriginalPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getOriginalImageGalleryPath();
//				Log.e("imageOriginalPath", "imageOriginalPath ========== "+imageOriginalPath + " *** picturePath ==== "+picturePath);
				if(imageOriginalPath.contains(picturePath))
				{
					flagCopy = false;
//					Log.e("flagCopy =", "flagCopy = "+flagCopy);
					break;
				}
			}
			
			if(flagCopy)
			{
				copyImageFormPath(picturePath);
			}
		}
	}
	
	public void copyImageFormPath(String picturePath)
	{
		ImageDrawObject imageDraw = new ImageDrawObject();
		imageDraw.setOriginalImageGalleryPath(picturePath);
		// create forder in sdcard
		Utilities.createFolderProject(MainActivity.getInstance().mCurrentProject);
		// copy file
		File fileSource = new File(picturePath);
		String imagePathSDCard = Utilities.getImageDrawFileNamePath(MainActivity.getInstance().mCurrentProject, picturePath + Configs.FLAG_ORIGINAL_FILE_NAME);
		File filedest = new File(imagePathSDCard);
		try {
			if(filedest.exists())
			{
				filedest.delete();
			}
			
			int angle = Utilities.checkDeviceAutoRotateBitmap(picturePath);
			if(angle > 0)
			{
				Bitmap bitMapSource = Utilities.decodeFile(true, fileSource);
				Bitmap bitMapRotate = Utilities.RotateBitmap(bitMapSource, angle);
				Utilities.saveToSdcardPNG(filedest, bitMapRotate);
				bitMapSource.recycle();
				bitMapRotate.recycle();
				bitMapSource = null;
				bitMapRotate = null;
			}
			else
			{
				Utilities.copyFileUsingFileStreams(fileSource, filedest);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		imageDraw.setOriginalImagePath(filedest.getAbsolutePath());
		// update data 
		mManagerObject.getListChooseDrawObject().add(imageDraw);
		mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
		mManagerObject = null;
		System.gc();
		mManagerObject = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
	}
	
	class LoadingCopyChooseImage extends AsyncTask<String, String, String> {

		private Dialog mProgressDialog;
		private Context mconContext;
		private String[] all_path;
		public LoadingCopyChooseImage(Context mContext,String[] all_path) {
			this.mconContext = mContext;
			this.all_path = all_path;
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
			
			for (String imagePath : all_path) {
				copyImageToSDCard(imagePath);
			}
			
			return "";
		}

		@Override
		protected void onPostExecute(String result) {
			
			if(mAdapter != null)
			{
				mAdapter.mManagerObject = null;
				mAdapter.mManagerObject = mManagerObject;
				mAdapter.notifyDataSetChanged();
			}
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}
	}
	
	
	private void openScreen(Bundle bundle) {
		if (bundle == null) {
			return;
		}
		if (mHistory == null) {
			mHistory = new ArrayList<String>();
		}

		if (bundle != null) {
			if (bundle.containsKey(Configs.OPEN_SCREEN)) {
				String className = bundle.getString(Configs.OPEN_SCREEN);
				String packageName = getApplicationContext().getPackageName();
				if (className.indexOf(packageName) == -1) {
					className = packageName + "." + className;
				}
				if (!bundle.containsKey(Configs.NOT_SAVE_TO_HISTORY)) {
					mHistory.add(className);
				} else {
					bundle.remove(Configs.NOT_SAVE_TO_HISTORY);
				}
				bundle.remove(Configs.OPEN_SCREEN);
				try {
					Intent intent = new Intent(this, Class.forName(className));
					intent.putExtras(bundle);
					startActivityForResult(intent, 1);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void back(Bundle bundle) {
		
		if(mHistory.isEmpty())
		{
			resetDataWhenUserExisted();
			finish();
			return;
		}
		int indexScene = mHistory.size() - 1;
		mHistory.remove(indexScene);
		
		if(mHistory.size() > 0 )
		{	
			String className = mHistory.get(mHistory.size() - 1);
			try {
				Intent intent = new Intent(this, Class.forName(className));
				intent.putExtras(bundle);
				startActivityForResult(intent, 1);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void resetDataWhenUserExisted()
	{
		if(mDrawingView != null)
		{
			mDrawingView.resetAllData();
		}
		System.gc();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		
		filterShapeInCurrentBitmap();
		mManagerObject.writeToFile(this,mCurrentProject);
		mManagerObject = null;
		
		if(mHistory.isEmpty())
		{
			resetDataWhenUserExisted();
			finish();
			return;
		}
	}
	
	private void filterShapeInCurrentBitmap(){
		if(mManagerObject != null)
		{
			int indexCurrentDraw = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
			
			ArrayList<CustomerShapeView> listShapeCurrent = mDrawingView.getListRootShape();
			
			//Clear invisible shape
			for(int i = listShapeCurrent.size() - 1 ; i >= 0 ; i-- )
			{
				CustomerShapeView v = listShapeCurrent.get(i);
				if (!v.isVisible){
					listShapeCurrent.remove(i);
				}
			}
			//Set id
			
			for (int i = 0;i<listShapeCurrent.size(); i++){
				listShapeCurrent.get(i)._shapeId = i;
			}
			// DuongGa
			if(mManagerObject.getListChooseDrawObject().size() > 0 && indexCurrentDraw <= mManagerObject.getListChooseDrawObject().size() -1)
			{
				mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().clear();
				mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().addAll(listShapeCurrent);
			}
	}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	@Override
	public void isShowSetting(boolean flag) {
		
//		if(flag)
//		{
//			btn_delete.setVisibility(View.VISIBLE);
//			btn_setting_paint.setVisibility(View.VISIBLE);
//		}
//		else
//		{
//			btn_delete.setVisibility(View.GONE);
//			btn_setting_paint.setVisibility(View.GONE);
//		}
		
	}

	@Override
	public void isOnOff_Undo_Redo(boolean isOffUndo, boolean isOffRedo) {
		
		if(isOffUndo)
		{
			btn_undo.setBackgroundResource(R.drawable.undo_ico);
			btn_undo.setEnabled(true);
		}
		else
		{
			btn_undo.setBackgroundResource(R.drawable.undo_dis);
			btn_undo.setEnabled(false);
		}
		
		if(isOffRedo)
		{
			btn_redo.setBackgroundResource(R.drawable.redo_ico);
			btn_redo.setEnabled(true);
		}
		else
		{
			btn_redo.setBackgroundResource(R.drawable.redo_dis);
			btn_redo.setEnabled(false);
		}
	}


	public DrawingView getmDrawingView() {
		return mDrawingView;
	}


	public void setmDrawingView(DrawingView mDrawingView) {
		this.mDrawingView = mDrawingView;
	}


//	@Override
//	public void getWidthAndHeightCanvas(int width, int height) {
//		
//		if(mFirstLoadImage)
//		{	
//			Handler handlerDeplay = new Handler();
//			handlerDeplay.postDelayed(new Runnable() {
//				@Override
//				public void run() {
//					loadImageSelectForDraw();
//					mFirstLoadImage = false;
//					// load description 
//					loadDescription();
//					// load gridview
//					loadGridView();
//					// check sendMail
//					CheckSendMailStatus();
//				}
//			}, Configs.DELAY_TIME_TO_INIT_VIEW);
//		}
//	}
	
//	class LoadingCurrentImageForDraw extends AsyncTask<String, String, String> {
//
//		
//		private Context mconContext;
//		private ProgressDialog Asycdialog;
//		public LoadingCurrentImageForDraw(Context mContext) {
//			this.mconContext = mContext;
//			Asycdialog = new ProgressDialog(mconContext);
//		}
//
//		@Override
//		protected void onPreExecute() {
//			Asycdialog.setMessage("Loading...");
//            Asycdialog.show();
//		}
//
//		@Override
//		protected String doInBackground(String... params) {
//			
//			loadImageSelectForDraw();
//			mFirstLoadImage = false;
//			// load description 
//			loadDescription();
//			
//			return "";
//		}
//
//		@Override
//		protected void onPostExecute(String result) {
//			
//			
//			
//		}
//	}
	
	@Override
	public void getIndexDeleteShapeInCurrentDraw(int indexCurrentDraw,int indexCurrentShapeDelete) {
//		Log.e("indexCurrentDraw", "indexCurrentDraw == "+indexCurrentDraw + " indexCurrentShapeDelete ==== "+indexCurrentShapeDelete);
//		Log.e("aaaaaaa", "aaaaaaaaa ========= "+ (mManagerObject.getListChooseDrawObject().size() - 1));
//		Log.e("bbbbbb", "bbbbbbbbb == "+(mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().size()-1));
		if(indexCurrentDraw <= mManagerObject.getListChooseDrawObject().size() - 1 && indexCurrentShapeDelete <= mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().size()-1)
		{
//			Log.e("cccccccc", "ccccccccccc");
			mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getListRootShape().remove(indexCurrentShapeDelete);
			mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
			mManagerObject = null;
			System.gc();
			mManagerObject  = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
		}
	}

	@Override
	public void showDeleteBar(boolean isShowDeleteBar,boolean isDeleteIcon) {
		
		if(isShowDeleteBar)
		{
			rl_delete.setVisibility(View.VISIBLE);
		}
		else
		{
			rl_delete.setVisibility(View.GONE);
		}
		
		if(isDeleteIcon)
		{
			img_delete.setBackgroundResource(R.drawable.deleted_sel);
		}
		else
		{
			img_delete.setBackgroundResource(R.drawable.delete);
		}
	}
	
	@Override
	public void isShowingMenu(boolean isShowMenu) {
		// close menu
		if(isShowMenu)
		{
			if (getSlidingMenu().isMenuShowing()) {
				getSlidingMenu().showContent();
			} 
			if(mDrawingView != null)
			{
				mDrawingView.setShowingMenu(!isShowMenu);
			}
		}
	}

	@Override
	public void isOpenSettingPaint(int typeShape) {
		openSettingPaint(typeShape);
	}

	@Override
	public void isDoubleTap(final float x,final float y) {
		
		LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.addtext, null);
		
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.str_addtext);
		builder.setView(textEntryView);
		builder.setPositiveButton(R.string.str_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
            	MyEditText editText = (MyEditText)textEntryView.findViewById(R.id.edt_add_text);
            	String text = editText.getText().toString().trim();
            	if(text != null && text.length() > 0 )
            	{
            		if(mDrawingView != null)
            		{
            			mDrawingView.createText(x,y,text);
            		}
            	}
            	dialog.cancel();
            }
        });
		builder.setNegativeButton(R.string.str_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	dialog.cancel();
            }
        });
		
		 AlertDialog alert = builder.create();
         alert.show();
        
      // Must call show() prior to fetching text view
         TextView titleView = (TextView)alert.findViewById(getResources().getIdentifier("alertTitle", "id", "android"));
         if (titleView != null) {
             titleView.setGravity(Gravity.CENTER);
         }
         
		
	}
	
	private void openEditText(String valueText)
	{
		try {
			String className = EditTextActivity.class.getSimpleName();
			String packageName = getApplicationContext().getPackageName();
			if (className.indexOf(packageName) == -1) {
				className = packageName + "." + className;
			}
			Intent intent = new Intent(this, Class.forName(className));
			intent.putExtra(Configs.KEY_BUNDLE_VALUE_TEXT, valueText);
			intent.putExtra(Configs.KEY_BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT);
			startActivityForResult(intent, Configs.BUNDLE_FORM_MAIN_SCREEN_OPEN_EDIT_TEXT);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void openBlurOpacitySetting()
	{
		
	}

	public Bitmap getmCurrentLoadingBitmap() {
		return mCurrentLoadingBitmap;
	}

	public void setmCurrentLoadingBitmap(Bitmap mCurrentLoadingBitmap) {
		this.mCurrentLoadingBitmap = mCurrentLoadingBitmap;
	}

	// DuongGa
	private void check_All_Action_Mail_Edit_Voice()
	{
		if(mManagerObject != null && mManagerObject.getListChooseDrawObject().size() > 0)
		{
			// check send mail
			img_sendmail.setBackgroundResource(R.drawable.share_blue);
			img_sendmail.setEnabled(true);
			// check description
			img_description.setBackgroundResource(R.drawable.description);
			img_description.setEnabled(true);
			
			int indexCurrentDraw = PreferenceConnector.readInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW	, 0);
			if(mManagerObject != null)
			{
				if(indexCurrentDraw <= mManagerObject.getListChooseDrawObject().size() -1 )
				{
					String description = mManagerObject.getListChooseDrawObject().get(indexCurrentDraw).getmDescription();
					tv_description.setText(description);
					if(description != null && description.length() > 0)
					{
						img_description.setBackgroundResource(R.drawable.edit_description);
					}
				}
			}
			
			img_record_audio.setBackgroundResource(R.drawable.add_voice);
			img_record_audio.setEnabled(true);
			
		}
		else
		{
			// check send mail
			img_sendmail.setBackgroundResource(R.drawable.share);
			img_sendmail.setEnabled(false);
			// check description
			img_description.setBackgroundResource(R.drawable.add_description_dis);
			img_description.setEnabled(false);
			// check audio
			img_record_audio.setBackgroundResource(R.drawable.add_voice_dis);
			img_record_audio.setEnabled(false);
		}
		
	}
	
	
}
