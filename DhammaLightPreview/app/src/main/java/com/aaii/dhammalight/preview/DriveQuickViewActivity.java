package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceRequest;
import android.webkit.CookieManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ProgressBar;

/** In-app online Google Drive preview without Android file picker.
 * For files requiring Google sign-in, use external browser; does not bypass Drive permissions.
 * This is a WEB preview. Offline PDF uses Android PdfRenderer with a user-selected local file.
 */
public final class DriveQuickViewActivity extends Activity {
  private static final int TEAL=0xff07393d, GOLD=0xffecc171;
  private WebView web;
  private ProgressBar progress;
  private String driveId, title;
  private boolean loaded;
  private boolean validId(String id){return id!=null&&id.matches("[A-Za-z0-9_-]{12,160}");}
  @Override public void onCreate(Bundle state){
    super.onCreate(state);
    driveId=getIntent().getStringExtra("book_id");title=getIntent().getStringExtra("book_title");
    if(!validId(driveId)){new AlertDialog.Builder(this).setMessage("Drive file ID မမှန်ပါ").setPositiveButton("OK",(d,w)->finish()).show();return;}
    LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(TEAL);setContentView(root);
    TextView header=new TextView(this);header.setText("☸  "+(title==null?"Google Drive Reader":title));
    header.setTextSize(17);header.setTextColor(GOLD);header.setPadding(18,19,12,14);
    root.addView(header);
    progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setIndeterminate(true);root.addView(progress);
    web=new WebView(this);web.setBackgroundColor(Color.WHITE);
    web.getSettings().setJavaScriptEnabled(true);
    web.getSettings().setDomStorageEnabled(true);
    web.getSettings().setBuiltInZoomControls(true);
    web.getSettings().setDisplayZoomControls(false);
    web.getSettings().setSupportZoom(true);
    CookieManager.getInstance().setAcceptCookie(true);
    web.setWebChromeClient(new WebChromeClient(){@Override public void onProgressChanged(WebView view,int newProgress){progress.setIndeterminate(false);progress.setProgress(newProgress);progress.setVisibility(newProgress==100?View.GONE:View.VISIBLE);}});
    web.setWebViewClient(new WebViewClient(){
      @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest req){
        String host=req.getUrl().getHost();
        if(host!=null&&(host.equals("drive.google.com")||host.equals("accounts.google.com")||host.equals("docs.google.com")))return false;
        try{startActivity(new Intent(Intent.ACTION_VIEW,req.getUrl()));}catch(Exception ignored){}
        return true;
      }
      @Override public void onPageFinished(WebView view,String url){loaded=true;}
    });
    root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
    LinearLayout buttons=new LinearLayout(this);
    buttons.addView(action("↻ Refresh",()->web.reload()),new LinearLayout.LayoutParams(0,60,1));
    buttons.addView(action("Browser ↗",this::external),new LinearLayout.LayoutParams(0,60,1));
    buttons.addView(action("Offline PDF",this::offline),new LinearLayout.LayoutParams(0,60,1));
    root.addView(buttons);
    TextView notice=new TextView(this);
    notice.setText("Online Preview • Drive ဖတ်ခွင့်လိုသည်။ Private စာအုပ်များအတွက် Browser သို့မဟုတ် OAuth လိုအပ်နိုင်သည်။");
    notice.setTextColor(Color.WHITE);notice.setTextSize(11);notice.setPadding(12,6,12,9);
    root.addView(notice);
    web.loadUrl("https://drive.google.com/file/d/"+driveId+"/preview");
  }
  private Button action(String text,Runnable call){Button b=new Button(this);b.setAllCaps(false);b.setText(text);b.setOnClickListener(v->call.run());return b;}
  private void external(){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("https://drive.google.com/file/d/"+driveId+"/view")));}catch(Exception ignored){}}
  private void offline(){Intent i=new Intent(this,NativePdfReaderActivity.class);i.putExtra("book_id",driveId);i.putExtra("book_title",title);startActivity(i);}
  @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
  @Override protected void onDestroy(){if(web!=null){web.stopLoading();web.destroy();}super.onDestroy();}
}
