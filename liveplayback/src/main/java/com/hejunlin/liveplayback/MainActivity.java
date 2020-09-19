/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
 *
 * Github:https://github.com/hejunlin2013/LivePlayback
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hejunlin.liveplayback;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hejunlin.liveplayback.bean.ProgramBean;
import com.hejunlin.liveplayback.biz.ProgramServer;
import com.hejunlin.liveplayback.widget.ijkplayer.CusVideoView;
import com.hejunlin.liveplayback.widget.ijkplayer.media.IjkVideoView;
import com.hejunlin.liveplayback.utils.RetrofitUtils;
import com.hejunlin.liveplayback.utils.SPUtils;
import com.hejunlin.liveplayback.widget.LinkageMenu;
import com.hejunlin.liveplayback.widget.MetroViewBorderImpl;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hejunlin on 2016/10/28.
 * blog: http://blog.csdn.net/hejjunlin
 */
public class MainActivity extends Activity implements ViewTreeObserver.OnGlobalFocusChangeListener {

    private View mainView;
    private LinkageMenu linkageMenu;
    private CusVideoView videoView;
    // 点击两下退出
    private static final int TIME_EXIT = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        mainView = findViewById(R.id.main);
        videoView = findViewById(R.id.view_video_View);
        linkageMenu = findViewById(R.id.view_linkageMenu);

        linkageMenu.setMainView(mainView);
        linkageMenu.setMenuListener(new LinkageMenu.OnClickMenuListener() {
            @Override
            public void onParentClick(int pos) {
                Log.d("TAG", "");
            }

            @Override
            public void onChildClick(int parentIndex, int pos) {
                Log.d("TAG", "");
                videoView.initVideo(linkageMenu.getData().get(parentIndex).getItems().get(pos).getUrl());
            }
        });
        mainView.getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    private void initData() {
        // 网络请求
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.8:8080/Live/")
                .client(RetrofitUtils.getOkHttpClient(this))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ProgramServer server = retrofit.create(ProgramServer.class);
        Call<List<ProgramBean>> call = server.postProgram("uid");
        call.enqueue(new Callback<List<ProgramBean>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProgramBean>> call, @NonNull Response<List<ProgramBean>> response) {
                List<ProgramBean> body = response.body();
                linkageMenu.setData(body);
                //typeAdapter.getDatas().addAll(body);
                //typeAdapter.notifyDataSetChanged();
                String b = new Gson().toJson(body);
                SPUtils.put(MainActivity.this,"body",b);
                Log.i("TAG", "");
            }

            @Override
            public void onFailure(@NonNull Call<List<ProgramBean>> call, @NonNull Throwable t) {
                Log.i("TAG", "");
                String json = (String) SPUtils.get(MainActivity.this,"body","body");
                if(TextUtils.isEmpty(json)) {
                    return;
                }
                ProgramBean[] array  = new Gson().fromJson(json,ProgramBean[].class);
                List<ProgramBean> body = Arrays.asList(array );
                linkageMenu.setData(body);
            }
        });
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (newFocus instanceof IjkVideoView) {
            oldFocus.requestFocus();
        }
        Log.d("TAG", "oldFocus->" + oldFocus + " newFocus->" + newFocus);
    }

    @Override
    protected void onStop() {
        super.onStop();
        videoView.release();
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_EXIT > System.currentTimeMillis()) {
            super.onBackPressed();
        } else {
            if(!linkageMenu.isShown()){
                Toast.makeText(this, "再点击一次返回退出程序", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }
}
