//package com.freshhome.fragments;
//
//
//import android.Manifest;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.content.ContextCompat;
//import android.support.v4.content.FileProvider;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.CheckBox;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.freshhome.CommonUtil.CommonMethods;
//import com.freshhome.CommonUtil.CommonUtilFunctions;
//import com.freshhome.CommonUtil.FlowLayout;
//import com.freshhome.MainActivity_NavDrawer;
//import com.freshhome.R;
//import com.freshhome.retrofit.ApiClient;
//import com.freshhome.retrofit.ApiInterface;
//import com.google.gson.JsonElement;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//import okhttp3.MediaType;
//import okhttp3.MultipartBody;
//import okhttp3.RequestBody;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//import static android.app.Activity.RESULT_OK;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class AddMenuFragment extends Fragment implements View.OnClickListener {
//    ImageView image_addcuisine, image_addcategory;
//    EditText edit_dishname, edit_dishdescription, edit_dishprice, edit_item_pretime;
//    LinearLayout linear_submit;
//    CircleImageView circle_image;
//    ApiInterface apiInterface;
//    ArrayList<String> array_category, array_cuisines;
//    FlowLayout flow_layout_cuisines, flow_layout_category;
//    private static final int REQUEST_IMAGE_CAPTURE = 2;
//    Uri mCapturedImageURI;
//    String fileName = "";
//    private static final int RESULT_LOAD_IMAGE = 1;
//    int PERMISSION_ALL = 1;
//    String image_namewith_path = "";
//    String[] PERMISSIONS = {
//            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            android.Manifest.permission.CAMERA
//    };
//
//    public AddMenuFragment() {
//        // Required empty public constructor
//    }
//
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View v = inflater.inflate(R.layout.fragment_add_menu, container, false);
//        MainActivity_NavDrawer.heading.setText(R.string.addmenu);
//        array_category = new ArrayList<>();
//        array_cuisines = new ArrayList<>();
//        apiInterface = ApiClient.getClient().create(ApiInterface.class);
//
//        image_addcuisine = (ImageView) v.findViewById(R.id.image_addcuisine);
//        image_addcuisine.setOnClickListener(this);
//
//        image_addcategory = (ImageView) v.findViewById(R.id.image_addcategory);
//        image_addcategory.setOnClickListener(this);
//
//        edit_dishname = (EditText) v.findViewById(R.id.edit_dishname);
//        edit_dishdescription = (EditText) v.findViewById(R.id.edit_dishdescription);
//        edit_dishprice = (EditText) v.findViewById(R.id.edit_dishprice);
//        edit_item_pretime = (EditText) v.findViewById(R.id.edit_item_pretime);
//
//        circle_image = (CircleImageView) v.findViewById(R.id.circle_image);
//        circle_image.setOnClickListener(this);
//
//        linear_submit = (LinearLayout) v.findViewById(R.id.linear_submit);
//        linear_submit.setOnClickListener(this);
//
//        flow_layout_cuisines = (FlowLayout) v.findViewById(R.id.flow_layout_cuisines);
//        flow_layout_category = (FlowLayout) v.findViewById(R.id.flow_layout_category);
//
////if open camere or notl
//        requestpermission(false);
////        if (CommonMethods.checkConnection()) {
////            getcategories();
////        } else {
////            CommonUtilFunctions.Error_Alert_Dialog(AddMenuActivity.this, getResources().getString(R.string.internetconnection));
////        }
//
//        return v;
//    }
//
//    private void requestpermission(boolean opencam) {
//        int result;
//        List<String> listPermissionsNeeded = new ArrayList<>();
//        for (String p : PERMISSIONS) {
//            result = ContextCompat.checkSelfPermission(getActivity(), p);
//            if (result != PackageManager.PERMISSION_GRANTED) {
//                listPermissionsNeeded.add(p);
//            }
//        }
//        if (!listPermissionsNeeded.isEmpty()) {
//            ActivityCompat.requestPermissions(getActivity(), listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
//        } else {
//            if (opencam == true) {
//                show_dialog();
//            }
//        }
//    }
//
//    private void activeTakePhoto() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
//            fileName = System.currentTimeMillis() + ".png";
//            File dir = new File(Environment.getExternalStorageDirectory() + "/FreshHome");
//            File output = new File(dir, fileName);
//            if (Build.VERSION.SDK_INT >= 24) {
//                mCapturedImageURI = FileProvider.getUriForFile(getActivity(), getActivity().getApplicationContext().getPackageName() + ".provider", output);
//            } else {
//                mCapturedImageURI = Uri.fromFile(output);
//            }
////            mCapturedImageURI = Uri.fromFile(output);
//            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//    private void activeGallery() {
//        Intent intent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(intent, RESULT_LOAD_IMAGE);
//    }
//
//
//    private void getcategories() {
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        progressDialog.show();
//        Call<JsonElement> calls = apiInterface.GetCategories();
//
//        calls.enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                try {
//                    if (response.code() == 200) {
//                        array_category = new ArrayList<>();
//                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
//                        if (object.getString("code").equalsIgnoreCase("200")) {
//                            JSONArray jarrary = object.getJSONArray("success");
//                            for (int i = 0; i < jarrary.length(); i++) {
//                                JSONObject obj = jarrary.getJSONObject(i);
//                                array_category.add(obj.getString("category_name"));
//                            }
//                            getCuisines();
//                        } else {
//                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
//                        }
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                call.cancel();
//            }
//        });
//    }
//
//    private void getCuisines() {
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        progressDialog.show();
//        Call<JsonElement> calls = apiInterface.GetCuisines();
//
//        calls.enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                try {
//                    if (response.code() == 200) {
//                        array_cuisines = new ArrayList<>();
//                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
//                        if (object.getString("code").equalsIgnoreCase("200")) {
//                            JSONArray jarrary = object.getJSONArray("success");
//                            for (int i = 0; i < jarrary.length(); i++) {
//                                JSONObject obj = jarrary.getJSONObject(i);
//                                array_cuisines.add(obj.getString("cuisine_name"));
//                            }
//                        } else {
//                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
//                        }
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                call.cancel();
//            }
//        });
//    }
//
//    private void addmenu() {
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setCancelable(false);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        progressDialog.show();
//
//        File file = new File(image_namewith_path);
//        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
//
//        MultipartBody.Part body =
//                MultipartBody.Part.createFormData("dish_image", file.getName(), requestBody);
//        // add another part within the multipart request
//        RequestBody dishname =
//                RequestBody.create(MediaType.parse("text/plain"), edit_dishname.getText().toString().trim());
//        RequestBody dishdescription =
//                RequestBody.create(MediaType.parse("text/plain"), edit_dishdescription.getText().toString().trim());
//        RequestBody dishprice =
//                RequestBody.create(MediaType.parse("text/plain"), edit_dishprice.getText().toString().trim());
//        RequestBody pre_time =
//                RequestBody.create(MediaType.parse("text/plain"), edit_item_pretime.getText().toString().trim());
//        RequestBody dish_category =
//                RequestBody.create(MediaType.parse("text/plain"), array_category.get(0));
//
//        RequestBody dish_cuisines =
//                RequestBody.create(MediaType.parse("text/plain"), array_cuisines.get(0));
//
//        RequestBody weight =
//                RequestBody.create(MediaType.parse("text/plain"), "0.5");
//
//        Call<JsonElement> calls = apiInterface.addMenuItem(dishname,
//                dishdescription,
//                dishprice,
//                dish_cuisines,
//                dish_category, pre_time, body);
//
//        calls.enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                try {
//                    if (response.code() == 200) {
//                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
//                        if (object.getString("code").equalsIgnoreCase("200")) {
//                            JSONObject obj = object.getJSONObject("success");
//
//                            CommonUtilFunctions.success_Alert_Dialog(getActivity(), "Dish added successfully!");
//                            reload_screen();
//
//                        } else {
//                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
//                        }
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//                if (progressDialog.isShowing()) {
//                    progressDialog.dismiss();
//                }
//                call.cancel();
//                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
//            }
//        });
//    }
//
//    private void reload_screen() {
//        edit_dishname.setText("");
//        edit_item_pretime.setText("");
//        edit_dishprice.setText("");
//        edit_dishdescription.setText("");
//        image_namewith_path = "";
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//
//            case R.id.image_addcuisine:
//                show_add_dialogcuisines();
//                break;
//
//
//            case R.id.image_addcategory:
//                show_add_dialog_categories();
//                break;
//
//            case R.id.circle_image:
//                requestpermission(true);
//                break;
//
//
//            case R.id.linear_submit:
//                if (image_namewith_path.equalsIgnoreCase("")) {
//                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_dishimage));
//                } else if (edit_dishname.getText().toString().trim().equalsIgnoreCase("")) {
//                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_dishname));
//                } else if (edit_dishdescription.getText().toString().trim().equalsIgnoreCase("")) {
//                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_dishdescription));
//                } else if (edit_dishprice.getText().toString().trim().equalsIgnoreCase("")) {
//                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_dishprice));
//                } else if (edit_item_pretime.getText().toString().trim().equalsIgnoreCase("")) {
//                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_dishtime));
//                } else if (array_cuisines.size() == 0) {
//                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_dishcuisine));
//                } else if (array_category.size() == 0) {
//                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.enter_dishcategory));
//                } else {
//                    if (CommonMethods.checkConnection()) {
//                        addmenu();
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
//                    }
//                }
//                break;
//
//        }
//    }
//
//    private void show_add_dialog_categories() {
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.layout_add_cuisine_category_dialog);
//
//        TextView heading = (TextView) dialog.findViewById(R.id.heading);
//        heading.setText(getResources().getString(R.string.addcategory));
//        TextView text_done = (TextView) dialog.findViewById(R.id.text_done);
//        final FlowLayout flow_layout = (FlowLayout) dialog.findViewById(R.id.flow_layout);
//
//        text_done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                //selected save
//                CommonMethods.hideSoftKeyboard(getActivity());
//                add_categories_tomain();
//            }
//        });
//        setcategories(flow_layout);
//        dialog.show();
//    }
//
//    private void add_categories_tomain() {
//        flow_layout_category.removeAllViews();
//        for (int i = 0; i < array_category.size(); i++) {
//            View view = getLayoutInflater().inflate(R.layout.single_row_menucategories, null);
//            TextView textview = (TextView) view.findViewById(R.id.textview);
//            textview.setText(array_category.get(i));
//            flow_layout_category.addView(view);
//        }
//    }
//
//    private void setcategories(final FlowLayout flow_layout) {
//        flow_layout.removeAllViews();
//        for (int i = 0; i < array_category.size(); i++) {
//            View view = getLayoutInflater().inflate(R.layout.single_row_cate_cuis, null);
//            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);
//            final CheckBox check_remove = (CheckBox) view.findViewById(R.id.check_remove);
//            check_remove.setTag(array_category.get(i));
//            check_remove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//
//                    } else {
//                        for (int j = 0; j < array_category.size(); j++) {
//                            if (check_remove.getTag().equals(array_category.get(j))) {
//                                array_category.remove(j);
//                                setcategories(flow_layout);
//                            }
//                        }
//                    }
//                }
//            });
//            TextView textview = (TextView) view.findViewById(R.id.textview);
//            textview.setText(array_category.get(i));
//            flow_layout.addView(view);
//        }
//    }
//
//    private void show_add_dialogcuisines() {
//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.layout_add_cuisine_category_dialog);
//        TextView heading = (TextView) dialog.findViewById(R.id.heading);;
//        TextView text_done = (TextView) dialog.findViewById(R.id.text_done);
//        final FlowLayout flow_layout = (FlowLayout) dialog.findViewById(R.id.flow_layout);
//
//        text_done.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                //selected save
//                CommonMethods.hideSoftKeyboard(getActivity());
//                setcuisines_tomain();
//            }
//        });
//        heading.setText(getResources().getString(R.string.addcuisine));
//        setcuisines(flow_layout);
//        dialog.show();
//    }
//
//    private void setcuisines_tomain() {
//        flow_layout_cuisines.removeAllViews();
//        for (int i = 0; i < array_cuisines.size(); i++) {
//            View view = getLayoutInflater().inflate(R.layout.single_row_menucategories, null);
//            TextView textview = (TextView) view.findViewById(R.id.textview);
//            textview.setText(array_cuisines.get(i));
//            flow_layout_cuisines.addView(view);
//        }
//    }
//
//    private void setcuisines(final FlowLayout flow_layout) {
//        flow_layout.removeAllViews();
//        for (int i = 0; i < array_cuisines.size(); i++) {
//            View view = getLayoutInflater().inflate(R.layout.single_row_cate_cuis, null);
//            LinearLayout linear_bg_category = (LinearLayout) view.findViewById(R.id.linear_bg_category);
//            final CheckBox check_remove = (CheckBox) view.findViewById(R.id.check_remove);
//            check_remove.setTag(array_cuisines.get(i));
//            check_remove.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                    if (isChecked) {
//
//                    } else {
//                        for (int j = 0; j < array_cuisines.size(); j++) {
//                            if (check_remove.getTag().equals(array_cuisines.get(j))) {
//                                array_cuisines.remove(j);
//                                setcuisines(flow_layout);
//                            }
//                        }
//                    }
//                }
//            });
//
//            TextView textview = (TextView) view.findViewById(R.id.textview);
//            textview.setText(array_cuisines.get(i));
//            flow_layout.addView(view);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 1: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    // permission was granted, yay! Do the
//                    // contacts-related task you need to do.a
//                } else {
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission
//                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
////                        showStoragePermissionRationale();
//                    } else {
//                        CommonUtilFunctions.show_permission_alert(getActivity(), "Please allow all permission to use all funtionalities in app.");
//                    }
//                }
//            }
//            return;
//        }
//
//        // other 'case' lines to check for other
//        // permissions this app might request.
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_IMAGE_CAPTURE:
//                if (resultCode == RESULT_OK) {
//                    image_namewith_path = CommonUtilFunctions.onCaptureImageResult(getActivity(), data,
//                            mCapturedImageURI, false, fileName, circle_image);
//                    Log.i("image", image_namewith_path);
//                }
//                break;
//            //gallery
//            case RESULT_LOAD_IMAGE:
//                if (data != null && !data.equals("")) {
//                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview(getActivity(), data,
//                            false, circle_image);
//                    Log.i("image", image_namewith_path);
//                }
//                break;
//
//        }
//    }
//
//    private void show_dialog() {
//        final Dialog dialog_img = new Dialog(getActivity());
//        dialog_img.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog_img.setContentView(R.layout.dialog_for_menuimage);
//        dialog_img.findViewById(R.id.btnChoosePath)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog_img.dismiss();
//                        activeGallery();
//
//                    }
//                });
//        dialog_img.findViewById(R.id.btnTakePhoto)
//                .setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog_img.dismiss();
//                        activeTakePhoto();
//                    }
//                });
//
//        // show dialog on screen
//        dialog_img.show();
//    }
//}
