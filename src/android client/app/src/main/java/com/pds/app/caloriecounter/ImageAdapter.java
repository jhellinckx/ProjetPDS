package com.pds.app.caloriecounter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.squareup.picasso.Picasso;

import org.calorycounter.shared.models.EdibleItem;
import org.calorycounter.shared.models.EdibleItemImage;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context _Context;
    private ArrayList<EdibleItem> _items;
    private ArrayList<View> _views;

    public ImageAdapter(Context c, ArrayList<EdibleItem> items) {
        _Context = c;
        _items = items;
        _views = new ArrayList<>();
    }

    public int getCount() {
        return _items.size();
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
            byte[] img_bytes = _items.get(position).getImagePic().getImageBytesArray();
            Bitmap bmp;
            bmp = BitmapFactory.decodeByteArray(img_bytes, 0, img_bytes.length);
            imageView.setImageBitmap(bmp);
            _views.add(grid);

        } else {

            grid = (View) convertView;
        }

        return grid;
    }
}

