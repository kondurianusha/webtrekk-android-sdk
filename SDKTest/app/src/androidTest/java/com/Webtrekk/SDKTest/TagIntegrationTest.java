package com.Webtrekk.SDKTest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.preference.PreferenceManager;
import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;
import com.webtrekk.webtrekksdk.Webtrekk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Created by vartbaronov on 11.05.16.
 */
public class TagIntegrationTest extends ActivityInstrumentationTestCase2Base<TagIntegrationActivity> {
    private Webtrekk mWebtrekk;


    public TagIntegrationTest() {
        super(TagIntegrationActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        cleanConfigPreference();
        mWebtrekk = Webtrekk.getInstance();
        mWebtrekk.initWebtrekk(mApplication, R.raw.webtrekk_config_tag_integration_test);
        mWebtrekk.getCustomParameter().put("AT1", "AT1Value");
        mWebtrekk.getCustomParameter().put("AT2", "AT2Value");
        mWebtrekk.getCustomParameter().put("AT3", "AT3Value");
        mWebtrekk.getCustomParameter().put("asdf", "seachString");
        getActivity();
    }

    @Override
    public void tearDown() throws Exception {
        finishActivitySync(getActivity());
        setActivity(null);
        super.tearDown();
    }

    public void testTagIntegration()
    {
        while (!PreferenceManager.getDefaultSharedPreferences(getInstrumentation().getTargetContext()).
                  contains(Webtrekk.PREFERENCE_KEY_CONFIGURATION))
        {
            getInstrumentation().waitForIdleSync();
        }

        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        mWaitMilliseconds = 20000;

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertEquals("ATS1", parcel.getValue("cg11"));
        assertEquals("AT1Value", parcel.getValue("cg10"));
        assertEquals("ATS1", parcel.getValue("cg11"));
        assertEquals("ATS1", parcel.getValue("cp3"));
        assertEquals("AT1Value", parcel.getValue("cp4"));
        assertEquals("AT1Value", parcel.getValue("cp8"));
        assertEquals(parcel.getValue("cp9"), "ATS1");
        assertEquals(parcel.getValue("cs5"), "ATS1");
        assertEquals(parcel.getValue("cs6"), "AT1Value");
        assertEquals(parcel.getValue("cd"), "AT1Value");
        assertEquals(parcel.getValue("uc7"), "AT1Value");
        assertEquals(parcel.getValue("co"), "AT1Value");
        assertEquals(parcel.getValue("is"), "seachString");
    }
}

