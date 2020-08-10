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

public class ChangeEmailActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edit_oldemail, edit_newemail;
    LinearLayout linear_update;
    ImageView image_back;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_email);

        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(ChangeEmailActivity.this);

        edit_oldemail = (EditText) findViewById(R.id.edit_oldemail);
        edit_newemail = (EditText) findViewById(R.id.edit_newemail);

        linear_update = (LinearLayout) findViewById(R.id.linear_update);
        linear_update.setOnClickListener(this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_update:
                if (edit_oldemail.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, getResources().getString(R.string.enteroldemail));
                } else if (!CommonMethods.isValidEmail(edit_oldemail.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, getResources().getString(R.string.valid_email));
                } else if (edit_newemail.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, getResources().getString(R.string.enternewemail));
                } else if (!CommonMethods.isValidEmail(edit_newemail.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, getResources().getString(R.string.valid_email));
                } else {
                    if (CommonMethods.checkConnection()) {
                        postdata();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;

            case R.id.image_back:
                ChangeEmailActivity.this.finish();
                break;
        }
    }

    private void postdata() {
        final ProgressDialog progressDialog = new ProgressDialog(ChangeEmailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.ChangeEmail(edit_oldemail.getText().toString().trim(),edit_newemail.getText().toString().trim(),"email");

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
                            success_Dialog(ChangeEmailActivity.this, obj.getString("msg"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ChangeEmailActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    public void success_Dialog(final Context context, String message) {
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
                ChangeEmailActivity.this.finish();
            }
        });

        // Showing Alert Message

        alertDialog.show();
    }
}
