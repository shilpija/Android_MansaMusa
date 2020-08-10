package com.freshhome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import utils.ProgressBarAnimation;

public class OrderTrackingDetailActivity extends AppCompatActivity {
    boolean isSteps = false;
    @BindView(R.id.image_back)
    ImageView image_back;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_ur_steps)
    TextView tv_ur_steps;
    @BindView(R.id.tv_expected_date)
    TextView tv_expected_date;
    @BindView(R.id.ll_first)
    RelativeLayout ll_first;
    @BindView(R.id.ll_confrim)
    RelativeLayout ll_confrim;
    @BindView(R.id.ll_processing)
    RelativeLayout ll_processing;
    @BindView(R.id.ll_out_del)
    RelativeLayout ll_out_del;
    @BindView(R.id.ll_delivery)
    RelativeLayout ll_delivery;
    @BindView(R.id.progress_bar1)
    ProgressBar progress_bar1;
    @BindView(R.id.progress_bar2)
    ProgressBar progress_bar2;
    @BindView(R.id.progress_bar4)
    ProgressBar progress_bar4;
    @BindView(R.id.progress_bar5)
    ProgressBar progress_bar5;
    @BindView(R.id.order_status1)
    TextView order_status1;
    @BindView(R.id.confrimed)
    TextView confrimed;
    @BindView(R.id.tv_processing)
    TextView tv_processing;
    @BindView(R.id.out_del)
    TextView out_del;
    @BindView(R.id.delivery)
    TextView delivery;
    @BindView(R.id.ll_steps)
    LinearLayout ll_steps;
    private String orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_tracking_detail);
        ButterKnife.bind(this);

        String orderStatus = "";
        if (getIntent() != null) {
            orderStatus = (String) getIntent().getStringExtra("order_status");
            String estimateTime = (String) getIntent().getStringExtra("estimate_time");
            orderId = (String) getIntent().getStringExtra("order_id");

            tv_status.setText("Status: " + orderStatus);
            tv_expected_date.setText("Expected Date: " + estimateTime);

        }
        // Ordered Progress.....
        //String status = "complete";

        switch (orderStatus) {
            case "New Order":
                ll_first.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "Order item no #" + orderId + " is placed, Please wait till it is confirmed";
                        // Toast.makeText(OrderTrackingDetailActivity.this, "order iem id #" +orderId+ " is confirmed, we will update you once it is in process", Toast.LENGTH_SHORT).show();
                        success_Alert_Dialog(OrderTrackingDetailActivity.this, msg);
                    }
                });
                order_status1.setTextColor(getResources().getColor(R.color.colorPrimary));
                ll_first.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));
                break;

            case "Accept":
                ll_confrim.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "order item id #" + orderId + " is confirmed, we will update you once it is in process";
                        // Toast.makeText(OrderTrackingDetailActivity.this, "order iem id #" +orderId+ " is confirmed, we will update you once it is in process", Toast.LENGTH_SHORT).show();
                        success_Alert_Dialog(OrderTrackingDetailActivity.this, msg);
                    }
                });

                ll_first.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));
                order_status1.setTextColor(getResources().getColor(R.color.colorPrimary));
                confrimed.setTextColor(getResources().getColor(R.color.colorPrimary));
                ProgressBarAnimation anim = new ProgressBarAnimation(progress_bar1, 0, 100);
                anim.setDuration(1500);
                progress_bar1.startAnimation(anim);

                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_confrim.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                break;

            case "Processing":

                ll_processing.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "order item id #" + orderId + "  is in process, we will update you once it is out for delivery. supplier is making the offer for you.";
                        success_Alert_Dialog(OrderTrackingDetailActivity.this, msg);
                        //Toast.makeText(OrderTrackingDetailActivity.this, "order item id #" +orderId+ "  is in process, we will update you once it is out for delivery. supplier is making the offer for you.", Toast.LENGTH_SHORT).show();
                    }
                });

                ll_first.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));
                order_status1.setTextColor(getResources().getColor(R.color.colorPrimary));
                confrimed.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_processing.setTextColor(getResources().getColor(R.color.colorPrimary));

                ProgressBarAnimation anim1 = new ProgressBarAnimation(progress_bar1, 0, 100);
                anim1.setDuration(1000);
                progress_bar1.startAnimation(anim1);

                anim1.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_confrim.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                ProgressBarAnimation anim2 = new ProgressBarAnimation(progress_bar2, 0, 100);
                anim2.setDuration(1500);
                progress_bar2.startAnimation(anim2);

                anim2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_processing.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                final Handler handler2 = new Handler();
                if (handler2 != null) {

                    handler2.postDelayed(new

                                                 Runnable() {
                                                     @Override
                                                     public void run() {


                                                         if (handler2 != null) {


                                                         }
                                                     }
                                                 }, 1500);
                }
                break;


            case "Out for delivery":
                ll_out_del.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "order item id #" + orderId + "  is out for delivery. Our driver will contact you soon";
                        success_Alert_Dialog(OrderTrackingDetailActivity.this, msg);
                        //Toast.makeText(OrderTrackingDetailActivity.this, "order item id #" +orderId+ "  is out for delivery. Our driver will contact you soon", Toast.LENGTH_SHORT).show();
                    }
                });
                ll_first.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));
                order_status1.setTextColor(getResources().getColor(R.color.colorPrimary));
                confrimed.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_processing.setTextColor(getResources().getColor(R.color.colorPrimary));
                out_del.setTextColor(getResources().getColor(R.color.colorPrimary));

                ProgressBarAnimation anim3 = new ProgressBarAnimation(progress_bar1, 0, 100);
                anim3.setDuration(1000);
                progress_bar1.startAnimation(anim3);

                anim3.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_confrim.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                ProgressBarAnimation anim4 = new ProgressBarAnimation(progress_bar2, 0, 100);
                anim4.setDuration(1500);
                progress_bar2.startAnimation(anim4);

                anim4.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_processing.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                ProgressBarAnimation anim5 = new ProgressBarAnimation(progress_bar4, 0, 100);
                anim5.setDuration(2000);
                progress_bar4.startAnimation(anim5);

                anim5.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_out_del.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                final Handler handler = new Handler();
                if (handler != null) {

                    handler.postDelayed(new

                                                Runnable() {
                                                    @Override
                                                    public void run() {


                                                        if (handler != null) {


                                                        }
                                                    }
                                                }, 500);
                }
                break;


            case "Completed":
                ll_delivery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String msg = "order item id #" + orderId + " is delivered. Please rate and review our item";
                        success_Alert_Dialog(OrderTrackingDetailActivity.this, msg);
                        //Toast.makeText(OrderTrackingDetailActivity.this, "order item id #" +orderId+ " is delivered. Please rate and review our item", Toast.LENGTH_SHORT).show();
                    }
                });
                ll_first.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));
                order_status1.setTextColor(getResources().getColor(R.color.colorPrimary));
                confrimed.setTextColor(getResources().getColor(R.color.colorPrimary));
                tv_processing.setTextColor(getResources().getColor(R.color.colorPrimary));
                out_del.setTextColor(getResources().getColor(R.color.colorPrimary));
                delivery.setTextColor(getResources().getColor(R.color.colorPrimary));

                ProgressBarAnimation anim6 = new ProgressBarAnimation(progress_bar1, 0, 100);
                anim6.setDuration(1000);
                progress_bar1.startAnimation(anim6);

                anim6.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_confrim.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                ProgressBarAnimation anim7 = new ProgressBarAnimation(progress_bar2, 0, 100);
                anim7.setDuration(1500);
                progress_bar2.startAnimation(anim7);

                anim7.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_processing.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                ProgressBarAnimation anim8 = new ProgressBarAnimation(progress_bar4, 0, 100);
                anim8.setDuration(2000);
                progress_bar4.startAnimation(anim8);

                anim8.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_out_del.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                ProgressBarAnimation anim9 = new ProgressBarAnimation(progress_bar5, 0, 100);
                anim9.setDuration(2500);
                progress_bar5.startAnimation(anim9);

                anim9.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        ll_delivery.setBackground(ContextCompat.getDrawable(OrderTrackingDetailActivity.this, R.drawable.back_grey_circle));

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                final Handler handler5 = new Handler();
                if (handler5 != null) {

                    handler5.postDelayed(new

                                                 Runnable() {
                                                     @Override
                                                     public void run() {


                                                         if (handler5 != null) {


                                                         }
                                                     }
                                                 }, 2000);
                }
                break;


        }
    }

    @OnClick({R.id.image_back, R.id.tv_ur_steps})
    void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_back:
                finish();
                break;
            case R.id.tv_ur_steps:
                if (isSteps) {
                    ll_steps.setVisibility(View.GONE);
                    isSteps = false;
                } else {
                    ll_steps.setVisibility(View.VISIBLE);
                    isSteps = true;

                }
                break;
        }
    }


    public void success_Alert_Dialog(final Context context, String message) {
        final AlertDialog alertDialog = new AlertDialog.Builder(
                context, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).create();

        // Setting Dialog Title
        alertDialog.setTitle("Mansa Musa!");

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting Icon to Dialog
//        alertDialog.setIcon(R.drawable.call);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog closed
                alertDialog.dismiss();

            }
        });

        // Showing Alert Message

        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }
}
