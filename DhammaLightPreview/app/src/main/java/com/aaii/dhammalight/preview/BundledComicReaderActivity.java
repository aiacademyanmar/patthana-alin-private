package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.GestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.InputStream;
import java.util.Locale;

/**
 * Private/offline artwork is added to the RELEASE APK *after compilation*.
 * The public GitHub source contains only loader code (no original book artwork).
 * Assets: assets/pages/patthana_01.jpg .. assets/pages/patthana_24.jpg
 */
public final class BundledComicReaderActivity extends Activity {
  private static final int TEAL=Color.rgb(3,49,48);
  private static final int GOLD=Color.rgb(233,183,70);
  private static final int IVORY=Color.rgb(255,250,239);
  private static final int INK=Color.rgb(40,40,36);
  private ImageView pageView;
  private Bitmap currentBitmap;
  private LinearLayout root;
  private SharedPreferences state;
  private int page=0;
  private TextView pageCounter, title;
  private float zoom=1;
  private final Matrix matrix=new Matrix();
  private ScaleGestureDetector scaling;
  private GestureDetector gestures;
  private float prevX,prevY;

  public static String assetPath(int n){
    return String.format(Locale.US,"pages/patthana_%02d.jpg",n+1);
  }
  private int dp(float value){return (int)(getResources().getDisplayMetrics().density*value+0.5f);}
  private TextView label(String text,int size,int color,boolean bold){
    TextView t=new TextView(this);t.setText(text);t.setTextSize(size);t.setTextColor(color);
    if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;
  }
  private GradientDrawable background(int color,int radius){
    GradientDrawable shape=new GradientDrawable();
    shape.setColor(color);shape.setCornerRadius(dp(radius));return shape;
  }
  private Button button(String text,int color,int fg,Runnable action){
    Button b=new Button(this);b.setAllCaps(false);b.setText(text);b.setTextSize(14);
    b.setTextColor(fg);b.setBackground(background(color,10));b.setOnClickListener(v->action.run());
    return b;
  }
  @Override public void onCreate(Bundle bundle){
    super.onCreate(bundle);getWindow().setStatusBarColor(TEAL);getWindow().setNavigationBarColor(TEAL);
    state=getSharedPreferences("comic_prototype",MODE_PRIVATE);
    page=Math.max(0,Math.min(23,getIntent().getIntExtra("lesson_page",state.getInt("last_lesson",0))));
    build();showPage(page);
  }
  private void build(){
    root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(TEAL);setContentView(root);
    LinearLayout head=new LinearLayout(this);head.setOrientation(LinearLayout.VERTICAL);head.setPadding(dp(14),dp(12),dp(14),dp(9));
    TextView brand=label("☸  Dhamma Light · ရုပ်ပြပဋ္ဌာန်း",20,GOLD,true);head.addView(brand);
    title=label("",15,IVORY,true);head.addView(title);root.addView(head);
    pageCounter=label("",14,GOLD,true);pageCounter.setGravity(Gravity.CENTER);root.addView(pageCounter);
    FrameLayout canvas=new FrameLayout(this);canvas.setBackgroundColor(Color.rgb(6,40,37));
    pageView=new ImageView(this);pageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    canvas.addView(pageView,new FrameLayout.LayoutParams(-1,-1));
    root.addView(canvas,new LinearLayout.LayoutParams(-1,0,1));

    scaling=new ScaleGestureDetector(this,new ScaleGestureDetector.SimpleOnScaleGestureListener(){
      @Override public boolean onScale(ScaleGestureDetector detector){
        float target=Math.max(1.0f,Math.min(3.0f,zoom*detector.getScaleFactor()));
        zoom=target;
        pageView.setScaleX(zoom);pageView.setScaleY(zoom);
        return true;
      }
    });
    gestures=new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
      @Override public boolean onDoubleTap(MotionEvent e){
        zoom=zoom>1.01f?1:2;resetTransforms(false);return true;
      }
      @Override public boolean onSingleTapConfirmed(MotionEvent e){return true;}
    });
    pageView.setOnTouchListener((v,event)->{
      scaling.onTouchEvent(event);gestures.onTouchEvent(event);
      switch(event.getActionMasked()){
        case MotionEvent.ACTION_DOWN:prevX=event.getX();prevY=event.getY();return true;
        case MotionEvent.ACTION_MOVE:
          if(!scaling.isInProgress()&&event.getPointerCount()==1&&zoom>1){
            float moveX=event.getX()-prevX, moveY=event.getY()-prevY;
            float width=pageView.getWidth(),height=pageView.getHeight();
            float maxX=(zoom-1)*width/2,maxY=(zoom-1)*height/2;
            pageView.setTranslationX(Math.min(maxX,Math.max(-maxX,pageView.getTranslationX()+moveX)));
            pageView.setTranslationY(Math.min(maxY,Math.max(-maxY,pageView.getTranslationY()+moveY)));
          }
          prevX=event.getX();prevY=event.getY();return true;
        case MotionEvent.ACTION_POINTER_UP:case MotionEvent.ACTION_UP:return true;
      }
      return true;
    });

    LinearLayout tabs=new LinearLayout(this);tabs.setPadding(dp(8),dp(7),dp(8),dp(7));
    tabs.setBackgroundColor(IVORY);
    tabs.addView(button("◀ ယခင်",TEAL,IVORY,()->showPage(Math.max(0,page-1))),new LinearLayout.LayoutParams(0,dp(50),1));
    tabs.addView(button("★ မှတ်",GOLD,INK,()->{
      state.edit().putInt("bookmarked",page).apply();Toast.makeText(this,"Page "+(page+1)+" bookmarked",Toast.LENGTH_SHORT).show();
    }),new LinearLayout.LayoutParams(0,dp(50),1));
    tabs.addView(button("နောက် ▶",TEAL,IVORY,()->showPage(Math.min(23,page+1))),new LinearLayout.LayoutParams(0,dp(50),1));
    root.addView(tabs);

    LinearLayout footer=new LinearLayout(this);footer.setPadding(dp(8),dp(6),dp(8),dp(8));
    footer.addView(button("▣ ၂၄ မျက်နှာ",GOLD,INK,()->finish()),new LinearLayout.LayoutParams(0,dp(48),1));
    footer.addView(button("✓ ဖတ်ပြီး",IVORY,TEAL,()->{
      state.edit().putBoolean("read_"+page,true).apply();
      Toast.makeText(this,"ဖတ်ပြီးမှတ်သားခဲ့သည်",Toast.LENGTH_SHORT).show();
    }),new LinearLayout.LayoutParams(0,dp(48),1));
    footer.addView(button("⌂ Bookmark",GOLD,INK,()->showPage(state.getInt("bookmarked",page))),new LinearLayout.LayoutParams(0,dp(48),1));
    root.addView(footer);
  }
  private void resetTransforms(boolean resetImage){
    pageView.setScaleX(zoom);pageView.setScaleY(zoom);
    pageView.setTranslationX(0);pageView.setTranslationY(0);
  }
  private void showPage(int newPage){
    page=Math.max(0,Math.min(23,newPage));state.edit().putInt("last_lesson",page).apply();
    title.setText(String.format(Locale.US,"%02d",page+1)+"  "+ComicPrototypeActivity.lessonName(page));
    pageCounter.setText((page+1)+" / 24    ·    Offline Original Comic");
    pageView.setImageDrawable(null);
    if(currentBitmap!=null){currentBitmap.recycle();currentBitmap=null;}
    zoom=1;resetTransforms(true);
    try(InputStream stream=getAssets().open(assetPath(page))){
      BitmapFactory.Options opts=new BitmapFactory.Options();opts.inPreferredConfig=Bitmap.Config.RGB_565;
      currentBitmap=BitmapFactory.decodeStream(stream,null,opts);
      if(currentBitmap==null)throw new IllegalStateException("Page decoding failed");
      pageView.setImageBitmap(currentBitmap);
    }catch(Exception e){
      Toast.makeText(this,"Comic page not bundled. Select the source PDF.",Toast.LENGTH_LONG).show();
      Intent i=new Intent(this,NativePdfReaderActivity.class);
      i.putExtra("comic_mode",true);i.putExtra("lesson_page",page);
      startActivity(i);finish();
    }
  }
  @Override protected void onDestroy(){
    if(pageView!=null)pageView.setImageDrawable(null);
    if(currentBitmap!=null){currentBitmap.recycle();currentBitmap=null;}
    super.onDestroy();
  }
}