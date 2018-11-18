package com.mojtaba_shafaei.android.lovSimple;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.mojtaba_shafaei.android.lovSimple.LovSimple.Item;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.comparators.ComparatorChain;
import org.apache.commons.lang3.ObjectUtils;

class LovSimpleAdapter extends RecyclerView.Adapter<ListItemSingleRowHolder> implements Disposable{

private final String TAG = "LovSimpleAdapter";

private final List<Item> data = new LinkedList<>();
private LayoutInflater inflater;
private OnListItemClickListener<LovSimple.Item> itemClickListener;

private String[] queries;

private final Collator mCollator;
private final CompositeDisposable mDisposable = new CompositeDisposable();

private RecyclerView mRecyclerView;

LovSimpleAdapter(RecyclerView recyclerView, OnListItemClickListener<LovSimple.Item> onListItemClickListener, LayoutInflater inflater){
  this.mRecyclerView = recyclerView;
  this.itemClickListener = onListItemClickListener;
  this.inflater = inflater;
  mCollator = PersianCollator.getPersianInstance();
  mCollator.setStrength(Collator.PRIMARY);
}

@NonNull
@Override
public ListItemSingleRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
  return ListItemSingleRowHolder.New(inflater, parent);
}

@Override
public void onBindViewHolder(@NonNull ListItemSingleRowHolder holder, int position){
  try{
    LovSimple.Item job = data.get(holder.getAdapterPosition());
    holder.setTypeface(LovSimple.TYPEFACE_IRANSANS_NORMAL);

    if(queries != null && queries.length > 0){
      SpannableStringBuilder ssb = new SpannableStringBuilder(job.getDes());
      for(String k : queries){
        final int index = StringUtils.indexOfIgnoreCase(job.getDes(), k);
        if(index != -1){
          ssb.setSpan(
              new CustomTypefaceSpan("", LovSimple.TYPEFACE_IRANSANS_BOLD),
              index, index + k.length(),
              Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        }
      }
      holder.setText(ssb);
      ssb.clear();

    } else{
      holder.setText(job.getDes());
    }

    holder.itemView.setOnClickListener((View v) -> itemClickListener
        .onListItemClicked(holder.getAdapterPosition(), job));
  } catch(IndexOutOfBoundsException e){
    Log.e(TAG, "Exception handled", e);
  }
}

@Override
public int getItemCount(){
  return data.size();
}

@Override
public long getItemId(int position){
  return data.get(position).hashCode();
}

void setHighlightFor(String[] queries){
  this.queries = queries;
}

void setData(List<Item> list){
  if(CollectionUtils.isEmpty(list)){
    clearData();

  } else{
    mDisposable.add(DiffUtil.getDiff(data, list)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(diffResultLce -> {
                          if(isDisposed()){
                            return;
                          }
                          if(diffResultLce.hasError()){
                            Toast.makeText(inflater.getContext().getApplicationContext(), diffResultLce.getError().getMessage(), Toast.LENGTH_LONG)
                                .show();
                          } else{
                            data.clear();
                            data.addAll(ObjectUtils.defaultIfNull(list, new ArrayList<>(0)));
                            final ComparatorChain<Item> chain = new ComparatorChain<>();
                            chain.addComparator((o1, o2) -> this.compare(o1.getPriority(), o2.getPriority()), true);
                            chain.addComparator((o1, o2) -> mCollator.compare(o1.getDes(), o2.getDes()));
                            Collections.sort(data, chain);

                            diffResultLce.getData().dispatchUpdatesTo(LovSimpleAdapter.this);
                            mRecyclerView.scrollToPosition(0);
                          }
                        })
    );
  }

}

/**
 * DON'T use Integer.compare, Its added in api 19
 */
int compare(int x, int y){
  return (x < y) ? -1 : ((x == y) ? 0 : 1);
}

@Override
public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView){
  dispose();
  mRecyclerView = null;
  super.onDetachedFromRecyclerView(recyclerView);
}

@Override
public void dispose(){
  mDisposable.clear();
}

@Override
public boolean isDisposed(){
  return mDisposable.isDisposed();
}

void clearData(){
  data.clear();
  notifyDataSetChanged();
}
}
