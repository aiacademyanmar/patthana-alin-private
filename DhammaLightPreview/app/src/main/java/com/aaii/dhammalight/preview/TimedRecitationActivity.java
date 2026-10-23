package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Locale;

/** Actual native audio + timed paragraph highlight implementation.
 * Recording, reviewed transcription, and segment timings are deliberately USER-IMPORTED.
 * Draft segments have null timings and are never presented as authenticated recitations.
 */
public final class TimedRecitationActivity extends Activity {
  private static final int PICK_AUDIO=412, PICK_TEXT=413;
  private static final int TEAL=Color.rgb(5,47,45),GOLD=Color.rgb(230,181,70);
  private static final int CREAM=Color.rgb(255,248,233),INK=Color.rgb(41,48,42);
  private final Handler handler=new Handler(Looper.getMainLooper());
  private final Runnable progressTick=new Runnable(){public void run(){refreshTime();handler.postDelayed(this,180);}};
  private SharedPreferences pref;
  private LinearLayout root,contents;
  private TextView label,status,time;
  private SeekBar seek;
  private MediaPlayer player;
  private JSONObject lesson;
  private JSONArray segments;
  private int index,highlight=-1;
  private TextView[] segmentViews;
  private boolean seeking;
  private Uri audio;
  private int dp(float d){return (int)(d*getResources().getDisplayMetrics().density+0.5f);}
  private GradientDrawable bg(int c){GradientDrawable g=new GradientDrawable();g.setColor(c);g.setCornerRadius(dp(12));return g;}
  private TextView tv(String text,int size,int color,boolean strong){TextView v=new TextView(this);v.setText(text);v.setTextSize(size);v.setTextColor(color);if(strong)v.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return v;}
  private Button btn(String name,Runnable run){Button b=new Button(this);b.setAllCaps(false);b.setText(name);b.setOnClickListener(x->run.run());return b;}
  private void add(LinearLayout l,View v,int margin){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(dp(10),dp(margin),dp(10),dp(margin));l.addView(v,lp);}
  @Override public void onCreate(Bundle s){super.onCreate(s);pref=getSharedPreferences("timed_recitation_v42",MODE_PRIVATE);
    index=Math.max(0,Math.min(23,getIntent().getIntExtra("lesson_index",0)));loadLesson();makeUI();
    handler.post(progressTick);
  }
  private String prefix(){return "lesson_"+index+"_";}
  private void loadLesson(){
    String saved=pref.getString(prefix()+"content","");
    lesson=PaliContent.lessonDraft(index);
    if(!saved.isEmpty()){try{JSONObject j=new JSONObject(saved);if(j.has("segments")&&j.getJSONArray("segments").length()>0)lesson=j;}catch(Exception ignored){}}
    segments=lesson.optJSONArray("segments");if(segments==null)segments=new JSONArray();
    String u=pref.getString(prefix()+"audio","");if(!u.isEmpty())audio=Uri.parse(u);
  }
  private void makeUI(){
    root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(CREAM);setContentView(root);
    LinearLayout top=new LinearLayout(this);top.setOrientation(LinearLayout.VERTICAL);top.setPadding(dp(14),dp(13),dp(14),dp(13));top.setBackgroundColor(TEAL);
    top.addView(tv("☸ Dhamma Light · စာသား + အသံ",20,GOLD,true));
    top.addView(tv(ComicPrototypeActivity.lessonName(index)+"  •  "+PaliContent.ROMAN[index],16,Color.WHITE,true));root.addView(top);
    status=tv("",13,INK,false);add(root,status,5);
    ScrollView scroll=new ScrollView(this);contents=new LinearLayout(this);contents.setOrientation(LinearLayout.VERTICAL);scroll.addView(contents);
    root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));renderText();
    time=tv("00:00 / 00:00",15,INK,true);time.setGravity(Gravity.CENTER);add(root,time,3);
    seek=new SeekBar(this);seek.setMax(1000);seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
      public void onStartTrackingTouch(SeekBar b){seeking=true;}
      public void onProgressChanged(SeekBar b,int val,boolean user){}
      public void onStopTrackingTouch(SeekBar b){if(player!=null)try{player.seekTo((int)(player.getDuration()*b.getProgress()/1000.0));}catch(Exception ignored){}seeking=false;}
    });root.addView(seek);
    LinearLayout actions=new LinearLayout(this);
    actions.addView(btn("▶ / ❚❚",this::toggle),new LinearLayout.LayoutParams(0,dp(50),1));
    actions.addView(btn("↶ 5s",()->skip(-5000)),new LinearLayout.LayoutParams(0,dp(50),1));
    actions.addView(btn("5s ↷",()->skip(5000)),new LinearLayout.LayoutParams(0,dp(50),1));
    root.addView(actions);
    LinearLayout imports=new LinearLayout(this);
    imports.addView(btn("♫ MP3",()->pick(PICK_AUDIO,"audio/*")),new LinearLayout.LayoutParams(0,dp(54),1));
    imports.addView(btn("▤ JSON",()->pick(PICK_TEXT,"application/json")),new LinearLayout.LayoutParams(0,dp(54),1));
    imports.addView(btn("Next ▷",()->{index=(index+1)%24;release();audio=null;loadLesson();makeUI();}),new LinearLayout.LayoutParams(0,dp(54),1));
    root.addView(imports);
    renderStatus();
  }
  private void renderStatus(){
    String editorial=lesson.optString("editorial_status","draft");
    boolean verified="reviewed".equals(editorial)&&!lesson.optString("reviewed_by","").isEmpty();
    boolean timed=hasTimings();
    status.setText((verified?"✓ REVIEWED CONTENT":"⚠ မူကြမ်းစာသား / အတည်ပြုမပြီး")+
      "\n"+(timed?"Timed highlighting ready":"Audio timecodes မရှိသေး: အသံနဲ့စာသားကို အလိုအလျောက် မညှိနိုင်သေး")+
      "\n"+(audio==null?"အသံဖိုင် မရွေးရသေး":"ရွေးထားသော အသံဖိုင်: "+audio.getLastPathSegment()));
  }
  private void renderText(){
    contents.removeAllViews();segmentViews=new TextView[segments.length()];
    for(int i=0;i<segments.length();i++){
      JSONObject o=segments.optJSONObject(i);if(o==null)continue;
      String pali=o.optString("pali","");
      String mm=o.optString("myanmar","");
      String line=(!pali.isEmpty()?pali+"\n":"")+mm;
      TextView row=tv(line,17,INK,false);row.setPadding(dp(13),dp(14),dp(13),dp(14));
      row.setBackground(bg(Color.WHITE));final int j=i;
      row.setOnClickListener(v->{long start=o.optLong("start_ms",-1);if(start>=0&&player!=null)try{player.seekTo((int)start);}catch(Exception ignored){}});
      add(contents,row,4);segmentViews[i]=row;
    }
    add(contents,tv("Text segments can be tapped to seek ONLY after valid per-segment timestamps are imported.",12,INK,false),5);
    highlight=-1;
  }
  private boolean hasTimings(){
    if(segments==null||segments.length()<1)return false;
    for(int i=0;i<segments.length();i++){JSONObject o=segments.optJSONObject(i);if(o==null||o.isNull("start_ms")||o.isNull("end_ms")||o.optLong("end_ms",-1)<=o.optLong("start_ms",-1))return false;}
    return true;
  }
  private void refreshTime(){
    if(player==null)return;
    try{
      int now=player.getCurrentPosition(),duration=player.getDuration();
      time.setText(format(now)+" / "+format(duration));
      if(!seeking&&duration>0)seek.setProgress((int)(1000L*now/duration));
      int chosen=-1;
      if(hasTimings())for(int i=0;i<segments.length();i++){
        JSONObject o=segments.optJSONObject(i);
        if(o!=null&&now>=o.optLong("start_ms",Long.MAX_VALUE)&&now<o.optLong("end_ms",0)){chosen=i;break;}
      }
      if(chosen!=highlight){
        highlight=chosen;
        for(int i=0;i<segmentViews.length;i++)if(segmentViews[i]!=null)segmentViews[i].setBackground(bg(i==chosen?Color.rgb(255,226,140):Color.WHITE));
        if(chosen>=0&&segmentViews[chosen]!=null){View v=segmentViews[chosen];v.post(()->{if(v.getParent()!=null){v.requestRectangleOnScreen(new android.graphics.Rect(0,0,v.getWidth(),v.getHeight()),true);}});}
      }
    }catch(Exception ignored){}
  }
  private String format(int millis){int x=Math.max(0,millis)/1000;return String.format(Locale.US,"%02d:%02d",x/60,x%60);}
  private void toggle(){
    if(player==null){
      if(audio==null){pick(PICK_AUDIO,"audio/*");return;}
      try{player=new MediaPlayer();player.setDataSource(this,audio);player.setOnPreparedListener(p->{p.start();refreshTime();});player.setOnCompletionListener(p->refreshTime());player.prepareAsync();}
      catch(Exception e){Toast.makeText(this,"Audio open failed: "+e.getMessage(),Toast.LENGTH_LONG).show();release();}
    }else try{if(player.isPlaying())player.pause();else player.start();}catch(Exception ignored){}
  }
  private void skip(int amount){if(player!=null)try{player.seekTo(Math.max(0,Math.min(player.getDuration(),player.getCurrentPosition()+amount)));}catch(Exception ignored){}}
  private void pick(int type,String mime){
    Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType(mime);
    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
    startActivityForResult(i,type);
  }
  @Override protected void onActivityResult(int request,int result,Intent data){
    super.onActivityResult(request,result,data);if(result!=RESULT_OK||data==null||data.getData()==null)return;
    Uri uri=data.getData();
    try{getContentResolver().takePersistableUriPermission(uri,data.getFlags()&Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}
    if(request==PICK_AUDIO){release();audio=uri;pref.edit().putString(prefix()+"audio",uri.toString()).apply();renderStatus();toggle();}
    if(request==PICK_TEXT){
      try(InputStream in=getContentResolver().openInputStream(uri);ByteArrayOutputStream out=new ByteArrayOutputStream()){
        if(in==null)throw new IllegalArgumentException("Missing file");
        byte[] buf=new byte[4096];int n;while((n=in.read(buf))!=-1){out.write(buf,0,n);if(out.size()>250000)throw new IllegalArgumentException("Content >250 KB");}
        JSONObject incoming=new JSONObject(out.toString("UTF-8"));validate(incoming);
        pref.edit().putString(prefix()+"content",incoming.toString()).apply();loadLesson();renderText();renderStatus();
        Toast.makeText(this,"Segment JSON imported",Toast.LENGTH_SHORT).show();
      }catch(Exception e){new AlertDialog.Builder(this).setTitle("Content Import Error").setMessage(e.getMessage()).setPositiveButton("OK",null).show();}
    }
  }
  private void validate(JSONObject json)throws Exception{
    int lessonId=json.optInt("lesson_id",-1);
    if(lessonId!=index+1)throw new IllegalArgumentException("lesson_id must equal "+(index+1));
    JSONArray list=json.getJSONArray("segments");if(list.length()<1||list.length()>300)throw new IllegalArgumentException("1-300 segments required");
    long previous=-1;
    for(int j=0;j<list.length();j++){
      JSONObject row=list.getJSONObject(j);String pali=row.optString("pali",""),mm=row.optString("myanmar","");
      if(TextUtils.isEmpty(pali)&&TextUtils.isEmpty(mm))throw new IllegalArgumentException("Empty segment "+j);
      if(pali.length()>1000||mm.length()>5000)throw new IllegalArgumentException("Segment too long");
      boolean a=row.has("start_ms")&&!row.isNull("start_ms");
      boolean b=row.has("end_ms")&&!row.isNull("end_ms");
      if(a!=b)throw new IllegalArgumentException("Both timestamps or neither required at segment "+j);
      if(a){long start=row.getLong("start_ms"),end=row.getLong("end_ms");if(start<0||end<=start||start<previous)throw new IllegalArgumentException("Invalid ordered timecodes at "+j);previous=end;}
    }
  }
  private void release(){if(player!=null){try{player.release();}catch(Exception ignored){}player=null;}}
  @Override protected void onDestroy(){handler.removeCallbacks(progressTick);release();super.onDestroy();}
}
