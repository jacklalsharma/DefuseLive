package com.app.defuse.fragments;

import android.content.Context;
import android.graphics.Typeface;
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
import java.util.List;

/**
 * Created by atuloholic on 27/02/15.
 */
public class InterstitialFragment extends BaseFragment implements View.OnClickListener {

	// UI Components
	View rootView;
	Button txtOne, txtTwo, txtThree, txtFour, txtFive, txtSix, txtSeven,
			txtEight, txtNine;
	TextView txtTimer, txtLives;
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

	// Helpers
	CountdownTimerHelper countdownTimerHelper;
	View disable[] = new View[9];
	CustomDialog customDialog;
	Bundle bundle;
	NumberGamePreferences numberGamePreferences;
	TimerPojo timerPojo;
	private boolean showReplay = true;
	TutorialDialog tutorialDialog;
	Long mTimerTime;

	public NumberGamePreferences mSharedPrefs;

	public final static String TAG_SWAP = "swapFragment";

	// @SWAP
	List<Integer> listSelector;

	// @SWAP
	Integer[] arraySelector = { R.drawable.selector_one,
			R.drawable.selector_two, R.drawable.selector_three,
			R.drawable.selector_four, R.drawable.selector_five,
			R.drawable.selector_six, R.drawable.selector_seven,
			R.drawable.selector_eight, R.drawable.selector_nine };

	private Animation anim;
	private Handler mHandler;
	public ImageView rePlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mSharedPrefs = new NumberGamePreferences(getActivity());

		// @SWAP
		listSelector = new ArrayList<Integer>();
		for (int i = 0; i < arraySelector.length; i++) {
			listSelector.add(arraySelector[i]);
		}
		
		GenerateRandomNumbers.shuffleList(listSelector);

		if (!mSharedPrefs.getShowClassic()) {
			mSharedPrefs.setShowClassic();
			Log.i("patrol", "::" + mSharedPrefs.getShowClassic());
			TutorialDialog tutorialDialog = new TutorialDialog();
			Bundle b = new Bundle();
			b.putString("msg", getResources().getString(R.string.swap_fragment));
			tutorialDialog.setArguments(b);
			getActivity()
					.getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.frame_base_container, tutorialDialog,
							"tutorialFragment")
					.addToBackStack(InterstitialFragment.TAG_SWAP).commit();
		} else {
			blink();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setContentResource(R.layout.fragment_swap);

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
		Log.d("Blink ", "inside blink");
		mHandler = new Handler();
		anim = AnimationUtils.loadAnimation(getActivity(),
				android.R.anim.fade_in);
		anim.setDuration(500);
		anim.setAnimationListener(listener);

	}

	@Override
	public void initializeComponent(View rootView) {
		Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/skranji_bold.ttf");
		linClassicParent = (LinearLayout) rootView
				.findViewById(R.id.lin_classic_parent);
		linNumpad = (LinearLayout) rootView.findViewById(R.id.lin_numpad);
		linRightOrWrong = (LinearLayout) rootView
				.findViewById(R.id.lin_right_or_wrong);
		linRightOrWrong.setVisibility(View.GONE);
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
		rePlay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (ifShowReplay()) {
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

		blinkViews(getActivity(), disable, randomNumber,
				numberGamePreferences.pref_classiclevel);

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
			Commons.disableView(disable);
			countdownTimerHelper.cancel();
			if (mNumber.trim().equals(mUserEnteredNumber.trim())) {

				winSetFragment();

			} else if (!mNumber.trim().equals(mUserEnteredNumber.trim())) {

				InterstitialFragment classicFragment = new InterstitialFragment();

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
			
			timerPojo.setStatus(true);

			imgRed.setVisibility(View.VISIBLE);
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(
					Context.VIBRATOR_SERVICE);
			vibrator.vibrate(500);
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			
			imgRed.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					
					
					
					if (((GameActivity) getActivity()).isDialogVisible()) {
						((GameActivity) getActivity()).setHaveLostGame(true);
					} else {
						haveLostGame();
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
	
	public void haveLostGame() {
		NumberGamePreferences.pref_classiclives = NumberGamePreferences.pref_classiclives - 1;
		InterstitialFragment swapFragment = new InterstitialFragment();
		Bundle b = new Bundle();
		b.putLong("timer", mTimerTime);
		swapFragment.setArguments(b);
		getBaseActivity().fragmentTransaction(
				R.id.frame_base_container,
				BaseActivity.REPLACE_FRAGMENT, swapFragment, false,
				InterstitialFragment.TAG_SWAP);
		setShowReplay(true);
	}

	Animation.AnimationListener shakeAnimationListenerwin = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			timerPojo.setStatus(true);

			imgGreen.setVisibility(View.VISIBLE);
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(
					Context.VIBRATOR_SERVICE);
			vibrator.vibrate(500);
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			
			imgGreen.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					
					
					if (((GameActivity) getActivity()).isDialogVisible()) {
						((GameActivity) getActivity()).setTakeToNextLevel(true);
					} else {
						takeToNextLevel();
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
		InterstitialFragment classicFragment = new InterstitialFragment();
		Bundle b = new Bundle();
		mTimerTime = mTimerTime + 3000;
		b.putLong("timer", mTimerTime);
		classicFragment.setArguments(b);

		getBaseActivity().fragmentTransaction(
				R.id.frame_base_container,
				BaseActivity.REPLACE_FRAGMENT, classicFragment,
				false, InterstitialFragment.TAG_SWAP);
		setShowReplay(true);
		imgGreen.setVisibility(View.GONE);
	}


	public void blinkViews(Context context, final View[] views,
			final int[] values, int count) {
		counter = 0;
		Commons.disableView(disable);
		enable(false);
		mHandler.postDelayed(runnable, 500);

	}

	Animation.AnimationListener listener = new Animation.AnimationListener() {
		@Override
		public void onAnimationStart(Animation animation) {
			Log.i("patrol", "Here in listner aniamtion start");
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Log.i("patrol", "Here in listner aniamtion end");
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Log.i("patrol", "Here in listner handler");

					for (int j = 0; j < disable.length; j++) {
						try {
							if (disable[j].isSelected()) {
								disable[j].setSelected(false);
							}
						} catch (NullPointerException e) {
							e.printStackTrace();
						}
					}

					if (counter >= randomNumber.length) {

						Animation animationUtils;
						animationUtils = AnimationUtils.loadAnimation(
								getActivity(), android.R.anim.fade_out);
						animationUtils.setDuration(500);
						animationUtils.setAnimationListener(listenerSwapLogic);
						linClassicParent.startAnimation(animationUtils);
					}

				}
			}, 500);

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}
	};

	// @SWAP
	private void swap() {
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
			swap();

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
		Log.i("Node:", "Here in onPause of Fragment");
		timerPojo.setStartTimer(false);
		stopTimer();
	}

	Animation.AnimationListener shakeAnimationListenerGameOver = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {
			
			timerPojo.setStatus(true);

			imgRed.setVisibility(View.VISIBLE);
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(
					Context.VIBRATOR_SERVICE);
			vibrator.vibrate(500);
			stopTimer();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			
			imgRed.setVisibility(View.GONE);
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					
					setLife();
					bundle.putInt(Commons.FLAG_SETTINGS_TYPE,
							Commons.DIALOG_GAME_OVER);
					customDialog.setArguments(bundle);
					getActivity().getSupportFragmentManager()
							.beginTransaction().add(customDialog, "GameOver")
							.commit();

				}
			}, 400);

		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			

		}

	};

	@Override
	public void onResume() {
		
		super.onResume();
		Log.i("Node:", "Here in onResume of Fragment");

		if (!timerPojo.getStartTimer()) {

			Log.i("Node:",
					"Here in onResume Condition" + timerPojo.getStartTimer());

			timerPojo.setStartTimer(true);
			if (!((GameActivity) getActivity()).isDialogVisible()) {
				startTimer(Commons.TIMER_RESTART);
			}

		}

	}

	public void startTimer(boolean isFresh) {
		if (timerPojo.getStartTimer()) {

			if (!isFresh && NumberGamePreferences.pref_milliseconds != 0) {

				countdownTimerHelper = new CountdownTimerHelper(
						NumberGamePreferences.pref_milliseconds, 1000,
						InterstitialFragment.this, txtTimer, txtLives, getActivity());
				countdownTimerHelper.start();
			} else {

				countdownTimerHelper = new CountdownTimerHelper(mTimerTime,
						1000, InterstitialFragment.this, txtTimer, txtLives,
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

	@Override
	public void onSaveInstanceState(Bundle outState) {

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

		final Animation animationFadeIn = AnimationUtils.loadAnimation(
				getActivity(), android.R.anim.fade_in);
		final Animation animationFadeOut = AnimationUtils.loadAnimation(
				getActivity(), android.R.anim.fade_out);

		linNumpad.setVisibility(View.INVISIBLE);
		linRightOrWrong.setVisibility(View.VISIBLE);

		Animation animation = AnimationUtils.loadAnimation(getActivity(),
				R.anim.shaking);
		animation.setAnimationListener(shakeAnimationListenerwin);

		linClassicParent.startAnimation(animation);
	}

}