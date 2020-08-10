package com.freshhome;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.UserOrderMenuAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.TotalBreakdown;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserOrderDetailActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recycler_menuitemList;
    private String orderStatus,estimate_time;
    boolean isPending = true;
    ImageView image_back;
    TextView text_heading, text_status, text_order_date, text_loc,
            text_email, text_phone, text_payment_method, text_totalprice, text_menu_items,
            text_getinvoice, text_cancel_order, text_cancel, text_additional_req,
            text_additional_phn, text_track_order, tv_return_order;
    ApiInterface apiInterface;
    String order_id = "", invoice_url = "", cancel_msg = "";
    ArrayList<Cart> arrayMenuItem;
    LinearLayout linear_repeat, linear_total_breakdown;
    boolean isFromDriver = false;
    BottomSheetDialog dialog;
    LinearLayout linear_send;
    EditText edit_msg;
    ArrayList<TotalBreakdown> totalpaymentArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_order_detail);
        arrayMenuItem = new ArrayList<>();
        totalpaymentArray = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        if (getIntent().hasExtra("fromDriver")) {
            isFromDriver = getIntent().getBooleanExtra("fromDriver", false);
        }
        order_id = getIntent().getStringExtra("order_id");
        isPending = getIntent().getBooleanExtra("ispending", true);
        recycler_menuitemList = (RecyclerView) findViewById(R.id.recycler_menuitemList);
        text_getinvoice = (TextView) findViewById(R.id.text_getinvoice);
        text_getinvoice.setOnClickListener(this);
        text_track_order = (TextView) findViewById(R.id.text_track_order);
        text_track_order.setOnClickListener(this);
        tv_return_order = (TextView) findViewById(R.id.tv_return_order);
        tv_return_order.setOnClickListener(this);


        text_heading = (TextView) findViewById(R.id.text_heading);
        text_status = (TextView) findViewById(R.id.text_status);
        text_order_date = (TextView) findViewById(R.id.text_order_date);
        text_loc = (TextView) findViewById(R.id.text_loc);
        text_email = (TextView) findViewById(R.id.text_email);
        text_phone = (TextView) findViewById(R.id.text_phone);
        text_payment_method = (TextView) findViewById(R.id.text_payment_method);
        text_totalprice = (TextView) findViewById(R.id.text_totalprice);
        text_menu_items = (TextView) findViewById(R.id.text_menu_items);
        text_cancel_order = (TextView) findViewById(R.id.text_cancel_order);
        text_cancel_order.setOnClickListener(this);
        text_additional_req = (TextView) findViewById(R.id.text_additional_req);
        text_additional_phn = (TextView) findViewById(R.id.text_additional_phn);
        linear_repeat = (LinearLayout) findViewById(R.id.linear_repeat);
        linear_repeat.setOnClickListener(this);
        linear_total_breakdown = (LinearLayout) findViewById(R.id.linear_total_breakdown);

        if (isPending) {
            text_status.setText("Pending");
            linear_repeat.setVisibility(View.GONE);
            text_cancel_order.setVisibility(View.VISIBLE);
        } else {
            text_status.setText("Completed");
            linear_repeat.setVisibility(View.VISIBLE);
            text_cancel_order.setVisibility(View.GONE);
        }

        if (isFromDriver) {
            text_getinvoice.setVisibility(View.GONE);
            linear_repeat.setVisibility(View.GONE);
            text_cancel_order.setVisibility(View.GONE);
            text_track_order.setVisibility(View.GONE);
        } else {
            text_getinvoice.setVisibility(View.VISIBLE);
            linear_repeat.setVisibility(View.VISIBLE);
            text_track_order.setVisibility(View.VISIBLE);
        }


    }

    private void getOrderDetail() {
        final ProgressDialog progressDialog = new ProgressDialog(UserOrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls;
        if (isFromDriver) {
            calls = apiInterface.GetDriverOrderDetail(order_id);
        } else {
            calls = apiInterface.GetUserOrderDetail(order_id);
        }

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
                            orderStatus = object.getString("order_status");
                            estimate_time = object.getString("estimate_deliver_time");
                            text_status.setText(object.getString("order_status"));
                            text_order_date.setText(object.getString("order_date"));
                            text_payment_method.setText(object.getString("payment_method"));
                            text_menu_items.setText(object.getString("total_items") + " " + getResources().getString(R.string.items));
                            text_totalprice.setText(object.getString("currency_code") + " " + object.getString("total"));

                            JSONObject obj_user = object.getJSONObject("user_info");
                            text_email.setText(obj_user.getString("email"));
                            text_phone.setText(obj_user.getString("phone_number"));
                            JSONObject obj_loc = obj_user.getJSONObject("delivery_address");
                            text_loc.setText(obj_loc.getString("location"));


                            JSONArray obj_array = object.getJSONArray("order_total");
                            totalpaymentArray.clear();
                            for (int i = 0; i < obj_array.length(); i++) {
                                JSONObject obj_payment = obj_array.getJSONObject(i);
                                TotalBreakdown breakdown = new TotalBreakdown();
                                breakdown.setCode(obj_payment.getString("code"));
                                breakdown.setLabel(obj_payment.getString("title"));
                                breakdown.setTotal(obj_payment.getString("value"));
                                breakdown.setCurrency(object.getString("currency_code"));
//                                breakdown.setTotal_value(obj_payment.getString("value"));
                                totalpaymentArray.add(breakdown);
                            }
                            setUpPaymentDetailUI(totalpaymentArray);

                            if (object.has("invoice_url")) {
                                //invoice_url = object.getString("invoice_url");
                                invoice_url = "https://www.mansamusa.ae/api/user/invoice-view/"+order_id;
                            }

                            text_additional_phn.setText(CommonMethods.checkNull(object.getString("additional_phone_number")));
                            text_additional_req.setText(CommonMethods.checkNull(object.getString("additional_notes")));

                            //hide and show cancel button
                            if (object.getString("allow_cancel").equalsIgnoreCase("true")) {
                                text_cancel_order.setVisibility(View.VISIBLE);
                            } else {
                                text_cancel_order.setVisibility(View.GONE);
                            }
                            //hide and show return button
                            if (isPending) {
                                tv_return_order.setVisibility(View.GONE);
                            }else {
                                if (object.getString("allow_return").equalsIgnoreCase("true")) {
                                    tv_return_order.setVisibility(View.VISIBLE);
                                } else {
                                    tv_return_order.setVisibility(View.GONE);
                                }
                            }
                            cancel_msg = object.getString("refund_text");
                            JSONArray arry_items = object.getJSONArray("order_items");
                            for (int j = 0; j < arry_items.length(); j++) {
                                JSONObject obj_item = arry_items.getJSONObject(j);
                                Cart cart = new Cart();
                                cart.setDish_name(obj_item.getString("name"));
                                cart.setDish_price(obj_item.getString("price"));
                                cart.setTotal_price(obj_item.getString("total"));
                                cart.setDish_qty(obj_item.getString("quantity"));
                                cart.setDish_id(obj_item.getString("dish_id"));

                                cart.setDish_image(obj_item.getString("dish_image"));
                                JSONObject obj_rating = obj_item.getJSONObject("dish_reviews");
                                cart.setDish_rating(CommonMethods.checkNull(obj_rating.getString("rating")));
                                cart.setDish_reviews(obj_rating.getString("review"));

                                cart.setProduct_type(object.getString("order_type"));
                                arrayMenuItem.add(cart);
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recycler_menuitemList.setLayoutManager(mLayoutManager);
                            recycler_menuitemList.setItemAnimator(new DefaultItemAnimator());
                            UserOrderMenuAdapter adapter = new UserOrderMenuAdapter(UserOrderDetailActivity.this, arrayMenuItem, isPending, order_id, isFromDriver);
                            recycler_menuitemList.setAdapter(adapter);


                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                UserOrderDetailActivity.this.finish();
                break;

            case R.id.linear_repeat:
                if (CommonMethods.checkConnection()) {
                    RepeatOrder();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_getinvoice:
                if (!invoice_url.equalsIgnoreCase("")) {
//                    CommonMethods.openInvoice(UserOrderDetailActivity.this, invoice_url);
//                    Intent intent = new Intent(UserOrderDetailActivity.this,CommonWebpage.class);
//                    intent.putExtra("invoice",invoice_url);
//                    startActivity(intent);

                    try {
                        openFile(UserOrderDetailActivity.this,invoice_url);
                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }
                break;

            case R.id.text_cancel_order:
                openCancelDialog(cancel_msg);
                break;

            case R.id.text_cancel:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;

            case R.id.linear_send:
                if (edit_msg != null) {
                    if (edit_msg.getText().toString().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.enter_msg));
                    } else {
                        if (CommonMethods.checkConnection()) {
                            CancelOrder(edit_msg.getText().toString());
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.internetconnection));
                        }
                    }
                }
                break;

            case R.id.text_track_order:
//                Intent i_track = new Intent(UserOrderDetailActivity.this, TrackOrderActivity.class);
//                startActivity(i_track);

                 Intent i_track = new Intent(UserOrderDetailActivity.this, OrderTrackingDetailActivity.class);
                 i_track.putExtra("order_status",orderStatus);
                 i_track.putExtra("estimate_time",estimate_time);
                 i_track.putExtra("order_id",order_id);
                 startActivity(i_track);



                break;

            case R.id.tv_return_order:
                Intent i_return = new Intent(UserOrderDetailActivity.this, OrderReturnActivity.class);
                i_return.putExtra("orderId",order_id);
                startActivity(i_return);
                break;


        }
    }

    private void openCancelDialog(String msg) {
        View view = getLayoutInflater().inflate(R.layout.layout_cancel_order, null);
        dialog = new BottomSheetDialog(UserOrderDetailActivity.this);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_msg = (TextView) dialog.findViewById(R.id.text_msg);
        text_msg.setText(msg);
        linear_send = (LinearLayout) dialog.findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);
        text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        edit_msg = (EditText) dialog.findViewById(R.id.edit_msg);
        dialog.show();
    }

    private void RepeatOrder() {
        final ProgressDialog progressDialog = new ProgressDialog(UserOrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.RepeatOrder(order_id);

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
                            CommonUtilFunctions.success_Alert_Dialog(UserOrderDetailActivity.this, obj.getString("success"));

                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void CancelOrder(String msg) {
        final ProgressDialog progressDialog = new ProgressDialog(UserOrderDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.CancelOrder_User(order_id, msg);

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
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            success_Alert_Dialog(UserOrderDetailActivity.this, obj.getString("success"));

                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            getOrderDetail();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(UserOrderDetailActivity.this, getResources().getString(R.string.internetconnection));
        }
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
                text_cancel_order.setVisibility(View.GONE);
                text_status.setText("Cancelled");
            }
        });

        // Showing Alert Message

        alertDialog.show();
    }

    private void setUpPaymentDetailUI(ArrayList<TotalBreakdown> totalpaymentArray) {
        linear_total_breakdown.removeAllViews();
        for (int i = 0; i < totalpaymentArray.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View itemview = inflater.inflate(R.layout.single_item_price_breakdown, null);
            TextView text_label = (TextView) itemview.findViewById(R.id.text_label);
            text_label.setText(totalpaymentArray.get(i).getLabel());
            TextView text_amount = (TextView) itemview.findViewById(R.id.text_amount);
            text_amount.setText(totalpaymentArray.get(i).getCurrency() + " " + totalpaymentArray.get(i).getTotal());
            linear_total_breakdown.addView(itemview);
        }
    }


    public static void openFile(Context context, String url) throws IOException {
        try {
// Create URI
            Uri uri = Uri.parse(url);

            Intent intent = new Intent();
// Check what kind of file you are trying to open, by comparing the url with extensions.
// When the if condition is matched, plugin sets the correct intent (mime) type,
// so Android knew what application to use to open the file
            if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
// Word document
                intent.setDataAndType(uri, "application/msword");
            } else if (url.toString().contains(".pdf")) {
// PDF file
                intent.setDataAndType(uri, "application/pdf");
            } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
// Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
            } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
// Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel");
            } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
// WAV audio file
                intent.setDataAndType(uri, "application/x-wav");
            } else if (url.toString().contains(".rtf")) {
// RTF file
                intent.setDataAndType(uri, "application/rtf");
            } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
// WAV audio file
                intent.setDataAndType(uri, "audio/x-wav");
            } else if (url.toString().contains(".gif")) {
// GIF file
                intent.setDataAndType(uri, "image/gif");
            } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
// JPG file
                intent.setDataAndType(uri, "image/jpeg");
            } else if (url.toString().contains(".txt")) {
// Text file
                intent.setDataAndType(uri, "text/plain");
            } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
// Video files
                intent.setDataAndType(uri, "video/*");
            } else {
//if you want you can also define the intent type for any other file

//additionally use else clause below, to manage other unknown extensions
//in this case, Android will show all applications installed on the device
//so you can choose which application to use
                intent.setDataAndType(uri, "*/*");
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
