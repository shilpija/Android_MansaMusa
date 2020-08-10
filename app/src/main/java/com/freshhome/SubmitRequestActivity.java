package com.freshhome;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.SubCategory;
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

public class SubmitRequestActivity extends AppCompatActivity implements View.OnClickListener {
    UserSessionManager sessionManager;
    EditText edtAmount,edtFullName,edtMobileNo;
    TextView tvSubmit,tvBankTransaction,tvCompanyExchange,heading;
    LinearLayout linearLayout2;
    ImageView image_back;
    String type = "";
    String totalAmount = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_request);
        sessionManager = new UserSessionManager(this);
        heading = findViewById(R.id.heading);
        linearLayout2 = findViewById(R.id.linearLayout2);
        edtAmount = findViewById(R.id.edtAmount);
        edtFullName = findViewById(R.id.edtFullName);
        edtMobileNo = findViewById(R.id.edtMobileNo);
        tvSubmit = findViewById(R.id.tvSubmit);
        tvBankTransaction = findViewById(R.id.tvBankTransaction);
        tvCompanyExchange = findViewById(R.id.tvCompanyExchange);
        image_back = findViewById(R.id.image_back);
        tvSubmit.setOnClickListener(this);
        tvBankTransaction.setOnClickListener(this);
        tvCompanyExchange.setOnClickListener(this);
        image_back.setOnClickListener(this);

        if(getIntent() != null){
            totalAmount = (String)getIntent().getStringExtra("WalletAmount");
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tvSubmit:
                double withdrawAmt=0.0;
                double walletAmt = Double.parseDouble(totalAmount);
                if (!edtAmount.getText().toString().equalsIgnoreCase("")){
                     withdrawAmt = Double.parseDouble(edtAmount.getText().toString());
                    if(type.equalsIgnoreCase("2")) {
                        if (withdrawAmt <= walletAmt) {
                            if (!edtFullName.getText().toString().equalsIgnoreCase("")) {
                                getMyWithdraw();
                            } else {
                                Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Please enter the less amount of the available wallet amount", Toast.LENGTH_SHORT).show();
                        }

                    }else {
                        if (withdrawAmt <= walletAmt) {
                            getMyWithdraw();
                        }else {
                            Toast.makeText(this, "Please enter the less amount of the available wallet amount", Toast.LENGTH_SHORT).show();
                        }
                    }

                }else {
                    Toast.makeText(this, "Please enter the amount", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.tvBankTransaction:
                showBankTransactionScreen();
                break;

            case R.id.tvCompanyExchange:
                showCompanyExchangeScreen();
                break;

            case R.id.image_back:
                onBackPressed();
                break;
        }
    }

    private void showBankTransactionScreen() {
        type = "1";
        heading.setText("Submit Request");
        linearLayout2.setVisibility(View.VISIBLE);
        edtAmount.setVisibility(View.VISIBLE);
        edtFullName.setVisibility(View.GONE);
        edtMobileNo.setVisibility(View.GONE);
        tvBankTransaction.setVisibility(View.GONE);
        tvCompanyExchange.setVisibility(View.GONE);
        tvSubmit.setVisibility(View.VISIBLE);

    }

    private void showCompanyExchangeScreen() {
        type = "2";
        heading.setText("Submit Request");
        linearLayout2.setVisibility(View.VISIBLE);
        edtAmount.setVisibility(View.VISIBLE);
        edtFullName.setVisibility(View.VISIBLE);
        edtMobileNo.setVisibility(View.VISIBLE);
        tvBankTransaction.setVisibility(View.GONE);
        tvCompanyExchange.setVisibility(View.GONE);
        tvSubmit.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        if(type.equalsIgnoreCase("1") || type.equalsIgnoreCase("2")){
            heading.setText("Withdraw Type");
            linearLayout2.setVisibility(View.INVISIBLE);
            edtAmount.setVisibility(View.INVISIBLE);
            edtFullName.setVisibility(View.GONE);
            edtMobileNo.setVisibility(View.GONE);
            tvBankTransaction.setVisibility(View.VISIBLE);
            tvCompanyExchange.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.GONE);
            type = "";
        }else {
            finish();
        }
    }

    //------------------------MY WITHDRAW------------------------
    private void getMyWithdraw() {
        final ProgressDialog progressDialog = new ProgressDialog(SubmitRequestActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        ApiInterface apiInterface = ApiClient.getInstance().getClient();
        Call<JsonElement> calls;
        if(type.equalsIgnoreCase("2")){
            calls = apiInterface.myWithdraw(type,edtAmount.getText().toString(),edtFullName.getText().toString(),edtMobileNo.getText().toString(),sessionManager.getLoginType());
        }else {
            calls = apiInterface.myWithdraw(type,edtAmount.getText().toString(),"","",sessionManager.getLoginType());
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

                          success_Alert_Dialog(SubmitRequestActivity.this, object.getString("success"));

                        }


                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SubmitRequestActivity.this, getResources().getString(R.string.server_error));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(SubmitRequestActivity.this, getResources().getString(R.string.server_error));
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
                 finish();
            }
        });

        // Showing Alert Message

        if(!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }
}
