package com.freshhome;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.UserSessionManager;
import com.squareup.picasso.Picasso;

public class ActivityQRCode extends AppCompatActivity implements View.OnClickListener {
    ImageView image_qrcode;
    UserSessionManager sessionManager;
    LinearLayout linear_share, linear_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        image_qrcode = (ImageView) findViewById(R.id.image_qrcode);
        sessionManager = new UserSessionManager(ActivityQRCode.this);

        //load qr image in imageview
        if(!sessionManager.getUserDetails().get(UserSessionManager.QRCODEURL).equalsIgnoreCase("")) {
            Picasso.get().load(sessionManager.getUserDetails().get(UserSessionManager.QRCODEURL)).into(image_qrcode);
        }

        linear_share = (LinearLayout) findViewById(R.id.linear_share);
        linear_share.setOnClickListener(this);

        linear_back = (LinearLayout) findViewById(R.id.linear_back);
        linear_back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_share:
                CommonMethods.share_image_with_other(ActivityQRCode.this, "", image_qrcode);
                break;


            case R.id.linear_back:
                ActivityQRCode.this.finish();
                break;
        }
    }


}
