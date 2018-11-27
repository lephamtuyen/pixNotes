package com.pixnotes.objects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.ExternalStorage;

public class ManagerImageObject implements Parcelable 
{
	private ArrayList<ImageDrawObject> listChooseDrawObject = new ArrayList<ImageDrawObject>();
	private int mFirstCreateProject;
	public ManagerImageObject()
	{
		
	}
	
	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		Log.e("listChooseDrawObject", "listChooseDrawObject ======= "+listChooseDrawObject.size());
		parcel.writeList(listChooseDrawObject);
		parcel.writeInt(mFirstCreateProject);
	}
	
	public void init(Parcel parcel)
	{
		listChooseDrawObject = parcel.readArrayList(ImageDrawObject.class.getClassLoader());
		mFirstCreateProject = parcel.readInt();
	}
	public void writeToFile(Context context,String projectName) {
		try {
			if(context.deleteFile(projectName+Configs.TYPE_FORMAT_PROJECT_NAME))
			{
				Log.e("context.deleteFile", "context.deleteFile context.deleteFile");
			}
			FileOutputStream fos = context.openFileOutput(projectName+Configs.TYPE_FORMAT_PROJECT_NAME, Context.MODE_PRIVATE);
			Parcel p = Parcel.obtain();
			writeToParcel(p, 0);
			byte[] data = p.marshall();
			fos.write(data);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			
		}
	}
	
	public static ManagerImageObject readFromFile(Context context,String projectName) {
		Parcel p = Parcel.obtain();
		try {
				File file = context.getFileStreamPath(projectName+Configs.TYPE_FORMAT_PROJECT_NAME);
				FileInputStream fis = new FileInputStream(file);
				byte[] data = new byte[(int) file.length()];
				fis.read(data);
				fis.close();
				p.unmarshall(data, 0, data.length);
				p.setDataPosition(0);
				return CREATOR.createFromParcel(p);
			
		} catch (Exception e) {
			Log.e("Edition.readFromFile", "got IOException readFromFile() Failed");
		}
		return null;
	}
	
	public static final Parcelable.Creator<ManagerImageObject> CREATOR = new Parcelable.Creator<ManagerImageObject>() {
		@Override
		public ManagerImageObject createFromParcel(Parcel in) {
			ManagerImageObject mManagerObject = new ManagerImageObject();
			try {
				mManagerObject.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return mManagerObject;
		}
		@Override
		public ManagerImageObject[] newArray(int size) {
			return new ManagerImageObject[size];
		}
	};
	public ArrayList<ImageDrawObject> getListChooseDrawObject() {
		return listChooseDrawObject;
	}

	public void setListChooseDrawObject(
			ArrayList<ImageDrawObject> listChooseDrawObject) {
		this.listChooseDrawObject = listChooseDrawObject;
	}

	public int getmFirstCreateProject() {
		return mFirstCreateProject;
	}

	public void setmFirstCreateProject(int mFirstCreateProject) {
		this.mFirstCreateProject = mFirstCreateProject;
	}
	
}
