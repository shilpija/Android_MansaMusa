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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.MyPlanPaymentAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.datamodel.myCards;
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

public class ChooseCard extends AppCompatActivity  implements View.OnClickListener {
    RecyclerView recycler_cardlist;
    LinearLayout linear_addcard;
    ImageView image_back, image_ok;
    TextView text_addedcards;
    boolean isOnline = false;
    ApiInterface apiInterface;
    ArrayList<myCards> arrayListCards;
    public static String selected_card_id = "", selected_masked_num = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proceed_payment_plans);
        arrayListCards = new ArrayList<>();

        apiInterface = ApiClient.getInstance().getClient();
        
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        image_ok = (ImageView) findViewById(R.id.image_ok);
        image_ok.setOnClickListener(this);

        text_addedcards = (TextView) findViewById(R.id.text_addedcards);

        linear_addcard = (LinearLayout) findViewById(R.id.linear_addcard);
        linear_addcard.setOnClickListener(this);

        recycler_cardlist = (RecyclerView) findViewById(R.id.recycler_cardlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ChooseCard.this);
        recycler_cardlist.setLayoutManager(mLayoutManager);
        recycler_cardlist.setItemAnimator(new DefaultItemAnimator());
        isOnline = getIntent().getBooleanExtra("isOnline", false);
      
    }

    private void GetMyCard() {
        final ProgressDialog progressDialog = new ProgressDialog(ChooseCard.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        calls = apiInterface.GetCards();
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
                            arrayListCards = new ArrayList<>();
                            selected_card_id = "";
                            selected_masked_num = "";
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                myCards mycards = new myCards();
                                mycards.setCard_id(obj.getString("card_id"));
                                mycards.setCard_image(obj.getString("card_image"));
                                mycards.setCard_type(obj.getString("card_type"));
                                mycards.setExp_date(obj.getString("exp_date"));
                                mycards.setExpired(obj.getString("expired"));
                                mycards.setMasked_num(obj.getString("masked_num"));
                                mycards.setName(obj.getString("name"));
                                mycards.setType(obj.getString("type"));
                                mycards.setSelected(false);
                                arrayListCards.add(mycards);
                            }
                            MyPlanPaymentAdapter adapter = new MyPlanPaymentAdapter(ChooseCard.this, arrayListCards);
                            recycler_cardlist.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ChooseCard.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ChooseCard.this, getResources().getString(R.string.server_error));
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
    

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_addcard:
                Intent i = new Intent(ChooseCard.this, AddCardDetailActivity.class);
                startActivity(i);
                break;


            case R.id.image_back:
                ChooseCard.this.finish();
                break;

            case R.id.image_ok:
                SelectPayMethod();
                break;
        }
    }
    
    

    private void SelectPayMethod() {
        if (selected_card_id.equalsIgnoreCase("")) {
            CommonUtilFunctions.Error_Alert_Dialog(ChooseCard.this, getResources().getString(R.string.select_card));
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("cardid", selected_card_id);
            resultIntent.putExtra("maskedno", selected_masked_num);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            GetMyCard();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(ChooseCard.this, getResources().getString(R.string.internetconnection));
        }
    }

    public static void selectedcardid(String card_id, String masked_num) {
        selected_card_id = card_id;
        selected_masked_num = masked_num;
    }
}
