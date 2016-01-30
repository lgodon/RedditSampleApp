package com.leandrogodon.redditsampleapp.rest;

import com.leandrogodon.redditsampleapp.model.Post;
import com.leandrogodon.redditsampleapp.settings.Constants;

import java.util.List;

import rx.Observable;

public class PostsService extends BaseService {

    private PostsApi postsApi;

    public PostsService() {
        postsApi = buildApi(PostsApi.class);
    }

    public Observable<List<Post>> getTopPosts() {
        return postsApi.getTopPosts(Constants.TOP_PERIOD, Constants.PAGE_COUNT).
                map(listing -> listing.data.children);
    }

    public Observable<List<Post>> getTopPostsAfter(String fullname) {
        return postsApi.getTopPostsAfter(Constants.TOP_PERIOD, Constants.PAGE_COUNT, fullname).
                map(listing -> listing.data.children);
    }

    @Override
    protected String getEndpoint() {
        return Constants.API_ENDPOINT;
    }
}
