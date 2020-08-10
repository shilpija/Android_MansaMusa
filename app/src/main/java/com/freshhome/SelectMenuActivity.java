package com.freshhome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshhome.AdapterClass.SelectMenuAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.MenuSupplier;
import com.freshhome.datamodel.NameID;
import com.freshhome.datamodel.ScheduleData;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectMenuActivity extends AppCompatActivity implements View.OnClickListener {
    RecyclerView menuList;
    ApiInterface apiInterface;
    TextView text_heading;
    ImageView image_back;
    public static ImageView image_ok;
    public static ArrayList<NameID> arrayList;
    String current_day = "", first_day = "";
    ArrayList<MenuSupplier> array_menu;
    public static ArrayList<MenuSupplier> array_selected_menu, array_raw_selected_menu;
    UserSessionManager sessionManager;
    ArrayList<ScheduleData> arraySchedule_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_menu);
        arraySchedule_data = new ArrayList<>();
        array_selected_menu = new ArrayList<>();
        array_raw_selected_menu = new ArrayList<>();
        sessionManager = new UserSessionManager(SelectMenuActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        array_selected_menu = getIntent().getParcelableArrayListExtra("menu");
//        arrayList = new ArrayList<>();
//        arrayList = (ArrayList<NameID>) getIntent().getSerializableExtra("weekarray");

        text_heading = (TextView) findViewById(R.id.text_heading);
//        first_day = getIntent().getStringExtra("date");
//        setDate(arrayList, first_day, "0");

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        image_ok = (ImageView) findViewById(R.id.image_ok);
        image_ok.setOnClickListener(this);
        image_ok.setVisibility(View.INVISIBLE);

        menuList = (RecyclerView) findViewById(R.id.menuList);

        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(SelectMenuActivity.this, getResources().getString(R.string.internetconnection));
        }

    }

    private void setDate(ArrayList<NameID> arrayList, String day, String s) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (!day.equalsIgnoreCase("")) {
                if (arrayList.get(i).getName().split("-")[2].equalsIgnoreCase(day)) {
                    String date = arrayList.get(i).getName().split("-")[2] + "," + arrayList.get(i).getName().split("-")[1] + " " + arrayList.get(i).getName().split("-")[3];
                    current_day = arrayList.get(i).getName().split("-")[2];
                    text_heading.setText(date);
                }
            } else {
                if (arrayList.get(i).getName().split("-")[2].equalsIgnoreCase(s)) {
                    if (arrayList.size() > i + 1) {
                        if (arrayList.get(i).isIsselected()) {
                            String date = arrayList.get(i + 1).getName().split("-")[2] + "," + arrayList.get(i + 1).getName().split("-")[1] + " " + arrayList.get(i + 1).getName().split("-")[3];
                            current_day = arrayList.get(i + 1).getName().split("-")[2];
                            text_heading.setText(date);
//reset menu
                            for (int j = 0; j < array_menu.size(); j++) {
                                array_menu.get(j).setIschecked(false);
                            }
                            SelectMenuAdapter adapter = new SelectMenuAdapter(SelectMenuActivity.this, array_menu, false);
                            menuList.setAdapter(adapter);
                        } else {
                            SelectMenuActivity.this.finish();
                        }
                    }
                }
            }
        }

    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(SelectMenuActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetMenuList(sessionManager.getUserDetails().get("user_id"), "");

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        array_menu = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jarrary = object.getJSONArray("success");

                            for (int i = 0; i < jarrary.length(); i++) {
                                JSONObject obj = jarrary.getJSONObject(i);
                                MenuSupplier menu = new MenuSupplier();
                                menu.setDname(obj.getString("dish_name"));
                                menu.setId(obj.getString("dish_id"));
                                menu.setImageurl(obj.getString("dish_image"));
                                menu.setDprice(obj.getString("dish_price"));
                                menu.setDstatus(obj.getString("status"));
                                menu.setDtime(obj.getString("dish_since"));
                                //pending
                                menu.setDavailable(obj.getString("item_available"));
                                menu.setDpending(obj.getString("user_view"));
                                menu.setDrating(obj.getString("rate_point"));
                                menu.setDveiws(obj.getString("user_view"));
                                menu.setDqtyl("0");
                                menu.setIschecked(false);
                                for (int j = 0; j < array_selected_menu.size(); j++) {
                                    if (obj.getString("dish_id").equalsIgnoreCase(array_selected_menu.get(j).getId())) {
                                        menu.setDqtyl(array_selected_menu.get(j).getDqtyl());
                                        menu.setIschecked(true);
                                    }
                                }
                                array_menu.add(menu);

                            }
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            menuList.setLayoutManager(mLayoutManager);
                            menuList.setItemAnimator(new DefaultItemAnimator());
                            SelectMenuAdapter adapter = new SelectMenuAdapter(SelectMenuActivity.this, array_menu, false);
                            menuList.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(SelectMenuActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(SelectMenuActivity.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(SelectMenuActivity.this, getResources().getString(R.string.server_error));
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                GoBack();
                break;

            case R.id.image_ok:
                getSelectedMenu();
                break;
        }
    }

    private void GoBack() {
//        ArrayList<MenuSupplier> selected_menu = new ArrayList<>();
        Intent resultIntent = new Intent();
// TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putParcelableArrayListExtra("arrayMenu", array_selected_menu);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void getSelectedMenu() {
        array_selected_menu = array_raw_selected_menu;
        ArrayList<MenuSupplier> selected_menu = new ArrayList<>();
        for (int i = 0; i < array_raw_selected_menu.size(); i++) {
            if (array_raw_selected_menu.get(i).isIschecked()) {
                selected_menu.add(array_raw_selected_menu.get(i));
            }
        }
        Intent resultIntent = new Intent();
// TODO Add extras or a data URI to this intent as appropriate.
        resultIntent.putParcelableArrayListExtra("arrayMenu", selected_menu);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    public static void activeNext(ArrayList<MenuSupplier> array_menu_s) {
        array_raw_selected_menu = array_menu_s;
        for (int i = 0; i < array_menu_s.size(); i++) {
            if (array_menu_s.get(i).isIschecked()) {
                image_ok.setVisibility(View.VISIBLE);
                return;
            } else {
                image_ok.setVisibility(View.INVISIBLE);
            }
        }


    }

//    private void UploadData() {
//        final ProgressDialog progressDialog = new ProgressDialog(SelectMenuActivity.this);
//        progressDialog.setCancelable(false);
////        progressDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
//        progressDialog.show();
//        Call<JsonElement> calls = apiInterface.AddMenuSchedule(sessionManager.getUserDetails().get("user_id"));
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
//                        } else {
//                            JSONObject obj = object.getJSONObject("error");
//                            CommonUtilFunctions.Error_Alert_Dialog(SelectMenuActivity.this, obj.getString("msg"));
//                        }
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(SelectMenuActivity.this, getResources().getString(R.string.server_error));
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

    @Override
    public void onBackPressed() {
        GoBack();
        super.onBackPressed();

    }
}
