package com.mojtaba_shafaei.android;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by mojtaba on 9/23/17.
 */

class ListItemSingleRowHolder extends RecyclerView.ViewHolder{

private AppCompatTextView text1;

static ListItemSingleRowHolder New(LayoutInflater inflater, ViewGroup parent){
  return new ListItemSingleRowHolder(inflater.inflate(R.layout.lov_simple_li_simple1, parent, false));
}

private ListItemSingleRowHolder(View itemView){
  super(itemView);
  text1 = itemView.findViewById(R.id.text1);
  ViewCompat.setLayoutDirection(text1, ViewCompat.LAYOUT_DIRECTION_RTL);
  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
    text1.setTextDirection(View.TEXT_DIRECTION_RTL);
  }
}

void setText(CharSequence text){
  text1.setText(text);
}

void setText(String text){
  text1.setText(text);
  text1.clearComposingText();
}

void setTypeface(Typeface typeface){
  if(typeface != null){
    text1.setTypeface(typeface);
  }
}
}
