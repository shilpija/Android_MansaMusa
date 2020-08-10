package com.freshhome.AdapterClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.freshhome.R;
import com.freshhome.datamodel.subAttributes;

import java.util.ArrayList;

public class ProductDetailColorselectionAdatpter extends RecyclerView.Adapter<ProductDetailColorselectionAdatpter.ProductDetailsViewHolder> {
    String attribute_type;
    private Context context;
    String currency;
    private ArrayList<subAttributes> proList;
    private OnColorClickListener onColorClickListener;

    public ProductDetailColorselectionAdatpter(Context context, ArrayList<subAttributes> proList, OnColorClickListener onColorClickListener,String currency, String attribute_type) {
        this.context = context;
        this.proList = proList;
        this.currency = currency;
        this.onColorClickListener = onColorClickListener;
        this.attribute_type = attribute_type;
    }

    @NonNull
    @Override
    public ProductDetailsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_radio_details_layout, viewGroup, false);
        return new ProductDetailsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductDetailsViewHolder holder, int position) {

        if (attribute_type.equalsIgnoreCase("checkbox")) {
            holder.checkbox.setText(proList.get(position).getSubAttr_name()+ "(" +
                    proList.get(position).getSubAttr_price_prefix() + currency + " " + proList.get(position).getSubAttr_price() + ")");
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.radiobtn.setVisibility(View.GONE);

//            if (proList.get(position).isSelected()) {
//                holder.checkbox.setChecked(true);
//            } else {
//                holder.checkbox.setChecked(false);
//            }


        } else {
            holder.checkbox.setVisibility(View.GONE);
            holder.radiobtn.setVisibility(View.VISIBLE);
            holder.radiobtn.setText(proList.get(position).getSubAttr_name()+ "(" +
                    proList.get(position).getSubAttr_price_prefix() + currency + " " + proList.get(position).getSubAttr_price() + ")");
            if (proList.get(position).isSelected()) {
                holder.radiobtn.setChecked(true);
            } else {
                holder.radiobtn.setChecked(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return proList.size();
    }

    public interface OnColorClickListener {
        void onColorClick(int position, String name, String attribute_type,boolean ischeck);
    }

    public class ProductDetailsViewHolder extends RecyclerView.ViewHolder {
        RadioButton radiobtn;
        CheckBox checkbox;

        public ProductDetailsViewHolder(@NonNull View itemView) {
            super(itemView);
            radiobtn = (RadioButton) itemView.findViewById(R.id.radiobtn);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            radiobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onColorClickListener.onColorClick(getAdapterPosition(), proList.get(getAdapterPosition()).getSubAttr_name(), attribute_type,checkbox.isChecked());
                }
            });
            checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        onColorClickListener.onColorClick(getAdapterPosition(), proList.get(getAdapterPosition()).getSubAttr_name(), attribute_type,checkbox.isChecked());
                }
            });


        }
    }
}
