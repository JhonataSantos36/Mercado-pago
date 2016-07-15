package com.mercadopago.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;

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
                for(int i=0; i < view.getChildCount(); i++) {
                    View child = view.getChildAt(i);
                    if(child != null && !textFound) {
                        if (child instanceof TextView) {
                            String textViewInGroup = ((TextView) child).getText().toString();
                            if(textViewInGroup.equals(text)) {
                                textFound = true;
                            }
                            else {
                                textsFound.add(textViewInGroup);
                            }
                        }
                        else if(child instanceof ViewGroup) {
                            textFound = anyChildMatches((ViewGroup)child, text);
                        }
                    }
                }
                return textFound;
            }


            @Override
            public void describeTo(Description description) {
                description.appendText("Text: " + text + " not found");
                if(!textsFound.isEmpty()) {
                    description.appendText(", but some text were: ");
                }
                for(String s : textsFound) {
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
                for(int i=0; i < view.getChildCount(); i++) {
                    View child = view.getChildAt(i);
                    if(child != null && !imageFound) {
                        if (child instanceof ImageView) {
                            Bitmap viewImage = ((BitmapDrawable) ((ImageView) child).getDrawable()).getBitmap();
                            if(viewImage == bitmap) {
                                imageFound = true;
                            }
                        }
                        else if(child instanceof ViewGroup) {
                            imageFound = anyChildMatches((ViewGroup)child, bitmap);
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
}
