package com.mojtaba_shafaei.android.lovSimple;

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
import android.support.annotation.UiThread;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.ProgressBar;
import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LovSimple extends AppCompatDialogFragment{

private final String TAG = "LovSimple";

public interface Item{

  String getCode();

  String getDes();

  CharSequence getLogo();

  int getPriority();

  void setPriority(int priority);

}

static Typeface TYPEFACE_IRANSANS_BOLD, TYPEFACE_IRANSANS_NORMAL;

//<editor-fold desc="bind ui">
private AppCompatEditText searchView;
private AppCompatImageButton btnClearText;
private RecyclerView recyclerView;
private ProgressBar progressBar;
private AppCompatTextView tvMessage;
private AppCompatImageButton btnBack;
//</editor-fold>

private LovSimpleAdapter adapter;
private final CompositeDisposable mDisposable = new CompositeDisposable();
/////////////////////////////////////
private CharSequence mSearchViewHint;
private CharSequence mDefaultSearchText;
private boolean mShowLogo;

private OnResultListener mOnResultListener;
private Dialog.OnCancelListener mOnCancelListener;
private Dialog.OnDismissListener mOnDismissListener;

private final PublishSubject<Lce<List<Item>>> mItemsSubject = PublishSubject.create();
private final PublishSubject<String> mQuerySubject = PublishSubject.create();

/////////////////////////////////////
//

/**
 * @param searchViewHint CharSequence that shown as EditText's hint.
 * @param searchText     CharSequence that shown as default query text.
 * @param showLogo       boolean. if {@code true} force component to show logos in every list item.<br/>  Enter {@code false} to hide logo ImageView.
 * @return An instance of "LovSimple" component.
 *
 * <pre>For example:</pre>
 * <pre>
 *   {@code
 *     LovSimple.create("Enter your desired company name...", "Google", true).show(getSupportFragmentManager());
 *   }
 * <pre/>
 */
public static LovSimple create(@Nullable CharSequence searchViewHint, @Nullable CharSequence searchText, boolean showLogo){

  LovSimple lovSimple = new LovSimple();
  lovSimple.mSearchViewHint = StringUtils.defaultIfBlank(searchViewHint);
  lovSimple.mDefaultSearchText = StringUtils.defaultIfBlank(searchText);
  lovSimple.mShowLogo = showLogo;

  return lovSimple;
}

public static LovSimple create(CharSequence searchViewHint, CharSequence searchText){
  return create(searchViewHint, searchText, false);
}

/**
 * synonym of {@link #show(FragmentManager, String)}
 *
 * It just set {@code 'tag'} as default.Its better to use this method over the {@link #show(FragmentManager, String)}
 */
public void show(FragmentManager manager){
  show(manager, TAG);
}

/**
 * please use {@link #show(FragmentManager)}
 *
 * @see #show(FragmentManager)
 */
@Override
public void show(FragmentManager manager, String tag){
  if(isAdded()){
    return;
  }

  if(manager.findFragmentByTag(tag) == null){
    super.show(manager, tag);
  }
}

@Override
public void onCreate(@Nullable Bundle savedInstanceState){
  super.onCreate(savedInstanceState);
  setStyle(DialogFragment.STYLE_NO_TITLE, R.style.ThemeOverlay_AppCompat_Light);
}

@Nullable
@Override
public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup __,
    @Nullable Bundle savedInstanceState){

  View view = inflater.inflate(R.layout.lov_simple_activity_all_lov, null);
  TYPEFACE_IRANSANS_BOLD = Typeface.createFromAsset(getResources().getAssets(), "IRANSansMobile_Bold.ttf");
  TYPEFACE_IRANSANS_NORMAL = Typeface.createFromAsset(getResources().getAssets(), "IRANSansMobile.ttf");

  initUi(view);

  searchView.setHint(mSearchViewHint);

  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(inflater.getContext());
  recyclerView.setLayoutManager(linearLayoutManager);
  recyclerView.setHasFixedSize(true);
  final int _8dp = getResources().getDimensionPixelSize(R.dimen.lov_simple_list_start_offset);
  recyclerView.addItemDecoration(new StartOffsetItemDecoration(_8dp));
  recyclerView.addItemDecoration(new EndOffsetItemDecoration(_8dp));
  recyclerView.addItemDecoration(new DividerItemDecoration(_8dp, _8dp));


  adapter = new LovSimpleAdapter(recyclerView,
                                 mShowLogo,
                                 (position, data) -> {
                                   try{
                                     onResult(data);
                                     dismissAllowingStateLoss();
                                   } catch(Exception e){
                                     Log.e(TAG, "onListItemClicked", e);
                                   }

                                 }, LayoutInflater.from(inflater.getContext()));

  adapter.setHasStableIds(true);

  searchView.setTypeface(TYPEFACE_IRANSANS_NORMAL);
  tvMessage.setTypeface(TYPEFACE_IRANSANS_NORMAL);
  tvMessage.setText(getString(R.string.lov_simple_no_data1p));
  recyclerView.setAdapter(adapter);

  btnBack.setOnClickListener(new OnClickListener(){
    @Override
    public void onClick(View v){
      onCancel(LovSimple.this.getDialog());
      hideSoftKeyboard(searchView);

      btnBack.getViewTreeObserver()
          .addOnGlobalLayoutListener(new OnGlobalLayoutListener(){
            @Override
            public void onGlobalLayout(){
              if(btnBack.getViewTreeObserver().isAlive()){
                btnBack.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float value =
                    ((View) btnBack.getParent()).getRight() - btnBack.getRight() + btnBack
                        .getWidth();
                ObjectAnimator animator = ObjectAnimator.ofFloat(btnBack, "translationX", value);
                animator.setInterpolator(new DecelerateInterpolator(.8F));
                animator.setDuration(300);
                animator.addListener(new AnimatorListenerAdapter(){
                  @Override
                  public void onAnimationEnd(Animator animation){
                    super.onAnimationEnd(animation);
                    dismissAllowingStateLoss();
                  }
                });
                animator.start();
              }
            }
          });
    }
  });

  btnClearText.setOnClickListener(v -> searchView.setText(""));

  RxTextView.afterTextChangeEvents(searchView)
      .observeOn(AndroidSchedulers.mainThread())
      .map(this::setButtonClearTextVisibilityAndReturnQuery)
      .observeOn(Schedulers.computation())
      .throttleWithTimeout(600, TimeUnit.MILLISECONDS, Schedulers.computation())
      .map(query -> StringUtils.replaceAll(query, "\\s(\\s)+", StringUtils.SPACE))//remove duplicate spaces
      .map(String::trim)
      .distinctUntilChanged()
      .onErrorReturnItem("")
      .subscribe(mQuerySubject)
  ;
  return view;
}

@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState){
  super.onActivityCreated(savedInstanceState);
  btnBack.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.lov_simple_ic_arrow_back_grey_600_24dp));
  btnClearText.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.lov_simple_ic_close_grey_600_24dp));
}

private void onResult(Item data){
  if(mOnResultListener != null){
    mOnResultListener.onResult(data);
  }
}

private void hideSoftKeyboard(AppCompatEditText searchView){
  if(searchView != null){
    InputMethodManager inputManager = (InputMethodManager)
        searchView.getContext().getSystemService(INPUT_METHOD_SERVICE);
    inputManager.hideSoftInputFromInputMethod(searchView.getWindowToken(), 0);
    inputManager.hideSoftInputFromWindow(searchView.getApplicationWindowToken(), 0);

    getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

    searchView.clearFocus();
    searchView.setSelected(false);
  }
}

public Observable<String> getQueryIntent(){
  return mQuerySubject.subscribeOn(Schedulers.io());
}

@Override
public void onStart(){
  super.onStart();
  tvMessage.setVisibility(View.GONE);
  try{
    render();
  } catch(Exception e){
    Log.e(TAG, "onCreate:RxSearch ", e);
  }

  searchView.setText(mDefaultSearchText);
}

private void render(){
  mDisposable.add(
      mItemsSubject.switchMap(it -> {
        if(it.isLoading()){
          return Observable.just(Lce.<QueryDataKeeper>loading());
        } else if(it.hasError()){
          return Observable.just(Lce.<QueryDataKeeper>error(it.getError()));
        }

        if(CollectionUtils.isEmpty(it.getData())){
          return Observable.just(Lce.<QueryDataKeeper>error(getString(R.string.lov_simple_no_data1p)));
        }

        //filter results base on query
        if(StringUtils.length(it.getQuery()) > 1){
          final String[] queries = StringUtils.split(it.getQuery(), StringUtils.SPACE);

          List<Item> results = new ArrayList<>();
          int priority;

          for(Item item : it.getData()){
            priority = 0;

            if(StringUtils.startsWithIgnoreCase(item.getDes(), it.getQuery())){
              priority++;
            }

            if(StringUtils.startsWithIgnoreCase(item.getDes(), it.getQuery() + ' ')){
              priority++;
            }

            if(StringUtils.equalsIgnoreCase(item.getDes(), it.getQuery())){
              priority++;
            }

            if(StringUtils.endsWithIgnoreCase(item.getDes(), it.getQuery())){
              priority++;
            }

            if(StringUtils.endsWithIgnoreCase(item.getDes(), ' ' + it.getQuery())){
              priority++;
            }

            if(StringUtils.containsIgnoreCase(item.getDes(), it.getQuery())){
              priority++;
            }

            for(String k : queries){
              priority += (StringUtils
                  .countMatches(item.getDes().toUpperCase(), StringUtils.wrap(k.toUpperCase(), StringUtils.SPACE)));
            }

            for(String k : queries){
              priority += (StringUtils.countMatches(item.getDes().toUpperCase(), k.toUpperCase()));
            }

            item.setPriority(priority);
            //Add item if it is desired one.
            if(priority > 0){
              results.add(item);
            }
          }
          return Observable.just(Lce.data(it.getQuery(), new QueryDataKeeper(queries, results)));
        } else{
          return Observable.just(Lce.data(it.getQuery(), new QueryDataKeeper(null, it.getData())));
        }
      })
          .observeOn(AndroidSchedulers.mainThread())
          .subscribeWith(new DisposableObserver<Lce<QueryDataKeeper>>(){
            @Override
            public void onNext(Lce<QueryDataKeeper> lce){
              if(isDisposed()){
                return;
              }
              if(BuildConfig.DEBUG){
                Log.d(TAG, "onNext: " + lce.toString());
              }
              try{
                tvMessage.setVisibility(View.GONE);
                showContentLoading(lce.isLoading());
                if(lce.hasError()){
                  showError(lce.getError());
                } else{
                  hideErrors();
                }

                final QueryDataKeeper queryDataKeeper = lce.getData();
                if(queryDataKeeper != null){
                  adapter.setHighlightFor(queryDataKeeper.queries);
                  adapter.setData(queryDataKeeper.data);

                  if(CollectionUtils.isEmpty(queryDataKeeper.data)){
                    tvMessage.setText(getString(R.string.lov_simple_no_data1p));
                    tvMessage.setVisibility(VISIBLE);
                  } else{
                    tvMessage.setVisibility(View.GONE);
                  }
                } else{
                  adapter.clearData();
                }
              } catch(Exception e){
                Log.e(TAG, "onNext: ", e);
              }
            }

            @Override
            public void onError(Throwable e){
              Log.e(TAG, "onError() called with: e = [" + e + "],isDisposed() = [" + isDisposed() + "]");
              if(isDisposed()){
                return;
              }
              showContentLoading(false);
              showInternetError();
            }

            @Override
            public void onComplete(){
            }
          })
  );
}

private String setButtonClearTextVisibilityAndReturnQuery(TextViewAfterTextChangeEvent event){
  btnClearText.setVisibility(StringUtils.isEmpty(event.editable()) ? View.GONE : View.VISIBLE);
  return StringUtils.defaultIfBlank(event.editable());
}

private void showContentLoading(boolean b){
  progressBar.setVisibility(b ? VISIBLE : View.GONE);
}

private void initUi(View root){
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
      .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
        @Override
        public void onGlobalLayout(){
          if(btnBack.getViewTreeObserver().isAlive()){
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

@UiThread
private void hideErrors(){
  tvMessage.setVisibility(View.GONE);
}

@UiThread
private void showInternetError(){
  tvMessage.setVisibility(VISIBLE);
  tvMessage.setText(getString(R.string.lov_simple_no_internet_connection));
}

@UiThread
private void showError(CharSequence error){
  tvMessage.setVisibility(VISIBLE);
  tvMessage.setText(error);
}

public LovSimple setOnResultListener(OnResultListener mOnResultListener){
  this.mOnResultListener = mOnResultListener;
  return this;
}

public LovSimple setOnCancelListener(OnCancelListener mOnCancelListener){
  this.mOnCancelListener = mOnCancelListener;
  return this;
}

public LovSimple setOnDismissListener(OnDismissListener mOnDismissListener){
  this.mOnDismissListener = mOnDismissListener;
  return this;
}

/**
 * Use this method to change LOV's data according to entered query text specially with online API.<br/>
 *
 * Just use this method or {@link #setState(Lce)}
 *
 * @see #setState(Lce)
 */
public void setState(Observable<Lce<List<Item>>> itemsObservable){
  itemsObservable.subscribe(mItemsSubject);
}

/**
 * @param lceItems an instance of {@code Lce<List<Item>>}.<br/>
 *
 *                 Use this method to initialize LOV's data once.<br/>
 *
 *                 Just use this method or {@link #setState(Observable)}
 * @see #setState(Observable)
 */
public void setState(Lce<List<Item>> lceItems){
  mItemsSubject.onNext(lceItems);
}

@Override
public void onPause(){
  hideSoftKeyboard(searchView);
  super.onPause();
  dismissAllowingStateLoss();
}

@Override
public void onDestroyView(){
  try{
    mOnResultListener = null;
    recyclerView.setAdapter(null);
    mDisposable.clear();
  } catch(Exception e){
    e.printStackTrace();
  }
  super.onDestroyView();
}

@Override
public void onDismiss(DialogInterface dialog){
  searchView.setFocusable(false);
  searchView.setCursorVisible(false);

  if(mOnDismissListener != null){
    mOnDismissListener.onDismiss(dialog);
  }
  super.onDismiss(dialog);
}

@Override
public void onCancel(DialogInterface dialog){
  if(mOnCancelListener != null){
    mOnCancelListener.onCancel(dialog);
  }
  super.onCancel(dialog);
}

public interface OnResultListener{

  void onResult(Item item);
}

private class QueryDataKeeper{

  String[] queries;
  List<Item> data;

  QueryDataKeeper(String[] queries, List<Item> data){
    this.queries = queries;
    this.data = data;
  }
}

public abstract static class Lce<T>{

  public abstract boolean isLoading();

  public abstract boolean hasError();

  public abstract CharSequence getError();

  public abstract String getQuery();

  public abstract T getData();

  @Override
  public String toString(){
    return new ToStringBuilder(this)
        .append("Loading", isLoading())
        .append("hasError", hasError())
        .append("error", getError())
        .append("data", (getData() instanceof List) ? "listSize=" + CollectionUtils.size(getData()) :
            (getData() instanceof QueryDataKeeper) ? CollectionUtils.size(((QueryDataKeeper) getData()).data) : getData())
        .toString();
  }

  public static <T> Lce<T> data(final String query, final T data){
    return new Lce<T>(){
      @Override
      public boolean isLoading(){
        return false;
      }

      @Override
      public boolean hasError(){
        return false;
      }

      @Override
      public CharSequence getError(){
        return null;
      }

      @Override
      public T getData(){
        return data;
      }

      @Override
      public String getQuery(){
        return query;
      }
    };
  }

  public static <T> Lce<T> error(final CharSequence error){
    return new Lce<T>(){
      @Override
      public boolean isLoading(){
        return false;
      }

      @Override
      public boolean hasError(){
        return true;
      }

      @Override
      public CharSequence getError(){
        return error;
      }

      @Override
      public T getData(){
        return null;
      }

      @Override
      public String getQuery(){
        return null;
      }
    };
  }

  public static <T> Lce<T> loading(){
    return new Lce<T>(){
      @Override
      public boolean isLoading(){
        return true;
      }

      @Override
      public boolean hasError(){
        return false;
      }

      @Override
      public CharSequence getError(){
        return null;
      }

      @Override
      public T getData(){
        return null;
      }

      @Override
      public String getQuery(){
        return null;
      }
    };
  }
}
}
