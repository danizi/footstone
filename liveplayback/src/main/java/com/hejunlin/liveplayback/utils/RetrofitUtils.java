package com.hejunlin.liveplayback.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
// https://blog.csdn.net/Agg_bin/article/details/86628340
public class RetrofitUtils {
    public static boolean notNetworkConnected(Context applicationContext){
        //通过getSystemService()方法得到connectionManager这个系统服务类，专门用于管理网络连接
        ConnectivityManager connectionManager = (ConnectivityManager) applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectionManager.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isAvailable();
    }

    public static OkHttpClient getOkHttpClient(final Context applicationContext) {
        Interceptor interceptor = new Interceptor() {
            // 读取缓存时，就不会走intercept回调
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                if (notNetworkConnected(applicationContext)) { // 判断网络是否连接
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }
                okhttp3.Response response = chain.proceed(request);
                if (notNetworkConnected(applicationContext)) {
                    return response.newBuilder() // 长缓存,有效期为7天
                            .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7)
                            .removeHeader("Pragma")
                            .build();
                } else {
                    String cacheControl = request.cacheControl().toString();
                    if (TextUtils.isEmpty(cacheControl))
                        cacheControl = "public,max-age=600"; // 短缓存,有效期10分钟
                    return response.newBuilder()
                            .header("Cache-Control", cacheControl)
                            .removeHeader("Pragma") // 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                            .build();
                }
            }
        };
        File cacheFile = new File(applicationContext.getCacheDir(), "responses");
        Cache cache = new Cache(cacheFile, 1024 * 1024 * 20); // 缓存大小20MB
        return new OkHttpClient.Builder()
                // addNetworkInterceptor添加的是网络拦截器，他会在在request和resposne是分别被调用一次，
                // addInterceptor添加的是application拦截器，他只会在response被调用一次.
                .addInterceptor(interceptor) // 不添加的话，没网络就不会有缓存
                .addNetworkInterceptor(interceptor) // 设置拦截器。
                .cache(cache) // 设置缓存。
                .retryOnConnectionFailure(true)// 设置重试。连接失败后是否重新连接,默认重试一次，若需要重试N次，则要实现拦截器。
                // 设置超时。square官方建议timeout
                .connectTimeout(3, TimeUnit.SECONDS) // 尽量设置得小一些(比如10s),这样可以减小弱网环境下手机的负载，同时对于用户体验也有好处
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
