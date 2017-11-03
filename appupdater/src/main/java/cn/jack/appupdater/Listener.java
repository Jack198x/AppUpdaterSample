package cn.jack.appupdater;

/**
 * Created by Jack on 2017/11/3.
 */

public interface Listener {

    void onComplete(String file);

    void onProgress(int percent);

    void onError(String error);

}
