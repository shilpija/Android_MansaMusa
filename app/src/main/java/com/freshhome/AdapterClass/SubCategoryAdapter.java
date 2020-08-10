package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.MultipleSubCategoryActivity;
import com.freshhome.R;
import com.freshhome.SupplierDetailActivity;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.SubCategory;
import com.freshhome.datamodel.SupplierInfo;
import com.freshhome.fragments.UserFavoFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class SubCategoryAdapter extends BaseAdapter {
    Context context;
    int arrayFavo;
    LayoutInflater inflater;
    ArrayList<SubCategory> arraySubCategory;
    ApiInterface apiInterface;
    ArrayList<SubCategory> array_sub;

    public SubCategoryAdapter(Context context, ArrayList<SubCategory> arraySubCategory) {
        this.context = context;
        this.arraySubCategory = arraySubCategory;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
        array_sub = new ArrayList<>();
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
            convertView = inflater.inflate(R.layout.single_row_sub_category, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }


        final ArrayList<NameID> array = arraySubCategory.get(i).getArrayList();
        String categories_selected = "";
        for (int j = 0; j < array.size(); j++) {
            if(array.get(j).isIsselected()) {
                if (categories_selected.equalsIgnoreCase("")) {
                    categories_selected = array.get(j).getName();
                } else {
                    categories_selected = categories_selected + "," + array.get(j).getName();
                }
            }
        }
        mViewHolder.text_more_categories.setText(categories_selected);
        mViewHolder.text_count.setText(String.valueOf(array.size()));
        mViewHolder.checkbox.setChecked(arraySubCategory.get(i).isIsselected());
        mViewHolder.checkbox.setText(arraySubCategory.get(i).getName());

//        mViewHolder.checkbox.setTag(i);
//        mViewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    array_sub=arraySubCategory.get(Integer.parseInt(mViewHolder.checkbox.getTag().toString())).getArrayList();
//                    Intent i = new Intent(context, MultipleSubCategoryActivity.class);
//                    i.putExtra("sub_categories", array_sub);
//                    context.startActivity(i);
//                }
//            }
//        });
        return convertView;
    }

    private class MyViewHolder {
        TextView text_more_categories, text_count, text_status;
        CheckBox checkbox;
        RatingBar ratingBar;
        LinearLayout linear_status, linear_main;

        public MyViewHolder(View convertView) {
            text_count = (TextView) convertView.findViewById(R.id.text_count);
            text_more_categories = (TextView) convertView.findViewById(R.id.text_more_categories);
            checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
        }
    }

}