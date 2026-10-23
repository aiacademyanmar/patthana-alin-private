package com.aaii.dhammalight.preview;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ScrollView;
import android.view.View;
public final class OfflineHomeActivity extends Activity {
 private LinearLayout list;
 private int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+0.5f);}
 private TextView text(String t,int sz,int color){TextView v=new TextView(this);v.setText(t);v.setTextSize(sz);v.setTextColor(color);return v;}
 private void tile(String title,String desc,Class<?> target){
  LinearLayout card=new LinearLayout(this);card.setOrientation(1);
  GradientDrawable bg=new GradientDrawable();bg.setColor(Color.WHITE);bg.setCornerRadius(dp(20));bg.setStroke(dp(1),0xffe7b75d);
  card.setBackground(bg);card.setPadding(dp(19),dp(19),dp(19),dp(19));
  TextView h=text(title,20,0xff063a3a);h.setTypeface(Typeface.DEFAULT,Typeface.BOLD);card.addView(h);
  TextView sub=text(desc,14,0xff6b6258);sub.setPadding(0,dp(7),0,0);card.addView(sub);
  card.setOnClickListener(v->startActivity(new Intent(this,target)));
  LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,dp(13));list.addView(card,lp);
 }
 @Override public void onCreate(Bundle saved){
  super.onCreate(saved);getWindow().setStatusBarColor(0xff063a3a);getWindow().setNavigationBarColor(0xff063a3a);
  LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(0xfffff9eb);setContentView(root);
  LinearLayout head=new LinearLayout(this);head.setOrientation(1);head.setBackgroundColor(0xff063a3a);head.setPadding(dp(22),dp(26),dp(22),dp(26));
  TextView brand=text("☸  Dhamma Light",29,0xffe7b75d);brand.setTypeface(Typeface.DEFAULT,Typeface.BOLD);head.addView(brand);
  head.addView(text("ပဋ္ဌာန်း • OFFLINE LEARNING",15,Color.WHITE));root.addView(head);
  ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(1);list.setPadding(dp(15),dp(23),dp(15),dp(20));scroll.addView(list);
  root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  TextView intro=text("သင်ယူရေးစနစ် ၄ မျိုး",22,0xff063a3a);intro.setTypeface(Typeface.DEFAULT,Typeface.BOLD);intro.setPadding(0,0,0,dp(16));list.addView(intro);
  tile("01 · ရုပ်ပြပဋ္ဌာန်း","မူရင်း PPTX ပုံ ၂၄ ရွက် • Zoom • Bookmark",BundledComicReaderActivity.class);
  tile("02 · Offline eLibrary","ဂျေဂျူဝိုင်စာအုပ် • PDF Reader • စာအုပ်စင်",OfflineLibraryActivity.class);
  tile("03 · ကလေးသင်ခန်းစာ & Quiz","ပစ္စည်း ၂၄ မျိုး • Quiz • သရုပ်ပြ • TTS",MainActivity.class);
  tile("04 · နေ့စဉ်စာဆောင် Channel","Daily Lesson • Notes • 14-day Archive",DailyReadingChannelActivity.class);
  tile("ထပ်ဆောင်း သင်ခန်းစာစာရင်း","အမည်ရှာဖွေရန် • ဖတ်ပြီးမှတ်သားရန်",ComicPrototypeActivity.class);
  list.addView(text("စာအုပ်စာမျက်နှာတွေကို Offline APK ထဲတွင် ထည့်သွင်းထားပါသည်။ YouTube Link အတွက်သာ Internet လိုအပ်ပါသည်။",12,0xff877964));
 }
}