package com.freshhome.DriverModule;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.LoginActivity;
import com.freshhome.MobileNumberActivity;
import com.freshhome.R;
import com.freshhome.SelectCookEatActivity;

public class SelectDriverTypeActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linear_driver, linear_company_driver;
    UserSessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_driver_type);
        sessionManager = new UserSessionManager(SelectDriverTypeActivity.this);
        linear_driver = (LinearLayout) findViewById(R.id.linear_driver);
        linear_driver.setOnClickListener(this);
        linear_company_driver = (LinearLayout) findViewById(R.id.linear_company_driver);
        linear_company_driver.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_driver:
                sessionManager.saveLoginType(ConstantValues.Driver);
                sessionManager.saveDriverType(ConstantValues.driver_individual);
                Intent i = new Intent(SelectDriverTypeActivity.this, LoginActivity.class);
                startActivity(i);
                SelectDriverTypeActivity.this.finish();
                break;

            case R.id.linear_company_driver:
                sessionManager.saveLoginType(ConstantValues.Driver);
                sessionManager.saveDriverType(ConstantValues.driver_company);
                Intent i_company = new Intent(SelectDriverTypeActivity.this, DriverLoginActivity.class);
                startActivity(i_company);
                SelectDriverTypeActivity.this.finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i_company = new Intent(SelectDriverTypeActivity.this, SelectCookEatActivity.class);
        startActivity(i_company);
        SelectDriverTypeActivity.this.finish();
    }
}
