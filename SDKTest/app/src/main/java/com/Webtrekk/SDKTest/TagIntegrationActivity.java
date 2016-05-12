package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.os.Bundle;

import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 11.05.16.
 */
public class TagIntegrationActivity extends Activity {
    Webtrekk mWebtrekk;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_example_activity);
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(getApplication(), R.raw.webtrekk_config_tag_integration_test);
    }
   protected void onStart() {
       super.onStart();
       mWebtrekk.getCustomParameter().put("AT1", "AT1Value");
       mWebtrekk.getCustomParameter().put("AT2", "AT2Value");
       mWebtrekk.getCustomParameter().put("AT3", "AT3Value");
   }
}
