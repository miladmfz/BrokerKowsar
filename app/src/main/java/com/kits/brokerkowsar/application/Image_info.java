package com.kits.brokerkowsar.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class Image_info {


    private final Context mContext;
    CallMethod callMethod;


    public Image_info(Context mContext) {
        this.mContext = mContext;
        callMethod = new CallMethod(mContext);
    }


    public void SaveImage(Bitmap finalBitmap, String code) {

        File dir = new File(Environment.getExternalStorageDirectory() + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/");
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (Exception e) {
        }
        String fname = code + ".jpg";
        File file = new File(dir, fname);
        file.setWritable(true);
        try {
            FileOutputStream out = new FileOutputStream(file, true);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void SaveImage_factor(Bitmap finalBitmap, String code) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/Kowsar/factor_image/");
        myDir.mkdirs();

        String fname = code + ".jpg";
        File file = new File(myDir, fname);
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void DeleteImage(String code) {
        String root = Environment.getExternalStorageDirectory().getAbsolutePath();
        File myDir = new File(root + "/Kowsar/" + callMethod.ReadString("EnglishCompanyNameUse") + "/");
        myDir.mkdirs();

        String fname = code + ".jpg";
        File file = new File(myDir, fname);
        try {
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public Boolean Image_exist(String code) {
        String root = Environment.getExternalStorageDirectory() + "/Kowsar";
        File imagefile = new File(root + "/" + callMethod.ReadString("EnglishCompanyNameUse") + "/" + code + ".jpg");
        return imagefile.exists();

    }


}
