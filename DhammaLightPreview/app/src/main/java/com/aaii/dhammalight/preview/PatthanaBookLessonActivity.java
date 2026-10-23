package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

/** Navigation-only source map. The author's PDF and excerpts are not shipped in the APK.
 * PDF page starts were inspected against the 35-page JJQQYY PDF.
 */
public final class PatthanaBookLessonActivity extends Activity {
  private static final int TEAL=0xff073d3e, GOLD=0xffe7b75d, IVORY=0xfffcf7ee;
  private static final int[] START={2,3,5,7,8,9,11,12,13,14,15,16,19,21,23,25,27,28,29,31,32,32,33,34};
  private static final String BOOK_ID="1AH_0-srHSfWYzslnTolS4XQk8crWCaw8";
  private static final String BOOK_NAME="ပဋ္ဌာန်း (ဂျေဂျူဝိုင် ).pdf";
  private LinearLayout rows;
  private int d(int x){return (int)(x*getResources().getDisplayMetrics().density+0.5f);}
  private TextView txt(String value,int size,int color,boolean bold){
    TextView v=new TextView(this);v.setText(value);v.setTextColor(color);v.setTextSize(size);
    if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;
  }
  private GradientDrawable panel(int color){
    GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(d(16));g.setStroke(d(1),GOLD);return g;
  }
  @Override public void onCreate(Bundle state){
    super.onCreate(state);getWindow().setStatusBarColor(TEAL);getWindow().setNavigationBarColor(TEAL);
    LinearLayout root=new LinearLayout(this);root.setBackgroundColor(IVORY);root.setOrientation(LinearLayout.VERTICAL);setContentView(root);
    LinearLayout top=new LinearLayout(this);top.setBackgroundColor(TEAL);top.setOrientation(LinearLayout.VERTICAL);
    top.setPadding(d(17),d(19),d(17),d(18));
    top.addView(txt("☸  ပဋ္ဌာန်းစာအုပ်မှ သင်ခန်းစာများ",20,GOLD,true));
    top.addView(txt("ဂျေဂျူဝိုင် PDF · ၃၅ မျက်နှာ · ၂၄ ပစ္စည်း",13,Color.WHITE,false));
    root.addView(top);
    ScrollView scroll=new ScrollView(this);rows=new LinearLayout(this);rows.setOrientation(LinearLayout.VERTICAL);
    rows.setPadding(d(14),d(13),d(14),d(28));scroll.addView(rows);
    root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    TextView expl=txt("ဤစာရင်းသည် မူရင်း PDF ကို အခန်းအလိုက်ဖတ်ရန် လမ်းညွှန်သာဖြစ်ပြီး မူရင်း PDF/စာသားကို APK ထဲမထည့်ထားပါ။ စာမျက်နှာအချို့တွင် အခန်းနှစ်ခု စတင်နိုင်ပါသည်။",12,0xff725e41,false);
    expl.setPadding(d(13),d(10),d(13),d(8));rows.addView(expl);
    for(int i=0;i<24;i++)addLesson(i);
    Button close=new Button(this);close.setAllCaps(false);close.setText("‹ eLibrary သို့ပြန်ရန်");close.setOnClickListener(v->finish());
    root.addView(close);
  }
  private void addLesson(int lesson){
    LinearLayout item=new LinearLayout(this);item.setOrientation(LinearLayout.VERTICAL);
    item.setBackground(panel(Color.WHITE));item.setPadding(d(15),d(10),d(15),d(10));
    item.addView(txt(String.format(java.util.Locale.US,"%02d  ",lesson+1)+ComicPrototypeActivity.lessonName(lesson),17,TEAL,true));
    item.addView(txt(ComicPrototypeActivity.lessonMeaning(lesson),12,0xff756c5f,false));
    int end=lesson==23?35:Math.max(START[lesson],START[lesson+1]);
    item.addView(txt("စာအုပ် PDF p. "+START[lesson]+"–"+end,12,0xffa07938,true));
    LinearLayout buttons=new LinearLayout(this);buttons.setGravity(Gravity.CENTER_VERTICAL);
    Button read=new Button(this);read.setAllCaps(false);read.setText("စာအုပ်၌ ဖတ်ရန်");
    read.setOnClickListener(v->{
      Intent in=new Intent(this,NativePdfReaderActivity.class);
      in.putExtra("book_id",BOOK_ID);in.putExtra("book_title",BOOK_NAME);
      in.putExtra("lesson_page",START[lesson]-1);startActivity(in);
    });
    buttons.addView(read,new LinearLayout.LayoutParams(0,d(48),1));
    Button comic=new Button(this);comic.setAllCaps(false);comic.setText("ရုပ်ပြ");
    comic.setOnClickListener(v->{
      Intent in=new Intent(this,BundledComicReaderActivity.class);
      in.putExtra("comic_mode",true);in.putExtra("lesson_page",lesson);startActivity(in);
    });
    buttons.addView(comic,new LinearLayout.LayoutParams(0,d(48),1));
    item.addView(buttons);
    LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,0,0,d(9));rows.addView(item,lp);
  }
}
