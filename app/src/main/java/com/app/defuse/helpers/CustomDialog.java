package com.app.defuse.helpers;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.activities.ArcadeActivity;
import com.app.defuse.activities.GameActivity;
import com.app.defuse.activities.TwistArcadeActivity;
import com.app.defuse.base.BaseActivity;
import com.minixeroxindia.defuse.R;

/**
 * Created by atuloholic on 01/03/15.
 */
public class CustomDialog extends DialogFragment implements
		View.OnClickListener {

	int mType;
	private static final int FLAG_TYPE_DIALOG_LOSE = 3;
	private static final int FLAG_TYPE_DIALOG_WIN = 2;
	private static final int FLAG_TYPE_DIALOG_PAUSE = 1;
	private static final int FLAG_TYPE_DIALOG_GAME_OVER = 4;
	private static final int FLAG_TYPE_DIALOG_ARCADE_WIN = 5;
	private static final int FLAG_TYPE_DIALOG_ARCADE_LOSE = 6;

	private TextView txtRestart, txtExit, txtResume, txtWinResume,
			txtLossRestart, txtWinRestart, txtWinExit, txtLossExit,
			txtGameOverExit, txtGameOverRestart, txtGameOverBuyCredits,
			txtArcadeWinRestart, txtArcadeLose, txtArcadeExit, txtShareScore;
	Fragment fragment;
	Communicator communicator = null;
	private NumberGamePreferences mSharedPrefs;
	private MediaPlayer mp;

	public static boolean isGameOver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setStyle(DialogFragment.STYLE_NORMAL,
				android.R.style.Theme_Light_NoTitleBar_Fullscreen);

		mp = MediaPlayer.create(getActivity(), R.raw.game_over_sound_effect);

		if (getActivity() instanceof GameActivity) {
			communicator = (GameActivity) getActivity();
		} else if (getActivity() instanceof ArcadeActivity) {
			communicator = (ArcadeActivity) getActivity();
		} else if (getActivity() instanceof TwistArcadeActivity) {
			communicator = (TwistArcadeActivity) getActivity();
		}
		isGameOver = true;

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		// getDialog().getWindow().setBackgroundDrawableResource(R.drawable.dialogbox_popupbg);

		setCancelable(false);
		View view = null;
		// getArguments();
		mType = getArguments().getInt(Commons.FLAG_SETTINGS_TYPE,
				Commons.DIALOG_GAME_OVER);
		mSharedPrefs = new NumberGamePreferences(getActivity());
		switch (mType) {

		case Commons.DIALOG_GAME_OVER:
			view = inflater.inflate(R.layout.dialog_game_over, null);
			intializeGameOverFragment(view);
			break;

		}

		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			if (mp.isPlaying()) {
				mp.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		isGameOver = false;
		try {
			mp.release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void intializeGameOverFragment(View view) {

		if (mSharedPrefs.getSoundStatus()) {
			mp.start();
		}

		txtGameOverRestart = (TextView) view
				.findViewById(R.id.game_over_restart);
		txtGameOverBuyCredits = (TextView) view
				.findViewById(R.id.game_buy_credtis);
		txtGameOverExit = (TextView) view.findViewById(R.id.game_over_exit);
		txtShareScore = (TextView) view.findViewById(R.id.share_score);
		txtGameOverRestart.setOnClickListener(this);
		txtGameOverBuyCredits.setOnClickListener(this);
		txtGameOverExit.setOnClickListener(this);
		txtShareScore.setOnClickListener(this);
		Commons.setFonts(getActivity(),
				view.findViewById(R.id.txtGameOverHeading));
		Commons.setFonts(getActivity(), txtShareScore);

		final int gameTYpe = getArguments().getInt(Commons.GAME_TYPE, 0);
		long score = 0;
		long curr_score = getArguments().getLong(Commons.SCORE, 0);
		switch (gameTYpe){
			case 0:
				score = mSharedPrefs.getHighScoreClassic();
				break;

			case 1:
				score = mSharedPrefs.getHighScoreReverse();
				break;

			case 2:
				score = mSharedPrefs.getHighScoreTwistClassic();
				break;

			case 3:
				score = mSharedPrefs.getHighScoreTwistReverse();
				break;
		}

        ((TextView) view.findViewById(R.id.high_score)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.high_score)).setText("HIGH SCORE - " + score);

        ((TextView) view.findViewById(R.id.curr_score)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.curr_score)).setText("CURRENT SCORE - " + curr_score);


        ((TextView) view.findViewById(R.id.update_score)).setVisibility(View.VISIBLE);
        ((TextView) view.findViewById(R.id.update_score)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadScore(gameTYpe);
            }
        });

        view.findViewById(R.id.share_score).setVisibility(View.GONE);
    }

	@Override
	public void onResume() {
		super.onResume();
		getDialog().getWindow().setLayout(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		case R.id.game_buy_credtis:
			//
			break;
		case R.id.game_over_exit:
			communicator.exit();
			break;
		case R.id.game_over_restart:

			communicator.gameOverRestart();
			break;
		case R.id.share_score:
			communicator.shareScore();
			return;
		}

		dismiss();

	}

	public interface Communicator {
		public void update();

		public void next();

		public void exit();

		public void resume();

		public void lossNext();

		public void gameOverRestart();

		public void shareScore();
	}

    public void uploadScore(int gameType){
        long score = 0;

        NumberGamePreferences mSharedPrefs = new NumberGamePreferences(getActivity());
        BaseActivity activity = (BaseActivity) getActivity();

        switch (gameType){
            case 0:
                if(!activity.isClassicAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }
                score = mSharedPrefs.getHighScoreClassic();
                break;

            case 1:

                if(!activity.isClassicReverseAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }

                score = mSharedPrefs.getHighScoreReverse();
                break;

            case 2:


                if(!activity.isTwistAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }
                score = mSharedPrefs.getHighScoreTwistClassic();
                break;

            case 3:

                if(!activity.isTwistReverseAlreadyPurchased()){
                    //Need to purchase classic to upload high score...
                    showPurchaseDialog(gameType);
                    return;
                }

                score = mSharedPrefs.getHighScoreTwistReverse();
                break;
        }

        GameActivity activity1 = (GameActivity) getActivity();
        if(activity1 != null) {
            activity1.fbShare(score);
        }

    }

    private void showPurchaseDialog(final int gameType){
        final Dialog mDialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_purchase, null);

        dialogView.findViewById(R.id.game_over_exit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog != null){
                    mDialog.dismiss();
                }
            }
        });

        dialogView.findViewById(R.id.join_contest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mDialog != null){
                    mDialog.dismiss();
                }
                initPurchase(gameType);
            }
        });

        new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
        mDialog.setContentView(dialogView);
        mDialog.show();
    }

    public void initPurchase(int gameType){
        GameActivity activity = (GameActivity) getActivity();
        if(activity == null){
            return;
        }

        switch (gameType){
            case 0:
                activity.checkClassicPurchase();
                break;

            case 1:
                activity.checkClassicReversePurchase();
                break;

            case 2:
                activity.checkTwistPurchase();
                break;

            case 3:
                activity.checkTwistReversePurchase();
                break;
        }
    }

}
