package com.app.defuse.helpers;

import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.activities.GameActivity;
import com.app.defuse.fragments.ArcadeFragment;
import com.app.defuse.fragments.ClassicFragment;
import com.app.defuse.fragments.ReverseFragment;
import com.app.defuse.fragments.TwistFragment;
import com.app.defuse.fragments.TwistReverseFragment;
import com.minixeroxindia.defuse.R;

/**
 * Created by webwerks on 28/2/15.
 */
public class CountdownTimerHelper extends CountDownTimer {

	TextView textviewTimer;
	Context mContext;
	private Fragment frag;
	MediaPlayer mediaPlayer;
	NumberGamePreferences mAppPrefs;

	public CountdownTimerHelper(long millisInFuture, long countDownInterval,
			Fragment frag, TextView textviewTimer, TextView textViewLives,
			Context context) {
		super(millisInFuture, countDownInterval);
		this.textviewTimer = textviewTimer;

		this.mContext = context;
		this.frag = frag;
		mediaPlayer = MediaPlayer.create(mContext, R.raw.tick_sound);
		mAppPrefs = new NumberGamePreferences(mContext);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		{

			if (frag instanceof ClassicFragment) {
				((ClassicFragment) frag).setHasTimerStarted(true);
			} else if (frag instanceof ReverseFragment) {
				((ReverseFragment) frag).setHasTimerStarted(true);
			} else if (frag instanceof TwistFragment) {
				((TwistFragment) frag).setHasTimerStarted(true);
			} else if (frag instanceof TwistReverseFragment) {
				((TwistReverseFragment) frag).setHasTimerStarted(true);
			} else if (frag instanceof ArcadeFragment) {
				((ArcadeFragment) frag).setHasTimerStarted(true);
			}

			if (textviewTimer != null) {

				if (mAppPrefs.getSoundStatus()) {
					mediaPlayer.start();
					textviewTimer
							.setText(""
									+ String.format(
											"%02d : %02d",
											TimeUnit.MILLISECONDS
													.toMinutes(millisUntilFinished),
											TimeUnit.MILLISECONDS
													.toSeconds(millisUntilFinished)
													- TimeUnit.MINUTES
															.toSeconds(TimeUnit.MILLISECONDS
																	.toMinutes(millisUntilFinished))));
					NumberGamePreferences.pref_milliseconds = millisUntilFinished;
				} else {
					textviewTimer
							.setText(""
									+ String.format(
											"%02d : %02d",
											TimeUnit.MILLISECONDS
													.toMinutes(millisUntilFinished),
											TimeUnit.MILLISECONDS
													.toSeconds(millisUntilFinished)
													- TimeUnit.MINUTES
															.toSeconds(TimeUnit.MILLISECONDS
																	.toMinutes(millisUntilFinished))));
					NumberGamePreferences.pref_milliseconds = millisUntilFinished;
				}

			}
		}
	}

	@Override
	public void onFinish() {
		if (textviewTimer != null) {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
			}
			textviewTimer.setText("00:00");

			if (NumberGamePreferences.pref_classiclives <= 1) {

				if (frag instanceof ClassicFragment) {
					((ClassicFragment) frag).setLife();
				} else if (frag instanceof ReverseFragment) {
					((ReverseFragment) frag).setLife();
				} else if (frag instanceof TwistFragment) {
					((TwistFragment) frag).setLife();
				} else if (frag instanceof TwistReverseFragment) {
					((TwistReverseFragment) frag).setLife();
				} else if (frag instanceof ArcadeFragment) {
					((ArcadeFragment) frag).setLife();
				}

				CustomDialog customDialog = new CustomDialog();
				Bundle bundle = new Bundle();
				bundle.putInt(Commons.FLAG_SETTINGS_TYPE,
						Commons.DIALOG_GAME_OVER);
				customDialog.setArguments(bundle);
				((GameActivity) mContext).getSupportFragmentManager()
						.beginTransaction().add(customDialog, "GameOver")
						.commit();

			} else if (frag instanceof ClassicFragment) {
				((ClassicFragment) frag).lossSetFragment();
			} else if (frag instanceof ReverseFragment) {
				((ReverseFragment) frag).lossSetFragment();
			} else if (frag instanceof ArcadeFragment) {
				((ArcadeFragment) frag).lossSetFragment();
			} else if (frag instanceof TwistFragment) {
				((TwistFragment) frag).lossSetFragment();
			} else if (frag instanceof TwistReverseFragment) {
				((TwistReverseFragment) frag).lossSetFragment();
			}

		}
	}
}
