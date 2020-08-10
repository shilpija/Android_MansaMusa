package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.freshhome.AdapterClass.MenuAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.MenuDetail;
import com.freshhome.R;
import com.freshhome.datamodel.MenuSupplier;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MenuFragment extends Fragment {
    LinearLayout linear_categories;
    LinkedHashMap<LinearLayout, Integer> linkedHashMapCategory;
    ListView menuList;
    ApiInterface apiInterface;
    ArrayList<MenuSupplier> array_menu;
    UserSessionManager sessionManager;

    public MenuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_menu, container, false);
        sessionManager = new UserSessionManager(getActivity());
        MainActivity_NavDrawer.heading.setText(R.string.mymenu);
        MainActivity_NavDrawer.image_addmenu.setVisibility(View.VISIBLE);
        array_menu = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        linear_categories = (LinearLayout) v.findViewById(R.id.linear_categories);
        linkedHashMapCategory = new LinkedHashMap<>();
        menuList = (ListView) v.findViewById(R.id.menuList);

        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), MenuDetail.class);
                intent.putExtra("item_id", array_menu.get(position).getId());
                startActivity(intent);
            }
        });

        setupcategories();

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
        Call<JsonElement> calls = apiInterface.GetMenuList(sessionManager.getUserDetails().get("user_id"), "");

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        array_menu = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jarrary = object.getJSONArray("success");

                            for (int i = 0; i < jarrary.length(); i++) {
                                JSONObject obj = jarrary.getJSONObject(i);
                                MenuSupplier menu = new MenuSupplier();
                                menu.setDname(obj.getString("dish_name"));
                                menu.setId(obj.getString("dish_id"));
                                menu.setImageurl(obj.getString("dish_image"));
                                menu.setDprice(obj.getString("currency")+" "+obj.getString("dish_price"));
                                menu.setDstatus(obj.getString("status"));
                                menu.setDtime(obj.getString("dish_since"));
//                                menu.setDavailable(String.valueOf(i + 1));
//                                menu.setDpending(String.valueOf(i + 3));
//                                menu.setDrating(obj.getString("dish_id"));
//                                menu.setDveiws(String.valueOf(i + 15));
                                //pending
                                menu.setDavailable(obj.getString("item_available"));
                                menu.setDpending(obj.getString("user_view"));
                                menu.setDrating(obj.getString("rate_point"));
                                menu.setDveiws(obj.getString("user_view"));
                                menu.setDiscount(obj.getString("discount"));
                                menu.setCollectedAmount(obj.getString("collected_amount"));
                                array_menu.add(menu);

                            }
                            MenuAdapter adapter = new MenuAdapter(getActivity(), array_menu);
                            menuList.setAdapter(adapter);
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

    private void setupcategories() {
        for (int i = 0; i < 5; i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_menucategories, null);
            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);

            //1 for selected 0 for not
            if (i == 1) {
                linear_bg_category.setAlpha(1);
                linkedHashMapCategory.put(linear_bg_category, 1);
            } else {
                linear_bg_category.setAlpha((float) .5);
                linkedHashMapCategory.put(linear_bg_category, 0);
            }


//            linear_bg_category.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(linkedHashMapCategory.get(v).equals(0)){
//
//                    }
//                }
//            });
            linear_categories.addView(view);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                getdata();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
//            CommonMethods.ShowLoginDialog(getActivity());
        }

    }
}