package com.app.defuse.helpers;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.app.defuse.activities.GameActivity;
import com.app.defuse.activities.HomeActivity;
import com.minixeroxindia.defuse.R;

public class TutorialDialog extends DialogFragment implements OnClickListener {

	String txtMsg;
	TextView tvMsg;
	Button btnOk;
	private int mType = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Light_NoTitleBar_Fullscreen);

		if (getArguments() != null) {
			mType = getArguments().getInt(GameActivity.GAME_TYPE);
		}

	}

	public boolean isDialogVisible() {
		if (getDialog().isShowing()) {
			return true;
		} else
			return false;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		txtMsg = getArguments().getString("msg");

		setCancelable(false);
		View view = null;
		view = inflater.inflate(R.layout.dialog_tutorial, null);
		initializeTutorial(view);
		return view;
	}

	private void initializeTutorial(View view) {
		// TODO Auto-generated method stub
		tvMsg = (TextView) view.findViewById(R.id.txt_tutorial);
		btnOk = (Button) view.findViewById(R.id.btn_tutorial_ok);

		Commons.setFonts(getActivity(), tvMsg);
		Commons.setFonts(getActivity(), btnOk);

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

			if (mType == GameActivity.GAME_CLASSIC) {
				((HomeActivity) getActivity())
						.startGameActivity(GameActivity.GAME_CLASSIC);
			}
			// communicator.exitTutorialDialog(fragment);
			getFragmentManager().popBackStackImmediate();
		}
	}

	public interface TutorialCommunicator {
		public void exitTutorialDialog(int type);
	}

}
