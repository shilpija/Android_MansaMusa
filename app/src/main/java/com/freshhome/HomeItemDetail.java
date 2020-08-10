package com.freshhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.freshhome.fragments.HomeFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeItemDetail extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_supplier, linear_back, linear_availableitems, linear_favo, linear_add_to_cart, linear_text_done, linear_qty, linear_cart_info, linear_comment_section;
    CircleImageView supplierImage, image_reviewgiver;
    ApiInterface apiInterface;
    String dish_id = "";
    TextView text_minus, text_plus, text_qty, text_add_cart, text_dname, text_dprice, text_description,
            text_customerviews, text_avialable, text_delivered, text_review, text_loadmore, text_meals, text_items,
            text_total_price, text_viewcart, text_review_name, text_review_time, text_review_comment;
    String price = "", time_since = "", cuisines = "", category = "", image = "", supplier_id = "", supplier_name = "", supplier_rating = "", supplier_location = "", supplier_url = "";
    RatingBar ratingBar;
    int cart_qty = 0;
    ArrayList<NameID> arrayCategory, arrayCuisines, arrayMeals;
    FlowLayout flow_cuisines, flow_category;
    LinearLayout linear_category, linear_cuisnies;
    ImageView dish_image, image_favo;
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_item_detail);
        arrayCategory = new ArrayList<>();
        arrayCuisines = new ArrayList<>();
        arrayMeals = new ArrayList<>();

        sessionManager = new UserSessionManager(HomeItemDetail.this);
        apiInterface = ApiClient.getInstance().getClient();
//        screen_id = getIntent().getStringExtra("item_type");
        dish_id = getIntent().getStringExtra("item_id");
        linear_supplier = (LinearLayout) findViewById(R.id.linear_supplier);
        linear_supplier.setOnClickListener(this);

        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);

        linear_add_to_cart = (LinearLayout) findViewById(R.id.linear_add_to_cart);
        linear_add_to_cart.setOnClickListener(this);

        linear_cart_info = (LinearLayout) findViewById(R.id.linear_cart_info);
        text_items = (TextView) findViewById(R.id.text_items);
        text_total_price = (TextView) findViewById(R.id.text_total_price);
        text_viewcart = (TextView) findViewById(R.id.text_viewcart);
        text_viewcart.setOnClickListener(this);

        //check if supplier or user  hide cart in case of supplier
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            //hide--show cart bannder according to data form homefragment
            linear_add_to_cart.setVisibility(View.VISIBLE);
            if (getIntent().hasExtra("cart_count") && !getIntent().getStringExtra("cart_count").equalsIgnoreCase("")) {
                linear_cart_info.setVisibility(View.VISIBLE);
                text_items.setText(getIntent().getStringExtra("cart_count"));
                text_total_price.setText(getIntent().getStringExtra("cart_price"));
            } else {
                linear_cart_info.setVisibility(View.GONE);
            }
        } else {
            linear_add_to_cart.setVisibility(View.GONE);
        }

        //linear plus---minus---qty textview---should hide at first
        linear_qty = (LinearLayout) findViewById(R.id.linear_qty);
        linear_qty.setVisibility(View.GONE);

        linear_text_done = (LinearLayout) findViewById(R.id.linear_text_done);
        linear_text_done.setOnClickListener(this);

        dish_image = (ImageView) findViewById(R.id.dish_image);
        supplierImage = (CircleImageView) findViewById(R.id.supplierImage);
        supplierImage.setOnClickListener(this);
        if (getIntent().hasExtra("from")) {
            if (getIntent().getStringExtra("from").equalsIgnoreCase("menu")) {
                supplierImage.setVisibility(View.GONE);
            } else {
                supplierImage.setVisibility(View.VISIBLE);
            }
        }

        linear_availableitems = (LinearLayout) findViewById(R.id.linear_availableitems);
        text_meals = (TextView) findViewById(R.id.text_meals);
        text_qty = (TextView) findViewById(R.id.text_qty);
        text_minus = (TextView) findViewById(R.id.text_minus);
        text_minus.setOnClickListener(this);
        text_plus = (TextView) findViewById(R.id.text_plus);
        text_plus.setOnClickListener(this);
        text_add_cart = (TextView) findViewById(R.id.text_add_cart);
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
        image_reviewgiver = (CircleImageView) findViewById(R.id.image_reviewgiver);
        text_review_name = (TextView) findViewById(R.id.text_review_name);
        text_review_time = (TextView) findViewById(R.id.text_review_time);
        text_review_comment = (TextView) findViewById(R.id.text_review_comment);
        linear_comment_section = (LinearLayout) findViewById(R.id.linear_comment_section);

        linear_category = (LinearLayout) findViewById(R.id.linear_category);
        linear_cuisnies = (LinearLayout) findViewById(R.id.linear_cuisnies);

        flow_category = (FlowLayout) findViewById(R.id.flow_category);
        flow_cuisines = (FlowLayout) findViewById(R.id.flow_cuisines);

        linear_favo = (LinearLayout) findViewById(R.id.linear_favo);
        linear_favo.setOnClickListener(this);
        image_favo = (ImageView) findViewById(R.id.image_favo);
        //hide favo for supplier
        //show for user and skipp
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            linear_favo.setVisibility(View.GONE);
        } else {
            linear_favo.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.supplierImage:
                Intent i = new Intent(HomeItemDetail.this, SupplierDetailActivity.class);
                i.putExtra("supplier_id", supplier_id);
                i.putExtra("supplier_name", supplier_name);
                i.putExtra("supplier_rating", supplier_rating);
                i.putExtra("supplier_location", supplier_location);
                i.putExtra("supplier_url", supplier_url);
                startActivity(i);
                break;

            case R.id.linear_back:
                HomeItemDetail.this.finish();
                break;
            case R.id.text_review:

                break;

            case R.id.text_loadmore:
                Intent intent = new Intent(HomeItemDetail.this, FeedbackListActivity.class);
                intent.putExtra("isSupplierDetail", false);
                intent.putExtra("id", supplier_id);
                startActivity(intent);
                break;

            case R.id.linear_favo:
                //TODO : LOGGEDIN CHECK
                if (sessionManager.isLoggedIn()) {
                    if (CommonMethods.checkConnection()) {
                        //check if we have to favo it or remove it from favo
                        if (((BitmapDrawable) image_favo.getDrawable()).getBitmap().sameAs(((BitmapDrawable) getResources().getDrawable(R.drawable.heart_green_filed)).getBitmap())) {
                            favoDish(dish_id, image_favo, false);
                        } else {
                            favoDish(dish_id, image_favo, true);
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.internetconnection));
                    }
                } else {
                    CommonMethods.ShowLoginDialog(HomeItemDetail.this);
                }
                break;

            case R.id.linear_add_to_cart:
                //TODO : LOGGEDIN CHECK
                //if (sessionManager.isLoggedIn()) {
                    linear_qty.setVisibility(View.VISIBLE);
                    text_add_cart.setVisibility(View.GONE);
                    linear_add_to_cart.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_bottom_round_coner_orange));
//                add_qty_dialog();
//                } else {
//                    CommonMethods.ShowLoginDialog(HomeItemDetail.this);
//                }
                break;

            case R.id.linear_text_done:
                //update qty
                if (CommonMethods.checkConnection()) {
                    addToCart(dish_id, String.valueOf(cart_qty), "", true);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_minus:
                if (cart_qty > 0) {
                    cart_qty--;
                    text_qty.post(new Runnable() {
                        public void run() {
                            text_qty.setText(String.valueOf(cart_qty));
                        }
                    });
                }
                break;

            case R.id.text_plus:

                cart_qty++;
                text_qty.post(new Runnable() {
                    public void run() {
                        text_qty.setText(String.valueOf(cart_qty));
                    }
                });

                break;

            case R.id.text_viewcart:
                Intent i_cart = new Intent(HomeItemDetail.this, MainActivity_NavDrawer.class);
                i_cart.putExtra("OpenFrag", ConstantValues.OPENCART);
                startActivity(i_cart);
                ActivityCompat.finishAffinity(HomeItemDetail.this);
                break;
        }

    }

    private void favoDish(final String dish_id, final ImageView image_favo, final boolean isAdd) {
        final ProgressDialog progressDialog = new ProgressDialog(HomeItemDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls;
        if (isAdd) {
            calls = apiInterface.AddFavoDish(dish_id);
        } else {
            calls = apiInterface.RemoveFavoDish(dish_id);
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
                            JSONObject jsonObject = object.getJSONObject("success");
                            CommonUtilFunctions.success_Alert_Dialog(HomeItemDetail.this, jsonObject.getString("msg"));
                            if (isAdd) {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green_filed));
                            } else {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green));
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void getData() {
        final ProgressDialog progressDialog = new ProgressDialog(HomeItemDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        calls = apiInterface.GetItemDeail(dish_id);
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
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject obj = object.getJSONObject("success");
                            text_dname.setText(obj.getString("dish_name"));
                            text_avialable.setText(obj.getString("dist_qty"));
                            if (obj.getString("status").equalsIgnoreCase("inactive")) {
                                linear_availableitems.setVisibility(View.GONE);
                            } else {
                                linear_availableitems.setVisibility(View.VISIBLE);
                            }
                            cart_qty = obj.getInt("cart_qty");
                            text_qty.setText(String.valueOf(cart_qty));

                            text_customerviews.setText(obj.getString("customer_view"));
                            text_delivered.setText(obj.getString("delivered_order"));
                           // text_description.setText(obj.getString("dish_description"));


                            String des = obj.getString("dish_description");

                            if (des.contains("<p>")) {
                                String[] separated = des.split("<p>");
                                String a = separated[0]; // this will contain "Fruit"
                                String ab = separated[1]; // this will contain " they taste good"
                                String[] separated1 = ab.split("</p>");
                                String aa = separated1[0]; // this will contain "Fruit"
                                text_description.setText(aa);
                            } else {
                                text_description.setText(des);
                            }

                            price = obj.getString("dish_price");
                            text_dprice.setText(ConstantValues.currency + obj.getString("dish_price"));

                            if (obj.getString("is_fav").equalsIgnoreCase("1")) {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green_filed));
                            } else {
                                image_favo.setImageDrawable(getResources().getDrawable(R.drawable.heart_green));
                            }

                            time_since = obj.getString("dish_since");

                            // dish_image
                            image = obj.getString("dish_image");
                            if (!image.equalsIgnoreCase("")) {
                                Picasso.get().load(image).placeholder(R.drawable.demo).into(dish_image);
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
//                            dish_serve=obj.getString("dish_serve");
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

//                            supplier_id
                            JSONObject supplier_obj = obj.getJSONObject("supplier");
                            supplier_id = supplier_obj.getString("supplier_id");
                            supplier_name = supplier_obj.getString("name");
                            supplier_rating = supplier_obj.getString("rating");
                            supplier_location = supplier_obj.getString("location");
                            supplier_url = supplier_obj.getString("profile_pic");

                            if (!supplier_url.equalsIgnoreCase("")) {
                                Picasso.get().load(supplier_url).placeholder(R.drawable.icon).into(supplierImage);
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.server_error));
            }
        });

    }

    private void addToCart(String menu_id, final String qty, String confirm, boolean isAdd) {
        final ProgressDialog progressDialog = new ProgressDialog(HomeItemDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isAdd) {
            calls = apiInterface.AddUpdateDeleteCartItem(menu_id, qty);
        } else {
            calls = apiInterface.AddConfirm(menu_id, qty, confirm);
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

                            if (object.has("is_allow") && object.getString("is_allow").equalsIgnoreCase("0")) {
                                //text and hit api
                                Error_Alert_Dialog(HomeItemDetail.this, object.getString("confirm"), qty);
                            } else {
                                //hide plus--minus
                                linear_qty.setVisibility(View.GONE);
                                //show text add to cart
                                text_add_cart.setVisibility(View.VISIBLE);
                                //change background color and text
                                if (cart_qty == 0) {
                                    text_add_cart.setText(getResources().getString(R.string.addtocart));
                                    linear_add_to_cart.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_bottom_round_coner_orange));
                                } else {
                                    text_add_cart.setText(qty + " " + getResources().getString(R.string.addedtocart));
                                    linear_add_to_cart.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_round_coner_green));

                                }

                                if (object.has("cart_items")) {
                                    JSONObject cart_obj = object.getJSONObject("cart_total");
                                    if (!object.getString("cart_items").equalsIgnoreCase("")) {
                                        linear_cart_info.setVisibility(View.VISIBLE);
                                        text_items.setText(object.getString("cart_items") + " Items");
                                        text_total_price.setText(cart_obj.getString("currency") + " " + cart_obj.getString("total"));
                                    } else {
                                        linear_cart_info.setVisibility(View.GONE);
                                    }
                                } else {
                                    linear_cart_info.setVisibility(View.GONE);
                                }
                                CommonUtilFunctions.success_Alert_Dialog(HomeItemDetail.this, object.getString("success"));
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, obj.getString("msg"));
                        }
                    } else {
//                        Error_Alert_Dialog(HomeItemDetail.this, "Change", qty);
                        CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        apiInterface = ApiClient.getInstance().getClient();
        if (CommonMethods.checkConnection()) {
            getData();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.internetconnection));
        }
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

    private void add_qty_dialog() {
        final Dialog dialog = new Dialog(HomeItemDetail.this);
        dialog.setContentView(R.layout.layout_add_qty_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_save = (TextView) dialog.findViewById(R.id.text_save);
        final EditText edit_text = (EditText) dialog.findViewById(R.id.edit_text);

        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!edit_text.getText().toString().equalsIgnoreCase("")) {
                    if (CommonMethods.checkConnection()) {
                        dialog.dismiss();
                        if (CommonMethods.checkConnection()) {
                            addToCart(dish_id, edit_text.getText().toString(), "", true);
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.internetconnection));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.internetconnection));
                    }
                } else {
                    CommonMethods.showtoast(HomeItemDetail.this, getResources().getString(R.string.enter_qty));
                }
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

    private void Error_Alert_Dialog(Context context, String message, final String qty) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Alert!");
        alertDialog.setMessage(message);
        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                if (CommonMethods.checkConnection()) {
                    addToCart(dish_id, qty, "1", false);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(HomeItemDetail.this, getResources().getString(R.string.internetconnection));
                }

            }
        });
        alertDialog.show();
    }
}
