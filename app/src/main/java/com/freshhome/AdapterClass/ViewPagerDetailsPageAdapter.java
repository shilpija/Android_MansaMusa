package com.freshhome.AdapterClass;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ViewPagerDetailsPageAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    private Integer[] images = {R.drawable.add_image, R.drawable.add_image, R.drawable.add_white};
    ArrayList<String> arrayImages;

    public ViewPagerDetailsPageAdapter(Context context, ArrayList<String> arrayImages) {
        this.context = context;
        this.arrayImages = arrayImages;
    }

    @Override
    public int getCount() {
        return arrayImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slidingimages_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
//        imageView.setImageResource(images[position]);
        Picasso.get().load(arrayImages.get(position)).placeholder(R.drawable.defualt_list).into(imageView);

        ViewPager vp = (ViewPager) container;
        vp.addView(view, 0);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ViewPager vp = (ViewPager) container;
        View view = (View) object;
        vp.removeView(view);

    }
}
