package com.freshhome.AdapterClass;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.R;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.subAttributes;
import com.freshhome.fragments.UserCartFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class RecyclerCartAdapter extends RecyclerView.Adapter<RecyclerCartAdapter.MyViewHolder> {
    Context context;
    ArrayList<Cart> array_cart;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    UserCartFragment userCartFragment;

    public RecyclerCartAdapter(Context context, ArrayList<Cart> array_cart, UserCartFragment userCartFragment) {
        this.context = context;
        this.array_cart = array_cart;
        this.userCartFragment = userCartFragment;
        inflater = LayoutInflater.from(this.context);
        apiInterface = ApiClient.getInstance().getClient();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_cart, parent, false);

        return new RecyclerCartAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, int i) {
        mViewHolder.text_dishname.setText(array_cart.get(i).getDish_name());
        mViewHolder.text_suppliername.setText("by " + array_cart.get(i).getSupplier_name());
        DecimalFormat precision = new DecimalFormat("0.00");
        String price_i = array_cart.get(i).getDish_price();

        if (price_i.contains(",")) {
            price_i = price_i.replace(",", "");
        }
        mViewHolder.text_price.setText(array_cart.get(i).getCurrency() + " " +
                precision.format(Double.parseDouble(price_i)));

        String price = array_cart.get(i).getTotal_price();
        if (price.contains(",")) {
            price = price.replace(",", "");
        }
        mViewHolder.text_total_price.setText(array_cart.get(i).getCurrency() + " " +
                precision.format(Double.parseDouble(price)));

        mViewHolder.text_qty.setText(array_cart.get(i).getDish_qty());
        if (!array_cart.get(i).getDish_image().equalsIgnoreCase("")) {
            Picasso.get().load(array_cart.get(i).getDish_image()).placeholder(R.drawable.demo).into(mViewHolder.imageDish);
        } else {
            Picasso.get().load(R.drawable.food).into(mViewHolder.imageDish);
        }

        mViewHolder.text_minus.setTag(array_cart.get(i).getDish_id());
        mViewHolder.text_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(false, mViewHolder.text_minus.getTag().toString());
            }
        });
        mViewHolder.text_plus.setTag(array_cart.get(i).getDish_id());
        mViewHolder.text_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateQuantity(true, mViewHolder.text_plus.getTag().toString());
            }
        });

        mViewHolder.image_delete.setTag(i);
        mViewHolder.image_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    addToCart(array_cart.get(Integer.parseInt(mViewHolder.image_delete.getTag().toString())).getDish_id(), "0");
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

        //TODO CHECK IF THE ATTRIBUTE ARRAY SIZE >0 THEN SET UP DATA IN LINEAR_ATTIBUTES ELSE HIDE LINEARLAYOUT
        if (array_cart.get(i).getArrayAttributes().size() > 0) {
            mViewHolder.linear_attribues_options.setVisibility(View.VISIBLE);
            setUpAttributeINUI(array_cart.get(i).getArrayAttributes(), mViewHolder.linear_attribues_options);
        } else {
            mViewHolder.linear_attribues_options.setVisibility(View.GONE);
        }
    }

    private void setUpAttributeINUI(ArrayList<subAttributes> arrayAttributes, LinearLayout linear_attribues_options) {
        for (int i = 0; i < arrayAttributes.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_cart_attributes, null);
            TextView text_value = (TextView) view.findViewById(R.id.text_value);
            text_value.setText(arrayAttributes.get(i).getSubAttr_name());
            TextView text_name = (TextView) view.findViewById(R.id.text_name);
            text_name.setText(arrayAttributes.get(i).getSubAttr_main_name());
            linear_attribues_options.addView(view);
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return array_cart.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_dishname, text_suppliername, text_price, text_total_price, text_qty, text_plus, text_minus;
        LinearLayout linear_status, linear_available, linear_main, linear_attribues_options;
        RatingBar ratingBar;
        ImageView image_delete;
        CircleImageView imageDish;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_suppliername = (TextView) convertView.findViewById(R.id.text_suppliername);
            text_dishname = (TextView) convertView.findViewById(R.id.text_dishname);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            text_total_price = (TextView) convertView.findViewById(R.id.text_total_price);
            text_qty = (TextView) convertView.findViewById(R.id.text_qty);
            text_plus = (TextView) convertView.findViewById(R.id.text_plus);
            text_minus = (TextView) convertView.findViewById(R.id.text_minus);
            imageDish = (CircleImageView) convertView.findViewById(R.id.imageDish);
            image_delete = (ImageView) convertView.findViewById(R.id.image_delete);
            linear_attribues_options = (LinearLayout) convertView.findViewById(R.id.linear_attribues_options);

        }
    }

    private void addToCart(final String menu_id, final String qty) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        calls = apiInterface.AddUpdateDeleteCartItem(menu_id, qty);

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
                            if (CommonMethods.checkConnection()) {
                                userCartFragment.getCartdata();
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                            }
//                            CommonUtilFunctions.success_Alert_Dialog(context, object.getString("success"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(context, obj.getString("msg"));
                        }
                    } else {
//                        Error_Alert_Dialog(HomeItemDetail.this, "Change", qty);
                        CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e)

                {
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

    private void UpdateQtyList(String dish_id, String qty) {
        for (int i = 0; i < array_cart.size(); i++) {
            if (array_cart.get(i).getDish_id().equalsIgnoreCase(dish_id)) {
                array_cart.get(i).setDish_qty(qty);
                notifyDataSetChanged();
            }
        }
    }

    private void RemoveFromList(String dish_id) {
        for (int i = 0; i < array_cart.size(); i++) {
            if (array_cart.get(i).getDish_id().equalsIgnoreCase(dish_id)) {
                array_cart.remove(i);
                notifyDataSetChanged();
            }
        }
    }

    private void updateQuantity(boolean isAdd, String dish_id) {
        int qty = 0;
        for (int i = 0; i < array_cart.size(); i++) {
            if (array_cart.get(i).getDish_id().equalsIgnoreCase(dish_id)) {
                if (isAdd) {
                    qty = Integer.parseInt(array_cart.get(i).getDish_qty()) + 1;
                    if (CommonMethods.checkConnection()) {
                        addToCart(dish_id, String.valueOf(qty));
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                    }
                } else {
                    if (array_cart.get(i).getDish_qty().equalsIgnoreCase("1")) {
                        // delete item
                        if (CommonMethods.checkConnection()) {
                            addToCart(dish_id, "0");
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                        }
                    } else {
                        qty = Integer.parseInt(array_cart.get(i).getDish_qty()) - 1;
                        if (CommonMethods.checkConnection()) {
                            addToCart(dish_id, String.valueOf(qty));
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                        }

                    }
                }
            }
        }
    }
}