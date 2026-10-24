package com.aaii.dhammalight.preview;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

/** Pinch, hand drag, double tap, easy buttons; swipe page only at fit zoom. */
public final class ZoomPanImageView extends ImageView {
 public interface PageSwipeListener { void onPageSwipe(int direction); }
 private final Matrix transform=new Matrix();
 private ScaleGestureDetector pinch;
 private GestureDetector taps;
 private PageSwipeListener listener;
 private float currentZoom=1f,lastX,lastY;
 private boolean moved=false,scaling=false;
 public ZoomPanImageView(Context ctx){super(ctx);init(ctx);}
 public ZoomPanImageView(Context ctx,AttributeSet attr){super(ctx,attr);init(ctx);}
 private void init(Context ctx){
  setScaleType(ScaleType.MATRIX);setBackgroundColor(0xffffffff);
  setContentDescription("Pinch to zoom, drag with one finger, double tap zoom, swipe pages at fit");
  pinch=new ScaleGestureDetector(ctx,new ScaleGestureDetector.SimpleOnScaleGestureListener(){
   @Override public boolean onScaleBegin(ScaleGestureDetector d){scaling=true;return true;}
   @Override public boolean onScale(ScaleGestureDetector d){zoomBy(d.getScaleFactor(),d.getFocusX(),d.getFocusY());return true;}
   @Override public void onScaleEnd(ScaleGestureDetector d){scaling=false;}
  });
  taps=new GestureDetector(ctx,new GestureDetector.SimpleOnGestureListener(){
   @Override public boolean onDown(MotionEvent e){return true;}
   @Override public boolean onDoubleTap(MotionEvent e){
    if(currentZoom>1.04f)resetZoom();else zoomBy(2.4f,e.getX(),e.getY());return true;
   }
   @Override public boolean onFling(MotionEvent a,MotionEvent b,float vx,float vy){
    if(listener==null||currentZoom>1.04f||a==null||b==null)return false;
    float dx=b.getX()-a.getX(),dy=b.getY()-a.getY();
    if(Math.abs(dx)>getResources().getDisplayMetrics().density*90f&&Math.abs(dx)>Math.abs(dy)*1.5f&&Math.abs(vx)>350){
     listener.onPageSwipe(dx<0?1:-1);return true;
    }return false;
   }
  });
 }
 public void setOnPageSwipeListener(PageSwipeListener v){listener=v;}
 @Override public void setImageBitmap(Bitmap b){super.setImageBitmap(b);post(this::resetZoom);}
 @Override protected void onSizeChanged(int w,int h,int ow,int oh){super.onSizeChanged(w,h,ow,oh);post(this::resetZoom);}
 public void resetZoom(){
  Drawable draw=getDrawable();if(draw==null||getWidth()<=0||getHeight()<=0)return;
  float w=draw.getIntrinsicWidth(),h=draw.getIntrinsicHeight();if(w<=0||h<=0)return;
  float sw=getWidth()-getPaddingLeft()-getPaddingRight(),sh=getHeight()-getPaddingTop()-getPaddingBottom();
  float fit=Math.min(sw/w,sh/h);
  currentZoom=1f;transform.reset();transform.postScale(fit,fit);
  transform.postTranslate(getPaddingLeft()+(sw-w*fit)/2f,getPaddingTop()+(sh-h*fit)/2f);
  setImageMatrix(transform);
 }
 public void zoomIn(){zoomBy(1.45f,getWidth()/2f,getHeight()/2f);}
 public void zoomOut(){zoomBy(1f/1.45f,getWidth()/2f,getHeight()/2f);}
 private void zoomBy(float n,float cx,float cy){
  if(getDrawable()==null)return;
  float zoom=Math.min(5f,Math.max(1f,currentZoom*n));if(Math.abs(zoom-currentZoom)<0.001f)return;
  float ratio=zoom/currentZoom;currentZoom=zoom;
  transform.postScale(ratio,ratio,cx,cy);bound();setImageMatrix(transform);
 }
 private void bound(){
  Drawable drawable=getDrawable();if(drawable==null)return;
  RectF rect=new RectF(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());transform.mapRect(rect);
  float left=getPaddingLeft(),top=getPaddingTop(),right=getWidth()-getPaddingRight(),bottom=getHeight()-getPaddingBottom();
  float dx=0,dy=0;
  if(rect.width()<=right-left)dx=left+(right-left-rect.width())/2f-rect.left;
  else if(rect.left>left)dx=left-rect.left;
  else if(rect.right<right)dx=right-rect.right;
  if(rect.height()<=bottom-top)dy=top+(bottom-top-rect.height())/2f-rect.top;
  else if(rect.top>top)dy=top-rect.top;
  else if(rect.bottom<bottom)dy=bottom-rect.bottom;
  transform.postTranslate(dx,dy);
 }
 @Override public boolean onTouchEvent(MotionEvent e){
  if(getDrawable()==null)return super.onTouchEvent(e);
  pinch.onTouchEvent(e);taps.onTouchEvent(e);
  switch(e.getActionMasked()){
   case MotionEvent.ACTION_DOWN:lastX=e.getX();lastY=e.getY();moved=false;return true;
   case MotionEvent.ACTION_POINTER_DOWN:scaling=true;return true;
   case MotionEvent.ACTION_MOVE:
    if(!pinch.isInProgress()&&e.getPointerCount()==1&&currentZoom>1.01f){
     float dx=e.getX()-lastX,dy=e.getY()-lastY;
     if(Math.abs(dx)+Math.abs(dy)>1f){transform.postTranslate(dx,dy);bound();setImageMatrix(transform);moved=true;}
    }lastX=e.getX();lastY=e.getY();return true;
   case MotionEvent.ACTION_POINTER_UP:lastX=e.getX(0);lastY=e.getY(0);return true;
   case MotionEvent.ACTION_UP:if(!moved&&!scaling)performClick();scaling=false;return true;
   case MotionEvent.ACTION_CANCEL:scaling=false;return true;
  }return true;
 }
 @Override public boolean performClick(){super.performClick();return true;}
}