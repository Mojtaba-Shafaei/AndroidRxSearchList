package com.mojtaba_shafaei.android.lovSimpleSample;

import android.util.Log;

import com.mojtaba_shafaei.android.Lce;
import com.mojtaba_shafaei.android.LovSimple;

import java.util.ArrayList;
import java.util.List;

public class SimpleFetcherBl implements LovSimple.FetchDataListener {
    private static final String TAG = "SimpleFetcherBl";


    @Override
    public io.reactivex.Observable<Lce<List<LovSimple.Item>>> fetch(String query) {
        Log.d(TAG, "query = " + query);
        List<LovSimple.Item> items = new ArrayList<>();
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

//        List<LovSimple.Item> result =  new ArrayList<>();

        return io.reactivex.Observable.just(Lce.data(items));
    }
}
