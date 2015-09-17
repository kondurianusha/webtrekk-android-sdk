package com.webtrekk.webbtrekksdk;

import android.test.AndroidTestCase;

/**
 * tests the helper functions, based on genymotion nexus 4 5.1 default device
 */
public class HelperFunctionsTest extends AndroidTestCase {
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());


    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetResolution() {
        String resolution = HelperFunctions.getResolution(getContext());
        assertEquals("768x1184", resolution);
    }

    public void testGetDepth() {
        String depth = HelperFunctions.getDepth(getContext());
        assertEquals("32", depth);
    }

    public void testGetLanguage() {
        String lang = HelperFunctions.getLanguage();
        assertEquals("en", lang);
    }

    public void testGetTimeZone() {
        String tz = HelperFunctions.getTimezone();
        assertEquals("-5", tz);
    }

    public void testGetgetOSName() {
        assertEquals("Android", HelperFunctions.getOSName());
    }

    public void testGetgetOSVersion() {
        assertEquals("5.1", HelperFunctions.getOSVersion());
    }

    public void testGetDevice() {
        assertEquals("Genymotion Google Nexus 4 - 5.1.0 - API 22 - 768x1280", HelperFunctions.getDevice());
    }

    public void testGetCountry() {
        assertEquals("US", HelperFunctions.getCountry());
    }

    public void testGetgetUseragent() {
        assertEquals("Tracking Library 400(Android;5.1;Genymotion Google Nexus 4 - 5.1.0 - API 22 - 768x1280;en_US)", HelperFunctions.getUserAgent());
    }

    public void testIsSysAutorotate() {
        assertEquals(true, HelperFunctions.isSysAutoRotate(getContext()));
    }

    public void testGetOrientation() {
        assertEquals("portrait", HelperFunctions.getOrientation(getContext()));
    }

    public void testGetConnectionString() {
        assertEquals("WIFI", HelperFunctions.getConnectionString(getContext()));
    }

    public void testIsRoaming() {
        assertEquals(false, HelperFunctions.isRoaming(getContext()));
    }

    public void testUrlEncode() {
        assertEquals("this+test", HelperFunctions.urlEncode("this test"));
    }

    public void testIsNetworkConnection() {
        assertTrue(HelperFunctions.isNetworkConnection(getContext()));
    }




}
