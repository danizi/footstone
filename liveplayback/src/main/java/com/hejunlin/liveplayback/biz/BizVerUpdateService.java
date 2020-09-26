package com.hejunlin.liveplayback.biz;



import com.hejunlin.liveplayback.bean.UpdateVersionBean;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * apk版本更新
 */
public interface BizVerUpdateService {

    @POST("updateVer")
    @FormUrlEncoded
    Call<UpdateVersionBean> updateVersion(@Field("version") String version);

    @Streaming
    @GET
    Call<ResponseBody> downloadApk(@Url String url);

}
