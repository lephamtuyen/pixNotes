package com.pixnotes.common;

import com.pixnotes.configsapp.Configs;
import com.pixnotes.views.ArrowObject;
import com.pixnotes.views.BlurObject;
import com.pixnotes.views.CustomerShapeView;
import com.pixnotes.views.DotObject;
import com.pixnotes.views.EllipseObject;
import com.pixnotes.views.FreeStyleObject;
import com.pixnotes.views.RectangleObject;
import com.pixnotes.views.TextObject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

public class DrawUtils {
	

	public static int ALPHA_DOT_POINT = 10;
	public static int ALPHA_RESIZE = 10;
	
	// Make dest a resized copy of src. This maintains the aspect ratio. and cuts
	// along edges of the image if src and dest have a different aspect ratio.
	public static void convertSizeClip(Bitmap src, Bitmap dest) {
		Canvas canvas = new Canvas(dest);
		RectF srcRect = new RectF(0, 0, src.getWidth(), src.getHeight());
		RectF destRect = new RectF(0, 0, dest.getWidth(), dest.getHeight());

		// Because the current SDK does not directly support the "dest fits
		// inside src" mode, we calculate the reverse matrix and invert to
		// get what we want.
		Matrix mDestSrc = new Matrix();
		mDestSrc.setRectToRect(destRect, srcRect, Matrix.ScaleToFit.CENTER);
		Matrix mSrcDest = new Matrix();
		mDestSrc.invert(mSrcDest);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(src, mSrcDest, new Paint(Paint.DITHER_FLAG));
	}

	public static void convertSizeFill(Bitmap src, Bitmap dest) {
		Canvas canvas = new Canvas(dest);
		RectF srcRect = new RectF(0, 0, src.getWidth(), src.getHeight());
		RectF destRect = new RectF(0, 0, dest.getWidth(), dest.getHeight());

		Matrix m = new Matrix();
		m.setRectToRect(srcRect, destRect, Matrix.ScaleToFit.FILL);
		canvas.drawColor(Color.WHITE);

		canvas.drawBitmap(src, m, new Paint(Paint.DITHER_FLAG));
	}

	public static int brightness(int color) {
		// Because this should be fast, we cheat: simply return the green channel.
		return (color >> 16) & 0xff;
	}
	
	
	public static boolean isTouchOnShape(CustomerShapeView shape, float x, float y){
		if(shape == null){
			return false;
		}
		switch (shape.mType) {
		case Configs.RECTANGLE_TYPE:
			return touchOnRectangle(shape, x, y);
		case Configs.CIRCLE_TYPE:
			return touchOnEclipse(shape, x, y);
		case Configs.BLUR_TYPE:
			return touchOnBlur(shape, x, y);
		case Configs.ARROW_TYPE:
			return touchOnArrow(shape, x, y);
		case Configs.FREE_STYLE_TYPE:
			return touchOnFreeStyle(shape, x, y);
		case Configs.ADD_TEXT_TYPE:
			return touchOnText(shape, x, y);
		default:
			return false;
		}
	}
	
	public static boolean touchOnRectangle(CustomerShapeView shape, float x, float y){
		RectangleObject rectangle = (RectangleObject)shape;
		if(checkTouchPointInSideRectangleShape(rectangle.x,rectangle.y,rectangle.w,rectangle.h, x, y)){
			return true;
		}
		return false;
	}
	public static boolean touchOnEclipse(CustomerShapeView shape, float x, float y){
		EllipseObject ellipse = (EllipseObject)shape;
		if(checkTouchPointInSideRectangleShape(ellipse.getRectF().left,ellipse.getRectF().top,
				ellipse.getRectF().right,ellipse.getRectF().bottom, x, y))
		{
			return true;
		}
		return false;
	}
	public static  boolean touchOnArrow(CustomerShapeView shape, float x, float y){
		ArrowObject arrow = (ArrowObject)shape;		
		if(checkTouchPointInSideRectangleShape(arrow.getArrowHeaderRectF().left,arrow.getArrowHeaderRectF().top,
				arrow.getArrowHeaderRectF().right,arrow.getArrowHeaderRectF().bottom, x, y))
		{ 
			return true;
		}
		return false;
	}
	public static boolean touchOnBlur(CustomerShapeView shape, float x, float y){
		BlurObject blur = (BlurObject)shape;
		if(checkTouchPointInSideRectangleShape(blur.x,blur.y,blur.w,blur.h, x, y)){
			return true;
		}
		return false;
	}
	public static boolean touchOnFreeStyle(CustomerShapeView shape, float x, float y){
		FreeStyleObject freeStyle = (FreeStyleObject)shape;
		if(checkTouchPointInSideRectangleShape(freeStyle.getRectFBodyPath().left,freeStyle.getRectFBodyPath().top,
				freeStyle.getRectFBodyPath().right,freeStyle.getRectFBodyPath().bottom, x, y))
		{
			return true;
		}
		return false;
	}
	public static boolean touchOnText(CustomerShapeView shape, float x, float y){
		TextObject textObj = (TextObject)shape;
		if(checkTouchPointInSideRectangleShape(textObj.x,textObj.y,textObj.w,textObj.h, x, y))
		{
			return true;
		}
		return false;
	}
	
	public static boolean checkTouchPointInSideRectangleShape(float x,float y,float w,float h,float xTouchStart,float yTouchStart)
	{
		boolean flag = false;
		if(x <= xTouchStart  && xTouchStart <= w &&  y <= yTouchStart  && yTouchStart <= h)
		{
			flag = true;
		}
		return flag;
	}
	
	public static boolean checkTouchPointInSideDotPoint(DotObject dot,float xTouchStart,float yTouchStart)
	{
		boolean flag = false;
		if( dot.getmX() - ALPHA_DOT_POINT <= xTouchStart  
				&& xTouchStart <= dot.getmX() + dot.getmW() + ALPHA_DOT_POINT 
				&&  dot.getmY() - ALPHA_DOT_POINT <= yTouchStart  
				&& yTouchStart <= dot.getmY() + dot.getmH() + ALPHA_DOT_POINT){
			flag = true;
		}
		return flag;
	}
}
