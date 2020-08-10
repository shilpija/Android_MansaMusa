package com.freshhome.DriverModule;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.freshhome.AdapterClass.DriverNotificationAdapter;
import com.freshhome.AdapterClass.RecyclerNotificationAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.NotificationActivity;
import com.freshhome.R;
import com.freshhome.datamodel.NotificationModel;
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

public class DriverNotificationActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    RecyclerView notificationRecyclerview;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ArrayList<NotificationModel> arrayNotification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_notification);
        arrayNotification = new ArrayList<>();
        sessionManager = new UserSessionManager(DriverNotificationActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        notificationRecyclerview = (RecyclerView) findViewById(R.id.notificationRecyclerview);

        if (CommonMethods.checkConnection()) {
            getNotificationdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(DriverNotificationActivity.this, getResources().getString(R.string.internetconnection));
        }
    }

    private void getNotificationdata() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverNotificationActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetDriverNotification(sessionManager.getLoginType());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayNotification = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                NotificationModel notificationModel = new NotificationModel();
                                notificationModel.setNotification_id(obj.getString("notification_id"));
                                notificationModel.setType(obj.getString("type"));
                                notificationModel.setTime(obj.getString("time"));
                                notificationModel.setDeliveryLoc(getResources().getString(R.string.name_loc_demo));
                                JSONObject jObj = obj.getJSONObject("information");
                                notificationModel.setOrder_id(jObj.getString("order_id"));
                                notificationModel.setMessage(jObj.getString("message"));
                                notificationModel.setOrderstatus(jObj.getString("order_status"));
                                arrayNotification.add(notificationModel);
                            }

                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(DriverNotificationActivity.this);
                            notificationRecyclerview.setLayoutManager(mLayoutManager);
                            notificationRecyclerview.setItemAnimator(new DefaultItemAnimator());
                            DriverNotificationAdapter adapter = new DriverNotificationAdapter(DriverNotificationActivity.this, arrayNotification);
                            notificationRecyclerview.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverNotificationActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverNotificationActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(DriverNotificationActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                DriverNotificationActivity.this.finish();
                break;
        }
    }
}
