package com.freshhome.DriverModule;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.UserOrderMenuAdapter;
import com.freshhome.ChooseCard;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.PlanActivity;
import com.freshhome.R;
import com.freshhome.UserOrderDetailActivity;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.MultipleLocOrder;
import com.freshhome.datamodel.Orders;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllOrdersMapActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    ImageView image_back;
    BottomSheetDialog bottomdialog;
    private GoogleMap mMap;
    String order_data = "", kitchen_lat = "", kitchen_lng = "", card_id = "", kithcen_name = "";
    boolean isFood = true;
    ArrayList<MultipleLocOrder> arrayOrders;
    ArrayList<Marker> arrayMarkers;
    ApiInterface apiInterface;
    private int CARD_CODE = 123;
    TextView text_cardno;
    ImageView img_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_orders_map);
        apiInterface = ApiClient.getInstance().getClient();
        arrayOrders = new ArrayList<>();
        arrayMarkers = new ArrayList<>();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        order_data = getIntent().getStringExtra("order_data");
        kitchen_lat = getIntent().getStringExtra("kit_lat");
        kitchen_lng = getIntent().getStringExtra("kit_lng");
        kithcen_name = getIntent().getStringExtra("kit_name");
        isFood = getIntent().getBooleanExtra("isFood", true);
        getOrdersArray(order_data, kitchen_lat, kitchen_lng);
    }

    private void getOrdersArray(String order_data, String kitchen_lat, String kitchen_lng) {
        arrayOrders = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(order_data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jobj = jsonArray.getJSONObject(i);
                ;
                MultipleLocOrder order = new MultipleLocOrder();
                order.setOrder_id(jobj.getString("order_id"));
                order.setOrder_currency(jobj.getString("currency_code"));
                order.setOrder_total(jobj.getString("total"));
                order.setOrder_paymethod(jobj.getString("payment_method"));
                order.setOrder_items(jobj.getString("total_items"));
                order.setOrder_delivery_time(jobj.getString("time"));

                JSONObject uobj = jobj.getJSONObject("user_info");
                order.setUser_phonenumber(uobj.getString("phone_number"));
                order.setUser_name(uobj.getString("name"));
                JSONObject locobj = uobj.getJSONObject("delivery_address");
                order.setOrder_delivery_address(locobj.getString("location") + ", " + locobj.getString("flat_no") + ", " +
                        locobj.getString("floor_no") + ", " + locobj.getString("building_name") + ", near " + locobj.getString("landmark") + ", " + locobj.getString("city_name"));
                order.setDestination_lat(locobj.getString("longitude"));
                order.setDestination_lng(locobj.getString("latitude"));
                order.setStarting_lng(kitchen_lng);
                order.setStarting_lat(kitchen_lat);
                arrayOrders.add(order);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setMarkersonMapIntial(ArrayList<MultipleLocOrder> arrayOrders) {
        arrayMarkers = new ArrayList<>();
        mMap.clear();
        for (int i = 0; i < arrayOrders.size(); i++) {
            LatLng loc = new LatLng(Double.valueOf(arrayOrders.get(i).getDestination_lat()),
                    Double.valueOf(arrayOrders.get(i).getDestination_lng()));
            MarkerOptions marker = null;
            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.username);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
            marker = new MarkerOptions().position(loc).anchor(0.5f, 0.5f)
                    .title("Order No: " + arrayOrders.get(i).getOrder_id()).
                            icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

            Marker marker1 = mMap.addMarker(marker);
            arrayMarkers.add(marker1);
            arrayOrders.get(i).setMarker(marker1);
        }
        LatLng loc = new LatLng(Double.valueOf(kitchen_lat), Double.valueOf(kitchen_lng));
        MarkerOptions marker = null;
        BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.kitchen);
        Bitmap b = bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, 50, 50, false);
        marker = new MarkerOptions().position(loc).anchor(0.5f, 0.5f)
                .title(kithcen_name).
                        icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        Marker marker1 = mMap.addMarker(marker);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                AllOrdersMapActivity.this.finish();
                break;

            case R.id.text_cancel:
                if (bottomdialog.isShowing()) {
                    bottomdialog.dismiss();
                }
                break;

            case R.id.text_cardno:
                Intent i = new Intent(AllOrdersMapActivity.this, ChooseCard.class);
                startActivityForResult(i, CARD_CODE);
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(AllOrdersMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(AllOrdersMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        googleMap.setOnMarkerClickListener(this);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        setMarkersonMapIntial(arrayOrders);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
//        if (isFood) {
            for (int i = 0; i < arrayOrders.size(); i++) {
                if (arrayOrders.get(i).getMarker() != null) {
                    if (arrayOrders.get(i).getMarker().getTitle().equals(marker.getTitle())) {
                        //if isfoodselected then make the path and show dialog
                        showKitchenDetails_Dialog(arrayOrders.get(i).getUser_name(), arrayOrders.get(i).getUser_phonenumber(),
                                arrayOrders.get(i).getOrder_delivery_address(),
                                arrayOrders.get(i).getDestination_lat(),
                                arrayOrders.get(i).getDestination_lng(), arrayOrders.get(i).getOrder_id());
                    }
                }
            }
       // }
        return false;
    }

    private void showKitchenDetails_Dialog(String name, final String phone_number, String
            loc, final String destination_lat, final String destination_lng, final String order_id) {

        View view = getLayoutInflater().inflate(R.layout.layout_order_detail_dialog, null);
        bottomdialog = new BottomSheetDialog(AllOrdersMapActivity.this);
        bottomdialog.setContentView(view);
        bottomdialog.setCanceledOnTouchOutside(false);
        TextView text_cancel = (TextView) bottomdialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        ImageView img_type = (ImageView) bottomdialog.findViewById(R.id.img_type);
        img_type.setVisibility(View.INVISIBLE);
//        if (supplier_type.equalsIgnoreCase(ConstantValues.home_food)) {
//            img_type.setImageDrawable(getResources().getDrawable(R.drawable.home_food));
//        } else {
//            img_type.setImageDrawable(getResources().getDrawable(R.drawable.home_product));
//        }
        TextView text_name = (TextView) bottomdialog.findViewById(R.id.text_name);
        text_name.setText(name);
        TextView text_loc = (TextView) bottomdialog.findViewById(R.id.text_loc);
        text_loc.setText(loc);
        TextView text_phone = (TextView) bottomdialog.findViewById(R.id.text_phone);
        text_phone.setText(phone_number);
        text_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtilFunctions.call(AllOrdersMapActivity.this, phone_number);
            }
        });

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
                    CommonUtilFunctions.Error_Alert_Dialog(AllOrdersMapActivity.this, getResources().getString(R.string.select_card));
                } else {
                    if (CommonMethods.checkConnection()) {
                        accept_order(order_id, card_id);
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AllOrdersMapActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
            }
        });


        //order detail
        TextView text_view_order_detail = (TextView) bottomdialog.findViewById(R.id.text_view_order_detail);
        text_view_order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AllOrdersMapActivity.this, UserOrderDetailActivity.class);
                i.putExtra("ispending", true);
                i.putExtra("order_id", order_id);
                i.putExtra("fromDriver", true);
                startActivity(i);
            }
        });

        bottomdialog.show();
    }

    private void accept_order(String order_id, String card_id) {
        final ProgressDialog progressDialog = new ProgressDialog(AllOrdersMapActivity.this);
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
                            success_Alert_Dialog(AllOrdersMapActivity.this, object.getString("msg"));
                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AllOrdersMapActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AllOrdersMapActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AllOrdersMapActivity.this, getResources().getString(R.string.server_error));
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

    private void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Success!");
        alertDialog.setCancelable(false);
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                AllOrdersMapActivity.this.finish();

            }
        });
        alertDialog.show();
    }
}
