package com.freshhome;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCardDetailActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_submit, linear_delete;
    ImageView image_back, img_card;
    EditText edit_name, edit_cardno, edit_expirydate, edit_cvv;
    ApiInterface apiInterface;
    String exp_month = "", exp_year = "", card_id = "";
    private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
    private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
    private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
    private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char DIVIDER = '-';

    private static final int TOTAL_DATE_SYMBOLS = 5; // size of pattern 00/00
    private static final int TOTAL_DATE_DIGITS = 4; // max numbers of digits in pattern: 00 * 4
    private static final int DIVIDER_DATE_MODULO = 3; // means divider position is every 5th symbol beginning with 1
    private static final int DIVIDER_DATE_POSITION = DIVIDER_DATE_MODULO - 1; // means divider position is every 4th symbol beginning with 0
    private static final char DIVIDER_DATE = '/';

    //    https://github.com/ragunathjawahar/android-saripaar/blob/master/saripaar/src/main/java/commons/validator/routines/CreditCardValidator.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card_detail);
        apiInterface = ApiClient.getInstance().getClient();
        img_card = (ImageView) findViewById(R.id.img_card);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);
        linear_delete = (LinearLayout) findViewById(R.id.linear_delete);
        linear_delete.setOnClickListener(this);
        edit_name = (EditText) findViewById(R.id.edit_name);
        edit_cardno = (EditText) findViewById(R.id.edit_cardno);
        edit_expirydate = (EditText) findViewById(R.id.edit_expirydate);
        edit_cvv = (EditText) findViewById(R.id.edit_cvv);

        if (getIntent().hasExtra("card_id")) {
            card_id = getIntent().getStringExtra("card_id");
            edit_name.setText(getIntent().getStringExtra("name"));
            edit_cardno.setText(getIntent().getStringExtra("masked_num"));
            String exp_Date = getIntent().getStringExtra("exp_date");
            String dyear = exp_Date.split("/")[1];
            dyear = dyear.substring(2, 4);
            edit_expirydate.setText(exp_Date.split("/")[0] + "/" + dyear);
            Picasso.get().load(getIntent().getStringExtra("card_img")).into(img_card);
            linear_delete.setVisibility(View.VISIBLE);
        } else {
            linear_delete.setVisibility(View.GONE);
        }

        edit_cardno.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }
        });

        edit_expirydate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_DATE_SYMBOLS, DIVIDER_DATE_MODULO, DIVIDER_DATE)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DATE_DIGITS), DIVIDER_DATE_POSITION, DIVIDER_DATE));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                AddCardDetailActivity.this.finish();
                break;

            case R.id.linear_submit:
                String edate = edit_expirydate.getText().toString().trim();
                if (edate.contains("/")) {
                    exp_month = edate.split("/")[0];
                    exp_year = edate.split("/")[1];
                }
                if (edit_name.getText().toString().trim().equalsIgnoreCase("") || edit_cardno.getText().toString().trim().equalsIgnoreCase("") || edit_cvv.getText().toString().trim().equalsIgnoreCase("")
                        || edit_expirydate.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(AddCardDetailActivity.this, getResources().getString(R.string.mandatory_fiels));
                } else {
                    if (CommonMethods.checkConnection()) {
                        AddDeleteCard(true);
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddCardDetailActivity.this, getResources().getString(R.string.internetconnection));
                    }
                }
                break;

            case R.id.linear_delete:
                if (CommonMethods.checkConnection()) {
                    AddDeleteCard(false);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(AddCardDetailActivity.this, getResources().getString(R.string.internetconnection));
                }
                break;

        }
    }

    private void AddDeleteCard(boolean isAdd) {
        final ProgressDialog progressDialog = new ProgressDialog(AddCardDetailActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isAdd) {
            if (card_id.equalsIgnoreCase("")) {
                //add card
                calls = apiInterface.AddCardDetails(edit_cardno.getText().toString().trim(),
                        exp_month,
                        exp_year,
                        edit_cvv.getText().toString().trim(),
                        edit_name.getText().toString().trim());
            } else {
                //update card
                calls = apiInterface.UpdateCardDetails(edit_cardno.getText().toString().trim(),
                        exp_month,
                        exp_year,
                        edit_cvv.getText().toString().trim(),
                        edit_name.getText().toString().trim(), card_id);
            }
        } else {
            //delete card
            calls = apiInterface.DeleteCardDetails(card_id);
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
                            AddCardDetailActivity.this.finish();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(AddCardDetailActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(AddCardDetailActivity.this, getResources().getString(R.string.server_error));
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

    private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
        boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
        for (int i = 0; i < s.length(); i++) { // check that every element is right
            if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect &= divider == s.charAt(i);
            } else {
                isCorrect &= Character.isDigit(s.charAt(i));
            }
        }
        return isCorrect;
    }

    private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
        final StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < digits.length; i++) {
            if (digits[i] != 0) {
                formatted.append(digits[i]);
                if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                    formatted.append(divider);
                }
            }
        }

        return formatted.toString();
    }

    private char[] getDigitArray(final Editable s, final int size) {
        char[] digits = new char[size];
        int index = 0;
        for (int i = 0; i < s.length() && index < size; i++) {
            char current = s.charAt(i);
            if (Character.isDigit(current)) {
                digits[index] = current;
                index++;
            }
        }
        return digits;
    }
}
