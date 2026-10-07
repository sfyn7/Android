package com.staysobercora.app;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import java.util.Calendar;

public final class NotificationScheduler {
 public static final String CHANNEL_ID="stay_sober_test_reminders";
 private static final int[][] TIMES={{9,55},{15,55},{21,55}};
 private static final int[] IDS={955,1555,2155};

 private NotificationScheduler(){}

 public static void createChannel(Context context){
  if(Build.VERSION.SDK_INT>=26){
   NotificationChannel ch=new NotificationChannel(
     CHANNEL_ID,"Scheduled test reminders",NotificationManager.IMPORTANCE_HIGH);
   ch.setDescription("Rings 5 minutes before each Stay Sober Cora test.");
   ch.enableVibration(true);
   ch.setVibrationPattern(new long[]{0,500,250,500,250,900});
   ch.setSound(android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI,null);
   NotificationManager nm=context.getSystemService(NotificationManager.class);
   nm.createNotificationChannel(ch);
  }
 }

 public static void scheduleAll(Context context){
  createChannel(context);
  for(int i=0;i<TIMES.length;i++) schedule(context,IDS[i],TIMES[i][0],TIMES[i][1]);
 }

 private static void schedule(Context context,int id,int hour,int minute){
  Calendar now=Calendar.getInstance();
  Calendar when=Calendar.getInstance();
  when.set(Calendar.HOUR_OF_DAY,hour);
  when.set(Calendar.MINUTE,minute);
  when.set(Calendar.SECOND,0);
  when.set(Calendar.MILLISECOND,0);
  if(!when.after(now)) when.add(Calendar.DAY_OF_YEAR,1);

  Intent intent=new Intent(context,ReminderReceiver.class);
  intent.putExtra("notification_id",id);
  intent.putExtra("test_hour",(hour==9?10:(hour==15?16:22)));
  PendingIntent pi=PendingIntent.getBroadcast(
    context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT|PendingIntent.FLAG_IMMUTABLE);

  AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
  if(Build.VERSION.SDK_INT>=31 && !am.canScheduleExactAlarms()){
   am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when.getTimeInMillis(),pi);
  }else if(Build.VERSION.SDK_INT>=23){
   am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,when.getTimeInMillis(),pi);
  }else{
   am.setExact(AlarmManager.RTC_WAKEUP,when.getTimeInMillis(),pi);
  }
 }
}
