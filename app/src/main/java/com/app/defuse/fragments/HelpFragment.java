package com.app.defuse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.app.defuse.base.BaseFragment;
import com.app.defuse.helpers.Commons;
import com.minixeroxindia.defuse.R;

public class HelpFragment extends BaseFragment implements OnClickListener {

	public final static String TAG_HELP = "helpFragment";
	private Button btnClassic, btnReverseClassic, btnArcade, btnTwist,
			btnTwistRev, btnTwistArcade;
	private LinearLayout btnReplay;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentResource(R.layout.fragment_help);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void basicInitialization() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeComponent(View mRootView) {

		btnClassic = (Button) mRootView.findViewById(R.id.btn_classic_fragment);
		btnReverseClassic = (Button) mRootView
				.findViewById(R.id.btn_reverse_fragment);
		btnArcade = (Button) mRootView.findViewById(R.id.btn_arcade_fragment);
		btnTwist = (Button) mRootView.findViewById(R.id.btn_twist_fragment);
		btnTwistRev = (Button) mRootView
				.findViewById(R.id.btn_twist_rev_fragment);
		btnTwistArcade = (Button) mRootView.findViewById(R.id.btn_twist_arcade);
		btnReplay = (LinearLayout) mRootView.findViewById(R.id.btn_replay);
		
		Commons.setFonts(getActivity(), mRootView.findViewById(R.id.txtReplay));
		Commons.setFonts(getActivity(), btnClassic);
		Commons.setFonts(getActivity(), btnArcade);
		Commons.setFonts(getActivity(), btnReverseClassic);
		Commons.setFonts(getActivity(), btnTwist);
		Commons.setFonts(getActivity(), btnTwistRev);
		Commons.setFonts(getActivity(), btnTwistArcade);
	}

	@Override
	public void setListeners() {
		btnClassic.setOnClickListener(this);
		btnReverseClassic.setOnClickListener(this);
		btnArcade.setOnClickListener(this);
		btnTwist.setOnClickListener(this);
		btnTwistArcade.setOnClickListener(this);
		btnTwistRev.setOnClickListener(this);
		btnReplay.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.btn_classic_fragment:
			Commons.showTutorial(getActivity(),
					getString(R.string.classic_fragment),
					ClassicFragment.TAG_CLASSIC, R.id.frame_base_container);
			break;
		case R.id.btn_reverse_fragment:
			Commons.showTutorial(getActivity(),
					getString(R.string.classic_reverse_fragment),
					ReverseFragment.TAG_CLASSIC_REVERSE,
					R.id.frame_base_container);
			break;
		case R.id.btn_arcade_fragment:
			Commons.showTutorial(getActivity(),
					getString(R.string.arcade_fragment),
					ArcadeGameFragment.TAG_CLASSIC_ARCADE_GAME,
					R.id.frame_base_container);
			break;
		case R.id.btn_twist_fragment:
			Commons.showTutorial(getActivity(),
					getString(R.string.swap_fragment), TwistFragment.TAG_TWIST,
					R.id.frame_base_container);
			break;
		case R.id.btn_twist_rev_fragment:
			Commons.showTutorial(getActivity(),
					getString(R.string.swap_reverse_fragment),
					TwistReverseFragment.TAG_TWIST_REVERSE,
					R.id.frame_base_container);
			break;
		case R.id.btn_twist_arcade:
			Commons.showTutorial(getActivity(),
					getString(R.string.arcade_fragment),
					TwistArcadeGameFragment.TAG_CLASSIC_ARCADE_GAME,
					R.id.frame_base_container);
			break;
		case R.id.btn_replay:
			Commons.showTutorial(getActivity(), getString(R.string.replay),
					"Replay", R.id.frame_base_container);
			break;
		}
	}

}
