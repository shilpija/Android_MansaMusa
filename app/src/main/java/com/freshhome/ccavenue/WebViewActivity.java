package com.freshhome.ccavenue;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.MainActivity_NavDrawer;
import com.freshhome.OrderCheckout;
import com.freshhome.PlanActivity;
import com.freshhome.R;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freshhome.CommonUtil.CommonUtilFunctions.success_Alert_Dialog;

public class WebViewActivity extends AppCompatActivity {
    Intent mainIntent;
    String encVal;
    String vResponse;
    ApiInterface apiInterface;
    private String from ="";


    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_webview);
        apiInterface = ApiClient.getInstance().getClient();
        mainIntent = getIntent();
        if(getIntent() != null){
            from = (String) getIntent().getStringExtra("from");
        }
        Log.e("idid:", mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
        //get rsa key method
        get_RSA_key(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), mainIntent.getStringExtra(AvenuesParams.ORDER_ID));
    }

    private void parseHTML(String html) throws XmlPullParserException, IOException, JSONException {

        String tag = "";
        String responseJSON = "";

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new StringReader(html));
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_DOCUMENT) {
                System.out.println("Start document");
            } else if (eventType == XmlPullParser.START_TAG) {
                tag = xpp.getName();

                System.out.println("Start tag " + xpp.getName());
            } else if (eventType == XmlPullParser.END_TAG) {
                System.out.println("End tag " + xpp.getName());
            } else if (eventType == XmlPullParser.TEXT) {
                if (tag.equalsIgnoreCase("pre")) {
                    responseJSON = xpp.getText();
                    JSONObject object = new JSONObject(responseJSON);
                    JSONObject successObject = object.getJSONObject("success");
                    String code = object.getString("code");
                    String orderId = object.getString("data");

                    if (code.equalsIgnoreCase("200")) {
                        //subscribePLan();
                        Log.e("Ravi find Code api "," "+code);

                        if (from.equalsIgnoreCase("SubPlan")) {
                            Intent intent = new Intent(WebViewActivity.this, PlanActivity.class);
                            intent.putExtra("payments", "Payment");
                            intent.putExtra("orderId", orderId);
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(WebViewActivity.this, OrderCheckout.class);
                            intent.putExtra("payments", "Payment");
                            intent.putExtra("orderId", orderId);
                            intent.putExtra("from", "payment");
                            startActivity(intent);
                            finish();

                        }

                    } else {
                        Toast.makeText(this, "error1", Toast.LENGTH_SHORT).show();
                    }


                }
                System.out.println("Text " + xpp.getText());
            }
            eventType = xpp.next();
        }
    }

    public void get_RSA_key(final String ac, final String od) {
        LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");

        ApiInterface apiInterface = ApiClient.getInstance().getClientPay();
        Call<AvenueResponse> call = apiInterface.getRsaKey(od);
        call.enqueue(new Callback<AvenueResponse>() {
            @Override
            public void onResponse(Call<AvenueResponse> call, retrofit2.Response<AvenueResponse> response) {

                LoadingDialog.cancelLoading();
//
//                if (response.body() != null && !response.body().equals("")) {
//                    vResponse = response.body();     ///save retrived rsa key
//                    if (vResponse.contains("!ERROR!")) {
//                        show_alert(vResponse);
//                    } else {
//                        new RenderView().execute();   // Calling async task to get display content
//                    }
//                } else {
//                    show_alert("No response");
//                }
//

                try {

                    vResponse = response.body().getSuccess();
                    //  vResponse=vResponse.replaceAll("\n","");
                    new RenderView().execute();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<AvenueResponse> call, Throwable t) {

            }
        });


//
//
//    {

//{            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put(AvenuesParams.ACCESS_CODE, ac);
//                params.put(AvenuesParams.ORDER_ID, od);
//                return params;
//            }
//
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
//                30000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        RequestQueue requestQueue = Volley.newRequestQueue(this);
//        requestQueue.add(stringRequest);
//    }


    }

    public void show_alert(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(
                WebViewActivity.this).create();

        alertDialog.setTitle("Error!!!");
        if (msg.contains("\n"))
            msg = msg.replaceAll("\\\n", "");

        alertDialog.setMessage(msg);


        alertDialog.setButton(Dialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });


        alertDialog.show();
    }

    private class RenderView extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            if (!ServiceUtility.chkNull(vResponse).equals("") && ServiceUtility.chkNull(vResponse).toString().indexOf("ERROR") == -1) {
                StringBuffer vEncVal = new StringBuffer();
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.AMOUNT, mainIntent.getStringExtra(AvenuesParams.AMOUNT)));
                vEncVal.append(ServiceUtility.addToPostParams(AvenuesParams.CURRENCY, mainIntent.getStringExtra(AvenuesParams.CURRENCY)));


//                try {
//                    JSONObject object=new JSONObject(vResponse);
//                    vResponse=object.getString("success");
//
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }


                Log.d("rsa_key", vResponse);
                encVal = RSAUtility.encrypt(vEncVal.substring(0, vEncVal.length() - 1), vResponse);
                Log.e("encrypted", encVal);
                //encrypt amount and currency
                //  encVal = "Q3EV9udaRL5L%2B93%2B41F6SOlXKQyXXwIKmayhtnwtITSrYoQulLjxO7jPAoXrbzqaJvCA7yquiJT7KiKdoUHMxloM9uohLRIHOeM5z6MRvN2ToKdxYRi6jYQAFHgbEdWaFDV3BCIJRBuQqlxMVxIvoAD07GpBcM8MQ8%2F4bkU%2BH1flz%2B0gzzQL9OfHHPuL6meytpn5u6CISVJtB9ov%2BoxP4GiI7hz7v1XNrPci2K%2Fz07ZhJ%2FQgbFPumXKfrMmNIGWwldrMJbNKqP3yjb7xOrMYhF55dFr9rR5Pgy7qu1Lyjdwd7jAA7BB9USWJuI1ZvA8Te8IkhdUju23a%2FAWp22NfRA%3D%3D";  //encrypt amount and currency
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            LoadingDialog.cancelLoading();

            @SuppressWarnings("unused")
            class MyJavaScriptInterface {
                @JavascriptInterface
                public void processHTML(String html) {
                    Log.e("response", html);
                    try {
                        parseHTML(html);
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // here we will get the transaction response
                    // process the html source code to get final status of transaction
//                    String status = null;
//                    if (html.indexOf("Failure") != -1) {
//                        status = "Transaction Declined!";
//                    } else if (html.indexOf("Success") != -1) {
//                        status = "Transaction Successful!";
//                    } else if (html.indexOf("Aborted") != -1) {
//                        status = "Transaction Cancelled!";
//                    } else {
//                        status = "Status Not Known!";
//                    }
//
//                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();


//                    Intent intent = new Intent(getApplicationContext(), StatusActivity.class);
//                    intent.putExtra("transStatus", status);
//                    startActivity(intent);
                }
            }


            final WebView webview = findViewById(R.id.webview);
            webview.getSettings().setJavaScriptEnabled(true);
            webview.addJavascriptInterface(new MyJavaScriptInterface(), "HTMLOUT");
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(webview, url);
                    LoadingDialog.cancelLoading();
                    webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");

//                    if (url.indexOf("/ccavResponseHandler.jsp") != -1) {
//                        webview.loadUrl("javascript:window.HTMLOUT.processHTML('<head>'+document.getElementsByTagName('html')[0].innerHTML+'</head>');");
//                    }
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    LoadingDialog.showLoadingDialog(WebViewActivity.this, "Loading...");
                }
            });

            try {
                String postData = AvenuesParams.ACCESS_CODE + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ACCESS_CODE), "UTF-8") + "&" + AvenuesParams.MERCHANT_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.MERCHANT_ID), "UTF-8") + "&" + AvenuesParams.ORDER_ID + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.ORDER_ID), "UTF-8") + "&" + AvenuesParams.REDIRECT_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.REDIRECT_URL), "UTF-8") + "&" + AvenuesParams.CANCEL_URL + "=" + URLEncoder.encode(mainIntent.getStringExtra(AvenuesParams.CANCEL_URL), "UTF-8") + "&" + AvenuesParams.ENC_VAL + "=" + URLEncoder.encode(encVal, "UTF-8");
                webview.postUrl(Constants.TRANS_URL, postData.getBytes());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                Log.e("error", e.getMessage());
            }

        }
    }



    private void subscribePLan() {
        final ProgressDialog progressDialog = new ProgressDialog(WebViewActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
            calls = apiInterface.SubscribePlan("1", "");

        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONObject obj = object.getJSONObject("success");
//                            if(isSubscribe) {
//                                JSONObject sub_obj = object.getJSONObject("subscription_data");
//                                sessionManager.saveSubscriptionDetails(sub_obj.getString("status"),
//                                        sub_obj.getString("subscription_status"),
//                                        sub_obj.getString("subscription_start_format"),
//                                        sub_obj.getString("subscription_end_format"));
                            //}
                            success_Alert_Dialog(WebViewActivity.this, obj.getString("msg"));




                           // GetPlans();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(WebViewActivity.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(WebViewActivity.this, getResources().getString(R.string.server_error));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
            }
        });
    }

    public  void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Success!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();

                Intent i = new Intent(WebViewActivity.this, PlanActivity.class);
                i.putExtra("payments","Payment");
                startActivity(i);
                finish();
            }
        });

        // Showing Alert Message

        if(!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }
}