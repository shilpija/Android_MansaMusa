package com.freshhome.AdapterClass;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.HomeItemDetail;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.SupplierMenuActivity;
import com.freshhome.datamodel.HomeItem;
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

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder> {
    Context context;
    ArrayList<HomeItem> arrayHomeItem;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    OnUpdateBannerListener onUpdateBannerListener;
    //this id indicate type of item home food,shop,handmade
    String screen_id;
    private String isAllowGuest = "";
    private String supplier_id = "250";

    public HomeAdapter(Context context, ArrayList<HomeItem> arrayHomeItem, String screen_id) {
        this.context = context;
        this.arrayHomeItem = arrayHomeItem;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
        sessionManager = new UserSessionManager(context);
        this.screen_id = screen_id;

    }

    public void setOnUpdateBannerListener(OnUpdateBannerListener onUpdateBannerListener) {
        this.onUpdateBannerListener = onUpdateBannerListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.singlerow_home, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_discount.setPaintFlags(mViewHolder.text_discount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        mViewHolder.dish_name.setText(arrayHomeItem.get(i).getDish_name());


        if (arrayHomeItem.get(i).getDiscount() != null && !arrayHomeItem.get(i).getDiscount().equalsIgnoreCase("null")
                && !arrayHomeItem.get(i).getDiscount().equalsIgnoreCase("0") && !arrayHomeItem.get(i).getDiscount().equalsIgnoreCase("0.00")) {
            try {
                double aa = Double.parseDouble(arrayHomeItem.get(i).getDish_price());
                double disCou = 0.0;
                if (arrayHomeItem.get(i).getDiscount() != null && !arrayHomeItem.get(i).getDiscount().equalsIgnoreCase("null")) {
                    disCou = Double.parseDouble(arrayHomeItem.get(i).getDiscount());
                } else {
                    disCou = 0.0;
                }
                double totalAmt = (aa * disCou) / 100;
                double total2nd = aa - totalAmt;

                Log.e("AbhiVeer", " >> Test >> " + String.valueOf(total2nd));

                mViewHolder.text_discount.setText(arrayHomeItem.get(i).getDish_price());
                mViewHolder.dishPrice.setText(String.valueOf(total2nd));

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mViewHolder.text_discount.setVisibility(View.INVISIBLE);
            mViewHolder.dishPrice.setText(arrayHomeItem.get(i).getDish_price());
        }


        if (arrayHomeItem.get(i).getDish_image().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.placeholder).into(mViewHolder.dishimage);
        } else {
            Picasso.get().load(arrayHomeItem.get(i).getDish_image()).placeholder(R.drawable.placeholder).into(mViewHolder.dishimage);
        }
        mViewHolder.text_favo_count.setText(arrayHomeItem.get(i).getFavo_count());
        mViewHolder.text_user_count.setText(arrayHomeItem.get(i).getUser_views());
        mViewHolder.text_rate.setText(arrayHomeItem.get(i).getRate_point());
        //set tag to avoid suffling of data
        mViewHolder.image_favo.setTag(i);
        if (arrayHomeItem.get(i).getIsFavo().equalsIgnoreCase("1")) {
            mViewHolder.image_favo.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_green_filed));
        } else {
            mViewHolder.image_favo.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_green));
        }

        mViewHolder.image_favo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : LOGGEDIN CHECK
                if (sessionManager.isLoggedIn()) {
                    if (CommonMethods.checkConnection()) {
                        if (arrayHomeItem.get((Integer) mViewHolder.image_favo.getTag()).getIsFavo().equalsIgnoreCase("1")) {
                            AddRemovetofavoMenu(arrayHomeItem.get((Integer) mViewHolder.image_favo.getTag()).getDish_id(), mViewHolder.image_favo, true);
                        } else {
                            AddRemovetofavoMenu(arrayHomeItem.get((Integer) mViewHolder.image_favo.getTag()).getDish_id(), mViewHolder.image_favo, false);
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                    }
                } else {
                    CommonMethods.ShowLoginDialog(context);
                }

            }
        });
        if (arrayHomeItem.get(i).getCart_qty() != 0) {
            mViewHolder.text_qty.setText(String.valueOf(arrayHomeItem.get(i).getCart_qty()));
        }

        mViewHolder.dish_name.setTag(arrayHomeItem.get(i).isAttributes());
        mViewHolder.text_qty.setTag(i);
        mViewHolder.image_cart.setTag(arrayHomeItem.get(i).getDish_id());
        mViewHolder.image_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (screen_id.equalsIgnoreCase(ConstantValues.home_food)) {
                    Intent i = new Intent(context, HomeItemDetail.class);
                    i.putExtra("item_type", screen_id);
                    i.putExtra("item_id", mViewHolder.image_cart.getTag().toString());
                    i.putExtra("cart_count", "");
                    i.putExtra("cart_price", "");
                    context.startActivity(i);
                } else {
//                    MovetoProductDetail(mViewHolder.image_cart.getTag().toString(), screen_id, HomeFragment.text_items.getText().toString(), HomeFragment.text_total_price.getText().toString());
                    MovetoProductDetail(mViewHolder.image_cart.getTag().toString(),
                            screen_id, "", "");

                }


//                //TODO : LOGGEDIN CHECK
//                if (arrayHomeItem.get(i).getSupplier_id() != null && !arrayHomeItem.get(i).getSupplier_id().equalsIgnoreCase("")) {
//                    supplier_id = arrayHomeItem.get(i).getSupplier_id();
//                }
//
//                //if (sessionManager.isLoggedIn()) {
//                String qty = "";
//                if (!mViewHolder.text_qty.getText().toString().equalsIgnoreCase("")) {
//                    qty = String.valueOf(Integer.parseInt(mViewHolder.text_qty.getText().toString()) + 1);
//                } else {
//                    qty = "1";
//                }
//
//                if (mViewHolder.dish_name.getTag().toString().equalsIgnoreCase("false")) {
//                    if (CommonMethods.checkConnection()) {
//                        addToCart(mViewHolder.image_cart.getTag().toString(), qty, "", true, Integer.parseInt(mViewHolder.text_qty.getTag().toString()));
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
//                    }
//                } else {
//                    //move to detail screen to select attributes
//                    if (onUpdateBannerListener != null)
//                        onUpdateBannerListener.onMoveToProductDetails(mViewHolder.image_cart.getTag().toString(), screen_id);
//                        /*MovetoProductDetail(mViewHolder.image_cart.getTag().toString(), screen_id,
//                                HomeFragment.text_items.getText().toString(), HomeFragment.text_total_price.getText().toString());*/
//                }
//                } else {
//                    CommonMethods.ShowLoginDialog(context);
//                }


            }
        });

        // hide favo and cart button in case of supplier
        // show for user and guest user
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            mViewHolder.linear_cart_favo.setVisibility(View.GONE);
        } else {
            mViewHolder.linear_cart_favo.setVisibility(View.VISIBLE);
        }

        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (screen_id.equalsIgnoreCase(ConstantValues.home_food)) {
                    Intent i = new Intent(context, HomeItemDetail.class);
                    i.putExtra("item_type", screen_id);
                    i.putExtra("item_id", mViewHolder.image_cart.getTag().toString());
                    i.putExtra("cart_count", "");
                    i.putExtra("cart_price", "");
                    context.startActivity(i);
                } else {
//                    MovetoProductDetail(mViewHolder.image_cart.getTag().toString(), screen_id, HomeFragment.text_items.getText().toString(), HomeFragment.text_total_price.getText().toString());
                    MovetoProductDetail(mViewHolder.image_cart.getTag().toString(),
                            screen_id, "", "");

                }
            }
        });

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayHomeItem.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void AddRemovetofavoMenu(final String dish_id, final ImageView image_favo, final boolean isRemove) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls;
        if (isRemove) {
            calls = apiInterface.RemoveFavoDish(dish_id);
        } else {
            calls = apiInterface.AddFavoDish(dish_id);
        }

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
                            if (isRemove) {
                                image_favo.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_green));
                                updateFavoValue("0", dish_id);
                            } else {
                                updateFavoValue("1", dish_id);
                                image_favo.setImageDrawable(context.getResources().getDrawable(R.drawable.heart_green_filed));
                            }
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

    private void updateFavoValue(String value, String dish_id) {
        if (arrayHomeItem.size() != 0) {
            for (int i = 0; i < arrayHomeItem.size(); i++) {
                if (arrayHomeItem.get(i).getDish_id().equalsIgnoreCase(dish_id)) {
                    arrayHomeItem.get(i).setIsFavo(value);
                }
            }
        }
    }

    private void addToCart(final String menu_id, final String qty, String confirm, boolean isAdd, final int pos) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isAdd) {
            calls = apiInterface.AddUpdateDeleteCartItem(menu_id, qty);
        } else {
            calls = apiInterface.AddConfirm(menu_id, qty, confirm);
        }

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
                            if (object.getString("is_allow").equalsIgnoreCase("0")) {
                                //text and hit api
                                //isAllowGuest = "true";
                                //showcleardata(context, object.getString("confirm"), qty, menu_id, pos);
                                showClearCartDialog(context, object.getString("confirm"), qty, menu_id, pos);
                            } else {
                                success_Alert_Dialog(context, object.getString("success"));
                                if (object.has("cart_items")) {
                                    JSONObject cart_obj = object.getJSONObject("cart_total");
                                    if (onUpdateBannerListener != null)
                                        onUpdateBannerListener.onUpdateCardBanner(object.getString("cart_items"), cart_obj.getString("currency") + " " + cart_obj.getString("total"));
                                    //HomeFragment.updateCartBanner(sessionManager,object.getString("cart_items"), cart_obj.getString("currency") + " " + cart_obj.getString("total"));
                                } else {
                                    if (onUpdateBannerListener != null)
                                        onUpdateBannerListener.onUpdateCardBanner("", "");
                                    //HomeFragment.updateCartBanner(sessionManager,"", "");
                                }
                            }

                            arrayHomeItem.get(pos).setCart_qty(Integer.parseInt(qty));
                            notifyDataSetChanged();

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

    public void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                if (sessionManager.getSupplierInfo() != null && !sessionManager.getSupplierInfo().equalsIgnoreCase("")) {
                    if (supplier_id.equals(sessionManager.getSupplierInfo())) {
                        isAllowGuest = "false";
                    } else {
                        isAllowGuest = "true";
                    }
                } else {
                    sessionManager.saveSupplierInfo(supplier_id);
                    isAllowGuest = "true";
                }

                if (isAllowGuest.equalsIgnoreCase("true")) {
                    isAllowGuest = "false";
                    showDiffSellerLayout(context, supplier_id);
                }

            }
        });

        // Showing Alert Message

        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    // forDiffrentsupllier..
    public void showDiffSellerLayout(final Context context, final String sellerId) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_show_different_seller_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView tvAddMore = dialog.findViewById(R.id.tvAddMore);
        TextView tvgocart = dialog.findViewById(R.id.tvgocart);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        tvgocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_cart = new Intent(context, MainActivity_NavDrawer.class);
                i_cart.putExtra("OpenFrag", ConstantValues.OPENCART);
                context.startActivity(i_cart);
                dialog.dismiss();
            }
        });

        tvAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_menu = new Intent(context, SupplierMenuActivity.class);
                i_menu.putExtra("supplier_id", sellerId);
                context.startActivity(i_menu);
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void Error_Alert_Dialog(final Context context, String message, final String qty, final String dish_id, final int pos) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage(message);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                removeAllCartQty();
                if (CommonMethods.checkConnection()) {
                    addToCart(dish_id, qty, "1", false, pos);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });
        alertDialog.show();
    }

    private void removeAllCartQty() {
        for (int i = 0; i < arrayHomeItem.size(); i++) {
            arrayHomeItem.get(i).setCart_qty(0);
        }
        notifyDataSetChanged();
    }

    private void MovetoProductDetail(String item_id, String screenid, String cart_count, String cart_price) {
        Intent i = new Intent(context, ProductDetail.class);
        i.putExtra("item_type", screenid);
        i.putExtra("item_id", item_id);
        i.putExtra("cart_count", cart_count);
        i.putExtra("cart_price", cart_price);
        context.startActivity(i);
    }

    private void showcleardata(final Context context, String message, final String qty, final String dish_id, final int pos){

        if (sessionManager.getSupplierInfo() != null && !sessionManager.getSupplierInfo().equalsIgnoreCase("")) {
            if (supplier_id.equals(sessionManager.getSupplierInfo())) {
                showClearCartDialog(context,message , qty, dish_id, pos);
            }
        } else {
            sessionManager.saveSupplierInfo(supplier_id);

        }
    }

    public void showClearCartDialog(final Context context, String message, final String qty, final String dish_id, final int pos) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_less_amount_checkout_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView tvShowMsg = dialog.findViewById(R.id.tvShowMsg);
        TextView tvheader = dialog.findViewById(R.id.rvheader);
        TextView tvAddMore = dialog.findViewById(R.id.tvAddMore);
        TextView tvyes = dialog.findViewById(R.id.tvyes);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvheader.setText("Clear Cart");
        tvShowMsg.setText(message);


        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                removeAllCartQty();
                if (CommonMethods.checkConnection()) {
                    addToCart(dish_id, qty, "1", false, pos);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public interface OnUpdateBannerListener {
        void onUpdateCardBanner(String cardItem, String currency);

        void onMoveToProductDetails(String itemId, String screenId);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView dish_name, text_discount, dishPrice, text_user_count, text_favo_count, text_qty, text_rate, dishTime;
        ImageView dishimage, image_favo, image_cart;
        LinearLayout linear_cart_favo, linear_main;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_user_count = (TextView) convertView.findViewById(R.id.text_user_count);
            text_favo_count = (TextView) convertView.findViewById(R.id.text_favo_count);

            text_rate = (TextView) convertView.findViewById(R.id.text_rate);
            dish_name = (TextView) convertView.findViewById(R.id.dish_name);
            text_qty = (TextView) convertView.findViewById(R.id.text_qty);
            dishPrice = (TextView) convertView.findViewById(R.id.dishPrice);
            dishTime = (TextView) convertView.findViewById(R.id.dishTime);
            text_discount = (TextView) convertView.findViewById(R.id.text_discount);
            dishimage = (ImageView) convertView.findViewById(R.id.dishimage);
            image_favo = (ImageView) convertView.findViewById(R.id.image_favo);
            image_cart = (ImageView) convertView.findViewById(R.id.image_cart);
            linear_cart_favo = (LinearLayout) convertView.findViewById(R.id.linear_cart_favo);

            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }

}