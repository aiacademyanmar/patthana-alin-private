package com.aaii.dhammalight.preview;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class NativePdfReaderActivity extends Activity {
    private static final int PICK=107;
    private PdfRenderer renderer;
    private ParcelFileDescriptor descriptor;
    private Bitmap bitmap;
    private ImageView image;
    private TextView counter;
    private SeekBar seek;
    private SharedPreferences prefs;
    private int current=0, total=0;
    private boolean picking=false;
    private boolean comicMode=false;
    private String bookId;
    private String bookTitle;
    private float zoom=1.0f;
    private LinearLayout tools;

    @Override public void onCreate(Bundle state){
        super.onCreate(state);
        comicMode=getIntent().getBooleanExtra("comic_mode",false);
        bookId=getIntent().getStringExtra("book_id");
        bookTitle=getIntent().getStringExtra("book_title");
        prefs=getSharedPreferences(comicMode?"comic_reader_v31":(bookId!=null?"ebook_"+bookId:"pdf_reader_preview"),MODE_PRIVATE);
        String bundled=getIntent().getStringExtra("asset_pdf");
        if(bundled!=null){try{openBundled(bundled);return;}catch(Exception ignored){}}
        String saved=prefs.getString("selected_pdf",null);
        if(saved!=null){
            try{open(Uri.parse(saved));return;}catch(Exception ignored){}
        }
        pick();
    }
    private void openBundled(String asset) throws IOException {
       if(!"books/patthana_jjyy.pdf".equals(asset))throw new IOException("Unsupported asset");
       File tmp=new File(getCacheDir(),"offline_patthana_jjyy.pdf");
       if(!tmp.exists()||tmp.length()==0){
          try(InputStream in=getAssets().open(asset);FileOutputStream out=new FileOutputStream(tmp)){
            byte[] block=new byte[65536];int n;while((n=in.read(block))!=-1)out.write(block,0,n);
          }
       }
       open(Uri.fromFile(tmp));
    }
    private int bookIndex(String id){for(int i=0;i<DriveBookCatalogActivity.DRIVE_IDS.length;i++)if(id.equals(DriveBookCatalogActivity.DRIVE_IDS[i]))return i;return -1;}
    private void pick(){
        picking=true;
        Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("application/pdf");
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(i,PICK);
    }
    @Override protected void onActivityResult(int request,int result,Intent data){
        super.onActivityResult(request,result,data);
        if(request!=PICK)return;
        picking=false;
        if(result!=RESULT_OK||data==null||data.getData()==null){finish();return;}
        Uri uri=data.getData();
        try{getContentResolver().takePersistableUriPermission(uri,data.getFlags()&Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(SecurityException ignored){}
        prefs.edit().putString("selected_pdf",uri.toString()).putInt("saved_page",0).apply();
        try{open(uri);}catch(Exception e){Toast.makeText(this,"Cannot open selected PDF",Toast.LENGTH_LONG).show();finish();}
    }
    private void open(Uri uri) throws IOException{
        closePdf();
        descriptor=getContentResolver().openFileDescriptor(uri,"r");
        if(descriptor==null)throw new IOException("PDF not available");
        renderer=new PdfRenderer(descriptor);
        total=renderer.getPageCount();if(total<1)throw new IOException("Empty PDF");
        int requested=getIntent().getIntExtra("lesson_page",-1);
        current=Math.max(0,Math.min(total-1,requested>=0?requested:prefs.getInt("saved_page",0)));
        zoom=1.0f;
        if(bookId!=null)getSharedPreferences("patthana_ebook_catalog",MODE_PRIVATE).edit().putBoolean("linked_"+bookIndex(bookId),true).apply();
        build();draw();
    }
    private Button button(String value,Runnable action){
        Button b=new Button(this);b.setAllCaps(false);b.setText(value);
        b.setOnClickListener(v->action.run());return b;
    }
    private void build(){
        LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setBackgroundColor(Color.rgb(5,28,34));setContentView(root);
        TextView head=new TextView(this);head.setText(comicMode?"☸  Dhamma Light  •  ရုပ်ပြ ပဋ္ဌာန်း":"☸  "+(bookTitle!=null?bookTitle:"Native PDF Reader"));
        head.setTextSize(16);head.setTextColor(Color.rgb(244,203,114));head.setPadding(20,18,10,16);
        root.addView(head);
        image=new ImageView(this);image.setBackgroundColor(Color.WHITE);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);root.addView(image,new LinearLayout.LayoutParams(-1,0,1));
        LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER);
        row.addView(button("◀",()->jump(current-1)),new LinearLayout.LayoutParams(0,62,1));
        counter=new TextView(this);counter.setGravity(Gravity.CENTER);counter.setTextColor(Color.WHITE);
        row.addView(counter,new LinearLayout.LayoutParams(0,62,2));
        row.addView(button("▶",()->jump(current+1)),new LinearLayout.LayoutParams(0,62,1));
        root.addView(row);
        seek=new SeekBar(this);seek.setMax(total-1);seek.setProgress(current);
        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onStartTrackingTouch(SeekBar bar){}
            public void onProgressChanged(SeekBar bar,int p,boolean user){if(user)counter.setText((p+1)+" / "+total);}
            public void onStopTrackingTouch(SeekBar bar){jump(bar.getProgress());}
        });
        root.addView(seek);
        tools=new LinearLayout(this);
        tools.addView(button("★ Save",()->{prefs.edit().putInt("marked_page",current).apply();Toast.makeText(this,"Page bookmarked",Toast.LENGTH_SHORT).show();}),new LinearLayout.LayoutParams(0,65,1));
        tools.addView(button("↪ Bookmark",()->jump(prefs.getInt("marked_page",current))),new LinearLayout.LayoutParams(0,65,1));
        tools.addView(button("Other PDF",this::pick),new LinearLayout.LayoutParams(0,65,1));
        if(comicMode){
          tools.addView(button("− Zoom",()->{zoom=Math.max(0.65f,zoom-0.25f);draw();}),new LinearLayout.LayoutParams(0,65,1));
          tools.addView(button("+ Zoom",()->{zoom=Math.min(3.0f,zoom+0.25f);draw();}),new LinearLayout.LayoutParams(0,65,1));
        }
        root.addView(tools);
        if(comicMode){
          LinearLayout bar=new LinearLayout(this);
          bar.addView(button("✓ ဖတ်ပြီး",()->{
            getSharedPreferences("comic_prototype",MODE_PRIVATE).edit().putBoolean("read_"+Math.min(current,23),true).apply();
            Toast.makeText(this,"Lesson marked read",Toast.LENGTH_SHORT).show();
          }),new LinearLayout.LayoutParams(0,60,1));
          bar.addView(button("★ Bookmark",()->{prefs.edit().putInt("marked_page",current).apply();Toast.makeText(this,"Page bookmarked",Toast.LENGTH_SHORT).show();}),new LinearLayout.LayoutParams(0,60,1));
          root.addView(bar);
        }
    }
    private void jump(int page){
        if(page<0||page>=total)return;current=page;draw();
    }
    private void draw(){
        if(renderer==null)return;
        if(bitmap!=null){image.setImageDrawable(null);bitmap.recycle();bitmap=null;}
        try(PdfRenderer.Page pdf=renderer.openPage(current)){
            float factor=Math.min(3.0f,Math.min(2.0f,1600f/Math.max(pdf.getWidth(),pdf.getHeight()))*zoom);
            final long pixels=(long)(pdf.getWidth()*factor)*(long)(pdf.getHeight()*factor);
            if(pixels>5500000L)factor*=(float)Math.sqrt(5500000.0/(double)pixels);
            int w=Math.max(1,(int)(pdf.getWidth()*factor));
            int h=Math.max(1,(int)(pdf.getHeight()*factor));
            bitmap=Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
            bitmap.eraseColor(Color.WHITE);
            pdf.render(bitmap,null,null,PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            image.setImageBitmap(bitmap);
            image.setScaleType(zoom>1.0f?ImageView.ScaleType.CENTER_CROP:ImageView.ScaleType.FIT_CENTER);
        }catch(Exception e){Toast.makeText(this,"Page render error",Toast.LENGTH_LONG).show();}
        counter.setText((current+1)+" / "+total);
        seek.setProgress(current);
        prefs.edit().putInt("saved_page",current).apply();
        if(comicMode)getSharedPreferences("comic_prototype",MODE_PRIVATE).edit().putInt("last_lesson",Math.min(current,23)).apply();
    }
    private void closePdf(){
        if(image!=null)image.setImageDrawable(null);
        if(bitmap!=null){bitmap.recycle();bitmap=null;}
        if(renderer!=null){try{renderer.close();}catch(Exception ignored){}renderer=null;}
        if(descriptor!=null){try{descriptor.close();}catch(Exception ignored){}descriptor=null;}
    }
    @Override public void onDestroy(){closePdf();super.onDestroy();}
}