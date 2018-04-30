package com.app.defuse.helpers;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.activities.GameActivity;
import com.minixeroxindia.defuse.R;

/**
 * Created by webwerks on 28/2/15.
 */
public class Commons {
	public final static String LEVEL_CLASSIC = "level_classic";
	public final static String DEFAULT_LIVES_CLASSIC = "default_lives_classic";
	public final static String FLAG_SETTINGS_TYPE = "flag_type";
	public final static String GAME_TYPE = "game_type";
	public final static String SCORE = "curr_score";

	public final static int DIALOG_PAUSE = 1;
	public final static int DIALOG_WON = 2;
	public final static int DIALOG_LOSS = 3;
	public final static int DIALOG_GAME_OVER = 4;
	public final static int DIALOG_ARCADE_WIN = 5;
	public final static int DIALOG_ARCADE_LOSE = 6;

	public final static String EXIT_CONFIRMATION_DIALOG = "Are you sure you want to exit";

	public final static long START_TIMER = 11000;

	public final static boolean TIMER_FRESH = true;
	public final static boolean TIMER_RESTART = false;

	// @SWAP
	private static Integer[] arraySelector = { R.drawable.selector_one,
			R.drawable.selector_two, R.drawable.selector_three,
			R.drawable.selector_four, R.drawable.selector_five,
			R.drawable.selector_six, R.drawable.selector_seven,
			R.drawable.selector_eight, R.drawable.selector_nine };

	public static Integer[] getArraySelector() {
		return arraySelector;
	}

	public static void setArraySelector(Integer[] arraySelector) {
		Commons.arraySelector = arraySelector;
	}

	public static void blinkDot(Context context, View view) {
		Animation anim = AnimationUtils.loadAnimation(context,
				android.R.anim.fade_in);
		anim.setDuration(2000);
		anim.setRepeatCount(Animation.INFINITE);
		anim.setFillAfter(false);
		anim.setFillBefore(false);
		view.setAnimation(anim);
	}

	public static void stopDot(View view) {
		view.setAnimation(null);
	}

	public static boolean isNetworkOnline(Context context) {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;

	}

	public static void appShareIntent(Context context) {

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_TEXT,
				context.getString(R.string.share_message)
						+ "https://play.google.com/store/apps/details?id="
						+ context.getPackageName());
		shareIntent.putExtra(Intent.EXTRA_SUBJECT,
				context.getString(R.string.share_subject));
		context.startActivity(Intent.createChooser(shareIntent,
				"Share Defuse via..."));

	}

	public static void startPLayStore(Context context, String packageName) {

		try {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("market://details?id=" + packageName)));
		} catch (android.content.ActivityNotFoundException anfe) {
			context.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse("https://play.google.com/store/apps/details?id="
							+ packageName)));
		}
	}

	public static void swap(View disable[], List<Integer> listSelector) {

		GenerateRandomNumbers.shuffleList(listSelector);

		for (int i = 0; i < disable.length; i++) {
			disable[i].setBackgroundResource(listSelector.get(i));

			if (listSelector.get(i) == R.drawable.selector_one) {
				disable[i].setTag("1");
			} else if (listSelector.get(i) == R.drawable.selector_two) {
				disable[i].setTag("2");
			} else if (listSelector.get(i) == R.drawable.selector_three) {
				disable[i].setTag("3");
			} else if (listSelector.get(i) == R.drawable.selector_four) {
				disable[i].setTag("4");
			} else if (listSelector.get(i) == R.drawable.selector_five) {
				disable[i].setTag("5");
			} else if (listSelector.get(i) == R.drawable.selector_six) {
				disable[i].setTag("6");
			} else if (listSelector.get(i) == R.drawable.selector_seven) {
				disable[i].setTag("7");
			} else if (listSelector.get(i) == R.drawable.selector_eight) {
				disable[i].setTag("8");
			} else if (listSelector.get(i) == R.drawable.selector_nine) {
				disable[i].setTag("9");
			}
		}
	}

	private static int counter = 0;

	public static int decrementLife() {
		return (NumberGamePreferences.pref_classiclives - 1);
	}

	public static void resetLife() {
		;
	}

	public static void disableView(View[] views) {
		for (int i = 0; i < views.length; i++) {
			if (views[i] != null) {
				views[i].setEnabled(false);
			}
		}
	}

	public static void enableView(View[] views) {
		for (int i = 0; i < views.length; i++) {
			if (views[i] != null) {
				views[i].setEnabled(true);
			}
		}
	}

	public static void setFonts(Context context, View view) {
		Typeface fonts = Typeface.createFromAsset(context.getAssets(),
				"fonts/skranji_bold.ttf");

		if (view instanceof TextView) {
			((TextView) view).setTypeface(fonts);
		} else if (view instanceof Button) {
			((Button) view).setTypeface(fonts);
		}
	}

	public static void showTutorial(FragmentActivity activity, String msg,
			String tag, int fragmentTobeReplaced) {
		TutorialDialog tutorialDialog = new TutorialDialog();
		Bundle b = new Bundle();
		b.putString("msg", msg);
		tutorialDialog.setArguments(b);

		activity.getSupportFragmentManager()
				.beginTransaction()
				.replace(fragmentTobeReplaced, tutorialDialog,
						"tutorialFragment").addToBackStack(null).commit();
	}

	public static void showTutorialHelper(FragmentActivity activity,
			String msg, String tag, int fragmentTobeReplaced, int type) {
		TutorialDialogHelper tutorialDialog = new TutorialDialogHelper();
		Bundle b = new Bundle();
		b.putString("msg", msg);
		b.putInt(GameActivity.GAME_TYPE, type);
		tutorialDialog.setArguments(b);

		activity.getSupportFragmentManager().beginTransaction()
				.add(fragmentTobeReplaced, tutorialDialog, "tutorialFragment")
				.commit();
	}

	@SuppressLint("NewApi")
	public static void setAlpha(View view, float alpha) {
		if (Build.VERSION.SDK_INT < 11) {
			final AlphaAnimation animation = new AlphaAnimation(alpha, alpha);
			animation.setDuration(0);
			animation.setFillAfter(true);
			view.startAnimation(animation);
		} else
			view.setAlpha(alpha);
	}

}