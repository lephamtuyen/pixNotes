package com.pixnotes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class TutorialFragment extends Fragment {
    
	private int mIdImage = 0;
    public static TutorialFragment newInstance(int mIdImage) {
    	TutorialFragment fragment = new TutorialFragment();
    	fragment.mIdImage = mIdImage;
        return fragment;
    }

    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View row = inflater.from(getActivity()).inflate(R.layout.image_tutorial, container, false);
        ImageView image = (ImageView)row.findViewById(R.id.img_tutorial);
        image.setBackgroundResource(mIdImage);

        return row;
    }

    
}
