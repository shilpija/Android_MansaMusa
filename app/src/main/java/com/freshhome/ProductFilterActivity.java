package com.freshhome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.freshhome.AdapterClass.FilterDepartmentCategoryadapter;
import com.freshhome.AdapterClass.FilterSubCategoryListAdapter;
import com.freshhome.AdapterClass.ProductFiterNewAdapter;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.datamodel.NameID;
import com.freshhome.fragments.AllProductShowFromCatFragment;
import com.freshhome.fragments.BuyerSearchFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductFilterActivity extends AppCompatActivity implements FilterSubCategoryListAdapter.OnItemListener, ProductFiterNewAdapter.OnItemListener {
    String jsonData = "";
    JSONArray cat_array;
    FilterDepartmentCategoryadapter filterDepartmentAdapter;
    ProductFiterNewAdapter productFiterNewAdapter;
    ArrayList<NameID> arrayCategory, arraySortingMethods;
    ArrayList<String> homecateList;
    HashSet<String> filterfinalCate;
    ArrayList<ArrayList<NameID>> totalCategoryList;
    int lastPosition = -1;
    @BindView(R.id.rvDepartment)
    RecyclerView rvDepartment;

    @BindView(R.id.rgAverageReview)
    RadioGroup rgAverageReview;

    @BindView(R.id.rgNewArrival)
    RadioGroup rgNewArrival;

    @BindView(R.id.rgPrice)
    RadioGroup rgPrice;

    @BindView(R.id.rgDiscount)
    RadioGroup rgDiscount;

    @BindView(R.id.tvDepartment)
    TextView tvDepartment;
    @BindView(R.id.view)
    View view;
    //--------//
    @BindView(R.id.rb10OffMore)
    RadioButton rb10OffMore;
    @BindView(R.id.rb20OffMore)
    RadioButton rb20OffMore;
    @BindView(R.id.rb30OffMore)
    RadioButton rb30OffMore;
    @BindView(R.id.rb50OffMore)
    RadioButton rb50OffMore;
    @BindView(R.id.rb50AED)
    RadioButton rb50AED;
    @BindView(R.id.rb50To100AED)
    RadioButton rb50To100AED;
    @BindView(R.id.rb100To300AED)
    RadioButton rb100To300AED;
    @BindView(R.id.rb300To700AED)
    RadioButton rb300To700AED;
    @BindView(R.id.rb700To1500AED)
    RadioButton rb700To1500AED;
    @BindView(R.id.rb1500To2500AED)
    RadioButton rb1500To2500AED;
    @BindView(R.id.rb2500ToAboveAED)
    RadioButton rb2500ToAboveAED;
    @BindView(R.id.rbLast7Days)
    RadioButton rbLast7Days;
    @BindView(R.id.rbLast30Days)
    RadioButton rbLast30Days;
    @BindView(R.id.rbLast90Days)
    RadioButton rbLast90Days;
    @BindView(R.id.rb4Up)
    RadioButton rb4Up;
    @BindView(R.id.rb3Up)
    RadioButton rb3Up;
    @BindView(R.id.rb2Up)
    RadioButton rb2Up;
    @BindView(R.id.rb1Up)
    RadioButton rb1Up;
    String from="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_filter);
        ButterKnife.bind(this);

        filterfinalCate = new HashSet<>();
        homecateList = new ArrayList<>();
        totalCategoryList = new ArrayList<>();
        jsonData = getIntent().getStringExtra("json");

         from = getIntent().getStringExtra("filter");

        if (from != null) {
            if (from.equalsIgnoreCase(AllProductShowFromCatFragment.class.getSimpleName())) {
                getWithoutCateListData(jsonData);
            } else {
                getData(jsonData);
            }
        }
        if (getIntent() != null) {

            String price = getIntent().getStringExtra("price");
            String newArrivalResult = getIntent().getStringExtra("new_arrival");
            String ratingResult = getIntent().getStringExtra("average_customer_review");
            String discountResult = getIntent().getStringExtra("discount");
            String departmentResult = getIntent().getStringExtra("department");
            setIntentData(price, newArrivalResult, ratingResult, discountResult, departmentResult);

        }


//        if (jsonData != null && !jsonData.equalsIgnoreCase("")) {
//            try{
//                JSONObject object = new JSONObject(jsonData);
//                if (object.getString("code").equalsIgnoreCase("200")) {
//                    JSONObject jsonObject = object.getJSONObject("success");
//                    //category array
//                     JSONArray cat_array = jsonObject.getJSONArray("category");
//                    if(cat_array != null && cat_array.length()>0) {
//
//                        for (int i = 0; i < cat_array.length(); i++) {
//                            JSONObject obj = cat_array.getJSONObject(i);
//                            JSONArray subCat_array = obj.getJSONArray("catlist");
//
//                            if(subCat_array == null ){
//
//                            }
//
//                        }}}
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        } else {
//            tvDepartment.setVisibility(View.VISIBLE);
//            rvDepartment.setVisibility(View.VISIBLE);
//            view.setVisibility(View.VISIBLE);
//        }
    }


    @OnClick({R.id.tvDepartment, R.id.tvAverageCustomerReview, R.id.tvNewArrival, R.id.tvPrice, R.id.tvDiscount, R.id.text_reset, R.id.text_done})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvDepartment:
                if (rvDepartment.getVisibility() == View.GONE)
                    rvDepartment.setVisibility(View.VISIBLE);
                else
                    rvDepartment.setVisibility(View.GONE);
                break;

            case R.id.tvAverageCustomerReview:
                if (rgAverageReview.getVisibility() == View.GONE)
                    rgAverageReview.setVisibility(View.VISIBLE);
                else
                    rgAverageReview.setVisibility(View.GONE);
                break;

            case R.id.tvNewArrival:
                if (rgNewArrival.getVisibility() == View.GONE)
                    rgNewArrival.setVisibility(View.VISIBLE);
                else
                    rgNewArrival.setVisibility(View.GONE);
                break;

            case R.id.tvPrice:
                if (rgPrice.getVisibility() == View.GONE)
                    rgPrice.setVisibility(View.VISIBLE);
                else
                    rgPrice.setVisibility(View.GONE);
                break;

            case R.id.tvDiscount:
                if (rgDiscount.getVisibility() == View.GONE)
                    rgDiscount.setVisibility(View.VISIBLE);
                else
                    rgDiscount.setVisibility(View.GONE);
                break;

            case R.id.text_reset:
                Intent intent = new Intent();
                intent.putExtra("department",new ArrayList<String>());
                intent.putExtra("average_customer_review", "");
                intent.putExtra("new_arrival", "");
                intent.putExtra("price", "");
                intent.putExtra("discount", "");
                intent.putExtra(BuyerSearchFragment.ISLOAD, true);
                setResult(RESULT_OK, intent);
                ProductFilterActivity.this.finish();
                break;
            case R.id.text_done:
                dispatchBuyerFrag();
                break;
        }
    }

    private void dispatchBuyerFrag() {
        Intent intent = new Intent();
        intent.putExtra("department", new ArrayList<String>(filterfinalCate));
        intent.putExtra("average_customer_review", getAverageReview());
        intent.putExtra("new_arrival", getNewArrival());
        intent.putExtra("price", getPrice());
        intent.putExtra("discount", getDiscount());
        intent.putExtra(BuyerSearchFragment.ISLOAD, true);
        setResult(RESULT_OK, intent);
        finish();
    }

//    private String getDepartment() {
//        switch(rgDepartment.getCheckedRadioButtonId()){
//            case  R.id.rbLatestProduct:
//                return rbLatestProduct.getText().toString();
//            case  R.id.rbHotDeals:
//                return rbHotDeals.getText().toString();
//            case  R.id.rbBestDeals:
//                return rbBestDeals.getText().toString();
//            default:
//                return "";
//        }
//    }

    private String getAverageReview() {
        switch (rgAverageReview.getCheckedRadioButtonId()) {
            case R.id.rb4Up:
                return "1";
            case R.id.rb3Up:
                return "2";
            case R.id.rb2Up:
                return "3";
            case R.id.rb1Up:
                return "4";
            default:
                return "";
        }
    }

    private String getNewArrival() {
        switch (rgNewArrival.getCheckedRadioButtonId()) {
            case R.id.rbLast7Days:
                return "1";
            case R.id.rbLast30Days:
                return "2";
            case R.id.rbLast90Days:
                return "3";
            default:
                return "";
        }
    }

    private String getPrice() {
        switch (rgPrice.getCheckedRadioButtonId()) {
            case R.id.rb50AED:
                return "1";
            case R.id.rb50To100AED:
                return "2";
            case R.id.rb100To300AED:
                return "3";
            case R.id.rb300To700AED:
                return "4";
            case R.id.rb700To1500AED:
                return "5";
            case R.id.rb1500To2500AED:
                return "6";
            case R.id.rb2500ToAboveAED:
                return "7";
            default:
                return "";
        }
    }

    private String getDiscount() {
        switch (rgDiscount.getCheckedRadioButtonId()) {
            case R.id.rb10OffMore:
                return "1";
            case R.id.rb20OffMore:
                return "2";
            case R.id.rb30OffMore:
                return "3";
            case R.id.rb50OffMore:
                return "4";
            default:
                return "";
        }
    }

    private void setIntentData(String price, String newArrivalResult, String ratingResult, String discountResult, String departmentResult) {
        if (from != null) {
            if (from.equalsIgnoreCase(AllProductShowFromCatFragment.class.getSimpleName())) {
                if (departmentResult != null && !departmentResult.equalsIgnoreCase("")) {
                    try {
                        for (int i = 0; i < cat_array.length(); i++) {
                            JSONObject objSub = cat_array.getJSONObject(i);
                            if(objSub.getString("category_id").equals(departmentResult)) {
                                objSub.put("isChecked", true);
                            }else {
                                objSub.put("isChecked", false);
                            }

                            productFiterNewAdapter.notifyDataSetChanged();
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            } else {
                if (departmentResult != null && !departmentResult.equalsIgnoreCase("")) {
                    ArrayList<String> dept_array = new ArrayList<>();
                    if(departmentResult.contains(",")){
                        String[] spilt_array = departmentResult.split(",");
                        for(int k=0;k<spilt_array.length;k++){
                            dept_array.add(spilt_array[k]);
                        }

                    }else {
                        dept_array.add(departmentResult);
                    }
                    try {
                        for (int i = 0; i < cat_array.length(); i++) {
                            JSONObject obj = cat_array.getJSONObject(i);
                            JSONArray subCat_array = obj.getJSONArray("catlist");
                            for (int j = 0; j < subCat_array.length(); j++) {
                                JSONObject objSub = subCat_array.getJSONObject(j);
                                for(int l=0;l<dept_array.size();l++){
                                    if(objSub.getString("category_id").equals(dept_array.get(l))){
                                        objSub.put("isChecked", true);
                                    }else {
                                        //objSub.put("isChecked", false);
                                    }
                                }


                            }
                            obj.put("catlist", subCat_array);
                        }

                        filterDepartmentAdapter.notifyDataSetChanged();

                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                }
            }


        if (discountResult != null && !discountResult.equalsIgnoreCase("")) {
            switch (discountResult) {
                case "1":
                    rb10OffMore.setChecked(true);
                    break;
                case "2":
                    rb20OffMore.setChecked(true);
                    break;
                case "3":
                    rb30OffMore.setChecked(true);
                    break;
                case "4":
                    rb50OffMore.setChecked(true);
                    break;

            }
        }
        if (price != null && !price.equalsIgnoreCase("")) {
            switch (price) {
                case "1":
                    rb50AED.setChecked(true);
                    break;
                case "2":
                    rb50To100AED.setChecked(true);
                    break;
                case "3":
                    rb100To300AED.setChecked(true);
                    break;
                case "4":
                    rb300To700AED.setChecked(true);
                    break;
                case "5":
                    rb700To1500AED.setChecked(true);
                    break;
                case "6":
                    rb1500To2500AED.setChecked(true);
                    break;
                case "7":
                    rb2500ToAboveAED.setChecked(true);
                    break;

            }
        }

        if (newArrivalResult != null && !newArrivalResult.equalsIgnoreCase("")) {
            switch (newArrivalResult) {
                case "1":
                    rbLast7Days.setChecked(true);
                    break;
                case "2":
                    rbLast30Days.setChecked(true);
                    break;
                case "3":
                    rbLast90Days.setChecked(true);
                    break;

            }
        }
        if (ratingResult != null && !ratingResult.equalsIgnoreCase("")) {
            switch (ratingResult) {
                case "1":
                    rb4Up.setChecked(true);
                    break;
                case "2":
                    rb3Up.setChecked(true);
                    break;
                case "3":
                    rb2Up.setChecked(true);
                    break;
                case "4":
                    rb1Up.setChecked(true);
                    break;

            }
        }

    }


    private void getWithoutCateListData(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            arrayCategory = new ArrayList<>();
            arraySortingMethods = new ArrayList<>();

            if (object.getString("code").equalsIgnoreCase("200")) {
                JSONObject jsonObject = object.getJSONObject("success");
                //category array
                cat_array = jsonObject.getJSONArray("category");
                if (cat_array != null && cat_array.length() > 0) {

                    for (int i = 0; i < cat_array.length(); i++) {
                        JSONObject objSub = cat_array.getJSONObject(i);
                        objSub.put("isChecked", false);
                        jsonObject.put("success", cat_array);

                    }

                    rvDepartment.setLayoutManager(new LinearLayoutManager(this));
                    productFiterNewAdapter = new ProductFiterNewAdapter(this, cat_array, ProductFilterActivity.this);
                    rvDepartment.setAdapter(productFiterNewAdapter);
                } else {
                    tvDepartment.setVisibility(View.GONE);
                    rvDepartment.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }


//                for (int i = 0; i < cat_array.length(); i++) {
//                    JSONObject obj = cat_array.getJSONObject(i);
//                    String homecategory = obj.getString("homecategory");
//                    homecateList.add(homecategory);
//                    JSONArray subCat_array = obj.getJSONArray("catlist");
//                    for(int j = 0; j<subCat_array.length(); j++) {
//                        JSONObject objSub = subCat_array.getJSONObject(j);
//                        NameID nameID = new NameID();
//                        nameID.setId(objSub.getString("category_id"));
//                        nameID.setName(objSub.getString("name"));
//                        //set to false first
//                        nameID.setIsselected(false);
//
//                        arrayCategory.add(nameID);
//                    }
//                    totalCategoryList.add(arrayCategory);
//                }


            } else {
                JSONObject obj = object.getJSONObject("error");
                CommonUtilFunctions.Error_Alert_Dialog(ProductFilterActivity.this, obj.getString("msg"));
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }


    }


    private void getData(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            arrayCategory = new ArrayList<>();
            arraySortingMethods = new ArrayList<>();

            if (object.getString("code").equalsIgnoreCase("200")) {
                JSONObject jsonObject = object.getJSONObject("success");
                //category array
                cat_array = jsonObject.getJSONArray("category");
                if (cat_array != null && cat_array.length() > 0) {

                    for (int i = 0; i < cat_array.length(); i++) {
                        JSONObject obj = cat_array.getJSONObject(i);
                        JSONArray subCat_array = obj.getJSONArray("catlist");
                        for (int j = 0; j < subCat_array.length(); j++) {
                            JSONObject objSub = subCat_array.getJSONObject(j);
                            objSub.put("isChecked", false);

                        }
                        obj.put("catlist", subCat_array);


                    }


                    rvDepartment.setLayoutManager(new LinearLayoutManager(this));
                    filterDepartmentAdapter = new FilterDepartmentCategoryadapter(this, cat_array, ProductFilterActivity.this);
                    rvDepartment.setAdapter(filterDepartmentAdapter);

                } else {
                    tvDepartment.setVisibility(View.GONE);
                    rvDepartment.setVisibility(View.GONE);
                    view.setVisibility(View.GONE);
                }


//                for (int i = 0; i < cat_array.length(); i++) {
//                    JSONObject obj = cat_array.getJSONObject(i);
//                    String homecategory = obj.getString("homecategory");
//                    homecateList.add(homecategory);
//                    JSONArray subCat_array = obj.getJSONArray("catlist");
//                    for(int j = 0; j<subCat_array.length(); j++) {
//                        JSONObject objSub = subCat_array.getJSONObject(j);
//                        NameID nameID = new NameID();
//                        nameID.setId(objSub.getString("category_id"));
//                        nameID.setName(objSub.getString("name"));
//                        //set to false first
//                        nameID.setIsselected(false);
//
//                        arrayCategory.add(nameID);
//                    }
//                    totalCategoryList.add(arrayCategory);
//                }


            } else {
                JSONObject obj = object.getJSONObject("error");
                CommonUtilFunctions.Error_Alert_Dialog(ProductFilterActivity.this, obj.getString("msg"));
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onSubItemClick(JSONArray findList, int position, int parentPosition) {

        try {
            for (int i = 0; i < findList.length(); i++) {
                JSONObject jsonObject = findList.getJSONObject(i);
                jsonObject.put("isChecked", false);

            }
            JSONObject jsonObject = findList.getJSONObject(position);
            jsonObject.put("isChecked", true);

            cat_array.getJSONObject(parentPosition).put("catlist", findList);
            filterDepartmentAdapter.notifyDataSetChanged();

            for (int i = 0; i < findList.length(); i++) {
                if (findList.getJSONObject(i).getBoolean("isChecked"))
                    filterfinalCate.add(findList.getJSONObject(i).getString("category_id"));
                else
                    filterfinalCate.remove(findList.getJSONObject(i).getString("category_id"));
            }

            Log.e("Ravi Test ", "Check  " + filterfinalCate);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("department", new ArrayList<String>(filterfinalCate));
        intent.putExtra("average_customer_review", getAverageReview());
        intent.putExtra("new_arrival", getNewArrival());
        intent.putExtra("price", getPrice());
        intent.putExtra("discount", getDiscount());
        intent.putExtra(BuyerSearchFragment.ISLOAD, false);
        setResult(BuyerSearchFragment.REQUEST_CODE, intent); // You can also send result without any data using setResult(int resultCode)
        super.onBackPressed();


    }

    @Override
    public void onInnerItemClick(JSONArray findJson, int position) {
        try {
            for (int i = 0; i < findJson.length(); i++) {
                JSONObject jsonObject = findJson.getJSONObject(i);
                jsonObject.put("isChecked", false);

            }
            JSONObject jsonObject = findJson.getJSONObject(position);
            jsonObject.put("isChecked", true);

            cat_array.getJSONObject(position).put("success", findJson);
            productFiterNewAdapter.notifyDataSetChanged();

            //filterfinalCate.add(findJson.getJSONObject(position).getString("category_id"));
            for (int i = 0; i < findJson.length(); i++) {
                if (cat_array.getJSONObject(i).getBoolean("isChecked"))
                    filterfinalCate.add(findJson.getJSONObject(i).getString("category_id"));
                else
                    filterfinalCate.remove(findJson.getJSONObject(i).getString("category_id"));
            }

            Log.e("Ravi Test ", "Check  " + filterfinalCate);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
