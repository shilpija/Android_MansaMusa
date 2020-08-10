package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.AddAddressActivity;
import com.freshhome.R;
import com.freshhome.datamodel.DeliveryAddress;
import com.freshhome.datamodel.HomeItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class DeliveryAddressAdapter extends BaseAdapter {
    Context context;
    ArrayList<DeliveryAddress> arrayAdress;
    LayoutInflater inflater;

    public DeliveryAddressAdapter(Context context, ArrayList<DeliveryAddress> arrayAdress) {
        this.context = context;
        this.arrayAdress = arrayAdress;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayAdress.size();
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
            convertView = inflater.inflate(R.layout.single_row_deliveryaddress, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        if(arrayAdress.get(i).getIsdefault().equalsIgnoreCase("yes")){
            mViewHolder.text_default.setVisibility(View.VISIBLE);
        }else{
            mViewHolder.text_default.setVisibility(View.GONE);
        }
        mViewHolder.text_title.setText(arrayAdress.get(i).getAddressTitle());
        mViewHolder.text_address.setText(arrayAdress.get(i).getAddressLocation());
        mViewHolder.image_edit.setTag(i);
        mViewHolder.image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(context, AddAddressActivity.class);
                i.putExtra("id",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressId());
                i.putExtra("title",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressTitle());
                i.putExtra("loc",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressLocation());
                i.putExtra("city",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressCity());
                i.putExtra("buildingno",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressBuildingno());
                i.putExtra("floorno",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressFloorno());
                i.putExtra("flatno",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressFlatno());
                i.putExtra("landmark",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddressLandmark());
                i.putExtra("latitude",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddress_lat());
                i.putExtra("longitude",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getAddress_lng());
                i.putExtra("default",arrayAdress.get((Integer) mViewHolder.image_edit.getTag()).getIsdefault());

                context.startActivity(i);
            }
        });
        return convertView;
    }

    private class MyViewHolder {
        TextView text_title, text_address, text_default;
        ImageView image_edit;
        RatingBar ratingBar;

        public MyViewHolder(View convertView) {
            text_title = (TextView) convertView.findViewById(R.id.text_title);
            text_address = (TextView) convertView.findViewById(R.id.text_address);
            text_default = (TextView) convertView.findViewById(R.id.text_default);
            image_edit = (ImageView) convertView.findViewById(R.id.image_edit);
//            ratingBar=(RatingBar)convertView.findViewById(R.id.ratingBar);
        }
    }
}