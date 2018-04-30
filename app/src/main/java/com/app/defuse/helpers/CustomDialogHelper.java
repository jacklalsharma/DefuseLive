package com.app.defuse.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.app.defuse.AppSharedPreference.NumberGamePreferences;
import com.app.defuse.fragments.ClassicFragment;


/**
 * Created by webwerks on 28/2/15.
 */
public class CustomDialogHelper {
    String message, buttonOneText, buttonTwoText, buttonThreeText;
    int icon, levels;
    Context context;
    ClassicFragment fragment;

    public CustomDialogHelper(String message, int icon, String buttonOneText, String buttonTwoText, String buttonThreeText, Context context, ClassicFragment fragment) {
        this.message = message;
        this.icon = icon;
        this.context = context;
        this.levels = levels;
        this.fragment = fragment;
        this.buttonOneText = buttonOneText;
        this.buttonTwoText = buttonTwoText;
        this.buttonThreeText = buttonThreeText;
    }

    public void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(message);
        builder.setIcon(icon);
        builder.setPositiveButton(buttonOneText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Next level
                NumberGamePreferences.pref_classiclevel += 1;
                Log.i("Levels", "" + NumberGamePreferences.pref_classiclevel);
                fragment = new ClassicFragment();
//                fragment.updateUI();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(buttonTwoText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Exit to home screen

            }
        });
        builder.setNeutralButton(buttonThreeText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Retry the same level
            }
        });
        builder.show();
//        builder.setCancelable(true);
    }

}
