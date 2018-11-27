package com.pixnotes.views;

import java.util.ArrayList;

import android.R.color;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.objects.FlaotPoint;
import com.pixnotes.objects.ImageDrawObject;
import com.pixnotes.utils.LogUtils;

public class FreeStyleObject extends CustomerShapeView {
	
	public Path mPathFreeStyle;
	private int mColor = 0;
	private int mStrokeWith = 0;
//	private FlaotPoint[] arrayPointPath ; 
	private ArrayList<FlaotPoint> listPointBody = new ArrayList<FlaotPoint>();
	private ArrayList<FlaotPoint> listPointMainPath = new ArrayList<FlaotPoint>();
	private RectF RectFBodyPath ;
	private int alphaResize = 40;
	private int distanceTouchPoint = 30;
//	private Paint mPaintDebug;
	private Paint mPaintFocus;
	private ArrayList<DotObject> mListDot;
	private final int FIXDOT = 8;
	private FlaotPoint[] arrayPointPathFirstCreate;
	public FreeStyleObject(Context mContext,int mType,float x,float y,float w,float h,Path mPath,Paint mPaint)
	{
		super(mContext,mType, x, y, w, h, mPaint);
		this.mType = mType;
		if(mPathFreeStyle == null)
		{
			mPathFreeStyle = new Path(mPath);
		}
		this.mPaint = new Paint(mPaint);
		this.mPaint.setPathEffect(new CornerPathEffect(0));
		
		RectFBodyPath = new RectF();
		mPathFreeStyle.computeBounds(RectFBodyPath, true);
		
		listPointBody.removeAll(listPointBody);
		listPointMainPath.removeAll(listPointMainPath);
		
		FlaotPoint[] arrayPointPath = getListPoints();
		if(arrayPointPath != null && arrayPointPath.length > 0)
		{	
			int distance = distanceTouchPoint;
			int mainPointBody = arrayPointPath.length/distance;
			for(int i = 0 ; i < mainPointBody;i++)
			{
				FlaotPoint point = arrayPointPath[i*distance];
				listPointBody.add(point);
			}
			if(listPointBody.size()>0)
			{	
				listPointBody.add(arrayPointPath[listPointBody.size()-1]);
			}
		}
		
		
//		mPaintDebug = new Paint();
//		mPaintDebug.setColor(Color.GREEN);
//		mPaintDebug.setStyle(Paint.Style.STROKE);
//		mPaintDebug.setStrokeWidth(10);
		
		mPaintFocus = new Paint(mPaint);
		mPaintFocus.setStrokeWidth(mPaintFocus.getStrokeWidth());
		mPaintFocus.setColor(Color.WHITE);
		mPaintFocus.setPathEffect(new CornerPathEffect(0));
		
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
	
	public FreeStyleObject()
	{
		
	}
	
	public FlaotPoint[] getListPoints() {
		PathMeasure pm = new PathMeasure(mPathFreeStyle, false);
		float length = pm.getLength();
		FlaotPoint[] pointArray = new FlaotPoint[(int) length];
		float distance = 0f;
		float speed = length / (int) length;
		// float speed = length / 20;
		int counter = 0;
		float[] aCoordinates = { 0f, 0f };
		while ((distance < length) && (counter < (int) length)) {
			// while ((distance < length) && (counter <20)) {
			// get point from the path
			pm.getPosTan(distance, aCoordinates, null);
			pointArray[counter] = new FlaotPoint(aCoordinates[0], aCoordinates[1]);
			counter++;
			distance = distance + speed;
		}
		return pointArray;
	}
	
	@Override
	public FreeStyleObject copyCustomerView() {
		FreeStyleObject freeStyle = new FreeStyleObject(mContext,mType,x,y,w,h,mPathFreeStyle,mPaint);
		freeStyle._shapeId = this._shapeId;
		return freeStyle;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		parcel.writeInt(mType);
		parcel.writeInt(mPaint.getColor());
		parcel.writeInt((int)mPaint.getStrokeWidth());
		// write list point object
		listPointMainPath.removeAll(listPointMainPath);
		FlaotPoint[] arrayPointPath = getListPoints();
		if(arrayPointPath != null && arrayPointPath.length > 0)
		{	
			for(int i = 0 ;i < arrayPointPath.length ;i++)
			{
				FlaotPoint point = new FlaotPoint(arrayPointPath[i].getX(), arrayPointPath[i].getY());
				listPointMainPath.add(point);
			}
		}
		parcel.writeList(listPointMainPath);
	}
	
	@Override
	public void init(Parcel parcel) {
		// TODO Auto-generated method stub
		super.init(parcel);
		mType = parcel.readInt();
		mColor = parcel.readInt();
		mStrokeWith = parcel.readInt();
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(mColor);
		mPaint.setStrokeWidth(mStrokeWith);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		
		listPointMainPath = parcel.readArrayList(FlaotPoint.class.getClassLoader());
		mPathFreeStyle = new Path();
		mPathFreeStyle.moveTo(listPointMainPath.get(0).getX(), listPointMainPath.get(0).getY());
		for(int i = 1; i < listPointMainPath.size(); i++) {
			mPathFreeStyle.lineTo(listPointMainPath.get(i).getX(), listPointMainPath.get(i).getY());
		}
		
		RectFBodyPath = new RectF();
		mPathFreeStyle.computeBounds(RectFBodyPath, true);
		
		x = RectFBodyPath.left;
		y = RectFBodyPath.top;
		w = RectFBodyPath.left + (RectFBodyPath.right - RectFBodyPath.left);
		h = RectFBodyPath.top + (RectFBodyPath.bottom - RectFBodyPath.top);
		
		listPointBody.removeAll(listPointBody);
		int distance = distanceTouchPoint;
		int mainPointBody = listPointMainPath.size()/distance;
		for(int i = 0 ; i < mainPointBody;i++)
		{
			FlaotPoint point = listPointMainPath.get(i*distance);
			listPointBody.add(point);
		}
		
		mPaintFocus = new Paint(mPaint);
		mPaintFocus.setStrokeWidth(mPaintFocus.getStrokeWidth());
		mPaintFocus.setColor(Color.WHITE);
		mPaintFocus.setPathEffect(new CornerPathEffect(0));
		
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
	
	public void initNewPointPositionMoveShape(float detaX,float detaY)
	{
		Matrix translateMatrix = new Matrix();
		translateMatrix.setTranslate(-detaX,-detaY);
		mPathFreeStyle.transform(translateMatrix); 
		
		RectFBodyPath = null;
		RectFBodyPath = new RectF();
		mPathFreeStyle.computeBounds(RectFBodyPath, true);
		
		listPointBody.removeAll(listPointBody);
		listPointMainPath.removeAll(listPointMainPath);
		
		FlaotPoint[] arrayPointPath = getListPoints();
		if(arrayPointPath != null && arrayPointPath.length > 0)
		{	
			int distance = distanceTouchPoint;
			int mainPointBody = arrayPointPath.length/distance;
			for(int i = 0 ; i < mainPointBody;i++)
			{
				FlaotPoint point = arrayPointPath[i*distance];
				listPointBody.add(point);
			}
			if(listPointBody.size()>0)
			{	
				listPointBody.add(arrayPointPath[listPointBody.size()-1]);
			}
		}
		
		x = RectFBodyPath.left;
		y = RectFBodyPath.top;
		w = RectFBodyPath.right;
		h = RectFBodyPath.bottom;
		
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
	
	public void resizePath(float x,float y,float w,float h)
	{
		// old x,y,w,h path 
		RectF RectFBodyPath = new RectF();
		mPathFreeStyle.computeBounds(RectFBodyPath, true);
		float oldWidth = RectFBodyPath.right - RectFBodyPath.left;
		float oldHeight = RectFBodyPath.bottom - RectFBodyPath.top;
		float oldx = RectFBodyPath.left;
		float oldy = RectFBodyPath.top;
		
		Log.e("old height ", "old height === "+oldHeight + " hh == "+oldHeight + " oldy === "+oldy + " yy == "+y);
		if(arrayPointPathFirstCreate == null)
		{
			arrayPointPathFirstCreate = getListPoints();
		}
		
		float mXStart = 0;
		float mYStart = 0;
		if(arrayPointPathFirstCreate != null && arrayPointPathFirstCreate.length > 0)
		{	
			mPathFreeStyle.reset();
			for(int i = 0 ; i < arrayPointPathFirstCreate.length;i++)
			{
				FlaotPoint point = arrayPointPathFirstCreate[i];
				// w is a new width , h is a new height
				float mNewX = x + (w-x) * (point.getX() - oldx)/oldWidth;
				float mNewY = y + (h-y) * (point.getY() - oldy)/oldHeight;
				arrayPointPathFirstCreate[i].setX(mNewX);
				arrayPointPathFirstCreate[i].setY(mNewY);
				if(i == 0 )
				{
					mPathFreeStyle.moveTo(arrayPointPathFirstCreate[i].getX(), arrayPointPathFirstCreate[i].getY());
					mXStart = arrayPointPathFirstCreate[i].getX();
					mYStart = arrayPointPathFirstCreate[i].getY();
				}
				else
				{
					 mPathFreeStyle.quadTo(mXStart, mYStart, arrayPointPathFirstCreate[i].getX(), arrayPointPathFirstCreate[i].getY());
			         mXStart = arrayPointPathFirstCreate[i].getX();
			         mYStart =  arrayPointPathFirstCreate[i].getY();
				}
			}
		}
		
		mPathFreeStyle.computeBounds(this.RectFBodyPath, true);
		
		for(int i = 0 ; i < mListDot.size() ; i++)
		{
			DotObject dot = null;
			
			float xNewPosition = 0;
			float yNewPosition = 0;
			switch (i) {
			case Configs.idDotOne:
				xNewPosition = RectFBodyPath.left ; 
				yNewPosition = RectFBodyPath.top ;
				break;
			case Configs.idDotTwo:
				xNewPosition = RectFBodyPath.left + (RectFBodyPath.right-RectFBodyPath.left)/2 ; 
				yNewPosition = RectFBodyPath.top ;
				break;
			case Configs.idDotThree:
				xNewPosition = RectFBodyPath.right ; 
				yNewPosition = RectFBodyPath.top ;
				break;
			case Configs.idDotFour:
				xNewPosition = RectFBodyPath.right ; 
				yNewPosition = RectFBodyPath.top + (RectFBodyPath.bottom-RectFBodyPath.top)/2 ;
				break;
			case Configs.idDotFive:
				xNewPosition = RectFBodyPath.right ; 
				yNewPosition =  RectFBodyPath.bottom ;
				break;
			case Configs.idDotSix:
				xNewPosition = RectFBodyPath.left + (RectFBodyPath.right-RectFBodyPath.left)/2 ; 
				yNewPosition =  RectFBodyPath.bottom ;
				break;
			case Configs.idDotSeven:
				xNewPosition = RectFBodyPath.left ; 
				yNewPosition =  RectFBodyPath.bottom ;
				break;
			case Configs.idDotEight:
				xNewPosition = RectFBodyPath.left ; 
				yNewPosition =  RectFBodyPath.top + (RectFBodyPath.bottom-RectFBodyPath.top)/2 ;
				break;
			}
			
			mListDot.get(i).setmX(xNewPosition);
			mListDot.get(i).setmY(yNewPosition);
		}
		
	}
	
	public static final Parcelable.Creator<FreeStyleObject> CREATOR = new Parcelable.Creator<FreeStyleObject>() {
		@Override
		public FreeStyleObject createFromParcel(Parcel in) {
			FreeStyleObject mFreeStyleObject = new FreeStyleObject();
			try {
				mFreeStyleObject.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return mFreeStyleObject;
		}
		@Override
		public FreeStyleObject[] newArray(int size) {
			return new FreeStyleObject[size];
		}
	};
	
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawPath(mPathFreeStyle, this.mPaint);
		
		LogUtils.LogError("          "+RectFBodyPath.left + "            "+RectFBodyPath.right);
		mPathFreeStyle.computeBounds(RectFBodyPath, true);
		if(isFocus)
		{
			canvas.drawRect(RectFBodyPath, mPaintFocus);
			for(int i = 0 ; i < mListDot.size() ; i++)
			{
				mListDot.get(i).onDraw(canvas);
			}
		}
		
//		LogUtils.LogError("RectFBodyPath", "RectFBodyPath.left === "+RectFBodyPath.left + " RectFBodyPath.right == "+RectFBodyPath.right);
//		for(int i = 0; i < listPointBody.size();i++)
//		{
//			FlaotPoint point = listPointBody.get(i);
//			canvas.drawRect(point.getX()-alphaResize, point.getY()-alphaResize, point.getX()+alphaResize, point.getY() +alphaResize, mPaintDebug);
//		}
	}

	public RectF getRectFBodyPath() {
		return RectFBodyPath;
	}

	public void setRectFBodyPath(RectF rectFBodyPath) {
		RectFBodyPath = rectFBodyPath;
	}

	public ArrayList<FlaotPoint> getListPointBody() {
		return listPointBody;
	}

	public void setListPointBody(ArrayList<FlaotPoint> listPointBody) {
		this.listPointBody = listPointBody;
	}

	public int getAlphaResize() {
		return alphaResize;
	}

	public void setAlphaResize(int alphaResize) {
		this.alphaResize = alphaResize;
	}

	public ArrayList<DotObject> getmListDot() {
		return mListDot;
	}

	public void setmListDot(ArrayList<DotObject> mListDot) {
		this.mListDot = mListDot;
	}
	
	
}
