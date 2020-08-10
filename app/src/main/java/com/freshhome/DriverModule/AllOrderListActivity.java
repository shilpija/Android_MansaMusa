package com.freshhome.DriverModule;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshhome.AdapterClass.AllOrdersAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.RecyclerItemClickListener;
import com.freshhome.OrderDetailActivity;
import com.freshhome.R;
import com.freshhome.datamodel.MultipleLocOrder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AllOrderListActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView orderList;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    ImageView image_back;
    String order_data = "", kitchen_lat = "", kitchen_lng = "", card_id = "", kithcen_name = "", kithcen_loc = "";
    boolean isFood = true;
    ArrayList<MultipleLocOrder> arrayOrders;
    TextView heading, text_pickup_loc;
    int ORDER_ACCEPT = 203;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_order_list);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        orderList = (RecyclerView) findViewById(R.id.orderList);
        orderList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        orderList.setLayoutManager(layoutManager);
        heading = (TextView) findViewById(R.id.heading);
        text_pickup_loc = (TextView) findViewById(R.id.text_pickup_loc);
        order_data = getIntent().getStringExtra("order_data");
        kitchen_lat = getIntent().getStringExtra("kit_lat");
        kitchen_lng = getIntent().getStringExtra("kit_lng");
        kithcen_name = getIntent().getStringExtra("kit_name");
        heading.setText(kithcen_name);
        kithcen_loc = getIntent().getStringExtra("kit_loc");
        text_pickup_loc.setText(kithcen_loc);
        isFood = getIntent().getBooleanExtra("isFood", true);
        getOrdersArray(order_data, kitchen_lat, kitchen_lng);

        orderList.addOnItemTouchListener(new RecyclerItemClickListener(AllOrderListActivity.this,
                orderList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent i = new Intent(AllOrderListActivity.this, DriverOrderDetailActivity.class);
                i.putExtra("order_id", arrayOrders.get(position).getOrder_id());
                startActivityForResult(i, ORDER_ACCEPT);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                AllOrderListActivity.this.finish();
                break;
        }
    }

    private void getOrdersArray(String order_data, String kitchen_lat, String kitchen_lng) {
        arrayOrders = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(order_data);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jobj = jsonArray.getJSONObject(i);
                MultipleLocOrder order = new MultipleLocOrder();
                order.setOrder_id(jobj.getString("order_id"));
                order.setOrder_currency(jobj.getString("currency_code"));
                order.setOrder_total(jobj.getString("total"));
                order.setOrder_paymethod(jobj.getString("payment_method"));
                order.setOrder_items(jobj.getString("total_items"));
                order.setOrder_delivery_time(CommonMethods.checkNull(jobj.getString("time")));
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
                order.setKitchen_loc(kithcen_loc);
                arrayOrders.add(order);
            }

            AllOrdersAdapter adapter = new AllOrdersAdapter(AllOrderListActivity.this, arrayOrders);
            orderList.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ORDER_ACCEPT) {
            if(data!=null)
            if (data.hasExtra("accept")) {
                Intent i = new Intent();
                i.putExtra("accept", "true");
                setResult(Activity.RESULT_OK, i);
                AllOrderListActivity.this.finish();
            }
        }
    }
}
