package com.freshhome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AddProdcutVarients_Activity extends AppCompatActivity implements View.OnClickListener {
    ImageView image_back;
    LinearLayout linear_contiune;
    LinearLayout linear_varients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prodcut_varients);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        linear_contiune = (LinearLayout) findViewById(R.id.linear_contiune);
        linear_contiune.setOnClickListener(this);

        linear_varients = (LinearLayout) findViewById(R.id.linear_varients);

        for (int i = 0; i < 2; i++) {
            View view = getLayoutInflater().inflate(R.layout.single_row_varients, null);
            Spinner spinner_var = (Spinner) view.findViewById(R.id.spinner_var);
            String[] arr = getResources().getStringArray(R.array.gender_array);
            spinner_var.setAdapter(new ArrayAdapter<>(AddProdcutVarients_Activity.this, R.layout.layout_spinner_text_bg_transparent, arr));
            linear_varients.addView(view);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                AddProdcutVarients_Activity.this.finish();
                break;

            case R.id.linear_contiune:
                Intent i = new Intent(AddProdcutVarients_Activity.this, AddProductDetailedActivity.class);
                startActivity(i);
                break;
        }
    }
}
