package com.freshhome.fragments;


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
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.MobileNumberActivity;
import com.freshhome.NationalIDActivity;
import com.freshhome.R;
import com.freshhome.UserAddressListActivity;
import com.freshhome.UserEditProfileActivity;
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
public class UserProfileFragment extends Fragment implements View.OnClickListener {
    TextView text_occupation, text_dob, text_name, text_username, text_about;
    ImageView image_gender, image_flag, profile_image_blur;
    LinearLayout linear_address, linear_changepassword, linear_edit, linear_change_email, linear_contact_number, linear_change_national_id;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    CircleImageView profile_image;
    String image_url = "", flag_url = "", nationality = "", name = "", header_image_url = "", banner_id = "", national_pic = "";

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();

        linear_address = (LinearLayout) v.findViewById(R.id.linear_address);
        linear_address.setOnClickListener(this);
        linear_changepassword = (LinearLayout) v.findViewById(R.id.linear_changepassword);
        linear_changepassword.setOnClickListener(this);
        linear_edit = (LinearLayout) v.findViewById(R.id.linear_edit);
        linear_edit.setOnClickListener(this);
        image_gender = (ImageView) v.findViewById(R.id.image_gender);
        image_flag = (ImageView) v.findViewById(R.id.image_flag);

        linear_change_email = (LinearLayout) v.findViewById(R.id.linear_change_email);
        linear_change_email.setOnClickListener(this);

        linear_contact_number = (LinearLayout) v.findViewById(R.id.linear_contact_number);
        linear_contact_number.setOnClickListener(this);

        linear_change_national_id = (LinearLayout) v.findViewById(R.id.linear_change_national_id);
        linear_change_national_id.setOnClickListener(this);

        text_name = (TextView) v.findViewById(R.id.text_name);
        text_dob = (TextView) v.findViewById(R.id.text_dob);
        text_about = (TextView) v.findViewById(R.id.text_about);
        text_username = (TextView) v.findViewById(R.id.text_username);
        text_occupation = (TextView) v.findViewById(R.id.text_occupation);
        image_flag = (ImageView) v.findViewById(R.id.image_flag);
        profile_image = (CircleImageView) v.findViewById(R.id.profile_image);
        profile_image_blur = (ImageView) v.findViewById(R.id.profile_image_blur);


        return v;
    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetUserProfile("1");

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
                            text_dob.setText(CommonMethods.checkNull(CommonUtilFunctions.changeDateFormatDDMMYYYY(jsonObject.getString("DOB"))));
                            text_about.setText(CommonMethods.checkNull(jsonObject.getString("description")));
                            text_occupation.setText(CommonMethods.checkNull((jsonObject.getString("occupation_name"))));
                            national_pic = jsonObject.getString("national_pic");
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

                            //image
                            if (jsonObject.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(R.drawable.icon).into(profile_image);
                                Picasso.get().load(R.drawable.blur).into(profile_image_blur);
                            } else {
                                image_url = jsonObject.getString("profile_pic");
                                Picasso.get().load(jsonObject.getString("profile_pic")).placeholder(R.drawable.defualt_list).into(profile_image);
                            }
                            banner_id = jsonObject.getString("banner_id");
                            header_image_url = jsonObject.getString("header_image");
                            if (!header_image_url.equalsIgnoreCase("")) {
                                Picasso.get().load(jsonObject.getString("header_image")).into(profile_image_blur);
                            }

                            //nav drawer
                            MainActivity_NavDrawer.text_name.setText(jsonObject.getString("username"));
                            if (!jsonObject.getString("profile_pic").equalsIgnoreCase("")) {
                                Picasso.get().load(jsonObject.getString("profile_pic")).placeholder(R.drawable.defualt_list).into(MainActivity_NavDrawer.profile_image);
                            }
                            //gender
                            if (jsonObject.getString("gender").equalsIgnoreCase("female")) {
                                image_gender.setImageDrawable(getResources().getDrawable(R.drawable.female));
                            } else {
                                image_gender.setImageDrawable(getResources().getDrawable(R.drawable.male));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_edit:
                Intent i = new Intent(getActivity(), UserEditProfileActivity.class);
                i.putExtra("name", text_name.getText().toString().trim());
                i.putExtra("username", text_username.getText().toString().trim());
                i.putExtra("about", text_about.getText().toString().trim());
                i.putExtra("imageurl", image_url);
                i.putExtra("header_url", header_image_url);
                i.putExtra("banner_id", banner_id);
                i.putExtra("nationality", nationality);
                i.putExtra("nationality_flag_url", flag_url);
                i.putExtra("occupation", text_occupation.getText().toString().trim());
                i.putExtra("dob", text_dob.getText().toString().trim());
                startActivity(i);
                break;


            case R.id.linear_changepassword:
                Intent i_pass = new Intent(getActivity(), ChangePassword.class);
                startActivity(i_pass);
                break;

            case R.id.linear_address:
                Intent i_address = new Intent(getActivity(), UserAddressListActivity.class);
                i_address.putExtra("fromCart", false);
                startActivity(i_address);
                break;

            case R.id.linear_contact_number:
                Intent i_pno = new Intent(getActivity(), MobileNumberActivity.class);
                i_pno.putExtra("fromLogin", true);
                i_pno.putExtra("fromEdit", true);
                startActivity(i_pno);
                break;

            case R.id.linear_change_email:
                Intent i_email = new Intent(getActivity(), ChangeEmailActivity.class);
                startActivity(i_email);
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
