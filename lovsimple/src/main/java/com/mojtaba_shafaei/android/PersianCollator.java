package com.mojtaba_shafaei.android;

import java.text.Collator;
import java.text.ParseException;
import java.text.RuleBasedCollator;
import java.util.Locale;

class PersianCollator{

/**
 * This string is persian collation rules which is in fact a modification to existing arabic collation rules. Features of these rules are: 1. vav
 * (u0648) is put before heh (u0647) 2. persian yeh (u06cc) is sorted before arabic yeh (0649 and 064a) 3. persian kaf (u06a9) is sorted before arabic
 * kaf 4. persian numerics (06f0..06f9) are sorted after english and before arabic 5. Rial sign (ufdfc) is put in list of currency symbols (see
 * java.text.CollationRules.java) and before asterick 6. hamze on vav is sorted right before vav 7. hamze on yeh is sorted right before yeh
 */
static final String FA_RULES =
    "&\u0646<\u0624;\u0648<\u0647<\u0626=\u06cc<\u0649"// noon<hamze on vav<vav<heh<hamze on yeh<yeh
// + "&\u0649=\u064a=\u06cc" // all yeh chars are equal
        + "&\u0643<\u06a9" // all kaf chars are equal; swash kaf is ignored
        // priority of persian digits is higher than arabic digits
        + "&\u0030<\u06f0<\u0660"
        + "&\u0031<\u06f1<\u0661"
        + "&\u0032<\u06f2<\u0662"
        + "&\u0033<\u06f3<\u0663"
        + "&\u0034<\u06f4<\u0664"
        + "&\u0035<\u06f5<\u0665"
        + "&\u0036<\u06f6<\u0666"
        + "&\u0037<\u06f7<\u0667"
        + "&\u0038<\u06f8<\u0668"
        + "&\u0039<\u06f9<\u0669"
        // put rial sign between yen and asterik. (according to java.text.CollationRules, yen is the last currency
        // sign and is right before asterick. So, we are inserting rial sign as the last currency sign
        + "&\u00a5<\ufdfc<'\u002a'";

public static final Locale ARABIC = new Locale("ar", "");

/**
 * Creates a persian collator base on system-arabic collator
 */
private static Collator persianCollator(){
  RuleBasedCollator ar = (RuleBasedCollator) Collator.getInstance(ARABIC);
  try{
    return new RuleBasedCollator(ar.getRules() + FA_RULES);
  } catch(ParseException e){
    throw new RuntimeException(e);
  }
}

private PersianCollator(){
}

/**
 * The arabic collator which persian collator is built on top of it
 */
static Collator ar = Collator.getInstance(ARABIC);
/**
 * Persian collator instance
 */
static Collator fa = persianCollator();

/**
 * @return persian collator instance
 */
public static Collator getPersianInstance(){
  return fa;
}

/**
 * test method
 */
/*public static void main(String[] args){
  fa.setStrength(Collator.PRIMARY);
  System.out.println("check for sort order of vav and heh:");
  compare("\u0648", "\u0647"); //1, -1
  System.out.println("check for farsi kaf proper location:");
  compare("\u0644", "\u06a9");//-1, 1
  System.out.println("check for farsi yeh proper location:");
  compare("\u064b", "\u06cc");
  System.out.println("check for equivalence of all yeh chars:");
  compare("\u0649", "\u064a"); //0, 0
  System.out.println("check for order of vav and hamze on vav");
  compare("\u0624", "\u0625");
  compare("\u0646", "\u0624");// ar: hamze < noon, fa: noon<hamze
  compare("\u0648", "\u0624");// ar: hamze < vav, fa: hamze;vav
  System.out.println("check for order of yeh and hamze on yeh");
  compare("\u0626", "\u0627");// ar: hamze<alef; fa: alef<hamze
  compare("\u06cc", "\u0626");
  System.exit(0);
}*/

// compare the two given strings using both arabic collator rules and persian collator rules
// this is only for test purpose
private static void compare(String a, String b){
  int ar_result = ar.compare(a, b);
  System.out.println("Arabic Rule: " + a + relation(ar_result) + b + " by " + ar_result);
  int fa_result = fa.compare(a, b);
  System.out.println("Persian Rule: " + a + relation(fa_result) + b + " by " + fa_result);
}

/**
 * test method
 */
private static String relation(int result){
  if(result > 0){
    return ">";
  }
  if(result < 0){
    return "<";
  }
  return "=";
}
}
