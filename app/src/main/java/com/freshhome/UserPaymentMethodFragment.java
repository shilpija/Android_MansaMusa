package com.freshhome;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.freshhome.AdapterClass.UserPaymentoptionAdapter;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.ccavenue.InitialScreenActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class UserPaymentMethodFragment extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    public static String selected_card_id = "", selected_masked_num = "";
    RecyclerView recycler_cardlist;
    LinearLayout linear_addcard;
    Spinner spinner_paymentmethod;
    ImageView image_arrow, image_back, image_ok;
    TextView text_addedcards;
    boolean isOnline = false;
    ApiInterface apiInterface;
    ArrayList<myCards> arrayListCards;
    private String price = "";

    public static void selectedcardid(String card_id, String masked_num) {
        selected_card_id = card_id;
        selected_masked_num = masked_num;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_user_payment_method);
        arrayListCards = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        spinner_paymentmethod = (Spinner) findViewById(R.id.spinner_paymentmethod);
        spinner_paymentmethod.setOnItemSelectedListener(this);
        image_arrow = (ImageView) findViewById(R.id.image_arrow);
        image_arrow.setOnClickListener(this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        image_ok = (ImageView) findViewById(R.id.image_ok);
        image_ok.setOnClickListener(this);

        setUpPaymentMethod();
        text_addedcards = (TextView) findViewById(R.id.text_addedcards);

        linear_addcard = (LinearLayout) findViewById(R.id.linear_addcard);
        linear_addcard.setOnClickListener(this);

//        linear_done = (LinearLayout) findViewById(R.id.linear_done);
//        linear_done.setVisibility(View.GONE);
//        linear_done.setOnClickListener(this);

        recycler_cardlist = (RecyclerView) findViewById(R.id.recycler_cardlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(UserPaymentMethodFragment.this);
        recycler_cardlist.setLayoutManager(mLayoutManager);
        recycler_cardlist.setItemAnimator(new DefaultItemAnimator());
        isOnline = getIntent().getBooleanExtra("isOnline", false);
        price = getIntent().getStringExtra("price");
        if (isOnline) {
            //both cash and online payment
            spinner_paymentmethod.setClickable(true);
            spinner_paymentmethod.setEnabled(true);
        } else {
            //only online advance payment
            spinner_paymentmethod.setClickable(false);
            spinner_paymentmethod.setEnabled(false);
            //select card payment
            spinner_paymentmethod.setSelection(1);
        }
    }

    private void GetMyCard() {
        final ProgressDialog progressDialog = new ProgressDialog(UserPaymentMethodFragment.this);
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
                            UserPaymentoptionAdapter adapter = new UserPaymentoptionAdapter(UserPaymentMethodFragment.this, arrayListCards);
                            recycler_cardlist.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(UserPaymentMethodFragment.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(UserPaymentMethodFragment.this, getResources().getString(R.string.server_error));
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

    private void setUpPaymentMethod() {
        String[] paymentmethod = getResources().getStringArray(R.array.payment_method);
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(UserPaymentMethodFragment.this, R.layout.layout_spinner_text, paymentmethod);
        spinner_paymentmethod.setAdapter(spinner_adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_addcard:
//                Intent i = new Intent(UserPaymentMethodFragment.this, AddCardDetailActivity.class);
//                startActivity(i);
                if (spinner_paymentmethod.getSelectedItemPosition() == 1) {
                    SelectPayMethodDiff();
//                    if (CommonMethods.checkConnection()) {
//                        //subscribePLan(true);
//                        Intent intent = new Intent(UserPaymentMethodFragment.this, InitialScreenActivity.class);
//                        intent.putExtra("price", price);
//                        intent.putExtra("from", "BuyerPay");
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        CommonUtilFunctions.Error_Alert_Dialog(this, getResources().getString(R.string.internetconnection));
//                    }
                } else {
                    SelectPayMethod();
                }

                break;

            case R.id.image_arrow:
                if (isOnline) {
                    spinner_paymentmethod.performClick();
                }
                break;

            case R.id.image_back:
                UserPaymentMethodFragment.this.finish();
                break;

            case R.id.image_ok:
                SelectPayMethod();
                break;
        }
    }

    //spinner selected
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (spinner_paymentmethod.getSelectedItemPosition() == 1) {
            recycler_cardlist.setVisibility(View.VISIBLE);
            text_addedcards.setVisibility(View.VISIBLE);

        } else {
            selected_card_id = "";
            recycler_cardlist.setVisibility(View.GONE);
            text_addedcards.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void SelectPayMethod() {
        if (spinner_paymentmethod.getSelectedItemPosition() == 1 && selected_card_id.equalsIgnoreCase("")) {
            CommonUtilFunctions.Error_Alert_Dialog(UserPaymentMethodFragment.this, getResources().getString(R.string.select_card));
        } else {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("payMethod", spinner_paymentmethod.getSelectedItem().toString());
            resultIntent.putExtra("cardid", selected_card_id);
            resultIntent.putExtra("maskedno", selected_masked_num);
            if (spinner_paymentmethod.getSelectedItem().toString().equalsIgnoreCase("Cash on delivery")) {
                resultIntent.putExtra("paytype", "cod");
            } else {
                resultIntent.putExtra("paytype", "card");
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }


    private void SelectPayMethodDiff() {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("payMethod", spinner_paymentmethod.getSelectedItem().toString());
            resultIntent.putExtra("cardid", selected_card_id);
            resultIntent.putExtra("maskedno", selected_masked_num);
            if (spinner_paymentmethod.getSelectedItem().toString().equalsIgnoreCase("Cash on delivery")) {
                resultIntent.putExtra("paytype", "cod");
            } else {
                resultIntent.putExtra("paytype", "card");
            }

            setResult(Activity.RESULT_OK, resultIntent);
            finish();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (CommonMethods.checkConnection()) {
            // GetMyCard();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(UserPaymentMethodFragment.this, getResources().getString(R.string.internetconnection));
        }
    }
}
