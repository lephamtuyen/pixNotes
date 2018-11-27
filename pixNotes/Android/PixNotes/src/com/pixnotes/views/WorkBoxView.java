package com.pixnotes.views;

import com.pixnotes.utils.LogUtils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.widget.RelativeLayout;

public class WorkBoxView extends RelativeLayout{

//	public float mScaleFactor=1.0f;
//	public float mTranslateX = 0.f;
//	public float mTranslateY = 0.f;
	public WorkBoxView(Context context) {
		super(context);
		
	}

//    @Override
//    protected void dispatchDraw(Canvas canvas) {
//        
//        canvas.save(Canvas.MATRIX_SAVE_FLAG);
//        canvas.scale(mScaleFactor, mScaleFactor, mTranslateX, mTranslateY);
//        super.dispatchDraw(canvas);
//        canvas.restore();
//
//        
//    }
//
//
//    @Override
//    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
//        // TODO Auto-generated method stub
////    	LogUtils.LogError("invalidateChildInParent", "invalidateChildInParent invalidateChildInParent");
//        return super.invalidateChildInParent(location, dirty);
//    }
//
//    protected void onLayout(boolean changed, int l, int t, int r, int b)
//    {
//        int count = getChildCount();
//        LogUtils.LogError("count", "count count === "+count);
//        for(int i=0;i<count;i++){
//            View child = getChildAt(i); 
//            if(child.getVisibility()!=GONE){
//                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)child.getLayoutParams();
//                child.layout(
//                    (int)(params.leftMargin * mScaleFactor), 
//                    (int)(params.topMargin * mScaleFactor), 
//                    (int)((params.leftMargin + child.getMeasuredWidth()) * mScaleFactor), 
//                    (int)((params.topMargin + child.getMeasuredHeight()) * mScaleFactor) 
//                    );
//            }
//        }
//    }
//	
//    public void resizeChild()
//    {
//    	 int count = getChildCount();
//         LogUtils.LogError("count", "count count === "+count);
//         
//         RelativeLayout.LayoutParams paramsParent = (RelativeLayout.LayoutParams)getLayoutParams();
//         this.layout((int)(paramsParent.leftMargin * mScaleFactor), 
//                 (int)(paramsParent.topMargin * mScaleFactor), 
//                 (int)((paramsParent.leftMargin + getMeasuredWidth()) * mScaleFactor), 
//                 (int)((paramsParent.topMargin + getMeasuredHeight()) * mScaleFactor) );
//         
//         for(int i=0;i<count;i++){
//             View child = getChildAt(i); 
//             if(child.getVisibility()!=GONE){
//                 RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)child.getLayoutParams();
//                 child.layout(
//                     (int)(params.leftMargin * mScaleFactor), 
//                     (int)(params.topMargin * mScaleFactor), 
//                     (int)((params.leftMargin + child.getMeasuredWidth()) * mScaleFactor), 
//                     (int)((params.topMargin + child.getMeasuredHeight()) * mScaleFactor) 
//                     );
//             }
//         }
//    }
//    public void scaleView(float scaleFactor,float mTranslateX,float mTranslateY)
//    {
//    	this.mScaleFactor = scaleFactor;
//    	this.mTranslateX = mTranslateX;
//    	this.mTranslateY = mTranslateY;
//    	invalidate();
//    }
}
