# AppUpdater

AppUpdater is a smart util to help update your app

## Using AppUpdater in your application

If you are building with Gradle, simply add the following line to the `dependencies` section of your `build.gradle` file:

```groovy
implementation 'cn.jack:appupdater:1.0.0'
```

then use it in java

```java
//do not forget request READ_EXTERNAL_STORAGE，Manifest.permission.WRITE_EXTERNAL_STORAGE
AppUpdater.with(context)         
  .setDownloadUrl(downloadUrl)      
  .setVersionCode(versionCode)//the new apk versionCode
  .setVersionName(versionName)//the new apk versionName
  .setListener(new Listener() {                
    @Override             
    public void onComplete(String file) {         
    }           
    @Override             
    public void onProgress(int percent) {
      
    }
    @Override          
    public void onError(String error) {
                    
    }           
  })   
  .start();
```

the apk will download to `sdcard/Download/yourpackagename.apk`,if there already have a save verison apk,AppUpdater will call `onComplete(String file)` immediately 

do not forget

`Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE`
