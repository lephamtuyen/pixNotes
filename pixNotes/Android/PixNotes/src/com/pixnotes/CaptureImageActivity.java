package com.pixnotes;

import it.sephiroth.android.library.widget.HListView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.pixnotes.adapter.Action;
import com.pixnotes.common.PreferenceConnector;
import com.pixnotes.common.Utilities;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.imageloader.ImageLoader;
import com.pixnotes.objects.ImageDrawObject;
import com.pixnotes.objects.ManagerImageObject;

public class CaptureImageActivity extends BaseActivity implements OnClickListener{
	
	private ImageView img_capture;
	private ImageView img_insert_photo;
	private HListView mygallery;
	private int RESULT_LOAD_IMAGE = 1;
	private ImageAdapter mAdapter;
	private Context mContext;
	private Uri fileUri; // file url to store image/video
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private static final String IMAGE_DIRECTORY_NAME = "CaptureImageFormCamera";
	private boolean mSwiping = false;
	private boolean mHorizontalSwiping = false;
    private boolean mItemPressed = false;
    private static final int SWIPE_DURATION = 250;
    private static final int MOVE_DURATION = 150;
    private HashMap<Long, Integer> mItemIdTopMap = new HashMap<Long, Integer>();
    private GestureDetector mGestureDetector;
    private View mCurrentView;
    private boolean isLongPressOnView = false;
    private Intent theIntent;
    private float positionFingerX = 0;
    private int positionCurrentChoiceImage = 0;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.captureimage);
		mContext = this;
		mGestureDetector = new GestureDetector(new MyGestureDetector());
		initComponent();
		setComponentListener();
		loadListImageFormHistoryStore();
		theIntent = getIntent();
		String theAction = theIntent.getAction();
		Log.e("theAction", "theAction ========= "+theAction);
	}
	
	public void initComponent()
	{
		img_capture = (ImageView) findViewById(R.id.img_capture);
		img_insert_photo = (ImageView) findViewById(R.id.img_insert_photo);
		mygallery = (HListView) findViewById(R.id.mygallery);
	}
	public void setComponentListener()
	{
		img_capture.setOnClickListener(this);
		img_insert_photo.setOnClickListener(this);
	}
	
	private void updateDataImageCurrentDraw(int position)
	{
		int sizeHistoryImage = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size();
		
		if(sizeHistoryImage == 1)
		{	
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(0).setIsSelect(Configs.FLAG_IS_SELECT);
		}
		Log.e("position", "position === "+position);
		for(int i = 0; i < sizeHistoryImage ;i++)
		{
			if(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getIsSelect() == Configs.FLAG_IS_SELECT)
			{
				if(i == position)
				{
					onBackPressed();
					return;
				}
				
				if(i != position)  // save current image edit
				{
					Log.e("iiii ", "iii ============ "+i);
					MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).setIsSelect(Configs.FLAG_IS_NOT_SELECT);
					if(MainActivity.getInstance().getmDrawingView().getMainBitmap() != null)
					{
						Bitmap bmCurrentDraw = Utilities.getBitmapFromView(MainActivity.getInstance().getmDrawingView());
						String folderPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getOriginalImageGalleryPath();
						String fileEditPath = Utilities.getImageDrawFileNamePath(MainActivity.getInstance().mCurrentProject, folderPath + Configs.FLAG_EDIT_FILE_NAME);
						Log.e("fileEditPath", "fileEditPath ========= "+fileEditPath);
						File fileEdit = new File(fileEditPath);
						if(fileEdit.exists())
						{
							fileEdit.delete();
						}
						Utilities.saveToSdcardPNG(fileEdit, bmCurrentDraw);
						MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).setEditImagePath(fileEdit.getAbsolutePath());
						// remove all shape in database
						MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getListRootShape().removeAll(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getListRootShape());
						// save shape in current view
						MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getListRootShape().addAll(MainActivity.getInstance().getmDrawingView().getListRootShape());
						MainActivity.getInstance().getmDrawingView().resetDataWhenUserDrawOtherImage();
						bmCurrentDraw.recycle();
						bmCurrentDraw = null;
					}
				}
			}
		}
		
		// save current index draw
		PreferenceConnector.writeInteger(this, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, position);
		MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(position).setIsSelect(Configs.FLAG_IS_SELECT);
		MainActivity.getInstance().mManagerObject.writeToFile(CaptureImageActivity.this,MainActivity.getInstance().mCurrentProject);
		MainActivity.getInstance().mManagerObject = null;
		System.gc();
		MainActivity.getInstance().mManagerObject  = ManagerImageObject.readFromFile(CaptureImageActivity.this,MainActivity.getInstance().mCurrentProject);
		
		onBackPressed();
	}
	
	public void resetAllIsCurrentSelect()
	{
		for(int i = 0 ; i < MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size();i++)
		{
			MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).setIsSelect(Configs.FLAG_IS_NOT_SELECT);
		}
	}
	public void loadListImageFormHistoryStore()
	{
		if(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size() == 0)
		{
			return;
		}
		
		if(mAdapter == null)
		{
			for(int i = 0 ; i < MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size();i++)
			{
				Log.e("getIsSelect", "getIsSelect == "+MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getIsSelect()+ " ii == "+i);
			}
			
			mAdapter = new ImageAdapter(this,MainActivity.getInstance().mManagerObject,mGestureDetector,mTouchListener);
			mygallery.setAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	public void actionInsetPhoto()
	{
//		Intent i = new Intent(
//				Intent.ACTION_PICK,
//				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//		startActivityForResult(i, RESULT_LOAD_IMAGE);
		
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
	public class ImageAdapter extends BaseAdapter {
		
		public Context mContext;
		public ImageLoader mImageLoader;
		public ManagerImageObject mManagerObject;
		public GestureDetector mGestureDetector;
		public View.OnTouchListener mTouchListener;
		public ImageAdapter(Context context,ManagerImageObject mManagerObject,GestureDetector mGestureDetector,View.OnTouchListener mTouchListener) {
			mContext = context;
			this.mManagerObject = mManagerObject;
			mImageLoader = new ImageLoader(context);
			this.mGestureDetector = mGestureDetector;
			this.mTouchListener = mTouchListener;
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
    		
    		return position;
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
            
            mImageLoader.DisplayImageView(true, position, mImageDisplayPath, imgView, 0, 0);
            row.setOnTouchListener(mTouchListener);
    		return row;
    	}
    }
	
	public void scaleView(View v) {
		ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.2f, 1, 1.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);  
        scaleAnimation.setDuration(1);  
        scaleAnimation.setFillAfter(true);
		v.startAnimation(scaleAnimation);
	}
	
	class MyGestureDetector extends SimpleOnGestureListener {
        
		float mUpY;
        private int mSwipeSlop = -1;
		
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.e("onSingleTapUp", "onSingleTapUp");
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.e("onShowPress", "onShowPress");
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			// TODO Auto-generated method stub
			Log.e("onScroll", "onScroll");
			
			
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			Log.e("onLongPress", "onLongPress");
			isLongPressOnView = true;
			if(mCurrentView != null)
			{
//				positionCurrentChoiceImage = mygallery.getPositionForView(mCurrentView);
//				mygallery.bringChildToFront(mCurrentView);
//            	mygallery.invalidate();
            	mCurrentView.setScaleX(1.1f);
            	mCurrentView.setScaleY(1.1f);
			}
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.e("onFling", "onFling");
			return false;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.e("onDown", "onDown");
			return false;
		}
	}
	
	public int dpToPx(int dp) {
	    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
	    int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));       
	    return px;
	}
	
	public boolean isSwapView(float direction,float positionFingerX)
	{
		boolean flag = false;
		positionCurrentChoiceImage = mygallery.getPositionForView(mCurrentView);
		int flagChangeDirection = 1;
		if(direction > 0)
		{	
			flagChangeDirection = -1;
		}
		Log.e("------------------------------------", "------------------------------");
//		Log.e("position", "position === "+positionCurrentChoiceImage + " flagChangeDirection == "+flagChangeDirection + "  mygallery.getChildCount() == "+mygallery.getChildCount());
		for(int i = 0 ; i < mygallery.getChildCount();i++)
		{
			final View child = mygallery.getChildAt(i);
			Log.e("child", "child ii = " +i+" X = "+child.getX() + " transitionX = "+child.getTranslationX()+" positionfingerX = "+positionFingerX + " getWidth "+child.getWidth());
			if(positionCurrentChoiceImage != i)
			{
				Log.e("child", "child ii = " +i+" X = "+child.getX() + " transitionX = "+child.getTranslationX()+" positionfingerX = "+positionFingerX);
				if(positionFingerX >= child.getX() && positionFingerX <= child.getX()+child.getWidth())
				{
					child.animate().setDuration(MOVE_DURATION).translationX((child.getWidth() + dpToPx(20))*flagChangeDirection);
					Log.e("i", "iiiiiiii ======= "+i);
					return true;
				}
			}
		}
		
		return flag;
	}
	
	private static final float TOUCH_TOLERANCE = 10;
	/**
     * Handle touch events to fade/move dragged items as they are swiped out
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        
        float mUpY;
        float mDownX;
        private int mSwipeSlop = -1;
        
        @Override
        public boolean onTouch(final View v, MotionEvent event) {
        	mGestureDetector.onTouchEvent(event);
            if (mSwipeSlop < 0) {
                mSwipeSlop = ViewConfiguration.get(CaptureImageActivity.this).
                        getScaledTouchSlop();
            }
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mItemPressed) {
                    // Multi-item swipes not handled
                    return false;
                }
                mItemPressed = true;
                mUpY = event.getY();
                mDownX = event.getX();
                positionFingerX = mDownX;
                Log.e("ACTION_DOWN", "ACTION_DOWN ACTION_DOWN "+mDownX);
                break;
            case MotionEvent.ACTION_CANCEL:
            {
            	v.setAlpha(1);
            	v.setTranslationY(0);
                mItemPressed = false;
                Log.e("ACTION_CANCEL", "ACTION_CANCEL ACTION_CANCEL");
            }
                
                break;
            case MotionEvent.ACTION_MOVE:
                {
                	mCurrentView = v;
                	
                    if(!isLongPressOnView)
                    {
                    	float y = event.getY() + v.getTranslationY();
                        float deltaY = y - mUpY;
                    	float deltaYAbs = Math.abs(deltaY);
                        if (!mSwiping) {
                          if (deltaYAbs > mSwipeSlop) {
                              mSwiping = true;
                              mygallery.requestDisallowInterceptTouchEvent(true);
                          }
                        }
                        if (mSwiping) {
                        	v.setTranslationY((y - mUpY));
                            v.setAlpha(1 - deltaYAbs / v.getHeight());
                        }
                    }
                    else
                    {
                    	mygallery.requestDisallowInterceptTouchEvent(true);
                    	float x = event.getX() + mCurrentView.getTranslationX();
                    	float dx = x - mDownX;
                    	mCurrentView.setTranslationX(mCurrentView.getTranslationX() + dx);
                    	mDownX = x;
                    	positionFingerX += dx;
//                        Log.e("mDownX", "mDownX ==== "+mDownX + " DX === "+dx);
                     	if(isSwapView(dx,positionFingerX))
                     	{
                        	
                     	}
//                        Log.e("ACTION_MOVE", "x = "+event.getX()+" mCurrentView.getTranslationX() = "+ mCurrentView.getTranslationX()+ " dx = "+dx + " mCurrentView.getX() = "+mCurrentView.getX() +" mDownX = "+mDownX);
                    }
                    
//                	Log.e("ACTION_MOVE", "ACTION_MOVE ACTION_MOVE");
                }
                break;
            case MotionEvent.ACTION_UP:
                {
                	if(!isLongPressOnView)
                	{
                		// User let go - figure out whether to animate the view out, or back into place
                        if (mSwiping) {
    	                      float y = event.getY() + v.getTranslationY();
    	                      float deltaY = y - mUpY;
    	                      float deltaYAbs = Math.abs(deltaY);
    	                      float fractionCovered;
    	                      float endY;
    	                      float endAlpha;
    	                      final boolean remove;
    	                      if (deltaYAbs > v.getHeight() / 2) {
    	                          // Greater than a quarter of the width - animate it out
    	                          fractionCovered = deltaYAbs / v.getHeight();
    	                          endY = deltaY < 0 ? -v.getHeight() : v.getHeight();
    	                          endAlpha = 0;
    	                          remove = true;
    	                      } else {
    	                          // Not far enough - animate it back
    	                          fractionCovered = 1 - (deltaYAbs / v.getHeight());
    	                          endY = 0;
    	                          endAlpha = 1;
    	                          remove = false;
    	                      }
                        	
    	                   // Animate position and alpha of swiped item
                            // NOTE: This is a simplified version of swipe behavior, for the
                            // purposes of this demo about animation. A real version should use
                            // velocity (via the VelocityTracker class) to send the item off or
                            // back at an appropriate speed.
                            long duration = (int) ((1 - fractionCovered) * SWIPE_DURATION);
                            duration = Math.abs(duration);
                            Log.e("duration", "duration ==== "+duration);
                            mygallery.setEnabled(false);
                            v.animate().setDuration(duration).
                                    alpha(endAlpha).translationY(endY).
                                    withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            // Restore animated values
                                        	v.setAlpha(1);
                                            v.setTranslationY(0);
                                            if (remove) {
                                                animateRemoval(mygallery, v);
                                            } else {
                                                mSwiping = false;
                                                mygallery.setEnabled(true);
                                            }
                                        }
                                    });
                        }
                        else
                        {
                        	int position = mygallery.getPositionForView(v);
                        	updateDataImageCurrentDraw(position);
                        }
                	}
                	else
                	{
                		isLongPressOnView = false;
                		mygallery.setEnabled(true);
                		
                		
                		for(int i = 0 ; i < mygallery.getChildCount();i++)
                		{
                			final View child = mygallery.getChildAt(i);
                			Log.e("child", "child ii = " +i+" X = "+child.getX() + " transitionX = "+child.getTranslationX()+" positionfingerX = "+positionFingerX + " getWidth "+child.getWidth());
                			
                		}
                		
                	}
                    
                    Log.e("ACTION_UP", "ACTION_UP ACTION_UP");
                }
                mItemPressed = false;
                break;
            default: 
                return false;
            }
            return true;
        }
    };

    /**
     * This method animates all other views in the ListView container (not including ignoreView)
     * into their final positions. It is called after ignoreView has been removed from the
     * adapter, but before layout has been run. The approach here is to figure out where
     * everything is now, then allow layout to run, then figure out where everything is after
     * layout, and then to run animations between all of those start/end positions.
     */
    private void animateRemoval(final HListView listview, View viewToRemove) {
        
//    	int firstVisiblePosition = listview.getFirstVisiblePosition();
//        for (int i = 0; i < listview.getChildCount(); ++i) {
//            View child = listview.getChildAt(i);
//            if (child != viewToRemove) {
//                int position = firstVisiblePosition + i;
//                Log.e("*****position", "******position == "+position);
//                long itemId = mAdapter.getItemId(position);
//                Log.e("*****itemId", "******itemId == "+itemId);
//                mItemIdTopMap.put(itemId, child.getLeft());
//                Log.e("***** mItemIdTopMap.put(itemId, child.getLeft());", " mItemIdTopMap.put(itemId, child.getLeft());"+child.getLeft());
//            }
//        }
        
//        // Delete the item from the adapter
        final int positionItemDelete = mygallery.getPositionForView(viewToRemove);
        MainActivity.getInstance().mManagerObject.getListChooseDrawObject().remove(positionItemDelete);
        MainActivity.getInstance().mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
		MainActivity.getInstance().mManagerObject = null;
		System.gc();
		MainActivity.getInstance().mManagerObject = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
		
		mSwiping = false;
        mygallery.setEnabled(true);
        mAdapter.updateListImage(MainActivity.getInstance().mManagerObject);
        mygallery.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        mygallery.setSelection(positionItemDelete);

//        final ViewTreeObserver observer = listview.getViewTreeObserver();
//        observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            public boolean onPreDraw() {
//                observer.removeOnPreDrawListener(this);
//                boolean firstAnimation = true;
//                int firstVisiblePosition = listview.getFirstVisiblePosition();
//                for (int i = positionItemDelete + 1; i < listview.getChildCount(); ++i) {
//                    final View child = listview.getChildAt(i);
//                    child.animate().setDuration(MOVE_DURATION).translationX(-child.getWidth());
//                    if (firstAnimation) {
//                        child.animate().withEndAction(new Runnable() {
//                            public void run() {
//                                mSwiping = false;
//                                mygallery.setEnabled(true);
//                            }
//                        });
//                        firstAnimation = false;
//                    }
//                }
//                mItemIdTopMap.clear();
//                return true;
//            }
//        });
    }
	
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_capture:
			actionCaptureImage();
			break;
		case R.id.img_insert_photo:
			actionInsetPhoto();
			break;
		}
		
	}
	
	public void displayImageAfterSelected()
	{
		if(mAdapter == null)
		{
			mAdapter = new ImageAdapter(this,MainActivity.getInstance().mManagerObject,mGestureDetector,mTouchListener);
			mygallery.setAdapter(mAdapter);
		}
		else
		{
			mAdapter.updateListImage(MainActivity.getInstance().mManagerObject);
		}
	}
	
	public void copyImageToSDCard(String picturePath)
	{
		if(MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size() == 0)
		{   
			Log.e("imageOriginalPath", "imageOriginalPath ========== " + " picturePath ==== "+picturePath);
			copyImageFormPath(picturePath);
		}
		else
		{
			boolean flagCopy = true;
			for(int i = 0 ; i < MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size() ; i++)
			{
				String imageOriginalPath = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(i).getOriginalImageGalleryPath();
				Log.e("imageOriginalPath", "imageOriginalPath ========== "+imageOriginalPath + " *** picturePath ==== "+picturePath);
				if(imageOriginalPath.contains(picturePath))
				{
					flagCopy = false;
					Log.e("flagCopy =", "flagCopy = "+flagCopy);
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
			Utilities.copyFileUsingFileStreams(fileSource, filedest);
		} catch (Exception e) {
			e.printStackTrace();
		}
		imageDraw.setOriginalImagePath(filedest.getAbsolutePath());
		// update data 
		MainActivity.getInstance().mManagerObject.getListChooseDrawObject().add(imageDraw);
		MainActivity.getInstance().mManagerObject.writeToFile(this,MainActivity.getInstance().mCurrentProject);
		MainActivity.getInstance().mManagerObject = null;
		System.gc();
		MainActivity.getInstance().mManagerObject = ManagerImageObject.readFromFile(this,MainActivity.getInstance().mCurrentProject);
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
			
			displayImageAfterSelected();
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//			Uri selectedImage = data.getData();
//			String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//			Cursor cursor = getContentResolver().query(selectedImage,
//					filePathColumn, null, null, null);
//			cursor.moveToFirst();
//
//			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//			String picturePath = cursor.getString(columnIndex);
//			cursor.close();
//			
//			loadPictureFromPath(picturePath);
			
			String[] all_path = data.getStringArrayExtra("all_path");
			if(all_path != null && all_path.length > 0)
			{
				new LoadingCopyChooseImage(CaptureImageActivity.this, all_path).execute();
			}
		}
		else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				copyImageToSDCard(fileUri.getPath());
				displayImageAfterSelected();
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				
			} else {
				// failed to capture image
				
			}
		} 
    }
}
