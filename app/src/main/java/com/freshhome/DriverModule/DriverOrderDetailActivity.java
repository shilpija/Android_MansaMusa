package com.freshhome.DriverModule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.UserOrderMenuAdapter;
import com.freshhome.ChooseCard;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.TrackOrderActivity;

import com.freshhome.UserOrderDetailActivity;
import com.freshhome.datamodel.Cart;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverOrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recycler_menuitemList;
    boolean isPending = true;
    ImageView image_back;
    TextView text_heading, text_status, text_order_date, text_loc,
            text_phone, text_payment_method, text_totalprice, text_menu_items,
            text_getinvoice, text_additional_req, text_additional_phn,
            text_track_order, text_supplier_phone, text_supplier_loc, text_supplier_name, text_user_name;
    ApiInterface apiInterface;
    String order_id = "", invoice_url = "", cancel_msg = "", card_id = "", kitchen_lat = "", kitchen_lng = "", user_lat = "", user_lng = "", kitchen_loc = "", user_loc = "";
    ArrayList<Cart> arrayMenuItem;
    LinearLayout linear_accept_order;
    boolean isFromDriver = false;
    BottomSheetDialog dialog;
    LinearLayout linear_send;
    EditText edit_msg;
    BottomSheetDialog bottomdialog;
    TextView text_cardno;
    ImageView img_delete;
    private int CARD_CODE = 123;
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_order_detail);
        arrayMenuItem = new ArrayList<>();
        sessionManager=new UserSessionManager(DriverOrderDetailActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        order_id = getIntent().getStringExtra("order_id");

        recycler_menuitemList = (RecyclerView) findViewById(R.id.recycler_menuitemList);
        text_getinvoice = (TextView) findViewById(R.id.text_getinvoice);
        text_getinvoice.setOnClickListener(this);
        text_track_order = (TextView) findViewById(R.id.text_track_order);
        text_track_order.setOnClickListener(this);
        text_heading = (TextView) findViewById(R.id.text_heading);
        text_status = (TextView) findViewById(R.id.text_status);
        text_order_date = (TextView) findViewById(R.id.text_order_date);
        text_loc = (TextView) findViewById(R.id.text_loc);

        text_phone = (TextView) findViewById(R.id.text_phone);
        text_payment_method = (TextView) findViewById(R.id.text_payment_method);
        text_totalprice = (TextView) findViewById(R.id.text_totalprice);

        text_menu_items = (TextView) findViewById(R.id.text_menu_items);

        text_supplier_phone = (TextView) findViewById(R.id.text_supplier_phone);
        text_supplier_loc = (TextView) findViewById(R.id.text_supplier_loc);
        text_supplier_name = (TextView) findViewById(R.id.text_supplier_name);
        text_user_name = (TextView) findViewById(R.id.text_user_name);

        text_additional_req = (TextView) findViewById(R.id.text_additional_req);
        text_additional_phn = (TextView) findViewById(R.id.text_additional_phn);
        linear_accept_order = (LinearLayout) findViewById(R.id.linear_accept_order);
        linear_accept_order.setOnClickListener(this);

        if (CommonMethods.checkConnection()) {
            getOrderDetail();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.internetconnection));
        }
    }

    private void getOrderDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverOrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetDriverOrderDetail(order_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject obj = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayMenuItem = new ArrayList<>();
                        if (obj.getString("code").equalsIgnoreCase("200")) {
                            JSONObject object = obj.getJSONObject("order_info");
                            text_heading.setText(getResources().getString(R.string.order) + "#" + object.getString("order_id"));
                            text_status.setText(object.getString("order_status"));

                            if (object.getString("order_status").equalsIgnoreCase("processing")) {
                                linear_accept_order.setVisibility(View.VISIBLE);
                            } else {
                                linear_accept_order.setVisibility(View.GONE);
                            }

                            if (object.getString("order_status").equalsIgnoreCase("completed")) {
                                text_track_order.setVisibility(View.GONE);
                            }

                            text_order_date.setText(object.getString("order_date"));
                            text_payment_method.setText(object.getString("payment_method"));
                            text_menu_items.setText(object.getString("total_items") + " " + getResources().getString(R.string.items));
                            JSONObject object_d = object.getJSONObject("driver_earning");
                            text_totalprice.setText(object_d.getString("currency") + " " + object_d.getString("total"));

                            JSONObject obj_user = object.getJSONObject("user_info");
                            text_user_name.setText(obj_user.getString("name"));
                            text_phone.setText(obj_user.getString("phone_number"));
                            JSONObject obj_loc = obj_user.getJSONObject("delivery_address");
                            text_loc.setText(obj_loc.getString("location"));
                            user_lat = obj_loc.getString("latitude");
                            user_lng = obj_loc.getString("longitude");
                            user_loc = obj_loc.getString("location");

                            text_additional_phn.setText(CommonMethods.checkNull(object.getString("additional_phone_number")));
                            text_additional_req.setText(CommonMethods.checkNull(object.getString("additional_notes")));

                            JSONObject object_k = object.getJSONObject("kitchen_location");
                            text_supplier_name.setText(object_k.getString("username"));
                            text_supplier_loc.setText(object_k.getString("location"));
                            text_supplier_phone.setText(object_k.getString("phone_number"));
                            kitchen_lat = object_k.getString("latitude");
                            kitchen_lng = object_k.getString("longitude");
                            kitchen_loc = object_k.getString("location");
//                            JSONArray arry_items = object.getJSONArray("order_items");
//                            for (int j = 0; j < arry_items.length(); j++) {
//                                JSONObject obj_item = arry_items.getJSONObject(j);
//                                Cart cart = new Cart();
//                                cart.setDish_name(obj_item.getString("name"));
//                                cart.setDish_price(obj_item.getString("price"));
//                                cart.setTotal_price(obj_item.getString("total"));
//                                cart.setDish_qty(obj_item.getString("quantity"));
//                                cart.setDish_id(obj_item.getString("dish_id"));
//
//                                cart.setDish_image(obj_item.getString("dish_image"));
//                                JSONObject obj_rating = obj_item.getJSONObject("dish_reviews");
//                                cart.setDish_rating(CommonMethods.checkNull(obj_rating.getString("rating")));
//                                cart.setDish_reviews(obj_rating.getString("review"));
//
//                                cart.setProduct_type(object.getString("order_type"));
//                                arrayMenuItem.add(cart);
//                            }
//
//                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
//                            recycler_menuitemList.setLayoutManager(mLayoutManager);
//                            recycler_menuitemList.setItemAnimator(new DefaultItemAnimator());
//                            UserOrderMenuAdapter adapter = new UserOrderMenuAdapter(DriverOrderDetailActivity.this, arrayMenuItem, isPending, order_id, isFromDriver);
//                            recycler_menuitemList.setAdapter(adapter);


                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                DriverOrderDetailActivity.this.finish();
                break;

            case R.id.linear_accept_order:
                if (CommonMethods.checkConnection()) {
                    showKitchenDetails_Dialog(order_id);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_getinvoice:
                if (!invoice_url.equalsIgnoreCase("")) {
                    CommonMethods.openInvoice(DriverOrderDetailActivity.this, invoice_url);
                }
                break;


            case R.id.text_track_order:
                Intent i_track = new Intent(DriverOrderDetailActivity.this, TrackOrderActivity.class);
                i_track.putExtra("kitchen_lat", kitchen_lat);
                i_track.putExtra("kitchen_lng", kitchen_lng);
                i_track.putExtra("user_lat", user_lat);
                i_track.putExtra("user_lng", user_lng);
                i_track.putExtra("kitchen_loc", kitchen_loc);
                i_track.putExtra("user_loc", user_loc);
                i_track.putExtra("driver_username", sessionManager.getDriveDetails().get(UserSessionManager.KEY_USERNAME));
                startActivity(i_track);
                break;

            case R.id.text_cancel:
                if (bottomdialog.isShowing()) {
                    bottomdialog.dismiss();
                }
                break;

            case R.id.text_cardno:
                Intent i = new Intent(DriverOrderDetailActivity.this, ChooseCard.class);
                startActivityForResult(i, CARD_CODE);
                break;


        }
    }


    @Override
    protected void onResume() {
        super.onResume();

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
                Intent i = new Intent();
                i.putExtra("accept", "true");
                setResult(Activity.RESULT_OK, i);
                DriverOrderDetailActivity.this.finish();

            }
        });

        // Showing Alert Message

        alertDialog.show();
    }

    private void showKitchenDetails_Dialog(final String order_id) {

        View view = getLayoutInflater().inflate(R.layout.layout_order_detail_dialog, null);
        bottomdialog = new BottomSheetDialog(DriverOrderDetailActivity.this);
        bottomdialog.setContentView(view);
        bottomdialog.setCanceledOnTouchOutside(false);
        TextView text_cancel = (TextView) bottomdialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);

        ImageView img_type = (ImageView) bottomdialog.findViewById(R.id.img_type);
        img_type.setVisibility(View.INVISIBLE);

        //show wallet and credit card option as hold money
        LinearLayout linear_wallet = (LinearLayout) bottomdialog.findViewById(R.id.linear_wallet);
        LinearLayout linear_credit_card = (LinearLayout) bottomdialog.findViewById(R.id.linear_credit_card);
        TextView text_amt_wallet = (TextView) bottomdialog.findViewById(R.id.text_amt_wallet);
        text_cardno = (TextView) bottomdialog.findViewById(R.id.text_cardno);
        text_cardno.setOnClickListener(this);

        //remove credit card
        img_delete = (ImageView) bottomdialog.findViewById(R.id.img_delete);
        img_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_cardno.setText(getResources().getString(R.string.select_card));
                text_cardno.setOnClickListener(this);
                img_delete.setVisibility(View.GONE);
                card_id = "";
            }
        });

        //accepet order
        LinearLayout linear_Accept_order = (LinearLayout) bottomdialog.findViewById(R.id.linear_Accept_order);
        linear_Accept_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (card_id.equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.select_card));
                } else {
                    if (CommonMethods.checkConnection()) {
                        accept_order(order_id, card_id);
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
            }
        });


        //order detail
        TextView text_view_order_detail = (TextView) bottomdialog.findViewById(R.id.text_view_order_detail);
        text_view_order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DriverOrderDetailActivity.this, UserOrderDetailActivity.class);
                i.putExtra("ispending", true);
                i.putExtra("order_id", order_id);
                i.putExtra("fromDriver", true);
                startActivity(i);
            }
        });

        bottomdialog.show();
    }

    private void accept_order(String order_id, String card_id) {
        final ProgressDialog progressDialog = new ProgressDialog(DriverOrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.DAcceptOrder(order_id, card_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject obj = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (obj.getString("code").equalsIgnoreCase("200")) {
                            JSONObject object = obj.getJSONObject("success");
                            success_Alert_Dialog(DriverOrderDetailActivity.this, object.getString("msg"));
                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(DriverOrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CARD_CODE) {
            if (data != null && data.hasExtra("cardid")) {
                card_id = data.getStringExtra("cardid");
                text_cardno.setText(data.getStringExtra("maskedno"));
                text_cardno.setOnClickListener(null);
                img_delete.setVisibility(View.VISIBLE);
            }
        }
    }
}
