package com.app.defuse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.defuse.base.BaseFragment;
import com.minixeroxindia.defuse.R;

/**
 * Created by atuloholic on 27/02/15.
 */
public class SplashFragment extends BaseFragment {

	View rootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_splash, null);

		return view;
	}

	@Override
	protected void basicInitialization() {

	}

	@Override
	public void initializeComponent(View mRootView) {

	}

	@Override
	public void setListeners() {

	}

}
