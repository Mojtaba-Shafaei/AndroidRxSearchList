package com.mojtaba_shafaei.android.rxSearchList;

import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.mojtaba_shafaei.android.rxSearchList.RxSearchList.Item;
import java.util.ArrayList;
import java.util.List;

class ListAdapter extends RecyclerView.Adapter<ListItemSingleRowHolder> {

  private final String TAG = "ListAdapter";

  private List<? extends Item> data = new ArrayList<>();
  private LayoutInflater inflater;
  private Typeface mTypeface;
  private OnListItemClickListener itemClickListener;

  private String[] queries;

  private boolean mShowLogo;

  /**
   * @param showLogo {@code Boolean}, it determines the visibility of the logo.<br/> If set as
   * {@code true} the logo will be visible in each row, otherwise the logo will be hidden.<br/> The
   * default value is {@code false}
   * @param onListItemClickListener {@link OnListItemClickListener}, it will be call whenever user
   * clicks on a row to return back the selected {@link Item}.
   * @param inflater {@link LayoutInflater}. It's required.
   * @param typeface {@link Typeface}.
   */
  ListAdapter(boolean showLogo
      , OnListItemClickListener onListItemClickListener
      , @NonNull LayoutInflater inflater
      , @Nullable Typeface typeface) {

    this.mShowLogo = showLogo;
    this.itemClickListener = onListItemClickListener;
    this.inflater = inflater;
    this.mTypeface = typeface;
  }

  @NonNull
  @Override
  public ListItemSingleRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    return ListItemSingleRowHolder.New(inflater, parent);
  }

  @Override
  public void onBindViewHolder(@NonNull ListItemSingleRowHolder holder, int position) {
    try {
      RxSearchList.Item item = data.get(holder.getAdapterPosition());
      holder.fill(item, mShowLogo, queries, mTypeface);
      holder.itemView.setOnClickListener(v -> itemClickListener.onListItemClicked(item));
    } catch (IndexOutOfBoundsException e) {
      Log.e(TAG, "Exception handled", e);
    }
  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  @Override
  public long getItemId(int position) {
    return data.get(position).hashCode();
  }

  /**
   * @param queries, Ensure that all elements are in lowercase, its important to prevent performance
   * issues
   */
  void setData(List<? extends Item> list, String[] queries) {
    this.queries = queries;

    data.clear();
    if (list != null && list.size() > 0) {
      data = list;
    }
    notifyDataSetChanged();
  }
}
