package com.pixnotes.interfaceconstructe;

public interface ListenerEventUpdate {
	
	public void isShowSetting(boolean flag);
	public void isOnOff_Undo_Redo(boolean isOffUndo,boolean isOffRedo);
//	public void getWidthAndHeightCanvas(int width,int height);
	public void getIndexDeleteShapeInCurrentDraw(int indexCurrentDraw,int indexCurrentShapeDelete);
	public void showDeleteBar(boolean isShowDeleteBar,boolean isDeleteIcon);
	public void isShowingMenu(boolean isShowMenu);
	public void isOpenSettingPaint(int typeShape);
	public void isDoubleTap(float x , float y);
	
}
