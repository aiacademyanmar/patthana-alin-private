package asia.aaii.patthanareader;

import android.app.*;
import android.content.*;
import android.graphics.*;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.*;
import android.text.*;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.io.*;
import java.util.*;

public class BookLibraryActivity extends Activity {
  private static final int PICK=4001;
  private JSONArray books=new JSONArray();
  private LinearLayout content;
  private EditText search;
  private JSONObject selected;
  private String keyword="";
  private boolean categories=false;
  private final int navy=Color.rgb(9,40,57);
  private int dp(int n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
  @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(navy);try(InputStream in=getAssets().open("patthana_research_books.json")){ByteArrayOutputStream buf=new ByteArrayOutputStream();byte[] a=new byte[8192];int n;while((n=in.read(a))!=-1)buf.write(a,0,n);books=new JSONObject(buf.toString("UTF-8")).getJSONArray("books");}catch(Exception e){Toast.makeText(this,"စာအုပ်စာရင်းဖတ်မရပါ",Toast.LENGTH_LONG).show();}draw();}
  private TextView text(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(navy);t.setPadding(dp(12),dp(12),dp(12),dp(12));return t;}
  private void entry(String s,String details,Runnable action){LinearLayout box=new LinearLayout(this);box.setOrientation(1);TextView title=text(s,16);title.setTypeface(null,1);box.addView(title);if(details!=null)box.addView(text(details,12));box.setBackgroundColor(Color.rgb(235,243,239));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(0,0,0,dp(9));content.addView(box,p);box.setOnClickListener(v->action.run());}
  private void draw(){LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setBackgroundColor(Color.rgb(250,249,244));TextView h=text("☸  ပဋ္ဌာန်း စာကြည့်တိုက် — V4.1",21);h.setTypeface(null,1);root.addView(h);LinearLayout nav=new LinearLayout(this);Button back=new Button(this);back.setText("Back");back.setOnClickListener(v->finish());nav.addView(back);Button all=new Button(this);all.setText("Books");all.setOnClickListener(v->{selected=null;categories=false;list();});nav.addView(all);Button cats=new Button(this);cats.setText("Category");cats.setOnClickListener(v->{selected=null;categories=true;list();});nav.addView(cats);root.addView(nav);search=new EditText(this);search.setSingleLine(true);search.setHint("စာအုပ် / Category / Zawgyi ရှာဖွေရန်");root.addView(search);ScrollView sc=new ScrollView(this);content=new LinearLayout(this);content.setOrientation(1);content.setPadding(dp(12),dp(12),dp(12),dp(20));sc.addView(content);root.addView(sc,new LinearLayout.LayoutParams(-1,0,1));setContentView(root);search.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int st,int count,int after){}public void afterTextChanged(Editable e){}public void onTextChanged(CharSequence s,int start,int before,int count){keyword=s.toString().trim().toLowerCase(Locale.ROOT);list();}});list();}
  private void list(){if(content==null)return;content.removeAllViews();if(selected!=null){details();return;}if(categories){LinkedHashSet<String> cat=new LinkedHashSet<>();for(int i=0;i<books.length();i++)cat.add(books.optJSONObject(i).optString("category"));for(String c:cat)if(keyword.isEmpty()||c.toLowerCase(Locale.ROOT).contains(keyword))entry(c,"Category အလိုက် စာအုပ်",()->{categories=false;keyword=c.toLowerCase(Locale.ROOT);search.setText(c);});return;}int ct=0;for(int i=0;i<books.length();i++){JSONObject b=books.optJSONObject(i);if(b==null)continue;String words=b.optString("title")+" "+b.optString("original")+" "+b.optString("category");if(!words.toLowerCase(Locale.ROOT).contains(keyword))continue;ct++;entry(b.optString("title"),b.optInt("pages")+" pages • "+b.optString("category")+(b.isNull("duplicate_of")?"":" • Duplicate"),()->{selected=b;details();});}if(ct==0)content.addView(text("မတွေ့ပါ",15));}
  private void details(){content.removeAllViews();JSONObject b=selected;if(b==null){list();return;}content.addView(text(b.optString("title"),23));content.addView(text("မူရင်း: "+b.optString("original"),14));content.addView(text("စာမျက်နှာ "+b.optInt("pages")+" • "+b.optString("category"),15));entry("📖 Native PDF Reader ဖွင့်ရန်","ဖုန်း / Google Drive App မှ PDF ရွေးချယ်ဖတ်ပါ",this::pick);entry("☁ Google Drive မူရင်း PDF ဖွင့်ရန်","Permission ရှိမှ ဖတ်နိုင်ပါသည်",()->{try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(b.optString("source_url"))));}catch(Exception e){Toast.makeText(this,"Google Drive ဖွင့်မရပါ",Toast.LENGTH_LONG).show();}});entry("← စာအုပ်စာရင်း","",()->{selected=null;list();});content.addView(text("Google Drive စာအုပ်များကို APK အတွင်း ပြန်လည်ဖြန့်ဝေထားခြင်း မရှိပါ။ Native ဖတ်ရန် Google Drive မှ ဖိုင်ကို ရွေးပါ။",12));}
  private void pick(){Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("application/pdf");i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION|Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);startActivityForResult(i,PICK);}
  @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(request==PICK&&result==RESULT_OK&&data!=null&&data.getData()!=null){Uri u=data.getData();try{getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION);}catch(Exception ignored){}Intent i=new Intent(this,NativePdfActivity.class);i.setData(u);i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);startActivity(i);}}
}
