package com.vforest.yeah.vforest.util;

import android.os.Environment;

import java.io.File;

/**
 * Created by 11923 on 2018/3/26.
 */

public class PicLocation {
    private static String photo_path;

    public static String getPicLocation(){
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)){
            File sdcardDir = Environment.getExternalStorageDirectory();
            photo_path = sdcardDir.getPath() + "/vforest/cache/photos/";
        }
        else
            photo_path= "/storage/emulated/0"+"/vforest/cache/photos/";
        return photo_path;

    }
}
