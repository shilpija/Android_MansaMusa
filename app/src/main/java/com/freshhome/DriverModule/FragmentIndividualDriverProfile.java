package com.freshhome.DriverModule;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.ChangeEmailActivity;
import com.freshhome.ChangePassword;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MobileNumberActivity;
import com.freshhome.NationalIDActivity;
import com.freshhome.R;
import com.freshhome.SalesModule.ActivitySalesNavDrawer;
import com.freshhome.SalesModule.Activity_Sales_EditProfile;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentIndividualDriverProfile extends Fragment implements View.OnClickListener {

    ImageView image_edit;
    LinearLayout linear_changepassword, linear_change_email, linear_contact_number, linear_change_license, linear_change_national_id;
    TextView text_username, text_name, text_loc, text_dob, text_city, text_phone, text_email, text_nationalid;
    CircleImageView circle_userimage;
    ApiInterface apiInterface;
    String image_url = "", city = "", latitute = "", longitute = "", license_url = "",national_pic="";
    RatingBar ratingBar_overall;
    UserSessionManager sessionManager;

    public FragmentIndividualDriverProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_individual_driver_profile, container, false);
        sessionManager = new UserSessionManager(getActivity());
        DriverNavDrawerActivity.heading.setText(getResources().getString(R.string.profile));
        apiInterface = ApiClient.getInstance().getClient();

        text_phone = (TextView) v.findViewById(R.id.text_phone);
        text_email = (TextView) v.findViewById(R.id.text_email);

        text_username = (TextView) v.findViewById(R.id.text_username);
        text_name = (TextView) v.findViewById(R.id.text_name);
        text_loc = (TextView) v.findViewById(R.id.text_loc);
        text_dob = (TextView) v.findViewById(R.id.text_dob);
        text_city = (TextView) v.findViewById(R.id.text_city);

        circle_userimage = (CircleImageView) v.findViewById(R.id.circle_userimage);
        image_edit = (ImageView) v.findViewById(R.id.image_edit);
        image_edit.setOnClickListener(this);

        linear_changepassword = (LinearLayout) v.findViewById(R.id.linear_changepassword);
        linear_changepassword.setOnClickListener(this);

        linear_change_email = (LinearLayout) v.findViewById(R.id.linear_change_email);
        linear_change_email.setOnClickListener(this);

        linear_contact_number = (LinearLayout) v.findViewById(R.id.linear_contact_number);
        linear_contact_number.setOnClickListener(this);

        linear_change_license = (LinearLayout) v.findViewById(R.id.linear_change_license);
        linear_change_license.setOnClickListener(this);

        linear_change_national_id = (LinearLayout) v.findViewById(R.id.linear_change_national_id);
        linear_change_national_id.setOnClickListener(this);

        ratingBar_overall = (RatingBar) v.findViewById(R.id.ratingBar_overall);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_edit:
                Intent i = new Intent(getActivity(), DriverIndividualEditProfileActivity.class);
                i.putExtra("name", text_name.getText().toString());
                i.putExtra("username", text_username.getText().toString());
                i.putExtra("loc", text_loc.getText().toString());
                i.putExtra("dob", text_dob.getText().toString());
                i.putExtra("city", city);
                i.putExtra("image_url", image_url);
                i.putExtra("lat", latitute);
                i.putExtra("lng", longitute);
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

            case R.id.linear_change_license:
                Intent i_licsense = new Intent(getActivity(), ChangeDrivingLicense.class);
                i_licsense.putExtra("license_url", license_url);
                startActivity(i_licsense);
                break;


            case R.id.linear_change_national_id:
                Intent i_national_id = new Intent(getActivity(), NationalIDActivity.class);
                i_national_id.putExtra("national_pic", national_pic);
                startActivity(i_national_id);
                break;
        }
    }

    private void getProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetIndividaulDriverProfile();
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

                            text_username.setText(obj.getString("username"));
                            text_name.setText(obj.getString("name"));
                            text_loc.setText(obj.getString("location"));
                            latitute = obj.getString("latitude");
                            longitute = obj.getString("longitude");
                            text_dob.setText(CommonUtilFunctions.changeDateFormatDDMMYYYY(obj.getString("DOB")));
                            city = obj.getString("city");
                            text_city.setText(obj.getString("city_name"));
                            text_phone.setText(obj.getString("phone_number"));
                            text_email.setText(obj.getString("email"));
                            ratingBar_overall.setRating(Float.parseFloat(obj.getString("rating")));
                            license_url = obj.getString("document");
                            national_pic=obj.getString("national_pic");
                            image_url = obj.getString("profile_pic");
                            // image
                            if (obj.getString("profile_pic").equalsIgnoreCase("null") || obj.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(R.drawable.icon).into(circle_userimage);
                            } else {
                                Picasso.get().load(obj.getString("profile_pic")).placeholder(R.drawable.icon).into(circle_userimage);
                            }

                            //UPDATE SESSION VALUES
                            sessionManager.createLoginSession(obj.getString("id"),
                                    obj.getString("username"),
                                    obj.getString("share_id"),
                                    obj.getString("email"),
                                    obj.getString("token"),
                                    sessionManager.getLoginType().toString(),
                                    CommonMethods.checkNull(obj.getString("phone_number")),
                                    "",
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_SUPPLIER),
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_USER),
                                    sessionManager.getUserDetails().get(UserSessionManager.QRCODEURL),
                                    obj.getString("is_sale_person"),
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE),
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_VIDEO_WATCHED),
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_HEADER_IMAGE),
                                    obj.getString("is_driver"),
                                    obj.getString("profile_pic"),
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_NATIONALITY),
                                    obj.getString("availability"),
                                    obj.getString("name"), true, false, obj.getString("is_driver"));


                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
    public void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            getProfile();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
    }
}
