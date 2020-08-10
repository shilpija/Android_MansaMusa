package com.freshhome.AdapterClass;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.freshhome.OrderCheckout;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.squareup.picasso.Picasso;


import java.util.List;

public class ViewPagerAdapter extends PagerAdapter {

//    private ArrayList<Integer> IMAGES;
    private List<HomeItem> images;
    private LayoutInflater inflater;
    private Context context;


    public ViewPagerAdapter(Context context, List<HomeItem> images) {
        this.context = context;
        this.images = images;
        inflater = LayoutInflater.from(context);
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View imageLayout = inflater.inflate(R.layout.slidingimages_layout, view, false);

        assert imageLayout != null;
        final ImageView imageView = (ImageView) imageLayout
                .findViewById(R.id.imageView);


//        if (images!=null && !images.isEmpty()) {
//            Glide.with(context).load(images.get(position)).into(imageView);
//        }else{
//            Glide.with(context).load(R.drawable.user_man).into(imageView);
//        }


        if (images!=null && !images.isEmpty() && !images.equals("")){
            Glide.with(context)
                    .load(images.get(position))
                    .into(imageView);
            //Picasso.get().load(images.get(position).getImage()).placeholder(R.drawable.defualt_list).into(imageView);
        }else {
            Glide.with(context).load(R.drawable.degault_bg).into(imageView);
        }

        view.addView(imageLayout, 0);

        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }


}
