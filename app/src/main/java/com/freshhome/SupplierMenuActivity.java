package com.freshhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.MenuAdapter;
import com.freshhome.AdapterClass.RecyclerMenuAdapter;
import com.freshhome.AdapterClass.SelectMenuAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.MenuSupplier;
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

public class SupplierMenuActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView recycler_supplierMenu;
    ImageView image_back;
    TextView heading;
    ApiInterface apiInterface;
    ArrayList<MenuSupplier> array_menu;
    UserSessionManager sessionManager;
    String supplier_id;
    LinearLayout linear_cart_info;
    private TextView text_total_price,text_viewcart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_menu);
        array_menu = new ArrayList<>();
        sessionManager = new UserSessionManager(SupplierMenuActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        supplier_id=getIntent().getStringExtra("supplier_id");
        image_back = (ImageView) findViewById(R.id.image_back);
        linear_cart_info = (LinearLayout) findViewById(R.id.linear_cart_info);
        text_total_price = (TextView) findViewById(R.id.text_total_price);
        text_viewcart = (TextView) findViewById(R.id.text_viewcart);
        text_viewcart.setOnClickListener(this);
        image_back.setOnClickListener(this);
        heading = (TextView) findViewById(R.id.heading);
        recycler_supplierMenu = (RecyclerView) findViewById(R.id.recycler_supplierMenu);
        if(sessionManager.getCartItem ()!=null && !sessionManager.getCartItem ().isEmpty () && !sessionManager.getCartItem ().equals ("0")){
            linear_cart_info.setVisibility (View.VISIBLE);
            text_total_price.setText (sessionManager.getCartItem ()+" Product Added");
        }else {
            linear_cart_info.setVisibility (View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                SupplierMenuActivity.this.finish();
                break;

            case R.id.text_viewcart:
                Intent i_cart = new Intent(SupplierMenuActivity.this, MainActivity_NavDrawer.class);
                i_cart.putExtra("OpenFrag", ConstantValues.OPENCART);
                startActivity(i_cart);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(SupplierMenuActivity.this, getResources().getString(R.string.internetconnection));
        }

    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(SupplierMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetSupplierMenu(supplier_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        array_menu = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jarrary = object.getJSONArray("success");

                            for (int i = 0; i < jarrary.length(); i++) {
                                JSONObject obj = jarrary.getJSONObject(i);
                                MenuSupplier menu = new MenuSupplier();
                                menu.setDname(obj.getString("dish_name"));
                                menu.setId(obj.getString("dish_id"));
                                menu.setImageurl(obj.getString("dish_image"));
                                menu.setDprice(obj.getString("dish_price"));
                                menu.setDstatus(obj.getString("status"));
                                menu.setDtime(obj.getString("dish_since"));
                                menu.setDiscount(obj.getString("discount"));
//                                menu.setDavailable(String.valueOf(i + 1));
//                                menu.setDpending(String.valueOf(i + 3));
//                                menu.setDrating(obj.getString("dish_id"));
//                                menu.setDveiws(String.valueOf(i + 15));
                                //pending
                                menu.setDavailable(obj.getString("item_available"));
                                menu.setDpending(obj.getString("user_view"));
                                menu.setDrating(obj.getString("rate_point"));
                                menu.setDveiws(obj.getString("user_view"));
                                array_menu.add(menu);

                            }
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            recycler_supplierMenu.setLayoutManager(mLayoutManager);
                            recycler_supplierMenu.setItemAnimator(new DefaultItemAnimator());
                            RecyclerMenuAdapter adapter = new RecyclerMenuAdapter(SupplierMenuActivity.this, array_menu);
                            recycler_supplierMenu.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SupplierMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SupplierMenuActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(SupplierMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
