package com.mojtaba_shafaei.android.lovSimpleSample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.mojtaba_shafaei.android.lovSimple.LovSimple;
import com.mojtaba_shafaei.android.lovSimple.LovSimple.Item;

public class MainActivity extends AppCompatActivity{

private String TAG = "MainActivity";
private TextView textView;

@Override
protected void onCreate(Bundle savedInstanceState){
  super.onCreate(savedInstanceState);
  setContentView(R.layout.activity_main);

  LovSimple lovSimple = LovSimple.create("Enter Query", "with")
      .setOnResultListener(this::displayItem)
      .setOnCancelListener(dialog -> Log.d(TAG, "cancelled: "))
      .setOnDismissListener(dialog -> Log.d(TAG, "dismissed: "));


  lovSimple.setItems(DataMocker.getList(lovSimple.getQueries()));

  findViewById(R.id.button).setOnClickListener(view -> lovSimple.show(getSupportFragmentManager(), ""));

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
}