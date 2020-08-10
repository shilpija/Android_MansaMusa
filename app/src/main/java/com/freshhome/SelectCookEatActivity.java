package com.freshhome;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.crashlytics.android.Crashlytics;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.DriverModule.DriverLoginActivity;
import com.freshhome.DriverModule.SelectDriverTypeActivity;

import io.fabric.sdk.android.Fabric;

public class SelectCookEatActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_buyer, linear_supplier, linear_supplychain, linear_business;
//    TextView text_arabic, text_english;
    UserSessionManager sessionManager;
//    ImageView image_cook, image_eat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_select_cook_eat);
        sessionManager = new UserSessionManager(SelectCookEatActivity.this);
//        text_arabic = (TextView) findViewById(R.id.text_arabic);
//        text_arabic.setOnClickListener(this);
//        text_english = (TextView) findViewById(R.id.text_english);
//        text_english.setOnClickListener(this);
        linear_buyer = (LinearLayout) findViewById(R.id.linear_buyer);
        linear_buyer.setOnClickListener(this);
        linear_supplier = (LinearLayout) findViewById(R.id.linear_supplier);
        linear_supplier.setOnClickListener(this);
        linear_supplychain = (LinearLayout) findViewById(R.id.linear_supplychain);
        linear_supplychain.setOnClickListener(this);

        linear_business = (LinearLayout) findViewById(R.id.linear_business);
        linear_business.setOnClickListener(this);
//
//        image_cook = (ImageView) findViewById(R.id.image_cook);
//        image_eat = (ImageView) findViewById(R.id.image_eat);
//
//        Glide.with(SelectCookEatActivity.this).load(R.raw.cooking_g).into(image_cook);
//        Glide.with(SelectCookEatActivity.this).load(R.raw.eating_g).into(image_eat);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_buyer:
                moveTonext(ConstantValues.ToEat);
                break;

            case R.id.linear_supplier:
                moveTonext(ConstantValues.ToCook);
                break;
//
//            case R.id.text_arabic:
////                forceCrash(text_arabic);
//                break;

//            case R.id.text_english:
//                Intent i_sign = new Intent(SelectCookEatActivity.this, SignUpActivity.class);
//                i_sign.putExtra("fromLogin",true);
//                startActivity(i_sign);
//                break;

            case R.id.linear_supplychain:
                Intent i = new Intent(SelectCookEatActivity.this, SelectDriverTypeActivity.class);
                startActivity(i);
                SelectCookEatActivity.this.finish();
                break;

            case R.id.linear_business:
                moveTonext(ConstantValues.Sales);
                break;
        }
    }

    private void moveTonext(String value) {
        sessionManager.saveLoginType(value);
        Intent i = new Intent(SelectCookEatActivity.this, LoginActivity.class);
        i.putExtra("cook_eat", value);
        startActivity(i);
    }

    public void forceCrash(View view) {
        throw new RuntimeException("This is a crash");

    }

}
