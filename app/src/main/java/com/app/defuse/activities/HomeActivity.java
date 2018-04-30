package com.app.defuse.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.base.BaseActivity;
import com.app.defuse.fragments.ClassicFragment;
import com.app.defuse.fragments.HomeFragment;
import com.app.defuse.fragments.ReverseFragment;
import com.app.defuse.fragments.TwistFragment;
import com.app.defuse.fragments.TwistReverseFragment;
import com.app.defuse.helpers.Commons;
import com.crittercism.app.Crittercism;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.minixeroxindia.defuse.R;

/**
 * Created by amit singh on 02-03-2015.
 */
public class HomeActivity extends BaseActivity {


    //Google play services Client ID....
    //65097366863-2cvkhdmuoe7lgtqbb7705oknh152dalh.apps.googleusercontent.com


    private ProgressDialog progressDialog;


    public NumberGamePreferences mSharedPrefs;

    private HomeFragment homeFragment;




	private TextView lblClassic, lblClassicReverse, lblArcade;
	MediaPlayer mediaPlayer;
	private boolean isPaused;
	private int length;
	private NumberGamePreferences mPreferenceHelper;
	private AdView adView;

	public static int REQ_GAME = 1001;
	public static int REQ_ARCADE = 1002;
	public static int REQ_TWIST_ARCADE = 1003;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}


        prizeMoney = 0;
		setContentView(R.layout.activity_home);

		mPreferenceHelper = new NumberGamePreferences(this);

		Crittercism.initialize(getApplicationContext(),
				getString(R.string.app_id));

		homeFragment = new HomeFragment();
		fragmentTransaction(R.id.homeContainer, ADD_FRAGMENT, homeFragment
				, false, null);

		mediaPlayer = MediaPlayer.create(this, R.raw.appmusic);

		adView = (AdView) findViewById(R.id.adView);
		initAdds();


        classicAlreadyPurchased = false;
        classicReverseAlreadyPurchased = false;
        twistAlreadyPurchased = false;
        twistReverseAlreadyPurchased = false;

        mSharedPrefs = new NumberGamePreferences(this);


        setupPrizeMoney();

        if(GoogleSignIn.getLastSignedInAccount(this) == null) {
            GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
            Intent intent = signInClient.getSignInIntent();
            startActivityForResult(intent, 1001);
        }
	}

	public void initAdds() {
		if (Commons.isNetworkOnline(this)) {
			// Request for Ads
			AdRequest adRequest = new AdRequest.Builder().build();

			/*// Add a test device to show Test Ads
					.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
					.addTestDevice(
							Secure.getString(getContentResolver(),
									Secure.ANDROID_ID)).build();*/

			if (adRequest != null) {
				// Load ads into Banner Ads
				adView.loadAd(adRequest);
				adView.setVisibility(View.VISIBLE);
			}

		} else {
			adView.setVisibility(View.GONE);
		}
	}

	public void startGameActivity(int flag) {
		Intent intent = new Intent(this, GameActivity.class);
		intent.putExtra(GameActivity.GAME_TYPE, flag);
		startActivityForResult(intent, REQ_GAME);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_GAME && resultCode == RESULT_OK) {

			/*
			 * startGameActivity(data.getIntExtra(GameActivity.GAME_TYPE,
			 * GameActivity.GAME_CLASSIC));
			 */

			startGameActivity(data.getIntExtra(GameActivity.GAME_TYPE,
					GameActivity.GAME_CLASSIC));

		} else if (requestCode == REQ_ARCADE && resultCode == RESULT_OK) {

			if (data != null) {
				startArcadeActivity(data.getIntExtra("no_of_blinks", 6),
						data.getLongExtra("timer", 20000));
			} else {
				startArcadeActivity();
			}

		} else if (requestCode == REQ_TWIST_ARCADE && resultCode == RESULT_OK) {
			if (data != null) {
				startTwistArcadeActivity(data.getIntExtra("no_of_blinks", 6),
						data.getLongExtra("timer", 20000));
			} else {
				startTwistArcadeActivity();
			}

		}



	}

	public void startArcadeActivity() {
		Intent intent = new Intent(this, ArcadeActivity.class);
		startActivityForResult(intent, REQ_ARCADE);
	}

	public void startTwistArcadeActivity() {
		Intent intent = new Intent(this, TwistArcadeActivity.class);
		startActivityForResult(intent, REQ_TWIST_ARCADE);
	}

	public void startArcadeActivity(int blink, long timer) {
		Intent intent = new Intent(this, ArcadeActivity.class);
		intent.putExtra("no_of_blinks", blink);
		intent.putExtra("timer", timer);
		intent.putExtra(ArcadeActivity.START_DIRECTLY, true);
		startActivityForResult(intent, REQ_ARCADE);
	}

	public void startTwistArcadeActivity(int blink, long timer) {
		Intent intent = new Intent(this, TwistArcadeActivity.class);
		intent.putExtra("no_of_blinks", blink);
		intent.putExtra("timer", timer);
		intent.putExtra(ArcadeActivity.START_DIRECTLY, true);
		startActivityForResult(intent, REQ_TWIST_ARCADE);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (mediaPlayer.isPlaying()) {
			mediaPlayer.pause();
			length = mediaPlayer.getCurrentPosition();
		}
	}

	public void toggleAdds() {
		if (Commons.isNetworkOnline(this)) {
			if (adView.getVisibility() == View.VISIBLE) {

			} else {
				initAdds();
				adView.setVisibility(View.VISIBLE);
			}
		} else {
			adView.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		toggleAdds();

		if (mPreferenceHelper.getSoundStatus()) {
			mediaPlayer.start();
		} else if (isPaused) {
			mediaPlayer.seekTo(length);
			mediaPlayer.start();

		}

	}






    //Start the classic game...
    public void startClassicGame(){
        if (!mSharedPrefs.getShowClassic()) {
            mSharedPrefs.setShowClassic();

            Commons.showTutorialHelper(this,
                    getString(R.string.classic_fragment),
                    ClassicFragment.TAG_CLASSIC, R.id.homeContainer,
                    GameActivity.GAME_CLASSIC);
        } else {
            startGameActivity(GameActivity.GAME_CLASSIC);
        }
    }

    //Start the classic reverse game...
    public void startClassicReverseGame(){
        if (!mSharedPrefs.getShowReverse()) {
				mSharedPrefs.setShowReverse(true);

				Commons.showTutorialHelper(this,
						getString(R.string.classic_reverse_fragment),
						ReverseFragment.TAG_CLASSIC_REVERSE,
						R.id.homeContainer, GameActivity.GAME_CLASSIC_REVERSE);
			} else {
				((HomeActivity) this)
						.startGameActivity(GameActivity.GAME_CLASSIC_REVERSE);
			}
    }

    //Start the twist game...
    public void startTwistGame(){
        if (!mSharedPrefs.getShowTwistClassic()) {
				mSharedPrefs.setShowTwistClassic();;

				Commons.showTutorialHelper(this,
						getString(R.string.swap_fragment),
						TwistFragment.TAG_TWIST, R.id.homeContainer,
						GameActivity.GAME_SWAP);
			} else {
				((HomeActivity) this)
						.startGameActivity(GameActivity.GAME_SWAP);
			}
    }

    //Start the twist game...
    public void startTwistReverseGame(){
        if (!mSharedPrefs.getShowTwistReverse()) {
				mSharedPrefs.setShowTwistReverse();;

				Commons.showTutorialHelper(this,
						getString(R.string.swap_reverse_fragment),
						TwistReverseFragment.TAG_TWIST_REVERSE,
						R.id.homeContainer, GameActivity.GAME_SWAP_REVERSE);
			} else {
				((HomeActivity) this)
						.startGameActivity(GameActivity.GAME_SWAP_REVERSE);
			}
    }

    /**
     *
     * @param type
     * @param purchaseToken
     */
    private void consumePurchase(final int type, final String purchaseToken){
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Using your purchase");
        progressDialog.setMessage("Please wait while we are adding your purchase to total prize money.");
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    //mService.consumePurchase(3, getPackageName(), purchaseToken);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();
                            switch (type){
                                case 1:
                                    startClassicGame();
                                    break;

                                case 2:
                                    startClassicReverseGame();
                                    break;

                                case 3:
                                    startTwistGame();
                                    break;

                                case 4:
                                    startTwistReverseGame();
                                    break;
                            }
                        }
                    });
                }catch (Exception e){

                }
            }
        });
    }

    private void setupPrizeMoney(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference ref = firebaseDatabase.getReference("prize");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String money = dataSnapshot.getValue().toString();
                Log.d("VALUE", dataSnapshot.getValue().toString());
                prizeMoney = Integer.parseInt(money);
                if(prizeMoney < 10000){
                    homeFragment.setPrizeMoney(10000);
                }else{
                    homeFragment.setPrizeMoney(prizeMoney);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updatePrizeMoney(){
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        DatabaseReference ref = firebaseDatabase.getReference("prize");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String money = dataSnapshot.getValue().toString();
                prizeMoney = Integer.parseInt(money);
                Log.d("VALUE", money);
                if(prizeMoney < 10000){
                    homeFragment.setPrizeMoney(10000);
                }else{
                    homeFragment.setPrizeMoney(prizeMoney);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //The purchase is made of only 1$...so updating the total prize money by1$...
        ref.setValue("" + (prizeMoney + 1), new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                Log.d("VALUE", "COMPLETED");
            }
        });
    }

    private void signInSilently() {

        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 1001);
    }



}
