package cn.jack.appupdater;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;

/**
 * Created by Jack on 2017/11/2.
 */

public class AppUpdater {

    private Context context;
    private String versionName;
    private int versionCode = -1;
    private String downloadUrl;
    private Listener listener;
    private int connectTimeout = 5000;
    private int readTimeout = 5000;
    private File downloadPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private File apkFile;

    private AppUpdater(Context context) {
        this.context = context;
        String apkName = context.getPackageName() + ".apk";
        apkFile = new File(downloadPath + "/" + apkName);
    }

    public static AppUpdater with(Context context) {
        return new AppUpdater(context);
    }

    public AppUpdater setVersionName(String versionName) {
        this.versionName = versionName;
        return this;
    }

    public AppUpdater setVersionCode(int versionCode) {
        if (versionCode > 0) {
            this.versionCode = versionCode;
        }
        return this;
    }


    public AppUpdater setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
        return this;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public AppUpdater setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    public void start() {
        if (!isPermissionGranted()) {
            listener.onError("请检查文件读写权限");
            return;
        }
        if (!isFileExists()) {
            listener.onError("下载路径创建失败,请检查文件读写权限");
            return;
        }
        startDownload();
    }


    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        } else {
            return true;
        }
    }

    private void startDownload() {
        checkFile();
    }

    private void checkFile() {
        if (apkFile.exists()) {
            PackageInfo packageInfo = ApkUtil.getApkPackageInfo(context, apkFile.toString());
            boolean isSameVersionName;
            boolean isSameVersionCode;
            isSameVersionName = !TextUtils.isEmpty(versionName) && packageInfo.versionName.equals(versionName);
            isSameVersionCode = versionCode > 0 && packageInfo.versionCode == versionCode;
            if (isSameVersionName && isSameVersionCode) {
                listener.onComplete(apkFile.toString());
            } else {
                deleteFile(apkFile);
            }
        } else {
            download();
        }
    }

    private void deleteFile(File file) {
        boolean deleteSuccess = file.delete();
        if (deleteSuccess) {
            download();
        } else {
            listener.onError("旧版本安装包删除失败，请检查文件权限");
        }
    }


    private boolean isFileExists() {
        boolean isFileExists;
        isFileExists = downloadPath.exists() || downloadPath.mkdirs();
        return isFileExists;
    }

    private void download() {
        DownloadUtil downloadUtil = new DownloadUtil(downloadUrl, apkFile);
        downloadUtil.setConnectTimeout(connectTimeout);
        downloadUtil.setReadTimeout(readTimeout);
        downloadUtil.setListener(listener);
        downloadUtil.downloadInBackground();
    }
}
