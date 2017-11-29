package com.trancendensoft.rxjavatutorial.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.trancendensoft.rxjavatutorial.R;
import com.trancendensoft.rxjavatutorial.api.manager.ApiManager;
import com.trancendensoft.rxjavatutorial.entity.Coordinate;
import com.trancendensoft.rxjavatutorial.entity.Geoname;
import com.trancendensoft.rxjavatutorial.entity.Shop;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    private Disposable geonamesDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLoading();

        Observable<Geoname> fetchGeonamesObservable =
                ApiManager.getInstance().fetchCities("Kie", "30");

        geonamesDisposable = fetchGeonamesObservable.subscribeWith(getGeonamesSubscriber());

        flatVersusConcatMap();
        groupByExample();
    }

    private void initLoading() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Loading...");
    }

    private void showLoading() {
        mProgressDialog.show();
    }

    private void hideLoading() {
        mProgressDialog.dismiss();
    }

    private DisposableObserver<Geoname> getGeonamesSubscriber() {
        return new DisposableObserver<Geoname>() {
            @Override
            public void onNext(Geoname geoname) {
                Log.d("TAG", geoname.getName());
            }

            @Override
            public void onError(Throwable t) {
                hideLoading();
                Log.d("TAG", t.getMessage());
            }

            @Override
            public void onComplete() {
                hideLoading();
                Log.d("TAG", "Completed!!");
            }

            @Override
            protected void onStart() {
                super.onStart();
                showLoading();
            }
        };
    }

    private void flatVersusConcatMap() {
        Observable.range(1, 15)
                .concatMap(value -> {
                    final int delay = new Random().nextInt(10);
                    return Observable.just(value * value).delay(delay, TimeUnit.SECONDS);
                })
                .subscribeOn(Schedulers.computation())
                .subscribe(value -> Log.d("TAG", "Squared value : " + value));
    }

    private void groupByExample() {
        String[] cursor1row = {"1", "Foo", "lat1-1", "long1-1"};
        String[] cursor2row = {"1", "Foo", "lat1-2", "long1-2"};
        String[] cursor3row = {"2", "Bar", "lat2-1", "long2-1"};
        String[] cursor4row = {"1", "Foo", "lat1-3", "long1-3"};
        String[] cursor5row = {"1", "Foo", "lat1-4", "long1-4"};
        String[] cursor6row = {"3", "Ping", "lat3-1", "long3-1"};
        String[] cursor7row = {"2", "Bar", "lat2-2", "long2-2"};

        List<String[]> cursor = Arrays.asList(cursor1row, cursor2row, cursor3row, cursor4row, cursor5row, cursor6row, cursor7row);

        Observable.fromIterable(cursor)
                .groupBy(cursorRow -> cursorRow[0])
                .flatMap(groups -> groups.collect(Shop::new, (shop, rows) -> {
                    shop.id = Integer.parseInt(rows[0]);
                    shop.name = rows[1];
                    shop.coordinates.add(new Coordinate(rows[2], rows[3]));
                }).toObservable())
                .subscribeOn(Schedulers.io())
                .subscribe(shop ->Log.d("RX",shop.toString()));
}


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!geonamesDisposable.isDisposed()) {
            geonamesDisposable.dispose();
        }
    }
}
