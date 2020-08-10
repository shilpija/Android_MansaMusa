package com.freshhome.AdapterClass;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.MenuDetail;
import com.freshhome.R;
import com.freshhome.SalesModule.Activity_Sales_RequestDetail;
import com.freshhome.SalesModule.Fragment_Sales_JobHistory;
import com.freshhome.SalesModule.Fragment_Sales_Myrequest;
import com.freshhome.datamodel.Cart;
import com.freshhome.datamodel.SalesRequest;
import com.freshhome.fragments.UserCartFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class RecyclerSalesMyRequestAdapter extends RecyclerView.Adapter<RecyclerSalesMyRequestAdapter.MyViewHolder> {
    Context context;
    ArrayList<SalesRequest> array_MyRequest;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    Fragment_Sales_Myrequest sales_myrequestFragment;
    Fragment_Sales_JobHistory salesjobHistory;
    boolean ismyRequest = true;
    TextView textView;
    LinearLayout linearLayoutBadge;

    public RecyclerSalesMyRequestAdapter(Context context, ArrayList<SalesRequest> array_MyRequest, Fragment_Sales_Myrequest sales_myrequestFragment, TextView textView, LinearLayout linearLayoutBadge) {
        this.context = context;
        this.array_MyRequest = array_MyRequest;
        this.sales_myrequestFragment = sales_myrequestFragment;
        inflater = LayoutInflater.from(this.context);
        apiInterface = ApiClient.getInstance().getClient();
        ismyRequest = true;
        this.textView = textView;
        this.linearLayoutBadge = linearLayoutBadge;
    }

    public RecyclerSalesMyRequestAdapter(Context context, ArrayList<SalesRequest> array_MyRequest, Fragment_Sales_JobHistory salesjobHistory) {
        this.context = context;
        this.array_MyRequest = array_MyRequest;
        this.salesjobHistory = salesjobHistory;
        inflater = LayoutInflater.from(this.context);
        apiInterface = ApiClient.getInstance().getClient();
        ismyRequest = false;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.salesmyrequest_single_row, parent, false);

        return new RecyclerSalesMyRequestAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {
        mViewHolder.text_messgae.setText(array_MyRequest.get(i).getMsg());
        mViewHolder.text_supplierphone.setText(array_MyRequest.get(i).getSupplier_phonenumber());
        mViewHolder.text_supplierloc.setText(array_MyRequest.get(i).getSupplier_loc());

if(array_MyRequest != null){
    if(array_MyRequest.get(i).getRequest_type() != null) {
        if (array_MyRequest.get(i).getRequest_type().equalsIgnoreCase("1")) {
            mViewHolder.cate_name.setText("Sweets");
        } else {
            mViewHolder.cate_name.setText("Products");
        }
    }
}
        //show accept/reject text as per request status
        //hide and show button as per jobdone and my request cls
        if (!ismyRequest) {
            mViewHolder.text_reject.setVisibility(View.GONE);
            mViewHolder.text_accept.setVisibility(View.GONE);
        } else {
            if (array_MyRequest.get(i).getRequest_status().equalsIgnoreCase("accepted")) {
                mViewHolder.text_accept.setVisibility(View.VISIBLE);
                mViewHolder.text_accept.setText(array_MyRequest.get(i).getRequest_status());
                mViewHolder.text_reject.setVisibility(View.GONE);
                mViewHolder.linear_phone.setVisibility(View.VISIBLE);
            } else if (array_MyRequest.get(i).getRequest_status().equalsIgnoreCase("rejected")) {
                mViewHolder.linear_phone.setVisibility(View.GONE);
                mViewHolder.text_reject.setVisibility(View.VISIBLE);
                mViewHolder.text_reject.setText(array_MyRequest.get(i).getRequest_status());
                mViewHolder.text_accept.setVisibility(View.GONE);
            } else if (array_MyRequest.get(i).getRequest_status().equalsIgnoreCase("completed")) {
                mViewHolder.linear_phone.setVisibility(View.VISIBLE);
                mViewHolder.text_reject.setVisibility(View.GONE);
                mViewHolder.text_accept.setVisibility(View.GONE);
            } else {
                mViewHolder.linear_phone.setVisibility(View.GONE);
                mViewHolder.text_reject.setVisibility(View.VISIBLE);
                mViewHolder.text_accept.setVisibility(View.VISIBLE);
            }
        }

        mViewHolder.text_time.setText(array_MyRequest.get(i).getTime());
        mViewHolder.linear_main.setTag(i);
        mViewHolder.linear_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tag = Integer.parseInt(mViewHolder.linear_main.getTag().toString());
                Intent i = new Intent(context, Activity_Sales_RequestDetail.class);
                i.putExtra("id", array_MyRequest.get(tag).getNotification_id());
                i.putExtra("name", array_MyRequest.get(tag).getSupplier_name());
                i.putExtra("msg", array_MyRequest.get(tag).getMsg());
                i.putExtra("phone", array_MyRequest.get(tag).getSupplier_phonenumber());
                i.putExtra("loc", array_MyRequest.get(tag).getSupplier_loc());
                i.putExtra("lat", array_MyRequest.get(tag).getSupplier_lat());
                i.putExtra("lng", array_MyRequest.get(tag).getSupplier_lng());
                i.putExtra("reqstatus", array_MyRequest.get(tag).getRequest_status());
                i.putExtra("req_id", array_MyRequest.get(tag).getId());
                context.startActivity(i);
            }
        });

        //TODO SET TAG TO TEXT TO AVOID MISMATCH ID
        mViewHolder.text_accept.setTag(array_MyRequest.get(i).getNotification_id());
        mViewHolder.text_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    AcceptRejectRequest(mViewHolder.text_accept.getTag().toString(), true,
                            i, mViewHolder.text_accept, mViewHolder.text_reject);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

        //TODO SET TAG TO TEXT TO AVOID MISMATCH ID
        mViewHolder.text_reject.setTag(array_MyRequest.get(i).getNotification_id());
        mViewHolder.text_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonMethods.checkConnection()) {
                    AcceptRejectRequest(mViewHolder.text_reject.getTag().toString(), false, i, mViewHolder.text_accept, mViewHolder.text_reject);
                } else {
                    CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.internetconnection));
                }
            }
        });

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return array_MyRequest.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView text_messgae, text_supplierphone, text_price, text_supplierloc, text_accept,cate_name, text_reject, text_time;
        LinearLayout linear_status, linear_available, linear_main, linear_phone;
        RatingBar ratingBar;
        ImageView image_delete;
        CircleImageView imageDish;

        public MyViewHolder(View convertView) {
            super(convertView);
            cate_name = (TextView) convertView.findViewById(R.id.cate_name);
            text_time = (TextView) convertView.findViewById(R.id.text_time);
            text_accept = (TextView) convertView.findViewById(R.id.text_accept);
            text_reject = (TextView) convertView.findViewById(R.id.text_reject);
            text_messgae = (TextView) convertView.findViewById(R.id.text_messgae);
            text_supplierphone = (TextView) convertView.findViewById(R.id.text_supplierphone);
            text_supplierloc = (TextView) convertView.findViewById(R.id.text_supplierloc);
            linear_main = (LinearLayout) convertView.findViewById(R.id.linear_main);
            linear_phone = (LinearLayout) convertView.findViewById(R.id.linear_phone);
        }
    }

    private void AcceptRejectRequest(final String req_id, final boolean isAccept, final int i, final TextView text_accept, final TextView text_reject) {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(context.getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls;
        if (isAccept) {
            calls = apiInterface.AcceptSalesReq(req_id);
        } else {
            calls = apiInterface.RejectSalesReq(req_id);
        }

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

                            if (isAccept) {
                                array_MyRequest.get(i).setRequest_status("accepted");
                            } else {
                                array_MyRequest.get(i).setRequest_status("rejected");
                            }

                            if (object.has("data")) {
                                JSONObject obj = object.getJSONObject("data");
                                textView.setText(obj.getString("count"));
                                if(obj.getString("count").equalsIgnoreCase("0")){
                                    linearLayoutBadge.setVisibility(View.GONE);
                                }else{
                                    linearLayoutBadge.setVisibility(View.VISIBLE);
                                }
                            }
                            notifyDataSetChanged();

                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(context, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.server_error));
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
                CommonUtilFunctions.Error_Alert_Dialog(context, context.getResources().getString(R.string.server_error));
            }
        });
    }

    private void Detaildialog() {
        final GoogleMap[] mMap = new GoogleMap[1];
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.salereq_detail_single_row);
        //remove shadow of dialg because that was showing shadow on google map
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCanceledOnTouchOutside(false);
        TextView text_reject = (TextView) dialog.findViewById(R.id.text_reject);
        MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
        MapsInitializer.initialize(context);
        mMapView.onCreate(dialog.onSaveInstanceState());
        mMapView.onResume();// needed to get the map to display immediately
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                if (ismyRequest) {
                    sales_myrequestFragment.linear_shadow.setVisibility(View.VISIBLE);
                } else {
                    salesjobHistory.linear_shadow.setVisibility(View.VISIBLE);
                }
                LatLng posisiabsen = new LatLng(-33.873, 151.206); ////your lat lng
                googleMap.addMarker(new MarkerOptions().position(posisiabsen).title("Yout title"));
                //show navigation
                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(posisiabsen));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                //style with google  online tool
                googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                        context, R.raw.style_map_json));
            }
        });
        text_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideShow_ShadowonList();
                dialog.dismiss();

            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    HideShow_ShadowonList();
                    dialog.dismiss();

                }
                return true;
            }
        });

        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    private void HideShow_ShadowonList() {
        if (ismyRequest) {
            sales_myrequestFragment.linear_shadow.setVisibility(View.GONE);
        } else {
            salesjobHistory.linear_shadow.setVisibility(View.GONE);
        }
    }

}