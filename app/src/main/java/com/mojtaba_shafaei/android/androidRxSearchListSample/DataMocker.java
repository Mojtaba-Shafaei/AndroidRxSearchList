package com.mojtaba_shafaei.android.androidRxSearchListSample;

import android.os.SystemClock;
import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Lce;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;

class DataMocker {

  final static String ENTER_QUERY_MESSAGE = "Please Query";
  public static final CharSequence ERROR_HAPPENED = "ERROR HAPPENED!!!";
  static List<Job> items = new ArrayList<>();

  static {
    for (int i = 0; i < 50; i++) {
      items.add(new Job(String.valueOf(i), "the job with code = " + i
          , "https://picsum.photos/300/300?image=" + i));
    }
  }

  static io.reactivex.Observable<Lce> getList(String query) {
    if (query == null || query.isEmpty()) {
      return Observable.just(Lce.message(ENTER_QUERY_MESSAGE));
    }

    return Observable.fromIterable(items)
        .subscribeOn(Schedulers.io())
        .filter(item -> item.getDes().contains(query))
        .toList()
        .toObservable()
        .map(data -> {
          // add a new dummy record of inserted query by User.
          data.add(0, new Job(null, query, null));
          return data;
        })
        .map(data -> Lce.data(query, data))
        .map(t -> {
          SystemClock.sleep(500);
          if (query.contentEquals("error")) {// to test rapidly error UI, write "error" as query
            return (Lce.error(ERROR_HAPPENED));
          }
          return (t);
        })
        .startWith(Lce.loading())
        ;
  }
}
