package com.freshhome.AdapterClass;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.R;
import com.freshhome.datamodel.MenuSupplier;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class MenuAdapter extends BaseAdapter {
    Context context;
    ArrayList<MenuSupplier> array_menu;
    LayoutInflater inflater;

    public MenuAdapter(Context context, ArrayList<MenuSupplier> array_menu) {
        this.context = context;
        this.array_menu = array_menu;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return array_menu.size();
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
            convertView = inflater.inflate(R.layout.single_row_menu, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.text_dishname.setText(array_menu.get(i).getDname());
        String dPrice = array_menu.get(i).getDprice();
        String discount = array_menu.get(i).getDiscount();
        if(discount!=null && !discount.isEmpty() && !discount.equals("null") && !discount.equals("0.00")){
            try {
                double price = Double.parseDouble(dPrice.split("\\s")[1]);
                double discountPercentage = (price*Double.parseDouble(discount))/100;
                double discountPrice = price - discountPercentage;
                mViewHolder.text_price.setVisibility(View.VISIBLE);
                mViewHolder.text_price.setText(discount+"%off    AED "+new DecimalFormat("#.##").format(discountPrice));
                mViewHolder.tvOriginalPrice.setText(dPrice);
                mViewHolder.tvOriginalPrice.setTextColor(context.getResources().getColor(R.color.light_gray));
                mViewHolder.tvOriginalPrice.setPaintFlags( mViewHolder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else {
            mViewHolder.tvOriginalPrice.setText(dPrice);
            mViewHolder.tvOriginalPrice.setTextColor(context.getResources().getColor(R.color.black));
            mViewHolder.tvOriginalPrice.setPaintFlags(mViewHolder.tvOriginalPrice.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            mViewHolder.text_price.setVisibility(View.GONE);
        }
        mViewHolder.text_views.setText(array_menu.get(i).getDveiws());
        mViewHolder.text_available.setText(array_menu.get(i).getDavailable());
        mViewHolder.text_pending.setText(array_menu.get(i).getDpending());

        if (array_menu.get(i).getDstatus().equalsIgnoreCase("inactive")) {
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_orange));
            mViewHolder.text_status.setText("Offline");
            mViewHolder.linear_available.setVisibility(View.GONE);
        } else if(array_menu.get(i).getDstatus().equalsIgnoreCase("active")){
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_blue));
            mViewHolder.text_status.setText("Online");
            mViewHolder.linear_available.setVisibility(View.GONE);
        }else{
            mViewHolder.linear_status.setVisibility(View.GONE);
        }
        if (!array_menu.get(i).getImageurl().equalsIgnoreCase("")) {
            Picasso.get().load(array_menu.get(i).getImageurl()).placeholder(R.drawable.defualt_list).into(mViewHolder.imageMenu);
        }else{
            Picasso.get().load(R.drawable.defualt_list).into(mViewHolder.imageMenu);
        }

        return convertView;
    }

    private class MyViewHolder {
        TextView text_dishname, text_price, text_status, text_views, text_available, text_pending,tvOriginalPrice;
        LinearLayout linear_status, linear_available;
        RatingBar ratingBar;
        ImageView imageMenu;

        public MyViewHolder(View convertView) {
            text_status = (TextView) convertView.findViewById(R.id.text_status);
            text_dishname = (TextView) convertView.findViewById(R.id.text_dishname);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            linear_status = (LinearLayout) convertView.findViewById(R.id.linear_status);
            linear_available = (LinearLayout) convertView.findViewById(R.id.linear_available);

            text_views = (TextView) convertView.findViewById(R.id.text_views);
            text_available = (TextView) convertView.findViewById(R.id.text_available);
            text_pending = (TextView) convertView.findViewById(R.id.text_pending);
            tvOriginalPrice = (TextView) convertView.findViewById(R.id.tvOriginalPrice);

            imageMenu = (ImageView) convertView.findViewById(R.id.imageMenu);

        }
    }
}