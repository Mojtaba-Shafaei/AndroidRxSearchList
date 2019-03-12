package com.mojtaba_shafaei.android.androidRxSearchListSample;

import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Item;

class Job implements Item {

  private String code;
  private String des;
  private String logo;

  Job(String code, String des, String logo) {
    this.code = code;
    this.des = des;
    this.logo = logo;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getDes() {
    return des;
  }

  @Override
  public String getLogo() {
    return logo;
  }
}
