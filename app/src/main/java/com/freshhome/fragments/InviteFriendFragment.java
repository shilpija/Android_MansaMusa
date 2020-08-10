package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class InviteFriendFragment extends Fragment implements View.OnClickListener {
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    TextView text_link, text_copy;
    LinearLayout linear_share;
    private String shareIdtxt ="";
    LinearLayout linear_link;
//    String baselink = "http://freshhomee.com/api/refer?code=";
    String baselink11 = "https://mansamusa.ae/api/refer?code=";
    String baselink = "https://mansamusa.ae/user/";
    String dynamiclink = "https://mansa.page.link/?link=https://mansamusa.ae/id&apn=com.freshhome&efr=1";

    public InviteFriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_invite_friend, container, false);
        MainActivity_NavDrawer.heading.setText(R.string.invite);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.GONE);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(getActivity());
        text_copy = (TextView) v.findViewById(R.id.text_copy);
        text_copy.setOnClickListener(this);
        text_link = (TextView) v.findViewById(R.id.text_link);
        if (sessionManager.isLoggedIn()) {
            if(sessionManager.getLoginType().equalsIgnoreCase("1")){
                shareIdtxt = sessionManager.getUserDetails().get("user_id");
                text_link.setText("https://mansa.page.link/?link=https://mansamusa.ae/"+shareIdtxt+"&apn=com.freshhome&isi=1503879600&ibi=com.freshomeapp");


            }else {
                shareIdtxt = sessionManager.getUserDetails().get("user_id");
                text_link.setText("https://mansa.page.link/?link=https://mansamusa.ae/"+shareIdtxt+"&apn=com.freshhome&isi=1503879600&ibi=com.freshomeapp");
            }
        } else {
            text_link.setText("");
        }
        linear_share = (LinearLayout) v.findViewById(R.id.linear_share);
        linear_share.setOnClickListener(this);
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

                            text_link.setText(AllAPIs.shareUrl + jsonObject.getString("share_id"));
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
            case R.id.linear_share:
                if(!text_link.getText().toString().equalsIgnoreCase("")) {
                   // CommonMethods.share_with_other(getActivity(), text_link.getText().toString().trim());
                    dynamicShare();
                }
                break;

            case R.id.text_copy:
                if(!text_link.getText().toString().equalsIgnoreCase("")) {
                    CopyToClipboard(text_link.getText().toString().trim());
                }
                break;
        }
    }

    private void CopyToClipboard(String link) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", link);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(), getResources().getString(R.string.copyed), Toast.LENGTH_SHORT).show();
    }


    private void dynamicShare(){

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
// String shareLing="https://jobyoda.page.link/?link=http://mobuloustech.com/jobyoda/api/"+JobPostId+"&apn=com.jobyodamo&isi=1471619860&ibi=com.Jobyoda.app";
//String shareLing="Job Description : "+"\n"+"https://jobyoda.page.link/?link=https://www.jobyoda.com/api/"+JobPostId+"&apn=com.jobyodamo&isi=1471619860&ibi=com.Jobyoda.app";
       // String shareLing="Job Description : "+"\n"+"https://jobyoda.page.link/?link=https://jobyoda.com/?api/"+JobPostId+"&apn=com.jobyodamo&isi=1471619860&ibi=com.Jobyoda.app";
        //String shareLing="Invite link : "+"\n"+"https://mansa.page.link/?link=https://mansamusa.ae/id&apn=com.freshhome&afl=https://mansamusa.ae/id&efr=1";
        String shareLing="Invite link : "+"\n"+"https://mansa.page.link/?link=https://mansamusa.ae/"+shareIdtxt+"&apn=com.freshhome&isi=1503879600&ibi=com.freshomeapp";
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareLing);
        startActivity(Intent.createChooser(sharingIntent, "Share using"));
    }
}
