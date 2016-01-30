package com.leandrogodon.redditsampleapp;

import android.app.Application;

import com.leandrogodon.redditsampleapp.cache.PersistenceManager;
import com.leandrogodon.redditsampleapp.cache.PostsCache;

public class RedditSampleApp extends Application {

    private PostsCache postsCache;

    @Override
    public void onCreate() {
        super.onCreate();

        PersistenceManager.initialize(this);

        postsCache = new PostsCache();
    }

    public PostsCache getPostsCache() {
        return postsCache;
    }
}
