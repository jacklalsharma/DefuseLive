package com.app.defuse.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.activities.GameActivity;
import com.app.defuse.base.BaseActivity;
import com.app.defuse.base.BaseFragment;
import com.app.defuse.bean.TimerPojo;
import com.app.defuse.helpers.AutoResizeTextView;
import com.app.defuse.helpers.Commons;
import com.app.defuse.helpers.CountdownTimerHelper;
import com.app.defuse.helpers.CustomDialog;
import com.app.defuse.helpers.GenerateRandomNumbers;
import com.app.defuse.helpers.TutorialDialog;
import com.minixeroxindia.defuse.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.minixeroxindia.defuse.R;

/**
 * Created by atuloholic on 27/02/15.
 */
public class TwistFragment extends BaseFragment implements View.OnClickListener {

	// UI Components
	View rootView;
	Button txtOne, txtTwo, txtThree, txtFour, txtFive, txtSix, txtSeven,
			txtEight, txtNine;
	TextView txtTimer, txtLives, txtScores, txtHighScores, txtCurrentScore,
			txtCurrentScoreToDisplay, tvFragment;
	AutoResizeTextView txtGeneratedNumber;
	ImageView imgLife1, imgLife2, imgLife3, imgRed, imgGreen;
	LinearLayout linClassicParent, linNumpad, linRightOrWrong;

	// Other Variables
	int recentNumber[];
	int randomNumber[];
	private int LEVEL;
	int DISPLAY_NUMBER = 1 * 500;
	String mNumber = "";
	String mUserEnteredNumber = "";
	int counter = 0;
	boolean isPlaying;
	private MediaPlayer mediaPlayer, mediaPlayerWin;
	// Helpers
	CountdownTimerHelper countdownTimerHelper;
	View disable[] = new View[9];
	NumberGamePreferences numberGamePreferences;
	TimerPojo timerPojo;
	private boolean showReplay = true;
	TutorialDialog tutorialDialog;
	Long mTimerTime, mCurrentScore;

	private boolean hasWon = false, isPaused = false, hasLost = false,
			isGameOver, isBlinkOver = false;

	public NumberGamePreferences mSharedPrefs;

	public final static String TAG_TWIST = "twistFragment";
	private boolean hasTimerStarted = false;

	public boolean hasTimerStarted() {
		return hasTimerStarted;
	}

	public void setHasTimerStarted(boolean hasTimerStarted) {
		this.hasTimerStarted = hasTimerStarted;
	}

	public boolean isBlinkOver() {
		return isBlinkOver;
	}

	private Animation anim;
	private Handler mHandler;
	public ImageView rePlay;

	// @SWAP
	List<Integer> listSelector;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPrefs = new NumberGamePreferences(getActivity());
		mediaPlayer = MediaPlayer.create(getActivity(), R.raw.game_over);

		mediaPlayerWin = MediaPlayer.create(getActivity(), R.raw.win_sound);
		// @SWAP
		listSelector = new ArrayList<Integer>();
		listSelector.addAll(Arrays.asList(Commons.getArraySelector()));

		/*
		 * for (int i = 0; i < Commons.getArraySelector().length; i++) {
		 * listSelector.add(Commons.getArraySelector()[i]); }
		 */

		blink();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setContentResource(R.layout.fragment_classic);
		// Toast.makeText(getActivity(),"Here in fragment",Toast.LENGTH_SHORT).show();
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public boolean ifShowReplay() {
		return showReplay;
	}

	public void setShowReplay(boolean replay) {
		this.showReplay = replay;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (countdownTimerHelper != null) {
			countdownTimerHelper.cancel();
		}

		if (mHandler != null) {
			mHandler.removeCallbacks(runnable);
		}
		try {
			mediaPlayerWin.release();
			mediaPlayer.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void enable(boolean enable) {
		try {
			rePlay.setEnabled(enable);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void basicInitialization() {
		mTimerTime = getArguments().getLong("timer");
		mCurrentScore = getArguments().getLong("current_score");
		timerPojo = ((GameActivity) getBaseActivity()).getTimerPojo();
		timerPojo.setStartTimer(true);
		timerPojo.setStatus(true);
		numberGamePreferences = new NumberGamePreferences(getActivity());

		randomNumber = new int[numberGamePreferences.pref_classiclevel];
		randomNumber = GenerateRandomNumbers
				.getRandomValues(randomNumber.length);
		for (int i = 0; i < randomNumber.length; i++) {
			mNumber += randomNumber[i];
		}
	}

	public void blink() {
		mHandler = new Handler();
		anim = AnimationUtils.loadAnimation(getActivity(),
				android.R.anim.fade_in);
		anim.setDuration(500);
		anim.setAnimationListener(listener);

	}

	@SuppressLint("NewApi")
	@Override
	public void initializeComponent(View rootView) {
		Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/skranji_bold.ttf");
		linClassicParent = (LinearLayout) rootView
				.findViewById(R.id.lin_classic_parent);
		tvFragment = (TextView) rootView.findViewById(R.id.tv_fragment);
		tvFragment.setText("Twist Classic");
		linNumpad = (LinearLayout) rootView.findViewById(R.id.lin_numpad);
		linRightOrWrong = (LinearLayout) rootView
				.findViewById(R.id.lin_right_or_wrong);
		linRightOrWrong.setVisibility(View.GONE);
		txtScores = (TextView) rootView.findViewById(R.id.tv_score);
		txtHighScores = (TextView) rootView.findViewById(R.id.tv_high_score);
		txtCurrentScore = (TextView) rootView
				.findViewById(R.id.tv_current_score);
		txtCurrentScoreToDisplay = (TextView) rootView
				.findViewById(R.id.tv_current_score_to_display);
		txtOne = (Button) rootView.findViewById(R.id.lbl_one);
		txtOne.setTag("1");
		txtTwo = (Button) rootView.findViewById(R.id.lbl_two);
		txtTwo.setTag("2");
		txtThree = (Button) rootView.findViewById(R.id.lbl_three);
		txtThree.setTag("3");
		txtFour = (Button) rootView.findViewById(R.id.lbl_four);
		txtFour.setTag("4");
		txtFive = (Button) rootView.findViewById(R.id.lbl_five);
		txtFive.setTag("5");
		txtSix = (Button) rootView.findViewById(R.id.lbl_six);
		txtSix.setTag("6");
		txtSeven = (Button) rootView.findViewById(R.id.lbl_seven);
		txtSeven.setTag("7");
		txtEight = (Button) rootView.findViewById(R.id.lbl_eight);
		txtEight.setTag("8");
		txtNine = (Button) rootView.findViewById(R.id.lbl_nine);
		txtNine.setTag("9");
		txtGeneratedNumber = (AutoResizeTextView) rootView
				.findViewById(R.id.lbl_enter_number);

		txtGeneratedNumber.setTypeface(typeface);
		Commons.setFonts(getActivity(), txtScores);
		Commons.setFonts(getActivity(), txtHighScores);
		Commons.setFonts(getActivity(), txtCurrentScore);
		Commons.setFonts(getActivity(), txtCurrentScoreToDisplay);
		Commons.setFonts(getActivity(), tvFragment);
		txtTimer = (TextView) rootView.findViewById(R.id.txtTimer);
		txtTimer.setTypeface(typeface);

		imgLife1 = (ImageView) rootView.findViewById(R.id.imgLife1);
		imgLife2 = (ImageView) rootView.findViewById(R.id.imgLife2);
		imgLife3 = (ImageView) rootView.findViewById(R.id.imgLife3);

		setLife();

		imgRed = (ImageView) rootView.findViewById(R.id.img_wrong);
		imgGreen = (ImageView) rootView.findViewById(R.id.img_correct);

		imgRed.setVisibility(View.GONE);
		imgGreen.setVisibility(View.GONE);

		rePlay = (ImageView) rootView.findViewById(R.id.rePlay);
		Commons.blinkDot(getActivity(), rePlay);
		rePlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (ifShowReplay()) {
					Commons.stopDot(v);
					Commons.setAlpha(v, 0.5f);
					stopTimer();
					blinkViews(getActivity(), disable, randomNumber,
							numberGamePreferences.pref_classiclevel);
					setShowReplay(false);
				}
			}
		});

		// txtTimer.setText("01:00");

		disable[0] = txtOne;
		disable[1] = txtTwo;
		disable[2] = txtThree;
		disable[3] = txtFive;
		disable[4] = txtFour;
		disable[5] = txtSix;
		disable[6] = txtSeven;
		disable[7] = txtEight;
		disable[8] = txtNine;

		if (mCurrentScore > mSharedPrefs.getHighScoreTwistClassic()) {
			txtScores.setText("" + mCurrentScore);
			mSharedPrefs.setHighScoreTwistClassic(mCurrentScore);
		} else if (mCurrentScore <= mSharedPrefs.getHighScoreTwistClassic()) {
			txtScores.setText("" + mSharedPrefs.getHighScoreTwistClassic());
		}

		txtCurrentScoreToDisplay.setText("" + mCurrentScore);

		blinkViews(getActivity(), disable, randomNumber,
				numberGamePreferences.pref_classiclevel);
		initScoreClicks(rootView, 2);
	}

	public void setLife() {
		if (NumberGamePreferences.pref_classiclives <= 0) {
			imgLife1.setEnabled(false);
			imgLife2.setEnabled(false);
			imgLife3.setEnabled(false);
		} else if (NumberGamePreferences.pref_classiclives <= 1) {
			imgLife2.setEnabled(false);
			imgLife3.setEnabled(false);
		} else if (NumberGamePreferences.pref_classiclives <= 2) {
			imgLife3.setEnabled(false);
		} else if (NumberGamePreferences.pref_classiclives > 2) {
			imgLife1.setEnabled(true);
			imgLife2.setEnabled(true);
			imgLife3.setEnabled(true);
		}

	}

	@Override
	public void setListeners() {
		txtOne.setOnClickListener(this);
		txtTwo.setOnClickListener(this);
		txtThree.setOnClickListener(this);
		txtFour.setOnClickListener(this);
		txtFive.setOnClickListener(this);
		txtSix.setOnClickListener(this);
		txtSeven.setOnClickListener(this);
		txtEight.setOnClickListener(this);
		txtNine.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.lbl_one:
		case R.id.lbl_two:
		case R.id.lbl_three:
		case R.id.lbl_four:
		case R.id.lbl_five:
		case R.id.lbl_six:
		case R.id.lbl_seven:
		case R.id.lbl_eight:
		case R.id.lbl_nine:
			setUserEnteredNumber((TextView) v);
			break;
		}

	}

	private void setUserEnteredNumber(TextView numberEntered) {

		mUserEnteredNumber += numberEntered.getTag().toString();
		txtGeneratedNumber.setText(mUserEnteredNumber);

		if (txtGeneratedNumber.getText().toString().length() >= randomNumber.length) {
			Commons.disableView(disable);
			countdownTimerHelper.cancel();
			if (mNumber.trim().equals(mUserEnteredNumber.trim())) {

				winSetFragment();

			} else if (!mNumber.trim().equals(mUserEnteredNumber.trim())) {

				TwistFragment classicFragment = new TwistFragment();

				if ((NumberGamePreferences.pref_classiclives) > 1) {
					setLife();

					lossSetFragment();

				} else {

					linNumpad.setVisibility(View.INVISIBLE);
					linRightOrWrong.setVisibility(View.VISIBLE);
					Animation animation = AnimationUtils.loadAnimation(
							getActivity(), R.anim.shaking);
					animation
							.setAnimationListener(shakeAnimationListenerGameOver);

					linClassicParent.startAnimation(animation);
				}

			}
		}

	}

	Animation.AnimationListener shakeAnimationListener = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

			hasLost = true;

			timerPojo.setStatus(true);

			imgRed.setVisibility(View.VISIBLE);
			if (mSharedPrefs.getSoundStatus()) {
				mediaPlayer.start();
			}
			if (mSharedPrefs.getVivrateStatus()) {
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				vibrator.vibrate(500);
			}
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {

			if (NumberGamePreferences.pref_was_paused) {

			}
			imgRed.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					if (isPaused) {
						return;
					}

					if (((GameActivity) getActivity()).isDialogVisible()) {
						((GameActivity) getActivity()).setHaveLostGame(true);
					} else {
						haveLostGame();
					}
					if (mediaPlayer.isPlaying()) {

						mediaPlayer.stop();
					}
					// startTimer(TIMER_FRESH);
				}
			}, 700);
			linNumpad.setVisibility(View.VISIBLE);
			linRightOrWrong.setVisibility(View.GONE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	};

	public void haveLostGame() {
		NumberGamePreferences.pref_classiclives = NumberGamePreferences.pref_classiclives - 1;
		TwistFragment classicFragment = new TwistFragment();
		Bundle b = new Bundle();
		b.putLong("timer", mTimerTime);
		b.putLong("current_score", mCurrentScore);
		classicFragment.setArguments(b);
		getBaseActivity().fragmentTransaction(R.id.frame_base_container,
				BaseActivity.REPLACE_FRAGMENT, classicFragment, false,
				TwistFragment.TAG_TWIST);
		setShowReplay(true);
	}

	Animation.AnimationListener shakeAnimationListenerwin = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

			hasWon = true;

			timerPojo.setStatus(true);

			imgGreen.setVisibility(View.VISIBLE);
			if (mSharedPrefs.getSoundStatus()) {
				mediaPlayerWin.start();
			}
			if (mSharedPrefs.getVivrateStatus()) {
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				vibrator.vibrate(500);
			}
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {

			imgGreen.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					if (isPaused) {
						return;
					}

					if (((GameActivity) getActivity()).isDialogVisible()) {
						((GameActivity) getActivity()).setTakeToNextLevel(true);
					} else {
						takeToNextLevel();
					}
					if (mediaPlayerWin.isPlaying()) {

						mediaPlayerWin.stop();
					}
				}
			}, 400);
			linNumpad.setVisibility(View.VISIBLE);
			linRightOrWrong.setVisibility(View.GONE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	};

	public void takeToNextLevel() {
		NumberGamePreferences.pref_classiclevel = NumberGamePreferences.pref_classiclevel + 1;
		TwistFragment classicFragment = new TwistFragment();
		Bundle b = new Bundle();
		mTimerTime = mTimerTime + 3000;
		b.putLong("timer", mTimerTime);
		b.putLong("current_score", mCurrentScore + 1);
		classicFragment.setArguments(b);

		getBaseActivity().fragmentTransaction(R.id.frame_base_container,
				BaseActivity.REPLACE_FRAGMENT, classicFragment, false,
				TwistFragment.TAG_TWIST);
		setShowReplay(true);
		imgGreen.setVisibility(View.GONE);
	}

	public void blinkViews(Context context, final View[] views,
			final int[] values, int count) {

		counter = 0;
		Commons.disableView(disable);
		enable(false);
		mHandler.postDelayed(runnable, 500);
		/*
		 * if (countdownTimerHelper != null) { countdownTimerHelper.cancel(); }
		 */

	}

	Animation.AnimationListener listener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {

			// @SWAP
			if (counter >= randomNumber.length) {
				isBlinkOver = true;
				Animation animationUtils;
				animationUtils = AnimationUtils.loadAnimation(getActivity(),
						android.R.anim.fade_out);
				animationUtils.setDuration(500);
				animationUtils.setAnimationListener(listenerSwapLogic);
				linClassicParent.startAnimation(animationUtils);
			}

			for (int j = 0; j < disable.length; j++) {
				try {
					if (disable[j].isSelected()) {
						disable[j].setSelected(false);
					}
				} catch (NullPointerException e) {
					e.printStackTrace();
				}

			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};

	Animation.AnimationListener listenerSwapLogic = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			Log.i("patrol", "Here in swap logic animation");
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i("patrol", "Here in swap logic animation end");
			// if (counter >= randomNumber.length) {

			// @SWAP
			Commons.swap(disable, (listSelector));

			Commons.enableView(disable);
			if (countdownTimerHelper == null) {
				startTimer(Commons.TIMER_FRESH);
			} else {
				startTimer(Commons.TIMER_RESTART);
			}

			enable(true);
			// }

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (counter < randomNumber.length) {
				int value = randomNumber[counter];

				for (int j = 0; j < disable.length; j++) {
					if (value == Integer.parseInt(disable[j].getTag()
							.toString())) {
						disable[j].setSelected(true);
						disable[j].startAnimation(anim);

						counter++;
						new Handler().postDelayed(this, 800);
						break;
					}
				}
			}
		}
	};

	@Override
	public void onPause() {

		super.onPause();
		isPaused = true;
		Log.i("Node:", "Here in onPause of Fragment");
		timerPojo.setStartTimer(false);
		stopTimer();
	}

	Animation.AnimationListener shakeAnimationListenerGameOver = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

			isGameOver = true;

			timerPojo.setStatus(true);

			imgRed.setVisibility(View.VISIBLE);
			if (mSharedPrefs.getSoundStatus()) {

				mediaPlayer.start();
			}
			if (mSharedPrefs.getVivrateStatus()) {
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);

				vibrator.vibrate(500);
			}
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			imgRed.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					hasGameOver();
					if (mediaPlayer.isPlaying()) {
						try {
							mediaPlayer.stop();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, 1100);

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

	};

	private void hasGameOver() {

		if (isPaused) {
			return;
		}

		setLife();

		CustomDialog customDialog = new CustomDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(Commons.FLAG_SETTINGS_TYPE, Commons.DIALOG_GAME_OVER);
		bundle.putInt(Commons.GAME_TYPE, 2);
		bundle.putLong(Commons.SCORE, mCurrentScore);
		customDialog.setArguments(bundle);

		getActivity().getSupportFragmentManager().beginTransaction()
				.add(customDialog, "GameOver").commit();
	}

	@Override
	public void onResume() {
		super.onResume();
		isPaused = false;

		if (hasWon) {
			// Toast.makeText(getActivity(), "Won resume", Toast.LENGTH_SHORT)
			// .show();
			takeToNextLevel();
		} else if (hasLost) {
			// Toast.makeText(getActivity(), "Lost resume", Toast.LENGTH_SHORT)
			// .show();
			haveLostGame();

		} else if (isGameOver) {
			hasGameOver();
		} else if (!timerPojo.getStartTimer()) {

			timerPojo.setStartTimer(true);

			if (!(((GameActivity) getActivity()).isDialogVisible())) {

				if (hasTimerStarted()) {
					startTimer(Commons.TIMER_RESTART);
				} else {
					startTimer(Commons.TIMER_FRESH);
				}
			}

			// Toast.makeText(getActivity(), "timer",
			// Toast.LENGTH_SHORT).show();

		}

	}

	public void startTimer(boolean isFresh) {
		if (((GameActivity) getActivity()).getWasBlinking()) {
			if (timerPojo != null) {
				timerPojo = ((GameActivity) getBaseActivity()).getTimerPojo();
			}
			timerPojo.setStartTimer(true);
			timerPojo.setStatus(true);
		}
		if (timerPojo.getStartTimer()) {

			if (!isFresh && NumberGamePreferences.pref_milliseconds != 0) {

				countdownTimerHelper = new CountdownTimerHelper(
						NumberGamePreferences.pref_milliseconds, 1000,
						TwistFragment.this, txtTimer, txtLives, getActivity());
				countdownTimerHelper.start();
			} else {

				countdownTimerHelper = new CountdownTimerHelper(mTimerTime,
						1000, TwistFragment.this, txtTimer, txtLives,
						getActivity());
				countdownTimerHelper.start();
			}
		}
	}

	public void stopTimer() {
		if (countdownTimerHelper != null) {
			countdownTimerHelper.cancel();
			timerPojo.setStatus(false);
		} else {

			timerPojo.setStartTimer(false);
			timerPojo.setStatus(true);
			startTimer(false);

		}

	}

	public void lossSetFragment() {
		timerPojo.setStatus(true);
		linNumpad.setVisibility(View.INVISIBLE);
		linRightOrWrong.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shaking);
		animation.setAnimationListener(shakeAnimationListener);

		linClassicParent.startAnimation(animation);

	}

	public void winSetFragment() {
		timerPojo.setStatus(true);

		linNumpad.setVisibility(View.INVISIBLE);
		linRightOrWrong.setVisibility(View.VISIBLE);

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shaking);
		animation.setAnimationListener(shakeAnimationListenerwin);

		linClassicParent.startAnimation(animation);
	}

}
