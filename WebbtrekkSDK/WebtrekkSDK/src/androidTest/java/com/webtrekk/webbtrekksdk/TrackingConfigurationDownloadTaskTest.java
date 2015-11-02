package com.webtrekk.webbtrekksdk;

import android.test.InstrumentationTestCase;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

/**
 * Created by user on 02/11/15.
 */
public class TrackingConfigurationDownloadTaskTest extends InstrumentationTestCase implements AsyncTest {
    private static boolean called;
    Webtrekk webtrekk;
    private SynchronizedWaiter synchronizedWaiter;

    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getInstrumentation().getContext().getCacheDir().toString());
        called = false;
        webtrekk = new Webtrekk();
        webtrekk.initWebtrekk(getInstrumentation().getContext());

    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * make sure that it never throws an exception or overrides the config with null,
     * in case of a network error it uses the local one
     * @throws Throwable
     */
    public void testTrackingConfigurationDownloadTaskNetworkError() throws Throwable {
        synchronizedWaiter = new SynchronizedWaiter();
        AsyncTest asyncTest = this;
        Log.d("TEST", "task started");
        TrackingConfigurationDownloadTask task = new TrackingConfigurationDownloadTask(webtrekk, asyncTest);
        task = spy(task);
        try {
            task.execute(webtrekk.getTrackingConfiguration().getTrackingConfigurationUrl());

        } catch (Exception e) {
            fail("should never throw an exception, just use the local configuration");
        }
        synchronizedWaiter.doWait();
        assertNotNull(webtrekk.getTrackingConfiguration());
    }
    /**
     * make sure that it never throws an exception or overrides the config with null,
     * in case of an invalid config it uses the local one
     * @throws Throwable
     */
    public void testTrackingConfigurationDownloadTaskInvalidConfig() throws Throwable {
        synchronizedWaiter = new SynchronizedWaiter();
        AsyncTest asyncTest = this;
        Log.d("TEST", "task started");
        TrackingConfigurationDownloadTask task = new TrackingConfigurationDownloadTask(webtrekk, asyncTest);
        task = spy(task);
        doReturn(new ByteArrayInputStream("foo".getBytes("UTF-8"))).when(task).getXmlFromUrl("http://foourl.de/config.xml");
        try {
            task.execute("http://nglab.org/config.xml");

        } catch (Exception e) {
            fail("should never throw an exception, just use the local configuration");
        }
        synchronizedWaiter.doWait();
        assertNotNull(webtrekk.getTrackingConfiguration());
    }

    /**
     * make sure that it never throws an exception or overrides the config with null,
     * in case of an invalid config it uses the local one
     * @throws Throwable
     */
    public void testTrackingConfigurationDownloadTaskSameConfigVersion() throws Throwable {
        synchronizedWaiter = new SynchronizedWaiter();
        AsyncTest asyncTest = this;
        Log.d("TEST", "task started");
        TrackingConfigurationDownloadTask task = new TrackingConfigurationDownloadTask(webtrekk, asyncTest);
        assertNotNull(webtrekk.getTrackingConfiguration());
        task = spy(task);
        webtrekk = spy(webtrekk);
        String config = "<webtrekkConfiguration>\n" +
                "    <!--the version number for this configuration file -->\n" +
                "    <version>2</version></webtrekkConfiguration>";
        doReturn(new ByteArrayInputStream(config.getBytes("UTF-8"))).when(task).getXmlFromUrl("http://foourl.de/config.xml");
        try {
            task.execute("http://nglab.org/config.xml");

        } catch (Exception e) {
            fail("should never throw an exception, just use the local configuration");
        }
        synchronizedWaiter.doWait();

        // make sure that no new tracking configuration is set
        verify(webtrekk, times(0)).setTrackingConfiguration((TrackingConfiguration)any());
    }

    /**
     * make sure that it never throws an exception or overrides the config with null,
     * in case of an invalid config it uses the local one
     * @throws Throwable
     */
    public void testTrackingConfigurationDownloadTaskNewConfigVersion() throws Throwable {
        synchronizedWaiter = new SynchronizedWaiter();
        AsyncTest asyncTest = this;
        String config = "<webtrekkConfiguration>\n" +
                "    <!--the version number for this configuration file -->\n" +
                "    <version>3</version></webtrekkConfiguration>";

        TrackingConfigurationDownloadTask task = spy(new TrackingConfigurationDownloadTask(webtrekk, asyncTest));
        doReturn("foo").when(task).getXmlFromUrl(anyString());
//        when(task.downloadUrl("http://nglab.org/config.xml")).thenReturn(new ByteArrayInputStream(config.getBytes("UTF-8")));
                assertNotNull(webtrekk.getTrackingConfiguration());
        // make sure it uses config version 2 before
        assertEquals(2, webtrekk.getTrackingConfiguration().getVersion());
        //webtrekk = spy(webtrekk);

        //final HttpURLConnection mockURLConnection = mock(HttpURLConnection.class);
        //when(mockURLConnection.getInputStream()).thenReturn(new ByteArrayInputStream(config.getBytes("UTF-8")));
        //doNothing().when(mockURLConnection).connect();
        //task.setConn(mockURLConnection);


        try {
            task.execute("http://wt.nglab.org:1480/webtrekk_config3.xml");

        } catch (Exception e) {
            fail("should never throw an exception, just use the local configuration");
        }
        synchronizedWaiter.doWait();
        // make sure the current tracking configuration has been set and has the new version number
        //verify(webtrekk, times(1)).setTrackingConfiguration((TrackingConfiguration)any());
        assertEquals(3, webtrekk.getTrackingConfiguration().getVersion());
    }

    @Override
    public void workDone() {
        synchronizedWaiter.doNotify();

    }
}
