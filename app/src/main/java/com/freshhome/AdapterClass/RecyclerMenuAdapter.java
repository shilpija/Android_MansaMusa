package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.HomeItemDetail;
import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.datamodel.MenuSupplier;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class RecyclerMenuAdapter extends RecyclerView.Adapter<RecyclerMenuAdapter.MyViewHolder> {
    Context context;
    ArrayList<MenuSupplier> array_menu;
    LayoutInflater inflater;

    public RecyclerMenuAdapter(Context context, ArrayList<MenuSupplier> array_menu) {
        this.context = context;
        this.array_menu = array_menu;
        inflater = LayoutInflater.from(this.context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_menu, parent, false);

        return new RecyclerMenuAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, int i) {
        mViewHolder.tvOriginalPrice.setPaintFlags(mViewHolder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (array_menu.get(i).getDiscount() != null && !array_menu.get(i).getDiscount().equalsIgnoreCase("null")
                && !array_menu.get(i).getDiscount().equalsIgnoreCase("0") && !array_menu.get(i).getDiscount().equalsIgnoreCase("0.00")) {
            try {
                double aa = Double.parseDouble(array_menu.get(i).getDprice());
                double disCou = 0.0;
                if (array_menu.get(i).getDiscount() != null && !array_menu.get(i).getDiscount().equalsIgnoreCase("null")) {
                    disCou = Double.parseDouble(array_menu.get(i).getDiscount());
                } else {
                    disCou = 0.0;
                }
                double totalAmt = (aa * disCou) / 100;
                double total2nd = aa - totalAmt;

                Log.e("VeerMenu ", " >> Test >> " + String.valueOf(total2nd));

                mViewHolder.tvOriginalPrice.setText(ConstantValues.currency + " " +array_menu.get(i).getDprice());
                mViewHolder.text_price.setText(ConstantValues.currency + " " +String.valueOf(total2nd));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mViewHolder.tvOriginalPrice.setVisibility(View.INVISIBLE);
            mViewHolder.text_price.setText(ConstantValues.currency + " " +array_menu.get(i).getDprice());
        }


        mViewHolder.text_dishname.setText(array_menu.get(i).getDname());
        //mViewHolder.text_price.setText(ConstantValues.currency + " " + array_menu.get(i).getDprice());
        mViewHolder.text_views.setText(array_menu.get(i).getDveiws());
        mViewHolder.text_available.setText(array_menu.get(i).getDavailable());
        mViewHolder.text_pending.setText(array_menu.get(i).getDpending());

        if (array_menu.get(i).getDstatus().equalsIgnoreCase("inactive")) {
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_orange));
            mViewHolder.text_status.setText("Offline");
            mViewHolder.linear_available.setVisibility(View.GONE);
        } else {
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_blue));
            mViewHolder.text_status.setText("Online");
            mViewHolder.linear_available.setVisibility(View.VISIBLE);
        }
        if (!array_menu.get(i).getImageurl().equalsIgnoreCase("")) {
            Picasso.get().load(array_menu.get(i).getImageurl()).placeholder(R.drawable.defualt_list).into(mViewHolder.imageMenu);
        } else {
            Picasso.get().load(R.drawable.defualt_list).into(mViewHolder.imageMenu);
        }

        //set tag to avoid exchange of value according to view create/destroy
        mViewHolder.linear_main.setTag(array_menu.get(i).getId());
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, ProductDetail.class);
                i.putExtra("from", "menu");
                i.putExtra("item_id", mViewHolder.linear_main.getTag().toString());
                i.putExtra("item_type", "2");
                i.putExtra("cart_count", "");
                i.putExtra("cart_price", "");
                context.startActivity(i);
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return array_menu.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_dishname, text_price, text_status, text_views, text_available, text_pending,tvOriginalPrice;
        LinearLayout linear_status, linear_available, linear_main;
        RatingBar ratingBar;
        ImageView imageMenu;

        public MyViewHolder(View convertView) {
            super(convertView);
            tvOriginalPrice = (TextView) convertView.findViewById(R.id.tvOriginalPrice);
            text_status = (TextView) convertView.findViewById(R.id.text_status);
            text_dishname = (TextView) convertView.findViewById(R.id.text_dishname);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            linear_status = (LinearLayout) convertView.findViewById(R.id.linear_status);
            linear_available = (LinearLayout) convertView.findViewById(R.id.linear_available);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
            text_views = (TextView) convertView.findViewById(R.id.text_views);
            text_available = (TextView) convertView.findViewById(R.id.text_available);
            text_pending = (TextView) convertView.findViewById(R.id.text_pending);

            imageMenu = (ImageView) convertView.findViewById(R.id.imageMenu);

        }
    }
}