package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;
import android.net.Uri;

/** In-app Drive preview fallback. This is not a substitute for Google OAuth.
 * Google may block private files or authentication in embedded WebView.
 */
public final class DrivePreviewActivity extends Activity {
 private WebView browser;
 @Override public void onCreate(Bundle state){
  super.onCreate(state);
  String id=getIntent().getStringExtra("drive_id");
  if(id==null||!id.matches("[A-Za-z0-9_-]{15,128}")){finish();return;}
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  root.setBackgroundColor(Color.rgb(5,47,48));setContentView(root);
  String title=getIntent().getStringExtra("book_title");
  TextView label=new TextView(this);label.setText("☸ Drive Preview • "+(title==null?"eBook":title));
  label.setTextSize(17);label.setTextColor(0xffffdfa7);label.setPadding(16,20,16,15);root.addView(label);
  TextView help=new TextView(this);
  help.setText("Online preview — Drive permission လိုအပ်နိုင်သည်။ PDF Download အောင်မြင်လျှင် Native Reader ကိုသုံးပါ။");
  help.setTextSize(12);help.setTextColor(Color.WHITE);help.setPadding(16,2,16,10);root.addView(help);
  browser=new WebView(this);
  WebSettings settings=browser.getSettings();
  settings.setJavaScriptEnabled(true);
  settings.setDomStorageEnabled(true);
  settings.setAllowFileAccess(false);
  settings.setAllowContentAccess(false);
  settings.setBuiltInZoomControls(true);settings.setDisplayZoomControls(false);
  browser.setWebViewClient(new WebViewClient(){
    @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
      String host=request.getUrl().getHost();
      if(host==null)return true;
      if("drive.google.com".equals(host)||"docs.google.com".equals(host))return false;
      Toast.makeText(DrivePreviewActivity.this,"Sign-in/External link cannot open inside this preview",Toast.LENGTH_LONG).show();
      return true;
    }
  });
  root.addView(browser,new LinearLayout.LayoutParams(-1,0,1));
  Button close=new Button(this);close.setText("‹ Native eLibrary");close.setAllCaps(false);
  close.setOnClickListener(v->finish());root.addView(close);
  browser.loadUrl("https://drive.google.com/file/d/"+id+"/preview");
 }
 @Override protected void onDestroy(){if(browser!=null){browser.destroy();browser=null;}super.onDestroy();}
}