package com.fragstack.app;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.fragstack.app.fragments.FragmentTabA;
import com.fragstack.app.fragments.FragmentTabB;
import com.fragstack.fragmentstack.controller.FragmentController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FragmentController mFragmentController;
    private Button btnTabA;
    private Button btnTabB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnTabA = findViewById(R.id.tab_1);
        btnTabB = findViewById(R.id.tab_2);
        btnTabA.setOnClickListener(this);
        btnTabB.setOnClickListener(this);
        mFragmentController = new FragmentController(getSupportFragmentManager(), R.id.frame_container, savedInstanceState, null);
    }

    @Override
    public void onBackPressed() {
        if (!mFragmentController.popBackStackImmediate())
            super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_1: {
                Fragment fragment = new FragmentTabA();
                displayFragment(fragment);
                break;
            }

            case R.id.tab_2: {
                Fragment fragment = new FragmentTabB();
                displayFragment(fragment);
                break;
            }
        }
    }

    public void displayFragment(Fragment fragment) {
        mFragmentController.displayFragment(fragment, false);
    }
}
