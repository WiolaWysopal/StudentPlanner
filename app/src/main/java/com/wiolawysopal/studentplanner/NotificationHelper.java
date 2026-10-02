package com.wiolawysopal.studentplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

public class NotificationHelper {

    public static final String CHANNEL_ID = "deadline_reminders";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Deadline reminders";
            String description = "Notifications about upcoming task deadlines";

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    name,
                    importance
            );

            channel.setDescription(description);

            NotificationManager notificationManager =
                    context.getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}