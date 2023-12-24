package com.webviewnewone.puzzel;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

public class GridAdaptor extends BaseAdapter {
    private ArrayList<ImageView> mButtons = null;
    private int mColumnWidth, mColumnHeight;

    public GridAdaptor(ArrayList<ImageView> buttons, int columnWidth, int columnHeight) {
        mButtons = buttons;
        mColumnWidth = columnWidth;
        mColumnHeight = columnHeight;
    }

    @Override
    public int getCount() {
        return mButtons.size();
    }

    @Override
    public Object getItem(int position) {return (Object) mButtons.get(position);}

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView button;

        if (convertView == null) {
            button = mButtons.get(position);

        } else {
            button = (ImageView) convertView;
        }

        android.widget.AbsListView.LayoutParams params =
                new android.widget.AbsListView.LayoutParams(mColumnWidth, mColumnHeight);
       button.setLayoutParams(params);

        return button;
    }
}
