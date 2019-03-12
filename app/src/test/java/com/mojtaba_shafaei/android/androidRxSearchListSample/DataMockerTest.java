package com.mojtaba_shafaei.android.androidRxSearchListSample;

import static com.mojtaba_shafaei.android.androidRxSearchListSample.DataMocker.ENTER_QUERY_MESSAGE;
import static com.mojtaba_shafaei.android.androidRxSearchListSample.DataMocker.ERROR_HAPPENED;

import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Lce;
import io.reactivex.observers.TestObserver;
import org.hamcrest.core.IsEqual;
import org.junit.Assert;

public class DataMockerTest {

  @org.junit.Test
  public void test_getList_returnMessageToInsertQueryOnEmptyQuery() {

    final Lce dataMockerTest = DataMocker.getList("")
        .blockingSingle();

    Assert.assertThat(dataMockerTest.getMessage(), IsEqual.equalTo(ENTER_QUERY_MESSAGE));

  }

  @org.junit.Test
  public void test_getList_returnJobCode10PlusADummyItemAtFirst() {
    TestObserver<Lce> testObserver = new TestObserver<>();
    DataMocker.getList("10")
        .subscribe(testObserver);
    testObserver.awaitTerminalEvent();

    testObserver.assertValueCount(2);
    testObserver.assertValueAt(0, Lce::isLoading);
    testObserver.assertValueAt(1, lce ->
        lce.getData() != null && lce.getData().size() == 2 &&
            (lce.getData().get(0).getCode() == null && lce.getData().get(0).getDes().equals("10") && lce.getData().get(0).getLogo()==null )
            && lce.getData().get(1).getCode().equals("10"));
  }

  @org.junit.Test
  public void test_returnErrorWhenQueryIsEqualToError() {
    TestObserver<Lce> testObserver = new TestObserver<>();
    DataMocker.getList("error")
        .subscribe(testObserver);
    testObserver.awaitTerminalEvent();

    testObserver.assertValueCount(2);
    testObserver.assertValueAt(0, Lce::isLoading);
    testObserver.assertValueAt(1, lce ->
        lce.hasError() && lce.getError().equals(ERROR_HAPPENED));
  }
}