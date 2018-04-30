package com.app.defuse.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.Toast;

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

/**
 * Created by atuloholic on 27/02/15.
 */
public class ReverseFragment extends BaseFragment implements
		View.OnClickListener {
	View rootView;
	Button txtOne, txtTwo, txtThree, txtFour, txtFive, txtSix, txtSeven,
			txtEight, txtNine;
	TextView txtTimer, txtLives, txtScores, txtHighScores, txtCurrentScore,
			txtCurrentScoreToDisplay, tvFragment;
	AutoResizeTextView txtGeneratedNumber;
	ImageView imgLife1, imgLife2, imgLife3, imgRed, imgGreen;
	LinearLayout linClassicReverseParent, linNumpad, linRightOrWrong;
	private boolean hasWon = false, isPaused = false, hasLost = false,
			isGameOver = false;

	int recentNumber[];
	int randomNumber[];
	private int LEVEL;
	int DISPLAY_NUMBER = 1 * 500;
	String mNumber = "";
	String mUserEnteredNumber = "";
	CountdownTimerHelper countdownTimerHelper;
	View disable[] = new View[10];
	CustomDialog customDialog;
	Bundle bundle;
	NumberGamePreferences numberGamePreferences;
	int counter = 0;
	Long mTimerTime, mCurrentScore;
	boolean isPlaying;

	private MediaPlayer mediaPlayer, mediaPlayerWin;
	public final static String TAG_CLASSIC_REVERSE = "reverseFragment";

	TimerPojo timerPojo;
	private boolean showReplay = true, isBlinkOver = false;

	// public final static boolean TIMER_FRESH = true;
	// public final static boolean TIMER_RESTART = false;

	private Animation anim;
	private Handler mHandler;
	public ImageView rePlay;
	public NumberGamePreferences mSharedPref;

	private boolean hasTimerStarted = false;

	public boolean hasTimerStarted() {
		return hasTimerStarted;
	}

	public boolean isBlinkOver() {
		return isBlinkOver;
	}

	public void setHasTimerStarted(boolean hasTimerStarted) {
		this.hasTimerStarted = hasTimerStarted;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mSharedPref = new NumberGamePreferences(getActivity());
		mediaPlayer = MediaPlayer.create(getActivity(), R.raw.game_over);
		mediaPlayerWin = MediaPlayer.create(getActivity(), R.raw.win_sound);
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

		linClassicReverseParent = (LinearLayout) rootView
				.findViewById(R.id.lin_classic_parent);
		linNumpad = (LinearLayout) rootView.findViewById(R.id.lin_numpad);
		linRightOrWrong = (LinearLayout) rootView
				.findViewById(R.id.lin_right_or_wrong);
		txtScores = (TextView) rootView.findViewById(R.id.tv_score);
		txtHighScores = (TextView) rootView.findViewById(R.id.tv_high_score);
		txtCurrentScore = (TextView) rootView
				.findViewById(R.id.tv_current_score);
		txtCurrentScoreToDisplay = (TextView) rootView
				.findViewById(R.id.tv_current_score_to_display);
		tvFragment = (TextView) rootView.findViewById(R.id.tv_fragment);
		tvFragment.setText("Reverse");
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

		txtTimer = (TextView) rootView.findViewById(R.id.txtTimer);

		// Set Typeface
		Commons.setFonts(getActivity(), txtTimer);
		Commons.setFonts(getActivity(), tvFragment);
		Commons.setFonts(getActivity(), txtGeneratedNumber);
		Commons.setFonts(getActivity(), txtScores);
		Commons.setFonts(getActivity(), txtHighScores);
		Commons.setFonts(getActivity(), txtCurrentScore);
		Commons.setFonts(getActivity(), txtCurrentScoreToDisplay);

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
				// Toast.makeText(getActivity(), "Inside restart",
				// Toast.LENGTH_LONG).show();
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

		// txtGeneratedNumber.setText(mNumber);

		disable[0] = txtOne;
		disable[1] = txtTwo;
		disable[2] = txtThree;
		disable[3] = txtFive;
		disable[4] = txtFour;
		disable[5] = txtSix;
		disable[6] = txtSeven;
		disable[7] = txtEight;
		disable[8] = txtNine;

		if (mCurrentScore > mSharedPref.getHighScoreReverse()) {
			txtScores.setText("" + mCurrentScore);
			mSharedPref.setHighScoreReverse(mCurrentScore);
		} else if (mCurrentScore <= mSharedPref.getHighScoreReverse()) {
			txtScores.setText("" + mSharedPref.getHighScoreReverse());
		}

		txtCurrentScoreToDisplay.setText("" + mCurrentScore);

		blinkViews(getActivity(), disable, randomNumber,
				numberGamePreferences.pref_classiclevel);

		initScoreClicks(rootView, 1);
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
		customDialog = new CustomDialog();
		bundle = new Bundle();

		mUserEnteredNumber += numberEntered.getTag().toString();
		txtGeneratedNumber.setText(mUserEnteredNumber);

		if (txtGeneratedNumber.getText().toString().length() >= randomNumber.length) {
			String reverseOfEnteredNumber = reverse(mUserEnteredNumber.trim());
			Commons.disableView(disable);
			countdownTimerHelper.cancel();

			if (mNumber.trim().equals(reverseOfEnteredNumber.trim())) {

				winSetFragment();

			} else if (!mNumber.trim().equals(reverseOfEnteredNumber.trim())) {

				ReverseFragment classicFragment = new ReverseFragment();

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

					linClassicReverseParent.startAnimation(animation);
				}

			}
		}

	}

	Animation.AnimationListener shakeAnimationListener = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub

			hasLost = true;
			timerPojo.setStatus(true);

			imgRed.setVisibility(View.VISIBLE);
			linNumpad.setVisibility(View.INVISIBLE);
			if (mSharedPref.getSoundStatus()) {
				mediaPlayer.start();
			}
			if (mSharedPref.getVivrateStatus()) {
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				vibrator.vibrate(500);
			}
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			imgRed.setVisibility(View.GONE);
			linNumpad.setVisibility(View.VISIBLE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
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

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

	};

	public void haveLostGame() {
		NumberGamePreferences.pref_classiclives = NumberGamePreferences.pref_classiclives - 1;
		ReverseFragment reverseFragment = new ReverseFragment();
		Bundle b = new Bundle();
		b.putLong("timer", mTimerTime);
		b.putLong("current_score", mCurrentScore);
		reverseFragment.setArguments(b);
		getBaseActivity().fragmentTransaction(R.id.frame_base_container,
				BaseActivity.REPLACE_FRAGMENT, reverseFragment, false,
				ReverseFragment.TAG_CLASSIC_REVERSE);
		setShowReplay(true);
	}

	Animation.AnimationListener shakeAnimationListenerwin = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			hasWon = true;

			timerPojo.setStatus(true);
			// TODO Auto-generated method stub
			linNumpad.setVisibility(View.INVISIBLE);
			imgGreen.setVisibility(View.VISIBLE);
			if (mSharedPref.getSoundStatus()) {
				mediaPlayerWin.start();
			}

			if (mSharedPref.getVivrateStatus()) {
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				vibrator.vibrate(500);
			}
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			imgGreen.setVisibility(View.GONE);
			linNumpad.setVisibility(View.VISIBLE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (isPaused) {
						return;
					}

					if (((GameActivity) getActivity()).isDialogVisible()) {
						((GameActivity) getActivity()).setTakeToNextLevel(true);
					} else {
						takeToNextLevel();
					}

					if (mediaPlayerWin.isPlaying()) {
						try {
							mediaPlayerWin.stop();
							// mediaPlayerWin.release();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}, 400);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

	};

	public void takeToNextLevel() {
		NumberGamePreferences.pref_classiclevel = NumberGamePreferences.pref_classiclevel + 1;
		ReverseFragment reverseFragment = new ReverseFragment();
		Bundle b = new Bundle();
		mTimerTime = mTimerTime + 3000;
		b.putLong("timer", mTimerTime);
		b.putLong("current_score", mCurrentScore + 1);
		reverseFragment.setArguments(b);
		getBaseActivity().fragmentTransaction(R.id.frame_base_container,
				BaseActivity.REPLACE_FRAGMENT, reverseFragment, false,
				ReverseFragment.TAG_CLASSIC_REVERSE);
		setShowReplay(true);
		imgGreen.setVisibility(View.GONE);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		isPaused = true;
		Log.i("Node:", "Here in onPause of Fragment");
		timerPojo.setStartTimer(false);
		stopTimer();
	}

	Animation.AnimationListener shakeAnimationListenerGameOver = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			// TODO Auto-generated method stub
			isGameOver = true;
			timerPojo.setStatus(true);

			imgRed.setVisibility(View.VISIBLE);
			if (mSharedPref.getSoundStatus()) {
				mediaPlayer.start();
			}
			if (mSharedPref.getVivrateStatus()) {
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				vibrator.vibrate(500);
				stopTimer();
			}
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// TODO Auto-generated method stub
			imgRed.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {

					if (isPaused) {
						return;
					}

					hasGameOver();
					if (mediaPlayer.isPlaying()) {

						mediaPlayer.stop();
					}
				}
			}, 1100);

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub

		}

	};

	private void hasGameOver() {

		setLife();

		CustomDialog customDialog = new CustomDialog();
		Bundle bundle = new Bundle();
		bundle.putInt(Commons.FLAG_SETTINGS_TYPE, Commons.DIALOG_GAME_OVER);
		bundle.putInt(Commons.GAME_TYPE, 1);
		bundle.putLong(Commons.SCORE, mCurrentScore);
		customDialog.setArguments(bundle);

		getActivity().getSupportFragmentManager().beginTransaction()
				.add(customDialog, "GameOver").commit();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.i("Node:", "Here in onResume of Fragment");
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

			Log.i("Node:",
					"Here in onResume Condition" + timerPojo.getStartTimer());

			timerPojo.setStartTimer(true);
			if (!((GameActivity) getActivity()).isDialogVisible()) {
				if (!((GameActivity) getActivity()).isDialogVisible()) {
					if (hasTimerStarted()) {
						startTimer(Commons.TIMER_RESTART);
					} else {
						startTimer(Commons.TIMER_FRESH);
					}
				}
			}

		}

	}

	public void blinkViews(Context context, final View[] views,
			final int[] values, int count) {
		counter = 0;
		Commons.disableView(disable);
		enable(false);
		mHandler.postDelayed(runnable, 500);

		if (countdownTimerHelper != null) {
			countdownTimerHelper.cancel();
		}

	}

	Animation.AnimationListener listener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			isBlinkOver = false;
		}

		@Override
		public void onAnimationEnd(Animation animation) {

			if (counter >= randomNumber.length) {

				isBlinkOver = true;

				setHasTimerStarted(true);
				Commons.enableView(disable);
				if (countdownTimerHelper == null) {
					startTimer(Commons.TIMER_FRESH);
				} else {
					startTimer(Commons.TIMER_RESTART);
				}

				enable(true);
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

	private String reverse(String enteredNumber) {
		StringBuilder b = new StringBuilder();
		for (int idx = enteredNumber.length() - 1; idx >= 0; idx--)
			b.append(enteredNumber.charAt(idx));
		return b.toString();
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
				// Toast.makeText(getActivity(), "Check timer",
				// Toast.LENGTH_SHORT)
				// .show();
				countdownTimerHelper = new CountdownTimerHelper(
						NumberGamePreferences.pref_milliseconds, 1000,
						ReverseFragment.this, txtTimer, txtLives, getActivity());
				countdownTimerHelper.start();
			} else {
				// Toast.makeText(getActivity(), "Check Timer else",
				// Toast.LENGTH_SHORT).show();
				countdownTimerHelper = new CountdownTimerHelper(mTimerTime,
						1000, ReverseFragment.this, txtTimer, txtLives,
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
		// linNumpad.setVisibility(View.INVISIBLE);
		// linRightOrWrong.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shaking);
		animation.setAnimationListener(shakeAnimationListener);

		linClassicReverseParent.startAnimation(animation);

	}

	public void winSetFragment() {
		timerPojo.setStatus(true);
		// linNumpad.setVisibility(View.INVISIBLE);
		// linRightOrWrong.setVisibility(View.VISIBLE);
		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shaking);
		animation.setAnimationListener(shakeAnimationListenerwin);
		linClassicReverseParent.startAnimation(animation);
	}

}
