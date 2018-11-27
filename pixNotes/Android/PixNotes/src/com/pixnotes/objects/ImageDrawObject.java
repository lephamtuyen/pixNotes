package com.pixnotes.objects;

import java.util.ArrayList;

import com.pixnotes.views.CustomerShapeView;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class ImageDrawObject implements Parcelable{
	
	private int isSelect = 0;
	private String originalImageGalleryPath = "";
	private String originalImagePath = "";
	private String EditImagePath = "";
	private ArrayList<CustomerShapeView> listRootShape = new ArrayList<CustomerShapeView>();
	private String mDescription = "";
	public ImageDrawObject()
	{
		
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		parcel.writeInt(isSelect);
		parcel.writeString(originalImageGalleryPath);
		parcel.writeString(originalImagePath);
		parcel.writeString(EditImagePath);
		parcel.writeList(listRootShape);
		parcel.writeString(mDescription);
		Log.e("ImageDrawObject", "writeToParcel");
	}
	
	public void init(Parcel parcel)
	{
		isSelect = parcel.readInt();
		originalImageGalleryPath = parcel.readString();
		originalImagePath = parcel.readString();
		EditImagePath = parcel.readString();
		listRootShape = parcel.readArrayList(CustomerShapeView.class.getClassLoader());
		mDescription = parcel.readString();
		Log.e("ImageDrawObject", "init");
	}
	
	public static final Parcelable.Creator<ImageDrawObject> CREATOR = new Parcelable.Creator<ImageDrawObject>() {
		@Override
		public ImageDrawObject createFromParcel(Parcel in) {
			ImageDrawObject mImageDrawObject = new ImageDrawObject();
			try {
				mImageDrawObject.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return mImageDrawObject;
		}
		@Override
		public ImageDrawObject[] newArray(int size) {
			return new ImageDrawObject[size];
		}
	};
	
	public String getOriginalImagePath() {
		return originalImagePath;
	}

	public void setOriginalImagePath(String originalImagePath) {
		this.originalImagePath = originalImagePath;
	}

	public String getEditImagePath() {
		return EditImagePath;
	}

	public void setEditImagePath(String editImagePath) {
		EditImagePath = editImagePath;
	}

	public ArrayList<CustomerShapeView> getListRootShape() {
		return listRootShape;
	}

	public void setListRootShape(ArrayList<CustomerShapeView> listRootShape) {
		this.listRootShape = listRootShape;
	}

	public String getOriginalImageGalleryPath() {
		return originalImageGalleryPath;
	}

	public void setOriginalImageGalleryPath(String originalImageGalleryPath) {
		this.originalImageGalleryPath = originalImageGalleryPath;
	}

	public int getIsSelect() {
		return isSelect;
	}

	public void setIsSelect(int isSelect) {
		this.isSelect = isSelect;
	}

	public String getmDescription() {
		return mDescription;
	}

	public void setmDescription(String mDescription) {
		this.mDescription = mDescription;
	}
	
}
