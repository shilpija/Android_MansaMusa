package com.freshhome;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.freshhome.AdapterClass.SubCategoryAdapter;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.SubCategory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SelectSubCategoriesActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back, image_done;
    ListView sub_categoryListview;
    ArrayList<SubCategory> arraySubCategories, arrayCategories;
    ArrayList<NameID> array_sub;
    private static final int REQUEST_SELECT_SUB_CATEGORY = 12;
    BottomSheetDialog dialog;
    TextView text_cancel;
    LinearLayout linear_done;
    SubCategoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_sub_categories);
        arraySubCategories = new ArrayList<>();
        arrayCategories = new ArrayList<>();
        array_sub = new ArrayList<>();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        image_done = (ImageView) findViewById(R.id.image_done);
        image_done.setOnClickListener(this);


        linear_done = (LinearLayout) findViewById(R.id.linear_done);
        linear_done.setOnClickListener(this);

        text_cancel = (TextView) findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);

        sub_categoryListview = (ListView) findViewById(R.id.sub_categoryListview);
        sub_categoryListview.setFocusable(false);

        arraySubCategories=getIntent().getParcelableArrayListExtra("sub_categories");

        adapter = new SubCategoryAdapter(SelectSubCategoriesActivity.this, arraySubCategories);
        sub_categoryListview.setAdapter(adapter);

//        try {
//            JSONObject jsonObj = new JSONObject(getIntent().getStringExtra("json_categories"));
//            getAllCategories(jsonObj);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        sub_categoryListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                array_sub = new ArrayList<>();
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                if (!cb.isChecked()) {
                    cb.setChecked(true);
                    arraySubCategories.get(position).setIsselected(true);
                    array_sub = arraySubCategories.get(position).getArrayList();
                    Intent i = new Intent(SelectSubCategoriesActivity.this, MultipleSubCategoryActivity.class);
                    i.putExtra("sub_categories", array_sub);
                    i.putExtra("categories_pos", position);
                    startActivityForResult(i, REQUEST_SELECT_SUB_CATEGORY);
                } else {
                    cb.setChecked(false);
                    arraySubCategories.get(position).setIsselected(false);
                }

            }
        });
    }

//    private void getAllCategories(JSONObject jsonObj) {
//        try {
//            JSONArray category_array = jsonObj.getJSONArray("data");
//            arraySubCategories = new ArrayList<>();
//            arrayCategories = new ArrayList<>();
//            for (int i = 0; i < category_array.length(); i++) {
//                JSONObject obj = category_array.getJSONObject(i);
//                NameID nameID = new NameID();
//                nameID.setId(obj.getString("sub_category"));
//                nameID.setName(obj.getString("category_name"));
//                nameID.setHasparents(obj.getString("has_parents"));
//                if (obj.has("sub_categories")) {
//                    arraySubCategories = new ArrayList<>();
//                    JSONArray jsonArray = obj.getJSONArray("sub_categories");
//                    for (int j = 0; j < jsonArray.length(); j++) {
//                        JSONObject jobj = jsonArray.getJSONObject(j);
//                        NameID nameID_i = new NameID();
//                        nameID_i.setId(jobj.getString("sub_category"));
//                        nameID_i.setName(jobj.getString("category_name"));
//                        nameID_i.setHasparents(jobj.getString("has_parents"));
//                        nameID_i.setIsselected(false);
//                        arraySubCategories.add(nameID_i);
//                    }
//                }
//                nameID.setArrayList(arraySubCategories);
//                arrayCategories.add(nameID);
//
//            }
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                SelectSubCategoriesActivity.this.finish();
                break;

            case R.id.linear_done:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("sub_categories", arraySubCategories);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;

            case R.id.text_cancel:
                SelectSubCategoriesActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SELECT_SUB_CATEGORY) {
            if (data != null && data.hasExtra("sub_categories")) {
                //replace same array in main array to keep the track of selected sub categories
                array_sub = new ArrayList<>();
                array_sub = data.getParcelableArrayListExtra("sub_categories");
                Log.e("id", String.valueOf(data.getIntExtra("categories_pos", 0)));
                if (arraySubCategories.size() >= data.getIntExtra("categories_pos", 0)) {
                    arraySubCategories.get(data.getIntExtra("categories_pos", 0)).setArrayList(array_sub);
                }
                if(adapter!=null) {
                    adapter = new SubCategoryAdapter(SelectSubCategoriesActivity.this, arraySubCategories);
                    sub_categoryListview.setAdapter(adapter);
                }
            }

        }
    }
}
