package com.hrxiang.android.base.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by hairui.xiang on 2017/9/13.
 */

public class FileUtils {

    public static boolean delete(String sPath) {
        boolean flag = false;
        File file = new File(sPath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
                flag = true;
            } else if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; ++i) {
                    delete(files[i].getAbsolutePath());
                }
            }
        }
        return flag;
    }

    public static int createFolder(String filePath) {
        File file = new File(filePath);
        return file.exists() ? 0 : (file.mkdirs() ? 1 : -1);
    }

    public static String readerFile(String filePath) {
        StringBuffer buffer = new StringBuffer();
        BufferedReader in = null;
        try {
            FileInputStream e = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(e, "utf-8");
            in = new BufferedReader(isr);

            int ch;
            while ((ch = in.read()) > -1) {
                buffer.append((char) ch);
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

    public static int writeFile(String path, String content) {
        try {
            File e = new File(path);
            if (e.exists()) {
                e.delete();
            }

            if (e.createNewFile()) {
                FileOutputStream fos = new FileOutputStream(e);
                fos.write(content.getBytes());
                fos.close();
                return 1;
            } else {
                return 0;
            }
        } catch (Exception var4) {
            var4.printStackTrace();
            return 0;
        }
    }

    public static int writeFile(String path, InputStream in) {
        FileOutputStream fos = null;
        try {
            if (in == null) {
                return 0;
            } else {
                File e = new File(path);
                if (e.exists()) {
                    e.delete();
                }

                if (!e.createNewFile()) {
                    return 0;
                } else {
                    fos = new FileOutputStream(e);
                    byte[] buffer = new byte[1024];

                    int count;
                    while ((count = in.read(buffer)) != -1) {
                        fos.write(buffer, 0, count);
                        fos.flush();
                    }

                    fos.close();
                    in.close();
                    return 1;
                }
            }
        } catch (Exception var6) {
            var6.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }

    public static int copyStream(InputStream is, OutputStream os) {
        byte bytes;
        try {
            boolean e = true;
            byte[] bytes1 = new byte[1024];

            while (true) {
                int e1 = is.read(bytes1, 0, 1024);
                if (e1 == -1) {
                    byte e2 = 1;
                    return e2;
                }

                os.write(bytes1, 0, e1);
            }
        } catch (IOException var14) {
            var14.printStackTrace();
            bytes = 0;
        } finally {
            try {
                is.close();
                os.close();
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        return bytes;
    }

    public static Object readerObject(String filePath) {
        try {
            FileInputStream e = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(e);
            Object oo = objectIn.readObject();
            objectIn.close();
            e.close();
            return oo;
        } catch (Exception var4) {
            return null;
        }
    }

    public static int writeObject(String path, Object object) {
        try {
            File e = new File(path);
            if (e.exists()) {
                e.delete();
            }

            if (e.createNewFile()) {
                FileOutputStream utput = new FileOutputStream(e);
                ObjectOutputStream objOut = new ObjectOutputStream(utput);
                objOut.writeObject(object);
                objOut.close();
                utput.close();
                return 1;
            } else {
                return 0;
            }
        } catch (Exception var5) {
            return 0;
        }
    }

    public static void unzip(String rootPath, InputStream fileIn) {
        try {
            File e = new File(rootPath);
            e.mkdir();
            e = new File(rootPath + "resource/");
            e.mkdir();
            ZipInputStream in = new ZipInputStream(new BufferedInputStream(fileIn, 2048));
            ZipEntry entry = null;

            while ((entry = in.getNextEntry()) != null) {
                decompression(entry, rootPath, in);
            }

            in.close();
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private static void decompression(ZipEntry entry, String rootPath, ZipInputStream in) throws Exception {
        File file;
        if (!entry.isDirectory() && -1 != entry.getName().lastIndexOf(".")) {
            file = new File(rootPath + entry.getName());
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file), 2048);

            int b;
            while ((b = in.read()) != -1) {
                bos.write(b);
            }

            bos.close();
        } else {
            file = new File(rootPath + entry.getName().substring(0, entry.getName().length() - 1));
            file.mkdir();
        }

    }

    public static String getExtensionName(String filename) {
        if (filename != null && filename.length() > 0) {
            int dot = filename.lastIndexOf(46);
            if (dot > -1 && dot < filename.length() - 1) {
                return filename.substring(dot + 1);
            }
        }

        return filename;
    }

    /**
     * 读取assets下的txt文件，返回utf-8 String
     *
     * @param fileName
     * @return
     */
    public static String readJsonFromAssetsFile(String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = ContextProvider.getContext().getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File picDir = new File(StorageCardUtils.getAppCacheDir(), StorageCardUtils.IMAGE_DIR);
        if (!picDir.exists()) {
            picDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(picDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
    }
}
