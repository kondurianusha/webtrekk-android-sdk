package com.Webtrekk.SDKTest;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.OutputStream;
import java.net.URLDecoder;


public class ShopExampleActivity extends ActionBarActivity {

    private Webtrekk webtrekk;
    TrackingParameter tp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_example);

        webtrekk = Webtrekk.getInstance();
        tp = new TrackingParameter();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_shop_example, menu);
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

    public void onButtonOrderClicked(View view) {
//        TrackingParameter buttonParameter = new TrackingParameter();
//        buttonParameter.add(Parameter.PRODUCT_STATUS, "add")
//        .add(Parameter.ACTION_NAME, "orderButton");
//        webtrekk.track(buttonParameter);

        try {
            OutputStream output = this.getApplication().getApplicationContext().openFileOutput("webtrekk-referrer-store", Context.MODE_PRIVATE);
            output.write(URLDecoder.decode("utm_source%3Dgoogle%26utm_medium%3Dcpc%26utm_term%3Drunning%252Bshoes%26utm_content%3DdisplayAd1%26utm_campaign%3Dshoe%252Bcampaign", "UTF-8").getBytes());
            output.close();
        }
        catch (Exception e){}

        tp.add(Parameter.ACTION, "action");
        tp.add(Parameter.ACTION_NAME, "action-name");

        webtrekk.track();
    }
}
