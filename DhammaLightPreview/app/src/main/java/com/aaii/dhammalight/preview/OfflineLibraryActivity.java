package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.io.IOException;

/** Local-only bookshelf. JJYY PDF is installed privately in the final assembled APK.
 * Other books are user-imported with Android's persistent document picker.
 * Nothing is fetched from Google Drive by this Activity.
 */
public final class OfflineLibraryActivity extends Activity{
 private static final int TEAL=0xff063a3a, GOLD=0xffeab95b, IVORY=0xfffcf8ee, INK=0xff173e3c;
 private LinearLayout main;
 private int d(int n){return (int)(getResources().getDisplayMetrics().density*n+0.5f);}
 private TextView text(String t,int sp,int color,boolean bold){
  TextView v=new TextView(this);v.setText(t);v.setTextSize(sp);v.setTextColor(color);
  if(bold)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;
 }
 private GradientDrawable rounded(int color){
  GradientDrawable bg=new GradientDrawable();bg.setColor(color);bg.setCornerRadius(d(18));bg.setStroke(d(1),GOLD);return bg;
 }
 private void add(View child,int margin){
  LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,d(margin),0,d(margin));main.addView(child,p);
 }
 private void panel(String title,String info,String button,Runnable onClick){
  LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setPadding(d(15),d(16),d(15),d(16));
  card.setBackground(rounded(Color.WHITE));card.addView(text(title,19,INK,true));
  TextView desc=text(info,13,0xff78684c,false);desc.setPadding(0,d(7),0,d(6));card.addView(desc);
  Button action=new Button(this);action.setAllCaps(false);action.setText(button);action.setOnClickListener(v->onClick.run());
  card.addView(action);add(card,8);
 }
 private boolean hasBundled(String name){
  try(java.io.InputStream stream=getAssets().open(name)){return stream.read()!=-1;}catch(IOException e){return false;}
 }
 @Override public void onCreate(Bundle state){
  super.onCreate(state);getWindow().setStatusBarColor(TEAL);getWindow().setNavigationBarColor(TEAL);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(IVORY);
  setContentView(root);
  LinearLayout head=new LinearLayout(this);head.setOrientation(LinearLayout.VERTICAL);
  head.setPadding(d(18),d(20),d(18),d(20));head.setBackgroundColor(TEAL);
  head.addView(text("☸  Dhamma Light eLibrary",22,GOLD,true));
  head.addView(text("OFFLINE READING LIBRARY",12,IVORY,true));root.addView(head);
  ScrollView scroll=new ScrollView(this);main=new LinearLayout(this);main.setOrientation(LinearLayout.VERTICAL);
  main.setPadding(d(16),d(15),d(16),d(25));scroll.addView(main);
  root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
  add(text("ဖုန်းအတွင်းမှာပဲ စာအုပ်ဖတ်ရှုနိုင်သော စာအုပ်စင်",19,INK,true),5);
  final boolean bundled=hasBundled("books/patthana_jjyy.pdf");
  panel("ပဋ္ဌာန်း — ဂျေဂျူဝိုင်",bundled?
   "မူရင်း PDF · ၃၅ မျက်နှာ · APK အတွင်းပါဝင်ပြီး · အင်တာနက်မလို":"မူရင်းစာအုပ်ကို ဖုန်းမှ ရွေးပြီး Offline ဖတ်ရှုရန်",
   bundled?"▣ Offline စာအုပ်ဖတ်ရန်":"▣ PDF ရွေးချယ်ရန်",
   ()->{Intent i=new Intent(this,NativePdfReaderActivity.class);
     i.putExtra("book_title","ပဋ္ဌာန်း — ဂျေဂျူဝိုင်");
     i.putExtra("book_id","patthana_jjyy");
     if(bundled)i.putExtra("asset_pdf","books/patthana_jjyy.pdf");
     startActivity(i);});
  panel("ပဋ္ဌာန်း ရုပ်ပြ A5 — ၂၄ မျက်နှာ","ပေးထားသော Comic PowerPoint မှ ၂၄ မျက်နှာလုံးကို Offline ကြည့်ရန်",
   "▣ ရုပ်ပြဖတ်ရန်",()->startActivity(new Intent(this,BundledComicReaderActivity.class)));
  panel("Unicode Offline Text — ပစ္စည်း ၂၄ မျိုး","အကျဉ်းချုပ်စာသားကို Native App ထဲမှာပဲ အင်တာနက်မလိုဖတ်ရန်","☷ Offline Text Reader",()->startActivity(new Intent(this,NativeTextLessonActivity.class)));
  panel("Google Drive eBooks — စာအုပ် ၁၀ အုပ်","Drive PDF ကို App အတွင်း Download ပြီး Native Reader ဖွင့်ရန်","▣ Native eLibrary",()->startActivity(new Intent(this,DriveBookCatalogActivity.class)));
  panel("အခြား ကိုယ်ပိုင်စာအုပ်များ","ဖုန်း/Downloads မှ PDF ကို Android File Picker ဖြင့် ရွေးဖတ်ပြီး Offline ဆက်ဖတ်နိုင်သည်။",
   "＋ PDF ရွေးဖတ်ရန်",()->startActivity(new Intent(this,NativePdfReaderActivity.class)));
  panel("စာအုပ်မှ ၂၄ ပစ္စည်း Lesson Map","ဂျေဂျူဝိုင် စာအုပ်၏ သက်ဆိုင်ရာ PDF စာမျက်နှာကို ရွေးဖတ်ရန်",
   "☷ Lesson Map",()->startActivity(new Intent(this,PatthanaBookLessonActivity.class)));
  add(text("မှတ်ချက်။ ယခင် Google Drive Catalog ရှိ အခြားစာအုပ်များကို APK ထဲတွင် အလိုအလျောက် မသိမ်းထားပါ။ အင်တာနက်မလိုဘဲဖတ်ရန် စာအုပ် PDF ကို ဖုန်းထဲမှာ အရင်သိမ်းထားရပါမယ်။",12,0xff756b57,false),14);
  Button back=new Button(this);back.setAllCaps(false);back.setText("‹ Home");back.setOnClickListener(v->finish());root.addView(back);
 }
}