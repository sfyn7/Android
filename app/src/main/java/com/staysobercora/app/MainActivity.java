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
 private static final String REWARD_RECIPIENT="https://www.facebook.com/profile.php?id=61581512888435";
 private static final String REMOTE="https://coradicker95.netlify.app/";
 private static final String REMOTE_DECOR =
 "(function(){"+
 "var old=document.getElementById('cora-native-head');if(old)old.remove();"+
 "['pwaInstallBtn','pwaInstallMsg','pwa-install-style'].forEach(function(i){var e=document.getElementById(i);if(e)e.remove();});"+
 "var names=['Cora','Amomiyoka','Baby Polar Bear','Mermaid','Lovely Flower','Nunatsiavut Woman','Baby Natuashish Girl'];"+
 "var d=new Date(),n=names[Math.floor(d.getTime()/86400000)%names.length];"+
 "var pics=['day1.jpeg','day2.jpeg','day3.jpeg','day4.jpeg','day5.jpeg','day6.jpeg','day7.jpeg'];"+
 "var p=pics[Math.floor(d.getTime()/86400000)%pics.length];"+
 "var h=document.createElement('div');h.id='cora-native-head';"+
 "h.style='position:relative;z-index:9998;padding:24px 16px 20px;text-align:center;color:white;background:linear-gradient(180deg,rgba(3,8,22,.25),rgba(3,8,22,.92)),url(https://coradicker95.netlify.app/assets/'+p+') center/cover;box-shadow:0 4px 24px #0008';"+
 "h.innerHTML='<div style=\"font-size:13px;letter-spacing:2px;color:#ffd34e\">⭐ STAY SOBER CORA ⭐</div><div style=\"font-size:27px;font-weight:800;margin-top:6px\">Keep Going Sober</div><div style=\"font-size:23px;color:#ffd34e;font-weight:700\">'+n+'</div>';"+
 "document.body.insertBefore(h,document.body.firstChild);"+
 "})();";

 @Override protected void onCreate(Bundle state){
  super.onCreate(state); setContentView(R.layout.activity_main);
  NotificationScheduler.createChannel(this);
  requestNotificationPermission();
  requestExactAlarmPermissionIfNeeded();
  NotificationScheduler.scheduleAll(this);

  webView=findViewById(R.id.webview);
  WebSettings s=webView.getSettings();
  s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setDatabaseEnabled(true);
  s.setAllowFileAccess(true); s.setAllowContentAccess(false);
  webView.setWebViewClient(new WebViewClient(){
   @Override public void onPageFinished(WebView v,String url){
    super.onPageFinished(v,url);
    // The web app supplies its own header and pages; avoid duplicate native overlays.
   }
   @Override public boolean shouldOverrideUrlLoading(WebView v,WebResourceRequest r){
    Uri u=r.getUrl(); String scheme=u.getScheme()==null?"":u.getScheme();
    if("cora".equals(scheme)&&"claim".equals(u.getHost())){ shareReward(); return true; }
    if("cora".equals(scheme)&&"home".equals(u.getHost())){ v.loadUrl(HOME); return true; }
    if("tel".equals(scheme)){ startActivity(new Intent(Intent.ACTION_DIAL,u)); return true; }
    if("sms".equals(scheme)||"mailto".equals(scheme)){ startActivity(new Intent(Intent.ACTION_VIEW,u)); return true; }
    String host=u.getHost()==null?"":u.getHost();
    if("coradicker95.netlify.app".equals(host)) return false;
    if("file".equals(scheme)) return false;
    startActivity(new Intent(Intent.ACTION_VIEW,u)); return true;
   }
  });
  if(state==null) webView.loadUrl(HOME); else webView.restoreState(state);
  getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){
   @Override public void handleOnBackPressed(){
    if(webView.canGoBack()) webView.goBack();
    else finish();
   }
  });
 }

 private void shareReward(){
  Intent send=new Intent(Intent.ACTION_SEND);
  send.setType("text/plain");
  send.putExtra(Intent.EXTRA_TEXT,"⭐ Stay Sober Cora reward request for Yan Drakarys ("+REWARD_RECIPIENT+"): I completed a sober week and earned my $25 reward. Please process my reward. Thank you 💛");
  Intent chooser=Intent.createChooser(send,"Send reward request to Yan Drakarys");
  startActivity(chooser);
 }

 private void requestNotificationPermission(){
  if(Build.VERSION.SDK_INT>=33 && ActivityCompat.checkSelfPermission(this,Manifest.permission.POST_NOTIFICATIONS)!=PackageManager.PERMISSION_GRANTED)
   ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.POST_NOTIFICATIONS},1001);
 }
 private void requestExactAlarmPermissionIfNeeded(){
  if(Build.VERSION.SDK_INT>=31){
   AlarmManager am=(AlarmManager)getSystemService(ALARM_SERVICE);
   if(!am.canScheduleExactAlarms()) try{
    startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,Uri.parse("package:"+getPackageName())));
   }catch(Exception ignored){}
  }
 }
 @Override protected void onResume(){super.onResume();NotificationScheduler.scheduleAll(this);}
 @Override protected void onSaveInstanceState(Bundle out){webView.saveState(out);super.onSaveInstanceState(out);}
}
