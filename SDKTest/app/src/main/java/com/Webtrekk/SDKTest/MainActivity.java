package com.Webtrekk.SDKTest;

import android.app.Application;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.webtrekk.webtrekksdk.TrackingParameter;
import com.webtrekk.webtrekksdk.Webtrekk;
import com.webtrekk.webtrekksdk.WebtrekkUserParameters;


public class MainActivity extends ActionBarActivity {
    private Webtrekk webtrekk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webtrekk = Webtrekk.getInstance();
        webtrekk.initWebtrekk(getApplication());

        webtrekk.getCustomParameter().put("own_para", "my-value");
        webtrekk.track();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
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

    /** Called when the user clicks the Page Example Activity Button button */
    public void showPageExampleActivity(View view) {
        Intent intent = new Intent(this, PageExampleActivity.class);
        startActivity(intent);
    }

    public void showShopExampleActivity(View view) {
        Intent intent = new Intent(this, ShopExampleActivity.class);
        startActivity(intent);
    }

    public void showMediaExampleActivity(View view) {
        Intent intent = new Intent(this, MediaExampleActivity.class);
        startActivity(intent);
    }

    public void sendCDBRequest(View view)
    {
        Intent intent = new Intent(this, CDBActivityTest.class);
        startActivity(intent);
    }

    public Webtrekk getWebtrekk() {
        return webtrekk;
    }
}