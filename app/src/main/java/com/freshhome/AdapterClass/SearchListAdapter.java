package com.freshhome.AdapterClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.freshhome.R;
import com.freshhome.datamodel.SearchModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SerahcViewHolder> {

    private Context context;
    private ArrayList<SearchModel> eventList;
    private OnItemListener onItemListener;

    public SearchListAdapter(Context context, ArrayList<SearchModel> eventList, OnItemListener onItemListener) {
        this.context = context;
        this.eventList = eventList;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public SerahcViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_search_list_show_layout,viewGroup,false);
        return new SerahcViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SerahcViewHolder holder, int position) {

        holder.tvSearchItem.setText(eventList.get(position).getValue());
    }

    @Override
    public int getItemCount() {
        return eventList != null ? eventList.size() : 0;
    }

    public class SerahcViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvSearchItem)
        TextView tvSearchItem;

        public SerahcViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            tvSearchItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemListener.onItemClick(eventList.get(getAdapterPosition()).getValue(),eventList.get(getAdapterPosition()).getType());
                }
            });
        }
    }

    public void update(ArrayList<SearchModel> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    public interface OnItemListener{
        void onItemClick(String value,String type);
    }
}
