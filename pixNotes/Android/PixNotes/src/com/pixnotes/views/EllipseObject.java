package com.pixnotes.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pixnotes.configsapp.Configs;

public class EllipseObject extends CustomerShapeView {
	
	private ArrayList<DotObject> mListDot;
	private final int FIXDOT = 8;
	private int mColor = 0;
	private int mStrokeWith = 0;
	private RectF rectF ;
	public EllipseObject(Context mContext,int mType, float x, float y, float w, float h, Paint mPaint) {
		super(mContext,mType, x, y, w, h, mPaint);
		Log.e("EllipseObject", " mType === "+mType);
		this.mType = mType;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.mPaint = new Paint(mPaint);
		this.mPaint.setPathEffect(new CornerPathEffect(0));
		rectF = new RectF(x, y, w, h);
		
		mListDot = new ArrayList<DotObject>();
		for(int i = 0 ; i < FIXDOT ; i++)
		{
			DotObject dot = null;
			switch (i) {
			case Configs.idDotOne:
				dot = new DotObject(i, x  , y  );
				break;
			case Configs.idDotTwo:
				dot = new DotObject(i, x + (w-x)/2 , y  );
				break;
			case Configs.idDotThree:
				dot = new DotObject(i, w , y  );
				break;
			case Configs.idDotFour:
				dot = new DotObject(i, w  , y + (h-y)/2  );
				break;
			case Configs.idDotFive:
				dot = new DotObject(i, w  , h );
				break;
			case Configs.idDotSix:
				dot = new DotObject(i, x + (w-x)/2 , h  );
				break;
			case Configs.idDotSeven:
				dot = new DotObject(i, x  , h  );
				break;
			case Configs.idDotEight:
				dot = new DotObject(i, x  , y + (h-y)/2 );
				break;
			}
			mListDot.add(dot);
		}
	}
	
	public EllipseObject()
	{
		
	}
	
	@Override
	public EllipseObject copyCustomerView() {
		EllipseObject ellipse = new EllipseObject(mContext,mType, getRectF().left, getRectF().top, getRectF().right, getRectF().bottom, mPaint);
		ellipse._shapeId = this._shapeId;
		return ellipse;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		parcel.writeInt(mType);
		parcel.writeFloat(x);
		parcel.writeFloat(y);
		parcel.writeFloat(w);
		parcel.writeFloat(h);
		parcel.writeInt(mPaint.getColor());
		parcel.writeInt((int)mPaint.getStrokeWidth());
		
	}
	
	@Override
	public void init(Parcel parcel) {
		// TODO Auto-generated method stub
		super.init(parcel);
		mType = parcel.readInt();
		x = parcel.readFloat();
		y = parcel.readFloat();
		w= parcel.readFloat();
		h = parcel.readFloat();
		mColor = parcel.readInt();
		mStrokeWith = parcel.readInt();
		
		
		rectF = new RectF(x, y, w, h);
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(mColor);
		mPaint.setStrokeWidth(mStrokeWith);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		
		mListDot = new ArrayList<DotObject>();
		for(int i = 0 ; i < FIXDOT ; i++)
		{
			DotObject dot = null;
			switch (i) {
			case Configs.idDotOne:
				dot = new DotObject(i, x  , y  );
				break;
			case Configs.idDotTwo:
				dot = new DotObject(i, x + (w-x)/2 , y );
				break;
			case Configs.idDotThree:
				dot = new DotObject(i, w , y  );
				break;
			case Configs.idDotFour:
				dot = new DotObject(i, w  , y + (h-y)/2  );
				break;
			case Configs.idDotFive:
				dot = new DotObject(i, w  , h );
				break;
			case Configs.idDotSix:
				dot = new DotObject(i, x + (w-x)/2 , h  );
				break;
			case Configs.idDotSeven:
				dot = new DotObject(i, x  , h  );
				break;
			case Configs.idDotEight:
				dot = new DotObject(i, x  , y + (h-y)/2 );
				break;
			}
			mListDot.add(dot);
		}
	}
	
	public static final Parcelable.Creator<EllipseObject> CREATOR = new Parcelable.Creator<EllipseObject>() {
		@Override
		public EllipseObject createFromParcel(Parcel in) {
			EllipseObject mEllipseObj = new EllipseObject();
			try {
				mEllipseObj.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return mEllipseObj;
		}
		@Override
		public EllipseObject[] newArray(int size) {
			return new EllipseObject[size];
		}
	};
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawOval(rectF, mPaint);
		if(isFocus)
		{
			for(int i = 0 ; i < mListDot.size() ; i++)
			{
				mListDot.get(i).onDraw(canvas);
			}
		}
	}
	
	public void initNewPointPosition(float x,float y,float w, float h)
	{
		for(int i = 0 ; i < mListDot.size() ; i++)
		{
			DotObject dot = null;
			
			float xNewPosition = 0;
			float yNewPosition = 0;
			switch (i) {
			case Configs.idDotOne:
				xNewPosition = x ; 
				yNewPosition = y ;
				break;
			case Configs.idDotTwo:
				xNewPosition = x + (w-x)/2 ; 
				yNewPosition = y ;
				break;
			case Configs.idDotThree:
				xNewPosition = w ; 
				yNewPosition = y ;
				break;
			case Configs.idDotFour:
				xNewPosition = w ; 
				yNewPosition = y + (h-y)/2 ;
				break;
			case Configs.idDotFive:
				xNewPosition = w ; 
				yNewPosition =  h ;
				break;
			case Configs.idDotSix:
				xNewPosition = x + (w-x)/2 ; 
				yNewPosition =  h ;
				break;
			case Configs.idDotSeven:
				xNewPosition = x ; 
				yNewPosition =  h ;
				break;
			case Configs.idDotEight:
				xNewPosition = x ; 
				yNewPosition =  y + (h-y)/2 ;
				break;
			}
			
			mListDot.get(i).setmX(xNewPosition);
			mListDot.get(i).setmY(yNewPosition);
		}
	}
	
	public ArrayList<DotObject> getmListDot() {
		return mListDot;
	}

	public void setmListDot(ArrayList<DotObject> mListDot) {
		this.mListDot = mListDot;
	}

	public RectF getRectF() {
		return rectF;
	}

	public void setRectF(RectF rectF) {
		this.rectF = rectF;
	}
	
	
}