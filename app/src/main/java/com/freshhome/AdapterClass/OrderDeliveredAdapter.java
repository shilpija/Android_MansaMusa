package com.freshhome.AdapterClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.R;
import com.freshhome.datamodel.MyKitchenOrders;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by HP on 01-05-2018.
 */

public class OrderDeliveredAdapter extends RecyclerView.Adapter<OrderDeliveredAdapter.MyViewHolder> {
    Context context;
    int i;
    LayoutInflater inflater;
    ArrayList<MyKitchenOrders> arrayOrder;

    public OrderDeliveredAdapter(Context context, ArrayList<MyKitchenOrders> arrayOrder) {
        this.context = context;
        this.arrayOrder = arrayOrder;
        inflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_orderdelivered, parent, false);
        return new OrderDeliveredAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder mViewHolder, int i) {
        mViewHolder.text_order_price.setText(arrayOrder.get(i).getCurrency_code() + " " + arrayOrder.get(i).getTotal());
       //sk mViewHolder.text_items.setText(arrayOrder.get(i).getTotal_items() + " Items");
        mViewHolder.text_items.setText(arrayOrder.get(i).getTotal_items() + " Product");
        mViewHolder.text_name.setText(arrayOrder.get(i).getuserName());
        mViewHolder.payment_method.setText(arrayOrder.get(i).getPayment_method());
      //sk  mViewHolder.text_date.setText(arrayOrder.get(i).getDate());
        mViewHolder.text_date.setText(arrayOrder.get(i).getOrder_date());
        if (!arrayOrder.get(i).getRating().equalsIgnoreCase("null")) {
            mViewHolder.linear_ratings.setVisibility(View.VISIBLE);
            mViewHolder.ratingBar.setRating(Float.parseFloat(arrayOrder.get(i).getRating()));
        } else {
            mViewHolder.linear_ratings.setVisibility(View.GONE);
        }
        if (!arrayOrder.get(i).getUser_profile_pic().equalsIgnoreCase("null") && !arrayOrder.get(i).getUser_profile_pic().equalsIgnoreCase("")) {
            Picasso.get().load(arrayOrder.get(i).getUser_profile_pic()).placeholder(R.drawable.demo).into(mViewHolder.circle_image);
        } else {
            Picasso.get().load(R.drawable.demo).into(mViewHolder.circle_image);
        }

        mViewHolder.text_status.setText(arrayOrder.get(i).getOrder_status());
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayOrder.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_name, text_items, text_date, text_loc, payment_method, text_order_price, text_status;
        LinearLayout linear_ratings;
        RatingBar ratingBar;
        CircleImageView circle_image;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_status = (TextView) convertView.findViewById(R.id.text_status);
            text_name = (TextView) convertView.findViewById(R.id.text_name);
            text_items = (TextView) convertView.findViewById(R.id.text_items);
            text_date = (TextView) convertView.findViewById(R.id.text_date);
            text_loc = (TextView) convertView.findViewById(R.id.text_loc);
            payment_method = (TextView) convertView.findViewById(R.id.payment_method);
            text_order_price = (TextView) convertView.findViewById(R.id.text_order_price);
            linear_ratings = (LinearLayout) convertView.findViewById(R.id.linear_ratings);
            circle_image = (CircleImageView) convertView.findViewById(R.id.circle_image);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
        }
    }
}