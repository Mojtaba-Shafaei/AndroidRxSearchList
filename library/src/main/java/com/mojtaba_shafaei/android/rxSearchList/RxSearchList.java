package com.mojtaba_shafaei.android.rxSearchList;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.mojtaba_shafaei.android.lovSimple.R;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Android Rx-List is library for Android developers to just easily pass data as an <code>{@link
 * Observable}</code> to this library and show it as a searchable list.
 *
 * <p>There is no need to implement adapter.</p>
 *
 * @author mojtaba-shafaei
 * @implSpec implement {@link Item} interface in data class which pass to library.
 * @implNote This library is based on RxJava, so it's required having knowledge of it. See <a
 * href>http://reactivex.io/RxJava/javadoc</a>
 */
public class RxSearchList extends AppCompatDialogFragment {

  // 48 dip
  private int D48;

  private boolean isRTL;
  private final String SPACE = " ";
  private final String TAG = "RxSearchList";

  /**
   * This is an interface that must implement by the data.
   */
  public interface Item {

    /**
     * @return the code of object. it usually is unique in the list.
     */
    String getCode();

    /**
     * @return the name or description of object.
     */
    String getDes();

    /**
     * @return the logo/avatar url of object.
     */
    String getLogo();
  }

  //<editor-fold desc="ui elements">
  private AppCompatEditText searchView;
  private AppCompatImageButton btnClearText;
  private RecyclerView recyclerView;
  private ContentLoadingProgressBar progressBar;
  private AppCompatTextView tvMessage;
  private AppCompatImageButton btnBack;
  //</editor-fold>

  //<editor-fold desc="PARAMETERS">
  private CharSequence mSearchViewHint;
  private CharSequence mDefaultSearchText;
  private boolean mShowLogo;
  private Typeface mDefaultTypeFace;
  //</editor-fold>

  private ListAdapter adapter;
  private final CompositeDisposable mDisposable = new CompositeDisposable();

  private final BehaviorSubject<Lce> mItemsSubject = BehaviorSubject.create();
  private final PublishSubject<String> mQuerySubject = PublishSubject.create();
  private final PublishSubject<Item> mResultSubject = PublishSubject.create();
  private final PublishSubject<Boolean> mCancelSubject = PublishSubject.create();
  private final PublishSubject<Boolean> mDismissSubject = PublishSubject.create();

/////////////////////////////////////

  ////////////////////////////   RETURN LISTENERS   /////////////////////
  public Observable<Item> results() {
    return mResultSubject;
  }

  public Observable<Boolean> onCancel() {
    return mCancelSubject;
  }

  public Observable<Boolean> onDismiss() {
    return mDismissSubject;
  }
////////////////////////////      /////////////////////////////////////

  /**
   * Create an instance of {@link RxSearchList}.
   *
   * @param searchViewHint {@link CharSequence}, the SearchView's hint.
   * @param searchText {@link CharSequence}, the default query text.
   * @param showLogo {@link Boolean}, It determines visibility of list item's logo.
   * @param defaultTypeFace {@link Typeface}, the typeface for of all {@code TextView}
   * @return An instance of {@link RxSearchList} component.
   * @implNote <p><ui><li>No default query emmit when searchText be <code>{@code
   * null}</code>.</li></ui></p>
   */
  public static RxSearchList create(@Nullable CharSequence searchViewHint
      , @Nullable CharSequence searchText
      , boolean showLogo
      , @Nullable Typeface defaultTypeFace) {

    RxSearchList rxSearchList = new RxSearchList();
    rxSearchList.mSearchViewHint = searchViewHint;
    rxSearchList.mDefaultSearchText = searchText;
    rxSearchList.mShowLogo = showLogo;
    rxSearchList.mDefaultTypeFace = defaultTypeFace;

    return rxSearchList;
  }

  /**
   * @param visible the Visibility state of logo images
   * @return an instance of current {@link RxSearchList}
   */
  public RxSearchList setShowLogo(boolean visible) {
    this.mShowLogo = visible;
    return this;
  }

  /**
   * synonym of {@link #show(FragmentManager, String)}
   *
   * It just set {@code 'tag'} as default.Its better to use this method over the {@link
   * #show(FragmentManager, String)}
   */
  public RxSearchList show(FragmentManager manager) {
    show(manager, TAG);
    return this;
  }

  /**
   * please use {@link #show(FragmentManager)}
   *
   * @see #show(FragmentManager)
   * @deprecated
   */
  @Override
  @Deprecated
  public void show(FragmentManager manager, String tag) {
    if (isAdded()) {
      return;
    }

    if (manager.findFragmentByTag(tag) == null) {
      super.show(manager, tag);
    }
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(AppCompatDialogFragment.STYLE_NO_TITLE, R.style.Theme_AppCompat_Light_NoActionBar);
  }

  @Nullable
  @Override
  public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      @Nullable Bundle savedInstanceState) {

    D48 = getResources().getDimensionPixelOffset(R.dimen.rx_search_list_48_dp);
    isRTL = getResources().getBoolean(R.bool.rx_search_list_is_rtl);

    View rootView = inflater.inflate(R.layout.rx_search_list_activity, parent, false);
    initUi(rootView);

    return rootView;
  }

  private void initAdapter(Context context) {
    LayoutInflater inflater = LayoutInflater.from(context);
    adapter = new ListAdapter(mShowLogo
        , this::returnResult
        , inflater
        , mDefaultTypeFace);

    adapter.setHasStableIds(true);
    recyclerView.setAdapter(adapter);
  }

  private void initListView(Context context) {
    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
    recyclerView.setLayoutManager(linearLayoutManager);

    final int lvListOffset = context.getResources()
        .getDimensionPixelSize(R.dimen.rx_search_list_list_offset);
    final int lvListDivider = context.getResources()
        .getDimensionPixelSize(R.dimen.rx_search_list_list_divider);

    recyclerView.addItemDecoration(new StartOffsetItemDecoration(lvListOffset));
    recyclerView.addItemDecoration(new EndOffsetItemDecoration(lvListOffset));
    recyclerView.addItemDecoration(new DividerItemDecoration(lvListDivider, lvListDivider));
  }

  private void returnResult(Item item) {
    mResultSubject.onNext(item);

    dismissAllowingStateLoss();
  }

  private void hideSoftKeyboard(TextView searchView) {
    if (searchView != null) {
      InputMethodManager inputManager = (InputMethodManager)
          searchView.getContext().getSystemService(INPUT_METHOD_SERVICE);
      inputManager.hideSoftInputFromInputMethod(searchView.getWindowToken(), 0);
      inputManager.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);

      getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

      searchView.clearFocus();
      searchView.setCursorVisible(false);
      searchView.setSelected(false);
    }
  }

  public Observable<String> getQueryIntent() {
    return mQuerySubject;
  }

  @Override
  public void onStart() {
    super.onStart();
    tvMessage.setVisibility(View.GONE);

    mDisposable.add(
        mItemsSubject.switchMap(lce -> {
          // just change Lce data type for lce's that are instance of any of Loading,Error,Message
          if (lce.isLoading()) {
            return Observable.just(ViewState.Loading());

          } else if (lce.hasError()) {
            return Observable.just(ViewState.Error(lce.getError()));

          } else if (lce.getMessage() != null) {
            return Observable.just(ViewState.Message(lce.getMessage()));

          } else if (lce.getData() == null || lce.getData().size() == 0) {
            return Observable
                .just(ViewState.Message(getString(R.string.rx_search_list_list_is_empty)));
          }

          // because of we need later in adapter to highlight query strings,
          // so cast the query to lowercase to prevent redundant actions in library.
          return Observable
              .just(ViewState
                  .Data(new ViewModel(lce.getQuery().toLowerCase().split(SPACE), lce.getData())));

        })
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturnItem(ViewState.Error(getString(R.string.rx_search_list_unknown_error)))
            .subscribeOn(Schedulers.io())
            .subscribe(state -> {

              showContentLoading(state.isLoading());

              if (state.getError() != null) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(state.getError());
                return;
              } else {
                tvMessage.setVisibility(View.GONE);
              }

              if (state.getMessage() != null) {
                tvMessage.setVisibility(View.VISIBLE);
                tvMessage.setText(state.getMessage());
              } else {
                tvMessage.setVisibility(View.GONE);
              }

              final ViewModel data = state.getData();
              if (data != null) {
                adapter.setData(data.getData(), data.getQueries());
              }
            })
    );

    // emmit default search-text after binding observable
    if (mDefaultSearchText != null) {
      searchView.setText(mDefaultSearchText);
    }
  }

  private CharSequence defaultString(CharSequence query) {
    return query == null ? "" : query;
  }

  private String setButtonClearTextVisibilityAndReturnQuery(@NonNull String query) {
    btnClearText.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
    return query;
  }

  private void showContentLoading(boolean b) {
    if (b) {
      progressBar.show();
    } else {
      progressBar.hide();
    }
  }

  private void initUi(View root) {
    // find views
    searchView = root.findViewById(R.id.rx_search_list_search_view);
    btnClearText = root.findViewById(R.id.rx_search_list_btn_clear_search);
    recyclerView = root.findViewById(R.id.rx_search_list_list);
    progressBar = root.findViewById(R.id.rx_search_list_progressBar);
    tvMessage = root.findViewById(R.id.rx_search_list_tv_message);
    btnBack = root.findViewById(R.id.rx_search_list_btn_back);

    // set buttons icon
    btnBack.setImageDrawable(ContextCompat
        .getDrawable(getContext(), R.drawable.rx_search_list_ic_arrow_back_grey_600_24dp));
    btnBack.setTranslationX(isRTL ? D48 : -D48);
    btnClearText.setImageDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.rx_search_list_ic_close_grey_600_24dp));

    // apply typeface
    if (mDefaultTypeFace != null) {
      searchView.setTypeface(mDefaultTypeFace);
      tvMessage.setTypeface(mDefaultTypeFace);
    }

    // set default search-hint
    searchView.setHint(defaultString(mSearchViewHint));
    searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH | EditorInfo.IME_FLAG_NO_EXTRACT_UI);

    // apply in-animation to close/back button
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

    initListView(root.getContext());
    initAdapter(root.getContext());

    bindClicks();
    bindRx();
  }

  private void bindRx() {
    mDisposable.add(
        RxTextView.afterTextChangeEvents(searchView)
            .observeOn(AndroidSchedulers.mainThread())
            .map(event -> {
              final Editable editable = event.editable();
              if (editable == null) {
                return "";
              }
              return editable.toString();
            })
            .map(this::setButtonClearTextVisibilityAndReturnQuery)
            .observeOn(Schedulers.computation())
            .throttleWithTimeout(600, TimeUnit.MILLISECONDS, Schedulers.computation())
            .map(query -> query.replaceAll("\\s(\\s)+", SPACE))//replace all double spaces with one.
            .map(String::trim)
            .distinctUntilChanged()
            .onErrorReturnItem("")
            .subscribe(mQuerySubject::onNext)
    );
  }

  private void bindClicks() {
    mDisposable.add(
        RxView.clicks(btnBack)
            .compose(this::clickComposer)
            .subscribe(t -> {
              hideSoftKeyboard(searchView);
              mCancelSubject.onNext(true);

              btnBack.getViewTreeObserver()
                  .addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                      if (btnBack.getViewTreeObserver().isAlive()) {
                        btnBack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        float value = isRTL ? D48 : -D48;
                        ObjectAnimator animator = ObjectAnimator
                            .ofFloat(btnBack, "translationX", value);
                        animator.setInterpolator(new DecelerateInterpolator(.8F));
                        animator.setDuration(300);
                        animator.addListener(new AnimatorListenerAdapter() {
                          @Override
                          public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            dismissAllowingStateLoss();
                          }
                        });
                        animator.start();
                      }
                    }
                  });
            })
    );

    mDisposable.add(
        RxView.clicks(btnClearText)
            .filter(t -> searchView.getText() != null && searchView.getText().length() > 0)
            .subscribe(t -> searchView.setText(""))
    );
  }

  private Observable<Object> clickComposer(Observable<Object> upstream) {
    return upstream.throttleFirst(500, TimeUnit.MILLISECONDS);
  }

  public void setState(Lce itemsObservable) {
    mItemsSubject.onNext(itemsObservable);
  }

  @Override
  public void onPause() {
    hideSoftKeyboard(searchView);

    super.onPause();
    dismissAllowingStateLoss();
  }

  @Override
  public void onDestroyView() {
    try {
      mDisposable.clear();
      recyclerView.setAdapter(null);
    } catch (Exception e) {
      e.printStackTrace();
    }
    super.onDestroyView();
  }

  @Override
  public void onDismiss(DialogInterface dialog) {
    mDismissSubject.onNext(true);
    super.onDismiss(dialog);
  }

  public abstract static class Lce {

    public abstract boolean isLoading();

    public abstract boolean hasError();

    public abstract CharSequence getError();

    public abstract String getQuery();

    public abstract String getMessage();

    public abstract List<? extends Item> getData();


    public static Lce data(final String query, final List<? extends Item> data) {
      return new Lce() {
        @Override
        public boolean isLoading() {
          return false;
        }

        @Override
        public boolean hasError() {
          return false;
        }

        @Override
        public String getMessage() {
          return null;
        }

        @Override
        public CharSequence getError() {
          return null;
        }

        @Override
        public List<? extends Item> getData() {
          return data;
        }

        @Override
        public String getQuery() {
          return query;
        }
      };
    }

    public static Lce error(final CharSequence error) {
      return new Lce() {
        @Override
        public boolean isLoading() {
          return false;
        }

        @Override
        public boolean hasError() {
          return true;
        }

        @Override
        public CharSequence getError() {
          return error;
        }

        @Override
        public String getMessage() {
          return null;
        }

        @Override
        public List<? extends Item> getData() {
          return null;
        }

        @Override
        public String getQuery() {
          return null;
        }
      };
    }

    public static Lce loading() {
      return new Lce() {
        @Override
        public boolean isLoading() {
          return true;
        }

        @Override
        public boolean hasError() {
          return false;
        }

        @Override
        public CharSequence getError() {
          return null;
        }

        @Override
        public String getMessage() {
          return null;
        }

        @Override
        public List<? extends Item> getData() {
          return null;
        }

        @Override
        public String getQuery() {
          return null;
        }
      };
    }

    public static Lce message(String message) {
      return new Lce() {
        @Override
        public boolean isLoading() {
          return false;
        }

        @Override
        public boolean hasError() {
          return false;
        }

        @Override
        public CharSequence getError() {
          return null;
        }

        @Override
        public String getMessage() {
          return message;
        }

        @Override
        public List<? extends Item> getData() {
          return null;
        }

        @Override
        public String getQuery() {
          return null;
        }
      };
    }
  }
}
