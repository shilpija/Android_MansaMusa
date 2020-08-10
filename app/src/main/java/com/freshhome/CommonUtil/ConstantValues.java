package com.freshhome.CommonUtil;

public class ConstantValues {
    public static final String TAG = "FreshHome";
    public static String Driver= "4";//driver
    public static String ToCook = "2";//Supplier
    public static String ToEat = "1";//User
    public static String Sales = "5";//Sales person
    public static String currency = "AED";
    public static String currency_2 = "د.إ";

    //driver type // 0 driver company // 1 individual driver
    public static String driver_individual= "6";
    public static String driver_company= "7";

    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";

    public static final int OPENCART = 2;


    //product type
    public static String home_food= "1";
    public static String home_products = "2";
    public static String shops = "3";


public static String attributeTypeSpinner="select";
    public static String attributeTyperadio="radio";
    public static String attributeTypecheckbox="checkbox";
}
