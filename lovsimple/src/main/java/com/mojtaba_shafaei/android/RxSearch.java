package com.mojtaba_shafaei.android;

import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.jakewharton.rxrelay2.PublishRelay;

import io.reactivex.Observable;

class RxSearch {
    static Observable<String> fromEdiText(@NonNull final EditText searchView) {
        final PublishRelay<String> subject = PublishRelay.create();
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                subject.accept(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //subject.onComplete();
            }
        });

        return subject;
    }
}
