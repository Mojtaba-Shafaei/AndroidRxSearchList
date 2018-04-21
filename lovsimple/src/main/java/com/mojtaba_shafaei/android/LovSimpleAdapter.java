package com.mojtaba_shafaei.android;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Locale;

class LovSimpleAdapter extends RecyclerView.Adapter<ListItemSingleRowHolder> {

    private SortedList<LovSimple.Item> data;
    private LayoutInflater inflater;
    private OnListItemClickListener<LovSimple.Item> itemClickListener;

    private String[] queries;
    private String TAG = getClass().getSimpleName();

    private final java.text.Collator collator;


    public LovSimpleAdapter(OnListItemClickListener<LovSimple.Item> onListItemClickListener,
                            LayoutInflater inflater) {
        this.itemClickListener = onListItemClickListener;
        this.inflater = inflater;
        collator = java.text.Collator.getInstance(new Locale("fa"));

        data = new SortedList<>(LovSimple.Item.class, new SortedListAdapterCallback<LovSimple.Item>(this) {
            @Override
            public int compare(LovSimple.Item f1, LovSimple.Item f2) {
                return collator
                        .compare(f1.getPriority() + " " + f1.getDes(), f2.getPriority() + " " + f2.getDes());
            }

            @Override
            public boolean areContentsTheSame(LovSimple.Item oldItem, LovSimple.Item newItem) {
                return (oldItem.getCode().equals(newItem.getCode()));
            }

            @Override
            public boolean areItemsTheSame(LovSimple.Item oldItem, LovSimple.Item newItem) {
                return (oldItem.getCode().equals(newItem.getCode()));
            }
        });

    }

    @Override
    public ListItemSingleRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.lov_simple_li_simple1, parent, false);
        return new ListItemSingleRowHolder(view);
    }

    @Override
    public void onBindViewHolder(ListItemSingleRowHolder holder, int position) {
        try {
            LovSimple.Item job = data.get(holder.getAdapterPosition());
            holder.getText1().setTypeface(LovSimple.TYPEFACE_IRANSANS_NORMAL);

            if (queries != null && queries.length > 0) {
                SpannableStringBuilder ssb = new SpannableStringBuilder(job.getDes());
                for (String k : queries) {
                    if (k.length() > 1 && job.getDes().toUpperCase().contains(k.toUpperCase())) {
                        int index = job.getDes().toUpperCase().indexOf(k.toUpperCase());
                        ssb.setSpan(
                                new CustomTypefaceSpan("", LovSimple.TYPEFACE_IRANSANS_BOLD),
                                index,
                                index + k.length(),
                                Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                    }
                }
                holder.getText1().setText(ssb);
                ssb.clear();

            } else {
                holder.getText1().setText(job.getDes());
            }
            holder.itemView.setOnClickListener((View v) -> itemClickListener
                    .onListItemClicked(holder.getAdapterPosition(), job));
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

    public void setHighlightFor(String[] queries) {
        this.queries = queries;
    }

    public void setData(List<LovSimple.Item> list) {
        data.clear();
        data.addAll(list);
        notifyDataSetChanged();
    }
}
