package com.aaii.dhammalight.preview;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.*;
import android.view.Gravity;
import android.view.View;
import android.graphics.drawable.GradientDrawable;
public final class MyatHomeActivity extends Activity {
 private static final int TEAL=0xff063c3e,GOLD=0xffcfa14c,IVORY=0xfff8f2e8;
 private LinearLayout body,base;
 private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 private TextView txt(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);return v;}
 private GradientDrawable bg(int color){GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(15));return g;}
 private void screen(String section){base=new LinearLayout(this);base.setOrientation(1);base.setBackgroundColor(IVORY);setContentView(base);
   TextView h=txt("☸  Dhamma Light\n"+section,21,GOLD);h.setTypeface(null,Typeface.BOLD);h.setPadding(dp(18),dp(20),dp(18),dp(17));h.setBackgroundColor(TEAL);base.addView(h);
   ScrollView scroll=new ScrollView(this);body=new LinearLayout(this);body.setPadding(dp(13),dp(10),dp(13),dp(18));body.setOrientation(1);scroll.addView(body);
   base.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));footer();}
 private void footer(){LinearLayout bar=new LinearLayout(this);bar.setBackgroundColor(Color.WHITE);
   String[] names={"⌂ Home","▣ Books","☷ 24","♫ Audio","⚙ Sync"};
   Runnable[] actions={this::home,this::books,this::paccaya,this::audio,this::settings};
   for(int i=0;i<5;i++){final int k=i;TextView v=txt(names[i],11,TEAL);v.setGravity(Gravity.CENTER);v.setPadding(0,dp(16),0,dp(15));
     v.setOnClickListener(x->actions[k].run());bar.addView(v,new LinearLayout.LayoutParams(0,dp(55),1));}base.addView(bar);}
 private void card(String title,String detail,Runnable run){LinearLayout card=new LinearLayout(this);card.setBackground(bg(Color.WHITE));card.setOrientation(1);card.setPadding(dp(15),dp(16),dp(15),dp(16));
   TextView t=txt(title,18,TEAL);t.setTypeface(null,Typeface.BOLD);card.addView(t);
   TextView sub=txt(detail,13,0xff766554);card.addView(sub);
   if(run!=null)card.setOnClickListener(v->run.run());
   LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(5),0,dp(6));body.addView(card,p);}
 private void go(Class<?> cls){startActivity(new Intent(this,cls));}
 private void pdf(){Intent i=new Intent(this,NativePdfReaderActivity.class);i.putExtra("book_id","1oWP7KpXcU2t8py_VkZyW-jNgdZIpj4iB");i.putExtra("book_title","မြတ်ပဋ္ဌန်း.pdf");startActivity(i);}
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(TEAL);getWindow().setNavigationBarColor(TEAL);home();}
 private void home(){screen("မြတ်ပဋ္ဌန်း · Native Learning");
   card("📖 မြတ်ပဋ္ဌန်း စာအုပ်","သိကောင်းစရာနှင့် ကိုယ်တွေ့ဖြစ်ရပ်များ • PDF ၂၈ မျက်နှာ",this::bookDetail);
   card("နေ့စဉ် ဖတ်စာ","၂၄ ရက်စာ • မှတ်စု • ဖတ်ပြီး မှတ်သားခြင်း",()->go(DailyReadingActivity.class));
   card("နေ့စဉ်စာဆောင် Channel","BOOKS / COMIC",()->go(DailyChannelActivity.class));
   card("ပဋ္ဌာန်း ၂၄ ပစ္စည်း","ပါဠိအမည်၊ အဓိပ္ပာယ်၊ အသံ",this::paccaya);
   card("အသံပူဇော်ခြင်း","MP3 • စာပိုဒ်လိုက် Highlight",this::audio);
   card("Settings / Sync","Google Drive catalog sync",this::settings);}
 private void books(){screen("BOOKS & COMIC");
   card("မြတ်ပဋ္ဌန်း","Book Detail • Native PDF Reader",this::bookDetail);
   card("နေ့စဉ်စာဆောင် · BOOKS / COMIC","Google Drive Catalog",()->go(DailyChannelActivity.class));
   card("eLibrary စာအုပ် ၁၀ အုပ်","စာအုပ်ကြိုတင်စာရင်း",()->go(DriveBookCatalogActivity.class));}
 private void bookDetail(){screen("မြတ်ပဋ္ဌန်း • စာအုပ်အသေးစိတ်");
   card("မူရင်းစာအုပ်","PDF Scan ၂၈ မျက်နှာ • Burmese Classic source",null);
   card("📖 စာဖတ်ရန်","Native Reader • Bookmark • Zoom",this::pdf);
   card("မာတိကာ","PDF 1–3 / 4–6 / 7–8 / 8–13 / 13–14 / 15–28",this::chapters);
   card("Unicode Reader","မူရင်းစာသားအပြည့် စိစစ်ရန် ကျန်ရှိသည်",this::paccaya);
   card("Audio / Recitation","Timecode လိုက် Highlight",this::audio);}
 private void chapters(){screen("မြတ်ပဋ္ဌန်း • မာတိကာ");
   String[] groups={"အဖွင့်နှင့် မာတိကာ — ၁–၃","မိတ်ဆက်နှင့် ရှင်းလင်းချက် — ၄–၆","ပဋ္ဌာန်းစာရင်း — ၇–၈","ပါဠိပိုဒ်များ — ၈–၁၃","ပူဇော်မှု၊ မေတ္တာပို့ — ၁၃–၁၄","ရှင်းလင်းချက် — ၁၅–၂၈"};
   for(String g:groups)card(g,"PDF Page Group • မူရင်းနှင့် ပြန်တိုက်စစ်ရန်",this::pdf);}
 private void paccaya(){screen("ပဋ္ဌာန်း ၂၄ ပစ္စည်း");
   for(int n=0;n<24;n++){final int id=n;card(String.format(java.util.Locale.US,"%02d · ",n+1)+ComicPrototypeActivity.lessonName(n),ComicPrototypeActivity.lessonMeaning(n),()->detail(id));}}
 private void detail(int n){screen(ComicPrototypeActivity.lessonName(n));
   card("ပါဠိအမည်",PaliContent.ROMAN[n],null);
   card("မြန်မာအဓိပ္ပာယ် အကျဉ်း",ComicPrototypeActivity.lessonMeaning(n),null);
   card("စာပိုဒ်နှင့် အသံ","MP3 + JSON Timecodes",()->{Intent i=new Intent(this,TimedRecitationActivity.class);i.putExtra("lesson_index",n);startActivity(i);});
   card("နေ့စဉ်ဖတ်စာ","လေ့လာရန်နှင့် မှတ်စု",()->go(DailyReadingActivity.class));}
 private void audio(){screen("အသံပူဇော်ခြင်း");
   card("Audio Player","MP3/M4A ဖိုင်ရွေး၍ နားထောင်ရန်",()->go(TimedRecitationActivity.class));
   card("Pali Text Highlight","Timecode JSON လိုအပ်သည်",()->go(TimedRecitationActivity.class));
   card("Professional recordings","အတည်ပြု အသံဖိုင်များ မထည့်ရသေးပါ",null);}
 private void settings(){screen("Settings / Sync");
   card("⟳ Google Drive Sync","BOOKS / COMIC စာရင်း refresh • API key ခွင့်ပြုချက် လိုအပ်",()->go(DailyChannelActivity.class));
   card("Native PDF Reader","ကိုယ်ပိုင် မြတ်ပဋ္ဌန်း PDF ရွေးဖွင့်မည်",this::pdf);
   card("About","မြတ်ပဋ္ဌန်း V5.0 Native • Original V4.0 visual style",null);}
 @Override public void onBackPressed(){home();}
}