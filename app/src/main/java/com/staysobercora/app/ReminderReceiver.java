package com.staysobercora.app;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
 @Override public void onReceive(Context context,Intent intent){
  int id=intent.getIntExtra("notification_id",9000);
  int hour=intent.getIntExtra("test_hour",10);
  String time=hour==10?"10:00 AM":(hour==16?"4:00 PM":"10:00 PM");

  Intent open=new Intent(context,MainActivity.class);
  open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
  PendingIntent content=PendingIntent.getActivity(
    context,id,open,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

  NotificationCompat.Builder b=new NotificationCompat.Builder(context,NotificationScheduler.CHANNEL_ID)
    .setSmallIcon(R.drawable.ic_notification_star)
    .setContentTitle("⭐ Stay Sober Cora — test in 5 minutes")
    .setContentText("Your "+time+" sober check-in starts in 5 minutes. Keep going 💛")
    .setStyle(new NotificationCompat.BigTextStyle().bigText(
      "Your "+time+" sober check-in starts in 5 minutes. Tap to open Stay Sober Cora. Keep going 💛"))
    .setPriority(NotificationCompat.PRIORITY_MAX)
    .setCategory(NotificationCompat.CATEGORY_ALARM)
    .setDefaults(NotificationCompat.DEFAULT_ALL)
    .setAutoCancel(true)
    .setContentIntent(content);

  NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
  nm.notify(id,b.build());

  NotificationScheduler.scheduleAll(context);
 }
}
