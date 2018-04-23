package com.mojtaba_shafaei.android;

import static android.view.View.VISIBLE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutManager;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
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
import org.parceler.Parcels;

public class LovSimple extends AppCompatActivity {

  private static final String TAG = LovSimple.class.getSimpleName();


  static final String HINT_KEY = "HINT_KEY";

  public interface Item {

    String getCode();

    String getDes();

    int getPriority();

    void setCode(String code);

    void setDes(String des);

    void setPriority(int priority);
  }

  public interface FetchDataListener {

    Observable<Lce<List<Item>>> fetch(String query);
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
  private static FetchDataListener sLoader;

  public static void startForResult(Activity activity,
      int requestCode,
      String searchViewHint,
      FetchDataListener loader) {

    sLoader = loader;
    Intent starter = new Intent(activity, LovSimple.class);
    starter.putExtra(HINT_KEY, searchViewHint);
    ActivityCompat.startActivityForResult(activity, starter, requestCode, null);
    /*activity.overridePendingTransition(R.anim.lov_simple_anim_slide_in_right,
        R.anim.lov_simple_anim_slide_out_left);*/
  }

  public static void startForResult(Fragment fragment,
      int requestCode,
      String searchViewHint,
      FetchDataListener loader) {

    sLoader = loader;
    if (fragment.getActivity() != null) {
      Intent starter = new Intent(fragment.getActivity(), LovSimple.class);
      starter.putExtra(HINT_KEY, searchViewHint);
      fragment.startActivityForResult(starter, requestCode);
      /*fragment.getActivity()
          .overridePendingTransition(R.anim.lov_simple_anim_slide_in_right,
              R.anim.lov_simple_anim_slide_out_left);*/
    } else {
      Log.e(TAG, "startForResult: fragment.getActivity() return null");
    }
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.lov_simple_activity_all_lov);

    TYPEFACE_IRANSANS_BOLD = Typeface
        .createFromAsset(getResources().getAssets(), "IRANSansMobile_Bold.ttf");
    TYPEFACE_IRANSANS_NORMAL = Typeface
        .createFromAsset(getResources().getAssets(), "IRANSansMobile.ttf");

    initUi();

    String hint = getIntent().getStringExtra(HINT_KEY);
    searchView.setHint(hint != null ? hint : "");

    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.setHasFixedSize(true);
    adapter = new LovSimpleAdapter((position, data) -> {
      try {
        Intent intent = new Intent();
        intent.putExtra("data", Parcels.wrap(data));
        setResult(RESULT_OK, intent);
        supportFinishAfterTransition();
      } catch (Exception e) {
        Log.e(TAG, "onListItemClicked", e);
      }

    }, getLayoutInflater());
    adapter.setHasStableIds(true);

    tvMessage.setTypeface(TYPEFACE_IRANSANS_NORMAL);
    tvMessage.setText(getString(R.string.lov_simple_no_data1p));
    recyclerView.setEmptyView(tvMessage);
    recyclerView.setAdapter(adapter);

    btnBack.setOnClickListener((view) -> {
      setResult(RESULT_CANCELED);
      hideSoftKeyboard(searchView);

      btnBack.getViewTreeObserver()
          .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
              if (btnBack.getViewTreeObserver().isAlive()) {
                btnBack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float value = ((View) btnBack.getParent()).getRight() - btnBack.getRight() + btnBack
                    .getWidth();
                ObjectAnimator animator = ObjectAnimator.ofFloat(btnBack, "translationX", value);
                animator.setInterpolator(new DecelerateInterpolator(.8F));
                animator.setDuration(300);
                animator.addListener(new AnimatorListenerAdapter() {
                  @Override
                  public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    supportFinishAfterTransition();
                  }
                });
                animator.start();
              }
            }
          });
    });
    btnClearText.setOnClickListener((view -> searchView.setText("")));

  }

  private void hideSoftKeyboard(AppCompatEditText searchView) {
    if (searchView != null) {
      InputMethodManager inputManager = (InputMethodManager)
          getSystemService(INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromInputMethod(searchView.getWindowToken(), 0);
      inputManager.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);

      getWindow().setSoftInputMode(
          WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

      searchView.clearFocus();
      searchView.setSelected(false);
    }
  }

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
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
              .switchMap(lce -> {
                final String query = getQueryText();
                //filter results base on query
                if (query.length() > 1 && !lce.isLoading() && !lce.hasError()) {
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
                  for (Item j : lce.getData()) {
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
                  return Observable.just(lce);
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

  private void initUi() {
    searchView = findViewById(R.id.lov_simple_search_view);
    btnClearText = findViewById(R.id.lov_simple_btn_clear_search);
    recyclerView = findViewById(R.id.lov_simple_list);
    progressBar = findViewById(R.id.lov_simple_progressBar);
    tvMessage = findViewById(R.id.lov_simple_tv_message);
    root = findViewById(R.id.lov_simple_root);
    btnBack = findViewById(R.id.lov_simple_btn_back);

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

  @Override
  protected void onPause() {
    hideSoftKeyboard(searchView);
    super.onPause();
    disposable.clear();
  }

  @Override
  public void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    sLoader = null;
  }

  public abstract static class Lce<T> {

    public static <T> Lce<T> data(final T data) {
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

    public static <T> Lce<T> error(final Throwable error) {
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

    public static <T> Lce<T> loading() {
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
}
