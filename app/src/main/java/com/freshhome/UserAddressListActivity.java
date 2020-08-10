package com.freshhome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.freshhome.AdapterClass.DeliveryAddressAdapter;
import com.freshhome.AdapterClass.MenuAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.DeliveryAddress;
import com.freshhome.datamodel.NameID;
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

public class UserAddressListActivity extends AppCompatActivity implements View.OnClickListener {
    ListView addressList;
    LinearLayout linear_add_address, linear_select_time;
    ImageView image_back;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ArrayList<DeliveryAddress> arrayAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_address_list);

        arrayAddress = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(UserAddressListActivity.this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        linear_select_time = (LinearLayout) findViewById(R.id.linear_select_time);

        //hide timepicker for add address
        if (!getIntent().getBooleanExtra("fromCart", false)) {
            linear_select_time.setVisibility(View.GONE);
        } else {
            linear_select_time.setVisibility(View.VISIBLE);
        }

        linear_add_address = (LinearLayout) findViewById(R.id.linear_add_address);
        linear_add_address.setOnClickListener(this);
        addressList = (ListView) findViewById(R.id.addressList);

        addressList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!getIntent().getBooleanExtra("fromCart", false)) {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("address_id", arrayAddress.get(position).getAddressId());
                    resultIntent.putExtra("address", arrayAddress.get(position).getAddressLocation() + "\n" + arrayAddress.get(position).getAddressFlatno() + ", " + arrayAddress.get(position).getAddressFloorno() + ", " + arrayAddress.get(position).getAddressBuildingno() + ", " + arrayAddress.get(position).getAddressCity());
                    resultIntent.putExtra("addresstitle", arrayAddress.get(position).getAddressTitle());
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }

    private void getAddressList() {
        final ProgressDialog progressDialog = new ProgressDialog(UserAddressListActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetAddressList();
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
                            arrayAddress = new ArrayList<>();
                            JSONArray jarray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject obj = jarray.getJSONObject(i);
                                DeliveryAddress address = new DeliveryAddress();
                                address.setAddressId(obj.getString("address_id"));
                                address.setAddressTitle(obj.getString("title"));
                                address.setAddressLocation(obj.getString("location"));
                                address.setAddressBuildingno(obj.getString("building_name"));
                                address.setAddressFlatno(obj.getString("flat_no"));
                                address.setAddressFloorno(obj.getString("floor_no"));
                                address.setAddressLandmark(obj.getString("landmark"));
                                address.setAddressCity(obj.getString("city"));
                                address.setAddress_lat(obj.getString("latitude"));
                                address.setAddress_lng(obj.getString("longitude"));
                                address.setIsdefault(obj.getString("is_default"));
                                arrayAddress.add(address);
                            }
                            DeliveryAddressAdapter adapter = new DeliveryAddressAdapter(UserAddressListActivity.this, arrayAddress);
                            addressList.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserAddressListActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserAddressListActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(UserAddressListActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_add_address:
                Intent i = new Intent(UserAddressListActivity.this, AddAddressActivity.class);
                startActivity(i);
                break;

            case R.id.image_back:
                UserAddressListActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        addressList = (ListView) findViewById(R.id.addressList);
        if (CommonMethods.checkConnection()) {
            getAddressList();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(UserAddressListActivity.this, getResources().getString(R.string.internetconnection));
        }
    }
}
