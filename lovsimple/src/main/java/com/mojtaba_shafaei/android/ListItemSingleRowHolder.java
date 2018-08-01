package com.mojtaba_shafaei.android;

import android.os.Build;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by mojtaba on 9/23/17.
 */

class ListItemSingleRowHolder extends RecyclerView.ViewHolder {

    private TextView text1;

    public ListItemSingleRowHolder(View itemView) {
        super(itemView);
        text1 = itemView.findViewById(android.R.id.text1);
        ViewCompat.setLayoutDirection(text1, ViewCompat.LAYOUT_DIRECTION_RTL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            text1.setTextDirection(View.TEXT_DIRECTION_RTL);
        }
    }

    public TextView getText1() {
        return text1;
    }

    public void setText(String text) {
        text1.setText(text);
    }
}
