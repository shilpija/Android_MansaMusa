package com.freshhome.CommonUtil;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.freshhome.DriverModule.DriverLoginActivity;
import com.freshhome.DriverModule.DriverNavDrawerActivity;
import com.freshhome.DriverModule.DriverProfileActivity;
import com.freshhome.DriverModule.SelectDriverTypeActivity;
import com.freshhome.LoginActivity;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.SalesModule.ActivitySalesNavDrawer;
import com.freshhome.SelectCookEatActivity;

import java.util.HashMap;

/**
 *
 */
public class UserSessionManager {
    // Shared Preferences
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "FreshHome";
    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String IS_FIRSTTIME = "IsFirstTime";
    public static final String KEY_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_SHARE_ID = "share_id";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ACTIVE = "active";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_SUPPLIER = "supplierinfo";
    public static final String KEY_INSURER_ID = "insurer_id";
    public static final String KEY_BASETOKEN = "token";
    public static final String KEY_LOGINTYPE = "logintype";
    public static final String KEY_USERGUEST = "userGuest";
    public static final String KEY_TEMP_PASSWORD = "temp_password";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";

    public static final String KEY_IS_SUPPLIER = "supplier";
    public static final String KEY_IS_CART_ITEM_COUNT = "cart_item_count";
    public static final String KEY_IS_USER = "user";
    public static final String KEY_SALES = "is_Sales";
    public static final String KEY_IS_DRIVER = "is_Driver";

    public static final String KEY_FCM_TOEKN = "fcmtoken";
    public static final String KEY_NATIONALITY = "nationality";
    public static final String KEY_AGE = "age";
    public static final String KEY_IS_AVAILABLE = "is_available";
    public static final String KEY_DRIVER = "isDriver";
    public static final String KEY_DRIVER_SUBSCCRIBE = "is_Driver_sub";
    //    public static final String KEY_DRIVER_INDIVIDUAL = "is_Driver_individual";
    public static final String KEY_DRIVER_TYPE = "is_Driver_type";// 0 driver company // 1 individual driver

    public static final String QRCODEURL = "qrcodeUrl";
    public static final String KEY_IS_SALES = "is_sale_person";
    public static final String KEY_SUPPLIER_TYPE = "supplier_type";
    public static final String KEY_IS_VIDEO_WATCHED = "is_video_watched";
    public static final String KEY_HEADER_IMAGE = "header_image";
    public static final String KEY_VAT = "vat";
    public static final String KEY_FRESHHOMEEFEE = "freshhomee_fee";
    public static final String KEY_TRANSACTIONCHARGES = "transaction_charges";
    public static final String KEY_LANGUAGE = "lang";
    public static final String KEY_NAME = "name";


    public static final String KEY_STATUS = "status";
    public static final String KEY_SUB_STATUS = "subscription_status";
    public static final String KEY_SUB_START = "subscription_start";
    public static final String KEY_SUB_END = "subscription_end";

    public static final String KEY_WALLET = "wallet_amt";
    public static final String KEY_profile_image = "profileimg";

    public static final String KEY_COUNTRY_NAME = "countryname";


    // Constructor
    public UserSessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String id, String username, String share_id, String email, String token,
                                   String type, String phone, String temp_password, String isSupplier, String isUser,
                                   String qrcodeUrl, String is_sale_person, String supplier_type, String is_watched, String header_image, String is_driver_sub ,
                                   String profile_pic,String nationality,String availability,String name,boolean isdrvier,boolean isSales,String is_driver) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);
        editor.putBoolean(KEY_DRIVER, isdrvier);
        editor.putBoolean(KEY_SALES, isSales);

        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_SHARE_ID, share_id);
        editor.putString(KEY_ID, id);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_BASETOKEN, token);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_TEMP_PASSWORD, temp_password);

        editor.putString(KEY_IS_SUPPLIER, isSupplier);
        editor.putString(KEY_IS_USER, isUser);
        editor.putString(KEY_IS_SALES, is_sale_person);
        editor.putString(KEY_IS_DRIVER, is_driver);

        editor.putString(QRCODEURL, qrcodeUrl);
        editor.putString(KEY_SUPPLIER_TYPE, supplier_type);
        editor.putString(KEY_IS_VIDEO_WATCHED, is_watched);
        editor.putString(KEY_HEADER_IMAGE, header_image);
        editor.putString(KEY_DRIVER_SUBSCCRIBE, is_driver_sub);
        editor.putString(KEY_IS_AVAILABLE, availability);
        editor.putString(KEY_NATIONALITY, nationality);
        editor.putString(KEY_AGE, "");
        editor.putString(KEY_profile_image, profile_pic);
        editor.putString(KEY_NAME, name);
        editor.commit();
    }

    public void createGuestLoginSession(String token,String phone) {
        editor.putString(KEY_BASETOKEN, token);
        editor.putString(KEY_PHONE, phone);
        // commit changes
        editor.commit();
    }

    public HashMap<String, String> getGuestUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_BASETOKEN, pref.getString(KEY_BASETOKEN, ""));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, ""));
   return user;
    }

 public void update_isWatched(String iswatched) {
        editor.putString(KEY_IS_VIDEO_WATCHED, iswatched);
        // commit changes
        editor.commit();
    }

    public void save_role(String issupplier) {
        editor.putString(KEY_IS_SUPPLIER, issupplier);
        // commit changes
        editor.commit();
    }

    public void save_cart_item(String itemCount) {
        editor.putString(KEY_IS_CART_ITEM_COUNT, itemCount);
        // commit changes
        editor.commit();
    }



    public void saveLoginType(String logintype) {
        editor.putString(KEY_LOGINTYPE, logintype);
        editor.commit();
    }
 public void saveUserGuest(String userGuest) {
        editor.putString(KEY_USERGUEST, userGuest);
        editor.commit();
    }

    public void saveLatitude(String latitude) {
        editor.putString(LATITUDE, latitude);
        editor.commit();
    }

    public void saveLongitude(String longitude) {
        editor.putString(LONGITUDE, longitude);
        editor.commit();
    }

    public String getLoginType() {
        return pref.getString(KEY_LOGINTYPE, "");
    }
    public String getUserGuestType() {
        return pref.getString(KEY_USERGUEST, "");
    }

    public String getLatitude() {
        return pref.getString(LATITUDE, "");
    }

    public String getLongitude() {
        return pref.getString(LONGITUDE, "");
    }

    public void savePhoneNumber(String phonenumber) {
        editor.putString(KEY_PHONE, phonenumber);
        editor.commit();
    }
    public void saveSupplierInfo(String supplierinfo) {
        editor.putString(KEY_SUPPLIER, supplierinfo);
        editor.commit();
    }

    public String getPhoneNumber() {
        return pref.getString(KEY_PHONE, "");
    }
 public String getSupplierInfo() {
        return pref.getString(KEY_SUPPLIER, "");
    }

    public void checkLogin() {
        if (this.isLoggedIn()) {
            if (this.getLoginType().equalsIgnoreCase(ConstantValues.Driver)) {
                if (this.isDriverInfoSaved()) {
                    Intent i = new Intent(_context, DriverNavDrawerActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                } else {
                    Intent i = new Intent(_context, DriverProfileActivity.class);
                    i.putExtra("isHome", false);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                }
            } else {
                if (this.getLoginType().equalsIgnoreCase(ConstantValues.Sales)) {
                    Intent i = new Intent(_context, ActivitySalesNavDrawer.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                } else {
                    Intent i = new Intent(_context, MainActivity_NavDrawer.class);
                    i.putExtra("deepLink","ravi");
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    _context.startActivity(i);
                }
            }
        } else {
            saveLoginType("1");
            Intent i = new Intent(_context, LoginActivity.class);
            i.putExtra("cook_eat", "1");
            _context.startActivity(i);
//            Intent intent = new Intent(_context, SelectCookEatActivity.class);
//            _context.startActivity(intent);
        }
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));
        user.put(KEY_ID, pref.getString(KEY_ID, ""));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, ""));
        user.put(KEY_BASETOKEN, pref.getString(KEY_BASETOKEN, ""));
        user.put(KEY_SHARE_ID, pref.getString(KEY_SHARE_ID, ""));
        user.put(KEY_TEMP_PASSWORD, pref.getString(KEY_TEMP_PASSWORD, ""));
        user.put(KEY_PHONE, pref.getString(KEY_PHONE, ""));
        user.put(QRCODEURL, pref.getString(QRCODEURL, ""));
        user.put(KEY_SUPPLIER_TYPE, pref.getString(KEY_SUPPLIER_TYPE, ""));
        user.put(KEY_IS_VIDEO_WATCHED, pref.getString(KEY_IS_VIDEO_WATCHED, ""));
        user.put(KEY_HEADER_IMAGE, pref.getString(KEY_HEADER_IMAGE, ""));
        user.put(KEY_DRIVER_SUBSCCRIBE, pref.getString(KEY_DRIVER_SUBSCCRIBE, ""));
        user.put(KEY_IS_AVAILABLE, pref.getString(KEY_IS_AVAILABLE, ""));
        user.put(KEY_NATIONALITY, pref.getString(KEY_NATIONALITY, ""));
        user.put(KEY_AGE, pref.getString(KEY_AGE, ""));
        user.put(KEY_profile_image, pref.getString(KEY_profile_image, ""));
        user.put(KEY_NAME, pref.getString(KEY_NAME, ""));

        user.put(KEY_IS_DRIVER, pref.getString(KEY_IS_DRIVER, ""));
        user.put(KEY_IS_SUPPLIER, pref.getString(KEY_IS_SUPPLIER, ""));
        user.put(KEY_IS_USER, pref.getString(KEY_IS_USER, ""));
        user.put(KEY_IS_SALES, pref.getString(KEY_IS_SALES, ""));
        return user;
    }

    public void clear_session() {
        editor.remove(IS_LOGIN);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_SHARE_ID);
        editor.remove(KEY_ID);
        editor.remove(KEY_EMAIL);
        editor.remove(KEY_BASETOKEN);
        editor.remove(KEY_PHONE);
        editor.remove(KEY_TEMP_PASSWORD);

        editor.remove(KEY_STATUS);
        editor.remove(KEY_SUB_STATUS);
        editor.remove(KEY_SUB_START);
        editor.remove(KEY_SUB_END);

        editor.remove(KEY_IS_AVAILABLE);
        editor.remove(KEY_NATIONALITY);
        editor.remove(KEY_AGE);
        editor.remove(KEY_profile_image);
        editor.remove(KEY_NAME);
        editor.remove(KEY_IS_CART_ITEM_COUNT);
//        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    // Get app start State
    public boolean isfirsttime() {
        return pref.getBoolean(IS_FIRSTTIME, false);
    }

    public boolean IsSupplierExist() {
        if (pref.getString(KEY_IS_SUPPLIER, "").equalsIgnoreCase("yes")) {
            return true;
        } else {
            return false;
        }
    }

    public void saveFCMToken(String fcm_token) {
        editor.putString(KEY_FCM_TOEKN, fcm_token);
        editor.commit();
    }

    public String getFCMToken() {
        return pref.getString(KEY_FCM_TOEKN, "");
    }

//TODO: DRIVER SESSION-----

    /**
     * Create login session
     */
    public void createDriverSession(String driver_id, String is_available, String age, String name,
                                    String nationality, String access_token, String scan_code,
                                    String is_driver, String profile_pic,String is_user,String is_supplier,String is_sales) {
        // Storing login value as TRUE
        editor.putBoolean(KEY_DRIVER, true);
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID, driver_id);
        editor.putString(KEY_USERNAME, name);
        editor.putString(KEY_BASETOKEN, access_token);
        editor.putString(KEY_NATIONALITY, nationality);
        editor.putString(KEY_IS_AVAILABLE, is_available);
        editor.putString(KEY_AGE, age);
        editor.putString(QRCODEURL, scan_code);
        editor.putString(KEY_profile_image, profile_pic);

        editor.putString(KEY_DRIVER_SUBSCCRIBE, is_driver);
        editor.putString(KEY_IS_SUPPLIER, is_supplier);
        editor.putString(KEY_IS_USER, is_user);
        editor.putString(KEY_IS_SALES, is_sales);
        editor.commit();
    }

    public HashMap<String, String> getDriveDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_ID, pref.getString(KEY_ID, ""));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, ""));
        user.put(KEY_BASETOKEN, pref.getString(KEY_BASETOKEN, ""));
        user.put(KEY_NATIONALITY, pref.getString(KEY_NATIONALITY, ""));
        user.put(KEY_IS_AVAILABLE, pref.getString(KEY_IS_AVAILABLE, ""));
        user.put(KEY_AGE, pref.getString(KEY_AGE, ""));
        user.put(QRCODEURL, pref.getString(QRCODEURL, ""));
        user.put(KEY_DRIVER_SUBSCCRIBE, pref.getString(KEY_DRIVER_SUBSCCRIBE, ""));
        user.put(KEY_profile_image, pref.getString(KEY_profile_image, ""));
//        user.put(KEY_HEADER_IMAGE, pref.getString(KEY_HEADER_IMAGE, ""));
//        user.put(KEY_DRIVER_SUBSCCRIBE, pref.getString(KEY_DRIVER_SUBSCCRIBE, ""));
        return user;
    }

    // Get Login State
    public boolean isDriverLoggedIn() {
        return pref.getBoolean(KEY_DRIVER, false);
    }

    // Get Login State
    public boolean isDriverInfoSaved() {
        if (pref.getString(KEY_USERNAME, "").equalsIgnoreCase("") &&
                pref.getString(KEY_AGE, "").equalsIgnoreCase("")) {
            return false;
        } else {
            return true;
        }

    }

    public void clear_session_driver() {
        editor.clear();
        editor.commit();
        Intent i = new Intent(_context, SelectDriverTypeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public void saveDriverType(String driverType) {
        editor.putString(KEY_DRIVER_TYPE, driverType);
        editor.commit();
    }

    public String getDriverType() {
        return pref.getString(KEY_DRIVER_TYPE, "");
    }


    //TODO ---------------------------chagres----------------------------

    public void saveCharges(String vat, String freshhomee_fee, String transaction_charges) {
        editor.putString(KEY_VAT, vat);
        editor.putString(KEY_FRESHHOMEEFEE, freshhomee_fee);
        editor.putString(KEY_TRANSACTIONCHARGES, transaction_charges);
        editor.commit();
    }

    public HashMap<String, String> getChagres() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(KEY_VAT, pref.getString(KEY_VAT, ""));
        data.put(KEY_FRESHHOMEEFEE, pref.getString(KEY_FRESHHOMEEFEE, ""));
        data.put(KEY_TRANSACTIONCHARGES, pref.getString(KEY_TRANSACTIONCHARGES, ""));
        return data;
    }

    //-------------------language----------------
    public void saveLanguageType(String driverType) {
        editor.putString(KEY_LANGUAGE, driverType);
        editor.commit();
    }

    public String getLanguageType() {
        return pref.getString(KEY_LANGUAGE, "");
    }

//TODO------------------PLAN SUBSCRIPTION-------------------

    public void saveSubscriptionDetails(String status, String sub_status, String sub_start, String sub_end) {
        editor.putString(KEY_STATUS, status);
        editor.putString(KEY_SUB_STATUS, sub_status);
        editor.putString(KEY_SUB_START, sub_start);
        editor.putString(KEY_SUB_END, sub_end);
        editor.commit();
    }

    public HashMap<String, String> getSubscriptionDetails() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(KEY_STATUS, pref.getString(KEY_STATUS, ""));
        data.put(KEY_SUB_STATUS, pref.getString(KEY_SUB_STATUS, ""));
        data.put(KEY_SUB_START, pref.getString(KEY_SUB_START, ""));
        data.put(KEY_SUB_END, pref.getString(KEY_SUB_END, ""));
        return data;
    }

    //TODO ------------------DRIVER INFO-----------------------
    public void saveDriverWallet(String wallet, String is_available) {
        editor.putString(KEY_WALLET, wallet);
        editor.putString(KEY_IS_AVAILABLE, is_available);
        editor.commit();
    }

    public HashMap<String, String> getSDriverWallet() {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put(KEY_WALLET, pref.getString(KEY_WALLET, ""));
        data.put(KEY_IS_AVAILABLE, pref.getString(KEY_IS_AVAILABLE, ""));
        return data;
    }

    //TODO ------------------COUNTRY NAME-----------------------
    public void saveCountryName(String country_name) {
        editor.putString(KEY_COUNTRY_NAME, country_name);
        editor.commit();
    }

    public String getCountryName() {
        return pref.getString(KEY_COUNTRY_NAME, "");
    }

    public String getCartItem() {
        return pref.getString(KEY_IS_CART_ITEM_COUNT, "");
    }
}