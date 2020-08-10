package com.freshhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.freshhome.AdapterClass.MyWalletTransactionAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.Wallet;
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

public class AddBankAccount_Activity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_submit;
    ImageView image_back;
    EditText edit_firstname, edit_lastname, edit_email_address, edit_bankname, edit_account_no, edit_iban;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    boolean isAddBank = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_account);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(AddBankAccount_Activity.this);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);
        edit_firstname = (EditText) findViewById(R.id.edit_firstname);
        edit_lastname = (EditText) findViewById(R.id.edit_lastname);
        edit_email_address = (EditText) findViewById(R.id.edit_email_address);
        edit_bankname = (EditText) findViewById(R.id.edit_bankname);
        edit_account_no = (EditText) findViewById(R.id.edit_account_no);
        edit_iban = (EditText) findViewById(R.id.edit_iban);

        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                GetBankDetails();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, getResources().getString(R.string.internetconnection));
            }
        } else {
            CommonMethods.ShowLoginDialog(AddBankAccount_Activity.this);
        }
    }

    private void GetBankDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(AddBankAccount_Activity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        calls = apiInterface.GetBankDetails();
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
                            JSONObject obj = object.getJSONObject("data");
                            isAddBank = false;
                            edit_firstname.setText(obj.getString("first_name"));
                            edit_lastname.setText(obj.getString("last_name"));
                            edit_email_address.setText(obj.getString("email_address"));
                            edit_bankname.setText(obj.getString("bank_name"));
                            edit_account_no.setText(obj.getString("account_number"));
                            edit_iban.setText(obj.getString("iban"));

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            isAddBank = true;
//                            CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, getResources().getString(R.string.server_error));
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

    private void AddUpdateBankDetails(boolean isAdd) {
        final ProgressDialog progressDialog = new ProgressDialog(AddBankAccount_Activity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isAdd) {
            calls = apiInterface.AddBankDetails(edit_firstname.getText().toString().trim(),
                    edit_lastname.getText().toString().trim(),
                    edit_email_address.getText().toString().trim(),
                    edit_bankname.getText().toString().trim(),
                    edit_account_no.getText().toString().trim(),
                    edit_iban.getText().toString().trim());
        } else {
            calls = apiInterface.UpdateBankDetails(edit_firstname.getText().toString().trim(),
                    edit_lastname.getText().toString().trim(),
                    edit_email_address.getText().toString().trim(),
                    edit_bankname.getText().toString().trim(),
                    edit_account_no.getText().toString().trim(),
                    edit_iban.getText().toString().trim());
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
                            success_Alert_Dialog(AddBankAccount_Activity.this, object.getString("success"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, getResources().getString(R.string.server_error));
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                AddBankAccount_Activity.this.finish();
                break;

            case R.id.linear_submit:
                if (edit_firstname.getText().toString().trim().equalsIgnoreCase("") ||
                        edit_lastname.getText().toString().trim().equalsIgnoreCase("") ||
                        edit_email_address.getText().toString().trim().equalsIgnoreCase("") ||
                        edit_bankname.getText().toString().trim().equalsIgnoreCase("") ||
                        edit_account_no.getText().toString().trim().equalsIgnoreCase("") ||
                        edit_iban.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, getResources().getString(R.string.mandatory_fiels));
                } else if (!CommonMethods.isValidEmail(edit_email_address.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, getResources().getString(R.string.valid_email));
                } else {
                    show_confirmation_alert(AddBankAccount_Activity.this, getResources().getString(R.string.confirmation_text));
                }
                break;
        }
    }

    public void show_confirmation_alert(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage(message);
        // Setting OK Button
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                if (CommonMethods.checkConnection()) {
                    AddUpdateBankDetails(isAddBank);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(AddBankAccount_Activity.this, getResources().getString(R.string.internetconnection));
                }
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();

            }
        });

        // Showing Alert Message
        alertDialog.show();
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
                AddBankAccount_Activity.this.finish();
            }
        });

        // Showing Alert Message

        alertDialog.show();
    }

}
