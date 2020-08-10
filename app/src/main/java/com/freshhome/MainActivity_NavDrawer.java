package com.freshhome;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.DriverModule.DriverNavDrawerActivity;
import com.freshhome.SalesModule.ActivitySalesNavDrawer;
import com.freshhome.fragments.ContactUsFragment;
import com.freshhome.fragments.FragmentAskForHelp;
import com.freshhome.fragments.InviteFriendFragment;
import com.freshhome.fragments.MenuFragment;
import com.freshhome.fragments.MyKitchenFragment;
import com.freshhome.fragments.MyWalletFragment;
import com.freshhome.fragments.ProductFragment;
import com.freshhome.fragments.SalesReportFragment;
import com.freshhome.fragments.SupplierProfileFragment;
import com.freshhome.fragments.UserCartFragment;
import com.freshhome.fragments.UserFavoFragment;
import com.freshhome.fragments.UserHistoryFragment;
import com.freshhome.fragments.UserHomeFragment;
import com.freshhome.fragments.UserProfileFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.freshhome.fragments.AddMenuFragment;

public class MainActivity_NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static ImageView image_addmenu;
    public static TextView text_name, text_badge;
    public static TextView heading;
    public static LinearLayout linear_active_inactive, linear_badge;
    public static CircleImageView profile_image;
    UserSessionManager sessionManager;
    ImageView cross, image_navicon, image_qrcode, img_search, img_cart;
    FrameLayout frame_notification;
    NavigationView navigationView;
    ApiInterface apiInterface;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav_drawer);
        apiInterface = ApiClient.getInstance().getClient();
        if (getIntent().hasExtra("message")) {
            Log.e("message", getIntent().getStringExtra("message"));
        }
        sessionManager = new UserSessionManager(MainActivity_NavDrawer.this);
        if (!sessionManager.getUserDetails().get("token").equalsIgnoreCase("") && sessionManager.getUserDetails().get("token") != null) {
            AllAPIs.token = sessionManager.getUserDetails().get("token");
        } else {
            AllAPIs.token = sessionManager.getGuestUserDetails().get("token");
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        image_navicon = (ImageView) findViewById(R.id.image_navicon);
        image_navicon.setOnClickListener(this);
        tvLogin = (TextView) findViewById(R.id.tvLogin);
        tvLogin.setOnClickListener(this);

        img_search = (ImageView) findViewById(R.id.img_search);
        img_search.setOnClickListener(this);

        img_cart = (ImageView) findViewById(R.id.img_cart);
        img_cart.setOnClickListener(this);
        HideShowBarBtn(true);
        image_addmenu = (ImageView) findViewById(R.id.image_addmenu);
        image_addmenu.setOnClickListener(this);

        heading = (TextView) findViewById(R.id.heading);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //header click
        View headerview = navigationView.getHeaderView(0);
        frame_notification = (FrameLayout) headerview.findViewById(R.id.frame_notification);
        frame_notification.setOnClickListener(this);

        profile_image = (CircleImageView) headerview.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);


        //image
//        if (sessionManager.getUserDetails().get("profile_pic").equalsIgnoreCase("")) {
//            Picasso.get().load(R.drawable.icon).into(profile_image);
//        } else {
        Picasso.get().load(sessionManager.getUserDetails().get("profile_pic")).placeholder(R.drawable.icon).into(profile_image);
        // }

//        Glide.with(this)
//                .load(sessionManager.getUserDetails().get("profile_pic")).into(profile_image);

//        Glide.with(this)
//                .load(sessionManager.getUserDetails().get("profile_pic"))
//                .into(profile_image);


        cross = (ImageView) headerview.findViewById(R.id.cross);
        cross.setOnClickListener(this);

        linear_active_inactive = (LinearLayout) headerview.findViewById(R.id.linear_active_inactive);


        text_name = (TextView) headerview.findViewById(R.id.text_name);
        text_name.setText(sessionManager.getUserDetails().get("username"));

        linear_badge = (LinearLayout) headerview.findViewById(R.id.linear_badge);
        linear_badge.setVisibility(View.GONE);
        text_badge = (TextView) headerview.findViewById(R.id.text_badge);

        image_qrcode = (ImageView) headerview.findViewById(R.id.image_qrcode);
        image_qrcode.setOnClickListener(this);

        heading.setText(getResources().getString(R.string.home));

        Menu nav_Menu = navigationView.getMenu();
        //hide show nav items
        setupNavDrawer(nav_Menu);

        //TODO set nav heading title(demo and switch) color and text size
        MenuItem menu_switch = nav_Menu.findItem(R.id.switchto);
        changeNavMenutextColor(menu_switch);

        MenuItem change_langauge = nav_Menu.findItem(R.id.change_langauge);
        changeNavMenutextColor(change_langauge);
        String from="";
        if(getIntent() != null){
             from = (String)getIntent().getStringExtra("From");
        }else {
            from="test";
        }
        if(from != null && !from.equalsIgnoreCase("")){
        if (from.equalsIgnoreCase("FROMCART")) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.mycart));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            UserCartFragment frag_cart = new UserCartFragment();
            fragmentTransaction.replace(R.id.main_linear, frag_cart);
            fragmentTransaction.commit();
        }} else if (getIntent().hasExtra("OpenFrag") && getIntent().getIntExtra("OpenFrag", 0) == ConstantValues.OPENCART) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.mycart));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            UserCartFragment frag_cart = new UserCartFragment();
            fragmentTransaction.replace(R.id.main_linear, frag_cart);
            fragmentTransaction.commit();
        } else {
            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
                HideShowBarBtn(false);
                heading.setText(getResources().getString(R.string.profile));
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                SupplierProfileFragment frag_edit = new SupplierProfileFragment();
                fragmentTransaction.replace(R.id.main_linear, frag_edit);
                fragmentTransaction.commit();
            } else {
                if(getIntent() != null){
                    String testfrom = getIntent().getStringExtra("deepLink");
                    if(testfrom.equalsIgnoreCase("deepLink")){

                        String supplierid = getIntent().getStringExtra("supplier_id");

                        Bundle bundle = new Bundle();
                        bundle.putString("supplier_id", supplierid);
                        bundle.putString("deeplink", "deeplink");

                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        UserHomeFragment frag_home = new UserHomeFragment();
                        frag_home.setArguments(bundle);
                        fragmentTransaction.add(R.id.main_linear, frag_home);
                        fragmentTransaction.commit();


                    }else {
                        HideShowBarBtn(true);
                        heading.setText(getResources().getString(R.string.home));
                        Bundle bundle = new Bundle();
                        bundle.putString("deeplink", "ravi");
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        UserHomeFragment frag_home = new UserHomeFragment();
                        frag_home.setArguments(bundle);
                        fragmentTransaction.add(R.id.main_linear, frag_home);
                        fragmentTransaction.commit();
                    }
                }else {
                    HideShowBarBtn(true);
                    heading.setText(getResources().getString(R.string.home));
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    UserHomeFragment frag_home = new UserHomeFragment();
                    fragmentTransaction.add(R.id.main_linear, frag_home);
                    fragmentTransaction.commit();
                }
            }
        }

        if (sessionManager.isLoggedIn()) {
            tvLogin.setVisibility(View.GONE);
        } else {
            tvLogin.setVisibility(View.VISIBLE);
        }
    }

    private void setupNavDrawer(Menu nav_Menu) {

        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            //disable user
            nav_Menu.findItem(R.id.nav_supplier).setVisible(false);
            nav_Menu.findItem(R.id.nav_paymentop).setVisible(false);
            nav_Menu.findItem(R.id.nav_favo).setVisible(false);
            nav_Menu.findItem(R.id.nav_history).setVisible(false);
            nav_Menu.findItem(R.id.nav_trackorder).setVisible(false);
            nav_Menu.findItem(R.id.nav_wallet).setVisible(true);
            nav_Menu.findItem(R.id.nav_cart).setVisible(false);
            nav_Menu.findItem(R.id.nav_home).setVisible(false);
            //enable supplier
            nav_Menu.findItem(R.id.nav_custmer).setVisible(true);

            nav_Menu.findItem(R.id.nav_menu).setVisible(true);
            nav_Menu.findItem(R.id.nav_kitchen).setVisible(true);
            // nav_Menu.findItem(R.id.nav_schedule).setVisible(true);
            nav_Menu.findItem(R.id.nav_plan).setVisible(true);
            nav_Menu.findItem(R.id.nav_sales).setVisible(true);
            nav_Menu.findItem(R.id.nav_salesreport).setVisible(true);
            nav_Menu.findItem(R.id.nav_askforhelp).setVisible(true);


            //change name of  to my items and my kitchen to my orders and hide schedule in case of
            //home made products and shop products
            if (sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase("2") ||
                    sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase("3")) {

                //  nav_Menu.findItem(R.id.nav_schedule).setVisible(false);
                nav_Menu.findItem(R.id.nav_kitchen).setTitle(getResources().getString(R.string.my_orders));
                nav_Menu.findItem(R.id.nav_menu).setTitle(getResources().getString(R.string.mymenu));
            }


        } else if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
            nav_Menu.findItem(R.id.nav_supplier).setVisible(true);
            nav_Menu.findItem(R.id.nav_paymentop).setVisible(false);
            nav_Menu.findItem(R.id.nav_favo).setVisible(true);
            nav_Menu.findItem(R.id.nav_history).setVisible(true);
            nav_Menu.findItem(R.id.nav_trackorder).setVisible(false);
            nav_Menu.findItem(R.id.nav_wallet).setVisible(true);
            nav_Menu.findItem(R.id.nav_cart).setVisible(true);
            nav_Menu.findItem(R.id.nav_home).setVisible(true);
            //disable supplier
            nav_Menu.findItem(R.id.nav_custmer).setVisible(false);
            nav_Menu.findItem(R.id.nav_menu).setVisible(false);
            nav_Menu.findItem(R.id.nav_kitchen).setVisible(false);
            // nav_Menu.findItem(R.id.nav_schedule).setVisible(false);
            nav_Menu.findItem(R.id.nav_plan).setVisible(false);
            nav_Menu.findItem(R.id.nav_salesreport).setVisible(false);
            nav_Menu.findItem(R.id.nav_sales).setVisible(false);
            nav_Menu.findItem(R.id.nav_askforhelp).setVisible(false);

        } else {
            //skip
            nav_Menu.findItem(R.id.nav_trackorder).setVisible(false);
            nav_Menu.findItem(R.id.nav_history).setVisible(true);
            nav_Menu.findItem(R.id.nav_custmer).setVisible(false);
            nav_Menu.findItem(R.id.nav_menu).setVisible(false);
            nav_Menu.findItem(R.id.nav_kitchen).setVisible(false);
            // nav_Menu.findItem(R.id.nav_schedule).setVisible(false);
            nav_Menu.findItem(R.id.nav_plan).setVisible(false);
            nav_Menu.findItem(R.id.nav_salesreport).setVisible(false);
            nav_Menu.findItem(R.id.nav_supplier).setVisible(false);
            nav_Menu.findItem(R.id.nav_paymentop).setVisible(false);
            nav_Menu.findItem(R.id.nav_favo).setVisible(false);
            nav_Menu.findItem(R.id.nav_wallet).setVisible(true);
            nav_Menu.findItem(R.id.nav_cart).setVisible(false);
            nav_Menu.findItem(R.id.nav_invitefriends).setVisible(false);
            nav_Menu.findItem(R.id.nav_home).setVisible(false);
            nav_Menu.findItem(R.id.nav_askforhelp).setVisible(true);
        }

        //CHECK supplier and driver as show supplier/driver
        if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_SUPPLIER).equalsIgnoreCase("yes")) {
            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
                nav_Menu.findItem(R.id.nav_supplier).setVisible(true);
            }
            nav_Menu.findItem(R.id.nav_driver).setVisible(false);
        } else if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_DRIVER).equalsIgnoreCase("yes")) {
            //TODO if not DRIVER THEN SHOW DRIVER AND HIDE SUPPLIER
            nav_Menu.findItem(R.id.nav_supplier).setVisible(false);
            nav_Menu.findItem(R.id.nav_driver).setVisible(false);
        } else {
            //TODO if not supplier and no driver then show both so that user can join either
            nav_Menu.findItem(R.id.nav_supplier).setVisible(true);
            nav_Menu.findItem(R.id.nav_driver).setVisible(false);
        }


        //check if loggedin the show loggout else hide logout
        if (sessionManager.isLoggedIn()) {
            nav_Menu.findItem(R.id.nav_logout).setVisible(true);
            nav_Menu.findItem(R.id.nav_editprofile).setVisible(true);
            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
                image_qrcode.setVisibility(View.GONE);
            } else {
                image_qrcode.setVisibility(View.VISIBLE);
            }
            text_name.setVisibility(View.VISIBLE);
        } else {
            nav_Menu.findItem(R.id.nav_wallet).setVisible(false);
            nav_Menu.findItem(R.id.nav_logout).setVisible(false);
            nav_Menu.findItem(R.id.nav_editprofile).setVisible(false);
            image_qrcode.setVisibility(View.GONE);
            text_name.setVisibility(View.GONE);
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity__nav_drawer, menu);
        return true;
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            heading.setText(getResources().getString(R.string.home));
            HideShowBarBtn(true);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            UserHomeFragment frag_home = new UserHomeFragment();
            fragmentTransaction.replace(R.id.main_linear, frag_home);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_menu) {
            HideShowBarBtn(false);
            //in case of home made and shop products
            if (sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase(ConstantValues.home_products) ||
                    sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase(ConstantValues.shops)) {
                heading.setText(R.string.my_items);
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                ProductFragment frag_product = new ProductFragment();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_linear, frag_product);
                fragmentTransaction.commit();
            } else {
                //in case of food
                heading.setText(getResources().getString(R.string.mymenu));
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                MenuFragment frag_menu = new MenuFragment();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_linear, frag_menu);
                fragmentTransaction.commit();
            }

        } else if (id == R.id.nav_kitchen) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.my_order));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MyKitchenFragment frag_kitchen = new MyKitchenFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_kitchen);
            fragmentTransaction.commit();

//        } else if (id == R.id.nav_addmenu) {
//
//            heading.setText(getResources().getString(R.string.addmenu));
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            AddMenuFragment frag_addmenu = new AddMenuFragment();
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.replace(R.id.main_linear, frag_addmenu);
//            fragmentTransaction.commit();

        }
//        else if (id == R.id.nav_schedule) {
//            HideShowBarBtn(false);
////            ScheduleWeeksFragment
//            heading.setText(getResources().getString(R.string.schedule));
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            ScheduleWeeksFragment frag_schdedule = new ScheduleWeeksFragment();
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.replace(R.id.main_linear, frag_schdedule);
//            fragmentTransaction.commit();
//
//
//        }
        else if (id == R.id.nav_plan) {
//            heading.setText(getResources().getString(R.string.plan));
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            PlanFragment frag_plan = new PlanFragment();
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.replace(R.id.main_linear, frag_plan);
//            fragmentTransaction.commit();

            Intent i = new Intent(MainActivity_NavDrawer.this, PlanActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_salesreport) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.salesreport));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SalesReportFragment frag_sales = new SalesReportFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_sales);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_invitefriends) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.invite));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            InviteFriendFragment frag_invite = new InviteFriendFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_invite);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_editprofile) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.profile));
            if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                UserProfileFragment frag_edit = new UserProfileFragment();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_linear, frag_edit);
                fragmentTransaction.commit();
            } else {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                SupplierProfileFragment frag_edit = new SupplierProfileFragment();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_linear, frag_edit);
                fragmentTransaction.commit();
            }
        } else if (id == R.id.nav_supplier) {
            if (sessionManager.isLoggedIn()) {
                if (sessionManager.IsSupplierExist()) {
                    sessionManager.saveLoginType(ConstantValues.ToCook);
                    Menu nav_Menu = navigationView.getMenu();
                    //hide show nav items
                    setupNavDrawer(nav_Menu);
                    Reload_Home();
                } else {
                    //check and add phone number //then entry all other fields
                    if (sessionManager.getPhoneNumber().equalsIgnoreCase("")) {
                        ///show add phone number sreen //verifcation
                        Intent i = new Intent(MainActivity_NavDrawer.this, MobileNumberActivity.class);
                        i.putExtra("fromLogin", false);
                        startActivity(i);
                    } else {
                        //open user to supplier as update
                        Intent i = new Intent(MainActivity_NavDrawer.this, UpdateUserToSupplier.class);
                        startActivity(i);
                    }
                }

            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_custmer) {
            //reset screen and load home again
            sessionManager.saveLoginType(ConstantValues.ToEat);
            Menu nav_Menu = navigationView.getMenu();
            //hide show nav items
            setupNavDrawer(nav_Menu);
            Reload_Home();            // for the timing this section is anactive

        } else if (id == R.id.nav_sales) {
            //move to sales modules //update session
            if (sessionManager.isLoggedIn()) {
                if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_SALES).equalsIgnoreCase("yes")) {
                    sessionManager.saveLoginType(ConstantValues.Sales);
                    Intent i = new Intent(MainActivity_NavDrawer.this, ActivitySalesNavDrawer.class);
                    startActivity(i);
                    MainActivity_NavDrawer.this.finish();
                } else {
                    if (CommonMethods.checkConnection()) {
                        SwitchToSale();
                    }
                }
            } else {
                Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_contactus) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.contact_us));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            ContactUsFragment frag_edit = new ContactUsFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_edit);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {
            sessionManager.clear_session();
            ActivityCompat.finishAffinity(MainActivity_NavDrawer.this);

        } else if (id == R.id.nav_cart) {
            heading.setText(getResources().getString(R.string.mycart));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            UserCartFragment frag_cart = new UserCartFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_cart);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_favo) {
            heading.setText(getResources().getString(R.string.myfavo));
            HideShowBarBtn(true);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            UserFavoFragment frag_edit = new UserFavoFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_edit);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_history) {
            HideShowBarBtn(true);
            heading.setText(getResources().getString(R.string.myhistory));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            UserHistoryFragment frag_history = new UserHistoryFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_history);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_paymentop) {

//            heading.setText(getResources().getString(R.string.paymentoption));
//            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//            UserPaymentMethodFragment frag_payment = new UserPaymentMethodFragment();
//            fragmentTransaction.addToBackStack(null);
//            fragmentTransaction.replace(R.id.main_linear, frag_payment);
//            fragmentTransaction.commit();

        } else if (id == R.id.nav_wallet) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.mywallet));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MyWalletFragment frag_wallet = new MyWalletFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_wallet);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_trackorder) {

        } else if (id == R.id.nav_askforhelp) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.askforhelp));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            FragmentAskForHelp frag_history = new FragmentAskForHelp();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_history);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_english) {


        } else if (id == R.id.nav_arabic) {

        } else if (id == R.id.nav_driver) {
            if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_DRIVER).equalsIgnoreCase("yes")) {
                sessionManager.saveLoginType(ConstantValues.Driver);
                sessionManager.saveDriverType(ConstantValues.driver_individual);
                Intent i = new Intent(MainActivity_NavDrawer.this, DriverNavDrawerActivity.class);
                startActivity(i);
                MainActivity_NavDrawer.this.finish();
            } else {
                if (CommonMethods.checkConnection()) {
                    SwtichtoDriver();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(MainActivity_NavDrawer.this, getResources().getString(R.string.internetconnection));
                }
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void SwitchToSale() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity_NavDrawer.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.SwitchSupplierToSales("true");
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
                            JSONObject obj = object.getJSONObject("data");
                            //save session again to update value
                            sessionManager.saveLoginType(ConstantValues.Sales);
                            sessionManager.createLoginSession(obj.getString("id"),
                                    obj.getString("username"),
                                    obj.getString("share_id"),
                                    obj.getString("email"),
                                    obj.getString("token"),
                                    sessionManager.getLoginType().toString(),
                                    CommonMethods.checkNull(obj.getString("phone_number")),
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_TEMP_PASSWORD),
                                    obj.getString("is_supplier"),
                                    obj.getString("is_user"),
                                    obj.getString("scan_code"),
                                    obj.getString("is_sale_person"),
                                    obj.getString("supplier_type"),
                                    obj.getString("is_watched"),
                                    obj.getString("header_image"), obj.getString("is_driver"),
                                    obj.getString("profile_pic"), obj.getString("nationality"), obj.getString("availability"), obj.getString("name"), false, true, obj.getString("is_driver"));


                            Intent i = new Intent(MainActivity_NavDrawer.this, ActivitySalesNavDrawer.class);
                            startActivity(i);
                            MainActivity_NavDrawer.this.finish();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(MainActivity_NavDrawer.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(MainActivity_NavDrawer.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(MainActivity_NavDrawer.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.frame_notification:
                if (sessionManager.isLoggedIn()) {
                    Intent intent = new Intent(MainActivity_NavDrawer.this, NotificationActivity.class);
                    startActivity(intent);
                } else {
//                    CommonMethods.ShowLoginDialog(MainActivity_NavDrawer.this);
                }
                closeDrawer();
                break;

            case R.id.cross:
                closeDrawer();
                break;

            case R.id.image_navicon:
                openDrawer();
                break;

            case R.id.profile_image:
                if (sessionManager.isLoggedIn()) {
                    heading.setText(getResources().getString(R.string.edit_profile));
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    SupplierProfileFragment frag_edit = new SupplierProfileFragment();
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.main_linear, frag_edit);
                    fragmentTransaction.commit();
                } else {
                    CommonMethods.ShowLoginDialog(MainActivity_NavDrawer.this);
                }
                closeDrawer();
                break;

            case R.id.tvLogin:
                sessionManager.saveLoginType("1");
                Intent il = new Intent(this, LoginActivity.class);
                il.putExtra("cook_eat", "1");
                startActivity(il);

                break;

            case R.id.image_addmenu:

                if (sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase("2") ||
                        sessionManager.getUserDetails().get(UserSessionManager.KEY_SUPPLIER_TYPE).equalsIgnoreCase("3")) {
                    //activity to add home products and shop produts
                    Intent i = new Intent(MainActivity_NavDrawer.this, AddProduct_Activity.class);
                    startActivity(i);

                } else {
                    Intent i = new Intent(MainActivity_NavDrawer.this, AddMenuActivity.class);
                    i.putExtra("dname", "");
                    i.putExtra("ddescription", "");
                    i.putExtra("dprice", "");
                    i.putExtra("dsince", "");
//                i.putExtra("dcuisnines", "");
//                i.putExtra("dcategory", "");
                    i.putExtra("dimage", "");
                    i.putExtra("dish_serve", "");
//                i.putExtra("d_meal", "");

                    startActivity(i);
                }
//                heading.setText(getResources().getString(R.string.addmenu));
//                FragmentTransaction fragmentTransaction_i = getSupportFragmentManager().beginTransaction();
//                AddMenuFragment frag_addmenu = new AddMenuFragment();
//                fragmentTransaction_i.addToBackStack(null);
//                fragmentTransaction_i.replace(R.id.main_linear, frag_addmenu);
//                fragmentTransaction_i.commit();

                break;


            case R.id.image_qrcode:
                Intent qrcode_intent = new Intent(MainActivity_NavDrawer.this, ActivityQRCode.class);
                startActivity(qrcode_intent);
                closeDrawer();
                break;


            case R.id.img_cart:
                break;

            case R.id.img_search:
                break;

        }

    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void openDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    private void Reload_Home() {

//        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        HomeFragment frag_home = new HomeFragment();
//        fragmentTransaction.replace(R.id.main_linear, frag_home);
//        fragmentTransaction.commit();
//        FragmentManager fm = getSupportFragmentManager();
////        int count = fm.getBackStackEntryCount();
////        for (int i = 0; i < count; ++i) {
////            fm.popBackStackImmediate();
////        }
//        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);


        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
            HideShowBarBtn(false);
            heading.setText(getResources().getString(R.string.profile));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            SupplierProfileFragment frag_edit = new SupplierProfileFragment();
            fragmentTransaction.replace(R.id.main_linear, frag_edit);
            fragmentTransaction.commit();
        } else {
            HideShowBarBtn(true);
            heading.setText(getResources().getString(R.string.home));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            UserHomeFragment frag_home = new UserHomeFragment();
            fragmentTransaction.replace(R.id.main_linear, frag_home);
            fragmentTransaction.commit();
        }

        FragmentManager fm = getSupportFragmentManager();
//        int count = fm.getBackStackEntryCount();
//        for (int i = 0; i < count; ++i) {
//            fm.popBackStackImmediate();
//        }
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    private void changeNavMenutextColor(MenuItem menuitem) {
        SpannableString s = new SpannableString(menuitem.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.nav_drawer_Text), 0, s.length(), 0);
        menuitem.setTitle(s);
    }

    //TODO ----register as a driver---------------------------
    private void SwtichtoDriver() {
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity_NavDrawer.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        calls = apiInterface.SwitchtoDriver("true");
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
                            JSONObject obj = object.getJSONObject("data");
                            //save session again to update value

                            String is_driver = "no";
                            if (obj.has("is_driver")) {
                                is_driver = obj.getString("is_driver");
                            }
                            sessionManager.createLoginSession(obj.getString("id"),
                                    obj.getString("username"),
                                    obj.getString("share_id"),
                                    obj.getString("email"),
                                    obj.getString("token"),
                                    sessionManager.getLoginType().toString(),
                                    CommonMethods.checkNull(obj.getString("phone_number")),
                                    sessionManager.getUserDetails().get(UserSessionManager.KEY_TEMP_PASSWORD),
                                    obj.getString("is_supplier"),
                                    obj.getString("is_user"),
                                    obj.getString("scan_code"),
                                    obj.getString("is_sale_person"),
                                    obj.getString("supplier_type"),
                                    obj.getString("is_watched"),
                                    obj.getString("header_image"), obj.getString("is_driver"),
                                    obj.getString("profile_pic"),


                                    "",
                                    "",
                                    obj.getString("name"), true, false, is_driver);
                            sessionManager.saveLoginType(ConstantValues.Driver);
                            sessionManager.saveDriverType(ConstantValues.driver_individual);
                            Intent i = new Intent(MainActivity_NavDrawer.this, DriverNavDrawerActivity.class);
                            startActivity(i);
                            MainActivity_NavDrawer.this.finish();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(MainActivity_NavDrawer.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(MainActivity_NavDrawer.this, getResources().getString(R.string.server_error));
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
            }
        });
    }

    private void HideShowBarBtn(boolean isVisible) {
        if (isVisible) {
            img_cart.setVisibility(View.GONE);
            img_search.setVisibility(View.GONE);
        } else {
            img_cart.setVisibility(View.GONE);
            img_search.setVisibility(View.GONE);
        }
    }
}
