package com.mojtaba_shafaei.android.rxSearchList;

import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;
import com.bumptech.glide.Glide;
import com.mojtaba_shafaei.android.lovSimple.R;
import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Item;

class ListItemSingleRowHolder extends RecyclerView.ViewHolder {

  private AppCompatImageView ivLogo;
  private AppCompatTextView tvTitle;

  static ListItemSingleRowHolder New(LayoutInflater inflater, ViewGroup parent) {
    return new ListItemSingleRowHolder(
        inflater.inflate(R.layout.rx_search_list_li_simple1, parent, false));
  }

  private ListItemSingleRowHolder(View itemView) {
    super(itemView);
    ivLogo = itemView.findViewById(R.id.iv_logo);
    tvTitle = itemView.findViewById(R.id.tv_title);
  }

  /**
   * @param item an Object/Class that implement the {@link Item}
   * @param showLogo hide logo if set to {@code false}
   * @param queries is always in lowercase mode.
   */
  void fill(Item item, boolean showLogo, String[] queries, Typeface typeface) {
    if (typeface != null) {
      tvTitle.setTypeface(typeface);
    }

    if (queries != null && queries.length > 0 && !TextUtils.isEmpty(item.getDes())) {
      SpannableStringBuilder ssb = new SpannableStringBuilder(item.getDes());

      final String itemDes = item.getDes().toLowerCase();
      for (String k : queries) {
        final int index = TextUtils.indexOf(itemDes, k);
        if (index != -1) {
          final int endIndex = index + k.length();
          if (index != endIndex) {
            ssb.setSpan(new ForegroundColorSpan(0xFF4CAF50), index,
                endIndex,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvTitle.setText(ssb);
          } else {
            tvTitle.setText(itemDes);
          }
        } else {
          tvTitle.setText(itemDes);
        }
      }
      ssb = null;

    } else {
      tvTitle.setText(item.getDes() == null ? "" : item.getDes());
    }

    if (showLogo) {
      ivLogo.setVisibility(View.VISIBLE);
      final CircularProgressDrawable placeHolder = new CircularProgressDrawable(
          ivLogo.getContext());
      placeHolder.setStyle(CircularProgressDrawable.DEFAULT);
      placeHolder.setColorSchemeColors(
          0xff00ddff /*Holo.blue_bright*/,
          0xff99cc00/*Holo.green_light*/,
          0xffffbb33/*Holo.orange_light*/,
          0xffff4444/*Holo.red_light*/
      );
      placeHolder.start();

      Glide.with(ivLogo)
          .load(TextUtils.isEmpty(item.getLogo())
              ? R.drawable.rx_search_list_ic_no_image_grey500
              : item.getLogo())
          .apply(GlideHelper.getInstance().getRequestOptions())
          .placeholder(placeHolder)
          .into(ivLogo);
    } else {
      ivLogo.setVisibility(View.GONE);
    }

    // This Item which has no Code, has typed by user in search view. So I distinct it with Carriage return like icon.
    if (TextUtils.isEmpty(item.getCode())) {
      ivLogo.setVisibility(View.VISIBLE);
      Glide.with(ivLogo)
          .asDrawable()
          .load(R.drawable.rx_search_list_ic_subdirectory_arrow_left_teal_400_24dp)
          .into(ivLogo);
    }
  }
}
