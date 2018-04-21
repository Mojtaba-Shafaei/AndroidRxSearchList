package com.mojtaba_shafaei.android.lovSimpleSample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.mojtaba_shafaei.android.LovSimple;

import org.parceler.Parcels;

public class MainActivity extends AppCompatActivity {

    private String TAG = getClass().getSimpleName();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(view ->
                LovSimple.startForResult(getActivity()
                        , 100
                        , "Search Jobs"
                        , new SimpleFetcherBl()));

        textView = findViewById(R.id.tvResult);
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (resultCode == RESULT_OK) {
            Job job = Parcels.unwrap(data.getParcelableExtra("data"));
            Log.d(TAG, "onActivityResult: " + job.toString());
            textView.setText(job.toString());
        }
    }
}