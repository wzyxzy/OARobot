package com.zgty.oarobot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DateTimeReceiver extends BroadcastReceiver {
    private static final String LOG_TAG = DateTimeReceiver.class.getSimpleName();
    private static final String ACTION_DATE_CHANGED = Intent.ACTION_DATE_CHANGED;
    private static final String ACTION_TIME_CHANGED = Intent.ACTION_TIME_CHANGED;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        String action = intent.getAction();

        if (ACTION_DATE_CHANGED.equals(action)) {


            Log.d(LOG_TAG, "---DATE_CHANGED!---");


        }

        if (ACTION_TIME_CHANGED.equals(action)) {


            Log.d(LOG_TAG, "---TIME_CHANGED!---");


        }

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
