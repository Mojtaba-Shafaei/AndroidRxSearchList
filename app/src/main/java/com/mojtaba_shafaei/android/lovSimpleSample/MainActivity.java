package com.mojtaba_shafaei.android.lovSimpleSample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.mojtaba_shafaei.android.lovSimple.LovSimple;
import com.mojtaba_shafaei.android.lovSimple.LovSimple.Item;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity{

private String TAG = "MainActivity";
private TextView textView;

private Disposable subscribe;

@Override
protected void onCreate(Bundle savedInstanceState){
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  LovSimple lovSimple = LovSimple.create("Enter Query", "job")
      .setOnResultListener(this::displayItem)
      .setOnCancelListener(dialog -> Log.d(TAG, "cancelled: "))
      .setOnDismissListener(dialog -> Log.d(TAG, "dismissed: "));

  subscribe = lovSimple.getQueryIntent()
      .flatMap(DataMocker::getList)
      .subscribe(lovSimple::setState);

  //test for online API

  findViewById(R.id.button).setOnClickListener(view -> {
    lovSimple.show(getSupportFragmentManager(), "");
  });

  //test for offline

  /*findViewById(R.id.button)
      .setOnClickListener(view -> {
        lovSimple.show(getSupportFragmentManager(), "");
        //test for offline
        subscribe = DataMocker.getList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(lovSimple::setState);
      });
*/
  textView = findViewById(R.id.tvResult);
}

private void displayItem(Item item){
  if(item != null){
    Log.d(TAG, "displayItem : " + item.getDes());

    Job job = (Job) item;
    textView.setText(job.toString());
  } else{
    Log.d(TAG, "displayItem : JUST returned");
  }
}

@Override
protected void onDestroy(){
  if(subscribe != null){
    subscribe.dispose();
  }
  super.onDestroy();
}
}