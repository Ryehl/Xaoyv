package com.xaoyv.mylibrary.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //获取版本号
    public static String createVersion(Context context) {
        String versionCode = "";
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    //获取版本号
    public static int createVersionCode(Context context) {
        int versionCode = 0;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    public static int[] getDisplayMetrics(Context mContext) {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(dm);
        int[] dp = {dm.widthPixels, dm.heightPixels};
        return dp;
    }

    /**
     * 获取屏幕宽和高
     */
    public static String[] getMetrics(Context mContext) {
        DisplayMetrics displayMertrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay()
                .getMetrics(displayMertrics);
        int widthPixels = displayMertrics.widthPixels;
        int heightPixels = displayMertrics.heightPixels;
        float density = displayMertrics.density;
        int densityDpi = displayMertrics.densityDpi;

        return new String[]{widthPixels + "", heightPixels + "",
                density + "", densityDpi + ""};
    }

    // MD5加密
    public static String md5(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            System.out.println("NoSuchAlgorithmException caught!");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }
        return md5StrBuff.toString();
    }

    public static boolean isEmpty(String str) {
        if (str == null || str.length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isBlank(String str) {
        if (str == null || str.length() == 0 || str.trim().length() == 0) {
            return true;
        }
        return false;
    }

    public static boolean isMobilePhone(String str) {
        String regex = "^((1[3,4,5,6,7,8][0-9]{1})+\\d{8})$";
        return match(regex, str);
    }

    public static boolean isPassWord(String str) {
        String regex = "^[a-z][A-Z]|[0-9]*$";
        return match(regex, str);
    }

    public static boolean isValidEmail(String email) {
        String check = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" + "\\@"
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(" + "\\."
                + "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" + ")+";
        Pattern pattern = Pattern.compile(check);
        Matcher matcher = pattern.matcher(email);

        return matcher.matches();
    }

    /**
     * @param regex 正则表达式字符串
     * @param str   要匹配的字符串
     * @return 如果str 符合 regex的正则表达式格式,返回true, 否则返回 false;
     */
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    //获取用户信息
/*
    public static LoginBean.ResultBean getUserInFo() {

        String login = CacheUtils.getCacheUtils().query("login");
        if (TextUtils.isEmpty(login)) {
            return null;
        }
        LoginBean loginBean = new Gson().fromJson(login, LoginBean.class);
        return loginBean.result;
    }

    //退出
    public static void loginOut() {
        CacheUtils.getCacheUtils().delete("login");
    }
*/

    public static void setttingWebView(WebView webView) {
        webView.getSettings().setJavaScriptEnabled(true);//支持javascript
        webView.setWebViewClient(new ArticleWebViewClient());
    }

    private static class ArticleWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            //重置webview中img标签的图片大小
            imgReset(view);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * 对图片进行重置大小，宽度就是手机屏幕宽度，高度根据宽度比便自动缩放
     **/
    private static void imgReset(WebView webView) {
        webView.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName('img'); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "var img = objs[i];   " +
                "    img.style.maxWidth = '100%'; img.style.height = 'auto';  " +
                "}" +
                "})()");
    }


    //渐进式
    public void setDrawView(SimpleDraweeView draweeView, String imageUrl) {
        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(Uri.parse(imageUrl))
                .setProgressiveRenderingEnabled(true)
                .build();
        PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .build();
        draweeView.setController(controller);
    }

    public void setControllerListener(SimpleDraweeView draweeView, String imageUrl, SimControllerListener mSimControllerListener) {
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(Uri.parse(imageUrl))
                .setControllerListener(controllerListener)
                .build();
        draweeView.setController(controller);
    }

    private SimControllerListener mSimControllerListener;

    public interface SimControllerListener {
        void succss();
    }

    ControllerListener controllerListener = new BaseControllerListener() {

        @Override
        public void onIntermediateImageFailed(String id, Throwable throwable) {
            super.onIntermediateImageFailed(id, throwable);
            // 设置图片为渐进式的时候，加载失败，会执行onIntermediateImageFailed方法
        }

        @Override
        public void onFailure(String id, Throwable throwable) {
            super.onFailure(id, throwable);
            // 图片加载失败时候，会执行onFailure方法
        }

        @Override
        public void onSubmit(String id, Object callerContext) {
            super.onSubmit(id, callerContext);
        }

        @Override
        public void onFinalImageSet(String id, Object imageInfo, Animatable animatable) {
            super.onFinalImageSet(id, imageInfo, animatable);
            // 图片加载成功时候会执行onFinalImageSet方法
            mSimControllerListener.succss();
        }
    };


}
