package com.webtrekk.android.test;

import android.test.AndroidTestCase;


import com.webtrekk.android.tracking.TrackingParams;
import com.webtrekk.android.tracking.TrackingRequestUrl;

import java.util.HashMap;


import static com.webtrekk.android.tracking.TrackingParams.Params;



/**
 * this app tests the tracking request class
 * it tests both url based tracking and the url creation for various tracking events and trackingparams
 * and also json based tracking
 *
 */
public class TestTrackingRequest extends AndroidTestCase {
    private TrackingParams tp_activity_start;
    private TrackingParams tp_action_user_button;
    private TrackingParams tp_impression_picture;
    private TrackingParams tp_conversion;
    TrackingRequestUrl tr_astart;
    TrackingRequestUrl tr_action;
    TrackingRequestUrl tr_impression;
    TrackingRequestUrl tr_conversion;

    private HashMap<Params, String> auto_tracked_values;



    public void test_generate_urls() {
        // set up the auto_tracked_values HashMap manually, later the WTrack class handles this
        auto_tracked_values = new HashMap<>();
        auto_tracked_values.put(Params.DEVICE, "Google Nexus 4");
        auto_tracked_values.put(Params.EVERID, "12345678901234");
        auto_tracked_values.put(Params.API_LEVEL, "21");
        auto_tracked_values.put(Params.TRACKING_LIB_VERSION, "400");
        auto_tracked_values.put(Params.ACTIVITY_NAME, "StartActivity");
        auto_tracked_values.put(Params.SCREEN_RESOLUTION, "1280x1024");
        auto_tracked_values.put(Params.SCREEN_DEPTH, "32");
        auto_tracked_values.put(Params.TIMESTAMP, "1231233243245");

        tp_activity_start = new TrackingParams();
        tp_activity_start.add(auto_tracked_values);
        tr_astart = new TrackingRequestUrl(tp_activity_start, null);
        tr_astart.setWebtrekk_track_id("1111111111");
        tr_astart.setWebtrekk_track_domain("q3.webtrekk.net");

        // example request, send when a user presses the Save Button on the User Profile activity
        tp_action_user_button = new TrackingParams()
                .add(Params.ACTION_NAME, "Save Button")
                .add(Params.ACTIVITY_NAME, "User Profile");
        tp_action_user_button.add(auto_tracked_values);
        tr_action = new TrackingRequestUrl(tp_action_user_button, null);
        tr_action.setWebtrekk_track_id("1111111111");
        tr_action.setWebtrekk_track_domain("q3.webtrekk.net");


        // example ecommerce params request, send when a user bought views some shoes
        tp_conversion = new TrackingParams()
                .add(Params.PRODUCT, "FREE 4.0 FLYKNIT")
                .add(Params.ORDER_TOTAL, "129,95")
                .add(Params.ORDER_NUMBER, "12345");
        tp_conversion.add(Params.ECOM, "1", "XXL")
                .add(Params.ECOM, "2", "Black")
                .add(Params.ECOM, "3", "paypal");
        tp_conversion.add(auto_tracked_values);
        tr_conversion = new TrackingRequestUrl(tp_conversion, null);
        tr_conversion.setWebtrekk_track_id("1111111111");
        tr_conversion.setWebtrekk_track_domain("q3.webtrekk.net");

        // make shure the url looks like: http://[trackdomain]/[trackid]/wt?param=value&param2=value2&param3=value3...
        String url = tr_astart.getURLString();
        System.out.println(url);
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&dev=Google Nexus 4&eid=12345678901234&api=21&mts=1231233243245&eor=1", url);
        url = tr_action.getURLString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&dev=Google Nexus 4&eid=12345678901234&api=21&ct=Save Button&mts=1231233243245&eor=1", url);
        url = tr_conversion.getURLString();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?p=400,StartActivity,0,1280x1024,32,0,1231233243245,0,0,0&dev=Google Nexus 4&eid=12345678901234&api=21&oi=12345&ov=129,95&ba=FREE 4.0 FLYKNIT&mts=1231233243245&cb1=XXL&cb2=Black&cb3=paypal&eor=1", url);
    }
}
