package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Webtrekk;
import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;


public class PageExampleActivity extends Activity {
    private Webtrekk webtrekk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_example_activity);
        webtrekk = Webtrekk.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        //webtrekk.startActivity("PageExampleActivity");
        webtrekk.getCustomParameter();
    }

    @Override
    public void onStop() {
        //webtrekk.stopActivity();
        super.onStop();
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
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ACTION_NAME, "Action Button clicked");
        webtrekk.track(tp);
    }

    public void onCheckboxActionClicked(View view) {
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.PAGE_CAT, "1", "Herren")
                .add(Parameter.ACTION, "2", "Schuhe")
                .add(Parameter.ACTION, "3", "Sportschuhe");
        webtrekk.track(tp);
    }

    public void onButtonActionParamsClicked(View view) {
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ACTION, "Action Button clicked")
                .add(Parameter.ACTION,"1", "grey")
                .add(Parameter.ACTION,"2", "pos3");
        webtrekk.track(tp);

        TrackingParameter tp_pageparams = new TrackingParameter();
        tp_pageparams.add(Parameter.PAGE, "1", "green")
                .add(Parameter.PAGE, "2", "4")
                .add(Parameter.PAGE, "3", "234");
        webtrekk.track(tp_pageparams);
    }
}
