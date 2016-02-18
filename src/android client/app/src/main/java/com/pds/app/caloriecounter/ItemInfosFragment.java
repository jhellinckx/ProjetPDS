package com.pds.app.caloriecounter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class ItemInfosFragment extends Fragment {

    private ImageView image;
    private TextView product;
    private TextView url;
    private TextView cal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_infos, container, false);

        image = (ImageView) view.findViewById(R.id.imageView);
        product = (TextView) view.findViewById(R.id.product_name);
        url = (TextView) view.findViewById(R.id.url_txt);
        cal = (TextView) view.findViewById(R.id.calorie_text);

        return view;
    }

    public void setProductName(String name){
        while(product == null);
        if (name != null){
            product.setText(name);
        }
    }

    public void setUrlName(String urln){
        if (url != null){
            url.setText(urln);
        }
    }

    public void setCal(String calorie){
        if (calorie != null){
            cal.setText(calorie);
        }
    }

    public void setImage(String imageurl){
        if (imageurl != null){
            Picasso.with(getContext()).load(imageurl).into(image);
        }
    }
}
