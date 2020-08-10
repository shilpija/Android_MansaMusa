package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.freshhome.AdapterClass.PlanAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.datamodel.Plans;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class PlanFragment extends Fragment implements View.OnClickListener {
    ListView planlist;
    UserSessionManager sessionManager;
    ApiInterface apiInterface;
    ArrayList<Plans> arrayPlans;
    LinearLayout linear_proceed;

    public PlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_plan, container, false);
        arrayPlans = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(getActivity());
        MainActivity_NavDrawer.heading.setText(R.string.plan);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.GONE);
        linear_proceed = (LinearLayout) v.findViewById(R.id.linear_proceed);
        linear_proceed.setBackgroundColor(getResources().getColor(R.color.light_gray));
        linear_proceed.setOnClickListener(null);
        planlist = (ListView) v.findViewById(R.id.planlist);

        if (CommonMethods.checkConnection()) {
            GetPlans();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }

        return v;
    }

    private void GetPlans() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            //supplier
            calls = apiInterface.GetPlans("supplier");
        } else {
            //driver
            calls = apiInterface.GetPlans("delivery");
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
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Plans plans = new Plans();
                                plans.setPlan_id(obj.getString("plan_id"));
                                plans.setPlan_description(obj.getString("plan_description"));
                                plans.setPlan_duration(obj.getString("months"));
                                plans.setPlan_name(obj.getString("plan_name"));
                                plans.setPlan_price(obj.getString("price"));
//                                plans.setPlan_subscribed(obj.getString(""));

                                arrayPlans.add(plans);

                            }
                            PlanAdapter adapter = new PlanAdapter(getActivity(), arrayPlans, PlanFragment.this);
                            planlist.setAdapter(adapter);

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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_proceed:
                CommonMethods.showtoast(getActivity(), "Clicked");
                break;
        }
    }

    public void enable_proceed_btn() {
        linear_proceed.setOnClickListener(this);
        linear_proceed.setBackgroundColor(getActivity().getResources().getColor(R.color.app_color_blue));
    }
}
