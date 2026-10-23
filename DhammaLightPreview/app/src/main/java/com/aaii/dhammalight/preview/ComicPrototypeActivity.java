package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

/* Public-safe APK prototype: no user-provided PDF or comic artwork is redistributed.
 * Import the user's original 24-page comic PDF with the Android document picker.
 */
public final class ComicPrototypeActivity extends Activity {
  private static final int TEAL=Color.rgb(4,49,49);
  private static final int TEAL2=Color.rgb(7,70,66);
  private static final int GOLD=Color.rgb(229,174,60);
  private static final int IVORY=Color.rgb(255,249,237);
  private static final int INK=Color.rgb(43,38,29);
  private static final int MUTED=Color.rgb(114,104,89);
  private static final int MAROON=Color.rgb(105,34,41);
  private static final String[] TITLE={
    "ဟေတုပစ္စယော","အာရမ္မဏပစ္စယော","အဓိပတိပစ္စယော","အနန္တရပစ္စယော",
    "သမနန္တရပစ္စယော","သဟဇာတပစ္စယော","အညမညပစ္စယော","နိဿယပစ္စယော",
    "ဥပနိဿယပစ္စယော","ပုရေဇာတပစ္စယော","ပစ္ဆာဇာတပစ္စယော","အာသေဝနပစ္စယော",
    "ကမ္မပစ္စယော","ဝိပါကပစ္စယော","အာဟာရပစ္စယော","ဣန္ဒြိယပစ္စယော",
    "ဈာနပစ္စယော","မဂ္ဂပစ္စယော","သမ္ပယုတ္တပစ္စယော","ဝိပ္ပယုတ္တပစ္စယော",
    "အတ္ထိပစ္စယော","နတ္ထိပစ္စယော","ဝိဂတပစ္စယော","အဝိဂတပစ္စယော"
  };
  private static final String[] EXPLAIN={
    "အမြစ်အဖြစ် ကျေးဇူးပြုသော ပစ္စည်း","အာရုံအဖြစ် ကျေးဇူးပြုသော ပစ္စည်း",
    "အကြီးအကဲအဖြစ် ကျေးဇူးပြုသော ပစ္စည်း","အကြားအလပ်မရှိ ဆက်၍ ကျေးဇူးပြုသော ပစ္စည်း",
    "အစဉ်တကျ အကြားမရှိ ဆက်၍ ကျေးဇူးပြုသော ပစ္စည်း","အတူတကွ ဖြစ်ပေါ်၍ ကျေးဇူးပြုသော ပစ္စည်း",
    "အချင်းချင်း အပြန်အလှန် ကျေးဇူးပြုသော ပစ္စည်း","မှီရာအဖြစ် ကျေးဇူးပြုသော ပစ္စည်း",
    "အားကြီးသော မှီရာအဖြစ် ကျေးဇူးပြုသော ပစ္စည်း","ရှေးဦးစွာ ဖြစ်ပေါ်၍ ကျေးဇူးပြုသော ပစ္စည်း",
    "နောက်မှ ဖြစ်ပေါ်၍ ကျေးဇူးပြုသော ပစ္စည်း","အဖန်ဖန် အားဖြည့်၍ ကျေးဇူးပြုသော ပစ္စည်း",
    "စေတနာအားဖြင့် ကျေးဇူးပြုသော ပစ္စည်း","ဝိပါက်အဖြစ် ကျေးဇူးပြုသော ပစ္စည်း",
    "ရုပ်နာမ်ကို အာဟာရအဖြစ် ထောက်ပံ့သော ပစ္စည်း","မိမိဆိုင်ရာ ကိစ္စ၌ အစိုးရ၍ ကျေးဇူးပြုသော ပစ္စည်း",
    "အာရုံ၌ စူးစိုက်တည်ကြည်စေ၍ ကျေးဇူးပြုသော ပစ္စည်း","လမ်းပြသဖွယ် ကျေးဇူးပြုသော ပစ္စည်း",
    "ယှဉ်တွဲ၍ ကျေးဇူးပြုသော ပစ္စည်း","မယှဉ်စပ်ဘဲ ကျေးဇူးပြုသော ပစ္စည်း",
    "ရှိနေခြင်းဖြင့် ကျေးဇူးပြုသော ပစ္စည်း","မရှိတော့ခြင်းဖြင့် ကျေးဇူးပြုသော ပစ္စည်း",
    "ကွယ်ပျောက်ခြင်းဖြင့် ကျေးဇူးပြုသော ပစ္စည်း","မကွယ်ပျောက်ခြင်းဖြင့် ကျေးဇူးပြုသော ပစ္စည်း"
  };
  private SharedPreferences prefs;
  private TextToSpeech speech;
  private LinearLayout base;
  private LinearLayout main;
  private int active=0;
  private String lastMode="home";

  public static String lessonName(int n){return TITLE[Math.max(0,Math.min(23,n))];}
  public static String lessonMeaning(int n){return EXPLAIN[Math.max(0,Math.min(23,n))];}

  @Override public void onCreate(Bundle state){
    super.onCreate(state);
    getWindow().setStatusBarColor(TEAL);
    getWindow().setNavigationBarColor(TEAL);
    prefs=getSharedPreferences("comic_prototype",MODE_PRIVATE);
    home();
  }
  private int d(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
  private GradientDrawable bg(int color,int radius){
    GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(d(radius));return g;
  }
  private GradientDrawable outline(int color,int radius,int line){
    GradientDrawable g=bg(color,radius);g.setStroke(d(1),line);return g;
  }
  private TextView t(String s,int sp,int color,boolean strong){
    TextView v=new TextView(this);v.setText(s);v.setTextSize(sp);v.setTextColor(color);
    if(strong)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);
    return v;
  }
  private void attach(LinearLayout l,View v,int height,int margin){
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,height<0?height:d(height));
    p.setMargins(0,d(margin),0,d(margin));l.addView(v,p);
  }
  private void padding(View v,int a,int b){v.setPadding(d(a),d(b),d(a),d(b));}
  private Button btn(String s,int color,int textColor,Runnable on){
    Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(15);
    b.setTextColor(textColor);b.setBackground(bg(color,13));b.setOnClickListener(v->on.run());
    return b;
  }
  private void label(String s,int sp,boolean strong){
    TextView v=t(s,sp,strong?INK:MUTED,strong);
    padding(v,4,4);attach(main,v,-2,3);
  }
  private void start(String caption){
    base=new LinearLayout(this);base.setOrientation(LinearLayout.VERTICAL);base.setBackgroundColor(IVORY);
    setContentView(base);
    LinearLayout header=new LinearLayout(this);header.setOrientation(LinearLayout.VERTICAL);
    header.setBackgroundColor(TEAL);padding(header,17,15);
    TextView logo=t("☸  Dhamma Light",26,GOLD,true);header.addView(logo);
    TextView sub=t("ပဋ္ဌာန်း ရုပ်ပြသင်ခန်းစာ   •   "+caption,13,IVORY,false);
    header.addView(sub);base.addView(header);
    ScrollView scroll=new ScrollView(this);scroll.setFillViewport(false);
    main=new LinearLayout(this);main.setOrientation(LinearLayout.VERTICAL);padding(main,14,12);
    scroll.addView(main);base.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    footer();
  }
  private void footer(){
    LinearLayout row=new LinearLayout(this);row.setBackgroundColor(TEAL);row.setGravity(Gravity.CENTER);
    String[] names={"⌂\nHome","☷\nLessons","▣\nComic","★\nProgress","⋯\nMore"};
    for(int i=0;i<names.length;i++){
      final int n=i;TextView v=t(names[i],12,GOLD,true);
      v.setGravity(Gravity.CENTER);padding(v,6,10);
      row.addView(v,new LinearLayout.LayoutParams(0,d(61),1));
      v.setOnClickListener(x->{
        if(n==0)home();else if(n==1)library("");
        else if(n==2)openReader(active);else if(n==3)progress();
        else more();
      });
    }
    base.addView(row);
  }
  private void feature(String name,String subtitle,int color,Runnable action){
    LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);
    card.setBackground(outline(Color.WHITE,17,GOLD));padding(card,16,10);
    TextView h=t(name,18,color,true);card.addView(h);
    card.addView(t(subtitle,13,MUTED,false));
    card.setOnClickListener(v->action.run());attach(main,card,-2,6);
  }
  private int completed(){
    int c=0;for(int i=0;i<24;i++)if(prefs.getBoolean("read_"+i,false))c++;return c;
  }
  private void home(){
    lastMode="home";start("24 PATTHANA COMIC LESSONS");
    LinearLayout hero=new LinearLayout(this);hero.setOrientation(LinearLayout.VERTICAL);
    hero.setBackground(bg(TEAL2,20));padding(hero,18,16);
    hero.addView(t("☸  ရုပ်ပြ ပဋ္ဌာန်း",27,GOLD,true));
    hero.addView(t("မူရင်းရုပ်ပြ ၂၄ မျက်နှာ • မြန်မာဘာသာ • Offline PDF Reader",14,IVORY,false));
    hero.addView(t("ဖတ်ပြီးသော သင်ခန်းစာ   "+completed()+" / 24",15,GOLD,true));
    attach(main,hero,-2,8);
    attach(main,btn("▶ ရုပ်ပြသင်ခန်းစာများ စတင်ဖတ်ရန်",GOLD,INK,()->library("")),57,8);
    feature("▣  မူရင်း Comic Reader","ဖုန်းမှ မူရင်း ၂၄ မျက်နှာ PDF ကို တစ်ကြိမ်ရွေးပြီး App အတွင်းဖတ်ရန်",TEAL,()->openReader(prefs.getInt("last_lesson",0)));
    feature("☀  နေ့စဉ်စာဆောင် Channel","နေ့စဉ်ပဋ္ဌာန်း Lesson၊ ရုပ်ပြ၊ အသံ၊ မှတ်စု၊ ၁၄ ရက် Archive",GOLD,()->startActivity(new Intent(this,DailyReadingChannelActivity.class)));
    feature("📚  Google Drive eLibrary · စာအုပ် ၁၀ အုပ်","စာအုပ်အမည် ကြိုတင်ထည့်ထားပြီး App အတွင်း PDF ဖတ်ရန်",TEAL,()->startActivity(new Intent(this,DriveBookCatalogActivity.class)));
    feature("☷  ပစ္စည်း ၂၄ မျိုး","အမည်ရှာခြင်း၊ အဓိပ္ပာယ်၊ တစ်ရွက်ချင်း ရွေးဖတ်ခြင်း",MAROON,()->library(""));
    feature("★  Bookmark & Progress","နောက်ဆုံးဖတ်ခဲ့သည့်စာမျက်နှာ၊ အကြိုက်ဆုံးသင်ခန်းစာများ",TEAL,this::progress);
    feature("♫  အသံဖတ်ပြခြင်း","ဖုန်းတွင် မြန်မာ TTS ရှိလျှင် ပါဠိအမည်နှင့် အဓိပ္ပာယ်ကိုဖတ်ပြ",TEAL,()->detail(prefs.getInt("last_lesson",0)));
    label("မှတ်ချက် — Comic Artwork ကို APK ထဲ မပါဝင်သေးပါ။ မူရင်း PDF ကို ဖုန်းမှ တစ်ကြိမ်ရွေးရပါမယ်။",13,false);
  }
  private void library(String filter){
    lastMode="library";start("LESSON LIBRARY");
    label("ပဋ္ဌာန်း ၂၄ ပစ္စည်း",23,true);
    EditText input=new EditText(this);
    input.setSingleLine(true);input.setTextSize(14);input.setHint("ပစ္စည်းအမည် ရှာရန် ...");
    input.setBackground(outline(Color.WHITE,14,GOLD));padding(input,16,12);input.setText(filter);
    attach(main,input,53,4);
    LinearLayout listing=new LinearLayout(this);listing.setOrientation(LinearLayout.VERTICAL);attach(main,listing,-2,6);
    populate(listing,filter);
    input.addTextChangedListener(new TextWatcher(){
      public void beforeTextChanged(CharSequence s,int st,int c,int a){}
      public void onTextChanged(CharSequence s,int st,int before,int count){populate(listing,s.toString());}
      public void afterTextChanged(Editable e){}
    });
  }
  private void populate(LinearLayout listing,String filter){
    listing.removeAllViews();
    for(int i=0;i<24;i++){
      String id=String.format(Locale.US,"%02d",i+1);
      if(!filter.isEmpty()&&!TITLE[i].contains(filter)&&!id.contains(filter))continue;
      final int num=i;LinearLayout card=new LinearLayout(this);
      card.setOrientation(LinearLayout.HORIZONTAL);card.setGravity(Gravity.CENTER_VERTICAL);
      card.setBackground(outline(Color.WHITE,14,Color.rgb(225,212,183)));padding(card,11,10);
      TextView badge=t(id,19,TEAL,true);badge.setGravity(Gravity.CENTER);
      badge.setBackground(bg(Color.rgb(246,218,147),12));
      card.addView(badge,new LinearLayout.LayoutParams(d(52),d(52)));
      LinearLayout info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);padding(info,12,0);
      info.addView(t(TITLE[i],16,INK,true));
      info.addView(t(EXPLAIN[i],12,MUTED,false));
      info.addView(t(prefs.getBoolean("read_"+i,false)?"✓ ဖတ်ပြီး":"▣ Comic Page "+id,12,prefs.getBoolean("read_"+i,false)?TEAL:MAROON,false));
      card.addView(info,new LinearLayout.LayoutParams(0,-2,1));
      TextView arrow=t("›",30,GOLD,true);card.addView(arrow);
      card.setOnClickListener(v->detail(num));attach(listing,card,-2,4);
    }
  }
  private void detail(int num){
    active=Math.max(0,Math.min(23,num));lastMode="detail";start("LESSON "+String.format(Locale.US,"%02d",active+1));
    TextView badge=t("စာမျက်နှာ "+(active+1)+" / 24",16,TEAL,true);attach(main,badge,-2,5);
    label(TITLE[active],25,true);
    label(EXPLAIN[active],17,false);
    feature("▣  မူရင်းရုပ်ပြ ဖတ်ရန်","PPTX/PDF မူရင်း၏ စာမျက်နှာ "+(active+1)+" ကို App အတွင်း ဖွင့်မည်",TEAL,()->openReader(active));
    feature("🔊  အသံဖြင့် နားထောင်ရန်","ဖုန်းရှိ Myanmar Text-to-Speech ဖြင့် ဖတ်ပြမည်",MAROON,()->speak(TITLE[active]+"။ "+EXPLAIN[active]));
    attach(main,btn(prefs.getBoolean("fav_"+active,false)?"★ Bookmark ဖြုတ်ရန်":"☆ Bookmark မှတ်ရန်",GOLD,INK,
      ()->{prefs.edit().putBoolean("fav_"+active,!prefs.getBoolean("fav_"+active,false)).apply();detail(active);}),52,6);
    attach(main,btn("‹ ယခင်ပစ္စည်း",TEAL,IVORY,()->detail((active+23)%24)),48,4);
    attach(main,btn("နောက်ပစ္စည်း ›",TEAL,IVORY,()->detail((active+1)%24)),48,4);
    label("ရုပ်ပြပုံများသည် နားလည်လွယ်ရန် သရုပ်ဖော်ဥပမာများဖြစ်သည်။ အတိအကျ ဓမ္မအဓိပ္ပာယ်ကို မူရင်းစာပေဖြင့် ပြန်လည်စစ်ဆေးပါ။",13,false);
  }
  private void openReader(int num){
    active=Math.max(0,Math.min(23,num));
    prefs.edit().putInt("last_lesson",active).apply();
    Intent intent=new Intent(this,BundledComicReaderActivity.class);
    intent.putExtra("comic_mode",true);intent.putExtra("lesson_page",active);
    startActivity(intent);
  }
  private void progress(){
    lastMode="progress";start("MY READING PROGRESS");
    label("ဖတ်ပြီး   "+completed()+" / 24   ပစ္စည်း",22,true);
    ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
    bar.setMax(24);bar.setProgress(completed());attach(main,bar,14,8);
    for(int i=0;i<24;i++){
      if(prefs.getBoolean("fav_"+i,false)||prefs.getBoolean("read_"+i,false)){
        final int n=i;feature(String.format(Locale.US,"%02d  ",i+1)+TITLE[i],
          (prefs.getBoolean("fav_"+i,false)?"★ ":"")+"စာမျက်နှာ "+(i+1),TEAL,()->detail(n));
      }
    }
    attach(main,btn("☷ စာမျက်နှာစာရင်း",TEAL,IVORY,()->library("")),50,8);
  }
  private void more(){
    lastMode="more";start("MORE LEARNING");
    label("Dhamma Light · Native Comic APK Prototype",21,true);
    feature("စာဖတ် / Quiz / YouTube Links","ယခင် Native Preview သင်ခန်းစာများကို ဆက်လက်အသုံးပြုရန်",TEAL,()->startActivity(new Intent(this,MainActivity.class)));
    feature("☀  နေ့စဉ်စာဆောင် Channel","Daily Reading + Archive + Notes",GOLD,()->startActivity(new Intent(this,DailyReadingChannelActivity.class)));
    feature("📚  Google Drive စာအုပ် ၁၀ အုပ်","Catalog + စာအုပ်တစ်အုပ်ချင်းဖတ်ရန်",TEAL,()->startActivity(new Intent(this,DriveBookCatalogActivity.class)));
    feature("စာအုပ်သီးသန့် PDF Reader","အခြားစာအုပ်ကို Native PDF Reader ဖြင့် ဖတ်ရန်",MAROON,()->startActivity(new Intent(this,NativePdfReaderActivity.class)));
    label("App ထဲတွင် မူရင်း PDF ကို မ Upload/မ Share လုပ်ပါ။ Android File Picker ဖြင့် မိမိဖုန်းမှ ရွေးဖတ်ခြင်းသာ ဖြစ်ပါတယ်။",13,false);
    label("V3.1 Debug Prototype — 24 comic lessons; source is public-safe; device QA remains.",12,false);
  }
  private void speak(String s){
    if(speech!=null){speech.speak(s,TextToSpeech.QUEUE_FLUSH,null,"comic-lesson");return;}
    final String words=s;
    speech=new TextToSpeech(this,status->{
      if(status==TextToSpeech.SUCCESS&&speech!=null){
        int result=speech.setLanguage(new Locale("my","MM"));
        if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED)
          Toast.makeText(this,"မြန်မာ TTS Voice မရှိသေးပါ",Toast.LENGTH_LONG).show();
        else speech.speak(words,TextToSpeech.QUEUE_FLUSH,null,"comic-lesson");
      }else Toast.makeText(this,"Text-to-Speech အလုပ်မလုပ်ပါ",Toast.LENGTH_SHORT).show();
    });
  }
  @Override protected void onResume(){super.onResume();if("progress".equals(lastMode))progress();}
  @Override public void onBackPressed(){if("home".equals(lastMode))super.onBackPressed();else home();}
  @Override protected void onDestroy(){if(speech!=null){speech.stop();speech.shutdown();}super.onDestroy();}
}
