package com.app.defuse.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.activities.ArcadeActivity;
import com.app.defuse.activities.GameActivity;
import com.app.defuse.activities.HomeActivity;
import com.app.defuse.base.BaseFragment;
import com.app.defuse.helpers.Commons;
import com.minixeroxindia.defuse.R;

/**
 * Created by atuloholic on 27/02/15.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener {
	private TextView lblClassic, lblClassicReverse, lblArcade, lblShare,
			lblSwap, lblSettings, lblSwapArcade, lblSwapReverse, lblCredits,
			lblHelp, lblRateUs;
	private ImageView igPlayButton;
	public NumberGamePreferences mSharedPrefs;
	private TextView prizeMoney;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentResource(R.layout.fragment_home);

		mSharedPrefs = new NumberGamePreferences(getActivity());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void basicInitialization() {

	}

	@Override
	public void initializeComponent(View rootView) {
		lblClassic = (TextView) rootView.findViewById(R.id.lbl_classic);
		lblClassicReverse = (TextView) rootView
				.findViewById(R.id.lbl_classic_reverse);
		lblRateUs = (TextView) rootView.findViewById(R.id.lbl_rate_us);
		lblArcade = (TextView) rootView.findViewById(R.id.lbl_classic_arcade);
		lblShare = (TextView) rootView.findViewById(R.id.lbl_share);
		lblSwap = (TextView) rootView.findViewById(R.id.lblTwist);
		lblSwapArcade = (TextView) rootView.findViewById(R.id.lblTwistArcade);
		lblSwapReverse = (TextView) rootView.findViewById(R.id.lblTwistReverse);
		lblSettings = (TextView) rootView.findViewById(R.id.lbl_settings);
		lblCredits = (TextView) rootView.findViewById(R.id.lbl_credits);
		lblHelp = (TextView) rootView.findViewById(R.id.lbl_help_tutorials);
		igPlayButton = (ImageView) rootView.findViewById(R.id.igv_play);

		Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(),
				"fonts/skranji_bold.ttf");

		prizeMoney = rootView.findViewById(R.id.prizeMoney);
		prizeMoney.setTypeface(typeface);

		Commons.setFonts(getActivity(), lblHelp);
		Commons.setFonts(getActivity(), lblRateUs);

		rootView.findViewById(R.id.lbl_leaderboard).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showLeaderBoard();
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.app.defuse.base.BaseFragment#setListeners()
	 */
	@Override
	public void setListeners() {
		lblClassic.setOnClickListener(this);
		lblClassicReverse.setOnClickListener(this);
		lblArcade.setOnClickListener(this);
		lblShare.setOnClickListener(this);
		lblSwap.setOnClickListener(this);
		lblSwapArcade.setOnClickListener(this);
		lblSwapReverse.setOnClickListener(this);
		lblSettings.setOnClickListener(this);
		lblCredits.setOnClickListener(this);
		lblHelp.setOnClickListener(this);
		igPlayButton.setOnClickListener(this);
		lblRateUs.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lbl_classic:

			((HomeActivity) getActivity()).startClassicGame();

//			if(!((HomeActivity) getActivity()).checkClassicPurchase()){
//				return;
//			}

//			if (!mSharedPrefs.getShowClassic()) {
//				mSharedPrefs.setShowClassic();
//
//				Commons.showTutorialHelper(getActivity(),
//						getString(R.string.classic_fragment),
//						ClassicFragment.TAG_CLASSIC, R.id.homeContainer,
//						GameActivity.GAME_CLASSIC);
//			} else {
//				((HomeActivity) getActivity())
//						.startGameActivity(GameActivity.GAME_CLASSIC);
//			}

			break;
		case R.id.lbl_classic_reverse:
			((HomeActivity) getActivity()).startClassicReverseGame();

			//((HomeActivity) getActivity()).checkClassicReversePurchase();
//			if (!mSharedPrefs.getShowReverse()) {
//				mSharedPrefs.setShowReverse(true);
//
//				Commons.showTutorialHelper(getActivity(),
//						getString(R.string.classic_reverse_fragment),
//						ReverseFragment.TAG_CLASSIC_REVERSE,
//						R.id.homeContainer, GameActivity.GAME_CLASSIC_REVERSE);
//			} else {
//				((HomeActivity) getActivity())
//						.startGameActivity(GameActivity.GAME_CLASSIC_REVERSE);
//			}
			break;
		case R.id.lbl_classic_arcade:


			  Intent intent = new Intent(getActivity(), ArcadeActivity.class);
			  startActivity(intent);


			//((HomeActivity) getActivity()).startClassicGame();

			//((HomeActivity) getActivity()).startArcadeActivity();

			break;
		case R.id.lblTwist:
			((HomeActivity) getActivity()).startTwistGame();


			//((HomeActivity) getActivity()).checkTwistPurchase();

//			if (!mSharedPrefs.getShowTwistClassic()) {
//				mSharedPrefs.setShowTwistClassic();;
//
//				Commons.showTutorialHelper(getActivity(),
//						getString(R.string.swap_fragment),
//						TwistFragment.TAG_TWIST, R.id.homeContainer,
//						GameActivity.GAME_SWAP);
//			} else {
//				((HomeActivity) getActivity())
//						.startGameActivity(GameActivity.GAME_SWAP);
//			}

			break;
		case R.id.lblTwistArcade:
			/*
			 * Intent intentTwist = new Intent(getActivity(),
			 * TwistArcadeActivity.class); startActivity(intentTwist);
			 */

			((HomeActivity) getActivity()).startTwistArcadeActivity();

			break;

		case R.id.lblTwistReverse:

			((HomeActivity) getActivity()).startTwistReverseGame();


			//((HomeActivity) getActivity()).checkTwistReversePurchase();

//			if (!mSharedPrefs.getShowTwistReverse()) {
//				mSharedPrefs.setShowTwistReverse();;
//
//				Commons.showTutorialHelper(getActivity(),
//						getString(R.string.swap_reverse_fragment),
//						TwistReverseFragment.TAG_TWIST_REVERSE,
//						R.id.homeContainer, GameActivity.GAME_SWAP_REVERSE);
//			} else {
//				((HomeActivity) getActivity())
//						.startGameActivity(GameActivity.GAME_SWAP_REVERSE);
//			}

			break;

		case R.id.lbl_share:
			Commons.appShareIntent(getActivity());
			break;

		case R.id.lbl_settings:
			((HomeActivity) getActivity())
					.startGameActivity(GameActivity.GAME_SETTINGS);
			break;
		case R.id.lbl_credits:
			((HomeActivity) getActivity())
					.startGameActivity(GameActivity.GAME_CREDITS);
			break;

		case R.id.lbl_help_tutorials:
			((HomeActivity) getActivity())
					.startGameActivity(GameActivity.GAME_HELP);
			break;
		case R.id.igv_play:
			Commons.startPLayStore(getActivity(),
					getString(R.string.ap_promotion));
			break;
		case R.id.lbl_rate_us:
			Commons.startPLayStore(getActivity(), getActivity()
					.getPackageName());
			/*
			 * Intent rateIntent = new Intent(getActivity(),
			 * RateUsActivity.class); startActivity(rateIntent);
			 */

			break;

		}

	}

	public void setPrizeMoney(int prize){
        prizeMoney.setText("" + prize + "$ to WIN");
    }
}
