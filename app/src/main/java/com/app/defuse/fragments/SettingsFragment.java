package com.app.defuse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.base.BaseFragment;
import com.app.defuse.helpers.Commons;
import com.minixeroxindia.defuse.R;

public class SettingsFragment extends BaseFragment implements
		OnCheckedChangeListener {

	public final static String TAG_SETTINGS = "settingFragment";
	// Button btnVolumeOn, btnVolumeOff, btnVibrationOn, btnVibrationOff;
	private ToggleButton toggleSound, toggleVibration;
	private NumberGamePreferences mPrefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		setContentResource(R.layout.fragment_set);

		mPrefs = new NumberGamePreferences(getActivity());
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void basicInitialization() {

	}

	@Override
	public void initializeComponent(View mRootView) {

		Commons.setFonts(getActivity(), mRootView.findViewById(R.id.lblTitle));
		Commons.setFonts(getActivity(),
				mRootView.findViewById(R.id.lblTogleFirst));
		Commons.setFonts(getActivity(),
				mRootView.findViewById(R.id.lblTogleSecond));

		toggleSound = (ToggleButton) mRootView.findViewById(R.id.toggleSound);
		toggleVibration = (ToggleButton) mRootView
				.findViewById(R.id.toggleVibrate);

		if (mPrefs.getSoundStatus()) {
			toggleSound.setChecked(true);
		} else {
			toggleSound.setChecked(false);
		}

		if (mPrefs.getVivrateStatus()) {
			toggleVibration.setChecked(true);
		} else {
			toggleVibration.setChecked(false);
		}

	}

	@Override
	public void setListeners() {
		toggleSound.setOnCheckedChangeListener(this);
		toggleVibration.setOnCheckedChangeListener(this);
	}

	@Override
	public void onCheckedChanged(CompoundButton v, boolean isChecked) {

		if (v.getId() == R.id.toggleSound) {
			mPrefs.setSoundStatus(toggleSound.isChecked());
		} else if (v.getId() == R.id.toggleVibrate) {
			mPrefs.setVibrateStatus(toggleVibration.isChecked());
		}

	}

}
