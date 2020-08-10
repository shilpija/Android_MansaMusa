package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.MultipleSubCategoryActivity;
import com.freshhome.R;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class MultipleSubCategoryAdapter extends BaseAdapter {
    Context context;
    int arrayFavo;
    LayoutInflater inflater;
    ArrayList<NameID> arraySubCategory;
    ApiInterface apiInterface;

    public MultipleSubCategoryAdapter(Context context, ArrayList<NameID> arraySubCategory) {
        this.context = context;
        this.arraySubCategory = arraySubCategory;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arraySubCategory.size();
    }

    @Override
    public Object getItem(int i) {
        return arraySubCategory.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_sub_sub_category, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.checkbox.setText(arraySubCategory.get(i).getName());
        mViewHolder.checkbox.setChecked(arraySubCategory.get(i).isIsselected());
        return convertView;
    }

    private class MyViewHolder {

        CheckBox checkbox;

        public MyViewHolder(View convertView) {
            checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        }
    }

}