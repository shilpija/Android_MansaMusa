package com.freshhome;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.SubCategory;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMenuActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    private static final int REQUEST_SELECT_SUB_CATEGORY = 12;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int RESULT_LOAD_IMAGE = 1;
    ImageView image_addcuisine, image_addcategory, image_back;
    EditText edit_dishname,edit_dishname_arabic, edit_dishdescription,edit_dishdescription_arabic, edit_dishprice, edit_discount;
    LinearLayout linear_submit, linear_discount;
    CircleImageView circle_image;
    ApiInterface apiInterface;
    FlowLayout flow_layout_cuisines, flow_layout_category;
    Uri mCapturedImageURI;
    String fileName = "";
    int PERMISSION_ALL = 1;
    String image_namewith_path = "", selected_cuisines = "",
            selected_categories = "", dish_id = "", prep_time = "", selected_categories_name = "",selected_categories_fee = "", selected_sub_categories_id = "", selected_brand = "";
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    Spinner mealSpinner, serveSpinner, spinner_item_pretime, FoodBrands, main_category,sub_category;
    ArrayList<NameID> arrayCategory, selected_arrayCategory, arrayCuisines, selected_arrayCuisines,
            arrayCity, arrayMeals, arraySortingMethods, selected_arrayMeals, array_maincategory,array_subcategory, array_brands;
    String[] mealSArray, serveSArray, brandsSArray;
    TextView heading, text_price_breakdown, text_final_price, text_discount_breakdown, text_sub_categories,tvMainCategory,tvCategory;
    boolean isedit = false;
    UserSessionManager sessionManager;
    CheckBox checkbox_discount;
    ArrayList<SubCategory> array_Sub_categories;
    private String payableAmt = "";
    LinkedList<String> mainCategoryArray;
    LinkedList<String> subCategoryArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_menu2);
        sessionManager = new UserSessionManager(AddMenuActivity.this);
        apiInterface = ApiClient.getInstance().getClient();

        linear_discount = (LinearLayout) findViewById(R.id.linear_discount);
        checkbox_discount = (CheckBox) findViewById(R.id.checkbox_discount);
        checkbox_discount.setOnCheckedChangeListener(this);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        text_discount_breakdown = (TextView) findViewById(R.id.text_discount_breakdown);
        text_price_breakdown = (TextView) findViewById(R.id.text_price_breakdown);
        text_final_price = (TextView) findViewById(R.id.text_final_price);
        text_sub_categories = (TextView) findViewById(R.id.text_sub_categories);
        tvMainCategory = (TextView) findViewById(R.id.tvMainCategory);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        image_addcuisine = (ImageView) findViewById(R.id.image_addcuisine);
        image_addcuisine.setOnClickListener(this);
        tvCategory.setOnClickListener(this);
        tvMainCategory.setOnClickListener(this);
        selected_arrayCuisines = new ArrayList<>();

        if(getIntent().hasExtra("collected_amount")){
            text_final_price.setText(getIntent().getStringExtra("collected_amount"));
        }

        heading = (TextView) findViewById(R.id.heading);
        if (getIntent().hasExtra("dish_id")) {
            isedit = true;
            heading.setText("Edit Menu Item");
            dish_id = getIntent().getStringExtra("dish_id");
        } else {
            isedit = false;
            heading.setText("Add Menu");
        }

        if (getIntent().hasExtra("dcuisnines")) {
            selected_arrayCuisines = (ArrayList<NameID>) getIntent().getSerializableExtra("dcuisnines");
        }

        image_addcategory = (ImageView) findViewById(R.id.image_addcategory);
        image_addcategory.setOnClickListener(this);
        selected_arrayCategory = new ArrayList<>();
        if (getIntent().hasExtra("dcategory")) {
            selected_arrayCategory = (ArrayList<NameID>) getIntent().getSerializableExtra("dcategory");
        }

        if (getIntent().hasExtra("mainCategory")) {
            ArrayList<NameID> mainCategory = (ArrayList<NameID>) getIntent().getSerializableExtra("mainCategory");
            if(mainCategory!=null && !mainCategory.isEmpty())
                tvMainCategory.setText(mainCategory.get(0).getName());
        }

        if (getIntent().hasExtra("subCategory")) {
            ArrayList<NameID> subCategory = (ArrayList<NameID>) getIntent().getSerializableExtra("subCategory");
            if(subCategory!=null && !subCategory.isEmpty()){
                tvCategory.setText(subCategory.get(0).getName());
                selected_sub_categories_id = subCategory.get(0).getId();
                //tractions charges
                sessionManager.saveCharges("5",
                        subCategory.get(0).getFee(),
                        "3");
            }
        }

        edit_discount = (EditText) findViewById(R.id.edit_discount);
        edit_dishname = (EditText) findViewById(R.id.edit_dishname);
        edit_dishname_arabic = (EditText) findViewById(R.id.edit_dishname_arabic);
        edit_dishname.setText((CommonMethods.checkNull(getIntent().getStringExtra("dname"))));
        edit_dishdescription = (EditText) findViewById(R.id.edit_dishdescription);
        edit_dishdescription_arabic = (EditText) findViewById(R.id.edit_dishdescription_arabic);
        edit_dishdescription.setText((CommonMethods.checkNull(getIntent().getStringExtra("ddescription"))));
        edit_dishprice = (EditText) findViewById(R.id.edit_dishprice);
        edit_dishprice.addTextChangedListener(this);
        edit_dishprice.setText((CommonMethods.checkNull(getIntent().getStringExtra("dprice"))));
        setPricewithBreakDown((CommonMethods.checkNull(getIntent().getStringExtra("dprice"))));
        spinner_item_pretime = (Spinner) findViewById(R.id.spinner_item_pretime);
        prep_time = CommonMethods.checkNull(getIntent().getStringExtra("dsince"));
//        spinner_item_pretime.setText(CommonMethods.checkNull(getIntent().getStringExtra("dsince")));
        setUpPrepTime();
        circle_image = (CircleImageView) findViewById(R.id.circle_image);
        if (!getIntent().getStringExtra("dimage").equalsIgnoreCase("")) {
            Picasso.get().load(getIntent().getStringExtra("dimage")).into(circle_image);
        }
        circle_image.setOnClickListener(this);

        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);

        flow_layout_cuisines = (FlowLayout) findViewById(R.id.flow_layout_cuisines);
        flow_layout_category = (FlowLayout) findViewById(R.id.flow_layout_category);

        mealSpinner = (Spinner) findViewById(R.id.mealSpinner);
        mealSpinner.setOnItemSelectedListener(this);

        selected_arrayMeals = new ArrayList<>();
        if (getIntent().hasExtra("d_meal")) {
            selected_arrayMeals = (ArrayList<NameID>) getIntent().getSerializableExtra("d_meal");
        }

        serveSArray = getResources().getStringArray(R.array.serving);
        serveSpinner = (Spinner) findViewById(R.id.serveSpinner);
        serveSpinner.setOnItemSelectedListener(this);
        serveSpinner.setAdapter(new ArrayAdapter<String>(this, R.layout.layout_spinner_sales, serveSArray));
        if (!getIntent().getStringExtra("dish_serve").equalsIgnoreCase("")) {
            for (int i = 0; i < serveSArray.length; i++) {
                if (serveSArray[i].equalsIgnoreCase(getIntent().getStringExtra("dish_serve"))) {
                    serveSpinner.setSelection(i);
                }
            }
        }

        if (getIntent().hasExtra("discount")) {
            String discount = getIntent().getStringExtra("discount");
            if(discount!=null && !discount.isEmpty()){
                checkbox_discount.setChecked(true);
                if(discount.equalsIgnoreCase("null")){
                    edit_discount.setText("0");
                }else {
                    edit_discount.setText(discount);
                }
            }
        }

        //if open camere or notl
        requestpermission(false);

        array_brands = new ArrayList<>();
        array_maincategory = new ArrayList<>();
        array_subcategory = new ArrayList<>();
        FoodBrands = (Spinner) findViewById(R.id.FoodBrands);
        main_category = (Spinner) findViewById(R.id.main_category);
        sub_category = (Spinner) findViewById(R.id.sub_category);
        array_Sub_categories = new ArrayList<>();
        if (CommonMethods.checkConnection()) {
            getMainCategories();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.internetconnection));
        }

        if (CommonMethods.checkConnection()) {
            getFilterdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.internetconnection));
        }

        edit_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_dishprice.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_proprice));
                    edit_discount.setText("");
                } else {
                    if (s.toString().equalsIgnoreCase("")) {
                        text_discount_breakdown.setVisibility(View.GONE);
                    } else {
                        text_discount_breakdown.setVisibility(View.VISIBLE);
                        CommonMethods.DiscountedPrice(AddMenuActivity.this, s.toString(), text_discount_breakdown, edit_dishprice.getText().toString());



                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (edit_discount.getText().toString().trim().equalsIgnoreCase("0") || edit_discount.getText().toString().trim().equalsIgnoreCase("")) {
                        text_final_price.setText(payableAmt);
                    }else {
                        String disAmt = text_discount_breakdown.getText().toString();
//                        String finalDisAmt = disAmt.split("\\s")[9];
//                        text_final_price.setText("");
//                        text_final_price.setText(finalDisAmt);

                        String raw_discoutn = edit_discount.getText().toString().replaceAll("[^0-9-.]", "").toString();
                        if (!raw_discoutn.toString().equals("")) {
                            double actul_price = Double.parseDouble(payableAmt);
                            double discount_i = Double.parseDouble(raw_discoutn);
                            double price = (actul_price / 100.0f) * discount_i;
                            double final_price = actul_price - price;

                        text_final_price.setText(new DecimalFormat("##.##").format(final_price));
                        //text_final_price.setText("" + final_price);
                            //textView.setTextColor(con.getResources().getColor(R.color.app_color_orange));
                        } else {
           // Toast.makeText(con, "Amount cannot be empty", Toast.LENGTH_SHORT).show();

                        }



                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        main_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    return;
                tvMainCategory.setText(parent.getItemAtPosition(position).toString());
                //clear selected sub categories
                selected_sub_categories_id = "";
                text_sub_categories.setText("");
                if (CommonMethods.checkConnection()) {
                    getSubCategories(array_maincategory.get(position).getId());
                    //Log.e("Andy "," find "+array_maincategory.get(position).getFee());

                    //tractions charges
//                    sessionManager.saveCharges("5",
//                            array_maincategory.get(position).getFee(),
//                            "3");

                }else {
                    CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.internetconnection));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        sub_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0)
                    return;
                tvCategory.setText(parent.getItemAtPosition(position).toString());
                //clear selected sub categories
                selected_sub_categories_id = "";
                text_sub_categories.setText("");
                if (CommonMethods.checkConnection()) {
                    //getSubCategories(array_subcategory.get(position).getId());
                    Log.e("Andy "," find "+array_subcategory.get(position).getFee());

                    //tractions charges
                    sessionManager.saveCharges("5",
                            array_subcategory.get(position).getFee(),
                            "3");
                    selected_sub_categories_id = array_subcategory.get(position).getId();

                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.internetconnection));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FoodBrands.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_brand = array_brands.get(position).getId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setUpPrepTime() {
        int selected = 0;
        List<String> countArray = new ArrayList<>();
        for (int i = 0; i < 59; i++) {
            if (i >= 9) {
                countArray.add("00:" + (i + 1));
            } else {
                countArray.add("00:" + "0" + (i + 1));
            }
        }
        Log.e("count", countArray.toString());
        Collections.reverse(countArray);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(AddMenuActivity.this, R.layout.layout_spinner_text_bg_transparent, countArray);
        spinner_item_pretime.setAdapter(adapter);

        for (int j = 0; j < countArray.size(); j++) {
            if (countArray.get(j).equalsIgnoreCase(prep_time)) {
                spinner_item_pretime.setSelection(j);
            }
        }

    }

    private void checkPreselectedCategory() {
        if (selected_arrayCategory.size() != 0) {
            for (int i = 0; i < arrayCategory.size(); i++) {
                for (int j = 0; j < selected_arrayCategory.size(); j++) {
                    if (selected_arrayCategory.get(j).getId().equalsIgnoreCase(arrayCategory.get(i).getId())) {
                        arrayCategory.get(i).setIsselected(true);
                    }
                }
            }
            add_categories_tomain();
        }


    }

    private void checkPreselecteCuisines() {
        if (selected_arrayCuisines.size() != 0) {
            for (int i = 0; i < arrayCuisines.size(); i++) {
                for (int j = 0; j < selected_arrayCuisines.size(); j++) {
                    if (selected_arrayCuisines.get(j).getId().equalsIgnoreCase(arrayCuisines.get(i).getId())) {
                        arrayCuisines.get(i).setIsselected(true);
                    }
                }
            }
            setcuisines_tomain();
        }


    }

    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(AddMenuActivity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(AddMenuActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog();
            }
        }
    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            fileName = System.currentTimeMillis() + ".png";
            File dir = new File(Environment.getExternalStorageDirectory() + "/FreshHome");
            File output = new File(dir, fileName);
            if (Build.VERSION.SDK_INT >= 24) {
                mCapturedImageURI = FileProvider.getUriForFile(AddMenuActivity.this, getApplicationContext().getPackageName() + ".fileprovider", output);
            } else {
                mCapturedImageURI = Uri.fromFile(output);
            }
//            mCapturedImageURI = Uri.fromFile(output);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }


    private void reload_screen() {
        AddMenuActivity.this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_addcuisine:
                show_add_dialogcuisines();
                break;


            case R.id.image_addcategory:
//                show_add_dialog_categories();
                Intent i = new Intent(AddMenuActivity.this, SelectSubCategoriesActivity.class);
                i.putExtra("sub_categories", array_Sub_categories);
                i.putExtra("json_categories", "");
                startActivityForResult(i, REQUEST_SELECT_SUB_CATEGORY);
                break;

            case R.id.circle_image:
                requestpermission(true);
                break;


            case R.id.linear_submit:
                if (sessionManager.isLoggedIn()) {
                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        if (edit_dishname.getText().toString().trim().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_dishname));
                        } else if (edit_dishdescription.getText().toString().trim().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_dishdescription));
                        } else if (edit_dishprice.getText().toString().trim().equalsIgnoreCase("")) {
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_dishprice));
//                        } else if (spinner_item_pretime.getSelectedItem().toString().equalsIgnoreCase("")) {
//                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_dishtime));
//                        } else if (selected_cuisines.equalsIgnoreCase("")) {
//                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_dishcuisine));
//                        } else if (selected_sub_categories_id.equalsIgnoreCase("")) {
//                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_dishcategory));
                        } else {
                            if (CommonMethods.checkConnection()) {
                                if (isedit) {
                                    if (image_namewith_path.equalsIgnoreCase("")) {
                                        UpdateMenuwithoutImage();
                                    } else {
                                        UpdateMenu();
                                    }
                                } else {
                                    if (image_namewith_path.equalsIgnoreCase("")) {
                                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.enter_dishimage));
                                    } else {
                                        addmenu();
                                    }
                                }
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.internetconnection));
                            }
                        }
                    } else {
                        CommonMethods.show_buy_plan(AddMenuActivity.this);
                    }
                } else {
                    CommonMethods.ShowLoginDialog(AddMenuActivity.this);
                }
                break;

            case R.id.image_back:
                AddMenuActivity.this.finish();
                break;

            case R.id.tvMainCategory:
                main_category.performClick();
                break;

            case R.id.tvCategory:
                sub_category.performClick();
                break;

        }
    }

    //NOT IN USE NOW
    private void show_add_dialog_categories() {
        final Dialog dialog = new Dialog(AddMenuActivity.this);
        dialog.setContentView(R.layout.layout_add_cuisine_category_dialog);
        TextView heading = (TextView) dialog.findViewById(R.id.heading);
        heading.setText(getResources().getString(R.string.addcategory));
        TextView text_done = (TextView) dialog.findViewById(R.id.text_done);
//        text_done.setVisibility(View.INVISIBLE);
        final FlowLayout flow_layout = (FlowLayout) dialog.findViewById(R.id.flow_layout);

        text_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //selected save
                CommonMethods.hideSoftKeyboard(AddMenuActivity.this);
                add_categories_tomain();
            }
        });
        setcategories(flow_layout, text_done);
        dialog.show();
    }

    //NOT IN USE NOW
    private void add_categories_tomain() {
        flow_layout_category.removeAllViews();
        selected_categories = "";
        for (int i = 0; i < arrayCategory.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_menucategories, null);
            TextView textview = (TextView) view.findViewById(R.id.textview);
            if (arrayCategory.get(i).isIsselected()) {
                if (selected_categories.equalsIgnoreCase("")) {
                    selected_categories = arrayCategory.get(i).getId();
                } else {
                    selected_categories = selected_categories + "," + arrayCategory.get(i).getId();
                }
                Log.e("category", selected_categories);
                textview.setText(arrayCategory.get(i).getName());
                flow_layout_category.addView(view);
            }
        }
    }

    private void setcategories(final FlowLayout flow_layout, TextView text_done) {
        flow_layout.removeAllViews();
        for (int i = 0; i < arrayCategory.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_cate_cuis, null);
            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);
            final CheckBox check_remove = (CheckBox) view.findViewById(R.id.check_remove);
            arrayCategory.get(i).setCheckBox(check_remove);
            check_remove.setChecked(arrayCategory.get(i).isIsselected());
            check_remove.setTag(arrayCategory.get(i));
            final int finalI = i;
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
            flow_layout.addView(view);
        }
    }

    private void show_add_dialogcuisines() {
        final Dialog dialog = new Dialog(AddMenuActivity.this);
        dialog.setContentView(R.layout.layout_add_cuisine_category_dialog);
        TextView heading = (TextView) dialog.findViewById(R.id.heading);
        TextView text_done = (TextView) dialog.findViewById(R.id.text_done);
        final FlowLayout flow_layout = (FlowLayout) dialog.findViewById(R.id.flow_layout);

        text_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                //selected save
                CommonMethods.hideSoftKeyboard(AddMenuActivity.this);
                setcuisines_tomain();
            }
        });
        heading.setText(getResources().getString(R.string.addcuisine));
        setcuisines(flow_layout);
        dialog.show();
    }

    private void setcuisines_tomain() {
        flow_layout_cuisines.removeAllViews();
        selected_cuisines = "";
        for (int i = 0; i < arrayCuisines.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_menucategories, null);
            TextView textview = (TextView) view.findViewById(R.id.textview);
            if (arrayCuisines.get(i).isIsselected()) {
                if (selected_cuisines.equalsIgnoreCase("")) {
                    selected_cuisines = arrayCuisines.get(i).getId();
                } else {
                    selected_cuisines = selected_cuisines + "," + arrayCuisines.get(i).getId();
                }
                Log.e("category", selected_cuisines);
                textview.setText(arrayCuisines.get(i).getName());
                flow_layout_cuisines.addView(view);
            }

        }
    }

    private void setcuisines(final FlowLayout flow_layout) {
        flow_layout.removeAllViews();
        for (int i = 0; i < arrayCuisines.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_cate_cuis, null);
            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);
            final CheckBox check_remove = (CheckBox) view.findViewById(R.id.check_remove);
            arrayCuisines.get(i).setCheckBox(check_remove);
            check_remove.setChecked(arrayCuisines.get(i).isIsselected());
            check_remove.setTag(arrayCuisines.get(i));
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
            flow_layout.addView(view);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.a
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission
                    if (ActivityCompat.shouldShowRequestPermissionRationale(AddMenuActivity.this, Manifest.permission.CAMERA)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(AddMenuActivity.this, "Please allow all permission to use all funtionalities in app.");
                    }
                }
            }
            return;
        }

        // other 'case' lines to check for other
        // permissions this app might request.
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResult(AddMenuActivity.this, data,
                            mCapturedImageURI, false, fileName, circle_image);
                    Log.i("image", image_namewith_path);
                }
                break;
            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview(AddMenuActivity.this, data,
                            false, circle_image);
                    Log.i("image", image_namewith_path);
                }
                break;


            case REQUEST_SELECT_SUB_CATEGORY:
                String selected_categories = "";
                selected_sub_categories_id = "";
                if (data != null && data.hasExtra("sub_categories")) {
                    array_Sub_categories = new ArrayList<>();
                    array_Sub_categories = data.getParcelableArrayListExtra("sub_categories");
                    for (int i = 0; i < array_Sub_categories.size(); i++) {
                        if (array_Sub_categories.get(i).isIsselected()) {
                            for (int j = 0; j < array_Sub_categories.get(i).getArrayList().size(); j++) {
                                if (array_Sub_categories.get(i).getArrayList().get(j).isIsselected()) {
                                    if (selected_sub_categories_id.equalsIgnoreCase("")) {
                                        selected_sub_categories_id = array_Sub_categories.get(i).getArrayList().get(j).getId();
                                        selected_categories_name = array_Sub_categories.get(i).getArrayList().get(j).getName();
                                        selected_categories_fee = array_Sub_categories.get(i).getArrayList().get(j).getFee();
                                    } else {
                                        selected_sub_categories_id = selected_sub_categories_id + "," + array_Sub_categories.get(i).getArrayList().get(j).getId();
                                        selected_categories_name = selected_categories_name + ", " + array_Sub_categories.get(i).getArrayList().get(j).getName();
                                        selected_categories_fee = selected_categories_fee+","+array_Sub_categories.get(i).getArrayList().get(j).getFee();
                                    }
                                }
                            }

                            if (selected_categories.equalsIgnoreCase("")) {
                                //selected_categories = array_Sub_categories.get(i).getName() + ", " + selected_categories_name;
                                selected_categories = selected_categories_name + ", ";
                            } else {
                                selected_categories = selected_categories + "," + array_Sub_categories.get(i).getName() + ", " + selected_categories_name;

                            }

                            //to add main category in the  selected cate string
                            if (selected_sub_categories_id.equalsIgnoreCase("")) {
                                selected_sub_categories_id = array_Sub_categories.get(i).getId();
                            } else {
                                //selected_sub_categories_id = selected_sub_categories_id + "," + array_Sub_categories.get(i).getId();
                                selected_sub_categories_id = selected_sub_categories_id + ",";
                            }
                        }
                    }
                    text_sub_categories.setText(selected_categories);
                    String[] separated = selected_sub_categories_id.split(",");
                    selected_sub_categories_id = TextUtils.join(",", separated);

                    //Log.e("selectedFee", selected_categories_fee);
                    if (!selected_categories.equalsIgnoreCase("")) {
                        //get brands related to select categories
                        if (CommonMethods.checkConnection()) {
                            //getBrands(selected_sub_categories_id);
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.internetconnection));
                        }
                    }
                }
                break;

        }
    }

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(AddMenuActivity.this);
        dialog_img.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_img.setContentView(R.layout.dialog_for_menuimage);
        dialog_img.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_img.dismiss();
                        activeGallery();

                    }
                });
        dialog_img.findViewById(R.id.btnTakePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_img.dismiss();
                        activeTakePhoto();
                    }
                });

        // show dialog on screen
        dialog_img.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (view.getId()) {

            case R.id.mealSpinner:

                break;

            case R.id.serveSpinner:

                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setPricewithBreakDown(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            linear_discount.setVisibility(View.VISIBLE);
            edit_discount.requestFocus();
        } else {
            linear_discount.setVisibility(View.GONE);
            edit_discount.setText("");
        }
    }

    private void setPricewithBreakDown(String s) {
        //TODO TRANSACTION CHARGES 2.5 AND FRESSHOMME FEE 10% VAT 10%.....(FreeHome free dynamic.. not fixed)
        if (s.toString().equalsIgnoreCase("")) {
            text_price_breakdown.setVisibility(View.GONE);
            text_final_price.setText("");
        } else {

            text_price_breakdown.setVisibility(View.VISIBLE);
            CommonMethods.BreakdownPrice(AddMenuActivity.this, s.toString(), text_price_breakdown, text_final_price,
                    sessionManager.getChagres().get(UserSessionManager.KEY_VAT),
                    sessionManager.getChagres().get(UserSessionManager.KEY_TRANSACTIONCHARGES)
                    , sessionManager.getChagres().get(UserSessionManager.KEY_FRESHHOMEEFEE));

            payableAmt = text_final_price.getText().toString();
        }
    }

    //ALL APIS CALLS--------------------------------------------------------------------------------

    private void getFilterdata() {
//        final ProgressDialog progressDialog = new ProgressDialog(AddMenuActivity.this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        if (!progressDialog.isShowing()) {
//            progressDialog.show();
//        }
        Call<JsonElement> calls = apiInterface.GetFilterData(sessionManager.getCountryName());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                try {
                    if (response.code() == 200) {
                        arrayCategory = new ArrayList<>();
                        arrayCuisines = new ArrayList<>();
                        arrayCity = new ArrayList<>();
                        arrayMeals = new ArrayList<>();
                        arraySortingMethods = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");

//                            //category array
//                            JSONArray cat_array = jsonObject.getJSONArray("category");
//                            for (int i = 0; i < cat_array.length(); i++) {
//                                JSONObject obj = cat_array.getJSONObject(i);
//                                NameID nameID = new NameID();
//                                nameID.setId(obj.getString("category_id"));
//                                nameID.setName(obj.getString("category_name"));
//                                arrayCategory.add(nameID);
//                            }
                            //cuisines array
                            JSONArray cuis_array = jsonObject.getJSONArray("cuisine");
                            for (int i = 0; i < cuis_array.length(); i++) {
                                JSONObject obj = cuis_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("cuisine_id"));
                                nameID.setName(obj.getString("cuisine_name"));
                                arrayCuisines.add(nameID);
                            }

                            //meal array
                            JSONArray meal_array = jsonObject.getJSONArray("meal");
                            mealSArray = new String[meal_array.length()];
                            for (int i = 0; i < meal_array.length(); i++) {
                                JSONObject obj = meal_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("meal_id"));
                                nameID.setName(obj.getString("meal_name"));
                                mealSArray[i] = obj.getString("meal_name");
                                arrayMeals.add(nameID);
                            }

                            mealSpinner.setAdapter(new ArrayAdapter<String>(AddMenuActivity.this, R.layout.layout_spinner_sales, mealSArray));
                            checkPreselectedCategory();
                            checkPreselecteCuisines();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //------------------------GETMAIN CATEGORIES------------------------
    private void getMainCategories() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.getHomeCategories(ConstantValues.home_food);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        array_maincategory = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            //category array
                            JSONArray cat_array = object.getJSONArray("homecategory");
                            mainCategoryArray = new LinkedList<>();
                            for (int i = 0; i < cat_array.length(); i++) {
                                JSONObject obj = cat_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("home_category_id"));
                                nameID.setName(obj.getString("home_category_name"));
                                //nameID.setFee(obj.getString("fee"));
                                array_maincategory.add(nameID);
                                mainCategoryArray.add(obj.getString("home_category_name"));
                            }
                            NameID nameID = new NameID();
                            nameID.setName("Select");
                            array_maincategory.add(0,nameID);
                            mainCategoryArray.add(0,"Select");
                            main_category.setAdapter(new ArrayAdapter<String>(AddMenuActivity.this, R.layout.layout_spinner_sales, mainCategoryArray));

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //------------------------GET SUB CATEGORIES AS PER MAIN ------------------------
    private void getSubCategories(String main_id) {
        final ProgressDialog progressDialog = new ProgressDialog(AddMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetSUBCategories(main_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        array_subcategory = new ArrayList<>();
                        array_Sub_categories = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            //category array
                            ArrayList<NameID> arraySubsubCategories = new ArrayList<>();
                            JSONArray category_array = object.getJSONArray("homecategorydetail");
                            for (int i = 0; i < category_array.length(); i++) {
                                JSONObject obj = category_array.getJSONObject(i);
                                SubCategory subCategory = new SubCategory(arraySubsubCategories);
                                //subCategory.setId(obj.getString("category_id"));
                                //subCategory.setName(obj.getString("name"));
//                                subCategory.setHasparents(obj.getString("has_parents"));
                                if (obj.has("sub_category1")) {
                                    //array_Sub_categories = new ArrayList<>();

                                    JSONArray jsonArray = obj.getJSONArray("sub_category1");
                                    subCategoryArray = new LinkedList<>();
                                    for (int j = 0; j < jsonArray.length(); j++) {
                                        JSONObject jobj = jsonArray.getJSONObject(j);
                                        NameID subCategory_i = new NameID();
                                        subCategory_i.setId(jobj.getString("category_id"));
                                        subCategory_i.setName(jobj.getString("name"));
                                        subCategory_i.setFee(jobj.getString("fee"));
//                                        subCategory_i.setHasparents(jobj.getString("has_parents"));
                                        array_subcategory.add(subCategory_i);
                                        subCategoryArray.add(jobj.getString("name"));
//                                        subCategory_i.setIsselected(false);
//                                        arraySubsubCategories.add(subCategory_i);
                                    }
                                    NameID subCategory_i = new NameID();
                                    subCategory_i.setName("Select");
                                    array_subcategory.add(0,subCategory_i);
                                    subCategoryArray.add(0,"Select");
                                    sub_category.setAdapter(new ArrayAdapter<String>(AddMenuActivity.this, R.layout.layout_spinner_sales, subCategoryArray));
                                }
//                                subCategory.setArrayList(arraySubsubCategories);
//                                array_Sub_categories.add(subCategory);


                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    //------------------------GET BRANDS AS PER SELECT CATEGORIES------------------------
    private void getBrands(String selected_Cats) {
        final ProgressDialog progressDialog = new ProgressDialog(AddMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetBrandsASCategories(selected_Cats);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        array_brands = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            //category array
                            JSONArray cat_array = object.getJSONArray("brand_list");
                            brandsSArray = new String[cat_array.length()];
                            for (int i = 0; i < cat_array.length(); i++) {
                                JSONObject obj = cat_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("brand_id"));
                                nameID.setName(obj.getString("name"));
                                nameID.setImg_url(obj.getString("image"));
                                array_brands.add(nameID);
                                brandsSArray[i] = obj.getString("name");
                            }

                            FoodBrands.setAdapter(new ArrayAdapter<String>(AddMenuActivity.this, R.layout.layout_spinner_sales, brandsSArray));

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }


    private void addmenu() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
//        RequestBody dishprice;

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("dish_image", file.getName(), requestBody);
        // add another part within the multipart request
        RequestBody dishname =
                RequestBody.create(MediaType.parse("text/plain"), edit_dishname.getText().toString().trim());
 RequestBody dishnameArabic =
                RequestBody.create(MediaType.parse("text/plain"), edit_dishname_arabic.getText().toString().trim());

        RequestBody dishdescription =
                RequestBody.create(MediaType.parse("text/plain"), edit_dishdescription.getText().toString().trim());
  RequestBody dishdescriptionArabic =
                RequestBody.create(MediaType.parse("text/plain"), edit_dishdescription_arabic.getText().toString().trim());

//        if (!edit_discount.getText().toString().trim().equalsIgnoreCase("0") && !edit_discount.getText().toString().trim().equalsIgnoreCase("")) {
//
//            String disAmt = text_discount_breakdown.getText().toString();
//
//            String finalDisAmt = disAmt.split("\\s")[9];
//
//            dishprice =
//                    RequestBody.create(MediaType.parse("text/plain"), finalDisAmt);
//        } else {
//
            RequestBody dishprice   =
                    RequestBody.create(MediaType.parse("text/plain"), edit_dishprice.getText().toString().trim());
        //}

        RequestBody pre_time =
                RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody dish_category =
                RequestBody.create(MediaType.parse("text/plain"), selected_sub_categories_id);

        RequestBody dish_cuisines =
                RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody weight =
                RequestBody.create(MediaType.parse("text/plain"), "0.5");

        RequestBody meals =
                RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody serving =
                RequestBody.create(MediaType.parse("text/plain"), serveSpinner.getSelectedItem().toString());

        RequestBody city =
                RequestBody.create(MediaType.parse("text/plain"), serveSpinner.getSelectedItem().toString());

        RequestBody discount_i =
                RequestBody.create(MediaType.parse("text/plain"), edit_discount.getText().toString().trim());

        RequestBody amt_collected =
                RequestBody.create(MediaType.parse("text/plain"), text_final_price.getText().toString().trim());

        RequestBody brand_id =
                RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody main_category =
                RequestBody.create(MediaType.parse("text/plain"), "1");

//        Call<JsonElement> calls = apiInterface.addMenuItem(dishname,
//                dishdescription,
//                dishprice,
//                dish_cuisines,
//                dish_category, pre_time, city, meals, serving, body, amt_collected, discount_i,brand_id,main_category);

        Call<JsonElement> calls = apiInterface.addMenuItem(dishname,dishnameArabic,
                dishdescription,dishdescriptionArabic,
                dishprice,
                dish_cuisines,
                dish_category, pre_time, city, meals, serving, body, amt_collected, discount_i, brand_id, main_category);

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

                            CommonUtilFunctions.success_Alert_Dialog(AddMenuActivity.this, "Dish added successfully!");
                            reload_screen();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void UpdateMenu() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("dish_image", file.getName(), requestBody);

        // add another part within the multipart request

        RequestBody dishid =
                RequestBody.create(MediaType.parse("text/plain"), dish_id);

        RequestBody dishname =
                RequestBody.create(MediaType.parse("text/plain"), edit_dishname.getText().toString().trim());
        RequestBody dishdescription =
                RequestBody.create(MediaType.parse("text/plain"), edit_dishdescription.getText().toString().trim());

        RequestBody dishprice =
                    RequestBody.create(MediaType.parse("text/plain"), edit_dishprice.getText().toString().trim());

        RequestBody pre_time =
                RequestBody.create(MediaType.parse("text/plain"), "");
        RequestBody dish_category =
                RequestBody.create(MediaType.parse("text/plain"), selected_sub_categories_id);

        RequestBody dish_cuisines =
                RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody dishqty =
                RequestBody.create(MediaType.parse("text/plain"), "1");
        RequestBody dish_status =
                RequestBody.create(MediaType.parse("text/plain"), "active");

        RequestBody meals =
                RequestBody.create(MediaType.parse("text/plain"), arrayMeals.get(mealSpinner.getSelectedItemPosition()).getId());

        RequestBody serving =
                RequestBody.create(MediaType.parse("text/plain"), serveSpinner.getSelectedItem().toString());
        RequestBody city =
                RequestBody.create(MediaType.parse("text/plain"), serveSpinner.getSelectedItem().toString());

        RequestBody discount_i =
                RequestBody.create(MediaType.parse("text/plain"), edit_discount.getText().toString().trim());

        RequestBody amt_collected =
                RequestBody.create(MediaType.parse("text/plain"), text_final_price.getText().toString().trim());

        RequestBody brand_id =
                RequestBody.create(MediaType.parse("text/plain"), "");

        RequestBody main_category =
                RequestBody.create(MediaType.parse("text/plain"), "1");

        Log.e("category", arrayMeals.get(mealSpinner.getSelectedItemPosition()).getId() + "-------meals"
                + serveSpinner.getSelectedItem().toString() + "---serve");

        Call<JsonElement> calls = apiInterface.UpdateMenuItem(dishid, dishname,
                dishdescription,
                dishprice,
                dish_cuisines,
                dish_category, pre_time, city, meals, serving, body, amt_collected, discount_i, brand_id, main_category);

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

                            CommonUtilFunctions.success_Alert_Dialog(AddMenuActivity.this, "Dish added successfully!");
                            reload_screen();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void UpdateMenuwithoutImage() {
        final ProgressDialog progressDialog = new ProgressDialog(AddMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();


        Call<JsonElement> calls = apiInterface.UpdateMenuItemWithoutImage(dish_id,
                edit_dishname.getText().toString().trim(),
                edit_dishdescription.getText().toString().trim(),
                edit_dishprice.getText().toString().trim(),
                "",
                selected_sub_categories_id,
                "",
                serveSpinner.getSelectedItem().toString(),
                arrayMeals.get(mealSpinner.getSelectedItemPosition()).getId(),
                "inactive",
                serveSpinner.getSelectedItem().toString(),
                text_final_price.getText().toString().trim(),
                edit_discount.getText().toString().trim(),
                "");

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

                            CommonUtilFunctions.success_Alert_Dialog(AddMenuActivity.this, "Dish added successfully!");
                            reload_screen();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }
}
