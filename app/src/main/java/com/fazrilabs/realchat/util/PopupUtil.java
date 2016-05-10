package com.fazrilabs.realchat.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by blastocode on 4/5/16.
 */
public class PopupUtil {
    private static Dialog mDialog;

    public static void showLoading(Context context, String msg) {
        dismiss();
        mDialog = ProgressDialog.show(context, "",
                msg, true);
    }

    public static void showMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void dismiss() {
        if(mDialog != null)
        {
            mDialog.dismiss();
        }
    }

}
