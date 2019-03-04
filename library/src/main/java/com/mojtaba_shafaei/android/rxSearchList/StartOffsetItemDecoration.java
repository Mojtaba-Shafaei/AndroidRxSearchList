package com.mojtaba_shafaei.android.rxSearchList;

import android.graphics.Rect;
import android.view.View;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

class StartOffsetItemDecoration extends RecyclerView.ItemDecoration {

  private final int mOffsetPx;

  /**
   * Sole constructor. Takes in the size of the offset to be added to the startForResult of the
   * RecyclerView.
   *
   * @param offsetPx The size of the offset to be added to the startForResult of the RecyclerView in
   * pixels
   */
  StartOffsetItemDecoration(int offsetPx) {
    mOffsetPx = offsetPx;
  }

  /**
   * Determines the size and location of the offset to be added to the startForResult of the
   * RecyclerView.
   *
   * @param outRect The {@link Rect} of offsets to be added around the child view
   * @param view The child view to be decorated with an offset
   * @param parent The RecyclerView onto which dividers are being added
   * @param state The current RecyclerView.State of the RecyclerView
   */
  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    super.getItemOffsets(outRect, view, parent, state);

    if (parent.getChildAdapterPosition(view) < 1) {
      int orientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
      if (orientation == LinearLayoutManager.HORIZONTAL) {
        if (ViewCompat.getLayoutDirection(parent) == ViewCompat.LAYOUT_DIRECTION_LTR) {
          outRect.left = mOffsetPx;
        } else {
          outRect.right = mOffsetPx;
        }

      } else if (orientation == LinearLayoutManager.VERTICAL) {
        outRect.top = mOffsetPx;
      }
    }
  }

}
