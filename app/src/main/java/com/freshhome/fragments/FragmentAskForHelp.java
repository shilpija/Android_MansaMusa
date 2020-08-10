package com.freshhome.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.ActivitySupplierRequestDetail;
import com.freshhome.AddressPickerAct;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAskForHelp extends BottomSheetDialogFragment implements View.OnClickListener {
    private static final int PLACE_PICKER_REQUEST = 3;
    LinearLayout linear_send_request, linear_send, linear_pending_req;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    TextView text_loc, text_cancel, text_roles, text_cancel_request;
    EditText edit_phone, edit_name, edit_email;
    Spinner stype_spinner;
    String supplierType = "", current_request_id = "", sales_data = "", reason_id = "";
    double latitute = 0.0, longitute = 0.0;
    TextView text_name, text_contactno, text_loc_request, text_time, text_status;
    BottomSheetDialog dialog;
    ArrayList<NameID> cancelReasonList;
    String[] spinner_reason_Array;
    EditText edit_msg;

    double pLat, pLong, dLat, dLong;
    private String lat1;
    private String lat2;
    private String deslat1;
    private String deslat2;
    private String address1 = "";

    String comId="";

    public FragmentAskForHelp() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_ask_for_help, container, false);


        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(getActivity());
        cancelReasonList = new ArrayList<>();
        text_roles = (TextView) v.findViewById(R.id.text_roles);
        linear_send_request = (LinearLayout) v.findViewById(R.id.linear_send_request);
        linear_send_request.setOnClickListener(this);
        linear_pending_req = (LinearLayout) v.findViewById(R.id.linear_pending_req);
        linear_pending_req.setOnClickListener(this);
        text_name = (TextView) v.findViewById(R.id.text_name);
        text_contactno = (TextView) v.findViewById(R.id.text_contactno);
        text_loc_request = (TextView) v.findViewById(R.id.text_loc_request);
        text_time = (TextView) v.findViewById(R.id.text_time);
        text_status = (TextView) v.findViewById(R.id.text_status);
        text_cancel_request = (TextView) v.findViewById(R.id.text_cancel_request);
        text_cancel_request.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_send_request:
                View view = getLayoutInflater().inflate(R.layout.layout_ask_request_dialog, null);
                dialog = new BottomSheetDialog(getActivity());
                dialog.setContentView(view);
                dialog.setCanceledOnTouchOutside(false);

                edit_name = (EditText) dialog.findViewById(R.id.edit_name);
                edit_phone = (EditText) dialog.findViewById(R.id.edit_phone);
                edit_email = (EditText) dialog.findViewById(R.id.edit_email);
                stype_spinner = (Spinner) dialog.findViewById(R.id.stype_spinner);
                text_loc = (TextView) dialog.findViewById(R.id.text_loc);
                linear_send = (LinearLayout) dialog.findViewById(R.id.linear_send);
                text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);

                if (sessionManager.isLoggedIn()) {
                    edit_phone.setText(sessionManager.getUserDetails().get(UserSessionManager.KEY_PHONE));
                    edit_email.setText(sessionManager.getUserDetails().get(UserSessionManager.KEY_EMAIL));

                    if (!sessionManager.getUserDetails().get(UserSessionManager.KEY_PHONE).equalsIgnoreCase("")) {
                        edit_phone.setEnabled(false);
                        edit_phone.setFocusable(false);
                    }

                    edit_email.setEnabled(false);
                    edit_email.setFocusable(false);
                } else {
                    edit_phone.setEnabled(true);
                    edit_phone.setFocusable(true);

                    edit_email.setEnabled(true);
                    edit_email.setFocusable(true);
                }
                String[] spinner_array = getResources().getStringArray(R.array.stypePr);
                ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(), R.layout.layout_spinner_text_bg_transparent, spinner_array);
                stype_spinner.setAdapter(spinner_adapter);

                stype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (stype_spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.type_food))) {
                            supplierType = "1";
                        } else if (stype_spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.type_homemade))) {
                            supplierType = "2";
                        }
//                        else if (stype_spinner.getSelectedItem().toString().equalsIgnoreCase(getResources().getString(R.string.type_shop))) {
//                            supplierType = "3";
//                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                text_loc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(AddressPickerAct.getIntent(getActivity(), "PICKUP", ""), 101);
//                        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                        try {
//                            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
//                        } catch (GooglePlayServicesRepairableException e) {
//                            e.printStackTrace();
//                        } catch (GooglePlayServicesNotAvailableException e) {
//                            e.printStackTrace();
//                        }

                    }
                });
                text_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                linear_send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edit_name.getText().toString().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_name));
                        } else if (edit_email.getText().toString().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_email_id));
                        } else if (!CommonMethods.isValidEmail(edit_email.getText().toString())) {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.valid_email));
                        } else if (edit_phone.getText().toString().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_phone));
                        } else if (text_loc.getText().toString().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.select_location));
                        } else {
                            if (CommonMethods.checkConnection()) {
                                SubmitHelpRequest(edit_name.getText().toString(), text_loc.getText().toString().trim(), edit_phone.getText().toString().trim(),
                                        latitute, longitute, dialog, edit_email.getText().toString());
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                            }
                        }
                    }
                });
                dialog.show();
                break;


            case R.id.linear_pending_req:
                if (!text_status.getText().toString().equalsIgnoreCase("Pending")) {
                    Intent i = new Intent(getActivity(), ActivitySupplierRequestDetail.class);
                    i.putExtra("req_id", current_request_id);
                    i.putExtra("sales_data", sales_data);
                    getActivity().startActivity(i);
                }
                break;


            case R.id.text_cancel_request:

                if (text_cancel_request.getText().toString().equalsIgnoreCase("Request Completed")) {
                    GetCompleteRequestApi();
                    //Toast.makeText(getActivity(), "yes find click", Toast.LENGTH_SHORT).show();

                } else {

                    if (cancelReasonList.size() != 0) {
                        ShowCancelRequestDialog();
                    } else {
                        if (CommonMethods.checkConnection()) {
                            ReasonForCancel();
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                        }
                    }
                }

                break;

            case R.id.linear_send:
                if (CommonMethods.checkConnection()) {
                    if (edit_msg != null) {
                        if (reason_id.equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.select_reason));
                        } else if (edit_msg.getText().toString().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_msg));
                        } else {
                            CancelRequest(current_request_id, reason_id, edit_msg.getText().toString().trim());
                        }
                    }
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_cancel:
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                break;

        }
    }

    private void SubmitHelpRequest(String name, String loc, String phone, double lat, double lng, final BottomSheetDialog dialog, String email) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.AskForHelp(name, loc, phone, lat, lng, sessionManager.getFCMToken(), supplierType, email);

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
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }
                            CommonUtilFunctions.success_Alert_Dialog(getActivity(), obj.getString("msg"));
                            if (CommonMethods.checkConnection()) {
                                GetCurrentRequestandRoles();
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                            }
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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
  //      switch (requestCode) {
//            case PLACE_PICKER_REQUEST:

                if (requestCode == 101) {
                    if (resultCode == Activity.RESULT_OK) {

                        address1 = data.getStringExtra("ADDRESS");
                        lat1 = data.getStringExtra("LAT");
                        lat2 = data.getStringExtra("LONG");
                        pLat = Double.parseDouble(lat1);
                        pLong = Double.parseDouble(lat2);
                        text_loc.setText(address1);
                        text_loc.setTextColor(getResources().getColor(R.color.black));
                    }

                }

//                if (resultCode == RESULT_OK) {
//                    Place place = PlacePicker.getPlace(data, getActivity());
//                    latitute = place.getLatLng().latitude;
//                    longitute = place.getLatLng().longitude;
//                    text_loc.setText(place.getName().toString());
//
////                String toastMsg = String.format("Place: %s", place.getName());
////                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
//
//                }
 //
    }


    //get roles text and current request of supplier
    private void GetCompleteRequestApi() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetCompleteReq(current_request_id);

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
                            //text_roles.setText(Html.fromHtml(object.getString("data")));

                            JSONObject obj = object.getJSONObject("success");
                            if (CommonMethods.checkConnection()) {
                                GetCurrentRequestandRoles();
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                            }
                            CommonUtilFunctions.success_Alert_Dialog(getActivity(), obj.getString("msg"));
                            //Toast.makeText(getActivity(), ""+obj.getString("msg"), Toast.LENGTH_SHORT).show();

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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }


    //get roles text and current request of supplier
    private void GetCurrentRequestandRoles() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetCurrentReq(sessionManager.getFCMToken());

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
                            text_roles.setText(Html.fromHtml(object.getString("data")));

                            JSONObject obj = object.getJSONObject("help_request");

                            if (obj.has("request_id")) {
                                linear_send_request.setVisibility(View.GONE);
                                linear_pending_req.setVisibility(View.VISIBLE);
                                current_request_id = obj.getString("request_id");
                                text_loc_request.setText(obj.getString("location"));
                                text_contactno.setText(obj.getString("phonenumber"));
                                text_name.setText(obj.getString("name"));
                                text_status.setText(obj.getString("request_status"));
                                if (obj.getString("request_status").equalsIgnoreCase("In progress")) {
                                    text_cancel_request.setText("Request Completed");
                                }

                                sales_data = obj.getString("salesperson");

                            } else {
                                linear_pending_req.setVisibility(View.GONE);
                                linear_send_request.setVisibility(View.VISIBLE);
                            }

                            //change
                            text_time.setText("2 days ago");

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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            GetCurrentRequestandRoles();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
    }


    private void ShowCancelRequestDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_cancel_request_dialog, null);
        dialog = new BottomSheetDialog(getActivity());
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(false);
        edit_msg = (EditText) dialog.findViewById(R.id.edit_msg);
        LinearLayout linear_send = (LinearLayout) dialog.findViewById(R.id.linear_send);
        linear_send.setOnClickListener(this);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);
        stype_spinner = (Spinner) dialog.findViewById(R.id.stype_spinner);

        ArrayAdapter<String> genderAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.layout_spinner_text_bg_transparent, spinner_reason_Array);
        stype_spinner.setAdapter(genderAdapter);


        stype_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (cancelReasonList.size() != 0) {
                    for (int i = 0; i < cancelReasonList.size(); i++) {
                        if (cancelReasonList.get(i).getName().equalsIgnoreCase(stype_spinner.getSelectedItem().toString())) {
                            reason_id = cancelReasonList.get(position).getId();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        dialog.show();
    }

    //TODO API HITs
    private void ReasonForCancel() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetCancelReason();
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
                            cancelReasonList = new ArrayList<>();
                            JSONArray jsonArray = object.getJSONArray("data");
                            spinner_reason_Array = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(jsonObject.getString("reason_id"));
                                nameID.setName(jsonObject.getString("name"));
                                cancelReasonList.add(nameID);
                                spinner_reason_Array[i] = jsonObject.getString("name");
                            }
                            ShowCancelRequestDialog();

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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }

    private void CancelRequest(final String request_id, String reasonID, String msg) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.CancelRequestBySupplier(request_id, reasonID, msg, sessionManager.getFCMToken());
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
                            linear_pending_req.setVisibility(View.GONE);
                            linear_send_request.setVisibility(View.VISIBLE);
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                            }

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
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
            }
        });
    }
}
