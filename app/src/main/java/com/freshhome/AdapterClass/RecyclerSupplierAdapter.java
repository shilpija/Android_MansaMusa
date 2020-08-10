package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.SupplierDetailActivity;
import com.freshhome.datamodel.categories;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by HP on 01-05-2019.
 */

public class RecyclerSupplierAdapter extends RecyclerView.Adapter<RecyclerSupplierAdapter.MyViewHolder> {
    Context context;
    ArrayList<categories> array_list;
    LayoutInflater inflater;

    public RecyclerSupplierAdapter(Context context, ArrayList<categories> array_list) {
        this.context = context;
        this.array_list = array_list;
        inflater = LayoutInflater.from(this.context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_supplier, parent, false);

        return new RecyclerSupplierAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_name.setText(array_list.get(i).get_name());
        if (!array_list.get(i).getimage().equalsIgnoreCase("")) {
            Picasso.get().load(array_list.get(i).getimage()).placeholder(R.drawable.defualt_list).into(mViewHolder.img_kitchen);
        } else {
            Picasso.get().load(R.drawable.defualt_list).into(mViewHolder.img_kitchen);
        }

        if (array_list.get(i).getAvailable().equalsIgnoreCase("online")) {
            mViewHolder.img_kitchen.setBorderColor(context.getResources().getColor(R.color.app_color_green_40));
        } else {
            mViewHolder.img_kitchen.setBorderColor(context.getResources().getColor(R.color.light_gray));
        }

        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_supplier = new Intent(context, SupplierDetailActivity.class);
                i_supplier.putExtra("supplier_id", array_list.get(i).getId());
                i_supplier.putExtra("supplier_name", "");
                i_supplier.putExtra("supplier_rating", "");
                i_supplier.putExtra("supplier_location", "");
                i_supplier.putExtra("supplier_url", "");
                context.startActivity(i_supplier);
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return array_list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_name;
        LinearLayout linear_status, linear_available, linear_main;
        RatingBar ratingBar;
        CircleImageView img_kitchen;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_name = (TextView) convertView.findViewById(R.id.text_name);
            img_kitchen = (CircleImageView) convertView.findViewById(R.id.img_kitchen);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }
}