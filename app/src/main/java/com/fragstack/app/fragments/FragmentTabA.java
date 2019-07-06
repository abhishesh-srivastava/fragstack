package com.fragstack.app.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.fragstack.app.MainActivity;
import com.fragstack.app.R;
import com.fragstack.contracts.StackableFragment;

import java.util.Random;

public class FragmentTabA extends Fragment implements StackableFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.who_am_i)).setText(getClass().getSimpleName());
        view.findViewById(R.id.btn_action).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random nextInt = new Random();
                int val = nextInt.nextInt(2);
                Fragment fragment = null;
                if (val == 0) {
                    fragment = new FragmentChild1();
                } else {
                    fragment = new FragmentChild2();
                }
                ((MainActivity) getActivity()).displayFragment(fragment);
            }
        });

    }

    @Override
    public String getFragmentStackName() {
        return "tabA";
    }

    @Override
    public void onFragmentScroll() {

    }
}
