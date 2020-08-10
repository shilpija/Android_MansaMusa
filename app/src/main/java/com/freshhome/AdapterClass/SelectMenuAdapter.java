package com.freshhome.AdapterClass;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.R;
import com.freshhome.ScheduleDayActivity;
import com.freshhome.SelectMenuActivity;
import com.freshhome.datamodel.MenuSupplier;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by HP on 01-05-2018.
 */

public class SelectMenuAdapter extends RecyclerView.Adapter<SelectMenuAdapter.MyViewHolder> {
    Context context;
    ArrayList<MenuSupplier> arrayMenu;
    LayoutInflater inflater;
    boolean isSelected;

    public SelectMenuAdapter(Context context, ArrayList<MenuSupplier> arrayMenu, boolean isSelected) {
        this.context = context;
        this.arrayMenu = arrayMenu;
        this.isSelected = isSelected;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_selectmenu, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_dishname.setText(arrayMenu.get(i).getDname());
        mViewHolder.text_price.setText(ConstantValues.currency + " " + arrayMenu.get(i).getDprice());
        mViewHolder.text_qty.setText(arrayMenu.get(i).getDqtyl());
        mViewHolder.text_qty.setTag(arrayMenu.get(i).getId());
        if (isSelected) {
            mViewHolder.linear_price.setVisibility(View.GONE);
            mViewHolder.checkbox.setVisibility(View.GONE);
            mViewHolder.image_remove.setVisibility(View.VISIBLE);
            mViewHolder.image_remove.setTag(arrayMenu.get(i).getId());
        } else {
            mViewHolder.linear_price.setVisibility(View.VISIBLE);
            mViewHolder.checkbox.setVisibility(View.VISIBLE);
            mViewHolder.image_remove.setVisibility(View.GONE);
        }
        mViewHolder.checkbox.setChecked(arrayMenu.get(i).isIschecked());
        mViewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        add_qty_dialog(mViewHolder.text_qty, mViewHolder.checkbox, i);
                        SelectMenuActivity.activeNext(arrayMenu);
                    } else {
                        arrayMenu.get(i).setDqtyl("0");
                        arrayMenu.get(i).setIschecked(false);
                        mViewHolder.text_qty.setText("0");
                        SelectMenuActivity.activeNext(arrayMenu);
                    }

                }
            }
        });
        mViewHolder.image_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (arrayMenu.get(i).getId().equalsIgnoreCase(mViewHolder.image_remove.getTag().toString())) {
                    arrayMenu.remove(i);
                    if (arrayMenu.size() > 0) {
                        ScheduleDayActivity.linear_upload_schedule.setVisibility(View.VISIBLE);
                        ScheduleDayActivity.text_selectmenu.setText("Edit Menu");
                    } else {
                        ScheduleDayActivity.linear_upload_schedule.setVisibility(View.GONE);
                        ScheduleDayActivity.text_selectmenu.setText("Select Menu");
                    }
                }
                notifyDataSetChanged();
            }
        });

        if (arrayMenu.get(i).getImageurl().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.demo).into(mViewHolder.circle_image);
        } else {
            Picasso.get().load(arrayMenu.get(i).getImageurl()).into(mViewHolder.circle_image);
        }

        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSelected) {
                    add_qty_dialog(mViewHolder.text_qty, mViewHolder.checkbox, i);
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
        return arrayMenu.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_dishname, text_price, text_status;
        LinearLayout linear_status, linear_main, linear_price;
        RatingBar ratingBar;
        ImageView img_edit_qty, image_remove;
        CheckBox checkbox;
        TextView text_qty;
        CircleImageView circle_image;

        public MyViewHolder(View convertView) {
            super(convertView);
            text_dishname = (TextView) convertView.findViewById(R.id.text_dishname);
            text_price = (TextView) convertView.findViewById(R.id.text_price);
            text_qty = (TextView) convertView.findViewById(R.id.text_qty);
            checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);
            img_edit_qty = (ImageView) convertView.findViewById(R.id.img_edit_qty);
            image_remove = (ImageView) convertView.findViewById(R.id.image_remove);
            circle_image = (CircleImageView) convertView.findViewById(R.id.circle_image);
            linear_price = (LinearLayout) convertView.findViewById(R.id.linear_price);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
        }
    }


    private void add_qty_dialog(final TextView text_qty, final CheckBox checkbox, final int i) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.layout_add_qty_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_save = (TextView) dialog.findViewById(R.id.text_save);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        final EditText edit_text = (EditText) dialog.findViewById(R.id.edit_text);
        if (!text_qty.getText().toString().equalsIgnoreCase("0")) {
            edit_text.setText(text_qty.getText().toString());
        }
        text_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edit_text.getText().toString().equalsIgnoreCase("") && !edit_text.getText().toString().equalsIgnoreCase("0")) {
                    if (arrayMenu.get(i).getId().equalsIgnoreCase(text_qty.getTag().toString())) {
                        arrayMenu.get(i).setDqtyl(edit_text.getText().toString());
                        arrayMenu.get(i).setIschecked(true);
                        text_qty.setText(edit_text.getText().toString().trim());
                        checkbox.setChecked(true);
                        SelectMenuActivity.activeNext(arrayMenu);
                        dialog.dismiss();

                    }
                } else {
                    checkbox.setChecked(false);
                    CommonMethods.showtoast(context, context.getResources().getString(R.string.enter_qty));
                }
            }
        });

        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox.setChecked(false);
                dialog.dismiss();
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }

}