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
[![](https://jitpack.io/v/mojtaba-shafaei/androidbadgebutton.svg)](https://jitpack.io/#mojtaba-shafaei/androidbadgebutton)

[![Android Arsenal]( https://img.shields.io/badge/Android%20Arsenal-AndroidBadgeButton-green.svg?style=flat )]( https://android-arsenal.com/details/1/7449 )

