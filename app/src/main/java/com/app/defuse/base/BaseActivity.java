package com.app.defuse.base;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.minixeroxindia.defuse.R;

public class BaseActivity extends FragmentActivity {


	protected static boolean classicAlreadyPurchased;
	protected static boolean classicReverseAlreadyPurchased;
	protected static boolean twistAlreadyPurchased;
	protected static boolean twistReverseAlreadyPurchased;

    protected static int prizeMoney;

    public static boolean isClassicAlreadyPurchased() {
        return classicAlreadyPurchased;
    }

    public static boolean isClassicReverseAlreadyPurchased() {
        return classicReverseAlreadyPurchased;
    }

    public static boolean isTwistAlreadyPurchased() {
        return twistAlreadyPurchased;
    }

    public static boolean isTwistReverseAlreadyPurchased() {
        return twistReverseAlreadyPurchased;
    }

    public static final int ADD_FRAGMENT = 0;
	public static final int REPLACE_FRAGMENT = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		 super.onSaveInstanceState(outState);
	}

	public void fragmentTransaction(int container, int transactionType,
			android.support.v4.app.Fragment fragment, boolean isAddToBackStack,
			String tag) {

		android.support.v4.app.FragmentTransaction trans = getSupportFragmentManager()
				.beginTransaction();

		/*
		 * trans.setCustomAnimations(android.R.anim.fade_in,
		 * android.R.anim.fade_out, android.R.anim.fade_in,
		 * android.R.anim.fade_out);
		 */

		/*trans.setCustomAnimations(android.R.anim.slide_in_left,
				android.R.anim.slide_out_right, android.R.anim.slide_out_right,
				android.R.anim.slide_in_left);*/

		switch (transactionType) {
		case (ADD_FRAGMENT):

			trans.add(container, fragment, tag);
			break;
		case (REPLACE_FRAGMENT):
			trans.replace(container, fragment, tag);

			if (isAddToBackStack) {
				trans.addToBackStack(null);
			}
			Log.i("Communicator", "communicator ::");

			break;
		default:
			break;

		}
		trans.commit();
	}

}
