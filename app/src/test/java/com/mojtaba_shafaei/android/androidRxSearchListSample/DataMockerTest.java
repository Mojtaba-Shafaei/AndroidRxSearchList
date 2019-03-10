package com.mojtaba_shafaei.android.androidRxSearchListSample;

import static com.mojtaba_shafaei.android.androidRxSearchListSample.DataMocker.ENTER_QUERY_MESSAGE;

import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Lce;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;

public class DataMockerTest {

  @org.junit.Test
  public void test_getList_returnMessageToInsertQueryOnEmptyQuery() {

    final Lce dataMockerTest = DataMocker.getList("")
        .blockingSingle();

    Assert.assertThat(dataMockerTest.getMessage(), IsEqual.equalTo(ENTER_QUERY_MESSAGE));

  }
}