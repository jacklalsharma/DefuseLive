package com.app.defuse.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.activities.GameActivity;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.games.Games;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.minixeroxindia.defuse.R;

/**
 * Created by atuloholic on 27/02/15.
 */
public abstract  class BaseFragment extends Fragment {
    private View mRootView;
    private BaseActivity mActivity;
    private int resID = 0;

    private Dialog mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        if (resID != 0) {
            mRootView = inflater.inflate(resID, container, false);
        } else {
            throw new IllegalStateException(
                    "Layout Resource not specified : Call setContentResource(your layout Resource ID) before calling super.onCreateView()");
        }
        basicInitialization();
        initializeComponent(mRootView);
        setListeners();

        return mRootView;

    }

    protected abstract void basicInitialization();

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (BaseActivity) activity;
    }
    
   
    public BaseActivity getBaseActivity() {
        if(mActivity != null){
            return mActivity;
        }

        return null;
    }


    public abstract void initializeComponent(View mRootView);
    public abstract void setListeners();

    protected void setContentResource(int layoutResID) {
        resID = layoutResID;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    protected void showLeaderBoard(){
        Log.d("LEADERBOARD", "SHOW");
        Games.getLeaderboardsClient(getActivity(), GoogleSignIn.getLastSignedInAccount(getActivity()))
                .getLeaderboardIntent("CgkIz_rrwPIBEAIQAQ")
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                })
                .addOnSuccessListener(new OnSuccessListener<Intent>() {
                    @Override
                    public void onSuccess(Intent intent) {
                        startActivityForResult(intent, 101);
                    }

                });
    }



    protected void initScoreClicks(View mRootView, final int gameType){
        mRootView.findViewById(R.id.click_high_score).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameActivity activity = (GameActivity) getActivity();
                activity.uploadScore(gameType);
            }
        });

        mRootView.findViewById(R.id.click_current_score).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLeaderBoard();
            }
        });
    }

    private void showPurchaseDialog(final int gameType){

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

        mDialog = new Dialog(getActivity(), android.R.style.Theme_Light_NoTitleBar_Fullscreen);
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

