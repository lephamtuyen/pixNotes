package com.pixnotes;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.pixnotes.InsertImageForProjectActivity.LoadingCopyChooseImage;
import com.pixnotes.OpenProjectActivity.ItemProjectName;
import com.pixnotes.adapter.Action;
import com.pixnotes.common.Utilities;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.objects.ImageDrawObject;
import com.pixnotes.objects.ManagerImageObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class NewProjectActivity extends Activity implements OnClickListener {

	private ImageView img_capture;
	private ImageView img_insert_photo;
	private EditText edt_projectname;
	private Uri fileUri; // file url to store image/video
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
	private int RESULT_LOAD_IMAGE = 1;
	private static final String IMAGE_DIRECTORY_NAME = "CaptureImageFormCamera";
	private String mProjectName = "";
	private String[] arrayProjectName ;
	public ManagerImageObject mManagerObject;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.insert_image_for_project);
		
		loadProject();
		initComponent();
		setComponentListener();
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
	
	private void initComponent()
	{
		img_capture = (ImageView) findViewById(R.id.img_capture);
		img_insert_photo = (ImageView) findViewById(R.id.img_insert_photo);
		edt_projectname = (EditText)findViewById(R.id.edt_projectname);
	}
	
	public void setComponentListener()
	{
		img_capture.setOnClickListener(this);
		img_insert_photo.setOnClickListener(this);
	}
	
	public void actionCaptureImage()
	{
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		// start the image capture Intent
		startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_capture:
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
				actionCaptureImage();
			}
			
			break;
		case R.id.img_insert_photo:
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
				actionInsetPhoto();
			}
			break;
		}
	}

	public void copyImageToSDCard(String picturePath)
	{
		if(mManagerObject.getListChooseDrawObject().size() == 0)
		{   
			copyImageFormPath(picturePath);
		}
		else
		{
			boolean flagCopy = true;
			for(int i = 0 ; i < mManagerObject.getListChooseDrawObject().size() ; i++)
			{
				String imageOriginalPath = mManagerObject.getListChooseDrawObject().get(i).getOriginalImageGalleryPath();
				if(imageOriginalPath.contains(picturePath))
				{
					flagCopy = false;
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
		Utilities.createFolderProject(mProjectName);
		// copy file
		File fileSource = new File(picturePath);
		String imagePathSDCard = Utilities.getImageDrawFileNamePath(mProjectName, picturePath + Configs.FLAG_ORIGINAL_FILE_NAME);
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
		mManagerObject.getListChooseDrawObject().add(imageDraw);
		mManagerObject.setmFirstCreateProject(Configs.FIRST_TIME_CREATE_PROJECT);
		
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
			
			saveData();
			reloadNewProject();
			if (mProgressDialog != null && mProgressDialog.isShowing())
				mProgressDialog.dismiss();
		}
	}
	
	private void saveData()
	{
		// save data 
		mManagerObject.writeToFile(this,mProjectName);
		mManagerObject = null;
		System.gc();
	}
	
	private void reloadNewProject()
	{
		try {
//			String className = MainActivity.class.getSimpleName();
//			String packageName = getApplicationContext().getPackageName();
//			if (className.indexOf(packageName) == -1) {
//				className = packageName + "." + className;
//			}
//			Intent intent = new Intent(getApplicationContext(), Class.forName(className));
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			intent.putExtra(Configs.BUNDLE_PROJECT_NAME, mProjectName);
//			startActivity(intent);
			Log.e("mProjectName", "mProjectName ======= "+mProjectName);
//			setResult(MainActivity.getInstance().RESULT_NEW_PROJECT, getIntent().putExtra(Configs.BUNDLE_PROJECT_NAME, mProjectName));
//			// finish two activity
//			finish();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	mProjectName = edt_projectname.getText().toString();
    	
    	mManagerObject = ManagerImageObject.readFromFile(this,mProjectName);
		if(mManagerObject == null)
		{
			mManagerObject = new ManagerImageObject();
		}
    	
		if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
			
			String[] all_path = data.getStringArrayExtra("all_path");
			if(all_path != null && all_path.length > 0)
			{
				new LoadingCopyChooseImage(this, all_path).execute();
			}
		}
		else if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// successfully captured the image
				// display it in image view
				copyImageToSDCard(fileUri.getPath());
				saveData();
				reloadNewProject();
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				
			} else {
				// failed to capture image
				
			}
		} 
    }
}
