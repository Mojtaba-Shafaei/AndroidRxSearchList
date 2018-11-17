package com.mojtaba_shafaei.android.lovSimpleSample;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

public class MyApplication extends MultiDexApplication{

@Override
public void onCreate(){
  super.onCreate();
  MultiDex.install(this);
}
}
