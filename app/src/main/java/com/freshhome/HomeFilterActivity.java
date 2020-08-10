package com.freshhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.datamodel.NameID;
import com.freshhome.fragments.BuyerSearchFragment;
import com.freshhome.fragments.HomeFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFilterActivity extends AppCompatActivity implements View.OnClickListener {
    TextView text_reset, text_done;
    String jsonData = "";
    FlowLayout flow_layout_category, flow_layout_cuisines, flow_layout_meals;
    ApiInterface apiInterface;
    ArrayList<NameID> arrayCategory, arrayCuisines, arrayCity, arrayMeals, arraySortingMethods;
    //    String[] sortingArray;
    String selectedCuisines = "", selectedCategories = "", selectedMeals = "";
private String homecategoryID="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_filter);
        arrayCategory = new ArrayList<>();
        arrayCuisines = new ArrayList<>();
        arrayCity = new ArrayList<>();
        arrayMeals = new ArrayList<>();
        arraySortingMethods = new ArrayList<>();

        jsonData = getIntent().getStringExtra("json");
        homecategoryID = getIntent().getStringExtra("category");

        apiInterface = ApiClient.getInstance().getClient();

        text_reset = (TextView) findViewById(R.id.text_reset);
        text_reset.setOnClickListener(this);

        text_done = (TextView) findViewById(R.id.text_done);
        text_done.setOnClickListener(this);

        flow_layout_category = (FlowLayout) findViewById(R.id.flow_layout_category);
        flow_layout_cuisines = (FlowLayout) findViewById(R.id.flow_layout_cuisines);
        flow_layout_meals = (FlowLayout) findViewById(R.id.flow_layout_meals);

        selectedMeals = getIntent().getStringExtra(BuyerSearchFragment.MEALS);
        selectedCategories = getIntent().getStringExtra(BuyerSearchFragment.CATEGORY);
        selectedCuisines = getIntent().getStringExtra(BuyerSearchFragment.CUISINE);

        setupMeals(flow_layout_meals);
        if (!jsonData.equalsIgnoreCase("")) {
            getData(jsonData);
        } else {
        }
    }

    private void setupMeals(final FlowLayout flow_layout) {
        flow_layout.removeAllViews();
        for (int i = 0; i < arrayMeals.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_cate_cuis, null);
            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);
            final TextView textview = (TextView) view.findViewById(R.id.textview);
            textview.setText(arrayMeals.get(i).getName());
            final CheckBox check_remove = (CheckBox) view.findViewById(R.id.check_remove);
            check_remove.setTag(arrayMeals.get(i).getName());
            check_remove.setChecked(arrayMeals.get(i).isIsselected());
            arrayMeals.get(i).setCheckBox(check_remove);
            check_remove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int j = 0; j < arrayMeals.size(); j++) {
                        if (arrayMeals.get(j).getCheckBox().equals(buttonView)) {
                            arrayMeals.get(j).setIsselected(isChecked);
                        }
                    }
                }
            });

            flow_layout.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_done:

                selectedMeals = getSelectedID(arrayMeals);
                selectedCategories = getSelectedID(arrayCategory);
                selectedCuisines = getSelectedID(arrayCuisines);

                Intent intent = new Intent();
                intent.putExtra(BuyerSearchFragment.MEALS, selectedMeals);  //..selectedMeals-- use this
                intent.putExtra(BuyerSearchFragment.CATEGORY, homecategoryID);
                intent.putExtra(BuyerSearchFragment.CUISINE, selectedCuisines);
                intent.putExtra(BuyerSearchFragment.ISLOAD,true);
                setResult(BuyerSearchFragment.REQUEST_CODE, intent); // You can also send result without any data using setResult(int resultCode)
                HomeFilterActivity.this.finish();

                break;

            case R.id.text_reset:
                Intent intent1 = new Intent();
                intent1.putExtra(BuyerSearchFragment.MEALS, "");
                intent1.putExtra(BuyerSearchFragment.CATEGORY, "");
                intent1.putExtra(BuyerSearchFragment.CUISINE, "");
                intent1.putExtra(BuyerSearchFragment.ISLOAD,true);
                setResult(BuyerSearchFragment.REQUEST_CODE, intent1);
                HomeFilterActivity.this.finish();
                break;
        }
    }

    private String getSelectedID(ArrayList<NameID> array) {
        String selectedID = "";
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).isIsselected()) {
                if (selectedID.equalsIgnoreCase("")) {
                    selectedID = array.get(i).getId();
                } else {
                    selectedID = selectedID + "," + array.get(i).getId();
                }
            }
        }
        return selectedID;

    }

    private void getData(String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            arrayCategory = new ArrayList<>();
            arrayCuisines = new ArrayList<>();
            arrayCity = new ArrayList<>();
            arrayMeals = new ArrayList<>();
            arraySortingMethods = new ArrayList<>();

            if (object.getString("code").equalsIgnoreCase("200")) {
                JSONObject jsonObject = object.getJSONObject("success");
                //category array
                JSONArray cat_array = jsonObject.getJSONArray("category");
                for (int i = 0; i < cat_array.length(); i++) {
                    JSONObject obj = cat_array.getJSONObject(i);
                    NameID nameID = new NameID();
                    nameID.setId(obj.getString("category_id"));
                    nameID.setName(obj.getString("category_name"));
                    //set to false first
                    nameID.setIsselected(false);
                    if (!selectedCategories.equalsIgnoreCase("")) {
                        String[] stringarray = selectedCategories.split(",");
                        for (int j = 0; j < stringarray.length; j++) {
                            if (stringarray[j].equalsIgnoreCase(obj.getString("category_id"))) {
                                nameID.setIsselected(true);
                            }
                        }
                    } else {
                        nameID.setIsselected(false);
                    }
                    arrayCategory.add(nameID);
                }


                //cuisines array
                JSONArray cuis_array = jsonObject.getJSONArray("cuisine");
                for (int i = 0; i < cuis_array.length(); i++) {
                    JSONObject obj = cuis_array.getJSONObject(i);
                    NameID nameID = new NameID();
                    nameID.setId(obj.getString("cuisine_id"));
                    nameID.setName(obj.getString("cuisine_name"));
                    //set to false first
                    nameID.setIsselected(false);
                    if (!selectedCuisines.equalsIgnoreCase("")) {
                        String[] stringarray = selectedCuisines.split(",");
                        for (int j = 0; j < stringarray.length; j++) {
                            if (stringarray[j].equalsIgnoreCase(obj.getString("cuisine_id"))) {
                                nameID.setIsselected(true);
                            }
                        }
                    } else {
                        nameID.setIsselected(false);
                    }
                    arrayCuisines.add(nameID);
                }


                //city array
                JSONArray city_array = jsonObject.getJSONArray("city");
                for (int i = 0; i < city_array.length(); i++) {
                    JSONObject obj = city_array.getJSONObject(i);
                    NameID nameID = new NameID();
                    nameID.setId(obj.getString("city_id"));
                    nameID.setName(obj.getString("city_name"));
                    arrayCity.add(nameID);
                }

                //meal array
                JSONArray meal_array = jsonObject.getJSONArray("meal");
                for (int i = 0; i < meal_array.length(); i++) {
                    JSONObject obj = meal_array.getJSONObject(i);
                    NameID nameID = new NameID();
                    nameID.setId(obj.getString("meal_id"));
                    nameID.setName(obj.getString("meal_name"));
                    //set to false first
                    nameID.setIsselected(false);
                    if (!selectedMeals.equalsIgnoreCase("")) {
                        String[] stringarrMeal = selectedMeals.split(",");
                        for (int j = 0; j < stringarrMeal.length; j++) {
                            if (stringarrMeal[j].equalsIgnoreCase(obj.getString("meal_id"))) {
                                nameID.setIsselected(true);
                            }
                        }
                    } else {
                        nameID.setIsselected(false);
                    }

                    arrayMeals.add(nameID);
                }

                setCategory();
                setCuisines();
                setupMeals(flow_layout_meals);

            } else {
                JSONObject obj = object.getJSONObject("error");
                CommonUtilFunctions.Error_Alert_Dialog(HomeFilterActivity.this, obj.getString("msg"));
            }

        } catch (
                JSONException e)

        {
            e.printStackTrace();
        }


    }

    private void setCuisines() {
        flow_layout_cuisines.removeAllViews();
        for (int i = 0; i < arrayCuisines.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_cate_cuis, null);
            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);
            final CheckBox check_remove = (CheckBox) view.findViewById(R.id.check_remove);
            check_remove.setTag(arrayCuisines.get(i).getId());
            check_remove.setChecked(arrayCuisines.get(i).isIsselected());
            arrayCuisines.get(i).setCheckBox(check_remove);
            check_remove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int j = 0; j < arrayCuisines.size(); j++) {
                        if (arrayCuisines.get(j).getCheckBox().equals(buttonView)) {
                            arrayCuisines.get(j).setIsselected(isChecked);
                        }
                    }
                }
            });
            TextView textview = (TextView) view.findViewById(R.id.textview);
            textview.setText(arrayCuisines.get(i).getName());
            flow_layout_cuisines.addView(view);
        }
    }

    private void setCategory() {
        flow_layout_category.removeAllViews();
        for (int i = 0; i < arrayCategory.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_cate_cuis, null);
            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);
            final CheckBox check_remove = (CheckBox) view.findViewById(R.id.check_remove);
            check_remove.setTag(arrayCategory.get(i).getId());
            check_remove.setChecked(arrayCategory.get(i).isIsselected());
            arrayCategory.get(i).setCheckBox(check_remove);
            check_remove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int j = 0; j < arrayCategory.size(); j++) {
                        if (arrayCategory.get(j).getCheckBox().equals(buttonView)) {
                            arrayCategory.get(j).setIsselected(isChecked);
                        }
                    }
                }
            });
            TextView textview = (TextView) view.findViewById(R.id.textview);
            textview.setText(arrayCategory.get(i).getName());
            flow_layout_category.addView(view);
        }
    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra(BuyerSearchFragment.MEALS, selectedMeals);
        intent.putExtra(BuyerSearchFragment.CATEGORY, selectedCategories);
        intent.putExtra(BuyerSearchFragment.CUISINE, selectedCuisines);
        intent.putExtra(BuyerSearchFragment.ISLOAD,false);
        setResult(BuyerSearchFragment.REQUEST_CODE, intent); // You can also send result without any data using setResult(int resultCode)
        super.onBackPressed();


    }

}
