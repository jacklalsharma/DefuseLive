package com.app.defuse.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.base.BaseActivity;
import com.app.defuse.bean.TimerPojo;
import com.app.defuse.fragments.TwistArcadeFragment;
import com.app.defuse.fragments.TwistArcadeGameFragment;
import com.app.defuse.helpers.Commons;
import com.app.defuse.helpers.CustomDialog.Communicator;
import com.app.defuse.helpers.TutorialDialog.TutorialCommunicator;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.minixeroxindia.defuse.R;

public class TwistArcadeActivity extends BaseActivity implements Communicator,
		TutorialCommunicator {

	Button btnSixBlinks, btnSevenBlinks, btnEightBlinks, btnNineBlinks,
			btnTenBlinks, btnElevenBlinks, btnTwelveBlinks, btnThirteenBlinks,
			btnFourteenBlinks, btnFifteenBlinks;
	private boolean showReplay = true;
	TimerPojo timerPojo = new TimerPojo();
	private AlertDialog mDialog;
	private AdView adView;

	private boolean takeToNextLevel = false, haveLostGame = false,
			wasBlinking = false;

	public boolean getWasBlinking() {
		return wasBlinking;
	}

	public void setWasBlinking(boolean wasBlinking) {
		this.wasBlinking = wasBlinking;
	}

	private InterstitialAd mInterstitialAd;

	public boolean shouldTakeToNextLevel() {
		return takeToNextLevel;
	}

	public void setTakeToNextLevel(boolean takeToNextLevel) {
		this.takeToNextLevel = takeToNextLevel;
	}

	public boolean ifHaveLostGame() {
		return haveLostGame;
	}

	public void setHaveLostGame(boolean haveLostGame) {
		this.haveLostGame = haveLostGame;
	}

	public TimerPojo getTimerPojo() {
		return timerPojo;
	}

	public void setTimerPojo(TimerPojo timerPojo) {
		this.timerPojo = timerPojo;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(R.layout.activity_arcade);
		/*
		 * fragmentTransaction(R.id.arcade_container, REPLACE_FRAGMENT, new
		 * TwistArcadeGameFragment(), false, null);
		 */

		TwistArcadeGameFragment fragment = new TwistArcadeGameFragment();

		if (getIntent() != null) {
			Bundle bundle = new Bundle();
			bundle.putBoolean(ArcadeActivity.START_DIRECTLY, getIntent()
					.getBooleanExtra(ArcadeActivity.START_DIRECTLY, false));
			bundle.putInt("no_of_blinks",
					getIntent().getIntExtra("no_of_blinks", 6));
			bundle.putInt("timer", getIntent().getIntExtra("timer", 20000));
			fragment.setArguments(bundle);

		}

		fragmentTransaction(R.id.arcade_container, REPLACE_FRAGMENT, fragment,
				false, null);

		adView = (AdView) this.findViewById(R.id.adView);

		initAdds();

		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd
				.setAdUnitId(getString(R.string.interstitial_ad_unit_id));

	}

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				Secure.getString(getContentResolver(), Secure.ANDROID_ID))
				.build();
		mInterstitialAd.loadAd(adRequest);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		toggleAdds();
		requestNewInterstitial();
	}

	public boolean isDialogVisible() {
		if (mDialog != null && mDialog.isShowing()) {
			return true;
		}
		return false;
	}

	public void toggleAdds() {
		if (Commons.isNetworkOnline(this)) {
			if (adView.getVisibility() == View.VISIBLE) {

			} else {
				initAdds();
				adView.setVisibility(View.VISIBLE);
			}
		} else {
			adView.setVisibility(View.GONE);
		}
	}

	public void initAdds() {
		if (Commons.isNetworkOnline(this)) {
			// Request for Ads
			AdRequest adRequest = new AdRequest.Builder().build();

			/*// Add a test device to show Test Ads
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice(
							Secure.getString(getContentResolver(),
									Secure.ANDROID_ID)).build();*/

			if (adRequest != null) {
				// Load ads into Banner Ads
				adView.loadAd(adRequest);
				adView.setVisibility(View.VISIBLE);
			}

		} else {
			adView.setVisibility(View.GONE);
		}
	}

	/*
	 * @Override public void onTrimMemory(int level) {
	 * super.onTrimMemory(level); if (level == Activity.TRIM_MEMORY_UI_HIDDEN) {
	 * NumberGamePreferences.pref_was_paused = true; //
	 * NumberGamePreferences.pref_was_paused); } }
	 */

	@Override
	public void onBackPressed() {

		// Fragment fragment = getSupportFragmentManager().findFragmentById(
		// R.id.frame_base_container);
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.arcade_container);
		if (fragment instanceof TwistArcadeGameFragment) {
			finish();
		} else if (fragment instanceof TwistArcadeFragment) {

			((TwistArcadeFragment) fragment).stopTimer();

			// super.onBackPressed();
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setCancelable(false);

			LayoutInflater inflater = this.getLayoutInflater();
			View dialogView = inflater.inflate(R.layout.dialog_yes_no, null);
			builder.setView(dialogView);
			TextView txtConfirmation = (TextView) dialogView
					.findViewById(R.id.lbl_confirmation_dialog);
			Commons.setFonts(this, txtConfirmation);
			ImageView yes = (ImageView) dialogView.findViewById(R.id.ig_yes);
			ImageView no = (ImageView) dialogView.findViewById(R.id.ig_no);
			yes.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					// TODO Auto-generated method stub

					mDialog.dismiss();
					Fragment fragment = getSupportFragmentManager()
							.findFragmentById(R.id.arcade_container);
					if (fragment instanceof TwistArcadeGameFragment) {
						finish();

						Log.i("patrol", "Life"
								+ NumberGamePreferences.pref_arcade);
					} else if (fragment instanceof TwistArcadeFragment) {
						NumberGamePreferences
								.resetArcadeLifePrefs(TwistArcadeActivity.this);
						fragmentTransaction(R.id.arcade_container,
								REPLACE_FRAGMENT,
								new TwistArcadeGameFragment(), false, null);

					}
				}

			});
			no.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					mDialog.dismiss();
					Fragment fragment = getSupportFragmentManager()
							.findFragmentById(R.id.arcade_container);
					if (fragment instanceof TwistArcadeFragment) {

						if (shouldTakeToNextLevel()) {

							takeToNextLevel = false;
							((TwistArcadeFragment) fragment).takeToNextLevel();
							return;

						}

						if (ifHaveLostGame()) {

							haveLostGame = false;
							((TwistArcadeFragment) fragment).haveLostGame();
							return;

						}
						if (!((TwistArcadeFragment) fragment).isBlinkOver()) {

							setWasBlinking(true);

							return;
						} else {
							setWasBlinking(false);
						}
						if (NumberGamePreferences.pref_milliseconds != 0
								&& !timerPojo.getStatus()) {
							timerPojo.setStartTimer(true);
							((TwistArcadeFragment) fragment)
									.startTimer(Commons.TIMER_RESTART);

						} else if (timerPojo.getStatus()) {
							timerPojo.setStartTimer(true);
							((TwistArcadeFragment) fragment)
									.startTimer(Commons.TIMER_FRESH);

						}
						// negativeButtonClick((ClassicFragment)fragment);
					}

				}
			});
			/*
			 * builder.setPositiveButton("Yes", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * { dialog.dismiss(); Fragment fragment =
			 * getSupportFragmentManager()
			 * .findFragmentById(R.id.arcade_container); if (fragment instanceof
			 * TwistArcadeFragment) { finish(); NumberGamePreferences
			 * .resetArcadeLifePrefs(TwistArcadeActivity.this); } } });
			 * builder.setNegativeButton("No", new
			 * DialogInterface.OnClickListener() {
			 * 
			 * @Override public void onClick(DialogInterface dialog, int which)
			 * {
			 * 
			 * dialog.dismiss(); Fragment fragment = getSupportFragmentManager()
			 * .findFragmentById(R.id.arcade_container); if (fragment instanceof
			 * TwistArcadeFragment) {
			 * 
			 * if (shouldTakeToNextLevel()) {
			 * 
			 * takeToNextLevel = false; ((TwistArcadeFragment) fragment)
			 * .takeToNextLevel(); return;
			 * 
			 * }
			 * 
			 * if (ifHaveLostGame()) {
			 * 
			 * haveLostGame = false; ((TwistArcadeFragment)
			 * fragment).haveLostGame(); return;
			 * 
			 * }
			 * 
			 * if (NumberGamePreferences.pref_milliseconds != 0 &&
			 * !timerPojo.getStatus()) { timerPojo.setStartTimer(true);
			 * ((TwistArcadeFragment) fragment)
			 * .startTimer(Commons.TIMER_RESTART);
			 * 
			 * } else if (timerPojo.getStatus()) {
			 * timerPojo.setStartTimer(true); ((TwistArcadeFragment) fragment)
			 * .startTimer(Commons.TIMER_FRESH);
			 * 
			 * } // negativeButtonClick((ClassicFragment)fragment); } } });
			 */

			mDialog = builder.create();
			mDialog.show();
		}

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public void next() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit() {

		try {

			NumberGamePreferences.resetArcadeLifePrefs(this);

			if (mInterstitialAd.isLoaded()) {
				mInterstitialAd.show();
				mInterstitialAd.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						super.onAdClosed();
						setResult(RESULT_OK);
						finish();
					}
				});
			} else {
				setResult(RESULT_OK);
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void lossNext() {
		// TODO Auto-generated method stub

	}

	public boolean ifShowReplay() {
		return showReplay;
	}

	public void setShowReplay(boolean replay) {
		this.showReplay = replay;
	}

	public void dismissVisibleDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public void gameOverRestart() {
		/*
		 * NumberGamePreferences.resetArcadeLifePrefs(this);
		 * fragmentTransaction(R.id.arcade_container, REPLACE_FRAGMENT, new
		 * ArcadeFragment(), false, ArcadeFragment.TAG_CLASSIC_ARCADE);
		 * setShowReplay(true);
		 */

		try {

			Fragment fragment = getSupportFragmentManager().findFragmentById(
					R.id.arcade_container);
			NumberGamePreferences.resetArcadeLifePrefs(this);

			final Intent intent = new Intent();
			intent.putExtra("no_of_blinks",
					((TwistArcadeFragment) fragment).getRandomNumber().length);
			intent.putExtra("timer",
					((TwistArcadeFragment) fragment).getmTimerTime());

			if (mInterstitialAd.isLoaded()) {
				mInterstitialAd.show();
				mInterstitialAd.setAdListener(new AdListener() {
					@Override
					public void onAdClosed() {
						super.onAdClosed();
						setResult(RESULT_OK, intent);
						finish();
					}
				});
			} else {
				setResult(RESULT_OK, intent);
				finish();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*
		 * if (isDialogVisible()) { dismissVisibleDialog(); }
		 * 
		 * Fragment fragment = getSupportFragmentManager().findFragmentById(
		 * R.id.arcade_container);
		 * 
		 * ((TwistArcadeGameFragment) fragment).afterGameOverFragment();
		 */

	}

	@Override
	public void exitTutorialDialog(int type) {
		// TODO Auto-generated method stub
		if (type == 3) {
			fragmentTransaction(R.id.arcade_container, 0,
					new TwistArcadeGameFragment(), false,
					TwistArcadeGameFragment.TAG_CLASSIC_ARCADE_GAME);
		}
	}

	@Override
	public void shareScore() {
		// TODO Auto-generated method stub
		Toast.makeText(this, "No High Scores available for this mode",
				Toast.LENGTH_SHORT).show();
	}
}
