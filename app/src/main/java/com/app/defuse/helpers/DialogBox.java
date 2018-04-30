package com.app.defuse.helpers;



import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by anurag on 21/02/18.
 */
public class DialogBox {

    private static boolean isAlertDialogShowing;

    public interface DialogBoxButtonClickListener{
        void onPositiveButtonClicked();
        void onNegativeButtonCLicked();
    }

    /**
     * Method to show alert dialog with one button.
     * @param context
     * @param title
     * @param message
     * @param btnText
     * @param dialogBoxButtonClickListener
     */
    public static void ShowAlertDialogOneButton(Context context, String title, String message, String btnText, final DialogBoxButtonClickListener dialogBoxButtonClickListener){
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(btnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogBoxButtonClickListener != null) {
                    dialogBoxButtonClickListener.onPositiveButtonClicked();
                }
                alertBuilder.create().dismiss();            }
        });
        AlertDialog dialog = alertBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
        isAlertDialogShowing = true;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isAlertDialogShowing = false;
            }
        });
    }

    /**
     * Method to show alert dialog with two button.
     *
     * @param context
     * @param title
     * @param message
     * @param positiveBtnText
     * @param negativeBtnText
     * @param dialogBoxButtonClickListener
     */
    public static void ShowAlertDialogTwoButton(Context context, String title, String message, String positiveBtnText, String negativeBtnText,
                                                final DialogBoxButtonClickListener dialogBoxButtonClickListener){
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setTitle(title);
        alertBuilder.setMessage(message);
        alertBuilder.setPositiveButton(positiveBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogBoxButtonClickListener != null) {
                    dialogBoxButtonClickListener.onPositiveButtonClicked();
                }
                alertBuilder.create().dismiss();
            }
        });

        alertBuilder.setNegativeButton(negativeBtnText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogBoxButtonClickListener != null){
                    dialogBoxButtonClickListener.onNegativeButtonCLicked();
                }
                alertBuilder.create().dismiss();
            }
        });

        AlertDialog dialog = alertBuilder.create();
        dialog.setCancelable(false);
        dialog.show();
        isAlertDialogShowing = true;
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                isAlertDialogShowing = false;
            }
        });
    }

    public static boolean IsAlertDialogShowing(){
        return isAlertDialogShowing;
    }


}