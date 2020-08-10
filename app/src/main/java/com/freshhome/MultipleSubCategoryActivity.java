package com.freshhome;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.AdapterClass.MultipleSubCategoryAdapter;
import com.freshhome.datamodel.NameID;

import java.util.ArrayList;

public class MultipleSubCategoryActivity extends AppCompatActivity implements View.OnClickListener {
    GridView sub_categoryGridview;
    ImageView image_back;
    TextView text_cancel;
    LinearLayout linear_done;
    ArrayList<NameID> arraySubCategories;
    int category_pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_sub_category);
        arraySubCategories = new ArrayList<>();

        sub_categoryGridview = (GridView) findViewById(R.id.sub_categoryGridview);
        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        linear_done = (LinearLayout) findViewById(R.id.linear_done);
        linear_done.setOnClickListener(this);

        text_cancel = (TextView) findViewById(R.id.text_cancel);
        text_cancel.setOnClickListener(this);

        arraySubCategories = getIntent().getParcelableArrayListExtra("sub_categories");
        category_pos = getIntent().getIntExtra("categories_pos",0);

        MultipleSubCategoryAdapter adapter = new MultipleSubCategoryAdapter(MultipleSubCategoryActivity.this, arraySubCategories);
        sub_categoryGridview.setAdapter(adapter);

        sub_categoryGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                if (!cb.isChecked()) {
                    cb.setChecked(true);
                    arraySubCategories.get(position).setIsselected(true);
                } else {
                    cb.setChecked(false);
                    arraySubCategories.get(position).setIsselected(false);
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                MultipleSubCategoryActivity.this.finish();
                break;

            case R.id.linear_done:
                Intent resultIntent = new Intent();
                resultIntent.putExtra("sub_categories", arraySubCategories);
                resultIntent.putExtra("categories_pos", category_pos);
                Log.e("id_done", String.valueOf(category_pos));
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                break;

            case R.id.text_cancel:
                MultipleSubCategoryActivity.this.finish();
                break;
        }
    }
}
