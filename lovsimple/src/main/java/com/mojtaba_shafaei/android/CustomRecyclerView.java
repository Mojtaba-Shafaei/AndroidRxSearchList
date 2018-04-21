package com.mojtaba_shafaei.android;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

class CustomRecyclerView extends RecyclerView {

    @Nullable
    private View emptyView;

    //<editor-fold desc="Observer">
    private AdapterDataObserver emptyObserver = new AdapterDataObserver() {

        @Override
        public void onChanged() {
            if (getAdapter() != null && emptyView != null) {
                if (getAdapter().getItemCount() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                    CustomRecyclerView.this.setVisibility(View.GONE);
                } else {
                    emptyView.setVisibility(View.GONE);
                    CustomRecyclerView.this.setVisibility(View.VISIBLE);
                }
            }

        }
    };
    //</editor-fold>

    public CustomRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
    }

    void setEmptyView(@Nullable View emptyView) {
        this.emptyView = emptyView;
    }

    void observeAdapter() {
        try {
            if (emptyObserver == null) {
                emptyObserver = new AdapterDataObserver() {
                    @Override
                    public void onChanged() {
                        if (getAdapter() != null && emptyView != null) {
                            if (getAdapter().getItemCount() == 0) {
                                emptyView.setVisibility(View.VISIBLE);
                                CustomRecyclerView.this.setVisibility(View.GONE);
                            } else {
                                emptyView.setVisibility(View.GONE);
                                CustomRecyclerView.this.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                };
            }
            emptyObserver.onChanged();

        } catch (Exception e) {
            Log.e("CustomRecyclerView", "Exception handled", e);
        }
    }

    void removeObservers() {
        if (emptyObserver != null) {
            emptyObserver = null;
        }
    }
}
