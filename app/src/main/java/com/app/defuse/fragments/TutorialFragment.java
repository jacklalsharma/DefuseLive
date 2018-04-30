package com.app.defuse.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.defuse.base.BaseFragment;
import com.minixeroxindia.defuse.R;

/**
 * Created by atuloholic on 27/02/15.
 */
public class TutorialFragment extends BaseFragment implements
		View.OnClickListener {

	// UI Components
	String txtMsg;
	TextView tvMsg;
	Button btnOk;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		txtMsg = getArguments().getString("msg");

		View view = null;
		view = inflater.inflate(R.layout.dialog_tutorial, null);
		initializeTutorial(view);
		return view;
	}

	private void initializeTutorial(View view) {
		// TODO Auto-generated method stub
		tvMsg = (TextView) view.findViewById(R.id.txt_tutorial);
		btnOk = (Button) view.findViewById(R.id.btn_tutorial_ok);

		tvMsg.setText(txtMsg);
		setListners();
	}

	private void setListners() {
		// TODO Auto-generated method stub
		btnOk.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v.getId() == R.id.btn_tutorial_ok) {

			// communicator.exitTutorialDialog(fragment);
			getFragmentManager().popBackStackImmediate();
		}
	}

	public interface TutorialCommunicator {
		public void exitTutorialDialog(int type);
	}

	@Override
	protected void basicInitialization() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void initializeComponent(View mRootView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setListeners() {
		// TODO Auto-generated method stub
		
	}

}