package com.fragstack.controller;

import android.view.View;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.IntDef;
import androidx.annotation.StyleRes;
import androidx.fragment.app.FragmentTransaction;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


public class FragmentTransactionOptions {

    @IntDef({FragmentTransaction.TRANSIT_UNSET, FragmentTransaction.TRANSIT_NONE, FragmentTransaction.TRANSIT_FRAGMENT_OPEN,
            FragmentTransaction.TRANSIT_FRAGMENT_CLOSE, FragmentTransaction.TRANSIT_FRAGMENT_FADE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Transit {
    }

    @StyleRes
    public int transitionStyle = 0;

    @Transit
    public int transit = FragmentTransaction.TRANSIT_UNSET;

    public View sharedElement;

    public String name;

    @AnimatorRes
    @AnimRes
    public int enter = 0;

    @AnimatorRes
    @AnimRes
    public int exit = 0;

    @AnimatorRes
    @AnimRes
    public int popEnter = 0;

    @AnimatorRes
    @AnimRes
    public int popExit = 0;

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
