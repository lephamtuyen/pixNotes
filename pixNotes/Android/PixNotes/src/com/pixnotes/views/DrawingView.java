package com.pixnotes.views;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.RelativeLayout;

import com.pixnotes.R;
import com.pixnotes.common.DrawUtils;
import com.pixnotes.common.PreferenceConnector;
import com.pixnotes.configsapp.Configs;
import com.pixnotes.configsapp.Configs.ShapeAction;
import com.pixnotes.interfaceconstructe.ListenerEventUpdate;
import com.pixnotes.managers.BlurManager;
import com.pixnotes.objects.FlaotPoint;
import com.pixnotes.objects.IntersectLine;
import com.pixnotes.objects.IntersectPoint;
import com.pixnotes.utils.LogUtils;

public class DrawingView extends RelativeLayout {
	
	private Context mContext;
	private Bitmap mainBitmap;
	private int xMainBitmap,yMainBitmap;
	private  Paint  mMainBitmapPaint;
	private int wMainView,hMainView;
	
	private Path mPath;
	private float mXStart, mYStart;
	private static final float TOUCH_TOLERANCE = 15;
	private boolean isPaintDebug = false;
	private Paint PaintDebug ;
	private ArrayList<Integer> listIndexCornerPath = new ArrayList<Integer>();
	private ArrayList<FlaotPoint> listPoint = new ArrayList<FlaotPoint>();
	private ArrayList<FlaotPoint> listIntersection = new ArrayList<FlaotPoint>();
	private int isStartDirection = 0;
	private int isDirection = 0;
	private final int  HorizontalDirection = 1;
	private final int  VerticalDirection = 2;
	private int countChangeDirection = 0;
	private final int SquareCornerRoot_90_Degrees = 90;
	private final int SquareCornerRoot_180_Degrees = 180;
	private final int SquareCornerRoot_360_Degrees = 360;
	private final int mValue_Degrees_Deviation = 10;
	private int countSquareCorner = 0;
	private boolean isRectangle = false;
	private boolean isCircle = false;
	private boolean isZigZag = false;
	private boolean isArrow = false;
	private final double alphaCircleRadius = 50;
	private final float alphaRectangle = 50;
	private final float alphaArrowDegrees = 30;
	private Paint mCurrentSettingPaint;
	private ArrayList<CustomerShapeView> listRootShape;
	private int[] arrayColor = new int[]{0xFFFF0000,0xFFFFFF33,0xFF0033FF,0xFF00FF00,0xFF838383,0xFFFFFFFF};
	private int[] arrayStrokeWith = new int []{3,6,9};
	private int[] arrayTextSize = new int[]{34,42,52};
	private int indexColor = 0;
	private int indexStrokeWith = 0;
	private int indexTextSize = 0 ;
	private ListenerEventUpdate listener;
	private boolean isTouchOnShape = false;
	private boolean isTouchOnPointFocusShape = false;
	private int indexCurrentTouchOnShape = 0;
	private CustomerShapeView mCurrentShape; //current shape touch for move or resize
	private int mCurrentTypeShape = 0;
	private int mIndexDotCurrentShape = 0;
	private int detalX = 0;
	private int detalY = 0;
	private boolean isDragGrid = false;
	private int mGridType = 0;
	private int mColorGrid = 0;
	private int distanceGridLine = 50;
	private int mCountLineGridPositionX = 0;
	private int mCountLineGridPositionY = 0;
	private Paint mPaintGridType = new Paint();
	private ArrayList<FlaotPoint> listPointDistanceGrid_X = new ArrayList<FlaotPoint>();
	private ArrayList<FlaotPoint> listPointDistanceGrid_Y = new ArrayList<FlaotPoint>();
	private final int connerArrowAccept = 60;
	private int distanceZigZag = 0;
	private int mMaxWidthZigZag = 30;
	private int mMaxHeightZigZag = 60;
	private int startIndexListPoint = 0;
	private ArrayList<ArrayList<FlaotPoint>> listArrowParagraph = new ArrayList<ArrayList<FlaotPoint>>(); 
//	private Bitmap BlurBitmap;
	private ArrayList<FlaotPoint> listPointArrow = new ArrayList<FlaotPoint>();
	private boolean isEdit;
//	private int isFirstShowDeleteBar = 0;
	private int alphaDistanceDelete = 0;
	private boolean isShowingMenu = false;
	private boolean isNotDrawWhenCloseMenu = false;
	private GestureDetector mGestureDetector;
	private boolean disableTouchEventWhenShowSettingPaint = false;
	
	private Paint mPaintVirtualRect;
	private int mX_VirtualRectOne,mY_VirtualRectOne,mW_VirtualRectOne,mH_VirtualRectOne;
	private int mX_VirtualRectTwo,mY_VirtualRectTwo,mW_VirtualRectTwo,mH_VirtualRectTwo;
	
	//These constants specify the mode that we're in
	private static int NONE = 0;
	private static int DRAG = 1;
	private static int ZOOM = 2;
	
	private int mode;
	//These two variables keep track of the X and Y coordinate of the finger when it first
	//touches the screen
	private float startX = 0f;
	private float startY = 0f;
	//These two variables keep track of the amount we need to translate the canvas along the X
	//and the Y coordinate
	private float translateX = 0f;
	private float translateY = 0f;
	//These two variables keep track of the amount we translated the X and Y coordinates, the last time we
	//panned.
	private float previousTranslateX = 0f;
	private float previousTranslateY = 0f;   
	private boolean dragged = true;
	
	private Bitmap mBitmapCaro;
	private int mRow_Caro_One;
	private int mColum_Caro_One;
	private int mRow_Caro_Two;
	private int mColum_Caro_Two;
	// debug variable
	private Paint shadowPaint;
	private Paint mPaintCircle;
	private Paint mPaintCircleDebug;
	private FlaotPoint centerPoint;
	private double radius;
	private IntersectPoint intersectArrowConner;
	
	private Matrix mMatrix;
	
	private static float MIN_ZOOM = 1f;
	private static float MAX_ZOOM = 5f;
	public float scaleFactor = 1.f;
	private ScaleGestureDetector mScaleDetector;
	private Configs.DrawingState mDrawingState = Configs.DrawingState.Idle;
	
	private int _undoRedoFirstIndex = Configs.INVALID_INDEX;
	private int _undoRedoCurrentIndex = Configs.INVALID_INDEX;
	
	private BlurManager _blurManager;
	
	public DrawingView(Context context,AttributeSet attributeSet) {
		super(context,attributeSet);
		
		this.mContext = context;
		// load bitmap caro
		mBitmapCaro = BitmapFactory.decodeResource(getResources(), R.drawable.bg_caro);
		// create paint for mainbitmap
		mMainBitmapPaint = new Paint();
		// create draw path  
		mPath = new Path();
				
		indexColor = PreferenceConnector.readInteger(context, Configs.FLAG_INDEX_COLOR, 0);
		indexStrokeWith = PreferenceConnector.readInteger(context, Configs.FLAG_INDEX_STROKEWIDTH, 0);
		
		// current setting paint 
		mCurrentSettingPaint = new Paint();
		mCurrentSettingPaint.setAntiAlias(true);
		mCurrentSettingPaint.setDither(true);
		mCurrentSettingPaint.setColor(arrayColor[indexColor]);
		mCurrentSettingPaint.setStyle(Paint.Style.STROKE);
		mCurrentSettingPaint.setStrokeJoin(Paint.Join.ROUND);
		mCurrentSettingPaint.setStrokeCap(Paint.Cap.ROUND);
		mCurrentSettingPaint.setStrokeWidth(arrayStrokeWith[indexStrokeWith]);
		mCurrentSettingPaint.setTextSize(arrayTextSize[indexStrokeWith]);
		mCurrentSettingPaint.setPathEffect(new CornerPathEffect(25));
		
		// List Root draw object in canvas
		listRootShape = new ArrayList<CustomerShapeView>();
		// init Gesture Engine
//		initGestureEngine();
		
		mPaintVirtualRect = new Paint();
		mPaintVirtualRect.setColor(Color.GRAY);
		mPaintVirtualRect.setStyle(Paint.Style.FILL);
		
		///////////// debug
		mPaintCircle = new Paint();
		mPaintCircle.setColor(Color.GREEN);
		mPaintCircle.setStyle(Paint.Style.STROKE);
		mPaintCircle.setStrokeWidth(10);
		
		mPaintCircleDebug = new Paint();
		mPaintCircleDebug.setColor(Color.BLUE);
		mPaintCircleDebug.setStyle(Paint.Style.STROKE);
		mPaintCircleDebug.setStrokeWidth(10);
		
		PaintDebug  = new Paint();
		PaintDebug.setColor(Color.CYAN);
		
		shadowPaint = new Paint();
        shadowPaint.setAntiAlias(true);
        shadowPaint.setColor(Color.RED);
        shadowPaint.setTextSize(45.0f);
        shadowPaint.setStrokeWidth(2.0f);
        shadowPaint.setStyle(Paint.Style.STROKE);
//      shadowPaint.setShadowLayer(5.0f, 10.0f, 10.0f, Color.BLACK);
        
        // init GestureDetector
        mGestureDetector = new GestureDetector(new MyGestureDetector());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
     
        _blurManager = new BlurManager(context);
	}
	
	private float mTranslateX = 0.f;
	private float mTranslateY = 0.f;
	public float lastFocusY = -1.f;
	public float lastFocusX = -1.f;
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
	    
		@Override
	    public boolean onScale(ScaleGestureDetector detector) {
			
			if(mainBitmap == null || !isEdit)
			{
				 return true;
			}
			
	    	scaleFactor *= detector.getScaleFactor();
	    	scaleFactor = Math.max(MIN_ZOOM, Math.min(scaleFactor, MAX_ZOOM));
	    	LogUtils.LogError("ScaleGestureDetector","detector.getScaleFactor() ******== "+detector.getScaleFactor() + "   mScaleDetector.getScaleFactor() == "+ mScaleDetector.getScaleFactor());
	    	LogUtils.LogError("scaleFactor","scaleFactor  @@@@@@ == "+scaleFactor);
	    	if(scaleFactor > MIN_ZOOM){
	    		
	    		getAbsolutePosition(detector.getFocusX(), detector.getFocusY());
	    		
	    		float newTouch[] = getAbsolutePosition(detector.getFocusX(), detector.getFocusY());
	    		mTranslateX = newTouch[0];
	    		mTranslateY = newTouch[1];
	    		
	    	} else {
	    		mTranslateX = 0;
	    		mTranslateY = 0;
	    		
	    	}
	    	LogUtils.LogError("ScaleListener", "mTranslateX == "+mTranslateX +  " mTranslateY === " + mTranslateY );
	    	
//	    	invalidate();
	    	
	        return true;
	    }
	    @Override
	    public boolean onScaleBegin(ScaleGestureDetector detector) {
	    	// TODO Auto-generated method stub
	    	//scaleFactor = 1.0f;
	    	lastFocusY = -1.f;
	    	lastFocusX = -1.f;
	    	return super.onScaleBegin(detector);
	    }
	    @Override
	    public void onScaleEnd(ScaleGestureDetector detector) {
	    	// TODO Auto-generated method stub
	    	super.onScaleEnd(detector);
	    }
	    
	    
	}
	
	public void applyGrid(boolean isDragGrid,int gridType,int colorGrid)
	{
		if(wMainView == 0 || hMainView ==0)
			return ;
		this.isDragGrid = isDragGrid;
		mGridType = gridType;
		mColorGrid = colorGrid;
		if(mPaintGridType == null)
			mPaintGridType = new Paint();
		mPaintGridType.setColor(arrayColor[mColorGrid]);
		mCountLineGridPositionX = 0;
		mCountLineGridPositionY = 0; 
		listPointDistanceGrid_X.removeAll(listPointDistanceGrid_X);
		listPointDistanceGrid_Y.removeAll(listPointDistanceGrid_Y);
		switch (gridType) {
		case Configs.FLAG_GRIDTYPE_1:
			mCountLineGridPositionX = wMainView/distanceGridLine;
			int calculateDistanceY = hMainView/mCountLineGridPositionX;
			for(int i = 0;i < mCountLineGridPositionX;i++)
			{
				FlaotPoint point_X = new FlaotPoint((i+1)*distanceGridLine, 0);
				listPointDistanceGrid_X.add(point_X);
				
				FlaotPoint point_Y = new FlaotPoint(0, (i+1)*calculateDistanceY);
				listPointDistanceGrid_Y.add(point_Y);
			}
			break;
		case Configs.FLAG_GRIDTYPE_2:
			mCountLineGridPositionX = wMainView/distanceGridLine;
			break;
		case Configs.FLAG_GRIDTYPE_3:
			mCountLineGridPositionY = hMainView/distanceGridLine;
			break;
		case Configs.FLAG_GRIDTYPE_4:
			mCountLineGridPositionX = wMainView/distanceGridLine;
			mCountLineGridPositionY = hMainView/distanceGridLine;
			break;
		}
	}
	
	public void setCurrentPaint(int indexColor,int indexStrokeWidth,String textValue)
	{
		mCurrentSettingPaint.setColor(arrayColor[indexColor]);
		mCurrentSettingPaint.setStrokeWidth(arrayStrokeWith[indexStrokeWidth]);
		mCurrentSettingPaint.setTextSize(arrayTextSize[indexStrokeWidth]);
		
		// update color for current shape focus
		switch (mCurrentTypeShape) {
		case Configs.RECTANGLE_TYPE:
		case Configs.CIRCLE_TYPE:
		case Configs.BLUR_TYPE:
		case Configs.ARROW_TYPE:
		case Configs.FREE_STYLE_TYPE:
			mCurrentShape.mPaint.setColor(arrayColor[indexColor]);
			mCurrentShape.mPaint.setStrokeWidth(arrayStrokeWith[indexStrokeWidth]);
			break;
		case Configs.ADD_TEXT_TYPE:
			TextObject textObj = (TextObject)mCurrentShape;
			textObj.mPaint.setColor(arrayColor[indexColor]);
			textObj.mPaint.setStrokeWidth(arrayStrokeWith[indexStrokeWidth]);
			textObj.mPaint.setTextSize(arrayTextSize[indexStrokeWidth]);
			textObj.initNewText(textValue);
			break;
		}
		mCurrentShape._action = ShapeAction.ChangeTextColorOrStroke;
		showShapeAndFocus(mCurrentShape, false, false);
		addNewSharp(mCurrentShape);
		mCurrentShape = mCurrentShape.copyCustomerView();
		showShapeAndFocus(mCurrentShape, true, true);
		mCurrentShape._action = ShapeAction.MoveShapeToTop;
		
		disableTouchEventWhenShowSettingPaint = false;
		postInvalidate();
	}
	
	public void setBlurOpacity(int mOPacity)
	{
		((BlurObject)mCurrentShape).setOpacity(mOPacity);
		mCurrentShape._action = ShapeAction.ChangeBlurOpacity;
		showShapeAndFocus(mCurrentShape, false, false);
		addNewSharp(mCurrentShape);
		mCurrentShape = mCurrentShape.copyCustomerView();
		showShapeAndFocus(mCurrentShape, true, true);
		mCurrentShape._action = ShapeAction.MoveShapeToTop;
		disableTouchEventWhenShowSettingPaint = false;
		postInvalidate();
	}
	public void setListener(ListenerEventUpdate listener)
	{
		this.listener = listener;
	}
	

	
	public void resetAllData()
	{
		if(mainBitmap != null)
		{	
			mainBitmap.recycle();
			mainBitmap = null;
		}
		
		if(listIndexCornerPath != null)
		{
			listIndexCornerPath.removeAll(listIndexCornerPath);
		}
		if(listPoint != null)
		{
			listPoint.removeAll(listPoint);
		}
		if(listIntersection != null)
		{
			listIntersection.removeAll(listIntersection);
		}
		if(listRootShape != null)
		{
			listRootShape.removeAll(listRootShape);
		}
		if(listPointDistanceGrid_X != null)
		{
			listPointDistanceGrid_X.removeAll(listPointDistanceGrid_X);
		}
		if(listPointDistanceGrid_Y != null)
		{
			listPointDistanceGrid_Y.removeAll(listPointDistanceGrid_Y);
		}
		if(listPointArrow != null)
		{
			listPointArrow.removeAll(listPointArrow);
		}
		mPaintGridType = null;
		if(listArrowParagraph != null)
		{
			listArrowParagraph.removeAll(listArrowParagraph);
		}
		isShowingMenu = false;
		System.gc();
	}
	
	public void loadListRootShape(ArrayList<CustomerShapeView> listRootShape)
	{
		Log.e("listRootShape", "listRootShape ======== "+listRootShape.size());
		clearAllRootShape();
		this.listRootShape.addAll(listRootShape);
		_undoRedoCurrentIndex = listRootShape.size();
		_undoRedoFirstIndex = listRootShape.size();
		checkHeaderStatus();
	}
	
	void addNewSharp(CustomerShapeView shape){
		//Clear list in redo
		listRootShape.add(shape);
		_undoRedoCurrentIndex = listRootShape.size();
		checkHeaderStatus();
	}
	
	void clearRedoSharpFromIndex(int index){
		if (index > listRootShape.size()-1)
			return;
		ArrayList<CustomerShapeView> tempList = new ArrayList<CustomerShapeView>();
		for(int i = 0; i < index; i++){
			tempList.add(listRootShape.get(i));
		}
		listRootShape.clear();
		listRootShape = null;
		listRootShape = tempList;
		_undoRedoCurrentIndex = listRootShape.size();
	}
	
	public void clearAllRootShape()
	{
		this.listRootShape.removeAll(this.listRootShape);
		this._undoRedoCurrentIndex = listRootShape.size();
		this._undoRedoFirstIndex = listRootShape.size();
		this.mCurrentShape = null;
		checkHeaderStatus();
	}
	
	public Bitmap scaleBitmapAfterZoomInOut()
	{
      float newWidth = wMainView * scaleFactor;
      float newHeight = hMainView * scaleFactor;
	  Bitmap scaledBitmap = Bitmap.createBitmap((int)newWidth, (int)newHeight, Config.ARGB_8888);

	  float scaleX = scaleFactor;
	  float scaleY = scaleFactor;
	  float pivotX = 0;
	  float pivotY = 0;

	  Matrix scaleMatrix = new Matrix();
	  scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

	  Canvas canvas = new Canvas(scaledBitmap);
	  canvas.setMatrix(scaleMatrix);
	  if(mainBitmap != null)
	  {
			canvas.drawBitmap(mainBitmap, xMainBitmap , yMainBitmap, mMainBitmapPaint);
      }
		 
		 if(isDragGrid)
		 {
			 switch (mGridType) {
				case Configs.FLAG_GRIDTYPE_1:
					for(int i = 0 ; i < listPointDistanceGrid_X.size();i++)
					{
						FlaotPoint point_X = listPointDistanceGrid_X.get(i);
						FlaotPoint point_Y = listPointDistanceGrid_Y.get(i);
						canvas.drawLine(point_X.getX(), point_X.getY(), point_Y.getX(), point_Y.getY(), mPaintGridType);
						canvas.drawLine(point_X.getX(), hMainView, wMainView, point_Y.getY(), mPaintGridType);
					}
					
					break;
				case Configs.FLAG_GRIDTYPE_2:
					for(int i = 0 ; i < mCountLineGridPositionX ; i++)
					{
						canvas.drawLine((i+1)*distanceGridLine, 0, (i+1)*distanceGridLine, hMainView, mPaintGridType);
					}
					break;
				case Configs.FLAG_GRIDTYPE_3:
					for(int i = 0 ; i < mCountLineGridPositionY ; i++)
					{
						canvas.drawLine(0, (i+1)*distanceGridLine, wMainView, (i+1)*distanceGridLine, mPaintGridType);
					}
					break;
				case Configs.FLAG_GRIDTYPE_4:
					for(int i = 0 ; i < mCountLineGridPositionX ; i++)
					{
						canvas.drawLine((i+1)*distanceGridLine, 0, (i+1)*distanceGridLine, hMainView, mPaintGridType);
					}
					for(int i = 0 ; i < mCountLineGridPositionY ; i++)
					{
						canvas.drawLine(0, (i+1)*distanceGridLine, wMainView, (i+1)*distanceGridLine, mPaintGridType);
					}
					break;
				}
		 }
		 
		 for(int i = 0 ; i < listRootShape.size() ; i++)
		 {
			 CustomerShapeView shape = listRootShape.get(i);
			 
			 //Prepare or recycle bitmap;
			 processBitmap(shape);
			 if(shape.isVisible){
				 shape.onDraw(canvas);
			 }
		 }
		 
		 if (mCurrentShape != null && mCurrentShape.isVisible){
			 processBitmap(mCurrentShape);
			 mCurrentShape.onDraw(canvas);
		 }
		 
		 canvas.drawPath(mPath, mCurrentSettingPaint);
	  
	  return scaledBitmap;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		//We're going to scale the X and Y coordinates by the same amount
        canvas.scale(scaleFactor, scaleFactor,mTranslateX ,mTranslateY);
//        LogUtils.LogError("mTranslateX", "mTranslateX === "+mTranslateX + "  mTranslateY === "+mTranslateY);
        
//		canvas.scale(scaleFactor, scaleFactor);
//        //If translateX times -1 is lesser than zero, let's set it to zero. This takes care of the left bound
//        if((translateX * -1) < 0) {
//           translateX = 0;
//        }
//
//        //This is where we take care of the right bound. We compare translateX times -1 to (scaleFactor - 1) * displayWidth.
//        //If translateX is greater than that value, then we know that we've gone over the bound. So we set the value of 
//        //translateX to (1 - scaleFactor) times the display width. Notice that the terms are interchanged; it's the same
//        //as doing -1 * (scaleFactor - 1) * displayWidth
//        else if((translateX * -1) > (scaleFactor - 1) * wMainView) {
//           translateX = (1 - scaleFactor) * wMainView;
//        }
//
//        if(translateY * -1 < 0) {
//           translateY = 0;
//        }
//
//        //We do the exact same thing for the bottom bound, except in this case we use the height of the display
//        else if((translateY * -1) > (scaleFactor - 1) * hMainView) {
//           translateY = (1 - scaleFactor) * hMainView;
//        }
//
//        //We need to divide by the scale factor here, otherwise we end up with excessive panning based on our zoom level
//        //because the translation amount also gets scaled according to how much we've zoomed into the canvas.
//        canvas.translate(translateX / scaleFactor, translateY / scaleFactor);
//		
//        LogUtils.LogError("translateX", "translateX == "+translateX + "  translateY === "+translateY + "  scaleFactor == "+scaleFactor);
       
         for(int i = 0 ; i < mRow_Caro_One ; i++)
         {
        	 for(int k = 0 ; k < mColum_Caro_One ; k++)
        	 {
        		 canvas.drawBitmap(mBitmapCaro, mX_VirtualRectOne + i * mBitmapCaro.getWidth(), mY_VirtualRectOne+ k * mBitmapCaro.getHeight(), mMainBitmapPaint);
        	 }
         }
        
         for(int i = 0 ; i < mRow_Caro_Two ; i++)
         {
        	 for(int k = 0 ; k < mColum_Caro_Two ; k++)
        	 {
        		 canvas.drawBitmap(mBitmapCaro, mX_VirtualRectTwo + i * mBitmapCaro.getWidth(), mY_VirtualRectTwo + k * mBitmapCaro.getHeight(), mMainBitmapPaint);
        	 }
         }
         
		 if(mainBitmap != null)
		 {
			canvas.drawBitmap(mainBitmap, xMainBitmap , yMainBitmap, mMainBitmapPaint);
		 }
		 
		 if(isDragGrid)
		 {
			 switch (mGridType) {
				case Configs.FLAG_GRIDTYPE_1:
					for(int i = 0 ; i < listPointDistanceGrid_X.size();i++)
					{
						FlaotPoint point_X = listPointDistanceGrid_X.get(i);
						FlaotPoint point_Y = listPointDistanceGrid_Y.get(i);
						canvas.drawLine(point_X.getX(), point_X.getY(), point_Y.getX(), point_Y.getY(), mPaintGridType);
						canvas.drawLine(point_X.getX(), hMainView, wMainView, point_Y.getY(), mPaintGridType);
					}
					
					break;
				case Configs.FLAG_GRIDTYPE_2:
					for(int i = 0 ; i < mCountLineGridPositionX ; i++)
					{
						canvas.drawLine((i+1)*distanceGridLine, 0, (i+1)*distanceGridLine, hMainView, mPaintGridType);
					}
					break;
				case Configs.FLAG_GRIDTYPE_3:
					for(int i = 0 ; i < mCountLineGridPositionY ; i++)
					{
						canvas.drawLine(0, (i+1)*distanceGridLine, wMainView, (i+1)*distanceGridLine, mPaintGridType);
					}
					break;
				case Configs.FLAG_GRIDTYPE_4:
					for(int i = 0 ; i < mCountLineGridPositionX ; i++)
					{
						canvas.drawLine((i+1)*distanceGridLine, 0, (i+1)*distanceGridLine, hMainView, mPaintGridType);
					}
					for(int i = 0 ; i < mCountLineGridPositionY ; i++)
					{
						canvas.drawLine(0, (i+1)*distanceGridLine, wMainView, (i+1)*distanceGridLine, mPaintGridType);
					}
					break;
				}
		 }
		 
		 for(int i = 0 ; i < listRootShape.size() ; i++)
		 {
			 CustomerShapeView shape = listRootShape.get(i);
			 
			 //Prepare or recycle bitmap;
			 processBitmap(shape);
			 if(shape.isVisible){
				 shape.onDraw(canvas);
			 }
		 }
		 
		 if (mCurrentShape != null && mCurrentShape.isVisible){
			 processBitmap(mCurrentShape);
			 mCurrentShape.onDraw(canvas);
		 }
		 
		 canvas.drawPath(mPath, mCurrentSettingPaint);
		 
//		 canvas.drawRect(mX_VirtualRectOne, mY_VirtualRectOne, mW_VirtualRectOne, mH_VirtualRectOne, mPaintVirtualRect);
//	     canvas.drawRect(mX_VirtualRectTwo, mY_VirtualRectTwo, mW_VirtualRectTwo, mH_VirtualRectTwo, mPaintVirtualRect);
//	     canvas.drawRect(mTranslateX, mTranslateY, mTranslateX + 50, mTranslateY + 50, mPaintVirtualRect);
		 
//		 if(isPaintDebug)
//		 {
//			 for(int i = 0 ; i < listPoint.size() ; i++)
//			 {
//				 FlaotPoint point = listPoint.get(i);
//				 canvas.drawRect(point.getX(), point.getY(), point.getX()+10, point.getY()+10, PaintDebug);
//			 }
			 
//			 for(int i = 0 ; i < listIntersection.size();i++)
//			 {
//				 FlaotPoint point = listIntersection.get(i);
//				 canvas.drawRect(point.getX(), point.getY(), point.getX()+10, point.getY()+10, mPaintCircleDebug);
//			 }
			 
//			 if(intersectArrowConner != null)
//			 canvas.drawRect((float)intersectArrowConner.x, (float)intersectArrowConner.y, (float)(intersectArrowConner.x+10), (float)(intersectArrowConner.y+10), mPaintCircleDebug);
			 
//			 if(centerPoint != null)
//			 {	 
//				 canvas.drawCircle(centerPoint.getX(), centerPoint.getY(), (float)(radius-alphaCircleRadius), mPaintCircleDebug);
//				 canvas.drawCircle(centerPoint.getX(), centerPoint.getY(), (float)radius, mPaintCircle);
//				 canvas.drawCircle(centerPoint.getX(), centerPoint.getY(), (float)(radius+alphaCircleRadius), mPaintCircleDebug);
//				 canvas.drawCircle(centerPoint.getX(), centerPoint.getY(), 1, mPaintCircleDebug);
//			 }
			 
//			 if(line1 != null && line2 != null)
//			 {
//				 Paint PaintDebugaa  = new Paint();
//				 PaintDebugaa.setColor(Color.BLUE);
//				 canvas.drawLine((float)line1.getX1(), (float)line1.getY1(), (float)line1.getX2(), (float)line1.getY2(), PaintDebugaa);
//				 canvas.drawLine((float)line2.getX1(), (float)line2.getY1(), (float)line2.getX2(), (float)line2.getY2(), PaintDebugaa);
//			 }
			 
//			 canvas.drawText("isRectangle == "+isRectangle, 30, 50, shadowPaint);
//			 canvas.drawText("isCircle == "+isCircle,30, 100, shadowPaint);
//			 canvas.drawText("Arrow == "+isArrow,30, 150, shadowPaint);
//			 canvas.drawText("Blur Line == "+isZigZag,30, 200, shadowPaint);
//		 }
		 
		 canvas.restore();
	}
	
	private void processBitmap(CustomerShapeView shape) {
		// TODO Auto-generated method stub
		if (shape.mType != Configs.BLUR_TYPE){
			return;
		}
		
		if(((BlurObject)shape).getBlurManager() == null){
			((BlurObject)shape).setBlurManager(_blurManager);
			((BlurObject)shape).setxMargin(xMainBitmap);
			((BlurObject)shape).setyMargin(yMainBitmap);
		}
		
		if (shape.isVisible){
			((BlurObject)shape).showBlurObj();
		} else {
			((BlurObject)shape).hideBlurObj();
		}
	}

	public void resetData()
	{
		mPath.reset();
		listIndexCornerPath.removeAll(listIndexCornerPath);
		listPoint.removeAll(listPoint);
		listIntersection.removeAll(listIntersection);
		listArrowParagraph.removeAll(listArrowParagraph);
		listPointArrow.removeAll(listPointArrow);
		isStartDirection = 0;
		isDirection = 0;
		countChangeDirection = 0;
		countSquareCorner = 0;
		isRectangle = false;
		isCircle = false;
		isTouchOnShape = false;
		isTouchOnPointFocusShape = false;
		mXStart = 0;
        mYStart = 0;
        distanceZigZag = 0;
        
	}
	
	public void resetDataWhenUserDrawOtherImage()
	{
		resetData();
//		listRootShape.removeAll(listRootShape);
	}
	
	private boolean FocusOnRectangleShape(int indexShapeInList,float xTouch,float yTouch)
	{
		boolean flag = false;
		RectangleObject rectangle = (RectangleObject)listRootShape.get(indexShapeInList);
		if(DrawUtils.checkTouchPointInSideRectangleShape(rectangle.x,rectangle.y,rectangle.w,rectangle.h, xTouch, yTouch))
		{	
			if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
			{
				if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
				{
					isTouchOnPointFocusShape = checkTouchOnPointFocusShape((int)xTouch,(int)yTouch);
					if(isTouchOnPointFocusShape)
					{
						return true;
					}
				}
				listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
			}
			indexCurrentTouchOnShape = indexShapeInList;
			mCurrentTypeShape = Configs.RECTANGLE_TYPE;
			flag = true;
			rectangle.isFocus = true;
			return flag;
		}
		return flag;
	}
	
	public String getTextObjValue()
	{
		String value = "";
		try {
			TextObject textObj = (TextObject)listRootShape.get(indexCurrentTouchOnShape);
			value = textObj.getText();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
	
	private boolean FocusOnText(int indexShapeInList,float xTouch,float yTouch)
	{
		boolean flag = false;
		TextObject textObj = (TextObject)listRootShape.get(indexShapeInList);
		Log.e("textObj.x", "textObj.x == "+textObj.x + "  textObj.w === "+textObj.w + "xtouch == "+xTouch + " ytouch == "+yTouch + " textObj.y == "+textObj.y + " textObj.h == "+textObj.h);
		if(DrawUtils.checkTouchPointInSideRectangleShape(textObj.x,textObj.y,textObj.w,textObj.h, xTouch, yTouch))
		{	
			if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
			{
				if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
				{
					isTouchOnPointFocusShape = checkTouchOnPointFocusShape((int)xTouch,(int)yTouch);
					if(isTouchOnPointFocusShape)
					{
						return true;
					}
				}
				listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
			}
			indexCurrentTouchOnShape = indexShapeInList;
			mCurrentTypeShape = Configs.ADD_TEXT_TYPE;
			flag = true;
			textObj.isFocus = true;
			return flag;
		}
		return flag;
	}
	
	private boolean FocusOnBlurShape(int indexShapeInList,float xTouch,float yTouch)
	{
		boolean flag = false;
		BlurObject blur = (BlurObject)listRootShape.get(indexShapeInList);
		if(DrawUtils.checkTouchPointInSideRectangleShape(blur.x,blur.y,blur.w,blur.h, xTouch, yTouch))
		{	
			if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
			{
				if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
				{
					isTouchOnPointFocusShape = checkTouchOnPointFocusShape((int)xTouch,(int)yTouch);
					if(isTouchOnPointFocusShape)
					{
						return true;
					}
				}
				listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
			}
			indexCurrentTouchOnShape = indexShapeInList;
			mCurrentTypeShape = Configs.BLUR_TYPE;
			flag = true;
			blur.isFocus = true;
			return flag;
		}
		return flag;
	}
	
	private boolean FocusOnFreeStyleShape(int indexShapeInList,float xTouch,float yTouch)
	{
		boolean flag = false;
		FreeStyleObject freeStyle = (FreeStyleObject)listRootShape.get(indexShapeInList);
		if(DrawUtils.checkTouchPointInSideRectangleShape(freeStyle.getRectFBodyPath().left,freeStyle.getRectFBodyPath().top,freeStyle.getRectFBodyPath().right,freeStyle.getRectFBodyPath().bottom, xTouch, yTouch))
		{	
			if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
			{
				if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
				{
					isTouchOnPointFocusShape = checkTouchOnPointFocusShape((int)xTouch,(int)yTouch);
					if(isTouchOnPointFocusShape)
					{
						return true;
					}
				}
				listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
			}
			indexCurrentTouchOnShape = indexShapeInList;
			mCurrentTypeShape = Configs.FREE_STYLE_TYPE;
			flag = true;
			freeStyle.isFocus = true;
			return flag;
		}
		
		FreeStyleObject freeStyleObj = (FreeStyleObject)listRootShape.get(indexShapeInList);
		for(int i = 0 ; i < freeStyleObj.getListPointBody().size();i++)
		{
			FlaotPoint point = freeStyleObj.getListPointBody().get(i);
			if(DrawUtils.checkTouchPointInSideRectangleShape(point.getX() - freeStyleObj.getAlphaResize(), point.getY() - freeStyleObj.getAlphaResize(),point.getX() + freeStyleObj.getAlphaResize(), point.getY() + freeStyleObj.getAlphaResize(), xTouch, yTouch))
			{	
				if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
				{
					if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
					{
						isTouchOnPointFocusShape = checkTouchOnPointFocusShape((int)xTouch,(int)yTouch);
						if(isTouchOnPointFocusShape)
						{
							return true;
						}
					}
					listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
				}
				indexCurrentTouchOnShape = indexShapeInList;
				mCurrentTypeShape = Configs.FREE_STYLE_TYPE;
				flag = true;
				freeStyleObj.isFocus = true;
				return flag;
			}
		}
		return flag;
		
	}
	
	private boolean FocusOnArrowShape(int indexShapeInList,float xTouch,float yTouch)
	{
		boolean flag = false;
		ArrowObject arrow = (ArrowObject)listRootShape.get(indexShapeInList);
		
		if(DrawUtils.checkTouchPointInSideRectangleShape(arrow.getArrowHeaderRectF().left,arrow.getArrowHeaderRectF().top,arrow.getArrowHeaderRectF().right,arrow.getArrowHeaderRectF().bottom, xTouch, yTouch))
		{	
			if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
			{
				if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
				{
					isTouchOnPointFocusShape = checkTouchOnPointFocusShape((int)xTouch,(int)yTouch);
					if(isTouchOnPointFocusShape)
					{
						return true;
					}
				}
				listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
			}
			indexCurrentTouchOnShape = indexShapeInList;
			mCurrentTypeShape = Configs.ARROW_TYPE;
			flag = true;
			arrow.isFocus = true;
			return flag;
		}
		
		for(int i = 0 ; i < arrow.getListPointBody().size();i++)
		{
			FlaotPoint point = arrow.getListPointBody().get(i);
			if(DrawUtils.checkTouchPointInSideRectangleShape(point.getX() - arrow.getAlphaResize(), point.getY() - arrow.getAlphaResize(),point.getX() + arrow.getAlphaResize(), point.getY() + arrow.getAlphaResize(), xTouch, yTouch))
			{	
				if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
				{
					if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
					{
						isTouchOnPointFocusShape = checkTouchOnPointFocusShape((int)xTouch,(int)yTouch);
						if(isTouchOnPointFocusShape)
						{
							return true;
						}
					}
					listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
				}
				indexCurrentTouchOnShape = indexShapeInList;
				mCurrentTypeShape = Configs.ARROW_TYPE;
				flag = true;
				arrow.isFocus = true;
				return flag;
			}
		}
		
		return flag;
	}
	
	private boolean FocusOnEllipseShape(int indexShapeInList,float xTouch,float yTouch)
	{
		boolean flag = false;
		EllipseObject ellipse = (EllipseObject)listRootShape.get(indexShapeInList);
		if(DrawUtils.checkTouchPointInSideRectangleShape(ellipse.getRectF().left,ellipse.getRectF().top,ellipse.getRectF().right,ellipse.getRectF().bottom, xTouch, yTouch))
		{	
			if(indexCurrentTouchOnShape != indexShapeInList && indexCurrentTouchOnShape < listRootShape.size())
			{
				if(listRootShape.get(indexCurrentTouchOnShape).isFocus)
				{
					isTouchOnPointFocusShape = checkTouchOnPointFocusShape(xTouch,yTouch);
					if(isTouchOnPointFocusShape)
					{
						return true;
					}
				}
				listRootShape.get(indexCurrentTouchOnShape).isFocus = false;
			}
			indexCurrentTouchOnShape = indexShapeInList;
			mCurrentTypeShape = Configs.CIRCLE_TYPE;
			flag = true;
			ellipse.isFocus = true;
			return flag;
		}
		return flag;
	}
	
	public boolean checkFocusOnShape(float xTouch,float yTouch)
	{
		boolean flag = false;
		for(int i = listRootShape.size() - 1 ; i >= 0 ; i--)
		{
			if (listRootShape.get(i).isVisible){
				int typeShape = listRootShape.get(i).mType;
	    		switch (typeShape) {
				case Configs.RECTANGLE_TYPE:
					if(FocusOnRectangleShape(i,xTouch,yTouch))
					{
						return true;
					}
					break;
				case Configs.CIRCLE_TYPE:
					if(FocusOnEllipseShape(i, xTouch, yTouch))
					{
						return true;
					}
					break;
				case Configs.BLUR_TYPE:
					if(FocusOnBlurShape(i, xTouch, yTouch))
					{
						return true;
					}
					break;
				case Configs.ARROW_TYPE:
					if(FocusOnArrowShape(i, xTouch, yTouch))
					{
						return true;
					}
					break;
				case Configs.FREE_STYLE_TYPE:
					if(FocusOnFreeStyleShape(i, xTouch, yTouch))
					{
						return true;
					}
					break;
				case Configs.ADD_TEXT_TYPE:
					if(FocusOnText(i, xTouch, yTouch))
					{
						return true;
					}
					break;
				default:
					
					break;
				}
			}
		}
		return flag;
	}

	
	
	private void touch_start(float x, float y) {
		 
		 if(mainBitmap == null || !isEdit)
		 {
			 if(listener != null)
			 listener.isShowingMenu(isShowingMenu);
			 return;
		 }
		 if(isShowingMenu)
		 {
			 isNotDrawWhenCloseMenu = true;
			 if(listener != null)
			 listener.isShowingMenu(isShowingMenu);
		 }
		 
		 if(disableTouchEventWhenShowSettingPaint)
		 {
			 return ;
		 }
		 
		 //Do nothing if touch in current focus shape
		 if (DrawUtils.isTouchOnShape(mCurrentShape, x, y)){
			 return;
		 } else {
			 //Show previous shape
			 if(mCurrentShape != null){
				 showShapeAndFocus(mCurrentShape, false, false);
				 CustomerShapeView previousShape = getPreviousShape(mCurrentShape._shapeId);
				 showShapeAndFocus(previousShape, true, false);
			 }
		 }
		 
		 resetData();
		 mXStart = x;
         mYStart = y;
         isPaintDebug = false;
         isRectangle = false;
         isCircle = false;
         isArrow = false;
         isZigZag = false;
         isTouchOnShape = checkFocusOnShape(x, y);
//         Log.e("isTouchOnShape", "isTouchOnShape ========= "+isTouchOnShape);
         isTouchOnPointFocusShape = checkTouchOnPointFocusShape(x,y);
         if(isTouchOnShape || isTouchOnPointFocusShape)
         {
        	 jumpShapeToTopLayer();
        	 return;
         }
         clearFocusAllShape();
         mPath.moveTo(x, y);
    }
	
	
	
	public boolean TouchOnPointRectangleFocus(float x,float y)
	{
		boolean flag = false;
		RectangleObject rectangle = (RectangleObject)mCurrentShape;
    	if(rectangle.isFocus)
    	{	 
        	 for(int i = 0 ; i < rectangle.getmListDot().size();i++)
        	 {
        		 if(DrawUtils.checkTouchPointInSideDotPoint(rectangle.getmListDot().get(i), x, y))
        		 {
        			 mIndexDotCurrentShape = i;
        			 return true;
        		 }
        	 }
    	 }
    	return flag;
	}
	
	public boolean TouchOnPointBlurFocus(float x,float y)
	{
		boolean flag = false;
		BlurObject blur = (BlurObject)mCurrentShape;
    	if(blur.isFocus)
    	{	 
        	 for(int i = 0 ; i < blur.getmListDot().size();i++)
        	 {
        		 if(DrawUtils.checkTouchPointInSideDotPoint(blur.getmListDot().get(i), x, y))
        		 {
        			 mIndexDotCurrentShape = i;
        			 return true;
        		 }
        	 }
    	 }
    	return flag;
	}
	
	public boolean TouchOnPointArrowFocus(float x,float y)
	{
		boolean flag = false;
		ArrowObject arrow = (ArrowObject)mCurrentShape;
    	if(arrow.isFocus)
    	{	 
        	 for(int i = 0 ; i < arrow.getmListDot().size();i++)
        	 {
        		 if(DrawUtils.checkTouchPointInSideDotPoint(arrow.getmListDot().get(i), x, y))
        		 {
        			 mIndexDotCurrentShape = i;
        			 return true;
        		 }
        	 }
    	 }
    	return flag;
	}
	
	public boolean TouchOnPointFreeStyleObjectFocus(float x,float y)
	{
		boolean flag = false;
		FreeStyleObject freeStyleObj = (FreeStyleObject)mCurrentShape;
    	if(freeStyleObj.isFocus)
    	{	 
        	 for(int i = 0 ; i < freeStyleObj.getmListDot().size();i++)
        	 {
        		 if(DrawUtils.checkTouchPointInSideDotPoint(freeStyleObj.getmListDot().get(i), x, y))
        		 {
        			 mIndexDotCurrentShape = i;
        			 return true;
        		 }
        	 }
    	 }
    	return flag;
	}
	
	public boolean TouchOnPointEllipseFocus(float x,float y)
	{
		boolean flag = false;
		EllipseObject ellipse = (EllipseObject)mCurrentShape;
		if(ellipse.isFocus)
    	{	 
        	 for(int i = 0 ; i < ellipse.getmListDot().size();i++)
        	 {
        		 if(DrawUtils.checkTouchPointInSideDotPoint(ellipse.getmListDot().get(i), x, y))
        		 {
        			 mIndexDotCurrentShape = i;
        			 return true;
        		 }
        	 }
    	 }
    	return flag;
	}
	
	
	/**
	 * @param x
	 * @param y
	 * @return true if user touch on current shape focused
	 */
	public boolean checkTouchOnPointFocusShape(float x,float y)
	{
		boolean flag = false;
		 if(indexCurrentTouchOnShape < listRootShape.size() && mCurrentShape != null)
         {
			 int typeShape = mCurrentShape.mType;
			 switch (typeShape) {
				case Configs.RECTANGLE_TYPE:
					if(TouchOnPointRectangleFocus(x,y))
					{
						return true;
					}
					break;
				case Configs.CIRCLE_TYPE:
					if(TouchOnPointEllipseFocus(x,y))
					{
						return true;
					}
					break;
				case Configs.BLUR_TYPE:
					if(TouchOnPointBlurFocus(x,y))
					{
						return true;
					}
					break;
				case Configs.ARROW_TYPE:
					if(TouchOnPointArrowFocus(x,y))
					{
						return true;
					}
					break;
				case Configs.FREE_STYLE_TYPE:
					if(TouchOnPointFreeStyleObjectFocus(x,y))
					{
						return true;
					}
					break;
				default:
					
					break;
				}
         }
		 return flag;
	}
	
	public void jumpShapeToTopLayer()
	{
		clearFocusAllShape();
		 // get object at indexcurrent root shape jump to top layer
	   	 if(indexCurrentTouchOnShape < listRootShape.size())
	   	 {
	   		 CustomerShapeView currentTouchShape = listRootShape.get(indexCurrentTouchOnShape);
	   		 showShapeAndFocus(currentTouchShape, false, false);
	   		 	   		 
	   		 if(mCurrentShape != null && mCurrentShape.isFocus
	   				 && mCurrentShape._shapeId != currentTouchShape._shapeId){
	   			 //Clear focus previous shape
	   			 CustomerShapeView previousShape = getPreviousShape(mCurrentShape._shapeId);
	   			 if(previousShape != null){
	   				 showShapeAndFocus(previousShape, true, false);
	   			 }
	   		 }
	   		 
	   		mCurrentShape = currentTouchShape.copyCustomerView();
	   		mCurrentShape._shapeId = currentTouchShape._shapeId;
	   		mCurrentShape._action = ShapeAction.MoveShapeToTop;
	   		showShapeAndFocus(mCurrentShape, true, true);
	   	 }
	}
	
	private void showShapeAndFocus(CustomerShapeView shape, boolean isVisible, boolean isFocus){
		if(shape != null){
			shape.isFocus = isFocus;
			shape.isVisible = isVisible;
		}
	}
	
    private void touch_move(float x, float y) {
    	
    	if(mainBitmap == null || !isEdit )
		{
			 return;
		}
    	
    	if(isNotDrawWhenCloseMenu)
    	{
    		return ;
    	}
    	
    	if(disableTouchEventWhenShowSettingPaint)
		{
			 return ;
		}
    	
    	if(isTouchOnPointFocusShape)
    	{
    		resizeCurrentShapeFocus(x, y);
    		return;
    	}
    	
    	if(isTouchOnShape)
    	{
    		if(listener != null)
    		{
    			listener.showDeleteBar(true,isCheckDeleteCurrentShape(y));
    		}
    		MoveShape(x, y);
    		return;
    	}
    	
    	float dx = Math.abs(x - mXStart);
        float dy = Math.abs(y - mYStart);
        if (checkTouchMovingToDraw(x, y, mXStart, mYStart)) {
            mPath.quadTo(mXStart, mYStart, (x + mXStart)/2, (y + mYStart)/2);
            mXStart = x;
            mYStart = y;
            distanceZigZag += dx;
        }
        
        boolean autoDetect = PreferenceConnector.readBoolean(mContext, Configs.BUNDLE_AUTO_DETECT, false);
        if(autoDetect)
        {
        	getListIndexPointCornerPath();
        }
    }
    
    boolean checkTouchMovingToDraw(float x1, float y1, float x2, float y2){
    	float dx = Math.abs(x1 - x2);
        float dy = Math.abs(y1 - y2);
        return (dx>=TOUCH_TOLERANCE || dy>=TOUCH_TOLERANCE);
    }
    
    public void deleteCurrentShape()
    {
    	if(isTouchOnShape && indexCurrentTouchOnShape < listRootShape.size())
    	{
    		
    		mCurrentShape.isFocus = mCurrentShape.isVisible = false;
    		mCurrentShape._action = ShapeAction.DeleteShape;
    		addNewSharp(mCurrentShape);
    		mCurrentShape = null;
    		
    		int indexCurrentDraw = PreferenceConnector.readInteger(mContext, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
    		// update data when user delete shape on current screen
    		if(listener != null)
    		{
    			listener.getIndexDeleteShapeInCurrentDraw(indexCurrentDraw, indexCurrentTouchOnShape);
    		}
    		indexCurrentTouchOnShape = 0;
			mCurrentTypeShape = 0;
			isTouchOnShape = false;
			isTouchOnPointFocusShape = false;
			checkHeaderStatus();
			postInvalidate();
    	}
    }
    
    public boolean isCheckDeleteCurrentShape(float y)
    {
    	boolean isFlagDelete = false;
    	if(y < 0 )
    	{
    		isFlagDelete = true;
    	}
//    	float mY_ABS = Math.abs(y);
//    	float limitHeader = MainActivity.getInstance().mWorkBox.getY();
//    	float mY_OutOf_Height_Header  = limitHeader - mY_ABS;
//    	if(limitHeader > 0 && y < 0)
//    	{
//    		if(mY_OutOf_Height_Header < alphaDistanceDelete)
//        	{
//        		isFlagDelete = true;
//        	}
//    	}
//    	else
//    	{
//    		if(y < alphaDistanceDelete)
//    		{
//    			isFlagDelete = true;
//    		}
//    	}
    	
//    	switch (mCurrentTypeShape) {
//		case Configs.RECTANGLE_TYPE:
//			RectangleObject rectangle = (RectangleObject)listRootShape.get(indexCurrentTouchOnShape);
//			if(rectangle.y < alphaDistanceDelete)
//			{
//				isFlagDelete = true;
//			}
//			break;
//		case Configs.CIRCLE_TYPE:
//			EllipseObject ellipse = (EllipseObject)listRootShape.get(indexCurrentTouchOnShape);
//			if(ellipse.getRectF().top < alphaDistanceDelete)
//			{
//				isFlagDelete = true;
//			}
//			break;
//		case Configs.BLUR_TYPE:
//			BlurObject blur = (BlurObject)listRootShape.get(indexCurrentTouchOnShape);
//			if(blur.y < alphaDistanceDelete)
//			{
//				isFlagDelete = true;
//			}
//			break;
//		case Configs.ARROW_TYPE:
//			ArrowObject arrow = (ArrowObject)listRootShape.get(indexCurrentTouchOnShape);
//			if(arrow.getTailY() < alphaDistanceDelete || arrow.getTipY() < alphaDistanceDelete)
//			{
//				isFlagDelete = true;
//			}
//			break;
//		case Configs.FREE_STYLE_TYPE:
//			FreeStyleObject freeStyleObj = (FreeStyleObject)listRootShape.get(indexCurrentTouchOnShape);
//			if(freeStyleObj.getRectFBodyPath().top < alphaDistanceDelete)
//			{
//				isFlagDelete = true;
//			}
//			break;
//		case Configs.ADD_TEXT_TYPE:
//			TextObject textObj = (TextObject)listRootShape.get(indexCurrentTouchOnShape);
//			if(textObj.y < alphaDistanceDelete)
//			{
//				isFlagDelete = true;
//			}
//			break;
//		}
    	return isFlagDelete;
    }
    
    private void touch_up(float x, float y)
    {
	    	if(mainBitmap == null || !isEdit )
			{
				 return;
			}
	    	
	    	if(isNotDrawWhenCloseMenu)
	    	{
	    		isNotDrawWhenCloseMenu = false;
	    		return ;
	    	}
	    	
	    	if(disableTouchEventWhenShowSettingPaint)
			{
				 return ;
			}
	    	if(isTouchOnShape || isTouchOnPointFocusShape)
	    	{
	    		if(isCheckDeleteCurrentShape(y))
	    		{
	    			deleteCurrentShape();
	    		} else {
	    			if (mCurrentShape != null 
	    					&& mCurrentShape._action == ShapeAction.MoveOrResizeShape){
		    			mCurrentShape._action = ShapeAction.MoveOrResizeShape;
		    			mCurrentShape.isVisible = mCurrentShape.isFocus = true;
		    			clearRedoSharpFromIndex(_undoRedoCurrentIndex);
		    			addNewSharp(mCurrentShape);
		    			mCurrentShape = null;
	    			}
	    		}
				listener.showDeleteBar(false,false);
	    		return ;
	    	}
	    	
	    	mPath.lineTo(mXStart, mYStart);
	    //  isPaintDebug = true;
	    //  check auto detect 
	    	boolean autoDetect = PreferenceConnector.readBoolean(mContext, Configs.BUNDLE_AUTO_DETECT, false);
	    	if(autoDetect)
	    	{
	    		getCollectionPointsCornerPath();
	        	detectShapeAndCreate();
	    	}
	    	else
	    	{
	    		createFreeStyleDraw();
	    	}
	    	
//		   // get current index draw
//		   int indexDraw = PreferenceConnector.readInteger(mContext, Configs.FLAG_INDEX_CURRENT_CHOOSE_DRAW, 0);
//		   if(indexDraw <= MainActivity.getInstance().mManagerObject.getListChooseDrawObject().size())
//		   {
//			   ArrayList<CustomerShapeView> listtemp = MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexDraw).getListRootShape();
//			   MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexDraw).getListRootShape().removeAll(listtemp);
//			   MainActivity.getInstance().mManagerObject.getListChooseDrawObject().get(indexDraw).getListRootShape().addAll(listRootShape);
//		   }
	       
	       checkHeaderStatus();
	       resetData();
    }
    
    public void clearFocusAllShape()
    {
    	for(int i = 0 ; i < listRootShape.size();i++)
    	{
    		listRootShape.get(i).isFocus = false;
    	}
    	if(mCurrentShape != null){
    		CustomerShapeView previous = getPreviousShape(mCurrentShape._shapeId);
    		if (previous != null){
    			previous.isVisible = true;
    		}
    		mCurrentShape = null;
    	}
    }
    
    private void moveRectangle(float x,float y)
    {
    	float detaX = mXStart - x;
		float detaY = mYStart - y;
		RectangleObject rectangle = (RectangleObject)mCurrentShape;
		float xTemp = rectangle.x - detaX;
		float yTemp = rectangle.y - detaY;
		float wTemp = rectangle.w - detaX;
		float hTemp = rectangle.h - detaY;
//		boolean isOutHorizontal = checkOutHorizontalScreen(xTemp, wTemp, 0, wMainView);
//		boolean isOutVerticalScreen = checkOutVerticalScreen(yTemp, hTemp, 0, hMainView);
		boolean isOutHorizontal = false;
		boolean isOutVerticalScreen = false;
		if(!isOutHorizontal || !isOutVerticalScreen)
		{
			if(!isOutHorizontal)
			{
				rectangle.x -= detaX;
				rectangle.w -= detaX;
				for(int i = 0 ; i < rectangle.getmListDot().size(); i++)
				{
					float xDot = rectangle.getmListDot().get(i).getmX() - detaX;
					rectangle.getmListDot().get(i).setmX(xDot);
				}
			}
			if(!isOutVerticalScreen)
			{
				rectangle.y -= detaY;
				rectangle.h -= detaY;
				for(int i = 0 ; i < rectangle.getmListDot().size(); i++)
				{
					float yDot = rectangle.getmListDot().get(i).getmY() - detaY;
					rectangle.getmListDot().get(i).setmY(yDot);
				}
			}
			mXStart = x;
			mYStart = y;
		}
    }
    
    private void moveBlur(float x,float y)
    {
    	float detaX = mXStart - x;
		float detaY = mYStart - y;
		BlurObject blur = (BlurObject)mCurrentShape;
		boolean isOutHorizontal = false;
		boolean isOutVerticalScreen = false;
		if(!isOutHorizontal || !isOutVerticalScreen)
		{
			boolean flagReloadBlurBitmap = false;
			if(!isOutHorizontal)
			{
				blur.x -= detaX;
				blur.w -= detaX;
				for(int i = 0 ; i < blur.getmListDot().size(); i++)
				{
					float xDot = blur.getmListDot().get(i).getmX() - detaX;
					blur.getmListDot().get(i).setmX(xDot);
				}
				
				flagReloadBlurBitmap = true;
				
			}
			if(!isOutVerticalScreen)
			{
				blur.y -= detaY;
				blur.h -= detaY;
				for(int i = 0 ; i < blur.getmListDot().size(); i++)
				{
					float yDot = blur.getmListDot().get(i).getmY() - detaY;
					blur.getmListDot().get(i).setmY(yDot);
				}
				flagReloadBlurBitmap = true;
			}
			
			if(flagReloadBlurBitmap)
			{
				blur.resizeBlurBitmap(blur.x, blur.y, blur.w, blur.h);
			}
			
			mXStart = x;
			mYStart = y;
		}
    }
    
    private void moveFreeStyleShape(float x,float y)
    {
    	float detaX = mXStart - x;
		float detaY = mYStart - y;
		FreeStyleObject freeStyle = (FreeStyleObject)mCurrentShape;
		float xTemp = freeStyle.getRectFBodyPath().left - detaX;
		float yTemp = freeStyle.getRectFBodyPath().top - detaY;
		float wTemp = freeStyle.getRectFBodyPath().right - detaX;
		float hTemp = freeStyle.getRectFBodyPath().bottom - detaY;
//		boolean isOutHorizontal = checkOutHorizontalScreen(xTemp, wTemp, 0, wMainView);
//		boolean isOutVerticalScreen = checkOutVerticalScreen(yTemp, hTemp, 0, hMainView);
		boolean isOutHorizontal = false;
		boolean isOutVerticalScreen = false;
		if(!isOutHorizontal || !isOutVerticalScreen)
		{
			boolean flagReloadBlurBitmap = false;
			if(!isOutHorizontal)
			{
				flagReloadBlurBitmap = true;
			}
			if(!isOutVerticalScreen)
			{
				flagReloadBlurBitmap = true;
			}
			
			if(flagReloadBlurBitmap)
			{
				freeStyle.initNewPointPositionMoveShape(detaX, detaY);
			}
			
			mXStart = x;
			mYStart = y;
		}
    }
    
    private void moveText(float x,float y)
    {
    	float detaX = mXStart - x;
		float detaY = mYStart - y;
		TextObject textObj = (TextObject)mCurrentShape;
		float xTemp = textObj.x - detaX;
		float yTemp = textObj.y - detaY;
		float wTemp = textObj.w - detaX;
		float hTemp = textObj.h - detaY;
//		boolean isOutHorizontal = checkOutHorizontalScreen(xTemp, wTemp, 0, wMainView);
//		boolean isOutVerticalScreen = checkOutVerticalScreen(yTemp, hTemp, 0, hMainView);
		boolean isOutHorizontal = false;
		boolean isOutVerticalScreen = false;
		if(!isOutHorizontal || !isOutVerticalScreen)
		{
			if(!isOutHorizontal)
			{
				textObj.x -= detaX;
				textObj.w -= detaX;
			}
			if(!isOutVerticalScreen)
			{
				textObj.y -= detaY;
				textObj.h -= detaY;
			}
			mXStart = x;
			mYStart = y;
		}
    }
    
    private void moveArrow(float x,float y)
    {
    	float detaX = mXStart - x;
		float detaY = mYStart - y;
		ArrowObject arrow = (ArrowObject)mCurrentShape;
		float xTemp = arrow.getArrowBodyRectF().left - detaX;
		float yTemp = arrow.getArrowBodyRectF().top - detaY;
		float wTemp = arrow.getArrowBodyRectF().right - detaX;
		float hTemp = arrow.getArrowBodyRectF().bottom - detaY;
//		boolean isOutHorizontal = checkOutHorizontalScreen(xTemp, wTemp, 0, wMainView);
//		boolean isOutVerticalScreen = checkOutVerticalScreen(yTemp, hTemp, 0, hMainView);
		boolean isOutHorizontal = false;
		boolean isOutVerticalScreen = false;
		if(!isOutHorizontal || !isOutVerticalScreen)
		{
			boolean flagReloadBlurBitmap = false;
			if(!isOutHorizontal)
			{
//				arrow.x -= detaX;
				arrow.setTailX(arrow.getTailX() - detaX);
				arrow.setTipX(arrow.getTipX() - detaX);
				for(int i = 0 ; i < arrow.getmListDot().size(); i++)
				{
					float xDot = arrow.getmListDot().get(i).getmX() - detaX;
					arrow.getmListDot().get(i).setmX(xDot);
				}
				flagReloadBlurBitmap = true;
			}
			if(!isOutVerticalScreen)
			{
//				arrow.y -= detaY;
				arrow.setTailY(arrow.getTailY() - detaY);
				arrow.setTipY(arrow.getTipY() - detaY);
				for(int i = 0 ; i < arrow.getmListDot().size(); i++)
				{
					float yDot = arrow.getmListDot().get(i).getmY() - detaY;
					arrow.getmListDot().get(i).setmY(yDot);
				}
				flagReloadBlurBitmap = true;
			}
			
			if(flagReloadBlurBitmap)
			{
//				blur.resizeBlurBitmap(blur.x, blur.y, blur.w, blur.h);
				arrow.initNewPointPositionMoveShape();
			}
			
			mXStart = x;
			mYStart = y;
		}
    }
    
    private void moveEllipse(float x,float y)
    {
    	float detaX = mXStart - x;
		float detaY = mYStart - y;
		EllipseObject ellipse = (EllipseObject)mCurrentShape;
		float xTemp = ellipse.getRectF().left - detaX;
		float yTemp = ellipse.getRectF().top - detaY;
		float wTemp = ellipse.getRectF().right - detaX;
		float hTemp = ellipse.getRectF().bottom - detaY;
//		boolean isOutHorizontal = checkOutHorizontalScreen(xTemp, wTemp, 0, wMainView);
//		boolean isOutVerticalScreen = checkOutVerticalScreen(yTemp, hTemp, 0, hMainView);
		boolean isOutHorizontal = false;
		boolean isOutVerticalScreen = false;
		if(!isOutHorizontal || !isOutVerticalScreen)
		{
			if(!isOutHorizontal)
			{
				ellipse.getRectF().left -= detaX;
				ellipse.x -= detaX;
				ellipse.getRectF().right-= detaX;
				ellipse.w -= detaX;
				for(int i = 0 ; i < ellipse.getmListDot().size(); i++)
				{
					float xDot = ellipse.getmListDot().get(i).getmX() - detaX;
					ellipse.getmListDot().get(i).setmX(xDot);
				}
			}
			if(!isOutVerticalScreen)
			{
				ellipse.getRectF().top -= detaY;
				ellipse.y -= detaY;
				ellipse.getRectF().bottom -= detaY;
				ellipse.h -= detaY;
				for(int i = 0 ; i < ellipse.getmListDot().size(); i++)
				{
					float yDot = ellipse.getmListDot().get(i).getmY() - detaY;
					ellipse.getmListDot().get(i).setmY(yDot);
				}
			}
			mXStart = x;
			mYStart = y;
		}
    }
    
    public void MoveShape(float x,float y)
    {
    	if (mCurrentShape == null)
    		return;
    	mCurrentShape._action = ShapeAction.MoveOrResizeShape;
		switch (mCurrentTypeShape) {
		case Configs.RECTANGLE_TYPE:
			moveRectangle(x, y);
			break;
		case Configs.CIRCLE_TYPE:
			moveEllipse(x, y);
			break;
		case Configs.BLUR_TYPE:
			moveBlur(x, y);
			break;
		case Configs.ARROW_TYPE:
			moveArrow(x, y);
			break;
		case Configs.FREE_STYLE_TYPE:
			moveFreeStyleShape(x, y);
			break;
		case Configs.ADD_TEXT_TYPE:
			moveText(x, y);
			break;
		}
    }
    
    public void resizeRectangle(float x,float y)
    {
    	RectangleObject rectangle = (RectangleObject)mCurrentShape;
		float detaX = mXStart - x;
		float detaY = mYStart - y;
		switch (mIndexDotCurrentShape) {
		case Configs.idDotOne:
			rectangle.x -= detaX;
			rectangle.y -= detaY;
			break;
		case Configs.idDotTwo:
			rectangle.y -= detaY;
			break;
		case Configs.idDotThree:
			rectangle.w -= detaX;
			rectangle.y -= detaY;
			break;
		case Configs.idDotFour:
			rectangle.w -= detaX;
			break;
		case Configs.idDotFive:
			rectangle.w -= detaX;
			rectangle.h -= detaY;
			break;
		case Configs.idDotSix:
			rectangle.h -= detaY;
			break;
		case Configs.idDotSeven:
			rectangle.x -= detaX;
			rectangle.h -= detaY;
			break;
		case Configs.idDotEight:
			rectangle.x -= detaX;
			break;
		}
		
		if(rectangle.x < 0)
		{
			rectangle.x = 0;
		}
		if(rectangle.y < 0)
		{
			rectangle.y = 0;
		}
		if(rectangle.w > wMainView)
		{
			rectangle.w = wMainView;
		}
		if(rectangle.h > hMainView)
		{
			rectangle.h = hMainView;
		}
		
		if(rectangle.x > rectangle.w - DrawUtils.ALPHA_RESIZE)
		{
			rectangle.x = rectangle.w - DrawUtils.ALPHA_RESIZE;
		}
		if(rectangle.y > rectangle.h - DrawUtils.ALPHA_RESIZE)
		{
			rectangle.y = rectangle.h - DrawUtils.ALPHA_RESIZE ;
		}
		if(rectangle.h - DrawUtils.ALPHA_RESIZE < rectangle.y)
		{
			rectangle.h = rectangle.y + DrawUtils.ALPHA_RESIZE;
		}
		if(rectangle.w - DrawUtils.ALPHA_RESIZE < rectangle.x)
		{
			rectangle.w = rectangle.x + DrawUtils.ALPHA_RESIZE;
		}
		rectangle.initNewPointPosition(rectangle.x, rectangle.y, rectangle.w, rectangle.h);
    }
    
    public void resizeBlur(float x,float y)
    {
    	BlurObject blur = (BlurObject)mCurrentShape;
		float detaX = mXStart - x;
		float detaY = mYStart - y;
		switch (mIndexDotCurrentShape) {
		case Configs.idDotOne:
			blur.x -= detaX;
			blur.y -= detaY;
			break;
		case Configs.idDotTwo:
			blur.y -= detaY;
			break;
		case Configs.idDotThree:
			blur.w -= detaX;
			blur.y -= detaY;
			break;
		case Configs.idDotFour:
			blur.w -= detaX;
			break;
		case Configs.idDotFive:
			blur.w -= detaX;
			blur.h -= detaY;
			break;
		case Configs.idDotSix:
			blur.h -= detaY;
			break;
		case Configs.idDotSeven:
			blur.x -= detaX;
			blur.h -= detaY;
			break;
		case Configs.idDotEight:
			blur.x -= detaX;
			break;
		}
		
		if(blur.x < 0)
		{
			blur.x = 0;
		}
		if(blur.y < 0)
		{
			blur.y = 0;
		}
		if(blur.w > wMainView)
		{
			blur.w = wMainView;
		}
		if(blur.h > hMainView)
		{
			blur.h = hMainView;
		}
		
		if(blur.x > blur.w - DrawUtils.ALPHA_RESIZE)
		{
			blur.x = blur.w - DrawUtils.ALPHA_RESIZE;
		}
		if(blur.y > blur.h - DrawUtils.ALPHA_RESIZE)
		{
			blur.y = blur.h - DrawUtils.ALPHA_RESIZE ;
		}
		if(blur.h - DrawUtils.ALPHA_RESIZE < blur.y)
		{
			blur.h = blur.y + DrawUtils.ALPHA_RESIZE;
		}
		if(blur.w - DrawUtils.ALPHA_RESIZE < blur.x)
		{
			blur.w = blur.x + DrawUtils.ALPHA_RESIZE;
		}
		
		blur.initNewPointPosition(blur.x, blur.y, blur.w, blur.h);
		blur.resizeBlurBitmap(blur.x, blur.y, blur.w, blur.h);
    }
    
    public void resizeArrow(float x,float y)
    {
    	ArrowObject arrow = (ArrowObject)mCurrentShape;
		arrow.initNewPointPosition(mIndexDotCurrentShape,x,y);
    }
    
    public void resizeFreeStyleObj(float x,float y)
    {
    	FreeStyleObject freeStyleObj = (FreeStyleObject)mCurrentShape;
    	float detaX = mXStart - x;
		float detaY = mYStart - y;
		
		switch (mIndexDotCurrentShape) {
		case Configs.idDotOne:
			freeStyleObj.x -= detaX;
			freeStyleObj.y -= detaY;
			break;
		case Configs.idDotTwo:
			freeStyleObj.y -= detaY;
			break;
		case Configs.idDotThree:
			freeStyleObj.w -= detaX;
			freeStyleObj.y -= detaY;
			break;
		case Configs.idDotFour:
			freeStyleObj.w -= detaX;
			break;
		case Configs.idDotFive:
			freeStyleObj.w -= detaX;
			freeStyleObj.h -= detaY;
			break;
		case Configs.idDotSix:
			freeStyleObj.h -= detaY;
			break;
		case Configs.idDotSeven:
			freeStyleObj.x -= detaX;
			freeStyleObj.h -= detaY;
			break;
		case Configs.idDotEight:
			freeStyleObj.x -= detaX;
			break;
		}
		
		if(freeStyleObj.x < 0)
		{
			freeStyleObj.x = 0;
		}
		if(freeStyleObj.y < 0)
		{
			freeStyleObj.y = 0;
		}
		if(freeStyleObj.w > wMainView)
		{
			freeStyleObj.w = wMainView;
		}
		if(freeStyleObj.h > hMainView)
		{
			freeStyleObj.h = hMainView;
		}
		
		if(freeStyleObj.x > freeStyleObj.w - DrawUtils.ALPHA_RESIZE)
		{
			freeStyleObj.x = freeStyleObj.w - DrawUtils.ALPHA_RESIZE;
		}
		if(freeStyleObj.y > freeStyleObj.h - DrawUtils.ALPHA_RESIZE)
		{
			freeStyleObj.y = freeStyleObj.h - DrawUtils.ALPHA_RESIZE ;
		}
		if(freeStyleObj.h - DrawUtils.ALPHA_RESIZE < freeStyleObj.y)
		{
			freeStyleObj.h = freeStyleObj.y + DrawUtils.ALPHA_RESIZE;
		}
		if(freeStyleObj.w - DrawUtils.ALPHA_RESIZE < freeStyleObj.x)
		{
			freeStyleObj.w = freeStyleObj.x + DrawUtils.ALPHA_RESIZE;
		}
    	
    	freeStyleObj.resizePath(freeStyleObj.x, freeStyleObj.y,freeStyleObj.w,freeStyleObj.h);
    }
    
    public void resizeEllipse(float x,float y)
    {
    	EllipseObject ellipse = (EllipseObject)mCurrentShape;
		float detaX = mXStart - x;
		float detaY = mYStart - y;
		switch (mIndexDotCurrentShape) {
		case Configs.idDotOne:
			ellipse.getRectF().left -= detaX;
			ellipse.getRectF().top -= detaY;
			break;
		case Configs.idDotTwo:
			ellipse.getRectF().top -= detaY;
			break;
		case Configs.idDotThree:
			ellipse.getRectF().right -= detaX;
			ellipse.getRectF().top -= detaY;
			break;
		case Configs.idDotFour:
			ellipse.getRectF().right -= detaX;
			break;
		case Configs.idDotFive:
			ellipse.getRectF().right -= detaX;
			ellipse.getRectF().bottom -= detaY;
			break;
		case Configs.idDotSix:
			ellipse.getRectF().bottom -= detaY;
			break;
		case Configs.idDotSeven:
			ellipse.getRectF().left -= detaX;
			ellipse.getRectF().bottom -= detaY;
			break;
		case Configs.idDotEight:
			ellipse.getRectF().left -= detaX;
			break;
		}
		
		if(ellipse.getRectF().left < 0)
		{
			ellipse.getRectF().left = 0;
		}
		if(ellipse.getRectF().top < 0)
		{
			ellipse.getRectF().top = 0;
		}
		if(ellipse.getRectF().right > wMainView)
		{
			ellipse.getRectF().right = wMainView;
		}
		if(ellipse.getRectF().bottom > hMainView)
		{
			ellipse.getRectF().bottom = hMainView;
		}
		
		if(ellipse.getRectF().left > ellipse.getRectF().right - DrawUtils.ALPHA_RESIZE)
		{
			ellipse.getRectF().left = ellipse.getRectF().right - DrawUtils.ALPHA_RESIZE;
		}
		if(ellipse.getRectF().top > ellipse.getRectF().bottom - DrawUtils.ALPHA_RESIZE)
		{
			ellipse.getRectF().top = ellipse.getRectF().bottom - DrawUtils.ALPHA_RESIZE ;
		}
		if(ellipse.getRectF().bottom - DrawUtils.ALPHA_RESIZE < ellipse.getRectF().top)
		{
			ellipse.getRectF().bottom = ellipse.getRectF().top + DrawUtils.ALPHA_RESIZE;
		}
		if(ellipse.getRectF().right - DrawUtils.ALPHA_RESIZE < ellipse.getRectF().left)
		{
			ellipse.getRectF().right = ellipse.getRectF().left + DrawUtils.ALPHA_RESIZE;
		}
		ellipse.initNewPointPosition(ellipse.getRectF().left, ellipse.getRectF().top, ellipse.getRectF().right, ellipse.getRectF().bottom);
    }
    
    private void resizeCurrentShapeFocus(float x,float y)
    {
    	if (mCurrentShape == null)
    		return;
    	mCurrentShape._action = ShapeAction.MoveOrResizeShape;
    	switch (mCurrentTypeShape) {
			case Configs.RECTANGLE_TYPE:
				resizeRectangle(x, y);
				break;
			case Configs.CIRCLE_TYPE:
				resizeEllipse(x, y);
				break;
			case Configs.BLUR_TYPE:
				resizeBlur(x, y);
				break;
			case Configs.ARROW_TYPE:
				resizeArrow(x, y);
				break;
			case Configs.FREE_STYLE_TYPE:
				resizeFreeStyleObj(x, y);
				break;
		}
    	
    	mXStart = x;
		mYStart = y;
    	
    }
    
    public boolean checkOutHorizontalScreen(float x1,float w1,float x2,float w2)
    {
    	boolean flag = true;
    	if(x2 <= x1 && w1 <= w2)
    	{
    		flag = false;
    	}
    	return flag;
    }
    
    public boolean checkOutVerticalScreen(float y1,float h1,float y2,float h2)
    {
    	boolean flag = true;
    	// do not check limit top
    	if(h1 <= h2)
    	{
    		flag = false;
    	}
    	return flag;
    }
    
    public void getCollectionPointsCornerPath()
    {
    	FlaotPoint[] points = getListPoints();
    	for(int i = 0; i < listIndexCornerPath.size();i++)
    	{
    		int index = listIndexCornerPath.get(i);
    		if(index < points.length)
    		{
    			FlaotPoint newPoint = points[index];
        		listPoint.add(newPoint);
//        		currentGesture.add(new Point(newPoint.getX(), newPoint.getY()));
    		}
    		else
    		{
    			if(index > 0)
    			{	
//	    			Log.e("index", "index ===== "+index);
	    			FlaotPoint newPoint = points[index-1];
	    			listPoint.add(newPoint);
//	    			currentGesture.add(new Point(newPoint.getX(), newPoint.getY()));
    			}
    		}
    	}
    }
    
    public void actionUndo()
    {
    	_undoRedoCurrentIndex--;
    	if(_undoRedoCurrentIndex != Configs.INVALID_INDEX && 
    			_undoRedoCurrentIndex < listRootShape.size()){
    		CustomerShapeView shape = listRootShape.get(_undoRedoCurrentIndex);
    		switch (shape._action) {
			case AddNewShape:
				showShapeAndFocus(shape, false, false);
				break;
			case MoveOrResizeShape:{
				showShapeAndFocus(shape, false, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, true, false);
				break;
    		}
			case DeleteShape:{
				showShapeAndFocus(shape, false, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, true, false);
				break;
    		}
			case ChangeBlurOpacity:{
				showShapeAndFocus(shape, false, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, true, false);
				break;
			}
			case ChangeTextColorOrStroke:{
				showShapeAndFocus(shape, false, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, true, false);
				break;
			}
			default:
				break;
			}
    		
    		if (mCurrentShape != null && mCurrentShape._shapeId == shape._shapeId){
				mCurrentShape = null;
			}
    	}
    	
    	checkHeaderStatus();
    	invalidate();
    }
    
    public void actionRedo()
    {
    	if(_undoRedoCurrentIndex != Configs.INVALID_INDEX && 
    			_undoRedoCurrentIndex < listRootShape.size()){
    		CustomerShapeView shape = listRootShape.get(_undoRedoCurrentIndex);
    		switch (shape._action) {
			case AddNewShape:
				showShapeAndFocus(shape, true, false);
				break;
			case MoveOrResizeShape:{
				showShapeAndFocus(shape, true, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, false, false);
				break;
			}
			case DeleteShape:{
				showShapeAndFocus(shape, true, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, false, false);
				break;
    		}
			case ChangeBlurOpacity:{
				showShapeAndFocus(shape, true, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, false, false);
				break;
			}
			case ChangeTextColorOrStroke:{
				showShapeAndFocus(shape, true, false);
				CustomerShapeView previous = getPreviousShape(shape._shapeId);
				showShapeAndFocus(previous, false, false);
				break;
			}
			default:
				break;
			}
    		if (mCurrentShape != null && mCurrentShape._shapeId == shape._shapeId){
    			mCurrentShape = null;
    		}
    	}
    	_undoRedoCurrentIndex++;
    	
    	checkHeaderStatus();
    	invalidate();
    }
    
    public void checkHeaderStatus()
    {
    	if(listener != null)
    	{
    		listener.isOnOff_Undo_Redo(enableUndo(), enableRedo());
    	}
    }
    
    boolean enableUndo(){
    	if (_undoRedoCurrentIndex != Configs.INVALID_INDEX
    			&& _undoRedoCurrentIndex > _undoRedoFirstIndex){
    		return true;
    	}
    	return false;
    }
    boolean enableRedo(){
    	if(_undoRedoCurrentIndex != Configs.INVALID_INDEX
    			&& listRootShape != null
    			&& _undoRedoCurrentIndex < listRootShape.size()){
    		return true;
    	}
    	return false;
    }
    
    CustomerShapeView getPreviousShape(int shapeId){
    	for(int i = _undoRedoCurrentIndex - 1; i >= 0; i--) {
    		CustomerShapeView shape  = listRootShape.get(i);
    		if(shapeId == shape._shapeId){
    			return shape;
    		}
    	}
    	return null;
    }
    CustomerShapeView getNextShape(int shapeId){
    	return null;
    }
    
    public void getListIndexPointCornerPath()
    {
    	PathMeasure pm = new PathMeasure(mPath, false);
		float length = pm.getLength();
		if(listIndexCornerPath.size() == 0)
		{
			listIndexCornerPath.add((int)length);
		}
		else
		{
			int theSameLenght = listIndexCornerPath.get(listIndexCornerPath.size()-1);
			int tempLength = (int)length;
			if(theSameLenght != tempLength)
			{
				listIndexCornerPath.add(tempLength);
			}
		}
    }
    
    public FlaotPoint[] getListPoints() {
		PathMeasure pm = new PathMeasure(mPath, false);
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
    
    public float[] getAbsolutePosition(float Ax, float Ay) {

        float fromAxToBxInCanvasSpace = (mTranslateX - Ax) / scaleFactor;
        float fromBxToCanvasEdge = wMainView - mTranslateX;
        float x = wMainView - fromAxToBxInCanvasSpace - fromBxToCanvasEdge;

        float fromAyToByInCanvasSpace = (mTranslateY - Ay) / scaleFactor;
        float fromByToCanvasEdge = hMainView - mTranslateY;
        float y = hMainView - fromAyToByInCanvasSpace - fromByToCanvasEdge;

        return new float[] { x, y };
    }
    
    /**
     * Determine the space between the first two fingers
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

    /**
     * Calculate the mid point of the first two fingers
     */
    private float[] midPoint(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new float[] { x / 2, y / 2 };
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    
    	LogUtils.LogInfo("mDrawingState : "+ mDrawingState);
    	mGestureDetector.onTouchEvent(event);
    	mScaleDetector.onTouchEvent(event);
    	float newTouch[] = getAbsolutePosition(event.getX(), event.getY());
    	float xTouch = newTouch[0];
    	float yTouch = newTouch[1];
    	
    	
    	
    	switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN://Touch 1 finger
			
//			startX = event.getX() - previousTranslateX;
//            startY = event.getY() - previousTranslateY;
			
			mDrawingState = Configs.DrawingState.ListenForNewTouchPoint;
			touch_start(xTouch, yTouch);
			break;
		case MotionEvent.ACTION_POINTER_DOWN://Having another finger touch
			if(mDrawingState != Configs.DrawingState.DrawingOrMove){
				if (event.getPointerCount() == 2){
					mDrawingState = Configs.DrawingState.Pinch;
				} else if(event.getPointerCount() == 3){
					mDrawingState = Configs.DrawingState.Pan;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE: //Move one pointer;
			switch (mDrawingState) {
			case DrawingOrMove:
				touch_move(xTouch, yTouch);
				break;
			case Pinch:
				
				
				break;
			case Pan:
				
//				translateX = event.getX() - startX;
//	            translateY = event.getY() - startY;
//	            //We cannot use startX and startY directly because we have adjusted their values using the previous translation values. 
//	            //This is why we need to add those values to startX and startY so that we can get the actual coordinates of the finger.
//	            double distance = Math.sqrt(Math.pow(event.getX() - (startX + previousTranslateX), 2) + 
//	                                        Math.pow(event.getY() - (startY + previousTranslateY), 2)
//	                                       );
//
//	            if(distance > 0) {
//	               dragged = true;
//	            }            
				
				break;
			case ListenForNewTouchPoint:
				//TODO : check if move > delta => set state is drawing
				if(checkTouchMovingToDraw(xTouch, yTouch, mXStart, mYStart))
				{
					mDrawingState = Configs.DrawingState.DrawingOrMove;
					touch_move(xTouch, yTouch);
				}
				break;
			default:
				break;
			}
			break;
		case MotionEvent.ACTION_POINTER_UP://One touch-point release until 1st pointer
			if(mDrawingState == Configs.DrawingState.Pinch
			|| mDrawingState == Configs.DrawingState.Pan){
				//Disable 
				mDrawingState = Configs.DrawingState.DisableAction;
			}
			
//			previousTranslateX = translateX;
//            previousTranslateY = translateY;
			
//			previousTranslateX = mTranslateX;
//			previousTranslateY = mTranslateY;
			break;
		case MotionEvent.ACTION_UP://Last pointer up
			if (mDrawingState == Configs.DrawingState.DrawingOrMove){
				touch_up(xTouch, yTouch);
			}
			mDrawingState = Configs.DrawingState.Idle;
			
//			previousTranslateX = translateX;
//            previousTranslateY = translateY;
			
//			previousTranslateX = mTranslateX;
//			previousTranslateY = mTranslateY;
			break;
		case MotionEvent.ACTION_CANCEL://Cancel touch event
			if (mDrawingState == Configs.DrawingState.DrawingOrMove){
				touch_up(xTouch, yTouch);
			}
			mDrawingState = Configs.DrawingState.Idle;
			break;
		default:
			break;
		}
    	
    	if (mDrawingState  != Configs.DrawingState.DisableAction)
    		invalidate();
    	
        return true;
    }
	
	@Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        wMainView = w;
        hMainView = h;
//        if(listener != null)
//        {
//        	listener.getWidthAndHeightCanvas(w, h);
//        }
        Log.e("onSizeChanged", "onSizeChanged ========= "+wMainView + " hMainView === "+hMainView);
    }
	
	public double angleBetween2Lines(FlaotPoint point1,FlaotPoint point2,FlaotPoint point3)
    {
		double line1_y1 = point1.getY();
		double line1_x1 = point1.getX();
		double line1_x2 = point2.getX();
		double line1_y2 = point2.getY();
		double line1_x3 = point3.getX();
		double line1_y3 = point3.getY();
		
		double angle1 = Math.atan2(line1_y1 - line1_y2,
				line1_x1 - line1_x2);
		double angle2 = Math.atan2(line1_y2 - line1_y3,
				line1_x2 - line1_x3);
		return angle1-angle2;
    }
	
	public double angleVector(FlaotPoint point1,FlaotPoint point2,FlaotPoint point3,FlaotPoint point4)
	{
		  float Ax = point1.getX();
		  float Ay = point1.getY();
		  
		  float Bx = point2.getX();
		  float By = point2.getY();
		  
		  float Cx = point3.getX();
		  float Cy = point3.getY();

		  float Dx = point4.getX();
		  float Dy = point4.getY();

		  float V1x = Ax - Bx;
		  float V1y = Ay - By;
		  float V2x = Cx - Dx;
		  float V2y = Cy - Dy;
		  
		  double angle = Math.atan2(V2y,V2x) - Math.atan2(V1y,V1x);
		  return angle;
	}
	
	public double distance(FlaotPoint A, FlaotPoint B) {

        double rs = Math.sqrt((B.getY() - A.getY()) * (B.getY() - A.getY())
            + (B.getX() - A.getX()) * (B.getX() - A.getX()));
        return rs;
    }
	
	private boolean isCircle()
	{
		boolean flag = true;
		if(listPoint.size() < 3)
		{
			return false;
		}
		
		// detect first time 
		FlaotPoint first = listPoint.get(0);
		FlaotPoint midle = listPoint.get(listPoint.size()/2);
		FlaotPoint end_last_point = listPoint.get(listPoint.size()-1);
		FlaotPoint tempCenterPoint_last_point = circleCenter(first,midle,end_last_point);
		double radius_last_point = distance(first, tempCenterPoint_last_point);
		
		// detect seconds time
		int indexMiddePoint = listPoint.size()/2;
		int indexNearLastPoint = indexMiddePoint + indexMiddePoint/2;
		FlaotPoint end_near_last_point = listPoint.get(indexNearLastPoint);
		FlaotPoint tempCenterPoint_near_last_point = circleCenter(first,midle,end_near_last_point);
		double radius_near_last_point = distance(first, tempCenterPoint_near_last_point);
		
		
//		Log.e("Radius", "radius_last_point == "+radius_last_point +" radius_near_last_point === "+radius_near_last_point);
		if(radius_last_point - radius_near_last_point <= alphaCircleRadius)
		{
			centerPoint = tempCenterPoint_last_point;
			radius = radius_last_point;
//			Log.e("<= alphaRadius", "<= alphaRadius <= alphaRadius");
		}
		else
		{
			centerPoint = tempCenterPoint_near_last_point;
			radius = radius_near_last_point;
//			Log.e(" > alphaRadius", " > alphaRadius > alphaRadius");
		}
		
		double minRadius = radius - alphaCircleRadius;
		double maxRadius = radius + alphaCircleRadius;
		
//		Log.e("radius", "radius === "+radius + "   wMainView/2 === "+wMainView/3);
		
		// if radius > wMainView/3.it not a chord
		if(radius > wMainView/2)
		{
			return false;
		}
		
		for(int i = 0 ; i < listPoint.size();i++)
		{
			double distanPointInPath = distance(listPoint.get(i), centerPoint);
			if(distanPointInPath > maxRadius || distanPointInPath < minRadius)
			{
//				Log.e("distanPointInPath > maxRadius", "distanPointInPath > maxRadius");
				return false;
			}
		}
		
		RectF rectF = new RectF();
		mPath.computeBounds(rectF, true);
		float width = (rectF.right - rectF.left);
		float heigth = (rectF.bottom - rectF.top);
		
		
		
		if(width < 60 || heigth < 60)
		{
			return false;
		}
		Log.e("Circle", "width ==== "+width + " heigth === "+heigth +" radius === "+radius);
		return flag;
	}
	
	public FlaotPoint circleCenter(FlaotPoint A, FlaotPoint B, FlaotPoint C) {

	    float yDelta_a = B.getY() - A.getY();
	    float xDelta_a = B.getX() - A.getX();
	    float yDelta_b = C.getY() - B.getY();
	    float xDelta_b = C.getX() - B.getX();
	    FlaotPoint center = new FlaotPoint(0,0);

	    
	    float aSlope = yDelta_a/xDelta_a;
	    float bSlope = yDelta_b/xDelta_b;  
	    center.setX((aSlope*bSlope*(A.getY() - C.getY()) + bSlope*(A.getX() + B.getX())
	        - aSlope*(B.getX()+C.getX()) )/(2* (bSlope-aSlope)));
	    
	    center.setY(-1*(center.getX() - (A.getX()+B.getX())/2)/aSlope +  (A.getY()+B.getY())/2);

	    return center;
	}
	
	private boolean isRectangleSquareCornerOne(int indexPointFirstRectangle,int indexPointSecondsRectangle)
	{
		boolean flag = true;
		// create virtual axis oy
		FlaotPoint virtualPointHorizontal = new FlaotPoint(listIntersection.get(indexPointSecondsRectangle).getX(), Math.abs(listIntersection.get(indexPointSecondsRectangle).getY() * listIntersection.get(indexPointSecondsRectangle).getY()));
		double angleSquare = angleBetween2Lines(listIntersection.get(indexPointFirstRectangle),listIntersection.get(indexPointSecondsRectangle),virtualPointHorizontal);
		double angleSquareDegrees = Math.toDegrees(angleSquare);
		angleSquareDegrees = Math.abs(angleSquareDegrees);
		if(angleSquareDegrees > mValue_Degrees_Deviation)
		{
			if(angleSquareDegrees > 180)
			{
				angleSquareDegrees = 360 - angleSquareDegrees;
			}
			if(SquareCornerRoot_90_Degrees - mValue_Degrees_Deviation > angleSquareDegrees || angleSquareDegrees > SquareCornerRoot_90_Degrees + mValue_Degrees_Deviation)
			{
				return false;
			}
		}
		return true;
	}
	
	private boolean detectPointOutOfRactangleMaxMin()
	{
		boolean flag = true;
		// try to detect out of max rectangle and min rectangle
		RectF rectF = new RectF();
		mPath.computeBounds(rectF, true);
		for(int i = 0 ; i < listPoint.size() ; i++)
		{	
			// check all point outside max rectangle
			if(!DrawUtils.checkTouchPointInSideRectangleShape(rectF.left - alphaRectangle,rectF.top - alphaRectangle,rectF.right + alphaRectangle,rectF.bottom + alphaRectangle, listPoint.get(i).getX(), listPoint.get(i).getY()))
			{
				return false;
			}
			// check all point inside min rectangle 
			if(DrawUtils.checkTouchPointInSideRectangleShape(rectF.left + alphaRectangle,rectF.top + alphaRectangle,rectF.right - alphaRectangle,rectF.bottom - alphaRectangle, listPoint.get(i).getX(), listPoint.get(i).getY()))
			{
				return false;
			}
		}
		
		return flag;
	}
	
	private boolean isZigZag(ArrayList<ArrayList<FlaotPoint>> listParagraphArrow)
	{		
		boolean isZigZagLine = true;
		boolean isArrowLine = false;
		for(int i = 0 ; i < listParagraphArrow.size();i++)
		{
			ArrayList<FlaotPoint> tempList = listParagraphArrow.get(i);
			isArrowLine = isArrow(tempList);
			if(!isArrowLine)
			{
				Log.e("isArrowLine ", "isArrowLine iiiiii ==== "+i);
				return isArrowLine;
			}
		}
		
		return isZigZagLine;
	}
	
	
	private boolean isArrow(ArrayList<FlaotPoint> listPoint)
	{
		listPointArrow.removeAll(listPointArrow);
		ArrayList<FlaotPoint> listPointArrowConner = new ArrayList<FlaotPoint>();
		boolean flag = false;
		int indexRootPointOfLineSeconds = 0;
//		Log.e("listPoint.size()", "listPoint.size() ==== "+listPoint.size());
		for(int i = 0 ; i < listPoint.size() ; i++)
		{
			int indexRangle = i+2;
			if(indexRangle <= listPoint.size()-1)
			{
				double angle = angleBetween2Lines(listPoint.get(i),listPoint.get(indexRangle-1),listPoint.get(indexRangle));
				double angleDegrees = Math.toDegrees(angle);
				angleDegrees = Math.abs(angleDegrees);
				if(angleDegrees > 180)
				{
					angleDegrees = 360 - angleDegrees;
				}
//				Log.e("angleDegrees isArrow", "angleDegrees isArrow =========== "+angleDegrees);
			
				if(angleDegrees > alphaArrowDegrees)
				{
					if(listPointArrowConner.size() == 0)
					{
						listPointArrowConner.add(listPoint.get(i));
						listPointArrowConner.add(listPoint.get(indexRangle-1));
						listPointArrowConner.add(listPoint.get(indexRangle));
						indexRootPointOfLineSeconds = indexRangle-1;
//						System.out.println("isArrow ****** listPointArrowConner.size() == 0   $$$$$$$$$$$$$$$$$$$$$");
					}
					else
					{
						if(listPointArrowConner.size() >= 4)
						{
//							System.out.println("isArrow ****** listPointArrowConner.size() >= 4   *****************");
							return flag;
						}
						
						int countExistPointInList = 0;
						for(int k = 0 ; k < listPointArrowConner.size();k++)
						{
							FlaotPoint flatPoint = listPointArrowConner.get(k);
							
							if(flatPoint.getX() == listPoint.get(i).getX() && flatPoint.getY() == listPoint.get(i).getY())
							{
								countExistPointInList++;
							}
							else if(flatPoint.getX() == listPoint.get(indexRangle-1).getX() && flatPoint.getY() == listPoint.get(indexRangle-1).getY())
							{
								countExistPointInList++;
							}
							else if(flatPoint.getX() == listPoint.get(indexRangle).getX() && flatPoint.getY() == listPoint.get(indexRangle).getY())
							{
								countExistPointInList++;
							}
						}
						
						if(countExistPointInList == 2)
						{
							indexRootPointOfLineSeconds = indexRangle-1;
							listPointArrowConner.add(listPoint.get(indexRangle));
						}
						
					}
				}
				
			}
		}
		
		// Check Second line of Arrow is Line
		if(listPointArrowConner.size() == 3 || listPointArrowConner.size() == 4)
		{
			int middlePointOfLineSecond = indexRootPointOfLineSeconds + (listPoint.size() - indexRootPointOfLineSeconds)/2;
			double angle = angleBetween2Lines(listPoint.get(indexRootPointOfLineSeconds),listPoint.get(middlePointOfLineSecond),listPoint.get(listPoint.size()-1));
			double angleDegrees = Math.toDegrees(angle);
			angleDegrees = Math.abs(angleDegrees);
			
			if(angleDegrees > 180)
			{
				angleDegrees = 360 - angleDegrees;
			}
			
			if(angleDegrees > alphaArrowDegrees)
			{
				return flag;
			}
			
			IntersectPoint intersectArrowConner = null;
			if(listPointArrowConner.size() == 3)
			{	
				FlaotPoint point_1 = listPoint.get(0);
				FlaotPoint point_2 = listPointArrowConner.get(1);
				FlaotPoint point_3 = listPoint.get(listPoint.size()-1);
				IntersectLine line1 = new IntersectLine(new IntersectPoint(point_1.getX(),point_1.getY()),new IntersectPoint(point_2.getX(), point_2.getY()));
				IntersectLine line2 = new IntersectLine(new IntersectPoint(point_2.getX(), point_2.getY()), new IntersectPoint(point_3.getX(),point_3.getY()));
				intersectArrowConner = line1.intersects(line2);
			}
			else if(listPointArrowConner.size() == 4)
			{
				FlaotPoint point_1 = listPoint.get(0);
				FlaotPoint point_2 = listPointArrowConner.get(1);
				FlaotPoint point_3 = listPointArrowConner.get(2);
				FlaotPoint point_4 = listPoint.get(listPoint.size()-1);
				IntersectLine line1 = new IntersectLine(new IntersectPoint(point_1.getX(),point_1.getY()),new IntersectPoint(point_2.getX(), point_2.getY()));
				IntersectLine line2 = new IntersectLine(new IntersectPoint(point_3.getX(), point_3.getY()), new IntersectPoint(point_4.getX(),point_4.getY()));
				intersectArrowConner = line1.intersects(line2);
			}
	        
			if(intersectArrowConner != null)
			{
//				Log.e("inter", "inter ====== "+inter.x + " inter.y == "+inter.y);
				FlaotPoint intersectPoint = new FlaotPoint((float)intersectArrowConner.x, (float)intersectArrowConner.y);
				listPointArrow.add(listPoint.get(0));
				listPointArrow.add(intersectPoint);
				listPointArrow.add(listPoint.get(listPoint.size()-1));
				
				double angleArrow = angleBetween2Lines(listPoint.get(0),intersectPoint,listPoint.get(listPoint.size()-1));
				double angleArrowDegrees = Math.toDegrees(angleArrow);
				angleArrowDegrees = Math.abs(angleArrowDegrees);
				
				if(angleArrowDegrees > 180)
				{
					angleArrowDegrees = angleArrowDegrees - 180;
				}
				else
				{
					angleArrowDegrees = 180 - angleArrowDegrees;
				}
				
//				Log.e("Actual Arrow Degress", "Actual Arrow Degress ======== "+angleArrowDegrees);
				
				if(angleArrowDegrees <= connerArrowAccept)
				{
					return true;
				}
			}
		}
		
//		Log.e("listPointArrowConner.size()", "listPointArrowConner.size() === "+listPointArrowConner.size());
		
		return flag;
	}
	
	private void detectListArrowParagraph(int startIndexParagraphArrow)
	{
//		Log.e("------------------", "-------------------------------------------");
		int startParagraphIndex = startIndexParagraphArrow;
//		Log.e("startParagraphIndex", "startParagraphIndex ==== "+startParagraphIndex);
		int middlePointIndex = 0;
		int endParagraphIndex = 0;
		int firstIndexConner = 0;
		boolean OnePointRoot = false;
		for(int i = startIndexParagraphArrow; i < listPoint.size() ; i++)
		{
			int indexRangle = i+2;
			if(indexRangle <= listPoint.size()-1)
			{
				double angle = angleBetween2Lines(listPoint.get(i),listPoint.get(indexRangle-1),listPoint.get(indexRangle));
				double angleDegrees = Math.toDegrees(angle);
				angleDegrees = Math.abs(angleDegrees);
				if(angleDegrees > 180)
				{
					angleDegrees = 360 - angleDegrees;
				}
//				Log.e("angleDegrees isArrow", "angleDegrees isArrow =========== "+angleDegrees);
			
				if(angleDegrees > alphaArrowDegrees)
				{
					if(firstIndexConner == 0 )
					{	
						middlePointIndex = indexRangle-1;
						firstIndexConner += 1;
						OnePointRoot = true;
//						Log.e("firstIndexConner == 0", "firstIndexConner ====== "+middlePointIndex);
					}
					else
					{
						if(middlePointIndex + 1 == indexRangle - 1)
						{
//							Log.e("middlePointIndex + 1 == indexRangle - 1", "middlePointIndex + 1 == indexRangle - 1 ==== "+ (indexRangle - 1));
						}
						else
						{
//							Log.e("End"," Start === "+startParagraphIndex +  "  middlePointIndex === "+middlePointIndex + "  End == "+(indexRangle - 1)  );
							OnePointRoot = false;
							endParagraphIndex = (indexRangle - 1);
							ArrayList<FlaotPoint> listArrowPoint = new ArrayList<FlaotPoint>();
							listArrowPoint.addAll(listPoint.subList(startParagraphIndex, endParagraphIndex));
							listArrowParagraph.add(listArrowPoint);
							detectListArrowParagraph(middlePointIndex + 1);
							return ;
						}
					}
				}
				
			}
		}
		
		if(OnePointRoot)
		{
			ArrayList<FlaotPoint> listArrowPoint = new ArrayList<FlaotPoint>();
			listArrowPoint.addAll(listPoint.subList(startParagraphIndex, listPoint.size()-1));
			listArrowParagraph.add(listArrowPoint);
		}
	}
	
	
	private boolean isRectangle()
	{
		boolean flag = false;
		if(listIntersection.size() < 3)
		{
			return flag;
		}
		
		int indexPointFirstRectangle = 0;
		int indexPointSecondsRectangle = 0;
		int indexPointThreeRectangle = 0;
		int indexPointFourRectangle = 0;
		int indexPointFiveRectangle = 0;
		
		for(int i = 0 ; i < listIntersection.size() ; i++)
		{
			int indexRangle = i+2;
			if(indexRangle <= listIntersection.size()-1)
			{
				double angle = angleBetween2Lines(listIntersection.get(i),listIntersection.get(indexRangle-1),listIntersection.get(indexRangle));
				double angleDegrees = Math.toDegrees(angle);
				if(isSquareCorners(angleDegrees))
				{
					countSquareCorner++;
					if(countSquareCorner == 1)
					{
						indexPointFirstRectangle = i;
						indexPointSecondsRectangle = indexRangle -1;
						indexPointThreeRectangle = indexRangle;
						if(!isRectangleSquareCornerOne(indexPointFirstRectangle,indexPointSecondsRectangle))
						{
							return flag;
						}
					}
					else if(countSquareCorner == 2)
					{
						double angleVector = angleVector(listIntersection.get(indexPointFirstRectangle),listIntersection.get(indexPointSecondsRectangle),listIntersection.get(indexRangle-1),listIntersection.get(indexRangle));
						double angleDegreesVector = Math.toDegrees(angleVector);
						indexPointFourRectangle = indexRangle;
						if(isSameDirectionVector(angleDegreesVector))  // is not Rectangle
						{	
							return flag;
						}
					}
					else if(countSquareCorner == 3)
					{
						double angleVector = angleVector(listIntersection.get(indexPointSecondsRectangle),listIntersection.get(indexPointThreeRectangle),listIntersection.get(indexRangle-1),listIntersection.get(indexRangle));
						double angleDegreesVector = Math.toDegrees(angleVector);
						indexPointFiveRectangle  = indexRangle;
						if(isSameDirectionVector(angleDegreesVector))  // is not Rectangle
						{	
							return flag;
						}
					}
					else if(countChangeDirection == 4)
					{
						// first detect the same direction
						double angleVector = angleVector(listIntersection.get(indexPointThreeRectangle),listIntersection.get(indexPointFourRectangle),listIntersection.get(indexRangle-1),listIntersection.get(indexRangle));
						double angleDegreesVector = Math.toDegrees(angleVector);
						if(isSameDirectionVector(angleDegreesVector))  // is not Rectangle
						{	
							return flag;
						}
						// send detect isSquareCorners
						double angleCombinateRectangle = angleBetween2Lines(listIntersection.get(indexPointFirstRectangle),listIntersection.get(indexPointFiveRectangle),listIntersection.get(indexPointFourRectangle));
						double angleCombinateDegrees = Math.toDegrees(angleCombinateRectangle);
						if(!isSquareCorners(angleCombinateDegrees))
						{
							return flag;
						}
					}
				}
				else
				{
					return flag;
				}
			}
		}
		
		if(countSquareCorner > 4)  // is not Rectangle 
		{
			return flag;
		}
		else if(countSquareCorner > 0 && countSquareCorner <= 4)// is Rectangle
		{
			flag = true;
			// detect point out of rectangle max or min 
			if(!detectPointOutOfRactangleMaxMin())
			{
				flag = false;
			}
		}
		
		return flag;
	}
	
	public boolean isSameDirectionVector(double angleDegreesVector)
	{
		boolean flag = false;
		angleDegreesVector = Math.abs(angleDegreesVector);
		if(-mValue_Degrees_Deviation < angleDegreesVector && mValue_Degrees_Deviation > angleDegreesVector)
		{
			flag = true;
		}
		return flag;
	}
	
	public boolean isSquareCorners(double anglDegrees)
	{
		anglDegrees = Math.abs(anglDegrees);
		if(anglDegrees > 180)
		{
			anglDegrees = 360 - anglDegrees;
		}
		boolean flag = false;
//		Log.e("anglDegrees", "anglDegrees ========= "+anglDegrees);
		if(SquareCornerRoot_90_Degrees - mValue_Degrees_Deviation < anglDegrees && anglDegrees < SquareCornerRoot_90_Degrees + mValue_Degrees_Deviation)
		{
			flag = true;
		}
		return flag;
	}
	
	public void detectDirection()
	{
		boolean flag = false;
		int indexNextPoint = 0;
		for(int i = 0 ; i < listPoint.size() ; i++)
		{
			FlaotPoint firstPoint = listPoint.get(i);
			if(i < listPoint.size() -1)
			{
				indexNextPoint = i+1;
				FlaotPoint secondPoint = listPoint.get(indexNextPoint);
				float resultY = Math.abs(secondPoint.getY() - firstPoint.getY());
				float resultX = Math.abs(secondPoint.getX() - firstPoint.getX());
//				Log.e("resultY", "resultY ==== "+resultY + " resultX === "+resultX );
				if(resultY > resultX)
				{
					if(isStartDirection == 0)
					{
						isDirection = HorizontalDirection;   // horizontal direction
						isStartDirection = 1;
						listIntersection.add(firstPoint);
					}
					
					if(isDirection == VerticalDirection)  // Save Intersection Point
					{
						listIntersection.add(firstPoint);
						isDirection = HorizontalDirection;
						countChangeDirection++;
//						Log.e("Dung Dung", "Dung Dung 111111111111111111111111111111");
					}
					else   
					{
						int indexRangle = i+2;
						if(indexRangle <= listPoint.size()-1)
						{
							double angle = angleBetween2Lines(listPoint.get(i),listPoint.get(indexRangle-1),listPoint.get(indexRangle));
							double angleDegrees = Math.toDegrees(angle);
//							Log.e("---------------", "--------------------------------------- Dung");
//							Log.e("Dung Dung", "Dung Dung angleDegrees ========= "+angleDegrees);
						}
					}
//					Log.e("horizontal direction", "horizontal direction ################");
				}
				else
				{
					if(isStartDirection == 0)
					{
						isDirection = VerticalDirection;  // vertoical direction
						isStartDirection = 1;
						listIntersection.add(firstPoint);
					}
					
					if(isDirection == HorizontalDirection) // Save Intersection Point
					{
						listIntersection.add(firstPoint);
						isDirection = VerticalDirection;
						countChangeDirection++;
//						Log.e("Ngang Ngang", "Ngang Ngang 11111111111111 ---------------------");
					}
					else
					{
						int indexRangle = i+2;
						if(indexRangle <= listPoint.size()-1)
						{
							double angle = angleBetween2Lines(listPoint.get(i),listPoint.get(indexRangle-1),listPoint.get(indexRangle));
							double angleDegrees = Math.toDegrees(angle);
//							Log.e("############", "######################################### Ngang");
//							Log.e("Ngang Ngang", "Ngang Ngang angleDegrees ========= "+angleDegrees);
							
						}
					}
//					Log.e("vertical direction", "vertical direction ---------------------");
				}
			}
			else
			{
//				Log.e("else", "else else else === "+indexNextPoint + " ii == "+i);
				listIntersection.add(listPoint.get(i));
			}
		}
	}
	
	
	public void detectShapeAndCreate()
	{
		detectDirection();
//		Log.e("listIntersection", "listIntersection  ===== "+listIntersection.size() +" countChangeDirection === "+countChangeDirection);
		isRectangle = isRectangle();
//		Log.e("isRectangle", "isRectangle ============ "+isRectangle);
		boolean isCreateFreeStyleDraw = true;
		if(isRectangle)   // Create
		{
			isCreateFreeStyleDraw = false;
			createRectangle();
		}
		else
		{
			detectListArrowParagraph(startIndexListPoint);
			Log.e("listArrowParagraph.size()", "listArrowParagraph.size() === "+listArrowParagraph.size());
			if(listArrowParagraph.size() == 1)
			{
				isArrow = isArrow(listPoint);
				if(isArrow)
				{
					isCreateFreeStyleDraw = false;
					createArrow();
				}
				else
				{
					isCircle = isCircle();
					if(isCircle)
					{
						isCreateFreeStyleDraw = false;
						createCircle();
					}
				}
			}
			else if(listArrowParagraph.size() > 1)
			{
				isZigZag = isZigZag(listArrowParagraph);
				if(isZigZag)
				{
					isCreateFreeStyleDraw = false;
					createZigZag();
				}
			}
			else
			{
				isCircle = isCircle();
				if(isCircle)
				{
					isCreateFreeStyleDraw = false;
					createCircle();
				}
			}
		}
		
		if(isCreateFreeStyleDraw)
		{
			createFreeStyleDraw();
		}
		
	}
	
	private void createFreeStyleDraw()
	{
		RectF rectF = new RectF();
		mPath.computeBounds(rectF, true);
		float distanceX = Math.abs(rectF.right - rectF.left);
		float distanceY = Math.abs(rectF.top - rectF.bottom);
		if(distanceX >= 30 || distanceY >= 30)
		{
			FreeStyleObject freeStyleObject = new FreeStyleObject(mContext, Configs.FREE_STYLE_TYPE,rectF.left, rectF.top,rectF.left + (rectF.right - rectF.left), rectF.top + (rectF.bottom - rectF.top), mPath, mCurrentSettingPaint);
			clearRedoSharpFromIndex(_undoRedoCurrentIndex);
			freeStyleObject._shapeId = listRootShape.size();
			freeStyleObject._action = ShapeAction.AddNewShape;
			addNewSharp(freeStyleObject);
		}
	}
	
	private void createArrow()
	{
		FlaotPoint firstPoint = listPointArrow.get(0);
		FlaotPoint tipPoint = listPointArrow.get(1);
		FlaotPoint endPoint = listPointArrow.get(2);
		double distanceLineOne = distance(firstPoint, tipPoint);
		double distanceLineTwo = distance(tipPoint, endPoint);
		FlaotPoint tailPoint = null;
		if(distanceLineOne > distanceLineTwo)
		{
			tailPoint = firstPoint;
		}
		else
		{
			tailPoint = endPoint;
		}
		ArrowObject arrow = new ArrowObject(mContext, Configs.ARROW_TYPE, tailPoint.getX(), tailPoint.getY(), tipPoint.getX(), tipPoint.getY(), mCurrentSettingPaint);
		clearRedoSharpFromIndex(_undoRedoCurrentIndex);
		arrow._shapeId = listRootShape.size();
		arrow._action = ShapeAction.AddNewShape;
		addNewSharp(arrow);
	}
	
	private void createZigZag()
	{
		Log.e("createZigZag", "createZigZag createZigZag createZigZag");
		RectF rectF = new RectF();
		mPath.computeBounds(rectF, true);
		BlurObject blurObject = new BlurObject(mContext, _blurManager,
				Configs.BLUR_TYPE, 
				xMainBitmap,
				yMainBitmap,
				rectF.left,
				rectF.top,
				rectF.right,
				rectF.bottom,
				mCurrentSettingPaint);
		clearRedoSharpFromIndex(_undoRedoCurrentIndex);
		blurObject._shapeId = listRootShape.size();
		blurObject._action = ShapeAction.AddNewShape;
		addNewSharp(blurObject);
	}
	
	private void createCircle()
	{
		RectF rectF = new RectF();
		mPath.computeBounds(rectF, true);
		Log.e("rectF", "rectF === "+rectF.left + "  "+ rectF.top + "  "+rectF.right + "  "+rectF.bottom);
		EllipseObject ellipseObject = new EllipseObject(mContext,Configs.CIRCLE_TYPE,rectF.left, rectF.top,rectF.left + (rectF.right - rectF.left), rectF.top + (rectF.bottom - rectF.top), mCurrentSettingPaint);
		clearRedoSharpFromIndex(_undoRedoCurrentIndex);
		ellipseObject._shapeId = listRootShape.size();
		ellipseObject._action = ShapeAction.AddNewShape;
		addNewSharp(ellipseObject);
	}
	
	private void createRectangle()
	{
		  RectF rectF = new RectF();
		  mPath.computeBounds(rectF, true);
		  RectangleObject rectangle = new RectangleObject(mContext,Configs.RECTANGLE_TYPE,rectF.left, rectF.top, rectF.right, rectF.bottom, mCurrentSettingPaint);
		  clearRedoSharpFromIndex(_undoRedoCurrentIndex);
			rectangle._shapeId = listRootShape.size();
			rectangle._action = ShapeAction.AddNewShape;
			addNewSharp(rectangle);
	}
	
	public void createText(float x , float y,String text)
	{
		Rect mRectText = new Rect();
		mCurrentSettingPaint.getTextBounds(text, 0, text.length(), mRectText);
		int width = mRectText.left + mRectText.width();
		int height = mRectText.bottom + mRectText.height();
		
		TextObject textObj = new TextObject(mContext, Configs.ADD_TEXT_TYPE, text, x, y,  x + width, y +height, mCurrentSettingPaint);
		clearRedoSharpFromIndex(_undoRedoCurrentIndex);
		textObj._shapeId = listRootShape.size();
		textObj._action = ShapeAction.AddNewShape;
		addNewSharp(textObj);
	}
	// detect long click in view 
	class MyGestureDetector extends SimpleOnGestureListener {
        
		
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			
			 if(mainBitmap == null || !isEdit)
			 {
				 return false;
			 }
			 if(isShowingMenu)
			 {
				 return false;
			 }
			 
			 if(disableTouchEventWhenShowSettingPaint)
			 {
				 return false;
			 }
			
			if(listener != null)
			{
				listener.isDoubleTap(e.getX(),e.getY());
			}
			return false;
		}
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			Log.e("onSingleTapUp", "onSingleTapUp");
			return false;
		}
		
		@Override
		public void onShowPress(MotionEvent e) {
			
			Log.e("onShowPress", "onShowPress");
		}
		
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			
			Log.e("onScroll", "onScroll");
			
			return false;
		}
		
		@Override
		public void onLongPress(MotionEvent e) {
			Log.e("onLongPress", "onLongPress");
			if(isTouchOnShape)
			{
				if(listener != null)
				{
					disableTouchEventWhenShowSettingPaint = true;
					listener.isOpenSettingPaint(mCurrentTypeShape);
				}
			}
		}
		
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log.e("onFling", "onFling");
			return false;
		}
		
		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			Log.e("onDown", "onDown");
			return false;
		}
		
	}
	
	
	
	public int getwMainView() {
		return wMainView;
	}

	public void setwMainView(int wMainView) {
		this.wMainView = wMainView;
	}

	public int gethMainView() {
		return hMainView;
	}

	public void sethMainView(int hMainView) {
		this.hMainView = hMainView;
	}

	public ArrayList<CustomerShapeView> getListRootShape() {
		return listRootShape;
	}

	public void setListRootShape(ArrayList<CustomerShapeView> listRootShape) {
		this.listRootShape = listRootShape;
	}

	public Bitmap getMainBitmap() {
		return mainBitmap;
	}

	public void setMainBitmap(Bitmap mainBitmap) {
		if(this.mainBitmap != null)
		{
			this.mainBitmap.recycle();
			this.mainBitmap = null;
		}
		this.mainBitmap = mainBitmap;
		this._blurManager.setMainBitmap(mainBitmap);
		xMainBitmap = (wMainView - mainBitmap.getWidth())/2;
		yMainBitmap = (hMainView - mainBitmap.getHeight())/2;
		createVirtualRect();
	}

	private void createVirtualRect()
	{
		// bitmap is landscape
		if(xMainBitmap == 0)
		{
			mX_VirtualRectOne = 0;
			mY_VirtualRectOne = 0;
			mW_VirtualRectOne = wMainView;
			mH_VirtualRectOne = yMainBitmap;
			
			mX_VirtualRectTwo = 0;
			mY_VirtualRectTwo = yMainBitmap + mainBitmap.getHeight();
			mW_VirtualRectTwo = wMainView;
			mH_VirtualRectTwo = mY_VirtualRectTwo + hMainView - mY_VirtualRectTwo;
		}
		else  // bitmap is protrait
		{
			mX_VirtualRectTwo = 0;
			mY_VirtualRectOne = 0;
			mW_VirtualRectOne = xMainBitmap;
			mH_VirtualRectOne = hMainView;
			
			mX_VirtualRectTwo = xMainBitmap + mainBitmap.getWidth();
			mY_VirtualRectTwo = 0;
			mW_VirtualRectTwo = mX_VirtualRectTwo + wMainView - mX_VirtualRectTwo;
			mH_VirtualRectTwo = hMainView;
		}
		
		mRow_Caro_One = mW_VirtualRectOne / mBitmapCaro.getWidth() + ((mW_VirtualRectOne % mBitmapCaro.getWidth()) > 0 ? 1 : 0) ;
		mColum_Caro_One = mH_VirtualRectOne / mBitmapCaro.getHeight() + ((mH_VirtualRectOne % mBitmapCaro.getHeight()) > 0 ? 1 : 0);
		
		mRow_Caro_Two = mW_VirtualRectTwo / mBitmapCaro.getWidth() + ((mW_VirtualRectTwo % mBitmapCaro.getWidth()) > 0 ? 1 : 0) ;
		mColum_Caro_Two = mH_VirtualRectTwo / mBitmapCaro.getHeight() + ((mH_VirtualRectTwo % mBitmapCaro.getHeight()) > 0 ? 1 : 0);
	}

	public boolean isEdit() {
		return isEdit;
	}

	public void setEdit(boolean isEdit) {
		this.isEdit = isEdit;
	}

	public boolean isShowingMenu() {
		return isShowingMenu;
	}

	public void setShowingMenu(boolean isShowingMenu) {
		this.isShowingMenu = isShowingMenu;
	}

	public boolean isDragGrid() {
		return isDragGrid;
	}

	public void setDragGrid(boolean isDragGrid) {
		this.isDragGrid = isDragGrid;
	}
}
