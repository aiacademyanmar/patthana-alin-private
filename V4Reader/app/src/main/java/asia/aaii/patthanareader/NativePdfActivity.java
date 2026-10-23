package asia.aaii.patthanareader;
import android.app.*;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.os.*;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import java.io.*;

public class NativePdfActivity extends Activity{
 private ParcelFileDescriptor fd;private PdfRenderer pdf;private Bitmap bitmap;private ImageView image;private TextView count;private int page=0;private float zoom=1f;
 @Override public void onCreate(Bundle s){super.onCreate(s);try{Uri u=getIntent().getData();if(u==null)throw new IOException("No PDF URI");fd=getContentResolver().openFileDescriptor(u,"r");if(fd==null)throw new IOException("PDF permission denied");pdf=new PdfRenderer(fd);if(pdf.getPageCount()==0)throw new IOException("Empty PDF");ui();render();}catch(Exception e){new AlertDialog.Builder(this).setTitle("PDF Reader Error").setMessage(e.toString()).setPositiveButton("Close",(a,b)->finish()).show();}}
 private Button button(String title,Runnable action){Button b=new Button(this);b.setAllCaps(false);b.setText(title);b.setOnClickListener(v->action.run());return b;}
 private void ui(){LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(Color.rgb(12,28,43));LinearLayout top=new LinearLayout(this);top.addView(button("‹ Back",this::finish));count=new TextView(this);count.setTextColor(Color.WHITE);count.setTextSize(17);count.setGravity(Gravity.CENTER);top.addView(count,new LinearLayout.LayoutParams(0,60,1));root.addView(top);ScrollView scroll=new ScrollView(this);image=new ImageView(this);image.setAdjustViewBounds(true);image.setScaleType(ImageView.ScaleType.FIT_CENTER);scroll.addView(image);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));LinearLayout bar=new LinearLayout(this);bar.addView(button("◀",()->{if(page>0){page--;render();}}),new LinearLayout.LayoutParams(0,65,1));bar.addView(button("−",()->{zoom=Math.max(.6f,zoom-.25f);render();}),new LinearLayout.LayoutParams(0,65,1));bar.addView(button("+",()->{zoom=Math.min(2.5f,zoom+.25f);render();}),new LinearLayout.LayoutParams(0,65,1));bar.addView(button("▶",()->{if(page<pdf.getPageCount()-1){page++;render();}}),new LinearLayout.LayoutParams(0,65,1));root.addView(bar);setContentView(root);}
 private void render(){if(pdf==null)return;try(PdfRenderer.Page p=pdf.openPage(page)){int width=Math.min(2400,Math.max(600,(int)(getResources().getDisplayMetrics().widthPixels*zoom)));int height=Math.min(3600,Math.max(1,(int)((double)p.getHeight()/p.getWidth()*width)));Bitmap next=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);next.eraseColor(Color.WHITE);p.render(next,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);image.setImageBitmap(next);if(bitmap!=null&&!bitmap.isRecycled())bitmap.recycle();bitmap=next;count.setText((page+1)+" / "+pdf.getPageCount());}catch(Exception e){Toast.makeText(this,"PDF page error: "+e.getMessage(),Toast.LENGTH_LONG).show();}}
 @Override protected void onDestroy(){if(image!=null)image.setImageDrawable(null);if(bitmap!=null&&!bitmap.isRecycled())bitmap.recycle();try{if(pdf!=null)pdf.close();}catch(Exception ignored){}try{if(fd!=null)fd.close();}catch(Exception ignored){}super.onDestroy();}
}
