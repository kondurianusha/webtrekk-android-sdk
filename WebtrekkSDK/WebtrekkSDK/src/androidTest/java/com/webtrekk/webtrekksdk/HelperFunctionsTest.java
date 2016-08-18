package com.webtrekk.webtrekksdk;

import android.test.AndroidTestCase;

import com.webtrekk.webtrekksdk.Utils.HelperFunctions;

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

    public void testIsSysAutorotate() {
        assertTrue(HelperFunctions.isSysAutoRotate(getContext()) == true || HelperFunctions.isSysAutoRotate(getContext()) == false);
    }

    public void testGetOrientation() {
        String orientation = HelperFunctions.getOrientation(getContext());
        assertTrue(orientation.equals("portrait") || orientation.equals("landscape"));
    }

    public void testGetConnectionString() {
        String connection = HelperFunctions.getConnectionString(getContext());
        assertTrue(connection.equals("WIFI") || connection.equals("offline") || connection.equals("3G")|| connection.equals("4G"));
    }

    public void testIsRoaming() {
        assertEquals(false, HelperFunctions.isRoaming(getContext()));
    }

    public void testUrlEncode() {
        String encoded = HelperFunctions.urlEncode("this testüää-17%test.5.4.3-(8)?param=45#asdf");
        assertEquals(encoded, "this+test%C3%BC%C3%A4%C3%A4-17%25test.5.4.3-%288%29%3Fparam%3D45%23asdf", encoded);
    }

    public void testIsNetworkConnection() {
        assertTrue(HelperFunctions.isNetworkConnection(getContext()));
    }




}
