package com.webtrekk.webtrekksdk;

import android.test.AndroidTestCase;
import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;

import java.util.HashMap;

public class TrackingRequestTests extends AndroidTestCase {
    private TrackingParameter tp_activity_start;
    private TrackingParameter tp_action_user_button;
    private TrackingParameter tp_impression_picture;
    private TrackingParameter tp_conversion;
    TrackingRequest tr_astart;
    TrackingRequest tr_action;
    TrackingRequest tr_conversion;
    private HashMap<Parameter, String> auto_tracked_values;
    TrackingConfiguration trackingConfiguration;
    private TrackingParameter tpMedia;
    private Webtrekk webtrekk;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

        webtrekk = new Webtrekk();

        trackingConfiguration = new TrackingConfiguration();
        trackingConfiguration.setTrackId("1111111111");
        trackingConfiguration.setTrackDomain("http://q3.webtrekk.net");

        webtrekk.setTrackingConfiguration(trackingConfiguration);
        webtrekk.setCurrentActivityName("test");
        webtrekk.setContext(getContext());
        webtrekk.initWebtrekkParameter();
        webtrekk.initInternalParameter();

        // for all tests just start with an tmpy list of parameters
        webtrekk.initAutoCustomParameter();
        webtrekk.setOptout(false);
        webtrekk.setIsSampling(false);


        auto_tracked_values = new HashMap<>();
        //auto_tracked_values.put(Parameter.DEVICE, "Google Nexus 4");
        auto_tracked_values.put(Parameter.EVERID, "12345678901234");
        //auto_tracked_values.put(Parameter.TRACKING_LIB_VERSION, "400");
        auto_tracked_values.put(Parameter.ACTIVITY_NAME, "StartActivity");
        auto_tracked_values.put(Parameter.SCREEN_RESOLUTION, "1280x1024");
        auto_tracked_values.put(Parameter.SCREEN_DEPTH, "32");
        auto_tracked_values.put(Parameter.TIMESTAMP, "1231233243245");

        tpMedia = new TrackingParameter();
        tpMedia.add(Parameter.MEDIA_FILE, "foo.mp4");
        tpMedia.add(Parameter.MEDIA_LENGTH, "300");
        tpMedia.add(Parameter.MEDIA_POS, "0");
        tpMedia.add(Parameter.MEDIA_CAT, "1", "mp4");
        tpMedia.add(Parameter.MEDIA_CAT, "1", "example");

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetUrlTrackingLibVersion() {
        tp_activity_start = new TrackingParameter();
        tp_activity_start.add(auto_tracked_values);
        tr_astart = new TrackingRequest(tp_activity_start, trackingConfiguration);

        String url = tr_astart.getUrlString();
        assertTrue(url.contains("p=400"));
    }

    public void testGetUrlStringStandard() {
        tp_activity_start = new TrackingParameter();
        tp_activity_start.add(auto_tracked_values);
        tr_astart = new TrackingRequest(tp_activity_start, trackingConfiguration);

        String url = tr_astart.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&eor=1", url);
    }

    public void testGetUrlStringActionButton() {
        // example request, send when a user presses the Save Button on the User Profile activity
        tp_action_user_button = new TrackingParameter()
                .add(Parameter.ACTION_NAME, "Save Button")
                .add(Parameter.ACTIVITY_NAME, "User Profile");
        tp_action_user_button.add(auto_tracked_values);
        tr_action = new TrackingRequest(tp_action_user_button, trackingConfiguration);

        String url = tr_action.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&ct=Save+Button&eor=1", url);

    }

    public void testGetUrlStringEcommerceParameter() {
        // example ecommerce trackingParameter request, send when a user bought views some shoes
        tp_conversion = new TrackingParameter()
                .add(Parameter.PRODUCT, "FREE 4.0 FLYKNIT")
                .add(Parameter.ORDER_TOTAL, "129,95")
                .add(Parameter.ORDER_NUMBER, "12345");
        tp_conversion.add(Parameter.ECOM, "1", "XXL")
                .add(Parameter.ECOM, "2", "Black")
                .add(Parameter.ECOM, "3", "paypal");
        tp_conversion.add(auto_tracked_values);
        tr_conversion = new TrackingRequest(tp_conversion, trackingConfiguration);

        String url = tr_conversion.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&ov=129%2C95&oi=12345&ba=FREE+4.0+FLYKNIT&cb1=XXL&cb2=Black&cb3=paypal&eor=1", url);

    }

    public void testGlobalTrackingParameter() {
        tp_activity_start = new TrackingParameter();
        tp_activity_start.add(auto_tracked_values);
        tr_astart = new TrackingRequest(tp_activity_start, trackingConfiguration);
        TrackingParameter globalTrackingParameter = new TrackingParameter();
        globalTrackingParameter.add(Parameter.ECOM, "1", "GLOBALTEST");
        tp_activity_start.add(globalTrackingParameter);

        String url = tr_astart.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&cb1=GLOBALTEST&eor=1", url);

    }

    public void testMediaTrackingPlay() {
        tpMedia.add(auto_tracked_values);
        tpMedia.add(Parameter.MEDIA_ACTION, "start");
        tpMedia.add(Parameter.MEDIA_POS, "0");
        TrackingRequest tm = new TrackingRequest(tpMedia, trackingConfiguration);

        String url = tm.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&mi=foo.mp4&mk=start&mt1=0&mt2=300&mg1=example&eor=1", url);

    }
    public void testMediaTrackingPause() {
        tpMedia.add(auto_tracked_values);
        tpMedia.add(Parameter.MEDIA_ACTION, "pause");
        tpMedia.add(Parameter.MEDIA_POS, "0");
        TrackingRequest tm = new TrackingRequest(tpMedia, trackingConfiguration);

        String url = tm.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&mi=foo.mp4&mk=pause&mt1=0&mt2=300&mg1=example&eor=1", url);

    }
    public void testMediaTrackingSeek() {
        tpMedia.add(auto_tracked_values);
        tpMedia.add(Parameter.MEDIA_ACTION, "seek");
        tpMedia.add(Parameter.MEDIA_POS, "0");
        TrackingRequest tm = new TrackingRequest(tpMedia, trackingConfiguration);

        String url = tm.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&mi=foo.mp4&mk=seek&mt1=0&mt2=300&mg1=example&eor=1", url);

    }
    public void testMediaTrackingStop() {
        tpMedia.add(auto_tracked_values);
        tpMedia.add(Parameter.MEDIA_ACTION, "stop");
        tpMedia.add(Parameter.MEDIA_POS, "0");
        TrackingRequest tm = new TrackingRequest(tpMedia, trackingConfiguration);

        String url = tm.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&eid=12345678901234&mi=foo.mp4&mk=stop&mt1=0&mt2=300&mg1=example&eor=1", url);

    }

    public void testAutoTrackAppUpdate() {
        webtrekk.getAutoCustomParameter().put("appUpdated", "0");
        trackingConfiguration.setAutoTrackAppUpdate(true);
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "appUpdated");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100=0"));

        webtrekk.getAutoCustomParameter().put("appUpdated", "1");
        TrackingParameter tp2 = new TrackingParameter();
        tp2.add(Parameter.ECOM, "100", "appUpdated");
        TrackingRequest tr2 = webtrekk.createTrackingRequest(tp2);
        String url2 = tr2.getUrlString();
        assertTrue(url2, url2.contains("cb100=1"));
    }

    public void testAutoAdvertiserId() {
        trackingConfiguration.setAutoTrackAdvertiserId(true);
        assertTrue(webtrekk.getTrackingConfiguration().isAutoTrackAdvertiserId());
        webtrekk.initAdvertiserId();
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "advertiserId");
        tp.add(Parameter.ECOM, "200", "advertisingOptOut");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100="));
        assertTrue(webtrekk.getCustomParameter().get("advertiserId").length() > 10);
        assertTrue(url, url.contains("cb200=false"));
    }

    public void testAutoTrackAppVersion() {
        webtrekk.getAutoCustomParameter().put("appVersion", "10");
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "appVersion");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100=10"));

        webtrekk.getAutoCustomParameter().put("appVersion", "11");
        TrackingParameter tp2 = new TrackingParameter();
        tp2.add(Parameter.ECOM, "100", "appVersion");
        TrackingRequest tr2 = webtrekk.createTrackingRequest(tp2);
        String url2 = tr2.getUrlString();
        assertTrue(url2, url2.contains("cb100=11"));
    }

    public void testAutoTrackAppVersionCode() {
        webtrekk.getAutoCustomParameter().put("appVersionCode", "10");
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "appVersionCode");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100=10"));
    }

    public void testAutoTrackAppPreinstalled() {
        webtrekk.getAutoCustomParameter().put("appPreinstalled", "1");
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "appPreinstalled");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100=1"));
    }

    public void testAutoTrackApiLevel() {
        webtrekk.getAutoCustomParameter().put("apiLevel", "19");
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "apiLevel");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100=19"));
    }


    public void testAutoTrackScreenOrientation() {
        //webtrekk.getCustomParameter().put("screenOrientation", "landscape");
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "screenOrientation");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100=portrait") || url.contains("cb100=landscape"));
    }

    public void testAutoTrackConnectionType() {
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "connectionType");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb100=WIFI") || url.contains("cb100=offline")|| url.contains("cb100=3G")|| url.contains("cb100=4G"));
    }

    public void testAutoTrackPlaystoreUsername() {
        //webtrekk.getCustomParameter().put("screenOrientation", "landscape");
        trackingConfiguration.setAutoTrackPlaystoreUsername(true);
        webtrekk.initAutoCustomParameter();
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "playstoreFamilyname");
        tp.add(Parameter.ECOM, "200", "playstoreGivenname");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertNotNull(webtrekk.getCustomParameter().get("playstoreFamilyname"));
        assertNotNull(webtrekk.getCustomParameter().get("playstoreGivenname"));
        assertTrue(url, url.contains("cb100="));
        assertTrue(url, url.contains("cb200="));
    }

    public void testAutoTrackPlaystoreMail() {
        //webtrekk.getCustomParameter().put("screenOrientation", "landscape");
        trackingConfiguration.setAutoTrackPlaystoreMail(true);
        assertTrue(trackingConfiguration.isAutoTrackPlaystoreMail());
        webtrekk.initAutoCustomParameter();
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "100", "playstoreMail");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertNotNull(webtrekk.getCustomParameter().get("playstoreMail"));
        assertTrue(url, url.contains("cb100="));
    }

    public void testActionParameter() {
        // replaces init function
        webtrekk.setGlobalTrackingParameter(new TrackingParameter());
        webtrekk.getGlobalTrackingParameter().add(Parameter.ECOM, "1", "test1");
        //trackingConfiguration.getActivityConfigurations().get("")
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ECOM, "2", "test2");
        TrackingRequest tr = webtrekk.createTrackingRequest(tp);
        String url = tr.getUrlString();
        assertTrue(url, url.contains("cb1=test1"));
        assertTrue(url, url.contains("cb2=test2"));
        // now an action parameter where the global ones are ignored
        tp.add(Parameter.ACTION_NAME, "OrderButton");
        tr = webtrekk.createTrackingRequest(tp);
        url = tr.getUrlString();
        assertFalse(url, url.contains("cb1=test1"));
        assertTrue(url, url.contains("cb2=test2"));
        assertTrue(url, url.contains("OrderButton"));

    }

    public void testUserAgent() {
        Webtrekk wt = new Webtrekk();
        wt.setContext(getContext());
        wt.setTrackingConfiguration(trackingConfiguration);
        wt.initWebtrekkParameter();
        TrackingParameter tp = new TrackingParameter();
        TrackingRequest tr = wt.createTrackingRequest(tp);
        assertTrue(tr.trackingParameter.getDefaultParameter().containsKey(Parameter.USERAGENT));
        String url = tr.getUrlString();
        assertTrue(url, url.contains("X-WT-UA"));
    }

    public void testActivityNameOverride() {
        Webtrekk wt = new Webtrekk();
        wt.setCurrentActivityName("autotrackname");
        wt.setContext(getContext());
        wt.setTrackingConfiguration(trackingConfiguration);
        wt.initWebtrekkParameter();
        TrackingParameter tp = new TrackingParameter();
        tp.add(Parameter.ACTION_NAME, "customname");
        TrackingRequest tr = wt.createTrackingRequest(tp);
        assertTrue(tr.trackingParameter.getDefaultParameter().containsKey(Parameter.ACTION_NAME));
        String url = tr.getUrlString();
        assertTrue(url, url.contains("customname"));
    }

}
