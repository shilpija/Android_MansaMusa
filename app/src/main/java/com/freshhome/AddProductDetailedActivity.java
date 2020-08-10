package com.freshhome;

import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddProductDetailedActivity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    LinearLayout linear_submit, linear_varients;
    CircleImageView circle_image;
    TextView text_pname, text_pprice, text_pdescription, text_category, text_sub_category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product_detailed);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        linear_varients = (LinearLayout) findViewById(R.id.linear_varients);
        linear_submit = (LinearLayout) findViewById(R.id.linear_submit);
        linear_submit.setOnClickListener(this);

        circle_image = (CircleImageView) findViewById(R.id.circle_image);
        circle_image.setImageBitmap(BitmapFactory.decodeFile(getIntent().getStringExtra("pimage")));
        text_pname= (TextView) findViewById(R.id.text_pname);
        text_pname.setText(getIntent().getStringExtra("pname"));
        text_pprice= (TextView) findViewById(R.id.text_pprice);
        text_pprice.setText(getIntent().getStringExtra("pprice"));
        text_pdescription= (TextView) findViewById(R.id.text_pdescription);
        text_pdescription.setText(getIntent().getStringExtra("pdesc"));
        text_category= (TextView) findViewById(R.id.text_category);
        text_category.setText(getIntent().getStringExtra("pcategory"));
        text_sub_category= (TextView) findViewById(R.id.text_sub_category);
        text_sub_category.setText(getIntent().getStringExtra("psubcategory"));



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                AddProductDetailedActivity.this.finish();
                break;

            case R.id.linear_submit:

                break;
        }
    }
}
