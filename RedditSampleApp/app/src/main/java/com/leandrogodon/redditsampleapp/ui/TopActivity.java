package com.leandrogodon.redditsampleapp.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.leandrogodon.redditsampleapp.R;
import com.leandrogodon.redditsampleapp.RedditSampleApp;
import com.leandrogodon.redditsampleapp.model.Post;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;

/**
 * Main activity of the application.
 *
 * Shows a RecyclerView with the "top" Reddit posts for the day
 */
public class TopActivity extends Activity implements PostsAdapter.PostProvider {

    private PostsAdapter postsAdapter;
    private RecyclerView postsRecyclerView;
    private RecyclerView.LayoutManager postsLayoutManager;

    @Override
    public void getNextPage(String fullnameFrom) {
        getApp().getPostsCache().getNextPage(fullnameFrom)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        newPosts -> {
                            postsAdapter.addAll(newPosts);
                            postsAdapter.notifyDataSetChanged();
                        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_top);

        postsRecyclerView = (RecyclerView) findViewById(R.id.posts);
        postsRecyclerView.setHasFixedSize(true);

        postsLayoutManager = new LinearLayoutManager(this);
        postsRecyclerView.setLayoutManager(postsLayoutManager);

        final SwipeRefreshLayout refreshLayout = (SwipeRefreshLayout) findViewById(R.id.posts_refresh_layout);
        refreshLayout.setOnRefreshListener(() -> {
                getApp().getPostsCache().refresh()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(posts -> {
                            refreshLayout.setRefreshing(false);
                            postsAdapter = null;
                            populateList(posts);
                        });
        });

        getApp().getPostsCache().getFirstPage()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(posts -> populateList(posts));
    }

    private void populateList(List<Post> posts) {
        if (postsAdapter == null) {
            postsAdapter = new PostsAdapter(posts, postsRecyclerView, this);
        }
        postsRecyclerView.setAdapter(postsAdapter);
    }

    private RedditSampleApp getApp() {
        return (RedditSampleApp) getApplication();
    }
}
