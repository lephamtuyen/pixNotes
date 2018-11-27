package com.pixnotes.objects;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class FlaotPoint implements Parcelable{
	private float x, y;

	public FlaotPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public FlaotPoint()
	{
		
	}
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		
		parcel.writeFloat(x);
		parcel.writeFloat(y);
		
	}
	

	public void init(Parcel parcel) {
		
		x = parcel.readFloat();
		y = parcel.readFloat();
		
	}
	
	public static final Parcelable.Creator<FlaotPoint> CREATOR = new Parcelable.Creator<FlaotPoint>() {
		@Override
		public FlaotPoint createFromParcel(Parcel in) {
			FlaotPoint flaotPoint = new FlaotPoint();
			try {
				flaotPoint.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return flaotPoint;
		}
		@Override
		public FlaotPoint[] newArray(int size) {
			return new FlaotPoint[size];
		}
	};
	
}