package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public final class DriveBookCatalogActivity extends Activity {
  public static final String FOLDER_URL="https://drive.google.com/drive/u/0/folders/1hceA6cL4CPrzZqewUrRyFLO-hp57otCo";
  public static final String[] BOOK_TITLES={
    "ရောင်စုံရုပ်ပြပဋ္ဌာန်း ၂၄ ပစ္စည်း.pdf",
    "အခြေပြုပဋ္ဌန်း.pdf",
    "မြတ်ပဋ္ဌန်း.pdf",
    "ပုံပြ ပဋ္ဌန်း.pdf",
    "ပဋ္ဌာန်းအဓိပ္ပါယ်ရှင်းလင်းချက်နှင့်အမေးအဖြေ(ခင်ချိုထွန်း အယ်ဒီတာအဖွဲ့).pdf",
    "ပဋ္ဌာန်းအဓိပ္ပါယ်ရှင်းလင်းချက် pa htan (a kone lone ).pdf",
    "ပဋ္ဌာန်း (ဂျေဂျူဝိုင် ).pdf",
    "ခင်မောင်အေး - ရောင်စုံရုပ်ပြပဋ္ဌာန်း ၂၄ ပစ္စည်းနှင့်ရှင်းလင်းချက်များ.pdf",
    "၂၄ ပစ္စည်း.pdf",
    "၂၄ ပစ္စည်း  ဆုတောင်း.pdf"
  };
  public static final String[] DRIVE_IDS={
    "1Te6Tz_pgsw0zFSCG0JBSUtF8lOg5neDO",
    "11Mr3xQqfFOEmOn5srpszXnMbBwg7uErX",
    "1oWP7KpXcU2t8py_VkZyW-jNgdZIpj4iB",
    "1VzwBSYe37HVWbyoiGN5cnnHaLXaoZjoE",
    "1OCjw6v1luQxzOcHyeI3HueIkq0VWX25W",
    "1xiqpsYPUB2eCfK9-p0qY74gZD9azytPT",
    "1AH_0-srHSfWYzslnTolS4XQk8crWCaw8",
    "1jJDlSHbJ3ZVjsfdYlP-DPzreSJUlkg-c",
    "1If00R4floO4L5XyHKFNjRQBdI_LW_54K",
    "1KQaGWnzrPOGcGcVw6UVgClKd3wSYMtDA"
  };
  public static final String[] CATEGORIES={
    "ရုပ်ပြ",
    "အခြေခံ",
    "ပါဠိ",
    "ရုပ်ပြ",
    "အမေးအဖြေ",
    "အဓိပ္ပာယ်",
    "သုတ",
    "ရုပ်ပြ",
    "၂၄ ပစ္စည်း",
    "ဆုတောင်း"
  };
  private static final int TEAL=Color.rgb(4,46,47);
  private static final int GOLD=Color.rgb(232,187,84);
  private static final int IVORY=Color.rgb(255,248,235);
  private SharedPreferences prefs;
  private LinearLayout list;
  private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
  private GradientDrawable bg(int color,int radius){
    GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(radius));return g;
  }
  private TextView text(String str,int size,int color,boolean bold){
    TextView t=new TextView(this);t.setText(str);t.setTextSize(size);t.setTextColor(color);
    if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
  }
  @Override public void onCreate(Bundle state){
    super.onCreate(state);getWindow().setStatusBarColor(TEAL);getWindow().setNavigationBarColor(TEAL);
    prefs=getSharedPreferences("patthana_ebook_catalog",MODE_PRIVATE);
    build();
  }
  private void build(){
    LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(IVORY);
    setContentView(root);
    LinearLayout top=new LinearLayout(this);top.setOrientation(LinearLayout.VERTICAL);top.setBackgroundColor(TEAL);
    top.setPadding(dp(18),dp(20),dp(18),dp(16));
    top.addView(text("☸  Dhamma Light · eLibrary",22,GOLD,true));
    top.addView(text("Google Drive စာအုပ် ၁၀ ဖိုင် · Native PDF Reader",14,Color.WHITE,false));
    root.addView(top);
    EditText search=new EditText(this);search.setSingleLine(true);search.setTextSize(15);
    search.setHint("စာအုပ်အမည် ရှာရန် ...");search.setBackground(bg(Color.WHITE,12));
    search.setPadding(dp(18),dp(10),dp(18),dp(10));
    LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(52));sp.setMargins(dp(12),dp(12),dp(12),dp(4));root.addView(search,sp);
    ScrollView scroll=new ScrollView(this);list=new LinearLayout(this);list.setOrientation(LinearLayout.VERTICAL);
    list.setPadding(dp(12),dp(5),dp(12),dp(20));scroll.addView(list);
    root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    Button folder=new Button(this);folder.setAllCaps(false);folder.setText("☁ Google Drive Folder ဖွင့်ရန်");
    folder.setOnClickListener(v->{Intent it=new Intent(Intent.ACTION_VIEW,Uri.parse(FOLDER_URL));startActivity(it);});
    root.addView(folder);
    search.addTextChangedListener(new TextWatcher(){
      public void beforeTextChanged(CharSequence s,int start,int count,int after){}
      public void onTextChanged(CharSequence s,int start,int before,int count){populate(s.toString());}
      public void afterTextChanged(Editable t){}
    });
    populate("");
  }
  private void populate(String query){
    list.removeAllViews();
    for(int i=0;i<BOOK_TITLES.length;i++){
      if(!query.isEmpty()&&!BOOK_TITLES[i].toLowerCase(Locale.ROOT).contains(query.toLowerCase(Locale.ROOT))&&!CATEGORIES[i].contains(query))continue;
      final int index=i;
      LinearLayout card=new LinearLayout(this);card.setOrientation(LinearLayout.VERTICAL);card.setBackground(bg(Color.WHITE,14));
      card.setPadding(dp(14),dp(12),dp(14),dp(13));
      TextView title=text(String.format(Locale.US,"%02d",i+1)+"  📖  "+BOOK_TITLES[i],16,TEAL,true);card.addView(title);
      card.addView(text(CATEGORIES[i]+" · PDF · "+(prefs.getBoolean("linked_"+i,false)?"ဖိုင်ချိတ်ဆက်ပြီး":"ဖိုင်ရွေးချယ်ရန်"),12,Color.rgb(131,103,59),false));
      LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
      Button read=new Button(this);read.setAllCaps(false);read.setText("▣ App ထဲမှာ ဖတ်မည်");
      read.setOnClickListener(v->read(index));row.addView(read,new LinearLayout.LayoutParams(0,dp(54),1));
      Button link=new Button(this);link.setAllCaps(false);link.setText("Drive ↗");
      link.setOnClickListener(v->{Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://drive.google.com/file/d/"+DRIVE_IDS[index]+"/view"));startActivity(intent);});
      row.addView(link,new LinearLayout.LayoutParams(0,dp(54),1));card.addView(row);
      LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.setMargins(0,dp(6),0,dp(6));list.addView(card,cp);
    }
  }
  private void read(int index){
    prefs.edit().putBoolean("linked_"+index,true).apply();
    Intent intent=new Intent(this,NativePdfReaderActivity.class);
    intent.putExtra("book_id",DRIVE_IDS[index]);
    intent.putExtra("book_title",BOOK_TITLES[index]);
    startActivity(intent);
  }
  @Override protected void onResume(){super.onResume();if(list!=null)populate("");}
}
