package com.mojtaba_shafaei.android.lovSimple;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.bumptech.glide.Glide;

/**
 * Created by mojtaba on 9/23/17.
 */

class ListItemSingleRowHolder extends RecyclerView.ViewHolder{

private AppCompatImageView ivLogo;
private AppCompatTextView tvTitle;

static ListItemSingleRowHolder New(LayoutInflater inflater, ViewGroup parent){
  return new ListItemSingleRowHolder(inflater.inflate(R.layout.lov_simple_li_simple1, parent, false));
}

private ListItemSingleRowHolder(View itemView){
  super(itemView);
  ivLogo = itemView.findViewById(R.id.iv_logo);
  tvTitle = itemView.findViewById(R.id.tv_title);

  ViewCompat.setLayoutDirection(tvTitle, ViewCompat.LAYOUT_DIRECTION_RTL);
  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
    tvTitle.setTextDirection(View.TEXT_DIRECTION_RTL);
  }
}

void setTitle(CharSequence text){
  tvTitle.setText(text);
}

void setTypeface(Typeface typeface){
  if(typeface != null){
    tvTitle.setTypeface(typeface);
  }
}

private void setLogo(CharSequence logoUrl){
  if(StringUtils.isAllBlank(logoUrl)){
    ivLogo.setVisibility(View.GONE);
  } else{
    ivLogo.setVisibility(View.VISIBLE);

    CircularProgressDrawable progressDrawable = new CircularProgressDrawable(ivLogo.getContext());
    progressDrawable.setStyle(0);
    progressDrawable.setColorSchemeColors(0xff00ddff, 0xff99cc00, 0xffffbb33, 0xffff4444);
    progressDrawable.start();

    Glide.with(ivLogo.getContext())
        .asDrawable()
        .apply(GlideUtil.getInstance().getOptions(progressDrawable))
        .transition(withCrossFade())
        .load(logoUrl)
        .into(ivLogo);
  }
}

public void fill(CharSequence title, CharSequence logoUrl){
  setTitle(title);
  setLogo(logoUrl);
}
}
