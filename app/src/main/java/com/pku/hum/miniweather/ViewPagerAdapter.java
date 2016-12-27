package com.pku.hum.miniweather;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


/**
 * Created by hum on 16-11-29.
 */

public class ViewPagerAdapter extends PagerAdapter{
    private List<View> views;
    private Context context;

    public ViewPagerAdapter(List<View> views, Context context) {
        this.context = context;
        this.views = views;
    }
    @Override
    public int getCount() {
        Log.d("guide","getCount");
        return views.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d("guide","instantiateItem");
        container.addView(views.get(position));
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        Log.d("guide","isViewFromObject");
        return (view == object);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(views.get(position));
        Log.d("guide","destroyItem");
    }

}
