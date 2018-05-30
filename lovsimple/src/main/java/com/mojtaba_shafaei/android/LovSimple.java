package com.mojtaba_shafaei.android;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.DisposableSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class LovSimple extends AppCompatDialogFragment {

  private static final String TAG = LovSimple.class.getSimpleName();
  private String searchViewHint;

  public interface Item {

    String getCode();

    String getDes();

    int getPriority();

    void setCode(String code);

    void setDes(String des);

    void setPriority(int priority);
  }

  public interface FetchDataListener {

    Observable<List<Item>> fetch(String query);
  }

  static Typeface TYPEFACE_IRANSANS_BOLD, TYPEFACE_IRANSANS_NORMAL;
  //<editor-fold desc="ButterKnife">
  private AppCompatEditText searchView;
  private AppCompatImageButton btnClearText;
  private CustomRecyclerView recyclerView;
  private ContentLoadingProgressBar progressBar;
  private TextView tvMessage;
  private ViewGroup root;
  private AppCompatImageButton btnBack;
  //</editor-fold>

  private LovSimpleAdapter adapter;
  private final CompositeDisposable disposable = new CompositeDisposable();

  /////////////////////////////////////
  private FetchDataListener sLoader;
  private OnResultListener mOnResultListener;
  private Dialog.OnCancelListener mOnCancelListener;
  private Dialog.OnDismissListener mOnDismissListener;

  /////////////////////////////////////
//
  private static LovSimple start(AppCompatActivity activity,
      String searchViewHint,
      FetchDataListener loader,
      OnResultListener onResultListener,
      Dialog.OnCancelListener onCancelListener,
      Dialog.OnDismissListener onDismissListener
  ) {

    LovSimple lovSimple = new LovSimple();
    lovSimple.sLoader = loader;
    lovSimple.mOnResultListener = onResultListener;
    lovSimple.mOnCancelListener = onCancelListener;
    lovSimple.mOnDismissListener = onDismissListener;

    lovSimple.searchViewHint = searchViewHint;
    lovSimple.show(activity.getSupportFragmentManager(), "");

    return lovSimple;
  }

  public static LovSimple start(AppCompatActivity activity,
      String searchViewHint,
      FetchDataListener loader
  ) {
    return start(activity, searchViewHint, loader, null, null, null);
  }

  public static void start(Fragment fragment,
      String searchViewHint,
      FetchDataListener loader,
      OnResultListener onResultListener) {

    LovSimple lovSimple = new LovSimple();
    lovSimple.sLoader = loader;
    lovSimple.mOnResultListener = onResultListener;
    lovSimple.searchViewHint = searchViewHint;

    if (fragment.getFragmentManager() != null) {
      //lovSimple.show(fragment.getFragmentManager(), "");
    } else {
      Log.e(TAG, "start: fragment.getFragmentManager() return null");
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NO_TITLE,
        R.style.ThemeOverlay_AppCompat_Light);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup __,
      @Nullable Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.lov_simple_activity_all_lov, null);
    TYPEFACE_IRANSANS_BOLD = Typeface
        .createFromAsset(getResources().getAssets(), "IRANSansMobile_Bold.ttf");
    TYPEFACE_IRANSANS_NORMAL = Typeface
        .createFromAsset(getResources().getAssets(), "IRANSansMobile.ttf");

    initUi(view);

    searchView.setHint(TextUtils.isEmpty(searchViewHint) ? "" : searchViewHint);

    recyclerView.setLayoutManager(
        new LinearLayoutManager(inflater.getContext(), LinearLayoutManager.VERTICAL, false));
    recyclerView.setHasFixedSize(true);
    adapter = new LovSimpleAdapter((position, data) -> {
      try {
        onResult(data);
        dismiss();
      } catch (Exception e) {
        Log.e(TAG, "onListItemClicked", e);
      }

    }, LayoutInflater.from(inflater.getContext()));

    adapter.setHasStableIds(true);

    tvMessage.setTypeface(TYPEFACE_IRANSANS_NORMAL);
    tvMessage.setText(getString(R.string.lov_simple_no_data1p));
    recyclerView.setEmptyView(tvMessage);
    recyclerView.setAdapter(adapter);

    btnBack.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        onCancel(LovSimple.this.getDialog());
        hideSoftKeyboard(searchView);

        btnBack.getViewTreeObserver()
            .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                if (btnBack.getViewTreeObserver().isAlive()) {
                  btnBack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                  float value =
                      ((View) btnBack.getParent()).getRight() - btnBack.getRight() + btnBack
                          .getWidth();
                  ObjectAnimator animator = ObjectAnimator.ofFloat(btnBack, "translationX", value);
                  animator.setInterpolator(new DecelerateInterpolator(.8F));
                  animator.setDuration(300);
                  animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                      super.onAnimationEnd(animation);
                      dismiss();
                    }
                  });
                  animator.start();
                }
              }
            });
      }
    });

    btnClearText.setOnClickListener(v -> searchView.setText(""));

    return view;
  }

  private void onResult(Item data) {
    if (mOnResultListener != null) {
      mOnResultListener.onResult(data);
    }
  }

  private void hideSoftKeyboard(AppCompatEditText searchView) {
    if (searchView != null) {
      InputMethodManager inputManager = (InputMethodManager)
          searchView.getContext().getSystemService(INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromInputMethod(searchView.getWindowToken(), 0);
      inputManager.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);

      getDialog().getWindow().setSoftInputMode(
          WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

      searchView.clearFocus();
      searchView.setSelected(false);
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    tvMessage.setVisibility(View.GONE);
    try {
      disposable.add(
          RxSearch.fromEdiText(searchView)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .startWith(searchView.getText().toString())
              .map(query -> {
                if (query.isEmpty()) {
                  btnClearText.setVisibility(View.GONE);
                } else {
                  btnClearText.setVisibility(View.VISIBLE);
                }
                return query;
              })
              .observeOn(Schedulers.io())
              .debounce(getResources().getInteger(R.integer.lov_simple_config_debounce_duration),
                  TimeUnit.MILLISECONDS)
              .throttleWithTimeout(
                  getResources().getInteger(R.integer.lov_simple_config_throttle_duration),
                  TimeUnit.MILLISECONDS)
              .distinctUntilChanged()
              .switchMap(query -> sLoader.fetch(query))
              .observeOn(Schedulers.computation())
              .switchMap(data -> {
                final String query = getQueryText();
                //filter results base on query
                if (query.length() > 1) {
                  String[] queries = query.split(" ");
                  //remove space and 1 char length parts
                  List<String> ss = new ArrayList<>(Arrays.asList(queries));
                  for (Iterator<String> iterator = ss.iterator(); iterator.hasNext(); ) {
                    if (iterator.next().length() <= 1) {
                      iterator.remove();
                    }
                  }
                  queries = new String[ss.size()];
                  ss.toArray(queries);
                  //
                  List<Item> results = new ArrayList<>();
                  int priority;
                  for (Item j : data) {
                    priority = Integer.MAX_VALUE;
                    for (String k : queries) {
                      k = k.toUpperCase();
                      if (j.getDes().toUpperCase().contains(k)) {
                        priority--;
                      }
                    }

                    if (j.getDes().contentEquals(query.toUpperCase())) {
                      priority--;
                    }

                    if (j.getDes().toUpperCase().startsWith(query.toUpperCase())) {
                      priority--;
                    }

                    j.setPriority(priority);
                    //Add item if it is desired one.
                    if (priority != Integer.MAX_VALUE) {
                      results.add(j);
                    }
                  }
                  return Observable.just(Lce.data(results));
                } else {
                  return Observable.just(Lce.data(data));
                }
              })
              .startWith(Lce.loading())
              .toFlowable(BackpressureStrategy.BUFFER)
              .observeOn(AndroidSchedulers.mainThread())
              .subscribeWith(new DisposableSubscriber<Lce<List<Item>>>() {
                @Override
                public void onNext(Lce<List<Item>> lce) {
                  if (isDisposed()) {
                    return;
                  }
                  try {
                    if (lce.isLoading()) {
                      hideErrors();
                      showContentLoading(true);

                    } else if (lce.hasError()) {
                      showContentLoading(false);
                      showInternetError();

                    } else {
                      hideErrors();
                      showContentLoading(false);
                      if (lce.getData() != null) {
                        String[] splitDesiredHighlight =
                            searchView.getText().toString().trim().split(" ");

                        adapter.setHighlightFor(splitDesiredHighlight);
                        adapter.setData(lce.getData());
                        recyclerView.getLayoutManager().scrollToPosition(0);

                        recyclerView.observeAdapter();
                      } else {
                        showInternetError();
                      }
                    }
                  } catch (Exception e) {
                    Log.e(TAG, "onNext: ", e);
                  }
                }

                @Override
                public void onError(Throwable e) {
                  Log.d(TAG,
                      "onError() called with: e = [" + e + "],isDisposed() = [" + isDisposed()
                          + "]");
                  if (isDisposed()) {
                    return;
                  }
                  showContentLoading(false);
                  showInternetError();
                }

                @Override
                public void onComplete() {
                  Log.d(TAG, "onComplete() called, isDisposed = [" + isDisposed() + "]");
                }
              })
      );
    } catch (Exception e) {
      Log.e(TAG, "onCreate:RxSearch ", e);
    }
  }

  private void showContentLoading(boolean b) {
    progressBar.setVisibility(b ? VISIBLE : View.GONE);
  }

  @NonNull
  private String getQueryText() {
    return searchView.getText().toString().toLowerCase().trim();
  }

  private void initUi(View root) {
    searchView = root.findViewById(R.id.lov_simple_search_view);
    btnClearText = root.findViewById(R.id.lov_simple_btn_clear_search);
    recyclerView = root.findViewById(R.id.lov_simple_list);
    progressBar = root.findViewById(R.id.lov_simple_progressBar);
    tvMessage = root.findViewById(R.id.lov_simple_tv_message);
    root = root.findViewById(R.id.lov_simple_root);
    btnBack = root.findViewById(R.id.lov_simple_btn_back);

    ViewCompat.setLayoutDirection(root, ViewCompat.LAYOUT_DIRECTION_RTL);

    progressBar.setVisibility(View.GONE);
    searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

    btnBack.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @Override
          public void onGlobalLayout() {
            if (btnBack.getViewTreeObserver().isAlive()) {
              btnBack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
              ObjectAnimator animator = ObjectAnimator.ofFloat(btnBack, "translationX", 0);
              animator.setInterpolator(new DecelerateInterpolator(1.5F));
              animator.setStartDelay(500);
              animator.setDuration(300);
              animator.start();
            }
          }
        });
  }

  private void hideErrors() {
    tvMessage.setVisibility(View.GONE);
  }

  private void showInternetError() {
    tvMessage.setVisibility(VISIBLE);
    tvMessage.setText(getString(R.string.lov_simple_no_internet_connection));
  }

  public LovSimple setOnResultListener(OnResultListener mOnResultListener) {
    this.mOnResultListener = mOnResultListener;
    return this;
  }

  public LovSimple setOnCancelListener(OnCancelListener mOnCancelListener) {
    this.mOnCancelListener = mOnCancelListener;
    return this;
  }

  public LovSimple setOnDismissListener(OnDismissListener mOnDismissListener) {
    this.mOnDismissListener = mOnDismissListener;
    return this;
  }

  @Override
  public void onResume() {
    super.onResume();
/*    try {
      getDialog().getWindow()
          .setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    } catch (Exception e) {
      e.printStackTrace();
    }*/
  }

  @Override
  public void onPause() {
    hideSoftKeyboard(searchView);
    disposable.clear();

    super.onPause();
    dismiss();
  }

  @Override
  public void onDestroy() {
    sLoader = null;
    mOnResultListener = null;

    super.onDestroy();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    if (mOnDismissListener != null) {
      mOnDismissListener.onDismiss(dialog);
    }
    super.onDismiss(dialog);
  }

  @Override
  public void onCancel(DialogInterface dialog) {
    if (mOnCancelListener != null) {
      mOnCancelListener.onCancel(dialog);
    }
    super.onCancel(dialog);
  }

  abstract static class Lce<T> {

    static <T> Lce<T> data(final T data) {
      return new Lce<T>() {
        @Override
        public boolean isLoading() {
          return false;
        }

        @Override
        public boolean hasError() {
          return false;
        }

        @Override
        public Throwable getError() {
          return null;
        }

        @Override
        public T getData() {
          return data;
        }
      };
    }

    static <T> Lce<T> error(final Throwable error) {
      return new Lce<T>() {
        @Override
        public boolean isLoading() {
          return false;
        }

        @Override
        public boolean hasError() {
          return true;
        }

        @Override
        public Throwable getError() {
          return error;
        }

        @Override
        public T getData() {
          return null;
        }
      };
    }

    static <T> Lce<T> loading() {
      return new Lce<T>() {
        @Override
        public boolean isLoading() {
          return true;
        }

        @Override
        public boolean hasError() {
          return false;
        }

        @Override
        public Throwable getError() {
          return null;
        }

        @Override
        public T getData() {
          return null;
        }
      };
    }

    public abstract boolean isLoading();

    public abstract boolean hasError();

    public abstract Throwable getError();

    public abstract T getData();

    @Override
    public String toString() {
      return "Lce{isLoading = " + isLoading() +
          ", hasError = " + (hasError() ? hasError() + "[" + getError() + "]" : hasError()) +
          ", data = " + getData() + "}";
    }
  }

  public interface OnResultListener {

    void onResult(Item item);
  }
}
