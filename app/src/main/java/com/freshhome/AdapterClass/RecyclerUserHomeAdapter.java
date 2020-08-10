package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.datamodel.categories;
import com.freshhome.fragments.CategoryDetailFragment;
import com.freshhome.fragments.ViewAllItemsFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by HP on 01-05-2019.
 */

public class RecyclerUserHomeAdapter extends RecyclerView.Adapter<RecyclerUserHomeAdapter.MyViewHolder> {
    Context context;
    ArrayList<categories> array_list;
    LayoutInflater inflater;

    public RecyclerUserHomeAdapter(Context context, ArrayList<categories> array_list) {
        this.context = context;
        this.array_list = array_list;
        inflater = LayoutInflater.from(this.context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_category, parent, false);

        return new RecyclerUserHomeAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, int i) {
        mViewHolder.text_name.setText(array_list.get(i).get_name());
        if (!array_list.get(i).getimage().equalsIgnoreCase("")) {
            Picasso.get().load(array_list.get(i).getimage()).placeholder(R.drawable.placeholder).into(mViewHolder.img_cat);
        } else {
            Picasso.get().load(R.drawable.placeholder).into(mViewHolder.img_cat);
        }

        //set tag to avoid exchange of value according to view create/destroy
        mViewHolder.linear_main.setTag(i);
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity_NavDrawer.heading.setText(array_list.get((Integer) mViewHolder.linear_main.getTag()).get_name());
                FragmentTransaction ft = ((FragmentActivity)v.getContext()).getSupportFragmentManager().beginTransaction();
                CategoryDetailFragment frag = new CategoryDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("cat_id", array_list.get((Integer) mViewHolder.linear_main.getTag()).getId());
                bundle.putString("cat_header", array_list.get((Integer) mViewHolder.linear_main.getTag()).get_name());
                frag.setArguments(bundle);
                ft.replace(R.id.main_linear, frag);
                ft.addToBackStack(null);
                ft.commit();
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
        CircleImageView img_cat;
        LinearLayout linear_main;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_name = (TextView) convertView.findViewById(R.id.text_name);
            img_cat = (CircleImageView) convertView.findViewById(R.id.img_cat);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }
}