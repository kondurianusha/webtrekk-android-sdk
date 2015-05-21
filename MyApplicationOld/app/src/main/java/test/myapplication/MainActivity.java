package test.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.webtrekk.android.tracking.Tracker;
import com.webtrekk.android.tracking.WTrackApplication;
import com.webtrekk.android.tracking.Webtrekk;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    EditText contentEventValueField;
    EditText clickEventNameField;
    EditText serviceEventNameField;

    static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.contentEventValueField = (EditText) this.findViewById(R.id.content_event_name_value);
        this.clickEventNameField = (EditText) this.findViewById(R.id.click_event_name_value);
        this.serviceEventNameField = (EditText) this.findViewById(R.id.service_event_name_value);


        final Button sendClickEventButton = (Button) this.findViewById(R.id.button_send_click_event);
        sendClickEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.format("android-demoapp click: %s", MainActivity.this.clickEventNameField.getText().toString()));
                Webtrekk.trackAction("android,demoapp", MainActivity.this.clickEventNameField.getText().toString());
            }
        });

        final Button sendContentEventButton = (Button) this.findViewById(R.id.button_send_content_event);
        sendContentEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, String.format("android-demoapp content: %s", MainActivity.this.contentEventValueField.getText().toString()));
                Webtrekk.trackPage(MainActivity.this.contentEventValueField.getText().toString());
            }
        });

        final Button startMediaEmulationButton = (Button) this.findViewById(R.id.button_launch_media_activity);
        startMediaEmulationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.startActivity(new Intent(MainActivity.this, MediaActivity.class));
            }
        });
        final Button startServiceEventButton = (Button) this.findViewById(R.id.button_send_service_event);
        startServiceEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Onlcick service");
                Intent sendIntent = new Intent(MainActivity.this, EventService.class);
                sendIntent.putExtra("name", MainActivity.this.serviceEventNameField.getText().toString());
                MainActivity.this.startService(sendIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart () {
        super.onStart();

        final Context applicationContext = this;

        Webtrekk.activityStart(new Activity() {
            {
                this.attachBaseContext(applicationContext);
            }
        });

        // Webtrekk.activityStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Um das Update einer Applikation zu übermitteln
        Map<String, String> parameter = new HashMap<String, String>();

        if(Webtrekk.isThisVersionAnUpdate()) {
            parameter.put("cs2", "1");
        }
        else {
            parameter.put("cs2", "0");
        }

        // Um die aktuelle Version der App ebenfalls zu übermitteln
        Webtrekk.setAppVersionParameter("cs5");
        Webtrekk.trackPage("app-update", parameter);
    }


    @Override
    protected void onStop() {
        Webtrekk.activityStop(this);

        super.onStop();
    }
}
