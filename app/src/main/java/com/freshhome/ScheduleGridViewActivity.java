package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;

import com.freshhome.AdapterClass.ScheduleGridAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.datamodel.ScheduleData;
import com.freshhome.datamodel.ScheduleMenuItem;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ScheduleGridViewActivity extends AppCompatActivity implements View.OnClickListener {
    GridView gridViewschedule;
    ImageView image_back;
    ArrayList<ScheduleData> arrayScheduledata;
    ApiInterface apiInterface;
    String supplier_id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_grid_view);
        arrayScheduledata = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        supplier_id=getIntent().getStringExtra("supplier_id");
        gridViewschedule = (GridView) findViewById(R.id.gridViewschedule);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(ScheduleGridViewActivity.this, getResources().getString(R.string.internetconnection));
        }

    }

    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(ScheduleGridViewActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetOtherSupplierSchedule(supplier_id);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        arrayScheduledata = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject obj = object.getJSONObject("success");
                           JSONArray array=obj.getJSONArray("data");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj_schedule = array.getJSONObject(i);
                                ScheduleData data = new ScheduleData();
                                data.setSchedule_date(obj_schedule.getString("schedule_date"));
                                data.setStart_time(obj_schedule.getString("start_time"));
                                data.setEnd_time(obj_schedule.getString("end_time"));
                                data.setScheduleItem(obj_schedule.getString("total_qty"));
                                arrayScheduledata.add(data);
                            }

                            ScheduleGridAdapter adapter = new ScheduleGridAdapter(ScheduleGridViewActivity.this, arrayScheduledata);
                            gridViewschedule.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ScheduleGridViewActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ScheduleGridViewActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(ScheduleGridViewActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                ScheduleGridViewActivity.this.finish();
                break;
        }
    }
}
