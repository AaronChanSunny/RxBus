# RxBus

一个基于 RxJava 的 EventBus 库。

## 1 基本用法

### 1.1 订阅

- 自定义订阅方式

```
 Subscription subscription = RxBus.INSTANCE
         .toObservable(Integer.class)
         .doOnUnsubscribe(new Action0() {
             @Override
             public void call() {
                 Log.d(TAG, "RxBus unregistered.");
             }
         })
         .subscribe(new Action1<Integer>() {
             @Override
             public void call(Integer integer) {
                 textView.setText("Count refreshed: " + mCount);
             }
         });
 RxBus.INSTANCE.addSubscription(this, subscription);
```

- 默认订阅方式

```
Subscription subscription = RxBus.INSTANCE.doSubscribe(Integer.class, new Action1<Integer>() {
    @Override
    public void call(Integer count) {
        textView.setText("Count refreshed: " + mCount);
    }
}, new Action1<Throwable>() {
    @Override
    public void call(Throwable ignore) {
    }
});
```

- 简洁订阅方式

```
Subscription subscription = RxBus.INSTANCE.doSubscribe(Integer.class, new Action1<Integer>() {
    @Override
    public void call(Integer count) {
        textView.setText("Count refreshed: " + mCount);
    }
});
```

### 1.2 发送事件

```
RxBus.INSTANCE.post(mCount);
```

### 1.3 反订阅

```
@Override
protected void onDestroy() {
    super.onDestroy();
    RxBus.INSTANCE.unSubscribe(this);
}
```

## 2 参考

- [Android 用RxJava模拟一个EventBus ———RxBus](http://www.jianshu.com/p/3a3462535b4d)
- [Implementing an Event Bus With RxJava - RxBus](http://blog.kaush.co/2014/12/24/implementing-an-event-bus-with-rxjava-rxbus/)
