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
import com.freshhome.R;
import com.freshhome.SupplierDetailActivity;
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

public class UserFavoChefAdapter extends BaseAdapter {
    Context context;
    int arrayFavo;
    LayoutInflater inflater;
    ArrayList<SupplierInfo> arrayFavoSupplier;
    ApiInterface apiInterface;

    public UserFavoChefAdapter(Context context, ArrayList<SupplierInfo> arrayFavoSupplier) {
        this.context = context;
        this.arrayFavoSupplier = arrayFavoSupplier;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return arrayFavoSupplier.size();
    }

    @Override
    public Object getItem(int i) {
        return arrayFavoSupplier.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_favo_chef, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.text_chefname.setText(arrayFavoSupplier.get(i).getSname());
        mViewHolder.text_loc.setText(arrayFavoSupplier.get(i).getSloc());

        mViewHolder.text_status.setText(arrayFavoSupplier.get(i).getSstatus());
        if (arrayFavoSupplier.get(i).getSstatus().equalsIgnoreCase("online")) {
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_blue));
        } else {
            mViewHolder.linear_status.setBackgroundColor(context.getResources().getColor(R.color.app_color_orange));
        }

        if (arrayFavoSupplier.get(i).getSimage().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.food).into(mViewHolder.image_chef);
        } else {
            Picasso.get().load(arrayFavoSupplier.get(i).getSimage()).placeholder(R.drawable.demo).into(mViewHolder.image_chef);
        }
        mViewHolder.ratingBar.setRating(Float.parseFloat(arrayFavoSupplier.get(i).getSrating()));
        mViewHolder.imagefavo.setTag(i);
        mViewHolder.imagefavo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //remove from list
                if (CommonMethods.checkConnection()) {
                    RemovefavoSupplier(arrayFavoSupplier.get((Integer) mViewHolder.imagefavo.getTag()).getSid());
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });
        mViewHolder.linear_main.setTag(arrayFavoSupplier.get(i).getSid());
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, SupplierDetailActivity.class);
                i.putExtra("supplier_id", mViewHolder.linear_main.getTag().toString());
                i.putExtra("supplier_name", "");
                i.putExtra("supplier_rating", "");
                i.putExtra("supplier_location", "");
                i.putExtra("supplier_url", "");
                context.startActivity(i);
            }
        });
        return convertView;
    }

    private class MyViewHolder {
        TextView text_chefname, text_loc, text_status;
        ImageView image_chef, imagefavo;
        RatingBar ratingBar;
        LinearLayout linear_status, linear_main;

        public MyViewHolder(View convertView) {
            text_chefname = (TextView) convertView.findViewById(R.id.text_chefname);
            text_loc = (TextView) convertView.findViewById(R.id.text_loc);
            text_status = (TextView) convertView.findViewById(R.id.text_status);
            image_chef = (ImageView) convertView.findViewById(R.id.image_chef);
            imagefavo = (ImageView) convertView.findViewById(R.id.imagefavo);
            ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);
            linear_status = (LinearLayout) convertView.findViewById(R.id.linear_status);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }

    private void RemovefavoSupplier(final String supplier_id) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.RemoveFavoSupplier(supplier_id);

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
                            removeSupplierFromArray(supplier_id);
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

    private void removeSupplierFromArray(String supplier_id) {
        for (int i = 0; i < arrayFavoSupplier.size(); i++) {
            if (arrayFavoSupplier.get(i).getSid().equalsIgnoreCase(supplier_id)) {
                arrayFavoSupplier.remove(i);
            }
            if (arrayFavoSupplier.size() == 0) {
                UserFavoFragment.linear_no_data.setVisibility(View.VISIBLE);
            }
            notifyDataSetChanged();
        }
    }
}