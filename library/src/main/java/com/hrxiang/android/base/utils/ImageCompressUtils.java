package com.hrxiang.android.base.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Build;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by hairui.xiang on 2017/9/14.
 */

public class ImageCompressUtils {
    private static final int IMAGE_MAX_SIZE = 512;// unit k
    private static final int IMAGE_MAX_WIDTH = 480;// 图片最大宽度
    private static final int IMAGE_MAX_HEIGHT = 800;// 图片最大高度

    /**
     * Returns the largest power-of-two divisor for use in downscaling a bitmap
     * that will not result in the scaling past the desired dimensions.
     *
     * @param actualWidth   Actual width of the bitmap
     * @param actualHeight  Actual height of the bitmap
     * @param desiredWidth  Desired width of the bitmap
     * @param desiredHeight Desired height of the bitmap
     */
    // Visible for testing.
    static int findBestSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }

        return (int) n;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        return findBestSampleSize(options.outWidth, options.outHeight, reqWidth, reqHeight);
    }

    /**
     * 等比压缩，质量不受损
     */
    private static Bitmap scaleCompress(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = findBestSampleSize(options.outWidth, options.outHeight, IMAGE_MAX_WIDTH, IMAGE_MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        // return BitmapFactory.decodeFile(filePath, options);
        return rotateTesting(getExifOrientation(filePath), BitmapFactory.decodeFile(filePath, options));
    }

    public static String compressImage(String filePath) {
        File sourceFile = null;
        File outFile = null;
        try {
            sourceFile = new File(filePath);
            if (!sourceFile.exists()) {
                return null;
            }
            //是否小于期望大小
            if (sourceFile.length() / 1024 <= IMAGE_MAX_SIZE) {
                //不做压缩
                return sourceFile.toString();
            }
            outFile = new File(getOutputPath());
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            // 等比压缩，不对影响图片质量
            Bitmap bitmap = scaleCompress(filePath);
            // 质量压缩，影响图片质量
            compressSave(bitmap, outFile, IMAGE_MAX_SIZE);
            if (!bitmap.isRecycled()) {
                bitmap.recycle();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return outFile.toString();
    }


    /**
     * Compress by quality,  and generate image to the path specified
     *
     * @param image
     * @param outFile
     * @param maxSize target will be compressed to be smaller than this size.(kb)
     * @throws IOException
     */
    private static void compressSave(Bitmap image, File outFile, int maxSize) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // scale
        int options = 80;
        // Store the bitmap into output stream(no compress)
        image.compress(Bitmap.CompressFormat.JPEG, options, os);
        // Compress by loop
        while (os.toByteArray().length / 1024 > maxSize) {
            // Clean up os
            os.reset();
            // interval 10
            options -= 10;
            image.compress(Bitmap.CompressFormat.JPEG, options, os);//"质量压缩: " + os.toByteArray().length / 1024 + "KB"
        }

        // Generate compressed image file
        FileOutputStream fos = new FileOutputStream(outFile);
        fos.write(os.toByteArray());
        fos.flush();
        fos.close();
    }

    /**
     * 旋转图片
     *
     * @param degree
     * @param bitmap
     */
    private static Bitmap rotateTesting(int degree, Bitmap bitmap) {
        if (degree == 90 || degree == 180 || degree == 270) {
            // Roate preview icon according to exif orientation
            Matrix matrix = new Matrix();
            matrix.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        }
        return bitmap;
    }

    /**
     * 当前图片角度
     */
    private static int getExifOrientation(String path) {
        int degree = 0;
        try {
            ExifInterface exif = new ExifInterface(path);
            if (exif != null) {
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);
                if (orientation != -1) {
                    // We only recognize a subset of orientation tag values.
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            degree = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            degree = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            degree = 270;
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception ex) {

        }
        return degree;
    }

    private static String getOutputPath() {
        File photosDir = new File(StorageCardUtils.getAppCacheDir(), StorageCardUtils.IMAGE_DIR);
        if (!photosDir.exists()) {
            photosDir.mkdirs();
        }
        String timestamp = TimeUtils.format("yyyy_MM_dd_HH_mm_ss");
        return photosDir.getPath() + File.separator + "IMG_" + timestamp + "_compress" + ".jpg";
    }

    /**
     * 获取bitmap的大小
     */
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {    //API 19
            return bitmap.getAllocationByteCount();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {//API 12
            return bitmap.getByteCount();
        }
        return bitmap.getRowBytes() * bitmap.getHeight();                //earlier version
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        // 首先通过 inJustDecodeBounds=true 获得图片的尺寸
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);
        // 然后根据图片分辨率以及我们实际需要展示的大小，计算压缩率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        // 设置压缩率，并解码
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}
