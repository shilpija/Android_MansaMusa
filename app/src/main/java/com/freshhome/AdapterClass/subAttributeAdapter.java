package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v4.widget.CompoundButtonCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.R;
import com.freshhome.datamodel.subAttributes;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class subAttributeAdapter extends BaseAdapter {
    Context context;
    ArrayList<subAttributes> arraysubAttributes;
    LayoutInflater inflater;
    boolean isSupplierDetail;
    String currency;
    String attribute_type;
    boolean isFromHome;

    public subAttributeAdapter(Context context, ArrayList<subAttributes> arraysubAttributes, String currency, String attribute_type, boolean isFromHome) {
        this.context = context;
        this.arraysubAttributes = arraysubAttributes;
        inflater = LayoutInflater.from(this.context);
        this.currency = currency;
        this.attribute_type = attribute_type;
        this.isFromHome = isFromHome;
    }

    @Override
    public int getCount() {
        return arraysubAttributes.size();
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
            convertView = inflater.inflate(R.layout.layout_sub_attributes, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        if (attribute_type.equalsIgnoreCase(ConstantValues.attributeTypecheckbox)) {
            mViewHolder.checkbox.setVisibility(View.VISIBLE);
            mViewHolder.radiobutton.setVisibility(View.GONE);
            mViewHolder.textview.setVisibility(View.GONE);
        } else if (attribute_type.equalsIgnoreCase(ConstantValues.attributeTyperadio)) {
            mViewHolder.checkbox.setVisibility(View.GONE);
            mViewHolder.radiobutton.setVisibility(View.VISIBLE);
            mViewHolder.textview.setVisibility(View.GONE);
        } else if (attribute_type.equalsIgnoreCase(ConstantValues.attributeTypeSpinner)) {
            mViewHolder.checkbox.setVisibility(View.GONE);
            mViewHolder.radiobutton.setVisibility(View.GONE);
            mViewHolder.textview.setVisibility(View.VISIBLE);
        } else {
            mViewHolder.checkbox.setVisibility(View.GONE);
            mViewHolder.radiobutton.setVisibility(View.GONE);
            mViewHolder.textview.setVisibility(View.GONE);
        }
        mViewHolder.textview.setText(arraysubAttributes.get(i).getSubAttr_name() + "(" +
                arraysubAttributes.get(i).getSubAttr_price_prefix() + currency + " " + arraysubAttributes.get(i).getSubAttr_price() + ")");

        mViewHolder.checkbox.setText(arraysubAttributes.get(i).getSubAttr_name() + "(" +
                arraysubAttributes.get(i).getSubAttr_price_prefix() + currency + " " + arraysubAttributes.get(i).getSubAttr_price() + ")");

        mViewHolder.radiobutton.setText(arraysubAttributes.get(i).getSubAttr_name() + "(" +
                arraysubAttributes.get(i).getSubAttr_price_prefix() + currency + " " + arraysubAttributes.get(i).getSubAttr_price() + ")");

        return convertView;
    }

    private class MyViewHolder {
        TextView textview;        CheckBox checkbox;

        RadioButton radiobutton;

        public MyViewHolder(View convertView) {
            textview = (TextView) convertView.findViewById(R.id.textview);
            checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            radiobutton = (RadioButton) convertView.findViewById(R.id.radiobutton);
            if (isFromHome) {
                checkbox.setEnabled(true);
                radiobutton.setEnabled(true);
                if (Build.VERSION.SDK_INT < 21) {
                    CompoundButtonCompat.setButtonTintList(checkbox, ColorStateList.valueOf(context.getResources().getColor(R.color.app_color_blue)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
                } else {
                    checkbox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.app_color_blue)));//setButtonTintList is accessible directly on API>19
                }
            } else {
                checkbox.setEnabled(false);
                radiobutton.setEnabled(false);
                if (Build.VERSION.SDK_INT < 21) {
                    CompoundButtonCompat.setButtonTintList(checkbox, ColorStateList.valueOf(context.getResources().getColor(R.color.light_gray_20)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
                } else {
                    checkbox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.light_gray_20)));//setButtonTintList is accessible directly on API>19
                }
            }
        }
    }
}