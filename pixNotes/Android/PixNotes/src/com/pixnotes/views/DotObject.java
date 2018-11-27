package com.pixnotes.views;

import com.pixnotes.configsapp.Configs;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

public class DotObject {
	
	private float mX,mY,mW,mH;
	private Paint mPaint;
	private int mIdDot;
	public DotObject(int idDot,float x,float y)
	{
		this.mIdDot = idDot;
		mX = x;
		mY = y;
		mW = Configs.RADIUS_CIRCLE_DOT * 2;
		mH = Configs.RADIUS_CIRCLE_DOT * 2;
		mPaint = new Paint();
		mPaint.setColor(Color.GRAY);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeWidth(8);
	}
	
	public void onDraw(Canvas canvas) {
		canvas.drawCircle(mX, mY, Configs.RADIUS_CIRCLE_DOT, mPaint);
	}

	public float getmX() {
		return mX;
	}

	public void setmX(float mX) {
		this.mX = mX;
	}

	public float getmY() {
		return mY;
	}

	public void setmY(float mY) {
		this.mY = mY;
	}

	public float getmW() {
		return mW;
	}

	public void setmW(float mW) {
		this.mW = mW;
	}

	public float getmH() {
		return mH;
	}

	public void setmH(float mH) {
		this.mH = mH;
	}

	public Paint getmPaint() {
		return mPaint;
	}

	public void setmPaint(Paint mPaint) {
		this.mPaint = mPaint;
	}

	public int getmIdDot() {
		return mIdDot;
	}

	public void setmIdDot(int mIdDot) {
		this.mIdDot = mIdDot;
	}
	
	
}
