package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Native Daily Reading Channel with predefined Drive book catalog and optional Drive API key sync.
 * The Drive API requires authorized access or a configured API key and publicly readable folders.
 * No Google credentials or user-owned PDF files are packaged in this repository.
 */
public final class DailyChannelActivity extends Activity {
  private static final int NAVY=Color.rgb(4,43,45),GOLD=Color.rgb(233,187,75),CREAM=Color.rgb(255,248,233);
  private static final String ROOT="1DSU1NLcJstotFRYCNDHtrqkNSPl8HlcM";
  private static final String FOLDER_BOOKS="1Orl6Vi3baRLdOwMH8IJK-W67-vGt8HPX";
  private static final String FOLDER_COMIC="1x6GR-2OGbWOUyVlvD2SrAXU7buLijl8P";
  private static final String ROOT_URL="https://drive.google.com/drive/folders/"+ROOT;
  private static final String BASE_BOOKS="[{\"id\":\"1gEb0hrc-30z3NigVE579QbVxtnuaAtnP\",\"name\":\"၂၄ ပစ္စည်း ပဋ္ဌာန်းပါဠိတော်.pdf\",\"mimeType\":\"application/pdf\"}]";
  private static final String BASE_COMIC="[{\"id\":\"1sfXF7v2aLgF8xNwa5A_g0hsDETF3K_bI\",\"name\":\"ပဋ္ဌာန်း ၂၄ ပစ္စည်း — ပုံပြင်နှင့် ကာတွန်း.pdf\",\"mimeType\":\"application/pdf\"}]";
  private final Handler mainThread=new Handler(Looper.getMainLooper());
  private SharedPreferences settings;
  private LinearLayout screen,items;
  private TextView status;
  private String category="BOOKS";
  private boolean busy=false;
  private int dp(float x){return (int)(x*getResources().getDisplayMetrics().density+0.5f);}
  private GradientDrawable bg(int color){GradientDrawable b=new GradientDrawable();b.setColor(color);b.setCornerRadius(dp(14));return b;}
  private TextView text(String s,int size,int color,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
  private Button button(String s, Runnable on){Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(14);b.setOnClickListener(v->on.run());return b;}
  @Override public void onCreate(Bundle saved){super.onCreate(saved);settings=getSharedPreferences("daily_channel_catalog_v33",MODE_PRIVATE);showCatalog();if(settings.getBoolean("auto_sync",false))sync();}
  private String cacheKey(String cat){return "catalog_"+cat;}
  private JSONArray getBooks(String cat){
    String saved=settings.getString(cacheKey(cat),cat.equals("BOOKS")?BASE_BOOKS:BASE_COMIC);
    try{return new JSONArray(saved);}catch(Exception e){try{return new JSONArray(cat.equals("BOOKS")?BASE_BOOKS:BASE_COMIC);}catch(Exception impossible){return new JSONArray();}}
  }
  private void displayShell(String subtitle){
    screen=new LinearLayout(this);screen.setOrientation(LinearLayout.VERTICAL);screen.setBackgroundColor(CREAM);
    setContentView(screen);
    LinearLayout head=new LinearLayout(this);head.setOrientation(LinearLayout.VERTICAL);head.setBackgroundColor(NAVY);head.setPadding(dp(16),dp(17),dp(16),dp(17));
    head.addView(text("☸  Dhamma Light",23,GOLD,true));
    head.addView(text("နေ့စဉ်စာဆောင်  ·  "+subtitle,16,Color.WHITE,true));screen.addView(head);
  }
  private void showCatalog(){
    displayShell("CHANNEL");
    LinearLayout tabs=new LinearLayout(this);tabs.setPadding(dp(8),dp(8),dp(8),0);
    Button book=button("📚  BOOKS",()->{category="BOOKS";showCatalog();});
    Button comic=button("▣  COMIC",()->{category="COMIC";showCatalog();});
    Button options=button("⚙ Sync",this::showSettings);
    tabs.addView(book,new LinearLayout.LayoutParams(0,dp(58),1));
    tabs.addView(comic,new LinearLayout.LayoutParams(0,dp(58),1));
    tabs.addView(options,new LinearLayout.LayoutParams(0,dp(58),1));screen.addView(tabs);
    TextView group=text(category.equals("BOOKS")?"BOOKS · စာအုပ်များ":"COMIC · ရုပ်ပြစာအုပ်များ",20,NAVY,true);
    group.setPadding(dp(16),dp(15),0,dp(6));screen.addView(group);
    ScrollView scroll=new ScrollView(this);items=new LinearLayout(this);items.setPadding(dp(12),dp(4),dp(12),dp(12));items.setOrientation(LinearLayout.VERTICAL);
    scroll.addView(items);screen.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    status=text("",12,NAVY,false);status.setPadding(dp(14),dp(5),dp(14),dp(5));screen.addView(status);
    Button syncButton=button("⟳  Sync လုပ်မည်",this::sync);screen.addView(syncButton);
    Button home=button("⌂  Home",this::finish);screen.addView(home);
    renderBooks();renderStatus();
  }
  private void renderBooks(){
    if(items==null)return;items.removeAllViews();
    JSONArray collection=getBooks(category);
    for(int n=0;n<collection.length();n++){
      JSONObject row=collection.optJSONObject(n);if(row==null)continue;
      String name=row.optString("name","PDF"),id=row.optString("id","");
      if(id.isEmpty())continue;
      LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(dp(15),dp(12),dp(15),dp(12));card.setBackground(bg(Color.WHITE));
      card.addView(text("📖 "+name,17,NAVY,true));
      card.addView(text("PDF · Google Drive · "+category,12,Color.rgb(143,107,44),false));
      LinearLayout actions=new LinearLayout(this);actions.setOrientation(LinearLayout.HORIZONTAL);
      actions.addView(button("▣ App အတွင်းဖတ်မည်",()->read(id,name)),new LinearLayout.LayoutParams(0,dp(52),2));
      actions.addView(button("Drive ↗",()->openUrl("https://drive.google.com/file/d/"+id+"/view")),new LinearLayout.LayoutParams(0,dp(52),1));
      card.addView(actions);
      LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(6),0,dp(6));items.addView(card,cp);
    }
    if(collection.length()==0)items.addView(text("စာအုပ်မတွေ့ပါ။ Settings → Sync တွင် ပြန်စစ်ပါ။",15,NAVY,false));
  }
  private void read(String id,String name){
    Intent i=new Intent(this,NativePdfReaderActivity.class);
    i.putExtra("book_id",id);i.putExtra("book_title",name);
    startActivity(i);
  }
  private void openUrl(String url){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)));}catch(Exception e){Toast.makeText(this,"Cannot open Drive link",Toast.LENGTH_LONG).show();}}
  private void renderStatus(){
    if(status==null)return;
    long last=settings.getLong("last_sync",0);
    status.setText((busy?"Sync လုပ်နေသည် ...":"Cached titles · "+(last==0?"Not synchronized yet":new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm",Locale.US).format(new java.util.Date(last))))+
      " · "+getBooks("BOOKS").length()+" Books / "+getBooks("COMIC").length()+" Comic");
  }
  private void showSettings(){
    displayShell("SETTINGS / SYNC");
    LinearLayout form=new LinearLayout(this);form.setOrientation(LinearLayout.VERTICAL);form.setPadding(dp(17),dp(15),dp(17),dp(15));screen.addView(form);
    form.addView(text("Google Drive Folder Sync",22,NAVY,true));
    form.addView(text("BOOKS + COMIC စာအုပ်အမည်များကို Refresh လုပ်ရန်",14,NAVY,false));
    EditText key=new EditText(this);
    key.setSingleLine(true);key.setTextSize(13);key.setHint("Google Drive API Key (optional)");
    key.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
    key.setText(settings.getString("google_api_key",""));
    form.addView(key);
    Button save=button("API Key ကို ဖုန်းထဲ သိမ်းရန်",()->{
      settings.edit().putString("google_api_key",key.getText().toString().trim()).apply();
      Toast.makeText(this,"Settings saved on device",Toast.LENGTH_SHORT).show();});
    form.addView(save);
    Button auto=button("Auto Sync: "+(settings.getBoolean("auto_sync",false)?"ON":"OFF"),()->{
      boolean mode=!settings.getBoolean("auto_sync",false);settings.edit().putBoolean("auto_sync",mode).apply();
      showSettings();});
    form.addView(auto);
    form.addView(button("⟳ Sync NOW",()->{
      settings.edit().putString("google_api_key",key.getText().toString().trim()).apply();sync();
    }));
    form.addView(button("Google Drive Folder ဖွင့်ရန်",()->openUrl(ROOT_URL)));
    form.addView(button("← ပြန်သွားရန်",this::showCatalog));
    form.addView(text("Sync သည် Google Drive REST API ဖြင့် စာအုပ် metadata များကိုသာ ရယူသည်။ Public/API key access မရှိပါက Refresh မဖြစ်ဘဲ ကြိုထည့်ထားသော စာအုပ် ၂ အုပ်ကို ဆက်ပြသည်။ PDF ဖိုင်အပြည့် Offline Download မလုပ်ပါ။",13,NAVY,false));
    status=null;
  }
  private JSONArray fetchFolder(String folderId,String apiKey)throws Exception{
    String q="'"+folderId+"' in parents and trashed = false and mimeType = 'application/pdf'";
    String url="https://www.googleapis.com/drive/v3/files?q="+java.net.URLEncoder.encode(q,"UTF-8")+
       "&fields="+java.net.URLEncoder.encode("nextPageToken,files(id,name,mimeType,size,modifiedTime)","UTF-8")+
       "&pageSize=1000&key="+java.net.URLEncoder.encode(apiKey,"UTF-8");
    HttpURLConnection conn=(HttpURLConnection)new URL(url).openConnection();
    conn.setConnectTimeout(12000);conn.setReadTimeout(12000);conn.setRequestProperty("Accept","application/json");
    try{
      int code=conn.getResponseCode();if(code!=200)throw new java.io.IOException("Drive API response "+code);
      try(InputStream input=conn.getInputStream();ByteArrayOutputStream buf=new ByteArrayOutputStream()){
        byte[] b=new byte[8192];int n;while((n=input.read(b))!=-1){buf.write(b,0,n);if(buf.size()>5000000)throw new java.io.IOException("Catalog too large");}
        JSONObject data=new JSONObject(buf.toString("UTF-8"));return data.optJSONArray("files")==null?new JSONArray():data.getJSONArray("files");
      }
    }finally{conn.disconnect();}
  }
  private void sync(){
    if(busy)return;
    String key=settings.getString("google_api_key","").trim();
    if(key.length()==0){
      Toast.makeText(this,"Settings တွင် Google Drive API Key ထည့်ရန်လိုအပ်သည်",Toast.LENGTH_LONG).show();return;
    }
    busy=true;renderStatus();
    new Thread(()->{
      try{
        JSONArray books=fetchFolder(FOLDER_BOOKS,key),comics=fetchFolder(FOLDER_COMIC,key);
        settings.edit().putString(cacheKey("BOOKS"),books.toString())
           .putString(cacheKey("COMIC"),comics.toString())
           .putLong("last_sync",System.currentTimeMillis()).apply();
        mainThread.post(()->{busy=false;if(items!=null)renderBooks();renderStatus();Toast.makeText(this,"Sync OK · "+books.length()+" Books / "+comics.length()+" Comic",Toast.LENGTH_LONG).show();});
      }catch(Exception e){
        mainThread.post(()->{busy=false;renderStatus();Toast.makeText(this,"Sync failed: "+e.getMessage(),Toast.LENGTH_LONG).show();});
      }
    }).start();
  }
  @Override public void onBackPressed(){finish();}
}
