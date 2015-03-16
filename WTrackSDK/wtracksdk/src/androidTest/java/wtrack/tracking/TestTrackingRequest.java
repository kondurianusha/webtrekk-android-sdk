package wtrack.tracking;

import android.test.AndroidTestCase;

import java.net.URL;
import java.util.HashMap;

import static wtrack.tracking.TrackingParams.Params;
import static wtrack.tracking.Tracker.Events;



/**
 * this app tests the tracking request class
 * it tests both url based tracking and the url creation for various tracking events and trackingparams
 * and also json based tracking
 *
 */
public class TestTrackingRequest extends AndroidTestCase{
    private TrackingParams tp_activity_start;
    private TrackingParams tp_action_user_button;
    private TrackingParams tp_impression_picture;
    private TrackingParams tp_conversion;
    TrackingRequestUrl tr_astart;
    TrackingRequestUrl tr_action;
    TrackingRequestUrl tr_impression;
    TrackingRequestUrl tr_conversion;

    private HashMap<Params, String> auto_tracked_values;

    @Override
    public void setUp() throws Exception {

        // set up the auto_tracked_values HashMap manually, later the WTrack class handles this
        auto_tracked_values = new HashMap<>();
        auto_tracked_values.put(Params.DEVICE, "Google Nexus 4");
        auto_tracked_values.put(Params.EVERID, "12345678901234");
        auto_tracked_values.put(Params.API_LEVEL, "21");
        auto_tracked_values.put(Params.OS_NAME, "Lollipop");

        tp_activity_start = new TrackingParams()
                .add(Params.ACTIVITY_NAME, "StartActivity");
        tp_activity_start.add(auto_tracked_values);
        tr_astart = new TrackingRequestUrl(Events.ACTIVITY, tp_activity_start, null);
        tr_astart.setWebtrekk_track_id("1111111111");
        tr_astart.setWebtrekk_track_domain("q3.webtrekk.net");

        // example request, send when a user presses the Save Button on the User Profile activity
        tp_action_user_button = new TrackingParams()
                .add(Params.ACTION_NAME, "Save Button")
                .add(Params.ACTIVITY_NAME, "User Profile");
        tp_action_user_button.add(auto_tracked_values);
        tr_action = new TrackingRequestUrl(Events.ACTION, tp_action_user_button, null);
        tr_action.setWebtrekk_track_id("1111111111");
        tr_action.setWebtrekk_track_domain("q3.webtrekk.net");

        // example impression request, this could be send when a user has seen the shoes or an add of them
        tp_impression_picture = new TrackingParams()
                .add(Params.PRODUCT, "FREE 4.0 FLYKNIT")
                .add(Params.PRODUCT_CATEGORY, "Schuhe");
        tp_impression_picture.add(auto_tracked_values);
        tr_impression = new TrackingRequestUrl(Events.IMPRESSION, tp_impression_picture, null);
        tr_impression.setWebtrekk_track_id("1111111111");
        tr_impression.setWebtrekk_track_domain("q3.webtrekk.net");


        // example conversion request, send when a user bought that shoes
        tp_conversion = new TrackingParams()
                .add(Params.PRODUCT, "FREE 4.0 FLYKNIT")
                .add(Params.PRODUCT_CATEGORY, "Schuhe")
                .add(Params.ORDER_TOTAL, "129,95")
                .add(Params.ORDER_NUMBER, "12345");
        tp_conversion.add(auto_tracked_values);
        tr_conversion = new TrackingRequestUrl(Events.CONVERSION, tp_conversion, null);
        tr_conversion.setWebtrekk_track_id("1111111111");
        tr_conversion.setWebtrekk_track_domain("q3.webtrekk.net");
    }

    public void test_generate_urls() {
        // make shure the url looks like: http://[trackdomain]/[trackid]/wt?param=value&param2=value2&param3=value3...
        String url = tr_astart.getUrlstring();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?event=activity&osname=Lollipop&dev=Google%20Nexus%204&eid=12345678901234&aname=StartActivity&api=21", url);
        url = tr_action.getUrlstring();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?event=action&osname=Lollipop&dev=Google%20Nexus%204&eid=12345678901234&aname=User%20Profile&api=21&a_name=Save%20Button", url);
        url = tr_impression.getUrlstring();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?event=impression&osname=Lollipop&dev=Google%20Nexus%204&eid=12345678901234&api=21&p=FREE%204.0%20FLYKNIT&p_cat=Schuhe", url);
        url = tr_conversion.getUrlstring();
        assertEquals("http://q3.webtrekk.net/1111111111/wt?event=conversion&osname=Lollipop&dev=Google%20Nexus%204&eid=12345678901234&api=21&o_number=12345&o_total=129%2C95&p=FREE%204.0%20FLYKNIT&p_cat=Schuhe", url);
    }
}
