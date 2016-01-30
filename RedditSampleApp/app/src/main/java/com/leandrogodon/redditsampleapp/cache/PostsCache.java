package com.leandrogodon.redditsampleapp.cache;

import com.google.gson.reflect.TypeToken;
import com.leandrogodon.redditsampleapp.model.Post;
import com.leandrogodon.redditsampleapp.rest.PostsService;
import com.leandrogodon.redditsampleapp.settings.Constants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Cache for Post objects
 */
public class PostsCache extends AbstractCache<Post> {

    private PostsService postsService;

    public PostsCache()  {
        postsService = new PostsService();
    }

    public Observable<List<Post>> refresh() {
        return postsService
                .getTopPosts()
                .map(posts -> {
                    clear();
                    if (!posts.isEmpty()) {
                        putAll(posts);
                        return pageFrom(0);
                    }
                    return posts;
                });
    }

    public Observable<List<Post>> getFirstPage() {
        if (cache.size() < Constants.PAGE_COUNT) {
            return refresh();
        }
        return Observable.just(pageFrom(0));
    }

    public Observable<List<Post>> getNextPage(String fullname) {
        int pos = findInCache(fullname);
        if (pos != -1 && cache.size() - pos >= Constants.PAGE_COUNT) {
            return Observable.just(pageFrom(pos));
        }

        return postsService
                .getTopPostsAfter(fullname)
                .map(posts -> {
                    putAll(posts);
                    return posts;
                });
    }

    @Override
    protected Type getType() {
        return new TypeToken<List<Post>>(){}.getType();
    }

    private List<Post> pageFrom (int position) {
       return new ArrayList<>(cache.subList(position, position + Constants.PAGE_COUNT));
    }

    private int findInCache(String fullname) {
        int i = 0;
        for (Post post : cache) {
            if (post.getFullname().equals(fullname)) {
                return i;
            }
            i++;
        }
        return -1;
    }
}
