package com.termux.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LaunchReceiver extends BroadcastReceiver {
    private static final String TAG = "LaunchReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String pkg = intent.getStringExtra("package");
            Log.d(TAG, "Received broadcast, package: " + pkg);

            if (pkg == null || pkg.isEmpty()) {
                Log.e(TAG, "No package specified");
                return;
            }

            Intent launch = context.getPackageManager()
                .getLaunchIntentForPackage(pkg);

            if (launch == null) {
                Log.e(TAG, "No launch intent found for: " + pkg);
                android.widget.Toast.makeText(
                    context,
                    "App not found: " + pkg,
                    android.widget.Toast.LENGTH_SHORT
                ).show();
                return;
            }

            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            context.startActivity(launch);
            Log.d(TAG, "Launched: " + pkg);

        } catch (Exception e) {
            Log.e(TAG, "LaunchReceiver error: " + e.getMessage());
        }
    }
}
