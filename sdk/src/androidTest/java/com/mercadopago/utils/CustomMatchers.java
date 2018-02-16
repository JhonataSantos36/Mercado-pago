package com.mercadopago.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;

/**
 * Created by mreverter on 11/7/16.
 */
public class CustomMatchers {
    public static Matcher<View> withAnyChildText(final String text) {
        return new BoundedMatcher<View, ViewGroup>(ViewGroup.class) {

            List<String> textsFound = new ArrayList<>();

            @Override
            public boolean matchesSafely(ViewGroup view) {
                return anyChildMatches(view, text);
            }

            private boolean anyChildMatches(ViewGroup view, String text) {
                boolean textFound = false;
                for (int i = 0; i < view.getChildCount(); i++) {
                    View child = view.getChildAt(i);
                    if (child != null && !textFound) {
                        if (child instanceof TextView) {
                            String textViewInGroup = ((TextView) child).getText().toString();
                            if (textViewInGroup.equals(text)) {
                                textFound = true;
                            } else {
                                textsFound.add(textViewInGroup);
                            }
                        } else if (child instanceof ViewGroup) {
                            textFound = anyChildMatches((ViewGroup) child, text);
                        }
                    }
                }
                return textFound;
            }


            @Override
            public void describeTo(Description description) {
                description.appendText("Text: " + text + " not found");
                if (!textsFound.isEmpty()) {
                    description.appendText(", but some text were: ");
                }
                for (String s : textsFound) {
                    description.appendText(s + " - ");
                }
            }
        };
    }

    public static Matcher<View> withAnyChildImage(final Bitmap bitmap) {
        return new BoundedMatcher<View, ViewGroup>(ViewGroup.class) {
            @Override
            public boolean matchesSafely(ViewGroup view) {
                return anyChildMatches(view, bitmap);
            }

            private boolean anyChildMatches(ViewGroup view, Bitmap bitmap) {
                boolean imageFound = false;
                for (int i = 0; i < view.getChildCount(); i++) {
                    View child = view.getChildAt(i);
                    if (child != null && !imageFound) {
                        if (child instanceof ImageView) {
                            Bitmap viewImage = ((BitmapDrawable) ((ImageView) child).getDrawable()).getBitmap();
                            if (viewImage == bitmap) {
                                imageFound = true;
                            }
                        } else if (child instanceof ViewGroup) {
                            imageFound = anyChildMatches((ViewGroup) child, bitmap);
                        }
                    }
                }
                return imageFound;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with child text: ");
            }
        };
    }

    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
}
