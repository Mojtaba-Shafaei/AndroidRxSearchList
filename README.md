# AndroidRxSearchList

**ListView to show List of data and search inside it.**   
- List refresh as changing the query text.
- The changed texts(queries) emmit through an Observable<String>.


![sample AndroidToolbarBadgeButton image](https://github.com/Mojtaba-Shafaei/AndroidRxSearchList/blob/master/screenShots/Screenshot_01.png)  

![sample AndroidToolbarBadgeButton image](https://github.com/Mojtaba-Shafaei/AndroidRxSearchList/blob/master/screenShots/gif1.gif)  


## Methods
Base class name "RxSearchList"
- `public static RxSearchList create(...)`
- `public RxSearchList setShowLogo(boolean visible)`
- `public RxSearchList show(FragmentManager manager)`
- `public RxSearchList showWithQuery(FragmentManager manager, String defaultQuery)`
- `public Observable<Item> results()`
- `public Observable<Boolean> onCancel()`
- `public Observable<Boolean> onDismiss()`
- `public void setState(Lce itemsObservable)`

## Sample
With using of RxBinding
- Declare an instance of "RxSearchList" in <code>onCreate()</code> method of your <code>Activity</code>.    
`rxSearchList = RxSearchList.create("the hint od search view", "the text of search view", false, null);`

- Bind click in <code>onStart()</code> method of your <code>Activity</code>.     
*Use filter() to prevent performing duplicate queries at the first launch.*

```java
RxView.clicks(btnShowList)
    .map(t -> rxSearchList.setShowLogo(true))
    .map(t -> rxSearchList.showWithQuery(getSupportFragmentManager(), "job with code = 20"))
    .switchMap(t -> Observable.just("").filter(ss -> true))
    .mergeWith(rxSearchList.getQueryIntent())
    .switchMap(DataMocker::getList) // get query results from API/Local
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(rxSearchList::setState)
```  

*Be careful to dispose above Disposable in <code>onPause()</code> method.*


## Download
[![](https://jitpack.io/v/Mojtaba-Shafaei/AndroidRxSearchList.svg)](https://jitpack.io/#Mojtaba-Shafaei/AndroidRxSearchList)



