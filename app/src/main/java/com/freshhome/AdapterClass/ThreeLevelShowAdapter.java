package com.freshhome.AdapterClass;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.datamodel.categories;
import com.freshhome.fragments.AllProductShowFromCatFragment;
import com.freshhome.fragments.BuyerSearchFragment;
import com.freshhome.fragments.ThreeLevelCategoryFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ThreeLevelShowAdapter extends BaseAdapter {
    Context context;
    ArrayList<categories> arraydata;
    LayoutInflater inflater;
    ApiInterface apiInterface;

    public ThreeLevelShowAdapter(Context context, ArrayList<categories> arraydata) {
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
            convertView = inflater.inflate(R.layout.single_row_sub_cat, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.text_name.setText(arraydata.get(i).get_name());
        if (arraydata.get(i).getimage().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.placeholder).into(mViewHolder.img_cat);
        } else {
            Picasso.get().load(arraydata.get(i).getimage()).placeholder(R.drawable.placeholder).into(mViewHolder.img_cat);
        }
        //mViewHolder.linear_main.setTag(i);
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity_NavDrawer.heading.setText(arraydata.get(i).get_name());
                FragmentTransaction ft = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                AllProductShowFromCatFragment frag = new AllProductShowFromCatFragment();
                Bundle bundle = new Bundle();
                bundle.putString("cat_id", arraydata.get(i).getId());
                bundle.putString("cat_header", arraydata.get(i).get_name());
                frag.setArguments(bundle);
                ft.replace(R.id.main_linear, frag);
                ft.addToBackStack(null);
                ft.commit();
            }
        });


        return convertView;
    }

    private class MyViewHolder {
        TextView text_name;
        ImageView img_cat;
        LinearLayout linear_main;

        public MyViewHolder(View convertView) {
            text_name = (TextView) convertView.findViewById(R.id.text_name);
            img_cat = (ImageView) convertView.findViewById(R.id.img_cat);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }
}
