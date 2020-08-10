package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.R;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactUsFragment extends Fragment implements View.OnClickListener {
    EditText edit_msg, edit_email, edit_subject;
    LinearLayout linear_send;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    String role = "";

    public ContactUsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contact_us, container, false);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(getActivity());

        linear_send = (LinearLayout) v.findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);

        edit_msg = (EditText) v.findViewById(R.id.edit_msg);
        edit_email = (EditText) v.findViewById(R.id.edit_email);
        edit_subject = (EditText) v.findViewById(R.id.edit_subject);
        edit_email.setText(sessionManager.getUserDetails().get("email"));
        edit_email.setEnabled(false);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_send:
                if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
                    role = "User";
                } else {
                    role = "Supplier";
                }
                if (edit_subject.getText().toString().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_sub));
                } else if (edit_email.getText().toString().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_email));
                } else if (!CommonMethods.isValidEmail(edit_email.getText().toString())) {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.valid_email));
                } else if (edit_msg.getText().toString().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_msg));
                } else {
                    if (CommonMethods.checkConnection()) {
                        postdata();
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                    }
                }
                break;
        }
    }

    private void postdata() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        //TODO check if the login type is driver then hit api accordingly
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.Driver)) {

            calls = apiInterface.ContactUsDriver(edit_subject.getText().toString().trim(),
                    edit_email.getText().toString().trim(), edit_msg.getText().toString().trim());
        } else {
            calls = apiInterface.ContactUs(edit_subject.getText().toString().trim(),
                    edit_email.getText().toString().trim(), edit_msg.getText().toString().trim(), role);
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
                            CommonUtilFunctions.success_Alert_Dialog(getActivity(), obj.getString("msg"));
                            edit_email.setText("");
                            edit_msg.setText("");
                            edit_subject.setText("");
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
}
