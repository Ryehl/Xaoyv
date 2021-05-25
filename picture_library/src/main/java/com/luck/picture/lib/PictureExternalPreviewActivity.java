package com.luck.picture.lib;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.billy.android.swipe.SmartSwipe;
import com.billy.android.swipe.consumer.ActivitySlidingBackConsumer;
import com.billy.android.swipe.listener.SwipeBackListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.dialog.CustomDialog;
import com.luck.picture.lib.dialog.PictureSpinView;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.permissions.RxPermissions;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.tools.ImageUtil;
import com.luck.picture.lib.tools.PictureFileUtils;
import com.luck.picture.lib.tools.ScreenUtils;
import com.luck.picture.lib.tools.ToastManage;
import com.luck.picture.lib.widget.PreviewViewPager;
import com.luck.picture.lib.widget.longimage.ImageSource;
import com.luck.picture.lib.widget.longimage.ImageViewState;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * author：luck
 * project：PictureSelector
 * package：com.luck.picture.ui
 * email：邮箱->893855882@qq.com
 * data：17/01/18
 */
public class PictureExternalPreviewActivity extends PictureBaseActivity implements View.OnClickListener {
    private LinearLayout mLlRoot;
    private LinearLayout mLlContent;
    private TextView status_view;
    private ImageButton left_back;
    private TextView tv_title;
    private TextView tv_Save;
    private PreviewViewPager viewPager;
    private List<LocalMedia> imageList = new ArrayList<>();
    private int position = 0;
    private String directory_path;
    private SimpleFragmentAdapter adapter;
    private LayoutInflater inflater;
    private RxPermissions rxPermissions;
    private loadDataThread loadDataThread;
    private boolean needNotDownload;
    private String savepath;
//    private RelativeLayout.LayoutParams vpLayoutParams;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_ImagePreview);
        setContentView(R.layout.picture_activity_external_preview);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        mLlRoot = (LinearLayout) findViewById(R.id.ll_root);
        mLlContent = (LinearLayout) findViewById(R.id.ll_content);
//        vpLayoutParams = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();
        //activity侧滑返回
        SmartSwipe.wrap(mLlContent)
                .addConsumer(new ActivitySlidingBackConsumer(this))
                //设置联动系数
                .setRelativeMoveFactor(0F)
                .setSwipeListener(new SwipeBackListener() {
                    @Override
                    public void onSwipeStateChanged(int state) {
                        if (state == 1) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (status_view.getVisibility() == View.VISIBLE) {
                                    status_view.setVisibility(View.GONE);
                                }
                            }
                            tv_title.setVisibility(View.GONE);
                            tv_Save.setVisibility(View.GONE);
//                            mLlRoot.setBackgroundColor(Color.TRANSPARENT);
                        } else if (state == 0) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (status_view.getVisibility() == View.GONE) {
                                    status_view.setVisibility(View.VISIBLE);
                                }
                            }
                            tv_title.setVisibility(View.VISIBLE);
                            tv_Save.setVisibility(View.VISIBLE);
//                            mLlRoot.setBackgroundColor(Color.BLACK);
                        }
                        Log.e("onSwipeStateChanged", "state" + state);
                        //1开始，0结束
                    }

                    @Override
                    public void onSwipeStart(int direction) {
                        Log.e("onSwipeStart", "direction" + direction);

                    }

                    @Override
                    public void onSwipeProcess(int direction, boolean settling, float progress) {
                        Log.e("onSwipeProcess", "progress" + progress);
                        if (progress > 1) {
                            progress = 0;
                        }
                        //0开始，1结束
                        float alpha = (255f * (1 - progress));
                        Log.e("alpha", "progress" + progress);
//                        viewPager.setAlpha(1 - progress);
                        mLlRoot.setAlpha(1 - progress);
                        viewPager.setScaleX(1 - progress);
                        viewPager.setScaleY(1 - progress);
//                        mLlRoot.setBackgroundColor(Color.argb((int) alpha, 0, 0, 0));
                    }

                    @Override
                    public void onSwipeRelease(int direction, float progress, float xVelocity, float yVelocity) {
                        Log.e("onSwipeRelease", "progress" + progress);

                    }
                })
                //指定可侧滑返回的方向，如：enableLeft() 仅左侧可侧滑返回
                .enableTop()
        ;
        inflater = LayoutInflater.from(this);
        status_view = (TextView) findViewById(R.id.tv_bar);
        tv_title = (TextView) findViewById(R.id.picture_title);
        left_back = (ImageButton) findViewById(R.id.left_back);
        viewPager = (PreviewViewPager) findViewById(R.id.preview_pager);
        tv_Save = (TextView) findViewById(R.id.tv_save);
        position = getIntent().getIntExtra(PictureConfig.EXTRA_POSITION, 0);
        directory_path = getIntent().getStringExtra(PictureConfig.DIRECTORY_PATH);
        needNotDownload = getIntent().getBooleanExtra(PictureConfig.NEEDNOT_DOWNLOAD, false);
        imageList = (List<LocalMedia>) getIntent().getSerializableExtra(PictureConfig.EXTRA_PREVIEW_SELECT_LIST);

        left_back.setOnClickListener(this);
        status_view.setHeight(ScreenUtils.getStatusBarHeight1(this));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            status_view.setVisibility(View.VISIBLE);
        }
        if (needNotDownload) {
            tv_Save.setVisibility(View.GONE);
        }
        initViewPageAdapterData();
    }

    @Override
    public boolean isImmersive() {
        return false;
    }

    private void initViewPageAdapterData() {
        tv_title.setText(position + 1 + "/" + imageList.size());
        adapter = new SimpleFragmentAdapter(imageList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        savepath = imageList.get(position).getPath();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                savepath = imageList.get(position).getPath();
                tv_title.setText(position + 1 + "/" + adapter.getCount());
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        finish();
        overridePendingTransition(0, R.anim.a3);
    }

    public class SimpleFragmentAdapter extends PagerAdapter {
        List<LocalMedia> images = new ArrayList<>();

        public SimpleFragmentAdapter(List<LocalMedia> images) {
            this.images = images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            (container).removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View contentView = inflater.inflate(R.layout.picture_image_preview, container, false);
            // 常规图控件
            final PhotoView imageView = (PhotoView) contentView.findViewById(R.id.preview_image);
            // 长图控件
            final SubsamplingScaleImageView longImg = (SubsamplingScaleImageView) contentView.findViewById(R.id.longImg);
            // 加载中loading
            final PictureSpinView loadingView = contentView.findViewById(R.id.iv_loading);

            LocalMedia media;
            media = images.get(position);
            if (media != null) {
                final String pictureType = media.getPictureType();
                final String path;
                if (media.isCut() && !media.isCompressed()) {
                    // 裁剪过
                    path = media.getCutPath();
                } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                    // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                    path = media.getCompressPath();
                } else {
                    path = media.getPath();
                }
                boolean isHttp = PictureMimeType.isHttp(path);
                // 可以长按保存并且是网络图片显示一个对话框
                if (isHttp) {
                    loadingView.setVisibility(View.VISIBLE);
//                    showPleaseDialog();
                }
                boolean isGif = PictureMimeType.isGif(pictureType);
                if (!isGif && path.toLowerCase().endsWith(".gif")) {
                    isGif = true;
                }
                final boolean eqLongImg = PictureMimeType.isLongImg(media);
                imageView.setVisibility(eqLongImg && !isGif ? View.GONE : View.VISIBLE);
                longImg.setVisibility(eqLongImg && !isGif ? View.VISIBLE : View.GONE);
                // 压缩过的gif就不是gif了
                if (isGif) {
                    RequestOptions gifOptions = new RequestOptions()
                            .override(480, 800)
                            .priority(Priority.HIGH)
                            .diskCacheStrategy(DiskCacheStrategy.NONE);
                    Glide.with(PictureExternalPreviewActivity.this)
                            .asGif()
                            .apply(gifOptions)
                            .load(path)
                            .listener(new RequestListener<GifDrawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model
                                        , Target<GifDrawable> target, boolean isFirstResource) {
//                                    dismissDialog();
                                    loadingView.setVisibility(View.INVISIBLE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(GifDrawable resource, Object model
                                        , Target<GifDrawable> target, DataSource dataSource,
                                                               boolean isFirstResource) {
//                                    dismissDialog();
                                    loadingView.setVisibility(View.INVISIBLE);
                                    return false;
                                }
                            })
                            .into(imageView);
                } else {
                    if (isHttp) {
                        Glide.with(PictureExternalPreviewActivity.this).downloadOnly().load(path).listener(new RequestListener<File>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<File> target, boolean isFirstResource) {
//                                dismissDialog();
                                loadingView.setVisibility(View.INVISIBLE);
                                return true;
                            }

                            @Override
                            public boolean onResourceReady(File resource, Object model, Target<File> target,
                                                           DataSource dataSource, boolean isFirstResource) {
                                //                                    dismissDialog();
                                boolean isLongImage = ImageUtil.isLongImage(PictureExternalPreviewActivity.this, resource.getAbsolutePath());
                                imageView.setVisibility(isLongImage ? View.GONE : View.VISIBLE);
                                longImg.setVisibility(isLongImage ? View.VISIBLE : View.GONE);
                                if (isLongImage) {
                                    loadingView.setVisibility(View.INVISIBLE);
                                    displayLongPic(resource.getAbsolutePath(), longImg);
                                } else {
                                    RequestOptions options = new RequestOptions()
                                            .diskCacheStrategy(DiskCacheStrategy.ALL);
                                    Glide.with(PictureExternalPreviewActivity.this)
                                            .asBitmap()
                                            .load(resource.getAbsolutePath())
                                            .apply(options)
                                            .into(new SimpleTarget<Bitmap>(480, 800) {
                                                @Override
                                                public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                                    super.onLoadFailed(errorDrawable);
//                                    dismissDialog();
                                                    loadingView.setVisibility(View.INVISIBLE);
                                                }

                                                @Override
                                                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                                    dismissDialog();
                                                    loadingView.setVisibility(View.INVISIBLE);
                                                    imageView.setImageBitmap(resource);
                                                }
                                            });
                                }
                                return true;
                            }
                        }).into(new FileTarget() {
                            @Override
                            public void onLoadStarted(@Nullable Drawable placeholder) {
                                super.onLoadStarted(placeholder);
                            }
                        });
                    } else {
                        if (eqLongImg) {
                            loadingView.setVisibility(View.INVISIBLE);
                            displayLongPic(path, longImg);
                        } else {
                            RequestOptions options = new RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL);
                            Glide.with(PictureExternalPreviewActivity.this)
                                    .asBitmap()
                                    .load(path)
                                    .apply(options)
                                    .into(new SimpleTarget<Bitmap>(480, 800) {
                                        @Override
                                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                                            super.onLoadFailed(errorDrawable);
//                                    dismissDialog();
                                            loadingView.setVisibility(View.INVISIBLE);
                                        }

                                        @Override
                                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
//                                    dismissDialog();
                                            loadingView.setVisibility(View.INVISIBLE);
                                            imageView.setImageBitmap(resource);
                                        }
                                    });
                        }
                    }
                }
                imageView.setOnViewTapListener(new OnViewTapListener() {
                    @Override
                    public void onViewTap(View view, float x, float y) {
                        finish();
                        overridePendingTransition(0, R.anim.a3);
                    }
                });
                longImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        overridePendingTransition(0, R.anim.a3);
                    }
                });
                tv_Save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rxPermissions == null) {
                            rxPermissions = new RxPermissions(PictureExternalPreviewActivity.this);
                        }
                        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                .subscribe(new Observer<Boolean>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {
                                    }

                                    @Override
                                    public void onNext(Boolean aBoolean) {
                                        if (aBoolean) {
                                            showDownLoadDialog(savepath);
                                        } else {
                                            ToastManage.s(mContext, getString(R.string.picture_jurisdiction));
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
                });
            }
            (container).addView(contentView, 0);
            return contentView;
        }
    }

    /**
     * 加载长图
     *
     * @param bmp
     * @param longImg
     */
    private void displayLongPic(Bitmap bmp, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
        longImg.setImage(ImageSource.cachedBitmap(bmp), new ImageViewState(0, new PointF(0, 0), 0));
    }

    /**
     * 加载长图
     *
     * @param longImg
     */
    private void displayLongPic(String path, SubsamplingScaleImageView longImg) {
        longImg.setQuickScaleEnabled(true);
        longImg.setZoomEnabled(true);
        longImg.setPanEnabled(true);
        longImg.setDoubleTapZoomDuration(100);
        longImg.setMinScale(ImageUtil.getLongImageMinScale(PictureExternalPreviewActivity.this, path));
        longImg.setMaxScale(ImageUtil.getLongImageMaxScale(PictureExternalPreviewActivity.this, path));
        longImg.setDoubleTapZoomScale(ImageUtil.getLongImageMaxScale(PictureExternalPreviewActivity.this, path));
        longImg.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP);
        longImg.setImage(ImageSource.uri(path), new ImageViewState(0, new PointF(0, 0), 0));
    }

    /**
     * 下载图片提示
     */
    private void showDownLoadDialog(final String path) {
        final CustomDialog dialog = new CustomDialog(PictureExternalPreviewActivity.this,
                ScreenUtils.getScreenWidth(PictureExternalPreviewActivity.this) * 3 / 4,
                ScreenUtils.getScreenHeight(PictureExternalPreviewActivity.this) / 4,
                R.layout.picture_wind_base_dialog_xml, R.style.Theme_dialog);
        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btn_commit = (Button) dialog.findViewById(R.id.btn_commit);
        TextView tv_title = (TextView) dialog.findViewById(R.id.tv_title);
        TextView tv_content = (TextView) dialog.findViewById(R.id.tv_content);
        tv_title.setText(getString(R.string.picture_prompt));
        tv_content.setText(getString(R.string.picture_prompt_content));
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPleaseDialog();
                boolean isHttp = PictureMimeType.isHttp(path);
                if (isHttp) {
                    loadDataThread = new loadDataThread(path);
                    loadDataThread.start();
                } else {
                    // 有可能本地图片
                    try {
                        String dirPath = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                                getUrlFileName(path), directory_path);
                        PictureFileUtils.copyFile(path, dirPath);
                        ToastManage.s(mContext, getString(R.string.picture_save_success) + "\n" + dirPath);
                        new SingleMediaScanner(mContext, dirPath, new SingleMediaScanner.ScanListener() {
                            @Override
                            public void onScanFinish() {
                                // scanning...
                            }
                        });
                        dismissDialog();
                    } catch (IOException e) {
                        ToastManage.s(mContext, getString(R.string.picture_save_error) + "\n" + e.getMessage());
                        dismissDialog();
                        e.printStackTrace();
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    // 进度条线程
    public class loadDataThread extends Thread {
        private String path;

        public loadDataThread(String path) {
            super();
            this.path = path;
        }

        @Override
        public void run() {
            try {
                showLoadingImage(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 下载图片保存至手机
    public void showLoadingImage(String urlPath) {
        try {
            URL u = new URL(urlPath);
            String path = PictureFileUtils.createDir(PictureExternalPreviewActivity.this,
                    getUrlFileName(urlPath), directory_path);
            byte[] buffer = new byte[1024 * 8];
            int read;
            int ava = 0;
            long start = System.currentTimeMillis();
            BufferedInputStream bin;
            bin = new BufferedInputStream(u.openStream());
            BufferedOutputStream bout = new BufferedOutputStream(
                    new FileOutputStream(path));
            while ((read = bin.read(buffer)) > -1) {
                bout.write(buffer, 0, read);
                ava += read;
                long speed = ava / (System.currentTimeMillis() - start);
            }
            bout.flush();
            bout.close();
            Message message = handler.obtainMessage();
            message.what = 200;
            message.obj = path;
            handler.sendMessage(message);
        } catch (IOException e) {
            ToastManage.s(mContext, getString(R.string.picture_save_error) + "\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    public static String getUrlFileName(String url) {
        String fileName = null;
        int index = url.lastIndexOf(File.separator);
        if (index != -1) {
            fileName = url.substring(index + 1);
        } else {
            fileName = url;
        }
        return fileName;
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 200:
                    String path = (String) msg.obj;
                    ToastManage.s(mContext, getString(R.string.picture_save_success) + "\n" + path);
                    new SingleMediaScanner(mContext, path, new SingleMediaScanner.ScanListener() {
                        @Override
                        public void onScanFinish() {
                            // scanning...
                        }
                    });
                    dismissDialog();
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0, R.anim.a3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadDataThread != null) {
            handler.removeCallbacks(loadDataThread);
            loadDataThread = null;
        }
    }
}
