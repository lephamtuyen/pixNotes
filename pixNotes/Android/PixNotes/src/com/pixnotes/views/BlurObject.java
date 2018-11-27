package com.pixnotes.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.datastorage.PreferenceConnector;
import com.pixnotes.managers.BlurManager;

public class BlurObject extends CustomerShapeView {
	
	private ArrayList<DotObject> mListDot;
	private final int FIXDOT = 8;
	private int mColor = 0;
	private int mStrokeWith = 0;
	private Paint mPaintDebug;
	private boolean isDebug;
	private float alphaRectangle;
	private Bitmap _blurBitmap;
	private int mOpacity;
	private float xMargin;
	private float yMargin;
	private BlurManager blurManager;
	private float translateX;
	private float translateY;
	
	public BlurObject(Context mContext, BlurManager manager,
			int mType, float xMargin, float yMargin, float x, float y, float w, float h, Paint mPaint){
		super(mContext,mType, x, y, w, h, mPaint);
		this.blurManager = manager;
		this.mType = mType;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.mPaint = new Paint(mPaint);
		this.xMargin = xMargin;
		this.yMargin = yMargin;
		this.mOpacity = PreferenceConnector.readInteger(mContext, Configs.FLAG_BLUR_OPACTIY, Configs.MIN_OPACITY_BLUR);
		initialBlur();
	}
	
	private void initialBlur() {
		this.mPaint.setPathEffect(new CornerPathEffect(0));
		reRenderBlurObject();
		
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
	
	public void setDebug(boolean isDebug,float alphaRectangle)
	{
		this.isDebug = isDebug;
		mPaintDebug = new Paint();
		mPaintDebug.setColor(Color.GREEN);
		mPaintDebug.setStyle(Paint.Style.STROKE);
		mPaintDebug.setStrokeWidth(10);
		this.alphaRectangle = alphaRectangle;
	}
	public BlurObject()
	{
		
	}
	
	public void hideBlurObj(){
		if(_blurBitmap != null){
			_blurBitmap.recycle();
			_blurBitmap = null;
		}
	}
	
	public void showBlurObj(){
		reRenderBlurObject();
	}
	
	@Override
	public BlurObject copyCustomerView() {
		BlurObject blur = new BlurObject();
		blur._shapeId = this._shapeId;
		blur.mContext = this.mContext;
		blur.blurManager = this.blurManager;
		blur.x = this.x;
		blur.y = this.y;
		blur.w = this.w;
		blur.h = this.h;
		blur.xMargin = this.xMargin;
		blur.yMargin = this.yMargin;
		blur.mPaint = this.mPaint;
		blur.mType = this.mType;
		blur.mListDot = this.mListDot;
		blur.mOpacity = this.mOpacity;
		return blur;
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
		parcel.writeInt(mOpacity);
		
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
		mOpacity = parcel.readInt();
		
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
	
	public static final Parcelable.Creator<BlurObject> CREATOR = new Parcelable.Creator<BlurObject>() {
		@Override
		public BlurObject createFromParcel(Parcel in) {
			BlurObject blurObj = new BlurObject();
			try {
				blurObj.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return blurObj;
		}
		@Override
		public BlurObject[] newArray(int size) {
			return new BlurObject[size];
		}
	};
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(_blurBitmap != null)
		{
			canvas.drawBitmap(_blurBitmap, x + translateX, y+translateY, mPaint);
		}
		
		if(isFocus)
		{
			for(int i = 0 ; i < mListDot.size() ; i++)
			{
				mListDot.get(i).onDraw(canvas);
			}
		}
	}
	
	public void reRenderBlurObject(){
		if(_blurBitmap == null || _blurBitmap.isRecycled()) {
			createBitmap(x, y, w, h);
		}
	}
	
	private void createBitmap(float x,float y,float w, float h) {
		translateX = translateY = 0;
		
		if (x < xMargin){
			translateX = xMargin - x;
			x = xMargin;
		}
		if(y < yMargin){
			translateY = yMargin - y;
			y = yMargin;
		}
		
		Bitmap largeBlurBitmap = blurManager.getBlurBitmapWithOpacity(mOpacity);
		
		if(w-xMargin > largeBlurBitmap.getWidth()){
			w = largeBlurBitmap.getWidth() + xMargin;
		}
		
		if(h-yMargin > largeBlurBitmap.getHeight()){
			h = largeBlurBitmap.getHeight() + yMargin;
		}
		
		try {
			_blurBitmap = Bitmap.createBitmap(largeBlurBitmap, (int)(x-xMargin), (int)(y-yMargin), (int)(w-x), (int)(h-y));
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
	}
	
	public void resizeBlurBitmap(float x,float y,float w, float h)
	{
		if(_blurBitmap != null)
		{
			_blurBitmap.recycle();
			_blurBitmap = null;
		}
		createBitmap(x, y, w, h);
	}
	
	public void setOpacity(int opacity){
		this.mOpacity = opacity;
		reRenderBlurObject();
	}
	
//	
//	public void clearBitmap()
//	{
//		if(blurBitmap != null)
//		{
//			blurBitmap.recycle();
//			blurBitmap = null;
//		}
////		if(mOriginalBlurBitmap != null)
////		{
////			mOriginalBlurBitmap.recycle();
////			mOriginalBlurBitmap = null;
////		}
//		if(mBlurOriginalBitmap != null)
//		{
//			mBlurOriginalBitmap.recycle();
//			mBlurOriginalBitmap = null;
//		}
//	}
	
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

	public BlurManager getBlurManager() {
		return blurManager;
	}

	public void setBlurManager(BlurManager blurManager) {
		this.blurManager = blurManager;
	}

	public float getxMargin() {
		return xMargin;
	}

	public void setxMargin(float xMargin) {
		this.xMargin = xMargin;
	}

	public float getyMargin() {
		return yMargin;
	}

	public void setyMargin(float yMargin) {
		this.yMargin = yMargin;
	}
}
