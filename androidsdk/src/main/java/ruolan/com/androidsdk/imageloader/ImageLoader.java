package ruolan.com.androidsdk.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.GifRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


/**
 * @author wuyinlei
 * @function 图片加载类
 */

public class ImageLoader {


    //默认配置
    public static ImageLoadConfig defConfig = new ImageLoadConfig.Builder().
            setCropType(ImageLoadConfig.CENTER_CROP).
            setAsBitmap(true).
//            setPlaceHolderResId(R.drawable.bg_loading).
//            setErrorResId(R.drawable.bg_error).
        setDiskCacheStrategy(ImageLoadConfig.DiskCache.SOURCE).
                    setPrioriy(ImageLoadConfig.LoadPriority.HIGH).build();

    /**
     * 加载String类型的资源
     * SD卡资源："file://"+ Environment.getExternalStorageDirectory().getPath()+"/test.jpg"<p/>
     * assets资源："file:///android_asset/f003.gif"<p/>
     * raw资源："Android.resource://com.frank.glide/raw/raw_1"或"android.resource://com.frank.glide/raw/"+R.raw.raw_1<p/>
     * drawable资源："android.resource://com.frank.glide/drawable/news"或load"android.resource://com.frank.glide/drawable/"+R.drawable.news<p/>
     * ContentProvider资源："content://media/external/images/media/139469"<p/>
     * http资源："http://img.my.csdn.net/uploads/201508/05/1438760757_3588.jpg"<p/>
     * https资源："https://img.alicdn.com/tps/TB1uyhoMpXXXXcLXVXXXXXXXXXX-476-538.jpg_240x5000q50.jpg_.webp"<p/>
     *
     * @param view     目标view
     * @param imageUrl 图片地址
     * @param config   配置信息
     * @param listener 监听器
     */
    public static void loadStringRes(ImageView view, String imageUrl, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, imageUrl, config, listener);
    }

    /**
     * 加载手机图片文件
     *
     * @param view     目标view
     * @param file     图片文件
     * @param config   配置信息
     * @param listener 监听器
     */
    public static void loadFile(ImageView view, File file, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, file, config, listener);
    }

    /**
     * 加载资源文件
     *
     * @param view       目标view
     * @param resourceId 资源id
     * @param config     配置信息
     * @param listener   监听器
     */
    public static void loadResId(ImageView view, Integer resourceId, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, resourceId, config, listener);
    }

    /**
     * 加载URI
     *
     * @param view     目标view
     * @param uri      uri
     * @param config   配置信息
     * @param listener 监听器
     */
    public static void loadUri(ImageView view, Uri uri, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, uri, config, listener);
    }

    /**
     * 简单加载url
     *
     * @param url       图片地址
     * @param imageView 目标view
     */
    public static void load(String url, ImageView imageView) {

        load(imageView.getContext(), imageView, url, null, null);

    }


    /**
     * 加载gif图片
     *
     * @param view     目标view
     * @param gifUrl   gif图片地址
     * @param config   配置
     * @param listener 监听器
     */
    public static void loadGif(ImageView view, String gifUrl, ImageLoadConfig config, LoaderListener listener) {
        load(view.getContext(), view, gifUrl, ImageLoadConfig.parseBuilder(config).setAsGif(true).build(), listener);
    }

    /**
     * @param context  上下文
     * @param objUrl   图片地址
     * @param config   图片加载配置
     * @param listener 监听器
     */
    public static void loadTarget(Context context, Object objUrl, ImageLoadConfig config, final LoaderListener listener) {
        load(context, null, objUrl, config, listener);
    }

    /**
     * 加载图片
     *
     * @param context  上下文
     * @param view     目标view
     * @param objUrl   图片地址
     * @param config   图片加载配置
     * @param listener 监听器
     */
    private static void load(Context context, ImageView view, Object objUrl, ImageLoadConfig config, final LoaderListener listener) {
        if (null == objUrl) {
            throw new IllegalArgumentException("objUrl is null");
        }
        if (null == config) {
            config = defConfig;
        }
        try {
            GenericRequestBuilder builder = null;
            if (config.isAsGif()) {//gif类型
                GifRequestBuilder request = Glide.with(context).load(objUrl).asGif();
                if (config.getCropType() == ImageLoadConfig.CENTER_CROP) {
                    request.centerCrop();
                } else {
                    request.fitCenter();
                }
                builder = request;
            } else if (config.isAsBitmap()) {  //bitmap 类型
                BitmapRequestBuilder request = Glide.with(context).load(objUrl).asBitmap();
                if (config.getCropType() == ImageLoadConfig.CENTER_CROP) {
                    request.centerCrop();
                } else {
                    request.fitCenter();
                }
                //transform bitmap
                if (config.isRoundedCorners()) {  //圆角转换
                    request.transform(new RoundedCornersTransformation(context, 50, 50));
                } else if (config.isCropCircle()) {
                    request.transform(new CropCircleTransformation(context));
                } else if (config.isGrayscale()) {
                    request.transform(new GrayscaleTransformation(context));
                } else if (config.isBlur()) {//图片模糊
                    request.transform(new BlurTransformation(context, 8, 8));
                } else if (config.isRotate()) {
//                    request.transform(new RotateTransformation(context, config.getRotateDegree()));
                }
                builder = request;
            } else if (config.isCrossFade()) { // 渐入渐出动画
                DrawableRequestBuilder request = Glide.with(context).load(objUrl).crossFade();
                if (config.getCropType() == ImageLoadConfig.CENTER_CROP) {
                    request.centerCrop();
                } else {
                    request.fitCenter();
                }
                builder = request;
            }
            //缓存设置
            assert builder != null;
            builder.diskCacheStrategy(config.getDiskCacheStrategy().getStrategy()).
                    skipMemoryCache(config.isSkipMemoryCache()).
                    priority(config.getPrioriy().getPriority());
            builder.dontAnimate();
            if (null != config.getTag()) {
                builder.signature(new StringSignature(config.getTag()));
            } else {
                builder.signature(new StringSignature(objUrl.toString()));
            }
            if (null != config.getAnimator()) {
                builder.animate(config.getAnimator());
            } else if (null != config.getAnimResId()) {
                builder.animate(config.getAnimResId());
            }
            if (config.getThumbnail() > 0.0f) {
                builder.thumbnail(config.getThumbnail());
            }
            if (null != config.getErrorResId()) {
                builder.error(config.getErrorResId());
            }
            if (null != config.getPlaceHolderResId()) {
                builder.placeholder(config.getPlaceHolderResId());
            }
            if (null != config.getSize()) {
                builder.override(config.getSize().getWidth(), config.getSize().getHeight());
            }
            if (null != listener) {
                setListener(builder, listener);
            }
            if (null != config.getThumbnailUrl()) {
                BitmapRequestBuilder thumbnailRequest = Glide.with(context).load(config.getThumbnailUrl()).asBitmap();
                builder.thumbnail(thumbnailRequest).into(view);
            } else {
                setTargetView(builder, config, view);
            }
        } catch (Exception e) {
            view.setImageResource(config.getErrorResId());
        }
    }

    /**
     * 设置监听器
     *
     * @param request  请求
     * @param listener 监听器
     */
    private static void setListener(GenericRequestBuilder request, final LoaderListener listener) {
        request.listener(new RequestListener() {
            @Override
            public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                if (!e.getMessage().equals("divide by zero")) {
                    listener.onError();
                }
                return false;
            }

            @Override
            public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                listener.onSuccess();
                return false;
            }
        });
    }

    /**
     * 设置目标view
     *
     * @param request 请求
     * @param config  配置信息
     * @param view    目标view
     */
    private static void setTargetView(GenericRequestBuilder request, ImageLoadConfig config, ImageView view) {
        //set targetView
        if (null != config.getSimpleTarget()) {
            request.into(config.getSimpleTarget());
        } else if (null != config.getViewTarget()) {
            request.into(config.getViewTarget());
        } else if (null != config.getNotificationTarget()) {
            request.into(config.getNotificationTarget());
        } else if (null != config.getAppWidgetTarget()) {
            request.into(config.getAppWidgetTarget());
        } else {
            request.into(view);
        }
    }

    /**
     * 加载bitmap
     *
     * @param context  上下文
     * @param url      图片地址
     * @param listener 监听器
     */
    public static void loadBitmap(Context context, Object url, final BitmapLoadingListener listener) {
        if (url == null) {
            if (listener != null) {
                listener.onError();
            }
        } else {
            Glide.with(context).
                    load(url).
                    asBitmap().
                    diskCacheStrategy(DiskCacheStrategy.NONE).
                    dontAnimate().
                    into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            if (listener != null) {
                                listener.onSuccess(resource);
                            }
                        }
                    });
        }
    }

    /**
     * 高优先级加载
     *
     * @param url       图片地址
     * @param imageView 目标view
     * @param listener  监听器
     */
    public static void loadImageWithHighPriority(Object url, ImageView imageView, final LoaderListener listener) {
        if (url == null) {
            if (listener != null) {
                listener.onError();
            }
        } else {
            Glide.with(imageView.getContext()).
                    load(url).
                    asBitmap().
                    priority(Priority.HIGH).
                    dontAnimate().
                    listener(new RequestListener<Object, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                            if (null != listener) {
                                listener.onError();
                            }
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            if (null != listener) {
                                listener.onSuccess();
                            }
                            return false;
                        }
                    }).into(imageView);
        }
    }

    /**
     * 取消所有正在下载或等待下载的任务。
     */
    public static void cancelAllTasks(Context context) {
        Glide.with(context).pauseRequests();
    }

    /**
     * 恢复所有任务
     */
    public static void resumeAllTasks(Context context) {
        Glide.with(context).resumeRequests();
    }

    /**
     * 清除磁盘缓存
     *
     * @param context
     */
    public static void clearDiskCache(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        }).start();
    }

    /**
     * 清除所有缓存
     *
     * @param context
     */
    public static void cleanAll(Context context) {
        clearDiskCache(context);
        Glide.get(context).clearMemory();
    }

    /**
     * 获取缓存大小
     *
     * @param context
     * @return
     */
//    public static synchronized long getDiskCacheSize(Context context) {
//        long size = 0L;
//        File cacheDir = PathUtils.getDiskCacheDir(context, CacheConfig.IMG_DIR);
//
//        if (cacheDir != null && cacheDir.exists()) {
//            File[] files = cacheDir.listFiles();
//            if (files != null) {
//                File[] arr$ = files;
//                int len$ = files.length;
//
//                for (int i$ = 0; i$ < len$; ++i$) {
//                    File imageCache = arr$[i$];
//                    if (imageCache.isFile()) {
//                        size += imageCache.length();
//                    }
//                }
//            }
//        }
//
//        return size;
//    }

    /**
     * 清除view
     *
     * @param context 上下文
     * @param uri
     */
    public static void clearTarget(Context context, String uri) {
//        if (SimpleGlideModule.cache != null && uri != null) {
//            SimpleGlideModule.cache.delete(new StringSignature(uri));
//            Glide.get(context).clearMemory();
//        }
    }

    //清除view
    public static void clearTarget(View view) {
        Glide.clear(view);
    }

//    public static File getTarget(Context context, String uri) {
//        return SimpleGlideModule.cache != null && uri != null ? SimpleGlideModule.cache.get(new StringSignature(uri)) : null;
//    }
}