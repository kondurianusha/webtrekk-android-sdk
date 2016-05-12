package com.Webtrekk.SDKTest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Utils.WebtrekkLogging;
import com.webtrekk.webtrekksdk.Webtrekk;

/**
 * Created by vartbaronov on 02.05.16.
 */
public class ConfigLoadTest extends ActivityInstrumentationTestCase2Base<EmptyActivity> {
    Webtrekk mWebtrekk;


    public ConfigLoadTest(){
        super(EmptyActivity.class);
    }


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
    }

    public void testConfigOK()
    {
        configTest(R.raw.webtrekk_config_remote_test_exists, "ThisIsLocalConfig", false);
    }

    public void testLoadDefaultOK()
    {
        configTest(R.raw.webtrekk_config_remote_test_not_exists, "ThisIsLocalConfig", true);
    }

    public void testBrokenConfigLoad()
    {
        configTest(R.raw.webtrekk_config_remote_test_broken_scheme, "ThisIsLocalConfig", true);
    }

    public void testEmptyConfigLoad()
    {
        configTest(R.raw.webtrekk_config_remote_test_empty_file, "ThisIsLocalConfig", true);
    }

    public void testLocked()
    {
        configTest(R.raw.webtrekk_config_remote_test_locked, "ThisIsLocalConfig", true);
    }

    public void testLargeSize()
    {
        configTest(R.raw.webtrekk_config_remote_test_large_size, "ThisIsLocalConfig", true);
    }

    public void testTagIntegration()
    {
        configTest(R.raw.webtrekk_config_remote_test_tag_integration, "ThisIsLocalConfig", false);

    }
    private void configTest(int config, String textToCheck, boolean isForExistence)
    {
        assertFalse(mWebtrekk.isInitialized());
        SharedPreferences sharedPrefs = HelperFunctions.getWebTrekkSharedPreference(getInstrumentation().getTargetContext());
        sharedPrefs.edit().remove(Webtrekk.PREFERENCE_KEY_CONFIGURATION).apply();

        mWebtrekk.initWebtrekk(getActivity().getApplication(), config);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                callStartActivity("com.Webtrekk.SDKTest.EmptyActivity", mWebtrekk);
                mWebtrekk.track();
            }
        });

        // Assert is inside call below
        String URL = waitForTrackedURL();
        if (isForExistence)
          assertTrue(URL.contains(textToCheck));
        else
          assertFalse(URL.contains(textToCheck));
    }
}
