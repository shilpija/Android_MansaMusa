package com.freshhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MobileNumberActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_submit, linear_back;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    EditText edit_mobilenumber;
    UserSessionManager sessionManager;
    private int REQUEST_CODE_OTP = 222;
    Spinner spinner_country_code;
    ImageView img_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_number);
        sessionManager = new UserSessionManager(MobileNumberActivity.this);
        progressDialog = new ProgressDialog(MobileNumberActivity.this);
        edit_mobilenumber = (EditText) findViewById(R.id.edit_mobilenumber);

        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);

        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        img_arrow = (ImageView) findViewById(R.id.img_arrow);
        img_arrow.setOnClickListener(this);

        apiInterface = ApiClient.getInstance().getClient();
        spinner_country_code = (Spinner) findViewById(R.id.spinner_country_code);
        String[] country_code = getResources().getStringArray(R.array.country_code);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MobileNumberActivity.this, R.layout.spinner_text_transparent, country_code);
        spinner_country_code.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_submit:
                    CommonMethods.hideSoftKeyboard(MobileNumberActivity.this);
                    if (edit_mobilenumber.getText().toString().trim().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(MobileNumberActivity.this, getResources().getString(R.string.enter_phone));
                    } else if (!CommonMethods.isValidMobile(edit_mobilenumber.getText().toString().trim())) {
                        CommonUtilFunctions.Error_Alert_Dialog(MobileNumberActivity.this, getResources().getString(R.string.valid_phone));
                    } else {
                        if (CommonMethods.checkConnection()) {
                            postdata();
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(MobileNumberActivity.this, getResources().getString(R.string.internetconnection));
                        }
                    }

                break;

            case R.id.linear_back:
                MobileNumberActivity.this.finish();
                break;

            case R.id.img_arrow:
                spinner_country_code.performClick();
                break;
        }
    }

    private void postdata() {
        progressDialog = new ProgressDialog(MobileNumberActivity.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.checkPhoneNumber(spinner_country_code.getSelectedItem().toString() + edit_mobilenumber.getText().toString().trim());

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
                            JSONObject obj = object.getJSONObject("success");
//                            ..add country code
                            sessionManager.savePhoneNumber(spinner_country_code.getSelectedItem().toString() + edit_mobilenumber.getText().toString());
                            //move to next screen
                            Intent i = new Intent(MobileNumberActivity.this, OTPActivity.class);
                            //fromedit means we are calling these screens from profile screens to update the phonenumber
                            if (getIntent().hasExtra("fromEdit")) {
                                i.putExtra("fromEdit", getIntent().getBooleanExtra("fromEdit", true));
                            }
                            //we are calling these screens for login
                            i.putExtra("fromLogin", getIntent().getBooleanExtra("fromLogin", true));
                            i.putExtra("phonenumber", edit_mobilenumber.getText().toString().trim());
                            startActivityForResult(i, REQUEST_CODE_OTP);
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(MobileNumberActivity.this, obj.getString("msg"));
                        }

                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(MobileNumberActivity.this, getResources().getString(R.string.server_error));
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OTP) {
            if (data != null && data.hasExtra("edit")) {
                MobileNumberActivity.this.finish();
            }
        }
    }
}
