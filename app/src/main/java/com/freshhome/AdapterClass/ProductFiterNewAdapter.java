package com.freshhome.AdapterClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.freshhome.R;
import com.freshhome.datamodel.FilterSubCategoryResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductFiterNewAdapter extends RecyclerView.Adapter<ProductFiterNewAdapter.SubCategoryViewHolder> {

    private Context context;
    private JSONArray eventList;
    private OnItemListener onItemListener;

    public ProductFiterNewAdapter(Context context, JSONArray eventList, OnItemListener onItemListener) {
        this.context = context;
        this.eventList = eventList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_filter_category_layout, viewGroup, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {

        try {
            JSONObject obj = eventList.getJSONObject(position);
            holder.rbLatestProduct.setText(obj.getString("name"));
            if(obj.getBoolean("isChecked"))
                holder.rbLatestProduct.setChecked(true);
            else
                holder.rbLatestProduct.setChecked(false);

        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.length() : 0;
    }

    public interface OnItemListener {
        void onInnerItemClick(JSONArray findJson,int position);
    }

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.rbLatestProduct)
        RadioButton rbLatestProduct;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            rbLatestProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemListener.onInnerItemClick(eventList,getAdapterPosition());

                }
            });
        }
    }
}
