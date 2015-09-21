package com.webtrekk.webbtrekksdk;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * parses the xml for the tracking configuration
 */
class TrackingConfigurationXmlParser {
    private static final String ns = null;

    /**
     * parses the TrackingConfiguration from XML String
     *
     * @param in the String containing the xml configuration
     * @return returns a TrackingConfiguration object
     * @throws XmlPullParserException
     * @throws IOException
     */
    public TrackingConfiguration parse(String in) throws XmlPullParserException, IOException {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(in));
            parser.nextTag();
            return readConfig(parser);
    }

    /**
     * reads the tags and sets the related configuration values in the TrackingConfiguration object
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private TrackingConfiguration readConfig(XmlPullParser parser) throws XmlPullParserException, IOException {
        TrackingConfiguration config = new TrackingConfiguration();
        parser.require(XmlPullParser.START_TAG, ns, "webtrekkConfiguration");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("version")) {
                parser.require(XmlPullParser.START_TAG, ns, "version");
                String versionValue = readText(parser);
                try {
                    int version = Integer.parseInt(versionValue);
                    config.setVersion(version);
                }  catch (Exception ex){
                    WebtrekkLogging.log("invalid version value: ", ex);
                }
                parser.require(XmlPullParser.END_TAG, ns, "version");


            } else if (name.equals("trackDomain")) {
                parser.require(XmlPullParser.START_TAG, ns, "trackDomain");
                    String trackDomain = readText(parser);
                    config.setTrackDomain(trackDomain);
                parser.require(XmlPullParser.END_TAG, ns, "trackDomain");


            } else if (name.equals("trackId")) {
                parser.require(XmlPullParser.START_TAG, ns, "trackId");

                    String trackId = readText(parser);
                    config.setTrackId(trackId);

                parser.require(XmlPullParser.END_TAG, ns, "trackId");


            }  else if (name.equals("sampling")) {
                parser.require(XmlPullParser.START_TAG, ns, "sampling");

                String samplingValue = readText(parser);

                try {
                    int sampling = Integer.parseInt(samplingValue);
                    config.setSampling(sampling);
                }  catch (Exception ex){
                    WebtrekkLogging.log("invalid sampling value: ", ex);
                }

                parser.require(XmlPullParser.END_TAG, ns, "sampling");


            } else if (name.equals("max_requests")) {
                parser.require(XmlPullParser.START_TAG, ns, "max_requests");

                String maxRequestsValue = readText(parser);

                try {
                    int maxRequests = Integer.parseInt(maxRequestsValue);
                    config.setMaximumRequests(maxRequests);
                }  catch (Exception ex){
                    WebtrekkLogging.log("invalid maxRequests value: ", ex);
                }

                parser.require(XmlPullParser.END_TAG, ns, "max_requests");


            } else if (name.equals("initialSendDelay")) {
                parser.require(XmlPullParser.START_TAG, ns, "initialSendDelay");

                String initialSendDelayValue = readText(parser);

                try {
                    int initialSendDelay = Integer.parseInt(initialSendDelayValue);
                    config.setSampling(initialSendDelay);
                }  catch (Exception ex){
                    WebtrekkLogging.log("invalid initialSendDelay value: ", ex);
                }

                parser.require(XmlPullParser.END_TAG, ns, "initialSendDelay");


            } else if (name.equals("send_delay")) {
                parser.require(XmlPullParser.START_TAG, ns, "send_delay");

                String sendDelayValue = readText(parser);

                try {
                    int sendDelay = Integer.parseInt(sendDelayValue);
                    config.setSendDelay(sendDelay);
                }  catch (Exception ex){
                    WebtrekkLogging.log("invalid send_delay value: ", ex);
                }

                parser.require(XmlPullParser.END_TAG, ns, "send_delay");


            } else if (name.equals("isAutoTrackLanguage")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackLanguage");

                String isAutoTrackLanguageValue = readText(parser);
                if(isAutoTrackLanguageValue.equals("true")) {
                    config.setIsAutoTrackLanguage(true);
                } else {
                    config.setIsAutoTrackLanguage(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackLanguage");


            } else if (name.equals("isAutoTrackApiLevel")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackApiLevel");

                String isAutoTrackApiLevel = readText(parser);
                if(isAutoTrackApiLevel.equals("true")) {
                    config.setIsAutoTrackApiLevel(true);
                } else {
                    config.setIsAutoTrackApiLevel(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackApiLevel");


            } else if (name.equals("isAutoTrackPlaystoreUsername")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackPlaystoreUsername");

                String isAutoTrackPlaystoreUsername = readText(parser);
                if(isAutoTrackPlaystoreUsername.equals("true")) {
                    config.setIsAutoTrackPlaystoreUsername(true);
                } else {
                    config.setIsAutoTrackPlaystoreUsername(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackPlaystoreUsername");

            } else if (name.equals("isAutoTrackPlaystoreEmail")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackPlaystoreEmail");

                String isAutoTrackPlaystoreEmail = readText(parser);
                if(isAutoTrackPlaystoreEmail.equals("true")) {
                    config.setIsAutoTrackPlaystoreEmail(true);
                } else {
                    config.setIsAutoTrackPlaystoreEmail(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackPlaystoreEmail");

            }  else if (name.equals("isAutoTrackAppversionName")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackAppversionName");

                String isAutoTrackAppversionName = readText(parser);
                if(isAutoTrackAppversionName.equals("true")) {
                    config.setIsAutoTrackAppversionName(true);
                } else {
                    config.setIsAutoTrackAppversionName(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackAppversionName");

            }   else if (name.equals("isAutoTrackAppversionName")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackAppversionName");

                String isAutoTrackAppversionName = readText(parser);
                if(isAutoTrackAppversionName.equals("true")) {
                    config.setIsAutoTrackAppversionName(true);
                } else {
                    config.setIsAutoTrackAppversionName(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackAppversionName");

            }   else if (name.equals("isAutoTrackAppversionCode")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackAppversionCode");

                String isAutoTrackAppversionCode = readText(parser);
                if(isAutoTrackAppversionCode.equals("true")) {
                    config.setIsAutoTrackAppversionCode(true);
                } else {
                    config.setIsAutoTrackAppversionCode(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackAppversionCode");

            }   else if (name.equals("isAutoTrackAppPreinstalled")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackAppPreinstalled");

                String isAutoTrackAppPreinstalled = readText(parser);
                if(isAutoTrackAppPreinstalled.equals("true")) {
                    config.setIsAutoTrackAppPreinstalled(true);
                } else {
                    config.setIsAutoTrackAppPreinstalled(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackAppPreinstalled");

            }   else if (name.equals("isAutoTrackAppUpdate")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackAppUpdate");

                String isAutoTrackAppUpdate = readText(parser);
                if(isAutoTrackAppUpdate.equals("true")) {
                    config.setIsAutoTrackAppUpdate(true);
                } else {
                    config.setIsAutoTrackAppUpdate(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackAppUpdate");

            }   else if (name.equals("isAutoTrackAdvertiserId")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackAdvertiserId");

                String isAutoTrackAdvertiserId = readText(parser);
                if(isAutoTrackAdvertiserId.equals("true")) {
                    config.setIsAutoTrackAdvertiserId(true);
                } else {
                    config.setIsAutoTrackAdvertiserId(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackAdvertiserId");

            } else if (name.equals("isAutoTrackAdvertisingOptOut")) {
                parser.require(XmlPullParser.START_TAG, ns, "isAutoTrackAdvertisingOptOut");

                String isAutoTrackAdvertisingOptOut = readText(parser);
                if(isAutoTrackAdvertisingOptOut.equals("true")) {
                    config.setIsAutoTrackAdvertisingOptOut(true);
                } else {
                config.setIsAutoTrackAdvertisingOptOut(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "isAutoTrackAdvertisingOptOut");

            } else if (name.equals("trackingConfigurationUrl")) {
                parser.require(XmlPullParser.START_TAG, ns, "trackingConfigurationUrl");

                String trackingConfigurationUrl = readText(parser);
                config.setTrackingConfigurationUrl(trackingConfigurationUrl);
                parser.require(XmlPullParser.END_TAG, ns, "trackingConfigurationUrl");

            }
            // parse the enabled plugins
            //TODO: maybe rename this options and hide the plugin layer totally from the end customer
            // possible name would be enable_featurename
            else if (name.equals("enable_plugin_hello_world")) {
                parser.require(XmlPullParser.START_TAG, ns, "enable_plugin_hello_world");

                String isPluginHelloWorldEnabled = readText(parser);
                if(isPluginHelloWorldEnabled.equals("true")) {
                    config.setIsHelloWorldPluginEnabed(true);
                } else {
                    config.setIsHelloWorldPluginEnabed(false);
                }
                parser.require(XmlPullParser.END_TAG, ns, "enable_plugin_hello_world");

            }
            else if (name.equals("activity")) {
                TrackingConfiguration.ActivityConfiguration act = readActivityConfiguration(parser);
                config.getActivityConfigurations().put(act.getClassName(), act);
            } else {
                skip(parser);
            }
        }
        return config;
    }

    /**
     * ActivityConfigurations are in their own XML Tag, this function reads and parses the Activity Configuration
     *
     * @param parser
     * @return
     * @throws XmlPullParserException
     * @throws IOException
     */
    private TrackingConfiguration.ActivityConfiguration readActivityConfiguration(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "activity");
        String className = null;
        String mappingName = null;
        boolean isAutoTrack = false;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("classname")) {
                parser.require(XmlPullParser.START_TAG, ns, "classname");
                className = readText(parser);
            } else if (name.equals("mappingname")) {
                parser.require(XmlPullParser.START_TAG, ns, "mappingname");
                mappingName = readText(parser);
            } else if (name.equals("autotrack")) {
                parser.require(XmlPullParser.START_TAG, ns, "autotrack");
                String autotrack = readText(parser);
                if (autotrack.equals("true")) {
                    isAutoTrack = true;
                }
            }

        }

        return new TrackingConfiguration.ActivityConfiguration(className, mappingName, isAutoTrack);
    }

    /**
     * reads a String value from xml tag
     * @param parser
     * @return
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    /**
     * skips a tag during parsing the xml string
     *
     * @param parser
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                default:
                    break;
            }
        }
    }


}

