package com.hrxiang.android.base.utils.fresco;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.memory.MemoryTrimmable;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.ProgressiveJpegConfig;
import com.facebook.imagepipeline.image.ImmutableQualityInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import okhttp3.OkHttpClient;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by hairui.xiang on 2017/8/2.
 */

public class ImageConfig {
    private static final String IMAGE_PIPELINE_CACHE_DIR = "fresco";

    private static ImagePipelineConfig sImagePipelineConfig;
    private static ArrayList<MemoryTrimmable> sMemoryTrimmable;

    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();

    public static final int MAX_DISK_CACHE_SIZE = 300 * ByteConstants.MB;
    public static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 10;

    /**
     * Creates config using android http stack as network backend.
     */
    public static ImagePipelineConfig getImagePipelineConfig(Context context) {
        if (sImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = ImagePipelineConfig.newBuilder(context);
            configureCaches(configBuilder, context);
            sImagePipelineConfig = configBuilder.build();
        }
        return sImagePipelineConfig;
    }

    public static ImagePipelineConfig getOkHttpImagePipelineConfig(Context context) {
        sMemoryTrimmable = new ArrayList<>();
        if (sImagePipelineConfig == null) {
            ImagePipelineConfig.Builder configBuilder = OkHttpImagePipelineConfigFactory
                    .newBuilder(context, new OkHttpClient())
                    .setProgressiveJpegConfig(new ProgressiveJpegConfig() {
                        @Override
                        public int getNextScanNumberToDecode(int scanNumber) {
                            return scanNumber + 2;
                        }

                        public QualityInfo getQualityInfo(int scanNumber) {
                            boolean isGoodEnough = (scanNumber >= 5);
                            return ImmutableQualityInfo.of(scanNumber, isGoodEnough, false);
                        }
                    })
                    .setMemoryTrimmableRegistry(new MemoryTrimmableRegistry() {
                        @Override
                        public void registerMemoryTrimmable(MemoryTrimmable trimmable) {
                            sMemoryTrimmable.add(trimmable);
                        }

                        @Override
                        public void unregisterMemoryTrimmable(MemoryTrimmable trimmable) {

                        }
                    })
                    .setBitmapsConfig(Bitmap.Config.RGB_565)
                    .setDownsampleEnabled(true);

            configureCaches(configBuilder, context);
            sImagePipelineConfig = configBuilder.build();
        }
        return sImagePipelineConfig;
    }

    /**
     * Configures disk and memory cache not to exceed common limits
     */
    private static void configureCaches(ImagePipelineConfig.Builder configBuilder, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final MemoryCacheParams bitmapCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in the cache
                Integer.MAX_VALUE,                     // Max entries in the cache
                MAX_MEMORY_CACHE_SIZE, // Max total size of elements in eviction queue
                Integer.MAX_VALUE,                     // Max length of eviction queue
                Integer.MAX_VALUE);                    // Max cache entry size
        configBuilder
                .setBitmapMemoryCacheParamsSupplier(
                        new BitmapMemoryCacheParamsSupplier(manager) {
                            public MemoryCacheParams get() {
                                return bitmapCacheParams;
                            }
                        })
                .setMainDiskCacheConfig(DiskCacheConfig.newBuilder(context)
                        .setBaseDirectoryPath(getExternalCacheDir(context))
                        .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR)
                        .setMaxCacheSize(MAX_DISK_CACHE_SIZE)
                        .build());
    }

    public static File getExternalCacheDir(final Context context) {
        if (hasExternalCacheDir())
            return context.getExternalCacheDir();

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return createFile(Environment.getExternalStorageDirectory().getPath() + cacheDir, "");
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static File createFile(String folderPath, String fileName) {
        File destDir = new File(folderPath);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        return new File(folderPath, fileName);
    }

    // http://blog.csdn.net/brycegao321/article/details/52440640
    public static void trimMemory() {
        try {
            if (sMemoryTrimmable != null) {
                for (MemoryTrimmable trimmable : sMemoryTrimmable) {
                    //just demo, it should be proper params according to level.
                    trimmable.trim(MemoryTrimType.OnSystemLowMemoryWhileAppInBackground);
                }
            }
            sMemoryTrimmable.clear();
        } catch (Exception e) {

        }

    }

    //清除Fresco中的缓存
    public static void clearMemoryCaches() {
        try {
            ImagePipeline imagePipeline = Fresco.getImagePipeline();
            //清空内存缓存（包括Bitmap缓存和未解码图片的缓存）
            //防止imagePipeline 为空
            if (imagePipeline != null)
                imagePipeline.clearMemoryCaches();
        } catch (Exception e) {

        }
    }
}
