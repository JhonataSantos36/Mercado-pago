package com.mercadopago.util;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.mercadopago.R;
import com.mercadopago.uicontrollers.card.BackCardView;
import com.mercadopago.uicontrollers.card.CardView;
import com.mercadopago.uicontrollers.card.FrontCardView;
import com.mercadopago.uicontrollers.card.IdentificationCardView;

/**
 * Created by vaserber on 6/22/16.
 */
public class MPAnimationUtils {

    public static final int ANIMATION_EXTRA_FACTOR = 3;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void fadeInLollipop(final int color, final ImageView imageView, final Context context) {

        imageView.post(new Runnable() {

            @Override
            public void run() {
                imageView.setColorFilter(ContextCompat.getColor(context, color),
                        PorterDuff.Mode.SRC_ATOP);

                int width = imageView.getWidth();

                Animator anim = ViewAnimationUtils.createCircularReveal(imageView, -width, 0,
                        width, ANIMATION_EXTRA_FACTOR * width);
                anim.setDuration(300);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.start();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void fadeOutLollipop(final int color, final ImageView imageView, final Context context) {
        imageView.post(new Runnable() {

            @Override
            public void run() {

                int width = imageView.getWidth();

                Animator anim = ViewAnimationUtils.createCircularReveal(imageView, -width, 0,
                        ANIMATION_EXTRA_FACTOR * width, width);
                anim.setDuration(300);
                anim.setInterpolator(new AccelerateDecelerateInterpolator());
                anim.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        imageView.setColorFilter(ContextCompat.getColor(context, color),
                                PorterDuff.Mode.SRC_ATOP);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                anim.start();
            }
        });

    }

    public static void fadeIn(final int color, final ImageView imageView, final Context context) {
        imageView.post(new Runnable() {

            @Override
            public void run() {
                final Animation mAnimFadeIn = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.mpsdk_fade_in);
                mAnimFadeIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageView.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.setBackgroundColor(ContextCompat.getColor(context, color));
                imageView.startAnimation(mAnimFadeIn);
            }
        });
    }

    public static void fadeOut(final int color, final ImageView imageView, final Context context) {
        imageView.post(new Runnable() {

            @Override
            public void run() {
                Animation mAnimFadeOut = android.view.animation.AnimationUtils.loadAnimation(context, R.anim.mpsdk_fade_out);
                mAnimFadeOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        imageView.setBackgroundColor(ContextCompat.getColor(context, color));
                        imageView.clearAnimation();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                imageView.startAnimation(mAnimFadeOut);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setImageViewColorLollipop(ImageView imageView, Context context, int color) {
        imageView.setColorFilter(ContextCompat.getColor(context, color),
                PorterDuff.Mode.SRC_ATOP);
    }

    public static void setImageViewColor(ImageView imageView, Context context, int color) {
        imageView.setBackgroundColor(ContextCompat.getColor(context, color));
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void flipToBack(Context context, float cameraDistance, final View frontView, final View backView,
                                  final BackCardView backCardView) {

        AnimatorSet animFront = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.mpsdk_card_flip_left_out);
        AnimatorSet animBack = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.mpsdk_card_flip_right_in);

        frontView.setCameraDistance(cameraDistance);
        animFront.setTarget(frontView);
        frontView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        backView.setCameraDistance(cameraDistance);
        animBack.setTarget(backView);
        backView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        animFront.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setAlpha(0);
                backView.setAlpha(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animFront.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });

        animBack.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (backCardView != null) {
                    backCardView.show();
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                backView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });
        animFront.start();
        animBack.start();
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static void flipToFront(Context context, float cameraDistance, final View frontView, final View backView) {

        AnimatorSet animFront = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.mpsdk_card_flip_left_in);
        AnimatorSet animBack = (AnimatorSet) AnimatorInflater.loadAnimator(context, R.animator.mpsdk_card_flip_right_out);

        frontView.setCameraDistance(cameraDistance);
        animFront.setTarget(frontView);
        frontView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        backView.setCameraDistance(cameraDistance);
        animBack.setTarget(backView);
        backView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        animBack.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                backView.setAlpha(0);
                frontView.setAlpha(1.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animFront.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                frontView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });

        animBack.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                backView.setLayerType(View.LAYER_TYPE_NONE, null);
            }
        });

        animBack.start();
        animFront.start();
    }

    public static void flipToBack(Context context, final FrontCardView frontCardView, BackCardView backCardView) {
        Animation animBack = AnimationUtils.loadAnimation(context, R.anim.mpsdk_from_middle_left);
        Animation animFront = AnimationUtils.loadAnimation(context, R.anim.mpsdk_to_middle_left);

        frontCardView.getView().startAnimation(animFront);

        animFront.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                frontCardView.hide();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        backCardView.getView().startAnimation(animBack);
        backCardView.show();
    }

    public static void flipToFront(Context context, final FrontCardView frontCardView, final BackCardView backCardView) {
        final Animation animFront = AnimationUtils.loadAnimation(context, R.anim.mpsdk_from_middle_left);
        final Animation animBack = AnimationUtils.loadAnimation(context, R.anim.mpsdk_to_middle_left);

        backCardView.getView().startAnimation(animBack);

        animBack.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                backCardView.hide();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        frontCardView.getView().startAnimation(animFront);
        frontCardView.show();
    }

    public static void transitionCardAppear(Context context, final CardView cardView, IdentificationCardView identificationCardView) {

        Animation animAppear = AnimationUtils.loadAnimation(context, R.anim.mpsdk_appear_from_right);
        Animation animDisappear = AnimationUtils.loadAnimation(context, R.anim.mpsdk_dissapear_to_left);

        cardView.getView().startAnimation(animDisappear);

        animAppear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                cardView.hide();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        identificationCardView.getView().startAnimation(animAppear);
        identificationCardView.show();
    }

    public static void transitionCardDisappear(Context context, final CardView cardView, final IdentificationCardView identificationCardView) {

        Animation animAppear = AnimationUtils.loadAnimation(context, R.anim.mpsdk_appear_from_left);
        Animation animDisappear = AnimationUtils.loadAnimation(context, R.anim.mpsdk_dissapear_to_right);

        identificationCardView.getView().startAnimation(animDisappear);

        animAppear.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                identificationCardView.hide();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        cardView.getView().startAnimation(animAppear);
        cardView.show();
    }

}
