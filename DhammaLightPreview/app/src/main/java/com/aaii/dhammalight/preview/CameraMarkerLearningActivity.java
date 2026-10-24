package com.aaii.dhammalight.preview;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.google.zxing.ResultPoint;
import java.util.List;
import java.util.Locale;

/** Camera marker learning: 24 printed QR codes DL:01..DL:24.
 * On-device scanning with educational overlay; NOT an ARCore 3D tracker.
 */
public final class CameraMarkerLearningActivity extends Activity {
  private static final int CAMERA_REQUEST=101;
  private static final int DEEP=0xff072f30, GOLD=0xffe7bd6b;
  private DecoratedBarcodeView camera;
  private LinearLayout info;
  private TextView title, description, banner;
  private CauseOverlay arrows;
  private int selected=-1;
  private String lastCode="";
  private TextToSpeech speech;
  private boolean resumed=false;
  private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+0.5f);}
  private GradientDrawable rounded(int color,int border){
    GradientDrawable g=new GradientDrawable();g.setColor(color);g.setCornerRadius(dp(20));
    if(border!=0)g.setStroke(dp(2),border);return g;
  }
  private TextView label(String s,int n,int col,boolean strong){
    TextView t=new TextView(this);t.setText(s);t.setTextSize(n);t.setTextColor(col);
    if(strong)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
  }
  private Button button(String s,Runnable action){
    Button b=new Button(this);b.setAllCaps(false);b.setText(s);b.setTextSize(14);
    b.setOnClickListener(v->action.run());return b;
  }
  @Override public void onCreate(Bundle state){
    super.onCreate(state);
    getWindow().setStatusBarColor(DEEP);getWindow().setNavigationBarColor(DEEP);
    FrameLayout scene=new FrameLayout(this);scene.setBackgroundColor(DEEP);setContentView(scene);
    camera=new DecoratedBarcodeView(this);
    camera.setStatusText("");
    camera.decodeContinuous(new BarcodeCallback(){
      @Override public void barcodeResult(BarcodeResult result){
        if(result==null)return;
        String code=result.getText();
        if(code==null||code.equals(lastCode))return;
        int lesson=parseMarker(code);
        if(lesson<0)return;
        lastCode=code;
        runOnUiThread(()->display(lesson));
      }
      @Override public void possibleResultPoints(List<ResultPoint> results){}
    });
    scene.addView(camera,new FrameLayout.LayoutParams(-1,-1));
    arrows=new CauseOverlay();scene.addView(arrows,new FrameLayout.LayoutParams(-1,-1));
    LinearLayout top=new LinearLayout(this);top.setOrientation(LinearLayout.VERTICAL);
    top.setPadding(dp(18),dp(15),dp(18),dp(12));top.setBackgroundColor(0xee072f30);
    top.addView(label("☸  Dhamma Light · Camera Learning",19,GOLD,true));
    top.addView(label("ပဋ္ဌာန်း Marker 01–24 ကို Camera ဖြင့် ချိန်ပါ",13,Color.WHITE,false));
    FrameLayout.LayoutParams tp=new FrameLayout.LayoutParams(-1,-2,Gravity.TOP);scene.addView(top,tp);
    info=new LinearLayout(this);info.setOrientation(LinearLayout.VERTICAL);
    info.setPadding(dp(17),dp(14),dp(17),dp(13));info.setBackground(rounded(0xf4083b3b,GOLD));
    banner=label("◎ MARKER SCAN MODE — OFFLINE",13,GOLD,true);info.addView(banner);
    title=label("Camera ကို QR Marker ဆီ ချိန်ပါ",19,Color.WHITE,true);info.addView(title);
    description=label("စာအုပ်မှာ ပူးတွဲထားသော DL:01 မှ DL:24 အထိ QR Marker ကို ဖတ်မိတာနဲ့ Lesson အကြောင်းအရာကို ဒီနေရာမှာ ပြပါမယ်။",14,0xfff0e8d7,false);
    description.setPadding(0,dp(5),0,dp(7));info.addView(description);
    LinearLayout actions=new LinearLayout(this);
    actions.addView(button("▣ ရုပ်ပြ",()->openComic()),new LinearLayout.LayoutParams(0,dp(51),1));
    actions.addView(button("🔊 အသံ",()->speakLesson()),new LinearLayout.LayoutParams(0,dp(51),1));
    actions.addView(button("↻ ပြန် Scan",()->{selected=-1;lastCode="";display(-1);}),new LinearLayout.LayoutParams(0,dp(51),1));
    info.addView(actions);
    FrameLayout.LayoutParams bp=new FrameLayout.LayoutParams(-1,-2,Gravity.BOTTOM);bp.setMargins(dp(12),0,dp(12),dp(20));
    scene.addView(info,bp);
    if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
      if(checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED)
        requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST);
      else startCamera();
    }else{
      title.setText("ဒီဖုန်းမှာ Camera မရှိပါ");description.setText("ရုပ်ပြပဋ္ဌာန်းကို Home Menu မှ ဖတ်ရှုနိုင်ပါတယ်။");
    }
  }
  private int parseMarker(String raw){
    String v=raw.trim().toUpperCase(Locale.US);
    if(!v.matches("DL:(0[1-9]|1[0-9]|2[0-4])"))return -1;
    try{return Integer.parseInt(v.substring(3))-1;}catch(NumberFormatException e){return -1;}
  }
  private void display(int index){
    selected=index;arrows.invalidate();
    if(index<0){
      banner.setText("◎ MARKER SCAN MODE — OFFLINE");
      title.setText("Camera ကို QR Marker ဆီ ချိန်ပါ");
      description.setText("ပဋ္ဌာန်း Lesson Marker ကို Scan ဖတ်ပြီး အဓိပ္ပာယ်၊ ရုပ်ပြနဲ့ အသံဖတ်ပြခြင်းကို ဝင်နိုင်ပါတယ်။");
      return;
    }
    banner.setText(String.format(Locale.US,"✓ MARKER %02d / 24 • CAMERA OVERLAY",index+1));
    title.setText(ComicPrototypeActivity.lessonName(index));
    description.setText(ComicPrototypeActivity.lessonMeaning(index)
      +"\n\nဒီအကျဉ်းချုပ်ကို သင်ယူရေးအထောက်အကူအဖြစ် ပြထားသည်။ မူရင်းစာအုပ်နဲ့ ရုပ်ပြမှာ ဆက်လေ့လာနိုင်ပါတယ်။");
  }
  private void openComic(){
    if(selected<0){Toast.makeText(this,"ပထမ Marker Scan ဖတ်ပါ",Toast.LENGTH_SHORT).show();return;}
    Intent i=new Intent(this,BundledComicReaderActivity.class);i.putExtra("lesson_page",selected);startActivity(i);
  }
  private void speakLesson(){
    if(selected<0){Toast.makeText(this,"Marker ရွေးပါ",Toast.LENGTH_SHORT).show();return;}
    String words=ComicPrototypeActivity.lessonName(selected)+"။ "+ComicPrototypeActivity.lessonMeaning(selected);
    if(speech!=null){speech.speak(words,TextToSpeech.QUEUE_FLUSH,null,"marker");return;}
    speech=new TextToSpeech(this,s->{
      if(s!=TextToSpeech.SUCCESS||speech==null)return;
      int lang=speech.setLanguage(new Locale("my","MM"));
      if(lang==TextToSpeech.LANG_MISSING_DATA||lang==TextToSpeech.LANG_NOT_SUPPORTED)
        Toast.makeText(this,"Myanmar TTS Voice ကို ဖုန်းမှာ ထည့်ပါ",Toast.LENGTH_LONG).show();
      else speech.speak(words,TextToSpeech.QUEUE_FLUSH,null,"marker");
    });
  }
  private void startCamera(){if(!resumed){camera.resume();resumed=true;}}
  @Override protected void onResume(){super.onResume();if(camera!=null&&checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)startCamera();}
  @Override protected void onPause(){if(camera!=null)camera.pause();resumed=false;super.onPause();}
  @Override protected void onDestroy(){if(speech!=null){speech.stop();speech.shutdown();}super.onDestroy();}
  @Override public void onRequestPermissionsResult(int request,String[] permissions,int[] grants){
    super.onRequestPermissionsResult(request,permissions,grants);
    if(request==CAMERA_REQUEST){
      if(grants.length>0&&grants[0]==PackageManager.PERMISSION_GRANTED)startCamera();
      else {title.setText("Camera Permission လိုအပ်ပါတယ်");description.setText("App Settings မှ Camera Permission ပေးပြီး ပြန်ဖွင့်ပါ။");}
    }
  }
  private final class CauseOverlay extends View {
    private final Paint paint=new Paint(3);
    CauseOverlay(){super(CameraMarkerLearningActivity.this);setClickable(false);}
    @Override protected void onDraw(Canvas c){
      super.onDraw(c);
      if(selected<0)return;
      float cx=getWidth()/2f, cy=getHeight()*0.42f;
      paint.setColor(0xddeabe65);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(dp(4));
      c.drawCircle(cx,cy,dp(43),paint);
      c.drawCircle(cx,cy,dp(52),paint);
      float r=dp(95);
      for(int i=0;i<4;i++){
        float rad=(float)(i*Math.PI/2+Math.PI/4);
        float tx=cx+(float)Math.cos(rad)*r;
        float ty=cy+(float)Math.sin(rad)*r;
        paint.setStyle(Paint.Style.STROKE);c.drawLine(cx,cy,tx,ty,paint);
        paint.setStyle(Paint.Style.FILL);paint.setColor(0xe6eabc72);
        c.drawCircle(tx,ty,dp(12),paint);paint.setColor(0xddeabe65);
      }
      paint.setStyle(Paint.Style.FILL);paint.setTextSize(dp(23));paint.setTypeface(Typeface.DEFAULT_BOLD);
      c.drawText(String.format(Locale.US,"%02d",selected+1),cx-dp(15),cy+dp(8),paint);
    }
  }
}