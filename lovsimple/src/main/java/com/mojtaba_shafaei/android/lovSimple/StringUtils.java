package com.mojtaba_shafaei.android.lovSimple;

class StringUtils extends org.apache.commons.lang3.StringUtils{

static String defaultIfBlank(CharSequence text){
  if(isBlank(text)){
    return StringUtils.EMPTY;
  }
  return text.toString();
}


}
