package com.trancendensoft.rxjavatutorial.api.manager;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.trancendensoft.rxjavatutorial.api.service.ApiService;
import com.trancendensoft.rxjavatutorial.entity.Geoname;
import com.trancendensoft.rxjavatutorial.entity.GeonamesResult;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * //TODO add class description 
 *
 * @author Andrii Chernysh. E-mail: itcherry97@gmail.com
 *         Developed by <u>Transcendensoft</u>
 */

public class ApiManager {
    private Retrofit mClient;
    private ApiService mService;

    private static final class Holder {
        static final ApiManager INSTANCE = new ApiManager();
    }

    private ApiManager() {
        initRetrofit();
        initService();
    }

    public static ApiManager getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Initialises Retrofit with a BASE_URL and GSON converter.
     */
    private void initRetrofit() {

        final String BASE_URL = "http://api.geonames.org/";

        /* It is good fit to use RxJava library in order
         to avoid low level programming with asynchronous tasks */

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        mClient = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    private void initService() {
        mService = mClient.create(ApiService.class);
    }

    /**
     * @return call instance for retrofit in order
     * to get list of geonames from the server
     */
    public Observable<Geoname> fetchCities(String cityNameBeginning, String maxRows) {
        String featureClass = "P";
        String username = "umypart";
        String style = "full";
        String lang = "ru";  //TODO change this when will have different localizations;

        return mService.fetchGeonames(cityNameBeginning, featureClass, username, style, maxRows, lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMapIterable(GeonamesResult::getGeonames);
    }
}
