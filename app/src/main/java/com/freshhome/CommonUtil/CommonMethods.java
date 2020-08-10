package com.freshhome.CommonUtil;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freshhome.LoginActivity;
import com.freshhome.PlanActivity;
import com.freshhome.R;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by HP.
 */

public class CommonMethods {

    public static String parseDateToMMMMDDYYYY(String time) {
        String inputPattern = "yyyy-MM-dd HH:mm:ss";
        String outputPattern = "dd/MM/yyyy HH:mm";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * hidesoft  keyboard
     **/
    public static void hideSoftKeyboard(Activity context) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus()
                    .getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * valid email address
     **/
    public static boolean isValidEmailAddress(String emailAddress) {
        String emailRegEx;
        Pattern pattern;
        // Regex for a valid email address
        emailRegEx = "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,4}$";
        // Compare the regex with the email address
        pattern = Pattern.compile(emailRegEx);
        Matcher matcher = pattern.matcher(emailAddress);
        if (!matcher.find()) {
            return false;
        }
        return true;
    }

    /**
     * email matcher
     **/
    public static boolean isEditTextContainEmail(EditText argEditText) {

        try {
            Pattern pattern = Pattern
                    .compile("[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                            + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                            + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+");
            Matcher matcher = pattern.matcher(argEditText.getText().toString()
                    .trim());
            return matcher.matches();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Showing the status in toast
    public static void showtoast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showtoast_connection(Context context, boolean isConnected) {
        String message;
        if (!isConnected) {
            message = "Sorry! Not connected to internet";

            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    public static void showtoast_no_connetion(Context context) {
        Toast.makeText(context, "Sorry! Not connected to internet", Toast.LENGTH_SHORT).show();

    }

    // Method to manually check connection status
    public static boolean checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        return isConnected;
    }


    public static boolean isMyServiceRunning(Context con, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) con.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void setLocale(String localeName, Context context) {
        Locale locale = new Locale(localeName);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());
    }

    public final static boolean isValidEmail(CharSequence target) {

        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();

    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = 12;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }


    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//    public static Bitmap retriveVideoFrameFromVideo(String videoPath)
//            throws Throwable
//    {
//        Bitmap bitmap = null;
//        FFmpegMediaMetadataRetriever mediaMetadataRetriever = null;
//        try
//        {
//            mediaMetadataRetriever = new FFmpegMediaMetadataRetriever ();
//            if (Build.VERSION.SDK_INT >= 14)
//                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
//            else
//                mediaMetadataRetriever.setDataSource(videoPath);
//            //   mediaMetadataRetriever.setDataSource(videoPath);
//            mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ALBUM);
//            mediaMetadataRetriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_ARTIST);
//            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//            throw new Throwable(
//                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
//                            + e.getMessage());
//
//        }
//        finally
//        {
//            if (mediaMetadataRetriever != null)
//            {
//                mediaMetadataRetriever.release();
//            }
//        }
//        return bitmap;
//    }

    public static void share_with_other(Context context, String msg) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }


    public static void share_image_with_other(Context context, String msg, ImageView imageivew) {
        Bitmap bitmap = getBitmapFromView(imageivew);
        try {
            File file = new File(context.getExternalCacheDir(), "qrcode.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            context.startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        } else {
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

    public static byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }

    public static String checkNull(String text) {
        if (text.equalsIgnoreCase("null")) {
            return "";
        } else {
            return text;
        }
    }

    public static String checkNullRatings(String text) {
        if (text.equalsIgnoreCase("null")) {
            return "0";
        } else {
            return text;
        }
    }

    public static String getDayName(String input) {
        String output = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat odf = new SimpleDateFormat("EEE");
        Date date = null;
        try {
            date = sdf.parse(input);
            output = odf.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return output;
    }

    public static String getDate(String input) {
        String output = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat odf = new SimpleDateFormat("dd");
        Date date = null;
        try {
            date = sdf.parse(input);
            output = odf.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return output;
    }

    public static String getMonthYear(String input) {
        String output = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat odf = new SimpleDateFormat("MMM");
        SimpleDateFormat ydf = new SimpleDateFormat("yyyy");
        Date date = null;
        try {
            date = sdf.parse(input);
            output = odf.format(date);
            output = output + " " + ydf.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return output;
    }

    public static String getDateDay(String input) {
        String output = "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat odf = new SimpleDateFormat("dd/MMM");
        Date date = null;
        try {
            date = sdf.parse(input);
            output = odf.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return output;
    }

    public static String getMonth(String input) {
        String output = "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM,yyyy");
        SimpleDateFormat odf = new SimpleDateFormat("MMM");
        Date date = null;
        try {
            date = sdf.parse(input);
            output = odf.format(date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }


        return output;
    }

    public static void ShowLoginDialog(final Context con) {
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
//        alertDialogBuilder.setMessage("To access this functionality, Please login first.")
//                .setCancelable(false)
//                .setPositiveButton("Login",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                              Intent i=new Intent(con, LoginActivity.class);
//                              con.startActivity(i);
//                            }
//                        });
//        alertDialogBuilder.setNegativeButton("Cancel",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        dialog.cancel();
//                    }
//                });
//        AlertDialog alert = alertDialogBuilder.create();
//        alert.show();

        final Dialog dialog = new Dialog(con);
        dialog.setContentView(R.layout.layout_loginfirst_dialog);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_login = (TextView) dialog.findViewById(R.id.text_login);
        text_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(con, LoginActivity.class);
                con.startActivity(i);
            }
        });

        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    public static void BreakdownPrice(Context con, String s, TextView textView, TextView text_final_price,
                                      String vat, String transaction_charges, String freshhomee) {
        if (s.equalsIgnoreCase(".")) {

        } else {
            String raw_price = s.replaceAll("[^0-9-.]", "").toString();
            if (!raw_price.toString().equals("")) {
                double actul_price = Double.parseDouble(raw_price);
                double totoal_discount = Double.valueOf(vat) + Double.valueOf(transaction_charges) + Double.valueOf(freshhomee);
                double price = (actul_price / 100.0f) * totoal_discount;
                double final_price = actul_price - price;
                text_final_price.setText("" + final_price);
                textView.setText("Transaction Charges " + transaction_charges + "%" + "  Mansa Musa fee " + freshhomee + "% " + " VAT " + vat + "% ");
                textView.setTextColor(con.getResources().getColor(R.color.app_color_orange));
            } else {
//            Toast.makeText(con, "Amount cannot be empty", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public static void DiscountedPrice(Context con, String discount_S, TextView textView, String actual_price) {
        if (discount_S.equalsIgnoreCase(".")) {

        } else {
            String raw_discoutn = discount_S.replaceAll("[^0-9-.]", "").toString();
            if (!raw_discoutn.toString().equals("")) {
                double actul_price = Double.parseDouble(actual_price);
                double discount_i = Double.parseDouble(raw_discoutn);
                double price = (actul_price / 100.0f) * discount_i;
                double final_price = actul_price - price;
//            Toast.makeText(con, "" + final_price, Toast.LENGTH_SHORT).show();
                textView.setText("The price buyer will see after discount is " + ConstantValues.currency + " " + final_price);
                textView.setTextColor(con.getResources().getColor(R.color.app_color_orange));
            } else {
//            Toast.makeText(con, "Amount cannot be empty", Toast.LENGTH_SHORT).show();

            }
        }
    }

    public static String timeLeftForDeliverySec(String deliveryTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a", Locale.ENGLISH);
        long difference = 0;
        try {
            String currentDateandTime = sdf.format(new Date());
            Date dcurrenttime = sdf.parse(currentDateandTime);
            Date ddelivertime = sdf.parse(deliveryTime.toLowerCase());
            difference = ddelivertime.getTime() - dcurrenttime.getTime();
            //this will give exact seconds
            difference = TimeUnit.MILLISECONDS.toSeconds(difference);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return String.valueOf(difference);
    }

    public static long timeLeftForDeliverySecond(String deliveryTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a", Locale.ENGLISH);
        long difference = 0;
        try {
            String currentDateandTime = sdf.format(new Date());
            Date dcurrenttime = sdf.parse(currentDateandTime);
            Date ddelivertime = sdf.parse(deliveryTime.toLowerCase());
            difference = ddelivertime.getTime() - dcurrenttime.getTime();
            //this will give exact seconds
            difference = TimeUnit.MILLISECONDS.toSeconds(difference);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return difference;
    }

    public static String calculateTime(long seconds) {
        int day = (int) TimeUnit.SECONDS.toDays(seconds);
        long hours = TimeUnit.SECONDS.toHours(seconds) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60);
        long second = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) * 60);

        System.out.println("D " + day + " H " + hours + " Min " + minute + " Sec " + second);
        return hours + " : " + minute + " : " + second;
    }


    public static void openInvoice(Context context, String invoice_url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=" + invoice_url), "text/html");
        context.startActivity(intent);
    }


    public static String convertIntoDate(String value) {
        long timestamp = Long.parseLong(value);
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    public static void show_buy_plan(final Context context) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Warning!");
        alertDialog.setMessage(context.getResources().getString(R.string.buy_plan));
        // Setting OK Button
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, PlanActivity.class);
                context.startActivity(i);
            }
        });
        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();

            }
        });
        alertDialog.show();
    }

    public static String getCountryName(Context context,double latitude, double longitude) {
        String country = "";
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            city = addresses.get(0).getSubAdminArea();
//            state = addresses.get(0).getAdminArea();
             country = addresses.get(0).getCountryName();
//            pincode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return country;
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    public static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    public static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static String getDirectionsUrl(Context context,LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";
        String key = "key=" + context.getResources().getString(R.string.api_key);
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&" + key;
        return url;
    }

    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobile_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifi_info = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mobile_info != null) {
                if (mobile_info.isConnectedOrConnecting()
                        || wifi_info.isConnectedOrConnecting()) {
                    return true;
                } else {
                    return false;
                }
            } else {
                if (wifi_info.isConnectedOrConnecting()) {
                    return true;
                } else {
                    return false;
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("" + e);
            return false;
        }
    }

}
