package com.webtrekk.webbtrekksdk;

import android.test.AndroidTestCase;
import static org.mockito.Mockito.*;


public class WebtrekkTests extends AndroidTestCase {

    Webtrekk webtrekk;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

        webtrekk = new Webtrekk();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetInstance() {
        Webtrekk webtrekk = Webtrekk.getInstance();
        assertNotNull(webtrekk);
        // make shure it always returns the same object
        assertSame(webtrekk, Webtrekk.getInstance());
    }

    public void testInitWebtrekk() {
        try {
            webtrekk.initWebtrekk(null);
            fail("null context, IllegalArgumentException");
        } catch (IllegalArgumentException e){

        }

        webtrekk.initWebtrekk(getContext());
        assertNotNull(webtrekk.getTrackingConfiguration());
        assertNotNull(webtrekk.getPlugins());
        assertNotNull(webtrekk.getContext());
        assertNotNull(webtrekk.getRequestUrlStore());
        assertNotNull(webtrekk.getStaticAutomaticData());
        assertNotNull(webtrekk.getTimerService());
        assertEquals(0, webtrekk.getActivityCount());
        // make shure it fails when init is called twice
        try {
            webtrekk.initWebtrekk(getContext());
            fail("already initalized, IllegalStateException");
        } catch (IllegalStateException e) {

        }

    }

    public void testInitTrackingConfiguration() {
        webtrekk.setContext(getContext());
        webtrekk.initTrackingConfiguration();
        assertNotNull(webtrekk.getTrackingConfiguration());
        assertEquals(webtrekk.getTrackingConfiguration().getTrackId(), "1111111111112");
        assertEquals(webtrekk.getTrackingConfiguration().getTrackDomain(), "http://trackingtest.nglab.org");
        assertEquals(webtrekk.getTrackingConfiguration().getSendDelay(), 60);
        assertEquals(webtrekk.getTrackingConfiguration().getInitialSendDelay(), 0);
        assertEquals(webtrekk.getTrackingConfiguration().getMaximumRequests(), 5000);
        assertEquals(webtrekk.getTrackingConfiguration().getTrackingConfigurationUrl(), "http://localhost/tracking_config.xml");
        assertEquals(webtrekk.getTrackingConfiguration().getVersion(), 2);

    }

    public void testInitPlugins() {
        webtrekk.setContext(getContext());
        webtrekk.initTrackingConfiguration();
        webtrekk.initPlugins();
        assertNotNull(webtrekk.getPlugins());
        assertEquals(1, webtrekk.getPlugins().size());
    }

    public void testInitStaticAutomaticData() {
        webtrekk.setContext(getContext());
        webtrekk.initTrackingConfiguration();
        webtrekk.initStaticAutomaticData();
        assertEquals(8, webtrekk.getStaticAutomaticData().size());
        assertEquals("Tracking Library 400(Android;5.1;Genymotion Google Nexus 4 - 5.1.0 - API 22 - 768x1280;en_US)", webtrekk.getStaticAutomaticData().get(TrackingParameter.Parameter.USERAGENT));
    }

    public void testInitDynamicAutomaticData() {
        webtrekk.setContext(getContext());
        assertEquals(2, webtrekk.initDynamicAutomaticData().size());
        assertEquals("portrait", webtrekk.initDynamicAutomaticData().get(TrackingParameter.Parameter.SCREEN_ORIENTATION));
        assertEquals("WIFI", webtrekk.initDynamicAutomaticData().get(TrackingParameter.Parameter.CONNECTION_TYPE));
    }

    public void testTrack() {
        try {
            webtrekk.track();
            fail("not initalized, IllegalStateException");
        } catch (IllegalStateException e) {

        }
        webtrekk.initWebtrekk(getContext());
        try {
            webtrekk.track();
            fail("startActivity not called, IllegalStateException");
        } catch (IllegalStateException e) {

        }
        try {
            webtrekk.track(null);
            fail("trackingparams is null, IllegalStateException");
        } catch (IllegalStateException e) {

        }
        webtrekk.startActivity("test");
        webtrekk.track();
        assertEquals(1, webtrekk.getRequestUrlStore().size());
        assertTrue(webtrekk.getRequestUrlStore().get(0).contains("http://trackingtest.nglab.org/1111111111112/wt?p=400,"));
    }
    public void testStartActivity() {
        try {
            webtrekk.startActivity("test");
            fail("not initalized, IllegalStateException");
        } catch (IllegalStateException e) {

        }
        webtrekk.initWebtrekk(getContext());
        RequestUrlStore requestUrlStore = mock(RequestUrlStore.class);
        webtrekk.setRequestUrlStore(requestUrlStore);
        webtrekk.startActivity("test");
        assertEquals("test", webtrekk.getCurrentActivityName());
        assertEquals(1, webtrekk.getActivityCount());
        //first time call, make shure onfirstactivitystart also gets called
        verify(requestUrlStore).loadRequestsFromFile();
        // second call
        webtrekk.startActivity("test2");
        assertEquals("test2", webtrekk.getCurrentActivityName());
        assertEquals(2, webtrekk.getActivityCount());
    }
    public void testStopActivity() {
        try {
            webtrekk.stopActivity();
            fail("not initalized, IllegalStateException");
        } catch (IllegalStateException e){
        }
        webtrekk.initWebtrekk(getContext());
        //init called but not the startActivity
        try {
            webtrekk.stopActivity();
            fail("activity has not been started yet, call startAcitivity");
        } catch (IllegalStateException e){
        }
        webtrekk.startActivity("test");
        webtrekk.startActivity("test2");

        webtrekk.stopActivity();
        assertEquals(1, webtrekk.getActivityCount());
        RequestUrlStore requestUrlStore = mock(RequestUrlStore.class);
        webtrekk.setRequestUrlStore(requestUrlStore);
        webtrekk.stopActivity();
        assertEquals(0, webtrekk.getActivityCount());
        verify(requestUrlStore).saveRequestsToFile();
    }

    public void testOnSendIntervalOver() {
        webtrekk.initWebtrekk(getContext());
        RequestUrlStore requestUrlStore = mock(RequestUrlStore.class);
        webtrekk.setRequestUrlStore(requestUrlStore);
        webtrekk.onSendIntervalOver();
        assertNull(webtrekk.getExecutorService());
        assertNull(webtrekk.getRequestProcessorFuture());

        when(requestUrlStore.size()).thenReturn(5);
        webtrekk.onSendIntervalOver();
        assertNotNull(webtrekk.getExecutorService());
        assertNotNull(webtrekk.getRequestProcessorFuture());
    }
}
