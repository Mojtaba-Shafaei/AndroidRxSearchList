package com.mojtaba_shafaei.android.lovSimpleSample;

import android.os.SystemClock;
import com.mojtaba_shafaei.android.lovSimple.LovSimple;
import com.mojtaba_shafaei.android.lovSimple.LovSimple.Item;
import com.mojtaba_shafaei.android.lovSimple.LovSimple.Lce;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

class DataMocker{

private final String TAG = "DataMocker";

static List<LovSimple.Item> items = new ArrayList<>();

static{
  items.add(new Job("1", "شغل یک از یک", 1));
  items.add(new Job("2", "آزاد شغل ۲", 1));
  items.add(new Job("3", "برنامه نویس", 1));
  items.add(new Job("4", "پزشک", 1));
  items.add(new Job("5", "fifth job with 1 code", 1));
  items.add(new Job("6", "sixth job with 1 code", 1));
  items.add(new Job("7", "seventh job with 1 code", 1));
  items.add(new Job("8", "eighth job with 1 code", 1));
  items.add(new Job("9", "9th job with 1 code", 1));
  items.add(new Job("10", "10th job with 1 code", 1));
  items.add(new Job("11", "11th job with parent code 10", 1));
  items.add(new Job("12", "12th job with parent code 10", 1));
  items.add(new Job("13", "13th job with parent code 10", 1));
  items.add(new Job("14", "14th job with 1 code", 1));
  items.add(new Job("15", "15th job with 1 code", 1));
  items.add(new Job("16", "16th job with 1 code", 1));
  items.add(new Job("17", "17th job with 1 code", 1));
  items.add(new Job("18", "18th job with 1 code", 1));
  items.add(new Job("19", "19th job with 1 code", 1));
  items.add(new Job("20", "20th job with 1 code", 1));
  items.add(new Job("21", "21th job with 1 code", 1));
  items.add(new Job("22", "22th job with 1 code", 1));
  items.add(new Job("23", "google", 1));
  items.add(new Job("24", "gozilla", 1));
  items.add(new Job("25", "guava", 1));
}

static io.reactivex.Observable<Lce<List<Item>>> getError(){
  return io.reactivex.Observable.just(Lce.error(new Error("Error Happened")));
}

static io.reactivex.Observable<Lce<List<Item>>> getList(String query){
  if(query.contentEquals("error")){ // ;) just for testing
    return getError();
  }

  return Observable.fromIterable(items)
      .subscribeOn(Schedulers.io())
      .filter(item -> item.getDes().contains(query))
      .toList()
      .toObservable()
      .map(data -> Lce.data(query, data))
      .map(t -> {
        SystemClock.sleep(2000);
        return t;
      })
      .startWith(Lce.loading())
      ;
}

static io.reactivex.Observable<Lce<List<Item>>> getList(){
  return Observable.fromIterable(items)
      .toList()
      .toObservable()
      .map(t -> Lce.data("", t))
      .delay(3, TimeUnit.SECONDS, Schedulers.computation())//make delay similar as online APIs.
      .startWith(Lce.loading());

}
}
