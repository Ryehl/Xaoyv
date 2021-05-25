package com.xaoyv.mylibrary.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.xaoyv.mylibrary.bean.Constant;
import com.xaoyv.mylibrary.bean.ConstantMMkv;
import com.xaoyv.mylibrary.interfaces.IApi;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
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

/**
 * <p>项目名称:维度科技</p>
 * <p>简述:网络工具类</p>   Model
 *
 * @author Xaoyv
 * date 2020/10/14 16:01
 */
public class NetUtils {
    private static NetUtils netUtils;
    private IApi mIApi;
    private Retrofit retrofit;

    private NetUtils(String baseUrl) {
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
                .baseUrl(baseUrl == null ? Constant.BASE_URL : baseUrl)
                .client(client)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //接口
        mIApi = retrofit.create(IApi.class);
    }

    public static NetUtils getNetUtils() {
        //静态单例模式
        return netUtils == null ? netUtils = new NetUtils("http://192.168.1.51:8080/") : netUtils;
    }

    /**
     * 设置头参
     *
     * @param sessionId sessionId
     * @param userId    userId
     */
    public void setHeader(String sessionId, String userId) {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient build = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(chain ->
                        chain.proceed(chain.request()
                                .newBuilder()
                                .addHeader(ConstantMMkv.Key_SessionId, sessionId)
                                .addHeader(ConstantMMkv.Key_UserId, userId)
                                .build())
                )
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .client(build)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mIApi = retrofit.create(IApi.class);
        Log.d(Constant.TAG, "setHeader: success");
    }

    /**
     * 接口回调
     */
    public interface GetJsonListener {
        void success(String json);

        void error();
    }

    /**
     * get info
     *
     * @param url      url
     * @param map      para
     * @param listener 接口回调
     */
    public void getInfo(String url, HashMap<String, Object> map, GetJsonListener listener) {
        mIApi.getInfo(url, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            listener.success(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.error();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * post info with map
     *
     * @param url      url
     * @param map      para
     * @param listener 接口回调
     */
    public void postInfo(String url, HashMap<String, Object> map, GetJsonListener listener) {
        mIApi.postInfo(url, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            listener.success(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.error();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * post info with body
     *
     * @param url      url
     * @param body     body
     * @param listener 接口回调
     */
    public void postInfoWithBody(String url, RequestBody body, GetJsonListener listener) {
        mIApi.postInfoWithBody(url, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            listener.success(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.error();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * put info with map
     *
     * @param url      url
     * @param map      参数parameters
     * @param listener 接口回调
     */
    public void putInfo(String url, HashMap<String, Object> map, GetJsonListener listener) {
        mIApi.putInfo(url, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            listener.success(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.error();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * put info with body
     *
     * @param url      url
     * @param body     参数parameters
     * @param listener 接口回调
     */
    public void putInfo(String url, RequestBody body, GetJsonListener listener) {
        mIApi.putInfoWithBody(url, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            listener.success(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.error();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * delete info with map
     *
     * @param url      url
     * @param map      para
     * @param listener 接口回调
     */
    public void deleteInfo(String url, HashMap<String, Object> map, GetJsonListener listener) {
        mIApi.deleteInfo(url, map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            listener.success(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.error();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    /**
     * delete info with body
     *
     * @param url      url
     * @param body     body
     * @param listener 接口回调
     */
    public void deleteInfoWithBody(String url, RequestBody body, GetJsonListener listener) {
        mIApi.deleteInfoWithBody(url, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String json = responseBody.string();
                            listener.success(json);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        listener.error();
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void uploadHd(String url, File file) {
        String TAG = "TAG";
        Log.d(TAG, "uploadHd: " + System.currentTimeMillis());
        RequestBody reqBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("img", file.getName(), reqBody);
        mIApi.uploadHd(url, part)
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
