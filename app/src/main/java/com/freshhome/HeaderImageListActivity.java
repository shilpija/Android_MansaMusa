package com.freshhome;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.freshhome.AdapterClass.HeaderImageAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.datamodel.NameID;
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

public class HeaderImageListActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    RecyclerView headerimageList;
    ApiInterface apiInterface;
    ArrayList<NameID> headerImagesArray;
    UserSessionManager sessionManager;
    private static final int REQUEST_BANNER_IMAGE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_header_image_list);
        headerImagesArray = new ArrayList<>();
        sessionManager=new UserSessionManager(HeaderImageListActivity.this);
        apiInterface = ApiClient.getInstance().getClient();
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        headerimageList = (RecyclerView) findViewById(R.id.headerimageList);
        headerimageList.setLayoutManager(new GridLayoutManager(this, 2));
        headerimageList.setItemAnimator(new DefaultItemAnimator());

        if (CommonMethods.checkConnection()) {
            if(sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToEat)){
                getData("user");
            }else{
                getData("supplier");
            }

        } else {
            CommonUtilFunctions.Error_Alert_Dialog(this, getResources().getString(R.string.internetconnection));
        }


    }

    private void getData(String type) {
        final ProgressDialog progressDialog = new ProgressDialog(HeaderImageListActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        Call<JsonElement> calls = apiInterface.GetBannerImages(type);

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        headerImagesArray = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                NameID nameID = new NameID();
                                nameID.setImg_url(obj.getString("image"));
                                nameID.setId(obj.getString("banner_id"));
                                headerImagesArray.add(nameID);
                            }
                            HeaderImageAdapter adapter = new HeaderImageAdapter(HeaderImageListActivity.this, headerImagesArray);
                            headerimageList.setAdapter(adapter);

                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(HeaderImageListActivity.this, getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(HeaderImageListActivity.this, getResources().getString(R.string.server_error));
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.image_back:
                HeaderImageListActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_BANNER_IMAGE) {
            if (resultCode == RESULT_OK) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("banner_id", data.getStringExtra("banner_id"));
                resultIntent.putExtra("image_url", data.getStringExtra("image_url"));
                setResult(Activity.RESULT_OK, resultIntent);
                HeaderImageListActivity.this.finish();
            }
        }
    }
}
