package com.freshhome;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

public class ChooseBannerImage extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back, imageview;
    LinearLayout linear_choose;
    String banner_id = "",image_url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_banner_image);
        imageview = (ImageView) findViewById(R.id.imageview);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);
        image_url=getIntent().getStringExtra("image_url");
        Picasso.get().load(getIntent().getStringExtra("image_url")).into(imageview);
        banner_id = getIntent().getStringExtra("banner_id");
        linear_choose = (LinearLayout) findViewById(R.id.linear_choose);
        linear_choose.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                ChooseBannerImage.this.finish();
                startActivity(new Intent(ChooseBannerImage.this, HeaderImageListActivity.class));
                break;

            case R.id.linear_choose:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("banner_id", banner_id);
                resultIntent.putExtra("image_url", image_url);
                setResult(Activity.RESULT_OK, resultIntent);
                ChooseBannerImage.this.finish();
                break;
        }
    }
}
