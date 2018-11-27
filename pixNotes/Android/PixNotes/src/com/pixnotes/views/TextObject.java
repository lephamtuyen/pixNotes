package com.pixnotes.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class TextObject extends CustomerShapeView {
	
	
	private int mColor = 0;
	private int mStrokeWith = 0;
	private String text;
	
	private int alphasize = 30;
	private Paint mPaintDebug;
	public TextObject(Context mContext,int mType, String text,float x, float y, float w, float h, Paint mPaint) {
		super(mContext,mType, x, y, w, h, mPaint);
		
		this.mType = mType;
		this.text = text;
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.mPaint = new Paint(mPaint);
		this.mPaint.setPathEffect(new CornerPathEffect(0));
		this.mPaint.setStyle(Paint.Style.FILL);
		
		mPaintDebug = new Paint();
		mPaintDebug.setColor(Color.GREEN);
		mPaintDebug.setStyle(Paint.Style.STROKE);
		
	}
	
	
	public TextObject()
	{
		
	}
	
	@Override
	public TextObject copyCustomerView() {
		TextObject textObj = new TextObject(mContext,mType, text,x, y, w, h, mPaint);
		textObj._shapeId = this._shapeId;
		return textObj;
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
		parcel.writeString(text);
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
		text = parcel.readString();
		
		
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(mColor);
		mPaint.setStrokeWidth(mStrokeWith);
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		
		
	}
	
	public void initNewText(String textValue)
	{
		this.text = textValue;
		Rect mRectText = new Rect();
		mPaint.getTextBounds(text, 0, text.length(), mRectText);
		int width = mRectText.left + mRectText.width();
		int height = mRectText.bottom + mRectText.height();
		w = x + width;
		h = y + height;
	}
	public static final Parcelable.Creator<TextObject> CREATOR = new Parcelable.Creator<TextObject>() {
		@Override
		public TextObject createFromParcel(Parcel in) {
			TextObject textObj = new TextObject();
			try {
				textObj.init(in);
			} catch (Throwable t) {
				Log.e("Edition.createFromParcel", "Failed to init Edition from parcel", t);
				return null;
			}
			return textObj;
		}
		@Override
		public TextObject[] newArray(int size) {
			return new TextObject[size];
		}
	};
	
	
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		canvas.drawRect(x,y,w,h , mPaintDebug);
		canvas.drawText(text, x, y + (h - y)/2 + 10, mPaint);
	}


	public String getText() {
		return text;
	}


	public void setText(String text) {
		this.text = text;
	}
	
}
