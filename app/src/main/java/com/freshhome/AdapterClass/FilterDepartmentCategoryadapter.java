package com.freshhome.AdapterClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.freshhome.R;
import com.freshhome.datamodel.FilterSubCategoryResponse;
import com.freshhome.datamodel.SearchModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterDepartmentCategoryadapter extends RecyclerView.Adapter<FilterDepartmentCategoryadapter.SubCategoryViewHolder> {

    private Context context;
    private JSONArray eventList;
    private ArrayList<FilterSubCategoryResponse> getData;
    private FilterSubCategoryListAdapter.OnItemListener onItemClick;


    public FilterDepartmentCategoryadapter(Context context, JSONArray eventList,FilterSubCategoryListAdapter.OnItemListener onItemClick) {
        this.context = context;
        this.eventList = eventList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public SubCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_filter_department_items_layout, viewGroup, false);
        return new SubCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubCategoryViewHolder holder, int position) {

        try {
            JSONObject obj = eventList.getJSONObject(position);
            holder.tvCategoryName.setText(obj.getString("homecategory"));

            JSONArray subCat_array = obj.getJSONArray("catlist");
            holder.rvCategoryName.setLayoutManager(new LinearLayoutManager(context));
            FilterSubCategoryListAdapter subCategoryAdapter = new FilterSubCategoryListAdapter(context,subCat_array,onItemClick,position);
            holder.rvCategoryName.setAdapter(subCategoryAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.length() : 0;
    }

    public interface OnItemListener {
        void onItemClick(String value);
    }

    public class SubCategoryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCategoryName)
        TextView tvCategoryName;

        @BindView(R.id.rvCategoryName)
        RecyclerView rvCategoryName;

        public SubCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            tvCategoryName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(rvCategoryName.getVisibility()==View.GONE)
                        rvCategoryName.setVisibility(View.VISIBLE);
                    else
                        rvCategoryName.setVisibility(View.GONE);
                }
            });
        }
    }
}
