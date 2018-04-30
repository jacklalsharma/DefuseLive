package com.app.defuse.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.base.BaseActivity;
import com.app.defuse.base.BaseFragment;
import com.app.defuse.bean.TimerPojo;
import com.app.defuse.fragments.ClassicFragment;
import com.app.defuse.fragments.CreditsFragment;
import com.app.defuse.fragments.HelpFragment;
import com.app.defuse.fragments.ReverseFragment;
import com.app.defuse.fragments.SettingsFragment;
import com.app.defuse.fragments.TwistFragment;
import com.app.defuse.fragments.TwistReverseFragment;
import com.app.defuse.helpers.Commons;
import com.app.defuse.helpers.CustomDialog;
import com.app.defuse.helpers.TutorialDialog;
import com.app.defuse.helpers.TutorialDialog.TutorialCommunicator;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minixeroxindia.defuse.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static com.app.defuse.IAB.IabHelper.BILLING_RESPONSE_RESULT_OK;

/**
 * Created by amit singh on 02-03-2015.
 */
@SuppressLint("NewApi")
public class GameActivity extends BaseActivity implements
		CustomDialog.Communicator, TutorialCommunicator {

    CallbackManager callbackManager;


	private String classicPurchaseId = "com.minixeroxindia.classic";
	private String classicReversePurchaseId = "com.minixeroxindia.classicreverse";
	private String twistPurchaseId = "com.minixeroxindia.twist";
	private String twistReversePurchaseId = "com.minixeroxindia.twistreverse";

	public static final int CLASSIC_REQUEST_CODE = 1001;
	public static final int CLASSIC_REVERSE_REQUEST_CODE = 1002;

	public static final int TWIST_REQUEST_CODE = 1003;
	public static final int TWIST_REVERSE_REQUEST_CODE = 1004;



	//+======================================
	//In App Purchase....
	IInAppBillingService mService;

	ServiceConnection mServiceConn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			mService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name,
									   IBinder service) {
			mService = IInAppBillingService.Stub.asInterface(service);
			queryPurchaseOnStartup();
		}
	};
	//-----------------------------------




	public final static String GAME_TYPE = "gameType";
	public final static int GAME_CLASSIC = 101;
	public final static int GAME_CLASSIC_REVERSE = 121;
	public final static int GAME_HELP = 131;
	public final static int GAME_SWAP = 141;
	public final static int GAME_SWAP_REVERSE = 142;
	public final static int GAME_SWAP_ARCADE = 143;
	public final static int GAME_SETTINGS = 151;
	public final static int GAME_CREDITS = 152;

	private AdView adView;

	private NumberGamePreferences mPreferenceHelper;

	private int mGameType, mRestartType;
	private boolean showReplay = true;

	private boolean takeToNextLevel = false, haveLostGame = false,
			wasBlinking = false;

	public boolean getWasBlinking() {
		return wasBlinking;
	}

	public void setWasBlinking(boolean wasBlinking) {
		this.wasBlinking = wasBlinking;
	}

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

	private AlertDialog mDialog;
	Bundle b = new Bundle();
	TimerPojo timerPojo = new TimerPojo();

	public TimerPojo getTimerPojo() {
		return timerPojo;
	}

	public void setTimerPojo(TimerPojo timerPojo) {
		this.timerPojo = timerPojo;
	}

	private InterstitialAd mInterstitialAd;

	private BaseFragment baseFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}

		setContentView(R.layout.activity_game);
		NumberGamePreferences.resetLifePrefs(this);

		mGameType = getIntent().getIntExtra(GAME_TYPE, GAME_CLASSIC);

		Log.d("Type", "" + mGameType);

		mPreferenceHelper = new NumberGamePreferences(this);

		adView = (AdView) this.findViewById(R.id.adView);
		initAdds();

        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA1");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                //String something = new String(Base64.encodeBytes(md.digest()));
                Log.e("FB_HASH_KEY", something);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }


        switch (mGameType) {

		case GAME_CLASSIC:
			ClassicFragment classicFragment = new ClassicFragment();
			b.putLong("timer", Commons.START_TIMER);
			b.putLong("current_score", 0);
			classicFragment.setArguments(b);
            baseFragment = classicFragment;
			fragmentTransaction(R.id.frame_base_container, ADD_FRAGMENT,
					classicFragment, false, ClassicFragment.TAG_CLASSIC);
			break;
		case GAME_CLASSIC_REVERSE:
			ReverseFragment reverseFragment = new ReverseFragment();
			b.putLong("timer", Commons.START_TIMER);
			b.putLong("current_score", 0);
			reverseFragment.setArguments(b);
            baseFragment = reverseFragment;
			fragmentTransaction(R.id.frame_base_container, ADD_FRAGMENT,
					reverseFragment, true, ReverseFragment.TAG_CLASSIC_REVERSE);
			break;
		case GAME_SWAP:
			TwistFragment swapFragment = new TwistFragment();
			b.putLong("timer", Commons.START_TIMER);
			b.putLong("current_score", 0);
			swapFragment.setArguments(b);

			baseFragment = swapFragment;
			fragmentTransaction(R.id.frame_base_container, ADD_FRAGMENT,
					swapFragment, true, TwistFragment.TAG_TWIST);
			break;
		case GAME_SWAP_REVERSE:
			TwistReverseFragment twistReverseFragment = new TwistReverseFragment();
			b.putLong("timer", Commons.START_TIMER);
			b.putLong("current_score", 0);
			twistReverseFragment.setArguments(b);
			baseFragment = twistReverseFragment;
			fragmentTransaction(R.id.frame_base_container, ADD_FRAGMENT,
					twistReverseFragment, true,
					TwistReverseFragment.TAG_TWIST_REVERSE);
			break;
		case GAME_HELP:
			fragmentTransaction(R.id.frame_base_container, ADD_FRAGMENT,
					new HelpFragment(), true, HelpFragment.TAG_HELP);
			break;
		case GAME_SETTINGS:
			fragmentTransaction(R.id.frame_base_container, ADD_FRAGMENT,
					new SettingsFragment(), true, SettingsFragment.TAG_SETTINGS);
			break;
		case GAME_CREDITS:
			fragmentTransaction(R.id.frame_base_container, ADD_FRAGMENT,
					new CreditsFragment(), true, CreditsFragment.TAG_CREDITS);
			break;

		}


        callbackManager = CallbackManager.Factory.create();


		//IAP...
		Intent serviceIntent =
				new Intent("com.android.vending.billing.InAppBillingService.BIND");
		serviceIntent.setPackage("com.android.vending");
		bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);

		//queryPurchaseOnStartup();
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd
				.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
		requestNewInterstitial();

	}


    public void fbShare(final long score){
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentTitle("Win 10000$")
                .setContentDescription("I don't think you can beat my high score " + score + " in game DEFUSE! Check out game in google play -")
                .setContentUrl(Uri.parse("https://play.google.com/store/apps/details?id=com.minixeroxindia.defuse"))
                .build();


        ShareDialog shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Log.d("POST_ID", result.getPostId() + "  " );
                //if(result.getPostId() != null)
                {

                    Games.getLeaderboardsClient(GameActivity.this, GoogleSignIn.getLastSignedInAccount(GameActivity.this))
                            .submitScore("CgkIz_rrwPIBEAIQAQ", score);
                    Toast.makeText(GameActivity.this, "Score Successfully Uploaded", Toast.LENGTH_LONG).show();
                    if(CustomDialog.isGameOver){
                        startActivity(new Intent(GameActivity.this, HomeActivity.class));
                    }
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);

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

	private void requestNewInterstitial() {
		AdRequest adRequest = new AdRequest.Builder().build();

		/*
		 * .addTestDevice( Secure.getString(getContentResolver(),
		 * Secure.ANDROID_ID)) .build();
		 */
		mInterstitialAd.loadAd(adRequest);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		toggleAdds();

	}

	public void initAdds() {
		if (Commons.isNetworkOnline(this)) {
			// Request for Ads
			AdRequest adRequest = new AdRequest.Builder().build();

			/*
			 * // Add a test device to show Test Ads
			 * .addTestDevice(AdRequest.DEVICE_ID_EMULATOR) .addTestDevice(
			 * Secure.getString(getContentResolver(),
			 * Secure.ANDROID_ID)).build();
			 */

			if (adRequest != null) {
				// Load ads into Banner Ads
				adView.loadAd(adRequest);
				adView.setVisibility(View.VISIBLE);
			}

		} else {
			adView.setVisibility(View.GONE);
		}
	}

	public boolean ifShowReplay() {
		return showReplay;
	}

	public void setShowReplay(boolean replay) {
		this.showReplay = replay;
	}

	@Override
	public void update() {

	}

	public boolean isDialogVisible() {
		if (mDialog != null && mDialog.isShowing()) {
			return true;
		}
		return false;
	}

	public void dismissVisibleDialog() {
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public void lossNext() {

		if (isDialogVisible()) {
			dismissVisibleDialog();
		}

		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.frame_base_container);
		if (fragment instanceof ClassicFragment) {

			if (NumberGamePreferences.pref_classiclevel >= 3
					&& NumberGamePreferences.pref_classiclives >= 0) {
				NumberGamePreferences.pref_classiclives = NumberGamePreferences.pref_classiclives - 1;
				fragmentTransaction(R.id.frame_base_container,
						REPLACE_FRAGMENT, new ClassicFragment(), false,
						ClassicFragment.TAG_CLASSIC);
				setShowReplay(true);
			} else {

			}
		}

		else if (fragment instanceof ReverseFragment) {
			if (NumberGamePreferences.pref_classiclevel >= 3
					&& NumberGamePreferences.pref_classiclives >= 0) {
				NumberGamePreferences.pref_classiclives = NumberGamePreferences.pref_classiclives - 1;
				fragmentTransaction(R.id.frame_base_container,
						REPLACE_FRAGMENT, new ReverseFragment(), false,
						ReverseFragment.TAG_CLASSIC_REVERSE);
				setShowReplay(true);
			} else {

			}
		}

		else if (fragment instanceof TwistFragment) {
			if (NumberGamePreferences.pref_classiclevel >= 3
					&& NumberGamePreferences.pref_classiclives >= 0) {
				NumberGamePreferences.pref_classiclives = NumberGamePreferences.pref_classiclives - 1;
				fragmentTransaction(R.id.frame_base_container,
						REPLACE_FRAGMENT, new TwistFragment(), false,
						TwistFragment.TAG_TWIST);
				setShowReplay(true);
			} else {

			}
		} else if (fragment instanceof TwistReverseFragment) {
			if (NumberGamePreferences.pref_classiclevel >= 3
					&& NumberGamePreferences.pref_classiclives >= 0) {
				NumberGamePreferences.pref_classiclives = NumberGamePreferences.pref_classiclives - 1;
				fragmentTransaction(R.id.frame_base_container,
						REPLACE_FRAGMENT, new TwistReverseFragment(), false,
						TwistReverseFragment.TAG_TWIST_REVERSE);
				setShowReplay(true);
			} else {

			}
		}
	}

	@Override
	public void next() {

		if (isDialogVisible()) {
			dismissVisibleDialog();
		}

		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.frame_base_container);
		if (fragment instanceof ClassicFragment) {
			NumberGamePreferences.pref_classiclevel = NumberGamePreferences.pref_classiclevel + 1;
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new ClassicFragment(), false, ClassicFragment.TAG_CLASSIC);
			setShowReplay(true);
		} else if (fragment instanceof ReverseFragment) {
			NumberGamePreferences.pref_classiclevel = NumberGamePreferences.pref_classiclevel + 1;
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new ReverseFragment(), false,
					ReverseFragment.TAG_CLASSIC_REVERSE);
			setShowReplay(true);
		} else if (fragment instanceof TwistFragment) {
			NumberGamePreferences.pref_classiclevel = NumberGamePreferences.pref_classiclevel + 1;
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new TwistFragment(), false, TwistFragment.TAG_TWIST);
			setShowReplay(true);
		} else if (fragment instanceof TwistReverseFragment) {
			NumberGamePreferences.pref_classiclevel = NumberGamePreferences.pref_classiclevel + 1;
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new TwistReverseFragment(), false,
					TwistReverseFragment.TAG_TWIST_REVERSE);
			setShowReplay(true);
		}

	}

	public void gameOverRestart() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
			mInterstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdClosed() {
					super.onAdClosed();
					gameOver();
				}
			});
		} else {
			gameOver();
		}
	}

	private void gameOver() {
		try {

			if (isDialogVisible()) {
				dismissVisibleDialog();
			}

			Fragment fragment = getSupportFragmentManager().findFragmentById(
					R.id.frame_base_container);

			if (fragment instanceof ClassicFragment) {
				mRestartType = GAME_CLASSIC;
			} else if (fragment instanceof ReverseFragment) {
				mRestartType = GAME_CLASSIC_REVERSE;
			} else if (fragment instanceof TwistFragment) {
				mRestartType = GAME_SWAP;
			} else if (fragment instanceof TwistReverseFragment) {
				mRestartType = GAME_SWAP_REVERSE;
			}

			Log.d("Type", "1 " + mRestartType);

			Intent intent = new Intent();
			intent.putExtra(GameActivity.GAME_TYPE, mRestartType);
			setResult(RESULT_OK, intent);
			finish();

		} catch (IllegalStateException iise) {
			iise.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void resume() {
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.frame_base_container);
		if (fragment instanceof ClassicFragment) {
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new ClassicFragment(), false, ClassicFragment.TAG_CLASSIC);
		} else if (fragment instanceof ReverseFragment) {
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new ReverseFragment(), false,
					ReverseFragment.TAG_CLASSIC_REVERSE);
		} else if (fragment instanceof TwistFragment) {
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new TwistFragment(), false, TwistFragment.TAG_TWIST);
		} else if (fragment instanceof TwistReverseFragment) {
			fragmentTransaction(R.id.frame_base_container, REPLACE_FRAGMENT,
					new TwistReverseFragment(), false,
					TwistReverseFragment.TAG_TWIST_REVERSE);
		}

	}

	@Override
	public void exit() {
		if (mInterstitialAd.isLoaded()) {
			mInterstitialAd.show();
			mInterstitialAd.setAdListener(new AdListener() {
				@Override
				public void onAdClosed() {
					// TODO Auto-generated method stub
					super.onAdClosed();

					setResult(RESULT_CANCELED);
					finish();
				}
			});
		} else {

			setResult(RESULT_CANCELED);
			finish();
		}

	}

	@Override
	public void onBackPressed() {

		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.frame_base_container);

		if (fragment instanceof HelpFragment
				|| fragment instanceof CreditsFragment) {
			finish();
			return;
		}

		if (fragment instanceof TutorialDialog) {
			getSupportFragmentManager().popBackStack();
			return;
		}

		if (fragment instanceof SettingsFragment) {
			finish();
			return;
		}
		if (fragment instanceof ClassicFragment) {
			((ClassicFragment) fragment).stopTimer();
		} else if (fragment instanceof ReverseFragment) {
			((ReverseFragment) fragment).stopTimer();
		} else if (fragment instanceof TwistFragment) {
			((TwistFragment) fragment).stopTimer();
		} else if (fragment instanceof TwistReverseFragment) {
			((TwistReverseFragment) fragment).stopTimer();
		}
		// super.onBackPressed();
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		LayoutInflater inflater = this.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.dialog_yes_no, null);
		builder.setView(dialogView);
		TextView txtConfirmation = (TextView) dialogView
				.findViewById(R.id.lbl_confirmation_dialog);
		Commons.setFonts(this, txtConfirmation);
		final ImageView yes = (ImageView) dialogView.findViewById(R.id.ig_yes);
		final ImageView no = (ImageView) dialogView.findViewById(R.id.ig_no);

		yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Commons.setAlpha(v, 0.5f);
				// yes.setAlpha(0.5f);
				finish();
			}
		});
		no.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Commons.setAlpha(v, 0.5f);
				// no.setAlpha(0.5f);
				mDialog.dismiss();

				Fragment fragment = getSupportFragmentManager()
						.findFragmentById(R.id.frame_base_container);

				if (fragment instanceof ClassicFragment) {

					if (shouldTakeToNextLevel()) {

						takeToNextLevel = false;
						((ClassicFragment) fragment).takeToNextLevel();
						return;

					}

					if (ifHaveLostGame()) {

						haveLostGame = false;
						((ClassicFragment) fragment).haveLostGame();
						return;

					}

					if (!((ClassicFragment) fragment).isBlinkOver()) {

						setWasBlinking(true);

						return;
					} else {
						setWasBlinking(false);
					}

					if (NumberGamePreferences.pref_milliseconds != 0
							&& !timerPojo.getStatus()) {

						timerPojo.setStartTimer(true);
						((ClassicFragment) fragment)
								.startTimer(Commons.TIMER_RESTART);
						Log.i("patrol",
								"Classic Fragment has time started 1"
										+ ((ClassicFragment) fragment)
												.hasTimerStarted());

					} else if (timerPojo.getStatus()
							&& ((ClassicFragment) fragment).hasTimerStarted()) {
						if (((ClassicFragment) fragment).hasTimerStarted()) {
							timerPojo.setStartTimer(true);
							((ClassicFragment) fragment)
									.startTimer(Commons.TIMER_FRESH);
						} else {
							// Toast.makeText(GameActivity.this, "ABC",
							// Toast.LENGTH_SHORT).show();
						}

					}

				} else if (fragment instanceof ReverseFragment) {

					if (shouldTakeToNextLevel()) {

						takeToNextLevel = false;
						((ReverseFragment) fragment).takeToNextLevel();
						return;

					}

					if (ifHaveLostGame()) {

						haveLostGame = false;
						((ReverseFragment) fragment).haveLostGame();
						return;

					}

					if (!((ReverseFragment) fragment).isBlinkOver()) {

						setWasBlinking(true);

						return;
					} else {
						setWasBlinking(false);
					}

					if (NumberGamePreferences.pref_milliseconds != 0
							&& !timerPojo.getStatus()) {
						timerPojo.setStartTimer(true);
						((ReverseFragment) fragment)
								.startTimer(Commons.TIMER_RESTART);

					} else if (timerPojo.getStatus()) {
						timerPojo.setStartTimer(true);
						((ReverseFragment) fragment)
								.startTimer(Commons.TIMER_FRESH);
					}

				} else if (fragment instanceof TwistFragment) {

					if (shouldTakeToNextLevel()) {
						takeToNextLevel = false;
						((TwistFragment) fragment).takeToNextLevel();
						return;

					}

					if (ifHaveLostGame()) {
						haveLostGame = false;
						((TwistFragment) fragment).haveLostGame();
						return;

					}

					if (!((TwistFragment) fragment).isBlinkOver()) {

						setWasBlinking(true);

						return;
					} else {
						setWasBlinking(false);
					}
					if (NumberGamePreferences.pref_milliseconds != 0
							&& !timerPojo.getStatus()) {
						timerPojo.setStartTimer(true);
						((TwistFragment) fragment)
								.startTimer(Commons.TIMER_RESTART);

					} else if (timerPojo.getStatus()) {
						timerPojo.setStartTimer(true);
						((TwistFragment) fragment)
								.startTimer(Commons.TIMER_FRESH);
					}

				} else if (fragment instanceof TwistReverseFragment) {

					if (shouldTakeToNextLevel()) {
						takeToNextLevel = false;
						((TwistReverseFragment) fragment).takeToNextLevel();
						return;

					}

					if (ifHaveLostGame()) {
						haveLostGame = false;
						((TwistReverseFragment) fragment).haveLostGame();
						return;

					}

					if (NumberGamePreferences.pref_milliseconds != 0
							&& !timerPojo.getStatus()) {
						timerPojo.setStartTimer(true);
						((TwistReverseFragment) fragment)
								.startTimer(Commons.TIMER_RESTART);

					} else if (timerPojo.getStatus()) {
						timerPojo.setStartTimer(true);
						((TwistReverseFragment) fragment)
								.startTimer(Commons.TIMER_FRESH);
					}
					if (!((TwistReverseFragment) fragment).isBlinkOver()) {

						setWasBlinking(true);

						Toast.makeText(GameActivity.this, "Blinking",
								Toast.LENGTH_SHORT).show();
						return;
					} else {
						setWasBlinking(false);
					}
				}

			}
		});

		mDialog = builder.create();

		mDialog.show();
		Fragment fragment1 = getSupportFragmentManager().findFragmentById(
				R.id.frame_base_container);

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mDialog != null && mDialog.isShowing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public void exitTutorialDialog(int type) {
		// TODO Auto-generated method stub
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.frame_base_container);
		if (type == 1) {
			Log.i("communicator", "communicator in activity");
			fragmentTransaction(R.id.frame_base_container, 1,
					new ClassicFragment(), false, ClassicFragment.TAG_CLASSIC);
		} else if (type == 2) {
			fragmentTransaction(R.id.frame_base_container, 1,
					new ReverseFragment(), false,
					ReverseFragment.TAG_CLASSIC_REVERSE);
		} else if (type == 4) {
			fragmentTransaction(R.id.frame_base_container, 1,
					new TwistFragment(), false, TwistFragment.TAG_TWIST);
		} else if (type == 5) {
			fragmentTransaction(R.id.frame_base_container, 1,
					new TwistReverseFragment(), false,
					TwistReverseFragment.TAG_TWIST_REVERSE);
		}

	}

	@Override
	public void shareScore() {
		// TODO Auto-generated method stub
		long highScore = 0;
		String fragmentName = null;
		Fragment fragment = getSupportFragmentManager().findFragmentById(
				R.id.frame_base_container);
		if (fragment instanceof ClassicFragment) {
			highScore = mPreferenceHelper.getHighScoreClassic();
			fragmentName = "Classic Fragment";
		} else if (fragment instanceof ReverseFragment) {
			highScore = mPreferenceHelper.getHighScoreReverse();
			fragmentName = "Reverse Fragment";
		} else if (fragment instanceof TwistFragment) {
			highScore = mPreferenceHelper.getHighScoreTwistClassic();
			fragmentName = "Twist Fragment";
		} else if (fragment instanceof TwistReverseFragment) {
			highScore = mPreferenceHelper.getHighScoreTwistReverse();
			fragmentName = "Twist Reverse Fragment";
		}
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.setType("text/plain");
		shareIntent.putExtra(Intent.EXTRA_TEXT,
				"I don't think you can beat my highscore " + highScore
						+ " in game DEFUSE!\nCheck out game in google play - "
						+ "https://play.google.com/store/apps/details?id="
						+ getPackageName());
		startActivity(Intent.createChooser(shareIntent, "Share score via"));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CLASSIC_REQUEST_CODE) {
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			if (resultCode == RESULT_OK) {
				try {
					updatePrizeMoney();
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");
					//Start the Classic Game....
					//startClassicGame();
                    classicAlreadyPurchased = true;
                    if(baseFragment != null){
                        uploadScore(0);
                    }
					//consumePurchase(1, jo.getString("purchaseToken"));
				}
				catch (JSONException e) {
					//logError("Failed to parse purchase data.");
					e.printStackTrace();
				}
			}else{
				Log.d("PURCHASE", "CLASSIC_STARTE5");

			}
		}




		if (requestCode == CLASSIC_REVERSE_REQUEST_CODE) {
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			if (resultCode == RESULT_OK) {
				try {
					updatePrizeMoney();
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");
					//Start the Classic Reverse Game....
					//startClassicReverseGame();
                    classicReverseAlreadyPurchased = true;
                    if(baseFragment != null){
                        uploadScore(1);
                    }
					//consumePurchase(2, jo.getString("purchaseToken"));

				}
				catch (JSONException e) {
					//logError("Failed to parse purchase data.");
					e.printStackTrace();
				}
			}
		}



		if (requestCode == TWIST_REQUEST_CODE) {
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			if (resultCode == RESULT_OK) {
				try {
					updatePrizeMoney();
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");
					//Start the Twist Game....
					//startTwistGame();
                    twistAlreadyPurchased = true;
                    if(baseFragment != null){
                        uploadScore(2);
                    }
					//consumePurchase(3, jo.getString("purchaseToken"));

				}
				catch (JSONException e) {
					//logError("Failed to parse purchase data.");
					e.printStackTrace();
				}
			}
		}



		if (requestCode == TWIST_REVERSE_REQUEST_CODE) {
			int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
			String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
			String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");

			if (resultCode == RESULT_OK) {
				try {
					updatePrizeMoney();
					JSONObject jo = new JSONObject(purchaseData);
					String sku = jo.getString("productId");
					//Start the Twist Reverse Game....
					//startTwistReverseGame();
                    twistReverseAlreadyPurchased = true;
                    if(baseFragment != null){
                        uploadScore(3);
                    }
					//consumePurchase(4, jo.getString("purchaseToken"));

				}
				catch (JSONException e) {
					//logError("Failed to parse purchase data.");
					e.printStackTrace();
				}
			}
		}

	}

	private Activity getActivity(){
	    return this;
    }

    public void uploadScore(int gameType){
        long score = 0;

        NumberGamePreferences mSharedPrefs = new NumberGamePreferences(getActivity());
        BaseActivity activity = (BaseActivity) getActivity();

        switch (gameType){
            case 0:
                if(!activity.isClassicAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }
                score = mSharedPrefs.getHighScoreClassic();
                break;

            case 1:

                if(!activity.isClassicReverseAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }

                score = mSharedPrefs.getHighScoreReverse();
                break;

            case 2:


                if(!activity.isTwistAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }
                score = mSharedPrefs.getHighScoreTwistClassic();
                break;

            case 3:

                if(!activity.isTwistReverseAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }

                score = mSharedPrefs.getHighScoreTwistReverse();
                break;
        }

        GameActivity activity1 = (GameActivity) getActivity();
        if(activity1 != null) {
            activity1.fbShare(score);
        }

    }


    private void showPurchaseDialog(final int gameType){
        final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_purchase, null);

        dialogView.findViewById(R.id.game_over_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog != null){
                    mDialog.dismiss();
                }
            }
        });

        dialogView.findViewById(R.id.join_contest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog != null){
                    mDialog.dismiss();
                }
                initPurchase(gameType);
            }
        });

        new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        mDialog.setContentView(dialogView);
        mDialog.show();
    }


    public void initPurchase(int gameType){
        GameActivity activity = (GameActivity) getActivity();
        if(activity == null){
            return;
        }

        switch (gameType){
            case 0:
                activity.checkClassicPurchase();
                break;

            case 1:
                activity.checkClassicReversePurchase();
                break;

            case 2:
                activity.checkTwistPurchase();
                break;

            case 3:
                activity.checkTwistReversePurchase();
                break;
        }
    }

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (mService != null) {
			unbindService(mServiceConn);
		}
	}

	/**
	 * This method is to check for previously purchased info and re use them...
	 */
	private void queryPurchaseOnStartup(){
        Log.d("IAP_SIZE", " HERE");

        try{
			Bundle ownedItems = mService.getPurchases(3, getPackageName(), "inapp", null);
			int response = ownedItems.getInt("RESPONSE_CODE");
			if (response == BILLING_RESPONSE_RESULT_OK) {
				ArrayList<String> ownedSkus =
						ownedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
				ArrayList<String>  purchaseDataList =
						ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				ArrayList<String>  signatureList =
						ownedItems.getStringArrayList("INAPP_DATA_SIGNATURE_LIST");
				String continuationToken =
						ownedItems.getString("INAPP_CONTINUATION_TOKEN");

				Log.d("IAP_SIZE", " " + purchaseDataList.size());

				for (int i = 0; i < purchaseDataList.size(); ++i) {
					String purchaseData = purchaseDataList.get(i);
					String signature = signatureList.get(i);
					String sku = ownedSkus.get(i);

					JSONObject object = new JSONObject(purchaseData);
                    Log.d("IAP_DATA", " " + object.toString());

                    if(object.getString("developerPayload").equals(classicPurchaseId)){
						classicAlreadyPurchased = true;
					}

					if(object.getString("developerPayload").equals(classicReversePurchaseId)){
						classicReverseAlreadyPurchased = true;
					}

					if(object.getString("developerPayload").equals(twistPurchaseId)){
						twistAlreadyPurchased = true;
					}

					if(object.getString("developerPayload").equals(twistReversePurchaseId)){
						twistReverseAlreadyPurchased = true;
					}

				}

			}

		}catch (Exception e){
            Log.d("IAP_EXCE", " " + e.toString());

        }
	}


	/**
	 *
	 * @return
	 */
	public boolean checkClassicPurchase(){
		//startClassicGame();

		try{
			Log.d("PURCHASE", "CLASSIC_STARTE");

			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					classicPurchaseId, "inapp", classicPurchaseId);
			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

			startIntentSenderForResult(pendingIntent.getIntentSender(),
					CLASSIC_REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
					Integer.valueOf(0));
		}catch (Exception e){

		}
		return false;
	}

	/**
	 *
	 * @return
	 */
	public boolean checkClassicReversePurchase(){

		try{
			Log.d("PURCHASE", "CLASSIC_STARTE2");

			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					classicReversePurchaseId, "inapp", classicReversePurchaseId);
			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

			startIntentSenderForResult(pendingIntent.getIntentSender(),
					CLASSIC_REVERSE_REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
					Integer.valueOf(0));
		}catch (Exception e){

		}
		return false;
	}

	/**
	 *
	 * @return
	 */
	public boolean checkTwistReversePurchase(){

		try{
			Log.d("PURCHASE", "CLASSIC_STARTE3");

			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					twistReversePurchaseId, "inapp", twistReversePurchaseId);
			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

			startIntentSenderForResult(pendingIntent.getIntentSender(),
					TWIST_REVERSE_REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
					Integer.valueOf(0));
		}catch (Exception e){

		}
		return false;
	}

	/**
	 *
	 * @return
	 */
	public boolean checkTwistPurchase(){

		try{
			Log.d("PURCHASE", "CLASSIC_STARTE4");

			Bundle buyIntentBundle = mService.getBuyIntent(3, getPackageName(),
					twistPurchaseId, "inapp", twistPurchaseId);
			PendingIntent pendingIntent = buyIntentBundle.getParcelable("BUY_INTENT");

			startIntentSenderForResult(pendingIntent.getIntentSender(),
					TWIST_REQUEST_CODE, new Intent(), Integer.valueOf(0), Integer.valueOf(0),
					Integer.valueOf(0));
		}catch (Exception e){

		}
		return false;
	}


	private void updatePrizeMoney(){
		FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

		DatabaseReference ref = firebaseDatabase.getReference("prize");

		ref.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String money = dataSnapshot.getValue().toString();

			}

			@Override
			public void onCancelled(DatabaseError databaseError) {

			}
		});

		//The purchase is made of only 1$...so updating the total prize money by1$...
		ref.setValue("" + (prizeMoney + 1), new DatabaseReference.CompletionListener() {
			@Override
			public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
				Log.d("VALUE", "COMPLETED");
			}
		});
	}

}
