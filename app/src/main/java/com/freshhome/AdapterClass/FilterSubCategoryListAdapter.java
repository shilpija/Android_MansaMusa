package com.freshhome.AdapterClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.freshhome.R;
import com.freshhome.datamodel.FilterSubCategoryResponse;
import com.freshhome.datamodel.SearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterSubCategoryListAdapter extends RecyclerView.Adapter<FilterSubCategoryListAdapter.SubCategoryViewHolder> {

    private Context context;
    private JSONArray eventList;
    private OnItemListener onItemListener;
    private int parentPosition;

    public FilterSubCategoryListAdapter(Context context, JSONArray eventList, OnItemListener onItemListener,int parentPosition) {
        this.context = context;
        this.eventList = eventList;
        this.onItemListener = onItemListener;
        this.parentPosition = parentPosition;
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
        void onSubItemClick(JSONArray findJson,int position,int parentPosition);
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

                    onItemListener.onSubItemClick(eventList,getAdapterPosition(),parentPosition);

                }
            });
        }
    }
}
