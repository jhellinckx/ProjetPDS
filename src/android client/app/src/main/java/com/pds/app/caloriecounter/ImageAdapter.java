package com.pds.app.caloriecounter;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context _Context;
    private ArrayList<String> _urls;

    public ImageAdapter(Context c, ArrayList<String> urls) {
        _Context = c;
        _urls = urls;
    }

    public int getCount() {
        return _urls.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(_Context);
        } else {
            imageView = (ImageView) convertView;
        }

        Picasso.with(this._Context)
                .load(_urls.get(position))
                .resize(330,330)
                .into(imageView);
        return imageView;
    }
}

