package com.hrxiang.android.base.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.hrxiang.android.base.utils.ucrop.Crop;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;

/**
 * Created by hairui.xiang on 2017/9/6.
 */

public class PhotoHelper {
    private static final String KEY_TAKE_PHOTOS_RESULT = "take_photos_result";
    private static ExecutorService mCompressExecutor = Executors.newFixedThreadPool(3);
    private static Map<String, String> mCompressedMap = Collections.synchronizedMap(new HashMap<String, String>());
    private final static int RC_ACTION_IMAGE_CAPTURE = 4002;//打开相机
    private final static int RC_ACTION_PICK = 4003;//打开相册

    public interface OnPhotoGetListener {
        void onGetPhotoPath(String sourcePath, String processedPath);
    }

    public interface IUCrop {
        void onStartUCrop(@NonNull Uri source, @NonNull File file);
    }

    private static final class CompressThread implements Runnable {
        String sourcePath;
        OnPhotoGetListener l;

        public CompressThread(String sourcePath, OnPhotoGetListener l) {
            this.sourcePath = sourcePath;
            this.l = l;
        }

        @Override
        public void run() {
            try {
                if (null == mCompressedMap.get(sourcePath)) {
                    mCompressedMap.put(sourcePath, ImageCompressUtils.compressImage(sourcePath));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        l.onGetPhotoPath(sourcePath, mCompressedMap.get(sourcePath));
                    }
                });
            }
        }
    }

    public static Crop.Options getDefaultUCropOptions() {
        Crop.Options options = new Crop.Options();
        //设置裁剪图片的保存格式
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);//  JPEG,PNG,WEBP;
        //设置裁剪图片的图片质量
        options.setCompressionQuality(90);//[0-100]
        //设置你想要指定的可操作的手势
        options.setAllowedGestures(UCropActivity.SCALE, UCropActivity.ROTATE, UCropActivity.ALL);
        options.setFreeStyleCropEnabled(true);
//        options.setMaxScaleMultiplier(5);
//        options.setImageToCropBoundsAnimDuration(666);
//        options.setCircleDimmedLayer(true);
//        options.setDimmedLayerColor(Color.DKGRAY);
//        options.setShowCropFrame(false);
//        options.setCropGridStrokeWidth(1);
//        options.setCropGridColor(Color.WHITE);
//        options.setCropGridColumnCount(3);
//        options.setCropGridRowCount(3);
//        options.setHideBottomControls(true);
        return options;
    }

    /**
     * @param ucrop 如果为null返回原图
     * @param l     图片地址回传
     */
    public static void onActivityResult(int requestCode, int resultCode, Intent data, IUCrop ucrop, boolean needCompress, OnPhotoGetListener l) {
        try {
            if (resultCode == RESULT_OK) {
                switch (requestCode) {
                    case RC_ACTION_PICK://data.getData().toString()
                   /* if ("content".equals(data.getScheme())) {
                    } else if ("file".equals(data.getScheme())) {
                    }*/
                        if (null != ucrop) {
                            try {
                                ucrop.onStartUCrop(data.getData(), getOutputPhoto(true));
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (null != l) {// 异常返回原图
                                    String path = queryPathByUri(data.getData());
                                    if (needCompress) {
                                        mCompressExecutor.execute(new CompressThread(path, l));
                                    } else {
                                        l.onGetPhotoPath(path, path);
                                    }
                                }
                            }
                        } else {
                            if (null != l) {
                                String path = queryPathByUri(data.getData());
                                if (needCompress) {
                                    mCompressExecutor.execute(new CompressThread(path, l));
                                } else {
                                    l.onGetPhotoPath(path, path);
                                }
                            }
                        }
                        break;
                    case RC_ACTION_IMAGE_CAPTURE://SharedPreferencesHelper.getObj("take_photos_result", String.class)
                        if (null != ucrop) {
                            try {
                                ucrop.onStartUCrop(getUri(new File(getPhotosPath())), getOutputPhoto(true));
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (null != l) {// 异常返回原图
                                    String path = getPhotosPath();
                                    if (needCompress) {
                                        mCompressExecutor.execute(new CompressThread(path, l));
                                    } else {
                                        l.onGetPhotoPath(path, path);
                                    }
                                }
                            }
                        } else {
                            if (null != l) {
                                String path = getPhotosPath();
                                if (needCompress) {
                                    mCompressExecutor.execute(new CompressThread(path, l));
                                } else {
                                    l.onGetPhotoPath(path, path);
                                }
                            }
                        }
                        break;
                    case Crop.REQUEST_CROP://成功crop
                        if (null != l) {
                            String path = queryPathByUri(Crop.getOutput(data));
                            if (needCompress) {
                                mCompressExecutor.execute(new CompressThread(path, l));
                            } else {
                                l.onGetPhotoPath(path, path);
                            }
                        }
                        break;
                }
            } else if (resultCode == Crop.RESULT_ERROR) {
                final Throwable cropError = Crop.getError(data);
                if (null != cropError) cropError.printStackTrace();
                if (null != l) {
                    String path = queryPathByUri(data.getData());
                    if (needCompress) {
                        mCompressExecutor.execute(new CompressThread(path, l));
                    } else {
                        l.onGetPhotoPath(path, path);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Uri getUri(File file) {
        return FileProviderUtils.getUriForFile(file);
    }

    /**
     * 打开相机
     */
    public static void onTakePhotos(Activity activity) {
        Intent intent = new Intent(ACTION_IMAGE_CAPTURE);
        File file = getOutputPhoto(false);
        if (null == file) {
            //File can not be created!
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(file));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        activity.startActivityForResult(intent, RC_ACTION_IMAGE_CAPTURE);
    }

    public static void onTakePhotos(Fragment fragment) {
        Intent intent = new Intent(ACTION_IMAGE_CAPTURE);
        File file = getOutputPhoto(false);
        if (null == file) {//File can not be created!
            return;
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getUri(file));
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        fragment.startActivityForResult(intent, RC_ACTION_IMAGE_CAPTURE);
    }

    /**
     * 打开相册
     */
    public static void onOpenAlbum(Activity activity) {
     /*   Intent intent = new Intent();
        intent.setType("image*//*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        activity.startActivityForResult(Intent.createChooser(intent, "选择图片"), RC_ACTION_PICK);*/

       /* Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image*//*");
        activity.startActivityForResult(intent, RC_ACTION_PICK);*/

        Intent intent = new Intent(Intent.ACTION_PICK);// 激活系统图库，选择一张图片
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        intent.setType("image/*");
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
        activity.startActivityForResult(intent, RC_ACTION_PICK);// 开启一个带有返回值的Activity，RC_ACTION_PICK
    }

    public static void onOpenAlbum(Fragment fragment) {
        Intent intent = new Intent(Intent.ACTION_PICK);// 激活系统图库，选择一张图片
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//        intent.setType("image/*");
//        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//使用以上这种模式，并添加以上两句
        fragment.startActivityForResult(intent, RC_ACTION_PICK);// 开启一个带有返回值的Activity，RC_ACTION_PICK
    }

    private static File getOutputPhoto(boolean isCrop) {
        File photosDir = new File(StorageCardUtils.getAppCacheDir(), StorageCardUtils.IMAGE_DIR);
        if (!photosDir.exists()) {
            photosDir.mkdirs();
        }
        long timestamp = System.currentTimeMillis();
        File photo;
        String photoPath;
        if (isCrop) {
            photoPath = photosDir.getPath() + File.separator + "IMG_" + timestamp + "_CROP" + ".jpg";
            photo = new File(photoPath);
        } else {
            photoPath = photosDir.getPath() + File.separator + "IMG_" + timestamp + ".jpg";
            photo = new File(photoPath);
        }
        recordPhotosPath(photo);
        return photo;
    }

    private static void recordPhotosPath(File photo) {
        SpHelper.putObj(KEY_TAKE_PHOTOS_RESULT, photo.toString());
    }

    private static String getPhotosPath() {
        return SpHelper.getObj(KEY_TAKE_PHOTOS_RESULT, String.class);
    }

    public static String queryPathByUri(Uri uri) {
        String path = null;
        if ("content".equals(uri.getScheme())) {
            ContentResolver mCr = ContextProvider.getContext().getContentResolver();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                    DocumentsContract.isDocumentUri(ContextProvider.getContext(), uri)) {
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "= ?";
                Cursor mCursor = mCr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id},
                        null);
                if (null != mCursor) {
                    if (mCursor.moveToFirst()) {
                        int _idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                        int image_column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        int _id = mCursor.getInt(_idColumn);
                        String img_path = mCursor.getString(image_column_index);
                        path = img_path;
                    }
                    mCursor.close();
                }

            } else {
                String[] proj = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};
                Cursor mCursor = mCr.query(uri, proj, null, null, null);
                if (null != mCursor) {
                    if (mCursor.moveToFirst()) {
                        int _idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                        int image_column_index = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                        int _id = mCursor.getInt(_idColumn);
                        String img_path = mCursor.getString(image_column_index);
                        path = img_path;
                    }
                    mCursor.close();
                }
            }
        } else if ("file".equals(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }
}
