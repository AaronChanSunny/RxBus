package me.aaron.rxbus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import me.aaron.library.RxBus;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private int mCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textView = (TextView) findViewById(R.id.tv_content);
        findViewById(R.id.btn_emit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RxBus.INSTANCE.post(mCount);
                mCount++;
            }
        });

//        Subscription subscription = RxBus.INSTANCE.doSubscribe(Integer.class, new Action1<Integer>() {
//            @Override
//            public void call(Integer count) {
//                textView.setText("Count refreshed: " + mCount);
//            }
//        });
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.INSTANCE.unSubscribe(this);
    }

}
