package com.pixnotes;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TutorialFragmentAdapter extends FragmentPagerAdapter{

	 protected static final int[] ICONS = new int[] {
         R.drawable.tutorial0,
         R.drawable.tutorial1,
         R.drawable.tutorial2,
         R.drawable.tutorial3,
         R.drawable.tutorial4
	};
	public TutorialFragmentAdapter(FragmentManager fm) {
	        super(fm);
	}
	 
	@Override
	public Fragment getItem(int position) {
		return TutorialFragment.newInstance(ICONS[position % ICONS.length]);
	}

	@Override
	public int getCount() {
		 return ICONS.length;
	}

}
