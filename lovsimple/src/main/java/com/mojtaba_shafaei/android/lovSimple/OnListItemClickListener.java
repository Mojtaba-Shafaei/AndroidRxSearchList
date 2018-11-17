package com.mojtaba_shafaei.android.lovSimple;

/**
 * Created by mojtaba on 9/3/17.
 */

interface OnListItemClickListener<T> {
    void onListItemClicked(int position, T data);
}
