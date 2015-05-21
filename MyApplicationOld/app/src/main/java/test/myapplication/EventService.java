package test.myapplication;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.webtrekk.android.tracking.Webtrekk;

public class EventService extends IntentService {

    static final String TAG = EventService.class.getName();


    public EventService() {
        super("EventService");
    }


    public EventService(String name) {
        super(name);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String id = intent.getStringExtra("name");
        if (TextUtils.isEmpty(id)) {
            Log.d(TAG, "id cannot be null or empty");
            return;
        }
        Log.d(TAG, String.format("android-demoapp service: %s", id));
        Webtrekk.activityStart(new Activity() {
            {
                this.attachBaseContext(EventService.this.getApplicationContext());
            }
        });
        Webtrekk.trackPage(id);
    }

}