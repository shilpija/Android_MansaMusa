package com.freshhome;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommonWebViewActivity extends AppCompatActivity {
    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.webviewBlog)
    WebView webView;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_web_view);
        ButterKnife.bind(this);

        if (getIntent() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.getString("Page").equalsIgnoreCase("tv_privacyPolicy"))
            {
                url ="https://www.mansamusa.ae/pagesapi/conditions-of-use-sale";
                tv_title.setText("Privacy Policy");
            }
            else if (bundle.getString("Page").equalsIgnoreCase("tv_terms_condition"))
            {
                url="https://www.mansamusa.ae/pagesapi/terms-conditions";
                tv_title.setText("Terms Condition");
            }
        }
        init();

    }




    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);

        final ProgressDialog progressDialog = new ProgressDialog(CommonWebViewActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                view.loadUrl(url);
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });
        // webView.loadUrl("http://mobuloustech.com/camel_api/blog");
        webView.loadUrl(url);
    }

    @OnClick(R.id.iv_back)
    void onclick(View view)
    {
        switch (view.getId())
        {
            case R.id.iv_back:
                onBackPressed();
                break;
        }
    }
}
