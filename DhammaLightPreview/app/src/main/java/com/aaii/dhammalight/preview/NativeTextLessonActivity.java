package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

/** 24 offline native Unicode summaries, not extracted book text.
 * Source PDF remains in bundled eLibrary, accessed from lesson buttons.
 */
public final class NativeTextLessonActivity extends Activity{
  private static final int DEEP=0xff073a3b,GOLD=0xffe7b461,IVORY=0xfffff7e8,INK=0xff1c3937;
  private static final String[] EXAMPLES={
    "သစ်ပင်တစ်ပင်ကို အမြစ်က ထောက်ပံ့သကဲ့သို့ ဟိတ်တရားများ၏ အထောက်အပံ့ကို လေ့လာပါ။",
    "မျက်စိဖြင့် မြင်သောအရာ၊ နားဖြင့် ကြားသောအသံတို့ကို အာရုံအဖြစ် နှိုင်းယှဉ်ကြည့်ပါ။",
    "လုပ်ဆောင်ချက်တစ်ခုတွင် အားကောင်းသော ဆန္ဒ၊ ဝီရိယ စသည်တို့၏ ဦးဆောင်ပုံကို စဉ်းစားပါ။",
    "ရှေ့စိတ်ပြီး၍ နောက်စိတ် ဆက်လက်ဖြစ်ပေါ်မှုကို အစဉ်လိုက်ကြည့်ပါ။",
    "တံခါးခေါက်သံကို ကြားရာမှ သိမှတ်ခြင်းအစဉ်ဖြစ်ပုံကို ရုပ်ပြနဲ့လေ့လာပါ။",
    "မီးသီးနှင့် အလင်းရောင် ဥပမာကဲ့သို့ အတူတကွဖြစ်ပေါ်မှုကို သရုပ်ဖော်ကြည့်ပါ။",
    "အချင်းချင်းမှီထောက်သော အရာများကို အပြန်အလှန်မြှားဖြင့် ဆက်ကြည့်ပါ။",
    "ပန်းချီရေးရန် Canvas ကို မှီခိုရသကဲ့သို့ မှီရာအကြောင်းကို သင်ယူပါ။",
    "အလှူအကြောင်း ပြန်သတိရခြင်းက စိတ်တွင် အားကောင်းသော မှီရာဖြစ်နိုင်ပုံကို စဉ်းစားပါ။",
    "ရှေးဦးဖြစ်ပြီးသောအရာနှင့် နောက်မှဖြစ်သော သိမှုကို အချိန်မျဉ်းဖြင့် ယှဉ်ပြပါ။",
    "နောက်မှဖြစ်သော စိတ်တရားက ရှေ့ကဖြစ်ပြီးသော ရုပ်တရားကို ထောက်ပံ့ပုံကို လေ့လာပါ။",
    "အဖန်ဖန် ဆက်တိုက်ဖြစ်သော ဇောစိတ်တို့၏ အားဖြည့်မှုကို Timeline ဖြင့်ကြည့်ပါ။",
    "ပြုလုပ်နေသော အလုပ်တစ်ခု၏ စေတနာကို ပြန်သုံးသပ်ကြည့်ပါ။",
    "အကြောင်းတရားနှင့် အကျိုးဆက်ကို တွဲဖက်စဉ်းစားပါ။",
    "ရုပ်အာဟာရနှင့် နာမ်အာဟာရအကြောင်း မူရင်းစာအုပ်မှ နှိုင်းယှဉ်ဖတ်ပါ။",
    "မျက်စိ၊ နား စသော ဣန္ဒြိယများ၏ သက်ဆိုင်ရာကိစ္စကို ခွဲခြားကြည့်ပါ။",
    "အာရုံတစ်ခု၌ စူးစိုက်တည်ကြည်မှု၏ သဘောကို လေ့လာပါ။",
    "လမ်းပြသကဲ့သို့ အကျိုးသို့ ပို့ဆောင်သော မဂ္ဂအင်္ဂများကို စာအုပ်မှလေ့လာပါ။",
    "အတူယှဉ်တွဲဖြစ်ပေါ်မှု၏ ဂုဏ်လက္ခဏာကို နှိုင်းယှဉ်ပါ။",
    "မယှဉ်စပ်သော်လည်း အထောက်အပံ့ပြုနိုင်သော သဘောကို လေ့လာပါ။",
    "ရှိနေခြင်းကြောင့် အထောက်အပံ့ ဖြစ်ပေါ်နိုင်ပုံကို စဉ်းစားပါ။",
    "ရှေ့တရား ချုပ်ပျောက်ခြင်းနောက် ဆက်ဖြစ်မှုကို လေ့လာပါ။",
    "နေဝင်ပြီး လရောင် ထင်ရှားလာသော ဥပမာကို လေ့လာပါ။",
    "ငါးနှင့် ရေရှိနေမှု ဥပမာဖြင့် မကွယ်ပျောက်ဘဲ ထောက်ပံ့ပုံကို လေ့လာပါ။"
  };
  private static final int[] START={2,3,5,7,8,9,11,12,13,14,15,16,19,21,23,25,27,28,29,31,32,32,33,34};
  private SharedPreferences store;
  private TextToSpeech tts;
  private int index=0, fontSize=18;
  private int dp(int px){return (int)(px*getResources().getDisplayMetrics().density+0.5f);}
  private TextView t(String s,int size,int color,boolean bold){
    TextView v=new TextView(this);v.setText(s);v.setTextColor(color);v.setTextSize(size);
    if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;
  }
  private Button button(String s,Runnable r){
    Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(13);b.setOnClickListener(v->r.run());return b;
  }
  @Override public void onCreate(Bundle state){
    super.onCreate(state);store=getSharedPreferences("native_text_reading_v36",MODE_PRIVATE);
    index=Math.max(0,Math.min(23,getIntent().getIntExtra("lesson_page",store.getInt("last",0))));
    fontSize=store.getInt("fontSize",18);
    getWindow().setStatusBarColor(DEEP);getWindow().setNavigationBarColor(DEEP);draw();
  }
  private void draw(){
    store.edit().putInt("last",index).apply();
    LinearLayout shell=new LinearLayout(this);shell.setBackgroundColor(IVORY);shell.setOrientation(1);setContentView(shell);
    LinearLayout head=new LinearLayout(this);head.setBackgroundColor(DEEP);head.setOrientation(1);head.setPadding(dp(18),dp(15),dp(18),dp(17));
    head.addView(t("☸  Dhamma Light · Offline Text",23,GOLD,true));
    head.addView(t("ပဋ္ဌာန်း ၂၄ ပစ္စည်း · Unicode Lesson Summary",13,Color.WHITE,false));shell.addView(head);
    ScrollView scroll=new ScrollView(this);LinearLayout content=new LinearLayout(this);content.setOrientation(1);
    content.setPadding(dp(19),dp(20),dp(19),dp(20));scroll.addView(content);
    shell.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    TextView counter=t("LESSON "+(index+1)+" / 24",15,DEEP,true);content.addView(counter);
    TextView title=t(ComicPrototypeActivity.lessonName(index),26,DEEP,true);
    title.setPadding(0,dp(12),0,dp(20));content.addView(title);
    TextView description=t("အဓိပ္ပာယ်",16,0xff976d2b,true);content.addView(description);
    TextView meaning=t(ComicPrototypeActivity.lessonMeaning(index),fontSize,INK,false);
    meaning.setLineSpacing(dp(7),1.0f);meaning.setPadding(0,dp(7),0,dp(24));content.addView(meaning);
    TextView exTitle=t("ရုပ်ပြနဲ့ ဆက်စပ်လေ့လာရန်",16,0xff976d2b,true);content.addView(exTitle);
    TextView ex=t(EXAMPLES[index],fontSize,INK,false);ex.setLineSpacing(dp(7),1.0f);ex.setPadding(0,dp(8),0,dp(25));content.addView(ex);
    TextView caution=t("ဤစာသည် App အတွက် ရေးသားထားသော အကျဉ်းချုပ်ဖြစ်ပြီး စာရေးသူ၏ PDF မူရင်းစာသား တိုက်ရိုက်ကူးယူမှု မဟုတ်ပါ။ ဓမ္မအဓိပ္ပာယ်ကို စာအုပ်မူရင်းနှင့် ပြန်လည်စိစစ်ပါ။",13,0xff8e7862,false);
    caution.setPadding(0,0,0,dp(15));content.addView(caution);
    Button book=button("▣ မူရင်း PDF p."+START[index]+" ဖတ်ရန်",()->{
      Intent i=new Intent(this,NativePdfReaderActivity.class);i.putExtra("book_title","ပဋ္ဌာန်း — ဂျေဂျူဝိုင်");
      i.putExtra("book_id","patthana_jjyy");i.putExtra("asset_pdf","books/patthana_jjyy.pdf");
      i.putExtra("lesson_page",START[index]-1);startActivity(i);
    });content.addView(book);
    content.addView(button("🖼 ရုပ်ပြပဋ္ဌာန်းဖတ်ရန်",()->{
      Intent i=new Intent(this,BundledComicReaderActivity.class);i.putExtra("lesson_page",index);startActivity(i);
    }));
    content.addView(button("🔊 ဖတ်ပြရန် (Myanmar TTS)",this::readAloud));
    content.addView(button(store.getBoolean("favorite_"+index,false)?"★ Bookmark ဖြုတ်ရန်":"☆ Bookmark မှတ်ရန်",()->{
      store.edit().putBoolean("favorite_"+index,!store.getBoolean("favorite_"+index,false)).apply();draw();
    }));
    LinearLayout controls=new LinearLayout(this);controls.setBackgroundColor(DEEP);
    controls.addView(button("− စာလုံး",()->{fontSize=Math.max(14,fontSize-2);saveFont();draw();}),new LinearLayout.LayoutParams(0,dp(52),1));
    controls.addView(button("‹ ယခင်",()->{index=(index+23)%24;draw();}),new LinearLayout.LayoutParams(0,dp(52),1));
    controls.addView(button("နောက် ›",()->{index=(index+1)%24;draw();}),new LinearLayout.LayoutParams(0,dp(52),1));
    controls.addView(button("+ စာလုံး",()->{fontSize=Math.min(30,fontSize+2);saveFont();draw();}),new LinearLayout.LayoutParams(0,dp(52),1));
    shell.addView(controls);
  }
  private void saveFont(){store.edit().putInt("fontSize",fontSize).apply();}
  private void readAloud(){
    String words=ComicPrototypeActivity.lessonName(index)+"။ "+ComicPrototypeActivity.lessonMeaning(index)+"။ "+EXAMPLES[index];
    if(tts!=null){tts.speak(words,TextToSpeech.QUEUE_FLUSH,null,"lesson");return;}
    tts=new TextToSpeech(this,s->{
      if(s!=TextToSpeech.SUCCESS||tts==null)return;
      int lang=tts.setLanguage(new Locale("my","MM"));
      if(lang!=TextToSpeech.LANG_MISSING_DATA&&lang!=TextToSpeech.LANG_NOT_SUPPORTED)
        tts.speak(words,TextToSpeech.QUEUE_FLUSH,null,"lesson");
    });
  }
  @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}