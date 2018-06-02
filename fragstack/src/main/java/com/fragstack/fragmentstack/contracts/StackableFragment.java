package com.fragstack.fragmentstack.contracts;

public interface StackableFragment {
    public String getFragmentStackName();

    public void onFragmentScroll();
}