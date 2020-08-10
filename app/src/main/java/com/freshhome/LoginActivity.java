package com.freshhome;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.DriverModule.DriverNavDrawerActivity;
import com.freshhome.DriverModule.IndividualDriverRegisterActivity;
import com.freshhome.SalesModule.ActivitySalesNavDrawer;
import com.freshhome.SalesModule.ActivitySalesRegister;
import com.freshhome.datamodel.SearchModel;
import com.freshhome.fragments.UserCartFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_login, linear_back, linear_google;
    TextView text_signup, text_forgotpassword, text_skip;
    EditText edit_email, edit_password;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    String temp_password = "", device_token = "";
    GoogleSignInClient mGoogleSignInClient;
    private int RC_SIGN_IN = 121;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initiate_fcm();
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(LoginActivity.this);

        linear_login = (LinearLayout) findViewById(R.id.linear_login);
        linear_login.setOnClickListener(this);

        linear_google = (LinearLayout) findViewById(R.id.linear_google);
        linear_google.setOnClickListener(this);


        edit_email = (EditText) findViewById(R.id.edit_email);
        edit_password = (EditText) findViewById(R.id.edit_password);

        text_signup = (TextView) findViewById(R.id.text_signup);
        text_signup.setOnClickListener(this);

        text_forgotpassword = (TextView) findViewById(R.id.text_forgotpassword);
        text_forgotpassword.setOnClickListener(this);

        text_skip = (TextView) findViewById(R.id.text_skip);
        text_skip.setOnClickListener(this);

        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);


        //TODOshow google login button in case of Eat/buy
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            linear_google.setVisibility(View.GONE);
        } else {
            linear_google.setVisibility(View.GONE);
        }

        //TODO hide skip in case of sales login
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Sales) || sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Driver)
        || sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            text_skip.setVisibility(View.GONE);
        }

        // TODO initate google login in case of User/toeat/tobuy
        setUpGoogleLogin();
    }

    private void setUpGoogleLogin() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().requestId()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //check if already logged in app // yes then signout
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            mGoogleSignInClient.signOut();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_forgotpassword:
                Intent i_password = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(i_password);
                break;

            case R.id.text_signup:
                Intent i = new Intent(LoginActivity.this, MobileNumberActivity.class);
                //Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                i.putExtra("fromLogin", true);
                startActivity(i);

                break;

            case R.id.linear_back:
                SelectCookEatActivity();
                break;

            case R.id.linear_login:
                CommonMethods.hideSoftKeyboard(LoginActivity.this);
                if (edit_email.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.enter_email));
                } else if (edit_email.getText().toString().contains("@") && !CommonMethods.isValidEmail(edit_email.getText().toString().trim())) {
//                } else if (!CommonMethods.isValidEmail(edit_email.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.valid_email));
                } else if (edit_password.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.enter_password));
                } else {
                    if (CommonMethods.checkConnection()) {
                        postdata(edit_email.getText().toString(), "", "", false);
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;


            case R.id.text_skip:
                getGuestUser();
                movetoNavDrawer(false,"");
                break;

            case R.id.linear_google:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;

        }
    }

//    private void postdata() {
//        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        progressDialog.show();
//        Call<JsonElement> calls = apiInterface.userlogin(edit_email.getText().toString().trim(),
//                edit_password.getText().toString().trim()
//        );
//
//        calls.enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                try {
//                    if (response.code() == 200) {
//                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
//                        if (object.getString("code").equalsIgnoreCase("200")) {
//                            JSONObject obj = object.getJSONObject("success");
//                            sessionManager.createLoginSession(obj.getString("id"),
//                                    obj.getString("name"),
//                                    obj.getString("share_id"),
//                                    edit_email.getText().toString().trim(),
//                                    obj.getString("token"),
//                                    sessionManager.getLoginType(), obj.getString("phone_number"));
//                            movetoNavDrawer(true);
//                        } else {
//                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, obj.getString("msg"));
//                        }
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.server_error));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                call.cancel();
//            }
//        });
//    }

    private void movetoNavDrawer(boolean islogin,String from) {
        Intent i_nav;
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Sales)) {
            i_nav = new Intent(LoginActivity.this, ActivitySalesNavDrawer.class);
        } else if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Driver)) {
            i_nav = new Intent(LoginActivity.this, DriverNavDrawerActivity.class);
        } else {
            i_nav = new Intent(LoginActivity.this, MainActivity_NavDrawer.class);
            i_nav.putExtra("From",from);
            i_nav.putExtra("deepLink","ravi");
        }
        startActivity(i_nav);
        if (islogin) {
            LoginActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SelectCookEatActivity();
    }

    private void SelectCookEatActivity() {
//        Intent i = new Intent(LoginActivity.this, SelectCookEatActivity.class);
//        startActivity(i);
//        ActivityCompat.finishAffinity(LoginActivity.this);

        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(ConstantValues.TAG, "signInResult:failed code=" + e.getStatusCode());
            CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, e.getMessage());
        }

    }

    private void updateUI(GoogleSignInAccount account) {
        //check email //name
        if (CommonMethods.checkConnection()) {
//            Log.e(ConstantValues.TAG, account.getId() + "-------" + account.getIdToken() + "----------------" + account.getServerAuthCode());
            postdata(account.getEmail(), account.getDisplayName(), account.getId(), true);
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.internetconnection));
        }
    }

    private void postdata(String email, String name, String source_id, boolean isgmail) {
//        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
//            sessionManager.saveLoginType("1");
//        }
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;

        if (isgmail) {
            calls = apiInterface.userGmaillogin(email, name, source_id, device_token);
        } else {
            calls = apiInterface.userlogin(edit_email.getText().toString().trim(),
                    edit_password.getText().toString().trim(), sessionManager.getLoginType(),sessionManager.getGuestUserDetails().get("phone"), device_token);
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
                            JSONObject obj = object.getJSONObject("success");
                            if (obj.has("temp_password")) {
                                temp_password = obj.getString("temp_password");
                            }
                            sessionManager.saveFCMToken(device_token);
                            if(!sessionManager.getLoginType().equalsIgnoreCase("2")){
                                sessionManager.save_cart_item (obj.getString ("cart_items"));
                            }

//                            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Driver)) {
//                                sessionManager.createDriverSession(obj.getString("id"),
//                                        obj.getString("availability"),
//                                        "",
//                                        obj.getString("username"),
//                                        obj.getString("nationality"),
//                                        obj.getString("token"),
//                                        obj.getString("scan_code"),
//                                        obj.getString("is_driver"),
//                                        obj.getString("profile_pic"),
//                                        obj.getString("is_user"),
//                                        obj.getString("is_supplier"),
//                                        obj.getString("is_sale_person"));
//                            } else {
                            boolean isdriver = false, isSales = false;
                            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Driver)) {
                                isdriver = true;
                            } else if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Sales)) {
                                isSales = true;
                            }

                            String is_driver = "no";
                            if (obj.has("is_driver")) {
                                is_driver = obj.getString("is_driver");
                            }
                            sessionManager.createLoginSession(obj.getString("id"),
                                    obj.getString("username"),
                                    obj.getString("share_id"),
                                    obj.getString("email"),
                                    obj.getString("token"),
                                    sessionManager.getLoginType().toString(),
                                    CommonMethods.checkNull(obj.getString("phone_number")),
                                    temp_password,
                                    obj.getString("is_supplier"),
                                    obj.getString("is_user"),
                                    obj.getString("scan_code"),
                                    obj.getString("is_sale_person"),
                                    obj.getString("supplier_type"),
                                    obj.getString("is_watched"),
                                    obj.getString("header_image"),
                                    is_driver,
                                    obj.getString("profile_pic"),
                                    "", "",
                                    obj.getString("name"),
                                    isdriver, isSales, is_driver);
//                            }

                            if (obj.has("subscription_data")) {
                                JSONObject subjson = obj.getJSONObject("subscription_data");
                                sessionManager.saveSubscriptionDetails(
                                        subjson.getString("status"),
                                        subjson.getString("subscription_status"),
                                        subjson.getString("subscription_start_format"),
                                        subjson.getString("subscription_end_format"));
                            }

                            //tractions charges
                            sessionManager.saveCharges("5",
                                    "10",
                                    "3");

                            if (temp_password.equalsIgnoreCase("1")) {
                                //randon generated password
                                success_Alert_Dialog("Your password is randomly generated by our system," +
                                        "kindly check your email and update the password under Edit Profile tab.");
                            } else {
                                if (getIntent() != null && getIntent().hasExtra("FROMCART") && getIntent().getStringExtra("FROMCART").equals("fromCart")) {
                                    movetoNavDrawer(true,"FROMCART");
                                }else {
                                    movetoNavDrawer(true,"");
                                }

                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.server_error));
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

    private void success_Alert_Dialog(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                LoginActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

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
                movetoNavDrawer(true,"");
            }
        });

        // Showing Alert Message
        alertDialog.show();
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


    private void getGuestUser() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.guestregister(sessionManager.getUserGuestType());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        //array_Sub_categories = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                                JSONObject obj = object.getJSONObject("data");


                            sessionManager.createGuestLoginSession(obj.getString("auth_key"),
                                    CommonMethods.checkNull(obj.getString("phone_number")));

                            if(!sessionManager.getUserDetails().get("token").equalsIgnoreCase("") && sessionManager.getUserDetails().get("token") != null){
                                AllAPIs.token = sessionManager.getUserDetails().get("token");
                            }else {
                                AllAPIs.token = sessionManager.getGuestUserDetails().get("token");
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(LoginActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
