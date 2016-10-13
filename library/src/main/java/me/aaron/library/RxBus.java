package me.aaron.library;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Chenll on 2016/10/12.
 */

public enum RxBus {

    INSTANCE;

    private Subject<Object, Object> mSubject;
    private Map<String, CompositeSubscription> mSubscriptionMap;

    RxBus() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
        mSubscriptionMap = new HashMap<>();
    }

    /**
     * 发送事件
     *
     * @param event 事件
     */
    public void post(Object event) {
        mSubject.onNext(event);
    }

    /**
     * 返回指定类型的Obs实例
     *
     * @param eventType 事件类型
     * @param <T>       泛型
     * @return Obs
     */
    public <T> Observable<T> toObservable(Class<T> eventType) {
        return mSubject.ofType(eventType);
    }

    /**
     * 是否已有观察者订阅
     *
     * @return 是否
     */
    public boolean hasObservers() {
        return mSubject.hasObservers();
    }

    /**
     * 默认的订阅方法
     * @param type 事件类型
     * @param next onNext
     * @param error onError
     * @param <T> 泛型
     * @return Subscription
     */
    public <T> Subscription doSubscribe(Class<T> type, Action1<T> next, Action1<Throwable> error) {
        return toObservable(type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(next, error);
    }

    /**
     * 忽略异常的订阅方法
     * @param type 事件类型
     * @param next onNext
     * @param <T> 泛型
     * @return Subscription
     */
    public <T> Subscription doSubscribe(Class<T> type, Action1<T> next) {
        return doSubscribe(type, next, new Action1<Throwable>() {
            @Override
            public void call(Throwable ignore) {
            }
        });
    }

    /**
     * 保存订阅后的Subscription
     * @param o 订阅时所处的类
     * @param subscription subs
     */
    public void addSubscription(Object o, Subscription subscription) {
        String key = o.getClass().getName();
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).add(subscription);
        } else {
            CompositeSubscription compositeSubscription = new CompositeSubscription();
            compositeSubscription.add(subscription);
            mSubscriptionMap.put(key, compositeSubscription);
        }
    }

    /**
     * 取消订阅
     * @param o 订阅时所处的类
     */
    public void unSubscribe(Object o) {
        String key = o.getClass().getName();
        if (!mSubscriptionMap.containsKey(key)) {
            return;
        }
        if (mSubscriptionMap.get(key) != null) {
            mSubscriptionMap.get(key).unsubscribe();
        }
        mSubscriptionMap.remove(key);
    }

}
