package com.Webtrekk.SDKTest;

import android.content.Intent;
import android.nfc.Tag;
import android.test.suitebuilder.annotation.Suppress;

import com.webtrekk.webtrekksdk.Webtrekk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

/**
 * Created by vartbaronov on 11.05.16.
 */
@Suppress
public class TagIntegrationTest extends ActivityInstrumentationTestCase2Base<TagIntegrationActivity> {
    private Webtrekk mWebtrekk;


    public TagIntegrationTest() {
        super(TagIntegrationActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mWebtrekk = Webtrekk.getInstance();
    }

    public void testTagIntegration()
    {
        initWaitingForTrack(new Runnable() {
            @Override
            public void run() {
                mWebtrekk.track();
            }
        });

        String URL = waitForTrackedURL();

        URLParsel parcel = new URLParsel();

        parcel.parseURL(URL);

        assertEquals(parcel.getValue("cg1"), "AT1Value");
        assertEquals(parcel.getValue("cg2"), "ATS1");
        assertEquals(parcel.getValue("cg10"), "AT1Value");
        assertEquals(parcel.getValue("cg11"), "ATS1");
        assertEquals(parcel.getValue("cp3"), "ATS1");
        assertEquals(parcel.getValue("cp4"), "AT1Value");
        assertEquals(parcel.getValue("cp8"), "AT1Value");
        assertEquals(parcel.getValue("cp9"), "ATS1");
        assertEquals(parcel.getValue("cs5"), "ATS1");
        assertEquals(parcel.getValue("cs6"), "AT1Value");
        assertEquals(parcel.getValue("cd"), "AT1Value");
        assertEquals(parcel.getValue("uc7"), "AT1Value");
        assertEquals(parcel.getValue("co"), "AT1Value");
    }
}

