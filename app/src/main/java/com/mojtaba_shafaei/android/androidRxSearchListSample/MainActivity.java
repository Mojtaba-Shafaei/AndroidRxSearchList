package com.mojtaba_shafaei.android.androidRxSearchListSample;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.jakewharton.rxbinding2.view.RxView;
import com.mojtaba_shafaei.android.rxSearchList.RxSearchList;
import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Item;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

  private String TAG = "MainActivity";
  private TextView tvResult;
  private TextView tvCancelled;
  private TextView tvDismissed;

  private final CompositeDisposable mDisposables = new CompositeDisposable();
  private View btnShowList;
  private Switch swShowLogo;

  private RxSearchList rxSearchList;
  ////////////////////////////      /////////////////////////////////////

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    View rootView = findViewById(R.id.root);
    btnShowList = findViewById(R.id.button);
    swShowLogo = findViewById(R.id.sw_showLogo);

    tvResult = findViewById(R.id.tvResult);
    tvCancelled = findViewById(R.id.tvCancelled);
    tvDismissed = findViewById(R.id.tvDismissed);

    rxSearchList = RxSearchList.create("Search Hint is here", "job", false, null);
  }

  @Override
  protected void onStart() {
    super.onStart();

    mDisposables.add(
        RxView.clicks(btnShowList)
            .map(t -> clearAllText())
            .map(t -> rxSearchList.setShowLogo(swShowLogo.isChecked()).show(getSupportFragmentManager()))
            .switchMap(RxSearchList::getQueryIntent)
            .switchMap(DataMocker::getList)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(rxSearchList::setState)
    );

    mDisposables.add(
        rxSearchList.results()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::displayItem)
    );

    mDisposables.add(
        rxSearchList.onCancel()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> tvCancelled.setText(getString(R.string.cancelled)))
    );

    mDisposables.add(
        rxSearchList.onDismiss()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(t -> tvDismissed.setText(getString(R.string.dismissed)))
    );
  }

  /**
   * @return value does not matter. just DON'T return null.
   */
  private Object clearAllText() {
    tvResult.setText("");
    tvCancelled.setText("");
    tvDismissed.setText("");
    return true;
  }

  private void displayItem(Item item) {
    if (item != null) {
      Log.d(TAG, "displayItem : " + item.getDes());

      tvResult.setText(String.format("code = %s , Des = %s", item.getCode(), item.getDes()));
    } else {
      Log.d(TAG, "displayItem : JUST returned");
    }
  }

  @Override
  protected void onPause() {
    mDisposables.clear();
    super.onPause();
  }
}