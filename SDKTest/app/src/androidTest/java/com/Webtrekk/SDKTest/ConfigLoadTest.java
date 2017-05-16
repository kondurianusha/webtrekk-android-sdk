/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Webtrekk GmbH
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 * Created by Arsen Vartbaronov on 02.05.16.
 */

package com.Webtrekk.SDKTest;

import android.app.Activity;
import com.webtrekk.webtrekksdk.Webtrekk;

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

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
    //create to emulate fail for Jenkin test
    public void testFail()
    {
        if (mIsExternalCall)
            assertFalse(true);
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

        cleanConfigPreference();

        mWebtrekk.initWebtrekk(mApplication, config);
        Activity activity = getActivity();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });
        // Assert is inside call below
        String URL = waitForTrackedURL();
        if (isForExistence)
          assertTrue(URL.contains(textToCheck));
        else
          assertFalse(URL.contains(textToCheck));

        finishActivitySync(getActivity());
        setActivity(null);
    }
}
