package com.freshhome;

import android.app.ProgressDialog;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.SalesModule.Activity_Sales_RequestDetail;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySupplierRequestDetail extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    TextView text_want_cancel, text_name, text_contactno, text_msg;
    RatingBar ratingBar_overall;
    ApiInterface apiInterface;
    BottomSheetDialog dialog;
    ArrayList<NameID> cancelReasonList;
    String[] spinner_reason_Array;
    EditText edit_msg;
    Spinner stype_spinner;
    UserSessionManager sessionManager;
    CircleImageView image_salesperson;
    String loc = "", reqdata = "", notification_id = "", request_id = "", reason_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_request_detail);

        sessionManager = new UserSessionManager(ActivitySupplierRequestDetail.this);

        cancelReasonList = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        image_salesperson = (CircleImageView) findViewById(R.id.image_salesperson);
        ratingBar_overall = (RatingBar) findViewById(R.id.ratingBar_overall);

        text_name = (TextView) findViewById(R.id.text_name);
        text_contactno = (TextView) findViewById(R.id.text_contactno);

        text_want_cancel = (TextView) findViewById(R.id.text_want_cancel);
        text_want_cancel.setOnClickListener(this);
        request_id = getIntent().getStringExtra("req_id");
        reqdata = getIntent().getStringExtra("sales_data");

        try {
            JSONObject object = new JSONObject(reqdata);
            text_name.setText(object.getString("name"));
            text_contactno.setText(object.getString("phone_number"));
            ratingBar_overall.setRating(Float.parseFloat(object.getString("rating")));

            if (object.getString("request_status").equalsIgnoreCase("In progress")) {
                text_want_cancel.setVisibility(View.GONE);
            }
//            if (!object.getString("profile_pic").equalsIgnoreCase(null)
//                    || !object.getString("profile_pic").equalsIgnoreCase("")) {
//                Picasso.get().load(object.getString("profile_pic")).placeholder(R.drawable.image).into(image_salesperson);
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (CommonMethods.checkConnection()) {
            ReasonForCancel();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.internetconnection));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                ActivitySupplierRequestDetail.this.finish();
                break;


            case R.id.text_want_cancel:
                ShowCancelRequestDialog();
                break;

            case R.id.linear_send:
                if (CommonMethods.checkConnection()) {
                    if (edit_msg != null) {
                        if (reason_id.equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.select_reason));
                        } else if (edit_msg.getText().toString().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.enter_msg));
                        } else {
                            CancelRequest(request_id, reason_id, edit_msg.getText().toString().trim());
                        }
                    }
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_cancel:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;
        }
    }

    private void ShowCancelRequestDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_cancel_request_dialog, null);
        dialog = new BottomSheetDialog(ActivitySupplierRequestDetail.this);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        edit_msg = (EditText) dialog.findViewById(R.id.edit_msg);
        LinearLayout linear_send = (LinearLayout) dialog.findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        stype_spinner = (Spinner) dialog.findViewById(R.id.stype_spinner);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(ActivitySupplierRequestDetail.this,
                R.layout.layout_spinner_text_bg_transparent, spinner_reason_Array);
        stype_spinner.setAdapter(genderAdapter);


        stype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cancelReasonList.size() != 0) {
                    for (int i = 0; i < cancelReasonList.size(); i++) {
                        if (cancelReasonList.get(i).getName().equalsIgnoreCase(stype_spinner.getSelectedItem().toString())) {
                            reason_id = cancelReasonList.get(position).getId();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        dialog.show();
    }

    //TODO API HITs
    private void ReasonForCancel() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivitySupplierRequestDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetCancelReason();
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
                            cancelReasonList = new ArrayList<>();
                            JSONArray jsonArray = object.getJSONArray("data");
                            spinner_reason_Array = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(jsonObject.getString("reason_id"));
                                nameID.setName(jsonObject.getString("name"));
                                cancelReasonList.add(nameID);
                                spinner_reason_Array[i] = jsonObject.getString("name");
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //TODO API HITs
    private void CancelRequest(final String request_id, String reasonID, String msg) {
        final ProgressDialog progressDialog = new ProgressDialog(ActivitySupplierRequestDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.CancelRequestBySupplier(request_id, reasonID, msg, sessionManager.getFCMToken());
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
                            text_want_cancel.setVisibility(View.GONE);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            ActivitySupplierRequestDetail.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ActivitySupplierRequestDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
