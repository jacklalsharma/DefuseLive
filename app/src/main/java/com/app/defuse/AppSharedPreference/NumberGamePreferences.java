package com.app.defuse.AppSharedPreference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by webwerks on 28/2/15.
 */
public class NumberGamePreferences {
	// Preference for classic
	public static int pref_classiclives = 3;
	public static int pref_classiclevel = 3;
	public static int pref_arcade = 3;

	public static final String PREF_BLINKS = "blinks";

	public static long pref_milliseconds;

	public static boolean pref_was_paused = false;

	private String wasShownClassic = "was_shown_classic";
	private String wasShownReverse = "was_shown_reverse";
	private String wasShownArcade = "was_shown_arcade";
	private String wasShownTwistClassic = "was_shown_twist_classic";
	private String wasShownTwistReverse = "was_shown_twist_reverse";
	private String wasShownTwistArcade = "was_shown_twist_arcade";
	private String highScoreClassicKey = "high_score_classic";
	private String highScoreReverseKey = "high_score_reverse";
	private String highScoreTwistClassicKey = "high_score_twist_classic";
	private String highScoreTwistReverseKey = "high_score_twist_reverse";

	private boolean classicShown, reverseShown, arcadeShown, twistClassicShown,
			twistReverseShown, twistArcadeShown;
	private SharedPreferences mSharedPrefrence;
	private SharedPreferences.Editor prefEditor;
	private long highScoreClassic, highScoreReverse, highScoreTwistClassic,
			highScoreTwistReverse;
	private Context mContext;

	public NumberGamePreferences(Context mContext) {
		super();
		this.mContext = mContext;
		mSharedPrefrence = mContext.getSharedPreferences("game_pref",
				Context.MODE_PRIVATE);
		prefEditor = mSharedPrefrence.edit();

	}

	public boolean shouldBlink() {
		return (getBlinks() <= 2);
	}

	public int getBlinks() {
		return mSharedPrefrence.getInt(PREF_BLINKS, 0);
	}

	public void setBlinked() {
		if (!shouldBlink()) {
			prefEditor.putInt(PREF_BLINKS, getBlinks() + 1);
			prefEditor.commit();
		}
	}

	public static NumberGamePreferences getMyPrefs(Context context) {
		return new NumberGamePreferences(context);
	}

	public static void resetLifePrefs(Context context) {
		getMyPrefs(context).pref_classiclives = 3;
		getMyPrefs(context).pref_classiclevel = 3;
	}

	public static void resetArcadeLifePrefs(Context context) {
		getMyPrefs(context).pref_arcade = 3;
	}

	public boolean getShowClassic() {
		return mSharedPrefrence.getBoolean(wasShownClassic, false);
	}

	public boolean getShowReverse() {
		return mSharedPrefrence.getBoolean(wasShownReverse, reverseShown);
	}

	public boolean getShowArcade() {
		return mSharedPrefrence.getBoolean(wasShownArcade, arcadeShown);
	}

	public void setShowClassic() {
		prefEditor.putBoolean(wasShownClassic, true);
		prefEditor.commit();
	}

	public boolean getShowtwistArcade() {
		return mSharedPrefrence.getBoolean(wasShownTwistArcade,
				twistArcadeShown);
	}

	public void setShowTwistArcade() {
		prefEditor.putBoolean(wasShownTwistArcade, true);
		prefEditor.commit();

	}

	public void setShowTwistClassic() {
		prefEditor.putBoolean(wasShownTwistClassic, true);
		prefEditor.commit();

	}

	public boolean getShowTwistClassic() {
		return mSharedPrefrence.getBoolean(wasShownTwistClassic,
				twistClassicShown);
	}

	public boolean getShowTwistReverse() {
		return mSharedPrefrence.getBoolean(wasShownTwistReverse,
				twistReverseShown);
	}

	public void setShowTwistReverse() {
		prefEditor.putBoolean(wasShownTwistReverse, true);
		prefEditor.commit();

	}

	public void setShowReverse(boolean reverseShown) {
		prefEditor.putBoolean(wasShownReverse, reverseShown);
		prefEditor.commit();

	}

	public void setShowArcade(boolean arcadeShown) {
		prefEditor.putBoolean(wasShownArcade, arcadeShown);
		prefEditor.commit();

	}

	public boolean getSoundStatus() {
		return mSharedPrefrence.getBoolean("sound", true);
	}

	public void setSoundStatus(boolean soundStatus) {
		prefEditor.putBoolean("sound", soundStatus);
		prefEditor.commit();
	}

	public boolean getVivrateStatus() {
		return mSharedPrefrence.getBoolean("vibrate", true);
	}

	public void setVibrateStatus(boolean vibrateStatus) {
		prefEditor.putBoolean("vibrate", vibrateStatus);
		prefEditor.commit();
	}

	public void setHighScoreClassic(long highScoreClassic) {
		prefEditor.putLong(highScoreClassicKey, highScoreClassic);
		prefEditor.commit();
	}

	public long getHighScoreClassic() {
		return mSharedPrefrence.getLong(highScoreClassicKey, 0);
	}

	public void setHighScoreReverse(long highScoreReverse) {
		prefEditor.putLong(highScoreReverseKey, highScoreReverse);
		prefEditor.commit();
	}

	public long getHighScoreReverse() {
		return mSharedPrefrence.getLong(highScoreReverseKey, 0);
	}

	public void setHighScoreTwistClassic(long highScoreTwistClassic) {
		prefEditor.putLong(highScoreReverseKey, highScoreTwistClassic);
		prefEditor.commit();
	}

	public long getHighScoreTwistClassic() {
		return mSharedPrefrence.getLong(highScoreTwistClassicKey, 0);
	}

	public void setHighScoreTwistReverse(long highScoreTwistReverse) {
		prefEditor.putLong(highScoreReverseKey, highScoreTwistReverse);
		prefEditor.commit();
	}

	public long getHighScoreTwistReverse() {
		return mSharedPrefrence.getLong(highScoreTwistReverseKey, 0);
	}

}
