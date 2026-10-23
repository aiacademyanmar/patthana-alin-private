package com.aaii.dhammalight.preview;

import org.json.JSONArray;
import org.json.JSONObject;

/** Source-grounded names and separately-authored Burmese STUDY DRAFTS (not canonical transcription).
 * Full Pali recitation and expert review must be imported independently with provenance.
 * The romanized name order was compared with Paccayaniddesa, Abhidhamma.com,
 * https://www.abhidhamma.com/Patthana_Paccayaniddesa.pdf
 */
public final class PaliContent {
  private PaliContent(){}
  public static final String[] ROMAN={
    "Hetu paccayo","Ārammaṇa paccayo","Adhipati paccayo","Anantara paccayo",
    "Samanantara paccayo","Sahajāta paccayo","Aññamañña paccayo","Nissaya paccayo",
    "Upanissaya paccayo","Purejāta paccayo","Pacchājāta paccayo","Āsevana paccayo",
    "Kamma paccayo","Vipāka paccayo","Āhāra paccayo","Indriya paccayo",
    "Jhāna paccayo","Magga paccayo","Sampayutta paccayo","Vippayutta paccayo",
    "Atthi paccayo","Natthi paccayo","Vigata paccayo","Avigata paccayo"
  };
  public static final String[] REFLECT={
    "ဟိတ်ဟူသော အမြစ်တရား၏ အထောက်အပံ့ကို လေ့လာပါ။",
    "စိတ်က အာရုံတစ်ခုကို သိယူနိုင်ရန် အာရုံ၏ အခန်းကဏ္ဍကို လေ့လာပါ။",
    "အားကောင်းသော ဦးဆောင်တရား၏ အထောက်အပံ့ကို သုံးသပ်ပါ။",
    "ရှေ့စိတ်နှင့် နောက်စိတ် ဆက်နွယ်ပုံကို သုံးသပ်ပါ။",
    "အကြားမရှိ အစဉ်လိုက်ဆက်စပ်မှုကို လေ့လာပါ။",
    "အတူဖြစ်သော စိတ်နှင့်စေတသိက်တို့၏ အထောက်အပံ့ကို လေ့လာပါ။",
    "အပြန်အလှန်မှီခိုသည့် သဘောကို လေ့လာပါ။",
    "မှီရာဖြစ်ခြင်း၏ သဘောကို အကျဉ်းချုပ်ဖတ်ရှုပါ။",
    "အားကြီးသော အမှီပြုမှုအကြောင်း လေ့လာပါ။",
    "ရှေးဦးစွာ ဖြစ်ပေါ်ထားသော တရား၏ အထောက်အပံ့ကို လေ့လာပါ။",
    "နောက်မှဖြစ်ပေါ်သော တရား၏ အထောက်အပံ့ကို လေ့လာပါ။",
    "အဖန်ဖန်ဖြစ်သော ဇောစိတ်၏ အထောက်အပံ့ကို လေ့လာပါ။",
    "စေတနာနှင့် ကမ္မပစ္စည်း၏ သဘောကို လေ့လာပါ။",
    "ဝိပါက်ဖြစ်ပေါ်မှုနှင့် အကျိုးအဖြစ် အထောက်အပံ့ကို လေ့လာပါ။",
    "အာဟာရပစ္စည်းနှင့် ရုပ်နာမ်တို့၏ ဆက်စပ်မှုကို လေ့လာပါ။",
    "ဣန္ဒြိယများ၏ သက်ဆိုင်ရာ ကိစ္စ၌ အစိုးရမှုကို လေ့လာပါ။",
    "ဈာနင်္ဂများ၏ အာရုံ၌ စူးစိုက်ထောက်ပံ့ပုံကို လေ့လာပါ။",
    "မဂ္ဂင်တရားများ၏ လမ်းပြသဖွယ် ထောက်ပံ့ပုံကို လေ့လာပါ။",
    "နာမ်တရားများ ယှဉ်တွဲဖြစ်ပေါ်မှုကို လေ့လာပါ။",
    "မယှဉ်တွဲဘဲ ဆက်စပ်သည့် သဘောကို လေ့လာပါ။",
    "ရှိနေခြင်းဖြင့် ကျေးဇူးပြုပုံကို လေ့လာပါ။",
    "မရှိတော့ခြင်းဖြင့် ကျေးဇူးပြုပုံကို လေ့လာပါ။",
    "ကွယ်ပျောက်ခြင်းဖြင့် ကျေးဇူးပြုပုံကို လေ့လာပါ။",
    "မကွယ်ပျောက်ခြင်းဖြင့် ကျေးဇူးပြုပုံကို လေ့လာပါ။"
  };
  public static JSONObject lessonDraft(int index) {
    int id=Math.max(0,Math.min(23,index));
    JSONObject item=new JSONObject();JSONArray lines=new JSONArray();
    try {
      item.put("lesson_id",id+1);
      item.put("editorial_status","draft");
      item.put("provenance","Draft learning overviews; not an exact transcription of scanned Myat Patthana");
      item.put("reviewed_by","");
      item.put("source_page",JSONObject.NULL);
      JSONObject a=new JSONObject();
      a.put("pali",ROMAN[id]);
      a.put("myanmar","ပစ္စည်းအမည် — "+ComicPrototypeActivity.lessonName(id));
      a.put("start_ms",JSONObject.NULL);a.put("end_ms",JSONObject.NULL);
      lines.put(a);
      JSONObject b=new JSONObject();
      b.put("pali","");
      b.put("myanmar","အဓိပ္ပာယ်အကျဉ်း — "+ComicPrototypeActivity.lessonMeaning(id)+"။");
      b.put("start_ms",JSONObject.NULL);b.put("end_ms",JSONObject.NULL);
      lines.put(b);
      JSONObject c=new JSONObject();
      c.put("pali","");
      c.put("myanmar","လေ့လာရန် — "+REFLECT[id]);
      c.put("start_ms",JSONObject.NULL);c.put("end_ms",JSONObject.NULL);
      lines.put(c);
      item.put("segments",lines);
    } catch (Exception ignored) {}
    return item;
  }
}
