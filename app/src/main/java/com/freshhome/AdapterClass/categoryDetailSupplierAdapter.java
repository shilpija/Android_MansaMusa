package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.R;
import com.freshhome.SupplierDetailActivity;
import com.freshhome.datamodel.ScheduleData;
import com.freshhome.datamodel.categories;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by HP on 01-05-2018.
 */

public class categoryDetailSupplierAdapter extends BaseAdapter {
    Context context;
    ArrayList<categories> arraydata;
    LayoutInflater inflater;
    ApiInterface apiInterface;

    public categoryDetailSupplierAdapter(Context context, ArrayList<categories> arraydata) {
        this.context = context;
        this.arraydata = arraydata;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arraydata.size();
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
    public View getView(final int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_supplier, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        mViewHolder.text_name.setText(arraydata.get(i).get_name());
        if (!arraydata.get(i).getimage().equalsIgnoreCase("")) {
            Picasso.get().load(arraydata.get(i).getimage()).placeholder(R.drawable.defualt_list).into(mViewHolder.img_kitchen);
        } else {
            Picasso.get().load(R.drawable.defualt_list).into(mViewHolder.img_kitchen);
        }

        if (arraydata.get(i).getAvailable().equalsIgnoreCase("online")) {
            mViewHolder.img_kitchen.setBorderColor(context.getResources().getColor(R.color.app_color_green_40));
        } else {
            mViewHolder.img_kitchen.setBorderColor(context.getResources().getColor(R.color.light_gray));
        }

        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i_supplier = new Intent(context, SupplierDetailActivity.class);
                i_supplier.putExtra("supplier_id", arraydata.get(i).getId());
                i_supplier.putExtra("supplier_name", "");
                i_supplier.putExtra("supplier_rating", "");
                i_supplier.putExtra("supplier_location", "");
                i_supplier.putExtra("supplier_url", "");
                context.startActivity(i_supplier);
            }
        });

        return convertView;
    }

    private class MyViewHolder {
        TextView text_name;
        LinearLayout linear_status, linear_available, linear_main;
        RatingBar ratingBar;
        CircleImageView img_kitchen;

        public MyViewHolder(View convertView) {
            text_name = (TextView) convertView.findViewById(R.id.text_name);
            img_kitchen = (CircleImageView) convertView.findViewById(R.id.img_kitchen);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }

}