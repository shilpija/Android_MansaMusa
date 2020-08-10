package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.SubcategoryDetailAdapter;
import com.freshhome.AdapterClass.categoryDetailBrandAdapter;
import com.freshhome.AdapterClass.categoryDetailSupplierAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ExpandableHeightGridView;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
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

import static com.freshhome.MainActivity_NavDrawer.heading;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryDetailFragment extends Fragment implements View.OnClickListener {
    LinearLayout linear_sub_categories, linear_suppliers, linear_brands;
    TextView text_Cviewall, text_Sviewall, text_Bviewall;
    ExpandableHeightGridView gridCategory, gridSupplier, gridBrands;
    ApiInterface apiInterface;
    public static UserSessionManager sessionManager;
    String homecategoryID = "";
    String homecategoryHeader = "";
    ArrayList<categories> arrayCategories, arraySupplier, arrayBrands;

    public CategoryDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_category_detail, container, false);
        arraySupplier = new ArrayList<>();
        arrayBrands = new ArrayList<>();
        arrayCategories = new ArrayList<>();
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();

        homecategoryID = getArguments().getString("cat_id");
        homecategoryHeader = getArguments().getString("cat_header");
        linear_brands = (LinearLayout) v.findViewById(R.id.linear_brands);
        linear_suppliers = (LinearLayout) v.findViewById(R.id.linear_suppliers);
        linear_sub_categories = (LinearLayout) v.findViewById(R.id.linear_sub_categories);

        text_Cviewall = (TextView) v.findViewById(R.id.text_Cviewall);
        text_Cviewall.setOnClickListener(this);
        text_Sviewall = (TextView) v.findViewById(R.id.text_Sviewall);
        text_Sviewall.setOnClickListener(this);
        text_Bviewall = (TextView) v.findViewById(R.id.text_Bviewall);
        text_Bviewall.setOnClickListener(this);

        gridCategory = (ExpandableHeightGridView) v.findViewById(R.id.gridCategory);
        gridCategory.setExpanded(true);
        gridSupplier = (ExpandableHeightGridView) v.findViewById(R.id.gridSupplier);
        gridSupplier.setExpanded(true);
        gridBrands = (ExpandableHeightGridView) v.findViewById(R.id.gridBrands);
        gridBrands.setExpanded(true);

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
        Call<JsonElement> calls = apiInterface.GetCategoryDetail(homecategoryID);
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

                            JSONArray jarray = object.getJSONArray("brand_list");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject jobj = jarray.getJSONObject(i);
                                categories cat = new categories();
                                cat.setId(jobj.getString("brand_id"));
                                cat.set_name(jobj.getString("name"));
                                cat.setimage(jobj.getString("image"));
                                arrayBrands.add(cat);
                            }

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

                            JSONArray jarray_cat = object.getJSONArray("sub_category");
                            for (int i = 0; i < jarray_cat.length(); i++) {
                                JSONObject jobj = jarray_cat.getJSONObject(i);
                                categories cat = new categories();
                                cat.setId(jobj.getString("sub_category_id"));
                                cat.set_name(jobj.getString("name"));
                                cat.setimage(jobj.getString("image"));
                                arrayCategories.add(cat);
                            }

                            if (arrayBrands.size() > 0) {
                                linear_brands.setVisibility(View.VISIBLE);
                                categoryDetailBrandAdapter adapter = new categoryDetailBrandAdapter(getActivity(), arrayBrands);
                                gridBrands.setAdapter(adapter);
                            } else {
                                linear_brands.setVisibility(View.GONE);
                            }

                            if (arraySupplier.size() > 0) {
                                linear_suppliers.setVisibility(View.VISIBLE);
                                categoryDetailSupplierAdapter adapter_supplier = new categoryDetailSupplierAdapter(getActivity(), arraySupplier);
                                gridSupplier.setAdapter(adapter_supplier);
                            } else {
                                linear_suppliers.setVisibility(View.GONE);
                            }

                            if (arrayCategories.size() > 0) {
                                linear_sub_categories.setVisibility(View.VISIBLE);
                                SubcategoryDetailAdapter adapter_latestp = new SubcategoryDetailAdapter(getActivity(), arrayCategories);
                                gridCategory.setAdapter(adapter_latestp);
                            } else {
                                linear_sub_categories.setVisibility(View.GONE);
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
            case R.id.text_Cviewall:
                heading.setText(getResources().getString(R.string.shop_category));
                replace_fragment(getResources().getString(R.string.shop_category));
                break;

            case R.id.text_Sviewall:
                heading.setText(getResources().getString(R.string.shop_supplier));
                replace_fragment(getResources().getString(R.string.shop_supplier));
                break;

            case R.id.text_Bviewall:
                heading.setText(getResources().getString(R.string.shop_brands));
                replace_fragment(getResources().getString(R.string.shop_brands));
                break;
        }
    }

    private void replace_fragment(String type) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ViewAllCategoryDetailFragment frag_best = new ViewAllCategoryDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        bundle.putString("homecategory_id", homecategoryID);
        frag_best.setArguments(bundle);
        ft.replace(R.id.main_linear, frag_best);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume ( );
        MainActivity_NavDrawer.heading.setText(homecategoryHeader);
    }
}
