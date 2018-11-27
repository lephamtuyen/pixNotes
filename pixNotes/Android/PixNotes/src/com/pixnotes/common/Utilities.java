package com.pixnotes.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.codec.binary.Base64;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.ExternalStorage;
import com.pixnotes.objects.DeviceObject;

public class Utilities {

	/**
	 * Recycle bitmap inside image view
	 * 
	 * @author ThoLH
	 */
	public static void recycleImageView(ImageView imgImage,boolean isCallGC) {
		if (imgImage != null) {
			Drawable drawable = imgImage.getDrawable();
			if (drawable instanceof BitmapDrawable) {

				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				if (bitmap != null) {
					bitmap.recycle();
					bitmap = null;
					imgImage.setImageBitmap(null);
					imgImage.setImageDrawable(null);
				}

			}
			if(isCallGC)
			{
				System.gc();
			}
		}
	}

	public static void recycleView(View view) {
		if (view != null) {
			Drawable drawable = view.getBackground();
			if (drawable instanceof BitmapDrawable) {

				Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
				if (bitmap != null) {
					bitmap.recycle();
					bitmap = null;
					view.setBackgroundDrawable(null);
				}
			}
			System.gc();
		}
	}

	public static void recycleBitmap(Bitmap bmp) {
		if (bmp != null) {
			bmp.recycle();
			bmp = null;
		}
		System.gc();
	}

	/**
	 * Format an Integer to String (#,###)
	 * 
	 * @param number
	 * @return
	 */
	public static String formatIntegerWithDecimal(int number) {
		DecimalFormat threeDec = new DecimalFormat("#,###");
		return threeDec.format(number) + "";
	}

	/**
	 * Parse a string of date to string EEEE,MMMM dd,yyyy
	 * 
	 * @param strDate
	 * @return
	 */
	// @SuppressLint("SimpleDateFormat")
	public static String formatDate(String strDate) {
		String result = "";
		if (strDate != null) {
			try {
				String temp = strDate;
				if (strDate.length() > 10) {
					temp = strDate.substring(0, 10);
					Date thedate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(temp);
					SimpleDateFormat format = new SimpleDateFormat("EEEE,MMMM dd,yyyy");
					result = format.format(thedate);
				}
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public static boolean isNetworkAvailable(Context mContext) {
		ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;
		return true;
	}

	public static final void sendEmail(Context context, String body, String title) {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_SUBJECT, title);
		i.putExtra(Intent.EXTRA_TEXT, body);
		try {
			context.startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
		}
	}
	
	public static DeviceObject getDeviceObject(Activity mActivity)
	{
		DeviceObject deviceObj = new DeviceObject();
		try {
			DisplayMetrics metrics = new DisplayMetrics();
			mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
			deviceObj.setmSystemVersion(android.os.Build.VERSION.RELEASE);
			deviceObj.setmModel(android.os.Build.MODEL);
			deviceObj.setmResolution(metrics.widthPixels +" X "+metrics.heightPixels);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deviceObj;
	}
	
	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
	    int width = bm.getWidth();
	    int height = bm.getHeight();
	    Log.e("width", "width ====== "+width+ " height === "+height);
	    float scaleWidth = ((float) newWidth) / width;
	    float scaleHeight = ((float) newHeight) / height;
	    // CREATE A MATRIX FOR THE MANIPULATION
	    Matrix matrix = new Matrix();
	    // RESIZE THE BIT MAP
	    matrix.postScale(scaleWidth, scaleHeight);

	    // "RECREATE" THE NEW BITMAP
	    Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
	    return resizedBitmap;
	}
	
	public static Bitmap decodeFile(boolean FlgScale,File f)
	{
    	try {
    		if(FlgScale)
    		{
    			InputStream in = null;
    			try {
    									 
    				int IMAGE_MAX_SIZE = 150000; // 1.2MP
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
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
        return null;
	}
		
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and
	        // width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will
	        // guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	
	public static String getImageDrawNameEncript(String folderName) {
		return java.util.UUID.nameUUIDFromBytes(folderName.getBytes()).toString();
	}
	
	public static String getImageDrawFileNamePath(String folderProjectName,String filename) {
		return ExternalStorage.getRootPathSDCard()+Configs.ROOT_FOLDER_NAME+"/"+folderProjectName+"/" + Utilities.getImageDrawNameEncript(filename);
	}
	
	
	
	public static void createFolderProject(String folderProjectName)
	{
		if(!ExternalStorage.isForderExited(Configs.ROOT_FOLDER_NAME+"/"+folderProjectName))
		{
			ExternalStorage.CreateForder(Configs.ROOT_FOLDER_NAME+"/"+folderProjectName);
		}
	}
	
	public static void copyFileUsingFileStreams(File source, File dest)
	        throws IOException {
	
	    InputStream input = null;
	
	    OutputStream output = null;

	    try {
	
	        input = new FileInputStream(source);

	        output = new FileOutputStream(dest);
	
	        byte[] buf = new byte[1024];
	
	        int bytesRead;
	
	        while ((bytesRead = input.read(buf)) > 0) {
	
	            output.write(buf, 0, bytesRead);

	        }
	    } finally {
	
	        input.close();

	        output.close();

	    }
	}

	public static Bitmap getBitmapFromView(View view) {
		Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(returnedBitmap);
		Drawable bgDrawable = view.getBackground();
		if (bgDrawable != null)
			bgDrawable.draw(canvas);
		else
			canvas.drawColor(Color.WHITE);
		view.draw(canvas);
		return returnedBitmap;
	}
	
	public static void saveToSdcardPNG(File file ,Bitmap bitmap) {
		OutputStream outStream = null;
		try {
			outStream = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
			outStream.flush();
			outStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static int convertDpToPixel(Context mContext,float dp) {
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
	}
	
	public static Bitmap RotateBitmap(Bitmap source, float angle)
	{
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	public static int checkDeviceAutoRotateBitmap(String photoPath)
	{
		int angle = 0;
		try {
			ExifInterface ei = new ExifInterface(photoPath);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			switch(orientation) {
			    case ExifInterface.ORIENTATION_ROTATE_90:
			    	Log.e("ORIENTATION_ROTATE_90", "ORIENTATION_ROTATE_90");
			    	angle = 90;
			        break;
			    case ExifInterface.ORIENTATION_ROTATE_180:
			    	Log.e("ORIENTATION_ROTATE_180", "ORIENTATION_ROTATE_180");
			    	angle = 180;
			        break;
			    default:
			    	Log.e("default", "default default default default");
			       break;
			    // etc.
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return angle ;
	}
	
//	public static Bitmap scaleToActualAspectRatio(Bitmap bitmap,int deviceWidth,int deviceHeight) 
//	{
//		 if (bitmap != null) {
//		 boolean flag = true;
//		 
//		int bitmapHeight = bitmap.getHeight(); // 563
//		int bitmapWidth = bitmap.getWidth(); // 900
//		 
//		// aSCPECT rATIO IS Always WIDTH x HEIGHT rEMEMMBER 1024 x 768
//		 
//		if (bitmapWidth > deviceWidth) 
//		{
//			 flag = false;
//			// scale According to WIDTH
//			 int scaledWidth = deviceWidth;
//			 int scaledHeight = (scaledWidth * bitmapHeight) / bitmapWidth;
//			try {
//				 if (scaledHeight > deviceHeight)
//				 scaledHeight = deviceHeight;
//				 
//				 bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
//				 scaledHeight, true);
//			 }
//			catch (Exception e) 
//			{
//				e.printStackTrace();
//			}
//		}
//		 
//		if (flag) 
//		{
//			 if (bitmapHeight > deviceHeight)
//			 {
//			 // scale According to HEIGHT
//			 int scaledHeight = deviceHeight;
//			 int scaledWidth = (scaledHeight * bitmapWidth)
//			 / bitmapHeight;
//			 
//				try {
//				 if (scaledWidth > deviceWidth)
//				 scaledWidth = deviceWidth;
//				 
//				bitmap = Bitmap.createScaledBitmap(bitmap, scaledWidth,
//				 scaledHeight, true);
//				 } catch (Exception e) {
//				 e.printStackTrace();
//				 }
//			 }
//		 }
//		 }
//		 return bitmap;
//	}
	
	public static Bitmap scaleToActualAspectRatio(Bitmap bitmap,int deviceWidth,int deviceHeight) 
	 {
	  if(bitmap == null)
	   return null;
	  
	  int bitmapHeight = bitmap.getHeight(); // 563
	  int bitmapWidth = bitmap.getWidth(); // 900
	  
	  int scaleWidth, scaleHeight;
	  
	  if (deviceWidth/deviceHeight >= bitmapWidth/bitmapHeight){
	   //resize according height
	   scaleHeight = deviceHeight;
	   scaleWidth = scaleHeight*bitmapWidth/bitmapHeight;
	  } else{
	   //resize according width
	   scaleWidth = deviceWidth;
	   scaleHeight = scaleWidth*bitmapHeight/bitmapWidth;
	  }
	  
	  try {
	   bitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth,
	     scaleHeight, true);
	  } catch (Exception e) {
	   // TODO: handle exception
	   e.printStackTrace();
	  }
	  
	  return bitmap;
	 }
	
	public static String encodeFileToBase64Binary(String fileName)
			throws IOException {
 
		File file = new File(fileName);
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.encodeBase64(bytes);
		String encodedString = new String(encoded);
		Log.e("encodeFileToBase64Binary", "encodeFileToBase64Binary ==== "+encodedString);
		return encodedString;
	}
 
	public static byte[] loadFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);
	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	    byte[] bytes = new byte[(int)length];
	    
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }
 
	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }
 
	    is.close();
	    return bytes;
	}
	
}
