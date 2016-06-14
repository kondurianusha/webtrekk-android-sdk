package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.Webtrekk.SDKTest.SimpleHTTPServer.HttpServer;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.io.IOException;

/**
 * Created by vartbaronov on 11.05.16.
 */
public class TagIntegrationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_example_activity);
    }
}
