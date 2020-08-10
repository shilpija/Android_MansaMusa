package com.freshhome.SalesModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.ChangeEmailActivity;
import com.freshhome.ChangePassword;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.MobileNumberActivity;
import com.freshhome.NationalIDActivity;
import com.freshhome.R;
import com.freshhome.datamodel.SalesRequest;
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

public class Fragment_Sales_Profile extends Fragment implements View.OnClickListener {
    ImageView image_edit;
    LinearLayout linear_changepassword, linear_change_email, linear_contact_number, linear_change_national_id;
    TextView text_username, text_name, text_gender, text_dob, text_city, text_phone, text_email;
    CircleImageView circle_userimage;
    ApiInterface apiInterface;
    String image_url = "", national_pic = "";

    public Fragment_Sales_Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sales_profile, container, false);
        ActivitySalesNavDrawer.heading.setText(getResources().getString(R.string.profile));
        apiInterface = ApiClient.getInstance().getClient();

        text_phone = (TextView) v.findViewById(R.id.text_phone);
        text_email = (TextView) v.findViewById(R.id.text_email);

        text_username = (TextView) v.findViewById(R.id.text_username);
        text_name = (TextView) v.findViewById(R.id.text_name);
        text_gender = (TextView) v.findViewById(R.id.text_gender);
        text_dob = (TextView) v.findViewById(R.id.text_dob);
        text_city = (TextView) v.findViewById(R.id.text_city);

        circle_userimage = (CircleImageView) v.findViewById(R.id.circle_userimage);
        image_edit = (ImageView) v.findViewById(R.id.image_edit);
        image_edit.setOnClickListener(this);


        linear_change_email = (LinearLayout) v.findViewById(R.id.linear_change_email);
        linear_change_email.setOnClickListener(this);

        linear_contact_number = (LinearLayout) v.findViewById(R.id.linear_contact_number);
        linear_contact_number.setOnClickListener(this);

        linear_change_national_id = (LinearLayout) v.findViewById(R.id.linear_change_national_id);
        linear_change_national_id.setOnClickListener(this);
//        if (CommonMethods.checkConnection()) {
//            getdata();
//        } else {
//            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
//        }
        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_edit:
                Intent i = new Intent(getActivity(), Activity_Sales_EditProfile.class);
                i.putExtra("name", text_name.getText().toString());
                i.putExtra("username", text_username.getText().toString());
                i.putExtra("gender", text_gender.getText().toString());
                i.putExtra("dob", text_dob.getText().toString());
                i.putExtra("city", text_city.getText().toString());
                i.putExtra("image_url", image_url);
                startActivity(i);
                break;

            case R.id.linear_change_email:
                Intent i_email = new Intent(getActivity(), ChangeEmailActivity.class);
                startActivity(i_email);
                break;

            case R.id.linear_contact_number:
                Intent i_pno = new Intent(getActivity(), MobileNumberActivity.class);
                i_pno.putExtra("fromLogin", true);
                i_pno.putExtra("fromEdit", true);
                startActivity(i_pno);
                break;


            case R.id.linear_changepassword:
                Intent i_pass = new Intent(getActivity(), ChangePassword.class);
                startActivity(i_pass);
                break;

            case R.id.linear_change_national_id:
                Intent i_national_id = new Intent(getActivity(), NationalIDActivity.class);
                i_national_id.putExtra("national_pic", national_pic);
                startActivity(i_national_id);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetSalesProfile();

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

                            JSONObject jsonObject = object.getJSONObject("success");
                            text_username.setText(jsonObject.getString("username"));
                            text_name.setText(jsonObject.getString("name"));
                            if (!jsonObject.getString("dob").equalsIgnoreCase("null") || !jsonObject.getString("dob").equalsIgnoreCase("")) {
                                text_dob.setText(CommonUtilFunctions.changeDateFormatDDMMYYYY(jsonObject.getString("dob")));
                            }
                            text_gender.setText(jsonObject.getString("gender"));
                            text_city.setText(jsonObject.getString("city_name"));
                            text_phone.setText(jsonObject.getString("phone_number"));
                            text_email.setText(jsonObject.getString("email"));

                            ActivitySalesNavDrawer.text_username.setText(jsonObject.getString("username"));
                            if (!jsonObject.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(jsonObject.getString("profile_pic")).placeholder(R.drawable.icon).into(ActivitySalesNavDrawer.profile_image);
                            }

                            //image
                            national_pic = jsonObject.getString("national_pic");
                            image_url = jsonObject.getString("profile_pic");
                            if (jsonObject.getString("profile_pic").equalsIgnoreCase("null") || jsonObject.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(R.drawable.icon).into(circle_userimage);
                            } else {
                                Picasso.get().load(jsonObject.getString("profile_pic")).
                                        placeholder(R.drawable.icon).into(circle_userimage);
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }
}
