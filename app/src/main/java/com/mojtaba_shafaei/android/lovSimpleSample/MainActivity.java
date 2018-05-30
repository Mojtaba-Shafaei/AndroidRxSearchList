package com.mojtaba_shafaei.android.lovSimpleSample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.mojtaba_shafaei.android.LovSimple;
import com.mojtaba_shafaei.android.LovSimple.Item;

public class MainActivity extends AppCompatActivity {

  private String TAG = getClass().getSimpleName();
  private TextView textView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.button).setOnClickListener(view ->
        LovSimple.start(getActivity()
            , "Search Jobs"
            , new SimpleFetcherBl()
            , this::displayItem)
    );

    textView = findViewById(R.id.tvResult);

    if (savedInstanceState != null && savedInstanceState.containsKey("item")) {
      textView.setText(savedInstanceState.getString("item"));
    }
  }

  private void displayItem(Item item) {
    if (item != null) {
      Log.d(TAG, "displayItem : " + item.getDes());

      Job job = (Job) item;
      textView.setText(job.toString());
    } else {
      Log.d(TAG, "displayItem : JUST returned");
    }
  }

  private AppCompatActivity getActivity() {
    return this;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putString("item", textView.getText().toString());
    super.onSaveInstanceState(outState);
  }
}