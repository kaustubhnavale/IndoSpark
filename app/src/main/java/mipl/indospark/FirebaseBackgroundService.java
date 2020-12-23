package mipl.indospark;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

public class FirebaseBackgroundService extends WakefulBroadcastReceiver {

    private static final String TAG = "FirebaseService";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "I'm in!!!");

        if (intent.getExtras() != null) {
            for (String key : intent.getExtras().keySet()) {
                Object value = intent.getExtras().get(key);
                Log.e("FirebaseDataReceiver", "Key: " + key + " Value: " + value);
                if(key.equalsIgnoreCase("gcm.notification.body") && value != null) {
                    Bundle bundle = new Bundle();
                    Intent backgroundIntent = new Intent(context, MyFirebaseMessagingService.class);
                    bundle.putString("push_message", value + "");
                    backgroundIntent.putExtras(bundle);
                    context.startService(backgroundIntent);
                }
            }
        }
    }
}