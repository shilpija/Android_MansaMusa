package com.freshhome.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.MyWalletTransactionAdapter;
import com.freshhome.AdapterClass.PlanAdapter;
import com.freshhome.AdapterClass.UserPaymentoptionAdapter;
import com.freshhome.AddBankAccount_Activity;
import com.freshhome.ChooseCard;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.NationalIDActivity;
import com.freshhome.R;
import com.freshhome.SalesModule.Activity_Sales_RequestDetail;
import com.freshhome.SubmitRequestActivity;
import com.freshhome.datamodel.Plans;
import com.freshhome.datamodel.Wallet;
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
public class MyWalletFragment extends Fragment implements View.OnClickListener {
    RecyclerView recycler_transactionList;
    UserSessionManager sessionManager;
    LinearLayout linear_link_bank, linear_add_money, linear_submit;
    private TextView tv_addmoney;
    ApiInterface apiInterface;
    ArrayList<Wallet> arrayWallet;
    TextView text_total_balance,tv_grey_total_balance, text_cardno,tv_total_centr,
            text_cancel, text_add_bank, text_approvedamt, text_cashout;
    int RESULT_ADDMONEY = 123;
    String card_id = "";
    private String total_balance ="",withdrawStatus;
    ImageView img_delete, image_cashout, image_approval;
    BottomSheetDialog bottomSheetDialog;
    EditText edit_amt;

    public MyWalletFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_wallet, container, false);
        apiInterface = ApiClient.getInstance().getClient();
        sessionManager = new UserSessionManager(getActivity());
        arrayWallet = new ArrayList<>();
        text_approvedamt = (TextView) v.findViewById(R.id.text_approvedamt);
        text_cashout = (TextView) v.findViewById(R.id.text_cashout);
        tv_grey_total_balance = (TextView) v.findViewById(R.id.tv_grey_total_balance);
        text_total_balance = (TextView) v.findViewById(R.id.text_total_balance);
        tv_total_centr = (TextView) v.findViewById(R.id.tv_total_centr);
        text_add_bank = (TextView) v.findViewById(R.id.text_add_bank);
        tv_addmoney = (TextView) v.findViewById(R.id.tv_addmoney);
        linear_link_bank = (LinearLayout) v.findViewById(R.id.linear_link_bank);
        linear_link_bank.setOnClickListener(this);

        linear_add_money = (LinearLayout) v.findViewById(R.id.linear_add_money);
        linear_add_money.setOnClickListener(this);

        image_cashout = (ImageView) v.findViewById(R.id.image_cashout);
        image_cashout.setOnClickListener(this);
        image_approval = (ImageView) v.findViewById(R.id.image_approval);
        image_approval.setOnClickListener(this);

//        if (sessionManager.getLoginType().equalsIgnoreCase(ConstantValues.ToCook)) {
//            linear_link_bank.setVisibility(View.VISIBLE);
//        } else {
//            linear_link_bank.setVisibility(View.GONE);
//        }

        recycler_transactionList = (RecyclerView) v.findViewById(R.id.recycler_transactionList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_transactionList.setLayoutManager(mLayoutManager);
        recycler_transactionList.setItemAnimator(new DefaultItemAnimator());

        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                GetWalletDetails();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
            CommonMethods.ShowLoginDialog(getActivity());
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            if (CommonMethods.checkConnection()) {
                GetWalletDetails();
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
            }
        } else {
            CommonMethods.ShowLoginDialog(getActivity());
        }
    }

    private void GetWalletDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        calls = apiInterface.GetWalletDetails(sessionManager.getLoginType());
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
                            arrayWallet = new ArrayList<>();
                            JSONArray jsonArray;

                            try {
                                 jsonArray = object.getJSONArray("success");
                            }catch (JSONException e){
                                jsonArray = object.getJSONArray("error");

                            }
                            finally {
                                withdrawStatus = object.getString("withdraw");
                                total_balance = object.getString("cashout");
                            }


                            withdrawStatus = object.getString("withdraw");
                            total_balance = object.getString("cashout");
                            if(!total_balance.equalsIgnoreCase("0") && !total_balance.equalsIgnoreCase("")) {
                                if (withdrawStatus.equalsIgnoreCase("2")) {
                                    tv_addmoney.setText(getResources().getString(R.string.withdraw));
                                    linear_add_money.setVisibility(View.GONE);

//                                else {
//                                    //tv_addmoney.setText(getResources().getString(R.string.withdraw));
//                                    linear_add_money.setVisibility(View.VISIBLE);
//                                    linear_add_money.setBackgroundColor(Color.WHITE);
//                                    tv_addmoney.setText(getResources().getString(R.string.withdraw_under_process));
//                                    tv_addmoney.setTextColor(Color.BLACK);
//                                }

                                } else if (withdrawStatus.equalsIgnoreCase("0")) {
                                    linear_add_money.setVisibility(View.VISIBLE);
                                    linear_add_money.setBackgroundColor(Color.WHITE);
                                    tv_addmoney.setText(getResources().getString(R.string.withdraw_under_process));
                                    tv_addmoney.setTextColor(Color.BLACK);
                                } else if (withdrawStatus.equalsIgnoreCase("4")) {
                                    tv_addmoney.setText(getResources().getString(R.string.withdraw));
                                    linear_add_money.setVisibility(View.VISIBLE);
                                }
                            }

                            //for buyer module use...
                            if(sessionManager.getLoginType().equalsIgnoreCase("1")){
                                tv_total_centr.setVisibility(View.VISIBLE);
                                text_total_balance.setVisibility(View.GONE);
                                tv_grey_total_balance.setVisibility(View.GONE);
                                tv_total_centr.setText(object.getString("currency") + " " + object.getString("cashout"));
                                linear_add_money.setVisibility(View.GONE);
                            }

                            //text_approvedamt.setText(object.getString("currency") + " " + object.getString("pendingforapproval"));
                            text_cashout.setText(object.getString("currency") + " " + object.getString("cashout"));
                            //text_total_balance.setText(object.getString("currency") + " " + object.getString("total_balance"));
                            tv_grey_total_balance.setText(object.getString("currency") + " " + object.getString("pendingforapproval"));
                            text_total_balance.setText(object.getString("currency") + " " + object.getString("cashout"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Wallet wallet = new Wallet();
                                wallet.setWallet_id(obj.getString("wallet_id"));
                                wallet.setWallet_amount(obj.getString("modifyamount"));
                                wallet.setWallet_currency(obj.getString("currency"));
                                wallet.setWallet_message(obj.getString("message"));
                                wallet.setWallet_time(obj.getString("time"));
                                wallet.setAmount_status(obj.getString("amountstatus"));
//                                if(sessionManager.getLoginType().equalsIgnoreCase("1")){
//
//                                }else {
//                                    wallet.setWallet_amount(obj.getString("amount"));
//                                }
                                arrayWallet.add(wallet);
                            }

                            if (object.has("bank_details")) {
                                JSONObject bankjson = object.getJSONObject("bank_details");
                                if (bankjson.has("account_number")) {
                                    if (bankjson.getString("account_number").equalsIgnoreCase(null)
                                            || bankjson.getString("account_number").equalsIgnoreCase("null")
                                            || bankjson.getString("account_number").equalsIgnoreCase("")) {
                                        linear_add_money.setVisibility(View.GONE);
                                        text_add_bank.setText(R.string.link_bank);
                                    } else {
                                        text_add_bank.setText(R.string.edit_bank);
                                    }
                                }
                            }
                            MyWalletTransactionAdapter adapter = new MyWalletTransactionAdapter(getActivity(), arrayWallet);
                            recycler_transactionList.setAdapter(adapter);

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            if(obj.getString("msg").equalsIgnoreCase("To activate “My Balance” Section please upload your UAE ID from your profile"))
                                Error_Alert_Dialog(obj.getString("msg"));
                            else
                                CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));

                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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
            case R.id.linear_link_bank:
                Intent i = new Intent(getActivity(), AddBankAccount_Activity.class);
                getActivity().startActivity(i);
                break;

            case R.id.linear_add_money:
                if(tv_addmoney.getText().toString().trim().equalsIgnoreCase("Withdraw")){
                    //startActivity(new Intent(getContext(), SubmitRequestActivity.class));
                    Intent intent = new Intent(getContext(),SubmitRequestActivity.class);
                    intent.putExtra("WalletAmount",total_balance);
                    startActivity(intent);
                }else {

                  //sk  show_bottom_sheet();
                }

                break;

            case R.id.img_delete:
                text_cardno.setText(getResources().getString(R.string.select_card));
                text_cardno.setOnClickListener(this);
                img_delete.setVisibility(View.GONE);
                card_id = "";
//                linear_submit.setBackgroundColor(getResources().getColor(R.color.light_gray));
                break;

            case R.id.text_cardno:
                Intent i_card = new Intent(getActivity(), ChooseCard.class);
                startActivityForResult(i_card, RESULT_ADDMONEY);
                break;

            case R.id.text_cancel:
                if (bottomSheetDialog != null) {
                    if (bottomSheetDialog.isShowing()) {
                        bottomSheetDialog.dismiss();
                    }
                }
                break;

            case R.id.linear_submit:
                if (edit_amt.getText().toString().trim().equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getActivity().getResources().getString(R.string.enter_amt));
                } else if (card_id.equalsIgnoreCase("")) {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getActivity().getResources().getString(R.string.select_card));
                } else {
                    if (sessionManager.isLoggedIn()) {
                        if (CommonMethods.checkConnection()) {
                            AddMoneyWallet(card_id, edit_amt.getText().toString().trim());
                        } else {
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                        }
                    } else {
                        CommonMethods.ShowLoginDialog(getActivity());
                    }
                }

                break;

            case R.id.image_cashout:
                showDialog();
                break;

            case R.id.image_approval:
                showDialog();
                break;
        }
    }

    private void AddMoneyWallet(String card_id, String amt) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        calls = apiInterface.AddMoneytoWallet(card_id, amt);
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
                            if (bottomSheetDialog.isShowing()) {
                                bottomSheetDialog.dismiss();
                            }
                            success_Alert_Dialog(getActivity(), object.getString("success"));
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(getActivity(), obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.server_error));
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

    private void show_bottom_sheet() {
        View view = getLayoutInflater().inflate(R.layout.layout_add_money, null);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.setCanceledOnTouchOutside(false);
        text_cancel = (TextView) bottomSheetDialog.findViewById(R.id.text_cancel);
        img_delete = (ImageView) bottomSheetDialog.findViewById(R.id.img_delete);
        text_cardno = (TextView) bottomSheetDialog.findViewById(R.id.text_cardno);
        linear_submit = (LinearLayout) bottomSheetDialog.findViewById(R.id.linear_submit);
        edit_amt = (EditText) bottomSheetDialog.findViewById(R.id.edit_amt);
        text_cardno.setOnClickListener(this);
        img_delete.setOnClickListener(this);
        text_cancel.setOnClickListener(this);
        linear_submit.setOnClickListener(this);
        bottomSheetDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_ADDMONEY) {
            if (data != null && data.hasExtra("cardid")) {
                card_id = data.getStringExtra("cardid");
                text_cardno.setText(data.getStringExtra("maskedno"));
                text_cardno.setOnClickListener(null);
                img_delete.setVisibility(View.VISIBLE);
//                linear_submit.setBackgroundColor(getResources().getColor(R.color.app_color_green));
            }
        }
    }

    private void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();
        alertDialog.setTitle("Success!");
        alertDialog.setMessage(message);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                if (alertDialog.isShowing()) {
                    alertDialog.dismiss();
                }
                if (CommonMethods.checkConnection()) {
                    GetWalletDetails();
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(getActivity(), getResources().getString(R.string.internetconnection));
                }
            }
        });
        alertDialog.show();
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.layout_wallet_info);

        TextView text_close = (TextView) dialog.findViewById(R.id.text_close);
        text_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void Error_Alert_Dialog(String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Mansa Musa!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();
                Intent intent = new Intent(getContext(), NationalIDActivity.class);
                intent.putExtra("national_pic","");
                startActivity(intent);
            }
        });

        // Showing Alert Message
        if(!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

}
