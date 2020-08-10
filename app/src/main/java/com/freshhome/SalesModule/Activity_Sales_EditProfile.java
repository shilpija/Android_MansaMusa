package com.freshhome.SalesModule;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.UserEditProfileActivity;
import com.freshhome.datamodel.NameID;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_Sales_EditProfile extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back, gender_spinner_arrow, city_spinner_arrow;
    EditText edit_name;
    TextView text_dob;
    Spinner genderspinner, cityspinner;
    String gender = "", city = "";
    ApiInterface apiInterface;
    String[] spinner_gender_array;
    ArrayList<NameID> arrayCity;
    String[] spinner_city_array;
    String[] PERMISSIONS = {
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA
    };
    CircleImageView circle_userimage;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    Uri mCapturedImageURI;
    private static final int PLACE_PICKER_REQUEST = 3;
    String fileName = "";
    private static final int RESULT_LOAD_IMAGE = 1;
    int PERMISSION_ALL = 1;
    String image_namewith_path = "";
    LinearLayout linear_update;
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__sales__edit_profile);
        sessionManager = new UserSessionManager(Activity_Sales_EditProfile.this);
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        circle_userimage = (CircleImageView) findViewById(R.id.circle_userimage);
        circle_userimage.setOnClickListener(this);

        gender_spinner_arrow = (ImageView) findViewById(R.id.gender_spinner_arrow);
        gender_spinner_arrow.setOnClickListener(this);

        city_spinner_arrow = (ImageView) findViewById(R.id.city_spinner_arrow);
        city_spinner_arrow.setOnClickListener(this);

        linear_update = (LinearLayout) findViewById(R.id.linear_update);
        linear_update.setOnClickListener(this);

        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_name.setText(getIntent().getStringExtra("name"));
        text_dob = (TextView) findViewById(R.id.text_dob);
        text_dob.setText(getIntent().getStringExtra("dob"));
        text_dob.setOnClickListener(this);

        gender = getIntent().getStringExtra("gender");
        city = getIntent().getStringExtra("city");

        if (!getIntent().getStringExtra("image_url").equalsIgnoreCase("")) {
            Picasso.get().load(getIntent().getStringExtra("image_url"))
                    .placeholder(R.drawable.icon).into(circle_userimage);
        }

        cityspinner = (Spinner) findViewById(R.id.cityspinner);
        genderspinner = (Spinner) findViewById(R.id.genderspinner);

        spinner_gender_array = getResources().getStringArray(R.array.gender_array);
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(Activity_Sales_EditProfile.this, R.layout.layout_spinner_text_bg_transparent, spinner_gender_array);
        genderspinner.setAdapter(genderAdapter);

        if (gender.equalsIgnoreCase("male")) {
            genderspinner.setSelection(1);
        }

        //-----request run time permission for storgae camera------------------
        requestpermission(false);


        if (CommonMethods.checkConnection()) {
            getCitdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, getResources().getString(R.string.internetconnection));
        }


        cityspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (arrayCity.size() != 0) {
                    city = arrayCity.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                Activity_Sales_EditProfile.this.finish();
                break;

            case R.id.city_spinner_arrow:
                cityspinner.performClick();
                break;

            case R.id.gender_spinner_arrow:
                genderspinner.performClick();
                break;

            case R.id.linear_update:
                if (CommonMethods.checkConnection()) {
                    if (image_namewith_path.equalsIgnoreCase("")) {
                        updateProfile_without_Image();
                    } else {
                        updateProfile();
                    }
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this,
                            getResources().getString(R.string.internetconnection));
                }
                break;

            case R.id.text_dob:
                CommonUtilFunctions.DatePickerDialog_DOB(Activity_Sales_EditProfile.this, text_dob);
                break;


            case R.id.circle_userimage:
                requestpermission(true);
                break;

        }
    }


    //getcity
    private void getCitdata() {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Sales_EditProfile.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
        Call<JsonElement> calls = apiInterface.GetFilterData(sessionManager.getCountryName());

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        arrayCity = new ArrayList<>();
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject jsonObject = object.getJSONObject("success");
                            //city array
                            JSONArray city_array = jsonObject.getJSONArray("city");
                            spinner_city_array = new String[city_array.length()];
                            for (int i = 0; i < city_array.length(); i++) {
                                JSONObject obj = city_array.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setId(obj.getString("city_id"));
                                nameID.setName(obj.getString("city_name"));
                                spinner_city_array[i] = obj.getString("city_name");
                                arrayCity.add(nameID);
                            }
                            //set up spinner
                            ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(Activity_Sales_EditProfile.this, R.layout.layout_spinner_text_bg_transparent, spinner_city_array);
                            cityspinner.setAdapter(spinner_adapter);

                            preSelectCity();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void preSelectCity() {
        for (int i = 0; i < spinner_city_array.length; i++) {
            if (spinner_city_array[i].equalsIgnoreCase(city)) {
                cityspinner.setSelection(i);
            }
        }
    }


    // pick image from gallery and click with cam
    private void requestpermission(boolean opencam) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(Activity_Sales_EditProfile.this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(Activity_Sales_EditProfile.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), PERMISSION_ALL);
        } else {
            if (opencam == true) {
                show_dialog();
            }
        }
    }

    private void show_dialog() {
        final Dialog dialog_img = new Dialog(Activity_Sales_EditProfile.this);
        dialog_img.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_img.setContentView(R.layout.dialog_for_menuimage);
        dialog_img.findViewById(R.id.btnChoosePath)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_img.dismiss();
                        activeGallery();

                    }
                });
        dialog_img.findViewById(R.id.btnTakePhoto)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_img.dismiss();
                        activeTakePhoto();
                    }
                });

        // show dialog on screen
        dialog_img.show();
    }

    private void activeTakePhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            fileName = System.currentTimeMillis() + ".png";
            File dir = new File(Environment.getExternalStorageDirectory() + "/FreshHome");
            File output = new File(dir, fileName);
            if (Build.VERSION.SDK_INT >= 24) {
                mCapturedImageURI = FileProvider.getUriForFile(Activity_Sales_EditProfile.this,
                        getApplicationContext().getPackageName() + ".fileprovider", output);
            } else {
                mCapturedImageURI = Uri.fromFile(output);
            }
//            mCapturedImageURI = Uri.fromFile(output);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void activeGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
                    image_namewith_path = CommonUtilFunctions.onCaptureImageResult(Activity_Sales_EditProfile.this, data,
                            mCapturedImageURI, false, fileName, circle_userimage);
                    Log.i("image", image_namewith_path);
                }
                break;
            //gallery
            case RESULT_LOAD_IMAGE:
                if (data != null && !data.equals("")) {
                    image_namewith_path = CommonUtilFunctions.onSelectFromGalleryResult_imageview(Activity_Sales_EditProfile.this, data,
                            false, circle_userimage);
                    Log.i("image", image_namewith_path);
                }
                break;

        }
    }


    //TODO api calls
    private void updateProfile() {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Sales_EditProfile.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        File file = new File(image_namewith_path);
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("image_url", file.getName(), requestBody);

        RequestBody name =
                RequestBody.create(MediaType.parse("text/plain"), edit_name.getText().toString());

        RequestBody citybody =
                RequestBody.create(MediaType.parse("text/plain"), city);

        RequestBody dob =
                RequestBody.create(MediaType.parse("text/plain"), CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()));

        RequestBody gender =
                RequestBody.create(MediaType.parse("text/plain"), genderspinner.getSelectedItem().toString());

        Call<JsonElement> calls = apiInterface.UpdateSalesProfileWithImage(name, gender,
                citybody, dob, body);

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
                            CommonMethods.showtoast(Activity_Sales_EditProfile.this, obj.getString("msg"));
//                            CommonUtilFunctions.success_Alert_Dialog(ChangePassword.this, obj.getString("msg"));
                            Activity_Sales_EditProfile.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, getResources().getString(R.string.server_error));
            }
        });
    }

    private void updateProfile_without_Image() {
        final ProgressDialog progressDialog = new ProgressDialog(Activity_Sales_EditProfile.this);
        progressDialog.setCancelable(false);
//        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls = apiInterface.UpdateSalesProfileWithoutImage(edit_name.getText().toString().trim(),
                genderspinner.getSelectedItem().toString(),
                city,
                CommonUtilFunctions.changeDateFormatYYYYMMDD(text_dob.getText().toString()));

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
                            CommonMethods.showtoast(Activity_Sales_EditProfile.this, obj.getString("msg"));
                            Activity_Sales_EditProfile.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(Activity_Sales_EditProfile.this, getResources().getString(R.string.server_error));
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
