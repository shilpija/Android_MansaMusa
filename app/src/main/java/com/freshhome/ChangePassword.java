package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends AppCompatActivity implements View.OnClickListener {
    EditText edit_oldpassword, edit_newpass, edit_re_newpass;
    LinearLayout linear_update;
    ImageView image_back;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(ChangePassword.this);

        edit_oldpassword = (EditText) findViewById(R.id.edit_oldpassword);
        edit_newpass = (EditText) findViewById(R.id.edit_newpass);
        edit_re_newpass = (EditText) findViewById(R.id.edit_re_newpass);

        linear_update = (LinearLayout) findViewById(R.id.linear_update);
        linear_update.setOnClickListener(this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_update:
                if (edit_oldpassword.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.enteroldpass));
                }else if(edit_oldpassword.getText().toString().trim().length()<5){
                    CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.pass_length));
                } else if (edit_newpass.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.enternewpass));
                }else if(edit_newpass.getText().toString().trim().length()<5){
                    CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.pass_length));
                } else if (edit_re_newpass.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.enterrepass));
                }else if(edit_re_newpass.getText().toString().trim().length()<5){
                    CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.pass_length));
                } else if (!edit_re_newpass.getText().toString().trim().equalsIgnoreCase(edit_newpass.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.repassmatch));
                } else {
                    if (CommonMethods.checkConnection()) {
                        postdata();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;

            case R.id.image_back:
                ChangePassword.this.finish();
                break;
        }
    }

    private void postdata() {
        final ProgressDialog progressDialog = new ProgressDialog(ChangePassword.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.ChangePassword(sessionManager.getUserDetails().get("user_id"),edit_oldpassword.getText().toString().trim(),
                edit_newpass.getText().toString().trim()
        );

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
                            CommonMethods.showtoast(ChangePassword.this,obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            ChangePassword.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ChangePassword.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
