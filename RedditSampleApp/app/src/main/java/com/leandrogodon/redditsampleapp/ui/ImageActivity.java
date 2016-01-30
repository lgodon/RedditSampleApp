package com.leandrogodon.redditsampleapp.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.leandrogodon.redditsampleapp.R;
import com.leandrogodon.redditsampleapp.settings.Constants;
import com.memtrip.CapturePhotoUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Show an image in fullscreen.
 * The activity transitions in from a thumbnail.
 */
public class ImageActivity extends Activity {

    private ImageView thumbnailView;
    private ImageView fullsizeView;
    private FloatingActionButton floatingActionButton;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String thumbnailUrl = getIntent().getStringExtra(Constants.INTENT_IMAGE_THUMBNAIL);
        imageUrl = getIntent().getStringExtra(Constants.INTENT_IMAGE_URL);
        if (thumbnailUrl == null || imageUrl == null) {
            Toast.makeText(this, "Internal error occurred", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_image);

        thumbnailView = (ImageView) findViewById(R.id.thumbnail_image);
        fullsizeView = (ImageView) findViewById(R.id.big_image);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.save_button);

        floatingActionButton.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle(R.string.save_image_title)
                        .setMessage(R.string.save_image_prompt)
                        .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> saveImageToGallery())
                        .setNegativeButton(android.R.string.no, null)
                        .show());

        Picasso.with(this).load(thumbnailUrl).into(thumbnailView);
        runAnimation(savedInstanceState, thumbnailView);
    }

    /**
     * Set up a transition animation for this activity
     *
     * @param savedInstanceState To know if the animation should trigger
     * @param animatedView The view that will be animated from the thumbnail coords to fullscreen
     */
    private void runAnimation(Bundle savedInstanceState, final View animatedView) {
        // Get starting coords from animation from bundle
        final int thumbnailLeft = getIntent().getIntExtra(Constants.INTENT_TRANSITION_X, 0);
        final int thumbnailTop = getIntent().getIntExtra(Constants.INTENT_TRANSITION_Y, 0);
        final int thumbnailWidth = getIntent().getIntExtra(Constants.INTENT_TRANSITION_WIDTH, 0);
        final int thumbnailHeight = getIntent().getIntExtra(Constants.INTENT_TRANSITION_HEIGHT, 0);

        // Only run the animation if we're coming from the parent activity, not if
        // we're recreated automatically by the window manager (e.g., device rotation)
        if (savedInstanceState == null) {
            ViewTreeObserver observer = animatedView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    animatedView.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    animatedView.getLocationOnScreen(screenLocation);
                    int leftDelta = thumbnailLeft - screenLocation[0];
                    int topDelta = thumbnailTop - screenLocation[1];

                    float widthScale = (float) thumbnailWidth / animatedView.getWidth();
                    float heightScale = (float) thumbnailHeight / animatedView.getHeight();

                    // Set starting values for properties we're going to animate. These
                    // values scale and position the full size version down to the thumbnail
                    // size/location, from which we'll animate it back up
                    animatedView.setPivotX(0);
                    animatedView.setPivotY(0);
                    animatedView.setScaleX(widthScale);
                    animatedView.setScaleY(heightScale);
                    animatedView.setTranslationX(leftDelta);
                    animatedView.setTranslationY(topDelta);

                    // Animate scale and translation to go from thumbnail to full size
                    ViewPropertyAnimator imageAnimator = animatedView.animate();

                    imageAnimator.setDuration(Constants.IMAGE_TRANSITION_TIME).
                            scaleX(1).scaleY(1).
                            translationX(0).translationY(0);

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                        imageAnimator.setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                animationEnded();
                            }
                        });
                    } else {
                        imageAnimator.withEndAction(() -> animationEnded());
                    }

                    int colorFrom = Color.TRANSPARENT;
                    int colorTo = Color.BLACK;
                    View mainView = findViewById(android.R.id.content);
                    ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                    colorAnimation.setDuration(Constants.IMAGE_TRANSITION_TIME);
                    colorAnimation.addUpdateListener(
                            animator -> mainView.setBackgroundColor((int) animator.getAnimatedValue()));
                    colorAnimation.start();

                    return true;
                }
            });
        }
    }

    private void animationEnded() {
        fullsizeView.setVisibility(View.VISIBLE);
        Picasso.with(this).load(imageUrl).into(fullsizeView, new Callback() {
            @Override
            public void onSuccess() {
                thumbnailView.setVisibility(View.GONE);
                floatingActionButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError() {}
        });
    }

    private void saveImageToGallery() {
        Toast.makeText(this, R.string.save_image_message, Toast.LENGTH_SHORT).show();
        Bitmap bm = ((BitmapDrawable) fullsizeView.getDrawable()).getBitmap();
        CapturePhotoUtils.insertImage(
                getContentResolver(),
                ((BitmapDrawable) fullsizeView.getDrawable()).getBitmap(),
                getString(R.string.save_image_image_title),
                "Description"); // TODO Get from post
    }
}
