package com.app.defuse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.activities.ArcadeActivity;
import com.app.defuse.base.BaseFragment;
import com.app.defuse.helpers.Commons;
import com.minixeroxindia.defuse.R;

public class TwistArcadeGameFragment extends BaseFragment implements
		OnClickListener {

	Button btnSixBlinks, btnSevenBlinks, btnEightBlinks, btnNineBlinks,
			btnTenBlinks, btnElevenBlinks, btnTwelveBlinks, btnThirteenBlinks,
			btnFourteenBlinks, btnFifteenBlinks;
	public final static String TAG_CLASSIC_ARCADE_GAME = "arcadegameFragment";
	public NumberGamePreferences mSharedPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mSharedPrefs = new NumberGamePreferences(getActivity());
		if (!mSharedPrefs.getShowtwistArcade()) {
			mSharedPrefs.setShowTwistArcade();

			Commons.showTutorial(getActivity(),
					getString(R.string.arcade_fragment),
					TwistArcadeGameFragment.TAG_CLASSIC_ARCADE_GAME,
					R.id.arcade_container);

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentResource(R.layout.fragment_arcade_game);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		if (getArguments() != null
				&& getArguments().getBoolean(ArcadeActivity.START_DIRECTLY,
						false)) {
			startArcadeGame(getArguments().getInt("no_of_blinks", 6),
					getArguments().getLong("timer", 20000));
		}
	}

	@Override
	protected void basicInitialization() {

	}

	@Override
	public void initializeComponent(View rootView) {
		// TODO Auto-generated method stub

		btnSixBlinks = (Button) rootView.findViewById(R.id.btn_six_blinks);
		btnSevenBlinks = (Button) rootView.findViewById(R.id.btn_seven_blinks);
		btnEightBlinks = (Button) rootView.findViewById(R.id.btn_eight_blinks);
		btnNineBlinks = (Button) rootView.findViewById(R.id.btn_nine_blinks);
		btnTenBlinks = (Button) rootView.findViewById(R.id.btn_ten_blinks);
		btnElevenBlinks = (Button) rootView
				.findViewById(R.id.btn_eleven_blinks);
		btnTwelveBlinks = (Button) rootView
				.findViewById(R.id.btn_twelve_blinks);
		btnThirteenBlinks = (Button) rootView
				.findViewById(R.id.btn_thirteen_blinks);
		btnFourteenBlinks = (Button) rootView
				.findViewById(R.id.btn_fourteen_blinks);
		btnFifteenBlinks = (Button) rootView
				.findViewById(R.id.btn_fifteen_blinks);

		// Set Typeface
		Commons.setFonts(getActivity(), btnSixBlinks);
		Commons.setFonts(getActivity(), btnSevenBlinks);
		Commons.setFonts(getActivity(), btnEightBlinks);
		Commons.setFonts(getActivity(), btnNineBlinks);
		Commons.setFonts(getActivity(), btnTenBlinks);
		Commons.setFonts(getActivity(), btnElevenBlinks);
		Commons.setFonts(getActivity(), btnTwelveBlinks);
		Commons.setFonts(getActivity(), btnThirteenBlinks);
		Commons.setFonts(getActivity(), btnFourteenBlinks);
		Commons.setFonts(getActivity(), btnFifteenBlinks);
	}

	@Override
	public void setListeners() {
		btnSixBlinks.setOnClickListener(this);
		btnSevenBlinks.setOnClickListener(this);
		btnEightBlinks.setOnClickListener(this);
		btnNineBlinks.setOnClickListener(this);
		btnTenBlinks.setOnClickListener(this);
		btnElevenBlinks.setOnClickListener(this);
		btnTwelveBlinks.setOnClickListener(this);
		btnThirteenBlinks.setOnClickListener(this);
		btnFourteenBlinks.setOnClickListener(this);
		btnFifteenBlinks.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_six_blinks:
			startArcadeGame(6, 20000);
			break;
		case R.id.btn_seven_blinks:

			startArcadeGame(7, 23000);
			break;
		case R.id.btn_eight_blinks:

			startArcadeGame(8, 26000);
			break;
		case R.id.btn_nine_blinks:

			startArcadeGame(9, 29000);

			break;
		case R.id.btn_ten_blinks:

			startArcadeGame(10, 32000);

			break;
		case R.id.btn_eleven_blinks:

			startArcadeGame(11, 35000);

			break;
		case R.id.btn_twelve_blinks:

			startArcadeGame(12, 38000);

			break;
		case R.id.btn_thirteen_blinks:

			startArcadeGame(13, 41000);

			break;
		case R.id.btn_fourteen_blinks:

			startArcadeGame(15, 44000);

			break;
		case R.id.btn_fifteen_blinks:

			startArcadeGame(15, 47000);

			break;

		}

	}

	private void startArcadeGame(int numBlinks, long timer) {
		Bundle bundle = new Bundle();
		TwistArcadeFragment arcadeFragment = new TwistArcadeFragment();
		bundle.putInt("no_of_blinks", numBlinks);
		bundle.putLong("timer", timer);
		arcadeFragment.setArguments(bundle);
		getBaseActivity().fragmentTransaction(R.id.arcade_container, 1,
				arcadeFragment, true, TwistArcadeFragment.TAG_CLASSIC_ARCADE);

	}
}
