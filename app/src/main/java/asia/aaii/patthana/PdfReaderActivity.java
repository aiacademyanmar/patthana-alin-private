package asia.aaii.patthana;

import android.app.*;
import android.os.*;
import android.net.Uri;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.*;
import android.widget.*;
import java.io.*;

public class PdfReaderActivity extends Activity {
  private PdfRenderer renderer;
  private ParcelFileDescriptor descriptor;
  private Bitmap bitmap;
  private ImageView image;
  private TextView status;
  private int page=0;
  private float zoom=1f;
  private File tempFile;
  @Override public void onCreate(Bundle b){
    super.onCreate(b);
    try {
      String path=getIntent().getStringExtra("local_pdf_path");
      if(path!=null){descriptor=ParcelFileDescriptor.open(new File(path),ParcelFileDescriptor.MODE_READ_ONLY);}
      else {
        Uri uri=getIntent().getData();
        if(uri==null)throw new IOException("PDF file missing");
        descriptor=getContentResolver().openFileDescriptor(uri,"r");
      }
      if(descriptor==null)throw new IOException("PDF permission denied");
      renderer=new PdfRenderer(descriptor);
      if(renderer.getPageCount()<=0)throw new IOException("PDF စာမျက်နှာမရှိ");
      buildUi();render();
    }catch(Exception e){
      new AlertDialog.Builder(this).setTitle("Native PDF Reader").setMessage("PDF ဖွင့်မရပါ — "+e.getMessage()).setPositiveButton("OK",(d,w)->finish()).show();
    }
  }
  private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
  private TextView btn(String value,Runnable run){
    TextView v=new TextView(this);v.setText(value);v.setTextSize(16);v.setTextColor(Color.WHITE);v.setGravity(Gravity.CENTER);v.setPadding(dp(7),dp(12),dp(7),dp(12));v.setOnClickListener(view->run.run());return v;
  }
  private void buildUi(){
    LinearLayout root=new LinearLayout(this);root.setBackgroundColor(Color.rgb(18,28,40));root.setOrientation(1);
    LinearLayout top=new LinearLayout(this);top.setBackgroundColor(Color.rgb(5,38,54));
    top.addView(btn("‹ Back",this::finish));
    status=btn("",()->{});top.addView(status,new LinearLayout.LayoutParams(0,dp(48),1));root.addView(top);
    ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
    image=new ImageView(this);image.setAdjustViewBounds(true);image.setScaleType(ImageView.ScaleType.FIT_CENTER);
    scroll.addView(image,new ScrollView.LayoutParams(-1,-2));root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
    LinearLayout footer=new LinearLayout(this);footer.setBackgroundColor(Color.rgb(5,38,54));
    footer.addView(btn("◀",()->{if(page>0){page--;render();}}),new LinearLayout.LayoutParams(0,dp(58),1));
    footer.addView(btn("−",()->{zoom=Math.max(0.7f,zoom-.2f);render();}),new LinearLayout.LayoutParams(0,dp(58),1));
    footer.addView(btn("+",()->{zoom=Math.min(2.4f,zoom+.2f);render();}),new LinearLayout.LayoutParams(0,dp(58),1));
    footer.addView(btn("▶",()->{if(page<renderer.getPageCount()-1){page++;render();}}),new LinearLayout.LayoutParams(0,dp(58),1));
    root.addView(footer);setContentView(root);
  }
  private void render(){
    if(renderer==null)return;
    try(PdfRenderer.Page p=renderer.openPage(page)){
      int width=Math.min(2500,Math.max(480,(int)(getResources().getDisplayMetrics().widthPixels*zoom)));
      int height=Math.min(3500,Math.max(600,(int)((float)p.getHeight()/p.getWidth()*width)));
      Bitmap b=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);b.eraseColor(Color.WHITE);
      p.render(b,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
      image.setImageBitmap(b);
      if(bitmap!=null&&!bitmap.isRecycled())bitmap.recycle();
      bitmap=b;
      status.setText((page+1)+" / "+renderer.getPageCount());
    }catch(Exception e){Toast.makeText(this,"PDF Page Error: "+e.getMessage(),Toast.LENGTH_LONG).show();}
  }
  @Override protected void onDestroy(){
    if(image!=null)image.setImageDrawable(null);
    if(bitmap!=null&&!bitmap.isRecycled())bitmap.recycle();
    try{if(renderer!=null)renderer.close();}catch(Exception e){}
    try{if(descriptor!=null)descriptor.close();}catch(Exception e){}
    if(tempFile!=null)tempFile.delete();
    super.onDestroy();
  }
}
