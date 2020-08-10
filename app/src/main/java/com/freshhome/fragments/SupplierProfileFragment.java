package com.freshhome.fragments;


import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.AdapterClass.MenuAdapter;
import com.freshhome.ChangeEmailActivity;
import com.freshhome.ChangePassword;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.MobileNumberActivity;
import com.freshhome.NationalIDActivity;
import com.freshhome.R;
import com.freshhome.UpdateProfileActivity;
import com.freshhome.datamodel.MenuSupplier;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupplierProfileFragment extends Fragment implements View.OnClickListener {
    LinearLayout linear_edit, linear_changepassword, linear_changecontactno, linear_changeemail,linear_change_national_id;
    TextView text_username, text_name, text_view, text_email, text_menuitems, text_pendingorders, text_deliveredorders,
            text_rating, text_loc, text_about, text_contactnumber, text_dob, text_landmark, text_flat, text_floorno, text_buildingno, text_city;
    // RatingBar ratingBar;
    SwitchCompat switch_button;
    ImageView image_flag, profile_image_blur;
    CircleImageView profile_image;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    String availablity = "";
    String image_url = "", flag_url = "", nationality = "", name = "", header_image_url = "", supplier_banner_id = "",national_pic="";

    public SupplierProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        MainActivity_NavDrawer.heading.setText(R.string.profile);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.GONE);
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();

        linear_edit = (LinearLayout) v.findViewById(R.id.linear_edit);
        linear_edit.setOnClickListener(this);

        linear_changepassword = (LinearLayout) v.findViewById(R.id.linear_changepassword);
        linear_changepassword.setOnClickListener(this);

        linear_changecontactno = (LinearLayout) v.findViewById(R.id.linear_changecontactno);
        linear_changecontactno.setOnClickListener(this);

        linear_changeemail = (LinearLayout) v.findViewById(R.id.linear_changeemail);
        linear_changeemail.setOnClickListener(this);

        linear_change_national_id = (LinearLayout) v.findViewById(R.id.linear_change_national_id);
        linear_change_national_id.setOnClickListener(this);

        text_name = (TextView) v.findViewById(R.id.text_name);
        text_email = (TextView) v.findViewById(R.id.text_email);
        text_username = (TextView) v.findViewById(R.id.text_username);
        text_view = (TextView) v.findViewById(R.id.text_view);
        text_dob = (TextView) v.findViewById(R.id.text_dob);
        text_loc = (TextView) v.findViewById(R.id.text_loc);


        text_landmark = (TextView) v.findViewById(R.id.text_landmark);
        text_flat = (TextView) v.findViewById(R.id.text_flat);
        text_floorno = (TextView) v.findViewById(R.id.text_floorno);
        text_buildingno = (TextView) v.findViewById(R.id.text_buildingno);
        text_city = (TextView) v.findViewById(R.id.text_city);

        text_menuitems = (TextView) v.findViewById(R.id.text_menuitems);
        text_pendingorders = (TextView) v.findViewById(R.id.text_pendingorders);
        text_deliveredorders = (TextView) v.findViewById(R.id.text_deliveredorders);
//        text_rating = (TextView) v.findViewById(R.id.text_rating);
        text_about = (TextView) v.findViewById(R.id.text_about);
        text_contactnumber = (TextView) v.findViewById(R.id.text_contactnumber);

        //  ratingBar = (RatingBar) v.findViewById(R.id.ratingBar);
        switch_button = (SwitchCompat) v.findViewById(R.id.switch_button);
        switch_button.setClickable(false);

        image_flag = (ImageView) v.findViewById(R.id.image_flag);
        profile_image = (CircleImageView) v.findViewById(R.id.profile_image);
        profile_image_blur = (ImageView) v.findViewById(R.id.profile_image_blur);

//        if (CommonMethods.checkConnection()) {
//            getdata();
//        } else {
//            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
//        }
        return v;
    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetSupplierProfile(sessionManager.getUserDetails().get("user_id"));

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
                            text_name.setText(jsonObject.getString("name"));

                            text_username.setText(jsonObject.getString("username"));
                            text_email.setText(jsonObject.getString("email"));
                            text_contactnumber.setText(jsonObject.getString("phone_number"));
                            text_dob.setText(CommonMethods.checkNull(CommonUtilFunctions.changeDateFormatDDMMYYYY(jsonObject.getString("DOB"))));
                            text_loc.setText(CommonMethods.checkNull(jsonObject.getString("location")));

                            text_landmark.setText(CommonMethods.checkNull(jsonObject.getString("landmark")));
                            text_flat.setText(CommonMethods.checkNull(jsonObject.getString("flat_no")));
                            text_floorno.setText(CommonMethods.checkNull(jsonObject.getString("floor_no")));
                            text_buildingno.setText(CommonMethods.checkNull(jsonObject.getString("building_no")));
                            text_city.setText(CommonMethods.checkNull(jsonObject.getString("city_name")));

                            text_about.setText(CommonMethods.checkNull(jsonObject.getString("description")));
                            availablity = CommonMethods.checkNull(jsonObject.getString("availability"));
                            national_pic=jsonObject.getString("national_pic");
                            if (jsonObject.getString("availability").equalsIgnoreCase("offline")) {
                                switch_button.setChecked(false);
                            } else {
                                switch_button.setChecked(true);
                            }


                            if (jsonObject.getString("nationality").equalsIgnoreCase("null")) {
                                image_flag.setVisibility(View.GONE);
                            } else {
                                //set nationality flag
                                image_flag.setVisibility(View.VISIBLE);
                                nationality = jsonObject.getString("nationality");
                                flag_url = jsonObject.getString("flag");
                                if (!jsonObject.getString("flag").equalsIgnoreCase("")) {
                                    Picasso.get().load(jsonObject.getString("flag")).into(image_flag);
                                }
                            }


                            // profile image
                            if (jsonObject.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(R.drawable.d_img).into(profile_image);
                                Picasso.get().load(R.drawable.degault_bg).into(profile_image_blur);
                            } else {
                                image_url = jsonObject.getString("profile_pic");
                                Picasso.get().load(jsonObject.getString("profile_pic")).placeholder(R.drawable.d_img).into(profile_image);

                            }
                            //header image)
                            supplier_banner_id = jsonObject.getString("supplier_header_image");
                            header_image_url = jsonObject.getString("supplier_header_image_path");
                            if (!header_image_url.equalsIgnoreCase("")) {
                                Picasso.get().load(jsonObject.getString("supplier_header_image_path")).into(profile_image_blur);
                            }

                            text_menuitems.setText(CommonMethods.checkNull(jsonObject.getString("menu")));
                            text_view.setText(CommonMethods.checkNull(jsonObject.getString("views")));
                            text_pendingorders.setText(CommonMethods.checkNull(jsonObject.getString("pending_order")));
                            text_deliveredorders.setText(CommonMethods.checkNull(jsonObject.getString("delivered_order")));


                            //nav drawer
                            MainActivity_NavDrawer.text_name.setText(jsonObject.getString("username"));
//                            if (Supplierobj.getString("status").equalsIgnoreCase("active")) {
//                                MainActivity_NavDrawer.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green_filled));
//                            } else {
//                                MainActivity_NavDrawer.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray));
//                            }

                            if (!jsonObject.getString("profile_pic").equalsIgnoreCase("")) {
//                                    MainActivity_NavDrawer.profile_image
                                Picasso.get().load(jsonObject.getString("profile_pic")).placeholder(R.drawable.icon).into(MainActivity_NavDrawer.profile_image);
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_edit:
                Intent i = new Intent(getActivity(), UpdateProfileActivity.class);
                i.putExtra("name", text_name.getText().toString().trim());
                i.putExtra("username", text_username.getText().toString().trim());
                i.putExtra("about", text_about.getText().toString().trim());
                i.putExtra("available", availablity);
                i.putExtra("phone", text_contactnumber.getText().toString());
                i.putExtra("imageurl", image_url);
                i.putExtra("header_url", header_image_url);
                i.putExtra("supplier_banner_id", supplier_banner_id);
                i.putExtra("nationality", nationality);
                i.putExtra("nationality_flag_url", flag_url);
                i.putExtra("dob", text_dob.getText().toString().trim());
                i.putExtra("loc", text_loc.getText().toString());
                i.putExtra("building_no", text_buildingno.getText().toString());
                i.putExtra("floor_no", text_floorno.getText().toString());
                i.putExtra("flat_no", text_flat.getText().toString());
                i.putExtra("landmark_no", text_landmark.getText().toString());
                i.putExtra("city", text_city.getText().toString());
                startActivity(i);
                break;

            case R.id.linear_changepassword:
                Intent intent = new Intent(getActivity(), ChangePassword.class);
                startActivity(intent);
                break;


            case R.id.linear_changecontactno:
                Intent i_pno = new Intent(getActivity(), MobileNumberActivity.class);
                i_pno.putExtra("fromLogin", true);
                i_pno.putExtra("fromEdit", true);
                startActivity(i_pno);

                break;


            case R.id.linear_changeemail:
                Intent intent_email = new Intent(getActivity(), ChangeEmailActivity.class);
                startActivity(intent_email);
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

}