package com.leandrogodon.redditsampleapp.rest;

import android.util.Base64;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leandrogodon.redditsampleapp.Session;
import com.leandrogodon.redditsampleapp.settings.Constants;
import com.squareup.okhttp.Authenticator;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

import java.io.IOException;
import java.net.Proxy;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;

/**
 * Base class for all REST services
 */
public abstract class BaseService {

    private final static String LOG_TAG = "RetrofitService";

    protected final Gson gson;
    private CountDownLatch waitForToken;
    private AuthenticationService authenticationService;

    public BaseService() {
        gson = new GsonBuilder()
                .create();
    }

    protected <T> T buildApi(Class<T> apiClass) {
        OkHttpClient httpClient = new OkHttpClient();

        httpClient.setAuthenticator(new TokenAuthenticator());

        RestAdapter.Builder restAdapterBuilder = new RestAdapter.Builder()
                .setEndpoint(getEndpoint())
                .setClient(new OkClient(httpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .setErrorHandler((RetrofitError cause) -> {
                    Log.e(LOG_TAG, "Retrofit error: " + cause);
                    for (StackTraceElement st : cause.getStackTrace()) {
                        Log.e(LOG_TAG, "Retrofit error stacktrace: " + st);
                    }
                    return cause;
                });

        restAdapterBuilder.setRequestInterceptor(request -> {
            request.addHeader("Accept", "application/json");
            request.addHeader("User-Agent", " android:com.leandrogodon.sampleapp:v1.0.0");
            if (this instanceof AuthenticationService) {
                final String basic = "Basic " + Base64.encodeToString(Constants.APP_ID.getBytes(), Base64.NO_WRAP);
                request.addHeader("Authorization", basic);
            } else {
                request.addHeader("Authorization", "Bearer " + Session.getAccessToken());
            }
        });

        return restAdapterBuilder.build().create(apiClass);
    }

    protected abstract String getEndpoint();

    private class TokenAuthenticator implements Authenticator {

        private AtomicBoolean isRefreshingToken = new AtomicBoolean(false);

        @Override
        public Request authenticate(Proxy proxy, com.squareup.okhttp.Response response) throws IOException {
            // This method is called when the server returns a 401 not authorized code
            if (!isRefreshingToken.getAndSet(true)) {
                waitForToken = new CountDownLatch(1);
                if (authenticationService == null) {
                    authenticationService = new AuthenticationService();
                }
                authenticationService.refreshToken();
                isRefreshingToken.set(false);
                waitForToken.countDown();
                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + Session.getAccessToken())
                        .build();
            } else {
                if (waitForToken != null) {
                    try {
                        waitForToken.await();
                    } catch (InterruptedException e) {
                        // Nothing to do
                    }
                }
            }

            waitForToken = null;

            // Discard this request and have it retried
            return null;
        }

        @Override
        public Request authenticateProxy(Proxy proxy, com.squareup.okhttp.Response response) throws IOException {
            return null;
        }
    }
}
