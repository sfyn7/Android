package com.staysobercora.app;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
 private WebView webView;
 private static final String HOME="https://coradicker95.netlify.app/";
 @Override protected void onCreate(Bundle state){
  super.onCreate(state); setContentView(R.layout.activity_main);
  webView=findViewById(R.id.webview);
  WebSettings s=webView.getSettings(); s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true);
  s.setDatabaseEnabled(true); s.setAllowFileAccess(false); s.setAllowContentAccess(false);
  webView.setWebViewClient(new WebViewClient(){
   @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r){
    Uri u=r.getUrl(); String scheme=u.getScheme()==null?"":u.getScheme();
    if("tel".equals(scheme)){ startActivity(new Intent(Intent.ACTION_DIAL,u)); return true; }
    if("sms".equals(scheme)||"mailto".equals(scheme)){ startActivity(new Intent(Intent.ACTION_VIEW,u)); return true; }
    String host=u.getHost()==null?"":u.getHost();
    if("coradicker95.netlify.app".equals(host)) return false;
    startActivity(new Intent(Intent.ACTION_VIEW,u)); return true;
   }
  });
  if(state==null) webView.loadUrl(HOME); else webView.restoreState(state);
  getOnBackPressedDispatcher().addCallback(this,new OnBackPressedCallback(true){
   @Override public void handleOnBackPressed(){ if(webView.canGoBack()) webView.goBack(); else finish(); }
  });
 }
 @Override protected void onSaveInstanceState(Bundle out){ webView.saveState(out); super.onSaveInstanceState(out); }
}
