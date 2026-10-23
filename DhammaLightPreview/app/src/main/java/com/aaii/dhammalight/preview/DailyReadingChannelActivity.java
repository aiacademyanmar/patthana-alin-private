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
import java.util.Locale;

/**
 * Daily Reading Channel: OFFLINE, deterministic 24-lesson rotating schedule.
 * Content is original short summaries from the existing ComicPrototypeActivity.
 * No copyrighted book pages or third-party devotional text is republished.
 * No network account, online channel sync, notifications, or push delivery.
 */
public final class DailyReadingChannelActivity extends Activity {
    private static final int TEAL=Color.rgb(6,51,52);
    private static final int TEAL2=Color.rgb(11,79,74);
    private static final int GOLD=Color.rgb(229,180,88);
    private static final int IVORY=Color.rgb(250,245,233);
    private static final int INK=Color.rgb(30,54,52);
    private static final int MUTED=Color.rgb(112,112,97);
    private static final int WHITE=Color.WHITE;
    private static final int ARCHIVE_LIMIT=14;
    private final SimpleDateFormat dayKeyFormat=new SimpleDateFormat("yyyyMMdd",Locale.US);
    private final SimpleDateFormat dateDisplay=new SimpleDateFormat("dd MMM yyyy",Locale.ENGLISH);
    private SharedPreferences prefs;
    private LinearLayout main;
    private LinearLayout root;
    private TextToSpeech tts;
    private int offset=0;
    private boolean archive=false;
    private String todayKey;
    private int d(int x){return (int)(x*getResources().getDisplayMetrics().density+0.5f);}
    private GradientDrawable shape(int color,int radius,int line){
        GradientDrawable v=new GradientDrawable();v.setColor(color);v.setCornerRadius(d(radius));
        if(line!=0)v.setStroke(d(1),line);return v;
    }
    private TextView t(String str,int sp,int color,boolean bold){
        TextView v=new TextView(this);v.setText(str);v.setTextColor(color);v.setTextSize(sp);
        if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;
    }
    private void add(LinearLayout parent,View view,int margin){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);
        lp.setMargins(0,d(margin),0,d(margin));parent.addView(view,lp);
    }
    private Button button(String name,int bg,int fg,Runnable action){
        Button b=new Button(this);b.setAllCaps(false);b.setText(name);b.setTextSize(14);b.setTextColor(fg);
        b.setBackground(shape(bg,12,0));b.setOnClickListener(v->action.run());return b;
    }
    private LinearLayout card(){
        LinearLayout c=new LinearLayout(this);c.setOrientation(LinearLayout.VERTICAL);
        c.setPadding(d(15),d(14),d(15),d(14));
        c.setBackground(shape(WHITE,16,0xffe2d2b7));return c;
    }
    private Calendar date(int daysBack){
        Calendar c=Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,12);c.set(Calendar.MINUTE,0);c.set(Calendar.SECOND,0);
        c.add(Calendar.DAY_OF_YEAR,-daysBack);
        return c;
    }
    private static long daySerial(Calendar c){
        int y=c.get(Calendar.YEAR);int y0=y-1;
        return 365L*y0+y0/4-y0/100+y0/400+c.get(Calendar.DAY_OF_YEAR)-1;
    }
    private int lessonFor(Calendar c){
        return (int)Math.floorMod(daySerial(c),24L);
    }
    private String key(Calendar c){return dayKeyFormat.format(c.getTime());}
    private int completedLast7(){
        int result=0;
        for(int i=0;i<7;i++)if(prefs.getBoolean("done_"+key(date(i)),false))result++;
        return result;
    }
    @Override public void onCreate(Bundle state){
        super.onCreate(state);prefs=getSharedPreferences("dhamma_daily_channel_v331",MODE_PRIVATE);
        getWindow().setStatusBarColor(TEAL);getWindow().setNavigationBarColor(TEAL);
        todayKey=key(date(0));show();
    }
    private void show(){
        root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(IVORY);
        setContentView(root);
        LinearLayout header=new LinearLayout(this);header.setOrientation(LinearLayout.VERTICAL);
        header.setBackgroundColor(TEAL);header.setPadding(d(18),d(15),d(18),d(16));
        header.addView(t("☸  Dhamma Light",24,GOLD,true));
        header.addView(t("နေ့စဉ်စာဆောင် CHANNEL  ·  DAILY READING",12,WHITE,true));
        root.addView(header);
        ScrollView scroll=new ScrollView(this);
        main=new LinearLayout(this);main.setOrientation(LinearLayout.VERTICAL);
        main.setPadding(d(14),d(14),d(14),d(20));scroll.addView(main);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        if(archive)showArchive();else showReading();
        bottomNav();
    }
    private void bottomNav(){
        LinearLayout nav=new LinearLayout(this);nav.setBackgroundColor(TEAL);
        String[] items={"☀\nယနေ့","▤\nယခင်နေ့","☷\nLesson","▣\nရုပ်ပြ"};
        for(int i=0;i<items.length;i++){
            final int index=i;TextView tab=t(items[i],12,GOLD,true);
            tab.setGravity(Gravity.CENTER);tab.setPadding(d(2),d(8),d(2),d(8));
            nav.addView(tab,new LinearLayout.LayoutParams(0,d(65),1));
            tab.setOnClickListener(v->{
                if(index==0){offset=0;archive=false;show();}
                else if(index==1){archive=true;show();}
                else if(index==2)startActivity(new Intent(this,PatthanaBookLessonActivity.class));
                else openComic(lessonFor(date(offset)));
            });
        }root.addView(nav);
    }
    private void showReading(){
        Calendar chosen=date(offset);int idx=lessonFor(chosen);
        String dateId=key(chosen);
        boolean done=prefs.getBoolean("done_"+dateId,false);
        TextView eyebrow=t(offset==0?"● ယနေ့အတွက် စာဆောင်":"◀ ယခင်နေ့ စာဆောင်",13,0xff9f7329,true);add(main,eyebrow,2);
        TextView day=t(dateDisplay.format(chosen.getTime()),22,INK,true);add(main,day,4);
        LinearLayout hero=new LinearLayout(this);hero.setOrientation(LinearLayout.VERTICAL);
        hero.setPadding(d(18),d(18),d(18),d(18));hero.setBackground(shape(TEAL2,18,GOLD));
        hero.addView(t("နေ့စဉ်ပဋ္ဌာန်း — သင်ခန်းစာ "+(idx+1)+" / 24",13,GOLD,true));
        hero.addView(t(ComicPrototypeActivity.lessonName(idx),24,WHITE,true));
        hero.addView(t(ComicPrototypeActivity.lessonMeaning(idx),16,0xffffeac5,false));
        add(main,hero,9);
        TextView source=t("အက်ပ်အတွက် ရေးထားသော သင်ယူရေးအကျဉ်းချုပ် — စာအုပ်မူရင်းမှ စာသားတိုက်ရိုက်ကူးယူထားခြင်း မဟုတ်ပါ။",12,MUTED,false);
        source.setPadding(d(5),d(5),d(5),d(5));add(main,source,3);
        LinearLayout prompts=card();
        prompts.addView(t("✦ ဒီနေ့ လေ့လာရန်",19,INK,true));
        add(prompts,t("၁။ ပစ္စည်းအမည်နဲ့ အဓိပ္ပာယ်ကို သေချာဖတ်ပါ။",14,INK,false),5);
        add(prompts,t("၂။ မူရင်းရုပ်ပြ Lesson ကို ကြည့်ပြီး ဆက်စပ်မှုကို မှတ်သားပါ။",14,INK,false),5);
        add(prompts,t("၃။ မိမိနားလည်ထားတာကို အောက်မှာ စာတိုရေးပါ။",14,INK,false),5);
        add(main,prompts,8);
        LinearLayout actions=new LinearLayout(this);
        Button comic=button("▣ ရုပ်ပြကြည့်",TEAL,WHITE,()->openComic(idx));
        Button listen=button("♫ အသံဖတ်ပြ",GOLD,INK,()->readAloud(idx));
        LinearLayout.LayoutParams a=new LinearLayout.LayoutParams(0,d(56),1);a.setMargins(0,0,d(5),0);
        actions.addView(comic,a);
        LinearLayout.LayoutParams b=new LinearLayout.LayoutParams(0,d(56),1);b.setMargins(d(5),0,0,0);
        actions.addView(listen,b);add(main,actions,6);
        Button book=button("▤ ဂျေဂျူဝိုင် စာအုပ် / Lesson Map",WHITE,TEAL,()->startActivity(new Intent(this,PatthanaBookLessonActivity.class)));
        add(main,book,4);
        LinearLayout noteCard=card();
        noteCard.addView(t("✎ နားလည်ချက် မှတ်စု",17,INK,true));
        EditText notes=new EditText(this);
        notes.setTextSize(14);notes.setMinLines(3);notes.setGravity(Gravity.TOP);
        notes.setHint("ဒီနေ့ သင်ယူမှုအကြောင်း ရေးရန်...");
        notes.setText(prefs.getString("note_"+dateId,""));
        notes.setBackground(shape(IVORY,12,0xffe4d6bd));
        notes.setPadding(d(12),d(10),d(12),d(10));
        add(noteCard,notes,8);
        add(noteCard,button("မှတ်စု သိမ်းဆည်းရန်",TEAL,WHITE,()->{
            prefs.edit().putString("note_"+dateId,notes.getText().toString()).apply();
            Toast.makeText(this,"နေ့စဉ်မှတ်စု သိမ်းဆည်းပြီး",Toast.LENGTH_SHORT).show();
        }),8);
        add(main,noteCard,8);
        add(main,button(done?"✓ ဖတ်ပြီးသား — ပြန်လည်သင်ယူရန်":"✓ ဒီနေ့စာဆောင် ဖတ်ပြီး",done?0xffdbe9db:GOLD,INK,()->{
            prefs.edit().putBoolean("done_"+dateId,true).apply();show();
        }),8);
        add(main,button("↗ စာဆောင်အကျဉ်းချုပ် မျှဝေရန်",WHITE,TEAL,()->share(idx,chosen)),5);
        LinearLayout progress=card();progress.addView(t("ပြီးခဲ့သော ၇ ရက်အတွင်း ဖတ်ပြီး  "+completedLast7()+" / 7",14,INK,true));
        ProgressBar bar=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal);
        bar.setMax(7);bar.setProgress(completedLast7());add(progress,bar,8);add(main,progress,7);
        LinearLayout browse=new LinearLayout(this);
        if(offset<ARCHIVE_LIMIT){
            Button older=button("‹ ယခင်နေ့",TEAL,WHITE,()->{offset++;archive=false;show();});
            browse.addView(older,new LinearLayout.LayoutParams(0,d(51),1));
        }
        if(offset>0){
            Button newer=button("နောက်နေ့ ›",GOLD,INK,()->{offset--;archive=false;show();});
            browse.addView(newer,new LinearLayout.LayoutParams(0,d(51),1));
        }
        if(browse.getChildCount()>0)add(main,browse,8);
        add(main,t("OFFLINE PROTOTYPE: နေ့စဉ် ၂၄ ပစ္စည်းကို အလှည့်ကျပြသသည်။ Live Channel၊ Server Sync၊ Push Notification မဟုတ်သေးပါ။",12,MUTED,false),9);
    }
    private void showArchive(){
        add(main,t("စာဆောင် မှတ်တမ်း",23,INK,true),1);
        add(main,t("ယနေ့မှ နောက်ပြန် ၁၄ ရက်အထိ စာဆောင်များ",13,MUTED,false),2);
        for(int i=0;i<=ARCHIVE_LIMIT;i++){
            Calendar c=date(i);int idx=lessonFor(c);boolean complete=prefs.getBoolean("done_"+key(c),false);
            LinearLayout item=card();item.setPadding(d(14),d(13),d(14),d(13));
            item.addView(t((i==0?"ယနေ့ · ":"")+dateDisplay.format(c.getTime()),12,0xff94661e,true));
            item.addView(t(ComicPrototypeActivity.lessonName(idx),17,TEAL,true));
            item.addView(t(complete?"✓ ဖတ်ပြီး":"စာဆောင်ဖတ်ရန် ›",12,complete?0xff24734d:MUTED,true));
            final int days=i;
            item.setOnClickListener(v->{offset=days;archive=false;show();});
            add(main,item,6);
        }
    }
    private void openComic(int idx){
        Intent comic=new Intent(this,BundledComicReaderActivity.class);
        comic.putExtra("comic_mode",true);
        comic.putExtra("lesson_page",idx);
        startActivity(comic);
    }
    private void readAloud(int idx){
        String value=ComicPrototypeActivity.lessonName(idx)+"။ "+ComicPrototypeActivity.lessonMeaning(idx);
        if(tts!=null){tts.speak(value,TextToSpeech.QUEUE_FLUSH,null,"daily-reading");return;}
        tts=new TextToSpeech(this,result->{
            if(result!=TextToSpeech.SUCCESS||tts==null){
                Toast.makeText(this,"အသံဖတ်ပြစနစ် မရရှိပါ",Toast.LENGTH_LONG).show();return;
            }
            int language=tts.setLanguage(new Locale("my","MM"));
            if(language==TextToSpeech.LANG_MISSING_DATA||language==TextToSpeech.LANG_NOT_SUPPORTED)
                Toast.makeText(this,"Myanmar TTS Voice ထည့်သွင်းရန် လိုအပ်ပါတယ်",Toast.LENGTH_LONG).show();
            else tts.speak(value,TextToSpeech.QUEUE_FLUSH,null,"daily-reading");
        });
    }
    private void share(int idx,Calendar chosen){
        String note="Dhamma Light — နေ့စဉ်စာဆောင် ("+dateDisplay.format(chosen.getTime())+")\n"
            +ComicPrototypeActivity.lessonName(idx)+"\n"
            +ComicPrototypeActivity.lessonMeaning(idx)+"\n"
            +"အက်ပ်၏ သင်ယူရေးအကျဉ်းချုပ် (မူရင်းစာအုပ်မှ ကိုးကားချက်မဟုတ်)";
        Intent send=new Intent(Intent.ACTION_SEND);send.setType("text/plain");
        send.putExtra(Intent.EXTRA_TEXT,note);startActivity(Intent.createChooser(send,"စာဆောင်မျှဝေရန်"));
    }
    @Override protected void onResume(){
        super.onResume();
        if(prefs!=null&&!todayKey.equals(key(date(0)))){todayKey=key(date(0));offset=0;archive=false;show();}
    }
    @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();}super.onDestroy();}
}
