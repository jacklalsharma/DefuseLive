package com.app.defuse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.defuse.base.BaseFragment;
import com.app.defuse.helpers.Commons;
import com.minixeroxindia.defuse.R;

public class CreditsFragment extends BaseFragment {

	public final static String TAG_CREDITS = "creditsFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setContentResource(R.layout.fragment_credits);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected void basicInitialization() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initializeComponent(View mRootView) {

		Commons.setFonts(getActivity(),
				mRootView.findViewById(R.id.txtCreditsText));

	}

	@Override
	public void setListeners() {

	}

}
