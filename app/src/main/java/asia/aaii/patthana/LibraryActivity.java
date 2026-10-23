package asia.aaii.patthana;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.text.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class LibraryActivity extends Activity {
  private final int NAVY=Color.rgb(7,32,51), GOLD=Color.rgb(204,155,68), PAPER=Color.rgb(253,250,242), MUTED=Color.rgb(95,111,115);
  private static final int PDF_PICKER=7401;
  private JSONArray books=new JSONArray(),writers=new JSONArray(),cats=new JSONArray();
  private LinearLayout content;
  private EditText query;
  private String mode="writers", folderId="", category="", search="";
  private JSONObject chosenBook=null;
  private int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
  @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(NAVY);load();layout();}
  private void load(){
    try (InputStream in=getAssets().open("dhamma_books_catalog.json")) {
      ByteArrayOutputStream out=new ByteArrayOutputStream();
      byte[] buf=new byte[8192];int n;
      while((n=in.read(buf))!=-1)out.write(buf,0,n);
      JSONObject j=new JSONObject(out.toString("UTF-8"));
      books=j.getJSONArray("books");writers=j.getJSONArray("writers");cats=j.getJSONArray("categories");
    }catch(Exception e){new AlertDialog.Builder(this).setMessage("စာအုပ်စာရင်း မဖတ်နိုင်ပါ — "+e.getMessage()).setPositiveButton("OK",null).show();}
  }
  private GradientDrawable bg(int color){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(16));return d;}
  private TextView text(String s,int size,boolean bold,int c){
    TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(c);
    if(bold)v.setTypeface(null,Typeface.BOLD);v.setPadding(dp(4),dp(8),dp(4),dp(8));return v;
  }
  private void addText(String s,int size,boolean bold,int color){content.addView(text(s,size,bold,color));}
  private void item(String title,String subtitle,Runnable click){
    LinearLayout card=new LinearLayout(this);card.setOrientation(1);
    card.setPadding(dp(15),dp(11),dp(15),dp(11));card.setBackground(bg(Color.rgb(238,233,222)));
    TextView t=text(title,16,true,NAVY);card.addView(t);
    if(subtitle!=null&&!subtitle.isEmpty())card.addView(text(subtitle,12,false,MUTED));
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(10);content.addView(card,p);
    card.setOnClickListener(v->click.run());
  }
  private void layout(){
    LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(PAPER);
    LinearLayout head=new LinearLayout(this);head.setOrientation(1);head.setBackgroundColor(NAVY);head.setPadding(dp(20),dp(12),dp(20),dp(14));
    TextView title=text("‹  ဓမ္မစာအုပ် eLibrary",22,true,Color.WHITE);head.addView(title);title.setOnClickListener(v->finish());
    head.addView(text("239 Writer/Folder • 144 Indexed PDF • 9 Categories",12,false,GOLD));
    root.addView(head);
    LinearLayout tabs=new LinearLayout(this);tabs.setPadding(dp(8),dp(8),dp(8),dp(8));
    tab(tabs,"Writer", "writers");tab(tabs,"Category","categories");tab(tabs,"Books","books");
    root.addView(tabs);
    query=new EditText(this);query.setSingleLine(true);query.setTextSize(15);query.setHint("စာရေးသူ၊ Category၊ စာအုပ်အမည် ရှာရန်");
    query.setPadding(dp(18),0,dp(15),0);query.setBackground(bg(Color.rgb(238,233,222)));
    LinearLayout.LayoutParams qp=new LinearLayout.LayoutParams(-1,dp(50));qp.setMargins(dp(15),0,dp(15),dp(5));root.addView(query,qp);
    ScrollView scroll=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(1);content.setPadding(dp(16),dp(14),dp(16),dp(16));scroll.addView(content);
    root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);
    query.addTextChangedListener(new TextWatcher(){
      public void beforeTextChanged(CharSequence s,int a,int c,int f){}
      public void onTextChanged(CharSequence s,int a,int before,int count){search=s.toString().trim().toLowerCase(Locale.ROOT);render();}
      public void afterTextChanged(Editable e){}
    });render();
  }
  private void tab(LinearLayout row,String label,String value){
    TextView v=text(label,14,true,NAVY);v.setGravity(Gravity.CENTER);v.setBackground(bg(Color.rgb(238,233,222)));
    LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(45),1);p.setMargins(dp(3),0,dp(3),0);row.addView(v,p);
    v.setOnClickListener(view->{mode=value;folderId="";category="";search="";query.setText("");render();});
  }
  private boolean has(String value){return search.isEmpty()||value.toLowerCase(Locale.ROOT).contains(search);}
  private void render(){
    if(content==null)return;
    content.removeAllViews();
    if(mode.equals("writers"))showWriters();
    else if(mode.equals("categories"))showCategories();
    else if(mode.equals("folder")||mode.equals("category-books")||mode.equals("books"))showBooks();
    else if(mode.equals("detail"))showDetail();
  }
  private void showWriters(){
    addText("စာရေးသူ / Folder အမည်စာရင်း",21,true,NAVY);
    addText("Google Drive မှ Folder အမည် ၂၃၉ ခု။ အမည်များကို တကယ့်စာရေးသူအဖြစ် မစစ်ဆေးရသေးပါ။",13,false,MUTED);
    int count=0;
    for(int i=0;i<writers.length();i++){
      JSONObject x=writers.optJSONObject(i);if(x==null)continue;
      String name=x.optString("name");if(!has(name+" "+x.optString("category")))continue;count++;
      item(name,x.optString("category")+" · Indexed "+x.optInt("indexed_books",0)+" PDF",()->{folderId=x.optString("id");mode="folder";search="";query.setText("");render();});
    }
    if(count==0)addText("မတွေ့ပါ",16,false,MUTED);
  }
  private void showCategories(){
    addText("စာအုပ် အမျိုးအစားများ",21,true,NAVY);
    addText("Category သည် Folder/စာရင်းမှ ခန့်မှန်းခွဲခြားထားခြင်းဖြစ်သည်။",13,false,MUTED);
    for(int i=0;i<cats.length();i++){
      String c=cats.optString(i);if(!has(c))continue;
      int n=0;for(int k=0;k<books.length();k++)if(c.equals(books.optJSONObject(k).optString("category")))n++;
      final String selected=c;item(c,n+" စာအုပ် indexed",()->{category=selected;mode="category-books";search="";query.setText("");render();});
    }
  }
  private void showBooks(){
    final boolean inFolder=mode.equals("folder"),inCategory=mode.equals("category-books");
    JSONObject w=findWriter(folderId);String folderName=w==null?"":w.optString("name");
    addText(inFolder?folderName:inCategory?category:"စာအုပ် ၁၄၄ အုပ်",21,true,NAVY);
    if(inFolder&&w!=null){
      item("Google Drive Folder ကိုဖွင့်ရန်","မထည့်ရသေးသော စာအုပ်များကို Drive မှာ ကြည့်နိုင်သည်",()->openUrl(w.optString("url")));
    }
    int count=0;
    for(int i=0;i<books.length();i++){
      JSONObject x=books.optJSONObject(i);if(x==null)continue;
      if(inFolder&&!folderId.equals(x.optString("folder_id")))continue;
      if(inCategory&&!category.equals(x.optString("category")))continue;
      if(!has(x.optString("title")+" "+x.optString("writer")+" "+x.optString("category")))continue;
      count++;
      item(x.optString("title"),x.optString("writer")+" • "+x.optString("category"),()->{chosenBook=x;mode="detail";render();});
    }
    if(count==0)addText("ဒီ Folder မှာ Index လုပ်ထားသော PDF မရှိသေးပါ။ Drive Folder မှ တိုက်ရိုက်ကြည့်နိုင်ပါတယ်။",15,false,MUTED);
    if(inFolder||inCategory)item("← စာရင်းသို့ ပြန်ရန်","",()->{mode=inFolder?"writers":"categories";render();});
  }
  private JSONObject findWriter(String id){
    for(int i=0;i<writers.length();i++){JSONObject x=writers.optJSONObject(i);if(x!=null&&id.equals(x.optString("id")))return x;}return null;
  }
  private void showDetail(){
    if(chosenBook==null){mode="books";render();return;}
    JSONObject x=chosenBook;
    addText(x.optString("title"),22,true,NAVY);
    addText("Writer / Folder: "+x.optString("writer"),15,true,NAVY);
    addText("Category: "+x.optString("category"),15,false,MUTED);
    item("📖  Native PDF Reader ဖြင့်ဖတ်ရန်","Google Drive PDF ကို တိုက်ရိုက်ရယူစမ်းမည်။ Permission ရှိမှ ရမည်။ မရပါက Drive မှ PDF ကို ရွေးနိုင်သည်။",()->downloadForReader(x));
    item("📂  ဖုန်း/Google Drive မှ PDF ရွေးဖတ်ရန်","Google Drive App ရှိလျှင် အကောင့်ဖြင့် Drive PDF ရွေးနိုင်သည်",this::selectPdf);
    item("🔗  Google Drive မှာဖတ်ရန်","မူရင်း Google Drive Viewer",()->openUrl(x.optString("view_url")));
    item("← စာအုပ်စာရင်း", "",()->{mode="books";render();});
  }
  private void downloadForReader(JSONObject x){
    String id=x.optString("id","");
    if(!id.matches("[A-Za-z0-9_-]{15,}")){selectPdf();return;}
    File cached=new File(getCacheDir(),"dhamma_"+id+".pdf");
    if(cached.isFile()&&cached.length()>5){openPdfFile(cached);return;}
    ProgressDialog progress=new ProgressDialog(this);progress.setMessage("Native Reader အတွက် PDF ရယူနေသည်…");progress.setIndeterminate(true);progress.setCancelable(false);progress.show();
    new Thread(()->{
      File tmp=new File(getCacheDir(),"tmp_"+id+".pdf");String error="";
      try {
        URL u=new URL("https://drive.google.com/uc?export=download&id="+id);
        HttpURLConnection c=(HttpURLConnection)u.openConnection();c.setInstanceFollowRedirects(true);c.setConnectTimeout(15000);c.setReadTimeout(25000);
        c.setRequestProperty("User-Agent","Mozilla/5.0");c.connect();
        if(c.getResponseCode()!=200)throw new IOException("HTTP "+c.getResponseCode());
        try(InputStream in=new BufferedInputStream(c.getInputStream());FileOutputStream out=new FileOutputStream(tmp)){
          byte[] header=new byte[5];int h=0;while(h<5){int n=in.read(header,h,5-h);if(n<0)break;h+=n;}
          if(h!=5||!new String(header,"US-ASCII").equals("%PDF-"))throw new IOException("PDF အစား Google Drive Login/HTML ပြန်လာသည်");
          out.write(header);byte[] buffer=new byte[32768];int n;long sum=5;
          while((n=in.read(buffer))!=-1){sum+=n;if(sum>100L*1024*1024)throw new IOException("စာအုပ်အရွယ် 100MB ကျော်သည်");out.write(buffer,0,n);}
        }
        c.disconnect();if(!tmp.renameTo(cached))throw new IOException("PDF သိမ်းမရပါ");
      }catch(Exception e){error=e.getMessage()==null?"PDF ကို ဒီအကောင့်ဖြင့် တိုက်ရိုက်ရယူမရပါ":e.getMessage();tmp.delete();}
      final String result=error;
      runOnUiThread(()->{progress.dismiss();if(result.isEmpty())openPdfFile(cached);
        else new AlertDialog.Builder(this).setTitle("Drive Permission လိုအပ်ပါသည်")
          .setMessage("Direct PDF မရပါ။ "+result+"\n\nGoogle Drive App မှ မိမိအကောင့်နဲ့ PDF ကိုရွေးဖတ်နိုင်ပါတယ်။")
          .setPositiveButton("Drive မှ PDF ရွေးမည်",(d,w)->selectPdf())
          .setNeutralButton("Online View",(d,w)->openUrl(x.optString("view_url")))
          .setNegativeButton("ပိတ်မည်",null).show();});
    }).start();
  }
  private void selectPdf(){
    Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.addCategory(Intent.CATEGORY_OPENABLE);i.setType("application/pdf");
    i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
    try{startActivityForResult(i,PDF_PICKER);}catch(Exception e){Toast.makeText(this,"PDF picker မဖွင့်နိုင်ပါ",Toast.LENGTH_LONG).show();}
  }
  @Override protected void onActivityResult(int request,int code,Intent data){
    super.onActivityResult(request,code,data);
    if(request==PDF_PICKER&&code==RESULT_OK&&data!=null&&data.getData()!=null){
      Uri uri=data.getData();
      try{getContentResolver().takePersistableUriPermission(uri,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignore){}
      Intent i=new Intent(this,PdfReaderActivity.class);i.setData(uri);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(i);
    }
  }
  private void openPdfFile(File f){Intent i=new Intent(this,PdfReaderActivity.class);i.putExtra("local_pdf_path",f.getAbsolutePath());startActivity(i);}
  private void openUrl(String link){
    if(!link.startsWith("https://")){Toast.makeText(this,"Link မရှိပါ",Toast.LENGTH_SHORT).show();return;}
    try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(link)).addCategory(Intent.CATEGORY_BROWSABLE));}
    catch(Exception e){Toast.makeText(this,"Google Drive App/Browser မဖွင့်နိုင်ပါ",Toast.LENGTH_LONG).show();}
  }
  @Override public void onBackPressed(){
    if(mode.equals("detail"))mode="books";else if(mode.equals("folder"))mode="writers";else if(mode.equals("category-books"))mode="categories";else{super.onBackPressed();return;}render();
  }
}
