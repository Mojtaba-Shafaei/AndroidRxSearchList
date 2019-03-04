package com.mojtaba_shafaei.android.androidRxSearchListSample;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication {

  @Override
  public void onCreate() {
    super.onCreate();
    MultiDex.install(this);
  }
}
