package com.freshhome.AdapterClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.R;
import com.freshhome.datamodel.Feedback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by HP on 01-05-2018.
 */

public class FeedbackAdapter extends BaseAdapter {
    Context context;
    ArrayList<Feedback> arrayFeedback;
    LayoutInflater inflater;
    boolean isSupplierDetail;

    public FeedbackAdapter(Context context, ArrayList<Feedback> arrayFeedback, boolean isSupplierDetail) {
        this.context = context;
        this.arrayFeedback = arrayFeedback;
        inflater = LayoutInflater.from(this.context);
        this.isSupplierDetail = isSupplierDetail;
    }

    @Override
    public int getCount() {
        return arrayFeedback.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.single_row_feedback, viewGroup, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }

        mViewHolder.text_time.setText(arrayFeedback.get(i).getTime());
        mViewHolder.text_comment.setText(arrayFeedback.get(i).getComment());
        mViewHolder.text_user_name.setText(arrayFeedback.get(i).getUser_name());
        if (!arrayFeedback.get(i).getUser_image().equalsIgnoreCase("")) {
            Picasso.get().load(arrayFeedback.get(i).getUser_image()).into(mViewHolder.image_user);
        }

//        if (isSupplierDetail) {
//            mViewHolder.linear_all_ratings.setVisibility(View.VISIBLE);
//        } else {
//            mViewHolder.linear_all_ratings.setVisibility(View.GONE);
//        }

        return convertView;
    }

    private class MyViewHolder {
        TextView text_user_name, text_comment, text_time;
        LinearLayout linear_all_ratings;
        //        RatingBar ratingBar_dish;//, ratingtaste, ratingBar_presentation, ratingBar_packing, ratingBar_overall;
        CircleImageView image_user;

        public MyViewHolder(View convertView) {
            text_user_name = (TextView) convertView.findViewById(R.id.text_user_name);
            text_comment = (TextView) convertView.findViewById(R.id.text_comment);
            text_time = (TextView) convertView.findViewById(R.id.text_time);
//            text_rate_presentation = (TextView) convertView.findViewById(R.id.text_rate_presentation);
//            ratingBar_dish = (RatingBar) convertView.findViewById(R.id.ratingBar_dish);
//            ratingtaste = (RatingBar) convertView.findViewById(R.id.ratingtaste);
//            ratingBar_presentation = (RatingBar) convertView.findViewById(R.id.ratingBar_presentation);
//            ratingBar_packing = (RatingBar) convertView.findViewById(R.id.ratingBar_packing);
//            ratingBar_overall = (RatingBar) convertView.findViewById(R.id.ratingBar_overall);
            image_user = (CircleImageView) convertView.findViewById(R.id.image_user);
//            linear_all_ratings = (LinearLayout) convertView.findViewById(R.id.linear_all_ratings);
//
        }
    }
}