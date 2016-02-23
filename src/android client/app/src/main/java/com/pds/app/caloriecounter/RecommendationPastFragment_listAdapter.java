package com.pds.app.caloriecounter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class RecommendationPastFragment_listAdapter extends BaseAdapter {
    private static ArrayList<String> mList;
    private LayoutInflater mInflater;

    public RecommendationPastFragment_listAdapter(Context recomPastFragment, ArrayList<String> list){
        mList = list;
        mInflater = LayoutInflater.from(recomPastFragment);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder holder;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.fragment_past_element_list, null);
            holder = new ViewHolder();
            holder.testTextView = (TextView) convertView.findViewById(R.id.textView_test);
            holder.testTextView.setText(mList.get(position));
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    static class ViewHolder{
        TextView testTextView;
    }

}
