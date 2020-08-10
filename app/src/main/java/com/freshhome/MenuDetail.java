package com.freshhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuDetail extends AppCompatActivity implements View.OnClickListener {
    String item_id = "";
    LinearLayout linear_back, linear_edit, linear_delete, linear_availableitems, linear_image;
    TextView text_dname, text_dprice, text_customerviews, text_avialable, text_delivered,
            text_description, text_review, text_loadmore, text_meals, text_review_name, text_review_time, text_review_comment;

    FlowLayout flow_cuisines, flow_category;
    LinearLayout linear_category, linear_cuisnies, linear_comment_section;
    ImageView imageDish;
    ArrayList<NameID> arrayCategory, arrayCuisines, arrayMeals, mainCategory, subCategory;
    RatingBar ratingBar;
    ApiInterface apiInterface;
    SwitchCompat switch_active_inactive;
    CircleImageView image_reviewgiver;
    UserSessionManager sessionManager;
    String price = "", time_since = "", dish_serve = "", category = "", image = "", dish_id = "", status = "",
            discount = "",collectedAmount = "";
    boolean firstTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);
        sessionManager = new UserSessionManager(MenuDetail.this);
        apiInterface = ApiClient.getInstance().getClient();

        arrayCategory = new ArrayList<>();
        arrayCuisines = new ArrayList<>();
        arrayMeals = new ArrayList<>();

        item_id = getIntent().getStringExtra("item_id");
        linear_edit = (LinearLayout) findViewById(R.id.linear_edit);
        linear_edit.setOnClickListener(this);
        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);
        linear_delete = (LinearLayout) findViewById(R.id.linear_delete);
        linear_delete.setOnClickListener(this);

        linear_image = (LinearLayout) findViewById(R.id.linear_image);
        imageDish = (ImageView) findViewById(R.id.imageDish);

        linear_category = (LinearLayout) findViewById(R.id.linear_category);
        linear_cuisnies = (LinearLayout) findViewById(R.id.linear_cuisnies);

        linear_availableitems = (LinearLayout) findViewById(R.id.linear_availableitems);
        linear_availableitems.setVisibility(View.GONE);


        flow_category = (FlowLayout) findViewById(R.id.flow_category);
        flow_cuisines = (FlowLayout) findViewById(R.id.flow_cuisines);

        text_meals = (TextView) findViewById(R.id.text_meals);
        text_dname = (TextView) findViewById(R.id.text_dname);
        text_dprice = (TextView) findViewById(R.id.text_dprice);
        text_description = (TextView) findViewById(R.id.text_description);

        text_customerviews = (TextView) findViewById(R.id.text_customerviews);
        text_avialable = (TextView) findViewById(R.id.text_avialable);
        text_delivered = (TextView) findViewById(R.id.text_delivered);
        text_loadmore = (TextView) findViewById(R.id.text_loadmore);
        text_loadmore.setOnClickListener(this);
        text_review = (TextView) findViewById(R.id.text_review);
        text_review.setOnClickListener(this);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        linear_comment_section = (LinearLayout) findViewById(R.id.linear_comment_section);
        image_reviewgiver = (CircleImageView) findViewById(R.id.image_reviewgiver);
        text_review_name = (TextView) findViewById(R.id.text_review_name);
        text_review_time = (TextView) findViewById(R.id.text_review_time);
        text_review_comment = (TextView) findViewById(R.id.text_review_comment);
        image_reviewgiver = (CircleImageView) findViewById(R.id.image_reviewgiver);

        switch_active_inactive = (SwitchCompat) findViewById(R.id.switch_active_inactive);
        switch_active_inactive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (firstTime == false) {
                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        if (isChecked) {
                            add_qty_dialog(isChecked);
                        } else {
                            if (CommonMethods.checkConnection()) {
                                UpdateMenuQTY("0", "inactive");
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.internetconnection));
                            }
                        }
                    } else {
                        switch_active_inactive.setChecked(false);
                        CommonMethods.show_buy_plan(MenuDetail.this);
                    }
                }
                firstTime = false;
            }
        });


    }

    private void add_qty_dialog(final boolean isChecked) {
        final Dialog dialog = new Dialog(MenuDetail.this);
        dialog.setContentView(R.layout.layout_add_qty_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_save = (TextView) dialog.findViewById(R.id.text_save);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        final EditText edit_text = (EditText) dialog.findViewById(R.id.edit_text);
        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edit_text.getText().toString().equalsIgnoreCase("")) {
                    if (CommonMethods.checkConnection()) {
                        dialog.dismiss();
                        if (isChecked) {
                            UpdateMenuQTY(edit_text.getText().toString(), "active");
                        } else {
                            UpdateMenuQTY(edit_text.getText().toString(), "inactive");
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.internetconnection));
                    }
                } else {
                    CommonMethods.showtoast(MenuDetail.this, getResources().getString(R.string.enter_qty));
                }
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTime = true;
                edit_text.setText("");
                resetStatus(status);
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(MenuDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetItemDeail(item_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayCategory = new ArrayList<>();
                        arrayCuisines = new ArrayList<>();
                        arrayMeals = new ArrayList<>();
                        mainCategory = new ArrayList<>();
                        subCategory = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject obj = object.getJSONObject("success");
                            discount = obj.getString("discount");
                            collectedAmount = obj.getString("collected_amount");
                            dish_id = obj.getString("dish_id");
                            text_dname.setText(obj.getString("dish_name"));
                            text_avialable.setText(obj.getString("dist_qty"));
                            status = obj.getString("status");
                            if (status.equalsIgnoreCase("active")) {
                                firstTime = true;
                            }

                            resetStatus(status);
                            text_customerviews.setText(obj.getString("customer_view"));
                            text_delivered.setText(obj.getString("delivered_order"));
                            text_description.setText(obj.getString("dish_description"));
                            price = obj.getString("dish_price");
                            String discount = obj.getString("discount");
                            if(discount!=null && !discount.isEmpty() && !discount.equals("null")){
                                try {
                                    double priceDouble = Double.parseDouble(price);
                                    double discountPercentage = (priceDouble*Double.parseDouble(discount))/100;
                                    double discountPrice = priceDouble - discountPercentage;
                                    text_dprice.setText(ConstantValues.currency + " " + new DecimalFormat("#.##").format(discountPrice));
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else {
                                text_dprice.setText(ConstantValues.currency +" "+ obj.getString("dish_price"));
                            }


                            time_since = obj.getString("dish_since");
                            image = obj.getString("dish_image");
                            if (!image.equalsIgnoreCase("")) {
                                Picasso.get().load(image).placeholder(R.drawable.demo).into(imageDish);
                            }

                            //show rating and one comment
                            ratingBar.setRating(Float.parseFloat(obj.getString("rate_point")));
                            text_review.setText("(" + obj.getString("rate_point") + " Rating)");

                            JSONObject obj_review = obj.getJSONObject("last_review");
                            if (obj_review.has("review")) {
                                linear_comment_section.setVisibility(View.VISIBLE);
                                text_review_time.setText(obj_review.getString("time"));
                                text_review_comment.setText(obj_review.getString("review"));
                                JSONObject obj_user = obj_review.getJSONObject("user_info");
                                text_review_name.setText(obj_user.getString("name"));
                                if (!obj_user.getString("profile_pic").equalsIgnoreCase("")) {
                                    Picasso.get().load(obj_user.getString("profile_pic")).placeholder(R.drawable.icon).into(image_reviewgiver);
                                }
                            } else {
                                linear_comment_section.setVisibility(View.GONE);
                            }

                            //meals//servings
                            JSONArray meals_array = obj.getJSONArray("meals");
                            for (int i = 0; i < meals_array.length(); i++) {
                                JSONObject cat_obj = meals_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(cat_obj.getString("meal_id"));
                                JSONObject d_obj = cat_obj.getJSONObject("meal_detail");
                                nameID.setName(d_obj.getString("meal_name"));
                                arrayMeals.add(nameID);
                            }
                            dish_serve = obj.getString("dish_serve");
                            if (arrayMeals.size() > 0) {
                                text_meals.setText(arrayMeals.get(0).getName() + " for " + obj.getString("dish_serve") + " people ");
                            } else {
                                text_meals.setVisibility(View.GONE);
                            }

                            JSONArray category_array = obj.getJSONArray("categories");
                            for (int i = 0; i < category_array.length(); i++) {
                                JSONObject cat_obj = category_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(cat_obj.getString("category_id"));
                                JSONObject d_obj = cat_obj.getJSONObject("detail");
                                nameID.setName(d_obj.getString("category_name"));
                                arrayCategory.add(nameID);
                            }

                            JSONArray cuisines_array = obj.getJSONArray("cuisines");
                            for (int i = 0; i < cuisines_array.length(); i++) {
                                JSONObject cuis_obj = cuisines_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(cuis_obj.getString("cuisine_id"));
                                JSONObject d_obj = cuis_obj.getJSONObject("cuisine_detail");
                                nameID.setName(d_obj.getString("cuisine_name"));
                                arrayCuisines.add(nameID);
                            }
                            setcategory();
                            setcuisines();

                            JSONArray mainCategoryJsonArray = obj.getJSONArray("main_category");
                            for (int i = 0; i < mainCategoryJsonArray.length(); i++) {
                                JSONObject cat_obj = mainCategoryJsonArray.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setName(cat_obj.getString("name"));
                                nameID.setId(cat_obj.getString("home_category_id"));
                                nameID.setImg_url(cat_obj.getString("image"));
                                mainCategory.add(nameID);
                            }

                            JSONArray subCategoryJsonArray = obj.getJSONArray("sub_category");
                            for (int i = 0; i < subCategoryJsonArray.length(); i++) {
                                JSONObject cat_obj = subCategoryJsonArray.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setName(cat_obj.getString("name"));
                                nameID.setId(cat_obj.getString("sub_category_id"));
                                nameID.setImg_url(cat_obj.getString("image"));
                                nameID.setFee(cat_obj.getString("fee"));
                                subCategory.add(nameID);
                            }

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.server_error));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_back:
                MenuDetail.this.finish();
                break;

            case R.id.linear_edit:
                Intent i = new Intent(MenuDetail.this, AddMenuActivity.class);
                i.putExtra("dish_id", dish_id);
                i.putExtra("dname", text_dname.getText().toString().trim());
                i.putExtra("ddescription", text_description.getText().toString().trim());
                i.putExtra("dprice", price);
                i.putExtra("dsince", time_since);
                i.putExtra("dcuisnines", arrayCuisines);
                i.putExtra("dcategory", arrayCategory);
                i.putExtra("dimage", image);
                i.putExtra("dish_serve", dish_serve);
                i.putExtra("d_meal", arrayMeals);
                i.putExtra("mainCategory", mainCategory);
                i.putExtra("subCategory", subCategory);
                i.putExtra("discount", discount);
                i.putExtra("collected_amount", collectedAmount);
                startActivity(i);
                break;

            case R.id.linear_delete:
                show_dialog();
                break;

            case R.id.text_review:

                break;

            case R.id.text_loadmore:
                Intent intent = new Intent(MenuDetail.this, FeedbackListActivity.class);
                intent.putExtra("isSupplierDetail", false);
                intent.putExtra("id", dish_id);
                startActivity(intent);
                break;


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        apiInterface = ApiClient.getInstance().getClient();
        if (CommonMethods.checkConnection()) {
            getData();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.internetconnection));
        }

    }

    private void show_dialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                MenuDetail.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert!");

        // Setting Dialog Message
        alertDialog.setMessage("Do you really want to delete this item?");

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        alertDialog.setButton2("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
            }
        });

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                if (CommonMethods.checkConnection()) {
                    RemoveMenuItem();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.internetconnection));
                }
                alertDialog.dismiss();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void setcategory() {
        flow_category.removeAllViews();
        if (arrayCategory.size() == 0) {
            linear_category.setVisibility(View.GONE);
        } else {
            linear_category.setVisibility(View.GONE);
        }
        for (int i = 0; i < arrayCategory.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_menucategories, null);
            TextView textview = (TextView) view.findViewById(R.id.textview);
            textview.setText(arrayCategory.get(i).getName());
            flow_category.addView(view);
        }
    }

    private void setcuisines() {
        flow_cuisines.removeAllViews();
        if (arrayCuisines.size() == 0) {
            linear_cuisnies.setVisibility(View.GONE);
        } else {
            linear_cuisnies.setVisibility(View.VISIBLE);
        }
        for (int i = 0; i < arrayCuisines.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_menucategories, null);
            TextView textview = (TextView) view.findViewById(R.id.textview);
            textview.setText(arrayCuisines.get(i).getName());
            flow_cuisines.addView(view);
        }
    }

    private void UpdateMenuQTY(final String qty, final String updated_status) {
        final ProgressDialog progressDialog = new ProgressDialog(MenuDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        Call<JsonElement> calls = apiInterface.UpdateMenuItemQTY(dish_id, qty, updated_status);

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
                            text_avialable.setText(qty);
                            resetStatus(updated_status);
                            CommonUtilFunctions.success_Alert_Dialog(MenuDetail.this, "Quantity updated sucessfully!");
//                            reload_screen();

                        } else {
                            resetStatus(status);
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, obj.getString("msg"));
                        }
                    } else {
                        resetStatus(status);
                        CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.server_error));
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
                resetStatus(status);
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void resetStatus(String status) {
        if (status.equalsIgnoreCase("inactive")) {
            linear_availableitems.setVisibility(View.GONE);
            switch_active_inactive.setChecked(false);
        } else {
//            firstTime = true;
            switch_active_inactive.setChecked(true);
            linear_availableitems.setVisibility(View.VISIBLE);
        }
    }

    private void RemoveMenuItem() {
        final ProgressDialog progressDialog = new ProgressDialog(MenuDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.DeleteMenuItem(item_id);

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
//                            CommonUtilFunctions.success_Alert_Dialog(MenuDetail.this, obj.getString("message"));
                            MenuDetail.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, obj.getString("msg"));
                        }
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
                CommonUtilFunctions.Error_Alert_Dialog(MenuDetail.this, getResources().getString(R.string.server_error));
            }
        });

    }
}
