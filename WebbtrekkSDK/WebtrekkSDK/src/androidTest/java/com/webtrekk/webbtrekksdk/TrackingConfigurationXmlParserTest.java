package com.webtrekk.webbtrekksdk;

import android.test.AndroidTestCase;


public class TrackingConfigurationXmlParserTest extends AndroidTestCase {

    private TrackingConfigurationXmlParser trackingConfigurationXmlParser;
    String validXmlConfiguration = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<configuration>\n" +
            "<trackdomain type=\"text\">http://q3.webtrekk.net</trackdomain>\n" +
            "<trackid type=\"text\">111111111111</trackid>\n" +
            "\n" +
            "\n" +
            "    <activity>\n" +
            "        <classname type=\"text\">MainActivity</classname>\n" +
            "        <mappingname type=\"text\">Startseite</mappingname>\n" +
            "        <autotrack type=\"text\">true</autotrack>\n" +
            "    </activity>\n" +
            "\n" +
            "    <plugin>\n" +
            "        <name>HelloWorldPlugin</name>\n" +
            "        <value>hello</value>\n" +
            "    </plugin>\n" +
            "\n" +
            "    <customvalues>\n" +
            "        <exampleval1>custom example val 1</exampleval1>\n" +
            "    </customvalues>\n" +
            "\n" +
            "</configuration>";

    String invalidXmlConfiguration = "foo";
    String missingTagXmlConfiguration = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<configuration>\n" +
            "<trackdomain type=\"text\">http://q3.webtrekk.net</trackdomain>\n" +
            "<trackid type=\"text\">111111111111\n" +
            "\n" +
            "\n" +
            "    <activity>\n" +
            "        <classname type=\"text\">MainActivity</classname>\n" +
            "        <mappingname type=\"text\">Startseite</mappingname>\n" +
            "        <autotrack type=\"text\">true</autotrack>\n" +
            "    </activity>\n" +
            "\n" +
            "    <plugin>\n" +
            "        <name>HelloWorldPlugin</name>\n" +
            "        <value>hello</value>\n" +
            "    </plugin>\n" +
            "\n" +
            "    <customvalues>\n" +
            "        <exampleval1>custom example val 1</exampleval1>\n" +
            "    </customvalues>\n" +
            "\n" +
            "</configuration>";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // android bug workaround: 308
        System.setProperty("dexmaker.dexcache", getContext().getCacheDir().toString());

        trackingConfigurationXmlParser = new TrackingConfigurationXmlParser();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testParseValidXml() {
        TrackingConfiguration config = null;
        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(trackingConfigurationString);
        } catch (Exception e) {
        }
        assertEquals("http://trackingtest.nglab.org", config.getTrackDomain());
        assertEquals("1111111111112", config.getTrackId());
        assertEquals(1, config.getActivityConfigurations().size());
        assertTrue(config.getActivityConfigurations().containsKey("MainActivity"));
        assertEquals(config.getActivityConfigurations().get("MainActivity").getMappingName(), "Startseite");
        assertEquals(config.getActivityConfigurations().get("MainActivity").isAutoTrack(), true);
        assertEquals(2, config.getVersion());
        assertEquals(0, config.getSampling());
        assertEquals(0, config.getInitialSendDelay());
        assertEquals(60, config.getSendDelay());
        assertEquals(5000, config.getMaximumRequests());
        assertEquals(true, config.isHelloWorldPluginEnabed());
        assertEquals("http://localhost/tracking_config.xml", config.getTrackingConfigurationUrl());
    }

    public void testParseInvalidXML() {
        TrackingConfiguration config = null;
        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(invalidXmlConfiguration);
            fail("invalid xml configuration");
        }  catch (Exception e) {
        }

        try {
            String trackingConfigurationString = HelperFunctions.stringFromStream(getContext().getResources().openRawResource(R.raw.webtrekk_config));
            config = trackingConfigurationXmlParser.parse(missingTagXmlConfiguration);
            fail("invalid xml configuration");
        }  catch (Exception e) {
        }
    }

}
