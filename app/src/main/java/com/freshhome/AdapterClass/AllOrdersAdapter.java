package com.freshhome.AdapterClass;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.DriverModule.AllOrderListActivity;
import com.freshhome.HomeItemDetail;
import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.MultipleLocOrder;
import com.freshhome.fragments.HomeFragment;
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

public class AllOrdersAdapter extends RecyclerView.Adapter<AllOrdersAdapter.MyViewHolder> {
    Context context;
    ArrayList<MultipleLocOrder> arrayOrders;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    //this id indicate type of item home food,shop,handmade
    String screen_id;

    public AllOrdersAdapter(Context context, ArrayList<MultipleLocOrder> arrayOrders) {
        this.context = context;
        this.arrayOrders = arrayOrders;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
        sessionManager = new UserSessionManager(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_order, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, int i) {
        mViewHolder.text_deliveyr_loc.setText(arrayOrders.get(i).getOrder_delivery_address());
        mViewHolder.text_pickup_loc.setText(arrayOrders.get(i).getKitchen_loc());
        mViewHolder.text_order_no.setText(arrayOrders.get(i).getOrder_id());
        mViewHolder.text_price.setText(arrayOrders.get(i).getOrder_total());
        mViewHolder.text_time.setText(arrayOrders.get(i).getOrder_delivery_time());

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayOrders.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_deliveyr_loc, text_pickup_loc, text_price, text_order_no, text_time;
        LinearLayout  linear_main;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_deliveyr_loc = (TextView) convertView.findViewById(R.id.text_deliveyr_loc);
            text_pickup_loc = (TextView) convertView.findViewById(R.id.text_pickup_loc);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            text_order_no= (TextView) convertView.findViewById(R.id.text_order_no);
            text_time= (TextView) convertView.findViewById(R.id.text_time);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }


}