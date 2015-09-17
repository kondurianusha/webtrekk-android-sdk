package com.webtrekk.webbtrekksdk;

import android.test.AndroidTestCase;
import com.webtrekk.webbtrekksdk.TrackingParameter.Parameter;

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

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

        trackingConfiguration = new TrackingConfiguration();
        trackingConfiguration.setTrackId("1111111111");
        trackingConfiguration.setTrackDomain("http://q3.webtrekk.net");

        auto_tracked_values = new HashMap<>();
        auto_tracked_values.put(Parameter.DEVICE, "Google Nexus 4");
        auto_tracked_values.put(Parameter.EVERID, "12345678901234");
        auto_tracked_values.put(Parameter.API_LEVEL, "21");
        auto_tracked_values.put(Parameter.TRACKING_LIB_VERSION, "400");
        auto_tracked_values.put(Parameter.ACTIVITY_NAME, "StartActivity");
        auto_tracked_values.put(Parameter.SCREEN_RESOLUTION, "1280x1024");
        auto_tracked_values.put(Parameter.SCREEN_DEPTH, "32");
        auto_tracked_values.put(Parameter.TIMESTAMP, "1231233243245");

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testGetUrlStringStandard() {
        tp_activity_start = new TrackingParameter();
        tp_activity_start.add(auto_tracked_values);
        tr_astart = new TrackingRequest(tp_activity_start, trackingConfiguration);

        String url = tr_astart.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&mts=1231233243245&dev=Google+Nexus+4&eid=12345678901234&eor=1", url);
    }

    public void testGetUrlStringActionButton() {
        // example request, send when a user presses the Save Button on the User Profile activity
        tp_action_user_button = new TrackingParameter()
                .add(Parameter.ACTION_NAME, "Save Button")
                .add(Parameter.ACTIVITY_NAME, "User Profile");
        tp_action_user_button.add(auto_tracked_values);
        tr_action = new TrackingRequest(tp_action_user_button, trackingConfiguration);

        String url = tr_action.getUrlString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&mts=1231233243245&dev=Google+Nexus+4&eid=12345678901234&ct=Save+Button&eor=1", url);

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
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&mts=1231233243245&dev=Google+Nexus+4&eid=12345678901234&ov=129%2C95&oi=12345&ba=FREE+4.0+FLYKNIT&cb1=XXL&cb2=Black&cb3=paypal&eor=1", url);

    }

    //TODO: media tracking tests

    //TODO: test more url variants and trackingParameter after the missing requirements are clear
}
