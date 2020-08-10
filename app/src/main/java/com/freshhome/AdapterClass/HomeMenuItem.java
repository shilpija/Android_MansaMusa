package com.freshhome.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.MenuSupplier;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class HomeMenuItem extends BaseAdapter {
    Context context;
    ArrayList<MenuSupplier> arrayHomeItem;
    LayoutInflater inflater;

    public HomeMenuItem(Context context, ArrayList<MenuSupplier> arrayHomeItem) {
        this.context = context;
        this.arrayHomeItem = arrayHomeItem;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayHomeItem.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.singlerow_home, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.dish_name.setText(arrayHomeItem.get(i).getDname());
        mViewHolder.dishPrice.setText(arrayHomeItem.get(i).getDprice());
////        mViewHolder.dishTime.setText(arrayHomeItem.get(i).getDate_time());
//
        if(arrayHomeItem.get(i).getImageurl().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.demo).into(mViewHolder.dishimage);
        }else{
            Picasso.get().load(arrayHomeItem.get(i).getImageurl()).placeholder(R.drawable.demo).into(mViewHolder.dishimage);
        }
//
        mViewHolder.ratingBar.setRating(Float.parseFloat(arrayHomeItem.get(i).getDrating()));

        return convertView;
    }

    private class MyViewHolder {
        TextView dish_name, dishTime, dishPrice;
        ImageView dishimage;
        RatingBar ratingBar;

        public MyViewHolder(View convertView) {
            dish_name = (TextView) convertView.findViewById(R.id.dish_name);
            dishPrice = (TextView) convertView.findViewById(R.id.dishPrice);
            dishTime = (TextView) convertView.findViewById(R.id.dishTime);
            dishimage = (ImageView) convertView.findViewById(R.id.dishimage);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        }
    }
}