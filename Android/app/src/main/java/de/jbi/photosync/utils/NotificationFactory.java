package de.jbi.photosync.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import de.jbi.photosync.R;
import de.jbi.photosync.activities.MainActivity;

/**
 * Created by Jan on 11.06.2016.
 */
public class NotificationFactory {
    private static final int NOTIFICATION_ID = 1;
    private static NotificationManager mNotifyMgr = (NotificationManager) AndroidUtil.ContextHandler.getMainContext().getSystemService(Context.NOTIFICATION_SERVICE);


    public static void notify(Notification notification) {
        mNotifyMgr.notify(NOTIFICATION_ID, notification);
    }

    public static void dismissNotification() {
        mNotifyMgr.cancel(NOTIFICATION_ID);
    }

    public static class NotificationBuilder {
        private NotificationCompat.Builder builder;

        public NotificationBuilder() {
            Context ctx = AndroidUtil.ContextHandler.getMainContext();
            builder = new NotificationCompat.Builder(ctx);
            builder.setAutoCancel(true);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Sync running");
            builder.setContentText("This is an empty notification!");

            Intent startActivityIntent = new Intent(ctx, MainActivity.class);
//            TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
//            stackBuilder.(MainActivity.class);
//            stackBuilder.addNextIntent(startActivityIntent);
//            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_ONE_SHOT);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, startActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
        }

        public NotificationBuilder setContentText(String text) {
            builder.setContentText(text);
            return this;
        }

        public NotificationBuilder setProgress(int max, int progress, boolean indeterminate) {
            builder.setProgress(max, progress, indeterminate);
            return this;
        }

        public Notification buildNotification() {
            return builder.build();
        }
    }
}
