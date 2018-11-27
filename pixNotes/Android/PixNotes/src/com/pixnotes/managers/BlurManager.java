package com.pixnotes.managers;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.enrique.stackblur.StackBlurManager;

import android.content.Context;
import android.graphics.Bitmap;

public class BlurManager {
	private final int MAX_SIZE = 3;
	private Context _context;
	private HashMap<Integer, Blur> _hashMap;
	private Bitmap _mainBitmap;
	private StackBlurManager _stackBlur;
	
	public BlurManager (Context context){
		this._context = context;
		this._hashMap = new HashMap<Integer, Blur>();
	}
	
	public void setMainBitmap(Bitmap bitmap){
		cleanData();
		this._mainBitmap = bitmap;
		this._stackBlur = new StackBlurManager(bitmap);
	}
	
	public boolean hasBlurWithOpacity(int opacity){
		if(_hashMap.containsKey(opacity)){
			return true;
		}
		return false;
	}
	
	public Bitmap getBlurBitmapWithOpacity(int opacity){
		if (hasBlurWithOpacity(opacity)){
			return _hashMap.get(opacity)._bitmap;
		} else {
			Bitmap bitmap = _stackBlur.processRenderScript(_context, opacity);
			addNewBitmap(opacity, bitmap);
			return getBlurBitmapWithOpacity(opacity);
		}
	}
	
	private void addNewBitmap(int opacity, Bitmap bitmap){
		if (_hashMap.containsKey(opacity))
			return;
		if(_hashMap.size() > MAX_SIZE){
			removeOldestBitmap();
		}
		_hashMap.put(opacity, new Blur(bitmap));		
	}
	
	private void removeOldestBitmap(){
		if(_hashMap.size() == 0)
			return;
		Object[] listKey = _hashMap.keySet().toArray();
		Integer oldestKey;
		oldestKey = (Integer)listKey[0];
		for (int i = 1; i < listKey.length; i++){
			Integer tmpKey = (Integer)listKey[i];
			if (_hashMap.get(oldestKey)._addingTime.after(_hashMap.get(tmpKey)._addingTime)){
				oldestKey = tmpKey;
			}
		}
		_hashMap.remove(oldestKey);
	}
	
	public void cleanData(){
		cleanBitmap(_mainBitmap);
		_hashMap.clear();
		System.gc();
	}
	
	private void cleanBitmap(Bitmap bitmap){
		if(bitmap != null){
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	public class Blur{
		public Date _addingTime;
		public Bitmap _bitmap;
		public Blur(Bitmap bitmap){
			_addingTime = Calendar.getInstance().getTime();
			_bitmap = bitmap;
		}
	}
}
