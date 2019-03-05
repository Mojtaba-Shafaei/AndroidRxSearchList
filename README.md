# AndroidRxSearchList

**ListView to show List of data and search inside it.**   
- List refresh as changing the query text.
- The changed texts(queries) emmit through an Observable<String>.


![sample AndroidToolbarBadgeButton image](https://github.com/Mojtaba-Shafaei/AndroidRxSearchList/blob/master/screenShots/Screenshot_01.png)  



## Methods
Base class name "RxSearchList"
- `public static RxSearchList create(...)`
- `public RxSearchList setShowLogo(boolean visible)`
- `public RxSearchList show(FragmentManager manager)`
- `public Observable<Item> results()`
- `public Observable<Boolean> onCancel()`
- `public Observable<Boolean> onDismiss()`
- `public void setState(Lce itemsObservable)`

## Download
[![](https://jitpack.io/v/Mojtaba-Shafaei/AndroidRxSearchList.svg)](https://jitpack.io/#Mojtaba-Shafaei/AndroidRxSearchList)



