package com.freshhome.AdapterClass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.AddCardDetailActivity;
import com.freshhome.R;
import com.freshhome.UserPaymentMethodFragment;
import com.freshhome.datamodel.myCards;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by HP on 01-05-2018.
 */

public class UserPaymentoptionAdapter extends RecyclerView.Adapter<UserPaymentoptionAdapter.MyViewHolder> {
    Context context;
    LayoutInflater inflater;
    ArrayList<myCards> arrayListcards;

    public UserPaymentoptionAdapter(Context context, ArrayList<myCards> arrayListcards) {
        this.context = context;
        this.arrayListcards = arrayListcards;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_paymentcard, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        arrayListcards.get(i).setSwitchCompat(mViewHolder.switch_active);
        mViewHolder.text_name.setText(arrayListcards.get(i).getName());
        mViewHolder.text_cardno.setText(arrayListcards.get(i).getMasked_num());
        mViewHolder.text_expiry.setText(arrayListcards.get(i).getExp_date());
        Picasso.get().load(arrayListcards.get(i).getCard_image()).into(mViewHolder.imageview);
        mViewHolder.switch_active.setChecked(arrayListcards.get(i).isSelected());
        mViewHolder.text_edit.setTag(i);
        mViewHolder.text_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (int) mViewHolder.text_edit.getTag();
                Intent i = new Intent(context, AddCardDetailActivity.class);
                i.putExtra("card_id", arrayListcards.get(position).getCard_id());
                i.putExtra("card_img", arrayListcards.get(position).getCard_image());
                i.putExtra("card_type", arrayListcards.get(position).getCard_type());
                i.putExtra("exp_date", arrayListcards.get(position).getExp_date());
                i.putExtra("expire", arrayListcards.get(position).getExpired());
                i.putExtra("masked_num", arrayListcards.get(position).getMasked_num());
                i.putExtra("name", arrayListcards.get(position).getName());
                i.putExtra("type", arrayListcards.get(position).getType());
                context.startActivity(i);
            }
        });

        mViewHolder.switch_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                for (int j = 0; j < arrayListcards.size(); j++) {
                    if (arrayListcards.get(j).getSwitchCompat().equals(buttonView)) {
                        arrayListcards.get(j).setSelected(isChecked);
                        arrayListcards.get(j).getSwitchCompat().setChecked(isChecked);
                    } else {
                        arrayListcards.get(j).setSelected(false);
                        arrayListcards.get(j).getSwitchCompat().setChecked(false);
                    }

                    if (isChecked) {
                        UserPaymentMethodFragment.selectedcardid(arrayListcards.get(j).getCard_id(), arrayListcards.get(j).getMasked_num());
                    } else {
                        UserPaymentMethodFragment.selectedcardid("", "");
                    }
                }
//                notifyDataSetChanged();
            }
        });
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayListcards.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_name, text_cardno, text_expiry, text_edit;
        SwitchCompat switch_active;
        ImageView imageview;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_name = (TextView) convertView.findViewById(R.id.text_name);
            text_cardno = (TextView) convertView.findViewById(R.id.text_cardno);
            text_expiry = (TextView) convertView.findViewById(R.id.text_expiry);
            switch_active = (SwitchCompat) convertView.findViewById(R.id.switch_active);
            imageview = (ImageView) convertView.findViewById(R.id.imageview);
            text_edit = (TextView) convertView.findViewById(R.id.text_edit);
        }
    }
}