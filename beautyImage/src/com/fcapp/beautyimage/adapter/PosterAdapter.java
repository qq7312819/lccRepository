package com.fcapp.beautyimage.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class PosterAdapter extends PagerAdapter {
	
	private ArrayList list;
	
	public PosterAdapter(ArrayList list){
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup view, int position, Object object) {
		//��viewpager���Ƴ�childview
//		view.removeView(list.get(position));
	}

//	@Override
//	public Object instantiateItem(ViewGroup view, int position) {
//		//��viewpager�����childview
////		view.addView(images.get(position));
////		return images.get(position);
//	}

}
