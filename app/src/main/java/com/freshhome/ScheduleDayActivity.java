package com.freshhome;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.SelectMenuAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.MenuSupplier;
import com.freshhome.datamodel.MonthDays;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleDayActivity extends AppCompatActivity implements View.OnClickListener {
    TextView text_day, text_start_date, text_end_date, text_delete, text_upload_schedule;
    public static TextView text_selectmenu;
    ImageView image_back;
    private int REQUEST_CODE = 11;
    RecyclerView selectedMenuList;
    ArrayList<MenuSupplier> arraymenu;
    public static LinearLayout linear_upload_schedule;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    String schedule_id = "";
    ArrayList<MonthDays> arrayWeekdays;
    boolean isAdd = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_day);
        arraymenu = new ArrayList<>();
        arrayWeekdays = new ArrayList<>();
        sessionManager = new UserSessionManager(ScheduleDayActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        linear_upload_schedule = (LinearLayout) findViewById(R.id.linear_upload_schedule);
        linear_upload_schedule.setOnClickListener(this);
        text_upload_schedule = (TextView) findViewById(R.id.text_upload_schedule);
        selectedMenuList = (RecyclerView) findViewById(R.id.selectedMenuList);
        text_day = (TextView) findViewById(R.id.text_day);
        text_start_date = (TextView) findViewById(R.id.text_start_date);
        text_start_date.setOnClickListener(this);
        text_end_date = (TextView) findViewById(R.id.text_end_date);
        text_end_date.setOnClickListener(this);
        text_selectmenu = (TextView) findViewById(R.id.text_selectmenu);
        text_selectmenu.setOnClickListener(this);
        text_delete = (TextView) findViewById(R.id.text_delete);
        text_delete.setOnClickListener(this);

        arrayWeekdays = getIntent().getParcelableArrayListExtra("date");
        MonthDays monthDays = null;
        if (arrayWeekdays.size() > getIntent().getIntExtra("pos", 0)) {
            monthDays = arrayWeekdays.get(getIntent().getIntExtra("pos", 0));
            text_day.setText(CommonUtilFunctions.changeDateFormatEEEDDMMYYYY(monthDays.getDate()));
            if (monthDays.getIs_selected().equalsIgnoreCase("1")) {
                isAdd = false;
                text_delete.setVisibility(View.VISIBLE);
                text_upload_schedule.setText(getResources().getString(R.string.update_schedule));
                //selected day -------edit-----------
                text_start_date.setText(CommonUtilFunctions.change24hourto12(monthDays.getStart_time()));
                text_end_date.setText(CommonUtilFunctions.change24hourto12(monthDays.getEnd_time()));
                schedule_id = monthDays.getSchedule_id();
                arraymenu = monthDays.getArrayMenu();
                if (arraymenu.size() != 0) {
                    SetMenuItems();
                }
            } else {
                //add day to schedule
                isAdd = true;
                text_upload_schedule.setText(getResources().getString(R.string.upload_schedule));
                text_delete.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.text_start_date:
                CommonUtilFunctions.timepickerdialog(ScheduleDayActivity.this, text_start_date);
                break;

            case R.id.text_end_date:
                CommonUtilFunctions.timepickerdialog_to(ScheduleDayActivity.this, text_end_date, text_start_date.getText().toString().trim());
                break;

            case R.id.image_back:
                ScheduleDayActivity.this.finish();
                break;

            case R.id.text_selectmenu:
                Intent i = new Intent(ScheduleDayActivity.this, SelectMenuActivity.class);
                i.putParcelableArrayListExtra("menu", arraymenu);
                startActivityForResult(i, REQUEST_CODE);
                break;

            case R.id.linear_upload_schedule:
                try {
                    CreateJSON();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;

            case R.id.text_delete:
                show_delete_dialog();
                break;
        }
    }

    private void show_delete_dialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                ScheduleDayActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Alert!");

        // Setting Dialog Message
        alertDialog.setMessage("Do you really want to delete this day?");

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        alertDialog.setButton2("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
            }
        });

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                if (CommonMethods.checkConnection()) {
                    DeleteScheduledDay();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(ScheduleDayActivity.this, getResources().getString(R.string.internetconnection));
                }
                alertDialog.dismiss();

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private void DeleteScheduledDay() {
        final ProgressDialog progressDialog = new ProgressDialog(ScheduleDayActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.DeleteScheduledDay(schedule_id);
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
//                            CommonUtilFunctions.success_Alert_Dialog(MenuDetail.this, obj.getString("message"));
                            ScheduleDayActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ScheduleDayActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ScheduleDayActivity.this, getResources().getString(R.string.server_error));
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

    private void CreateJSON() throws JSONException {
        JSONObject obj = null;
        JSONArray jarray = new JSONArray();
        for (int i = 0; i < arraymenu.size(); i++) {
            obj = new JSONObject();
            try {
                obj.put("menu_id", arraymenu.get(i).getId());
                obj.put("qty", arraymenu.get(i).getDqtyl());

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            jarray.put(obj);
        }
        JSONObject object = new JSONObject();
        object.put("menu_items", jarray);
        object.put("start_time", CommonUtilFunctions.change12hourto24(text_start_date.getText().toString()));
        object.put("end_time", CommonUtilFunctions.change12hourto24(text_end_date.getText().toString()));
        object.put("date", CommonUtilFunctions.changeDateFormatYYYY_MM_dd(text_day.getText().toString()));
        JSONArray jarray_menu = new JSONArray();
        jarray_menu.put(object);
        JSONObject finalobject = new JSONObject();
        finalobject.put("data", jarray_menu);
        String final_out = finalobject.toString();
        if (CommonMethods.checkConnection()) {
            UploadData(finalobject.toString());
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(ScheduleDayActivity.this, getResources().getString(R.string.internetconnection));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        arraymenu = new ArrayList<>();
        if (requestCode == REQUEST_CODE) {
            arraymenu = data.getParcelableArrayListExtra("arrayMenu");
            if (arraymenu.size() != 0) {
                SetMenuItems();
            }
        }
    }

    private void UploadData(String jsondata) throws JSONException {
        final ProgressDialog progressDialog = new ProgressDialog(ScheduleDayActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                (new JSONObject(jsondata)).toString());
        Call<JsonElement> calls;
        if (isAdd) {
            calls = apiInterface.AddMenuSchedule(body);
        } else {
            calls = apiInterface.UpdateMenuSchedule(body);
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
                            JSONObject obj = object.getJSONObject("success");
                            ShowSucessDialog(obj.getString("message"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ScheduleDayActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ScheduleDayActivity.this, getResources().getString(R.string.server_error));
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

    private void SetMenuItems() {
        if (arraymenu.size() > 0) {
            linear_upload_schedule.setVisibility(View.VISIBLE);
            text_selectmenu.setText("Edit Menu");
        } else {
            linear_upload_schedule.setVisibility(View.GONE);
            text_selectmenu.setText("Select Menu");
        }
        //set data to recycler list view
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        selectedMenuList.setLayoutManager(mLayoutManager);
        selectedMenuList.setItemAnimator(new DefaultItemAnimator());
        SelectMenuAdapter adapter = new SelectMenuAdapter(ScheduleDayActivity.this, arraymenu, true);
        selectedMenuList.setAdapter(adapter);
    }

    private void ShowSucessDialog(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                ScheduleDayActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                ScheduleDayActivity.this.finish();
                alertDialog.dismiss();

            }
        });

        // Showing Alert Message

        alertDialog.show();
    }
}
