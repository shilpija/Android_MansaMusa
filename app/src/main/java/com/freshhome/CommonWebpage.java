package com.freshhome;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommonWebpage extends AppCompatActivity {

//   @BindView(R.id.tv_title)
//    TextView tv_title;

    @BindView(R.id.webviewBlog)
    WebView webView;


    String url="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_page);
        ButterKnife.bind(this);

        if (getIntent() != null) {

             url = (String)getIntent().getStringExtra("invoice");

            //Bundle bundle = getIntent().getExtras();

            //if (bundle.getString("Page").equalsIgnoreCase("tv_aboutUs"))
            //{
                //url="http://18.189.223.53/jokar/about.html";
                //tv_title.setText("Invoice");
            //}
//            else if (bundle.getString("Page").equalsIgnoreCase("tv_privacyPolicy"))
//            {
//               url="http://18.189.223.53/jokar/privacy.html";
//                tv_title.setText("Privacy Policy");
//            }
//            else if (bundle.getString("Page").equalsIgnoreCase("tv_terms_condition"))
//            {
//               url ="http://18.189.223.53/jokar/terms.html";
//                tv_title.setText("Terms Condition");
//            }
        }
        init();
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void init() {

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);

        //MyDialog.getInstance(this).showDialog(CommonWebpage.this);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

               // MyDialog.getInstance(CommonWebpage.this).hideDialog();
                view.loadUrl(url);
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                //MyDialog.getInstance(CommonWebpage.this).hideDialog();

            }

            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

                //MyDialog.getInstance(CommonWebpage.this).hideDialog();
            }
        });
        // webView.loadUrl("http://mobuloustech.com/camel_api/blog");
        webView.loadUrl(url);
    }

    @OnClick(R.id.image_back)
    void onclick(View view)
    {
     switch (view.getId())
     {
         case R.id.image_back:
             onBackPressed();
             break;
     }
    }
}
