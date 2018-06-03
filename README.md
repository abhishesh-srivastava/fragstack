# fragstack :  Android library for managing individual fragment backstack. 

An Easy to use library for managing individual fragment back stack as Instagram and Youtube does.
Easily pluggable with client code, not much code change needed.

To implement, client needs to implement StackableFragment of com.fragstack.contracts package on all fragments who wants to have their own backstack.
Client needs to provide a unique name in getFragmentName() method.

# Example : 
# Integration with BottomBar
If there are 4 tabs in BottomBar and each tab corresponds to different fragment and want to have individual stacks. Client should implement StackableFragment and provide implementation for getFragmentName() and onFragmentScroll() method.

# Initialization inside onCreate() method of activity

    mFragmentController = new FragmentController(getSupportFragmentManager(), R.id.frame_container, savedInstanceState, null);

here R.id.frame_container corresponds to id's on which fragments view are added/replaced.

# To display a fragment, call

        FragmentTransactionOptions fragmentTransactionOptions = new FragmentTransactionOptions.Builder()
        			.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out).build();
        mFragmentController.displayFragment(fragment, fragmentTransactionOptions); // its not necessary to provide FragmentTransactionOptions, pass null if animation, transition or shared element animations are not required

# Back Press Handling :
    public void onBackPressed() {
        if (!mFragmentController.popBackStackImmediate())
            super.onBackPressed();
    }

