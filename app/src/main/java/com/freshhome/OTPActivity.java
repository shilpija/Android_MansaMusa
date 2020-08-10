package com.freshhome;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.DriverModule.IndividualDriverRegisterActivity;
import com.freshhome.SalesModule.ActivitySalesRegister;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTPActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_submit, linear_back, linear_button_resend;
    EditText edit_six, edit_five, edit_four, edit_three, edit_two, edit_one;
    TextView timer;
    UserSessionManager sessionManager;
    int time = 0, total_time = 60;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    private String mVerificationId = "", phonenumber = "";
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    boolean fromLogin = true, fromEdit = false;
    ApiInterface apiInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(OTPActivity.this);
        fromLogin = getIntent().getBooleanExtra("fromLogin", true);
        if (getIntent().hasExtra("fromEdit")) {
            fromEdit = getIntent().getBooleanExtra("fromEdit", true);
        }
        if (getIntent().hasExtra("phonenumber")) {
            phonenumber = getIntent().getStringExtra("phonenumber");
        }
        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);
        linear_submit.setAlpha((float) .4);
        linear_submit.setClickable(false);
        linear_submit.setEnabled(false);
        linear_submit.setVisibility(View.VISIBLE);

        linear_button_resend = (LinearLayout) findViewById(R.id.linear_button_resend);
        linear_button_resend.setVisibility(View.GONE);
        linear_button_resend.setOnClickListener(this);


        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        timer = (TextView) findViewById(R.id.timer);
        timer.setText("01:00");

        edit_one = (EditText) findViewById(R.id.edit_one);
        edit_one.setSelection(edit_one.getText().length());
        edit_two = (EditText) findViewById(R.id.edit_two);
//        edit_two.setSelection(edit_two.getText().length());
        edit_three = (EditText) findViewById(R.id.edit_three);
        edit_three.setSelection(edit_three.getText().length());
        edit_four = (EditText) findViewById(R.id.edit_four);
        edit_four.setSelection(edit_four.getText().length());
        edit_five = (EditText) findViewById(R.id.edit_five);
        edit_five.setSelection(edit_five.getText().length());
        edit_six = (EditText) findViewById(R.id.edit_six);
        edit_six.setSelection(edit_six.getText().length());

        edit_one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edit_two.requestFocus();
                    edit_two.setSelection(edit_two.getText().length());
                }
                check_all_fields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_one.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                edit_two.requestFocus();
                edit_two.setSelection(edit_two.getText().length());
                check_all_fields();
                return false;
            }
        });

        edit_two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edit_three.requestFocus();
                    edit_three.setSelection(edit_three.getText().length());
                }
                check_all_fields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_two.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                edit_three.requestFocus();
                edit_three.setSelection(edit_three.getText().length());
                check_all_fields();
                return false;
            }
        });
        edit_three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edit_four.requestFocus();
                    edit_four.setSelection(edit_four.getText().length());
                }
                check_all_fields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_three.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                edit_four.requestFocus();
                edit_four.setSelection(edit_four.getText().length());
                check_all_fields();
                return false;
            }
        });
        edit_four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edit_five.requestFocus();
                    edit_five.setSelection(edit_five.getText().length());
                }
                check_all_fields();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_four.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                edit_five.requestFocus();
                edit_five.setSelection(edit_five.getText().length());
                check_all_fields();
                return false;
            }
        });
        edit_five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edit_six.requestFocus();
                }
                check_all_fields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_five.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                edit_six.requestFocus();
                edit_six.setSelection(edit_six.getText().length());
                check_all_fields();
                return false;
            }
        });
        edit_six.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    linear_submit.setAlpha((float) 1);
                    linear_submit.setClickable(true);
                    linear_submit.setEnabled(true);
                }
                check_all_fields();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edit_six.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                edit_six.requestFocus();
                check_all_fields();
                return false;
            }
        });

        progressDialog = new ProgressDialog(OTPActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        //start timer
        timer_for_otp();
        phoneauth();
    }

    private void phoneauth() {
        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
                //set otp here
//                edit_OTP.setText(phoneAuthCredential.getSmsCode());
                String otp = phoneAuthCredential.getSmsCode();
                if (otp != null) {
                    Toast.makeText(OTPActivity.this, otp, Toast.LENGTH_SHORT).show();
                    if (otp.length() == 6) {
                        edit_one.setText(Character.toString(otp.charAt(0)));
                        edit_two.setText(Character.toString(otp.charAt(1)));
                        edit_three.setText(Character.toString(otp.charAt(2)));
                        edit_four.setText(Character.toString(otp.charAt(3)));
                        edit_five.setText(Character.toString(otp.charAt(4)));
                        edit_six.setText(Character.toString(otp.charAt(5)));
                    }
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(ConstantValues.TAG, "onVerificationFailed", e);
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                CommonUtilFunctions.Error_Alert_Dialog(OTPActivity.this, getResources().getString(R.string.verification_failed));
//                Toast.makeText(OTPActivity.this, "Verification Failed! Please try again later", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(ConstantValues.TAG, "onCodeSent:" + verificationId);
                //show otp field
                mResendToken = forceResendingToken;
                mVerificationId = verificationId;
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }
        };

        send_otpcode();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(ConstantValues.TAG, "signInWithCredential:success");
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            final FirebaseUser user = task.getResult().getUser();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    updateUI();
                                }
                            }, 300);


                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(ConstantValues.TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                CommonUtilFunctions.Error_Alert_Dialog(OTPActivity.this, getResources().getString(R.string.invalid_code));
//                                Toast.makeText(OTPActivity.this, "Invalid code.", Toast.LENGTH_SHORT).show();
                                // [END_EXCLUDE]
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
//                                updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }

    private void updateUI() {
        //move to next screen and safe value in pref
        Intent i;
        if (fromEdit) {
            //means intent is coming from edit profile screens so hit api and update the phonenumber and close screens
            CommonMethods.hideSoftKeyboard(OTPActivity.this);
            if (CommonMethods.checkConnection()) {
                UpdatePhoneNumber();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(OTPActivity.this, getResources().getString(R.string.internetconnection));
            }
        } else {
            if (fromLogin) {
                if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Sales)) {
                    i = new Intent(OTPActivity.this, ActivitySalesRegister.class);
                } else if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Driver)) {
                    i = new Intent(OTPActivity.this, IndividualDriverRegisterActivity.class);
                } else {
                    i = new Intent(OTPActivity.this, SignUpActivity.class);
                }
            } else {
                i = new Intent(OTPActivity.this, UpdateUserToSupplier.class);
            }
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            OTPActivity.this.finish();
        }
    }

    private void UpdatePhoneNumber() {
        final ProgressDialog progressDialog = new ProgressDialog(OTPActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.ChangePhoneNumber(phonenumber, "phone_number");
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
//                            JSONObject obj = object.getJSONObject("success");
                            success_Dialog(OTPActivity.this, object.getString("success"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(OTPActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(OTPActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(OTPActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_submit:
                String otp_ed = edit_one.getText().toString().trim() + edit_two.getText().toString().trim()
                        + edit_three.getText().toString().trim() + edit_four.getText().toString().trim() +
                        edit_five.getText().toString().trim() + edit_six.getText().toString().trim();
                if (!otp_ed.equalsIgnoreCase("")) {
                    if (!progressDialog.isShowing()) {
                        progressDialog.show();
                    }
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp_ed);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(OTPActivity.this, getResources().getString(R.string.enter_otp_warn));
                }


                break;

            case R.id.linear_back:
                OTPActivity.this.finish();
                break;

            case R.id.linear_button_resend:
                edit_one.setText("");
                edit_two.setText("");
                edit_three.setText("");
                edit_four.setText("");
                edit_five.setText("");
                edit_six.setText("");
                edit_one.requestFocus();
                timer.setText("01:00");
                timer_for_otp();
                send_otpcode();
                phoneauth();
                break;
        }
    }

    //check empty fields-------------------------
    private void check_all_fields() {
        if (((edit_one.getText().toString().trim().equalsIgnoreCase("") || edit_two.getText().toString().trim().equalsIgnoreCase("")
                || edit_three.getText().toString().trim().equalsIgnoreCase("") || edit_four.getText().toString().trim().equalsIgnoreCase("")))
                || edit_five.getText().toString().trim().equalsIgnoreCase("") || edit_six.getText().toString().trim().equalsIgnoreCase("")) {
            linear_submit.setAlpha((float) .4);
            linear_submit.setClickable(false);
            linear_submit.setEnabled(false);
            linear_submit.setVisibility(View.VISIBLE);
        } else {
            linear_submit.setAlpha((float) 1);
            linear_submit.setClickable(true);
            linear_submit.setEnabled(true);
        }
    }

    private void timer_for_otp() {
        timer.setVisibility(View.VISIBLE);
        total_time = 60;
        final Timer t = new Timer();
//Set the schedule function and rate
        t.scheduleAtFixedRate(new TimerTask() {
                                  @Override
                                  public void run() {
                                      //Called each time when 1000 milliseconds (1 second) (the period parameter)
                                      runOnUiThread(new Runnable() {

                                          @Override
                                          public void run() {
                                              if (total_time != 0) {
                                                  timer.setText("00:" + String.valueOf(total_time));
                                                  total_time -= 1;
                                              } else {
                                                  t.cancel();
                                                  timer.setVisibility(View.GONE);
                                                  linear_button_resend.setVisibility(View.VISIBLE);
                                                  linear_submit.setVisibility(View.GONE);
                                              }
                                          }

                                      });
                                  }

                              },
//Set how long before to start calling the TimerTask (in milliseconds)
                0,
//Set the amount of time between each execution (in milliseconds)
                1000);
    }

    private void send_otpcode() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                sessionManager.getPhoneNumber(),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);

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

                Intent resultIntent = new Intent();
                resultIntent.putExtra("edit", "close");
                setResult(Activity.RESULT_OK, resultIntent);
                OTPActivity.this.finish();

            }
        });

        // Showing Alert Message

        alertDialog.show();
    }
}
