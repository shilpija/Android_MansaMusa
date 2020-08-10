package com.freshhome.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.R;
import com.freshhome.datamodel.categories;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class categoryDetailBrandAdapter extends BaseAdapter {
    Context context;
    ArrayList<categories> arraydata;
    LayoutInflater inflater;
    ApiInterface apiInterface;

    public categoryDetailBrandAdapter(Context context, ArrayList<categories> arraydata) {
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
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_brand, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        if (arraydata.get(i).getimage().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.placeholder_h).into(mViewHolder.img_brand);
        } else {
            Picasso.get().load(arraydata.get(i).getimage()).placeholder(R.drawable.placeholder_h).into(mViewHolder.img_brand);
        }

        return convertView;
    }

    private class MyViewHolder {
        ImageView img_brand;


        public MyViewHolder(View convertView) {
            img_brand = (ImageView) convertView.findViewById(R.id.img_brand);

        }
    }

}