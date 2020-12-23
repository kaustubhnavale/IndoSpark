package mipl.indospark;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

/**
 * Created by android01 on 12/7/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";
    int TAG_counter = 0;
    int count;
    Bitmap bitmap;

//    SharedPreferences sharedPreferences = getSharedPreferences("counters", Context.MODE_PRIVATE);

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
//        TAG_counter = sharedPreferences.getInt("counter", 0);

//        count = +1;

//        SharedPreferences.Editor editor = sharedPreferences.edit();

//        editor.putInt("counter", count).commit();

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }

        /*Map<String, String> data = remoteMessage.getData();
        if (data.containsKey("click_action")) {
            ClickActionHelper.startActivity(data.get("click_action"), null, this);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

        //message will contain the Push Message
        String message = remoteMessage.getData().get("message");
        // String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");
        //imageUri will contain URL of the image to be displayed with Notification
        //If the key AnotherActivity has  value as True then when the user taps on notification, in the app AnotherActivity will be opened.
        //If the key AnotherActivity has  value as False then when the user taps on notification, in the app MainActivity will be opened.

        String TrueOrFlase = remoteMessage.getData().get("AnotherActivity");

        //To get a Bi.0tmap image from the URL received

        sendNotification(message, TrueOrFlase);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     */

    private void sendNotification(String messageBody, String True) {
        Intent intent = new Intent(this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("AnotherActivity", True);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)

                .setSmallIcon(R.mipmap.icon)
                .setContentTitle(messageBody)
                /*Notification with Image*/
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "IndoSpark";
            String description = "Channel";
            int importance = NotificationManager.IMPORTANCE_HIGH; //Important for heads-up notification
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            channel.setShowBadge(true);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager notificationManager1 = getSystemService(NotificationManager.class);
            notificationManager1.createNotificationChannel(channel);

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "1")
                    .setSmallIcon(R.mipmap.icon)
                    .setContentTitle("IndoSpark")
                    .setContentText(messageBody)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE) //Important for heads-up notification
                    .setPriority(Notification.PRIORITY_MAX); //Important for heads-up notification

            Notification buildNotification = mBuilder.build();
            NotificationManager mNotifyMgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(001, buildNotification);
        }
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }
}