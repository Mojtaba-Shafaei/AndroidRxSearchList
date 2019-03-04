package com.mojtaba_shafaei.android.rxSearchList;

import android.graphics.Bitmap.CompressFormat;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.mojtaba_shafaei.android.lovSimple.R;
import java.util.Calendar;

final class GlideHelper {

  private static GlideHelper instance = null;
  private final static Object lock = new Object();

  private RequestOptions requestOptions;

  private GlideHelper() {
    MediaStoreSignature mediaStoreSignature = new MediaStoreSignature("WEBP"
        , Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
        , 0);

    requestOptions = new RequestOptions()
        .skipMemoryCache(true)
        .error(R.drawable.rx_search_list_ic_error_outline_red_a100_24dp)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .format(DecodeFormat.PREFER_RGB_565)
        .encodeFormat(CompressFormat.WEBP)
        .centerInside()
        .signature(mediaStoreSignature)
        .transforms(new CircleCrop());
  }

  static GlideHelper getInstance() {
    if (instance == null) {
      synchronized (lock) {
        if (instance == null) {
          instance = new GlideHelper();
        }
      }
    }
    return instance;
  }

  RequestOptions getRequestOptions() {
    return requestOptions;
  }
}
