package com.staysobercora.app;

import android.Manifest;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {
 private WebView webView;
 private static final String HOME="https://coradicker95.netlify.app/";
 private static final String APP_CLEANUP =
   "(function(){"+
   "var b=document.getElementById('pwaInstallBtn');if(b)b.remove();"+
   "var m=document.getElementById('pwaInstallMsg');if(m)m.remove();"+
   "var s=document.getElementById('pwa-install-style');if(s)s.remove();"+
   "})();";

 @Override protected void onCreate(Bundle state){
  super.onCreate(state);
  setContentView(R.layout.activity_main);

  NotificationScheduler.createChannel(this);
  requestNotificationPermission();
  requestExactAlarmPermissionIfNeeded();
  NotificationScheduler.scheduleAll(this);

  webView=findViewById(R.id.webview);
  WebSettings s=webView.getSettings();
  s.setJavaScriptEnabled(true);
  s.setDomStorageEnabled(true);
  s.setDatabaseEnabled(true);
  s.setAllowFileAccess(false);
  s.setAllowContentAccess(false);

  webView.setWebViewClient(new WebViewClient(){
   @Override public void onPageFinished(WebView view,String url){
    super.onPageFinished(view,url);
    view.evaluateJavascript(APP_CLEANUP,null);
   }
   @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){
    Uri u=r.getUrl();
    String scheme=u.getScheme()==null?"":u.getScheme();
    if("tel".equals(scheme)){ startActivity(new Intent(Intent.ACTION_DIAL,u)); return true; }
    if("sms".equals(scheme)||"mailto".equals(scheme)){ startActivity(new Intent(Intent.ACTION_VIEW,u)); return true; }
    String host=u.getHost()==null?"":u.getHost();
    if("coradicker95.netlify.app".equals(host)) return false;
    startActivity(new Intent(Intent.ACTION_VIEW,u)); return true;
   }
  });

  if(state==null) webView.loadUrl(HOME); else webView.restoreState(state);
  getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){
   @Override public void handleOnBackPressed(){
    if(webView.canGoBack()) webView.goBack(); else finish();
   }
  });
 }

 private void requestNotificationPermission(){
  if(Build.VERSION.SDK_INT>=33 &&
     ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED){
    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},1001);
  }
 }

 private void requestExactAlarmPermissionIfNeeded(){
  if(Build.VERSION.SDK_INT>=31){
   AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
   if(!am.canScheduleExactAlarms()){
    try{
     Intent i=new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
       Uri.parse("package:"+getPackageName()));
     startActivity(i);
    }catch(Exception ignored){}
   }
  }
 }

 @Override protected void onResume(){
  super.onResume();
  NotificationScheduler.scheduleAll(this);
 }

 @Override protected void onSaveInstanceState(Bundle out){
  webView.saveState(out);
  super.onSaveInstanceState(out);
 }
}
