package com.freshhome.CommonUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.SupplierDetailActivity;
import com.freshhome.SupplierMenuActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class CommonUtilFunctions {
    public static Bitmap bm;
    public static Matrix matrix;

    public static void previewCapturedImage(File media_file, Matrix matrix,
                                            int rotationAngle, ImageView img) throws IOException {
        try {

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 3;

            final Bitmap bitmap = BitmapFactory.decodeFile(
                    media_file.getAbsolutePath(), options);

            ExifInterface ei = new ExifInterface(media_file.getAbsolutePath());

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationAngle = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationAngle = 180;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationAngle = 270;
                    break;
            }
            matrix.setRotate(rotationAngle);
            // to make image in portrait mode.
            Bitmap cambitmap = Bitmap.createBitmap(bitmap, 0, 0,
                    options.outWidth, options.outHeight, matrix, true);

            img.setImageBitmap(cambitmap);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    public static String onCaptureImageResult(Context con, Intent data,
                                              Uri imageUri, boolean b, String fileName, ImageView img_edit_profile) {
        String bitmap_name = "";
        matrix = new Matrix();
        try {
            bm = MediaStore.Images.Media.getBitmap(
                    con.getContentResolver(), imageUri);

            bm = Bitmap.createScaledBitmap(bm, 800, 600, true);
            img_edit_profile.setImageBitmap(bm);


        } catch (IOException e) {
            e.printStackTrace();
        }
        createDirectoryAndSaveFile(bm, fileName);
        bitmap_name = Environment.getExternalStorageDirectory() + "/FreshHome/" + fileName.toString();
        return bitmap_name;
    }


    public static String onCaptureImageResultblur(Context con, Intent data,
                                                  Uri imageUri, boolean b, String fileName, ImageView img_edit_profile, ImageView profile_image_blur) {
        String bitmap_name = "";
        matrix = new Matrix();
        try {
            bm = MediaStore.Images.Media.getBitmap(
                    con.getContentResolver(), imageUri);

//            ExifInterface ei = new ExifInterface(imageUri.getPath());
//
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotationAngle = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotationAngle = 180;
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotationAngle = 270;
//                    break;
//            }
//            if (getDeviceName().contains("Samsung")) {
//                rotationAngle = 90;
//                matrix.setRotate(rotationAngle);
//            } else {
//                matrix.setRotate(rotationAngle);
//            }

//            if (bool == false) {
//                imageView.setImageBitmap(Bitmap.createScaledBitmap(bm,
//                        120, 160, true));
//            } else {

            bm = Bitmap.createScaledBitmap(bm, 800, 600, true);
//            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

//            imageView.setImageBitmap(bm);
//            }

            img_edit_profile.setImageBitmap(bm);

//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                Bitmap blurredBitmap = RSBlurProcessor.blur(con, bm);
//                profile_image_blur.setImageBitmap(blurredBitmap);
//            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        createDirectoryAndSaveFile(bm, fileName);
        bitmap_name = Environment.getExternalStorageDirectory() + "/FreshHome/" + fileName.toString();
        return bitmap_name;
    }


    public static String onSelectFromGalleryResult_imageview(Context con, Intent data, boolean bool,
                                                             ImageView img_edit_profile) {
        String bitmap_name = "";
        Bitmap selectedImage = null;

//
        if (data != null && !data.equals("")) {
            Bundle bundle = data.getExtras();
            Uri selectImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = con.getContentResolver().openInputStream(selectImage);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 450);

                img_edit_profile.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String fileName = System.currentTimeMillis() + ".png";
            createDirectoryAndSaveFile(selectedImage, fileName);
            bitmap_name = Environment.getExternalStorageDirectory() + "/FreshHome/" + fileName.toString();

//            Bitmap selectedBitmap = bundle.getParcelable("data");
//            Uri selectedImage = getImageUri(con, selectedBitmap);

//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = con.getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            File f = new File(picturePath);
//            cursor.close();
//            bm = BitmapFactory.decodeFile(picturePath);

//            bitmap_name = picturePath;

//        if (bool == false) {
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(bm,
//                    bm.getWidth(),bm.getHeight(), true));
//        } else {

//        }
        }
        return bitmap_name;
    }

    public static String onSelectFromGalleryResult_imageview_blur(Context con, Intent data, boolean bool,
                                                                  ImageView img_edit_profile, ImageView profile_image_blur) {
        String bitmap_name = "";
        Bitmap selectedImage = null;

//
        if (data != null && !data.equals("")) {
            Bundle bundle = data.getExtras();
            Uri selectImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = con.getContentResolver().openInputStream(selectImage);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 450);

                img_edit_profile.setImageBitmap(selectedImage);

//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                    Bitmap blurredBitmap = RSBlurProcessor.blur(con, selectedImage);
//                    profile_image_blur.setImageBitmap(blurredBitmap);
//                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String fileName = System.currentTimeMillis() + ".png";
            createDirectoryAndSaveFile(selectedImage, fileName);
            bitmap_name = Environment.getExternalStorageDirectory() + "/FreshHome/" + fileName.toString();

//            Bitmap selectedBitmap = bundle.getParcelable("data");
//            Uri selectedImage = getImageUri(con, selectedBitmap);

//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = con.getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            File f = new File(picturePath);
//            cursor.close();
//            bm = BitmapFactory.decodeFile(picturePath);

//            bitmap_name = picturePath;

//        if (bool == false) {
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(bm,
//                    bm.getWidth(),bm.getHeight(), true));
//        } else {

//        }
        }
        return bitmap_name;
    }

    public static String onSelectFromGalleryResult_imageview(Context con, Intent data, boolean bool,
                                                             ImageView img_edit_profile, ImageView profile_image_blur) {
        String bitmap_name = "";
        Bitmap selectedImage = null;

//
        if (data != null && !data.equals("")) {
            Bundle bundle = data.getExtras();
            Uri selectImage = data.getData();
            InputStream imageStream = null;
            try {
                imageStream = con.getContentResolver().openInputStream(selectImage);
                selectedImage = BitmapFactory.decodeStream(imageStream);
                selectedImage = getResizedBitmap(selectedImage, 450);

                img_edit_profile.setImageBitmap(selectedImage);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    Bitmap blurredBitmap = RSBlurProcessor.blur(con, selectedImage);
                    profile_image_blur.setImageBitmap(blurredBitmap);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String fileName = System.currentTimeMillis() + ".png";
            createDirectoryAndSaveFile(selectedImage, fileName);
            bitmap_name = Environment.getExternalStorageDirectory() + "/FreshHome/" + fileName.toString();

//            Bitmap selectedBitmap = bundle.getParcelable("data");
//            Uri selectedImage = getImageUri(con, selectedBitmap);

//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = con.getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            File f = new File(picturePath);
//            cursor.close();
//            bm = BitmapFactory.decodeFile(picturePath);

//            bitmap_name = picturePath;

//        if (bool == false) {
//            imageView.setImageBitmap(Bitmap.createScaledBitmap(bm,
//                    bm.getWidth(),bm.getHeight(), true));
//        } else {

//        }
        }
        return bitmap_name;
    }


    public static String onCaptureImageResult_imageview(Context con, Intent data, boolean bool, Uri imageUri,
                                                        boolean b, String fileName, ImageView img_edit_profile) {
        String bitmap_name = "";
        matrix = new Matrix();
        try {
            bm = MediaStore.Images.Media.getBitmap(
                    con.getContentResolver(), imageUri);

//            ExifInterface ei = new ExifInterface(imageUri.getPath());
//
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotationAngle = 90;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotationAngle = 180;
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotationAngle = 270;
//                    break;
//            }
//            if (getDeviceName().contains("Samsung")) {
//                rotationAngle = 90;
//                matrix.setRotate(rotationAngle);
//            } else {
//                matrix.setRotate(rotationAngle);
//            }

//            if (bool == false) {
//                imageView.setImageBitmap(Bitmap.createScaledBitmap(bm,
//                        120, 160, true));
//            } else {

            bm = Bitmap.createScaledBitmap(bm, 600, 450, true);
//            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);

//            imageView.setImageBitmap(bm);
//            }
            img_edit_profile.setImageBitmap(bm);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                Bitmap blurredBitmap = RSBlurProcessor.blur(con, bm);
//                image_bg.setImageBitmap(blurredBitmap);
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        createDirectoryAndSaveFile(bm, fileName);
        bitmap_name = Environment.getExternalStorageDirectory() + "/FreshHome/" + fileName.toString();
        return bitmap_name;
    }


    public static String getRealPathFromURI(Context con, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = con.getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public static Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    public static Bitmap ShrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }


    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }

    public static void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/FreshHome/");

        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/FreshHome/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File(Environment.getExternalStorageDirectory() + "/FreshHome/"), fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    public static void Enable_GPS_Setting(final Context con) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(con);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                con.startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public static String capture_image_rotated(Context con, Intent data, ImageView imageView, boolean bool, Uri imageUri) {
        String image_path = "";

        File direct = new File(Environment.getExternalStorageDirectory() + "/FreshHome");
        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + "/FreshHome/");
            wallpaperDirectory.mkdirs();
        }
        String output_file_name = Environment.getExternalStorageDirectory() + "/FreshHome/" + System.currentTimeMillis() + ".jpg";
        File pictureFile = new File(output_file_name);
        if (pictureFile.exists()) {
            pictureFile.delete();
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            Bitmap realImage = MediaStore.Images.Media.getBitmap(
                    con.getContentResolver(), imageUri);
//            Bitmap realImage = BitmapFactory.decodeByteArray(data, 0, data.length);

            ExifInterface exif = new ExifInterface(pictureFile.toString());

            Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")) {
                realImage = rotate(realImage, 90);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")) {
                realImage = rotate(realImage, 270);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                realImage = rotate(realImage, 180);
            } else if (exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")) {
                realImage = rotate(realImage, 90);
            }

            boolean bo = realImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.close();
            imageView.setImageBitmap(realImage);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image_path;
    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        //       mtx.postRotate(degree);
        mtx.setRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }


    public static void call(Context context, String no) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + no));
        context.startActivity(intent);
    }

    public static void Error_Alert_Dialog(Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Mansa Musa");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message
        if(!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public static void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Mansa Musa!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
            }
        });

        // Showing Alert Message

        if(!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public static void show_permission_alert(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Mansa Musa!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                intent.setData(uri);
                context.startActivity(intent);
            }
        });

        alertDialog.setButton(Dialog.BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    /**
     * Date Picker future date only
     **/
    public static String DatePickerFuture(final Context context, final TextView edittext) {
        // TODO Auto-generated method stub
        final String[] date = {""};
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        date[0] = dayOfMonth + "-" + (monthOfYear + 1) + "/" + year;
                        edittext.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.show();
        return date[0];

    }


    /**
     * Date Picker
     **/
    public static String DatePickerDialog_DOB(final Context context, final TextView edittext) {
        // TODO Auto-generated method stub
        final String[] dob = {""};
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        c.set(year, monthOfYear, dayOfMonth);
                        if (c.after(Calendar.getInstance())) {
                            DatePickerDialog_DOB(context, edittext);
//                            showShortToast(context, "Please Select Valid Date");
                        } else {
                            String date_of_birth = year + "-" + (monthOfYear + 1) + "-"
                                    + dayOfMonth;
                            dob[0] = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            edittext.setText(changeDateFormatDDMMYYYY(date_of_birth));
                            edittext.setTextColor(context.getResources().getColor(R.color.black));
                        }
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.show();
        return dob[0];

    }

    public static void DatePickerToDialog(Context context, final TextView edittext, final String textFrom) {
        // TODO Auto-generated method stub
        long startDate = 0;
        final Calendar c = Calendar.getInstance();
        String[] date = textFrom.split("/");
        int mYear = Integer.parseInt(date[2]);
        int mMonth = Integer.parseInt(date[1]) - 1;
        int mDay = Integer.parseInt(date[0]);
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date_i = sdf.parse(textFrom);
            startDate = date_i.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        String date_of_birth = "";
                        if (monthOfYear + 1 < 10) {
                            date_of_birth = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" +
                                    +year;
                        } else {
                            date_of_birth = dayOfMonth + "/" + (monthOfYear + 1) + "/" +
                                    +year;
                        }
                        edittext.setText(date_of_birth);
                    }
                }, mYear, mMonth, mDay);


        dpd.getDatePicker().setMinDate(startDate);
        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.show();

    }

    public static void DatePickerFromDialog(Context context, final TextView edittext) {
        // TODO Auto-generated method stub

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        String date_of_birth = "";
                        if (monthOfYear + 1 < 10) {
                            date_of_birth = dayOfMonth + "/" + "0" + (monthOfYear + 1) + "/" +
                                    +year;
                        } else {
                            date_of_birth = dayOfMonth + "/" + (monthOfYear + 1) + "/" +
                                    +year;
                        }
                        edittext.setText(date_of_birth);
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.show();

    }

    public static String DatePickerDialog_yy_mm_dd(Context context, final TextView edittext) {
        // TODO Auto-generated method stub
        final String[] date = {""};
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        Format formatter = new SimpleDateFormat("HH:mm:ss");
                        date[0] = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                        Calendar temp = Calendar.getInstance();
                        edittext.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year + " " + formatter.format(temp.getTime()));
                    }
                }, mYear, mMonth, mDay);
        dpd.getDatePicker().setMinDate(System.currentTimeMillis());
        dpd.getDatePicker().setCalendarViewShown(false);
        dpd.show();
        return date[0];
    }

    /**
     * Time Picker
     **/
    public static String timepickerdialog(final Context context, final TextView textView) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY, selectedHour);
                temp.set(Calendar.MINUTE, selectedMinute);
                Calendar cal = Calendar.getInstance();

                textView.setTextColor(context.getResources().getColor(R.color.black));

//                if (temp.getTime().before(cal.getTime())) {
//                    Toast.makeText(context, "Please Select Valid Time", Toast.LENGTH_SHORT).show();
//                    textView.setText("");
//                } else {
                Format formatter = new SimpleDateFormat("hh:mm a");
                textView.setText(formatter.format(temp.getTime()));
//                }

            }
        }, hour, minute, false);//Yes 24 hour time
//        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
        return "selected";
    }

    /**
     * Time Picker
     **/
    public static String timepickerCheckout(final Context context, final TextView textView) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY, selectedHour);
                temp.set(Calendar.MINUTE, selectedMinute);
                Calendar cal = Calendar.getInstance();

                textView.setTextColor(context.getResources().getColor(R.color.black));

//                if (temp.getTime().before(cal.getTime())) {
//                    Toast.makeText(context, "Please Select Valid Time", Toast.LENGTH_SHORT).show();
//                    textView.setText("");
//                } else {
                Format formatter = new SimpleDateFormat("hh:mm a");
                Format formatter1 = new SimpleDateFormat("hh:mm");
                if (formatter.format(temp.getTime()).contains("a.m.")) {
                    textView.setText(formatter1.format(temp.getTime()) + " AM");
                } else {
                    textView.setText(formatter1.format(temp.getTime()) + " PM");
                }
//                textView.setText(formatter.format(temp.getTime()));
//                }

            }
        }, hour, minute, false);//Yes 24 hour time
//        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
        return "selected";
    }

    /**
     * Time Picker
     **/
    public static String timepickerdialogPrep(final Context context, final TextView textView) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY, selectedHour);
                temp.set(Calendar.MINUTE, selectedMinute);
                Calendar cal = Calendar.getInstance();
                textView.setTextColor(context.getResources().getColor(R.color.black));
                Format formatter = new SimpleDateFormat("hh:mm");
                textView.setText(formatter.format(temp.getTime()));
            }
        }, hour, minute, true);//true 24 hour time
        mTimePicker.show();
        return "selected";
    }

    public static String timepickerdialog_to(final Context context, final TextView textView,
                                             final String s) {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        final SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
        final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd hh:mm:ss 'GMT'Z yyyy");
        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                Calendar temp = Calendar.getInstance();
                temp.set(Calendar.HOUR_OF_DAY, selectedHour);
                temp.set(Calendar.MINUTE, selectedMinute);
                Log.e("time", temp.getTime().toString());
                long current = temp.getTimeInMillis();
                Calendar cal = Calendar.getInstance();
                long selected = 0;
                try {
                    String time = displayFormat.format(parseFormat.parse(s));
                    String[] time_array = time.split(":");
                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_array[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(time_array[1]) + 1);
                    //from time
                    selected = cal.getTimeInMillis();

                } catch (ParseException e) {
                    e.printStackTrace();
                }

//                if (temp.getTime().before(cal.getTime())) {
//                    Toast.makeText(context, "Please Select Valid Time", Toast.LENGTH_SHORT).show();
//                    textView.setText("");
//                } else
                if (current - selected < 17940000) {
                    //time difference less than 5 hours
                    Toast.makeText(context, "You have to work minimum 5 hours a day", Toast.LENGTH_SHORT).show();
                } else {
                    Format formatter = new SimpleDateFormat("hh:mm a");
                    textView.setText(parseFormat.format(temp.getTime()));

                }


            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");

        mTimePicker.show();
        return "selected";
    }

    public static String padding_str(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return ("0" + String.valueOf(c));
        }
    }

    public static float getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);

            Date today = new Date();

            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear = 0, cMonth = 0, cDay = 0;

//        if (createdConvertedDate.after(todayWithZeroTime)) {
        Calendar cCal = Calendar.getInstance();
        cCal.setTime(createdConvertedDate);
        cYear = cCal.get(Calendar.YEAR);
        cMonth = cCal.get(Calendar.MONTH);
        cDay = cCal.get(Calendar.DAY_OF_MONTH);

//        } else {
//            Calendar cCal = Calendar.getInstance();
//            cCal.setTime(todayWithZeroTime);
//            cYear = cCal.get(Calendar.YEAR);
//            cMonth = cCal.get(Calendar.MONTH);
//            cDay = cCal.get(Calendar.DAY_OF_MONTH);
//        }


    /*Calendar todayCal = Calendar.getInstance();
    int todayYear = todayCal.get(Calendar.YEAR);
    int today = todayCal.get(Calendar.MONTH);
    int todayDay = todayCal.get(Calendar.DAY_OF_MONTH);
    */

        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return dayCount;
    }

    public static String changeDateFormatYYYYMMDD(String dob) {
        String str = "";
        if (!dob.equalsIgnoreCase("")) {
            String outputPattern = "yyyy-MM-dd";
            String inputPattern = "dd-MM-yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;

            try {
                date = inputFormat.parse(dob);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return str;

    }

    public static String changeDateFormatDDMMYYYY(String dob) {
        String str = "";
        if (!dob.equalsIgnoreCase("")) {
            String inputPattern = "yyyy-MM-dd";
            String outputPattern = "dd-MM-yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;

            try {
                date = inputFormat.parse(dob);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return str;

    }

    public static String changeDateFormatDDMMYYYY_2(String dob) {
        String str = "";
        if (!dob.equalsIgnoreCase("")) {
            String inputPattern = "yyyy-MM-dd";
            String outputPattern = "dd/MM/yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;

            try {
                date = inputFormat.parse(dob);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return str;

    }

    public static String changeDateFormatEEEDDMMYYYY(String date_i) {
        String str = "";
        if (!date_i.equalsIgnoreCase("")) {
            String outputPattern = "EEE, dd-MM-yyyy";
            String inputPattern = "E-MMM-dd-yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;

            try {
                date = inputFormat.parse(date_i);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return str;

    }

    public static String changeDateFormatYYYY_MM_dd(String date_i) {
        String str = "";
        if (!date_i.equalsIgnoreCase("")) {
            String inputPattern = "EEE, dd-MM-yyyy";
            String outputPattern = "yyyy-MM-dd";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;

            try {
                date = inputFormat.parse(date_i);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return str;

    }

    public static String changeTimeFormahhmm(String dob) {
        String str = "";
        if (!dob.equalsIgnoreCase("")) {
            String inputPattern = "hh:mm:ss";
            String outputPattern = "hh:mm";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;

            try {
                date = inputFormat.parse(dob);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return str;

    }

    public static String change12hourto24(String time) {
        String time_output = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = inputFormat.parse(time);
            time_output = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time_output;
    }

    public static String change24hourto12(String time) {
        String time_output = "";
        SimpleDateFormat outputFormat = new SimpleDateFormat("hh:mm aa");
        SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = null;
        try {
            date = inputFormat.parse(time);
            time_output = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return time_output;
    }

    public static String changeDateDD_MMM_YYYY(String date_i) {
        String str = "";
        if (!date_i.equalsIgnoreCase("")) {
            String inputPattern = "yyyy-mm-dd";
            String outputPattern = "dd MMM, yyyy";
            SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
            SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);
            Date date = null;

            try {
                date = inputFormat.parse(date_i);
                str = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return str;

    }

    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                resources.getDisplayMetrics());
    }

    public static String NullToZero(String value) {
        if (value.equalsIgnoreCase("null") || value.equalsIgnoreCase(" ")) {
            value = "0";
        }
        return value;
    }

    public static String extractYTId(String ytUrl) {
        String vId = null;
        Pattern pattern = Pattern.compile(
                "^https?://.*(?:youtu.be/|v/|u/\\w/|embed/|watch?v=)([^#&?]*).*$",
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(ytUrl);
        if (matcher.matches()) {
            vId = matcher.group(1);
        }
        return vId;
    }



}

