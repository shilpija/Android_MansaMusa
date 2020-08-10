package com.freshhome.AdapterClass;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.R;
import com.freshhome.datamodel.Wallet;

import java.util.ArrayList;


/**
 * Created by HP on 01-05-2018.
 */

public class MyWalletTransactionAdapter extends RecyclerView.Adapter<MyWalletTransactionAdapter.MyViewHolder> {
    UserSessionManager sessionManager;
    Context context;
    LayoutInflater inflater;
    ArrayList<Wallet> arrayWallet;

    public MyWalletTransactionAdapter(Context context, ArrayList<Wallet> arrayWallet) {
        this.context = context;
        this.arrayWallet = arrayWallet;
        sessionManager = new UserSessionManager(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_transactionhistory, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {

       Wallet wallet = arrayWallet.get(i);
       mViewHolder.text_order.setText(context.getResources().getString(R.string.trans_no) + " " + arrayWallet.get(i).getWallet_id());
        mViewHolder.tv_msg_amt.setText("" + arrayWallet.get(i).getWallet_message());

        if(sessionManager.getLoginType().equalsIgnoreCase("1")){

            if (arrayWallet.get(i).getAmount_status().equalsIgnoreCase("3")) {
              //sk  mViewHolder.text_amt.setTextColor(R.color.green);
                mViewHolder.text_amt.setTextColor(context.getResources().getColor(R.color.green));
                mViewHolder.text_amt.setText(arrayWallet.get(i).getWallet_amount());

            } else {
              //sk  mViewHolder.text_amt.setTextColor(R.color.black);
                mViewHolder.text_amt.setTextColor(context.getResources().getColor(R.color.black));
                mViewHolder.text_amt.setText(arrayWallet.get(i).getWallet_amount());
            }
            mViewHolder.text_time.setText(arrayWallet.get(i).getWallet_time());


            if (arrayWallet.get(i).getAmount_status().equalsIgnoreCase("3")) {
                //cash in
                mViewHolder.img_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.arrow_down_orange));
                mViewHolder.text_status.setText(context.getResources().getString(R.string.cash_out));
            } else {
                //cash out
                mViewHolder.text_status.setText(context.getResources().getString(R.string.cash_in));
                mViewHolder.img_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.arrow_up_green));
            }

        }else {
            if (arrayWallet.get(i).getAmount_status().equalsIgnoreCase("1")) {
             //sk   mViewHolder.text_amt.setTextColor(R.color.greytxt);
                mViewHolder.text_amt.setTextColor(context.getResources().getColor(R.color.greytxt));
                mViewHolder.text_amt.setText("" + arrayWallet.get(i).getWallet_amount());
                //mViewHolder.text_amt.setText(arrayWallet.get(i).getWallet_currency() + " " + arrayWallet.get(i).getWallet_amount());

            } else if (arrayWallet.get(i).getAmount_status().equalsIgnoreCase("3")) {
              //sk  mViewHolder.text_amt.setTextColor(R.color.green);
                mViewHolder.text_amt.setTextColor(context.getResources().getColor(R.color.green));
                mViewHolder.text_amt.setText("" + arrayWallet.get(i).getWallet_amount());

            } else if (arrayWallet.get(i).getAmount_status().equalsIgnoreCase("4")) {
             //sk   mViewHolder.text_amt.setTextColor(R.color.red);
                mViewHolder.text_amt.setTextColor(context.getResources().getColor(R.color.red));
                mViewHolder.text_amt.setText("" +"" + arrayWallet.get(i).getWallet_amount());

            } else {
               //sk mViewHolder.text_amt.setTextColor(R.color.black);
                mViewHolder.text_amt.setTextColor(context.getResources().getColor(R.color.black));
                mViewHolder.text_amt.setText("" + arrayWallet.get(i).getWallet_amount());
            }
            mViewHolder.text_time.setText(arrayWallet.get(i).getWallet_time());
            //if (arrayWallet.get(i).getWallet_amount().contains("-")) {
            if (arrayWallet.get(i).getAmount_status().equalsIgnoreCase("3")
                    || arrayWallet.get(i).getAmount_status().equalsIgnoreCase("4")) {
                //cash out
                mViewHolder.text_status.setText(context.getResources().getString(R.string.cash_out));
                mViewHolder.img_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.arrow_down_orange));
            } else {
                //cash in
                mViewHolder.img_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.arrow_up_green));
                mViewHolder.text_status.setText(context.getResources().getString(R.string.cash_in));
            }
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayWallet.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_order, text_amt, tv_msg_amt, text_time, text_status;
        ImageView img_arrow;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_order = (TextView) convertView.findViewById(R.id.text_order);
            tv_msg_amt = (TextView) convertView.findViewById(R.id.tv_msg_amt);
            text_amt = (TextView) convertView.findViewById(R.id.text_amt);
            text_time = (TextView) convertView.findViewById(R.id.text_time);
            text_status = (TextView) convertView.findViewById(R.id.text_status);
            img_arrow = (ImageView) convertView.findViewById(R.id.img_arrow);
        }
    }
}