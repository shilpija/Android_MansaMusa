package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.HomeItemDetail;
import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
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

public class UserFavoDishAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ArrayList<HomeItem> arrayFavoDishes;
    ApiInterface apiInterface;

    public UserFavoDishAdapter(Context context, ArrayList<HomeItem> arrayFavoDishes) {
        this.context = context;
        this.arrayFavoDishes = arrayFavoDishes;
        inflater = LayoutInflater.from(this.context);
        apiInterface = ApiClient.getInstance().getClient();
    }

    @Override
    public int getCount() {
        return arrayFavoDishes.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayFavoDishes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_favo_dish, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.text_dishname.setText(arrayFavoDishes.get(i).getDish_name());
        mViewHolder.text_price.setText(arrayFavoDishes.get(i).getDish_price());
        mViewHolder.text_categories.setText(arrayFavoDishes.get(i).getDish_categories());
        mViewHolder.text_cuisines.setText(arrayFavoDishes.get(i).getDish_cuisines());
        mViewHolder.text_meal.setText(arrayFavoDishes.get(i).getDish_meal());
        mViewHolder.text_status.setText(arrayFavoDishes.get(i).getDate_status());
        if(arrayFavoDishes.get(i).getDate_status().equalsIgnoreCase("inactive")){
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_orange));
        }else{
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_blue));
        }

        if (arrayFavoDishes.get(i).getDish_image().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.food).into(mViewHolder.image_dish);
        } else {
            Picasso.get().load(arrayFavoDishes.get(i).getDish_image()).placeholder(R.drawable.demo).into(mViewHolder.image_dish);
        }
        mViewHolder.ratingBar.setRating(Float.parseFloat(arrayFavoDishes.get(i).getRate_point()));
        mViewHolder.imageFavo.setTag(i);
        mViewHolder.imageFavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    RemoveFromfavoDish(arrayFavoDishes.get((Integer) mViewHolder.imageFavo.getTag()).getDish_id());
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

        mViewHolder.text_dishname.setTag(arrayFavoDishes.get(i).getProduct_type());
        mViewHolder.linear_main.setTag(arrayFavoDishes.get(i).getDish_id());
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewHolder.text_dishname.getTag().toString().equals("1")) {
                    Intent i = new Intent(context, HomeItemDetail.class);
                    i.putExtra("item_id", mViewHolder.linear_main.getTag().toString());
                    context.startActivity(i);
                }else{
                    Intent i = new Intent(context, ProductDetail.class);
                    i.putExtra("item_id", mViewHolder.linear_main.getTag().toString());
                    i.putExtra("item_type", mViewHolder.text_dishname.getTag().toString());
                    context.startActivity(i);
                }
            }
        });
        return convertView;
    }

    private class MyViewHolder {
        TextView text_dishname, text_price, text_categories, text_cuisines, text_meal, text_status;
        ImageView image_dish, imageFavo;
        RatingBar ratingBar;
        LinearLayout linear_main, linear_status;

        public MyViewHolder(View convertView) {
            text_dishname = (TextView) convertView.findViewById(R.id.text_dishname);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            text_categories = (TextView) convertView.findViewById(R.id.text_categories);
            text_cuisines = (TextView) convertView.findViewById(R.id.text_cuisines);
            text_meal = (TextView) convertView.findViewById(R.id.text_meal);
            text_status = (TextView) convertView.findViewById(R.id.text_status);
            image_dish = (ImageView) convertView.findViewById(R.id.image_dish);
            imageFavo = (ImageView) convertView.findViewById(R.id.imageFavo);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
            linear_status = (LinearLayout) convertView.findViewById(R.id.linear_status);
        }
    }

    private void RemoveFromfavoDish(final String dish_id) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.RemoveFavoDish(dish_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            CommonUtilFunctions.success_Alert_Dialog(context, jsonObject.getString("msg"));
                            removeDishFromArray(dish_id);
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(context, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.server_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.server_error));
            }
        });
    }

    private void removeDishFromArray(String dish_id) {
        for (int i = 0; i < arrayFavoDishes.size(); i++) {
            if (arrayFavoDishes.get(i).getDish_id().equalsIgnoreCase(dish_id)) {
                arrayFavoDishes.remove(i);
            }
            if (arrayFavoDishes.size() == 0) {
                UserFavoFragment.linear_no_data.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}