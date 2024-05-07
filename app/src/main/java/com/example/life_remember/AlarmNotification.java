package com.example.life_remember;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class AlarmNotification extends BroadcastReceiver {

    public static final int NOTIFICATION_ID = 1;


    @Override
    public void onReceive(Context context, Intent intent) {
        // createSimpleNotification(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(R.drawable.vector_write)
                .setContentTitle("Titulo")
                .setContentText("Esto es la notificación")
                .setPriority(Notification.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(200, builder.build()); // 200 es un número único, quiza le modifiquemos
    }

    private void createSimpleNotification(Context context) {

        Toast.makeText(context, "Llega al create simple noti", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        int flag = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) ? PendingIntent.FLAG_IMMUTABLE : 0;
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, flag);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_delete) 
                .setContentTitle("My title")
                .setContentText("Esto es un ejemplo <3")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi Holita holi "))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setColor(Color.BLUE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setLights(Color.MAGENTA, 1000, 1000)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setDefaults(Notification.DEFAULT_SOUND);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(AlarmNotification.NOTIFICATION_ID, builder.build());
            Toast.makeText(context, "Se acaba de notificar", Toast.LENGTH_SHORT).show();
        }
    }
}