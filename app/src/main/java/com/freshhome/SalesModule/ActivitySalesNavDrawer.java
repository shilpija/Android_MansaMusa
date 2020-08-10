package com.freshhome.SalesModule;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.freshhome.CommonUtil.AllAPIs;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.DriverModule.DriverNavDrawerActivity;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.MobileNumberActivity;
import com.freshhome.NotificationActivity;
import com.freshhome.R;
import com.freshhome.UpdateUserToSupplier;
import com.freshhome.fragments.ContactUsFragment;
import com.freshhome.fragments.MyCustomerFragment;
import com.freshhome.fragments.MyWalletFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitySalesNavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    public static ImageView image_addmenu;
    public static TextView text_username, text_badge;
    public static TextView heading;
    public static LinearLayout linear_active_inactive, linear_badge;
    public static CircleImageView profile_image;
    UserSessionManager sessionManager;
    ImageView cross, image_navicon, image_notification;
    FrameLayout frame_notification;
    NavigationView navigationView;
    ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_nav_drawer);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(ActivitySalesNavDrawer.this);
        AllAPIs.token = sessionManager.getUserDetails().get("token");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        image_navicon = (ImageView) findViewById(R.id.image_navicon);
        image_navicon.setOnClickListener(this);

        heading = (TextView) findViewById(R.id.heading);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();

        //TODO set nav heading title(demo and switch) color and text size
        MenuItem menu_demo = menu.findItem(R.id.nav_demo);
        MenuItem menu_switch = menu.findItem(R.id.switchto);
        changeNavMenutextColor(menu_demo);
        changeNavMenutextColor(menu_switch);

        MenuItem change_langauge = menu.findItem(R.id.change_langauge);
        changeNavMenutextColor(change_langauge);

        Menu nav_Menu = navigationView.getMenu();
        setSwitchToValues(nav_Menu);

        View headerview = navigationView.getHeaderView(0);
        cross = (ImageView) headerview.findViewById(R.id.cross);
        cross.setOnClickListener(this);
        image_notification = (ImageView) headerview.findViewById(R.id.image_notification);
        image_notification.setOnClickListener(this);
        text_username = (TextView) headerview.findViewById(R.id.text_username);
        profile_image = (CircleImageView) headerview.findViewById(R.id.profile_image);
        profile_image.setOnClickListener(this);


        //image
//        if (sessionManager.getUserDetails().get("profile_pic").equalsIgnoreCase("")) {
//            Picasso.get().load(R.drawable.icon).into(profile_image);
//        } else {
            Picasso.get().load(sessionManager.getUserDetails().get("profile_pic")).placeholder(R.drawable.d_img).into(profile_image);
       // }


        Glide.with(this)
                .load(sessionManager.getUserDetails().get("profile_pic"))
                .into(profile_image);

        //add first fragment
        heading.setText(R.string.myrequest);

        Fragment_Sales_Myrequest frag_home = new Fragment_Sales_Myrequest();
        AddReplaceFragment(false, frag_home);

//        if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_VIDEO_WATCHED).equalsIgnoreCase("0")) {
//            //MEANS all videos are not watched by salesperson so show video screen
//            Intent i_demovideo = new Intent(ActivitySalesNavDrawer.this, ActivityDemoVideos.class);
//            startActivity(i_demovideo);
//
//        }


        //TODO CHECK AND HIDE SHOW SUPPLIER AND DRIVER AS PER THE SUBSCRIPTION TYPE nav_tosell  nav_driver


    }

    private void setSwitchToValues(Menu nav_Menu) {
        if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_SUPPLIER).equalsIgnoreCase("yes")) {
            nav_Menu.findItem(R.id.nav_tosell).setVisible(true);
            nav_Menu.findItem(R.id.nav_driver).setVisible(false);
        } else if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_DRIVER).equalsIgnoreCase("yes")) {
            //TODO if not DRIVER THEN SHOW DRIVER AND HIDE SUPPLIER
            nav_Menu.findItem(R.id.nav_tosell).setVisible(false);
            nav_Menu.findItem(R.id.nav_driver).setVisible(true);
        } else {
            //TODO if not supplier and no driver then show both so that user can join either
            nav_Menu.findItem(R.id.nav_tosell).setVisible(true);
            nav_Menu.findItem(R.id.nav_driver).setVisible(true);
        }

    }

    private void changeNavMenutextColor(MenuItem menuitem) {
        SpannableString s = new SpannableString(menuitem.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.nav_drawer_Text), 0, s.length(), 0);
        menuitem.setTitle(s);
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
        getMenuInflater().inflate(R.menu.activity_sales_nav_drawer, menu);
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

        if (id == R.id.nav_request) {
            heading.setText(R.string.myrequest);
            Fragment_Sales_Myrequest frag_home = new Fragment_Sales_Myrequest();
            AddReplaceFragment(true, frag_home);

        } else if (id == R.id.nav_jobhistory) {
            heading.setText(R.string.jobhistory);
            Fragment_Sales_JobHistory frag_job = new Fragment_Sales_JobHistory();
            AddReplaceFragment(true, frag_job);


        } else if (id == R.id.nav_contactus) {
            heading.setText(getResources().getString(R.string.contact_us));
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            ContactUsFragment frag_edit = new ContactUsFragment();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.main_linear, frag_edit);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_mycustomer) {
            heading.setText(R.string.mycustomer);
            Bundle bundle = new Bundle();

            bundle.putString("message", "myCustomer");
            Fragment_Sales_Myrequest frag_home = new Fragment_Sales_Myrequest();
            frag_home.setArguments(bundle);
            AddReplaceFragment(true, frag_home);


//            transaction.replace(R.id.fragment_single, fragInfo);
//            transaction.commit();



        } else if (id == R.id.nav_wallet) {
            heading.setText(R.string.mywallet);
            MyWalletFragment frag_wallet = new MyWalletFragment();
            AddReplaceFragment(true, frag_wallet);

        } else if (id == R.id.nav_scan) {
            openScanner();

        } else if (id == R.id.nav_profile) {
            heading.setText(R.string.profile);
            Fragment_Sales_Profile frag_profile = new Fragment_Sales_Profile();
            AddReplaceFragment(true, frag_profile);

        } else if (id == R.id.nav_tosell) {
            if (sessionManager.IsSupplierExist()) {
                sessionManager.saveLoginType(ConstantValues.ToCook);
                Intent i_buy = new Intent(ActivitySalesNavDrawer.this, MainActivity_NavDrawer.class);
                i_buy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i_buy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i_buy);
                ActivitySalesNavDrawer.this.finish();
            } else {
                //check and add phone number //then entry all other fields
                if (sessionManager.getPhoneNumber().equalsIgnoreCase("")) {
                    ///show add phone number sreen //verifcation
                    Intent i = new Intent(ActivitySalesNavDrawer.this, MobileNumberActivity.class);
                    i.putExtra("fromLogin", false);
                    startActivity(i);
                } else {
                    //open user to supplier as update
                    Intent i = new Intent(ActivitySalesNavDrawer.this, UpdateUserToSupplier.class);
                    startActivity(i);
                }
            }
        } else if (id == R.id.nav_tobuy) {
            //reset screen and load home again
//            sessionManager.saveLoginType(ConstantValues.ToEat);
//            Intent i_buy = new Intent(ActivitySalesNavDrawer.this, MainActivity_NavDrawer.class);
//            i_buy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            i_buy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i_buy);                     // this is inactive for the some time........
//            ActivitySalesNavDrawer.this.finish();

        } else if (id == R.id.nav_demo_video) {
            heading.setText(R.string.demovideo);
            Intent i_demovideo = new Intent(ActivitySalesNavDrawer.this, ActivityDemoVideos.class);
            startActivity(i_demovideo);

        } else if (id == R.id.nav_guest_supplier) {
            //TODO hit login api with demo credentails
            if (CommonMethods.checkConnection()) {
                postdata();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, getResources().getString(R.string.internetconnection));
            }

        } else if (id == R.id.nav_logout) {
            sessionManager.clear_session();

        } else if (id == R.id.nav_english) {

        } else if (id == R.id.nav_arabic) {

        } else if (id == R.id.nav_driver) {
            if (sessionManager.getUserDetails().get(UserSessionManager.KEY_IS_DRIVER).equalsIgnoreCase("yes")) {
                sessionManager.saveLoginType(ConstantValues.Driver);
                sessionManager.saveDriverType(ConstantValues.driver_individual);
                Intent i = new Intent(ActivitySalesNavDrawer.this, DriverNavDrawerActivity.class);
                startActivity(i);
                ActivitySalesNavDrawer.this.finish();
            } else {
                if (CommonMethods.checkConnection()) {
                    SwtichtoDriver();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, getResources().getString(R.string.internetconnection));
                }
            }
        }

        closeDrawer();
        return true;
    }

    private void openScanner() {
        IntentIntegrator integrator = new IntentIntegrator(ActivitySalesNavDrawer.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getResources().getString(R.string.scan));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        //set orientation locked to false
        integrator.setOrientationLocked(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_navicon:
                openDrawer();
                break;

            case R.id.cross:
                closeDrawer();
                break;

            case R.id.profile_image:
                heading.setText(R.string.profile);
                Fragment_Sales_Profile frag_profile = new Fragment_Sales_Profile();
                AddReplaceFragment(true, frag_profile);
                closeDrawer();
                break;


            case R.id.image_notification:
                Intent intent = new Intent(ActivitySalesNavDrawer.this, NotificationActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void openDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void AddReplaceFragment(boolean isbackstack, Fragment frag_ment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        //add backstack for all fragments except myrequest bez that is home fragment
        if (isbackstack) {
            fragmentTransaction.replace(R.id.main_linear, frag_ment);
            fragmentTransaction.addToBackStack(null);
        } else {
            fragmentTransaction.add(R.id.main_linear, frag_ment);
        }
        fragmentTransaction.commit();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            if (result.getContents() == null) {
            } else {
                try {
                    JSONObject jsonObj = new JSONObject(result.getContents());
                    String device_token = jsonObj.getString("device_token");
                    String email = jsonObj.getString("email");
                    upload_scanned_data(email, device_token);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void upload_scanned_data(String email, String device_token) {
        final ProgressDialog progressDialog = new ProgressDialog(ActivitySalesNavDrawer.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;

        calls = apiInterface.SubmitScannedData(email, device_token);

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
                            CommonUtilFunctions.success_Alert_Dialog(ActivitySalesNavDrawer.this, obj.getString("msg"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, getResources().getString(R.string.server_error));
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

    private void postdata() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivitySalesNavDrawer.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;

        calls = apiInterface.userlogin("test_user",
                "test_user", ConstantValues.ToCook,"", sessionManager.getFCMToken());

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
                            sessionManager.saveLoginType(ConstantValues.ToCook);
                            sessionManager.createLoginSession(obj.getString("id"),
                                    obj.getString("username"),
                                    obj.getString("share_id"),
                                    obj.getString("email"),
                                    obj.getString("token"),
                                    ConstantValues.ToCook,
                                    CommonMethods.checkNull(obj.getString("phone_number")),
                                    "",
                                    obj.getString("is_supplier"),
                                    obj.getString("is_user"), obj.getString("scan_code"),
                                    obj.getString("is_sale_person"),
                                    obj.getString("supplier_type"),
                                    obj.getString("is_watched"),
                                    obj.getString("header_image"), obj.getString("is_driver"),
                                    obj.getString("profile_pic"), obj.getString("nationality"),
                                    obj.getString("availability"), obj.getString("name"), false, false, "");

                            Intent i_buy = new Intent(ActivitySalesNavDrawer.this, MainActivity_NavDrawer.class);
                            i_buy.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i_buy.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i_buy);
                            ActivitySalesNavDrawer.this.finish();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, getResources().getString(R.string.server_error));
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

    //TODO ----register as a driver---------------------------
    private void SwtichtoDriver() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivitySalesNavDrawer.this);
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
                            Intent i = new Intent(ActivitySalesNavDrawer.this, DriverNavDrawerActivity.class);
                            startActivity(i);
                            ActivitySalesNavDrawer.this.finish();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivitySalesNavDrawer.this, getResources().getString(R.string.server_error));
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
}
