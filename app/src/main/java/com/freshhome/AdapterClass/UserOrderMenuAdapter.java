package com.freshhome.AdapterClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.HomeItemDetail;
import com.freshhome.MenuItemFeedbackActivity;
import com.freshhome.ProductDetail;
import com.freshhome.R;
import com.freshhome.SelectMenuActivity;
import com.freshhome.UserOrderFeedbackActivity;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.MenuSupplier;
import com.freshhome.fragments.HomeFragment;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by HP on 01-05-2018.
 */

public class UserOrderMenuAdapter extends RecyclerView.Adapter<UserOrderMenuAdapter.MyViewHolder> {
    Context context;
    ArrayList<Cart> arrayMenuItem;
    LayoutInflater inflater;
    boolean isPending;
    String order_id;
    boolean isFromDriver;

    public UserOrderMenuAdapter(Context context, ArrayList<Cart> arrayMenuItem, boolean isPending, String order_id, boolean isFromDriver) {
        this.context = context;
        this.arrayMenuItem = arrayMenuItem;
        this.isPending = isPending;
        this.order_id = order_id;
        this.isFromDriver = isFromDriver;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_order_menu, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {


        mViewHolder.text_dishname.setText(arrayMenuItem.get(i).getDish_name());
        mViewHolder.text_price.setText(arrayMenuItem.get(i).getTotal_price());
        mViewHolder.text_qty.setText(arrayMenuItem.get(i).getDish_qty());

        if (!arrayMenuItem.get(i).getDish_image().equalsIgnoreCase("")) {
            Picasso.get().load(arrayMenuItem.get(i).getDish_image()).into(mViewHolder.circle_image);
        }


        if (isPending || isFromDriver) {
            mViewHolder.linear_feedback.setVisibility(View.GONE);
        } else {
            mViewHolder.linear_feedback.setVisibility(View.VISIBLE);
            if (arrayMenuItem.get(i).getDish_rating().equalsIgnoreCase(null) || arrayMenuItem.get(i).getDish_rating().equalsIgnoreCase("null") || arrayMenuItem.get(i).getDish_rating().equalsIgnoreCase("")) {
                mViewHolder.text_feedback.setText(context.getResources().getString(R.string.feedback_i));
                mViewHolder.linear_feedback.setVisibility(View.VISIBLE);
                mViewHolder.img_edit_star.setImageDrawable(context.getResources().getDrawable(R.drawable.edit_g));
                mViewHolder.linear_feedback.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, MenuItemFeedbackActivity.class);
                        i.putExtra("order_id", order_id);
                        i.putExtra("dish_id", arrayMenuItem.get((Integer) mViewHolder.linear_feedback.getTag()).getDish_id());
                        i.putExtra("dish_name", arrayMenuItem.get((Integer) mViewHolder.linear_feedback.getTag()).getDish_name());
                        i.putExtra("dish_image", arrayMenuItem.get((Integer) mViewHolder.linear_feedback.getTag()).getDish_image());
                        context.startActivity(i);
                    }
                });
            } else {
                mViewHolder.text_feedback.setText(arrayMenuItem.get(i).getDish_rating());
                mViewHolder.img_edit_star.setImageDrawable(context.getResources().getDrawable(R.drawable.star));
                mViewHolder.linear_feedback.setOnClickListener(null);
            }

            mViewHolder.linear_feedback.setTag(i);
        }
        mViewHolder.text_dishname.setTag(arrayMenuItem.get(i).getProduct_type());
        mViewHolder.linear_main.setTag(arrayMenuItem.get(i).getDish_id());
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mViewHolder.text_dishname.getTag().toString().equalsIgnoreCase("1")) {
                    Intent i = new Intent(context, HomeItemDetail.class);
                    i.putExtra("item_id", mViewHolder.linear_main.getTag().toString());
                    i.putExtra("cart_count", "");
                    i.putExtra("cart_price", "");
                    context.startActivity(i);
                }else{
                    Intent i = new Intent(context, ProductDetail.class);
                    i.putExtra("item_id", mViewHolder.linear_main.getTag().toString());
                    i.putExtra("item_type", mViewHolder.text_dishname.getTag().toString());
                    context.startActivity(i);
                }
            }
        });


    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayMenuItem.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_dishname, text_price, text_qty, text_feedback;
        LinearLayout linear_feedback, linear_main;
        RatingBar ratingBar;
        ImageView img_edit_star;
        CheckBox checkbox;
        CircleImageView circle_image;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_dishname = (TextView) convertView.findViewById(R.id.text_dishname);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            text_qty = (TextView) convertView.findViewById(R.id.text_qty);
            circle_image = (CircleImageView) convertView.findViewById(R.id.circle_image);
            linear_feedback = (LinearLayout) convertView.findViewById(R.id.linear_feedback);
            img_edit_star = (ImageView) convertView.findViewById(R.id.img_edit_star);
            text_feedback = (TextView) convertView.findViewById(R.id.text_feedback);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }


//    private void add_qty_dialog(final TextView text_qty, final CheckBox checkbox, final int i) {
//        final Dialog dialog = new Dialog(context);
//        dialog.setContentView(R.layout.layout_add_qty_dialog);
//        dialog.setCancelable(false);
//        dialog.setCanceledOnTouchOutside(false);
//        TextView text_save = (TextView) dialog.findViewById(R.id.text_save);
//        final EditText edit_text = (EditText) dialog.findViewById(R.id.edit_text);
//
//        text_save.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!edit_text.getText().toString().equalsIgnoreCase("")) {
//                    if (arrayMenu.get(i).getId().equalsIgnoreCase(text_qty.getTag().toString())) {
//                        arrayMenu.get(i).setDqtyl(edit_text.getText().toString());
//                        arrayMenu.get(i).setIschecked(true);
//                        text_qty.setText(edit_text.getText().toString().trim());
//                        checkbox.setChecked(true);
//                        SelectMenuActivity.activeNext(arrayMenu);
//                        dialog.dismiss();
//
//                    }
//                } else {
//                    checkbox.setChecked(false);
//                    CommonMethods.showtoast(context, context.getResources().getString(R.string.enter_qty));
//                }
//            }
//        });
//
//        dialog.show();
//        Window window = dialog.getWindow();
//        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//    }

}