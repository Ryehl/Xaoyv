package com.xaoyv.qqcache.utils;

import android.os.Build;
import android.util.Log;

import com.xaoyv.qqcache.interfaces.IApi;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetUtils {
    private static NetUtils netUtils;
    private final String TAG = this.getClass().getSimpleName();
    private IApi mIApi;
    private Retrofit retrofit;

    private NetUtils() {
        //拦截器
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //okHttp
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        //retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //接口
        mIApi = retrofit.create(IApi.class);
    }

    public static NetUtils getNetUtils() {
        //静态单例模式
        return netUtils == null ? netUtils = new NetUtils() : netUtils;
    }

    public void uploadHd(String url, File file, String deviceId) {
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("img", file.getName(), reqBody);
        mIApi.uploadHd(url, part, deviceId, Build.MANUFACTURER)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.d(TAG, "onNext: success" + responseBody.string());
                            Log.d(TAG, "uploadHd: " + System.currentTimeMillis());
                            Log.d(TAG, "uploadHd: " + file.getAbsolutePath());
                        } catch (IOException ignored) {
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
