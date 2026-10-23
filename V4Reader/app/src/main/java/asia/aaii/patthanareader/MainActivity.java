package asia.aaii.patthanareader;

import android.app.Activity;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.res.AssetFileDescriptor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import java.util.Locale;

public class MainActivity extends Activity {
  private static final String[] MM={"ဟေတုပစ္စယော","အာရမ္မဏပစ္စယော","အဓိပတိပစ္စယော","အနန္တရပစ္စယော","သမနန္တရပစ္စယော","သဟဇာတပစ္စယော","အညမညပစ္စယော","နိဿယပစ္စယော","ဥပနိဿယပစ္စယော","ပုရေဇာတပစ္စယော","ပစ္ဆာဇာတပစ္စယော","အာသေဝနပစ္စယော","ကမ္မပစ္စယော","ဝိပါကပစ္စယော","အာဟာရပစ္စယော","ဣန္ဒြိယပစ္စယော","ဈာနပစ္စယော","မဂ္ဂပစ္စယော","သမ္ပယုတ္တပစ္စယော","ဝိပ္ပယုတ္တပစ္စယော","အတ္ထိပစ္စယော","နတ္ထိပစ္စယော","ဝိဂတပစ္စယော","အဝိဂတပစ္စယော"};
  private static final String[] PALI={"Hetupaccayo","Ārammaṇapaccayo","Adhipatipaccayo","Anantarapaccayo","Samanantarapaccayo","Sahajātapaccayo","Aññamaññapaccayo","Nissayapaccayo","Upanissayapaccayo","Purejātapaccayo","Pacchājātapaccayo","Āsevanapaccayo","Kammapaccayo","Vipākapaccayo","Āhārapaccayo","Indriyapaccayo","Jhānapaccayo","Maggapaccayo","Sampayuttapaccayo","Vippayuttapaccayo","Atthipaccayo","Natthipaccayo","Vigatapaccayo","Avigatapaccayo"};
  private static final String[] MEANING={"အမြစ်အကြောင်းတရားဖြင့် ကျေးဇူးပြုခြင်း","သိရာအာရုံအဖြစ် ကျေးဇူးပြုခြင်း","အကြီးအမှူးအဖြစ် ကျေးဇူးပြုခြင်း","အကြားမရှိ ဆက်တိုက်ဖြစ်၍ ကျေးဇူးပြုခြင်း","အစဉ်တကျ အခြားမဲ့ ကျေးဇူးပြုခြင်း","အတူတကွ ဖြစ်ပေါ်၍ ကျေးဇူးပြုခြင်း","အချင်းချင်း အပြန်အလှန် ကျေးဇူးပြုခြင်း","မှီခိုရာအဖြစ် ကျေးဇူးပြုခြင်း","အားကြီးသော အထောက်အပံ့ဖြင့် ကျေးဇူးပြုခြင်း","ရှေ့ကဖြစ်ပေါ်၍ ကျေးဇူးပြုခြင်း","နောက်မှဖြစ်ပေါ်၍ ကျေးဇူးပြုခြင်း","ထပ်တလဲလဲဖြစ်၍ ကျေးဇူးပြုခြင်း","ကံစေတနာဖြင့် ကျေးဇူးပြုခြင်း","အကျိုးဝိပါက်ဖြစ်၍ ကျေးဇူးပြုခြင်း","အာဟာရအဖြစ် ကျေးဇူးပြုခြင်း","အစိုးရစွမ်းအားဖြင့် ကျေးဇူးပြုခြင်း","ဈာနင်္ဂတရားဖြင့် ကျေးဇူးပြုခြင်း","မဂ္ဂင်္ဂတရားဖြင့် ကျေးဇူးပြုခြင်း","ယှဉ်တွဲဆက်စပ်၍ ကျေးဇူးပြုခြင်း","မယှဉ်တွဲဘဲ ကျေးဇူးပြုခြင်း","ရှိနေခြင်းဖြင့် ကျေးဇူးပြုခြင်း","မရှိတော့ခြင်းဖြင့် ကျေးဇူးပြုခြင်း","ကင်းပျောက်ခြင်းဖြင့် ကျေးဇူးပြုခြင်း","မကင်းပျောက်သေးခြင်းဖြင့် ကျေးဇူးပြုခြင်း"};
  private int selected=1; private float font=27f; private boolean dark=false, roman=true, all=false, pictures=false; private int repeat=0; private MediaPlayer player=null; private LinearLayout column;
  private int dp(int n){return (int)(getResources().getDisplayMetrics().density*n);}
  private int ink(){return dark?Color.rgb(232,238,249):Color.rgb(25,40,59);}
  private int muted(){return dark?Color.rgb(176,199,215):Color.rgb(82,101,123);}
  private int accent(){return dark?Color.rgb(245,202,113):Color.rgb(89,71,173);}
  private int bg(){return dark?Color.rgb(10,20,36):Color.rgb(247,250,253);}
  private String num(int i){String s=String.valueOf(i);StringBuilder b=new StringBuilder();for(char c:s.toCharArray())b.append("၀၁၂၃၄၅၆၇၈၉".charAt(c-'0'));return b.toString();}
  @Override public void onCreate(Bundle state){super.onCreate(state);android.content.SharedPreferences p=getSharedPreferences("reader",0);selected=Math.max(1,Math.min(24,p.getInt("selected",1)));font=Math.max(18,Math.min(44,p.getFloat("font",27)));dark=p.getBoolean("dark",false);roman=p.getBoolean("roman",true);render();}
  private void save(){getSharedPreferences("reader",0).edit().putInt("selected",selected).putFloat("font",font).putBoolean("dark",dark).putBoolean("roman",roman).apply();}
  private TextView label(String s,float size,boolean bold,boolean centered,boolean highlighted){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(highlighted?accent():ink());if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);if(centered)t.setGravity(Gravity.CENTER);t.setPadding(dp(8),dp(8),dp(8),dp(9));t.setLineSpacing(dp(6),1.15f);column.addView(t);return t;}
  private Button button(String s,Runnable action){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextSize(12);b.setOnClickListener(v->action.run());return b;}
  private void buttons(Button... args){LinearLayout l=new LinearLayout(this);l.setOrientation(0);for(Button b:args)l.addView(b,new LinearLayout.LayoutParams(0,dp(51),1));column.addView(l);}
  private void message(String text){Toast.makeText(this,text,Toast.LENGTH_LONG).show();}
  private void select(int n){if(n<1||n>24)return;stopAudio();selected=n;all=false;save();render();}
  private void choose(){String[] items=new String[24];for(int i=0;i<24;i++)items[i]=num(i+1)+"။ "+MM[i];new AlertDialog.Builder(this).setTitle("ပစ္စည်း ၂၄ ပါး").setItems(items,(d,w)->select(w+1)).setNegativeButton("ပိတ်",null).show();}
  private void stopAudio(){if(player!=null){try{player.setOnCompletionListener(null);player.stop();}catch(Exception e){}player.release();player=null;}}
  private void audio(){if(selected>23){message("ပစ္စည်း ၂၄ အတွက် သီးခြားအသံဖိုင် မရှိသေးပါ။");return;}if(player!=null){if(player.isPlaying())player.pause();else player.start();return;}try(AssetFileDescriptor f=getAssets().openFd(String.format(Locale.US,"audio/%02d.mp3",selected))){MediaPlayer next=new MediaPlayer();next.setDataSource(f.getFileDescriptor(),f.getStartOffset(),f.getLength());next.setOnCompletionListener(m->{if(repeat==1){m.seekTo(0);m.start();}else if(repeat==2&&selected<23){int n=selected+1;select(n);audio();}});next.prepare();player=next;player.start();}catch(Exception e){stopAudio();message("အသံဖိုင် ဖွင့်၍မရပါ: "+e.getClass().getSimpleName());}}
  private void image(){if(selected>20){message("ပစ္စည်း ၂၁–၂၄ ရုပ်ပုံ မထည့်ရသေးပါ။");return;}try(java.io.InputStream f=getAssets().open(String.format(Locale.US,"illustrations/%02d.webp",selected))){android.graphics.Bitmap bitmap=BitmapFactory.decodeStream(f);if(bitmap==null){message("ရုပ်ပုံဖတ်၍မရပါ");return;}ImageView v=new ImageView(this);v.setImageBitmap(bitmap);v.setAdjustViewBounds(true);v.setScaleType(ImageView.ScaleType.FIT_CENTER);column.addView(v,new LinearLayout.LayoutParams(-1,dp(420)));}catch(Exception e){message("ရုပ်ပုံမရှိသေးပါ");}}
  private void render(){ScrollView sc=new ScrollView(this);sc.setFillViewport(true);sc.setBackgroundColor(bg());column=new LinearLayout(this);column.setOrientation(1);column.setPadding(dp(10),dp(10),dp(10),dp(20));sc.addView(column);setContentView(sc);
    label("☸ ပဋ္ဌာန်း ၂၄ ပစ္စည်း",24,true,true,true);
    label("ပါဠိဖတ်ရှုရန် · အသံနားထောင်ရန် · Offline Reader",12,false,true,false);
    buttons(button("ပစ္စည်းစာရင်း",()->choose()),button("၂၄ ပါး အကျဉ်း",()->{all=true;render();}));
    buttons(button("ဓမ္မစာအုပ် ၁၀ အုပ်",()->startActivity(new Intent(this,BookLibraryActivity.class))));
    buttons(button("A−",()->{font=Math.max(18,font-2);save();render();}),button("A+",()->{font=Math.min(44,font+2);save();render();}),button(dark?"အလင်း":"ညဖတ်",()->{dark=!dark;save();render();}),button(roman?"Roman ✓":"Roman –",()->{roman=!roman;save();render();}));
    if(all){label("ပစ္စယုဒ္ဒေသ အကျဉ်းစာရင်း",20,true,true,true);label("နမော တဿ ဘဂဝတော အရဟတော သမ္မာသမ္ဗုဒ္ဓဿ။",font,true,true,false);StringBuilder full=new StringBuilder();for(int i=0;i<24;i++){if(i>0)full.append("၊ ");full.append(MM[i]);}full.append("တိ။");label(full.toString(),font,false,false,false);if(roman)label(String.join(", ",PALI)+"ti.",Math.max(13,font-6),false,false,true);for(int i=0;i<24;i++){final int n=i+1;column.addView(button(num(n)+"။ "+MM[i],()->select(n)));}return;}
    int i=selected-1;label("ပစ္စည်း "+num(selected)+" / ၂၄",13,true,true,true);label(MM[i],font+3,true,true,false);if(roman)label(PALI[i],17,false,true,true);label(MM[i]+"။",font,true,true,false);label("မြန်မာအဓိပ္ပာယ် (အကျဉ်း)",16,true,false,true);label(MEANING[i]+"။",font-2,false,false,false);label("ဤအဓိပ္ပာယ်မှာ သင်ကြားရေးအကျဉ်းဖြစ်ပြီး ကျမ်းစာအနက် အပြည့်အစုံ မဟုတ်ပါ။",12,false,false,false);
    buttons(button(pictures?"ပုံဖျောက်":"ပုံကြည့်",()->{pictures=!pictures;render();}),button(selected<=23?"အသံ ဖွင့်/ရပ်":"အသံ မရှိ",()->audio()),button("🔁"+repeat,()->{repeat=(repeat+1)%3;render();}));
    if(pictures)image();
    buttons(button("← ရှေ့ပစ္စည်း",()->select(selected-1)),button("နောက်ပစ္စည်း →",()->select(selected+1)));
  }
  @Override protected void onPause(){super.onPause();if(player!=null&&player.isPlaying())player.pause();}
  @Override protected void onDestroy(){stopAudio();super.onDestroy();}
}