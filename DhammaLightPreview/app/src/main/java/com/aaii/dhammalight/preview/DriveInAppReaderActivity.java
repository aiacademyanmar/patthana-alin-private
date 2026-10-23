package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/** Native Google Drive PDF downloader. Never puts an access token in the URL.
 * Supports publicly downloadable Drive files; private files need authenticated backend/OAuth.
 * Downloads to app-private persistent disk, not external browser, then opens PdfRenderer.
 * If Drive returns HTML (sign-in, permission, quota, virus-scan confirmation), reject it.
 */
public final class DriveInAppReaderActivity extends Activity {
 private static final int TEAL=0xff063b3c, GOLD=0xffeabd68, IVORY=0xfffff9ed;
 private static final int MAX_BYTES=180*1024*1024;
 private LinearLayout body;private ProgressBar progress;private TextView message;
 private Button primary;private String id,title;private File cache;
 private volatile boolean cancelled=false;private Thread worker;
 private int d(int n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
 private TextView t(String s,int size,int color,boolean bold){
  TextView v=new TextView(this);v.setText(s);v.setTextColor(color);v.setTextSize(size);
  if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;
 }
 private GradientDrawable rounded(int c){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(d(16));return g;}
 private void add(LinearLayout parent,android.view.View view,int space){
  LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,d(space),0,d(space));parent.addView(view,p);
 }
 @Override public void onCreate(Bundle state){
  super.onCreate(state);getWindow().setStatusBarColor(TEAL);getWindow().setNavigationBarColor(TEAL);
  id=getIntent().getStringExtra("drive_id");title=getIntent().getStringExtra("book_title");
  if(title==null)title="Drive eBook";
  if(id==null||!id.matches("[A-Za-z0-9_-]{15,128}")){Toast.makeText(this,"Drive File ID invalid",Toast.LENGTH_LONG).show();finish();return;}
  cache=new File(new File(getFilesDir(),"drive-books"),id+".pdf");
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(IVORY);setContentView(root);
  LinearLayout header=new LinearLayout(this);header.setOrientation(LinearLayout.VERTICAL);header.setBackgroundColor(TEAL);header.setPadding(d(18),d(25),d(18),d(25));
  header.addView(t("☸  Dhamma Light · Native eBook",24,GOLD,true));
  header.addView(t("Google Drive → App Reader",13,Color.WHITE,false));root.addView(header);
  body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(d(18),d(20),d(18),d(16));
  root.addView(body,new LinearLayout.LayoutParams(-1,0,1));
  add(body,t("📖 "+title,21,TEAL,true),8);
  message=t("Drive PDF ကို စစ်ဆေးနေပါတယ်…",15,0xff625b51,false);add(body,message,12);
  progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);progress.setIndeterminate(true);add(body,progress,12);
  primary=new Button(this);primary.setAllCaps(false);primary.setText("PDF ရယူပြီး ဖတ်မည်");primary.setOnClickListener(v->startFetch());add(body,primary,12);
  Button preview=new Button(this);preview.setAllCaps(false);preview.setText("Drive Online Preview (App အတွင်း)");
  preview.setOnClickListener(v->preview());add(body,preview,9);
  Button close=new Button(this);close.setAllCaps(false);close.setText("ပိတ်ရန်");close.setOnClickListener(v->finish());add(body,close,9);
  add(body,t("PDF ကို App-private Storage ထဲ Download လုပ်ပြီး Offline ဖတ်နိုင်ပါတယ်။ Private/Restricted ဖိုင်ကို စာအုပ်ပိုင်ရှင်က ခွင့်မပြုထားရင် Download မရနိုင်ပါ။",13,0xff777268,false),14);
  if(valid(cache)){message.setText("✓ Offline စာအုပ် Download ပြီးသား — ပြန်ဖတ်နိုင်ပါတယ်။");primary.setText("▣ Native Reader ဖွင့်ရန်");primary.setOnClickListener(v->openReader());progress.setVisibility(android.view.View.GONE);}
  else startFetch();
 }
 private boolean valid(File f){
  if(f==null||!f.isFile()||f.length()<8)return false;
  try(FileInputStream in=new FileInputStream(f)){
   byte[] magic=new byte[5];return in.read(magic)==5&&new String(magic,java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-");
  }catch(Exception ignored){return false;}
 }
 private void startFetch(){
  if(worker!=null&&worker.isAlive()){Toast.makeText(this,"Downloading…",Toast.LENGTH_SHORT).show();return;}
  cancelled=false;primary.setEnabled(false);primary.setText("Downloading…");progress.setIndeterminate(true);progress.setVisibility(android.view.View.VISIBLE);
  message.setText("Google Drive မှ PDF ရယူနေပါတယ်…");
  worker=new Thread(()->download(),"dhamma-drive-pdf");worker.start();
 }
 private void download(){
  File part=new File(cache.getParentFile(),id+".part");HttpURLConnection conn=null;long count=0;
  try{
   File dir=cache.getParentFile();if(dir==null||(!dir.exists()&&!dir.mkdirs()))throw new Exception("Cannot create cache directory");
   // This is a public share endpoint only. Google login through embedded WebView is not supported.
   URL url=new URL("https://drive.google.com/uc?export=download&id="+id);
   conn=(HttpURLConnection)url.openConnection();conn.setInstanceFollowRedirects(true);
   conn.setConnectTimeout(20000);conn.setReadTimeout(40000);
   conn.setRequestProperty("Accept","application/pdf,application/octet-stream;q=0.9,*/*;q=0.1");
   conn.setRequestProperty("User-Agent","DhammaLight-Android-PDFReader/3.5");
   int status=conn.getResponseCode();if(status!=200)throw new Exception("Drive HTTP "+status);
   String kind=conn.getContentType();
   if(kind!=null&&kind.toLowerCase(Locale.US).contains("text/html"))throw new Exception("Drive returned sign-in/confirmation page. Check file access.");
   long length=conn.getContentLengthLong();
   if(length>MAX_BYTES)throw new Exception("PDF too large for prototype cache (180MB max)");
   byte[] buf=new byte[64*1024];int size;long lastUpdate=0;
   try(InputStream in=new BufferedInputStream(conn.getInputStream());FileOutputStream out=new FileOutputStream(part)){
    while((size=in.read(buf))!=-1){
     if(cancelled)throw new Exception("Download cancelled");
     count+=size;if(count>MAX_BYTES)throw new Exception("PDF size exceeds 180MB");
     out.write(buf,0,size);
     if(System.currentTimeMillis()-lastUpdate>250){
      final int percent=length>0?(int)Math.min(100,count*100/length):-1;
      final long bytes=count;
      runOnUiThread(()->{
       if(percent>=0){progress.setIndeterminate(false);progress.setProgress(percent);}
       message.setText("Downloading PDF… "+(bytes/1024)+" KB"+(percent>=0?" • "+percent+"%":""));
      });
      lastUpdate=System.currentTimeMillis();
     }
    }
    out.getFD().sync();
   }
   if(!valid(part))throw new Exception("Drive returned HTML or an invalid PDF. Public download permission may be missing.");
   if(cache.exists()&&!cache.delete())throw new Exception("Unable to replace cached file");
   if(!part.renameTo(cache))throw new Exception("Unable to finalize cached PDF");
   runOnUiThread(()->{message.setText("✓ PDF ကို Offline သိမ်းပြီးပါပြီ။");progress.setVisibility(android.view.View.GONE);
    primary.setEnabled(true);primary.setText("▣ Native Reader ဖွင့်ရန်");primary.setOnClickListener(v->openReader());openReader();});
  }catch(Exception error){
   final String reason=error.getMessage()==null?"Download error":error.getMessage();
   part.delete();
   runOnUiThread(()->{message.setText("PDF တိုက်ရိုက်မဖွင့်နိုင်သေးပါ။ "+reason);
    progress.setVisibility(android.view.View.GONE);primary.setEnabled(true);primary.setText("↻ ပြန်ကြိုးစားရန်");primary.setOnClickListener(v->startFetch());});
  }finally{if(conn!=null)conn.disconnect();}
 }
 private void openReader(){
  if(!valid(cache)){startFetch();return;}
  Intent reader=new Intent(this,NativePdfReaderActivity.class);
  reader.putExtra("book_id",id);reader.putExtra("book_title",title);
  reader.putExtra("local_pdf_path",cache.getAbsolutePath());startActivity(reader);
 }
 private void preview(){
  // In-app preview fallback only; may require permission in Google's own viewer.
  Intent i=new Intent(this,DrivePreviewActivity.class);i.putExtra("drive_id",id);i.putExtra("book_title",title);startActivity(i);
 }
 @Override protected void onDestroy(){cancelled=true;super.onDestroy();}
}