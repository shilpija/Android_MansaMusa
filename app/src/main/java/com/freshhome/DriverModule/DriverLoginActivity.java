package com.freshhome.DriverModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.MobileNumberActivity;
import com.freshhome.R;
import com.freshhome.SelectCookEatActivity;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverLoginActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spinner_company_name;
    EditText edit_phone;
    LinearLayout linear_submit, linear_back;
    ArrayList<NameID> arrayCompanyData;
    ArrayList<String> arrayCompanyName;
    ApiInterface apiInterface;
    String device_token = "";
    UserSessionManager sessionManager;
    Spinner spinner_country_code;
    ImageView img_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_login);
        sessionManager = new UserSessionManager(DriverLoginActivity.this);
        initiate_fcm();
        apiInterface = ApiClient.getInstance().getClient();
        arrayCompanyName = new ArrayList<>();
        arrayCompanyData = new ArrayList<>();
        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);

        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        edit_phone = (EditText) findViewById(R.id.edit_phone);

        spinner_company_name = (Spinner) findViewById(R.id.spinner_company_name);

        img_arrow = (ImageView) findViewById(R.id.img_arrow);
        img_arrow.setOnClickListener(this);
        spinner_country_code = (Spinner) findViewById(R.id.spinner_country_code);
        String[] country_code = getResources().getStringArray(R.array.country_code);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(DriverLoginActivity.this, R.layout.spinner_text_transparent, country_code);
        spinner_country_code.setAdapter(adapter);

        if (CommonMethods.checkConnection()) {
            GetDeliveryCompanies();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, getResources().getString(R.string.internetconnection));
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_submit:
//                if (spinner_company_name.getSelectedItemPosition() == 0) {
//                    CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, getResources().getString(R.string.select_company_name));
//                } else
                if (edit_phone.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, getResources().getString(R.string.enter_company_phone));
                } else if (!CommonMethods.isValidMobile(edit_phone.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, getResources().getString(R.string.valid_phone));
                } else {
                    if (CommonMethods.checkConnection()) {
                        CheckDriverPhoneNumber();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, getResources().getString(R.string.internetconnection));
                    }

                }
                break;

            case R.id.linear_back:
                SelectCookEatActivity();
                break;

            case R.id.img_arrow:
                spinner_country_code.performClick();
                break;
        }
    }

    private void SelectCookEatActivity() {
        Intent i = new Intent(DriverLoginActivity.this, SelectCookEatActivity.class);
        startActivity(i);
        ActivityCompat.finishAffinity(DriverLoginActivity.this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SelectCookEatActivity();
    }

    private void initiate_fcm() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.e(ConstantValues.TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        device_token = task.getResult().getToken();
                    }
                });
    }


    // TODO: API parsing---------------------------------------------------
    private void GetDeliveryCompanies() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverLoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetDeliveryCompanies();

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
                            arrayCompanyName = new ArrayList<>();
                            arrayCompanyData = new ArrayList<>();
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setName(obj.getString("company_name"));
                                nameID.setId(obj.getString("company_id"));
                                arrayCompanyData.add(nameID);
                                arrayCompanyName.add(obj.getString("company_name"));
                            }
                            spinner_company_name.setAdapter(new ArrayAdapter<>(DriverLoginActivity.this, R.layout.layout_spinner_text_bg_transparent, arrayCompanyName));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, getResources().getString(R.string.server_error));
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
            }
        });
    }

    private void CheckDriverPhoneNumber() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverLoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.CheckDrvierNumber(arrayCompanyData.get(spinner_company_name.getSelectedItemPosition()).getId(),
                edit_phone.getText().toString().trim(), device_token);
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
                            sessionManager.savePhoneNumber(spinner_country_code.getSelectedItem().toString().trim() + edit_phone.getText().toString().trim());
                            Intent i = new Intent(DriverLoginActivity.this, DriverOTPVerification.class);
                            i.putExtra("dataArray", object.getString("data"));
                            startActivity(i);
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverLoginActivity.this, getResources().getString(R.string.server_error));
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
            }
        });
    }
}
