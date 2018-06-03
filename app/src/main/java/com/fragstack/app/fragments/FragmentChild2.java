package com.fragstack.app.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fragstack.app.MainActivity;
import com.fragstack.app.R;

import java.util.Random;

public class FragmentChild2 extends Fragment {

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
}
