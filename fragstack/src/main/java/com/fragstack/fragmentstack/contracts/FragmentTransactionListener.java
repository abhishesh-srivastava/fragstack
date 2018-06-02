package com.fragstack.fragmentstack.contracts;

import android.support.v4.app.Fragment;

public interface FragmentTransactionListener {
    void onCommit(Fragment f);
}