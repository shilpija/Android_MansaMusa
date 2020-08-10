package com.freshhome.DriverModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.freshhome.ActivityQRCode;
import com.freshhome.ChooseCard;
import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.PlanActivity;
import com.freshhome.R;
import com.freshhome.SalesModule.ActivitySalesNavDrawer;
import com.freshhome.fragments.ContactUsFragment;
import com.freshhome.fragments.MyWalletFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DriverNavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {//} OnMapReadyCallback {
    public static TextView text_notification_count;
    public static ImageView image_notification, image_arrow, image_nav, image_qrcode;
    //    CircleImageVi ew image_user;
    LinearLayout linear_all_orders;
    private GoogleMap mMap;
    private BottomSheetBehavior mBottomSheetBehavior;
    private SupportMapFragment mapFragment;
    private LinearLayout linear_main_slider;
    NavigationView navigationView;
    public static TextView heading, text_drivername;
    UserSessionManager sessionManager;
    public static SwitchCompat switch_button;
    public static CircleImageView profile_image;
    public static LinearLayout linear_active_inactive;
    ApiInterface apiInterface;
    boolean isfirsttime = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_nav_drawer);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(DriverNavDrawerActivity.this);
        AllAPIs.token = sessionManager.getDriveDetails().get(UserSessionManager.KEY_BASETOKEN);
        sessionManager.saveLoginType(ConstantValues.Driver);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        heading = (TextView) findViewById(R.id.heading);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
        //hide show nav items
        //TODO set nav heading title(demo and switch) color and text size
        MenuItem menu_switch = nav_Menu.findItem(R.id.switchto);
        changeNavMenutextColor(menu_switch);
        setupNavDrawer(nav_Menu);
        MenuItem change_langauge = nav_Menu.findItem(R.id.change_langauge);
        changeNavMenutextColor(change_langauge);

            View headerview = navigationView.getHeaderView(0);

        switch_button = (SwitchCompat) headerview.findViewById(R.id.switch_button);
        switch_button.setOnCheckedChangeListener(this);
        linear_active_inactive = (LinearLayout) headerview.findViewById(R.id.linear_active_inactive);
        profile_image = (CircleImageView) headerview.findViewById(R.id.profile_image);
        Glide.with(this)
                .load(sessionManager.getUserDetails().get("profile_pic"))
                .into(profile_image);
        text_drivername = (TextView) headerview.findViewById(R.id.text_name);
        image_nav = (ImageView) findViewById(R.id.image_nav);
        image_nav.setOnClickListener(this);

        image_qrcode = (ImageView) headerview.findViewById(R.id.image_qrcode);
        image_qrcode.setOnClickListener(this);
        //show qr code in case of individual driver bez compmay drivers can't have any
        if (sessionManager.getDriverType().equalsIgnoreCase(ConstantValues.driver_individual)) {
            image_qrcode.setVisibility(View.VISIBLE);
        } else {
            image_qrcode.setVisibility(View.GONE);
        }

        image_notification = (ImageView) findViewById(R.id.image_notification);
        image_notification.setOnClickListener(this);
        text_notification_count = (TextView) findViewById(R.id.text_notification_count);

        heading.setText(getResources().getString(R.string.home));
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        DriverCompanyHomefragment frag = new DriverCompanyHomefragment();
        fragmentTransaction.replace(R.id.main_linear, frag);
        fragmentTransaction.commit();

    }

    private void setupNavDrawer(Menu nav_menu) {
        if (sessionManager.getDriverType().equalsIgnoreCase(ConstantValues.driver_individual)) {
            nav_menu.findItem(R.id.nav_wallet).setVisible(true);
            nav_menu.findItem(R.id.switchto).setVisible(true);
            nav_menu.findItem(R.id.nav_cards).setVisible(false);
            nav_menu.findItem(R.id.nav_plan).setVisible(true);
        } else {
            nav_menu.findItem(R.id.nav_wallet).setVisible(false);
            nav_menu.findItem(R.id.switchto).setVisible(false);
            nav_menu.findItem(R.id.nav_cards).setVisible(false);
            nav_menu.findItem(R.id.nav_plan).setVisible(false);
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
        getMenuInflater().inflate(R.menu.driver_nav_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
            heading.setText(getResources().getString(R.string.home));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            DriverCompanyHomefragment frag = new DriverCompanyHomefragment();
            fragmentTransaction.replace(R.id.main_linear, frag);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_orders) {

            heading.setText(getResources().getString(R.string.mytrips));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            DriverOrderList frag = new DriverOrderList();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_editprofile) {

            if (sessionManager.getDriverType().equalsIgnoreCase(ConstantValues.driver_individual)) {
                heading.setText(getResources().getString(R.string.profile));
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                FragmentIndividualDriverProfile profile = new FragmentIndividualDriverProfile();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.main_linear, profile);
                fragmentTransaction.commit();

            } else {
                Intent i = new Intent(DriverNavDrawerActivity.this, DriverProfileActivity.class);
                i.putExtra("isHome", true);
                startActivity(i);
            }

        } else if (id == R.id.nav_contactus) {

            heading.setText(getResources().getString(R.string.contact_us));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            ContactUsFragment frag_edit = new ContactUsFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_edit);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_logout) {
            sessionManager.clear_session_driver();
            ActivityCompat.finishAffinity(DriverNavDrawerActivity.this);

        } else if (id == R.id.nav_wallet) {

            heading.setText(getResources().getString(R.string.mywallet));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            MyWalletFragment frag_wallet = new MyWalletFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_wallet);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_custmer) {
            //reset screen and load home again
            sessionManager.saveLoginType(ConstantValues.ToEat);
            Intent i_buy = new Intent(DriverNavDrawerActivity.this, MainActivity_NavDrawer.class);
            i_buy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i_buy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i_buy);
            DriverNavDrawerActivity.this.finish();

        } else if (id == R.id.nav_sales) {
            //move to sales modules //update session
            if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_SALES).equalsIgnoreCase("yes")) {
                sessionManager.saveLoginType(ConstantValues.Sales);
                Intent i = new Intent(DriverNavDrawerActivity.this, ActivitySalesNavDrawer.class);
                startActivity(i);
                DriverNavDrawerActivity.this.finish();
            } else {
                if (CommonMethods.checkConnection()) {
                    SwitchToSale();
                }else{
                    CommonUtilFunctions.Error_Alert_Dialog(DriverNavDrawerActivity.this, getResources().getString(R.string.internetconnection));
                }
            }

        } else if (id == R.id.nav_english) {

        } else if (id == R.id.nav_arabic) {

        } else if (id == R.id.nav_plan) {
            Intent i = new Intent(DriverNavDrawerActivity.this, PlanActivity.class);
            startActivity(i);

        } else if (id == R.id.nav_cards) {
            Intent i = new Intent(DriverNavDrawerActivity.this, ChooseCard.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_nav:
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);

//                Intent i = new Intent(DriverNavDrawerActivity.this, DriverProfileActivity.class);
//                i.putExtra("isHome", true);
//                startActivity(i);

                break;

            case R.id.image_notification:
                Intent intent = new Intent(DriverNavDrawerActivity.this, DriverNotificationActivity.class);
                startActivity(intent);
                break;

            case R.id.image_qrcode:
                Intent intent_orders = new Intent(DriverNavDrawerActivity.this, ActivityQRCode.class);
                startActivity(intent_orders);
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        //checked means available
        if (isfirsttime == false) {
            if (CommonMethods.checkConnection()) {
                if (isChecked) {
                    UdpateProfileStatus("1");
                } else {
                    UdpateProfileStatus("0");
                }
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(DriverNavDrawerActivity.this, getResources().getString(R.string.internetconnection));
            }
        }else{
            isfirsttime = false;
        }

    }


    private void UdpateProfileStatus(String available) {
        final ProgressDialog progressDialog = new ProgressDialog(DriverNavDrawerActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls;
        if (sessionManager.getDriverType().equalsIgnoreCase(ConstantValues.driver_individual)) {
            calls = apiInterface.UpdateIndividaulDriverStatus(available);
        } else {
            calls = apiInterface.UpdateDriverStatus(available);
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
                            JSONObject obj = object.getJSONObject("data");
                            if (obj.getString("is_available").equalsIgnoreCase("1")) {
                                DriverNavDrawerActivity.switch_button.setChecked(true);
                                DriverNavDrawerActivity.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_green_filled));
                            } else {
                                DriverNavDrawerActivity.linear_active_inactive.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_bg_gray));
                                DriverNavDrawerActivity.switch_button.setChecked(false);
                            }
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverNavDrawerActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverNavDrawerActivity.this, getResources().getString(R.string.server_error));
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


    private void SwitchToSale() {
        final ProgressDialog progressDialog = new ProgressDialog(DriverNavDrawerActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.SwitchSupplierToSales("yes");

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
                                    obj.getString("profile_pic"), obj.getString("nationality"),obj.getString("availability"),obj.getString("name"),true,false, obj.getString("is_driver"));

                            sessionManager.saveLoginType(ConstantValues.Sales);
                            Intent i = new Intent(DriverNavDrawerActivity.this, ActivitySalesNavDrawer.class);
                            startActivity(i);
                            DriverNavDrawerActivity.this.finish();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(DriverNavDrawerActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(DriverNavDrawerActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(DriverNavDrawerActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }


    private void changeNavMenutextColor(MenuItem menuitem) {
        SpannableString s = new SpannableString(menuitem.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.nav_drawer_Text), 0, s.length(), 0);
        menuitem.setTitle(s);
    }
}
