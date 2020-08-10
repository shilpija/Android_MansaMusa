package com.freshhome.SalesModule;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.freshhome.CommonUtil.CommonMethods;
import com.freshhome.CommonUtil.CommonUtilFunctions;
import com.freshhome.CommonUtil.ConstantValues;
import com.freshhome.R;
import com.freshhome.datamodel.DemoVideos;
import com.freshhome.retrofit.ApiClient;
import com.freshhome.retrofit.ApiInterface;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityDemoVideos extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, YouTubePlayer.PlayerStateChangeListener, View.OnClickListener {
    LinearLayout linear_player;
    ApiInterface apiInterface;
    ArrayList<DemoVideos> videosArray;
    List<String> videosListArray;
    YouTubePlayerView youtubePlayerview;
    ImageView image_back;
    TextView text_description, text_previous, text_next, text_count, heading;
    String video_youtube_id = "", videoid = "", isVideoWatched = "";
    YouTubePlayer youtubeplay;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    int currentVideo = 0;
//    VideoView videoview;

    public ActivityDemoVideos() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_demo_videos);
        ActivitySalesNavDrawer.heading.setText(R.string.demovideo);
        videosArray = new ArrayList<>();
        videosListArray = new ArrayList<>();
        apiInterface = ApiClient.getInstance().getClient();
        youtubePlayerview = (YouTubePlayerView) findViewById(R.id.youtubePlayerview);
//        videoview = (VideoView) findViewById(R.id.videoview);
//        linear_player = (LinearLayout) findViewById(R.id.linear_player);
        heading = (TextView) findViewById(R.id.heading);
        text_count = (TextView) findViewById(R.id.text_count);
        text_description = (TextView) findViewById(R.id.text_description);
        text_previous = (TextView) findViewById(R.id.text_previous);
        text_previous.setOnClickListener(this);
        text_next = (TextView) findViewById(R.id.text_next);
        text_next.setOnClickListener(this);

        image_back = (ImageView) findViewById(R.id.image_back);
        image_back.setOnClickListener(this);

        if (CommonMethods.checkConnection()) {
            getdata();
        } else {
            CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this, getResources().getString(R.string.internetconnection));
        }
    }


    private void setUpVideos() {
        for (int i = 0; i < videosArray.size(); i++) {
//            if (videosArray.get(i).isWatched().equalsIgnoreCase("0")) {
            playVideo(videosArray.get(0).getUrl(), videosArray.get(0).getName(),
                    videosArray.get(0).getDescription(), 0, true, videosArray.get(0).getId(), videosArray.get(0).isWatched());
//                return;
//            }
        }
    }

    private void playVideo(String url, String name, String description, int currentV, boolean isStart, String id, String isWatched) {
        currentVideo = currentV;
        video_youtube_id = CommonUtilFunctions.extractYTId(url);
        videoid = id;
        isVideoWatched = isWatched;
        heading.setText(name);
        text_description.setText(Html.fromHtml(description));
        text_count.setText((currentVideo + 1) + "/" + videosArray.size());
        if (isStart) {
            youtubePlayerview.initialize(getResources().getString(R.string.api_key), this);
        } else {
            if (youtubeplay != null) {
                youtubeplay.loadVideo(video_youtube_id);
            }
        }
    }


    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {
        if (!wasRestored) {
            youTubePlayer.loadVideo(video_youtube_id);
//            youTubePlayer.loadVideos(videosListArray);
            youTubePlayer.setShowFullscreenButton(false);
            youTubePlayer.setPlayerStateChangeListener(this);
            youtubeplay = youTubePlayer;
            youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                    getString(R.string.error_player), errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.image_back:
                ActivityDemoVideos.this.finish();
                break;

            case R.id.text_previous:
                if (currentVideo != 0) {
                    currentVideo = currentVideo - 1;
                    text_count.setText((currentVideo + 1) + "/" + videosArray.size());
                    playVideo(videosArray.get(currentVideo).getUrl(), videosArray.get(currentVideo).getName(),
                            videosArray.get(currentVideo).getDescription(), currentVideo, false, videosArray.get(currentVideo).getId(), videosArray.get(currentVideo).isWatched());
                    if (currentVideo == 0) {
                        text_previous.setVisibility(View.INVISIBLE);
                        text_next.setVisibility(View.VISIBLE);
                    }
                } else {
                    text_previous.setVisibility(View.INVISIBLE);
                    text_next.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.text_next:
                if (currentVideo < videosArray.size() - 1) {
                    currentVideo = currentVideo + 1;
                    text_count.setText(currentVideo + "/" + videosArray.size());
                    playVideo(videosArray.get(currentVideo).getUrl(), videosArray.get(currentVideo).getName(),
                            videosArray.get(currentVideo).getDescription(), currentVideo, false, videosArray.get(currentVideo).getId(), videosArray.get(currentVideo).isWatched());
                    if (currentVideo == videosArray.size() - 1) {
                        text_next.setVisibility(View.INVISIBLE);
                        text_previous.setVisibility(View.VISIBLE);
                    }
                } else {
                    text_next.setVisibility(View.INVISIBLE);
                    text_previous.setVisibility(View.VISIBLE);
                }
                if (youtubeplay != null) {
                    youtubeplay.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getYouTubePlayerProvider().initialize(getResources().getString(R.string.api_key),
                ActivityDemoVideos.this);
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtubePlayerview);
    }

    @Override
    public void onLoading() {
        Log.e(ConstantValues.TAG, "onLoading");
    }

    @Override
    public void onLoaded(String s) {
        Log.e(ConstantValues.TAG, "onLoaded");
    }

    @Override
    public void onAdStarted() {
        Log.e(ConstantValues.TAG, "onAdStarted");
    }

    @Override
    public void onVideoStarted() {
        Log.e(ConstantValues.TAG, "onVideoStarted");
    }

    @Override
    public void onVideoEnded() {
//        //api hit

        //check if iswatched is 0 then hit api to make it watched
        if (isVideoWatched.equalsIgnoreCase("0")) {
            if (CommonMethods.checkConnection()) {
                UpdateVideoWatched(videoid);
            } else {
                CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this, getResources().getString(R.string.internetconnection));
            }
        }

    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.e(ConstantValues.TAG, "onError");
    }


    //TODO API parsing
    private void getdata() {
        final ProgressDialog progressDialog = new ProgressDialog(ActivityDemoVideos.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.GetSalesDemoVideos();
        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        videosArray = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                            JSONArray jsonArray = object.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                DemoVideos demoVideos = new DemoVideos();
                                demoVideos.setId(obj.getString("video_id"));
                                demoVideos.setName(obj.getString("title"));
                                demoVideos.setUrl(obj.getString("link"));
                                demoVideos.setDescription(obj.getString("description"));
                                demoVideos.setisWatched(obj.getString("is_watched"));
                                demoVideos.setCurrentVideo(0);
                                videosListArray.add(CommonUtilFunctions.extractYTId(obj.getString("link")));
                                videosArray.add(demoVideos);
                            }
                            setUpVideos();
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this,
                        getResources().getString(R.string.server_error));
            }
        });
    }

    //TODO API parsing
    private void UpdateVideoWatched(String video_id) {
        final ProgressDialog progressDialog = new ProgressDialog(ActivityDemoVideos.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.processing_dilaog));
        progressDialog.show();
        Call<JsonElement> calls = apiInterface.UdpdateVideoWatched(video_id);
        calls.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                try {
                    if (response.code() == 200) {
                        JSONObject object = new JSONObject(response.body().getAsJsonObject().toString().trim());
                        videosArray = new ArrayList<>();
                        if (object.getString("code").equalsIgnoreCase("200")) {
                        } else {
                            JSONObject obj = object.getJSONObject("error");
                            CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this, obj.getString("msg"));
                        }
                    } else {
                        CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this, getResources().getString(R.string.server_error));
                    }
                } catch (
                        JSONException e)

                {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                call.cancel();
                CommonUtilFunctions.Error_Alert_Dialog(ActivityDemoVideos.this,
                        getResources().getString(R.string.server_error));
            }
        });
    }
}
