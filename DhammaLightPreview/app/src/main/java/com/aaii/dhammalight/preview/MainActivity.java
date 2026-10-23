package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int NAVY=Color.rgb(7,33,42), GOLD=Color.rgb(228,189,106), WHITE=Color.rgb(250,246,232);
    private static final String[] NAMES={
        "ဟေတုပစ္စယော",
        "အာရမ္မဏပစ္စယော",
        "အဓိပတိပစ္စယော",
        "အနန္တရပစ္စယော",
        "သမနန္တရပစ္စယော",
        "သဟဇာတပစ္စယော",
        "အညမညပစ္စယော",
        "နိဿယပစ္စယော",
        "ဥပနိဿယပစ္စယော",
        "ပုရေဇာတပစ္စယော",
        "ပစ္ဆာဇာတပစ္စယော",
        "အာသေဝနပစ္စယော",
        "ကမ္မပစ္စယော",
        "ဝိပါကပစ္စယော",
        "အာဟာရပစ္စယော",
        "ဣန္ဒြိယပစ္စယော",
        "စျာနပစ္စယော",
        "မဂ္ဂပစ္စယော",
        "သမ္ပယုတ္တပစ္စယော",
        "ဝိပ္ပယုတ္တပစ္စယော",
        "အတ္ထိပစ္စယော",
        "နတ္ထိပစ္စယော",
        "ဝိဂတပစ္စယော",
        "အဝိဂတပစ္စယော"
    };
    private static final String[] MEANINGS={
        "အမြစ်အဖြစ် ထောက်ပံ့ခြင်း",
        "အာရုံအဖြစ် ထောက်ပံ့ခြင်း",
        "ဦးဆောင်ကြီးစိုးမှုဖြင့် ထောက်ပံ့ခြင်း",
        "အကြားမရှိ ဆက်တိုက်ဖြစ်ပေါ်ခြင်း",
        "အစဉ်လိုက် ဆက်တိုက်ဖြစ်ပေါ်ခြင်း",
        "အတူတကွ ဖြစ်ပေါ်ခြင်း",
        "အပြန်အလှန် အထောက်အပံ့ပြုခြင်း",
        "မှီခိုရာအဖြစ် ထောက်ပံ့ခြင်း",
        "အင်အားကြီးသော အထောက်အပံ့",
        "အရင် ဖြစ်ပေါ်ထားခြင်း",
        "နောက်မှ ဖြစ်ပေါ်ခြင်း",
        "အဖန်ဖန် ဖြစ်ပေါ်ခြင်း",
        "ကံဖြင့် ထောက်ပံ့ခြင်း",
        "ကံ၏ အကျိုးအဖြစ် ထောက်ပံ့ခြင်း",
        "အာဟာရအဖြစ် ထောက်ပံ့ခြင်း",
        "ဣန္ဒြိယအစွမ်းဖြင့် ထောက်ပံ့ခြင်း",
        "စျာန်အင်္ဂများဖြင့် ထောက်ပံ့ခြင်း",
        "မဂ္ဂအင်္ဂများဖြင့် ထောက်ပံ့ခြင်း",
        "ယှဉ်တွဲနေခြင်းဖြင့် ထောက်ပံ့ခြင်း",
        "မယှဉ်တွဲဘဲ ထောက်ပံ့ခြင်း",
        "ရှိနေခြင်းဖြင့် ထောက်ပံ့ခြင်း",
        "မရှိတော့ခြင်းဖြင့် ထောက်ပံ့ခြင်း",
        "ကွယ်ပျောက်ခြင်းဖြင့် ထောက်ပံ့ခြင်း",
        "မကွယ်ပျောက်သေးခြင်းဖြင့် ထောက်ပံ့ခြင်း"
    };
    private static final String[] ROOTS={"လောဘ","ဒေါသ","မောဟ","အလောဘ","အဒေါသ","အမောဟ"};
    private static final String[] ROOT_MEANINGS={"လိုချင်တပ်မက်ခြင်း","မနှစ်သက်၊ စိတ်ဆိုးခြင်း","မသိမှု၊ မှားယွင်းသိမှု","မတပ်မက်ခြင်း၊ စွန့်လွှတ်ခြင်း","မမုန်းတီးခြင်း၊ မေတ္တာ","ပညာဖြင့် အမှန်ကို သိခြင်း"};
    private final String[] VIDEO_IDS={"KcjkwHTdWvc","t4Kh51NjwF4","TPhwBwWTkps","k7Kx2veKs1I"};
    private final String[] VIDEO_NAMES={"ပဋ္ဌာန်းအဓိပ္ပာယ်နှင့် ဟေတုပစ္စည်း","ပဋ္ဌာန်းတရားတော် အနှစ်ချုပ် (၂)","ဟေတုပစ္စယော — ပဋ္ဌာန်း ၂၄ ပစ္စည်း","အကြောင်းတရားနှင့် အာရုံ"};
    private TextToSpeech tts;
    private LinearLayout body;
    private SharedPreferences prefs;
    private int current=0;
    @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("kids_progress",MODE_PRIVATE);home();}
    private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
    private TextView text(String s,int size,int color,boolean bold){
        TextView t=new TextView(this);t.setText(s);t.setTextColor(color);t.setTextSize(size);t.setPadding(dp(9),dp(8),dp(9),dp(9));
        if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
    }
    private void frame(String title){
        ScrollView s=new ScrollView(this);s.setFillViewport(true);s.setBackgroundColor(NAVY);
        body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);
        body.setPadding(dp(15),dp(18),dp(15),dp(28));s.addView(body);setContentView(s);
        TextView h=text("☸  "+title,23,GOLD,true);h.setGravity(Gravity.CENTER);body.addView(h);
    }
    private void line(String s){body.addView(text(s,16,WHITE,false));}
    private void btn(String title,Runnable r){
        Button b=new Button(this);b.setAllCaps(false);b.setText(title);b.setTextSize(15);b.setMinHeight(dp(50));
        b.setOnClickListener(v->r.run());LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(56));
        p.setMargins(0,dp(5),0,dp(5));body.addView(b,p);
    }
    private void home(){
        frame("Dhamma Light · Kids Preview");
        line("ပဋ္ဌာန်း ၂၄ ပစ္စည်း — ကလေးများနှင့် မိသားစုများအတွက် Native သင်ခန်းစာများ");
        int stars=0;for(int i=0;i<24;i++)stars+=prefs.getInt("q"+i,0);
        line("⭐ ရရှိပြီးသောကြယ်ပွင့်  "+stars+" / 48");
        btn("📖 ၂၄ ပစ္စည်း သင်ခန်းစာများ",this::list);
        btn("🌳 ဟေတုပစ္စယော · အမြစ် ၆ ပါး",()->detail(0));
        btn("🧩 ကလေးများအတွက် 3D သင်္ကေတ",()->symbol(0));
        btn("▶ ဟေတုပစ္စယော YouTube Lessons",this::videos);
        line("TEST BUILD — ဒီ APK မှာ မူရင်း A5 Comic ပုံနှင့် ARCore Camera Mode မပါသေးပါ။ 3D ပုံသည် Camera မပါသော သင်္ကေတနမူနာဖြစ်သည်။");
    }
    private void list(){
        frame("ပဋ္ဌာန်း ၂၄ ပစ္စည်း");
        btn("⌂ Home",this::home);
        for(int i=0;i<NAMES.length;i++){final int id=i;btn(String.format(Locale.US,"%02d",i+1)+"  "+NAMES[i],()->detail(id));}
    }
    private void detail(int id){
        current=Math.max(0,Math.min(23,id));frame(String.format(Locale.US,"%02d",current+1)+" · "+NAMES[current]);
        line("အဓိပ္ပာယ် — "+MEANINGS[current]);
        line("ကလေးတို့အတွက် — ဒီတရားကို အကြောင်းတရားတစ်ခုက နောက်တစ်ခုကို အထောက်အကူပြုသည့် သဘောနှင့် ဆက်စပ်ပြီး လေ့လာပါ။");
        if(current==0){
            line("ဟိတ် ၆ ပါး — လောဘ၊ ဒေါသ၊ မောဟ၊ အလောဘ၊ အဒေါသ၊ အမောဟ");
            line("သစ်ပင်တစ်ပင်မှာ အမြစ်က အထောက်အပံ့ပေးသလို စိတ်အခြေအနေအတွက်လည်း ဟိတ်တရားတွေဟာ အခြေခံကျတယ်။");
            for(int i=0;i<6;i++){final int k=i;btn((i<3?"🟠 ":"🟢 ")+ROOTS[i],()->{Toast.makeText(this,ROOT_MEANINGS[k],Toast.LENGTH_LONG).show();speak(ROOTS[k]+"၊ "+ROOT_MEANINGS[k]);});}
        }
        btn("🔊 မြန်မာအသံ ဖတ်ပြပါ",()->speak(NAMES[current]+"။ "+MEANINGS[current]));
        btn("🌳 3D သင်္ကေတကြည့်မယ်",()->symbol(current));
        btn("⭐ Quiz ဖြေမယ်",()->quiz(current,0));
        btn("‹ ယခင်ပစ္စည်း",()->detail(Math.max(0,current-1)));
        btn("နောက်ပစ္စည်း ›",()->detail(Math.min(23,current+1)));
        btn("⌂ Home",this::home);
    }
    private void quiz(int id,int stage){
        frame("Quiz "+(stage+1)+"/2 · "+NAMES[id]);
        final boolean first=stage==0;
        line(first?"ဒီပစ္စည်းရဲ့ အဓိပ္ပာယ်ကို ရွေးပါ။":"တရားတစ်ခုကို ဂရုပြုလေ့လာတဲ့အခါ ဘယ်အရာက အထောက်အကူဖြစ်မလဲ။");
        String correct=first?MEANINGS[id]:"နားလည်အောင် စူးစမ်းလေ့လာခြင်း";
        String wrongA=first?MEANINGS[(id+7)%24]:"မေးခွန်းကို မဖတ်ဘဲ ဖြေခြင်း";
        String wrongB=first?MEANINGS[(id+13)%24]:"တခြားသူကို ရန်ဖြစ်ခြင်း";
        int offset=(id+stage)%3;
        String[] choices={correct,wrongA,wrongB};
        for(int i=0;i<3;i++){final String answer=choices[(i+offset)%3];btn(answer,()->{
            if(answer.equals(correct)){
                int old=prefs.getInt("q"+id,0);
                if(old<stage+1)prefs.edit().putInt("q"+id,stage+1).apply();
                Toast.makeText(this,"မှန်တယ်! ⭐",Toast.LENGTH_SHORT).show();
                if(stage==0)quiz(id,1);else detail(id);
            }else Toast.makeText(this,"ထပ်ကြိုးစားကြည့်ပါ",Toast.LENGTH_SHORT).show();
        });}
        btn("‹ သင်ခန်းစာသို့",()->detail(id));
    }
    private void speak(String value){
        if(tts==null){
            tts=new TextToSpeech(this,status->{
                if(status==TextToSpeech.SUCCESS){
                    int result=tts.setLanguage(new Locale("my","MM"));
                    if(result==TextToSpeech.LANG_MISSING_DATA||result==TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(this,"ဖုန်းမှာ မြန်မာ TTS Voice မရှိသေးပါ",Toast.LENGTH_LONG).show();
                    }else tts.speak(value,TextToSpeech.QUEUE_FLUSH,null,"dhamma");
                }else Toast.makeText(this,"Voice system မရရှိပါ",Toast.LENGTH_SHORT).show();
            });
        }else tts.speak(value,TextToSpeech.QUEUE_FLUSH,null,"dhamma");
    }
    private void symbol(int id){
        frame("3D သင်္ကေတနမူနာ");
        line(NAMES[id]+" — ကလေးများနားလည်ရန် ရိုးရှင်းသော ပုံစံဖြစ်ပြီး AR Camera မဟုတ်ပါ။");
        View tree=new View(this){
            Paint p=new Paint(3);
            void color(int col){p.setColor(col);p.setStrokeWidth(dp(7));p.setStrokeCap(Paint.Cap.ROUND);p.setStyle(Paint.Style.STROKE);}
            @Override public void onDraw(Canvas original){
                super.onDraw(original);Canvas c=original;float w=getWidth(),h=getHeight(),x=w/2,y=h*.58f;
                color(Color.rgb(128,84,46));c.drawLine(x,y,x,y-h*.38f,p);
                for(int i=0;i<6;i++){
                    float dx=((i%3)-1)*w*.27f,dy=h*(.2f+(i/3)*.12f);
                    color(i<3?Color.rgb(244,151,82):Color.rgb(105,203,132));
                    c.drawLine(x,y,x+dx,y+dy,p);
                }
                color(Color.rgb(104,199,118));p.setStyle(Paint.Style.FILL);
                c.drawCircle(x,y-h*.38f,w*.18f,p);
                c.drawCircle(x-w*.12f,y-h*.32f,w*.13f,p);
                c.drawCircle(x+w*.12f,y-h*.32f,w*.13f,p);
            }
        };tree.setBackgroundColor(Color.rgb(243,236,209));
        body.addView(tree,new LinearLayout.LayoutParams(-1,dp(310)));
        line("အမြစ် ၆ ခုကို ကွဲပြားသော အရောင်များဖြင့် နှိုင်းယှဉ်ပြထားသည်။");
        btn("Quiz ဖြေမယ်",()->quiz(id,0));btn("‹ ပြန်သွားမယ်",()->detail(id));
    }
    private void videos(){
        frame("ဟေတုပစ္စယော · YouTube");
        line("Video ကို YouTube App သို့မဟုတ် Browser တွင် ဖွင့်သည်။ အင်တာနက်လိုအပ်သည်။");
        for(int i=0;i<VIDEO_IDS.length;i++){
            final int k=i;btn("▶ "+VIDEO_NAMES[i],()->{
                Intent intent=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.youtube.com/watch?v="+VIDEO_IDS[k]));
                try{startActivity(intent);}catch(Exception e){Toast.makeText(this,"YouTube ကို မဖွင့်နိုင်ပါ",Toast.LENGTH_SHORT).show();}
            });
        }
        btn("⌂ Home",this::home);
    }
    @Override public void onBackPressed(){home();}
    @Override public void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();tts=null;}super.onDestroy();}
}
