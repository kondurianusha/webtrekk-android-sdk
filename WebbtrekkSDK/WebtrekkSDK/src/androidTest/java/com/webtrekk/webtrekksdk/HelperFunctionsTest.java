package com.webtrekk.webtrekksdk;

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
        assertTrue(resolution.contains("x"));
        assertTrue(resolution.length() > 5);
    }

    public void testGetDepth() {
        String depth = HelperFunctions.getDepth(getContext());
        assertEquals("32", depth);
    }

    public void testGetLanguage() {
        String lang = HelperFunctions.getLanguage();
        assertNotSame("", lang);
        assertTrue(lang.length() > 0);
    }

    public void testGetTimeZone() {
        String tz = HelperFunctions.getTimezone();
        assertNotSame("", tz);
        assertTrue(tz.length() > 0);
    }

    public void testGetgetOSName() {
        assertEquals("Android", HelperFunctions.getOSName());
    }

    public void testGetgetOSVersion() {
        assertNotNull(HelperFunctions.getOSVersion());
    }

    public void testGetDevice() {
        assertNotSame("", HelperFunctions.getDevice());
        assertTrue(HelperFunctions.getDevice().length() > 1);
    }

    public void testGetCountry() {
        assertNotSame("", HelperFunctions.getCountry());
        assertTrue(HelperFunctions.getCountry().length() > 1);
    }

    public void testGetgetUseragent() {
        assertTrue(HelperFunctions.getUserAgent(), HelperFunctions.getUserAgent().contains("Tracking Library 4.0(Android"));
    }

    public void testIsSysAutorotate() {
        assertTrue(HelperFunctions.isSysAutoRotate(getContext()) == true || HelperFunctions.isSysAutoRotate(getContext()) == false);
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
