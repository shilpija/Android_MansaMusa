package com.freshhome;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.AdapterClass.ProductDetailColorselectionAdatpter;
import com.freshhome.AdapterClass.ProductDetailConfrigationAdatpter;
import com.freshhome.AdapterClass.ProductSizeSelectAdapter;
import com.freshhome.AdapterClass.ViewPagerDetailsPageAdapter;
import com.freshhome.AdapterClass.subAttributeAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.ExpandableHeightGridView;
import com.freshhome.CommonUtil.FlowLayout;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.product_attributes;
import com.freshhome.datamodel.subAttributes;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetail extends AppCompatActivity implements View.OnClickListener, ProductDetailColorselectionAdatpter.OnColorClickListener, ProductSizeSelectAdapter.OnSizeClickListener, ProductDetailConfrigationAdatpter.OnConfrigationClickListener {
    String item_id = "";
    private String productDes="";
    LinearLayout linear_back, linear_edit, linear_delete, linear_availableitems, linear_image, llSize;
    TextView text_dname, text_dprice, tvDisprice, text_customerviews, text_avialable, text_delivered,
             text_review, text_loadmore, text_meals, text_review_name, text_review_time, text_review_comment;
    CircleImageView image_reviewgiver;
    WebView text_description;
    FlowLayout flow_cuisines, flow_category;
    LinearLayout linear_category, linear_cuisnies, linear_comment_section;
    ImageView imageDish, image_favo;
    ArrayList<NameID> arrayCategory, arrayCuisines, arrayMeals;
    RatingBar ratingBar;
    ApiInterface apiInterface;
    SwitchCompat switch_active_inactive;
    String price = "", currency = "", time_since = "", dish_serve = "", category = "", image = "", pro_id = "", status = "", collected_amount = "",
            supplier_id = "", supplier_name = "", supplier_rating = "", supplier_location = "", supplier_url = "", edit_link = "", fee = "", discount = "", size = "";
    boolean firstTime = false;
    ArrayList<String> arrayImages;
    String[] sizeArray;
    ViewPager viewPager;
    RecyclerView rvSize, rvColor, rvConfiguration;
    LinearLayout sliderDotspanel, linear_attribues;
    ArrayList<product_attributes> arrayAttributes;
    ArrayList<subAttributes> arraysubAttributes;
    ArrayList<subAttributes> arrayspinner, arrayradio, array_checkbox;
    ArrayList<subAttributes> arrayColor, arraySize, arrayCon;
    //new addons
    CircleImageView supplierImage;
    LinearLayout linear_cart_info, linear_add_to_cart, linear_qty, linear_text_done, linear_favo;
    TextView text_items, text_total_price, text_viewcart, text_add_cart, text_minus, text_qty, text_plus,
            tvCate, tvColor, tvConfiguration;
    //make it inactive for detail switch_active_inactive
    UserSessionManager sessionManager;
    int cart_qty = 0;
    //will enable/disable attributes enable in case user and accessing product detail screen
    boolean isFromHome = false;
    ArrayList<String> attriConfitype;
    ArrayList<String> attriColortype;
    ArrayList<String> attriSizetype;
    String attrColorType, attrSizeType, attrConfiType;
    private ProductDetailColorselectionAdatpter colorAdapter;
    private ProductDetailConfrigationAdatpter confriAdapter;
    private ProductSizeSelectAdapter sizeAdapter;
    private String SizeoptionId, ColoroptionId, ConfigurationptionId, ConfigurationName, SizeName, ColorName;
    private String isAllowGuest = "";
    private int dotscount;
    private ImageView[] dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        sessionManager = new UserSessionManager(ProductDetail.this);
        apiInterface = ApiClient.getInstance().getClient();
        arrayImages = new ArrayList<>();
        arrayCategory = new ArrayList<>();
        arrayCuisines = new ArrayList<>();
        arrayMeals = new ArrayList<>();
        arrayAttributes = new ArrayList<>();
        arraysubAttributes = new ArrayList<>();
        arrayspinner = new ArrayList<>();
        arrayradio = new ArrayList<>();
        arraySize = new ArrayList<>();
        arrayCon = new ArrayList<>();
        arrayColor = new ArrayList<>();
        array_checkbox = new ArrayList<>();
        attriSizetype = new ArrayList<>();
        attriConfitype = new ArrayList<>();
        attriColortype = new ArrayList<>();


        sizeArray = new String[2];
        item_id = getIntent().getStringExtra("item_id");
        llSize = (LinearLayout) findViewById(R.id.llSize);
        linear_edit = (LinearLayout) findViewById(R.id.linear_edit);
        linear_edit.setOnClickListener(this);
        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);
        linear_delete = (LinearLayout) findViewById(R.id.linear_delete);
        linear_delete.setOnClickListener(this);

        linear_image = (LinearLayout) findViewById(R.id.linear_image);
        imageDish = (ImageView) findViewById(R.id.imageDish);

        rvSize = (RecyclerView) findViewById(R.id.rvSize);
        rvColor = (RecyclerView) findViewById(R.id.rvColor);
        rvConfiguration = (RecyclerView) findViewById(R.id.rvConfiguration);
        linear_category = (LinearLayout) findViewById(R.id.linear_category);
        linear_cuisnies = (LinearLayout) findViewById(R.id.linear_cuisnies);

        linear_availableitems = (LinearLayout) findViewById(R.id.linear_availableitems);
        linear_availableitems.setVisibility(View.GONE);

        linear_favo = (LinearLayout) findViewById(R.id.linear_favo);
        linear_favo.setVisibility(View.GONE);

        flow_category = (FlowLayout) findViewById(R.id.flow_category);
        flow_cuisines = (FlowLayout) findViewById(R.id.flow_cuisines);

        text_meals = (TextView) findViewById(R.id.text_meals);
        text_meals.setVisibility(View.GONE);
        text_dname = (TextView) findViewById(R.id.text_dname);
        tvDisprice = (TextView) findViewById(R.id.tvDisprice);
        text_dprice = (TextView) findViewById(R.id.text_dprice);
        text_description = (WebView) findViewById(R.id.text_description);

        tvCate = (TextView) findViewById(R.id.tvCate);
        tvColor = (TextView) findViewById(R.id.tvColor);
        tvConfiguration = (TextView) findViewById(R.id.tvConfiguration);
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
        image_favo = (ImageView) findViewById(R.id.image_favo);
        switch_active_inactive = (SwitchCompat) findViewById(R.id.switch_active_inactive);
        switch_active_inactive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!firstTime) {
                    if (sessionManager.getSubscriptionDetails().get(UserSessionManager.KEY_STATUS).equalsIgnoreCase("active")) {
                        if (isChecked) {
                            add_qty_dialog(isChecked);
                        } else {
                            if (CommonMethods.checkConnection()) {
                                UpdateMenuQTY("0", "inactive");
                            } else {
                                CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
                            }
                        }
                    } else {
                        switch_active_inactive.setChecked(false);
                        CommonMethods.show_buy_plan(ProductDetail.this);
                    }
                }
                firstTime = false;
            }
        });

        viewPager = (ViewPager) findViewById(R.id.pager);
        sliderDotspanel = (LinearLayout) findViewById(R.id.SliderDots);
        linear_attribues = (LinearLayout) findViewById(R.id.linear_attribues);

        supplierImage = (CircleImageView) findViewById(R.id.supplierImage);
        supplierImage.setOnClickListener(this);
        text_items = (TextView) findViewById(R.id.text_items);
        text_total_price = (TextView) findViewById(R.id.text_total_price);
        text_viewcart = (TextView) findViewById(R.id.text_viewcart);
        text_viewcart.setOnClickListener(this);
        linear_cart_info = (LinearLayout) findViewById(R.id.linear_cart_info);
        linear_add_to_cart = (LinearLayout) findViewById(R.id.linear_add_to_cart);
        linear_add_to_cart.setOnClickListener(this);
        linear_text_done = (LinearLayout) findViewById(R.id.linear_text_done);
        linear_text_done.setOnClickListener(this);
        linear_qty = (LinearLayout) findViewById(R.id.linear_qty);

        text_add_cart = (TextView) findViewById(R.id.text_add_cart);
        text_minus = (TextView) findViewById(R.id.text_minus);
        text_minus.setOnClickListener(this);
        text_qty = (TextView) findViewById(R.id.text_qty);
        text_plus = (TextView) findViewById(R.id.text_plus);
        text_plus.setOnClickListener(this);

        if (getIntent().hasExtra("item_type")) {
            //means from home
            makeElementsVisibleGone(true);
            checkBuySell();
        } else {
            //from the items
            makeElementsVisibleGone(false);
        }


        if (CommonMethods.checkConnection()) {
            getData();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
        }

        if (sessionManager.getCartItem() != null && !sessionManager.getCartItem().isEmpty() && !sessionManager.getCartItem().equals("0")) {
            linear_cart_info.setVisibility(View.VISIBLE);
            text_total_price.setText(sessionManager.getCartItem() + " Product Added");
        } else {
            linear_cart_info.setVisibility(View.GONE);
        }

    }

    //TODO CHECK IF THE SCREEN CALLED FORM USER OR SUPPLIER MODULE AND HIDE BUTTON
    private void checkBuySell() {
        //check if supplier or user  hide cart in case of supplier
        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            //hide--show cart bannder according to data form homefragment
            isFromHome = true;
            linear_add_to_cart.setVisibility(View.VISIBLE);
            linear_favo.setVisibility(View.VISIBLE);
            if (getIntent().hasExtra("cart_count") && !getIntent().getStringExtra("cart_count").equalsIgnoreCase("")) {
                linear_cart_info.setVisibility(View.VISIBLE);
                text_items.setText(getIntent().getStringExtra("cart_count"));
                text_total_price.setText(getIntent().getStringExtra("cart_price"));
            } else {
                linear_cart_info.setVisibility(View.GONE);
            }
        } else {
            isFromHome = false;
            linear_add_to_cart.setVisibility(View.GONE);
            linear_cart_info.setVisibility(View.GONE);
            linear_favo.setVisibility(View.GONE);
        }
    }

    private void makeElementsVisibleGone(boolean b) {
        if (b) {
            //means form home screen
            supplierImage.setVisibility(View.VISIBLE);
            linear_cart_info.setVisibility(View.VISIBLE);
            switch_active_inactive.setVisibility(View.GONE);
            linear_add_to_cart.setVisibility(View.VISIBLE);
            linear_delete.setVisibility(View.GONE);
            linear_edit.setVisibility(View.GONE);
            linear_favo.setVisibility(View.VISIBLE);
        } else {
            //means from menu items
            supplierImage.setVisibility(View.GONE);
            linear_cart_info.setVisibility(View.GONE);
            switch_active_inactive.setVisibility(View.VISIBLE);
            linear_add_to_cart.setVisibility(View.GONE);
            linear_delete.setVisibility(View.VISIBLE);
            linear_edit.setVisibility(View.VISIBLE);
            linear_favo.setVisibility(View.GONE);
        }
    }

    private void setUpPager(ArrayList<String> arrayImages) {
        ViewPagerDetailsPageAdapter viewPagerAdapter = new ViewPagerDetailsPageAdapter(this, arrayImages);
        viewPager.setAdapter(viewPagerAdapter);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        sliderDotspanel.removeAllViews();
        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void add_qty_dialog(final boolean isChecked) {
        final Dialog dialog = new Dialog(ProductDetail.this);
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
                        CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
                    }
                } else {
                    CommonMethods.showtoast(ProductDetail.this, getResources().getString(R.string.enter_qty));
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
        final ProgressDialog progressDialog = new ProgressDialog(ProductDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetProductDetail(item_id);
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
                        arrayAttributes = new ArrayList<>();
                        arraysubAttributes = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject obj_i = object.getJSONObject("success");
                            fee = obj_i.getString("fee");
                            JSONObject obj = obj_i.getJSONObject("data");
                            pro_id = obj.getString("product_id");
                            collected_amount = obj.getString("collected_amount");
                            text_dname.setText(obj.getString("product_name"));
                            text_avialable.setText(obj.getString("quantity"));
                            status = obj.getString("status");
                            if (status.equalsIgnoreCase("active")) {
                                firstTime = true;
                            }
                            resetStatus(status);
                            text_customerviews.setText(obj.getString("customer_view"));
                            text_delivered.setText(obj.getString("delivered_order"));
                            String des = obj.getString("product_description");
                            productDes = des;

//                            try {
//                                parseHTML(des);
//                            } catch (XmlPullParserException e) {
//                                e.printStackTrace();
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }

//                            if (des.contains("<p>")) {
//                                Html.fromHtml(Html.fromHtml(des).toString());
//                                String[] separated = des.split("<p>");
//                                String a = separated[0]; // this will contain "Fruit"
//                                String ab = separated[1]; // this will contain " they taste good"
//                                String[] separated1 = ab.split("</p>");
//                                String aa = separated1[0]; // this will contain "Fruit"
//                                text_description.setText(aa);
//                            } else {

                            //text_description.setText((Html.fromHtml(des).toString()));
                            //text_description.setText(des);
                            // }

//                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                                text_description.setText(Html.fromHtml(des, Html.FROM_HTML_MODE_LEGACY));
//                            } else {
//                                text_description.setText(Html.fromHtml( des));
//                            }
                            //text_description.setText("\u2022 " + TextUtils.join(System.getProperty("line.separator") + "• " , des ));

                            text_description.loadData(des, "text/html; charset=utf-8", "UTF-8");
//                            if (!des.equals("")) {
//                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                                    text_description.setText(Html.fromHtml(des, Html.FROM_HTML_MODE_LEGACY));
//                                } else {
//                                    text_description.setText(Html.fromHtml(des));
//                                }
//                            }

                            tvDisprice.setPaintFlags(tvDisprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            currency = obj.getString("currency");
                            price = obj.getString("product_price");
                            discount = obj.getString("discount");
                            if (discount != null && !discount.isEmpty() && !discount.equals("null") && !discount.equalsIgnoreCase("0") && !discount.equalsIgnoreCase("0.0")) {
                                try {
                                    double priceDouble = Double.parseDouble(price);
                                    double discountPercentage = (priceDouble * Double.parseDouble(discount)) / 100;
                                    double discountPrice = priceDouble - discountPercentage;
                                    tvDisprice.setText(obj.getString("currency") + " " + price);
                                    text_dprice.setText(obj.getString("currency") + " " + new DecimalFormat("#.##").format(discountPrice));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                tvDisprice.setVisibility(View.GONE);
                                text_dprice.setText(obj.getString("currency") + " " + obj.getString("product_price"));
                            }

                            //show rating and one comment
                            ratingBar.setRating(Float.parseFloat(obj.getString("rate_point")));
                            text_review.setText("(" + obj.getString("rate_point") + " Rating)");

                            cart_qty = obj.getInt("cart_qty");
                            text_qty.setText(String.valueOf(cart_qty));
                            edit_link = CommonMethods.checkNull(obj.getString("edit_link"));
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


//                            time_since = obj.getString("dish_since");
                            image = obj.getString("main_image");
                            if (!image.equalsIgnoreCase("")) {
                                Picasso.get().load(image).placeholder(R.drawable.demo).into(imageDish);
                            }

                            arrayImages = new ArrayList<>();
                            if (!image.equalsIgnoreCase("null") && !image.equalsIgnoreCase("")) {
                                arrayImages.add(image);
                            }
                            JSONArray Imgarray = obj.getJSONArray("other_images");
                            for (int j = 0; j < Imgarray.length(); j++) {
                                arrayImages.add(Imgarray.getString(j));
                            }
                            if (arrayImages.size() != 0) {
                                setUpPager(arrayImages);
                            }

                            //setup attributes
                            JSONArray attr_array = obj.getJSONArray("product_attributes");
                            for (int i = 0; i < attr_array.length(); i++) {
                                JSONObject att_obj = attr_array.getJSONObject(i);
                                product_attributes attributes = new product_attributes();
                                attributes.setAttribute_id(att_obj.getString("attribute_id"));
                                String checkType;
                                attributes.setAttribute_name(att_obj.getString("name"));
                                checkType = att_obj.getString("name");
                                if (checkType.equalsIgnoreCase("Size")) {
                                    SizeoptionId = att_obj.getString("option_id");
                                }
                                if (checkType.equalsIgnoreCase("Color")) {
                                    ColoroptionId = att_obj.getString("option_id");
                                }
                                if (checkType.equalsIgnoreCase("Configuration")) {
                                    ConfigurationptionId = att_obj.getString("option_id");
                                }
                                attributes.setAttribute_type(att_obj.getString("type"));
                                attributes.setMenu_attr(att_obj.getString("menu_attr"));
                                attributes.setIs_required(att_obj.getString("is_required"));
                                attributes.setOption_id(att_obj.getString("option_id"));
                                JSONArray jsonArray = att_obj.getJSONArray("option_data");
                                arraysubAttributes = new ArrayList<>();
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject obj_op = jsonArray.getJSONObject(j);
                                    subAttributes attributes1 = new subAttributes();
                                    attributes1.setSubAttr_main_name(att_obj.getString("name"));
                                    attributes1.setSubAttr_name(obj_op.getString("name"));
                                    attributes1.setSubAttr_quantity(obj_op.getString("quantity"));
                                    attributes1.setSubAttr_subtract(obj_op.getString("subtract"));
                                    attributes1.setSubAttr_price(obj_op.getString("price"));
                                    attributes1.setSubAttr_price_prefix(obj_op.getString("price_prefix"));
                                    attributes1.setOption_id(att_obj.getString("option_id"));
                                    arraysubAttributes.add(attributes1);

                                    if (checkType.equalsIgnoreCase("Size")) {
                                        arraySize.add(attributes1);
                                    } else if (checkType.equalsIgnoreCase("Color")) {
                                        arrayColor.add(attributes1);
                                    } else {
                                        arrayCon.add(attributes1);
                                    }
                                }
                                attributes.setArraySubAttributies(arraysubAttributes);
                                arrayAttributes.add(attributes);
                            }
                            setupRecyclerColorSize();
                            //setAttributes();

                            //categories
                            JSONArray category_array = obj.getJSONArray("categories");
                            for (int i = 0; i < category_array.length(); i++) {
                                JSONObject cat_obj = category_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(cat_obj.getString("category_id"));
                                JSONObject d_obj = cat_obj.getJSONObject("detail");
                                nameID.setName(d_obj.getString("category_name"));
                                arrayCategory.add(nameID);
                            }
                            setcategory();
                            //supplier_id
                            JSONObject supplier_obj = obj_i.getJSONObject("supplier");
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
                            CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.server_error));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_back:
                ProductDetail.this.finish();
                break;

            case R.id.linear_edit:
                Intent i = new Intent(ProductDetail.this, EditProduct_Activity.class);
                i.putExtra("pro_id", pro_id);
                i.putExtra("pro_name", text_dname.getText().toString().trim());
                i.putExtra("pro_description", productDes);
                i.putExtra("pro_price", price);
                i.putExtra("pro_image", image);
                i.putExtra("pro_qty", text_avialable.getText().toString().trim());
                i.putExtra("edit_link", edit_link);
                i.putExtra("fee", fee);
                i.putExtra("collected_amount", collected_amount);
                i.putExtra("discount", discount);
                startActivity(i);

                break;

            case R.id.linear_delete:
                show_dialog();
                break;

            case R.id.text_review:

                break;

            case R.id.text_loadmore:
                Intent intent = new Intent(ProductDetail.this, FeedbackListActivity.class);
                intent.putExtra("isSupplierDetail", false);
                intent.putExtra("fromProductDetail", true);
                intent.putExtra("id", pro_id);
                startActivity(intent);
                break;

            case R.id.text_viewcart:
                Intent i_cart = new Intent(ProductDetail.this, MainActivity_NavDrawer.class);
                i_cart.putExtra("OpenFrag", ConstantValues.OPENCART);
                startActivity(i_cart);
                ActivityCompat.finishAffinity(ProductDetail.this);
                break;

            case R.id.linear_add_to_cart:
                //TODO : LOGGEDIN CHECK
//                if (sessionManager.isLoggedIn()) {
                //check if attribute then scroll to bottom section
                linear_attribues.requestFocus();
                linear_qty.setVisibility(View.VISIBLE);
                text_add_cart.setVisibility(View.GONE);
                linear_add_to_cart.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_bottom_round_coner_orange));
//                } else {
//                    CommonMethods.ShowLoginDialog(ProductDetail.this);
//                }
                break;

            case R.id.linear_text_done:
                //update qty
                if (text_qty.getText().toString().equalsIgnoreCase("0")) {
                    CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.enter_qty));
                } else {
                    if (CommonMethods.checkConnection()) {
                        if (validation()) {
                            addToCart(pro_id, String.valueOf(cart_qty), "", true);
                        }

                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
                    }
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

            case R.id.supplierImage:
                Intent i_supplier = new Intent(ProductDetail.this, SupplierDetailActivity.class);
                i_supplier.putExtra("supplier_id", supplier_id);
                i_supplier.putExtra("supplier_name", supplier_name);
                i_supplier.putExtra("supplier_rating", supplier_rating);
                i_supplier.putExtra("supplier_location", supplier_location);
                i_supplier.putExtra("supplier_url", supplier_url);
                startActivity(i_supplier);
                break;

        }
    }

    private boolean validation() {

        if (arraySize != null && !arraySize.isEmpty()) {
            if (SizeName == null || SizeName.isEmpty()) {
                Toast.makeText(this, "Please select size", Toast.LENGTH_SHORT).show();
                return false;
            }

        } else if (arrayColor != null && !arrayColor.isEmpty()) {
            if (ColorName == null || ColorName.isEmpty()) {
                Toast.makeText(this, "Please select color", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else if (arrayCon != null && !arrayCon.isEmpty()) {
            if (ConfigurationName == null || ConfigurationName.isEmpty()) {
                Toast.makeText(this, "Please select configuration", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        apiInterface = ApiClient.getInstance().getClient();
//        if (CommonMethods.checkConnection()) {
//            getData();
//        } else {
//            CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
//        }

    }

    private void show_dialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                ProductDetail.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

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
                    CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
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
            linear_category.setVisibility(View.VISIBLE);
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
        final ProgressDialog progressDialog = new ProgressDialog(ProductDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        Call<JsonElement> calls = apiInterface.UpdateProductStatus(pro_id, qty, updated_status, text_dname.getText().toString(), productDes, price);

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
                            CommonUtilFunctions.success_Alert_Dialog(ProductDetail.this, "Quantity updated sucessfully!");
//                            reload_screen();

                        } else {
                            resetStatus(status);
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, obj.getString("msg"));
                        }
                    } else {
                        resetStatus(status);
                        CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.server_error));
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
        final ProgressDialog progressDialog = new ProgressDialog(ProductDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.DeleteProductItem(item_id);

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
                            ProductDetail.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, obj.getString("msg"));
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
                CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.server_error));
            }
        });

    }

    private void setupRecyclerColorSize() {
        String attrubute_color = "";
        String attrubute_size = "";
        String attrubute_confi = "";
        if (arrayAttributes.size() == 0) {
            linear_attribues.setVisibility(View.GONE);
            llSize.setVisibility(View.GONE);
        } else {
            linear_attribues.setVisibility(View.GONE);
            llSize.setVisibility(View.VISIBLE);
        }
//        arrayColor = new ArrayList<>();
//        arraySize = new ArrayList<>();
//        arrayCon = new ArrayList<>();
        for (int i = 0; i < arrayAttributes.size(); i++) {
            if (arrayAttributes.get(i).getAttribute_name().equalsIgnoreCase("Size")) {
                //arraySize.addAll(arraysubAttributes);
                attrubute_size = arrayAttributes.get(i).getAttribute_type();

            } else if (arrayAttributes.get(i).getAttribute_name().equalsIgnoreCase("Configuration")) {
                //arrayColor.addAll(arraysubAttributes);
                attrubute_confi = arrayAttributes.get(i).getAttribute_type();
            } else {
                attrubute_color = arrayAttributes.get(i).getAttribute_type();
            }
        }

        if (arraySize != null && arraySize.size() > 0) {
            rvSize.setLayoutManager(new LinearLayoutManager(this));
            sizeAdapter = new ProductSizeSelectAdapter(this, arraySize, this,currency, attrubute_size);
            rvSize.setAdapter(sizeAdapter);
            rvSize.setVisibility(View.VISIBLE);
            tvCate.setVisibility(View.VISIBLE);
        } else {
            rvSize.setVisibility(View.GONE);
            tvCate.setVisibility(View.GONE);
        }
        if (arrayColor != null && arrayColor.size() > 0) {
            rvColor.setLayoutManager(new LinearLayoutManager(this));
            colorAdapter = new ProductDetailColorselectionAdatpter(this, arrayColor, this,currency, attrubute_color);
            rvColor.setAdapter(colorAdapter);
            tvColor.setVisibility(View.VISIBLE);
            rvColor.setVisibility(View.VISIBLE);
        } else {
            tvColor.setVisibility(View.GONE);
            rvColor.setVisibility(View.GONE);
        }
        if (arrayCon != null && arrayCon.size() > 0) {
            rvConfiguration.setLayoutManager(new LinearLayoutManager(this));
            confriAdapter = new ProductDetailConfrigationAdatpter(this, arrayCon, this,currency, attrubute_confi);
            rvConfiguration.setAdapter(confriAdapter);
            tvConfiguration.setVisibility(View.VISIBLE);
            rvConfiguration.setVisibility(View.VISIBLE);
        } else {
            tvConfiguration.setVisibility(View.GONE);
            rvConfiguration.setVisibility(View.GONE);
        }


    }


    @Override
    public void onColorClick(int position, String name, String attri_type,boolean ischeck) {

        attrColorType = attri_type;
        if (attri_type.equalsIgnoreCase("checkbox")) {

            ColorName = name;

            if(ischeck) {
                attriColortype.add(name);
            }else {
                attriColortype.remove(name);
            }
        } else {
            for (int i = 0; i < arrayColor.size(); i++) {
                arrayColor.get(i).setSelected(false);
            }
            arrayColor.get(position).setSelected(true);
            colorAdapter.notifyDataSetChanged();
            ColorName = name;
        }

    }

    @Override
    public void onConfrigationClick(int position, String name, String attri_type,boolean ischeck) {

        attrConfiType = attri_type;
        if (attri_type.equalsIgnoreCase("checkbox")) {
            if(ischeck) {
                attriConfitype.add(name);
            }else {
                attriConfitype.remove(name);
            }
            ConfigurationName = name;

        } else {
            ConfigurationName = name;
            for (int i = 0; i < arrayCon.size(); i++) {
                arrayCon.get(i).setSelected(false);
            }
            arrayCon.get(position).setSelected(true);
            confriAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onSizeClick(int position, String name, String attri_type,boolean ischeck) {
        attrSizeType = attri_type;
        if (attri_type.equalsIgnoreCase("checkbox")) {
            if(ischeck) {
                attriSizetype.add(name);
            }else {
                attriSizetype.remove(name);
            }
            SizeName = name;
        } else {
            for (int i = 0; i < arraySize.size(); i++) {
                arraySize.get(i).setSelected(false);
            }
            arraySize.get(position).setSelected(true);
            sizeAdapter.notifyDataSetChanged();
            SizeName = name;
        }

    }


    private void setAttributes() {
        if (arrayAttributes.size() == 0) {
            linear_attribues.setVisibility(View.GONE);
        } else {
            linear_attribues.setVisibility(View.VISIBLE);
        }
        arrayspinner = new ArrayList<>();
        arrayradio = new ArrayList<>();
        array_checkbox = new ArrayList<>();
        for (int i = 0; i < arrayAttributes.size(); i++) {
            View view = getLayoutInflater().inflate(R.layout.layout_attributes_main, null);
            TextView textview = (TextView) view.findViewById(R.id.textview);
            textview.setText(arrayAttributes.get(i).getAttribute_name());
            LinearLayout linear_spinner = (LinearLayout) view.findViewById(R.id.linear_spinner);
            LinearLayout linear_checkbox = (LinearLayout) view.findViewById(R.id.linear_checkbox);
            LinearLayout linear_radio = (LinearLayout) view.findViewById(R.id.linear_radio);
            final Spinner attribute_spinner = (Spinner) view.findViewById(R.id.attribute_spinner);
            ExpandableHeightGridView checkbox_grid = (ExpandableHeightGridView) view.findViewById(R.id.checkbox_grid);
            checkbox_grid.setExpanded(true);
            final ExpandableHeightGridView radio_grid = (ExpandableHeightGridView) view.findViewById(R.id.radio_grid);
            radio_grid.setExpanded(true);
            ImageView image_attribute_arrow = (ImageView) view.findViewById(R.id.image_attribute_arrow);
            image_attribute_arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    attribute_spinner.performClick();
                }
            });

            subAttributeAdapter adapter = new subAttributeAdapter(ProductDetail.this, arrayAttributes.get(i).getArraySubAttributies()
                    , currency, arrayAttributes.get(i).getAttribute_type(), isFromHome);
            if (arrayAttributes.get(i).getAttribute_type().equalsIgnoreCase(ConstantValues.attributeTypeSpinner)) {
                linear_spinner.setVisibility(View.VISIBLE);
                linear_checkbox.setVisibility(View.GONE);
                linear_radio.setVisibility(View.GONE);
                arrayspinner = arrayAttributes.get(i).getArraySubAttributies();
                attribute_spinner.setAdapter(adapter);
            } else if (arrayAttributes.get(i).getAttribute_type().equalsIgnoreCase(ConstantValues.attributeTyperadio)) {
                linear_spinner.setVisibility(View.GONE);
                linear_checkbox.setVisibility(View.GONE);
                linear_radio.setVisibility(View.VISIBLE);
                arrayradio = arrayAttributes.get(i).getArraySubAttributies();
                radio_grid.setAdapter(adapter);
            } else if (arrayAttributes.get(i).getAttribute_type().equalsIgnoreCase(ConstantValues.attributeTypecheckbox)) {
                linear_spinner.setVisibility(View.GONE);
                linear_checkbox.setVisibility(View.VISIBLE);
                linear_radio.setVisibility(View.GONE);
                array_checkbox = arrayAttributes.get(i).getArraySubAttributies();
                checkbox_grid.setAdapter(adapter);
            }

            attribute_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    ArrayList<subAttributes> arrayList = resetAllSelectedValues(arrayspinner);
                    arrayspinner = arrayList;
                    if (!arrayspinner.get(position).isSelected()) {
                        arrayspinner.get(position).setSelected(true);
                    } else {
                        arrayspinner.get(position).setSelected(false);
                    }
                    Log.e("selected_spinner", arrayspinner.get(position).getSubAttr_main_name()
                            + "---------" + arrayspinner.get(position).getSubAttr_name()
                            + "---------" + arrayspinner.get(position).isSelected());
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            radio_grid.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);
            radio_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LinearLayout layout = (LinearLayout) view;
                    RadioButton radioButton = (RadioButton) layout.findViewById(R.id.radiobutton);
                    ArrayList<subAttributes> arrayList = resetAllSelectedValues(arrayradio);
                    arrayradio = arrayList;
                    if (!arrayradio.get(position).isSelected()) {
                        arrayradio.get(position).setSelected(true);
                    } else {
                        arrayradio.get(position).setSelected(false);
                    }
                    arrayradio.get(position).setRadioButton(radioButton);

                    if (isFromHome) {
                        radioButton.setChecked(arrayradio.get(position).isSelected());
                        size = radioButton.getText().toString();
                        sizeArray[1] = size.split("\\(")[0];
                    }
                    Log.e("selected_gird", arrayradio.get(position).getSubAttr_main_name() +
                            "---------" + arrayradio.get(position).getSubAttr_name() +
                            "---" + arrayradio.get(position).isSelected());
                }
            });


            checkbox_grid.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
            checkbox_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    LinearLayout layout = (LinearLayout) view;
                    CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox);
                    if (!array_checkbox.get(position).isSelected()) {
                        array_checkbox.get(position).setSelected(true);
                    } else {
                        array_checkbox.get(position).setSelected(false);
                    }
                    if (isFromHome) {
                        checkBox.setChecked(array_checkbox.get(position).isSelected());
                    }
                    Log.e("selected_checkbox", array_checkbox.get(position).getSubAttr_main_name() + "---------" + array_checkbox.get(position).getSubAttr_name() + "---" + array_checkbox.get(position).isSelected());
                }
            });

            linear_attribues.addView(view);
        }
    }

    private ArrayList<subAttributes> resetAllSelectedValues(ArrayList<subAttributes> arrayReseted) {
        for (int i = 0; i < arrayReseted.size(); i++) {
            if (arrayReseted.get(i).isSelected()) {
                arrayReseted.get(i).setSelected(false);
                if (arrayReseted.get(i).getRadioButton() != null) {
                    arrayReseted.get(i).getRadioButton().setChecked(false);
                }
            }
        }
        return arrayReseted;
    }

    private void addToCart(String menu_id, final String qty, String confirm, boolean isAdd) {
        final ProgressDialog progressDialog = new ProgressDialog(ProductDetail.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        //add all values here

        Map<String, String> data = getAllParameters(menu_id, qty);

        Call<JsonElement> calls;
        if (isAdd) {
            calls = apiInterface.AddUpdateDeleteCartItem(data);
        } else {
            calls = apiInterface.AddConfirm(data, confirm);
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
                                //isAllowGuest = "true";
                                //text and hit api
                                //showcleardata(ProductDetail.this, object.getString("confirm"), qty);
                                showClearCartDialog(ProductDetail.this, object.getString("confirm"), qty);
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
                                    text_add_cart.setText(getResources().getString(R.string.addtocart));
                                    linear_add_to_cart.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_bottom_round_coner_orange));
//                                    text_add_cart.setText(qty + " " + getResources().getString(R.string.addedtocart));
//                                    linear_add_to_cart.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_round_coner_green));

                                }

                                if (object.has("cart_items")) {
                                    JSONObject cart_obj = object.getJSONObject("cart_total");
                                    if (!object.getString("cart_items").equalsIgnoreCase("")) {
                                        linear_cart_info.setVisibility(View.VISIBLE);
                                        text_items.setText(object.getString("cart_items") + " Items");
                                        sessionManager.save_cart_item(object.getString("cart_items"));
                                        text_total_price.setText(cart_obj.getString("currency") + " " + cart_obj.getString("total"));
                                        // text_items.setText(object.getString("cart_items") + " Items");
                                        //text_total_price.setText(cart_obj.getString("currency") + " " + cart_obj.getString("total"));
                                        text_items.setVisibility(View.GONE);
                                        text_total_price.setText(object.getString("cart_items") + " Product Added");

                                    } else {
                                        linear_cart_info.setVisibility(View.GONE);
                                    }
                                } else {
                                    linear_cart_info.setVisibility(View.GONE);
                                }
                                success_Alert_Dialog(ProductDetail.this, object.getString("success"));
                                //CommonUtilFunctions.success_Alert_Dialog(ProductDetail.this, object.getString("success"));
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, obj.getString("msg"));
                        }
                    } else {
//                        Error_Alert_Dialog(HomeItemDetail.this, "Change", qty);
                        CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.server_error));
            }
        });
    }


    private void showcleardata(Context context, String message, final String qty) {

        if (sessionManager.getSupplierInfo() != null && !sessionManager.getSupplierInfo().equalsIgnoreCase("")) {
            if (supplier_id.equals(sessionManager.getSupplierInfo())) {
                showClearCartDialog(ProductDetail.this, message, qty);
            }
        } else {
            sessionManager.saveSupplierInfo(supplier_id);

        }
    }


    private Map<String, String> getAllParameters(String menu_id, String qty) {
        Map<String, String> data = new HashMap<>();
        data.put("menu_id", menu_id);
        data.put("quantity", qty);

        if (ColorName != null && !ColorName.isEmpty()) {
            if(attrColorType.equalsIgnoreCase("checkbox")){
                data.put("product_attributes" + "[" + ColoroptionId + "]", attriColortype.toString());
            }else {
                data.put("product_attributes" + "[" + ColoroptionId + "]", ColorName);
            }

        }
        if (SizeName != null && !SizeName.isEmpty()) {
            if(attrSizeType.equalsIgnoreCase("checkbox")) {
                data.put("product_attributes" + "[" + SizeoptionId + "]", attriSizetype.toString());
            }else {
                data.put("product_attributes" + "[" + SizeoptionId + "]", SizeName);
            }
        }
        if (ConfigurationName != null && !ConfigurationName.isEmpty()) {
            if(attrConfiType.equalsIgnoreCase("checkbox")) {
                data.put("product_attributes" + "[" + ConfigurationptionId + "]",  attriConfitype.toString());
            }else {
                data.put("product_attributes" + "[" + ConfigurationptionId + "]", ConfigurationName);
            }

        }
        for (int i = 0; i < arrayspinner.size(); i++) {
            if (arrayspinner.get(i).isSelected()) {
                data.put("product_attributes" + "[" + arrayspinner.get(i).getOption_id() + "]", arrayspinner.get(i).getSubAttr_name());
            }
        }

//        for (int i = 0; i < arrayradio.size(); i++) {
//            if (arrayradio.get(i).isSelected()) {
//                //sizeArray[0] = arrayradio.get(i).getSubAttr_name();
//                //data.put("product_attributes" + "[" + arrayradio.get(i).getOption_id() + "]", Arrays.toString (sizeArray));
//                data.put("product_attributes" + "[" + arrayradio.get(i).getOption_id() + "]", arrayradio.get(i).getSubAttr_name());
//                //data.put("product_attributes" + "[" + arrayradio.get(i).getOption_id() + "]", size);
//            }
//        }

//        for (int i = 0; i < array_checkbox.size(); i++) {
//            if (array_checkbox.get(i).isSelected()) {
//                data.put("product_attributes" + "[" + array_checkbox.get(i).getOption_id() + "]" + "[]", array_checkbox.get(i).getSubAttr_name());
//            }
//        }
        return data;
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
                    addToCart(pro_id, qty, "1", false);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
                }

            }
        });
        alertDialog.show();
    }

    public void success_Alert_Dialog(final Context context, String message) {
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
                alertDialog.dismiss();

                if (sessionManager.getSupplierInfo() != null && !sessionManager.getSupplierInfo().equalsIgnoreCase("")) {
                    if (supplier_id.equals(sessionManager.getSupplierInfo())) {
                        isAllowGuest = "false";
                    } else {
                        isAllowGuest = "true";
                    }
                } else {
                    sessionManager.saveSupplierInfo(supplier_id);
                    isAllowGuest = "true";
                }

                if (isAllowGuest.equalsIgnoreCase("true")) {
                    isAllowGuest = "false";
                    showDiffSellerLayout(context, supplier_id);
                }

                if (isAllowGuest.equalsIgnoreCase("true")) {
                    isAllowGuest = "false";
                    showDiffSellerLayout(context, supplier_id);
                }

            }
        });

        // Showing Alert Message

        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    // forDiffrentsupllier..
    public void showDiffSellerLayout(final Context context, final String sellerId) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_show_different_seller_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView tvAddMore = dialog.findViewById(R.id.tvAddMore);
        TextView tvgocart = dialog.findViewById(R.id.tvgocart);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        tvgocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_cart = new Intent(context, MainActivity_NavDrawer.class);
                i_cart.putExtra("OpenFrag", ConstantValues.OPENCART);
                context.startActivity(i_cart);
                dialog.dismiss();
            }
        });

        tvAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i_menu = new Intent(context, SupplierMenuActivity.class);
                i_menu.putExtra("supplier_id", sellerId);
                context.startActivity(i_menu);
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void showClearCartDialog(Context context, String message, final String qty) {
        final Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_less_amount_checkout_layout);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        TextView tvShowMsg = dialog.findViewById(R.id.tvShowMsg);
        TextView tvheader = dialog.findViewById(R.id.rvheader);
        TextView tvAddMore = dialog.findViewById(R.id.tvAddMore);
        TextView tvyes = dialog.findViewById(R.id.tvyes);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);
        tvheader.setText("Clear Cart");
        tvShowMsg.setText(message);


        tvyes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                if (CommonMethods.checkConnection()) {
                    addToCart(pro_id, qty, "1", false);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(ProductDetail.this, getResources().getString(R.string.internetconnection));
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void parseHTML(String html) throws XmlPullParserException, IOException, JSONException {

        String tag = "";
        String responseJSON = "";

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(html));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                tag = xpp.getName();

                System.out.println("Start tag " + xpp.getName());
            } else if (eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag " + xpp.getName());
            } else if (eventType == XmlPullParser.TEXT) {
                if (tag.equalsIgnoreCase("li")) {
                    responseJSON = xpp.getText();
                    //// JSONObject object = new JSONObject(responseJSON);

                    Log.e("VeeR", " .. test.." + responseJSON);
//                    JSONObject successObject = object.getJSONObject("success");
//                    String code = object.getString("code");
//                    String orderId = object.getString("data");
//
//                    if (code.equalsIgnoreCase("200")) {
//                        //subscribePLan();
//                        Log.e("Ravi find Code api "," "+code);
//
//
//                    } else {
//                        Toast.makeText(this, "error1", Toast.LENGTH_SHORT).show();
//                    }


                }
                System.out.println("Text " + xpp.getText());
            }
            eventType = xpp.next();
        }
    }


}
