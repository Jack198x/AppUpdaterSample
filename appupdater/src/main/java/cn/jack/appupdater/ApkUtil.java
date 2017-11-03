package cn.jack.appupdater;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by Jack on 2017/11/2.
 */

public class ApkUtil {


    /**
     * GetApkPackageInfo
     *
     * @param context
     * @param apkFilePath
     * @return
     */
    public static PackageInfo getApkPackageInfo(Context context, String apkFilePath) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getPackageArchiveInfo(apkFilePath, PackageManager.GET_ACTIVITIES);
    }

}
