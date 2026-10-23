package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Native offline 24-day reading program.
 * Texts below are new educational overviews; they are NOT verbatim transcriptions
 * or quotations from the historic scanned "မြတ်ပဋ္ဌန်း" PDF.
 */
public final class DailyReadingActivity extends Activity {
  private static final int TEAL=Color.rgb(3,46,46), GOLD=Color.rgb(231,180,70);
  private static final int IVORY=Color.rgb(255,248,234), BROWN=Color.rgb(69,53,40);
  private static final int GREY=Color.rgb(117,103,84);
  private static final String MYAT_ID="1oWP7KpXcU2t8py_VkZyW-jNgdZIpj4iB";
  private static final String[] THINK={
    "အခြေတည်စေသော အကြောင်းတရားများကို သတိထားလေ့လာပါ။",
    "မြင်၊ ကြား၊ သိသော အာရုံတို့နှင့် စိတ်၏ ဆက်စပ်မှုကို သတိပြုပါ။",
    "အားကောင်းသော အရာတစ်ခု၏ ဦးဆောင်မှုကို တွေးဆကြည့်ပါ။",
    "စိတ်ဖြစ်စဉ်တစ်ခုနောက် တစ်ခု ဆက်ဖြစ်ပုံကို အကျဉ်းသဘောတရားအဖြစ် လေ့လာပါ။",
    "အကြားမရှိ ဆက်တိုက်ကူညီပေးခြင်း၏ သဘောကို သင်ယူပါ။",
    "အတူတကွ ဖြစ်ပေါ်သော တရားတို့ကို ခွဲခြားသိရှိရန် ကြိုးစားပါ။",
    "အပြန်အလှန် အထောက်အပံ့ပြုမှုကို လေ့လာပါ။",
    "မှီခိုရာရှိမှ ဖြစ်ပေါ်နိုင်သော အခြေအနေများကို အကြောင်း-အကျိုး သဘောဖြင့် ကြည့်ပါ။",
    "အားကောင်းသော မှီခိုရာ အထောက်အပံ့မျိုးကို သတိပြုပါ။",
    "ရှေးဦးစွာ ဖြစ်ပြီးသားတရား၏ ထောက်ပံ့မှုကို လေ့လာပါ။",
    "နောက်မှ ဖြစ်လာသောတရား၏ ထောက်ပံ့မှုကို လေ့လာပါ။",
    "အဖန်ဖန်ဖြစ်မှုနှင့် အလေ့အကျင့်၏ ဆက်စပ်မှုကို နားလည်ရန် ကြိုးစားပါ။",
    "စေတနာ၏ အခန်းကဏ္ဍကို စာအုပ်အကိုးအကားဖြင့် ဆက်လေ့လာပါ။",
    "အကျိုးဖြစ်စေသည့် ဝိပါကတရားများအကြောင်း လေ့လာပါ။",
    "အာဟာရအဖြစ် ထောက်ပံ့ပုံကို နာမ်နှင့်ရုပ် ခွဲခြားစဉ်းစားပါ။",
    "မိမိကဏ္ဍတွင် အစိုးရသော ဣန္ဒြိယတရားတို့ကို လေ့လာပါ။",
    "အာရုံကို စူးစိုက်စေသော ဈာနင်္ဂများ၏ သဘောကို လေ့လာပါ။",
    "လမ်းပြသဖွယ် ထောက်ပံ့သော မဂ္ဂင်တရားများအကြောင်း လေ့လာပါ။",
    "ယှဉ်တွဲဖြစ်ပေါ်သည့် တရားတို့၏ ဆက်နွယ်မှုကို လေ့လာပါ။",
    "မယှဉ်စပ်ဘဲ ထောက်ပံ့ပုံကို မူရင်းအဓိပ္ပာယ်နှင့် တိုက်စစ်ပါ။",
    "ရှိနေခြင်းဖြင့် အထောက်အပံ့ဖြစ်ပုံကို လေ့လာပါ။",
    "မရှိတော့ခြင်းဖြင့် ထောက်ပံ့ပုံကို လေ့လာပါ။",
    "ကွယ်ပျောက်ခြင်းဖြင့် ထောက်ပံ့ပုံကို လေ့လာပါ။",
    "မကွယ်ပျောက်ခြင်းဖြင့် ထောက်ပံ့ပုံကို ပြန်လည်သုံးသပ်ပါ။"
  };
  private static final String[] REFLECT={
    "အခြေအနေတစ်ခု၏ အခြေခံအကြောင်းရင်းကို မေးခွန်းထုတ်ကြည့်ပါ။",
    "ယနေ့ သတိပြုမိသော အာရုံတစ်ခုကို ကိုယ်ပိုင်မှတ်စုရေးပါ။",
    "မိမိက အရေးပေးထားသည့် အရာတစ်ခုကို စဉ်းစားပါ။",
    "အစဉ်လိုက်ဖြစ်ပေါ်ပုံကို ရှင်းလင်းစွာ မိမိစကားဖြင့် မှတ်စုရေးပါ။",
    "အချိန်အစဉ်အဆက်ကို အဆင့် ၂ ဆင့်ဖြင့် ရှင်းပြကြည့်ပါ။",
    "တစ်ပြိုင်တည်း ကူညီပံ့ပိုးနေသည့် အရာနှစ်ခုကို ဥပမာတစ်ခုအဖြစ် စဉ်းစားပါ။",
    "အချင်းချင်း ကူညီမှုကို မိမိစကားဖြင့် ရှင်းပြပါ။",
    "မှီခိုရာအကြောင်းတစ်ခုကို ရေးမှတ်ပါ။",
    "အင်အားကြီးသော အထောက်အပံ့၏ အဓိပ္ပာယ်ကို ပြန်ဖတ်ပါ။",
    "ရှေ့ဖြစ်-နောက်ဖြစ် ကွဲပြားပုံကို နှိုင်းယှဉ်ပါ။",
    "နောက်မှပံ့ပိုးမှုဆိုသည်ကို မူရင်းစာအုပ်တွင် ပြန်စစ်ပါ။",
    "ဖတ်ပြီးနောက် အကျဉ်းချုပ်ကို နှစ်ကြောင်းရေးပါ။",
    "စေတနာနှင့် ပတ်သက်သည့် ယနေ့သင်ခန်းစာအချက်ကို မှတ်စုရေးပါ။",
    "အကျိုးနှင့် အကြောင်းကို မရောထွေးရန် ပြန်လည်ဆင်ခြင်ပါ။",
    "အာဟာရပစ္စည်း၏ သဘောကို ပြန်လည်ဖတ်ရှုပါ။",
    "ဣန္ဒြိယတရား၏ အခန်းကဏ္ဍကို မှတ်သားပါ။",
    "စူးစိုက်မှုအကြောင်း အကျဉ်းချုပ်ရေးပါ။",
    "လမ်းမှန်သို့ ဦးတည်ခြင်းအကြောင်း ပြန်လည်ဆင်ခြင်ပါ။",
    "ယှဉ်တွဲခြင်း၏ သဘောကို မှတ်သားပါ။",
    "မယှဉ်တွဲခြင်းအကြောင်း မူရင်းစာအုပ်ကို ဆက်လေ့လာပါ။",
    "ရှိခြင်းက ထောက်ပံ့မှုဖြစ်သည့် သဘောကို ရှင်းပြကြည့်ပါ။",
    "မရှိခြင်းက ထောက်ပံ့မှုဖြစ်သည့် သဘောကို ပြန်ဖတ်ပါ။",
    "ကွယ်ပျောက်ခြင်း၏ သဘောကို မူရင်းအရ သုံးသပ်ပါ။",
    "၂၄ ပစ္စည်းအားလုံးကို အစဉ်လိုက် ပြန်လည်လေ့လာပါ။"
  };
  private SharedPreferences prefs;
  private TextToSpeech tts;
  private LinearLayout root,body;
  private int selected;
  private String todayKey;

  @Override public void onCreate(Bundle state){
    super.onCreate(state);
    getWindow().setStatusBarColor(TEAL);
    getWindow().setNavigationBarColor(TEAL);
    prefs=getSharedPreferences("dhamma_daily_reading_v41",MODE_PRIVATE);
    Calendar c=Calendar.getInstance();
    selected=(c.get(Calendar.DAY_OF_YEAR)-1)%24;
    todayKey=new SimpleDateFormat("yyyy-MM-dd",Locale.US).format(new Date());
    render();
  }
  private int dp(float v){return (int)(getResources().getDisplayMetrics().density*v+0.5f);}
  private GradientDrawable bg(int color,int radius){
    GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;
  }
  private TextView text(String s,int sp,int color,boolean bold){
    TextView x=new TextView(this);x.setText(s);x.setTextSize(sp);x.setTextColor(color);
    if(bold)x.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return x;
  }
  private void add(View v,int margin){
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,dp(margin),0,dp(margin));body.addView(v,p);
  }
  private Button button(String name,int color,int textColor,Runnable work){
    Button b=new Button(this);b.setAllCaps(false);b.setText(name);b.setTextSize(14);
    b.setTextColor(textColor);b.setBackground(bg(color,12));b.setOnClickListener(v->work.run());return b;
  }
  private TextView card(String message,int size,boolean strong){
    TextView x=text(message,size,BROWN,strong);x.setPadding(dp(16),dp(13),dp(16),dp(13));
    x.setBackground(bg(Color.WHITE,13));return x;
  }
  private String passage(){
    int n=selected;
    return "ပစ္စည်း "+String.format(Locale.US,"%02d",n+1)+" — "+ComicPrototypeActivity.lessonName(n)+
      "\n\nအဓိပ္ပာယ်အကျဉ်း — "+ComicPrototypeActivity.lessonMeaning(n)+
      "။\n\nယနေ့ လေ့လာရန် — "+THINK[n]+"\n\nမူရင်းကျမ်းစာပိုဒ်ကို အတိအကျ မကူးယူထားပါ။";
  }
  private int doneCount(){
    int x=0;for(int i=0;i<24;i++)if(prefs.getBoolean("lesson_"+i,false))x++;return x;
  }
  private void render(){
    root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(IVORY);setContentView(root);
    LinearLayout head=new LinearLayout(this);head.setPadding(dp(15),dp(15),dp(15),dp(15));head.setOrientation(LinearLayout.VERTICAL);head.setBackgroundColor(TEAL);
    head.addView(text("☸  Dhamma Light",24,GOLD,true));
    head.addView(text("နေ့စဉ်ဖတ်စာ  •  ၂၄ ပစ္စည်း",17,Color.WHITE,true));root.addView(head);
    ScrollView scroll=new ScrollView(this);
    body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);
    body.setPadding(dp(14),dp(12),dp(14),dp(18));scroll.addView(body);
    root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    int originalDay=(Calendar.getInstance().get(Calendar.DAY_OF_YEAR)-1)%24;
    add(card("📅 ယနေ့  "+todayKey+"     |     "+(selected+1)+" / 24",15,true),3);
    TextView heading=text(ComicPrototypeActivity.lessonName(selected),24,TEAL,true);heading.setPadding(dp(8),dp(12),dp(8),dp(6));add(heading,2);
    add(card("ဖတ်စာ — ယနေ့အကျဉ်း\n\n"+passage(),17,false),5);
    add(card("ကိုယ်တိုင် လေ့ကျင့်ရန်\n\n"+REFLECT[selected],16,false),5);
    add(card("📖 မူရင်းစာအုပ် ကိုးကားချက်\nမြတ်ပဋ္ဌန်း · Scan PDF ၂၈ မျက်နှာ\nပစ္စယောအမည်များနှင့် ဆက်စပ်ပါဠိကို မူရင်းစာအုပ်တွင် တိုက်စစ်ဖတ်ပါ။ တိကျသည့် စာပိုဒ်-စာမျက်နှာ Mapping ကို စိစစ်နေဆဲဖြစ်သည်။",14,false),5);
    String saved=prefs.getString("note_"+selected,"");
    add(button(saved.isEmpty()?"✍ ကိုယ်ပိုင်မှတ်စုရေးမည်":"✍ မှတ်စုကြည့် / ပြင်မည်",Color.WHITE,TEAL,this::editNote),3);
    add(button("🔊 မြန်မာအသံ ဖတ်ပြပါ",TEAL,Color.WHITE,()->readAloud(passage())),3);
    add(button("📚 မြတ်ပဋ္ဌန်း PDF ကို Native Reader မှ ဖွင့်မည်",TEAL,Color.WHITE,this::openSource),3);
    add(button(prefs.getBoolean("lesson_"+selected,false)?"✓ ဖတ်ပြီး — ပြန်ဖတ်နိုင်သည်":"✓ ယနေ့ ဖတ်ပြီးမှတ်သားမည်",GOLD,BROWN,()->{
      prefs.edit().putBoolean("lesson_"+selected,true).putString("done_date_"+selected,todayKey).apply();
      Toast.makeText(this,"ဖတ်ပြီးမှတ်တမ်း သိမ်းပြီးပါပြီ",Toast.LENGTH_SHORT).show();render();
    }),3);
    ProgressBar progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
    progress.setMax(24);progress.setProgress(doneCount());add(progress,8);
    TextView count=text("ဖတ်ပြီးသော ပစ္စည်းများ  "+doneCount()+" / 24",14,BROWN,true);
    count.setGravity(Gravity.CENTER);add(count,3);
    LinearLayout row=new LinearLayout(this);
    row.addView(button("◀ ယခင်",TEAL,Color.WHITE,()->{selected=(selected+23)%24;render();}),new LinearLayout.LayoutParams(0,dp(52),1));
    row.addView(button("ယနေ့",GOLD,BROWN,()->{selected=originalDay;render();}),new LinearLayout.LayoutParams(0,dp(52),1));
    row.addView(button("နောက် ▶",TEAL,Color.WHITE,()->{selected=(selected+1)%24;render();}),new LinearLayout.LayoutParams(0,dp(52),1));add(row,8);
    add(button("▤  ပစ္စယော ၂၄ မျိုး စာရင်း",Color.WHITE,TEAL,this::chooseLesson),3);
    TextView note=text("ဤနေ့စဉ်ဖတ်စာများမှာ စာအုပ်ပါ စာသားအပြည့်ပြန်ကူးထားခြင်းမဟုတ်ဘဲ App အတွက် သင်ယူရေးအကျဉ်း မူကြမ်းဖြစ်သည်။ ပါဠိနှင့် အသေးစိတ်အဓိပ္ပာယ်ကို မူရင်းစာပေနှင့် ကျွမ်းကျင်သူစိစစ်မှု လိုအပ်သည်။",12,GREY,false);
    add(note,8);
  }
  private void editNote(){
    EditText editor=new EditText(this);editor.setMinLines(4);
    editor.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE);
    editor.setText(prefs.getString("note_"+selected,""));
    new AlertDialog.Builder(this).setTitle("သင်ခန်းစာ မှတ်စု")
      .setView(editor).setPositiveButton("သိမ်းမည်",(dialog,which)->{
         prefs.edit().putString("note_"+selected,editor.getText().toString()).apply();render();
      }).setNegativeButton("မသိမ်းပါ",null).show();
  }
  private void chooseLesson(){
    String[] names=new String[24];
    for(int i=0;i<24;i++)names[i]=String.format(Locale.US,"%02d  ",i+1)+ComicPrototypeActivity.lessonName(i);
    new AlertDialog.Builder(this).setTitle("နေ့စဉ်ဖတ်စာ — ၂၄ ပစ္စည်း")
      .setItems(names,(dialog,id)->{selected=id;render();}).show();
  }
  private void openSource(){
    Intent in=new Intent(this,NativePdfReaderActivity.class);
    in.putExtra("book_id",MYAT_ID);
    in.putExtra("book_title","မြတ်ပဋ္ဌန်း.pdf");
    startActivity(in);
  }
  private void readAloud(String words){
    if(tts!=null){tts.speak(words,TextToSpeech.QUEUE_FLUSH,null,"daily-reading");return;}
    tts=new TextToSpeech(this,code->{
      if(code!=TextToSpeech.SUCCESS||tts==null){Toast.makeText(this,"အသံဖတ်ပြစနစ် မရနိုင်ပါ",Toast.LENGTH_LONG).show();return;}
      int result=tts.setLanguage(new Locale("my","MM"));
      if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
        Toast.makeText(this,"ဖုန်းတွင် Myanmar TTS Voice ထည့်ရန် လိုအပ်သည်",Toast.LENGTH_LONG).show();
      }else tts.speak(words,TextToSpeech.QUEUE_FLUSH,null,"daily-reading");
    });
  }
  @Override protected void onDestroy(){
    if(tts!=null){tts.stop();tts.shutdown();tts=null;}
    super.onDestroy();
  }
}
