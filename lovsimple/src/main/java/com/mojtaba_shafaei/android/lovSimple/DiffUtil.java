package com.mojtaba_shafaei.android.lovSimple;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil.DiffResult;
import com.mojtaba_shafaei.android.lovSimple.LovSimple.Item;
import io.reactivex.Single;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

class DiffUtil{

static Single<Lce<DiffResult>> getDiff(@Nullable List<Item> old, @Nullable List<Item> news){
  return Single.just(ObjectUtils.defaultIfNull(old, new ArrayList<Item>(0)))
      .map(oldItems -> {
        WeakReference<List<Item>> newItems = new WeakReference<>(ObjectUtils.defaultIfNull(news, new ArrayList<>(0)));

        return android.support.v7.util.DiffUtil.calculateDiff(new android.support.v7.util.DiffUtil.Callback(){
          @Override
          public int getOldListSize(){
            return oldItems.size();
          }

          @Override
          public int getNewListSize(){
            return newItems.get().size();
          }

          @Override
          public boolean areItemsTheSame(int oldItemPosition, int newItemPosition){
            return StringUtils.equals(oldItems.get(oldItemPosition).getCode(), newItems.get().get(newItemPosition).getCode());
          }

          @Override
          public boolean areContentsTheSame(int oldItemPosition, int newItemPosition){
            return oldItems.get(oldItemPosition).equals(newItems.get().get(newItemPosition));
          }

        }, true);
      })
      .map(Lce::data)
      .onErrorReturnItem(Lce.error(new Error("مجددا تلاش نمایید")));
}
}
