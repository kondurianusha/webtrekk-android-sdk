package com.webtrekk.android.trackingplugin;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;

import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.TrackingRequest;
import com.webtrekk.android.tracking.WTrack;
import android.R;
import android.support.v4.app.TaskStackBuilder;

/**
 * Created by user on 10/04/15.
 */

/*
 this is an example plugin, its before_request method gets called each time before a request is made
 it shows the usage of a filter logic, it waits for the trackingrequest where a user has seen the
 advertisement for XYZ, and if that happend the plugin will execute and send the user a push message
 offering him a discount for XYZ. it also shows accessing the shared ActivityState from the WTrack
 class and how to configure it
 default activitys for displaying ads or fragments could be distributed with the sdk as well
 */
public class ExampleDiscountAdNotificationPlugin extends Plugin {

    private Context context;
    // this activity gets passed as param and shows the discount image
    private Activity adactivity;
    private int notificationId;
    // contains a reference to the singleton instance of the wtrac class
    // this can be used to send or get network details or access other tracking params and activity state
    private WTrack wtrack;

    public ExampleDiscountAdNotificationPlugin(WTrack wtrack) {
        super(wtrack);
        context = wtrack.getContext();
    }

      @Override
    public void before_request(TrackingRequest request) {

    }

    @Override
    public void after_request(TrackingRequest request) {
        // check if the user has seen an advertisement for product XYZ before offering the discount

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification_overlay)
                        .setContentTitle("ExampleAdPugin")
                        .setContentText("you have seen the ad for XYZ");
// Creates an explicit intent for an Activity in your app
        // additionally maybe get the ad image here from the remote server and display it in the activity
        // basicly any information from mac or other remote systems can be passed as intent
        Intent resultIntent = new Intent(context, adactivity.getClass());

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(adactivity.getClass());
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(notificationId, mBuilder.build());
    }
}
