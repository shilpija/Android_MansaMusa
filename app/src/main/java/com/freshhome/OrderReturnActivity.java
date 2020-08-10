package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderReturnActivity extends AppCompatActivity {
    ApiInterface apiInterface;
    @BindView(R.id.tvReason)
    TextView tvReason;
    @BindView(R.id.spnReason)
    Spinner spnReason;
    @BindView(R.id.edtDetails)
    EditText edtDetails;


    private String orderId, postId;

    private String[] category;

    Spinner.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            if (position == 0) {
                return;
            }

            tvReason.setText(category[position]);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_return);
        ButterKnife.bind(this);
        apiInterface = ApiClient.getInstance().getClient();

        if(getIntent() != null){
            orderId = (String)getIntent().getStringExtra("orderId");
        }

        category = new String[]{
                "Select Reason",
                "Accidental Order",
                "Quality not adequate",
                "The item is damaged but the box or envelope is undamaged",
                "Missing parts or accessories",
                "Item defective or doesn't work",
                "Other"
        };


        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, category){

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setBackgroundColor(getResources().getColor(R.color.white));
                TextView tv = (TextView) view;
                tv.setTextSize(14.0f);
                if (position == 0) {
                    tv.setTextColor(getResources().getColor(R.color.greytxt));
                } else {
                    tv.setTextColor(getResources().getColor(android.R.color.black));
                }
                return view;
            }

        };
        spnReason.setAdapter(adapter);
        spnReason.setOnItemSelectedListener(onItemSelectedListener);

    }


    @OnClick({R.id.btnSubmit, R.id.iv_back, R.id.tvReason})
    void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                if(!tvReason.getText().toString().equalsIgnoreCase("")) {
                    if(!edtDetails.getText().toString().equalsIgnoreCase("")){
                        callRetrunOrderApi();
                    }else {
                        Toast.makeText(this, "Please give the description of retuen product", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(this, "Select Reason", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tvReason:
                spnReason.performClick();
                break;
        }
    }


    private void callRetrunOrderApi() {
        final ProgressDialog progressDialog = new ProgressDialog(OrderReturnActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.ReturnOrder(orderId,tvReason.getText().toString(),edtDetails.getText().toString());

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
                           // CommonUtilFunctions.success_Alert_Dialog(OrderReturnActivity.this, obj.getString("success"));

                            finish();
                        } else {
                            JSONObject object = obj.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(OrderReturnActivity.this, object.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OrderReturnActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(OrderReturnActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

}
