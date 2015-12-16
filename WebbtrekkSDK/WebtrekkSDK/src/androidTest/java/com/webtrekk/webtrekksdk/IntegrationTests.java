package com.webtrekk.webtrekksdk;

import android.test.AndroidTestCase;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import com.webtrekk.webtrekksdk.TrackingParameter.Parameter;

/**
 * Created by user on 16/12/15.
 */
public class IntegrationTests extends AndroidTestCase {
    private TrackingConfigurationXmlParser trackingConfigurationXmlParser;
    private Webtrekk webtrekk;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

        trackingConfigurationXmlParser = new TrackingConfigurationXmlParser();
        webtrekk = Webtrekk.getInstance();
        webtrekk.initWebtrekk(getContext());
    }

    /**
     * test custom const global parameter from config to url
     */

    public void testGlobalConstParameter(){
        TrackingConfiguration config = null;

        String configString = "<?xml version=\"1.0\" encoding=\"utf-8\"?><webtrekkConfiguration><globalTrackingParameter><parameter id=\"PRODUCT\">test_product</parameter><ecomParameter><parameter id=\"1\">test_ecomparam1</parameter></ecomParameter></globalTrackingParameter></webtrekkConfiguration>";
        try {
            config = trackingConfigurationXmlParser.parse(configString);
            assertNotNull(config);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // first step is to make sure the product parameter is in the config
        assertTrue(config.getConstGlobalTrackingParameter().containsKey(TrackingParameter.Parameter.PRODUCT));
        //check the value
        assertEquals("test_product", config.getConstGlobalTrackingParameter().getDefaultParameter().get(Parameter.PRODUCT));
        // set the config in the webtrekk object
        webtrekk.setTrackingConfiguration(config);
        // test request creation
        TrackingRequest tr = webtrekk.createTrackingRequest(new TrackingParameter());
        assertTrue(tr.getTrackingParameter().getDefaultParameter().containsKey(Parameter.PRODUCT));
        String url = tr.getUrlString();
        assertTrue(url, url.contains("ba=test_product"));
        // now call track method
        webtrekk.startActivity("test");
        webtrekk.track();
        //make sure the tracking request is created correct and the url is on the requesturlstore
        assertEquals(1, webtrekk.getRequestUrlStore().size());
        assertTrue(webtrekk.getRequestUrlStore().get(0).contains("ba=test_product"));

    }
}
