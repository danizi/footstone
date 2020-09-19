package com.hejunlin.liveplayback.biz;

import com.hejunlin.liveplayback.bean.ProgramBean;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * 电视节目接口
 */
public interface ProgramServer {
    @POST("login")
    @FormUrlEncoded
    Call<List<ProgramBean>> postProgram(@Field("uid") String uid);
}
