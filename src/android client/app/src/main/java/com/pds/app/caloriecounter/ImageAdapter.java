package com.pds.app.caloriecounter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;

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
        View grid;
        LayoutInflater inflater = (LayoutInflater) _Context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = new View(_Context);
            grid = inflater.inflate(R.layout.grid_singlefood, null);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_imageView);
            RatingBar ratingBar = (RatingBar) grid.findViewById(R.id.grid_ratingBar);
            Picasso.with(this._Context)
                    .load(_urls.get(position))
                    .resize(330,330)
                    .into(imageView);

        } else {

            grid = (View) convertView;
        }

        return grid;
    }
}

