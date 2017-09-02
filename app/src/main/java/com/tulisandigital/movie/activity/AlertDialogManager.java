package com.tulisandigital.movie.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.tulisandigital.movie.R;

/**
 * Developed with love for completing competence android developer by UDACITY
 * Project beasiswa android developer dari udacity by Andre Marbun @developerpdak
 * Website : http://tulisandigital.com
 */
public class AlertDialogManager {
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
         alertDialog.setMessage(message);
        if(status != null)
            alertDialog.setIcon((status) ? R.mipmap.ic_action_bell : R.mipmap.ic_action_bell);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
      alertDialog.show();
    }
}