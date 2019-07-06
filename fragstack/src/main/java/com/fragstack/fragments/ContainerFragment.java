package com.fragstack.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.fragstack.R;
import com.fragstack.contracts.FragmentTransactionListener;
import com.fragstack.contracts.RootFragmentListener;
import com.fragstack.controller.FragmentTransactionOptions;

import java.util.ArrayList;
import java.util.List;

public class ContainerFragment extends Fragment {

    public static final String FRAGMENT_TAG = "tag";
    private Context mContext;
    private List<Fragment> mFragmentsToAdd = new ArrayList<>();
    private FragmentTransactionListener mFragmentTransactionListener;
    private RootFragmentListener mRootFragmentListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public static Fragment newInstance(String tag) {
        Fragment fragment = new ContainerFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FRAGMENT_TAG, tag);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_container, container, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        mFragmentsToAdd.clear();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        performBottomBarSelection(getFragmentName());
        if (mFragmentsToAdd.size() > 0) {
            for (int i = 0; i < mFragmentsToAdd.size(); i++) {
                performOps(mFragmentsToAdd.get(i));
            }
        }
        mFragmentsToAdd.clear();
    }

    private void performOps(Fragment fragment) {
        performOps(fragment, null);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    public void wrapFragment(Fragment fragment) {
        mFragmentsToAdd.add(fragment);
    }

    public String getFragmentName() {
        if (getArguments() != null)
            return getArguments().getString(FRAGMENT_TAG, "root");
        return "";
    }

    public void performOps(Fragment fragment, @Nullable FragmentTransactionOptions fragmentTransactionOptions) {
        if (isAdded() && fragment != null) {
            String tag = String.valueOf(getChildFragmentManager().getBackStackEntryCount());
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment, tag);
            fragmentTransaction.addToBackStack(tag);
            try {
                if (mFragmentTransactionListener != null) {
                    mFragmentTransactionListener.onCommit(fragment);
                }
                if (fragmentTransactionOptions != null) {
                    bindFragmentTransactionOptions(fragmentTransaction, fragmentTransactionOptions);
                }
                fragmentTransaction.commitAllowingStateLoss();
                fragment.setHasOptionsMenu(true);
            } catch (IllegalStateException e) {
            }
        } else if (fragment != null) {
            mFragmentsToAdd.add(fragment);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @SuppressLint("WrongConstant")
    private void bindFragmentTransactionOptions(@NonNull FragmentTransaction fragmentTransaction, @Nullable FragmentTransactionOptions fragmentTransactionOptions) {
        if (fragmentTransactionOptions != null) {
            if (fragmentTransactionOptions.sharedElement != null) {
                fragmentTransaction.addSharedElement(fragmentTransactionOptions.sharedElement,
                        fragmentTransactionOptions.name);
            }
            if (fragmentTransactionOptions.transit != FragmentTransaction.TRANSIT_UNSET) {
                fragmentTransaction.setTransition(fragmentTransactionOptions.transit);
            }
            if (fragmentTransactionOptions.transitionStyle != 0) {
                fragmentTransaction.setTransitionStyle(fragmentTransactionOptions.transitionStyle);
            }
            fragmentTransaction.setCustomAnimations(fragmentTransactionOptions.enter,
                    fragmentTransactionOptions.exit,
                    fragmentTransactionOptions.popEnter,
                    fragmentTransactionOptions.popExit);
        }

    }

    public int getFragmentCount() {
        return isAdded() ? getChildFragmentManager().getBackStackEntryCount() : 0;
    }

    public Fragment getWrappedFragment() {
        if (isAdded() && getChildFragmentManager().getBackStackEntryCount() > 0) {
            String tagName = getChildFragmentManager().getBackStackEntryAt(getChildFragmentManager().getBackStackEntryCount() - 1).getName();
            return getChildFragmentManager().findFragmentByTag(tagName);
        }
        return null;
    }

    public boolean popUntilLast() {
        return popBackStackImmediate(String.valueOf(0), 0);
    }

    public boolean popBackStackImmediate() {
        try {
            return getChildFragmentManager().getBackStackEntryCount() > 1 && getChildFragmentManager().popBackStackImmediate();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public boolean popBackStackImmediate(String fragName, int flag) {
        try {
            return getChildFragmentManager().popBackStackImmediate(fragName, flag);
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private void performBottomBarSelection(String tabType) {
        if (mRootFragmentListener != null) {
            mRootFragmentListener.onRootFragmentSelected(tabType);
        }
    }

    public boolean hasFragment(String fragName) {
        return getChildFragmentManager().findFragmentByTag(fragName) != null;
    }
}
