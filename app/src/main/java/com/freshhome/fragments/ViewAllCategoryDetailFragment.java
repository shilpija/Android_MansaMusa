package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.freshhome.AdapterClass.SubcategoryDetailAdapter;
import com.freshhome.AdapterClass.ViewAllSubcategoryDetailAdapter;
import com.freshhome.AdapterClass.categoryDetailBrandAdapter;
import com.freshhome.AdapterClass.categoryDetailSupplierAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.datamodel.categories;
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
public class ViewAllCategoryDetailFragment extends Fragment {
    GridView gridView;
    String homecategoryID = "", type = "";
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    ArrayList<categories> arrayCategories, arraySupplier, arrayBrands;

    public ViewAllCategoryDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_view_all_category_detail, container, false);
        gridView = (GridView) v.findViewById(R.id.gridView);
        arraySupplier = new ArrayList<>();
        Bundle extras = getArguments();
        type = extras.getString("type");
        homecategoryID = extras.getString("homecategory_id");
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(getActivity());

        if (CommonMethods.checkConnection()) {
            getData();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
        }
        return v;
    }


    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = null;
        //category detail page view all
        if (type.equalsIgnoreCase(getResources().getString(R.string.shop_category))) {
            //calls = apiInterface.GetAllSubCategories(homecategoryID,"1");
            calls = apiInterface.GetCategoryDetail(homecategoryID);

        } else if (type.equalsIgnoreCase(getResources().getString(R.string.shop_supplier))) {
            calls = apiInterface.GetKitchens("1");

        } else if (type.equalsIgnoreCase(getResources().getString(R.string.shop_brands))) {
            calls = apiInterface.GetAllBrands(homecategoryID,"1");

        } else {
            calls = apiInterface.GetAllSubCategories(homecategoryID,"1");
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
                            arraySupplier = new ArrayList<>();
                            arrayBrands = new ArrayList<>();
                            arrayCategories = new ArrayList<>();

                            if (object.has("brand_list")) {
                                JSONArray jarray = object.getJSONArray("brand_list");
                                for (int i = 0; i < jarray.length(); i++) {
                                    JSONObject jobj = jarray.getJSONObject(i);
                                    categories cat = new categories();
                                    cat.setId(jobj.getString("brand_id"));
                                    cat.set_name(jobj.getString("name"));
                                    cat.setimage(jobj.getString("image"));
                                    arrayBrands.add(cat);
                                }
                            }
                            if (object.has("suppliers")) {
                                JSONArray jsarray = object.getJSONArray("suppliers");
                                for (int i = 0; i < jsarray.length(); i++) {
                                    JSONObject jobj = jsarray.getJSONObject(i);
                                    categories cat = new categories();
                                    cat.setId(jobj.getString("id"));
                                    cat.set_name(jobj.getString("name"));
                                    cat.setimage(jobj.getString("image"));
                                    cat.setAvailable(jobj.getString("availability"));
                                    arraySupplier.add(cat);
                                }
                            }
                            if (object.has("online_kitchens")) {
                                JSONArray jsarray = object.getJSONArray("online_kitchens");
                                for (int i = 0; i < jsarray.length(); i++) {
                                    JSONObject jobj = jsarray.getJSONObject(i);
                                    categories cat = new categories();
                                    cat.setId(jobj.getString("id"));
                                    cat.set_name(jobj.getString("name"));
                                    cat.setimage(jobj.getString("profile_pic"));
                                    cat.setAvailable(jobj.getString("availability"));
                                    arraySupplier.add(cat);
                                }
                            }
                            if (object.has("sub_category")) {
                                JSONArray jarray_cat = object.getJSONArray("sub_category");
                                for (int i = 0; i < jarray_cat.length(); i++) {
                                    JSONObject jobj = jarray_cat.getJSONObject(i);
                                    categories cat = new categories();
                                    cat.setId(jobj.getString("sub_category_id"));
                                    cat.set_name(jobj.getString("name"));
                                    cat.setimage(jobj.getString("image"));
                                    arrayCategories.add(cat);
                                }
                            }

                            if (arrayBrands.size() > 0) {
                                categoryDetailBrandAdapter adapter = new categoryDetailBrandAdapter(getActivity(), arrayBrands);
                                gridView.setAdapter(adapter);
                            }

                            if (arraySupplier.size() > 0) {
                                categoryDetailSupplierAdapter adapter_supplier = new categoryDetailSupplierAdapter(getActivity(), arraySupplier);
                                gridView.setAdapter(adapter_supplier);
                            }

                            if (arrayCategories.size() > 0) {
                                ViewAllSubcategoryDetailAdapter adapter_latestp = new ViewAllSubcategoryDetailAdapter(getActivity(), arrayCategories);
                                gridView.setAdapter(adapter_latestp);
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
