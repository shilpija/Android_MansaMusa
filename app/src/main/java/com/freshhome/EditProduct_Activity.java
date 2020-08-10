package com.freshhome;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.SubCategory;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProduct_Activity extends AppCompatActivity implements View.OnClickListener, TextWatcher, CompoundButton.OnCheckedChangeListener {
    ImageView image_back, image_spinner_arrow, image_add_sub_cat;
    //    TextView text_sub_categories;
//    Spinner categorySpinner;
    ApiInterface apiInterface;
    private String payableAmt = "";
    UserSessionManager sessionManager;
    EditText edit_prodescription, edit_proprice, edit_proname, edit_proqty, edit_discount;
    ArrayList<SubCategory> array_Categories, array_Sub_categories;
    //    String[] categorySArray;
//    String parent_id = "";
    LinearLayout linear_contiune, linear_varients, linear_sub_category, linear_discount;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri mCapturedImageURI;
    String fileName = "";
    private static final int RESULT_LOAD_IMAGE = 1;
    int PERMISSION_ALL = 1;
    String image_namewith_path = "", selected_cuisines = "", selected_sub_categories_id = "", pro_id = "", jsonDataSubCategories = "", image_url = "";
    String[] PERMISSIONS = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    CircleImageView circle_image;
    TextView text_web_link, text_price_breakdown, text_collected_price, text_discount_breakdown;
    private static final int REQUEST_SELECT_SUB_CATEGORY = 12;
    boolean isSubCategories = false;
    CheckBox checkbox_discount;
    RadioButton radioHand, radioFactory;
    String product_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);
        array_Categories = new ArrayList<>();
        array_Sub_categories = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(EditProduct_Activity.this);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        radioHand = (RadioButton) findViewById(R.id.radioHand);
        radioHand.setChecked(true);
        radioFactory = (RadioButton) findViewById(R.id.radioFactory);
        linear_discount = (LinearLayout) findViewById(R.id.linear_discount);
        checkbox_discount = (CheckBox) findViewById(R.id.checkbox_discount);
        checkbox_discount.setOnCheckedChangeListener(this);
        text_discount_breakdown = (TextView) findViewById(R.id.text_discount_breakdown);
        text_price_breakdown = (TextView) findViewById(R.id.text_price_breakdown);
        text_collected_price = (TextView) findViewById(R.id.text_collected_price);
        pro_id = getIntent().getStringExtra("pro_id");
        if(getIntent().hasExtra("fee")){
            //tractions charges
            sessionManager.saveCharges("5",
                    getIntent().getStringExtra("fee"),
                    "3");
        }
        edit_prodescription = (EditText) findViewById(R.id.edit_prodescription);
        edit_prodescription.setText(getIntent().getStringExtra("pro_description"));
        edit_proprice = (EditText) findViewById(R.id.edit_proprice);
        edit_proprice.setText((CommonMethods.checkNull(getIntent().getStringExtra("pro_price"))));
        setPricewithBreakDown((CommonMethods.checkNull(getIntent().getStringExtra("pro_price"))));
        edit_proprice.addTextChangedListener(this);
        edit_proname = (EditText) findViewById(R.id.edit_proname);
        edit_proname.setText(getIntent().getStringExtra("pro_name"));
        edit_proqty = (EditText) findViewById(R.id.edit_proqty);
        edit_proqty.setText(getIntent().getStringExtra("pro_qty"));
        edit_discount = (EditText) findViewById(R.id.edit_discount);
        linear_contiune = (LinearLayout) findViewById(R.id.linear_contiune);
        linear_contiune.setOnClickListener(this);

        text_web_link = (TextView) findViewById(R.id.text_web_link);
        text_web_link.setText(getIntent().getStringExtra("edit_link"));
        text_web_link.setOnClickListener(this);
        image_url = getIntent().getStringExtra("pro_image");
        circle_image = (CircleImageView) findViewById(R.id.circle_image);
        circle_image.setOnClickListener(this);
        if (!image_url.equalsIgnoreCase("")) {
            Picasso.get().load(image_url).into(circle_image);
        }

        if (getIntent().hasExtra("discount")) {
            String discount = getIntent().getStringExtra("discount");
            if(discount!=null && !discount.isEmpty()){
                checkbox_discount.setChecked(true);
                edit_discount.setText(discount);
            }
        }

        if (getIntent().hasExtra("collected_amount")) {
            text_collected_price.setText(getIntent().getStringExtra("collected_amount"));
        }

        requestpermission(false);

        edit_discount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (edit_proprice.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.enter_proprice));
                    edit_discount.setText("");
                } else {
                    if (s.toString().equalsIgnoreCase("")) {
                        text_discount_breakdown.setVisibility(View.GONE);
                    } else {
                        text_discount_breakdown.setVisibility(View.VISIBLE);
                        CommonMethods.DiscountedPrice(EditProduct_Activity.this, s.toString(), text_discount_breakdown, edit_proprice.getText().toString());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    if (edit_discount.getText().toString().trim().equalsIgnoreCase("0") || edit_discount.getText().toString().trim().equalsIgnoreCase("")) {
                        text_collected_price.setText(payableAmt);
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

                            text_collected_price.setText(new DecimalFormat("##.##").format(final_price));
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

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                EditProduct_Activity.this.finish();
                break;

//            case R.id.image_spinner_arrow:
//                categorySpinner.performClick();
//                break;

            case R.id.image_add_sub_cat:
                Intent i = new Intent(EditProduct_Activity.this, SelectSubCategoriesActivity.class);
                i.putExtra("sub_categories", array_Sub_categories);
                i.putExtra("json_categories", jsonDataSubCategories);
                startActivityForResult(i, REQUEST_SELECT_SUB_CATEGORY);
                break;

            case R.id.linear_contiune:
                CommonMethods.hideSoftKeyboard(EditProduct_Activity.this);
                if (radioFactory.isChecked()) {
                    product_type = "factory";
                } else if (radioHand.isChecked()) {
                    product_type = "hand";
                }
                if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                    if (edit_proname.getText().toString().trim().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.enter_proname));
                    } else if (edit_proprice.getText().toString().trim().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.enter_proprice));
                    } else if (edit_proqty.getText().toString().trim().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.enter_proqty));
                    } else if (edit_prodescription.getText().toString().trim().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.enter_prodescription));
                    } else if (checkbox_discount.isChecked() && edit_discount.getText().toString().equalsIgnoreCase("")) {
                        CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.enter_discount));
                    } else {
                        if (CommonMethods.checkConnection()) {
                            SubMitAddProduct();
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this,
                                    getResources().getString(R.string.internetconnection));
                        }
                    }
                } else {
                    CommonMethods.show_buy_plan(EditProduct_Activity.this);
                }
                break;

            case R.id.circle_image:
                requestpermission(true);
                break;

            case R.id.text_web_link:
                Intent i_web = new Intent(EditProduct_Activity.this, WebBrowserActivity.class);
                i_web.putExtra("url", text_web_link.getText().toString());
                startActivity(i_web);
                EditProduct_Activity.this.finish();
                break;
        }

    }

    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(EditProduct_Activity.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(EditProduct_Activity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
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
                mCapturedImageURI = FileProvider.getUriForFile(EditProduct_Activity.this, getApplicationContext().getPackageName() + ".fileprovider", output);
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
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
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
                    if (ActivityCompat.shouldShowRequestPermissionRationale(EditProduct_Activity.this, Manifest.permission.CAMERA)) {
//                        showStoragePermissionRationale();
                    } else {
                        CommonUtilFunctions.show_permission_alert(EditProduct_Activity.this, "Please allow all permission to use all funtionalities in app.");
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
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResult(EditProduct_Activity.this, data,
                            mCapturedImageURI, false, fileName, circle_image);
                    Log.i("image", image_namewith_path);
                }
                break;
            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview(EditProduct_Activity.this, data,
                            false, circle_image);
                    Log.i("image", image_namewith_path);
                }
                break;

//            case REQUEST_SELECT_SUB_CATEGORY:
//                String selected_categories = "";
//                selected_sub_categories_id = "";
//                if (data != null && data.hasExtra("sub_categories")) {
//                    array_Sub_categories = new ArrayList<>();
//                    array_Sub_categories = data.getParcelableArrayListExtra("sub_categories");
//                    for (int i = 0; i < array_Sub_categories.size(); i++) {
//                        if (array_Sub_categories.get(i).isIsselected()) {
//                            for (int j = 0; j < array_Sub_categories.get(i).getArrayList().size(); j++) {
//                                if (array_Sub_categories.get(i).getArrayList().get(j).isIsselected()) {
//                                    if (selected_sub_categories_id.equalsIgnoreCase("")) {
//                                        selected_sub_categories_id = array_Sub_categories.get(i).getArrayList().get(j).getId();
//                                    } else {
//                                        selected_sub_categories_id = selected_sub_categories_id + "," + array_Sub_categories.get(i).getArrayList().get(j).getId();
//                                    }
//                                }
//                            }
//                            if (selected_categories.equalsIgnoreCase("")) {
//                                selected_categories = array_Sub_categories.get(i).getName();
//                            } else {
//                                selected_categories = selected_categories + "," + array_Sub_categories.get(i).getName();
//
//                            }
//
//                            if (selected_sub_categories_id.equalsIgnoreCase("")) {
//                                selected_sub_categories_id = array_Sub_categories.get(i).getId();
//                            } else {
//                                selected_sub_categories_id = selected_sub_categories_id + "," + array_Sub_categories.get(i).getId();
//                            }
//                        }
//                    }
//                    text_sub_categories.setText(selected_categories);
//                }
//                break;

        }
    }

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(EditProduct_Activity.this);
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


    // TODO ------------------------API HITS--------------------
    //add product
    private void SubMitAddProduct() {
        final ProgressDialog progressDialog = new ProgressDialog(EditProduct_Activity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part body =
                MultipartBody.Part.createFormData("product_image", file.getName(), requestBody);

        // add another part within the multipart request
        RequestBody pro_name =
                RequestBody.create(MediaType.parse("text/plain"), edit_proname.getText().toString().trim());

        RequestBody pro_des =
                RequestBody.create(MediaType.parse("text/plain"), edit_prodescription.getText().toString().trim());

        RequestBody pro_price =
                RequestBody.create(MediaType.parse("text/plain"), edit_proprice.getText().toString().trim());

        RequestBody pro_qty =
                RequestBody.create(MediaType.parse("text/plain"), edit_proqty.getText().toString().trim());

        RequestBody product_id =
                RequestBody.create(MediaType.parse("text/plain"), pro_id);

        RequestBody discount_i =
                RequestBody.create(MediaType.parse("text/plain"), edit_discount.getText().toString().trim());


        RequestBody type_product =
                RequestBody.create(MediaType.parse("text/plain"), product_type);


        RequestBody amt_collected =
                RequestBody.create(MediaType.parse("text/plain"), text_collected_price.getText().toString().trim());

        Call<JsonElement> calls;

        if (!image_namewith_path.equalsIgnoreCase("")) {
            calls = apiInterface.EditProductItemWithImage(pro_name, pro_des, pro_price, pro_qty, body,
                    product_id, amt_collected, type_product, discount_i);
        } else {
            calls = apiInterface.EditProductItemWithoutImage(pro_id, edit_proname.getText().toString().trim(),
                    edit_prodescription.getText().toString().trim(), edit_proprice.getText().toString().trim(), edit_proqty.getText().toString().trim(), "inactive", text_collected_price.getText().toString().trim(), product_type,
                    edit_discount.getText().toString().trim());
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
                            JSONObject obj = object.getJSONObject("success");
                            success_Dialog(EditProduct_Activity.this, obj.getString("msg"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.server_error));
            }
        });
    }

//    //get category
//    private void getCategoriesdata(final String parent_id) {
//        final ProgressDialog progressDialog = new ProgressDialog(EditProduct_Activity.this);
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        if (!progressDialog.isShowing()) {
//            progressDialog.show();
//        }
//        Call<JsonElement> calls = apiInterface.GetProductCategories(parent_id);
//        calls.enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                try {
//                    if (response.code() == 200) {
////                        array_Categories = new ArrayList<>();
//                        array_Sub_categories = new ArrayList<>();
//                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
//                        if (object.getString("code").equalsIgnoreCase("200")) {
//                            JSONArray category_array = object.getJSONArray("data");
//                            //categories
//                            if (parent_id.equalsIgnoreCase("")) {
//                                categorySArray = new String[category_array.length()];
//                                for (int i = 0; i < category_array.length(); i++) {
//                                    JSONObject obj = category_array.getJSONObject(i);
//                                    SubCategory subCategory = new SubCategory();
//                                    subCategory.setId(obj.getString("main_category"));
//                                    subCategory.setName(obj.getString("category_name"));
//                                    subCategory.setHasparents(obj.getString("has_parents"));
//                                    array_Categories.add(subCategory);
//                                    categorySArray[i] = obj.getString("category_name");
//                                }
//                                categorySpinner.setAdapter(new ArrayAdapter<>(EditProduct_Activity.this,
//                                        R.layout.layout_spinner_text_bg_transparent, categorySArray));
//                            } else {
//                                //sub- categories
//                                array_Sub_categories = new ArrayList<>();
//                                ArrayList<NameID> arraySubsubCategories = new ArrayList<>();
//                                if (category_array.length() != 0) {
//                                    isSubCategories = true;
//                                    linear_sub_category.setVisibility(View.VISIBLE);
//                                } else {
//                                    isSubCategories = false;
//                                    linear_sub_category.setVisibility(View.GONE);
//                                }
//                                for (int i = 0; i < category_array.length(); i++) {
//                                    JSONObject obj = category_array.getJSONObject(i);
//                                    SubCategory subCategory = new SubCategory(arraySubsubCategories);
//                                    subCategory.setId(obj.getString("sub_category"));
//                                    subCategory.setName(obj.getString("category_name"));
//                                    subCategory.setHasparents(obj.getString("has_parents"));
//                                    if (obj.has("sub_categories")) {
//                                        arraySubsubCategories = new ArrayList<>();
//                                        JSONArray jsonArray = obj.getJSONArray("sub_categories");
//                                        for (int j = 0; j < jsonArray.length(); j++) {
//                                            JSONObject jobj = jsonArray.getJSONObject(j);
//                                            NameID subCategory_i = new NameID();
//                                            subCategory_i.setId(jobj.getString("sub_category"));
//                                            subCategory_i.setName(jobj.getString("category_name"));
//                                            subCategory_i.setHasparents(jobj.getString("has_parents"));
//                                            subCategory_i.setIsselected(false);
//                                            arraySubsubCategories.add(subCategory_i);
//                                        }
//                                    }
//                                    subCategory.setArrayList(arraySubsubCategories);
//                                    array_Sub_categories.add(subCategory);
//                                }
//                            }
//                        } else {
//                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, obj.getString("msg"));
//                            linear_sub_category.setVisibility(View.GONE);
//                        }
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.server_error));
//                    }
//                } catch (
//                        JSONException e)
//
//                {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                call.cancel();
//                CommonUtilFunctions.Error_Alert_Dialog(EditProduct_Activity.this, getResources().getString(R.string.server_error));
//            }
//        });
//    }

    public void success_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                EditProduct_Activity.this.finish();
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message

        alertDialog.show();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //TODO TRANSACTION CHARGES 2.5 AND FRESSHOMME FEE 10% VAT 10%
        setPricewithBreakDown(s.toString());

        payableAmt = text_collected_price.getText().toString();
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
        }
    }

    private void setPricewithBreakDown(String s) {
        if (s.equalsIgnoreCase("")) {
            text_price_breakdown.setVisibility(View.GONE);
            text_collected_price.setText("");
        } else {
            text_price_breakdown.setVisibility(View.VISIBLE);
            CommonMethods.BreakdownPrice(EditProduct_Activity.this, s.toString(), text_price_breakdown, text_collected_price,
                    sessionManager.getChagres().get(UserSessionManager.KEY_VAT),
                    sessionManager.getChagres().get(UserSessionManager.KEY_TRANSACTIONCHARGES)
                    , sessionManager.getChagres().get(UserSessionManager.KEY_FRESHHOMEEFEE));
        }
    }
}
