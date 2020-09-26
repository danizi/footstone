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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.hejunlin.liveplayback.bean.ProgramBean;
import com.hejunlin.liveplayback.bean.UpdateVersionBean;
import com.hejunlin.liveplayback.biz.BizProgramServer;
import com.hejunlin.liveplayback.biz.BizVerUpdateService;
import com.hejunlin.liveplayback.utils.AppUtils;
import com.hejunlin.liveplayback.utils.FileUtils;
import com.hejunlin.liveplayback.utils.LogC;
import com.hejunlin.liveplayback.utils.RetrofitUtils;
import com.hejunlin.liveplayback.utils.SPUtils;
import com.hejunlin.liveplayback.widget.LinkageMenu;
import com.hejunlin.liveplayback.widget.ijkplayer.CusVideoView;
import com.hejunlin.liveplayback.widget.ijkplayer.media.IjkVideoView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 整改点
 * 1 节目文字大于宽度范围设置跑马灯效果。
 * 2 更新接口便于随时更新APK。
 * 3 整改下适配器类代码 和 cusVideoView 和 LinkageMenu，使其通用规范。
 * 4 体验细节
 * 4.1 点击home按键弹出多级菜单，当没显示多级菜单时，方向左右按键可以切换台。
 * 4.2 界面新增 视频下载的速度、电视台信息
 * 4.3 首次进入白屏处理
 * 4.4 视频加载速度优化
 * 5 增加播放器的配置信息
 * 6 crash 信息相关收集。
 */
public class MainActivity extends Activity implements ViewTreeObserver.OnGlobalFocusChangeListener {

    private View mainView;
    private LinkageMenu linkageMenu;
    private CusVideoView videoView;
    private static final int TIME_EXIT = 2000;
    private long mBackPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 去掉顶部状态栏
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

        // 联动菜单处理
        linkageMenu.setMainView(mainView);
        linkageMenu.setMenuListener(new LinkageMenu.OnClickMenuListener() {
            @Override
            public void onParentClick(int pos) {
                LogC.d("");
            }

            @Override
            public void onChildClick(int parentIndex, int pos) {
                LogC.d("");
                videoView.initVideo(linkageMenu.getData().get(parentIndex).getItems().get(pos).getUrl());
            }
        });
        mainView.getViewTreeObserver().addOnGlobalFocusChangeListener(this);
    }

    private void initData() {
        // 节目数据获取
        ProgramServer();
        // APP文件下载
        FileUtils.verifyStoragePermissions(this);
        FileUtils.addFile("down.apk");
        verUpdateService();
    }

    private void ProgramServer() {
        BizProgramServer server = RetrofitUtils.getRetrofit(getApplicationContext()).create(BizProgramServer.class);
        Call<List<ProgramBean>> call = server.postProgram("uid");
        call.enqueue(new Callback<List<ProgramBean>>() {
            @Override
            public void onResponse(@NonNull Call<List<ProgramBean>> call, @NonNull Response<List<ProgramBean>> response) {
                if (response.code() == 200) {
                    List<ProgramBean> body = response.body();
                    linkageMenu.setData(body);
                    cacheProgramData(body);
                } else {
                    useCache();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ProgramBean>> call, @NonNull Throwable t) {
                LogC.i("request fail");
                useCache();
            }

            private void cacheProgramData(List<ProgramBean> body) {
                String b = new Gson().toJson(body);
                if (TextUtils.isEmpty(b)) {
                    return;
                }
                LogC.i("cacheProgram data");
                SPUtils.put(MainActivity.this, "body", b);
            }

            private void useCache() {
                String json = (String) SPUtils.get(MainActivity.this, "body", "body");
                if (TextUtils.isEmpty(json)) {
                    LogC.e("request fail use cache fail");
                    return;
                }
                LogC.i("request fail and use cache");
                ProgramBean[] array = new Gson().fromJson(json, ProgramBean[].class);
                List<ProgramBean> body = Arrays.asList(array);
                linkageMenu.setData(body);
            }
        });
    }

    private void verUpdateService() {
        /* 版本更新步骤
         * 1 请求更新接口，根据响应可以通过版本号或者状态码判断是否更新。
         * 2 如果有新版本弹出对话框，用户点击确定或者取消进行操作
         * 3 如果点击确认，弹出进度条设置信息，根据更新接口中的链接开始下载，更新进度条。
         * 4 下载完成安装文件
         * 5 异常情况
         *   1） 下载文件过程失败（例如用户退出、断网、代码问题）
         *   2） 下载错误文件安装失败
         *
         * 6 用户优化体验
         *   1）3G下载提醒用户。
         *   2）支持断点下载？
         *   3）下载失败提示用户。
         *
         * 实现技术：Retrofit + 系统弹框进度框。
         */
        final Context ctx = MainActivity.this;
        // 1 请求更新版本接口
        BizVerUpdateService bizVerUpdateService = RetrofitUtils.getRetrofit(getApplicationContext()).create(BizVerUpdateService.class);
        Call<UpdateVersionBean> updateVerCall = bizVerUpdateService.updateVersion(AppUtils.getVersionName(this));
        updateVerCall.enqueue(new Callback<UpdateVersionBean>() {
            @Override
            public void onResponse(@NonNull Call<UpdateVersionBean> call, @NonNull Response<UpdateVersionBean> response) {
                if (response.code() == 200) {
                    if (response.body() == null) {
                        LogC.e("verUpdateService ,bizVerUpdateService response.body() is null");
                        return;
                    }
                    final UpdateVersionBean data = response.body();
                    int verCode = AppUtils.getVersionCode(ctx);
                    if (data.getVerCode() > verCode) {
                        // 2 当前版本不是最新版本，弹出更新提示框
                        new AlertDialog.Builder(ctx)
                                .setTitle("更新提示")
                                .setMessage(data.getModifyContent())
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        LogC.i("verUpdateService, cancel");
                                    }
                                })
                                .setPositiveButton("升级", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        String apkUrl = data.getDownloadUrl();
                                        if (TextUtils.isEmpty(apkUrl)) {
                                            LogC.e("verUpdateService, downloadUrl is null,updateVersion fail");
                                            return;
                                        }
                                        // 3 用户点击确认升级，开始下载，并更新进度条。
                                        final ProgressDialog pd = new ProgressDialog(ctx);
                                        pd.setMax(100);
                                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                        pd.show();
                                        LogC.i("verUpdateService, enter");
                                        BizVerUpdateService service = RetrofitUtils.getRetrofit(getApplicationContext()).create(BizVerUpdateService.class);
                                        Call<ResponseBody> downloadApkCall = service.downloadApk(apkUrl);
                                        downloadApkCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull final Response<ResponseBody> response) {
                                                if (response.body() == null) {
                                                    LogC.e("verUpdateService, downloadApkCall response.body() is null");
                                                    return;
                                                }
                                                // 6 3G下载提醒用户。
                                                new Thread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        String path = FileUtils.path;
                                                        String fileName = "down.apk";
                                                        OutputStream os = null;
                                                        InputStream is = null;
                                                        try {
                                                            long time = System.currentTimeMillis();
                                                            os = new FileOutputStream(new File(path + fileName));
                                                            is = response.body().byteStream();
                                                            long contentTotalLength = response.body().contentLength();
                                                            long contentProgressLength = 0;
                                                            byte[] buf = new byte[1024 * 10];
                                                            int len;
                                                            int percent;
                                                            while ((len = is.read(buf)) != -1) {
                                                                os.write(buf, 0, len);
                                                                contentProgressLength += len;
                                                                percent = (int) (contentProgressLength * 100 / contentTotalLength);
                                                                pd.setProgress(percent);
                                                                LogC.i("verUpdateService,downLoadApk progress is :" + percent);
                                                            }
                                                            LogC.i("verUpdateService,downLoad finish use " + (System.currentTimeMillis() - time) / 1000 + " seconds");
                                                            pd.dismiss();
                                                            // 4 安装下载的apk
                                                            AppUtils.installAPK(ctx, path, fileName);
                                                        } catch (IOException e) {
                                                            LogC.e("verUpdateService, IOException , downLoadApk is fail");
                                                            // 5 提示用户下载失败
                                                            showErrorDlg();
                                                        } finally {
                                                            if (is != null) {
                                                                try {
                                                                    is.close();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                            if (os != null) {
                                                                try {
                                                                    os.close();
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                            }
                                                            pd.dismiss();
                                                        }
                                                    }
                                                }).start();
                                            }

                                            @Override
                                            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                                                LogC.i("verUpdateService,onFailure,downLoadApk is fail");
                                                if (call.isExecuted())
                                                    call.cancel();
                                                // 5 提示用户下载失败
                                                showErrorDlg();
                                            }

                                            private void showErrorDlg() {
                                                pd.dismiss();
                                                ((MainActivity) ctx).runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        new AlertDialog.Builder(ctx)
                                                                .setTitle("提示")
                                                                .setMessage("抱歉下载失败，请联系客服人员。")
                                                                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        dialog.dismiss();
                                                                    }
                                                                })
                                                                .create()
                                                                .show();
                                                    }
                                                });
                                            }

                                        });
                                    }
                                })
                                .create()
                                .show();
                    } else {
                        LogC.i("verUpdateService ,latest version");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateVersionBean> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onGlobalFocusChanged(View oldFocus, View newFocus) {
        if (newFocus instanceof IjkVideoView) {
            oldFocus.requestFocus();
        }
        LogC.d("oldFocus->" + oldFocus + " newFocus->" + newFocus);
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
            if (!linkageMenu.isShow()) {
                Toast.makeText(this, "再点击一次返回退出程序", Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
    }
}
