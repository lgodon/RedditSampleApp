package com.leandrogodon.redditsampleapp.ui;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.leandrogodon.redditsampleapp.R;
import com.leandrogodon.redditsampleapp.model.Post;
import com.leandrogodon.redditsampleapp.settings.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    private final static int VISIBLE_THRESHOLD = 2;

    private List<Post> posts;
    private boolean loading;
    private PostProvider provider;

    public PostsAdapter(List<Post> posts, RecyclerView recyclerView, PostProvider provider) {
        this.posts = posts;
        this.provider = provider;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = linearLayoutManager.getItemCount();
                int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!loading && totalItemCount <= (lastVisibleItem + VISIBLE_THRESHOLD)) {
                    loading = true;
                    loadNextPage();
                }
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View mainLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_post, parent, false);

        ViewHolder vh = new ViewHolder(mainLayout);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPost(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void addAll(List<Post> newPosts) {
        posts.addAll(newPosts);
        loading = false;
    }

    private void loadNextPage() {
        provider.getNextPage(posts.get(posts.size() - 1).getFullname());
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView titleText;
        private TextView userText;
        private TextView dateText;
        private TextView commentText;
        private View thumbnailBkg;
        private ImageView thumbnailImage;

        public ViewHolder(View mainView) {
            super(mainView);
            this.titleText = (TextView) mainView.findViewById(R.id.post_title);
            this.userText = (TextView) mainView.findViewById(R.id.post_user);
            this.dateText = (TextView) mainView.findViewById(R.id.post_date);
            this.commentText = (TextView) mainView.findViewById(R.id.post_comments);
            this.thumbnailBkg = mainView.findViewById(R.id.post_thumbnail_bkg);
            this.thumbnailImage = (ImageView) mainView.findViewById(R.id.post_thumbnail);
        }

        public void setPost(final Post post) {
            titleText.setText(post.data.title);
            userText.setText("Posted by " + post.data.author);
            dateText.setText(
                    DateUtils.getRelativeTimeSpanString(
                            post.data.created * 1000,
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS, 0));
            commentText.setText(post.data.comments + " comments");
            if (post.hasThumbnail()) {
                thumbnailBkg.setVisibility(View.VISIBLE);
                thumbnailImage.setVisibility(View.VISIBLE);
                Picasso.with(thumbnailImage.getContext()).load(post.data.thumbnail).into(thumbnailImage); // TODO Center inside
                thumbnailImage.setOnClickListener(view -> {
                    if (post.getImageUrl() != null) {

                        int[] screenLocation = new int[2];
                        view.getLocationOnScreen(screenLocation);

                        Intent imageIntent = new Intent(view.getContext(), ImageActivity.class);
                        imageIntent.putExtra(Constants.INTENT_IMAGE_THUMBNAIL, post.data.thumbnail);
                        imageIntent.putExtra(Constants.INTENT_IMAGE_URL, post.getImageUrl());
                        imageIntent.putExtra(Constants.INTENT_TRANSITION_X, screenLocation[0]);
                        imageIntent.putExtra(Constants.INTENT_TRANSITION_Y, screenLocation[1]);
                        imageIntent.putExtra(Constants.INTENT_TRANSITION_WIDTH, view.getWidth());
                        imageIntent.putExtra(Constants.INTENT_TRANSITION_HEIGHT, view.getHeight());

                        view.getContext().startActivity(imageIntent);
                    }
                });
            } else {
                thumbnailBkg.setVisibility(View.GONE);
                thumbnailImage.setVisibility(View.GONE);
            }
        }
    }

    public interface PostProvider {
        void getNextPage(String fullnameFrom);
    }
}

