package com.mojtaba_shafaei.android;

/**
 * Created by mojtaba on 9/3/17.
 */

interface OnListItemClickListener<T> {
    void onListItemClicked(int position, T data);
}
