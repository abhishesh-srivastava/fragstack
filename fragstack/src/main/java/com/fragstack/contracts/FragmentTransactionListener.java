package com.fragstack.contracts;

import androidx.fragment.app.Fragment;

public interface FragmentTransactionListener {
    void onCommit(Fragment f);
}