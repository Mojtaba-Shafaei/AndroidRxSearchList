package com.mojtaba_shafaei.android.rxSearchList;

import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Item;
import java.util.List;
import lombok.Data;

@Data
class ViewModel {

  private String[] queries;
  private List<? extends Item> data;

  ViewModel(String[] queries, List<? extends Item> data) {
    this.queries = queries;
    this.data = data;
  }
}
