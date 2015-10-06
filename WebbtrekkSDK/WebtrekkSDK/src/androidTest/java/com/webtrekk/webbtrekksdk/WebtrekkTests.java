package com.webtrekk.webbtrekksdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.AndroidTestCase;
import static org.mockito.Mockito.*;
import com.webtrekk.webbtrekksdk.TrackingParameter.Parameter;


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
        assertNotNull(webtrekk.getWebtrekkParameter());
        assertNotNull(webtrekk.getTimerService());
        assertEquals(0, webtrekk.getActivityCount());
        // make shure it fails when init is called twice
//        try {
//            webtrekk.initWebtrekk(getContext());
//            fail("already initalized, IllegalStateException");
//        } catch (IllegalStateException e) {
//
//        }

    }

    public void testInitTrackingConfiguration() {
        webtrekk.setContext(getContext());
        webtrekk.initTrackingConfiguration();
        assertNotNull(webtrekk.getTrackingConfiguration());
        assertEquals(webtrekk.getTrackingConfiguration().getTrackId(), "1111111111112");
        assertEquals(webtrekk.getTrackingConfiguration().getTrackDomain(), "http://trackingtest.nglab.org");
        assertEquals(webtrekk.getTrackingConfiguration().getSendDelay(), 60);
        assertEquals(webtrekk.getTrackingConfiguration().getInitialSendDelay(), 33);
        assertEquals(webtrekk.getTrackingConfiguration().getMaxRequests(), 5000);
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

    public void testInitWebtrekkParameter() {
        // make sure the default params have valid values
        webtrekk.setContext(getContext());
        webtrekk.initTrackingConfiguration();
        webtrekk.initInternalParameter();
        webtrekk.initWebtrekkParameter();
        assertEquals(6, webtrekk.getWebtrekkParameter().size());
        assertTrue(webtrekk.getWebtrekkParameter().get(TrackingParameter.Parameter.USERAGENT).contains("Tracking Library 400(Android;"));

    }

    public void testUpdateDynamicParameter() {
        // make sure that the values which change with every request are inserted as well
        webtrekk.setContext(getContext());
        webtrekk.initTrackingConfiguration();
        webtrekk.initInternalParameter();
        webtrekk.initWebtrekkParameter();
        webtrekk.initCustomParameter();

        RequestUrlStore requestUrlStore = mock(RequestUrlStore.class);
        webtrekk.setRequestUrlStore(requestUrlStore);
        when(requestUrlStore.size()).thenReturn(55);

        //set some different default values
        webtrekk.getCustomParameter().put("screenOrientation", "stttr");
        webtrekk.getCustomParameter().put("connectionType", "offline");

        webtrekk.updateDynamicParameter();
        assertEquals(7, webtrekk.getWebtrekkParameter().size());
        assertTrue(webtrekk.getCustomParameter().get("screenOrientation").matches("(portrait|landscape)"));
        assertEquals("WIFI", webtrekk.getCustomParameter().get("connectionType"));
        assertEquals("55", webtrekk.getCustomParameter().get("requestUrlStoreSize"));
    }


    public void testInitCustomParameter() {
        // make sure that the values which change with every request are inserted as well
        webtrekk.setContext(getContext());
        webtrekk.initTrackingConfiguration();
        webtrekk.initInternalParameter();
        webtrekk.initWebtrekkParameter();
        webtrekk.initCustomParameter();

        assertNotNull(webtrekk.getCustomParameter().get("apiLevel"));
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

    public void testSetOptOut() {
        webtrekk.initWebtrekk(getContext());
        SharedPreferences preferences = getContext().getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        webtrekk.setOptout(true);
        assertTrue(webtrekk.isOptout());
        assertTrue(preferences.getBoolean(Webtrekk.PREFERENCE_KEY_OPTED_OUT, false));

        webtrekk.setOptout(false);
        assertFalse(preferences.getBoolean(Webtrekk.PREFERENCE_KEY_OPTED_OUT, true));
        assertFalse(webtrekk.isOptout());

    }

    public void testInitSamplingNull() {
        webtrekk.initWebtrekk(getContext());
        SharedPreferences preferences = getContext().getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        webtrekk.getTrackingConfiguration().setSampling(0);
        webtrekk.initSampling();
        assertFalse(webtrekk.isSampling());
        assertFalse(preferences.getBoolean(Webtrekk.PREFERENCE_KEY_IS_SAMPLING, true));
        assertEquals(0, preferences.getInt(Webtrekk.PREFERENCE_KEY_SAMPLING, -1));
    }

    public void testInitSamplingNotNull() {
        webtrekk.initWebtrekk(getContext());
        SharedPreferences preferences = getContext().getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);

        webtrekk.getTrackingConfiguration().setSampling(10);
        webtrekk.initSampling();
        assertEquals(10, preferences.getInt(Webtrekk.PREFERENCE_KEY_SAMPLING, -1));
        // also make sure the value is reinitalized after change
        webtrekk.getTrackingConfiguration().setSampling(20);
        webtrekk.initSampling();
        assertEquals(20, preferences.getInt(Webtrekk.PREFERENCE_KEY_SAMPLING, -1));
    }

    public void testFnsParameter() {
        // make shure fns gets send once and only once when a new session starts
        webtrekk.initWebtrekk(getContext());
        SharedPreferences preferences = getContext().getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        webtrekk.startActivity("testact");
        webtrekk.track();
        //assertEquals(webtrekk.getRequestUrlStore().get(0), "test");
        assertTrue(webtrekk.getRequestUrlStore().get(0).contains("&fns=1"));
        webtrekk.track();
        assertEquals(webtrekk.getInternalParameter().getDefaultParameter().get(Parameter.FORCE_NEW_SESSION), "0");
        assertTrue(webtrekk.getRequestUrlStore().get(1).contains("&fns=0"));
    }

    public void testFirstParameter() {
        // make shure fns gets send once and only once when a new session starts
        webtrekk.initWebtrekk(getContext());
        SharedPreferences preferences = getContext().getSharedPreferences(Webtrekk.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        webtrekk.startActivity("testact");
        webtrekk.track();
        //assertEquals(webtrekk.getRequestUrlStore().get(0), "test");
        assertTrue(webtrekk.getRequestUrlStore().get(0).contains("&one=1"));
        //preferences.edit().remove(Webtrekk.PREFERENCE_KEY_EVER_ID).commit();
        //webtrekk.initInternalParameter();
        webtrekk.track();
        assertTrue(webtrekk.getRequestUrlStore().get(1).contains("&one=0"));
        assertEquals(webtrekk.getInternalParameter().getDefaultParameter().get(Parameter.APP_FIRST_START), "0");
    }
}
