package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.os.Bundle;

import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 10.05.16.
 */
public class SuspendActivity extends Activity {
    Webtrekk mWebtrekk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_example_activity);
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(getApplication(), R.raw.webtrekk_config_suspend_test);
    }
}
