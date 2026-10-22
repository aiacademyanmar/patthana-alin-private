package asia.aaii.patthana;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
  private static final int NAVY=Color.rgb(7,32,51), GOLD=Color.rgb(218,169,83), WHITE=Color.rgb(255,251,242), SOFT=Color.rgb(228,234,233);
  private static final String[] MM = {
    "ဟေတုပစ္စယော","အာရမ္မဏပစ္စယော","အဓိပတိပစ္စယော","အနန္တရပစ္စယော","သမနန္တရပစ္စယော","သဟဇာတပစ္စယော",
    "အညမညပစ္စယော","နိဿယပစ္စယော","ဥပနိဿယပစ္စယော","ပုရေဇာတပစ္စယော","ပစ္ဆာဇာတပစ္စယော","အာသေဝနပစ္စယော",
    "ကမ္မပစ္စယော","ဝိပါကပစ္စယော","အာဟာရပစ္စယော","ဣန္ဒြိယပစ္စယော","ဈာနပစ္စယော","မဂ္ဂပစ္စယော",
    "သမ္ပယုတ္တပစ္စယော","ဝိပ္ပယုတ္တပစ္စယော","အတ္ထိပစ္စယော","နတ္ထိပစ္စယော","ဝိဂတပစ္စယော","အဝိဂတပစ္စယော"
  };
  private static final String[] PALI = {
    "Hetu","Ārammaṇa","Adhipati","Anantara","Samanantara","Sahajāta","Aññamañña","Nissaya",
    "Upanissaya","Purejāta","Pacchājāta","Āsevana","Kamma","Vipāka","Āhāra","Indriya",
    "Jhāna","Magga","Sampayutta","Vippayutta","Atthi","Natthi","Vigata","Avigata"
  };
  private static final String[] MEAN = {
    "အမြစ်အကြောင်းတရားအဖြစ် ကျေးဇူးပြုခြင်း","အာရုံအဖြစ် ကျေးဇူးပြုခြင်း","အကြီးအမှူးဖြစ်၍ ကျေးဇူးပြုခြင်း",
    "အကြားမရှိ ဆက်တိုက်ဖြစ်၍ ကျေးဇူးပြုခြင်း","အခြားမဲ့ အစဉ်အတိုင်း ဆက်စပ်ကျေးဇူးပြုခြင်း",
    "အတူတကွ ဖြစ်ပေါ်၍ ကျေးဇူးပြုခြင်း","အပြန်အလှန် ကျေးဇူးပြုခြင်း","မှီခိုရာအဖြစ် ကျေးဇူးပြုခြင်း",
    "အားကြီးသော ထောက်ပံ့မှုဖြင့် ကျေးဇူးပြုခြင်း","ရှေ့ကဖြစ်ပေါ်၍ ကျေးဇူးပြုခြင်း","နောက်မှဖြစ်ပေါ်၍ ကျေးဇူးပြုခြင်း",
    "ထပ်ခါတလဲလဲ ဖြစ်ခြင်းဖြင့် ကျေးဇူးပြုခြင်း","ကံစေတနာဖြင့် ကျေးဇူးပြုခြင်း","အကျိုးဝိပါက်အဖြစ် ကျေးဇူးပြုခြင်း",
    "အာဟာရအဖြစ် ထောက်ပံ့ခြင်း","အစိုးရခြင်းသဘောဖြင့် ကျေးဇူးပြုခြင်း","ဈာနင်္ဂတရားတို့၏ အစွမ်းဖြင့် ကျေးဇူးပြုခြင်း",
    "လမ်းကြောင်းအဖြစ် ကျေးဇူးပြုခြင်း","ယှဉ်တွဲဆက်စပ်၍ ကျေးဇူးပြုခြင်း","မယှဉ်တွဲဘဲ ကျေးဇူးပြုခြင်း",
    "ရှိနေခြင်းဖြင့် ကျေးဇူးပြုခြင်း","မရှိတော့ခြင်းဖြင့် ကျေးဇူးပြုခြင်း","ကင်းပျောက်ခြင်းဖြင့် ကျေးဇူးပြုခြင်း",
    "မကင်းပျောက်သေးခြင်းဖြင့် ကျေးဇူးပြုခြင်း"
  };
  private LinearLayout body;
  private int page=0;
  private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
  @Override public void onCreate(Bundle saved){super.onCreate(saved);getWindow().setStatusBarColor(NAVY);getWindow().setNavigationBarColor(NAVY);home();}
  private GradientDrawable background(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
  private TextView label(String text,int size,boolean bold,int color){
    TextView v=new TextView(this);v.setText(text);v.setTextSize(size);v.setTextColor(color);
    if(bold)v.setTypeface(null,1);v.setPadding(dp(3),dp(7),dp(3),dp(7));return v;
  }
  private void screen(String title){
    LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(WHITE);
    LinearLayout header=new LinearLayout(this);header.setOrientation(LinearLayout.HORIZONTAL);header.setBackgroundColor(NAVY);header.setPadding(dp(18),dp(13),dp(18),dp(13));
    TextView back=label("‹",27,true,GOLD);back.setPadding(dp(6),0,dp(15),0);back.setOnClickListener(v->home());header.addView(back);
    TextView caption=label(title,20,true,WHITE);header.addView(caption);
    root.addView(header);
    ScrollView scroll=new ScrollView(this);body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);body.setPadding(dp(20),dp(22),dp(20),dp(20));
    scroll.addView(body);root.addView(scroll);setContentView(root);
  }
  private void spacer(int h){Space s=new Space(this);body.addView(s,new LinearLayout.LayoutParams(1,dp(h)));}
  private void paragraph(String value,int size,boolean bold,int color){body.addView(label(value,size,bold,color));}
  private void button(String title,Runnable run){
    TextView t=label(title,16,true,NAVY);t.setGravity(Gravity.CENTER_VERTICAL);t.setBackground(background(Color.rgb(239,230,211),16));
    t.setPadding(dp(16),dp(15),dp(16),dp(15));LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.bottomMargin=dp(10);body.addView(t,lp);t.setOnClickListener(v->run.run());
  }
  private void url(String url){try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(url)).addCategory(Intent.CATEGORY_BROWSABLE));}catch(Exception e){Toast.makeText(this,"Browser မဖွင့်နိုင်ပါ",Toast.LENGTH_LONG).show();}}
  private void note(String msg){paragraph(msg,13,false,Color.rgb(100,112,115));}
  private void home(){
    screen("ပဋ္ဌာန်း အလင်း");
    paragraph("ပဋ္ဌာန်း ၂၄ ပစ္စည်း",27,true,NAVY);
    paragraph("Paṭṭhāna Alin • Native Android Public Edition",13,false,Color.rgb(88,107,110));
    body.addView(new SacredArt(0),new LinearLayout.LayoutParams(-1,dp(190)));
    spacer(18);
    paragraph("သင်ယူရန်",18,true,NAVY);
    button("၁   ပဋ္ဌာန်း ၂၄ ပစ္စည်း • အဓိပ္ပာယ်",this::conditions);
    button("၂   ၂၄ ပစ္စည်း ပုံဖော်စာမျက်နှာ",()->reader(0));
    button("၃   ပဋ္ဌာန်း အသံတော် (မူရင်းဆိုက်)",()->url("https://www.abhidhamma.com/incl.php?aw=abhidhamma_audios.txt"));
    button("၄   YouTube ဓမ္မ Video Library",this::videos);
    button("၅   ဆရာတော်များ MP3 Library",this::sayadaws);
    button("၆   ဓမ္မစာအုပ် Google Drive eLibrary",this::books);
    spacer(5);note("Public Build: မူပိုင်ခွင့်ပြန်လည်ဖြန့်ဝေခွင့် မသေချာသေးသည့် MP3 နှင့် မူရင်းပုံများကို APK ထဲ မထည့်ထားပါ။");
  }
  private void conditions(){
    screen("ပဋ္ဌာန်း ၂၄ ပစ္စည်း");paragraph("Paccaya 01–24",17,true,GOLD);
    for(int i=0;i<24;i++){final int ix=i;button(String.format(java.util.Locale.US,"%02d",i+1)+"   "+MM[i]+"\n"+PALI[i]+" paccayo",()->detail(ix));}
  }
  private void detail(int i){
    page=i;screen(String.format(java.util.Locale.US,"%02d / 24 • %s",i+1,MM[i]));
    body.addView(new SacredArt(i),new LinearLayout.LayoutParams(-1,dp(235)));
    paragraph(PALI[i]+" paccayo",22,true,NAVY);
    paragraph(MM[i],18,true,Color.rgb(154,99,33));
    paragraph(MEAN[i],16,false,NAVY);
    spacer(12);
    button("ပုံဖော်စာမျက်နှာ ကြည့်ရန်",()->reader(i));
    button("ရွတ်ဖတ်သံ မူရင်းဆိုက်",()->url("https://www.abhidhamma.com/incl.php?aw=abhidhamma_audios.txt"));
    if(i>0)button("← အရင်ပစ္စည်း",()->detail(i-1));
    if(i<23)button("နောက်ပစ္စည်း →",()->detail(i+1));
  }
  private void reader(int i){
    page=i;screen("ပုံဖော်စာမျက်နှာ");
    paragraph(String.format(java.util.Locale.US,"ပစ္စည်း %02d / 24",i+1),17,true,GOLD);
    body.addView(new SacredArt(i),new LinearLayout.LayoutParams(-1,dp(300)));
    paragraph(MM[i],21,true,NAVY);
    paragraph(PALI[i]+" paccayo",17,true,Color.rgb(154,99,33));
    paragraph(MEAN[i],16,false,NAVY);
    spacer(10);note("ဤ Public Build တွင် သင်ယူရေးသင်္ကေတပုံများကို App အတွင်းဖန်တီးပြထားသည်။ မူရင်း A5 Illustration ၂၄ ပုံ မပါဝင်သေးပါ။");
    if(i>0)button("← Previous",()->reader(i-1));
    if(i<23)button("Next →",()->reader(i+1));else button("၂၄ ပစ္စည်းစာရင်း",this::conditions);
  }
  private void videos(){
    screen("YouTube ဓမ္မ Library");
    paragraph("ပဋ္ဌာန်းတရားတော် Video သင်ခန်းစာများ",19,true,NAVY);
    button("ပဋ္ဌာန်း ၂၄ ပစ္စည်းဆိုတာ ဘာလဲ",()->url("https://www.youtube.com/watch?v=UgeOGn7xUV8"));
    button("ပဋ္ဌာန်းတရားတော် အနှစ်ချုပ် (၁)",()->url("https://www.youtube.com/watch?v=V76bW_AueBE"));
    button("Dhamma Lulin Channel",()->url("https://www.youtube.com/@dhammalulin"));
    button("Dhamma Dharna Channel",()->url("https://www.youtube.com/channel/UCisxmpfjLDDNLCXaKqwtkAw"));
    note("Video playback အတွက် YouTube ကိုဖွင့်သည်။ Internet လိုအပ်ပါသည်။");
  }
  private void sayadaws(){
    screen("ဆရာတော်ကြီးများ MP3");
    paragraph("မူရင်းတရားတော်ရင်းမြစ်များ",19,true,NAVY);
    button("မိုးကုတ်ဆရာတော် ဦးဝိမလ",()->url("https://www.dhammadownload.com/MogokSayadaw-mp3.htm"));
    button("မဟာစည်ဆရာတော် ဦးသောဘန",()->url("https://www.dhammadownload.com/Mahasi-Sayadaw-U-Sobhana-Mahathera-mp3.htm"));
    note("မူရင်းဆိုက်၏ ခွင့်ပြုချက်နှင့် အသုံးပြုနိုင်မှုအပေါ် မူတည်ပါသည်။ Audio ဖိုင်များကို ဒီ Public APK အတွင်း ထပ်မံဖြန့်ဝေမထားပါ။");
  }
  private void books(){
    screen("ဓမ္မစာအုပ် eLibrary");
    paragraph("Google Drive စာကြည့်တိုက်",22,true,NAVY);
    paragraph("စာရေးသူနှင့် အမျိုးအစားအလိုက် စာအုပ်များ",16,false,NAVY);
    button("Google Drive ဓမ္မစာအုပ် Folder ဖွင့်ရန်",()->url("https://drive.google.com/drive/folders/1lRO_Bz5CLW4q5WYwpMnttGDNFOv7ps0m"));
    note("ဒီ Public Build သည် Online Folder ကိုဖွင့်ပေးခြင်းသာဖြစ်သည်။ V2.3 Full Offline/144 PDF catalog filtering စနစ် မပါသေးပါ။ Drive Share Permissions လိုအပ်ပါသည်။");
  }
  @Override public void onBackPressed(){home();}
  private final class SacredArt extends View {
    final int index;final Paint paint=new Paint(3);
    SacredArt(int index){super(MainActivity.this);this.index=index;}
    @Override protected void onDraw(Canvas c){
      int w=getWidth(),h=getHeight();float cx=w/2f,cy=h/2f,sz=Math.min(w,h);
      c.drawColor(NAVY);
      paint.setStyle(Paint.Style.FILL);paint.setColor(Color.rgb(13,59,76));
      c.drawCircle(cx,cy,sz*.43f,paint);
      c.save();c.translate(cx,cy);
      for(int k=0;k<12;k++){
        c.save();c.rotate(k*30f+index*4f);paint.setColor(k%2==0?GOLD:Color.rgb(168,121,59));
        c.drawOval(-sz*.061f,-sz*.41f,sz*.061f,-sz*.15f,paint);c.restore();
      }
      paint.setColor(NAVY);c.drawCircle(0,0,sz*.19f,paint);
      paint.setColor(GOLD);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(dp(3));c.drawCircle(0,0,sz*.18f,paint);
      for(int k=0;k<8;k++){c.save();c.rotate(k*45f);c.drawLine(0,-sz*.16f,0,-sz*.043f,paint);c.restore();}
      paint.setStyle(Paint.Style.FILL);paint.setColor(GOLD);c.drawCircle(0,0,sz*.044f,paint);
      c.restore();
      paint.setTextAlign(Paint.Align.CENTER);paint.setColor(WHITE);paint.setTypeface(Typeface.DEFAULT_BOLD);
      paint.setTextSize(sp(15));c.drawText(String.format(java.util.Locale.US,"PAṬṬHĀNA • %02d",index+1),cx,h-dp(12),paint);
    }
    private float sp(float n){return n*getResources().getDisplayMetrics().scaledDensity;}
  }
}
