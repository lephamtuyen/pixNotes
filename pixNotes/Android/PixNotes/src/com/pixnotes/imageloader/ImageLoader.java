package com.pixnotes.imageloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.pixnotes.R;
import com.pixnotes.common.Utilities;
import com.pixnotes.utils.LogUtils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageLoader {

	private  MemoryCache memoryCache=new MemoryCache();
	private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private Map<Bitmap, String> bitmapViews=Collections.synchronizedMap(new WeakHashMap<Bitmap, String>());
	private FileCache fileCache;
	private ExecutorService executorService;
	public int fixImageMaxeSize = 150000;
	public Context mContext;
	public ImageLoader(Context context) {
		this.mContext = context;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
	}
	
	public void DisplayImageView(boolean mFlgScale,int id,String url, ImageView imageView , int reqWidth, int reqHeight) {
		
		if (url == null)
			return;
		if (url.trim().equals(""))
			return;
		
		imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
        {
        	imageView.setImageBitmap(bitmap);
        }
        else
        {
        	queuePhotoImageView(mFlgScale,id, url,imageView,reqWidth,reqHeight);
        }
	}
	
	public void DisplayImageViewForLoadEdit(boolean mFlgScale,int id,String url, ImageView imageView , int reqWidth, int reqHeight) {
		
		if (url == null)
			return;
		if (url.trim().equals(""))
			return;
		
		imageViews.put(imageView, url);
        queuePhotoImageView(mFlgScale,id, url,imageView,reqWidth,reqHeight);
	}
	
	public void queuePhotoImageView(boolean mFlgScale,int id,String url, ImageView imageView,int reqWidth, int reqHeight) {
		PhotoToLoadImageView p = new PhotoToLoadImageView( mFlgScale,id, url,  imageView , reqWidth , reqHeight );
		executorService.submit(new PhotosLoaderImageView(p));
	}
	
	// Task for the queue
	public class PhotoToLoadImageView {
		public boolean mFlgScale;
		public int reqWidth; 
		public int reqHeight;
		public int id;
		public String url;
		public ImageView imageView;
		public PhotoToLoadImageView(boolean mFlgScale,int id,String url, ImageView imageView,int reqWidth, int reqHeight) {
			this.mFlgScale=mFlgScale;
			this.reqWidth=reqWidth;
			this.reqHeight=reqHeight;
			this.id=id;
			this.url=url;
			this.imageView=imageView;
		}
	}
	
	class PhotosLoaderImageView implements Runnable {
		PhotoToLoadImageView photoToLoadImageView;
		PhotosLoaderImageView(PhotoToLoadImageView photoToLoad) {
			this.photoToLoadImageView = photoToLoad;
		}
		@Override
		public void run() {
			
			if(imageViewReused(photoToLoadImageView))
				return;
//          Bitmap bmp = getBitmap(photoToLoadImageView.mFlgScale,photoToLoadImageView.url,photoToLoadImageView.reqWidth,photoToLoadImageView.reqHeight);
            Bitmap bmp = getBitmapFromSDCard(photoToLoadImageView.mFlgScale,photoToLoadImageView.url,photoToLoadImageView.reqWidth,photoToLoadImageView.reqHeight);
            if(bmp==null)
            	return;
            memoryCache.put(photoToLoadImageView.url, bmp);
            if(imageViewReused(photoToLoadImageView))
            	return;
            BitmapDisplayerImageView bd=new BitmapDisplayerImageView(bmp, photoToLoadImageView);
            Activity a = (Activity) photoToLoadImageView.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}
	
	// Used to display bitmap in the UI thread
	class BitmapDisplayerImageView implements Runnable {
		Bitmap bitmap;
		PhotoToLoadImageView photoToLoad;

		public BitmapDisplayerImageView(Bitmap b, PhotoToLoadImageView p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			
			if(imageViewReused(photoToLoad))
				return;
            if(bitmap!=null)
            {
            	LogUtils.LogError("bitmap ******", "bitmap width ***** =====  "+bitmap.getWidth() + "  === "+bitmap.getHeight());
//            	bmCurrentDraw = Utilities.scaleToActualAspectRatio(bmCurrentDraw, mWidthImage, mHeightImage);
            	// landscape bitmap
            	if(bitmap.getWidth() > bitmap.getHeight())
            	{
            		 int mHeightImage = (int)mContext.getResources().getDimension(R.dimen.image_width_size_100dp);
            	     int mWidthImage = (int)mContext.getResources().getDimension(R.dimen.image_height_size_120dp);
            		 RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(mWidthImage, mHeightImage);
            		 params.addRule(RelativeLayout.CENTER_IN_PARENT);
            		 photoToLoad.imageView.setLayoutParams(params);
            		 
            	}
            	
            	photoToLoad.imageView.setImageBitmap(bitmap);
            	photoToLoad.imageView.setVisibility(View.VISIBLE);
            }
		}
	}
	
	public Bitmap getBitmapFromSDCard(boolean FlgScale,String url,int reqWidth, int reqHeight)
	{
		File f = new File(url);
        Log.e("URL URL *********", "URL URL ********* "+url);
        if(f.exists())
        {	
	        //from SD cache
	        Bitmap b = decodeFile(FlgScale,f,reqWidth,reqHeight);
	        if(b!=null)
	        {
	        	return b;
	        }
        }
        return null;
	}
		
	 public Bitmap getBitmap(boolean FlgScale,String url,int reqWidth, int reqHeight) 
	 {
	        File f=fileCache.getFile(url);
	        Log.e("URL URL *********", "URL URL ********* "+url);
	        if(f.exists())
	        {	
		        //from SD cache
		        Bitmap b = decodeFile(FlgScale,f,reqWidth,reqHeight);
		        if(b!=null)
		        {
		        	return b;
		        }
	        }
		 
	        //from web
	        try {
	            Bitmap bitmap=null;
	            URL imageUrl = new URL(url);
	            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
	            conn.setConnectTimeout(30000);
	            conn.setReadTimeout(30000);
	            conn.setInstanceFollowRedirects(true);
	            InputStream is=conn.getInputStream();
	            OutputStream os = new FileOutputStream(f);
//	            Logger.e("getBitmap 2222222222", "getBitmap 2222222222222");
	            Utils.CopyStream(is, os);
	            os.close();
	           
	            bitmap = decodeFile(FlgScale,f,reqWidth,reqHeight);
	            return bitmap;
	        } catch (Throwable ex){
	           ex.printStackTrace();
	           if(ex instanceof OutOfMemoryError)
	               memoryCache.clear();
	           return null;
	        }
	  }

	 public int calculateInSampleSize(
			   BitmapFactory.Options options, int reqWidth, int reqHeight) {
			  // Raw height and width of image
			  final int height = options.outHeight;
			  final int width = options.outWidth;
			  Log.e("width ***** height", "width ==== "+width+" height = "+height);
			  int inSampleSize = 1;
			  
			  if (height > reqHeight || width > reqWidth) {
			   if (width > height) {
			    inSampleSize = Math.round((float)height / (float)reqHeight); 
			   } else {
			    inSampleSize = Math.round((float)width / (float)reqWidth); 
			   } 
			  }
			  if(inSampleSize==0)
				  inSampleSize=1;
			  Log.e("inSampleSize", "inSampleSize ********* "+inSampleSize);
			  return inSampleSize; 
			 }
	 
	    //decodes image and scales it to reduce memory consumption
	    private Bitmap decodeFile(boolean FlgScale,File f,int reqWidth, int reqHeight){
	    	try {
	    		if(FlgScale)
	    		{
	    			InputStream in = null;
	    			try {
//	    				int IMAGE_MAX_SIZE = 1200000; // 1.2MP
	    				int IMAGE_MAX_SIZE = fixImageMaxeSize; // 1.2MP
	    			    in = new FileInputStream(f);
	    			    // Decode image size
	    			    BitmapFactory.Options o = new BitmapFactory.Options();
	    			    o.inJustDecodeBounds = true;
	    			    BitmapFactory.decodeStream(in, null, o);
	    			    in.close();

	    			    int scale = 1;
	    			    while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > 
	    			          IMAGE_MAX_SIZE) {
	    			       scale++;
	    			    }
	    			    Log.e("getBitmap", "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

	    			    Bitmap b = null;
	    			    in = new FileInputStream(f);
	    			    if (scale > 1) {
	    			        scale--;
	    			        // scale to max possible inSampleSize that still yields an image
	    			        // larger than target
	    			        o = new BitmapFactory.Options();
	    			        o.inSampleSize = scale;
	    			        b = BitmapFactory.decodeStream(in, null, o);

	    			        // resize to desired dimensions
	    			        int height = b.getHeight();
	    			        int width = b.getWidth();
	    			        Log.e("getBitmap", "1th scale operation dimenions - width: " + width + ",height: " + height);

	    			        double y = Math.sqrt(IMAGE_MAX_SIZE
	    			                / (((double) width) / height));
	    			        double x = (y / height) * width;

	    			        Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x, 
	    			           (int) y, true);
	    			        b.recycle();
	    			        b = scaledBitmap;
	    			        System.gc();
	    			    } else {
	    			        b = BitmapFactory.decodeStream(in);
	    			    }
	    			    in.close();

	    			    Log.e("getBitmap", "bitmap size - width: " +b.getWidth() + ", height: " + b.getHeight());
	    			    return b;
	    			} catch (Exception e) {
	    			    Log.e("getBitmap", e.getMessage(),e);
	    			    return null;
	    			}
	    		}
	    		 //decode with inSampleSize
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=1;
//	            Logger.e("FlgScale", "FlgScale FlgScale 222222222 ==== "+o2.inSampleSize);
	            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    		
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return null;
	    }
	
	  //decodes image and scales it to reduce memory consumption
	    private Bitmap decodeFileScaleSize(int size,File f){
	    	try {
	    		 //decode with inSampleSize
	            BitmapFactory.Options o2 = new BitmapFactory.Options();
	            o2.inSampleSize=size;
	            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
			} catch (Exception e) {
				e.printStackTrace();
			}
	        return null;
	    }
	    
	 boolean imageViewReused(PhotoToLoadImageView photoToLoad){
	        String tag=imageViews.get(photoToLoad.imageView);
	        if(tag==null || !tag.equals(photoToLoad.url))
	            return true;
	        return false;
	 }
		
	 public void clearCache() {
        memoryCache.clear();
        imageViews.clear();
	 }
	 
	 public void stopExecutorService()
	 {
    	if(executorService!=null)
    	{
    		executorService.shutdown();
    	}
	 }

	public FileCache getFileCache() {
		return fileCache;
	}

	public void setFileCache(FileCache fileCache) {
		this.fileCache = fileCache;
	}
	
	
}
