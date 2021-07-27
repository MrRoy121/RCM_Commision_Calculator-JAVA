package com.rcm.calculator;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class PushNotificationService extends FirebaseMessagingService {
    String NOTIFICATION_CHANNEL_ID = "NOTIFIER";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if(remoteMessage.getData().size()>0){

            Map<String, String> data = remoteMessage.getData();

                String title = data.get("title");
                String message = data.get("body");
                String bigImage = data.get("icon");
                String link = data.get("ExtraValue");
                String activity = data.get("Activity");

            if(activity.equals("open_app")){

                    sendNotification(title, message, bigImage, link, false);
                }else{
                    sendNotification(title, message, bigImage, link, true);
                }
        }

        super.onMessageReceived(remoteMessage);
    }

    public  void  sendNotification(String title, String message, String imageUrl, String link, boolean activty){


        Intent intent;
        PendingIntent pendingIntent;
        if (!activty) {
            intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        }
        pendingIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getActivity
                    (this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        }
        Uri def = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notiBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setAutoCancel(true)
                .setSound(def)
                .setContentIntent(pendingIntent);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Bitmap image = fetchBitmap(imageUrl);
            if (image != null) {
                notiBuilder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(image));
            }
        }
        NotificationManager notiman = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, getString(R.string.app_name), NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.shouldShowLights();
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            assert notiman != null;
            notiman.createNotificationChannel(notificationChannel);
        }

        notiman.notify(0, notiBuilder.build());

    }

    private Bitmap fetchBitmap(String src) {
        try {
            if (src != null) {
                URL url = new URL(src);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(1200000);
                connection.setReadTimeout(1200000);
                connection.connect();
                InputStream input = connection.getInputStream();
                return BitmapFactory.decodeStream(input);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}