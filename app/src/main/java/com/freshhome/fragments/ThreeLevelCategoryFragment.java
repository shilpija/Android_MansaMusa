package com.freshhome.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.freshhome.AdapterClass.SubcategoryDetailAdapter;
import com.freshhome.AdapterClass.ThreeLevelShowAdapter;
import com.freshhome.AdapterClass.categoryDetailBrandAdapter;
import com.freshhome.AdapterClass.categoryDetailSupplierAdapter;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ExpandableHeightGridView;
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
public class ThreeLevelCategoryFragment extends Fragment {
    ApiInterface apiInterface;
    public static UserSessionManager sessionManager;

    String homecategoryID = "";
    ArrayList<categories> arrayCategories;
private LinearLayout linear_sub_categories;
    ExpandableHeightGridView gridCategory;
    public ThreeLevelCategoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_three_level_category, container, false);


        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();

        homecategoryID = getArguments().getString("cat_id");
        linear_sub_categories = (LinearLayout) view.findViewById(R.id.linear_sub_categories);
        gridCategory = (ExpandableHeightGridView) view.findViewById(R.id.gridCategory);
        gridCategory.setExpanded(true);

        getData();
         return view;

    }


    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetsubcategoryList(homecategoryID);
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
                            arrayCategories = new ArrayList<>();

                            JSONArray jarray = object.getJSONArray("subcategory-list");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject jobj = jarray.getJSONObject(i);
                                categories cat = new categories();
                                cat.setId(jobj.getString("sub_category_id"));
                                cat.set_name(jobj.getString("name"));
                                cat.setimage(jobj.getString("image"));
                                arrayCategories.add(cat);
                            }


                            if (arrayCategories.size() > 0) {
                                linear_sub_categories.setVisibility(View.VISIBLE);
                                ThreeLevelShowAdapter adapter_latestp = new ThreeLevelShowAdapter(getActivity(), arrayCategories);
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
}
