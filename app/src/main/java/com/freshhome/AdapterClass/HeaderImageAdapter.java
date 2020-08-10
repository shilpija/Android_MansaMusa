package com.freshhome.AdapterClass;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freshhome.ChooseBannerImage;
import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.CommonUtil.UserSessionManager;
import com.freshhome.HomeItemDetail;
import com.freshhome.R;
import com.freshhome.datamodel.HomeItem;
import com.freshhome.datamodel.NameID;
import com.freshhome.fragments.HomeFragment;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.gson.JsonElement;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by HP on 01-05-2018.
 */

public class HeaderImageAdapter extends RecyclerView.Adapter<HeaderImageAdapter.MyViewHolder> {
    Context context;
    ArrayList<NameID> arrayHeaderImages;
    LayoutInflater inflater;
    ApiInterface apiInterface;
    UserSessionManager sessionManager;
    private static final int REQUEST_BANNER_IMAGE = 4;
    public HeaderImageAdapter(Context context, ArrayList<NameID> arrayHeaderImages) {
        this.context = context;
        this.arrayHeaderImages = arrayHeaderImages;
        apiInterface = ApiClient.getInstance().getClient();
        inflater = LayoutInflater.from(this.context);
        sessionManager = new UserSessionManager(context);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.single_row_header_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder mViewHolder, final int i) {

        if (arrayHeaderImages.get(i).getImg_url().equalsIgnoreCase("")) {
            Picasso.get().load(R.drawable.food).into(mViewHolder.imageview);
        } else {
            Picasso.get().load(arrayHeaderImages.get(i).getImg_url()).placeholder(R.drawable.left_menu).into(mViewHolder.imageview);
        }

        mViewHolder.imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChooseBannerImage.class);
                intent.putExtra("banner_id", arrayHeaderImages.get(i).getId());
                intent.putExtra("image_url", arrayHeaderImages.get(i).getImg_url());
                ((Activity) context).startActivityForResult(intent,REQUEST_BANNER_IMAGE);
            }
        });

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return arrayHeaderImages.size();
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;

        public MyViewHolder(View convertView) {
            super(convertView);

            imageview = (ImageView) convertView.findViewById(R.id.imageview);

        }
    }


}