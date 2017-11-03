package cn.jack.appupdater;

import android.os.Handler;
import android.os.Looper;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

import static java.net.HttpURLConnection.HTTP_OK;

/**
 * Created by Jack on 2017/11/3.
 */

public class DownloadUtil {

    private String downloadUrl;
    private File destinationFile;
    private Listener listener;
    private int connectTimeout = 5000;
    private int readTimeout = 5000;
    private long contentLength;

    public DownloadUtil(String downloadUrl, File destinationFile) {
        this.downloadUrl = downloadUrl;
        this.destinationFile = destinationFile;
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public void downloadInBackground() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                download();
            }
        }).start();
    }

    private void download() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(downloadUrl);
            conn = (HttpURLConnection) url.openConnection();
            conn.setInstanceFollowRedirects(false);
            conn.setConnectTimeout(connectTimeout);
            conn.setReadTimeout(readTimeout);
            conn.connect();
            final int responseCode = conn.getResponseCode();
            if (responseCode == HTTP_OK) {
                if (readResponseHeaders(conn) == 1) {
                    transferData(conn);
                } else {
                    onError("无法获取下载文件大小");
                }
            } else {
                onError("下载失败，httpCode:" + responseCode);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            onError(e.toString());
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            onError(e.toString());
        } catch (ConnectTimeoutException e) {
            e.printStackTrace();
            onError(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            onError(e.toString());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private int readResponseHeaders(HttpURLConnection conn) {
        final String transferEncoding = conn.getHeaderField("Transfer-Encoding");
        if (transferEncoding == null) {
            contentLength = conn.getContentLength();
        } else {
            contentLength = -1;
        }
        if (contentLength == -1 && (transferEncoding == null || !transferEncoding.equalsIgnoreCase("chunked"))) {
            return -1;
        } else {
            return 1;
        }
    }


    private void transferData(URLConnection conn) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            int BUFFER_SIZE = 4096;
            final byte data[] = new byte[BUFFER_SIZE];
            inputStream = conn.getInputStream();
            outputStream = new FileOutputStream(destinationFile, true);
            long totalWritten = 0;
            int bytesRead;
            int lastProgress = 0;
            while (true) {
                bytesRead = inputStream.read(data);
                totalWritten += bytesRead;
                int progress = (int) ((totalWritten * 100) / contentLength);
                if (progress != lastProgress) {
                    onProgress(progress);
                    lastProgress = progress;
                }
                if (bytesRead == -1) {
                    onComplete(destinationFile.toString());
                    return;
                }
                outputStream.write(data, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(e.toString());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                onError(e.toString());
            }
        }
    }

    private void onComplete(final String file) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onComplete(file);
                }
            }
        });
    }

    private void onProgress(final int percent) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onProgress(percent);
                }
            }
        });
    }

    private void onError(final String error) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.onError(error);
                }
            }
        });
    }
}
