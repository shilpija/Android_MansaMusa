package com.freshhome.SalesModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.R;
import com.freshhome.SignUpActivity;
import com.freshhome.UserEditProfileActivity;
import com.freshhome.datamodel.NameID;
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

public class ActivitySalesRegister extends AppCompatActivity implements View.OnClickListener {
    TextView text_mobilenumber, text_select_dob, text_login;
    EditText edit_name, edit_username, edit_emailid, edit_password, edit_re_password;
    Spinner genderspinner, cityspinner;
    LinearLayout linear_signup, linear_back;
    ArrayList<NameID> arrayCity;
    String[] spinner_city_array;
    ApiInterface apiInterface;
    String latitute = "0", longitute = "0", selected_city_id = "";
    UserSessionManager sessionManager;
    String[] spinner_gender_array;
    ImageView gender_spinner_arrow, city_spinner_arrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_register);

        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(ActivitySalesRegister.this);

        linear_signup = (LinearLayout) findViewById(R.id.linear_signup);
        linear_signup.setOnClickListener(this);

        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        text_mobilenumber = (TextView) findViewById(R.id.text_mobilenumber);
        text_mobilenumber.setText(sessionManager.getPhoneNumber());

        text_select_dob = (TextView) findViewById(R.id.text_select_dob);
        text_select_dob.setOnClickListener(this);

        text_login = (TextView) findViewById(R.id.text_login);
        text_login.setOnClickListener(this);

        genderspinner = (Spinner) findViewById(R.id.genderspinner);
        cityspinner = (Spinner) findViewById(R.id.cityspinner);

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_emailid = (EditText) findViewById(R.id.edit_emailid);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_re_password = (EditText) findViewById(R.id.edit_re_password);

        gender_spinner_arrow = (ImageView) findViewById(R.id.gender_spinner_arrow);
        gender_spinner_arrow.setOnClickListener(this);
        city_spinner_arrow = (ImageView) findViewById(R.id.city_spinner_arrow);
        city_spinner_arrow.setOnClickListener(this);


        if (CommonMethods.checkConnection()) {
            getCitdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.internetconnection));
        }

        spinner_gender_array = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(ActivitySalesRegister.this, R.layout.layout_spinner_text_bg_transparent, spinner_gender_array);
        genderspinner.setAdapter(genderAdapter);


        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrayCity.size() != 0) {
                    selected_city_id = arrayCity.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_signup:
                if (edit_name.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.enter_name));
                } else if (edit_username.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.enter_username));
                } else if (edit_emailid.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.enter_email));
                } else if (!CommonMethods.isValidEmail(edit_emailid.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.valid_email));
                } else if (edit_password.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.enter_password));
                } else if (edit_password.getText().toString().trim().length() < 5) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.pass_length));
                } else if (edit_re_password.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.enter_repassword));
                } else if (!edit_password.getText().toString().trim().equalsIgnoreCase(edit_re_password.getText().toString().trim())) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.password_match));
                } else if (text_select_dob.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.enterdob));
                } else {
                    if (CommonMethods.checkConnection()) {
                        postSignupdata();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.internetconnection));
                    }
                }

                break;
            case R.id.text_login:
                ActivitySalesRegister.this.finish();
//                Intent i = new Intent(ActivitySalesRegister.this, ActivitySalesNavDrawer.class);
//                startActivity(i);
                break;


            case R.id.linear_back:
                ActivitySalesRegister.this.finish();
                break;

            case R.id.gender_spinner_arrow:
                genderspinner.performClick();
                break;

            case R.id.city_spinner_arrow:
                cityspinner.performClick();
                break;

            case R.id.text_select_dob:
                CommonUtilFunctions.DatePickerDialog_DOB(ActivitySalesRegister.this, text_select_dob);
                break;

        }
    }

    //signup
    private void postSignupdata() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivitySalesRegister.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.SalesRegister(edit_username.getText().toString().trim(),
                edit_emailid.getText().toString().trim(), text_mobilenumber.getText().toString().trim(),
                edit_name.getText().toString().trim(),
                genderspinner.getSelectedItem().toString(),
                CommonUtilFunctions.changeDateFormatYYYYMMDD(text_select_dob.getText().toString()),
                selected_city_id, edit_password.getText().toString().trim(), 0.0, 0.0);

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
                            //move to next login screen
                            Toast.makeText(ActivitySalesRegister.this, "An email has been sent. Please verify your account.", Toast.LENGTH_SHORT).show();
                            Intent i_nav = new Intent(ActivitySalesRegister.this, LoginActivity.class);
                            i_nav.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i_nav);
                            ActivitySalesRegister.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, obj.getString("msg"));
                        }

                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.server_error));
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


    //getcity
    private void getCitdata() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivitySalesRegister.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetFilterData(sessionManager.getCountryName());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        arrayCity = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            //city array
                            JSONArray city_array = jsonObject.getJSONArray("city");
                            spinner_city_array = new String[city_array.length()];
                            for (int i = 0; i < city_array.length(); i++) {
                                JSONObject obj = city_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("city_id"));
                                nameID.setName(obj.getString("city_name"));
                                spinner_city_array[i] = obj.getString("city_name");
                                arrayCity.add(nameID);
                            }
                            //set up spinner
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(ActivitySalesRegister.this, R.layout.layout_spinner_text_bg_transparent, spinner_city_array);
                            cityspinner.setAdapter(spinner_adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesRegister.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
