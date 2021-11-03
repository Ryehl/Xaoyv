package com.xaoyv.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xaoyv.app.activity.HomeActivity;

public class NetWorkStateReceiver extends BroadcastReceiver {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, HomeActivity.class);
        context.startActivity(intent1);
        Log.d(TAG, "onReceive: " + intent);
    }
}
