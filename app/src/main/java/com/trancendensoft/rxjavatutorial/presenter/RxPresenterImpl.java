package com.trancendensoft.rxjavatutorial.presenter;
/**
 * Copyright 2017. Andrii Chernysh
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.trancendensoft.rxjavatutorial.view.ScreenOrientationView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * //TODO add class description
 *
 * @author Andrii Chernysh. E-mail: itcherry97@gmail.com
 *         Developed by <u>Transcendensoft</u>
 */

public class RxPresenterImpl extends BasePresenter<List<Long>, ScreenOrientationView> implements RxPresenter {
    private PublishSubject<Long> mPublishSubject = PublishSubject.create();

    @Override
    protected void updateView() {
        showData();
        if (mPublishSubject != null) {
            mPublishSubject.subscribe(observe());
        }
    }

    private void showData() {
        if (view() != null && model != null) {
            view().showValue(getStringFromIntegerList(model));
        }
    }

    private String getStringFromIntegerList(List<Long> values) {
        StringBuilder sb = new StringBuilder();
        for (Long value : values) {
            sb.append("Next value : ");
            sb.append(value);
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public void loadData() {
        Disposable dataDisposable = io.reactivex.Observable.intervalRange(1,10,1, 1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observe());

        addDisposable(dataDisposable);
    }

    public DisposableObserver<Long> observe() {
        return new DisposableObserver<Long>() {
            @Override
            protected void onStart() {
                super.onStart();
                view().showLoading();
                model = new ArrayList<>();
            }

            @Override
            public void onNext(Long v) {
                model.add(v);
                showData();
                mPublishSubject.onNext(v);
            }

            @Override
            public void onError(Throwable e) {
                view().showServerError();
            }

            @Override
            public void onComplete() {
                showData();
                view().showContent();
            }
        };
    }
}
