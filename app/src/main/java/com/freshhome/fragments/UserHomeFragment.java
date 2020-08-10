package com.freshhome.fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.models.SlideModel;
import com.freshhome.AdapterClass.HomeAdapter;
import com.freshhome.AdapterClass.RecyclerSupplierAdapter;
import com.freshhome.AdapterClass.RecyclerUserHomeAdapter;
import com.freshhome.AdapterClass.SlidingImage_Adapter;
import com.freshhome.AdapterClass.ViewPagerAdapter;
import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.R;
import com.freshhome.SupplierDetailActivity;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.categories;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.viewpagerindicator.CirclePageIndicator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freshhome.MainActivity_NavDrawer.heading;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserHomeFragment extends Fragment implements View.OnClickListener, HomeAdapter.OnUpdateBannerListener {
    CirclePageIndicator indicator;
    RecyclerView recyclerCategories, recyclerSuppliers, recyclerOrderNow, recyclerlatestproducts, recyclerbestselling;
    ArrayList<HomeItem> arrayHomeList, arrayLatestProduct, arrayBestSelling, arrayOrderNow,arrayImagesPager;
    ArrayList<categories> arrayCategories, arraySupplier;
    ViewPager viewPager;
    LinearLayout sliderDotspanel, linear_order_now, linear_kitchens, linear_bestselling, linear_latestpro;
    private int dotscount;
    private ImageView[] dots;
    Timer timer;
    public int position = 0;
    ArrayList<String> arrayImages;
    TextView text_lviewall, text_bviewall, text_sviewall,
            text_oviewall,tvsearch,tvCartCount,tvNoFound;
    ApiInterface apiInterface;

    ImageButton btn_search;
    ImageView img_cart;
    public static UserSessionManager sessionManager;

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static int NUM_PAGES = 0;
    private ArrayList ImagesArray = new ArrayList();

    private static final Integer[] IMAGES=

            {R.drawable.cal,R.drawable.buy,R.drawable.building};

    public UserHomeFragment() {
        // Required empty public constructor
    }

    ImageSlider image_slider;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_user_home, container, false);
        sessionManager = new UserSessionManager(getActivity());
        apiInterface = ApiClient.getInstance().getClient();
        arrayCategories = new ArrayList<>();
        arrayImages = new ArrayList<>();
        arraySupplier = new ArrayList<>();
        arrayHomeList = new ArrayList<>();
        arrayBestSelling = new ArrayList<>();
        arrayOrderNow = new ArrayList<>();
        arrayLatestProduct = new ArrayList<>();
        arrayImagesPager = new ArrayList<>();

        image_slider = (ImageSlider) v.findViewById(R.id.image_slider);

        tvsearch = (TextView) v.findViewById(R.id.tvsearch);
        tvCartCount = (TextView) v.findViewById(R.id.tvCartCount);
        tvNoFound = (TextView) v.findViewById(R.id.tvNoFound);
        tvsearch.setOnClickListener(this);
        img_cart = (ImageView) v.findViewById(R.id.img_cart);
        btn_search = (ImageButton) v.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);
        img_cart.setOnClickListener(this);

        text_lviewall = (TextView) v.findViewById(R.id.text_lviewall);
        text_lviewall.setOnClickListener(this);
        text_bviewall = (TextView) v.findViewById(R.id.text_bviewall);
        text_bviewall.setOnClickListener(this);
        text_sviewall = (TextView) v.findViewById(R.id.text_sviewall);
        text_sviewall.setOnClickListener(this);
        text_oviewall = (TextView) v.findViewById(R.id.text_oviewall);
        text_oviewall.setOnClickListener(this);

        sliderDotspanel = (LinearLayout) v.findViewById(R.id.SliderDots);
        viewPager = (ViewPager) v.findViewById(R.id.pager);
        indicator = (CirclePageIndicator) v.findViewById(R.id.indicator);
//        arrayImages.add("https://freshhomee.com//web//uploads//users//327c028c3c6f3e4f252b2a6a4c0fefb7.jpg");
//        arrayImages.add("https://freshhomee.com//web//uploads//users//327c028c3c6f3e4f252b2a6a4c0fefb7.jpg");
//        arrayImages.add("https://freshhomee.com//web//uploads//users//327c028c3c6f3e4f252b2a6a4c0fefb7.jpg");
//        arrayImages.add("https://freshhomee.com//web//uploads//users//327c028c3c6f3e4f252b2a6a4c0fefb7.jpg");
//        setUpPager(arrayImages);

        recyclerSuppliers = (RecyclerView) v.findViewById(R.id.recyclerSuppliers);
        recyclerOrderNow = (RecyclerView) v.findViewById(R.id.recyclerOrderNow);
        recyclerlatestproducts = (RecyclerView) v.findViewById(R.id.recyclerlatestproducts);
        recyclerbestselling = (RecyclerView) v.findViewById(R.id.recyclerbestselling);
        recyclerCategories = (RecyclerView) v.findViewById(R.id.recyclerCategories);
        linear_order_now = (LinearLayout) v.findViewById(R.id.linear_order_now);
        linear_kitchens = (LinearLayout) v.findViewById(R.id.linear_kitchens);
        linear_bestselling = (LinearLayout) v.findViewById(R.id.linear_bestselling);
        linear_latestpro = (LinearLayout) v.findViewById(R.id.linear_latestpro);

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            String myfrom = bundle.getString("deeplink");
            if (myfrom != null) {
                if (myfrom.equalsIgnoreCase("deeplink")) {
                    String myValue = bundle.getString("supplier_id");
                    Intent intent = new Intent(getActivity(), SupplierDetailActivity.class);
                    intent.putExtra("supplier_id", myValue);
                    startActivity(intent);
                }
            }
        }

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
        Call<JsonElement> calls = apiInterface.getHomeData();
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
                            arrayCategories = new ArrayList<>();
                            arrayLatestProduct = new ArrayList<>();
                            arrayBestSelling = new ArrayList<>();
                            arrayOrderNow = new ArrayList<>();
                            JSONArray jarray = object.getJSONArray("home_category");
                            for (int i = 0; i < jarray.length(); i++) {
                                JSONObject jobj = jarray.getJSONObject(i);
                                categories cat = new categories();
                                cat.setId(jobj.getString("home_category_id"));
                                cat.set_name(jobj.getString("name"));
                                cat.setimage(jobj.getString("image"));
                                arrayCategories.add(cat);
                            }

                            JSONArray jsarray = object.getJSONArray("online_kitchens");
                            for (int i = 0; i < jsarray.length(); i++) {
                                JSONObject jobj = jsarray.getJSONObject(i);
                                categories cat = new categories();
                                cat.setId(jobj.getString("id"));
                                cat.set_name(jobj.getString("name"));
                                cat.setimage(jobj.getString("profile_pic"));
                                cat.setAvailable(jobj.getString("availability"));
                                arraySupplier.add(cat);
                            }


                            JSONArray jLarray = object.getJSONArray("latest_products");
                            for (int i = 0; i < jLarray.length(); i++) {
                                JSONObject obj = jLarray.getJSONObject(i);
                                HomeItem item = new HomeItem();
                                item.setSupplier_id(obj.getString("supplier_id"));
                                item.setDish_id(obj.getString("product_id"));
                                item.setDish_description(obj.getString("product_description"));
                                item.setDish_image(obj.getString("main_image"));
                                item.setDish_name(obj.getString("product_name"));
                                item.setDish_price(obj.getString("product_price"));
                                item.setRate_point(obj.getString("rate_point"));
                                item.setIsFavo(obj.getString("is_fav"));
                                item.setFavo_count(obj.getString("fav_count"));
                                item.setUser_views(obj.getString("user_view"));
                                item.setCart_qty(obj.getInt("cart_qty"));
                                item.setDiscount(obj.getString("discount"));
                                //check attributes and if attributes then take him to detail screen else add to cart
                                if (obj.has("product_attributes")) {
                                    JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                    if (jAttribute.length() != 0) {
                                        item.setAttributes(true);
                                    } else {
                                        item.setAttributes(false);
                                    }
                                }
                                arrayLatestProduct.add(item);
                            }

                            JSONArray jBSarray = object.getJSONArray("best_selling_products");
                            for (int i = 0; i < jBSarray.length(); i++) {
                                JSONObject obj = jBSarray.getJSONObject(i);
                                HomeItem item = new HomeItem();
                                item.setSupplier_id(obj.getString("supplier_id"));
                                item.setDish_id(obj.getString("product_id"));
                                item.setDish_description(obj.getString("product_description"));
                                item.setDish_image(obj.getString("main_image"));
                                item.setDish_name(obj.getString("product_name"));
                                item.setDish_price(obj.getString("product_price"));
                                item.setRate_point(obj.getString("rate_point"));
                                item.setIsFavo(obj.getString("is_fav"));
                                item.setFavo_count(obj.getString("fav_count"));
                                item.setUser_views(obj.getString("user_view"));
                                item.setCart_qty(obj.getInt("cart_qty"));
                                item.setDiscount(obj.getString("discount"));
                                //check attributes and if attributes then take him to detail screen else add to cart
                                if (obj.has("product_attributes")) {
                                    JSONArray jAttribute = obj.getJSONArray("product_attributes");
                                    if (jAttribute.length() != 0) {
                                        item.setAttributes(true);
                                    } else {
                                        item.setAttributes(false);
                                    }
                                }
                                arrayBestSelling.add(item);
                            }


                            JSONArray jONarray = object.getJSONArray("order_now");
                            for (int i = 0; i < jONarray.length(); i++) {
                                JSONObject obj = jONarray.getJSONObject(i);
                                HomeItem item = new HomeItem();
                                item.setDish_id(obj.getString("dish_id"));
                                item.setDish_description(obj.getString("dish_description"));
                                item.setDish_image(obj.getString("dish_image"));
                                item.setDish_name(obj.getString("dish_name"));
                                item.setDish_price(obj.getString("dish_price"));
                                item.setRate_point(obj.getString("rate_point"));
                                item.setIsFavo(obj.getString("is_fav"));
                                item.setFavo_count(obj.getString("fav_count"));
                                item.setUser_views(obj.getString("user_view"));
                                item.setCart_qty(obj.getInt("cart_qty"));
                                item.setDiscount(obj.getString("discount"));
                                item.setAttributes(false);
                                arrayOrderNow.add(item);
                            }

                            JSONArray pagerarray = object.getJSONArray("banners");
                            for (int i = 0; i < pagerarray.length(); i++) {
                                JSONObject obj = pagerarray.getJSONObject(i);
                                HomeItem item = new HomeItem();
                                item.setImage(obj.getString("image"));
                                arrayImagesPager.add(item);
                                setUpPager(arrayImagesPager);
                               //sk setUpBanner(arrayImagesPager);
                                //init(arrayImagesPager);
                            }


                            if (arrayCategories.size() > 0) {
                                recyclerCategories.setLayoutManager(new GridLayoutManager(getActivity(), arrayCategories.size()));
                                recyclerCategories.setItemAnimator(new DefaultItemAnimator());
                                recyclerCategories.setNestedScrollingEnabled(false);
                                RecyclerUserHomeAdapter adapter = new RecyclerUserHomeAdapter(getActivity(), arrayCategories);
                                recyclerCategories.setAdapter(adapter);
                            }

                            if (arraySupplier.size() > 0) {
                                recyclerSuppliers.setLayoutManager(new GridLayoutManager(getActivity(), arraySupplier.size()));
                                recyclerSuppliers.setItemAnimator(new DefaultItemAnimator());
                                recyclerSuppliers.setNestedScrollingEnabled(false);
                                RecyclerSupplierAdapter adapter_supplier = new RecyclerSupplierAdapter(getActivity(), arraySupplier);
                                recyclerSuppliers.setAdapter(adapter_supplier);
                            } else {
                                linear_kitchens.setVisibility(View.GONE);
                            }

                            if (arrayLatestProduct.size() > 0) {
                                recyclerlatestproducts.setLayoutManager(new GridLayoutManager(getActivity(), arrayLatestProduct.size()));
                                recyclerlatestproducts.setItemAnimator(new DefaultItemAnimator());
                                recyclerlatestproducts.setNestedScrollingEnabled(false);
                                HomeAdapter adapter_latestp = new HomeAdapter(getActivity(), arrayLatestProduct, "2");
                                adapter_latestp.setOnUpdateBannerListener(UserHomeFragment.this);
                                recyclerlatestproducts.setAdapter(adapter_latestp);
                            } else {
                                linear_latestpro.setVisibility(View.GONE);
                            }

                            if (arrayBestSelling.size() > 0) {
                                recyclerbestselling.setLayoutManager(new GridLayoutManager(getActivity(), arrayBestSelling.size()));
                                recyclerbestselling.setItemAnimator(new DefaultItemAnimator());
                                recyclerbestselling.setNestedScrollingEnabled(false);
                                HomeAdapter adapter_best = new HomeAdapter(getActivity(), arrayBestSelling, "2");
                                adapter_best.setOnUpdateBannerListener(UserHomeFragment.this);
                                recyclerbestselling.setAdapter(adapter_best);
                            } else {
                                linear_bestselling.setVisibility(View.GONE);
                            }

                            if (arrayOrderNow.size() > 0) {
                                recyclerOrderNow.setLayoutManager(new GridLayoutManager(getActivity(), arrayOrderNow.size()));
                                recyclerOrderNow.setItemAnimator(new DefaultItemAnimator());
                                recyclerOrderNow.setNestedScrollingEnabled(false);
                                HomeAdapter adapter_ordernow = new HomeAdapter(getActivity(), arrayOrderNow, "1");
                                adapter_ordernow.setOnUpdateBannerListener(UserHomeFragment.this);
                                recyclerOrderNow.setAdapter(adapter_ordernow);
                                tvNoFound.setVisibility(View.GONE);
                                recyclerOrderNow.setVisibility(View.VISIBLE);
                            } else {
                                linear_order_now.setVisibility(View.VISIBLE);
                                tvNoFound.setVisibility(View.VISIBLE);
                                recyclerOrderNow.setVisibility(View.GONE);
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

    public void setUpBanner(ArrayList<HomeItem> arrayImages){
        List<SlideModel> imageList = new ArrayList<>();
        for(int i=0; i<arrayImages.size();i++){
            imageList.add(new SlideModel(arrayImages.get(i).getImage()));
        }
        image_slider.setImageList(imageList,true);
    }

    private void setUpPager(ArrayList<HomeItem> arrayImages) {
        SlidingImage_Adapter viewPagerAdapter = new SlidingImage_Adapter(getActivity(), arrayImages);
        viewPager.setAdapter(viewPagerAdapter);
        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        sliderDotspanel.removeAllViews();
        for (int i = 0; i < dotscount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.non_active_dot));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            sliderDotspanel.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for (int i = 0; i < dotscount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.non_active_dot));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.active_dot));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        if (arrayImages.size() > 1) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new scrollPager(), 0, 3000); // delay*/
        }

    }


    private void init(ArrayList<HomeItem> arrayImages) {
        for(int i=0;i<arrayImages.size();i++)
            ImagesArray.add(arrayImages.get(i).getImage());
        //mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new ViewPagerAdapter(getActivity(),ImagesArray));
        indicator.setViewPager(mPager);
        final float density = getResources().getDisplayMetrics().density;
        indicator.setRadius(5 * density);
        NUM_PAGES =arrayImages.size();
        // Auto start of viewpager
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == NUM_PAGES) {
                    currentPage = 0;
                }
                mPager.setCurrentItem(currentPage++, true);
            }
        };
        Timer swipeTimer = new Timer();
        swipeTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }
        }, 0, 3000);

// Pager listener over indicator

        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
            }
            @Override
            public void onPageScrolled(int pos, float arg1, int arg2) {
            }
            @Override
            public void onPageScrollStateChanged(int pos) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_lviewall:
                heading.setText(getResources().getString(R.string.latest_products));
                replace_fragment(getResources().getString(R.string.latest_products));
                break;

            case R.id.text_bviewall:
                heading.setText(getResources().getString(R.string.best_selling));
                replace_fragment(getResources().getString(R.string.best_selling));

                break;

            case R.id.text_sviewall:
                heading.setText(getResources().getString(R.string.order_sellers));
                replace_fragment(getResources().getString(R.string.order_kitchens));
                break;

            case R.id.text_oviewall:
                if (arrayOrderNow.size() > 0) {
                    heading.setText(getResources().getString(R.string.best_deals));
                    replace_fragment(getResources().getString(R.string.ordernow));
                }else {
                    Toast.makeText(getActivity(), "No menu added", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_search:
            case R.id.tvsearch:
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                BuyerSearchFragment frag_best = new BuyerSearchFragment();
                ft.replace(R.id.main_linear, frag_best);
                ft.addToBackStack(null);
                ft.commit();
                break;
            case R.id.img_cart:
                FragmentTransaction fragmentTransaction = getFragmentManager ().beginTransaction();
                UserCartFragment frag_cart = new UserCartFragment();
                fragmentTransaction.replace(R.id.main_linear, frag_cart);
                fragmentTransaction.commit();
                break;
        }
    }

    @Override
    public void onUpdateCardBanner(String cardItem, String currency) {
        Log.d("onUpadte","------------------------");
    }

    @Override
    public void onMoveToProductDetails(String itemId, String screenId) {
        Log.d("onUpadte","------------------------");
    }

    private class scrollPager extends TimerTask {
        @Override
        public void run() {
            viewPager.post(new Runnable() {

                @Override
                public void run() {
                    if (position == arrayImages.size()) {
                        position = 0;
                    }
                    viewPager.setCurrentItem(position);
                    position++;
                }
            });
        }
    }


    private void replace_fragment(String type) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ViewAllItemsFragment frag_best = new ViewAllItemsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type",type);
        frag_best.setArguments(bundle);
        ft.replace(R.id.main_linear, frag_best);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onResume() {
        super.onResume ( );
        MainActivity_NavDrawer.heading.setText(getString (R.string.home));
        if(sessionManager!=null){
            String itemCount = sessionManager.getCartItem ();
            if(itemCount!=null && !itemCount.isEmpty ()){
                //img_cart.setVisibility (View.VISIBLE);
                //tvCartCount.setVisibility (View.VISIBLE);
                tvCartCount.setText (itemCount);
            }else {
                //img_cart.setVisibility (View.GONE);
                //tvCartCount.setVisibility (View.GONE);
            }
        }
    }
}
