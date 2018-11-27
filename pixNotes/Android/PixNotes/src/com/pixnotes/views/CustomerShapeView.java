package com.pixnotes.views;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.configsapp.Configs.ShapeAction;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class CustomerShapeView implements Parcelable{
	
	public int _shapeId;	
	public float x,y,w,h;
	public Paint mPaint;
	public int mType;
	public boolean isFocus;
	public Path mPath;
	public Context mContext;
	
	//check for this sharp is visible or not
	public boolean isVisible = true;
	public Configs.ShapeAction _action;
	
	public CustomerShapeView(Context mContext,int mType,float x,float y,float w,float h,Paint mPaint)
	{
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.mPaint = new Paint(mPaint);
		this.mPaint.setPathEffect(new CornerPathEffect(0));
		this.mContext = mContext;
		_action = ShapeAction.AddNewShape;
		Log.e("CustomerShapeView", "CustomerShapeView xxxxxxxx === "+x);
	}
	
	public CustomerShapeView()
	{
		
	}
	
	public CustomerShapeView copyCustomerView()
	{
		//CustomerShapeView customerShape = new CustomerShapeView(mContext,mType, x, y, w, h, mPaint);
		return null;
	}
	
	public void onDraw(Canvas canvas) {
		
	}

	@Override
	public int describeContents() {
		
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(_shapeId);
		
	}
	
	public void scale (float xRatio, float yRatio){
		
	}
	
	public void init(Parcel parcel)
	{
		_shapeId = parcel.readInt();
	}
	
//	public static final Parcelable.Creator<CustomerShapeView> CREATOR = new Parcelable.Creator<CustomerShapeView>() {
//		@Override
//		public CustomerShapeView createFromParcel(Parcel in) {
//			CustomerShapeView mCustomerShapeView = new CustomerShapeView();
//			try {
//				mCustomerShapeView.init(in);
//			} catch (Throwable t) {
//				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
//				return null;
//			}
//			return mCustomerShapeView;
//		}
//		@Override
//		public CustomerShapeView[] newArray(int size) {
//			return new CustomerShapeView[size];
//		}
//	};
	
	
}
