package com.leandrogodon.redditsampleapp.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Persist Caches to disk
 */
public class PersistenceManager {

    private final static String SHARED_PREFERENCES_CACHE = "storage";

    private static SharedPreferences storage;
    private static Gson gson;

    public static void initialize(Context context) {
        storage = context.getSharedPreferences(SHARED_PREFERENCES_CACHE, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    protected static void persistAll(AbstractCache cache) {
        SharedPreferences.Editor e = storage.edit();
        e.putString(cache.getClass().getSimpleName(), gson.toJson(cache.cache));
        e.commit();
    }

    protected static void readAll(AbstractCache cache, Type type) {
        String stored = storage.getString(cache.getClass().getSimpleName(), null);
        List<?> storedObjects = gson.fromJson(stored, type);
        cache.clear();
        cache.putAll(storedObjects);
    }

    protected static void clear(AbstractCache cache) {
        SharedPreferences.Editor e = storage.edit();
        e.putString(cache.getClass().getSimpleName(), null);
        e.commit();
    }
}
