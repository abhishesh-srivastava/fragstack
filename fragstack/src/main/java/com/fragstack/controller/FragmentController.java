package com.fragstack.controller;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import com.fragstack.contracts.StackableFragment;
import com.fragstack.fragments.ContainerFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class FragmentController {
    private int mContainerId;
    private Stack<String> mTagsStack = new Stack<>();
    private Map<String, Fragment> mRootFragments = new HashMap<>();
    private Map<String, Fragment.SavedState> mSavedStates = new HashMap<>();
    private FragmentManager mFragmentManager;
    private FragmentTransactionListener mFragmentTransactionListener;
    private static final int CMD_ADD = 1;
    private static final int CMD_REPLACE = 2;
    private static final int CMD_REMOVE = 3;
    private static final int CMD_HIDE = 4;
    private static final int CMD_SHOW = 5;
    private static final int CMD_DETACH = 6;
    private static final int CMD_ATTACH = 7;
    private boolean fragmentTrasState = true;

    public FragmentController(FragmentManager fragmentManager, @IdRes int containerId, Bundle savedInstanceState, FragmentTransactionListener fragmentTransactionListener) {
        mFragmentManager = fragmentManager;
        mContainerId = containerId;
        mFragmentTransactionListener = fragmentTransactionListener;
        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState);
    }

    public void displayFragment(Fragment fragment, FragmentTransactionOptions fragmentTransactionOptions) {
        if (fragment != null) {
            if (isRootFragment(fragment)) {
                String tag = ((StackableFragment) fragment).getFragmentStackName();
                if (tag == null)
                    tag = fragment.getClass().getSimpleName() + "##" + mTagsStack.size();
                if (mTagsStack.contains(tag)) { // we already have the root fragment attached
                    displayRootFragment(tag, mTagsStack.peek(), fragmentTransactionOptions);
                } else {
                    ContainerFragment lFragment = (ContainerFragment) ContainerFragment.newInstance(tag);
                    lFragment.wrapFragment(fragment);
                    String lastTag = !mTagsStack.isEmpty() ? mTagsStack.peek() : null;
                    mTagsStack.push(tag);
                    mRootFragments.put(tag, lFragment);
                    displayRootFragment(tag, lastTag, fragmentTransactionOptions);
                }
            } else {
                if (!mTagsStack.isEmpty()) {
                    ContainerFragment topFragment = (ContainerFragment) mRootFragments.get(mTagsStack.peek());
                    if (!topFragment.isAdded()) {
                        performTransaction(topFragment, mTagsStack.peek(), CMD_REPLACE, fragmentTransactionOptions);
                    }
                    topFragment.performOps(fragment);
                } else { // uh Oh, I have no root fragment, let me create one for you
                    performTransaction(fragment, "root", CMD_REPLACE, fragmentTransactionOptions);
                }
            }
        }
    }

    public void displayFragment(String fragmentTag, FragmentTransactionOptions fragmentTransactionOptions) {
        displayRootFragment(fragmentTag, !mTagsStack.isEmpty() ? mTagsStack.peek() : null, fragmentTransactionOptions);
    }

    public boolean popBackStackImmediate() {
        return popBackStackImmediate(null);
    }

    public boolean popBackStackImmediate(String fragName) {
        return popBackStackImmediate(fragName, 0);
    }

    public boolean popBackStackImmediate(String fragName, int flag) {
        if (TextUtils.isEmpty(fragName)) {
            if (!mTagsStack.isEmpty()) {
                String rootFragmentTag = mTagsStack.peek();
                ContainerFragment lFragment = (ContainerFragment) mRootFragments.get(rootFragmentTag);
                return lFragment.popBackStackImmediate() || removeAndAttachPreviousFragment();
            }
        } else {
            if (!mTagsStack.isEmpty()) {
                ContainerFragment lFragment = (ContainerFragment) mRootFragments.get(mTagsStack.peek());
                if (lFragment.hasFragment(fragName)) {
                    return lFragment.popBackStackImmediate(fragName, flag);
                }
            }
        }
        return false;
    }

    public void clearFragmentStack(String fragmentTag) {
        if (!mTagsStack.isEmpty()) {
            if (mTagsStack.remove(fragmentTag)) {
                mRootFragments.remove(fragmentTag);
                mSavedStates.remove(fragmentTag);
            }
        }
    }

    public void onPostResume() {
        if (!fragmentTrasState) {
            try {
                mFragmentManager.beginTransaction().commitAllowingStateLoss();
            } catch (IllegalStateException e) {
            }
        }
    }

    private void displayRootFragment(String fragmentTag, String lastTag, FragmentTransactionOptions fragmentTransactionOptions) {
        Fragment fragmentToAttach = mRootFragments.get(fragmentTag);
        boolean performTransaction = false;
        if (!mTagsStack.isEmpty()) {
            if (!TextUtils.isEmpty(lastTag)) {
                if (!lastTag.equals(fragmentTag)) { // check whether we need to save current fragment managers state
                    if (mRootFragments.get(lastTag).isAdded()) {
                        Fragment.SavedState savedState = mFragmentManager.saveFragmentInstanceState(mRootFragments.get(lastTag));
                        mSavedStates.put(lastTag, savedState);
                    }
                    performTransaction = true;
                } else { //pop all the fragments of current root element except the stackable fragment
                    if (!((ContainerFragment) fragmentToAttach).popUntilLast()) { //dispatch scroll to fragment contained
                        if (((ContainerFragment) fragmentToAttach).getWrappedFragment() instanceof StackableFragment) {
                            ((StackableFragment) ((ContainerFragment) fragmentToAttach).getWrappedFragment()).onFragmentScroll();
                        }
                    }
                }
            } else { // we are about to attach the first root fragment
                performTransaction = true;
            }
        }
        if (performTransaction) {
            if (mSavedStates.get(fragmentTag) != null && !fragmentToAttach.isAdded()) {
                fragmentToAttach.setInitialSavedState(mSavedStates.get(fragmentTag));
            }
            if (!mTagsStack.peek().equals(fragmentTag)) {
                mTagsStack.remove(fragmentTag);
                mTagsStack.push(fragmentTag);
            }
            performTransaction(fragmentToAttach, fragmentTag, CMD_REPLACE, fragmentTransactionOptions);
        }
    }

    private boolean removeAndAttachPreviousFragment() {
        if (mTagsStack.size() > 1) {
            String fragmentTagToPop = mTagsStack.pop();
            mRootFragments.remove(fragmentTagToPop);
            mSavedStates.remove(fragmentTagToPop);
            if (!mTagsStack.isEmpty()) {
                String fragmentTagToDisplay = mTagsStack.peek();
                Fragment fragmentToDisplay = mRootFragments.get(fragmentTagToDisplay);
                if (!fragmentToDisplay.isAdded())
                    fragmentToDisplay.setInitialSavedState(mSavedStates.get(fragmentTagToDisplay));
                return performTransaction(fragmentToDisplay, fragmentTagToDisplay, CMD_REPLACE, null);
            }
        }
        return false;
    }

    private boolean performTransaction(@NonNull Fragment fragment, @NonNull String fragmentTag, int command, FragmentTransactionOptions fragmentTransactionOptions) {
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        fragmentTrasState = false;
        switch (command) {
            case CMD_ADD:
                ft.add(mContainerId, fragment, fragmentTag);
                break;
            case CMD_REPLACE:
                ft.replace(mContainerId, fragment, fragmentTag);
                break;
            case CMD_REMOVE:
                ft.remove(fragment);
                break;
            case CMD_HIDE:
                ft.hide(fragment);
                break;
            case CMD_SHOW:
                ft.show(fragment);
                break;
            case CMD_DETACH:
                ft.detach(fragment);
                break;
            case CMD_ATTACH:
                ft.attach(fragment);
                break;
        }
        try {
            ft.commitAllowingStateLoss();
            fragmentTrasState = true;
            return true;
        } catch (IllegalStateException e) {

        }
        return false;
    }

    public Fragment getCurrentFragment() {
        if (!mTagsStack.isEmpty()) {
            String topTag = mTagsStack.peek();
            ContainerFragment fragment = (ContainerFragment) mRootFragments.get(topTag);
            return fragment.getWrappedFragment();
        }
        return null;
    }

    public int getStackSize() {
        return mTagsStack.size();
    }

    public Stack<Fragment> getAllFragmentStacks() {
        Stack<Fragment> stack = new Stack<>();
        stack.addAll(mRootFragments.values());
        return stack;
    }

    private boolean isRootFragment(Fragment fragment) {
        return fragment instanceof StackableFragment;
    }

    public boolean isStateSaved(Fragment fragment) {
        return fragment.isStateSaved();
    }

    interface FragmentTransactionListener {
        void onCommit(Fragment f);
    }

    public void onSaveInstanceState(Bundle outState) {
        Bundle state = new Bundle();
        if (!mTagsStack.isEmpty()) {
            if (mSavedStates.size() > 0) {
                Fragment.SavedState[] fss = new Fragment.SavedState[mSavedStates.size()];
                String[] fStateTags = new String[mSavedStates.size()];

                Set<Map.Entry<String, Fragment.SavedState>> entrySet = mSavedStates.entrySet();
                int i = 0;
                for (Map.Entry e : entrySet) {
                    fss[i] = (Fragment.SavedState) e.getValue();
                    fStateTags[i++] = (String) e.getKey();
                }
                state.putParcelableArray("frag_states", fss);
                state.putStringArray("frag_tag_states", fStateTags);
            }
            String[] stackTags = new String[mTagsStack.size()];
            mTagsStack.toArray(stackTags);
            state.putStringArray("stack_states", stackTags);
        }
        outState.putParcelable("fc_state", state);
    }

    public void onRestoreInstanceState(@NonNull Bundle restoreBundle) {
        Bundle bundle = restoreBundle.getParcelable("fc_state");
        if (bundle != null) {
            Parcelable[] fss = bundle.getParcelableArray("frag_states");
            String[] stateTags = bundle.getStringArray("frag_tag_states");

            if (fss != null && stateTags != null) {
                mRootFragments.clear();
                mSavedStates.clear();
                for (int i = 0, j = 0; i < fss.length && j < stateTags.length; i++, j++) {
                    mSavedStates.put(stateTags[j], (Fragment.SavedState) fss[i]);
                }
            }
            String[] stackTags = bundle.getStringArray("stack_states");
            if (stackTags != null) {
                mTagsStack.clear();
                for (int i = 0; i < stackTags.length; i++) {
                    mTagsStack.push(stackTags[i]);
                    mRootFragments.put(stackTags[i], ContainerFragment.newInstance(stackTags[i]));
                }
            }
            if (!mTagsStack.isEmpty()) {
                Fragment lastSavedFragment = mFragmentManager.findFragmentByTag(mTagsStack.peek());
                if (lastSavedFragment != null)
                    mRootFragments.put(mTagsStack.peek(), lastSavedFragment);
            }
        }
    }

    public boolean hasRootFragment(String tag) {
        return (mTagsStack.contains(tag));
    }

}
