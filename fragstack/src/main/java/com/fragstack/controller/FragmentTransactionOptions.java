package com.fragstack.controller;

import android.support.annotation.AnimRes;
import android.support.annotation.AnimatorRes;
import android.support.annotation.IntDef;
import android.support.annotation.StyleRes;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class FragmentTransactionOptions {

    @IntDef({FragmentTransaction.TRANSIT_NONE, FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE, FragmentTransaction.TRANSIT_FRAGMENT_FADE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Transit {
    }

    @StyleRes
    int transitionStyle;

    @Transit
    int transit;

    View sharedElement;

    String name;

    @AnimatorRes
    @AnimRes
    int enter;

    @AnimatorRes
    @AnimRes
    int exit;

    @AnimatorRes
    @AnimRes
    int popEnter;

    @AnimatorRes
    @AnimRes
    int popExit;

    private FragmentTransactionOptions(Builder builder) {
        this.transitionStyle = builder.transitionStyle;
        this.transit = builder.transit;
        this.sharedElement = builder.sharedElement;
        this.name = builder.name;
        this.enter = builder.enter;
        this.exit = builder.exit;
        this.popEnter = builder.popEnter;
        this.popExit = builder.popExit;
    }

    public static class Builder {
        @StyleRes
        int transitionStyle;

        @Transit
        int transit;

        View sharedElement;

        String name;

        @AnimatorRes
        @AnimRes
        int enter;

        @AnimatorRes
        @AnimRes
        int exit;

        @AnimatorRes
        @AnimRes
        int popEnter;

        @AnimatorRes
        @AnimRes
        int popExit;

        public Builder setTransitionStyle(@StyleRes int transitionStyle) {
            this.transitionStyle = transitionStyle;
            return this;
        }

        public Builder setTransition(int transit) {
            this.transit = transit;
            return this;
        }

        public Builder addSharedElement(View sharedElement, String name) {
            this.sharedElement = sharedElement;
            this.name = name;
            return this;
        }

        public Builder setCustomAnimations(@AnimatorRes @AnimRes int enter, @AnimatorRes @AnimRes int exit,
                                           @AnimatorRes @AnimRes int popEnter, @AnimatorRes @AnimRes int popExit) {
            this.enter = enter;
            this.exit = exit;
            this.popEnter = popEnter;
            this.popExit = popExit;
            return this;
        }

        public Builder setCustomAnimations(@AnimatorRes @AnimRes int enter, @AnimatorRes @AnimRes int exit) {
            this.exit = exit;
            this.enter = enter;
            return this;
        }

        public FragmentTransactionOptions build() {
            return new FragmentTransactionOptions(this);
        }
    }
}
