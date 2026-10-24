package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.content.Intent;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.widget.*;
import android.view.View;
import android.graphics.drawable.GradientDrawable;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import android.util.SparseArray;
import android.content.SharedPreferences;
import java.io.IOException;
import java.util.Locale;

/** Camera marker-based learning overlay, NOT spatial ARCore tracking.
 * Scanning a QR marker such as PATTHANA:01 shows the related 24-Paccaya interpretation.
 * Keeps recognition on-device via Google Mobile Vision and needs Google Play services.
 */
@SuppressWarnings("deprecation")
public final class MarkerCameraActivity extends Activity {
  private static final int CAMERA_REQ=4001;
  private static final int TEAL=0xff063c3e,GOLD=0xffe3b75f;
  private CameraSource camera;
  private BarcodeDetector detector;
  private SurfaceView preview;
  private TextView content,status;
  private int current=-1;
  private SurfaceHolder.Callback cb;
  private boolean started=false;
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(TEAL);setContentView(root);
    TextView header=new TextView(this);header.setText("☸ Camera Learning • ပဋ္ဌာန်း ၂၄ ပစ္စည်း");
    header.setTextColor(GOLD);header.setTextSize(19);header.setPadding(16,16,12,10);root.addView(header);
    preview=new SurfaceView(this);root.addView(preview,new LinearLayout.LayoutParams(-1,0,1));
    content=new TextView(this);content.setTextSize(17);content.setTextColor(Color.WHITE);
    content.setPadding(20,14,20,20);content.setText("PATTHANA:01 မှ PATTHANA:24 QR Code များကို Camera နဲ့ Scan လုပ်ပါ။");
    root.addView(content);
    status=new TextView(this);status.setText("Marker-based camera overlay · Full 3D ARCore မဟုတ်သေးပါ");
    status.setTextSize(12);status.setTextColor(GOLD);status.setPadding(14,4,14,9);root.addView(status);
    Button list=new Button(this);list.setAllCaps(false);list.setText("☷ ပစ္စည်း ၂၄ မျိုး");
    list.setOnClickListener(v->{Intent i=new Intent(this,ComicPrototypeActivity.class);startActivity(i);});
    root.addView(list);
    if(Build.VERSION.SDK_INT>=23&&checkSelfPermission(Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
      requestPermissions(new String[]{Manifest.permission.CAMERA},CAMERA_REQ);
    }else initialize();
  }
  private void initialize(){
    detector=new BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.QR_CODE).build();
    if(!detector.isOperational()){status.setText("QR recognizer မရနိုင်သေးပါ။ Google Play services လိုအပ်နိုင်သည်။");return;}
    detector.setProcessor(new Detector.Processor<Barcode>(){
      public void release(){}
      public void receiveDetections(Detector.Detections<Barcode> d){
        SparseArray<Barcode> codes=d.getDetectedItems();
        for(int i=0;i<codes.size();i++){
          Barcode b=codes.valueAt(i);
          int id=parseCode(b.rawValue);
          if(id>=0&&id!=current){current=id;runOnUiThread(()->show(id));break;}
        }
      }
    });
    camera=new CameraSource.Builder(this,detector).setFacing(CameraSource.CAMERA_FACING_BACK)
      .setRequestedPreviewSize(1280,720).setAutoFocusEnabled(true).build();
    cb=new SurfaceHolder.Callback(){
      public void surfaceCreated(SurfaceHolder h){start(h);}
      public void surfaceChanged(SurfaceHolder h,int fmt,int w,int hei){}
      public void surfaceDestroyed(SurfaceHolder h){stop();}
    };
    preview.getHolder().addCallback(cb);
    if(preview.getHolder().getSurface().isValid())start(preview.getHolder());
  }
  private int parseCode(String value){
    if(value==null||!value.matches("PATTHANA:(0[1-9]|1[0-9]|2[0-4])"))return -1;
    return Integer.parseInt(value.substring(value.length()-2))-1;
  }
  private void show(int id){
    content.setText(String.format(Locale.US,"%02d",id+1)+" • "+ComicPrototypeActivity.lessonName(id)+"\n"+ComicPrototypeActivity.lessonMeaning(id)+"\n"+PaliContent.REFLECT[id]);
    status.setText("QR Detected • On-camera educational overlay • "+(id+1)+"/24");
  }
  private void start(SurfaceHolder h){if(camera==null||started)return;
    try{if(Build.VERSION.SDK_INT<23||checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED){
      camera.start(h);started=true;
    }}catch(IOException|RuntimeException e){status.setText("Camera startup failed: "+e.getMessage());}}
  private void stop(){if(camera!=null&&started){camera.stop();started=false;}}
  @Override public void onRequestPermissionsResult(int code,String[] p,int[] results){
    super.onRequestPermissionsResult(code,p,results);
    if(code==CAMERA_REQ){if(results.length>0&&results[0]==PackageManager.PERMISSION_GRANTED)initialize();
    else status.setText("Camera permission မရရှိပါ။ Settings မှ Camera ခွင့်ပြုပါ။");}
  }
  @Override protected void onResume(){super.onResume();if(camera!=null&&preview!=null&&preview.getHolder().getSurface().isValid())start(preview.getHolder());}
  @Override protected void onPause(){stop();super.onPause();}
  @Override protected void onDestroy(){stop();if(camera!=null)camera.release();if(detector!=null)detector.release();super.onDestroy();}
}
