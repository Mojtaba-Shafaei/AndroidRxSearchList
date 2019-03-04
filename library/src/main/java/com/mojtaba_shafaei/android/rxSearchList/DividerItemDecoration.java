package com.mojtaba_shafaei.android.rxSearchList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.LinearLayout;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by mojtaba on 9/24/18.
 */

class DividerItemDecoration extends RecyclerView.ItemDecoration {

  private final ColorDrawable mDivider = new ColorDrawable(Color.TRANSPARENT) {
    @Override
    public int getIntrinsicWidth() {
      return Math.abs(getBounds().right - getBounds().left);
    }

    @Override
    public int getIntrinsicHeight() {
      return Math.abs(getBounds().top - getBounds().bottom);
    }
  };
  private int mOrientation;

  public DividerItemDecoration(int widthInPix, int heightInPix) {
    setDividerSize(0, 0, widthInPix, heightInPix);
  }

  private void setDividerSize(int left, int top, int right, int bottom) {
    mDivider.setBounds(left, top, right, bottom);
  }

  public void setWidth(int widthInPix) {
    setDividerSize(mDivider.getBounds().left
        , mDivider.getBounds().top
        , widthInPix
        , mDivider.getBounds().bottom);
  }

  public void setHeight(int heightInPix) {
    setDividerSize(mDivider.getBounds().left
        , mDivider.getBounds().top
        , mDivider.getBounds().right
        , heightInPix);
  }

  /**
   * Draws horizontal or vertical dividers onto the parent RecyclerView.
   *
   * @param canvas The {@link Canvas} onto which dividers will be drawn
   * @param parent The RecyclerView onto which dividers are being added
   * @param state The current RecyclerView.State of the RecyclerView
   */
  @Override
  public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
    if (mOrientation == LinearLayout.HORIZONTAL) {
      drawHorizontalDividers(canvas, parent);
    } else if (mOrientation == LinearLayout.VERTICAL) {
      drawVerticalDividers(canvas, parent);
    }
  }

  /**
   * Determines the size and location of offsets between items in the parent RecyclerView.
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

    if (parent.getChildAdapterPosition(view) == 0) {
      return;
    }

    mOrientation = ((LinearLayoutManager) parent.getLayoutManager()).getOrientation();
    if (mOrientation == LinearLayoutManager.HORIZONTAL) {
      if (ViewCompat.getLayoutDirection(parent) == ViewCompat.LAYOUT_DIRECTION_LTR) {
        outRect.left = mDivider.getIntrinsicWidth();
      } else {
        outRect.right = mDivider.getIntrinsicWidth();
      }
    } else if (mOrientation == LinearLayoutManager.VERTICAL) {
      outRect.top = mDivider.getIntrinsicHeight();
    }
  }

  /**
   * Adds dividers to a RecyclerView with a LinearLayoutManager or its subclass oriented
   * horizontally.
   *
   * @param canvas The {@link Canvas} onto which horizontal dividers will be drawn
   * @param parent The RecyclerView onto which horizontal dividers are being added
   */
  private void drawHorizontalDividers(Canvas canvas, RecyclerView parent) {
    int parentTop = parent.getPaddingTop();
    int parentBottom = parent.getHeight() - parent.getPaddingBottom();

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount - 1; i++) {
      View child = parent.getChildAt(i);

      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

      int parentLeft = child.getRight() + params.rightMargin;
      int parentRight = parentLeft + mDivider.getIntrinsicWidth();

      mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
      mDivider.draw(canvas);
    }
  }

  /**
   * Adds dividers to a RecyclerView with a LinearLayoutManager or its subclass oriented
   * vertically.
   *
   * @param canvas The {@link Canvas} onto which vertical dividers will be drawn
   * @param parent The RecyclerView onto which vertical dividers are being added
   */
  private void drawVerticalDividers(Canvas canvas, RecyclerView parent) {
    int parentLeft = parent.getPaddingLeft();
    int parentRight = parent.getWidth() - parent.getPaddingRight();

    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount - 1; i++) {
      View child = parent.getChildAt(i);

      RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

      int parentTop = child.getBottom() + params.bottomMargin;
      int parentBottom = parentTop + mDivider.getIntrinsicHeight();

      mDivider.setBounds(parentLeft, parentTop, parentRight, parentBottom);
      mDivider.draw(canvas);
    }
  }
}
