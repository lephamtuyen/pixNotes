package com.pixnotes.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.Paint.Style;
import android.graphics.drawable.PaintDrawable;
import android.graphics.Path;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.objects.FlaotPoint;

public class ArrowObject extends CustomerShapeView {
	
	private ArrayList<DotObject> mListDot;
	private final int FIXDOT = 2;
	private int mColor = 0;
	private int mStrokeWith = 0;
	private boolean isDebug;
	private float tailX,tailY,tipX,tipY;
	private Path arrowHeadrPath;
	private Path arrowBodyPath;
	private float xEndPoint;
	private float yEndPoint;
	private Paint mPaintDebug;
	private RectF arrowHeaderRectF ;
	private RectF arrowBodyRectF ;
	private ArrayList<FlaotPoint> listPointBody = new ArrayList<FlaotPoint>();
	private int alphaResize = 50;
	public ArrowObject(Context mContext,int mType,float tailX , float tailY, float tipX , float tipY, Paint mPaint) {
		super(mContext,mType, tailX, tailY, tipX, tipY , mPaint);
		this.mType = mType;
		this.tailX = tailX;
		this.tailY = tailY;
		this.tipX = tipX;
		this.tipY = tipY;
		this.mPaint = new Paint(mPaint);
		this.mPaint.setPathEffect(new CornerPathEffect(0));
		this.mPaint.setStyle(Style.FILL);
		listPointBody = new ArrayList<FlaotPoint>();
		arrowHeadrPath = new Path();
		arrowBodyPath = new Path();
		logicDrawArrow(tailX, tailY, tipX, tipY);
		createListDotPoint(tailX,tailY,tipX,tipY);
		
		arrowHeaderRectF = new RectF();
		arrowHeadrPath.computeBounds(arrowHeaderRectF, true);
		arrowBodyRectF = new RectF();
		arrowBodyPath.computeBounds(arrowBodyRectF, true);
		
		
		listPointBody.removeAll(listPointBody);
		FlaotPoint[] points = getListPoints();
		int distance = 60;
		int mainPointBody = points.length/distance;
		for(int i = 0 ; i < mainPointBody;i++)
		{
			FlaotPoint point = points[i*distance];
			listPointBody.add(point);
		}
		
		mPaintDebug = new Paint();
		mPaintDebug.setColor(Color.GREEN);
		mPaintDebug.setStyle(Paint.Style.STROKE);
		mPaintDebug.setStrokeWidth(10);
	}
	
	
	public ArrowObject()
	{
		
	}
	
	
	public float getTailX() {
		return tailX;
	}


	public void setTailX(float tailX) {
		this.tailX = tailX;
	}


	public float getTailY() {
		return tailY;
	}


	public void setTailY(float tailY) {
		this.tailY = tailY;
	}


	public float getTipX() {
		return tipX;
	}


	public void setTipX(float tipX) {
		this.tipX = tipX;
	}


	public float getTipY() {
		return tipY;
	}


	public void setTipY(float tipY) {
		this.tipY = tipY;
	}


	public RectF getArrowBodyRectF() {
		return arrowBodyRectF;
	}


	public void setArrowBodyRectF(RectF arrowBodyRectF) {
		this.arrowBodyRectF = arrowBodyRectF;
	}


	public RectF getArrowHeaderRectF() {
		return arrowHeaderRectF;
	}


	public void setArrowHeaderRectF(RectF arrowHeaderRectF) {
		this.arrowHeaderRectF = arrowHeaderRectF;
	}


	public int getAlphaResize() {
		return alphaResize;
	}


	public void setAlphaResize(int alphaResize) {
		this.alphaResize = alphaResize;
	}


	public ArrayList<FlaotPoint> getListPointBody() {
		return listPointBody;
	}


	public void setListPointBody(ArrayList<FlaotPoint> listPointBody) {
		this.listPointBody = listPointBody;
	}


	public FlaotPoint[] getListPoints() {
		PathMeasure pm = new PathMeasure(arrowBodyPath, false);
		float length = pm.getLength();
		FlaotPoint[] pointArray = new FlaotPoint[(int) length];
		float distance = 0f;
		float speed = length / (int) length;
		Log.e("speed", "speed ========= "+speed);
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
	
	private void createListDotPoint(float tailX , float tailY, float tipX , float tipY)
	{
		mListDot = new ArrayList<DotObject>();
		for(int i = 0 ; i < FIXDOT ; i++)
		{
			DotObject dot = null;
			switch (i) {
			case Configs.idDotOne:
				dot = new DotObject(i, tailX , tailY);
				break;
			case Configs.idDotTwo:
				dot = new DotObject(i, tipX , tipY );
				break;
			}
			mListDot.add(dot);
		}
	}
	
	@Override
	public ArrowObject copyCustomerView() {
		ArrowObject arrow = new ArrowObject(mContext,mType, tailX, tailY, tipX, tipY, mPaint);
		arrow._shapeId = this._shapeId;
		return arrow;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		parcel.writeInt(mType);
		parcel.writeFloat(tailX);
		parcel.writeFloat(tailY);
		parcel.writeFloat(tipX);
		parcel.writeFloat(tipY);
		parcel.writeInt(mPaint.getColor());
		parcel.writeInt((int)mPaint.getStrokeWidth());
		
	}
	
	@Override
	public void init(Parcel parcel) {
		// TODO Auto-generated method stub
		super.init(parcel);
		mType = parcel.readInt();
		tailX = parcel.readFloat();
		tailY = parcel.readFloat();
		tipX = parcel.readFloat();
		tipY = parcel.readFloat();
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
		mPaint.setStyle(Style.FILL);
		
		arrowHeadrPath = new Path();
		arrowBodyPath = new Path();
		logicDrawArrow(tailX, tailY, tipX, tipY);
		createListDotPoint(tailX,tailY,tipX,tipY);
		
		arrowHeaderRectF = new RectF();
		arrowHeadrPath.computeBounds(arrowHeaderRectF, true);
		
		arrowBodyRectF = new RectF();
		arrowBodyPath.computeBounds(arrowBodyRectF, true);
		
		listPointBody.removeAll(listPointBody);
		FlaotPoint[] points = getListPoints();
		int distance = 60;
		int mainPointBody = points.length/distance;
		for(int i = 0 ; i < mainPointBody;i++)
		{
			FlaotPoint point = points[i*distance];
			listPointBody.add(point);
		}
		
		Log.e("Arrow Object Init", "Arrow Object Init ===== ");
	}
	
	public static final Parcelable.Creator<ArrowObject> CREATOR = new Parcelable.Creator<ArrowObject>() {
		@Override
		public ArrowObject createFromParcel(Parcel in) {
			ArrowObject arrowObj = new ArrowObject();
			try {
				arrowObj.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return arrowObj;
		}
		@Override
		public ArrowObject[] newArray(int size) {
			return new ArrowObject[size];
		}
	};
	
	public  void logicDrawArrow( float tailX , float tailY, float tipX , float tipY)
	{
		arrowHeadrPath.reset();
		arrowBodyPath.reset();
	    int arrowLength = 60; //can be adjusted
	    float dx = tipX - tailX;
	    float dy = tipY - tailY;

	    double theta = Math.atan2(dy, dx);
	    double rad = Math.toRadians(45); //35 angle, can be adjusted
	    double x = tipX - arrowLength * Math.cos(theta + rad);
	    double y = tipY - arrowLength * Math.sin(theta + rad);

	    double phi2 = Math.toRadians(-45);//-35 angle, can be adjusted
	    double x2 = tipX - arrowLength * Math.cos(theta + phi2);
	    double y2 = tipY - arrowLength * Math.sin(theta + phi2);

	    float[] arrowYs = new float[3];
	    arrowYs[0] = tipY;
	    arrowYs[1] = (int) y;
	    arrowYs[2] = (int) y2;

	    float[] arrowXs = new float[3];
	    arrowXs[0] = tipX;
	    arrowXs[1] = (int) x;
	    arrowXs[2] = (int) x2;
	    arrowHeadrPath.moveTo(tipX, tipY);
//	    test.lineTo((float)x, (float)y);
//	    test.lineTo((float)x2, (float)y2);
	    arrowHeadrPath.quadTo(tipX, tipY, (float)x, (float)y);
	    arrowHeadrPath.quadTo((float)x, (float)y, (float)x2, (float)y2);
	    arrowHeadrPath.quadTo((float)x2, (float)y2,tipX, tipY);
	   
	    xEndPoint = (tipX + (float)x + (float)x2)/3;
	    yEndPoint = (tipY + (float)y + (float)y2)/3;
	    
	    arrowBodyPath.moveTo(tailX, tailY);
	    arrowBodyPath.quadTo(tailX, tailY, tipX, tipY);
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.drawPath(arrowHeadrPath, mPaint);
		canvas.drawLine(tailX, tailY, xEndPoint, yEndPoint, mPaint);
		
//		canvas.drawRect(arrowHeaderRectF, mPaintDebug);
//		canvas.drawRect(arrowBodyRectF, mPaintDebug);
//		for(int i = 0; i < listPointBody.size();i++)
//		{
//			FlaotPoint point = listPointBody.get(i);
//			canvas.drawRect(point.getX()-alphaResize, point.getY()-alphaResize, point.getX()+alphaResize, point.getY() +alphaResize, mPaintDebug);
//		}
		
		if(isFocus)
		{
			for(int i = 0 ; i < mListDot.size() ; i++)
			{
				mListDot.get(i).onDraw(canvas);
			}
		}
	}
	
	public void initNewPointPositionMoveShape()
	{
		logicDrawArrow(tailX, tailY, tipX, tipY);
		
		arrowHeaderRectF = null;
		arrowHeaderRectF = new RectF();
		arrowHeadrPath.computeBounds(arrowHeaderRectF, true);
		
		arrowBodyRectF = null;
		arrowBodyRectF = new RectF();
		arrowBodyPath.computeBounds(arrowBodyRectF, true);
		
		listPointBody.removeAll(listPointBody);
		FlaotPoint[] points = getListPoints();
		int distance = 60;
		int mainPointBody = points.length/distance;
		for(int i = 0 ; i < mainPointBody;i++)
		{
			FlaotPoint point = points[i*distance];
			listPointBody.add(point);
		}
	}
	
	public void initNewPointPosition(int indexDotPoint,float x,float y)
	{
		if(indexDotPoint == Configs.idDotOne)
		{
			tailX = x;
			tailY = y;
		}
		else
		{
			tipX = x;
			tipY = y;
		}
		
		logicDrawArrow(tailX, tailY, tipX, tipY);
		
		mListDot.get(indexDotPoint).setmX(x);
		mListDot.get(indexDotPoint).setmY(y);

		
		arrowHeaderRectF = null;
		arrowHeaderRectF = new RectF();
		arrowHeadrPath.computeBounds(arrowHeaderRectF, true);
		
		arrowBodyRectF = null;
		arrowBodyRectF = new RectF();
		arrowBodyPath.computeBounds(arrowBodyRectF, true);
		
		listPointBody.removeAll(listPointBody);
		FlaotPoint[] points = getListPoints();
		int distance = 60;
		int mainPointBody = points.length/distance;
		for(int i = 0 ; i < mainPointBody;i++)
		{
			FlaotPoint point = points[i*distance];
			listPointBody.add(point);
		}
		
	}
	
	public ArrayList<DotObject> getmListDot() {
		return mListDot;
	}

	public void setmListDot(ArrayList<DotObject> mListDot) {
		this.mListDot = mListDot;
	}
	
	
}
