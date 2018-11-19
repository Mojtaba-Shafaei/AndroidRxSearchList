package com.mojtaba_shafaei.android.lovSimple;

import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

class GlideUtil{

static final Object lock = new Object();
static GlideUtil instance = null;

private RequestOptions mOptions = null;

private GlideUtil(){
}

static GlideUtil getInstance(){
  if(instance == null){
    synchronized(lock){
      if(instance == null){
        instance = new GlideUtil();
      }
    }
  }
  return instance;
}

RequestOptions getOptions(Drawable placeHolder){
  if(mOptions == null){
    mOptions = new RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .skipMemoryCache(true)
        .centerInside()
        .encodeFormat(CompressFormat.JPEG)
        .error(R.drawable.lov_simple_ic_error_outline_red_200_24dp)
        .placeholder(placeHolder)
        .format(DecodeFormat.PREFER_RGB_565)
    ;
  }

  return mOptions;
}
}
