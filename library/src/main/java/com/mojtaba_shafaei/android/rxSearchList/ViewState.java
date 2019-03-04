package com.mojtaba_shafaei.android.rxSearchList;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ViewState {

  private boolean loading;
  private CharSequence error;
  private CharSequence message;
  private ViewModel data;

  static ViewState Loading() {
    return new ViewState(true, null, null, null);
  }

  static ViewState Error(CharSequence error) {
    return new ViewState(false, error, null, null);
  }

  static ViewState Message(String message) {
    return new ViewState(false, null, message, null);
  }

  static ViewState Data(ViewModel data) {
    return new ViewState(false, null, null, data);
  }
}
