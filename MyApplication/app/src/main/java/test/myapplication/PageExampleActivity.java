package test.myapplication;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webtrekk.android.tracking.Tracker;
import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.WTrackApplication;
import com.webtrekk.android.tracking.TrackingParams.Params;


public class PageExampleActivity extends ActionBarActivity {
    private Tracker t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_example_activity);
        t = ((WTrackApplication) getApplication()).getTracker("test");
        t.track();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_page_example_activity, menu);
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

    public void onButtonActionClicked(View view) {
        TrackingParams tp = new TrackingParams();
        tp.add(Params.ACTION_NAME, "Action Button clicked")
        .add(Params.ACTIVITY_NAME, this.getClass().getName());
        t.track(tp);
    }

    public void onCheckboxActionClicked(View view) {
        TrackingParams tp = new TrackingParams();
        tp.add(Params.PAGE_CAT, "1", "Herren")
                .add(Params.ACTION, "2", "Schuhe")
                .add(Params.ACTION, "3", "Sportschuhe");
        t.track(tp);
    }

    public void onButtonActionParamsClicked(View view) {
        TrackingParams tp = new TrackingParams();
        tp.add(Params.ACTION, "Action Button clicked")
                .add(Params.ACTIVITY_NAME, this.getClass().getName())
                .add(Params.ACTION,"1", "grey")
                .add(Params.ACTION,"2", "pos3");
        t.track(tp);

        TrackingParams tp_pageparams = new TrackingParams();
        tp_pageparams.add(Params.PAGE, "1", "green")
                .add(Params.PAGE, "2", "4")
                .add(Params.PAGE, "3", "234");
        t.track(tp_pageparams);
    }
}
