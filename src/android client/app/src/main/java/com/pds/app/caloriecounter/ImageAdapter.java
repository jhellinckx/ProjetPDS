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
    private ArrayList<View> _views;

    public ImageAdapter(Context c, ArrayList<String> urls) {
        _Context = c;
        _urls = urls;
        _views = new ArrayList<>();
    }

    public int getCount() {
        return _urls.size();
    }

    public Object getItem(int position) {
        return _views.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View grid;
        LayoutInflater inflater = (LayoutInflater) _Context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            grid = inflater.inflate(R.layout.grid_singlefood, null);
            ImageView imageView = (ImageView) grid.findViewById(R.id.grid_imageView);
            Picasso.with(this._Context)
                    .load(_urls.get(position))
                    .tag("tag")
                    .resize(330,330)
                    .transform(new RoundedTransformation(100, 0))
                    .into(imageView);
            _views.add(grid);

        } else {

            grid = (View) convertView;
        }

        return grid;
    }
}

